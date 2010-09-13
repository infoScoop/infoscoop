package org.infoscoop.dao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.infoscoop.dao.model.base.BaseTabTemplate;



public class TabTemplate extends BaseTabTemplate {
	private static final long serialVersionUID = 1L;

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
		java.lang.String name,
		java.lang.Integer published,
		java.lang.Integer temp) {

		super (
			id,
			tabId,
			name,
			published,
			temp);
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
			if(widgetId == sibling.getWidgetId())return gadget;
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
		
		Set<TabTemplatePersonalizeGadget> tabPGs 
			= this.getTabTemplatePersonalizeGadgets();
		Set<TabTemplatePersonalizeGadget> tabClonePGs = new HashSet<TabTemplatePersonalizeGadget>();
		for(TabTemplatePersonalizeGadget pg: tabPGs){
			tabClonePGs.add(pg.createTemp());
		}
		
		
		
		Set<TabTemplateStaticGadget> tabSGs 
			= this.getTabTemplateStaticGadgets();
		Set<TabTemplateStaticGadget> tabCloneSGs = new HashSet<TabTemplateStaticGadget>();
		for(TabTemplateStaticGadget sg: tabSGs){
			tabCloneSGs.add(sg.createTemp());
		}
		
		tabClone.setName(this.getName());
		tabClone.setPublished(this.getPublished());
		tabClone.setLayout(this.getLayout());
		tabClone.setAccessLevel(this.getAccessLevel());
		tabClone.setTabId(this.getTabId());
		tabClone.setTemp(Integer.valueOf(1));
		tabClone.setTabTemplatePersonalizeGadgets(tabClonePGs);
		tabClone.setTabTemplateStaticGadgets(tabCloneSGs);
		
		return tabClone;
	}
}