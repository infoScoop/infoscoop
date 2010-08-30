package org.infoscoop.dao.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the is_tab_templates table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="is_tab_templates"
 */

public abstract class BaseTabTemplate  implements Serializable {

	public static String REF = "TabTemplate";
	public static String PROP_ACCESS_LEVEL = "AccessLevel";
	public static String PROP_NAME = "Name";
	public static String PROP_LAYOUT = "Layout";
	public static String PROP_PUBLISHED = "Published";
	public static String PROP_ID = "Id";
	public static String PROP_TEMP = "Temp";


	// constructors
	public BaseTabTemplate () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseTabTemplate (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseTabTemplate (
		java.lang.Integer id,
		java.lang.String name,
		java.lang.Integer published,
		java.lang.Integer temp) {

		this.setId(id);
		this.setName(name);
		this.setPublished(published);
		this.setTemp(temp);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String name;
	private java.lang.Integer published = Integer.valueOf(0);
	private java.lang.Integer accessLevel;
	private java.lang.String layout;
	private java.lang.Integer temp;

	// collections
	private java.util.Set<org.infoscoop.dao.model.TabTemplateParsonalizeGadget> tabTemplateParsonalizeGadgets;
	private java.util.Set<org.infoscoop.dao.model.TabTemplateStaticGadget> tabTemplateStaticGadgets;



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



	/**
	 * Return the value associated with the column: published
	 */
	public java.lang.Integer getPublished () {
		return published;
	}

	/**
	 * Set the value related to the column: published
	 * @param published the published value
	 */
	public void setPublished (java.lang.Integer published) {
		this.published = published;
	}



	/**
	 * Return the value associated with the column: access_level
	 */
	public java.lang.Integer getAccessLevel () {
		return accessLevel;
	}

	/**
	 * Set the value related to the column: access_level
	 * @param accessLevel the access_level value
	 */
	public void setAccessLevel (java.lang.Integer accessLevel) {
		this.accessLevel = accessLevel;
	}



	/**
	 * Return the value associated with the column: layout
	 */
	public java.lang.String getLayout () {
		return layout;
	}

	/**
	 * Set the value related to the column: layout
	 * @param layout the layout value
	 */
	public void setLayout (java.lang.String layout) {
		this.layout = layout;
	}



	/**
	 * Return the value associated with the column: temp
	 */
	public java.lang.Integer getTemp () {
		return temp;
	}

	/**
	 * Set the value related to the column: temp
	 * @param temp the temp value
	 */
	public void setTemp (java.lang.Integer temp) {
		this.temp = temp;
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



	/**
	 * Return the value associated with the column: TabTemplateStaticGadgets
	 */
	public java.util.Set<org.infoscoop.dao.model.TabTemplateStaticGadget> getTabTemplateStaticGadgets () {
		return tabTemplateStaticGadgets;
	}

	/**
	 * Set the value related to the column: TabTemplateStaticGadgets
	 * @param tabTemplateStaticGadgets the TabTemplateStaticGadgets value
	 */
	public void setTabTemplateStaticGadgets (java.util.Set<org.infoscoop.dao.model.TabTemplateStaticGadget> tabTemplateStaticGadgets) {
		this.tabTemplateStaticGadgets = tabTemplateStaticGadgets;
	}

	public void addToTabTemplateStaticGadgets (org.infoscoop.dao.model.TabTemplateStaticGadget tabTemplateStaticGadget) {
		if (null == getTabTemplateStaticGadgets()) setTabTemplateStaticGadgets(new java.util.TreeSet<org.infoscoop.dao.model.TabTemplateStaticGadget>());
		getTabTemplateStaticGadgets().add(tabTemplateStaticGadget);
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.TabTemplate)) return false;
		else {
			org.infoscoop.dao.model.TabTemplate tabTemplate = (org.infoscoop.dao.model.TabTemplate) obj;
			if (null == this.getId() || null == tabTemplate.getId()) return false;
			else return (this.getId().equals(tabTemplate.getId()));
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