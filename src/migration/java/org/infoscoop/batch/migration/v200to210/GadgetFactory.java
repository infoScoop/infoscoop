package org.infoscoop.batch.migration.v200to210;

import org.infoscoop.batch.migration.CSVBeanFactory;
import org.infoscoop.batch.migration.CSVField;
import org.infoscoop.dao.model.Gadget;

public class GadgetFactory implements CSVBeanFactory {
	public Gadget newBean( CSVField[] values ) {
		Gadget gadget = new Gadget();
		gadget.setType( values[1].toString() );
		gadget.setPath( values[2].toString() );
		gadget.setName( values[3].toString() );
		gadget.setData( values[4].getBytes() );
		
		return gadget;
	}
}
