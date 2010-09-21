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

	public CommandBar getMyCommandBar() {
		List<CommandBar> cmdBars = this.dao.all();
		//TODO: Check role
		CommandBar cmdBar = cmdBars.get(0);
		return cmdBar;
	}

}
