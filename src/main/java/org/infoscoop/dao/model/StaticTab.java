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
 */package org.infoscoop.dao.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.infoscoop.dao.model.base.BaseStaticTab;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class StaticTab extends BaseStaticTab {
	private static final long serialVersionUID = 1L;

	public static final String COMMANDBAR_TAB_ID = "commandbar";
	public static final String PORTALHEADER_TAB_ID = "header";
	public static final String TABID_HOME = "0";
	public static final Integer TABNUMBER_HOME = new Integer(0);
	
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
	@Override
	public void setTabdesc(String tabDesc) {
		super.setTabdesc(tabDesc != null? tabDesc: "");
	}
}