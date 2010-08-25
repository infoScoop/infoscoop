package org.infoscoop.manager.controller;

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
	public void editGroup(Model model)
			throws Exception {
		Role grp = new Role();
		model.addAttribute("role", grp);
	}

	@RequestMapping
	public void updateGroup( @RequestParam("id") String roleId, Model model)
			throws Exception {
		Role role = RoleDAO.newInstance().get(roleId);
		model.addAttribute("role", role);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void saveGroup(Role grp)throws Exception {
		for(RolePrincipal principal :grp.getRolePrincipals()){
			principal.setFkRole(grp);
		}
		RoleDAO.newInstance().save(grp);
	}

	@RequestMapping
	public String delete( @RequestParam("roleId") String roleId, Model model)
			throws Exception {
		RoleDAO.newInstance().delete(roleId);
		return "redirect:index";
	}

	@RequestMapping
	public String deleteRolePrincipal( @RequestParam("rolePrincipalId") String rolePrincipalId, @RequestParam("roleId") String roleId)
			throws Exception {
		RolePrincipal principal = RoleDAO.newInstance().getRolePrincipal(rolePrincipalId);
		RoleDAO.newInstance().deleteRolePrincipal(principal);
		return "redirect:updateGroup?id="+ roleId;
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public String update(Role role) throws Exception {
//		Role role = RoleDAO.newInstance().get(roleId);
		for(RolePrincipal principal :role.getRolePrincipals()){
			principal.setFkRole(role);
			//RoleDAO.newInstance().updatePrindipcal(principal);
		}
		RoleDAO.newInstance().save(role);
		return "redirect:updateGroup?id="+ role.getId();
	}
}