package org.infoscoop.request.filter.ical;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.util.NoOpEntityResolver;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ICalendarUtil {
	private static Log log = LogFactory.getLog(ICalendarUtil.class);
	
	public static String RDF_CALENDAR_NAMESPACE = "http://www.w3.org/2002/12/cal/ical";
	
	public static Reader convertRdf2Ics(InputStream is) throws SAXException,
			IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating( false );
		factory.setNamespaceAware(true);
		XMLReader reader = null;
		try {
			reader = factory.newSAXParser().getXMLReader();
			reader.setEntityResolver(NoOpEntityResolver.getInstance());
		} catch (ParserConfigurationException e) {
			log.error("", e);
		}

		Rdf2IcsHandler xmlHandler = new Rdf2IcsHandler();

		reader.setContentHandler(xmlHandler);
		reader.parse(new InputSource(is));
		return new StringReader(xmlHandler.getResult());
	}

	public static class Rdf2IcsHandler extends DefaultHandler {
		private CharArrayWriter buf = new CharArrayWriter();

		private StringWriter xml = new StringWriter();

		private List qNameList = new ArrayList();

		boolean isMixed = true;

		boolean isFirst = true;

		boolean isRoot = true;

		String dtstart = "";

		String startValue = "";

		String dtend = "";

		String summary = "";

		String location = "";

		String description = "";

		String url = "";

		String urlAttr = null;

		StringBuffer rrule = new StringBuffer();

		String parentTag = null;

		public Rdf2IcsHandler() {

		}

		public String getResult() {
			return xml.toString();
		}

		public void startDocument() throws SAXException {
			xml.write("BEGIN:VCALENDAR\n");
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			buf.write(ch, start, length);
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equals("rdf:RDF")) {
				isRoot = false;
			} else if (qName.equals("rss")
					&& attributes.getValue(0).equals("2.0")) {
				isRoot = false;
			} else if (isRoot) {
				throw new SAXException("It is a form of the RSS that is not supported.");
			}

			if(uri.startsWith( RDF_CALENDAR_NAMESPACE )){
				qName = localName;
				if (qName.equals("Vcalendar")) {
					//xml.write("BEGIN:VCALENDAR\n");
				} else if (qName.equals("Vevent")) {
					xml.write("BEGIN:VEVENT\n");
					rrule = new StringBuffer();
					qNameList.clear();
				} else if (qName.equals("url")) {
					urlAttr = attributes.getValue(0);
				} else if (qName.equals("dtstart")) {
					parentTag = qName;
				} else if (qName.equals("dtend")) {
					parentTag = qName;
				} else if (qName.equals("dtstamp")) {
					parentTag = qName;
				}

			}
			//tagStack.add(qName);
			buf.reset();
			qNameList.add(qName);
			isMixed = false;
		}

		public void endElement(String uri, String localName, String qName)
		throws SAXException {
			if(uri.startsWith( RDF_CALENDAR_NAMESPACE )){
				qName = localName;
				String value = buf.toString();
				if (qName.equals("Vcalendar")) {
					//xml.write("END:VCALENDAR\n");
				} else if (qName.equals("version")) {
					xml.write("VERSION:" + value + "\n");
				} else if (qName.equals("prodid")) {
					xml.write("PRODID:" + value + "\n");
				} else if (qName.equals("x:wrRelcalid")) {
					xml.write("X-WR-RELCALID:" + value + "\n");
				} else if (qName.equals("x:wrTimezone")) {
					xml.write("X-WR-TIMEZONE:" + value + "\n");
				} else if (qName.equals("calscale")) {
					xml.write("CALSCALE:" + value + "\n");
				} else if (qName.equals("method")) {
					xml.write("METHOD:" + value + "\n");
				} else if (qName.equals("Vevent")) {
					if (rrule.length() > 0) {
						xml.write("RRULE:"
								+ rrule.toString().substring(0, rrule.length() - 1)
								+ "\n");
					}
					xml.write("END:VEVENT\n");
				} else if ((qName.equals("date") || qName.equals("dateTime"))
						&& (parentTag != null && parentTag.equals("dtstart"))) {
					parentTag = null;
					xml.write("DTSTART;VALUE=DATE:"
							+ value.replaceAll("-", "").replaceAll(":", "") + "\n");
				} else if ((qName.equals("date") || qName.equals("dateTime"))
						&& (parentTag != null && parentTag.equals("dtend"))) {
					parentTag = null;
					xml.write("DTEND;VALUE=DATE:"
							+ value.replaceAll("-", "").replaceAll(":", "") + "\n");
				} else if ((qName.equals("date") || qName.equals("dateTime"))
						&& (parentTag != null && parentTag.equals("dtstamp"))) {
					parentTag = null;
					xml.write("DTSTAMP;VALUE=DATE:"
							+ value.replaceAll("-", "").replaceAll(":", "") + "\n");
				} else if (qName.equals("dtstart")) {
					if(value.trim().length() > 0)
						xml.write("DTSTART;VALUE=DATE:"
								+ value.replaceAll("-", "").replaceAll(":", "") + "\n");
				} else if (qName.equals("dtend")) {
					if(value.trim().length() > 0)
						xml.write("DTEND;VALUE=DATE:"
								+ value.replaceAll("-", "").replaceAll(":", "") + "\n");
				} else if (qName.equals("summary")) {
					xml.write("SUMMARY:" + value + "\n");
				} else if (qName.equals("location")) {
					xml.write("LOCATION:" + value + "\n");
				} else if (qName.equals("description")) {
					if (!"".equals(value.trim()))
						xml.write("DESCRIPTION:" + value + "\n");
				} else if (qName.equals("url")) {
					xml.write("URL;VALUE=URL:" + urlAttr + "\n");
				} else if (qName.equals("uid")) {
					xml.write("UID:" + value + "\n");
				} else if (qName.equals("dtstamp")) {
					if(value.trim().length() > 0)
						xml.write("DTSTAMP:" + value + "\n");
				} else if (qName.equals("sequence")) {
					xml.write("SEQUENCE:" + value + "\n");
				} else if (qNameList.contains("rrule") && qName.equals("bymonth")) {
					rrule.append("BYMONTH=" + value + ";");
				} else if (qNameList.contains("rrule") && qName.equals("byday")) {
					rrule.append("BYDAY=" + value + ";");
				} else if (qNameList.contains("rrule") && qName.equals("interval")) {
					rrule.append("INTERVAL=" + value + ";");
				} else if (qNameList.contains("rrule") && qName.equals("freq")) {
					rrule.append("FREQ=" + value + ";");
				}

				/*if (tagStack.size() > 0)
			 tagStack.pop();*/
				//			isMixed = true;
				buf.reset();
			}
		}

		public void endDocument() throws SAXException {
			xml.write("END:VCALENDAR\n");
		}
	}
}
