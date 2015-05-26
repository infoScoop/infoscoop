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

package org.infoscoop.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.CacheDAO;
import org.infoscoop.dao.KeywordLogDAO;
import org.infoscoop.dao.model.Cache;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.request.filter.RssFilter;
import org.infoscoop.util.DateUtility;
import org.infoscoop.util.XmlUtil;

/**
 * create a keyword-ranking-RSS and return it.
 * @author nishiumi
 *
 */
public class KeywordRankingServlet extends HttpServlet {
	private static final long serialVersionUID = "org.infoscoop.web.KeywordRankingServlet"
			.hashCode();
	
	private static Log log = LogFactory.getLog(KeywordRankingServlet.class);

	private static int RANKING_PERIOD_MAX;
	private static int RANKING_NUM_MAX;

	private static int RANKING_PERIOD_DEFAULT = 30;
	private static int RANKING_NUM_DEFAULT = 20;
	
	private static final String TODAY = "TODAY";
	private static final String NOW = "NOW";
	private static final String DATE_FORMAT = "yyyyMMddHH";
	private static final String W3CDTF_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String uid = (String) request.getSession().getAttribute("Uid");
		String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
		
		if (log.isInfoEnabled()) {
			log.info("uid:[" + uid + "]: doPost");
		}
		
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		
		OutputStream out = null;
		
		int rankingPeriod;
		int rankingNum;
		String endDate;
		int offset = UserContext.instance().getUserInfo().getClientTimezoneOffset() / 60;
		String cacheName = "keywordRanking_UTC" + offset;

		try {
			String param_baseDate = request.getParameter("baseDate");
			
			// If baseDate is null, it is behavior of TODAY.
			endDate = (param_baseDate == null)? TODAY : param_baseDate;
			
			Cache cache = CacheDAO.newInstance().getCacheById(cacheName, squareid);
			
			String rss;
			// We do cash only in case that appoined "TODAY".
			if(TODAY.equals(endDate) && cache != null && DateUtility.isToday(cache.getTimestamp())){
				List<Header> headerList = cache.getHeaderList();
				for(Header header : headerList){
					if(header.getName().equalsIgnoreCase("Last-Modified")){
						String lastModified = header.getValue();
						if( lastModified != null && lastModified.equals( request.getHeader("If-Modified-Since") )) {
							response.setStatus( 304 );
							return;
						}
						response.addHeader("Last-Modified",lastModified );	
					}
				}

				rss = new String(cache.getBodyBytes(), "UTF-8");
				log.info("get keywordRanking from cache.");
			}else{
				String param_period = request.getParameter("period");
				String param_rankingNum = request.getParameter("rankingNum");
				
				try{
					rankingPeriod = Integer.parseInt(param_period);
				}catch(NumberFormatException e){
					log.warn("rankingPeriod ["+param_period+"] is not integer !");
					rankingPeriod = RANKING_PERIOD_DEFAULT;
				}
				try{
					rankingNum = Integer.parseInt(param_rankingNum);
				}catch(NumberFormatException e){
					log.warn("rankingNum["+param_rankingNum+"] is not integer !");
					rankingNum = RANKING_NUM_DEFAULT;
				}
				
				// check of the maximum
				rankingPeriod = (rankingPeriod > RANKING_PERIOD_MAX)? RANKING_PERIOD_MAX : rankingPeriod;
				rankingNum = (rankingNum > RANKING_NUM_MAX)? RANKING_NUM_MAX : rankingNum;
				
				boolean saveCache = true;
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
				sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
				
				if(TODAY.equals(endDate)){
					Calendar yesterday = Calendar.getInstance();
					yesterday.setTimeZone(TimeZone.getTimeZone("UTC"));
					yesterday.set(Calendar.HOUR_OF_DAY, 0);
					endDate = sdf.format(yesterday.getTime());
				}
				else if(NOW.equals(endDate)){
					endDate = sdf.format(new Date());
					saveCache = false;	// When it is appointed "NOW", we don't do cash.
				}
				
				Map countMap = KeywordLogDAO.newInstance().getCountMap(getStartDate(endDate, rankingPeriod), endDate, new Integer(0), UserContext.instance().getUserInfo().getCurrentSquareId());
				if (countMap.size() == 0) {
					response.setStatus(204);
					return;
				}
				
				response.addDateHeader("Last-Modified",new Date().getTime() );
				rss = makeRss(countMap, rankingPeriod, rankingNum);

				if(saveCache)
					insertRss(cacheName, rss);
			}
			
			boolean noProxy = false;
			Enumeration headers = request.getHeaderNames();
			while(headers.hasMoreElements()){
				String headerName = (String)headers.nextElement();
				if(headerName.equalsIgnoreCase("X-IS-NOPROXY")){
					noProxy = true;
					break;
				}
			}
			
			byte[] resByte;
			if(noProxy){
				response.setContentType("text/plain; charset=UTF-8");
				String requestURL = request.getRequestURL() != null ? request.getRequestURL().toString() : "";
				ProxyRequest proxyRequest = new ProxyRequest(requestURL, "RSSReader");
				proxyRequest.setPortalUid((String)request.getSession().getAttribute("Uid"));
				
				resByte = RssFilter.process(proxyRequest, new ByteArrayInputStream(rss.getBytes("UTF-8")));
			}else{
				response.setContentType("text/xml; charset=UTF-8");
				resByte = rss.getBytes("UTF-8");
			}
			
			out = response.getOutputStream();
			out.write(resByte);
		} catch (Exception e){
			log.error("--- unexpected error occurred.", e);
			response.sendError(500);
		}  finally{
			if(out != null){
				out.flush();
				out.close();
			}
		}
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		String param_rankingPeriod_max = config.getInitParameter("rankingPeriod_max");
		String param_rankingNum_max = config.getInitParameter("rankingNum_max");
		try {
			if (param_rankingPeriod_max != null)
				RANKING_PERIOD_MAX = Integer.parseInt(param_rankingPeriod_max);
		} catch (Exception e) {
			log.error("param_rankingPeriod_max must be a number. RANKING_PERIOD_MAX = "
					+ param_rankingPeriod_max);
			throw new ServletException();
		}

		try {
			if (param_rankingNum_max != null)
				RANKING_NUM_MAX = Integer.parseInt(param_rankingNum_max);
		} catch (Exception e) {
			log.error("param_rankingNum_max must be a number. RANKING_NUM_MAX = "
					+ param_rankingNum_max);
			throw new ServletException();
		}
	}
	
	/**
	 * From the handed date and days, we calculate an end date.
	 * @param date
	 * @param rankingPeriod
	 * @return
	 */
	private static String getStartDate(String date, int rankingPeriod) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		if (date.length() == 10) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
			cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6, 8)));
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(date.substring(8, 10)));
			cal.add(Calendar.DAY_OF_YEAR, -(rankingPeriod -1));
			return sdf.format(cal.getTime());
		}
		throw new  IllegalArgumentException("Illegal Date format.");
	}
	
	/**
	 * We convert a date of java.util.Date type into a W3CDTF form.
	 * 
	 * @param date
	 * @return
	 */
	private static String getW3CDTFDate(Date date) {
		SimpleDateFormat sdf = UserContext.instance().getUserInfo().getClientDateFormat(W3CDTF_FORMAT);
		String str = sdf.format(date);
		str = str.substring(0, str.length() - 2) + ":"
				+ str.substring(str.length() - 2);
		return str;
	}

	private static String makeRss(Map countMap, int rankingPeriod, int rankingNum) {
		StringBuffer xml = new StringBuffer();
		
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		xml.append("<rss version=\"2.0\" xmlns:rep=\"http://www.beacon-it.co.jp/namespaces/msd/report\">\n");
		xml.append("<channel>\n");
		//TODO The item between title tag also should be internationalized. 
		xml.append("<title>Search keyword ranking</title>\n<link></link>\n<rep:reportConf>\n<reportConf name=\"keywordRanking\">\n<span>"
						+ rankingPeriod
						+ "</span>\n</reportConf>\n</rep:reportConf>\n<rep:createDate>"
						+ getW3CDTFDate(new Date()) + "</rep:createDate>\n");
		Iterator ucIt = countMap.entrySet().iterator();
		int max = 0;
		int preCount = -1; 
		while (ucIt.hasNext()) {
			Map.Entry entry = (Map.Entry)ucIt.next();
			Integer countObj = (Integer)entry.getValue();
			int count = countObj.intValue();
			
			if (max >= rankingNum && count != preCount)
				break;
			
			String keyword = (String)entry.getKey();
			xml.append("<item>\n");
			xml.append("<title>").append(
					XmlUtil.escapeXmlEntities(keyword)).append(
					"</title>\n");
			xml.append("<link>").append("</link>\n");
			xml.append("<rep:count>").append(count).append("</rep:count>\n");
			xml.append("</item>\n");
			max++;
			preCount = count;
		}
		xml.append("</channel>\n</rss>\n");
		
		return xml.toString();
	}

	private static void insertRss(String id, String rss) {
		try {
			byte[] rssBytes = rss.getBytes("utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(rssBytes);
			Map headerMap = new HashMap();
			headerMap.put("Last-Modified",Arrays.asList( DateUtility.getW3CDTFDate( new Date())));
			headerMap.put("Content-Type",Arrays.asList( "text/xml;charset=UTF-8"));
			headerMap.put("Content-Length",Arrays.asList( String.valueOf(rssBytes.length)));
			CacheDAO.newInstance().insertUpdateCache(id, "KeywordRankingGenerator", "KeywordRankingGenerator",
					is, headerMap, UserContext.instance().getUserInfo().getCurrentSquareId());
		} catch (UnsupportedEncodingException e) {
		}
	}
	
}
