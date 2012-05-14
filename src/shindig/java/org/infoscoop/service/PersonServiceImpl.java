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

package org.infoscoop.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.protocol.model.SortOrder;
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.IGroup;
import org.infoscoop.account.SearchUserService;
import org.infoscoop.util.SpringUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class PersonServiceImpl implements PersonService {
	private static Log log = LogFactory.getLog(PersonServiceImpl.class);

	private BeanConverter converter;
	private static final Comparator<Person> NAME_COMPARATOR = new Comparator<Person>() {
		public int compare(Person person, Person person1) {
			String name = person.getName().getFormatted();
			String name1 = person1.getName().getFormatted();
			return name.compareTo(name1);
		}
	};
	
	@Inject
	public PersonServiceImpl(@Named("shindig.bean.converter.json") BeanConverter converter) throws Exception {
	    this.converter = converter;
	}
	
	public Future<RestfulCollection<Person>> getPeople(Set<UserId> userIds, GroupId groupId,
		      CollectionOptions options, Set<String> fields, SecurityToken token) throws ProtocolException {
	    List<Person> result = Lists.newArrayList();
	    try {
	      Set<String> idSet = getIdSet(userIds, groupId, token);
	      SearchUserService search = (SearchUserService) SpringUtil.getBean("searchUserService");
	      for(Iterator<String> itr = idSet.iterator();itr.hasNext();){
	    	  IAccount account = getUser(itr.next(), search);
	    	  if(account == null)
	    		  continue;
	    	  
		      JSONObject person = new JSONObject();
		      person.put(Person.Field.ID.toString(), account.getUid());
	    	  Person personObj = getPersonObject(filterFields(person, fields, Person.class), account);
	    	  result.add(personObj);
		  }
	  
	   	  if (GroupId.Type.self == groupId.getType() && result.isEmpty()) {
	   		  throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST, "People '" + idSet + "' not found");
		  }
	    	  
	      if (options.getSortBy().equals(Person.Field.NAME.toString())) {
	    	  Collections.sort(result, NAME_COMPARATOR);
		   	  if (options.getSortOrder() == SortOrder.descending) {
		   		  Collections.reverse(result);
		   	  }
		  }
	      
	      int totalSize = result.size();
	      int last = options.getFirst() + options.getMax();
	      result = result.subList(options.getFirst(), Math.min(last, totalSize));

	      return ImmediateFuture.newInstance(new RestfulCollection<Person>(result, options.getFirst(), totalSize, options.getMax()));	      

	    } catch (JSONException je) {
	      throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, je.getMessage(), je);
	    } catch (Exception e){
	    	throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(),e);
	    }
	}

	public Future<Person> getPerson(UserId id, Set<String> fields, SecurityToken token) throws ProtocolException {
	    try {
		    SearchUserService search = (SearchUserService) SpringUtil.getBean("searchUserService");
	    	IAccount account = getUser(id.getUserId(token), search);
	    	JSONObject person = new JSONObject();
	    	person.put(Person.Field.ID.toString(), account.getUid());
	    	Person personObj = getPersonObject(filterFields(person, fields, Person.class), account);	
	    	
	    	return ImmediateFuture.newInstance(personObj);
	    } catch (JSONException je) {
	    	throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, je.getMessage(),je);
	    } catch (Exception e){
	    	throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST, "Person '" + id.getUserId(token) + "' not found");
		}
	}

	public Set<String> getIdSet(Set<UserId> users, GroupId group, SecurityToken token) throws JSONException {
		Set<String> ids = Sets.newLinkedHashSet();
		for (UserId user : users) {
			String userId = user.getUserId(token);
			ids.addAll(ImmutableSortedSet.of(userId));
//			ids.addAll(getIdSet(user, group, token));
		}
		return ids;
	}
/*	
	private Set<String> getIdSet(UserId user, GroupId group, SecurityToken token) throws JSONException {
		String userId = user.getUserId(token);
		return ImmutableSortedSet.of(userId);
		
		/*
		if (group == null) {
		}

		Set<String> returnVal = Sets.newLinkedHashSet();
		switch (group.getType()) {
			case all:
			case friends:
			case groupId:
			case self:
				returnVal.add(userId);
				break;
		}
		return returnVal;
	}
*/	
	public IAccount getUser(String uid, SearchUserService search) throws Exception {
		if (!SearchUserService.isAvailable())
			return null;
		try {
			IAccount user = search.getUser(uid);
			return user;
		} catch (NoSuchBeanDefinitionException e) {
			log.warn("searchUserService not found.", e);
			return null;
		}
	}

	public Person getPersonObject(Person obj, IAccount account) throws JSONException {
		//DisplayName
		obj.setDisplayName(account.getName());
		
		//EMails
		List<ListField> mailList = new ArrayList<ListField>();
		List<String> mails = account.getMails();
		if(mails.size()!=0){
			for(String mail : mails){
				JSONObject mailObj = new JSONObject();
				mailObj.put(ListField.Field.VALUE.toString(), mail);
				mailObj.put(ListField.Field.TYPE.toString(), "");
				mailList.add(converter.convertToObject(mailObj.toString(), ListField.class));
			}
		}
				obj.setEmails(mailList);
		
		//Organization
		IGroup[] groups = account.getGroups();
		List<Organization> orgList = new ArrayList<Organization>();
		if(groups != null){
			for(int i = 0; i < groups.length;i++){
				JSONObject orgObj = new JSONObject();
				orgObj.put(Organization.Field.NAME.toString(), groups[i].getName());
				orgList.add(converter.convertToObject(orgObj.toString(), Organization.class));
			}
		}else{
			JSONObject orgObj = new JSONObject();
			orgObj.put(Organization.Field.NAME.toString(), account.getGroupName());			
			orgList.add(converter.convertToObject(orgObj.toString(), Organization.class));
		}
		obj.setOrganizations(orgList);
		return obj;
	}
	
	public <T> T filterFields(JSONObject object, Set<String> fields, Class<T> clz) throws JSONException {
		if (!fields.isEmpty()) {
			// Create a copy with just the specified fields
			object = new JSONObject(object, fields.toArray(new String[fields.size()]));
		}
		return converter.convertToObject(object.toString(), clz);
	}
}
