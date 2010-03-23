package org.infoscoop.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.util.NoOpEntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class ValidationServlet extends HttpServlet {
	
	private static SAXParserFactory factory;
	private static Set invalidCharset = new HashSet();

	static{
		// SAXParserFactory factory = SAXParserFactory.newInstance();
		factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);// We add it to pick up the element of the msd namespace.
		factory.setValidating( false );

		invalidCharset.add("EUC_JP");
		invalidCharset.add("EUCJP");
		invalidCharset.add("EUC_KR");
		invalidCharset.add("EUCKR");
	}

	private static final long serialVersionUID = "org.infoscoop.web.StringServlet"
			.hashCode();

	private Log log = LogFactory.getLog(this.getClass());

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//text of validation target 
		String text = request.getParameter("text");
		
		//type of validation.
		String method = request.getParameter("method");
		
		//length for check max length 
		String lengthStr = request.getParameter("length");
		
		boolean result;
		if("regexp".equals(method)){
			result = checkRegExp(text);		
		}else if("charset".equals(method)){
			result = checkCharset(text);		
		}else if("datefmt".equals(method)){
			result = checkDateFormat(text);
		}else {
			int length = Integer.parseInt(lengthStr);
			result = checkMaxLength(text, length);
		}
		String resultStr = Boolean.toString(result);
		response.setContentType("text/plain");
		response.setContentLength( resultStr.length() );
		Writer writer = response.getWriter();
		writer.write( resultStr );
		writer.flush();
		writer.close();
	}
	
	private static boolean checkMaxLength(String value, int maxLength){
		try {
			return value.getBytes("UTF-8").length <= maxLength;
			
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

	private static boolean checkRegExp(String text){
		try{
			Pattern.compile(text);
			return true;
		}catch(PatternSyntaxException e){
			return false;
		}
	}
	
	private static boolean checkCharset(String text){
		if(text != null && invalidCharset.contains(text.toUpperCase()))
			return false;
		try{
			Charset.forName(text);
			return true;
		}catch(UnsupportedCharsetException e){
			return false;
		}
	}

	private static boolean checkDateFormat(String text){
		try{
			new SimpleDateFormat(text);
			return true;
		}catch(IllegalArgumentException e){
			return false;
		}
	}
	
	private static boolean checkXml(String text){
		
		try{
			XMLReader reader = factory.newSAXParser().getXMLReader();
			reader.setEntityResolver(NoOpEntityResolver.getInstance());
			reader.parse(text);
			return true;
		} catch (SAXException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (ParserConfigurationException e) {
			return false;
		}
	}
	
	public static void main(String args[]){
		System.out.println(checkCharset("EUC_JP"));
	}
}
