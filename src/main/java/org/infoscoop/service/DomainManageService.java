package org.infoscoop.service;

import java.util.List;

import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.CommandBarDAO;
import org.infoscoop.dao.DomainDAO;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.MenuTreeDAO;
import org.infoscoop.dao.TabTemplateDAO;
import org.infoscoop.dao.model.CommandBar;
import org.infoscoop.dao.model.CommandBarStaticGadget;
import org.infoscoop.dao.model.Domain;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.MenuTree;
import org.infoscoop.dao.model.TabTemplate;
import org.infoscoop.dao.model.TabTemplatePersonalizeGadget;
import org.infoscoop.dao.model.TabTemplateStaticGadget;
import org.infoscoop.util.SpringUtil;
import org.springframework.transaction.annotation.Transactional;

public class DomainManageService {

	//private static final String ORIGINAL_DOMAIN = "test.infoscoop4g.com";
	private static final String TEMPLATE_DOMAIN_NAME = "DOMAIN_TEMPLATE";
	
	public static DomainManageService getHandle() {
		return (DomainManageService)SpringUtil.getBean("DomainManageService");
	}

	private CommandBarDAO commandBarDAO;
	private MenuItemDAO menuItemDAO;
	private MenuTreeDAO menuTreeDAO;
	private TabTemplateDAO tabTemplateDAO;
	
	public void setCommandBarDAO(CommandBarDAO commandBarDAO) {
		this.commandBarDAO = commandBarDAO;
	}
	
	public void setMenuItemDAO(MenuItemDAO menuItemDAO) {
		this.menuItemDAO = menuItemDAO;
	}
	public void setMenuTreeDAO(MenuTreeDAO menuTreeDAO) {
		this.menuTreeDAO = menuTreeDAO;
	}

	public void setTabTemplateDAO(TabTemplateDAO tabTemplateDAO) {
		this.tabTemplateDAO = tabTemplateDAO;
	}

	@Transactional
	public void newDomain(String domain) throws CloneNotSupportedException{
		Domain orgDomain = DomainDAO.newInstance().getByName(TEMPLATE_DOMAIN_NAME);
		DomainManager.registerContextDomainId(orgDomain.getId());
		
		Domain newDomain = DomainDAO.newInstance().getByName(domain);
		if (newDomain != null) {
			throw new RuntimeException("The domain \"" + domain
					+ "\" already exists.");
		}
		
		newDomain = new Domain();
		newDomain.setName(domain);
		DomainDAO.newInstance().save(newDomain);
		
		copyCommandBar(newDomain);
		
		copyTabTemplates(newDomain);
		
		copyMenuTree(newDomain);
	}

	
	private void copyCommandBar(Domain newDomain) {
		for(CommandBar cmdBar:commandBarDAO.all()){
			CommandBar cmd = new CommandBar();
			cmd.setFkDomainId(newDomain.getId());
			cmd.setAccessLevel(cmdBar.getAccessLevel());
			cmd.setDisplayOrder(cmdBar.getDisplayOrder());
			for(CommandBarStaticGadget gi: cmdBar.getCommandBarStaticGadgets()){
				//TODO:
			}
			commandBarDAO.save(cmd);
		}
		
	}

	private void copyTabTemplates(Domain newDomain)
	throws CloneNotSupportedException {
		for (TabTemplate tt : tabTemplateDAO.all()) {
			TabTemplate copy;
			copy = tt.createTemp();
			copy.setStatus(0);
			copy.setFkDomainId(newDomain.getId());

			for (TabTemplateStaticGadget sg : copy
					.getTabTemplateStaticGadgets()) {
				sg.getGadgetInstance().setFkDomainId(newDomain.getId());
			}
			for (TabTemplatePersonalizeGadget pg : copy
					.getTabTemplatePersonalizeGadgets()) {
				pg.getFkGadgetInstance().setFkDomainId(newDomain.getId());
			}
		}
	}
	
	private void copyMenuTree(Domain newDomain){
		List<MenuTree> menuTreeList = menuTreeDAO.all();
		for(MenuTree menuTree: menuTreeList){
			MenuTree newMenuTree = new MenuTree();
			newMenuTree.setTitle(menuTree.getTitle());
			newMenuTree.setHref(menuTree.getHref());
			newMenuTree.setFkDomainId(newDomain.getId());
			newMenuTree.setAlert(menuTree.getAlert());
			//newMenuTree.setRoles(menuTree.getRoles());
			newMenuTree.setDescription(menuTree.getDescription());
			newMenuTree.setLang(menuTree.getLang());
			newMenuTree.setCountry(menuTree.getCountry());
			newMenuTree.setOrderIndex(menuTree.getOrderIndex());
			newMenuTree.setPublish(menuTree.getPublish());
			newMenuTree.setSide(menuTree.getSide());
			newMenuTree.setTop(menuTree.getTop());
			menuTreeDAO.save(newMenuTree);
			
			menuTree.setChildItems();
			for(MenuItem menuItem: menuTree.getChildItems()){
				MenuItem newMenuItem = new MenuItem();
				newMenuItem.setTitle(menuItem.getTitle());
				newMenuItem.setHref(menuItem.getHref());
				newMenuItem.setFkDomainId(newDomain.getId());
				newMenuItem.setAlert(menuItem.getAlert());
				newMenuItem.setFkMenuTree(newMenuTree);
				if(menuItem.getGadgetInstance() != null){
					GadgetInstance gi = menuItem.getGadgetInstance().copy();
					gi.setFkDomainId(newDomain.getId());
					newMenuItem.setGadgetInstance(gi);
				}
				newMenuItem.setMenuOrder(menuItem.getMenuOrder());
				newMenuItem.setPublish(menuItem.getPublish());
				menuItemDAO.save(newMenuItem);
				
				for(MenuItem child : menuItem.getChildItems()){
					copyMenuItem(newDomain,  newMenuItem, child);
				}
			}
		}
	}

	private void copyMenuItem(Domain newDomain, MenuItem parent, MenuItem menuItem){
		MenuItem newMenuItem = new MenuItem();
		newMenuItem.setTitle(menuItem.getTitle());
		newMenuItem.setHref(menuItem.getHref());
		newMenuItem.setFkDomainId(newDomain.getId());
		newMenuItem.setAlert(menuItem.getAlert());
		newMenuItem.setFkParent(parent);
		if(menuItem.getGadgetInstance() != null){
			GadgetInstance gi = menuItem.getGadgetInstance().copy();
			gi.setFkDomainId(newDomain.getId());
			newMenuItem.setGadgetInstance(gi);
		}
		newMenuItem.setMenuOrder(menuItem.getMenuOrder());
		newMenuItem.setPublish(menuItem.getPublish());
		menuItemDAO.save(newMenuItem);
		for(MenuItem child : menuItem.getChildItems()){
			copyMenuItem(newDomain, newMenuItem, child);
		}
	}
}
