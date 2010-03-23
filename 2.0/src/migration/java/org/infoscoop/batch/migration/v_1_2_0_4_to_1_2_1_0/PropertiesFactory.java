package org.infoscoop.batch.migration.v_1_2_0_4_to_1_2_1_0;

import org.infoscoop.dao.model.Properties;

// id,value,description
public class PropertiesFactory implements CSVBeanFactory {
	public Object newBean( String[] values ) {
		Properties property = new Properties();
		property.setId( values[0] );
		property.setValue( values[1] );
		property.setDescription( values[2] );
		
		return property;
	}
}
