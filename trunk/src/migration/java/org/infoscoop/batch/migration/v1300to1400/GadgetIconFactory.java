package org.infoscoop.batch.migration.v1300to1400;

import org.infoscoop.dao.model.GadgetIcon;


class GadgetIconFactory implements CSVBeanFactory {
	public GadgetIcon newBean( CSVField[] values ) {
		GadgetIcon gadgetIcon = new GadgetIcon();
		gadgetIcon.setType( values[0].toString() );
		gadgetIcon.setUrl( values[1].toString() );

		return gadgetIcon;
	}
}
