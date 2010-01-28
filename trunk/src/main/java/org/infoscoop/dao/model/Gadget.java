package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseGadget;



public class Gadget extends BaseGadget {
	private static final long serialVersionUID = 1L;
	
	public static enum FileType {
		GADGET,
		RESOURCE
	}

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Gadget () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Gadget (java.lang.Long id) {
		super(id);
	}

/*[CONSTRUCTOR MARKER END]*/


	public byte[] getData() {
		byte[] data = super.getData();
		if( data == null )
			return new byte[0];
		
		return data;
	}
}