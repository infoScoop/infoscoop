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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.IAccountManager;
import org.infoscoop.account.helper.AccountHelper;
import org.infoscoop.account.simple.AccountAttributeName;
import org.infoscoop.api.rest.v1.response.model.Provisioning;
import org.infoscoop.properties.InfoScoopProperties;
import org.infoscoop.service.PreferenceService;
import org.infoscoop.service.SquareService;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProvisioningService {
	private static Log log = LogFactory.getLog(ProvisioningService.class);

	private static final String SQUAREID_DEFAULT = "default";
	private static final String OWNED_SQUARE_NUM = "owned.square.number";

	public void checkParameterForRegistration(Provisioning user, int index, String execSquareId) throws Exception {
		// uid:
		if(AccountHelper.isExistsUser(user.uid)){
			throw new IllegalArgumentException("users[" + index + "].uid is exists.");
		}

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

				// null
				if(key == null || key.length() < 1)
					throw new IllegalArgumentException("users[" + index + "].attrs[" + i +"].key is not defined.");

				// ToDo: Check Repository SystemFlg, but slow down processing speed...
				if(AccountAttributeName.OWNED_SQUARE_NUMBER.equals(key)
						|| AccountAttributeName.UPDATE_PERMISSION.equals(key)
						|| AccountAttributeName.REGISTERED_SQUARE.equals(key)) {
					throw new IllegalArgumentException("users[" + index + "].attrs[" + i + "].key[" +key + "] is system argument.");
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

		// create mysquare
		String squareId = SquareService.generateSquareId();
		SquareService.getHandle().createSquare(squareId, uid, "", SQUAREID_DEFAULT, uid);

		// create account
		manager.registUser(
				uid,
				user.password,
				user.givenName,
				user.familyName,
				squareId,
				user.email,
				maxSquare,
				String.valueOf(1)
		);

		// set square
		for(Map<String, String> map : user.belongSquare) {
			String belongSquareId = map.get("id");
			manager.addSquareId(uid, belongSquareId);

			// set background-image
			String bgImg = map.get("background_image");
			if(bgImg != null && bgImg.length() > 0)
				PreferenceService.getHandle().setBackgroundImage(uid, belongSquareId, bgImg);
		}

		// set default Square
		if(user.defaultSquareId != null && user.defaultSquareId.length() > 0)
			manager.updateDefaultSquare(uid, user.defaultSquareId);

		// set attrs
		for(Map<String, String> map : user.attrs) {
			String key = map.get("key");
			String val = map.get("value");

			if(key != null && key.length() > 0)
				manager.setAccountAttribute(uid, key, val, false);
		}

		// set owner
		manager.setAccountOwner(uid, execSquareId);
	}

	public void checkParameterForUpdate(Provisioning user, int index, String execSquareId) throws Exception {
		String uid = user.uid;

		// uid:
		if(!AccountHelper.isExistsUser(uid)){
			throw new IllegalArgumentException("users[" + index + "].uid is not exists.");
		}

		// permission check
		if(!AccountHelper.isUpdateUser(uid, execSquareId)) {
			throw new PermissionDeniedDataAccessException("Permission denied", new Throwable());
		}

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

				// null
				if(key == null || key.length() < 1)
					throw new IllegalArgumentException("users[" + index + "].attrs[" + i +"].key is not defined.");

				// ToDo: Check Repository SystemFlg, but slow down processing speed...
				if(AccountAttributeName.OWNED_SQUARE_NUMBER.equals(key)
						|| AccountAttributeName.UPDATE_PERMISSION.equals(key)
						|| AccountAttributeName.REGISTERED_SQUARE.equals(key)) {
					throw new IllegalArgumentException("users[" + index + "].attrs[" + i + "].key[" +key + "] is system argument.");
				}
			}
		}
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

			if(!resultSquareIdSet.contains(repoSquareId))
				manager.removeSquareId(uid, repoSquareId);
		}
	}

	private void updateAttributes(List<Map<String, String>> sendingAttrs, String uid) throws Exception {
		IAccountManager manager = AuthenticationService.getInstance().getAccountManager();

		for(Map<String, String> sendAttr : sendingAttrs) {
			String name = sendAttr.get("key");
			String value = sendAttr.get("value");
			manager.setAccountAttribute(uid, name, value, false);
		}
	}
}
