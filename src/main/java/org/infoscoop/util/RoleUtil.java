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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;
import org.infoscoop.context.UserContext;

/**
 * The class which get information from a tablayout table depending on role information.
 * 
 * @author nishiumi
 *
 */
public class RoleUtil {
	private static final long serialVersionUID = "org.infoscoop.util.RoleUtil".hashCode();
	
	private static final Log log = LogFactory.getLog(RoleUtil.class);
	
	/**
	 * confirmation the roll information.
	 * 
	 * @param type
	 * @param regx
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static boolean isPermitted(String type, String regx) throws ClassNotFoundException {
		return getPermittedMatchList(type, regx) != null;
	}
	
	/**
	 * @param type
	 * @param regx
	 * @return The result string list matched to group in regx, when type is not matched to regx, return null
	 * @throws ClassNotFoundException
	 */
	public static List<String> getPermittedMatchList(String type, String regx) throws ClassNotFoundException {
		Subject loginUser = SecurityController.getContextSubject();
		List<String> retVal = null;
		if(loginUser != null){
			Collection<ISPrincipal> principals = loginUser.getPrincipals(ISPrincipal.class);
			if(log.isInfoEnabled())
				log.info("LoginUser Prinipales:" + principals);
			
			try {
				Pattern pattern = Pattern.compile(regx);
				String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
				ISPrincipal expectPrincipal = null;
				for(ISPrincipal p: principals){
					if (type.equals(p.getType()) 
							&& p.getName() != null
							&& (p.getSquareId() == null || p.getSquareId().equals(squareid))){
						if(expectPrincipal != null) {
							if(p.getSquareId() != null) {
								expectPrincipal = p;
								break;
							}
						} else {
							expectPrincipal = p;
						}
					}
				}
				if(expectPrincipal != null) {
					Matcher matcher = pattern.matcher(expectPrincipal.getName());
					retVal = matcher2List(matcher);
				}
			} catch (PatternSyntaxException e) {
				log.warn("\"" + regx + "\" is invalid regular expression.");
			}
		}
		if (log.isInfoEnabled())
			log.info("PrincipalType=" + type + ",RegExp=" + regx + ", isPermitted="
					+ retVal);
		return retVal;
	}
	
	private static List<String> matcher2List(Matcher matcher){
		int groupCount = matcher.groupCount();
		if( groupCount == 0 && matcher.matches()){
			return  new ArrayList<String>();
		}else if(matcher.find()){
			List<String> results = new ArrayList<String>();
			for(int i = 1; i <= groupCount; i++){
				results.add(matcher.group(i));
			}
			return results;
		}else{
			return null;
		}
	}
	
}
