package org.infoscoop.dao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.TabTemplateDAO;
import org.infoscoop.dao.model.base.BaseTabTemplate;
import org.json.JSONObject;



public class TabTemplate extends BaseTabTemplate {
	public static final int TYPE_USE_BOTH_AREA = 0;
	public static final int TYPE_STATIC_AREA_ONLY = 1;
	public static final int TYPE_STATIC_AREA_ADJUST_HEIGHT = 2;
	
	private static final long serialVersionUID = 1L;
	private static final Integer DEFAULT_NUMBER_OF_COLUMNS = Integer.valueOf(3);
	private String layoutModified;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public TabTemplate () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public TabTemplate (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public TabTemplate (
		java.lang.Integer id,
		java.lang.String tabId,
		java.lang.Integer orderIndex,
		java.lang.String name,
		java.lang.Integer status) {

		super (
			id,
			tabId,
			orderIndex,
			name,
			status);
	}

	/* [CONSTRUCTOR MARKER END] */
	/*
	List<String> staticGadgets;
	
	public List<String> getStaticGadgets() {
		if(this.staticGadgets != null){
			return this.staticGadgets;
		}else{
			this.staticGadgets = new SGList(this);
			if (super.getTabTemplateStaticGadgets() != null) {
				for (TabTemplateStaticGadget sg : super
						.getTabTemplateStaticGadgets()) {
					staticGadgets.add(sg.getContainerId());
				}
			}
			return this.staticGadgets;
		}
	}
	
	public class SGList extends ArrayList<String>{
		private static final long serialVersionUID = 1L;
		TabTemplate self;
		
		public SGList(TabTemplate tabTemplate) {
			this.self = tabTemplate;
		}
		
		@Override
		public boolean add(String id) {
			super.add(id);
			TabTemplateStaticGadget sg = new TabTemplateStaticGadget();
			sg.setContainerId(id);
			this.self.getTabTemplateStaticGadgets().add(sg);
			return true;
		}

	}
	*/

	/*
	private List<TabTemplateStaticGadget> staticGadgets = LazyList.decorate(
			new ArrayList<TabTemplateStaticGadget>(), FactoryUtils
					.instantiateFactory(TabTemplateStaticGadget.class));

	public java.util.List<org.infoscoop.dao.model.TabTemplateStaticGadget> getStaticGadgets() {
		return staticGadgets;
	}
*/
	@Override
	protected void initialize() {
		super.initialize();
		super.setFkDomainId(DomainManager.getContextDomainId());
		super.setAreaType(TabTemplate.TYPE_USE_BOTH_AREA);
		super.setNumberOfColumns(DEFAULT_NUMBER_OF_COLUMNS);
	}

	public TabTemplatePersonalizeGadget getPersonalizeGadget(Integer id) {
		for(TabTemplatePersonalizeGadget gadget : super.getTabTemplatePersonalizeGadgets()){
			if(gadget.getId().equals(id))return gadget;
		}
		return null;
	}

	public TabTemplatePersonalizeGadget getPersonalizeGadgetByWidgetId(String widgetId) {
		for(TabTemplatePersonalizeGadget gadget : super.getTabTemplatePersonalizeGadgets()){
			if(gadget.getWidgetId().equals(widgetId))return gadget;
		}
		return null;
	}

	public TabTemplatePersonalizeGadget getPersonalizeGadgetBySibling(String widgetId) {
		for(TabTemplatePersonalizeGadget gadget : super.getTabTemplatePersonalizeGadgets()){
			if(gadget.getSiblingId() == null)continue;
			TabTemplatePersonalizeGadget sibling = this.getPersonalizeGadget(gadget.getSiblingId());
			if(widgetId.equals(sibling.getWidgetId()))return gadget;
		}
		return null;
	}

	public TabTemplatePersonalizeGadget getSubWidgetBySibling(String siblingId, String parentId) {
		Collection<TabTemplatePersonalizeGadget> subWidgets = new ArrayList<TabTemplatePersonalizeGadget>();
		for(TabTemplatePersonalizeGadget gadget : super.getTabTemplatePersonalizeGadgets()){
			//
		}
		return null;
	}

	public TabTemplatePersonalizeGadget getNextSiblingOnColumn(String siblingId, Integer colNum) {
		for(TabTemplatePersonalizeGadget gadget : super.getTabTemplatePersonalizeGadgets()){
			if(gadget.getColumnNum() == null || gadget.getSiblingId() == null)continue;
			TabTemplatePersonalizeGadget sibling = this.getPersonalizeGadget(gadget.getSiblingId());
			if(colNum.equals(gadget.getColumnNum()) && sibling.getWidgetId().equals(siblingId))
				return gadget;
		}
		return null;
	}

	public void removeTabTemplatePersonalizeGadget(TabTemplatePersonalizeGadget widget) {
		this.getTabTemplatePersonalizeGadgets().remove(widget);
	}

	public TabTemplatePersonalizeGadget getNextSibling(String siblingId) {
		for(TabTemplatePersonalizeGadget gadget : super.getTabTemplatePersonalizeGadgets()){
			if(gadget.getSiblingId() == null)continue;
			if( gadget.getSiblingId().equals(Integer.valueOf(siblingId)))
				return gadget;
		}
		return null;
	}

	public TabTemplate createTemp() throws CloneNotSupportedException{
		TabTemplate tabClone = new TabTemplate();
		tabClone.setName(this.getName());
		tabClone.setLayout(this.getLayout());
		tabClone.setPublish(this.getPublish());
		tabClone.setTabId(this.getTabId());
		tabClone.setOrderIndex(this.getOrderIndex());
		tabClone.setAreaType(this.getAreaType());
		tabClone.setStatus(Integer.valueOf(1));
		TabTemplateDAO.newInstance().save(tabClone);
		
		Set<TabTemplatePersonalizeGadget> tabPGs =
				this.getTabTemplatePersonalizeGadgets();
		Set<TabTemplatePersonalizeGadget> tabClonePGs = 
				new HashSet<TabTemplatePersonalizeGadget>();
		for(TabTemplatePersonalizeGadget pg: tabPGs){
			tabClonePGs.add(pg.createTemp(tabClone));
		}
		
		
		
		Set<TabTemplateStaticGadget> tabSGs 
			= this.getTabTemplateStaticGadgets();
		Set<TabTemplateStaticGadget> tabCloneSGs = new HashSet<TabTemplateStaticGadget>();
		for(TabTemplateStaticGadget sg: tabSGs){
			tabCloneSGs.add(sg.createTemp(tabClone));
		}
		
		
		tabClone.setTabTemplatePersonalizeGadgets(tabClonePGs);
		tabClone.setTabTemplateStaticGadgets(tabCloneSGs);
		
		Set<Role> cloneRoles = new HashSet<Role>();
		for(Role role:this.getRoles())
			cloneRoles.add(role);
		tabClone.setRoles(cloneRoles);
		return tabClone;
	}

	
	public void setLayoutModified(String layoutModified){
		this.layoutModified = layoutModified;
	}
	
	public String getLayoutModified(){
		return this.layoutModified;
	}

	public boolean isLayoutModified() {
		return Boolean.valueOf(this.layoutModified);
	}
	
	public String getEscapedLayout(){
		return JSONObject.quote(super.getLayout());
	}

	public boolean getPublishBool(){
		return super.getPublish() == 1;
	}
	
}