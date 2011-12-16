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
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

public class RssJsonResultBuilder extends RssResultBuilder{
	
	private StringBuffer itemsJson = new StringBuffer();
	private List items = new ArrayList(200);
	
	public RssJsonResultBuilder(){
		super();
	}
	
	public RssJsonResultBuilder(int pageSize){
		super(pageSize);
	}

	public void addItem(RssHandler handler, RssItem rssItem) {
		/*
		if(itemsJson.length() > 0)itemsJson.append(",");
		itemsJson.append(jsonStr);
		
		*/
		if(handler.isFresh(rssItem.date))latestItemCount++;
		itemCount++;
		items.add(rssItem);
	}
	
	public void addItem(long freshDays, RssItem rssItem) {
		/*
		if(itemsJson.length() > 0)itemsJson.append(",");
		itemsJson.append(jsonStr);
		
		*/
		if(RssHandler.isFresh(freshDays, rssItem.date))latestItemCount++;
		itemCount++;
		items.add(rssItem);
	}
	
	public List getItems() {
		return this.items;
	}

	public String getResult(){
		return getResult(0);
	}

	public String getResult(int pageNumber) {
		
		StringBuffer rssJson = new StringBuffer();
		rssJson.append("{statusCode:").append(statusCode).append(",");
		rssJson.append("message:\"").append(message).append("\",");
		rssJson.append("itemCount:").append(this.getItemCount()).append(",");
		rssJson.append("latestItemCount:").append(latestItemCount).append(",");
		if(getPageCount() > 1){
			rssJson.append("pageCount:").append(getPageCount()).append(",");
		}
		rssJson.append("title : \"").append(channelTitle).append("\",");
		if( channelLink != null )
			rssJson.append("link : \"").append(channelLink).append("\",");
		if( channelDesc != null )
			rssJson.append("description : \"").append(channelDesc).append("\",");
		if (channelFullDate != null)
			rssJson.append("rssDate : new Date(").append(channelFullDate).append("),");		// check the FeedJsonFilter.json#postProcess
		rssJson.append("date : \"").append(channelDate).append("\"");
		
		if( channelOtherProperties != null ) {
			Iterator keys = channelOtherProperties.keySet().iterator();
			while (keys.hasNext()) {
				String name = (String) keys.next();
				String value = (String) channelOtherProperties
				.get(name);
				rssJson.append(",").append(JSONObject.quote(name)).append(
						" : \"").append(value).append("\"");
			}
		}
		rssJson.append(",items : [");
		int offset = (pageSize > 0) ? pageNumber * pageSize : 0;
		List items = getItems();
		for(int i = offset; ( pageSize <= 0 || i < offset + pageSize) && i < items.size(); i++){
			if(i > offset)rssJson.append(",");
			RssItem rssItem = (RssItem)items.get(i);
			rssJson.append(rssItem.toJSONString());
		}
		rssJson.append(itemsJson);
		rssJson.append("]");
		rssJson.append("}");
		return rssJson.toString();
	}

}
