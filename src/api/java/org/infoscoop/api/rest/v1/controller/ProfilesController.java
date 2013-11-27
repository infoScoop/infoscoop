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

package org.infoscoop.api.rest.v1.controller;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.api.rest.v1.response.UserProfile;
import org.infoscoop.api.rest.v1.response.UserProfilesResponse;
import org.infoscoop.dao.model.Account;
import org.infoscoop.service.InformationService;
import org.infoscoop.service.TabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Controller
@RequestMapping("/v1/profiles")
public class ProfilesController extends BaseController{
	private static Log log = LogFactory.getLog(ProfilesController.class);

	@Autowired
	private InformationService informationService;
    
	@XStreamAlias("users")
	List<Account> userList;
	
	/**
	 * 指定ユーザプロファイルを完全に削除します。
	 * 
	 * @param target_uid 削除対象ユーザID
	 * @param apiKey APIキー
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteProfile", method = RequestMethod.GET)
	public UserProfilesResponse deleteProfile(@RequestParam("target_uid") String targetUid) throws Exception{
		System.out.println("targetUid=" + targetUid);
		TabService.getHandle().clearProfile(targetUid);
		return null;
	}
    
	/**
	 * 存在するユーザプロファイルのユーザID一覧を返します。
	 * 
	 * @return profiles
	 * @throws Exception
	 */
	@RequestMapping(value="/uidList", method = RequestMethod.GET)
	public UserProfilesResponse getUidList(){
		UserProfilesResponse profiles = new UserProfilesResponse();

		List<String> uidList = InformationService.getHandle().getUserIdList();
		for(Iterator<String> ite=uidList.iterator();ite.hasNext();){
			profiles.add(new UserProfile(ite.next()));
		}
		
		return profiles;
	}

}
