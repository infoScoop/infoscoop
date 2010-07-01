package org.infoscoop.batch.migration.v1300to1400;

import java.util.ArrayList;
import java.util.List;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.infoscoop.batch.migration.HibernateBeansTask;
import org.infoscoop.batch.migration.SQLTask;
import org.infoscoop.dao.model.Siteaggregationmenu;
import org.infoscoop.dao.model.Widget;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WidgetAddMenuIdTask implements HibernateBeansTask.BeanTask2 {
	private Session session;
	private List<String> menuIdList = new ArrayList<String>();

	public WidgetAddMenuIdTask() {
	}

	public void execute(Project project, Object object) throws BuildException {
		Widget widget = (Widget) object;
		try {
			String widgetId = widget.getWidgetid();
			if (widgetId.startsWith("w_")) {
				widgetId = widgetId.substring(2);
				if (menuIdList.contains(widgetId)) {
					widget.setMenuid(widgetId);
					return;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new BuildException("Add menu ID to " + widget.getId()
					+ " Failed" + ex.getClass(), ex);
		}
	}

	public void prepare(Project project) throws BuildException {
		SessionFactory sessionFactory = (SessionFactory) SQLTask.getContext()
				.getBean("sessionFactory");
		session = sessionFactory.openSession();

		try {
			fillMenuIds("topmenu");
			fillMenuIds("sidemenu");
		} catch (Exception ex) {
			throw new BuildException(ex);
		} finally {
			session.close();
		}
	}

	public void finish(Project project) throws BuildException {
	}

	private void fillMenuIds(String menuType) throws Exception {
		Siteaggregationmenu menu = (Siteaggregationmenu) session.get(
				Siteaggregationmenu.class,menuType);
		NodeList sites = menu.getElement().getElementsByTagName("site");
		for (int i = 0; i < sites.getLength(); i++) {
			Element site = (Element) sites.item(i);
			menuIdList.add(site.getAttribute("id"));
		}
	}
}
