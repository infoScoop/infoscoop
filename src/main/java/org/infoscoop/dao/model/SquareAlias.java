package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseSquareAlias;

public class SquareAlias extends BaseSquareAlias {
	private static final long serialVersionUID = 1L;

	public SquareAlias(){
		super();
	}

	public SquareAlias(String name, String squareId, Boolean system) {
		super(name, squareId, system);
	}
}
