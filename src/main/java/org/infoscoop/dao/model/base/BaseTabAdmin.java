package org.infoscoop.dao.model.base;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonRootName;
import org.infoscoop.dao.model.TabAdminPK;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * This is an object that contains data related to the IS_TAB_ADMINS table. Do
 * not modify this class because it will be overwritten if the configuration
 * file related to this class is modified.
 * 
 * @hibernate.class table="IS_TAB_ADMINS"
 */

@JsonRootName("tabAdmin")
@XStreamAlias("tabAdmin")
public abstract class BaseTabAdmin implements Serializable {

	public static String PROP_ID = "Tabid";
	public static String REF = "TabAdmin";
	public static String PROP_UID = "Uid";

	// constructors
	public BaseTabAdmin() {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseTabAdmin(java.lang.String tabId, java.lang.String uid) {
		this.id = new TabAdminPK(tabId, uid);
		initialize();
	}

	protected void initialize() {
	}

	@XStreamOmitField
	private int hashCode = Integer.MIN_VALUE;

	// primary key
	@XStreamOmitField
	private BaseTabAdminPK id;

	/**
	 * Return the unique identifier of this class
	 * 
	 * @hibernate.id column="id"
	 */
	public BaseTabAdminPK getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * 
	 * @param id
	 *            the new ID
	 */
	public void setId(BaseTabAdminPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}

	public int hashCode() {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId())
				return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":"
						+ this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}

	public String toString() {
		return super.toString();
	}

}