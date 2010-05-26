package org.infoscoop.batch.migration.v200to210;

import java.io.UnsupportedEncodingException;


public class CSVField {
	private byte[] bytes;
	private String string;
	
	public CSVField( String string ) {
		this.string = string;
	}
	public CSVField( byte[] bytes ) {
		this.bytes = bytes;
	}
	
	public byte[] getBytes() {
		if( bytes == null ) {
			try {
				bytes = string.getBytes("UTF-8");
			} catch( UnsupportedEncodingException ex ) {
				throw new RuntimeException( ex );
			}
		}
		
		return bytes;
	}
	public String toString() {
		if( string == null ) {
			try {
				string = new String( bytes,"UTF-8");
			} catch( UnsupportedEncodingException ex ) {
				throw new RuntimeException( ex );
			}
		}
		
		return string;
	}
	
	public Integer toInt() {
		try {
			return new Integer( toString() );
		} catch( NumberFormatException ex ) {
			return null;
		}
	}
}
