package org.infoscoop.dao.model;

import org.infoscoop.dao.model.base.BaseTabTemplateStaticGadget;



public class TabTemplateStaticGadget extends BaseTabTemplateStaticGadget implements StaticGadget{
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public TabTemplateStaticGadget () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public TabTemplateStaticGadget (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public TabTemplateStaticGadget (
		java.lang.Integer id,
		org.infoscoop.dao.model.GadgetInstance gadgetInstance,
		java.lang.String containerId) {

		super (
			id,
			gadgetInstance,
			containerId);
	}

/*[CONSTRUCTOR MARKER END]*/

	private String tabTemplateId;
	
	public String getTabTemplateId(){
		return this.tabTemplateId;
	}

	public void setTabTemplateId(String tabId){
		this.tabTemplateId = tabId;
	}
	
	private String instanceId;
	
	public String getInstanceId(){
		return this.instanceId;
	}
	
	public void setInstanceId(String instanceId){
		this.instanceId = instanceId;
	}
	
	public TabTemplateStaticGadget createTemp(
			TabTemplate tabCopy)throws CloneNotSupportedException{
		TabTemplateStaticGadget SGClone = new TabTemplateStaticGadget();
		
		SGClone.setContainerId(this.getContainerId());
		SGClone.setIgnoreHeader(this.getIgnoreHeader());
		SGClone.setNoBorder(this.getNoBorder());
		SGClone.setGadgetInstance(this.getGadgetInstance().copy());
		SGClone.setFkTabTemplate(tabCopy);
		
		//TabTemplateStaticGadgetDAO.newInstance().save(SGClone);
		
		return SGClone;
	}
	
	public boolean isIgnoreHeaderBool() {
		return this.getIgnoreHeader() != null && this.getIgnoreHeader() == 1;
	}

	public boolean isNoBorderBool() {
		return this.getNoBorder() != null && this.getNoBorder() == 1;
	}

	public void setIgnoreHeaderBool(boolean ignoreHeader) {
		this.setIgnoreHeader(ignoreHeader ? 1 : null);
	}

	public void setNoBorderBool(boolean noBorder) {
		this.setNoBorder(noBorder ? 1 : null);
	}
}