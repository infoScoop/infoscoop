package org.infoscoop.batch.migration.v200to210;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.infoscoop.batch.migration.HibernateBeansTask;
import org.infoscoop.dao.model.Proxyconf;
import org.infoscoop.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ProxyConfConvertTask implements HibernateBeansTask.BeanTask2 {
	public ProxyConfConvertTask() {
	}

	public void execute(Project project, Object object) throws BuildException {
		Proxyconf bean = (Proxyconf) object;
		if (bean.getTemp().equals(0)) {
			try {
				Document doc = (Document) XmlUtil.string2Dom(bean.getData());

				NodeList caseList = doc.getElementsByTagName("case");
				for (int i = 0; i < caseList.getLength(); i++) {
					Element caseEle = (Element) caseList.item(i);
					caseEle.setAttribute("intranet", "true");
				}

				NodeList defaultList = doc.getElementsByTagName("default");
				Element defaultEle = (Element) defaultList.item(0);
				defaultEle.setAttribute("intranet", "true");

				bean.setElement(doc.getDocumentElement());
			} catch (SAXException e) {
				throw new BuildException(e);
			}
		}
	}

	public void prepare(Project project) throws BuildException {
	}

	public void finish(Project project) throws BuildException {
	}
}
