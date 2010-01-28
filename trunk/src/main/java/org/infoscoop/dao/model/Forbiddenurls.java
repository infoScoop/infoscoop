package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseForbiddenurls;



public class Forbiddenurls extends BaseForbiddenurls {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Forbiddenurls () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Forbiddenurls (Long id) {
		super(id);
	}
	
	public Forbiddenurls( Long id,String url ) {
		this( id );
		
		super.setUrl( url );
	}

/*[CONSTRUCTOR MARKER END]*/


}