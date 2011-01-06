/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.DomainManager;
import org.infoscoop.dao.MenuItemDAO;
import org.infoscoop.dao.PreferenceDAO;
import org.infoscoop.dao.SessionDAO;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.CommandBar;
import org.infoscoop.dao.model.CommandBarStaticGadget;
import org.infoscoop.dao.model.GadgetInstance;
import org.infoscoop.dao.model.GadgetInstanceUserpref;
import org.infoscoop.dao.model.MenuItem;
import org.infoscoop.dao.model.Preference;
import org.infoscoop.dao.model.StaticGadget;
import org.infoscoop.dao.model.TABPK;
import org.infoscoop.dao.model.Tab;
import org.infoscoop.dao.model.TabTemplate;
import org.infoscoop.dao.model.TabTemplatePersonalizeGadget;
import org.infoscoop.dao.model.TabTemplateStaticGadget;
import org.infoscoop.dao.model.UserPref;
import org.infoscoop.dao.model.Widget;
import org.infoscoop.util.SpringUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;

public class TabService {
	private static Log log = LogFactory.getLog(TabService.class);
	
	private static final String TABID_HOME = "0";
	private TabDAO tabDAO;
	private WidgetDAO widgetDAO;

	
	public void setTabDAO(TabDAO tabDAO) {
		this.tabDAO = tabDAO;
	}
	
	public void setWidgetDAO(WidgetDAO widgetDAO) {
		this.widgetDAO = widgetDAO;
	}
	
	public static TabService getHandle() {
		return (TabService)SpringUtil.getBean("TabService");
	}
	
	/**
	 * Return object of specified tabOrder
	 * 
	 * @param uid
	 * @param defaultUid
	 * @param tabNumber
	 * @return
	 * @throws Exception
	 */
	public Object getWidgetsNodeByTabOrder(String uid, int tabOrder) throws Exception {
		ArrayList tabList = (ArrayList)getWidgetsNode(uid);
		
		//TODO:implement for the moment
		Collections.sort((ArrayList)tabList, new Comparator(){

			public int compare(Object arg0, Object arg1) {
				Object[] obj0 = (Object[])arg0;
				Object[] obj1 = (Object[])arg1;
				
				Tab tab1 = (Tab)obj0[0];
				Tab tab2 = (Tab)obj1[0];
				if("static".equalsIgnoreCase( tab1.getType().toLowerCase() ) && "static".equalsIgnoreCase(tab2.getType().toLowerCase()) ||
						!"static".equalsIgnoreCase( tab1.getType().toLowerCase() ) && !"static".equalsIgnoreCase(tab2.getType().toLowerCase())){
					return tab1.getOrder().compareTo(tab2.getOrder());
				}else{
					if("static".equalsIgnoreCase( tab1.getType().toLowerCase()) ){
						return -1;
					}else{
						return 1;
					}
				}
			}
		});
		
		return tabList.get(tabOrder);
	}
	/**
	 * Obtain "widgets" node of the specified userID from DB
	 * @param uid
	 * @return
	 * @throws Exception 
	 * @throws DBAccessException 
	 */
	public Collection<TabDetail> getWidgetsNode(String uid) throws Exception {
		Collection<TabDetail> tabList = new ArrayList<TabDetail>();
	
		CommandBar cmdBar = CommandBarService.getHandle().getMyCommandBar();
		Tab commandbar = new Tab(new TABPK(cmdBar.getFkDomainId(), uid, "commandbar"));
		commandbar.setName("commandbar");
		commandbar.setData("{}");
		tabList.add(
				new TabDetail(
					commandbar,
					cmdBar.getLayout(),
					new ArrayList<Widget>(),
					createStaticGadgetList(uid, cmdBar)
				)
			);
		
		
		List<TabTemplate> tabTemplates = TabTemplateService.getHandle().getMyTabTemplate();
		Collection<Tab> currentTabList = this.tabDAO.getTabs(uid);
		
		Map<String, Tab> staticTabMap = new HashMap<String,Tab>();
		for(Tab tab: currentTabList)
			if("static".equals(tab.getType()))
				staticTabMap.put(tab.getTabId(), tab);
		
		Set<String> tabTemplateIds = new HashSet<String>();
		for(TabTemplate tabTemplate: tabTemplates){
			Tab staticTab = staticTabMap.get(tabTemplate.getTabId());
			boolean isNewTab = false;
			if(staticTab == null){
				staticTab = this.createTabFromTabTemplate(uid, tabTemplate);
				isNewTab = true;
			}
			staticTab.setName(tabTemplate.getName());
			staticTab.setDisabledDynamicPanel(tabTemplate.getAreaType() != TabTemplate.TYPE_USE_BOTH_AREA );
			staticTab.setAdjustStaticHeight(tabTemplate.getAreaType() == TabTemplate.TYPE_STATIC_AREA_ADJUST_HEIGHT );
			JSONObject data = new JSONObject();
			data.put("numCol", tabTemplate.getNumberOfColumns());
			data.put("columnsWidth", tabTemplate.getColumnWidth());
			staticTab.setData(data.toString());

			if(isNewTab){
				tabList.add(new TabDetail(
						staticTab,
						tabTemplate.getLayout(),
						this.createPersonalizeGadgetList( uid, tabTemplate ),
						this.createStaticGadgetList( uid, tabTemplate )
					));
			}else{
				boolean isChanged = staticTab.getTemplateTimestamp() != null
						&& tabTemplate.getUpdatedAt().compareTo(
								staticTab.getTemplateTimestamp()) > 0;

				List<Widget> dynamicWidgetList = tabDAO.getDynamicWidgetList( staticTab );
				this.updateWidgetUserPrefs(dynamicWidgetList);
				tabList.add(new TabDetail(
						staticTab,
						tabTemplate.getLayout(),
						dynamicWidgetList,
						this.copyStaticGadgetsUserPrefs(uid, staticTab, tabTemplate),
						isChanged
					));
			}
			staticTab.setTemplateTimestamp(tabTemplate.getUpdatedAt());
			tabTemplateIds.add(tabTemplate.getTabId());
		}
		
		List<String> dynamicTabIdList = createDynamicTabIdList( currentTabList );
		for(Tab tab: currentTabList){
			if("static".equals(tab.getType())){
				if(!tabTemplateIds.contains(tab.getTabId()))
					tabList.add(convertStaticToDynamic(tab, dynamicTabIdList));
			}else{
				List<Widget> dynamicWidgetList = tabDAO.getDynamicWidgetList( tab );
				this.updateWidgetUserPrefs(dynamicWidgetList);
				tabList.add(
						new TabDetail(
							tab,
							null,
							dynamicWidgetList,
							new ArrayList<Widget>()
						));
			}
		}
		if (tabList.size() == 1) {
			tabList.add(createNewDynamicTab(uid));
		}
		
		return tabList;
	}
	
	private void updateWidgetUserPrefs(List<Widget> dynamicWidgetList) {
		for(Widget widget : dynamicWidgetList){
			if(widget.getType().startsWith("g_"))continue;
			if(widget.isMenuUpdatedBoolean()){
				widget.getUserPrefs().clear();
				MenuItem menuItem = widget.getMenuItem();
				widget.setTitle(menuItem.getTitle());
				widget.setHref(menuItem.getHref());
				GadgetInstance gadgetInstance = menuItem.getGadgetInstance();
				for(GadgetInstanceUserpref giup : gadgetInstance.getGadgetInstanceUserPrefs()){
					String name = giup.getId().getName();
					widget.setUserPref(name, giup.getValue());
				}
				widget.setIconUrl(gadgetInstance.getIcon());
				this.widgetDAO.updateWidget(widget);
				
			}
		}
	}

	private List<Widget> copyStaticGadgetsUserPrefs(String uid, Tab tab,
			TabTemplate tabTemplate) {
		List<Widget> widgets = tabDAO.getStaticWidgetList( tab );
		Map<String, Widget> widgetMap = new HashMap<String, Widget>();
		for(Widget widget : widgets)
			widgetMap.put(widget.getWidgetid(), widget);
			
		for(TabTemplateStaticGadget gadget : tabTemplate.getTabTemplateStaticGadgets()){
			GadgetInstance gadgetInst = gadget.getGadgetInstance();
			Widget widget = widgetMap.get(gadget.getContainerId());
			if(widget == null){
				widget = convertStaticGadgetInstance2Widget(uid, tab.getTabId(), gadget);
				this.widgetDAO.addWidget(widget,false);
				widgets.add(widget);
			} else {
				widget.setTitle(gadgetInst.getTitle());
				widget.setHref(gadgetInst.getHref());
				widget.setIconUrl(gadgetInst.getIcon());
				widget.setIgnoreHeader(gadget.isIgnoreHeaderBool());
				widget.setNoBorder(gadget.isNoBorderBool());
			}
			Map<String, UserPref> upMap = widget.getUserPrefs();
			
			for(GadgetInstanceUserpref giup : gadgetInst.getGadgetInstanceUserPrefs()){
				UserPref up = upMap.get(giup.getId().getName());
				if(up == null)
					widget.setUserPref(giup.getId().getName(), giup.getValue());
			}
		}
		return widgets;
	}

	private Widget convertStaticGadgetInstance2Widget(String uid, String tabId, StaticGadget gadget ){
		GadgetInstance gadgetInst = gadget.getGadgetInstance();
		Widget widget = new Widget();
		widget.setTabid( tabId );
		widget.setDeletedate(Long.valueOf(0));
		widget.setWidgetid(gadget.getContainerId());
		widget.setUid( uid );
		widget.setFkDomainId(DomainManager.getContextDomainId());
		//TODO:
		widget.setType(gadgetInst.getType().startsWith("upload_") ? "g_" + gadgetInst.getType() + "/gadget" : gadgetInst.getType() );
		widget.setColumn(Integer.valueOf(0));
		widget.setTitle(gadgetInst.getTitle());
		widget.setHref(gadgetInst.getHref());
		widget.setIsstatic(Integer.valueOf(1));
		
		widget.setIgnoreHeader(gadget.isIgnoreHeaderBool());
		widget.setNoBorder(gadget.isNoBorderBool());
		
		widget.setIconUrl(gadgetInst.getIcon());
		
		return widget;
	}
	
	private List<Widget> createStaticGadgetList(String uid, CommandBar cmdBar) {
		List<Widget> widgetList = new ArrayList<Widget>();
		for(CommandBarStaticGadget gadget : cmdBar.getCommandBarStaticGadgets()){
			GadgetInstance gadgetInst = gadget.getGadgetInstance();;
			
			Widget widget = convertStaticGadgetInstance2Widget(uid, "commandbar", gadget);
			
			for(GadgetInstanceUserpref up : gadgetInst.getGadgetInstanceUserPrefs())
				widget.setUserPref(up.getId().getName(), up.getValue());
			
			widgetList.add( widget );
			//this.widgetDAO.addWidget(widget,false);
			
		}
		return widgetList;
	}


	private List<Widget> createStaticGadgetList(String uid, TabTemplate tabTemplate) {
		List<Widget> widgetList = new ArrayList<Widget>();
		for(TabTemplateStaticGadget gadget : tabTemplate.getTabTemplateStaticGadgets()){
			GadgetInstance gadgetInst = gadget.getGadgetInstance();;
			
			Widget widget = convertStaticGadgetInstance2Widget(uid, tabTemplate.getTabId(), gadget);
			
			for(GadgetInstanceUserpref up : gadgetInst.getGadgetInstanceUserPrefs())
				widget.setUserPref(up.getId().getName(), up.getValue());
			
			widgetList.add(widget);
			this.widgetDAO.addWidget(widget, false);
		}
		return widgetList;
	}
	
	private List<Widget> createPersonalizeGadgetList(String uid,
			TabTemplate tabTemplate,
			Collection<TabTemplatePersonalizeGadget> gadgets) {
		List<Widget> widgetList = new ArrayList<Widget>();
		for (TabTemplatePersonalizeGadget gadget : gadgets) {
			if (widgetDAO.exist(uid, gadget.getWidgetId()))
				continue;

			GadgetInstance gadgetInst = gadget.getFkGadgetInstance();

			Widget widget = new Widget();
			widget.setTabid(tabTemplate.getTabId());
			widget.setDeletedate(Long.valueOf(0));
			widget.setWidgetid(gadget.getWidgetId());
			widget.setUid(uid);
			widget.setFkDomainId(DomainManager.getContextDomainId());
			// TODO:
			widget.setType(gadgetInst.getType().startsWith("upload_") ? "g_"
					+ gadgetInst.getType() + "/gadget" : gadgetInst.getType());
			widget.setColumn(gadget.getColumnNum());
			widget.setTitle(gadgetInst.getTitle());
			widget.setHref(gadgetInst.getHref());

			widget.setIconUrl(gadgetInst.getIcon());

			TabTemplatePersonalizeGadget sibling = tabTemplate
					.getPersonalizeGadget(gadget.getSiblingId());
			if (sibling != null)
				widget.setSiblingid(sibling.getWidgetId());

			for (GadgetInstanceUserpref up : gadgetInst
					.getGadgetInstanceUserPrefs())
				widget.setUserPref(up.getId().getName(), up.getValue());
			widget.setIsstatic(Integer.valueOf(0));

			widget.setMenuItem(MenuItemDAO.newInstance().getByGadgetInstanceId(gadgetInst.getId()));
			this.widgetDAO.addWidget(widget, true);
			widgetList.add(widget);
		}
		return widgetList;
	}

	private List<Widget> createPersonalizeGadgetList(String uid,
			TabTemplate tabTemplate) {
		return createPersonalizeGadgetList(uid, tabTemplate, tabTemplate
				.getTabTemplatePersonalizeGadgets());
	}

	private Tab createTabFromTabTemplate(String uid, TabTemplate tabTemplate) throws JSONException {
		Tab newTab = new Tab(new TABPK(tabTemplate.getFkDomainId(), uid, tabTemplate.getTabId()));
		
		//Delete StaticPanel, tabType=dynamic
		newTab.setType("static");
		newTab.setName(tabTemplate.getName());
		//Delete tab number
		//TODO: Is it placed at last if order is null?
		newTab.setOrder(null);
		JSONObject data = new JSONObject();
		data.put("numCol", tabTemplate.getNumberOfColumns());
		if(tabTemplate.getColumnWidth() != null)
			data.put("columnsWidth", tabTemplate.getColumnWidth());
		newTab.setData(data.toString());
		
		tabDAO.addTab(newTab);
		return newTab;
	}
	
	private TabDetail convertStaticToDynamic( Tab staticTab, List<String> dynamicTabIdList ) {
		// Processing of allocating tab ID again.
		int newTabId = getNextNumber(dynamicTabIdList);
		
		Tab newTab = new Tab(new TABPK(staticTab.getId().getFkDomainId(), staticTab.getUid(), String.valueOf(newTabId)));
		
		newTab.setType("dynamic");
		newTab.setName(staticTab.getName());
		newTab.setData(staticTab.getData());
		newTab.setOrder(null);
		newTab.setData("{}");
		
		tabDAO.addTab(newTab);
		
		List<Widget> dynamicWidgets = tabDAO.getDynamicWidgetList(staticTab.getUid(),staticTab.getTabId() );
		for( Widget widget : dynamicWidgets) {
			widget.setTabid( String.valueOf( newTabId ) );
			widget.setIsstatic( Integer.valueOf( 0 ) );
		}
		
		WidgetDAO widgetDAO = WidgetDAO.newInstance();
		Collection<Widget> staticWidgets = tabDAO.getStaticWidgetList(staticTab.getUid(),staticTab.getTabId() );
		for( Widget widget : staticWidgets) {
			widgetDAO.delete(widget);
		}
				
		tabDAO.deleteTab( staticTab );
		
		return new TabDetail(newTab, null, dynamicWidgets, new ArrayList<Widget>());
	}
	
	private TabDetail createNewDynamicTab(String uid) {
		int newTabId = getNextNumber(new ArrayList());
		Tab newTab = new Tab(new TABPK(DomainManager.getContextDomainId(), uid,
				String.valueOf(newTabId)));
		newTab.setType("dynamic");
		newTab.setName("Home");
		newTab.setData("{}");
		newTab.setOrder(1);
		tabDAO.addTab(newTab);
		return new TabDetail(newTab, null, new ArrayList<Widget>(),
				new ArrayList<Widget>());
	}

	private List<String> createDynamicTabIdList( Collection<Tab> tabList ) {
		List<String> dynamicTabIdList = new ArrayList<String>();
		for(Tab tab : tabList){
			String widgetTabId = tab.getTabId();
			if(!"static".equals( tab.getType().toLowerCase())){
				if(widgetTabId != null)
					dynamicTabIdList.add(widgetTabId);
			}
		}
		
		return dynamicTabIdList;
	}
	
	
	/**
	 * Generate maximum tab id of dynamicTab + 1 
	 * 
	 * @param dynamicTabList
	 * @return
	 * @throws Exception
	 */
	private int getNextNumber(List idList){
		int id = 1;
		
		if(idList.size() == 0){
			idList.add(String.valueOf(id));
			return id;
		}
		Collections.sort(idList, new Comparator(){

			public int compare(Object o1, Object o2) {
				try{
					int i1 = Integer.parseInt(o1.toString());
					int i2 = Integer.parseInt(o2.toString());
					
					return i1 - i2;
				}catch(Exception e){
					return 0;
				}
			}
			
		});
		
		String tabNumberStr = (String)idList.get(idList.size() - 1);
		
		try{
			id = Integer.parseInt(tabNumberStr);
			
		}catch(NumberFormatException e){
			idList.remove(idList.size()-1);
			return getNextNumber(idList);
		}
		
		int newId = id + 1;
		
		idList.add(String.valueOf(newId));
		return newId;
	}
	
	/**
	 * Replace StaticPanel of specified tab
	 */
	private void replaceStaticPanel( String uid,Tab tab,Collection widgets ) throws Exception{
		WidgetDAO widgetDAO = WidgetDAO.newInstance();
		
		Map oldWidgets = new HashMap();
		for( Iterator ite=tabDAO.getStaticWidgetList( tab ).iterator();ite.hasNext();) {
			Widget widget = ( Widget )ite.next();
			
			oldWidgets.put( widget.getWidgetid(),widget );
		}
		
		for( Iterator ite=widgets.iterator();ite.hasNext();) {
			Widget widget = ( Widget )ite.next();
			if( oldWidgets.containsKey( widget.getWidgetid() )) {
				Widget oldWidget = ( Widget )oldWidgets.get( widget.getWidgetid());
				for( Map.Entry<String,UserPref> entry : oldWidget.getUserPrefs().entrySet() )
					widget.setUserPref( entry.getKey(),entry.getValue().getValue() );
			}
			
			widget.setTabid( tab.getTabId());
		}

		widgetDAO.emptyWidgets( uid,tab.getTabId(),1 );
		widgetDAO.getHibernateTemplate().saveOrUpdateAll( widgets );
		widgetDAO.updateUserPrefs( widgets );
	}
	
	public void clearConfigurations(String uid) throws Exception {
		clearConfigurations(uid, null);
	}
	
	public void clearConfigurations( String uid, String tabId ) throws Exception{
		if( uid == null )
			return;
		
		Preference preference = PreferenceDAO.newInstance().select(uid);
		Element prefEl = preference.getElement();
		PreferenceService.removeProperty(prefEl, "freshDays");
		PreferenceService.removeProperty(prefEl, "mergeconfirm");
		PreferenceService.removeProperty(prefEl, "searchOption");
		PreferenceService.removeProperty(prefEl, "theme");
		preference.setElement(prefEl);
		if(tabId == null){
			WidgetDAO.newInstance().deleteWidget( uid );
			TabDAO.newInstance().deleteTab( uid );
		}else{
			WidgetDAO.newInstance().deleteWidget( uid, tabId );
			TabDAO.newInstance().deleteTab( uid, tabId );	
		}
		SessionDAO.newInstance().setForceReload( uid );
		
		log.info("reset user data ["+uid+"]");
	}
	
	public class TabDetail {
		private Tab tab;
		private String layout;
		private List<Widget> staticWidgets;
		private List<Widget> personalWidgets;
		private boolean isChanged;

		public TabDetail(Tab tab, String layout, List<Widget> personalWidgets,
				List<Widget> staticWidgets) {
			this.tab = tab;
			this.layout = layout;
			this.staticWidgets = staticWidgets;
			this.personalWidgets = personalWidgets;
		}
		
		public TabDetail(Tab tab, String layout, List<Widget> personalWidgets,
				List<Widget> staticWidgets, boolean isChanged) {
			this(tab, layout, personalWidgets, staticWidgets);
			this.isChanged = isChanged;
		}

		public Tab getTab() {
			return tab;
		}

		public String getLayout() {
			return layout;
		}

		public List<Widget> getStaticWidgets() {
			return staticWidgets;
		}

		public List<Widget> getPersonalWidgets() {
			return personalWidgets;
		}

		public boolean isChanged() {
			return isChanged;
		}
	}
}
