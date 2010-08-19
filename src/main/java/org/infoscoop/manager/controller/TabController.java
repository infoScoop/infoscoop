package org.infoscoop.manager.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.TabTemplateDAO;
import org.infoscoop.dao.WidgetConfDAO;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.TabTemplate;
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
		tab.setLayout("<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">	<tr>		<td width=\"75%\">			<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">				<tr>					<td style=\"width:33%\">						<div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div>					</td>					<td>						<div style=\"width:10px\">&nbsp;</div>					</td>					<td style=\"width:33%\">						<div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div>					</td>					<td>						<div style=\"width:10px\">&nbsp;</div>					</td>					<td style=\"width:34%\">						<div class=\"static_column\" style=\"width: 99%; height:82px; min-height: 1px;\"></div>					</td>				</tr>			</table>		</td>	</tr></table>");
		model.addAttribute(tab);
	}
	
	@RequestMapping
	public void selectGadgetType(@RequestParam("id") String parentId,
			Model model) throws Exception {
		model.addAttribute("parentId", parentId);
	}
	
	@RequestMapping
	public void showGadgetDialog(@RequestParam("type") String type, Model model)throws Exception {
		MenuItem menuItem = new MenuItem();
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
	
	@RequestMapping(method = RequestMethod.POST)
	public void addTab(TabTemplate tab)throws Exception {
		tab.setId("m_" + new Date().getTime());
		TabTemplateDAO.newInstance().save(tab);
	}
	
}
