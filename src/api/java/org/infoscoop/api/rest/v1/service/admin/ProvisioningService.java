/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.api.rest.v1.service.admin;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.IAccountManager;
import org.infoscoop.account.simple.AccountAttributeName;
import org.infoscoop.api.rest.v1.response.model.Provisioning;
import org.infoscoop.dao.model.AccountAttr;
import org.infoscoop.properties.InfoScoopProperties;
import org.infoscoop.service.PreferenceService;
import org.infoscoop.service.SquareService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProvisioningService {
	private static Log log = LogFactory.getLog(ProvisioningService.class);

	private static final String SQUAREID_DEFAULT = "default";
	private static final String OWNED_SQUARE_NUM = "owned.square.number";
	private static final String ACCOUNT_ATTR_MULTI_FACTOR_AUTH = "multi_factor_authentication";
	private static final String ACCOUNT_ATTR_DOMAIN_SLAVE_USER = "domain_slave_user";

	public void checkParameter(Provisioning user, int index, String execSquareId) throws Exception {
		// default_square_id
		String defaultSquareId = user.defaultSquareId;
		if(defaultSquareId != null && defaultSquareId.length() > 0){

			// exists
			if(!SquareService.getHandle().existsSquare(defaultSquareId))
				throw new IllegalArgumentException("users[" + index + "].default_square_id is not exists.");

			// owned
			/*
				System knows squareid, beacause system has token.
				If system knows squareid, executor is owner its square.
				Pat.1: sent squareid == getSquareId (OK)
				Pat.2: sent squareid == getSquareId.childSquareId (OK)
			*/
			if(!defaultSquareId.equals(execSquareId)
					&& (!SquareService.getHandle().isNotDefaultUntilAncient(defaultSquareId, execSquareId) || !SquareService.getHandle().comparisonParentSquare(defaultSquareId, execSquareId)))
				throw new IllegalArgumentException("users[" + index + "].default_square_id is not owned square.");
		}

		// belong_square
		if(user.belongSquare != null) {
			List<Map<String, String>> belongSquareList = user.belongSquare;
			for(int i = 0; i < belongSquareList.size(); i++) {
				Map<String, String> map = belongSquareList.get(i);
				String squareId = map.get("id");

				// null
				if(squareId == null || squareId.length() < 1)
					throw new IllegalArgumentException("users[" + index + "].belong_square[" + i +"].id is not defined.");

				// exists
				if(!SquareService.getHandle().existsSquare(squareId))
					throw new IllegalArgumentException("users[" + index + "].belong_square[" + i +"].id[" + squareId + "] is not exists.");

				// owned
				/*
					System knows squareid, beacause system has token.
					If system knows squareid, executor is owner its square.
					Pat.1: sent squareid == getSquareId (OK)
					Pat.2: sent squareid == getSquareId.childSquareId (OK)
				*/
				if(!squareId.equals(execSquareId)
						&& (!SquareService.getHandle().isNotDefaultUntilAncient(squareId, execSquareId) || !SquareService.getHandle().comparisonParentSquare(squareId, execSquareId)))
					throw new IllegalArgumentException("users[" + index + "].belong_square[" + i +"].id[" + squareId + "] is not owned square.");
			}
		}

		// attrs
		if(user.attrs != null) {
			List<Map<String, String>> attrList = user.attrs;
			for(int i = 0; i < attrList.size(); i++) {
				Map<String, String> map = attrList.get(i);
				String key = map.get("key");
				String squareId = map.get("square_id");

				// null
				if(key == null || key.length() < 1)
					throw new IllegalArgumentException("users[" + index + "].attrs[" + i +"].key is not defined.");

				// ToDo: Check Repository SystemFlg, but slow down processing speed...
				if(AccountAttributeName.OWNED_SQUARE_NUMBER.equals(key)
						|| AccountAttributeName.UPDATE_PERMISSION.equals(key)
						|| AccountAttributeName.REGISTERED_SQUARE.equals(key)
						|| ACCOUNT_ATTR_MULTI_FACTOR_AUTH.equals(key)
						|| ACCOUNT_ATTR_DOMAIN_SLAVE_USER.equals(key)) {
					throw new IllegalArgumentException("users[" + index + "].attrs[" + i + "].key[" +key + "] is system argument.");
				}

				// squareId
				if(squareId != null && squareId.length() > 0) {
					// exist squareId
					if(!SquareService.getHandle().existsSquare(squareId))
						throw new IllegalArgumentException("users[" + index + "].attrs[" + i +"].square_id[" + squareId + "] is not exists.");

					// owned
					/*
					System knows squareid, beacause system has token.
					If system knows squareid, executor is owner its square.
					Pat.1: sent squareid == getSquareId (OK)
					Pat.2: sent squareid == getSquareId.childSquareId (OK)
				 */
					if(!squareId.equals(execSquareId)
							&& (!SquareService.getHandle().isNotDefaultUntilAncient(squareId, execSquareId) || !SquareService.getHandle().comparisonParentSquare(squareId, execSquareId)))
						throw new IllegalArgumentException("users[" + index + "].attrs[" + i +"].square_id[" + squareId + "] is not owned square.");

					// belong
					/*
					Do not set attribute belonging square.
					*/
					List<Map<String, String>> belongSquareList = user.belongSquare;
					List<String> expect = new ArrayList<String>();
					for(Map<String, String> squareMap : belongSquareList) {
						expect.add(squareMap.get("id"));
					}
					if(!expect.contains(squareId))
						throw new IllegalArgumentException("users[" + index + "].attrs[" + i +"].square_id[" + squareId + "] is not belonging.");
				}
			}
		}
	}

	public void registAccount(Provisioning user, String execSquareId) throws Exception {
		String uid = user.uid;
		IAccountManager manager = AuthenticationService.getInstance().getAccountManager();
		String maxSquare = InfoScoopProperties.getInstance().getProperty(OWNED_SQUARE_NUM);
		if(maxSquare == null || maxSquare.length() > 0) {
			int num = Integer.parseInt(maxSquare);
			if(num < 1)
				maxSquare = "1";
		}

		// set default Square & mysquareid
		String defaultSquareId = "";
		if(user.defaultSquareId != null && user.defaultSquareId.length() > 0) {
			defaultSquareId = user.defaultSquareId;
		}

		// create account
		manager.registUser(
				uid,
				user.password,
				user.givenName,
				user.familyName,
				user.name,
				defaultSquareId,
				user.email,
				maxSquare,
				String.valueOf(1),
				user.requirePasswordReset
		);

		// set square
		if(user.belongSquare != null) {
			for(Map<String, String> map : user.belongSquare) {
				String belongSquareId = map.get("id");
				manager.addSquareId(uid, belongSquareId);

				// set background-image
				String bgImg = map.get("background_image");
				if(bgImg != null && bgImg.length() > 0)
					PreferenceService.getHandle().setBackgroundImage(uid, belongSquareId, bgImg);
			}
		}

		// set default Square & mysquareid
		if(defaultSquareId.length() == 0
				&& user.belongSquare != null
				&& user.belongSquare.size() > 0) {
			Map<String, String> map = user.belongSquare.get(0);
			manager.updateDefaultSquare(uid, map.get("id"));
			manager.updateMySquareId(uid, map.get("id"));
			manager.addSquareId(uid, map.get("id"));
		}

		// set attrs
		if(user.attrs != null) {
			for(Map<String, String> map : user.attrs) {
				String key = map.get("key");
				String val = map.get("value");
				String attrSquareId = map.get("square_id");

				if(attrSquareId != null && attrSquareId.length() < 1)
					attrSquareId = null;

				if(key != null && key.length() > 0)
					manager.setAccountAttribute(uid, key, val, false, attrSquareId);
			}
		}

		// set owner
		manager.setAccountOwner(uid, execSquareId);

		// set system
		manager.setAccountAttribute(uid, ACCOUNT_ATTR_MULTI_FACTOR_AUTH, String.valueOf(BooleanUtils.toInteger(false)), true, null);
		manager.setAccountAttribute(uid, ACCOUNT_ATTR_DOMAIN_SLAVE_USER, String.valueOf(BooleanUtils.toInteger(false)), true, null);

	}

	public void updateAccount(Provisioning user, String execSquareId) throws Exception {
		String uid = user.uid;

		IAccountManager manager = AuthenticationService.getInstance().getAccountManager();
		Map<String, Object> map = new HashMap<>();
		map.put("uid", uid);
		map.put("password", user.password);
		map.put("email", user.email);
		map.put("givenName", user.givenName);
		map.put("familyName", user.familyName);
		map.put("name", user.name);
		map.put("defaultSquareId", user.defaultSquareId);
		map.put("requirePasswordReset", user.requirePasswordReset);

		// belong square
		List<Map<String, String>> belongSquareList = (List<Map<String, String>>)user.belongSquare;
		if(belongSquareList != null) {
			updateAccountSquare(belongSquareList, uid, execSquareId);
		}

		// user attribute
		List<Map<String, String>> attrList = (List<Map<String, String>>)user.attrs;
		if(attrList != null && attrList.size() > 0) {
			updateAttributes(attrList, uid);
		}

		if(map.size() > 1)
			manager.updateUser(map);
	}

	public List<Map<String, String>> getAccountAttribute(IAccount account, String execSquareId) {
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> attrs = account.getAttributes();

		for(Map<String, String> accountAttr : attrs) {
			// not System attribute
			String squareId = accountAttr.get(AccountAttr.PROP_SQUARE_ID);
			if(!Boolean.valueOf(accountAttr.get(AccountAttr.PROP_SYSTEM))) {
				// owned square attribute
				Map<String, String> map = new HashMap<>();
				if(squareId == null) {
					map.put(AccountAttr.PROP_NAME, accountAttr.get(AccountAttr.PROP_NAME));
					map.put(AccountAttr.PROP_VALUE, accountAttr.get(AccountAttr.PROP_VALUE));
				} else if(squareId.equals(execSquareId)
						|| (SquareService.getHandle().isNotDefaultUntilAncient(squareId, execSquareId) && SquareService.getHandle().comparisonParentSquare(squareId, execSquareId))) {
					map.put(AccountAttr.PROP_NAME, accountAttr.get(AccountAttr.PROP_NAME));
					map.put(AccountAttr.PROP_VALUE, accountAttr.get(AccountAttr.PROP_VALUE));
					map.put(AccountAttr.PROP_SQUARE_ID, squareId);
				}
				if(map.size() > 0)
					resultList.add(map);
			}
		}
		return resultList;
	}

	public List<Map<String, String>> getBelongSquare(IAccount account, String execSquareId) {
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<String> belongIds = account.getBelongids();

		for(String belongId : belongIds) {
			Map<String, String> map = new HashMap<>();
			if(belongId.equals(execSquareId)
					|| (SquareService.getHandle().isNotDefaultUntilAncient(belongId, execSquareId) && SquareService.getHandle().comparisonParentSquare(belongId, execSquareId))) {
				map.put("id", belongId);
				resultList.add(map);
			}
		}
		return resultList;
	}

	private void updateAccountSquare(List<Map<String, String>> belongSquareList, String uid, String execSquareId) throws Exception{
		IAccountManager manager = AuthenticationService.getInstance().getAccountManager();
		IAccount account = manager.getUser(uid);

		// repository data
		String mySquareId = account.getMySquareId();
		List<String> repoSquareList = account.getBelongids();

		// temp set
		Set<String> resultSquareIdSet = new HashSet<>();
		// save mysquare
		resultSquareIdSet.add(mySquareId);

		// result set
		for(Map<String, String> squareMap : belongSquareList) {
			boolean addFlg = true;
			// sending data
			String sendingSquareId = squareMap.get("id");
			String bgImg = squareMap.get("background_image");

			if(sendingSquareId.equals(mySquareId)
					|| (!sendingSquareId.equals(execSquareId) && !SquareService.getHandle().comparisonParentSquare(sendingSquareId, execSquareId))) {
				continue;
			}

			// update
			if(repoSquareList.contains(sendingSquareId)) {
				resultSquareIdSet.add(sendingSquareId);
				addFlg = false;
			}

			// create
			if(addFlg) {
				manager.addSquareId(uid, sendingSquareId);
				resultSquareIdSet.add(sendingSquareId);
			}

			// set background Image
			if(bgImg != null && bgImg.length() > 0)
				PreferenceService.getHandle().setBackgroundImage(uid, sendingSquareId, bgImg);
		}

			// delete set
		for(String repoSquareId : repoSquareList) {
			// save other owner square
			if(!repoSquareId.equals(execSquareId) && !SquareService.getHandle().comparisonParentSquare(repoSquareId, execSquareId)) {
				continue;
			}

			if(!resultSquareIdSet.contains(repoSquareId)) {
				manager.removeSquareId(uid, repoSquareId);

				// remove attr
				manager.deleteAccountAttribute(uid, repoSquareId);
			}
		}
	}

	private void updateAttributes(List<Map<String, String>> sendingAttrs, String uid) throws Exception {
		IAccountManager manager = AuthenticationService.getInstance().getAccountManager();

		for(Map<String, String> sendAttr : sendingAttrs) {
			String name = sendAttr.get("key");
			String value = sendAttr.get("value");
			String squareId = sendAttr.get("square_id");
			manager.setAccountAttribute(uid, name, value, false, squareId);
		}
	}
}
