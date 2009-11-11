package org.infoscoop.batch.migration.v1300to1400;

import org.infoscoop.batch.migration.v_1_2_0_4_to_1_2_1_0.CSVBeanFactory;
import org.infoscoop.dao.model.Properties;


public class PropertiesFactory implements CSVBeanFactory {
	public Properties newBean( String[] values ) {
		Properties property = new Properties();
		property.setId( values[0] );
		property.setCategory( values[1] );
		property.setAdvanced( Integer.parseInt( values[2] ));
		property.setValue( values[3] );
		property.setDatatype( values[4] );
		property.setEnumvalue( values[5] );
		property.setRequired( Integer.parseInt( values[6] ));
		property.setRegex( values[7] );
		property.setRegexmsg( values[8] );
		
		return property;
	}
}
