package org.infoscoop.manager.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.GadgetInstanceDAO;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.TabTemplateDAO;
import org.infoscoop.dao.WidgetConfDAO;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.TabTemplate;
import org.infoscoop.service.GadgetService;
import org.infoscoop.service.TabLayoutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Element;

@Controller
public class TabController {
	@RequestMapping
	public void index(Model model)throws Exception {
		List<TabTemplate> tabs = TabTemplateDAO.newInstance().all();
		System.out.print(tabs);
		model.addAttribute("tabs", tabs);
	}

	@RequestMapping
	public void showAddTab(Model model)
			throws Exception {
		TabTemplate tab = new TabTemplate();
		tab.setName("新しいタブ");
		tab.setLayout("<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">	<tr>		<td width=\"75%\">			<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">				<tr>					<td style=\"width:33%\">						<div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div>					</td>					<td>						<div style=\"width:10px\">&nbsp;</div>					</td>					<td style=\"width:33%\">						<div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div>					</td>					<td>						<div style=\"width:10px\">&nbsp;</div>					</td>					<td style=\"width:34%\">						<div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div>					</td>				</tr>			</table>		</td>	</tr></table>");
		TabTemplateDAO.newInstance().save(tab);
		model.addAttribute(tab);
	}
	
	@RequestMapping
	public void selectGadgetType(HttpServletRequest request, @RequestParam("id") String containerId,
			Model model) throws Exception {
		model.addAttribute("containerId", containerId);
		model.addAttribute("gadgetConfs", GadgetService.getHandle().getGadgetConfs(request.getLocale()));
	}
	
	@RequestMapping
	public void showGadgetDialog(HttpServletRequest request, @RequestParam("type") String type, Model model)throws Exception {
		MenuItem menuItem = new MenuItem();
//		menuItem.setType(type);
		model.addAttribute("menuItem", menuItem);
		
		//TODO 国際化処理して言語ごとにDBにキャッシュとして保存する。そしてそれを取得する。
		Element conf = null;
		if (type.startsWith("upload__")) {
			conf = GadgetDAO.newInstance().getGadgetElement(type.substring(8));
		} else {
			conf = WidgetConfDAO.newInstance().getElement(type);
		}
		model.addAttribute("conf", conf.getOwnerDocument());
	
	}

	@RequestMapping
	public void listGadgetInstances(){
	}

	@RequestMapping(method = RequestMethod.POST)
	public void submitGadgetSettings(GadgetInstance gadget)throws Exception {
		TabLayoutService.getHandle().insertStaticGadget("temp", gadget);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void addTab(TabTemplate tab)throws Exception {
		
		TabTemplateDAO.newInstance().save(tab);
	}
	
}
