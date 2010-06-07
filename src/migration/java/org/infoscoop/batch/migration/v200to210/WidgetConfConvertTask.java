package org.infoscoop.batch.migration.v200to210;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.infoscoop.batch.migration.HibernateBeansTask;
import org.infoscoop.dao.model.WidgetConf;
import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class WidgetConfConvertTask implements HibernateBeansTask.BeanTask2 {
	public WidgetConfConvertTask() {
	}
	
	public void execute(Project project, Object object) throws BuildException {
		WidgetConf bean = (WidgetConf) object;
		if("FragmentMiniBrowser".equals(bean.getType())){
			try {
				Document doc = (Document) XmlUtil.string2Dom(bean.getData());
				
				Element newUserPref = doc.createElement("UserPref");
				newUserPref.setAttribute("default_value", "");
				newUserPref.setAttribute("datatype", "hidden");
				newUserPref.setAttribute("name", "additional_css");
				newUserPref.setAttribute("display_name", "!{lb_additionalCss}");
				newUserPref.setAttribute("admin_datatype", "textarea");
				
				NodeList userPrefs = doc.getElementsByTagName("UserPref");
				Node lastUserPref = userPrefs.item(userPrefs.getLength()-1);
				lastUserPref.getParentNode().insertBefore(newUserPref, lastUserPref.getNextSibling());
				bean.setData(XmlUtil.dom2String(doc));
			} catch (SAXException e) {
				throw new BuildException(e);
			}
		}
	}
	
	public void prepare( Project project ) throws BuildException {
	}
	
	public void finish( Project project ) throws BuildException {
	}
}
