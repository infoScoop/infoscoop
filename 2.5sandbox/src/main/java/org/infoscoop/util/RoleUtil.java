package org.infoscoop.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.security.auth.Subject;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.acl.SecurityController;

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
		Subject loginUser = SecurityController.getContextSubject();
		boolean retVal = false;
		if(loginUser == null){
			retVal = false;
		}else{
			Collection principals = loginUser.getPrincipals(ISPrincipal.class);
			if(log.isInfoEnabled())
				log.info("LoginUser Prinipales:" + principals);
			Pattern pattern = null;
			try {
				pattern = Pattern.compile(regx);
				for(Iterator it = principals.iterator(); it.hasNext();){
					ISPrincipal p = (ISPrincipal)it.next();
					if (type.equals(p.getType()) 
							&& p.getName() != null
							&& pattern.matcher(p.getName()).matches()) {
						retVal = true;
						break;
					}
				}
			} catch (PatternSyntaxException e) {
				log.warn("\"" + regx + "\" is invalid regular expression.");
				retVal = false;
			}
		}
		if (log.isInfoEnabled())
			log.info("PrincipalType=" + type + ",RegExp=" + regx + ", isPermitted="
					+ retVal);
		return retVal;
	}
	
}
