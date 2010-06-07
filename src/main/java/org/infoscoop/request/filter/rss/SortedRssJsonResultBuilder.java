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

package org.infoscoop.request.filter.rss;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SortedRssJsonResultBuilder extends RssJsonResultBuilder {

	public SortedRssJsonResultBuilder(int pageSize){
		super(pageSize);
	}
	
	private Map itemLinks = new HashMap();
	private Map items = new TreeMap(new Comparator(){

		public int compare(Object o1, Object o2) {
			if(!(o1 instanceof RssItem) || !(o2 instanceof RssItem))return 0;
			RssItem item1 = (RssItem)o1;
			RssItem item2 = (RssItem)o2;
			
			int r1 = 0;
			
			if(!( item2.date == null || item1.date == null) ){
				r1 = item2.date.compareTo(item1.date);
			} else if( item1.date != item2.date ) {
				r1 = ( item2.date != null )? 1:-1;
			}
			
			if( r1 != 0 )
				return r1;
			
			if(item2.link != null){
				r1 = item2.link.compareToIgnoreCase( item1.link );
			}else if (item1.link != null){
				r1 = -(item1.link.compareTo(item2.link));
			}
			if(r1 != 0)
				return r1;
			
			if (item2.title != null) {
				r1 = item2.title.compareToIgnoreCase(item1.title);
			} else if (item1.title != null) {
				r1 = -(item1.title.compareTo(item2.title));
			}
			return r1;
		}});
	
	public void addItem(RssHandler handler, RssItem rssItem) {
		int currentRssIndex = handler.currentRssIndex;
		if (rssItem.link == null || rssItem.link.equals("")
				|| !itemLinks.containsKey(rssItem.link)) {
			Object oldItem = items.put(rssItem, rssItem);
			itemLinks.put(rssItem.link, rssItem);
			if(oldItem == null){
				if(handler.isFresh(rssItem.date))latestItemCount++;
				itemCount++;
			}
		}else{
			rssItem = (RssItem)itemLinks.get(rssItem.link);
		}
		if(rssItem != null && currentRssIndex >= 0) rssItem.addRssUrlIndex(new Integer(currentRssIndex));
		
	}

	public void addItem(long freshDays, RssItem rssItem) {
		if (rssItem.link == null || rssItem.link.equals("")
				|| !itemLinks.containsKey(rssItem.link)){
			Object oldItem = items.put(rssItem, rssItem);
			itemLinks.put(rssItem.link, rssItem);
			if(oldItem == null){
				if(RssHandler.isFresh(freshDays, rssItem.date))latestItemCount++;
				itemCount++;
			}
		}else{
			rssItem = (RssItem)itemLinks.get(rssItem.link);
		}
		
	}
	public List getItems(){
		return new ArrayList(items.values());
	}
	
	public int getItemCount(){
		return this.items.size();
	}
	
}
