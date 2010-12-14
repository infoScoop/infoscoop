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

package org.infoscoop.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.dao.model.Role;
import org.infoscoop.dao.model.RolePrincipal;

/**
 * The class which get information from a tablayout table depending on role information.
 * 
 * @author nishiumi
 *
 */
public class RoleUtil {
	private static final long serialVersionUID = "org.infoscoop.util.RoleUtil".hashCode();
	
	private static final Log log = LogFactory.getLog(RoleUtil.class);
	
	public static boolean isAccessible(boolean isMangerView , Set<Role> roles){
		boolean canDisplay = false;
		for(Role role: roles){
			for(RolePrincipal p: role.getRolePrincipals()){
				Subject loginUser = SecurityController.getContextSubject();
				for(ISPrincipal principal : loginUser.getPrincipals(ISPrincipal.class)){
					if(isMangerView && ISPrincipal.ADMINISTRATOR_PRINCIPAL == principal.getType()){
						canDisplay = true;
						break;
					}else if(p.getType().equalsIgnoreCase(principal.getType()) && 
							p.getName().equalsIgnoreCase(principal.getName())){
						canDisplay = true;
						break;
					}
				}
			}
		}
		return canDisplay;
	}
	
}
