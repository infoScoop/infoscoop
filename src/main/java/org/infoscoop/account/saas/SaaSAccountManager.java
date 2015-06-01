/* infoScoop OpenSource
 * Copyright (C) 2015 UNIRITA Inc.
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

package org.infoscoop.account.saas;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.*;
import org.infoscoop.acl.ISPrincipal;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.security.auth.Subject;
import java.util.*;

/**
 * @author hr-endoh
 *
 * TODO:getNameInNamespace isn't possible if version of Java isn't 1.5 or more.
 * As for present, the way of making dn is slight.
 */
public class SaaSAccountManager implements IAccountManager{
	private static Log log = LogFactory.getLog(SaaSAccountManager.class);

	private static String USER_SEARCH_BASE_KEY = "userSearchBase";

	private String connectionURL;
	private String connectionName;
	private String connectionPassword;
	private String userBase = "o=infoScoop.org,dc=infoscoop,dc=org";

	private Map<String,String> propAttrMap = new HashMap<String,String>();
	private Map<String,String> propBaseMap = new HashMap<String,String>();

	/**
	 * Account atrribute in User entry;
	 */
	private String accountAttr = "uid";

	/**
	 * Display name atrribute in User entry.
	 */
	private String userNameAttr = null;

	public SaaSAccountManager(String connectionURL, String userSearchBase) throws NamingException{
		this( connectionURL, null, null, userSearchBase);
	}

	public SaaSAccountManager(String connectionURL, String connectionName, String connectionPassword, String userSearchBase) throws NamingException{
		this.connectionURL =  connectionURL;
	    this.connectionName = connectionName;
	    this.connectionPassword = connectionPassword;
	    this.userBase = userSearchBase;
	}


	private DirContext initContext() throws NamingException{
		Hashtable env = new Hashtable();
	    env.put(Context.INITIAL_CONTEXT_FACTORY,
	            "com.sun.jndi.ldap.LdapCtxFactory");

	    env.put(Context.PROVIDER_URL , this.connectionURL);
	    env.put("java.naming.ldap.version" , "3" );
	    if(this.connectionName != null){
	    	env.put(Context.SECURITY_PRINCIPAL , this.connectionName );
	    	env.put(Context.SECURITY_CREDENTIALS , this.connectionPassword );
	    }
	    return new InitialDirContext( env );
	}

	public void setUserSearchAttr(Map propAttrMap){
		this.propAttrMap.putAll(propAttrMap);

		for(Iterator it = propAttrMap.keySet().iterator(); it.hasNext();){
			String propName = (String)it.next();
			this.propBaseMap.put(propName, USER_SEARCH_BASE_KEY);
		}

	}

	public void setUserNameAttr(String userNameAttr) {
		this.userNameAttr = userNameAttr;
	}

	/**
	 * Return the filter of each SearchBase.
	 * @param searchConditionMap
	 * @return
	 */
	private Map getConditionForSearchBase(Map searchConditionMap){
		Map conditionForBase = new HashMap();
		for(Iterator it = searchConditionMap.entrySet().iterator();it.hasNext();){
			Map.Entry entry = (Map.Entry)it.next();
			String propName = (String)entry.getKey();
			String value = (String)entry.getValue();
			if(value == null || "".equals(value)) continue;
			String attrName = (String)this.propAttrMap.get(propName);
			String searchBase = (String)this.propBaseMap.get(propName);

			Map filterMap = (Map)conditionForBase.get(searchBase);
			if(filterMap == null)filterMap = new HashMap();
			filterMap.put(attrName, value);
			conditionForBase.put(searchBase, filterMap);
		}
		return conditionForBase;
	}

	public IAccount getUser(String uid) throws NamingException {
		
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
		NamingEnumeration searchResultEnum;
		Map filters = new HashMap();

		String uidAttrName = "uid";
		if( this.propAttrMap.containsKey("user_id") ) {
			try {
				uidAttrName = ( String )this.propAttrMap.get("user_id");
			} catch( Exception ex ) {
				//ignore
			}
		}
		if(uid != null && !"".equals(uid)) filters.put( uidAttrName, uid);

		DirContext context = null;
		try{
			context = this.initContext();
			searchResultEnum = context.search(userBase, buildFilterByUid(filters), searchControls);
			//roop of retrieval result

			while ( searchResultEnum.hasMore() ){
				SearchResult searchResult =
					(SearchResult)searchResultEnum.next();

				String dn = searchResult.getName() + "," + userBase;
				SaaSAccount user = createSaaSUser(dn, searchResult.getAttributes());

				return user;
			}

			return null;
		}finally{
			if(context != null)
				context.close();
		}
	}

	public List searchUser(Map searchConditionMap) throws Exception {
		Map confitionForBase = getConditionForSearchBase(searchConditionMap);

		Collection users = new TreeSet(new Comparator(){

			public int compare(Object o1, Object o2) {
				try{
				SaaSAccount user1 = (SaaSAccount)o1;
				SaaSAccount user2 = (SaaSAccount)o2;

				return user1.getUid().compareTo(user2.getUid());
				}catch(Exception e){
					log.error("", e);
					return  0;
				}
			}

		});

		DirContext context = null;
		try{
			context = this.initContext();
			Map userFilterMap = (Map)confitionForBase.get(USER_SEARCH_BASE_KEY);

			if( userFilterMap != null ){
				users = searchFromUsers(context, userFilterMap);
			}

			List result = new ArrayList();
			for(Iterator it = users.iterator(); it.hasNext();){
				SaaSAccount user = (SaaSAccount)it.next();
				result.add(user);
			}

			return result;
		}finally{
			context.close();
		}
		
	}

	@Override
	public Collection<PrincipalDef> getPrincipalDefs() {
		return new ArrayList<PrincipalDef>();
	}

	public void login(String userid, String password) throws AuthenticationException{
		try {
			SaaSAccount user = (SaaSAccount) getUser(userid);
			if(user == null){
				throw new AuthenticationException(userid + " is not found.");
			}
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		    env.put(Context.PROVIDER_URL , this.connectionURL);
			env.put("java.naming.ldap.version" , "3" );
			env.put(Context.SECURITY_PRINCIPAL , user.getDn() );
			env.put(Context.SECURITY_CREDENTIALS , password );

			new InitialDirContext( env );
			
		} catch (NamingException e) {
			throw new AuthenticationException(e);
		}
	}

	public Subject getSubject(String userid) throws Exception {
		SaaSAccount user = (SaaSAccount) getUser(userid);
		if(user == null){
			throw new AuthenticationException(userid + " is not found.");
		}
		Subject loginUser = new Subject();
		ISPrincipal p = new ISPrincipal(ISPrincipal.UID_PRINCIPAL, user.getUid());
		p.setDisplayName(user.getName());
		loginUser.getPrincipals().add(p);

		return loginUser;
	}


	public boolean enableChangePassword(){
		return false;
	}

	public void changePassword(String userid, String password,
			String oldPassword) {
		throw new UnsupportedOperationException();
	}

	private List searchFromUsers(DirContext context, Map filters) throws NamingException{

		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
		NamingEnumeration searchResultEnum;

		String filter = buildFilter(filters);
		if(log.isInfoEnabled())
			log.info("Search User from " + userBase + " by " +  filter );
		searchResultEnum = context.search(userBase, filter, searchControls);
		//roop of retrieval result

		List users = new ArrayList();
		while ( searchResultEnum.hasMore() ){
			SearchResult searchResult =
				(SearchResult)searchResultEnum.next();
			String dn = searchResult.getName() + "," + userBase;
			SaaSAccount user = createSaaSUser(dn, searchResult.getAttributes());
			users.add(user);
		}
		return users;
	}

	private String buildFilter(Map filters){
		StringBuffer filter = new StringBuffer();

		if(filters.size() > 1){
			filter.append("(&");
		}
		for(Iterator it = filters.entrySet().iterator(); it.hasNext();){
			Map.Entry entry = (Map.Entry)it.next();
			filter.append("(").append(entry.getKey()).append("=").append(entry.getValue()).append("*)");
		}

		if(filters.size() > 1){
			filter.append(")");
		}
		return filter.toString();
	}

	private String buildFilterByUid(Map filters){
		StringBuffer filter = new StringBuffer();

		if(filters.size() > 1){
			filter.append("(&");
		}
		for(Iterator it = filters.entrySet().iterator(); it.hasNext();){
			Map.Entry entry = (Map.Entry)it.next();
			filter.append("(").append(entry.getKey()).append("=").append(entry.getValue()).append(")");
		}

		if(filters.size() > 1){
			filter.append(")");
		}
		return filter.toString();
	}

	private SaaSAccount createSaaSUser(String dn, Attributes attrs) throws NamingException{
		String uid = null;
		if( propAttrMap.containsKey("user_id") ) {
			uid = getAttribute( attrs,propAttrMap.get("user_id") );
		} else {
			uid = getAttribute( attrs,accountAttr );
		}
		String mail = uid;

		String displayName = null;
		if( userNameAttr != null ) {
			displayName = getAttribute( attrs,this.userNameAttr );
		} else if( propAttrMap.containsKey("user_name")) {
			displayName = getAttribute( attrs,propAttrMap.get("user_name"));
		}

		String defaultSquareId = null;
		if( propAttrMap.containsKey("user_default_square")) {
			defaultSquareId = getAttribute(attrs, propAttrMap.get("user_default_square"));
		}

		List<String> mails = new ArrayList<String>();
		if( propAttrMap.containsKey("user_email")) {
			mails = getAttributes(attrs, propAttrMap.get("user_email"));
		}

		List<String> belongSquareId = new ArrayList<String>();
		if( propAttrMap.containsKey("user_belong_square")) {
			belongSquareId = getAttributes(attrs, propAttrMap.get("user_belong_square"));
		}

		SaaSAccount user = new SaaSAccount(dn,uid,mail,displayName,defaultSquareId,belongSquareId,mails);

		return user;
	}

	private String getAttribute( Attributes attrs,String name ) throws NamingException {
		if( name == null )
			return null;

		Attribute attr = attrs.get( name );
		if( attr != null ){
			try{
				return ( String )attr.get( 0 );
			}catch( ArrayIndexOutOfBoundsException ex ){}
		}

		return null;
	}

	private List<String> getAttributes( Attributes attrs, String name ) throws NamingException {
		if(name == null) {
			return null;
		}

		Attribute attr = attrs.get(name);
		List<String> attributeList = new ArrayList<String>();
		if(attr != null){
			NamingEnumeration n = attr.getAll();

			while(n.hasMoreElements()) {
				String item = (String) n.next();
				attributeList.add(item);
			}
		}

		return attributeList;
	}
}
