package org.infoscoop.batch.migration.v1300to1400;

import java.io.ByteArrayInputStream;
import java.sql.*;

import javax.sql.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.apache.tools.ant.*;
import org.apache.xpath.XPathAPI;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.infoscoop.batch.migration.*;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.dao.model.GadgetIcon;
import org.infoscoop.util.NoOpEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class GadgetIconTask implements HibernateBeansTask.BeanTask2 {
	private DocumentBuilderFactory builderFactory;
	private Session session;
	
	public GadgetIconTask() {
	}
	
	public void execute( Project project,Object object ) throws BuildException {
		Gadget bean = ( Gadget )object;
		String type = bean.getType();
		byte[] data = bean.getData();

		try {
			GadgetIcon icon = ( GadgetIcon )session.get( GadgetIcon.class,type );
			if( icon != null )
				return;
			
			// Copy from GadgetResourceService.validateGadget
			
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			builder.setEntityResolver( NoOpEntityResolver.getInstance());
			Document gadgetDoc = builder.parse( new ByteArrayInputStream( data ) );

			Element moduleNode = gadgetDoc.getDocumentElement();
			NodeList modulePrefsList = gadgetDoc.getElementsByTagName("ModulePrefs");
			
			Element iconElm = (Element) XPathAPI.selectSingleNode(gadgetDoc,
					"/Module/ModulePrefs/Icon");
			if (iconElm != null) {
				String iconUrl = iconElm.getTextContent();
				for (int i = 0; i < modulePrefsList.getLength(); i++) {
					Element modulePrefs = (Element) modulePrefsList.item(i);
					if (modulePrefs.hasAttribute("resource_url")) {
						iconUrl = modulePrefs.getAttribute("resource_url")
								+ iconUrl;
						break;
					}
				}
				
				icon = new GadgetIcon(type, iconUrl);
			} else {
				icon = new GadgetIcon(type, "");
			}
			
			session.save( icon );
			
			// end of copy
		} catch( Exception ex ) {
			finish( project );
			
			throw new BuildException( ex );
		}
	}

	public void prepare(Project project) throws BuildException {
		builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setValidating(false);
		
		SessionFactory sessionFactory = ( SessionFactory )SQLTask.getContext().getBean("sessionFactory");
		session = sessionFactory.openSession();
	}
	
	public void finish( Project project ) throws BuildException {
		if( session != null )
			session.close();
	}
}
