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

import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class AtomHandler extends RssHandler {

	
	public AtomHandler(RssResultBuilder resultBuilder, String dateTimeFormat,
			String latestDatetime, int maxCount, String titleFilter,
			String creatorFilter, String categoryFilter) {
		super(resultBuilder, dateTimeFormat, latestDatetime, maxCount,
				titleFilter, creatorFilter, categoryFilter);
	}

	public AtomHandler(RssResultBuilder resultBuilder, String dateTimeFormat,
			String latestDatetime, int maxCount, String titleFilter,
			String creatorFilter, String categoryFilter, int rssUrlIndex) {
		super(resultBuilder, dateTimeFormat, latestDatetime, maxCount,
				titleFilter, creatorFilter, categoryFilter, rssUrlIndex);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (itemCount >= maxCount)
			return;
		// buf.reset();
		
		//if (contentsLevel > 1) {
		//	writeStartElement(uri, localName, qName, attributes, charBuf);
		//}
		if (!qNameList.isEmpty()) {
			String parentTag = (String) qNameList.peek();
			if (parentTag.equals("feed")) {
				if (qName.equals("link")) {
					if("text/html".equals(attributes.getValue("type")))
						resultBuilder.setChannelLink(attributes.getValue("href"));
				}
			} else if(parentTag.equals("entry")){
				String href = attributes.getValue("href");
				if (qName.equals("link") && href != null) {
					String rel = attributes.getValue("rel");
					if (rel == null || rel.equals("alternate"))
						link = href;
					String type = attributes.getValue("type");
					if (rel != null) {
						otherProperties.put("link_" + rel + "_" + type , href);
					}
				} else if (qName.equals("category")) {
					String term = attributes.getValue("term");
					if (term != null)
						categoryList.add(term);
				}
			}
			if (parentTag.equals("entry")
					|| (parentTag.equals("feed") && !qName.equals("entry"))
					|| parentTag.equals("author")) {
				//if (uri.startsWith(MSD_NAMESPACE))
				//	contentsLevel = 2;
				//else
				//	contentsLevel = 1;
				isUnknownTag = !isKnownTag(parentTag, uri, qName, localName);
				charBuf.reset();
			}
		}
		if(isUnknownTag)
			writeStartElement(uri, localName, qName, attributes, charBuf);
		qNameList.push(qName);
	}
	
	private boolean isKnownTag(String parentTag, String uri, String qName,
			String localName) {
		if (parentTag.equals("entry")) {
			if (qName.equals("title") || qName.equals("summary")
					|| qName.equals("content") || qName.equals("updated")
					|| qName.equals("modified") || qName.equals("author")
					|| qName.equals("link") || qName.equals("category"))
				return true;
		} else if (parentTag.equals("feed")) {
			if (qName.equals("title") || qName.equals("updated")
					|| qName.equals("modified") || qName.equals("tagline")
					|| qName.equals("entry") || qName.equals("link"))
				return true;
		} else if (parentTag.equals("author")) {
			if (qName.equals("name"))
				return true;
		}
		return false;
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (itemCount >= maxCount)
			return;
		if (!qNameList.isEmpty()) {
			qNameList.pop();
		}
		if (isUnknownTag)
			writeEndElement(uri, localName, qName, charBuf);
		if (!qNameList.isEmpty()) {
			String parentTag = (String) qNameList.peek();
			if (parentTag.equals("entry")) {
				isUnknownTag = false;
				//contentsLevel = 0;
				String planeValue = charBuf.toString();
				String value = getJSONString();
				if (qName.equals("title")) {
					if (value.length() > 0) {
						title = value;
					}
				} else if (qName.equals("summary")) {
		        	if(!"".equals(value.trim()) && "".equals( description ))
		        		description = value;
				} else if (qName.equals("content")) {
		        	if(!"".equals(value.trim()))
		        		description = value;
				} else if (qName.equals("updated") || qName.equals("modified")) {
					rssDate = parseDate(planeValue);
					if( rssDate != null )
						displayDate = formatRssDate(rssDate);

					if(displayDate == null || "".equals(displayDate))
						displayDate = value;
				} else if (qName.equals("link")) {
				} else if (qName.equals("author")) {
				} else {
//				} else if (uri.startsWith(MSD_NAMESPACE)) {
					int index = uri.lastIndexOf('/');
					otherProperties.put(uri.substring(index + 1) + "_"
							+ localName, value);
				}
			} else if (parentTag.equals("author") && qNameList.size() > 2) {
				isUnknownTag = false;
				//contentsLevel = 0;
				String value = getJSONString();
				if(qName.equals("name")){
					creator = value;
				}
			} else if (parentTag.equals("feed")) {
				isUnknownTag = false;
				//contentsLevel = 0;
				String planeValue = charBuf.toString();
				String value = getJSONString();
				if (qName.equals("title")) {
					if (value.length() > 0) {
						resultBuilder.setChannelTitle(value);
					}
				} else if (qName.equals("updated") || qName.equals("modified")) {
					Date date = parseDate(planeValue);
					resultBuilder.setChannelFullDate(getFullDate(date));
					String channelDate = formatRssDate(date);
					if (channelDate == null)
						channelDate = value;
					resultBuilder.setChannelDate(channelDate);
				} else if (qName.equals("tagline")) {
					if (value.trim().length() > 0) {
						resultBuilder.setChannelDescription(value);
					}
				} else if (qName.equals("link")) {
				} else if (!qName.equals("entry")) {
//				} else if (uri.startsWith(MSD_NAMESPACE)) {
					int index = uri.lastIndexOf('/');
					channelOtherProperties.put(uri.substring(index + 1)
							+ "_" + localName, value);
				}
			}
		}
		if (qName.equals("entry")) {
			if (matchCreator(creator) && matchCategory(categoryList)
					&& matchTitle(title)) {
				RssItem rssItem = new RssItem(title, link, description,
						rssDate, displayDate, creator, creatorImg,
						categoryList, otherProperties);
				this.resultBuilder.addItem(this, rssItem);
				itemCount++;
			}
			initVariables();
		}else if(qName.equals("feed")){
			this.resultBuilder.setChannelOtherProperties(channelOtherProperties);
		}
		//if (contentsLevel > 1) {
		//	writeEndElement(uri, localName, qName, charBuf);
		//}
	}
}
