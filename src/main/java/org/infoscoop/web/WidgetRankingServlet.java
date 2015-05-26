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
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.CacheDAO;
import org.infoscoop.dao.SiteAggregationMenuDAO;
import org.infoscoop.dao.WidgetDAO;
import org.infoscoop.dao.model.Cache;
import org.infoscoop.dao.model.Siteaggregationmenu;
import org.infoscoop.request.ProxyRequest;
import org.infoscoop.util.I18NUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * We create Widget-Ranking and return it.
 * 
 * @author a-kimura
 * 
 */
public class WidgetRankingServlet extends HttpServlet {
	private static final long serialVersionUID = "org.infoscoop.web.WidgetRankingServlet"
			.hashCode();
	
	public static final String XMLNS = "http://www.infoscop.org/namespace/widgetranking";
	
	private static final String CACHE_USER_ID = "WidgetRankingGenerator";
	private static final String CACHE_ID = "widgetRanking";
	private static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

	private static Log log = LogFactory.getLog(WidgetRankingServlet.class);
	private static WidgetRankingSynchronizer sync = new WidgetRankingSynchronizer();

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

		response.setContentType("text/plain; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");

		// The default of freshDay is 10th.
		int freshDay = getIntProperty(request.getHeader("X-IS-FRESHDAY"), 10);
		// The default of maxCount is 20.
		int maxCount = getIntProperty(request.getHeader("X-IS-MAXCOUNT"), 20);
		// The default of cacheLifetime is 10 minutes.
		int cacheLifetime = getIntProperty(request.getHeader("X-IS-CACHELIFETIME"),
				10);

		Writer out = response.getWriter();

		try {
			Cache cache = CacheDAO.newInstance().getCacheById(CACHE_ID, squareid);

			// If there is the cash, and when we are generating ranking, we return the cash even if the cash is old.
			// If it is in validity of the cash, naturally we  return the cash.
			if (cache != null
					&& (sync.inProcess() || !isExpire(cache, cacheLifetime))) {
				out.write(new String(cache.getBodyBytes(), "UTF-8"));
				return;
			}

			String rankStr = sync.getRanking(cacheLifetime, maxCount, freshDay,
					cache, request);

			out.write(rankStr);
		} catch (Exception e) {
			log.error("--- unexpected error occurred.", e);
			response.sendError(500);
		} finally {
			out.flush();
			out.close();
		}
	}

	private int getIntProperty(String value, int defaultValue) {
		if (value == null)
			return defaultValue;
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private boolean isExpire(Cache cache, int cacheLifetime) {
		long cacheTime = cache.getTimestamp().getTime();
		if (cacheTime < System.currentTimeMillis() - cacheLifetime * 60 * 1000) {
			return true;
		}
		return false;
	}

	static class WidgetRankingSynchronizer {
		private boolean inProcess = false;
		private String ranking;
		private Date cacheLastmodified;

		public boolean inProcess() {
			return inProcess;
		}

		public synchronized String getRanking(int cacheLifetime, int maxCount,
				int freshDay, Cache cache, HttpServletRequest request)
				throws Exception {
			inProcess = true;
			String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();

			try {
				// When new cash was create during the wait of the synchronized block by the other thread, we return the cash.
				// If a date of the latest cash is different from handed cash, we show that the other thread creates ranking during a wait.
				if (cache != null && cacheLastmodified != null
						&& cache.getTimestamp().before(cacheLastmodified))
					return ranking;

				log.info("Expiration date of the cash(" + cacheLifetime
						+ "minutses)passed, I recount the widget-ranking.");
				WidgetDAO widgetDao = WidgetDAO.newInstance();
				List<Object[]> ranks = widgetDao.getWidgetRanking(maxCount,
						freshDay, squareid);
				JSONObject cacheJson = null;
				if (cache != null)
					cacheJson = new JSONObject(new String(cache.getBodyBytes(),
							"UTF-8"));
				SiteAggregationMenuDAO menuDao = SiteAggregationMenuDAO
						.newInstance();
				Siteaggregationmenu topmenu = menuDao.select("topmenu", squareid);
				Siteaggregationmenu sidemenu = menuDao.select("sidemenu", squareid);
				Map i18nMap = I18NUtil.getResourceMap(I18NUtil.TYPE_MENU,
						request.getLocale());
				JSONObject rankJson = new JSONObject();
				JSONArray rankArray = new JSONArray();
				Date now = new Date();
				for (Object[] rank : ranks) {
					JSONObject json = new JSONObject();
					String menuId = (String) rank[0];
					String type = (String) rank[1];
					String url = (String) rank[2];
					try {
						TitleInfo titleHref = getTitleAndHrefFromCache(type,
								url, menuId, cacheJson);
						if (cache != null) {
							//When there is not a cash, we don't return a rankingTime.
							if (titleHref == null) {
								// In the case of the widget which there is not in a cash, we register the first appearance time.
								json.put("rankinTime", now.getTime());
							} else {
								// We return the first appearance time of cash.
								json.put("rankinTime", titleHref
										.getRankinTime());
								if (!titleHref.isValidTitle())
									titleHref = null;
							}
						}
						//We get a title from a menu even if we can get it from a cash.
						if (menuId != null && menuId.length() > 0) {
							titleHref = getTitleAndHrefFromMenu(menuId,
									topmenu, sidemenu, i18nMap);
						}
						//When we cannot get a title, we send a request.
						if (titleHref == null) {
							titleHref = getTitleAndHrefByRequest(type, url,
									request);
						}
						//exclude the widget which there is not menuId and can't get its title.
						if ((menuId == null || menuId.length() == 0)
								&& titleHref == null) {
							continue;
						}
						if (titleHref != null) {
							if (titleHref.isValidTitle())
								json.put("title", titleHref.getTitle());
							if (titleHref.isValidHref())
								json.put("href", titleHref.getHref());
							if (titleHref.isExcepted())
								json.put("excepted", titleHref.isExcepted());
						}
					} catch (Exception e) {
						log.warn("Failed to get the title:menuId=" + menuId + ",type="
								+ type + ",url=" + url + ",error="
								+ e.getMessage());
					}
					if (menuId != null && menuId.length() > 0)
						json.put("menuId", menuId);
					if (type != null && type.length() > 0)
						json.put("type", type);
					if (url != null && url.length() > 0)
						json.put("url", url);
					json.put("count", rank[3]);
					json.put("lastCount", rank[4]);
					rankArray.put(json);
				}
				rankJson.put("data", rankArray);

				String dateFormat = request.getHeader("X-IS-DATETIMEFORMAT");
				String lastmodified = getLastmodified(dateFormat);
				rankJson.put("lastmodified", lastmodified);

				// save to cash
				ranking = rankJson.toString();
				Cache newCache = saveCache(ranking);
				cacheLastmodified = newCache.getTimestamp();
			} finally {
				inProcess = false;
			}
			return ranking;
		}

		private Cache saveCache(String rankStr) throws Exception {
			byte[] rssBytes = rankStr.getBytes("utf-8");
			String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
			ByteArrayInputStream is = new ByteArrayInputStream(rssBytes);
			return CacheDAO.newInstance().insertUpdateCache(CACHE_ID,
					CACHE_USER_ID, CACHE_USER_ID, is, new HashMap(), squareid);
		}

		private TitleInfo getTitleAndHrefFromCache(String type, String url,
				String menuId, JSONObject cacheJson) throws Exception {
			// get a title by former cash
			if (cacheJson != null) {
				JSONArray data = cacheJson.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject rank = data.getJSONObject(i);
					String cacheType = rank.optString("type");
					String cacheUrl = rank.optString("url");
					String cacheMenuId = rank.optString("menuId");
					if (((type != null && type.length() > 0 && type
							.equals(cacheType)) && (url == null
							|| url.length() == 0 || url.equals(cacheUrl)))
							|| (menuId != null && menuId.length() > 0 && menuId
									.equals(cacheMenuId))) {
						String cacheTitle = rank.optString("title");
						long cacheRankinTime = rank.optLong("rankinTime");
						boolean cacheExcepted = rank.optBoolean("excepted");
						if (cacheTitle != null && cacheTitle.length() > 0) {
							String cacheHref = rank.optString("href");
							return new TitleInfo(cacheTitle, cacheHref,
									cacheRankinTime, cacheExcepted);
						}
						return new TitleInfo(null, null, cacheRankinTime,
								cacheExcepted);
					}
				}
			}
			return null;
		}

		private TitleInfo getTitleAndHrefByRequest(String type, String url,
				HttpServletRequest request) throws Exception {
			if (type.startsWith("g_") && type.length() > 2)
				url = type.substring(2);
			if (url == null || url.trim().length() == 0)
				return null;
			ProxyRequest proxyRequest = new ProxyRequest(url, "Detect");
			Enumeration headers = request.getHeaderNames();
			while (headers.hasMoreElements()) {
				String headerName = (String) headers.nextElement();
				proxyRequest.putRequestHeader(headerName, request
						.getHeader(headerName));
			}
			proxyRequest.setLocales(request.getLocales());
			int status = proxyRequest.executeGet();
			if (status != 200)
				return null;
			String res = proxyRequest.getResponseBodyAsString("UTF-8");
			JSONArray types = new JSONArray(res);
			for (int i = 0; i < types.length(); i++) {
				JSONObject typeObj = types.getJSONObject(i);
				String detectType = typeObj.optString("type");
				if (type.equals(detectType)
						|| (type.startsWith("g_") && detectType
								.equals("Gadget"))) {
					String title = typeObj.optString("title");
					String href = typeObj.optString("href");
					boolean excepted = typeObj.optBoolean("excepted");
					return new TitleInfo(title, href, excepted);
				}
			}
			return null;
		}

		private TitleInfo getTitleAndHrefFromMenu(String menuId,
				Siteaggregationmenu topmenu, Siteaggregationmenu sidemenu,
				Map i18nMap) throws Exception {
			TitleInfo title = getMenuTitle(menuId, topmenu);
			if (title == null)
				title = getMenuTitle(menuId, sidemenu);
			if (title != null) {
				title.setTitle(I18NUtil.replace(title.getTitle(), i18nMap));
				return title;
			}
			return null;
		}

		private TitleInfo getMenuTitle(String menuId, Siteaggregationmenu menu)
				throws SAXException {
			NodeList sites = menu.getElement().getElementsByTagName("site");
			try {
				for (int i = 0; i < sites.getLength(); i++) {
					Element site = (Element) sites.item(i);
					String siteId = site.getAttribute("id");
					if (siteId.equals(menuId)) {
						String href = site.getAttribute("href");
						String dirTitle = site.getAttribute("directory_title");
						if (dirTitle != null && dirTitle.length() > 0)
							return new TitleInfo(dirTitle, href);
						return new TitleInfo(site.getAttribute("title"), href);
					}
				}
			} catch (Exception e) {
				log.warn("We failed in the acquisition of the title from a menu." + e.getMessage());
			}
			return null;
		}

		private String getLastmodified(String dateFormat) {
			SimpleDateFormat df = null;
			if (dateFormat == null)
				df = new SimpleDateFormat( DEFAULT_DATE_FORMAT );
			else {
				try {
					df = new SimpleDateFormat(dateFormat);
				} catch (Exception e) {
					df = new SimpleDateFormat( DEFAULT_DATE_FORMAT );
				}
			}
			return df.format(new Date());
		}
	}
	
	static class TitleInfo {
		private String title;
		private String href;
		private long rankinTime;
		private boolean excepted;

		public TitleInfo(String title, String href) {
			this.title = title;
			this.href = href;
		}

		public TitleInfo(String title, String href, boolean excepted) {
			this.title = title;
			this.href = href;
			this.excepted = excepted;
		}
		
		public TitleInfo(String title, String href, long rankinTime, boolean excepted) {
			this.title = title;
			this.href = href;
			this.excepted = excepted;
			this.rankinTime = rankinTime;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setHref(String href) {
			this.href = href;
		}

		public String getTitle() {
			return title;
		}

		public String getHref() {
			return href;
		}
		
		public long getRankinTime() {
			return rankinTime;
		}
		
		public boolean isExcepted() {
			return excepted;
		}

		public boolean isValidTitle() {
			return title != null && title.length() > 0;
		}

		public boolean isValidHref() {
			return href != null && href.length() > 0;
		}
	}
}
