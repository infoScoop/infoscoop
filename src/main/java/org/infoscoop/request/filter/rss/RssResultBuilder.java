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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class RssResultBuilder {
	protected int pageSize = 200;
	 
	protected int statusCode;
	protected String message = "";
	protected int itemCount = 0;
	protected int latestItemCount = 0;
	
	protected Map channelOtherProperties = new HashMap();
	protected String channelTitle = "";
	protected String channelDate;
	protected String channelFullDate;
	protected String channelDesc;
	protected String channelLink;

	/**
	 * @param pageSize :If the "pageSize" is 0 or a negative number, we return the all.
	 */
	public RssResultBuilder(int pageSize){
		if(pageSize < 0) pageSize = 0;
		this.pageSize = pageSize;
	}
	public RssResultBuilder(){
	}
	/**
	 * {
	 *   title:"",
	 *   link:"",
	 *   description:"",
	 *   rssDate:new Date("2008/02/06 19:00:00+0900"),
	 *   date:"2008/02/06 19:00:00+0900",
	 *   isHot : "false",
	 *   creator : "",
	 *   creatorImg : ""
	 * }
	 * @param jsonStr
	 * @param link
	 * @param fullDate 
	 */
	public abstract void addItem(RssHandler handler, RssItem item);
	
	public abstract List getItems();
	
	public abstract String getResult();
	public abstract String getResult(int pageNumber);

	public void setChannelDate(String date) {
		this.channelDate = date;
	}

	public void setChannelDescription(String desc) {
		this.channelDesc = desc;
	}

	public void setChannelLink(String link) {
		this.channelLink = link;
	}

	public void setChannelTitle(String title) {
		this.channelTitle = title;
	}

	public void setChannelOtherProperties(Map channelOtherProperties) {
		this.channelOtherProperties = channelOtherProperties;
	}

	public void setChannelFullDate(String fullDate) {
		this.channelFullDate = fullDate;
	}

	public void setStatusCode(int status) {
		this.statusCode = status;
	}
	
	public void setMessage(String message){
		this.message = message.replaceAll("\\\"","\\\\\"");
	}
	
	/*
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	*/
	public int getItemCount(){
		return this.itemCount;
	}
	
	public int getLatestItemCount() {
		return this.latestItemCount;
	}
	/*
	public void setLatestItemCount( int latestItemCount ) {
		this.latestItemCount = latestItemCount;
	}
		*/
	public int getPageCount(){
		if(this.pageSize == 0)return 1;
		
		if(this.getItemCount() % this.pageSize == 0)
			return (this.getItemCount() / this.pageSize) ; 
		else
			return (this.getItemCount() / this.pageSize) + 1; 
	}
}
