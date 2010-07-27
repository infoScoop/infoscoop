/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.dao.model;

import java.io.UnsupportedEncodingException;

import org.infoscoop.dao.model.base.BaseUserPref;




public class UserPref extends BaseUserPref {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public UserPref () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public UserPref (org.infoscoop.dao.model.USERPREFPK id) {
		super(id);
	}
	
	public UserPref( USERPREFPK id,String value ) {
		this( id );
		
		setValue( value );
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
