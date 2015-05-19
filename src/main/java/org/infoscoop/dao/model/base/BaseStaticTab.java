package org.infoscoop.dao.model.base;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.infoscoop.dao.model.StaticTabPK;
import org.infoscoop.dao.model.TabLayout;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * This is an object that contains data related to the IS_STATIC_TABS table.
 * Do not modify this class because it will be overwritten if the configuration
 * file related to this class is modified.
 * 
 * @hibernate.class table="IS_STATIC_TABS"
 */

@JsonRootName("tab")
@XStreamAlias("tab")
public abstract class BaseStaticTab implements Serializable {

	public static String PROP_ID = "Tabid";
	public static String REF = "StaticTab";
	public static String PROP_TAB_DESC = "Tabdesc";
	public static String PROP_TABNUMBER = "Tabnumber";
	public static String PROP_DELETEFLAG = "Deleteflag";
	public static Integer DELETEFLAG_FALSE = 0;
	public static Integer DELETEFLAG_TRUE = 1;
	public static Integer DISABLE_DEFAULT_FALSE = 0;
	public static Integer DISABLE_DEFAULT_TRUE = 1;

	// constructors
	public BaseStaticTab() {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseStaticTab(StaticTabPK id) {
		this.setId(id);
		initialize();
	}

	protected void initialize() {
	}

	@XStreamOmitField
	private int hashCode = Integer.MIN_VALUE;

	// primary key
    @XStreamAsAttribute
	private StaticTabPK id;

	// fields
    @XStreamAsAttribute
	private java.lang.Integer tabNumber;
    
	@XStreamOmitField
	private java.lang.Integer deleteFlag;
	
    @XStreamAsAttribute
	private java.lang.Integer disableDefault;
	private java.lang.String tabDesc;

	@XStreamImplicit(itemFieldName="admin")
	private java.util.Set<org.infoscoop.dao.model.TabAdmin> TabAdmin;

	@XStreamOmitField
	private java.util.Set<org.infoscoop.dao.model.TabLayout> TabLayout;

	// tabLayout (temp=0)
	@XStreamImplicit(itemFieldName="role")
	private java.util.Set<org.infoscoop.dao.model.TabLayout> currentTabLayout;

	public java.util.Set<org.infoscoop.dao.model.TabLayout> getTabLayout() {
		return TabLayout;
	}

	public void setTabLayout(
			java.util.Set<org.infoscoop.dao.model.TabLayout> tabLayout) {
		TabLayout = tabLayout;
		
		Set<TabLayout> hashSet = new HashSet<TabLayout>();
		
		// Processing which should be defined as a subclass. 
		// But, if it extends, the conversion result of XStream is not right. 
		for(Iterator<TabLayout> ite=tabLayout.iterator();ite.hasNext();){
			TabLayout tl = ite.next();
			if(tl.getId().getTemp().intValue() == org.infoscoop.dao.model.TabLayout.TEMP_FALSE)
				hashSet.add(tl);
		}
		currentTabLayout = hashSet;
	}

	/**
	 * Return the unique identifier of this class
	 * 
	 * @hibernate.id column="id"
	 */
	public StaticTabPK getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * 
	 * @param id
	 *            the new ID
	 */
	public void setId(StaticTabPK id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}

	public java.lang.String getTabdesc() {
		return this.tabDesc;
	}

	public void setTabdesc(java.lang.String tabDesc) {
		this.tabDesc = tabDesc;
	}

	public java.lang.Integer getTabnumber() {
		return this.tabNumber;
	}

	public void setTabnumber(java.lang.Integer tabNumber) {
		this.tabNumber = tabNumber;
	}

	public java.lang.Integer getDeleteflag() {
		return this.deleteFlag;
	}

	public void setDeleteflag(java.lang.Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public java.lang.Integer getDisabledefault() {
		return this.disableDefault;
	}

	public void setDisabledefault(java.lang.Integer disableDefault) {
		this.disableDefault = disableDefault;
	}

	public void setTabAdmin(
			java.util.Set<org.infoscoop.dao.model.TabAdmin> TabAdmin) {
		this.TabAdmin = TabAdmin;
	}

	public java.util.Set<org.infoscoop.dao.model.TabAdmin> getTabAdmin() {
		return this.TabAdmin;
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