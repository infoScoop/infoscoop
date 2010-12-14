package org.infoscoop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import org.infoscoop.acl.SecurityController;
import org.infoscoop.dao.TabTemplateDAO;
import org.infoscoop.dao.model.Role;
import org.infoscoop.dao.model.TabTemplate;
import org.infoscoop.util.RoleUtil;
import org.infoscoop.util.SpringUtil;

public class TabTemplateService {
	
	private TabTemplateDAO dao;
	
	public void setTabTemplateDAO(TabTemplateDAO dao){
		this.dao = dao;
	}

	public static TabTemplateService getHandle() {
		return (TabTemplateService)SpringUtil.getBean("TabTemplateService");
	}
	
	public List<TabTemplate> getMyTabTemplate(){
		List<TabTemplate> templates = dao.all();
		Subject loginUser = SecurityController.getContextSubject();
		List<TabTemplate> results = new ArrayList<TabTemplate>();
		for(TabTemplate template : templates){
			if(template.getPublishBool()){
				Set<Role> roles = template.getRoles();
				if(roles.isEmpty()){
					results.add(template);
				}else{
					if(RoleUtil.isAccessible(false, roles)){
						results.add(template);					
					}
				}
			}
		}
		return results;
	}
}
