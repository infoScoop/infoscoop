package org.infoscoop.batch.migration.v200to210;

import org.infoscoop.batch.migration.CSVBeanFactory;
import org.infoscoop.batch.migration.CSVField;
import org.infoscoop.dao.model.Properties;


public class PropertiesFactory implements CSVBeanFactory {

	public Object newBean(CSVField[] values) throws Exception {
		Properties property = new Properties();
		property.setId( values[0].toString() );
		property.setCategory( values[1].toString() );
		property.setAdvanced( values[2].toInt() );
		property.setValue( values[3].toString() );
		property.setDatatype( values[4].toString() );
		property.setEnumvalue( values[5].toString() );
		property.setRequired( values[6].toInt() );
		property.setRegex( values[7].toString() );
		property.setRegexmsg( values[8].toString() );
		return property;
		
	}
}
