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
import org.infoscoop.account.IAccount;
import org.infoscoop.account.helper.AccountHelper;
import org.infoscoop.api.rest.v1.controller.BaseController;
import org.infoscoop.api.rest.v1.response.model.Provisioning;
import org.infoscoop.api.rest.v1.response.model.ProvisioningList;
import org.infoscoop.api.rest.v1.service.admin.ProvisioningService;
import org.infoscoop.service.SquareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/v1/admin/provisioning")
public class ProvisioningController extends BaseController{
	private static Log log = LogFactory.getLog(ProvisioningController.class);

	@Autowired
	ProvisioningService service;

	/**
	 * get account
	 *
	 * @throws Exception
	 */
	@RequestMapping(value="/users", method = RequestMethod.GET,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
					+ ";charset=utf-8")
	@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
	@ResponseBody()
	public void getAccounts() throws Exception {}

	/**
	 * get account in {squareId}
	 *
	 * @pathvariable {squareId}
	 * @throws Exception
	 */
	@RequestMapping(value="/users/{squareId}", method = RequestMethod.GET,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
					+ ";charset=utf-8")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody()
	public ProvisioningList getAccountsBySquareId(@PathVariable("squareId") String squareId) throws Exception {
		String execSquareId = getSquareId();
		ProvisioningList provisioningList = new ProvisioningList();
		List<Provisioning> list = new ArrayList<>();
		provisioningList.users = list;

	if(!squareId.equals(execSquareId) && !SquareService.getHandle().comparisonParentSquare(squareId, execSquareId))
		throw new PermissionDeniedDataAccessException(squareId + " is not owned square.", new Throwable());

		List<IAccount> accounts = AccountHelper.searchUsersBySquareId(squareId);
		for(IAccount account : accounts) {
			Provisioning provisioning = new Provisioning();
			provisioning.uid = account.getUid();
			list.add(provisioning);
		}

		return provisioningList;
	}

	/**
	 * create account
	 *
	 * @throws Exception
	 */
	@RequestMapping(value="/users", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
					+ ";charset=utf-8")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<String> createAccounts(@RequestBody @Validated(Provisioning.Create.class) ProvisioningList provisioningList) throws Exception {
		List<Provisioning> provisioning = provisioningList.users;
		String execSquareId = getSquareId();
		List<String> results = new ArrayList<>();

		for(int i = 0; i < provisioning.size(); i++){
			Provisioning user = provisioning.get(i);
			// validation
			try{
				service.checkParameterForRegistration(user, i, execSquareId);
			} catch(Exception e) {
				results.add(e.getMessage());
				continue;
			}

			// registration
			service.registAccount(user, execSquareId);
		}

		if(results != null && results.size() < 1)
			results.add("All Account Created.");

		return results;
	}

	/**
	 * update account
	 *
	 * @throws Exception
	 */
	@RequestMapping(value="/users", method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
					+ ";charset=utf-8")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<String> updateAccounts(@RequestBody @Validated(Provisioning.Update.class) ProvisioningList provisioningList) throws Exception {
		List<Provisioning> provisioning = provisioningList.users;
//		String execSquareId = getSquareId();
		String execSquareId = "unirita@unirita.co.jp";
		List<String> results = new ArrayList<>();

		for(int i = 0; i < provisioning.size(); i++){
			Provisioning user = provisioning.get(i);
			// validation
			try{
				service.checkParameterForUpdate(user, i, execSquareId);
			} catch(Exception e) {
				results.add(e.getMessage());
				continue;
			}
			// registration
			service.updateAccount(user, execSquareId);
		}

		if(results != null && results.size() < 1)
			results.add("All Account Updated.");

		return results;
	}
}
