package org.infoscoop.request.filter.rss;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The object that shows an item element of RSS.
 * 
 * @author hr-endoh
 */
public class RssItem {
	String title;
	String link;
	String description;
	Date date;
	String displayDate;
	String creator;
	String creatorImg;
	Map otherProperties;
	List<String> categoryList;
	List rssUrlIndexList = new ArrayList(2);

	public RssItem(
			String title, 
			String link, 
			String description, 
			Date date, 
			String displayDate,
			String creator, 
			String creatorImg,
			List<String> categoryList,
			Map otherProperties) {
		super();
		this.title = title;
		this.link = link;
		this.description = description;
		this.date = date;
		this.displayDate = displayDate;
		this.creator = creator;
		this.creatorImg = creatorImg;
		this.categoryList = categoryList;
		this.otherProperties = otherProperties;
	}
	
	public void addRssUrlIndex(Integer index){
		rssUrlIndexList.add(index);
	}
	
	public String toJSONString(){

		StringBuffer itemJSON = new StringBuffer();
		
		itemJSON.append("{title : \"").append(title).append("\",");
		itemJSON.append("link : \"").append(link).append("\",");
		itemJSON.append("description : \"").append(description).append("\",");
		
		String rssDate = (this.date != null) ? "new Date(" + RssHandler.getFullDate(this.date) + ")" : "null";
		itemJSON.append("date : \"").append(displayDate).append("\",");
		itemJSON.append("dateLong : ").append((this.date != null) ? Long.toString(this.date.getTime()) : "0").append(",");
		
		itemJSON.append("creator : \"").append(creator).append("\",");
		itemJSON.append("creatorImg : \"").append(creatorImg).append("\"");
		
		if (categoryList != null && categoryList.size() > 0) {
			itemJSON.append(", category : ").append(
					new JSONArray(categoryList).toString());
		}
		
		Set props = otherProperties.entrySet();
		for(Iterator it = props.iterator(); it.hasNext();){
			Map.Entry property = (Map.Entry)it.next();
			String name = (String) property.getKey();
			String value = (String) property.getValue();
			itemJSON.append(",").append(JSONObject.quote(name)).append(" : \"")
					.append(value).append("\"");
		}

		if(rssUrlIndexList.size() > 0){
			itemJSON.append(", rssUrlIndex : ").append(rssUrlIndexList.toString()).append("");
		}
		itemJSON.append("}");
		return itemJSON.toString();
	}

	public boolean equals(Object obj) {
		if(obj == null)return false;
		if(obj instanceof RssItem){
			RssItem item = (RssItem)obj;
			if(this.link != null){
				return this.link.equals(item.link);
			}else{
				return item.link == null;
			}
		}else{
			return false;
		}
	}

	public int hashCode() {
		if(this.link != null){
			return this.link.hashCode();
		}else{
			return 0;
		}
	}
	
	
}
