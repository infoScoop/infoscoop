package org.infoscoop.batch.migration.v1300to1400;

import org.apache.tools.ant.*;

import org.infoscoop.batch.migration.*;
import org.infoscoop.dao.model.Widget;

public class EtcWidgetToGadgetTask2 implements HibernateBeansTask.BeanTask {
	public EtcWidgetToGadgetTask2() {
	}
	
	public void execute( Project project,Object object ) throws BuildException {
		Widget widget = ( Widget )object;
		
		String type = widget.getType().toLowerCase();
		if( EtcWidgetMenuToGadgetMenuTask.TYPE_MAP.containsKey( type )) {
			String gadgetType = EtcWidgetMenuToGadgetMenuTask.TYPE_MAP.get( type );
			widget.setType( gadgetType );
			
			project.log("Widget #"+widget.getId()+"@"+type+" => "+ gadgetType );
		}
	}
}
