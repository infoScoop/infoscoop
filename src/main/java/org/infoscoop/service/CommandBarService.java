package org.infoscoop.service;

import java.util.List;

import org.infoscoop.dao.CommandBarDAO;
import org.infoscoop.dao.model.CommandBar;
import org.infoscoop.dao.model.CommandBarStaticGadget;
import org.infoscoop.util.SpringUtil;

public class CommandBarService {

	private CommandBarDAO dao;
	
	public void setCommandBarDAO(CommandBarDAO dao){
		this.dao = dao;
	}

	public static CommandBarService getHandle() {
		return (CommandBarService)SpringUtil.getBean("CommandBarService");
	}

	public String getMyTemplate() {
		StringBuffer html = new StringBuffer();
		html.append("<table cellpadding='0' cellspacing='3' width='100%'>\n  <tr>");
		List<CommandBar> cmdBars = this.dao.all();
		//TODO: Check role
		CommandBar cmdBar = cmdBars.get(0);
		for(CommandBarStaticGadget gadget: cmdBar.getCommandBarStaticGadgets()){
			html.append("  <td><div id='").append(gadget.getContainerId()).append("'></div></td>\n");			
		}
		html.append("  <td><div id='portal-go-home'></div></td>\n");
		html.append("  <td><div id='portal-preference'></div></td>\n");
		html.append("  <td><div id='portal-trash'></div></td>\n");
		html.append("  <td><div id='portal-admin-link'></div></td>\n");
		html.append("  </tr>\n</table>");
		return html.toString();
	}

	public CommandBar getMyCommandBar() {
		List<CommandBar> cmdBars = this.dao.all();
		//TODO: Check role
		CommandBar cmdBar = cmdBars.get(0);
		return cmdBar;
	}

}
