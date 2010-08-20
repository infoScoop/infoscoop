package org.infoscoop.dao.model.base;

import java.io.Serializable;


public abstract class BaseIsGadgetInstanceUserprefsPK implements Serializable {

	protected int hashCode = Integer.MIN_VALUE;

	private org.infoscoop.dao.model.GadgetInstance fkGadgetInstance;
	private java.lang.String name;


	public BaseIsGadgetInstanceUserprefsPK () {}
	
	public BaseIsGadgetInstanceUserprefsPK (
		org.infoscoop.dao.model.GadgetInstance fkGadgetInstance,
		java.lang.String name) {

		this.setFkGadgetInstance(fkGadgetInstance);
		this.setName(name);
	}


	/**
	 * Return the value associated with the column: fk_gadget_instance_id
	 */
	public org.infoscoop.dao.model.GadgetInstance getFkGadgetInstance () {
		return fkGadgetInstance;
	}

	/**
	 * Set the value related to the column: fk_gadget_instance_id
	 * @param fkGadgetInstance the fk_gadget_instance_id value
	 */
	public void setFkGadgetInstance (org.infoscoop.dao.model.GadgetInstance fkGadgetInstance) {
		this.fkGadgetInstance = fkGadgetInstance;
	}



	/**
	 * Return the value associated with the column: name
	 */
	public java.lang.String getName () {
		return name;
	}

	/**
	 * Set the value related to the column: name
	 * @param name the name value
	 */
	public void setName (java.lang.String name) {
		this.name = name;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.GadgetInstanceUserprefPK)) return false;
		else {
			org.infoscoop.dao.model.GadgetInstanceUserprefPK mObj = (org.infoscoop.dao.model.GadgetInstanceUserprefPK) obj;
			if (null != this.getFkGadgetInstance() && null != mObj.getFkGadgetInstance()) {
				if (!this.getFkGadgetInstance().equals(mObj.getFkGadgetInstance())) {
					return false;
				}
			}
			else {
				return false;
			}
			if (null != this.getName() && null != mObj.getName()) {
				if (!this.getName().equals(mObj.getName())) {
					return false;
				}
			}
			else {
				return false;
			}
			return true;
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			StringBuilder sb = new StringBuilder();
			if (null != this.getFkGadgetInstance()) {
				sb.append(this.getFkGadgetInstance().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			if (null != this.getName()) {
				sb.append(this.getName().hashCode());
				sb.append(":");
			}
			else {
				return super.hashCode();
			}
			this.hashCode = sb.toString().hashCode();
		}
		return this.hashCode;
	}


}