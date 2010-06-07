package org.infoscoop.batch.migration.v200to210;

import org.infoscoop.batch.migration.CSVBeanFactory;
import org.infoscoop.batch.migration.CSVField;
import org.infoscoop.dao.model.I18nlocale;


// type,id,country,lang,message
public class I18nLocaleFactory implements CSVBeanFactory{

	public Object newBean(CSVField[] values) throws Exception {

		I18nlocale i18nlocale = new I18nlocale();
		i18nlocale.setType(values[1].toString());
		i18nlocale.setCountry( values[2].toString() );
		i18nlocale.setLang( values[3].toString() );

		return i18nlocale;
	}
}
