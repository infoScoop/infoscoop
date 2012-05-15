/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.batch.migration.v300to310;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.cyberneko.html.HTMLConfiguration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.infoscoop.batch.migration.HibernateBeansTask;
import org.infoscoop.batch.migration.SQLTask;
import org.infoscoop.dao.model.TabLayout;
import org.infoscoop.util.SpringUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * "Logo" is added to CommandBar.
 * "Search Form" is added to CommandBar.
 * "To Top Page" is deleted from CommandBar.
 * Attr "outside" is added to Ticker.
 * 
 * @author nishiumi
 *
 */
public class TabLayoutTask implements HibernateBeansTask.BeanTask2 {
	private final String ATTR_OUTSIDE = "outside";
	
	static {
		String beanDefinitionsParam = "datasource.xml,dataaccess.xml";
		String[] beanDefinitions = beanDefinitionsParam.split(",");
		for(int i = 0; i < beanDefinitions.length; i++){
			beanDefinitions[i] = beanDefinitions[i].trim();
		}
		
		SpringUtil.initContext(beanDefinitions);
	}
	
	public TabLayoutTask() {
	}
	
	public void execute(Project project, Object object) throws BuildException {
		TabLayout bean = (TabLayout) object;
		
		String tabId = bean.getId().getTabid();
		
		if("commandbar".equalsIgnoreCase(tabId)){
			String layout = bean.getLayout();
			project.log(bean.getRolename() +  ".oldLayout=\n" + layout);
			
			DOMParser parser = new DOMParser(new HTMLConfiguration());

			try {
				parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
				parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content", true);
				parser.setFeature("http://cyberneko.org/html/features/scanner/notify-builtin-refs", true);
				parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
				parser.setProperty("http://cyberneko.org/html/properties/names/attrs", "no-change");
				parser.setProperty("http://cyberneko.org/html/properties/default-encoding",	"UTF-8");
				
				parser.parse(new InputSource(new StringReader(layout)));
				Document doc = parser.getDocument();
				Element root = doc.getDocumentElement();
				
				// search commandbar html doc
				while(root != null && !"table".equalsIgnoreCase(root.getNodeName())){
					root = (Element)root.getFirstChild();
				}
				if(root == null){
					try{
						root = (Element)doc.getElementsByTagName("table").item(0);
					}catch(Exception e){
						String msg = "command bar layout is not found: " + dom2String(doc, "UTF-8");
						project.log(msg);
						throw new RuntimeException(msg);
					}
				}
				
				// insert new elements [portal-searchform]
				insertNewElements(root);
				
				// add outside to Ticker
				String tickerId = "p_1_w_4";
				JSONObject widgetsObj = bean.getStaticPanelJson();
				
				@SuppressWarnings("unchecked")
				Iterator<String> ite = widgetsObj.keys();
				while(ite.hasNext()){
					String key = ite.next();
					JSONObject json = (JSONObject)widgetsObj.get(key);
					if(json.has("type") && json.getString("type").equalsIgnoreCase("ticker")){
						tickerId = json.getString("id");
						project.log("tickerId=" + tickerId);
						break;
					}
				}
				
				Element ticker = getElementById(root, tickerId);
				if(ticker != null){
					ticker.setAttribute(ATTR_OUTSIDE, "true");
				}
				
				// remove portal-go-home
				Element go_home = getElementById(root, "portal-go-home");
				if(go_home != null && go_home.getParentNode() != null && go_home.getParentNode().getParentNode() != null){
					go_home.getParentNode().getParentNode().removeChild(go_home.getParentNode());
				}
				
				// add class="commandbar-item"
				try {
					NodeIterator nodeIte = XPathAPI.selectNodeIterator(root, "//tr/td/div");
					Element itemDiv = null;
					while((itemDiv = (Element)nodeIte.nextNode()) != null){
						itemDiv.setAttribute("class", "commandbar-item");
					}
				} catch (TransformerException e) {
					e.printStackTrace();
				}
				
				String newLayout = dom2String(root, "UTF-8");
				
				project.log(bean.getRolename() +  ".newLayout=\n" + newLayout);
				bean.setLayout(newLayout);
			} catch (SAXException e) {
				project.log(e.getMessage(), Project.MSG_ERR);
				e.printStackTrace();
			} catch (IOException e) {
				project.log(e.getMessage(), Project.MSG_ERR);
				e.printStackTrace();
			} catch (JSONException e) {
				project.log(e.getMessage(), Project.MSG_ERR);
				e.printStackTrace();
			}
		
		}
	}
	
	private Element getElementById(Element root, String id){
		NodeList divList = root.getElementsByTagName("div");
		for(int i=0;i<divList.getLength();i++){
			Element div = (Element)divList.item(i);
			if(id.equalsIgnoreCase(div.getAttribute("id"))){
				return div;
			}
		}
		return null;
	}
	
	/**
	 * insert following html elements
	 * <td>
	 *	<div outside="true" id="portal-searchform"></div>
	 * </td>
	 */
	private void insertNewElements(Element root){
		Document doc = root.getOwnerDocument();
		Element baseElement = (Element)root.getElementsByTagName("td").item(0);
		Element parent = (Element)baseElement.getParentNode();
		
		// portal-logo
		/*
		if(getElementById(root, "portal-logo") == null){
			Element portalLogoTd = doc.createElement("td");
			Element portalLogoDiv = doc.createElement("div");
			Element portalLogoA = doc.createElement("a");
			Element portalLogoImage = doc.createElement("img");
			
			portalLogoDiv.setAttribute("id", "portal-logo");
			portalLogoDiv.setAttribute(ATTR_OUTSIDE, "true");
			portalLogoA.setAttribute("href", "javascript:void(0)");
			portalLogoImage.setAttribute("border", "0");
			portalLogoImage.setAttribute("class", "pngfix");
			portalLogoImage.setAttribute("src", "skin/imgs/infoscoop_logo.png");
			portalLogoA.appendChild(portalLogoImage);
			portalLogoDiv.appendChild(portalLogoA);
			portalLogoTd.appendChild(portalLogoDiv);

			parent.insertBefore(portalLogoTd, baseElement);
		}
		*/
		
		// portal-searchform
		if(getElementById(root, "portal-searchform") == null){
			Element portalSearchTd = doc.createElement("td");
			Element portalSearchDiv = doc.createElement("div");
			
			portalSearchDiv.setAttribute(ATTR_OUTSIDE, "true");
			portalSearchDiv.setAttribute("id", "portal-searchform");
			portalSearchTd.appendChild(portalSearchDiv);
			
			// insert to last
			parent.appendChild(portalSearchTd);
		}
	}
	
	public void prepare( Project project ) throws BuildException {
	}
	
	public void finish( Project project ) throws BuildException {
	}
	
	private static String dom2String(Node node, String encoding) {
		Source source = new DOMSource(node);
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		try{
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
//			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			
			transformer.transform(source, result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return stringWriter.getBuffer().toString();
	}
	
	/*
	public static void main(String[] args) {
		SessionFactory sessionFactory = ( SessionFactory )SQLTask.getContext().getBean("sessionFactory");
		Session session = sessionFactory.openSession();
		
		class DummyProject extends Project {
			@Override
			public void log(String message) {
				System.out.println(message);
			}
			@Override
			public void log(Target target, String message, int msgLevel) {
				System.out.println(message);
			}
		}
		
		try {
			String queryString = "from TabLayout";
			
			System.out.println("QueryString: "+queryString );
			List objects = session.createQuery( queryString ).list();
			
			TabLayoutTask task = new TabLayoutTask();
			for( int i=0;i<objects.size();i++ ) {
				Object object = objects.get( i );
					
				task.execute( new DummyProject(),object );
				session.update( object );
			}
			session.flush();
			session.clear();
			
		} catch( Exception ex ) {
			throw new BuildException( ex );
		} finally {
			session.close();
		}
	}
	*/
}
