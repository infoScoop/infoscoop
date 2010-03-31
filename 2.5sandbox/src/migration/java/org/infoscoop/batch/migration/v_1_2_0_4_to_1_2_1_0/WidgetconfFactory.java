package org.infoscoop.batch.migration.v_1_2_0_4_to_1_2_1_0;

import org.infoscoop.dao.model.WidgetConf;

//type,data

class WidgetconfFactory implements CSVBeanFactory {
	public Object newBean( String[] values ) {
		WidgetConf widgetConf = new WidgetConf();
		widgetConf.setType( values[0] );
		widgetConf.setData( values[1] );
		
		return widgetConf;
	}
}