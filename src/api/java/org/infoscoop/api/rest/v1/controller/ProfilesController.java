/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
