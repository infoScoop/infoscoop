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
