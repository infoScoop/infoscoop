package org.infoscoop.batch.migration.v1300to1400;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.tools.ant.*;
import org.infoscoop.batch.migration.*;
import org.infoscoop.dao.model.Siteaggregationmenu;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class MultiPropertyToAttrTask implements HibernateBeansTask.BeanTask {
	private static final String XPEX_MULTI_PROPERTY = "properties/property[@name='multi']";
	private static final String XPEX_MULTI_SITE = "//site["+XPEX_MULTI_PROPERTY+"]";
	public MultiPropertyToAttrTask() {
	}
	
	public void execute( Project project,Object object ) throws BuildException {
		Siteaggregationmenu bean = ( Siteaggregationmenu )object;
		
		try {
			Element element = bean.getElement();
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList sites = ( NodeList )xpath.evaluate( XPEX_MULTI_SITE,element,XPathConstants.NODESET );
			for( int i=0;i<sites.getLength();i++ ) {
				Element site = ( Element )sites.item( i );
				
				Element property = ( Element )xpath.evaluate( XPEX_MULTI_PROPERTY,site,XPathConstants.NODE );
				
				if("true".equals( property.getTextContent().toLowerCase() )) {
					site.setAttribute("multi","true");
					
					project.log("site#"+site.getAttribute("id")+" set multi");
					
					Element properties = ( Element )property.getParentNode();
					properties.removeChild( property );
				}
			}
			
			bean.setElement( element );
			
		} catch( Exception ex ) {
			throw new BuildException( ex );
		}
	}
}
