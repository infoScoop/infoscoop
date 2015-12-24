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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.api.rest.v1.controller.BaseController;
import org.infoscoop.api.rest.v1.response.UserProfilesResponse;
import org.infoscoop.api.rest.v1.response.model.UserProfile;
import org.infoscoop.service.InformationService;
import org.infoscoop.service.TabService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/v1/admin/profiles")
public class ProfilesController extends BaseController{
	private static Log log = LogFactory.getLog(ProfilesController.class);

	/**
	 * delete user profile
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/{uid}", method = RequestMethod.DELETE)
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void deleteProfile(@PathVariable("uid") String uid) throws Exception{
		TabService.getHandle().clearProfile(uid);
	}

	/**
	 * returning exist user profile
	 * 
	 * @return profiles
	 * @throws Exception
	 */
	@RequestMapping(value="/user", method = RequestMethod.GET)
	@ResponseBody
	public UserProfilesResponse getUidList(){
		UserProfilesResponse profiles = new UserProfilesResponse();

		List<String> uidList = InformationService.getHandle().getUserIdList(getSquareId());
		for(Iterator<String> ite=uidList.iterator();ite.hasNext();){
			profiles.add(new UserProfile(ite.next()));
		}
		
		return profiles;
	}
}
