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

package org.infoscoop.request.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.request.ProxyRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class FeedJsonFilter extends ProxyFilter {
	private static final Log log = LogFactory.getLog( FeedJsonFilter.class );
	
	protected int preProcess( HttpClient client,HttpMethod method,ProxyRequest request ) {
		int numEntries = 3;
		try {
			numEntries = Integer.parseInt( request.getFilterParameter("ne"));
		} catch( NumberFormatException ex ) {
			// ignore ?
		}
		request.putRequestHeader("X-IS-RSSMAXCOUNT",String.valueOf( numEntries ));
		
		if( log.isDebugEnabled() )
			log.debug("NUM_ENTRIES: "+numEntries );
		
		return 0;
	}
	protected InputStream postProcess( ProxyRequest request,InputStream responseStream )
			throws IOException {
		boolean getSummaries = false;
		try {
			getSummaries = Boolean.parseBoolean( request.getFilterParameter("gs"));
		} catch( Exception ex ) {
			// ignore ?
		}
		
		if( log.isDebugEnabled() )
			log.debug("GET_SUMMARIES: "+getSummaries );
		
		try {
			String r1Str = request.getResponseBodyAsString("UTF-8");
			Pattern pattern = Pattern.compile("rssDate : new Date\\(.*\\),");
			Matcher matcher = pattern.matcher(r1Str);
			r1Str = matcher.replaceFirst("");
			JSONObject r1 = new JSONObject( r1Str );
			
			if( r1.getInt("statusCode") != 0)
				throw new IOException(r1.optString("errorMessage")); //FIXME
			
			JSONObject feed = new JSONObject();
			feed.put("Title",r1.optString("title"));
			feed.put("Url",r1.optString("url"));
			feed.put("Link",r1.optString("link"));
			feed.put("Description",r1.optString("description"));
			feed.put("ErrorMsg",r1.optString("errorMessage"));
			
			JSONArray entries = new JSONArray();
			feed.put("Entry",entries );
			
			JSONArray items = r1.getJSONArray("items");
			for( int i=0;i<items.length();i++ ) {
				JSONObject item = items.getJSONObject( i );
				JSONObject entry = new JSONObject();
				entry.put("Title",item.optString("title"));
				entry.put("Link",item.optString("link"));
				
				if( item.has("creator"));
					entry.put("Author",item.getString("creator"));
				
				if( getSummaries )
					entry.put("Summary",item.getString("description"));
				
				if( item.has("dateLong")) {
					long dateLong = -1;
					try {
						dateLong = Long.parseLong( item.getString("dateLong"));
					} catch( Exception ex ) {
						// ignore ?
					}
					
					if( dateLong > 0 )
						entry.put("Date",( long )( dateLong /1000 ));
				}
				
				entries.put( entry );
			}
			
			return new ByteArrayInputStream( feed.toString().getBytes("UTF-8"));
		} catch( Exception ex ) {
			// FIXME
			throw new RuntimeException( ex );
		}
	}
}
