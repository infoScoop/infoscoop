package org.infoscoop.manager.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.infoscoop.dao.RoleDAO;
import org.infoscoop.dao.UserDAO;
import org.infoscoop.dao.GroupDAO;
import org.infoscoop.dao.model.Role;
import org.infoscoop.dao.model.RolePrincipal;
import org.infoscoop.dao.model.User;
import org.infoscoop.dao.model.Group;
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
		System.out.print(roles);
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
	public @ResponseBody ArrayList<String> autocompleteUser( @RequestParam("query") String query, Model model )
			throws Exception {
		 List<User> json = UserDAO.newInstance().selectByName(query);
		 ArrayList<String> list = new ArrayList<String>();
		 for (int i=0 ; i < json.size(); i++){
			 list.add(json.get(i).getName());
		 }
		 return list;
	}

	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> autocompleteGroup( @RequestParam("query") String query, Model model )
			throws Exception {
		 List<Group> json = GroupDAO.newInstance().getJson(query);
		 ArrayList<String> list = new ArrayList<String>();
		 for (int i=0 ; i < json.size(); i++){
			 list.add(json.get(i).getName());
		 }
		 return list;
	}

	@RequestMapping(method=RequestMethod.GET)
	public String delete( @RequestParam("roleId") String roleId, Model model)
			throws Exception {
		RoleDAO.newInstance().delete(roleId);
		return "redirect:index";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String deleteRolePrincipal( Role role, Model model)
			throws Exception {

		List<RolePrincipal> list = new ArrayList<RolePrincipal>();
		for(RolePrincipal rolePrincipal : role.getRolePrincipals()){
			if(rolePrincipal !=null && rolePrincipal.getId() != null){
				if(rolePrincipal.getType() != null)
					list.add(rolePrincipal);
				else if(rolePrincipal.getType() == null)
					role.getDeletePrincipalIdList().add(rolePrincipal.getId().toString());
			}
		}
		role.setRolePrincipals(list);
		model.addAttribute("role", role);

		return "group/edit";
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public String save(Role role) throws Exception {
		RoleDAO.newInstance().save(role);
		for(String id: role.getDeletePrincipalIdList()){
			RolePrincipal principal = RoleDAO.newInstance().getRolePrincipal(id);
			RoleDAO.newInstance().deleteRolePrincipal(principal);
		}
		return "redirect:index";
	}
}