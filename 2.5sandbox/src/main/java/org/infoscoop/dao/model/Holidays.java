package org.infoscoop.dao.model;

import java.util.Date;

import org.infoscoop.dao.model.base.BaseHolidays;




public class Holidays extends BaseHolidays {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Holidays () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Holidays (HOLIDAYSPK id) {
		super(id);
	}
	
	public Holidays( HOLIDAYSPK id,String data,Date updatedAt ) {
		super( id,data,updatedAt );
	}

/*[CONSTRUCTOR MARKER END]*/


}