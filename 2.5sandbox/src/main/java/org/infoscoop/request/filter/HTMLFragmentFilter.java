package org.infoscoop.request.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xpath.XPathAPI;
import org.infoscoop.dao.model.Cache;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.service.CacheService;
import org.infoscoop.util.DocumentBuildFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sun.io.MalformedInputException;

public class HTMLFragmentFilter extends ProxyFilter {
	
	private static final Log log = LogFactory.getLog(HTMLFragmentFilter.class);
	
	protected int preProcess(HttpClient client, HttpMethod method, ProxyRequest request) {
		String cacheURL = request.getRequestHeader("fragment-chacheID");
		String cacheLifeTimeStr = request.getRequestHeader("fragment-cacheLifeTime");
		int cacheLifeTime = 60;//360 is set by default in script
				
		if (cacheLifeTimeStr != null) {
			try {
				cacheLifeTime = Integer.parseInt(cacheLifeTimeStr);
			} catch (NumberFormatException e) {}
		}

		if (cacheURL != null && cacheURL.length() > 0) {
			Cache cache = CacheService.getHandle().getCacheByUrl(cacheURL);
			if (cache != null && cache.getId() != null) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, - cacheLifeTime);// Update cache every 6 hours
				Date now = cal.getTime();
				
				if (!now.after(cache.getTimestamp())) {
					List<Header> headerList;
					try {
						headerList = cache.getHeaderList();
					} catch( SAXException ex ) {
						log.error("parsing error", ex);
						throw new RuntimeException();
					}
					
					//TODO:Fix this later　20090612 by endoh
					String ifModifiedSince = request.getRequestHeader("If-Modified-Since");
					if(ifModifiedSince != null )
						for(Header header : headerList)
							if(header.getName().equalsIgnoreCase("If-Modified-Since"))
								if(ifModifiedSince.equals(header.getValue()))
									return HttpStatus.SC_NOT_MODIFIED;
							
					String ifNoneMatch = request.getRequestHeader("If-None-Match");
					if( ifNoneMatch != null )
						for(Header header : headerList)
							if(header.getName().equalsIgnoreCase("etag"))
								if(ifNoneMatch.equals(header.getValue()))
									return HttpStatus.SC_NOT_MODIFIED;
					//TODO:end
					
					for(Header header : headerList){
						String name = header.getName();
						if (!name.equalsIgnoreCase("transfer-encoding"))								
							request.putResponseHeader(name, header.getValue());
					}
					request.setResponseBody( new ByteArrayInputStream( cache.getBodyBytes()) );
					request.putResponseHeader("MSDPortal-Cache-ID", cache.getId());
					//if(log.isInfoEnabled())
						log.error("use cache " + cacheURL);
					return 200;
				}
			}
		}
		
		return 0;
	}
	protected InputStream postProcess(
			ProxyRequest request, InputStream responseStream) throws IOException {
		if( !ProxyHtmlUtil.isHtml(request.getResponseHeader("content-type")))
			return null;//return 500;TODO:
		
		ProxyHtmlUtil.headerProcess( request);
		
		String xPath = request.getRequestHeader("fragment-xpath");
		String encoding = request.getRequestHeader("fragment-charset");
		String outputEncoding = encoding != null && encoding.length() > 0 ? encoding
				: "UTF-8";
		
		if(log.isDebugEnabled())
			log.debug("input charset = " + encoding + ",  output charset = " + outputEncoding);
		request.putResponseHeader("Content-Type", "text/html; charset=" + outputEncoding + "");
		
		byte[] fragmentBytes = null;
		if(xPath != null && 0 < xPath.length()){
			DocumentBuildFilter filter = new DocumentBuildFilter();
			
			try{
				String requestURL = request.getRedirectURL();
				if( requestURL == null )
					requestURL = request.getEscapedOriginalURL();
				
				ProxyHtmlUtil.getInstance().nekoProcess( responseStream,
					encoding,new XMLDocumentFilter[] {
						new ProxyHtmlUtil.AttachBaseTagFilter( requestURL ),
						filter
					});
			}catch(MalformedInputException e){
				log.error("Invalid encoding is specified to the page[" + encoding + "]");
				request.putResponseHeader("Content-Length", "0");
				throw e;
			}
			fragmentBytes = fragmentHTML( filter.getDocument(), xPath, outputEncoding);
		}
		
		if(fragmentBytes != null){
			String cacheURL = request.getRequestHeader("fragment-chacheID");
			
			request.putResponseHeader("Content-Length", Integer.toString(fragmentBytes.length));
			if (cacheURL != null && cacheURL.length() > 0) {
				Map headerMap = request.getResponseHeaders();
				try {
					String cacheId = CacheService.getHandle().insertUpdateCache(cacheURL, new ByteArrayInputStream(fragmentBytes), headerMap);
					if(log.isInfoEnabled())
						log.info("save cache: url=" + cacheURL + ", cacheId=" + cacheId);
					request.putResponseHeader("MSDPortal-Cache-ID", cacheId);
				} catch (Exception e) {
					log.error("save cache failed.[id = " + cacheURL+ "]:[url = " + cacheURL + "]", e);
				}
				
			}
			return new ByteArrayInputStream(fragmentBytes);
		}else{
			//request.setResponseBody(null);
			request.putResponseHeader("Content-Length", "0");
			
			log.error("empty fragmentBytes");
			return null;
		}
		
		//return method.getStatusCode();
	}
	public byte[] fragmentHTML( Document doc, String xpath, String encoding ){
		if(log.isDebugEnabled())
			log.debug("start fragmentHTML : xpath="+xpath);
		
		Element root = doc.getDocumentElement();
		if(!"html".equalsIgnoreCase(root.getNodeName())){
			NodeList htmlList = root.getElementsByTagName("html");
			if(htmlList.getLength() == 0) {
				log.error("document do not have html tag.");
				
				return null;
			}
			root = (Element) htmlList.item(0);
		}
		
		if(log.isDebugEnabled())
			log.debug("before html : \n"+document2String(doc));
		
		// remove scriptTags
		/*
		NodeList scriptTags = XPathAPI.selectNodeList(root, "//script");
		int tagsLength = scriptTags.getLength();
		for(int i=0;i<tagsLength;i++){
			scriptTags.item(i).getParentNode().removeChild(scriptTags.item(i));;
		}
		*/
		
		NodeList headTags = root.getElementsByTagName("head");
		for( int j=0;j<headTags.getLength();j++ ) {
			Element headTag = (Element)headTags.item(j);
			NodeList metas = headTag.getElementsByTagName("meta");
			for(int i = 0; i < metas.getLength(); i++){
				Element tmpTag = (Element)metas.item(i);
				if(!"Content-Type".equalsIgnoreCase(tmpTag.getAttribute("http-equiv")))
					headTag.removeChild(tmpTag);
			}
		}
		
		Node targetNode = null;
		Node body = null;
		byte[] fragmentHTML = null;
		try {
			targetNode = XPathAPI.selectSingleNode(root, xpath);
			body = XPathAPI.selectSingleNode(root, "//body");
		} catch ( Exception ex ) {
			log.error("fragment failed.", ex );
		}
		
		if(log.isDebugEnabled())
			log.debug("target:"+targetNode+" : body:"+body);
		
		if(targetNode != null && body != null){
			if("body".equals(targetNode.getNodeName().toLowerCase())){
				// No processing
			}else{
				NodeList childNodes = body.getChildNodes();
				int childLength = childNodes.getLength();
				for(int i=childLength-1; 0 <= i; i--){
					body.removeChild(childNodes.item(i));
				}
				body.appendChild(targetNode);
			}
			
			String resultHTML = document2String( doc );
			//FIXME
			resultHTML = resultHTML.replaceAll("&amp;","&");
			
			if(log.isDebugEnabled()){
				log.debug("result html : \n"+resultHTML);
			}
			
			try {
				fragmentHTML = resultHTML.getBytes( encoding );
			} catch (UnsupportedEncodingException ex){
				log.error("Invalid encoding is specified.", ex);
				
				throw new IllegalArgumentException("unsupported encoding :["+encoding+"]");
			}
		}else{
			log.error("target is null : "+targetNode + " : "+body+" @"+xpath );
			
			throw new IllegalArgumentException("node not found :["+xpath+"]");
		}
		
		return fragmentHTML;
	}
	private String document2String(Document doc) {
		try {
			Source source = new DOMSource(doc);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch( TransformerException ex ) {
			throw new RuntimeException("unexcepted error.",ex );
		}
	}

}
