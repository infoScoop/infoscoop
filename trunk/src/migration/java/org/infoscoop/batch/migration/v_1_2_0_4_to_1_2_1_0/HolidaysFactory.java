package org.infoscoop.batch.migration.v_1_2_0_4_to_1_2_1_0;

import org.infoscoop.dao.model.HOLIDAYSPK;
import org.infoscoop.dao.model.Holidays;


// lang,country,message
public class HolidaysFactory implements CSVBeanFactory{
	public Object newBean( String[] values ) {
		HOLIDAYSPK pk = new HOLIDAYSPK();
		pk.setLang( values[0] );
		pk.setCountry( values[1] );
		
		Holidays holiday = new Holidays( pk );
		holiday.setData( values[2] );
		
		return holiday;
	}
}
