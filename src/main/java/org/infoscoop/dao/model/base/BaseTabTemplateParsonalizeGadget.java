package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_tabtemplate_parsonalize_gadgets table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_tabtemplate_parsonalize_gadgets"
 */

public abstract class BaseTabTemplateParsonalizeGadget  implements Serializable {

	public static String REF = "TabTemplateParsonalizeGadget";
	public static String PROP_SIBLING = "Sibling";
	public static String PROP_ID = "Id";
	public static String PROP_FK_TAB_TEMPLATE = "FkTabTemplate";
	public static String PROP_FK_GADGET_INSTANCE = "FkGadgetInstance";
	public static String PROP_COLUMN_NUM = "ColumnNum";


	// constructors
	public BaseTabTemplateParsonalizeGadget () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseTabTemplateParsonalizeGadget (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseTabTemplateParsonalizeGadget (
		java.lang.Integer id,
		org.infoscoop.dao.model.GadgetInstance fkGadgetInstance,
		org.infoscoop.dao.model.TabTemplate fkTabTemplate,
		java.lang.Integer columnNum) {

		this.setId(id);
		this.setFkGadgetInstance(fkGadgetInstance);
		this.setFkTabTemplate(fkTabTemplate);
		this.setColumnNum(columnNum);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.Integer columnNum;

	// many to one
	private org.infoscoop.dao.model.GadgetInstance fkGadgetInstance;
	private org.infoscoop.dao.model.TabTemplate fkTabTemplate;
	private org.infoscoop.dao.model.TabTemplateParsonalizeGadget sibling;

	// collections
	private java.util.Set<org.infoscoop.dao.model.TabTemplateParsonalizeGadget> tabTemplateParsonalizeGadgets;



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



	/**
	 * Return the value associated with the column: sibling_id
	 */
	public org.infoscoop.dao.model.TabTemplateParsonalizeGadget getSibling () {
		return sibling;
	}

	/**
	 * Set the value related to the column: sibling_id
	 * @param sibling the sibling_id value
	 */
	public void setSibling (org.infoscoop.dao.model.TabTemplateParsonalizeGadget sibling) {
		this.sibling = sibling;
	}



	/**
	 * Return the value associated with the column: TabTemplateParsonalizeGadgets
	 */
	public java.util.Set<org.infoscoop.dao.model.TabTemplateParsonalizeGadget> getTabTemplateParsonalizeGadgets () {
		return tabTemplateParsonalizeGadgets;
	}

	/**
	 * Set the value related to the column: TabTemplateParsonalizeGadgets
	 * @param tabTemplateParsonalizeGadgets the TabTemplateParsonalizeGadgets value
	 */
	public void setTabTemplateParsonalizeGadgets (java.util.Set<org.infoscoop.dao.model.TabTemplateParsonalizeGadget> tabTemplateParsonalizeGadgets) {
		this.tabTemplateParsonalizeGadgets = tabTemplateParsonalizeGadgets;
	}

	public void addToTabTemplateParsonalizeGadgets (org.infoscoop.dao.model.TabTemplateParsonalizeGadget tabTemplateParsonalizeGadget) {
		if (null == getTabTemplateParsonalizeGadgets()) setTabTemplateParsonalizeGadgets(new java.util.TreeSet<org.infoscoop.dao.model.TabTemplateParsonalizeGadget>());
		getTabTemplateParsonalizeGadgets().add(tabTemplateParsonalizeGadget);
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.TabTemplateParsonalizeGadget)) return false;
		else {
			org.infoscoop.dao.model.TabTemplateParsonalizeGadget tabTemplateParsonalizeGadget = (org.infoscoop.dao.model.TabTemplateParsonalizeGadget) obj;
			if (null == this.getId() || null == tabTemplateParsonalizeGadget.getId()) return false;
			else return (this.getId().equals(tabTemplateParsonalizeGadget.getId()));
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