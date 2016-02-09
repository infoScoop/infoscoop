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
import org.infoscoop.account.IAccountManager;
import org.infoscoop.account.helper.AccountHelper;
import org.infoscoop.account.simple.AccountAttributeName;
import org.infoscoop.api.rest.v1.response.model.Provisioning;
import org.infoscoop.properties.InfoScoopProperties;
import org.infoscoop.service.PreferenceService;
import org.infoscoop.service.SquareService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProvisioningService {
	private static Log log = LogFactory.getLog(ProvisioningService.class);

	private static final String SQUAREID_DEFAULT = "default";
	private static final String OWNED_SQUARE_NUMBER = "owned.square.number";

	public boolean checkParameterForRegistration(Provisioning user, int index) throws Exception {
		// uid:
		if(AccountHelper.isExistsUser(user.uid)){
			throw new IllegalArgumentException("users[" + index + "].uid is exists.");
		}

		// default_square_id
		if(user.defaultSquareId != null
				&& user.defaultSquareId.length() > 0
				&& !SquareService.getHandle().existsSquare(user.defaultSquareId)){
			throw new IllegalArgumentException("users[" + index + "].default_square_id is not exists.");
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

				if(AccountAttributeName.OWNED_SQUARE_NUMBER.equals(key)
						|| AccountAttributeName.UPDATE_PERMISSION.equals(key)) {
					throw new IllegalArgumentException("users[" + index + "].attrs[" + i + "].key[" +key + "] is system argument.");
				}
			}
		}

		return true;
	}

	public void registAccount(Provisioning user) throws Exception {
		String uid = user.uid;
		IAccountManager manager = AuthenticationService.getInstance().getAccountManager();
		String maxSquare = InfoScoopProperties.getInstance().getProperty(OWNED_SQUARE_NUMBER);
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
		manager.updateDefaultSquare(uid, user.defaultSquareId);

		// set attrs
		for(Map<String, String> map : user.attrs) {
			String key = map.get("key");
			String val = map.get("value");

			if(key != null && key.length() > 0)
				manager.setAccountAttributeValue(uid, key, val, false);
		}
	}
}
