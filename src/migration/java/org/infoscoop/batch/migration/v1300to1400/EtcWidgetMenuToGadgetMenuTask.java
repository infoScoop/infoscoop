package org.infoscoop.batch.migration.v1300to1400;

import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.*;
import org.infoscoop.batch.migration.*;
import org.infoscoop.dao.model.Siteaggregationmenu;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class EtcWidgetMenuToGadgetMenuTask implements HibernateBeansTask.BeanTask {
	public static final Map<String,String> TYPE_MAP = new HashMap<String,String>();
	static {
		TYPE_MAP.put("alarm","g_upload__alarm/gadget");
		TYPE_MAP.put("calculator","g_upload__calc/gadget");
		TYPE_MAP.put("stickey","g_upload__sticky/gadget");
		TYPE_MAP.put("todolist","g_upload__todoList/gadget");
		TYPE_MAP.put("worldclock","g_upload__worldclock/gadget");
	}
	public EtcWidgetMenuToGadgetMenuTask() {
	}
	
	public void execute( Project project,Object object ) throws BuildException {
		Siteaggregationmenu bean = ( Siteaggregationmenu )object;
		Element element;
		try {
			element = bean.getElement();
		} catch( SAXException ex ) {
			throw new BuildException( ex );
		}
		
		NodeList sites = element.getElementsByTagName("site");
		for( int i=0;i<sites.getLength();i++ ) {
			Element site = ( Element )sites.item( i );
			
			String type = site.getAttribute("type").toLowerCase();
			if( TYPE_MAP.containsKey( type )) {
				site.setAttribute("type",TYPE_MAP.get( type ));
				
				project.log("site#"+site.getAttribute("id")+" type "+type+" to "+TYPE_MAP.get( type ));
			}
		}
		
		bean.setElement( element );
	}
}
