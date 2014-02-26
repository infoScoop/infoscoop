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

package org.infoscoop.api.oauth2.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.api.dao.OAuth2ProviderClientDetailDAO;
import org.infoscoop.api.dao.model.OAuth2ProviderClientDetail;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

public class ISClientDetailsService implements ClientDetailsService {
	private static Log log = LogFactory.getLog(ISClientDetailsService.class);
	private static String resourceId;

	public void setResourceId(String resourceId){
	    this.resourceId = resourceId;
    }

	public static String getResouceId(){
	    return resourceId;
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		OAuth2ProviderClientDetailDAO provider = OAuth2ProviderClientDetailDAO.newInstance();
		OAuth2ProviderClientDetail pcd = provider.getClientDetailById(clientId);
		
		if(pcd == null){
			throw new ClientRegistrationException("Client Detail not set up.");
		}
		
		BaseClientDetails clientDetails = new BaseClientDetails(clientId, pcd.getResourceIds(), pcd.getScope(), pcd.getGrantTypes(), pcd.getAuthorities());
		clientDetails.setClientSecret(pcd.getSecret());

		return clientDetails;
	}
}
