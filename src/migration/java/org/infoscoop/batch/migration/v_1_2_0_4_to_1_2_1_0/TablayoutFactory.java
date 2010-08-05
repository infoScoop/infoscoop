package org.infoscoop.batch.migration.v_1_2_0_4_to_1_2_1_0;

import org.infoscoop.dao.model.TABLAYOUTPK;
import org.infoscoop.dao.model.TabLayout;


//tabId,roleOrder,role,rolename,principalType,defaultUid,widgets,layout,widgetsLastmodified,tabNumber,deleteFlag

public class TablayoutFactory implements CSVBeanFactory {
	public Object newBean( String[] values ) {
		TABLAYOUTPK pk = new TABLAYOUTPK();
		pk.setTabid( values[0] );
		pk.setRoleorder( asInteger( values[1] ) );
		
		TabLayout tabLayout = new TabLayout( pk );
		tabLayout.setRole( values[2] );
		tabLayout.setRolename( values[3] );
		tabLayout.setPrincipaltype( values[4] );
		tabLayout.setDefaultuid( values[5] );
		tabLayout.setWidgets( values[6] );
		tabLayout.setLayout( values[7] );
		tabLayout.setWidgetslastmodified( values[8] );
		tabLayout.setTabnumber( asInteger( values[9] ));
		tabLayout.setDeleteflag( asInteger( values[10] ));
		
		return tabLayout;
	}
	public static Integer asInteger( String str ) {
		if( str == null || "".equals( str ))
			return null;
		
		return new Integer( str );
	}
}
