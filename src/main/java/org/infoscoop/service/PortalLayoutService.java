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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.dao.I18NLocaleDAO;
import org.infoscoop.dao.PortalLayoutDAO;
import org.infoscoop.dao.model.I18nlocale;
import org.infoscoop.dao.model.Portallayout;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.XmlUtil;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class PortalLayoutService {

	private static Log log = LogFactory.getLog(PortalLayoutService.class);
	
	private PortalLayoutDAO portalLayoutDAO;
	
	public PortalLayoutService() {
	}

	public void setPortalLayoutDAO(PortalLayoutDAO portalLayoutDAO) {
		this.portalLayoutDAO = portalLayoutDAO;
	}
	
	public static PortalLayoutService getHandle() {
		return (PortalLayoutService)SpringUtil.getBean("PortalLayoutService");
	}

	/**
	 * @param layoutMap
	 * @throws Exception
	 */
	public synchronized void updatePortalLayout(Map layoutMap) throws Exception {
		for (Iterator it = layoutMap.keySet().iterator(); it.hasNext();) {
			String name = (String) it.next();
			String layout = (String) layoutMap.get(name);
			if (name != null) {
				updatePortalLayout(name, layout);
			}
		}
	}

	/**
	 * @param name
	 * @param layout
	 * @throws Exception
	 */
	public synchronized void updatePortalLayout(String name, String layout)
			throws Exception {
		// fix 28  A check of the injustice value in case of the XML
		if( name.toLowerCase().equals("contentfooter")) {
			try {
				XmlUtil.string2Dom("<contentFooter>"+layout+"</contentFooter>");
			} catch( Exception ex ) {
				log.error("The XML that you specifyed in contentFooter is unjust.");
				
				throw new IllegalArgumentException("The XML that you specifyed in contentFooter is unjust.");
			}
		}
		
		Portallayout entity = this.portalLayoutDAO.selectByName(name);
		entity.setLayout(layout);
		this.portalLayoutDAO.update(entity);
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public String getPortalLayoutJson() throws Exception {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		MakePortalLayoutHandler handler = new MakePortalLayoutHandler();
		parser.parse(new InputSource(new StringReader(getPortalLayout())),
				handler);
		
		return handler.getJSONPString();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	private String getPortalLayout() throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("<portalLayouts>");

		List layoutList = this.portalLayoutDAO.select();
		for(Iterator layoutIt = layoutList.iterator();layoutIt.hasNext();){
			Portallayout portalLayout = (Portallayout)layoutIt.next();
			
			sb.append("<portallayout>");
			sb.append("<name>").append(portalLayout.getId().getName()).append("</name>");
			
			String layout = (portalLayout.getLayout()!=null) ? portalLayout.getLayout(): "" ;
			String layoutData = XmlUtil.escapeXmlEntities(layout);
			
			sb.append("<layout>").append(layoutData).append("</layout>");

			sb.append("<country>").append(portalLayout.getId().getCountry()).append("</country>");
			sb.append("<lang>").append(portalLayout.getId().getLang()).append("</lang>");
			
			
			sb.append("</portallayout>");
		}

		sb.append("</portalLayouts>");
		
		return sb.toString();
	}
	
	public String getPortalLayout(String name, String country, String lang){
		Portallayout layout = this.portalLayoutDAO.getById(name, country, lang);
		if(layout == null){
			log.error("Layout " + name + " is not found.");
			return "";
		}
		return layout.getLayout();
	}

	public List<Portallayout> getPortalLayoutList(){
		return this.portalLayoutDAO.select();
	}
	
	/**
	 * InnerClass
	 */
	private class MakePortalLayoutHandler extends DefaultHandler implements
			LexicalHandler {
		private CharArrayWriter buf = new CharArrayWriter();

		private StringBuffer layoutsArray = new StringBuffer();

		boolean firstLayoutElement = true;

		boolean endLayoutElement = false;

		boolean close = false;

		Stack idStack = new Stack();

		long start = System.currentTimeMillis();

		public void startDocument() throws SAXException {
			layoutsArray.append("{");
		}

		public String getJSONPString() {
			return "ISA_PortalLayout.setPortalLayouts("
					+ layoutsArray.toString() + ");";
		}

		public void endDocument() throws SAXException {
			layoutsArray.append("}");
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			StringBuffer stringbuffer = new StringBuffer();

			buf.reset();
			endLayoutElement = false;
			if (qName.equals("portalLayouts")) {
				idStack.push(qName);
				firstLayoutElement = true;
			} else if (qName.equals("portallayout")) {
				if (!firstLayoutElement) {
					if (!close) {
						stringbuffer.append("}");
					}
					stringbuffer.append(",");
				}

				close = false;
				firstLayoutElement = false;

				// stringbuffer.append("{");
				appendDivision(stringbuffer);
			} else if (qName.equals("name")) {
				
			} else if (qName.equals("layout") || qName.equals("country") || qName.equals("lang")) {
				stringbuffer.append(",").append(qName).append(":");
				appendDivision(stringbuffer);
			}
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			
			buf.write(ch, start, length);
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (qName.equals("portalLayouts")) {
				if (!firstLayoutElement) {
					appendDivision("}");
				}
				idStack.clear();
			} else if (qName.equals("portallayout")) {
				if (endLayoutElement && !close) {
					appendDivision("}");
					close = true;
				}
				endLayoutElement = true;
			} else if (qName.equals("name")) {
				appendDivision(JSONObject.quote(buf.toString()));
				appendDivision(":{name:");
				appendDivision(JSONObject.quote(buf.toString()));
			} else if (qName.equals("layout") || qName.equals("country") || qName.equals("lang")) {
				appendDivision(JSONObject.quote(buf.toString()));
			}
			buf.reset();
		}

		/**
		 * @param string
		 */
		private void appendDivision(String string) {
			appendDivision(new StringBuffer(string));
		}

		/**
		 * @param stringbuffer
		 */
		private void appendDivision(StringBuffer stringbuffer) {
			String peek = "";
			if (!idStack.isEmpty())
				peek = idStack.peek().toString();

			if ("portalLayouts".equals(peek)) {
				layoutsArray.append(stringbuffer);
			}
		}

		/*
		 * (Not Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#endCDATA()
		 */
		public void endCDATA() throws SAXException {
			try {
				buf.write("]]>");
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}

		/*
		 * (Not Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#endDTD()
		 */
		public void endDTD() throws SAXException {
			
		}

		/*
		 * (Not Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#startCDATA()
		 */
		public void startCDATA() throws SAXException {
			try {
				buf.write("<![CDATA[");
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}

		/*
		 * (Not Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
		 */
		public void comment(char[] ch, int start, int length)
				throws SAXException {
			
		}

		/*
		 * (Not Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
		 */
		public void endEntity(String name) throws SAXException {
			
		}

		/*
		 * (Not Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
		 */
		public void startEntity(String name) throws SAXException {
			
		}

		/*
		 * (Not Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String,
		 *      java.lang.String, java.lang.String)
		 */
		public void startDTD(String name, String publicId, String systemId)
				throws SAXException {
			
		}
	}

}
