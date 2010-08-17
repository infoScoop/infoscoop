package org.infoscoop.dao.model;

import java.io.UnsupportedEncodingException;

import org.infoscoop.dao.model.base.BaseMenuUserpref;



public class MenuUserpref extends BaseMenuUserpref {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MenuUserpref () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MenuUserpref (org.infoscoop.dao.model.MenuUserprefsPK id) {
		super(id);
	}

/*[CONSTRUCTOR MARKER END]*/



	public String getValue() {
		String shortValue = getShortValue();
		
		return ( "".equals(shortValue) ? getLongValue() : shortValue );
	}
	public void setValue( String value ) {
		int length;
		try {
			length = value.getBytes("UTF-8").length;
		} catch( UnsupportedEncodingException ex ) {
			throw new RuntimeException( ex );
		}
		
		if( length < 4000 ) {
			setShortValue( value );
			setLongValue( null );
		} else {
			setShortValue( null );
			setLongValue( value );
		}
	}
	public boolean hasLongValue() {
		return !"".equals(getLongValue());
	}

}