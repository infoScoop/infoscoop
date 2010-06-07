package org.infoscoop.batch.migration.v200to210;

import org.infoscoop.batch.migration.CSVBeanFactory;
import org.infoscoop.batch.migration.CSVField;
import org.infoscoop.dao.model.Portallayout;

public class PortalLayoutFactory implements CSVBeanFactory {

	public Object newBean(CSVField[] values) throws Exception {
		Portallayout portalLayout = new Portallayout();
		portalLayout.setName( values[0].toString() );
		portalLayout.setLayout( values[1].toString() );
		
		return portalLayout;
	}
}