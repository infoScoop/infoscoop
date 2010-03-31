package org.infoscoop.batch.migration.v_1_2_0_4_to_1_2_1_0;

import org.infoscoop.dao.model.Portallayout;

public class PortalLayoutFactory implements CSVBeanFactory {
	public Portallayout newBean( String[] values ) {
		Portallayout portalLayout = new Portallayout();
		portalLayout.setName( values[0] );
		portalLayout.setLayout( values[1] );
		
		return portalLayout;
	}
}