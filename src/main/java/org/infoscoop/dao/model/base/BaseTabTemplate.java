package org.infoscoop.dao.model.base;

import java.io.Serializable;

import org.infoscoop.dao.model.TabTemplate;


/**
 * This is an object that contains data related to the IS_TAB_TEMPLATES table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="IS_TAB_TEMPLATES"
 */

public abstract class BaseTabTemplate  implements Serializable {

	public static String REF = "TabTemplate";
	public static String PROP_ACCESS_LEVEL = "AccessLevel";
	public static String PROP_NAME = "Name";
	public static String PROP_TAB_ID = "TabId";
	public static String PROP_LAYOUT = "Layout";
	public static String PROP_PUBLISHED = "Published";
	public static String PROP_NUMBER_OF_COLUMNS = "NumberOfColumns";
	public static String PROP_AREA_TYPE = "AreaType";
	public static String PROP_ID = "Id";
	public static String PROP_ORIGINAL_ID = "OriginalId";
	public static String PROP_COLUMN_WIDTH = "ColumnWidth";
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
		java.lang.String tabId,
		java.lang.String name,
		java.lang.Integer published,
		java.lang.Integer temp) {

		this.setId(id);
		this.setTabId(tabId);
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
	private java.lang.Integer originalId;
	private java.lang.String tabId;
	private java.lang.Integer areaType = TabTemplate.TYPE_USE_BOTH_AREA;
	private java.lang.String name;
	private java.lang.Integer published;
	private java.lang.Integer accessLevel;
	private java.lang.String layout;
	private java.lang.Integer numberOfColumns;
	private java.lang.String columnWidth;
	private java.lang.Integer temp;

	// collections
	private java.util.Set<org.infoscoop.dao.model.TabTemplatePersonalizeGadget> tabTemplatePersonalizeGadgets;
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
	 * Return the value associated with the column: original_id
	 */
	public java.lang.Integer getOriginalId () {
		return originalId;
	}

	/**
	 * Set the value related to the column: original_id
	 * @param originalId the original_id value
	 */
	public void setOriginalId (java.lang.Integer originalId) {
		this.originalId = originalId;
	}



	/**
	 * Return the value associated with the column: tab_id
	 */
	public java.lang.String getTabId () {
		return tabId;
	}

	/**
	 * Set the value related to the column: tab_id
	 * @param tabId the tab_id value
	 */
	public void setTabId (java.lang.String tabId) {
		this.tabId = tabId;
	}



	/**
	 * Return the value associated with the column: area_type
	 */
	public java.lang.Integer getAreaType () {
		return areaType;
	}

	/**
	 * Set the value related to the column: area_type
	 * @param areaType the area_type value
	 */
	public void setAreaType (java.lang.Integer areaType) {
		this.areaType = areaType;
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
	 * Return the value associated with the column: number_of_columns
	 */
	public java.lang.Integer getNumberOfColumns () {
		return numberOfColumns;
	}

	/**
	 * Set the value related to the column: number_of_columns
	 * @param numberOfColumns the number_of_columns value
	 */
	public void setNumberOfColumns (java.lang.Integer numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}



	/**
	 * Return the value associated with the column: column_width
	 */
	public java.lang.String getColumnWidth () {
		return columnWidth;
	}

	/**
	 * Set the value related to the column: column_width
	 * @param columnWidth the column_width value
	 */
	public void setColumnWidth (java.lang.String columnWidth) {
		this.columnWidth = columnWidth;
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
	 * Return the value associated with the column: TabTemplatePersonalizeGadgets
	 */
	public java.util.Set<org.infoscoop.dao.model.TabTemplatePersonalizeGadget> getTabTemplatePersonalizeGadgets () {
		return tabTemplatePersonalizeGadgets;
	}

	/**
	 * Set the value related to the column: TabTemplatePersonalizeGadgets
	 * @param tabTemplatePersonalizeGadgets the TabTemplatePersonalizeGadgets value
	 */
	public void setTabTemplatePersonalizeGadgets (java.util.Set<org.infoscoop.dao.model.TabTemplatePersonalizeGadget> tabTemplatePersonalizeGadgets) {
		this.tabTemplatePersonalizeGadgets = tabTemplatePersonalizeGadgets;
	}

	public void addToTabTemplatePersonalizeGadgets (org.infoscoop.dao.model.TabTemplatePersonalizeGadget tabTemplatePersonalizeGadget) {
		if (null == getTabTemplatePersonalizeGadgets()) setTabTemplatePersonalizeGadgets(new java.util.TreeSet<org.infoscoop.dao.model.TabTemplatePersonalizeGadget>());
		getTabTemplatePersonalizeGadgets().add(tabTemplatePersonalizeGadget);
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