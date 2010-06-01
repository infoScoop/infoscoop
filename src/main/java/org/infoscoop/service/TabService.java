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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.PreferenceDAO;
import org.infoscoop.dao.SessionDAO;
import org.infoscoop.dao.TabDAO;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.Preference;
import org.infoscoop.dao.model.TABPK;
import org.infoscoop.dao.model.Tab;
import org.infoscoop.dao.model.TabLayout;
import org.infoscoop.dao.model.UserPref;
import org.infoscoop.dao.model.Widget;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
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
	public Object getWidgetsNodeByTabOrder(String uid, String defaultUid, int tabOrder) throws Exception {
		ArrayList tabList = (ArrayList)getWidgetsNode(uid, defaultUid);
		
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
	public Collection getWidgetsNode(String uid, String defaultUid) throws Exception {
		Collection<Object[]> tabList = new ArrayList<Object[]>();
		Map tabLayoutsMap = TabLayoutService.getHandle().getMyTabLayout();
		
		if(uid != null){
			Collection currentTabs = syncPanels(tabLayoutsMap, uid);
			
			for( Iterator ite=currentTabs.iterator();ite.hasNext();) {
				Tab tab = ( Tab )ite.next();
				
				tabList.add( new Object[]{
						tab,
						tabDAO.getDynamicWidgetList( tab ),
						tabDAO.getStaticWidgetList( tab )
					});
			}
		}else{
			// fix #641
			Collection defaultTabLayouts = getDefaultTabsForGuestUser( tabLayoutsMap );
			for( Iterator ite=defaultTabLayouts.iterator();ite.hasNext();) {
				TabObject tabObject = ( TabObject )ite.next();
				
				tabList.add( new Object[]{
						tabObject.getTab(),
						tabObject.getDynamicPanelWidgetList(),
						tabObject.getStaticPanelWidgetList()
					});
			}
		}
		
		return tabList;
	}
	/**
	 * Synchronize tabLayout information of role
	 * @throws FactoryConfigurationError 
	 * @throws Exception 
	 *
	 */
	private Collection syncPanels(Map tabLayoutMap, String uid) throws FactoryConfigurationError, Exception{
		// Delete StaticPanel if the tab is not found in tabLayout information. Change tabType to dynamic.
		Collection currentTabList = TabDAO.newInstance().getTabs(uid);
		
		obsoleteStaticTabToDynamicTab( tabLayoutMap,currentTabList,uid  );
		
		List differenceTabs = getDifferenceTabs( tabLayoutMap,currentTabList,uid );
		for( int i=0;i<differenceTabs.size();i++ ) {
			TabLayout tabLayout = ( TabLayout )differenceTabs.get( i );
			Tab tab = tabLayout.toTab(uid);
			tabDAO.addTab( tab );
			
			Collection staticWidgets = tabLayout.getStaticPanelXmlWidgets( uid );
			
			if( TABID_HOME.equals( tab.getTabId())) {
				Collection commandbarWidgets =
						(( TabLayout)tabLayoutMap.get("commandbar")).getStaticPanelXmlWidgets( uid );
				for( Iterator ite=commandbarWidgets.iterator();ite.hasNext();) {
					Widget widget = ( Widget )ite.next();
					widget.setTabid( tab.getTabId() );
					
					staticWidgets.add( widget );
				}
			}
			
			Map<String, Widget> widgetMap = new HashMap<String, Widget>();
			for( Widget widget: tabLayout.getDynamicPanelXmlWidgets( uid )) {
				widgetMap.put( widget.getWidgetid(),widget );
			}
			
			List<Widget> exists = WidgetDAO.newInstance().getExistsWidgets( uid,new ArrayList( widgetMap.keySet()) );
			for( Widget widget : exists){
				if(tab.getTabId().equals(widget.getTabid())){
					widgetMap.remove( widget.getWidgetid());
				}else{
					JSONArray children = new JSONArray(
							widgetMap.get(widget.getWidgetid()).getUserPrefs().get("children").getValue());
					long now = new Date().getTime();
					for(int j = 0; j < children.length(); j++){
						WidgetDAO.newInstance().deleteWidget(uid, widget.getTabid(), children.getString(j), now);
					}
					WidgetDAO.newInstance().deleteWidget(uid, widget.getTabid(), widget.getWidgetid(), now);
				}
			}
			
			tabDAO.getHibernateTemplate().saveOrUpdateAll( widgetMap.values() );
			WidgetDAO.newInstance().updateUserPrefs( widgetMap.values() );
			
			tabDAO.getHibernateTemplate().saveOrUpdateAll( staticWidgets );
			WidgetDAO.newInstance().updateUserPrefs( staticWidgets );
			
			currentTabList.add( tab );
		}
		
		// Replace to new StaticPanel if it is edited.
		for(Iterator ite = tabLayoutMap.keySet().iterator();ite.hasNext();){
			String tempTabId = (String)ite.next();
			TabLayout layout = (TabLayout)tabLayoutMap.get(tempTabId);
			String tempDefaultUid = layout.getDefaultuid();
			String tempLastModified = layout.getWidgetslastmodified();
			int tempTabNumber = layout.getTabnumber() != null ? layout.getTabnumber().intValue() : 0;
			for(Iterator it = currentTabList.iterator(); it.hasNext();){
				Tab tab = (Tab)it.next();
				String widgetTabId = tab.getTabId();
				String widgetDefaultUid = tab.getDefaultuid();
				String widgetLastModified = tab.getWidgetlastmodified();
				String tabType = tab.getType();
				
				if("static".equals(tabType.toLowerCase()) && widgetTabId.equals(tempTabId)){
					tab.setOrder(Integer.valueOf(tempTabNumber));
					
					if(!tempDefaultUid.equals(widgetDefaultUid) || !tempLastModified.equals(widgetLastModified)){
						// Replace StaticPanel if tabLayout and defaultUid are different.
						tab.setWidgetlastmodified(tempLastModified);
						tab.setDefaultuid(tempDefaultUid);
//						el.setAttribute("tabNumber", tempTabNumber);
						Collection staticPanelWidgets = layout.getStaticPanelXmlWidgets( uid );
						if (widgetTabId.equals(TABID_HOME)) {
							TabLayout commandbarLayout = (TabLayout) tabLayoutMap.get("commandbar");
							
							staticPanelWidgets.addAll( commandbarLayout.getStaticPanelXmlWidgets( uid ) );
						}
						
						tab.setName(layout.getTabName());
						tab.setDisabledDynamicPanelBool(layout.isDisabledDynamicPanel());
						if (layout.isDisabledDynamicPanel()
								&& trashDynamicPanelWidgets(tab)) {
							//notify user of putting all gadgets of the dynamic panel in the trash box.
							tab.setTrashDynamicPanelWidgets(true);
						}
						replaceStaticPanel( uid, tab, staticPanelWidgets );
//						tab.setStaticPanelXml(layout.getStaticPanel());
						break;
					}
				}
			}
		}
		
		return currentTabList;
	}
	
	private boolean trashDynamicPanelWidgets(Tab tab) {
		List<Widget> widgets = tabDAO.getDynamicWidgetList(tab);
		if (widgets.size() == 0)
			return false;
		long now = new Date().getTime();
		for (Widget widget : widgets) {
			widgetDAO.deleteWidget(widget.getUid(), widget.getTabid(), widget
					.getWidgetid(), now);
		}
		return true;
	}
	
	private List createDynamicTabIdList( Collection tabList ) {
		List dynamicTabIdList = new ArrayList();
		for(Iterator it = tabList.iterator(); it.hasNext();){
			Tab tab = (Tab)it.next();
			String widgetTabId = tab.getTabId();
			if(!"static".equals( tab.getType().toLowerCase())){
				if(widgetTabId != null)
					dynamicTabIdList.add(widgetTabId);
			}
		}
		
		return dynamicTabIdList;
	}
	private void obsoleteStaticTabToDynamicTab( Map tabLayoutMap,Collection tabList,String uid ){
		// Create tab id list to allocate tab id again.
		List dynamicTabIdList = createDynamicTabIdList( tabList );
		
		Collection temp = new ArrayList();
		Collection obsolutes = new ArrayList();
		for(Iterator it = tabList.iterator(); it.hasNext();){
			Tab tab = (Tab)it.next();
			
			//Transform static tab to dynamic tab.
			if("static".equals(tab.getType().toLowerCase())){
				if( !tabLayoutMap.containsKey(tab.getTabId())) {
					obsolutes.add( tab );
				} else {
					temp.add( tab );
				}
			}
		}
		
		for(Iterator ite=obsolutes.iterator();ite.hasNext();) {
			Tab tab = convertStaticToDynamic( dynamicTabIdList,(Tab)ite.next() );
			tab.setOrder( new Integer( temp.size()));
			tabDAO.updateTab( tab );
			temp.add( tab );
		}
		
		for(Iterator it = tabList.iterator(); it.hasNext();){
			Tab tab = (Tab)it.next();
			if(!"static".equals(tab.getType().toLowerCase())) {
				tab.setOrder( new Integer( temp.size()));
				tabDAO.updateTab( tab );
				temp.add( tab );
			}
		}
		
		tabList.clear();
		tabList.addAll( temp );
	}
	
	/**
	 * Compare the TabLayout and current tab list and return list of StaticTab that is needed to add.
	 * @param uid UID
	 * @param currentTabs Current tab list
	 * @param tabLayoutMap Map of TabLayout
	 * @return
	 */
	private List getDifferenceTabs( Map tabLayoutMap,Collection currentTabs,String uid ) {
		Set currentStaticTabId = new HashSet();
		for(Iterator it = currentTabs.iterator(); it.hasNext();){
			Tab tab = (Tab)it.next();
			if("static".equals( tab.getType().toLowerCase() ) ){
				currentStaticTabId.add( tab.getTabId() );
			}
		}
		
		List differenceTabLayouts = new ArrayList();
		// Insert the difference of added tabLayout information
		for(Iterator ite = tabLayoutMap.keySet().iterator();ite.hasNext();){
			String tempTabId = (String)ite.next();
			if("commandbar".equals(tempTabId.toLowerCase())) continue;
			
			// Insert the difference here.
			if(!currentStaticTabId.contains( tempTabId ))
				differenceTabLayouts.add( ( TabLayout )tabLayoutMap.get( tempTabId ) );
		}
		
		return differenceTabLayouts;
	}
	
	/**
	 * Obtain default for guest user
	 * @param tabLayoutMap
	 * @return
	 * @throws Exception 
	 */
	private List getDefaultTabsForGuestUser( Map tabLayoutMap ) throws Exception {
		List defaultTabLayouts = new ArrayList();
		List differenceTabs = getDifferenceTabs(tabLayoutMap, new ArrayList(), null);
		
		String tabId;
		TabObject tabObj;
		
		Set dynamicWidgetIdSet = new HashSet();
		for( int i=0;i<differenceTabs.size();i++ ) {
			TabLayout tabLayout = ( TabLayout )differenceTabs.get( i );
			
			tabId = tabLayout.getId().getTabid();
			
			tabObj = new TabObject();

			Collection dynamicWidgets = new ArrayList();
			for( Iterator widgets=tabLayout.getDynamicPanelXmlWidgets( null ).iterator();widgets.hasNext();) {
				Widget widget = ( Widget )widgets.next();
				if( dynamicWidgetIdSet.contains( widget.getWidgetid() ))
					continue;
				
				dynamicWidgets.add( widget );
				dynamicWidgetIdSet.add( widget.getWidgetid() );
			}
			
			Collection staticWidgets = tabLayout.getStaticPanelXmlWidgets( null );
			
			if( TABID_HOME.equals( tabId )) {
				Collection commandbarWidgets =
						(( TabLayout)tabLayoutMap.get("commandbar")).getStaticPanelXmlWidgets( null );
				for( Iterator ite=commandbarWidgets.iterator();ite.hasNext();) {
					Widget widget = ( Widget )ite.next();
					widget.setTabid( tabId );
					
					staticWidgets.add( widget );
				}
			}

			tabObj.addDynamicWidget(dynamicWidgets);
			tabObj.addStaticWidget(staticWidgets);
			tabObj.setTab(tabLayout.toTab(null));
			
			defaultTabLayouts.add( tabObj );
		}
		return defaultTabLayouts;
	}
	
	private Tab convertStaticToDynamic( List dynamicTabIdList,Tab staticTab ) {
		// Processing of allocating tab ID again.
		int newTabId = getNextNumber(dynamicTabIdList);
		
		Tab newTab = new Tab(new TABPK(staticTab.getUid(), String.valueOf(newTabId)));
		
		//Delete StaticPanel, tabType=dynamic
		newTab.setType("dynamic");
		newTab.setName(staticTab.getName());
		newTab.setData(staticTab.getData());
		newTab.setDefaultuid(staticTab.getDefaultuid());
		//Delete tab number
		//TODO: Is it placed at last if order is null?
		newTab.setOrder(null);
		
		tabDAO.addTab(newTab);
		
		Collection<Widget> dynamicWidgets = tabDAO.getDynamicWidgetList(staticTab.getUid(),staticTab.getTabId() );
		for( Widget widget : dynamicWidgets) {
			widget.setTabid( String.valueOf( newTabId ) );
			widget.setIsstatic( new Integer( 0 ) );
		}
		
		WidgetDAO widgetDAO = WidgetDAO.newInstance();
		Collection<Widget> staticWidgets = tabDAO.getStaticWidgetList(staticTab.getUid(),staticTab.getTabId() );
		for( Widget widget : staticWidgets) {
			widgetDAO.delete(widget);
		}
		
		
		//update
//		WidgetDAO.newInstance().addTab(newTab);
		
		// Delete differences here
		// Delete existing (can not update key��
		tabDAO.deleteTab( staticTab );
//		WidgetDAO.newInstance().updateTab( tab );
		
		return newTab;
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
	
	/**
	 * Tab information of guest user
	 * @author nishiumi
	 *
	 */
	private class TabObject{
		Tab tab;
		List staticWidgetList = new ArrayList();
		List dynamicWidgetList = new ArrayList();
		
		public void setTab(Tab tab){
			this.tab = tab;
		}
		public Tab getTab(){
			return tab;
		}
		public void addStaticWidget(Widget widget){
			staticWidgetList.add(widget);
		}
		public void addDynamicWidget(Widget widget){
			dynamicWidgetList.add(widget);
		}
		public void addStaticWidget(Collection widgets){
			staticWidgetList.addAll(widgets);
		}
		public void addDynamicWidget(Collection widgets){
			dynamicWidgetList.addAll(widgets);
		}
		public List getStaticPanelWidgetList() {
			return staticWidgetList;
		}
		public List getDynamicPanelWidgetList() {
			return dynamicWidgetList;
		}
		
	}
	
	public void clearConfigurations( String uid, Integer tabId ) throws Exception{
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
}
