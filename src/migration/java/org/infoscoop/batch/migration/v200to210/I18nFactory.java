package org.infoscoop.batch.migration.v200to210;

import org.infoscoop.batch.migration.CSVBeanFactory;
import org.infoscoop.batch.migration.CSVField;
import org.infoscoop.dao.model.I18NPK;
import org.infoscoop.dao.model.I18n;


// type,id,country,lang,message
public class I18nFactory implements CSVBeanFactory{

	public Object newBean(CSVField[] values) throws Exception {
		I18NPK pk = new I18NPK();
		pk.setType( values[0].toString() );
		pk.setId( values[1].toString() );
		pk.setCountry( values[2].toString() );
		pk.setLang( values[3].toString() );
		
		I18n i18n = new I18n( pk );
		if( "".equals( values[4].toString() ) )
			values[4] = new CSVField("-");
		
		i18n.setMessage( values[4].toString() );
		
		return i18n;
	}
}
