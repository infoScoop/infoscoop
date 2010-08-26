package org.infoscoop.manager.controller;

import java.util.ArrayList;
import java.util.List;

import org.infoscoop.dao.RoleDAO;
import org.infoscoop.dao.model.Role;
import org.infoscoop.dao.model.RolePrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GroupController {
	@RequestMapping
	public void index(Model model) throws Exception {
		List<Role> roles = RoleDAO.newInstance().all();
		System.out.print(roles);
		model.addAttribute("roles", roles);
	}

	@RequestMapping
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
	}

	@RequestMapping
	public String delete( @RequestParam("roleId") String roleId, Model model)
			throws Exception {
		RoleDAO.newInstance().delete(roleId);
		return "redirect:index";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String deleteRolePrincipal( Role role, 
			Model model)
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