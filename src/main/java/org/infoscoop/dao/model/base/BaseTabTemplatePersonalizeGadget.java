package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the IS_TAB_TEMPLATE_PERSONALIZE_GADGETS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_TAB_TEMPLATE_PERSONALIZE_GADGETS"
 */

public abstract class BaseTabTemplatePersonalizeGadget  implements Serializable {

	public static String REF = "TabTemplatePersonalizeGadget";
	public static String PROP_WIDGET_ID = "WidgetId";
	public static String PROP_SIBLING_ID = "SiblingId";
	public static String PROP_SIBLING = "Sibling";
	public static String PROP_ID = "Id";
	public static String PROP_FK_TAB_TEMPLATE = "FkTabTemplate";
	public static String PROP_FK_GADGET_INSTANCE = "FkGadgetInstance";
	public static String PROP_COLUMN_NUM = "ColumnNum";


	// constructors
	public BaseTabTemplatePersonalizeGadget () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseTabTemplatePersonalizeGadget (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseTabTemplatePersonalizeGadget (
		java.lang.Integer id,
		org.infoscoop.dao.model.GadgetInstance fkGadgetInstance,
		org.infoscoop.dao.model.TabTemplate fkTabTemplate,
		java.lang.String widgetId,
		java.lang.Integer columnNum) {

		this.setId(id);
		this.setFkGadgetInstance(fkGadgetInstance);
		this.setFkTabTemplate(fkTabTemplate);
		this.setWidgetId(widgetId);
		this.setColumnNum(columnNum);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String widgetId;
	private java.lang.Integer columnNum;
	private java.lang.Integer siblingId;

	// many to one
	private org.infoscoop.dao.model.GadgetInstance fkGadgetInstance;
	private org.infoscoop.dao.model.TabTemplatePersonalizeGadget sibling;
	private org.infoscoop.dao.model.TabTemplate fkTabTemplate;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="native"
     *  column="id"
     */
	public java.lang.Integer getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: widget_id
	 */
	public java.lang.String getWidgetId () {
		return widgetId;
	}

	/**
	 * Set the value related to the column: widget_id
	 * @param widgetId the widget_id value
	 */
	public void setWidgetId (java.lang.String widgetId) {
		this.widgetId = widgetId;
	}



	/**
	 * Return the value associated with the column: column_num
	 */
	public java.lang.Integer getColumnNum () {
		return columnNum;
	}

	/**
	 * Set the value related to the column: column_num
	 * @param columnNum the column_num value
	 */
	public void setColumnNum (java.lang.Integer columnNum) {
		this.columnNum = columnNum;
	}



	/**
	 * Return the value associated with the column: sibling_id
	 */
	public java.lang.Integer getSiblingId () {
		return siblingId;
	}

	/**
	 * Set the value related to the column: sibling_id
	 * @param siblingId the sibling_id value
	 */
	public void setSiblingId (java.lang.Integer siblingId) {
		this.siblingId = siblingId;
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
	 * Return the value associated with the column: sibling_id
	 */
	public org.infoscoop.dao.model.TabTemplatePersonalizeGadget getSibling () {
		return sibling;
	}

	/**
	 * Set the value related to the column: sibling_id
	 * @param sibling the sibling_id value
	 */
	public void setSibling (org.infoscoop.dao.model.TabTemplatePersonalizeGadget sibling) {
		this.sibling = sibling;
		if(sibling!=null)
			this.siblingId = sibling.getId();
	}



	/**
	 * Return the value associated with the column: fk_tabtemplate_id
	 */
	public org.infoscoop.dao.model.TabTemplate getFkTabTemplate () {
		return fkTabTemplate;
	}

	/**
	 * Set the value related to the column: fk_tabtemplate_id
	 * @param fkTabTemplate the fk_tabtemplate_id value
	 */
	public void setFkTabTemplate (org.infoscoop.dao.model.TabTemplate fkTabTemplate) {
		this.fkTabTemplate = fkTabTemplate;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.TabTemplatePersonalizeGadget)) return false;
		else {
			org.infoscoop.dao.model.TabTemplatePersonalizeGadget tabTemplatePersonalizeGadget = (org.infoscoop.dao.model.TabTemplatePersonalizeGadget) obj;
			if (null == this.getId() || null == tabTemplatePersonalizeGadget.getId()) return false;
			else return (this.getId().equals(tabTemplatePersonalizeGadget.getId()));
		}
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


	public String toString () {
		return super.toString();
	}


}