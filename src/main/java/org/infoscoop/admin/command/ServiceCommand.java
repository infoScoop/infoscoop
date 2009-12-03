package org.infoscoop.admin.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.infoscoop.service.HolidaysService;
import org.infoscoop.util.SpringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceCommand extends CommandSupport {
	public static void main( String[] args ) throws Exception {
		HolidaysService service = ( HolidaysService )SpringUtil.getBean("HolidaysService");
		ServiceCommand commandSupport = new ServiceCommand();
		commandSupport.setService( service );
		
		JSONArray arguments = new JSONArray();
		arguments.put("ALL");
		arguments.put("ALL");
		
		CommandResponse response = commandSupport.execute("getHolidayData",
				toJavaObject( arguments ).toArray() );
		System.out.println( response.getResponseBody());
	}
	
	protected Object service;
	
	private Properties notPermittedPatterns = null;

	private Class serviceClass;
	private Map serviceMethods;
	
	public Properties getNotPermittedPatterns() {
		return this.notPermittedPatterns;
	}

	public void setNotPermittedPatterns(Properties notPermittedPatterns) {
		this.notPermittedPatterns = notPermittedPatterns;
	}
	public Object getService() {
		return service;
	}
	
	public void setService( Object service ) {
		if( service instanceof String )
			service = SpringUtil.getBean(( String )service );
		
		this.service = service;
		serviceClass = service.getClass();
		
		serviceMethods = new HashMap();
		Method[] methods = serviceClass.getMethods();
		for( int i=0;i<methods.length;i++ ) {
			Method method = methods[i];
			String key = method.getName();
			if( !serviceMethods.containsKey( key ))
				serviceMethods.put( key,new ArrayList() );
			
			(( Collection )serviceMethods.get( key )).add( method );
		}
	}
	public CommandResponse execute( String commandName,HttpServletRequest req,
			HttpServletResponse resp ) throws Exception {
		String contentString = getRequestContentString( req );
		Object[] arguments;
		try {
			arguments = toJavaObject( new JSONArray( contentString )).toArray();
		} catch( JSONException ex ) {
			arguments = new Object[0];
		}
		
		return execute( commandName,arguments );
	}
	private CommandResponse execute( String commandName,Object[] arguments ) throws Exception  {
		//DEBUG
		if("methods".equals( commandName )) {
			return new CommandResponse( true,serviceMethods.keySet().toString());
		}
		
		Collection methods = ( Collection )serviceMethods.get( commandName );
		if( methods == null ) {
			log.error( commandName+" not in "+serviceMethods.keySet());
			throw new UnsupportedOperationException( commandName );
		}
		
		for( Iterator ite=methods.iterator();ite.hasNext();) {
			Method method = ( Method )ite.next();
			if( method.getParameterTypes().length == arguments.length ) {
				Object result = null;
				try {
					result = method.invoke( service,arguments );
				} catch( InvocationTargetException ex ) {
					throw ( Exception )ex.getCause();
				} catch( IllegalArgumentException ex ) {
					throw new Exception(Arrays.asList( method.getParameterTypes())+
							" called by "+Arrays.asList( arguments )+"("+Arrays.asList( getTypes( arguments ))+")", ex);
				} catch( ClassCastException ex ) {
					throw new Exception(ex);
				}
				
				if( result == null ) {
					return new CommandResponse( true,"");
				} else if( result instanceof Collection ) {
					result = new JSONArray( ( Collection )result ).toString();
				} else if( result instanceof Map ) {
					result = new JSONObject( ( Map )result );
				}
				
				return new CommandResponse( true,result.toString() );
			}
		}
		
		throw new IllegalArgumentException("illegal number of arguments ["+arguments.length+"]");
	}
	
	private static Class[] getTypes( Object[] objects ) {
		Class[] types = new Class[ objects.length ];
		for( int i=0;i<objects.length;i++ )
			types[i] = objects[i].getClass();
		
		return types;
	}
	
	protected static String getRequestContentString( HttpServletRequest req ) throws IOException {
		BufferedReader reader = new BufferedReader( new InputStreamReader(
				req.getInputStream(),Charset.forName("UTF-8") ));
		StringBuffer buf = new StringBuffer();
		String line = null;
		
		while(( line = reader.readLine() ) != null )
			buf.append( line ).append("\r\n");
		
		String content = buf.toString();
		if( content.length() > 0 && content.charAt(0) == '%')
			content = URLDecoder.decode( content.substring(1),"UTF-8");
		
		return content;
	}
	
	private static Object toJavaObject( Object o ) throws JSONException{
		if( o instanceof JSONArray ) {
			return toJavaObject( ( JSONArray )o );
		} else if( o instanceof JSONObject ) {
			
			return toJavaObject( ( JSONObject )o );
		} else if( o.equals( JSONObject.NULL ) || o == JSONObject.NULL ) {
			return null;
		}
		
		return o;
	}
	private static Collection toJavaObject( JSONArray jsonArray ) throws JSONException {
		Collection array = new ArrayList();
		for( int i=0;i<jsonArray.length();i++ )
			array.add( toJavaObject( jsonArray.get( i )));
		
		return array;
	}
	private static Map toJavaObject( JSONObject jsonObject ) throws JSONException {
		Map map = new HashMap();
		for( Iterator keys=jsonObject.keys();keys.hasNext();) {
			String key = ( String )keys.next();
			
			map.put(  key,toJavaObject( jsonObject.get( key ) ) );
		}
		
		return map;
	}
}