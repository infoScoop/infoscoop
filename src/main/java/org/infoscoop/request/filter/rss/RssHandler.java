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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.util.DateUtility;
import org.infoscoop.util.XmlUtil;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class RssHandler extends DefaultHandler implements LexicalHandler {
	protected static final String MSD_NAMESPACE = "http://www.beacon-it.co.jp/namespaces/msd";
	protected static final String DUBLIN_CORE_NAMESPACE = "http://purl.org/dc/elements/1.1/";
	protected static final String CONTENT_MODULE_NAMESPACE = "http://purl.org/rss/1.0/modules/content/";

	private static Log log = LogFactory.getLog(RssHandler.class);
	
	private DateFormat formatGMT = DateUtility.newGMTDateFormat();
	private DateFormat format1 = DateUtility.newW3CDFWithoutSecond();
	private DateFormat format2 = DateUtility.newW3CDFWithoutT();
	private DateFormat imap4Date = DateUtility.newImap4DateFormat();
		
	CharArrayWriter charBuf = new CharArrayWriter();
	Stack qNameList = new Stack();

	RssResultBuilder resultBuilder;
	
	int currentRssIndex = -1; 
	
	/**
	 * Handling with RSS of Plagger. The first date element is loaded if the both pubDate and dc:date in the item is included.
	 */
	String enableDateElement = null;
	
	String dateTimeFormat = "yyyy/MM/dd";

	long freshTime = 0;

	//boolean isFirst = true;

	boolean isW3CFormat = false;

	boolean isFormat1 = false;
	
	boolean isIMAP4Date = false;

	//boolean includingHoliday = false;

	
	Map channelOtherProperties = new HashMap();

	String title = "";

	String link = "";

	String description = "";

	Date rssDate = null;
	String displayDate = "";

	String creator = "";

	String creatorImg = "";
	
	List<String> categoryList = new ArrayList<String>();

	Map otherProperties = new HashMap();
	
	boolean isUnknownTag = false;
	
	int itemCount = 0;//For a logic to limit the number and count the number of item.
	//int latestItemCount = 0;
	
	int maxCount;
	//boolean uniqueLink = false;
	///Set linkSet = new HashSet(200);
	SimpleDateFormat formatter = null;
	
	private String titleFilter;
	private String creatorFilter;
	private String categoryFilter;
	
	private String planeTitle;
	private String planeCreator;
	
	void initVariables() {
		title = "";
		link = "";
		description = "";
		rssDate = null;
		displayDate = "";
		creator = "";
		creatorImg = "";
		categoryList = new ArrayList<String>();
		otherProperties = new HashMap();
		
		planeTitle = "";
		planeCreator = "";
	}

	public RssHandler(RssResultBuilder resultBuilder, String dateTimeFormat,
			String freshDatetime, int maxCount, String titleFilter,
			String creatorFilter, String categoryFilter) {
		this.resultBuilder = resultBuilder;
		if (dateTimeFormat != null && dateTimeFormat.length() > 0) {
			this.dateTimeFormat = dateTimeFormat;
		}
		
		try {
			formatter = new SimpleDateFormat(this.dateTimeFormat, Locale.ENGLISH);
		} catch( Exception ex ) {
			formatter = new SimpleDateFormat("yyyy/MM/dd");
		}
		
		if( freshDatetime != null ) {
			try {
				this.freshTime = Long.parseLong( freshDatetime.trim());
			} catch( NumberFormatException ex ) {
				log.error("", ex);
			}
		}
		
		if ( this.freshTime <= 0 )
			this.freshTime = new Date().getTime();
		
		this.maxCount = maxCount;
		//this.uniqueLink = uniqueLink;
		
		this.titleFilter = titleFilter;
		this.creatorFilter = creatorFilter;
		this.categoryFilter = categoryFilter;
	}
	
	public RssHandler(RssResultBuilder resultBuilder, String dateTimeFormat,
			String freshDatetime, int maxCount, String titleFilter,
			String creatorFilter, String categoryFilter, int rssUrlIndex) {
		this(resultBuilder, dateTimeFormat, freshDatetime, maxCount,
				titleFilter, creatorFilter, categoryFilter);
		this.currentRssIndex = rssUrlIndex;
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (itemCount >= maxCount)
			return;
//		if (contentsLevel > 0)
			charBuf.write(ch, start, length);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// buf.reset();
		if (itemCount >= maxCount)
			return;
//		if (contentsLevel > 1) {
//			writeStartElement(uri, localName, qName, attributes, charBuf);
//		}
		if (!qNameList.isEmpty()) {
			String parentTag = (String) qNameList.peek();
			if (parentTag.equals("item")
					|| (parentTag.equals("channel"))) {
//				if (uri.startsWith(MSD_NAMESPACE))
//					contentsLevel = 2;
//				else
//					contentsLevel = 1;
//				contentsLevel = 2;
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
		if(parentTag.equals("item")){
			if (qName.equals("title") || qName.equals("link")
					|| qName.equals("description")
					|| qName.equals("pubDate")
					|| qName.equals("category")
					|| isContentEncoded(uri, qName, localName)
					|| isDcCreator(uri, qName, localName)
					|| isDcDate(uri, qName, localName))
				return true;
		} else if (parentTag.equals("channel")) {
			if (qName.equals("title") || qName.equals("link")
					|| qName.equals("description")
					|| qName.equals("pubDate")
					|| qName.equals("item")
					|| qName.equals("items")
					|| isDcDate(uri, qName, localName))
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
		if(isUnknownTag)
			writeEndElement(uri, localName, qName, charBuf);
		if (!qNameList.isEmpty()) {
			String parentTag = (String) qNameList.peek();
			if (parentTag.equals("item")) {
				isUnknownTag = false;
//				contentsLevel = 0;
				/*
				 * String value = buf.toString().replace('"', '\'').replace(
				 * '\n', ' '); value = value.replaceAll("[\r\n]+", "\\n");
				 * buf.reset();
				 */
				String planeValue = charBuf.toString();
				String value = getJSONString();
				if (qName.equals("title")) {
					if (value.length() > 0) {
						title = value;
						planeTitle = planeValue;
					}
				} else if (qName.equals("link")) {
					link = value;
				} else if (qName.equals("description")) {
		        	if(!"".equals(value.trim()))
		        		description = value;
				} else if ( isContentEncoded( uri,qName,localName ) ){
		        	if(!"".equals(value.trim()))
		        		description = value;
				}else if ( (enableDateElement == null  &&
						( isDcDate( uri,qName,localName ) || qName.equals("pubDate") ) ) 
						|| localName.equals(enableDateElement) ) {
					enableDateElement = localName;
					//Date tmpDate = parseDate(value);
					rssDate = parseDate(planeValue);
					if( rssDate != null )
						displayDate = formatRssDate(rssDate);
					
					if(displayDate == null || "".equals(displayDate))
						displayDate = value;
					
				} else if ( isDcCreator( uri,qName,localName )) {
					int index = value.indexOf("<img");
					if (index >= 0) {
						int lastIndex = value.indexOf(">", index);
						creatorImg = value.substring(index, lastIndex + 1);
						creator = value.substring(lastIndex + 1);
					} else {
						creator = value;
					}
					
					index = planeValue.indexOf("<img");
					if(index >= 0 ) {
						int lastIndex = planeValue.indexOf(">", index);
						planeCreator = planeValue.substring(lastIndex + 1);
					} else {
						planeCreator = planeValue;
					}
				} else if (qName.equals("category")) {
					categoryList.add(planeValue);
				} else{
				//} else if (uri.startsWith(MSD_NAMESPACE)) {
					int index = uri.lastIndexOf('/');
					otherProperties.put(uri.substring(index + 1) + "_"
							+ localName, value);
				}
			} else if (parentTag.equals("channel")) { // for RSS
				isUnknownTag = false;
//				contentsLevel = 0;
				/*
				 * String value = buf.toString().replace('"', '\'').replace(
				 * '\n', ' '); value = value.replaceAll("[\r\n]+", "\\n");
				 * buf.reset();
				 */
				String planeValue = charBuf.toString();
				String value = getJSONString();
				if (qName.equals("title")) {
					if (value.length() > 0) {
						this.resultBuilder.setChannelTitle(value);
					}
				} else if (qName.equals("link")) {
					this.resultBuilder.setChannelLink(value);
				} else if ( isDcDate( uri,qName,localName )
						|| qName.equals("pubDate") ) {
					//
					Date date = parseDate(planeValue);
					String channelRssDate = getFullDate(date);
					this.resultBuilder.setChannelFullDate(channelRssDate);
					String channelDate = formatRssDate(date);
					if (channelDate == null)
						channelDate = value;
					this.resultBuilder.setChannelDate(channelDate);
				} else if (qName.equals("description")) {
					if (value.trim().length() > 0) {
						this.resultBuilder.setChannelDescription(value);
					}
				} else if (qName.equals("items")) {
				} else if (qName.equals("item")) {
				} else {
				//} else if (uri.startsWith(MSD_NAMESPACE)) {
					int index = uri.lastIndexOf('/');
					channelOtherProperties.put(uri.substring(index + 1)
							+ "_" + localName, value);
				}
			}
		}
		if (qName.equals("channel")){
			this.resultBuilder.setChannelOtherProperties(channelOtherProperties);
		}
		if (qName.equals("item")) {
			if (matchCreator(planeCreator) && matchCategory(categoryList)
					&& matchTitle(planeTitle)) {
				RssItem rssItem = new RssItem(title, link, description,
						rssDate, displayDate, creator, creatorImg,
						categoryList, otherProperties);
				this.resultBuilder.addItem(this, rssItem);
				itemCount++;
			}
			
			initVariables();
		}
//		if (contentsLevel > 1) {
//			writeEndElement(uri, localName, qName, charBuf);
//		}
		// buf.reset();
	}

	public void comment(char[] ch, int start, int length)
			throws SAXException {
		if (itemCount >= maxCount)
			return;
//		if (contentsLevel > 1) {
			try {
				charBuf.write("<!--");
				charBuf.write(ch, start, length);
				charBuf.write("-->");
			} catch (IOException e) {
				throw new SAXException(e);
			}
//		}
	}

	public void startCDATA() throws SAXException {
		if (itemCount >= maxCount)
			return;
//		if (contentsLevel > 1) {
		if (isUnknownTag) {
			try {
				charBuf.write("<![CDATA[");
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}
//		}
	}

	public void endCDATA() throws SAXException {
		if (itemCount >= maxCount)
			return;
//		if (contentsLevel > 1) {
		if (isUnknownTag) {
			try {
				charBuf.write("]]>");
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}
//		}
	}

	public void endDTD() throws SAXException {
	}

	public void endEntity(String name) throws SAXException {
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
	}

	public void startEntity(String name) throws SAXException {
	}

	String getJSONString() {
		String value = JSONObject.quote(charBuf.toString());
		charBuf.reset();
		value = value.substring(1, value.length() - 1);
		return value;
	}

	void writeStartElement(String uri, String localName, String qName,
			Attributes attributes, Writer writer) throws SAXException {
		//System.out.println("writeStartElement");
		try {
			writer.write("<" + localName);
			for (int i = 0; i < attributes.getLength(); i++) {
				String qname = attributes.getQName(i);
				String value = attributes.getValue(i);
				writer.write(" " + qname + "=\""
						+ XmlUtil.escapeXmlEntities(value) + "\"");
			}
			writer.write(">");
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	void writeEndElement(String uri, String localName, String qName,
			Writer writer) throws SAXException {
		try {
			writer.write("</" + localName + ">");
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	Date parseDate(String dateStr) {
		dateStr = dateStr.trim();
		if ( "".equals(dateStr) ) {
			return null;
		}
		
		try {
			if (isW3CFormat) {
				return DateUtility.parseW3CDTFDate(dateStr);
			} else if (isFormat1) {
				return format1.parse(dateStr);
			} else if (isIMAP4Date){
				return imap4Date.parse(dateStr);
			} else {
				return formatGMT.parse(dateStr);
			} 
		} catch (ParseException e) {
			Date date = DateUtility.parseW3CDTFDate(dateStr);
			if (date != null) {
				isW3CFormat = true;
				return date;
			}
			try {
				date = format1.parse(dateStr);
				isFormat1 = true;
				return date;
			} catch (ParseException e3) {
				try{
					date = imap4Date.parse(dateStr);
					isIMAP4Date = true;
					return date;
				}catch(ParseException e4){
					try {
						date = checkDateFormat(dateStr);
						return date;
					} catch (ParseException e5) {
						return null;
					}
				}
			}
		}

	}

	Date checkDateFormat(String dateStr) throws ParseException {
		Pattern pattern = Pattern.compile("(.*[+|-][0-9]{2}):00");
		Matcher m = pattern.matcher(dateStr);
		if (m.matches()) {
			dateStr = m.replaceAll("$100");
			return format2.parse(dateStr);
		}
		return null;
	}

	String getW3CDTFDate(String date) {
		String str = null;
		try {
			str = date.substring(0, date.length() - 3)
					+ date.substring(date.length() - 2);
		} catch (IllegalArgumentException iae) {
			str = date;
		}
		return str;
	}

	/**
	 * Return the date whose form is "yyyy/MM/dd HH:mm:ss" -> obsolute
	 * javascript Date :An argument form of the object instance.
	 * Return the date whose form is "yyyy,MM,dd,HH,mm,ss".
	 * 
	 * @param date
	 * @return
	 */
	public static String getFullDate(Date date) {
		if (date == null) {
			return null;
		}
		
		try {
//			return formatFullDate.format(date);
			return ( date.getYear()+1900 )+","+date.getMonth()+","+date.getDate()+","+
			date.getHours()+","+date.getMinutes()+","+date.getSeconds();
		} catch (IllegalArgumentException e1) {
			return null;
		}
	}

	/**
	 * Get the character string on a date for indication
	 * @param date
	 * @return
	 */
	String formatRssDate(Date date) {
		if (date == null) {
			return null;
		}
		return formatter.format(date);
	}
	
	boolean isFresh( Date rssDatetime ) {
		
		return isFresh(this.freshTime, rssDatetime);
	}
	
	boolean matchCreator(String creator) {
		return RssRefineUtil.matchCreator(creator, creatorFilter);
	}
	
	boolean matchTitle(String title) {
		return RssRefineUtil.matchTitle(title, titleFilter);
	}
	
	boolean matchCategory(List<String> categoryList) {
		return RssRefineUtil.matchCategory(categoryList, categoryFilter);
	}

	public static boolean isFresh(long freshTime, Date rssDatetime) {
		if( rssDatetime == null || freshTime == 0)
			return false;
		return rssDatetime.getTime() > freshTime;
	}
	
	private static boolean isContentEncoded( String uri,String qName,String localName ) {
		return ( ( uri.startsWith( CONTENT_MODULE_NAMESPACE )&& localName.equals("encoded") )||
				qName.equals("content:encoded") );
	}
	private static boolean isDcCreator( String uri,String qName,String localName ) {
		return ( ( uri.startsWith( DUBLIN_CORE_NAMESPACE )&& localName.equals("creator") )||
				qName.equals("dc:creator") );
	}
	private static boolean isDcDate( String uri,String qName,String localName ) {
		return ( ( uri.startsWith( DUBLIN_CORE_NAMESPACE )&& localName.equals("date") )||
				qName.equals("dc:date") );
	}

	
}
