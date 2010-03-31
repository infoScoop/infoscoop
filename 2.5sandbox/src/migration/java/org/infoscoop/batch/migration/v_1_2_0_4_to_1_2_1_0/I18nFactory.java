package org.infoscoop.batch.migration.v_1_2_0_4_to_1_2_1_0;

import org.infoscoop.dao.model.I18NPK;
import org.infoscoop.dao.model.I18n;


// type,id,country,lang,message
public class I18nFactory implements CSVBeanFactory{
	public Object newBean( String[] values ) {
		I18NPK pk = new I18NPK();
		pk.setType( values[0] );
		pk.setId( values[1] );
		pk.setCountry( values[2] );
		pk.setLang( values[3] );
		
		I18n i18n = new I18n( pk );
		i18n.setMessage( values[4] );
		
		return i18n;
	}
}
