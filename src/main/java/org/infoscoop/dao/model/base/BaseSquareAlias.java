package org.infoscoop.dao.model.base;

import java.io.Serializable;

import org.infoscoop.dao.model.SquareAlias;

public abstract class BaseSquareAlias implements Serializable {
	private static final long serialVersionUID = 1L;
	private int hashCode = Integer.MIN_VALUE;

	public static String REF = "SuareAlias";
	public static String PROP_ID = "id";
	public static String PROP_NAME = "name";
	public static String PROP_SQUARE_ID = "square_id";
	public static String PROP_SYSTEM = "system";

	// primary key
	private Long id;

	// fields
	private String name;
	private String squareId;
	private Boolean system;
	
	public BaseSquareAlias() {
		initialize();
	}

	public BaseSquareAlias(String name, String squareId, Boolean system) {
		this.name = name;
		this.squareId = squareId;
		this.system = system;
	}

	protected void initialize () {}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSquareId() {
		return squareId;
	}

	public void setSquareId(String squareId) {
		this.squareId = squareId;
	}

	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		
		SquareAlias sa = (SquareAlias)obj;
		return this.getId() == sa.getId();
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}
}