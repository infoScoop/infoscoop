package org.infoscoop.manager.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.infoscoop.dao.GroupDAO;
import org.infoscoop.dao.RoleDAO;
import org.infoscoop.dao.UserDAO;
import org.infoscoop.dao.model.Group;
import org.infoscoop.dao.model.Role;
import org.infoscoop.dao.model.RolePrincipal;
import org.infoscoop.dao.model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

@Controller
public class RoleController {
	@RequestMapping(method=RequestMethod.GET)
	public void index(Model model) throws Exception {
		List<Role> roles = RoleDAO.newInstance().all();
		model.addAttribute("roles", roles);
	}

	@RequestMapping(method=RequestMethod.GET)
	public void selectRole( Model model){
		List<Role> roles = RoleDAO.newInstance().all();
		model.addAttribute("roles", roles);
	}
	
	@RequestMapping(method=RequestMethod.GET)
	@Transactional
	public void edit( @RequestParam(value="id", required=false) String roleId, Model model)
			throws Exception {
		Role role;
		if(roleId == null){
			role = new Role();
		}else{
			role = RoleDAO.newInstance().get(roleId);
		}
		model.addAttribute("role", role);

		List<User> users = UserDAO.newInstance().all();
		Map<String, Object> map = new HashMap<String, Object>();
		for(User user : users){
			map.put("name", user.getName());
		}
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		view.setAttributesMap(map);
		model.addAttribute("json", view);
	}

	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody String autocompleteUser( @RequestParam("query") String query, Model model )
			throws Exception {
		 List<User> userList = UserDAO.newInstance().selectByName(query);
		 JSONArray list = new JSONArray();
		 for (User user : userList){
			 JSONObject userJson = new JSONObject();
			 userJson.put("label", user.getName());
			 userJson.put("value", user.getEmail());
			 list.put(userJson);
		 }
		 return list.toString();
	}

	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody String autocompleteGroup( @RequestParam("query") String query, Model model )
			throws Exception {
		 List<Group> groupList = GroupDAO.newInstance().selectByName(query);
		 JSONArray list = new JSONArray();
		for (Group group : groupList){
			 JSONObject groupJson = new JSONObject();
			 groupJson.put("label", group.getName());
			 groupJson.put("value", group.getEmail());
			 list.put(groupJson);
		 }
		 return list.toString();
	}

	@RequestMapping(method=RequestMethod.GET)
	@Transactional
	public String delete( @RequestParam("roleId") String roleId, Model model)
			throws Exception {
		RoleDAO.newInstance().delete(roleId);
		return "redirect:index";
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public String save(Role role) throws Exception {
		Role oldRole;
		if(role.getId() != null)
			oldRole = RoleDAO.newInstance().get(role.getId().toString());
		else
			oldRole = new Role();
		
		oldRole.setName(role.getName());
		oldRole.setDescription(role.getDescription());
		
		//add
		Set<Integer> newPrincipalIds = new HashSet<Integer>();
		for(RolePrincipal principal : role.getRolePrincipals()){
			if(principal.getType() != null){
				if(principal.getId() == null){
					principal.setFkRole(oldRole);
					oldRole.getRolePrincipals().add(principal);
				}else
					newPrincipalIds.add(principal.getId());
			}
		}
		
		//delete
		HashSet<RolePrincipal> deletePrincipals = new HashSet<RolePrincipal>();
		for(RolePrincipal principal: oldRole.getRolePrincipals()){
			if(principal.getId() != null && !newPrincipalIds.contains(principal.getId()))
				deletePrincipals.add(principal);
		}
		if(!deletePrincipals.isEmpty()){
			oldRole.getRolePrincipals().removeAll(deletePrincipals);
			RoleDAO.newInstance().deleteRolePrincipal(deletePrincipals);
		}
		RoleDAO.newInstance().save(oldRole);
		return "redirect:index";
	}
}