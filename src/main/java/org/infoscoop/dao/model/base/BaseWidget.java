package org.infoscoop.dao.model.base;

import java.io.Serializable;

import org.infoscoop.dao.model.UserPref;



/**
 * This is an object that contains data related to the WIDGET table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="WIDGET"
 */

public abstract class BaseWidget  implements Serializable {

	public static String REF = "Widget";
	public static String PROP_SIBLINGID = "Siblingid";
	public static String PROP_TYPE = "Type";
	public static String PROP_DELETEDATE = "Deletedate";
	public static String PROP_PARENTID = "Parentid";
	public static String PROP_WIDGETID = "Widgetid";
	public static String PROP_DEFAULTUID = "Defaultuid";
	public static String PROP_TABID = "Tabid";
	public static String PROP_IGNOREHEADER = "Ignoreheader";
	public static String PROP_HREF = "Href";
	public static String PROP_ISSTATIC = "Isstatic";
	public static String PROP_TITLE = "Title";
	public static String PROP_COLUMN = "Column";
	public static String PROP_ID = "Id";
	public static String PROP_UID = "Uid";
	public static String PROP_MENUID = "Menuid";
	public static String PROP_CREATEDATE = "Createdate";


	// constructors
	public BaseWidget () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseWidget (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
	private java.lang.String tabid;
	private java.lang.Long deletedate;
	private java.lang.String widgetid;
	private java.lang.String uid;
	private java.lang.String defaultuid;
	private java.lang.Integer column;
	private java.lang.String siblingid;
	private java.lang.String parentid;
	private java.lang.String href;
	private java.lang.String title;
	private java.lang.String type;
	private java.lang.Integer isstatic;
	private java.lang.Integer ignoreheader;
	private java.lang.String menuid;
	private java.lang.Long createdate = 0L;

	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="native"
     *  column="ID"
     */
	public java.lang.String getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.String id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: TABID
	 */
	public java.lang.String getTabid () {
		return tabid;
	}

	/**
	 * Set the value related to the column: TABID
	 * @param tabid the TABID value
	 */
	public void setTabid (java.lang.String tabid) {
		this.tabid = tabid;
	}



	/**
	 * Return the value associated with the column: DELETEDATE
	 */
	public java.lang.Long getDeletedate () {
		return deletedate;
	}

	/**
	 * Set the value related to the column: DELETEDATE
	 * @param deletedate the DELETEDATE value
	 */
	public void setDeletedate (java.lang.Long deletedate) {
		this.deletedate = deletedate;
	}



	/**
	 * Return the value associated with the column: WIDGETID
	 */
	public java.lang.String getWidgetid () {
		return widgetid;
	}

	/**
	 * Set the value related to the column: WIDGETID
	 * @param widgetid the WIDGETID value
	 */
	public void setWidgetid (java.lang.String widgetid) {
		this.widgetid = widgetid;
	}



	/**
	 * Return the value associated with the column: `UID`
	 */
	public java.lang.String getUid () {
		return uid;
	}

	/**
	 * Set the value related to the column: `UID`
	 * @param uid the `UID` value
	 */
	public void setUid (java.lang.String uid) {
		this.uid = uid;
	}



	/**
	 * Return the value associated with the column: DEFAULTUID
	 */
	public java.lang.String getDefaultuid () {
		return defaultuid;
	}

	/**
	 * Set the value related to the column: DEFAULTUID
	 * @param defaultuid the DEFAULTUID value
	 */
	public void setDefaultuid (java.lang.String defaultuid) {
		this.defaultuid = defaultuid;
	}



	/**
	 * Return the value associated with the column: `COLUMN`
	 */
	public java.lang.Integer getColumn () {
		return column;
	}

	/**
	 * Set the value related to the column: `COLUMN`
	 * @param column the `COLUMN` value
	 */
	public void setColumn (java.lang.Integer column) {
		this.column = column;
	}



	/**
	 * Return the value associated with the column: SIBLINGID
	 */
	public java.lang.String getSiblingid () {
		return siblingid;
	}

	/**
	 * Set the value related to the column: SIBLINGID
	 * @param siblingid the SIBLINGID value
	 */
	public void setSiblingid (java.lang.String siblingid) {
		this.siblingid = siblingid;
	}



	/**
	 * Return the value associated with the column: PARENTID
	 */
	public java.lang.String getParentid () {
		return parentid;
	}

	/**
	 * Set the value related to the column: PARENTID
	 * @param parentid the PARENTID value
	 */
	public void setParentid (java.lang.String parentid) {
		this.parentid = parentid;
	}



	/**
	 * Return the value associated with the column: HREF
	 */
	public java.lang.String getHref () {
		return href;
	}

	/**
	 * Set the value related to the column: HREF
	 * @param href the HREF value
	 */
	public void setHref (java.lang.String href) {
		this.href = href;
	}



	/**
	 * Return the value associated with the column: TITLE
	 */
	public java.lang.String getTitle () {
		return title;
	}

	/**
	 * Set the value related to the column: TITLE
	 * @param title the TITLE value
	 */
	public void setTitle (java.lang.String title) {
		this.title = title;
	}



	/**
	 * Return the value associated with the column: TYPE
	 */
	public java.lang.String getType () {
		return type;
	}

	/**
	 * Set the value related to the column: TYPE
	 * @param type the TYPE value
	 */
	public void setType (java.lang.String type) {
		this.type = type;
	}



	/**
	 * Return the value associated with the column: ISSTATIC
	 */
	public java.lang.Integer getIsstatic () {
		return isstatic;
	}

	/**
	 * Set the value related to the column: ISSTATIC
	 * @param isstatic the ISSTATIC value
	 */
	public void setIsstatic (java.lang.Integer isstatic) {
		this.isstatic = isstatic;
	}



	/**
	 * Return the value associated with the column: IGNOREHEADER
	 */
	public java.lang.Integer getIgnoreheader () {
		return ignoreheader;
	}

	/**
	 * Set the value related to the column: IGNOREHEADER
	 * @param ignoreheader the IGNOREHEADER value
	 */
	public void setIgnoreheader (java.lang.Integer ignoreheader) {
		this.ignoreheader = ignoreheader;
	}



	/**
	 * Return the value associated with the column: MENUID
	 */
	public java.lang.String getMenuid () {
		return menuid;
	}

	/**
	 * Set the value related to the column: MENUID
	 * @param menuid the MENUID value
	 */
	public void setMenuid (java.lang.String menuid) {
		this.menuid = menuid;
	}



	/**
	 * Return the value associated with the column: CREATEDATE
	 */
	public java.lang.Long getCreatedate () {
		return createdate;
	}

	/**
	 * Set the value related to the column: CREATEDATE
	 * @param deletedate the CREATEDATE value
	 */
	public void setCreatedate (java.lang.Long createdate) {
		this.createdate = createdate;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof org.infoscoop.dao.model.Widget)) return false;
		else {
			org.infoscoop.dao.model.Widget widget = (org.infoscoop.dao.model.Widget) obj;
			if (null == this.getId() || null == widget.getId()) return false;
			else return (this.getId().equals(widget.getId()));
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