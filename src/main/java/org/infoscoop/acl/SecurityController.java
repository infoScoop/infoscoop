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

package org.infoscoop.acl;

import java.util.Collection;
import java.util.Iterator;

import javax.security.auth.Subject;

public class SecurityController {
	public static String menuURIPrefix = "menu://";
	
	private static ThreadLocal contextSubject = new ThreadLocal();
	
	
	/**
	 * get the subject persion from all registered SecurityAgent.
	 * @param uid
	 * @return
	 */
	public static void registerContextSubject(Subject subject){
		
		contextSubject.set(subject);
	}

	/**
	 * delete the Subject that registered in a current thread.
	 */
	public static void clearContextSubject(){
		contextSubject.set(null);
	}

	/**
	 * get the Subject that registered in a current thread.
	 * @return
	 */
	public static Subject getContextSubject(){
		return (Subject)contextSubject.get();
	}
	
	/**
	 * return the ISPrincipal that specifyed type.
	 * @param type
	 * @return
	 */
	public static ISPrincipal getPrincipalByType(String type){
		Subject loginUser = SecurityController.getContextSubject();
		Collection principals = loginUser.getPrincipals(ISPrincipal.class);
		
		for(Iterator it = principals.iterator(); it.hasNext();){
			ISPrincipal p = (ISPrincipal)it.next();
			if(type.equals(p.getType())) return p;
		}
		
		return null;
	}
}
