package org.infoscoop.dao.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.infoscoop.dao.model.base.BaseStaticTab;




public class StaticTab extends BaseStaticTab {
	private static final long serialVersionUID = 1L;

	public static final String COMMANDBAR_TAB_ID = "commandbar";
	public static final String PORTALHEADER_TAB_ID = "header";

/*[CONSTRUCTOR MARKER BEGIN]*/
	public StaticTab () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public StaticTab (java.lang.String tabId) {
		super(tabId);
	}
	
	public List<String> getTabAdminUidList (){
		List<String> result = new ArrayList<String>();
		Set<TabAdmin> tabAdminList = getTabAdmin();
		for(Iterator<TabAdmin> ite=tabAdminList.iterator();ite.hasNext();){
			TabAdmin ta = ite.next();
			result.add(ta.getId().getUid());
		}
		return result;
	}

/*[CONSTRUCTOR MARKER END]*/
}