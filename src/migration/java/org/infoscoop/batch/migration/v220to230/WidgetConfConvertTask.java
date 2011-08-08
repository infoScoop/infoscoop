package org.infoscoop.batch.migration.v220to230;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.infoscoop.batch.migration.HibernateBeansTask;
import org.infoscoop.dao.model.WidgetConf;
import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Add <EnumValue display_value="signed" value="signed"/> to RssReader and FragmentMiniBrowser.
 * @author nishiumi
 *
 */
public class WidgetConfConvertTask implements HibernateBeansTask.BeanTask2 {
	public WidgetConfConvertTask() {
	}

	public void execute(Project project, Object object) throws BuildException {
		WidgetConf bean = (WidgetConf) object;
		try {
			if("FragmentMiniBrowser".equalsIgnoreCase(bean.getType()) || "RssReader".equalsIgnoreCase(bean.getType())){
				Document doc = (Document) XmlUtil.string2Dom(bean.getData());

				NodeList userPrefs = doc.getElementsByTagName("UserPref");
				int length = userPrefs.getLength();
				
				loop:
				for(int i=0;i<length;i++){
					Element userPref = (Element)userPrefs.item(i);
					if(userPref.getAttribute("name").equalsIgnoreCase("authType")){
						
						if(!checkExists(userPref)){
							Element newEnumValue = doc.createElement("EnumValue");
							newEnumValue.setAttribute("display_value", "signed");
							newEnumValue.setAttribute("value", "signed");
							
							userPref.appendChild(newEnumValue);
							project.log("[" + bean.getType() + "] add authType \"signed.\"");
							break loop;
						}
					}
				}
				bean.setData(XmlUtil.dom2String(doc));
			}
		} catch (SAXException e) {
			throw new BuildException(e);
		}
	}
	
	private boolean checkExists(Element userPref){
		NodeList enumValueList = userPref.getElementsByTagName("EnumValue");
		for(int i=0;i<enumValueList.getLength();i++){
			Element enumValue = (Element)enumValueList.item(i);
			if("signed".equalsIgnoreCase(enumValue.getAttribute("display_value"))){
				return true;
			}
		}
		return false;
	}
	
	public void prepare( Project project ) throws BuildException {
	}

	public void finish( Project project ) throws BuildException {
	}
}
