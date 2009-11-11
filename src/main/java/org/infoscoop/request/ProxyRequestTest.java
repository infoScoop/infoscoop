package org.infoscoop.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.infoscoop.util.Crypt;

public class ProxyRequestTest {
	
	public static void main(String args[]) throws HttpException, IOException{
		//testActiveMailFilter();
		//testCalendarFilter();
		//testNekoFilter();
		//testMakeMenuFilter();
		//testRssFilter();
		//testNoOperationFilter();
		//testURLReplaceFilter();
		//testMaximizeGadgetFilter();
		//testSearchResultFilter();
		
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod("http://loclahost/ntlmtest/inicio.xml");
		method.setRequestHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*");
		method.setRequestHeader("Accept-Language", "ja,en;q=0.5");
		method.setRequestHeader("Accept-Encoding", "gzip, deflate");
		method.setRequestHeader("If-Modified-Since", "Wed, 14 May 2008 07:15:21 GMT");
		method.setRequestHeader("If-None-Match", "\"3d6c2-23f3-8b33cdb5\"");
		method.setRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)");
		method.setRequestHeader("Host", "localhost");
		method.setRequestHeader("Connection", "Keep-Alive");
		method.setRequestHeader("Authorization", "NTLM TlRMTVNTUAADAAAAAAAAAEgAAAAAAAAASAAAAAAAAABIAAAAAAAAAEgAAAAAAAAASAAAAAAAAABIAAAABcKIogUBKAoAAAAP");
		method.setRequestHeader("Cookie", "Apache=58.80.230.67.214291213856606176");
		client.executeMethod(method);
		method.getResponseBody();
	}
	
	public static void testActiveMailFilter() throws Exception{
		ProxyRequest info = new ProxyRequest("https://mail.beacon-it.co.jp/am_bin/am_main.cgi/omlist", "ActiveMail");

		//headers.put("accept-encoding", "gzip,feflate");
		info.putRequestHeader("authuserid","hr-endoh");
		Crypt cryptInstance = Crypt.gerCryptInstance();
		try {
			info.putRequestHeader("authpassword", cryptInstance.doCrypt(Crypt.ENCRYPT, "xxxx"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		info.putRequestHeader("authtype", "formActiveMail");
		//headers.put("authtype", "postCredential");
		
		System.out.println(info.executePost());
		BufferedReader br =  new BufferedReader(new InputStreamReader(info.getResponseBody(),"UTF-8"));
		String out = null;
		while((out = br.readLine())!= null){
			System.out.println(out);
		}
	}
	
	public static void testCalendarFilter() throws Exception{
		ProxyRequest info = new ProxyRequest("http://weather.livedoor.com/forecast/ical/13/63.ics", "Calendar");
		
		System.out.println(info.executeGet());

		BufferedReader br =  new BufferedReader(new InputStreamReader(info.getResponseBody(),"UTF-8"));
		String out = null;
		while((out = br.readLine())!= null){
			System.out.println(out);
		}
	}
	
	public static void testMakeMenuFilter() throws Exception{
		ProxyRequest info = new ProxyRequest("http://localhost:8080/msd-portal/siteaggregationmenu.xml", "MakeMenu");
		
		System.out.println(info.executeGet());

		BufferedReader br =  new BufferedReader(new InputStreamReader(info.getResponseBody(),"UTF-8"));
		String out = null;
		while((out = br.readLine())!= null){
			System.out.println(out);
		}
	}

	public static void testNekoFilter() throws Exception{
		ProxyRequest info = new ProxyRequest("http://jp.msn.com/", "Neko");
		
		System.out.println(info.executeGet());

		BufferedReader br =  new BufferedReader(new InputStreamReader(info.getResponseBody(),"UTF-8"));
		String out = null;
		while((out = br.readLine())!= null){
			System.out.println(out);
		}
	}
	
	public static void testNoOperationFilter() throws Exception{
		ProxyRequest info = new ProxyRequest("http://localhost:8080/msd-portal/searchEngine.xml", "NoOperation");
		
		System.out.println(info.executeGet());

		BufferedReader br =  new BufferedReader(new InputStreamReader(info.getResponseBody(),"UTF-8"));
		String out = null;
		while((out = br.readLine())!= null){
			System.out.println(out);
		}
	}
	
	public static void testURLReplaceFilter() throws Exception{
		ProxyRequest info = new ProxyRequest("http://jp.msn.com/", "URLReplace");
		
		System.out.println(info.executeGet());

		BufferedReader br =  new BufferedReader(new InputStreamReader(info.getResponseBody(),"UTF-8" ));
		String out = null;
		while((out = br.readLine())!= null){
			System.out.println(out);
		}
		for(Iterator it = info.getResponseHeaders().entrySet().iterator(); it.hasNext();){
			Map.Entry entry = (Map.Entry)it.next();
			System.out.println(entry.getKey() + ", " + entry.getValue());
		}
	}
	public static void testSearchResultFilter() throws Exception{
		
		ProxyRequest info = new ProxyRequest("http://www.google.co.jp/search?q=hage", "SearchResult");
		info.putRequestHeader("Accept-Charset","utf-8;q=0.7,*;q=0.7");
		info.putRequestHeader("msdportal-select","regexp="+URLEncoder.encode( ".*検索結果 約 <b>([0-9,]+)</b> 件中.*","UTF-8"));
		String encoding = "Shift_JIS";
		
		
		info.setFilterEncoding( encoding );
		System.out.println(info.executeGet());
		
		if( encoding == null )
			encoding = "UTF-8";
		BufferedReader br =  new BufferedReader(new InputStreamReader(info.getResponseBody(),encoding ));
		String out = null;
		while((out = br.readLine())!= null){
			System.out.println(out);
		}
		for(Iterator it = info.getResponseHeaders().entrySet().iterator(); it.hasNext();){
			Map.Entry entry = (Map.Entry)it.next();
			System.out.println(entry.getKey() + ", " + entry.getValue());
		}
	}
	
	public static void testRssFilter() throws Exception{
		ProxyRequest info = new ProxyRequest("http://pheedo.nikkeibp.co.jp/f/nikkeibp_news_flash", "RssReader");
		info.setPortalUid("admin");
		info.putRequestHeader("Connection", "keep-alive");
		info.putRequestHeader("_pageSize", "5");
		info.putRequestHeader("accept", "text/javascript, text/html, application/xml, text/xml");
		System.out.println(info.executeGet());
		Map map = info.getResponseHeaders();
		String lastModified = (String)map.get("Last-Modified");
		String etag = (String)map.get("Etag");
		System.out.println(lastModified + ": " + etag);
		info.putRequestHeader("If-Modified-Since", lastModified);
		info.putRequestHeader("If-None-Match", etag);
		System.out.println(info.executeGet());
				
		BufferedReader br =  new BufferedReader(new InputStreamReader(info.getResponseBody(),"UTF-8"));
		String out = null;
		while((out = br.readLine())!= null){
			System.out.println(out);
		}
	}

	public static void testMaximizeGadgetFilter() throws Exception{
		ProxyRequest info = new ProxyRequest("http://localhost:8080/gadget-examples/maximize2.xml", "MaximizeGadget");
		
		//System.out.println(info.executeGet());
		
		Map headers = info.getResponseHeaders();
		for( Iterator keys=headers.keySet().iterator();keys.hasNext();) {
			Object key = keys.next();
			System.out.println( key+": "+headers.get( key ));
		}
		
		BufferedReader br =  new BufferedReader(new InputStreamReader(info.getResponseBody(),"UTF-8"));
		
		int c = br.read();
		while( c != -1 ) {
			System.out.print((char)c);c=br.read();
		}
		
		br.close();
	}
	
	
}
