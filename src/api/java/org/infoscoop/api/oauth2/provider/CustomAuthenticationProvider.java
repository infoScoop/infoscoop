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
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;

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

public class CustomAuthenticationProvider implements AuthenticationProvider {
	private static Log log = LogFactory.getLog(CustomAuthenticationProvider.class);

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String userid = auth.getName();
        String password = auth.getCredentials().toString();
        
        AuthenticationService service = AuthenticationService.getInstance();
        try{
        	// login
        	service.login(userid, password);
        	
        	// authority
        	PortalAdminsService portalService = PortalAdminsService.getHandle();
        	Portaladmins admins = portalService.getPortalAdmin(userid);
        	String permission = admins.getAdminrole().getPermission();
        	System.out.println(permission);
        	
            List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new UsernamePasswordAuthenticationToken(userid, password, grantedAuths);
        } catch(AuthenticationException | org.infoscoop.account.AuthenticationException e) {
        	// login error
        	e.printStackTrace();
        	return null;
        } catch (Exception ex) {
        	// normal user
            List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new UsernamePasswordAuthenticationToken(userid, password, grantedAuths);
		}
	}

	@Override
	public boolean supports(Class<?> token) {
		return token.equals(UsernamePasswordAuthenticationToken.class);
	}
}
