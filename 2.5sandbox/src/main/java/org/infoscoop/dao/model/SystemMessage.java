package org.infoscoop.dao.model;

import java.util.ArrayList;
import java.util.Collection;

import org.infoscoop.dao.model.base.BaseSystemMessage;



public class SystemMessage extends BaseSystemMessage {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public SystemMessage () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public SystemMessage (java.lang.Long id) {
		super(id);
	}

	public SystemMessage(String to, String resourceId, String replaceValues) {
		super(to, resourceId, replaceValues);
	}

	public Collection<String> getReplaceValueCollection() {
		String[] values = super.getReplaceValues().split(",");
		Collection<String> msgs = new ArrayList<String>();
		for(int i = 0; i < values.length; i++){
			msgs.add(values[i].trim());
		}
		return msgs;
	}

/*[CONSTRUCTOR MARKER END]*/


}