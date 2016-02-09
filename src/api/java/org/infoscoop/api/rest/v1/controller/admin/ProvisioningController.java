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

package org.infoscoop.api.rest.v1.controller.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.api.rest.v1.controller.BaseController;
import org.infoscoop.api.rest.v1.response.model.Provisioning;
import org.infoscoop.api.rest.v1.response.model.ProvisioningList;
import org.infoscoop.api.rest.v1.service.admin.ProvisioningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/v1/admin/provisioning")
public class ProvisioningController extends BaseController{
	private static Log log = LogFactory.getLog(ProvisioningController.class);

	@Autowired
	ProvisioningService service;

	/**
	 * create account
	 *
	 * @throws Exception
	 */
	@RequestMapping(value="/users", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
					+ ";charset=utf-8")
	@ResponseStatus(HttpStatus.CREATED)
	public void createAccounts(@RequestBody @Valid ProvisioningList provisioningList) throws Exception {
		List<Provisioning> provisioning = provisioningList.users;

		for(int i = 0; i < provisioning.size(); i++){
			Provisioning user = provisioning.get(i);
			// validation
			service.checkParameterForRegistration(user, i);

			// registration
			service.registAccount(user);
		}
	}

	/**
	 * update account
	 *
	 * @throws Exception
	 */
	@RequestMapping(value="/users", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateAccounts(){
//		UserProfilesResponse profiles = new UserProfilesResponse();
//
//		List<String> uidList = InformationService.getHandle().getUserIdList(getSquareId());
//		for(Iterator<String> ite=uidList.iterator();ite.hasNext();){
//			profiles.add(new UserProfile(ite.next()));
//		}
	}
}
