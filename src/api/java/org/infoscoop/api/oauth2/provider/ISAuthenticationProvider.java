/* 
 * infoScoop OpenSource
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.dao.model.Portaladmins;
import org.infoscoop.service.PortalAdminsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class ISAuthenticationProvider implements AuthenticationProvider {
	private static Log log = LogFactory.getLog(ISAuthenticationProvider.class);

	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_CLIENT = "ROLE_CLIENT";
	
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String userid = auth.getName();
        String password = auth.getCredentials().toString();
        
        AuthenticationService service = AuthenticationService.getInstance();
        try{
        	// login
        	service.login(userid, password);
        	
        	// authority
            List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
            PortalAdminsService portalService = PortalAdminsService.getHandle();
        	portalService.getPortalAdmins();
        	Portaladmins admin = portalService.getPortalAdmin(userid);
        	if(admin != null){
        		grantedAuths.add(new SimpleGrantedAuthority(ROLE_ADMIN));
        	}else{
        		grantedAuths.add(new SimpleGrantedAuthority(ROLE_USER));
        	}
        	
        	if(log.isDebugEnabled())
        		log.debug("complete login "+userid+" - authotiry:" + grantedAuths.toString());
        	
            return new UsernamePasswordAuthenticationToken(userid, password, grantedAuths);
        } catch(AuthenticationException e) {
        	// login error
        	log.error(e);
        	e.printStackTrace();
        	return null;
        } catch(org.infoscoop.account.AuthenticationException e) {
        	log.error(e);
        	e.printStackTrace();
        	return null;
        } catch (Exception ex) {
        	log.error(ex);
        	ex.printStackTrace();
            return null;
		}
	}

	@Override
	public boolean supports(Class<?> token) {
		return token.equals(UsernamePasswordAuthenticationToken.class);
	}
}
