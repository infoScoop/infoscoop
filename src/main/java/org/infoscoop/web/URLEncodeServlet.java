package org.infoscoop.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class URLEncodeServlet extends HttpServlet {

	private static final long serialVersionUID = "org.infoscoop.web.URLEncodeServlet"
			.hashCode();

	private Log log = LogFactory.getLog(this.getClass());

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//		String text = request.getParameter("text");
		String[] textAry = request.getParameterValues("text");
		String encoding = request.getParameter("encoding");
		
		StringBuffer textsb = new StringBuffer();
		String text = "";
		if(textAry.length > 1){
			textsb.append("[");
			for(int i=0;i<textAry.length;i++){
				text = textAry[i];
				text = encodeText(text, encoding);
				
				if(i != 0){
					textsb.append(",");
				}
				textsb.append("'" + text + "'");
			}
			textsb.append("]");
		}else{
			text = textAry[0];
			textsb.append(encodeText(text, encoding));
		}
		
		if(log.isDebugEnabled())
			log.debug("URL encoded : " + textsb.toString()
				+ ", encoding : " + encoding);

		response.setContentType("text/plain; charset=UTF-8");

		Writer writer = response.getWriter();

		writer.write(textsb.toString());

		writer.flush();
		writer.close();
	}
	
	private String encodeText(String text, String encoding) throws UnsupportedEncodingException{
//		text = new String(text.getBytes("iso-8859-1"), "utf-8");
		if(log.isDebugEnabled())
			log.debug("original : " + text);

		if (encoding != null) {
			//text = URLDecoder.decode(text, "UTF-8");
			text = URLEncoder.encode(text, encoding);
			// Correspondence to the problem that URLEncoder converts a blank into "+".
			text = text.replaceAll("\\+", "%20");
		}
		return text;
	}
}
