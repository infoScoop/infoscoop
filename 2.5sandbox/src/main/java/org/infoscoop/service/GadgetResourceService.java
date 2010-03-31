package org.infoscoop.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.serializer.dom3.LSSerializerImpl;
import org.apache.xpath.XPathAPI;
import org.infoscoop.dao.GadgetDAO;
import org.infoscoop.dao.GadgetIconDAO;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.util.NoOpEntityResolver;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.util.XmlUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSSerializer;

public class GadgetResourceService {
	private static Log log = LogFactory.getLog(GadgetResourceService.class);
	
	public static Pattern REGEX_NAME = Pattern.compile("^[\\w-.]+");
	public static Pattern REGEX_PATH = Pattern.compile("^(?:/[\\w-.]*)+");
	
	private GadgetDAO gadgetDAO;
	private GadgetIconDAO gadgetIconDAO;

	public static GadgetResourceService getHandle() {
		return (GadgetResourceService) SpringUtil.getBean("GadgetResourceService");
	}

	public GadgetResourceService(){
	}

	public void setGadgetDAO(GadgetDAO gadgetDAO) {
		this.gadgetDAO = gadgetDAO;
	}

	public void setGadgetIconDAO(GadgetIconDAO gadgetIconDAO) {
		this.gadgetIconDAO = gadgetIconDAO;
	}
	
	public Gadget getResource( String type,String path,String name ) {
		return gadgetDAO.select( type,path,name );
	}
	public byte[] selectResource( String type,String path,String name ) {
		Gadget gadget = gadgetDAO.select( type,path,name );
		if( gadget == null )
			return null;

		return gadget.getData();
	}
	public String selectResource( String type,String path,String name,String encoding ) {
		try {
			return new String( selectResource( type,path,name ),encoding );
		} catch( UnsupportedEncodingException ex ) {
			throw new RuntimeException( ex );
		}
	}
	public Collection<JSONObject> getResourceListJson( String type ) {
		Collection<JSONObject> resources = new ArrayList<JSONObject>();
		for( Gadget resource : gadgetDAO.list( type ) ) {
			JSONObject rMap = new JSONObject();

			try {
				rMap.put("path",resource.getPath() );
				rMap.put("name",resource.getName() );
			} catch( JSONException ex ) {
				throw new RuntimeException( ex );
			}

			resources.add( rMap );
		}

		return resources;
	}

	public void deleteResource( String type,String path,String name ) {
		List<Gadget> list = gadgetDAO.list( type,path );

		if( gadgetDAO.delete( type, path, name ) && list.size() == 1 )
			gadgetDAO._insert( type,path,"",new byte[0]);
		
		log.info("delete gadget resource ["+type+"] "+path+name );
	}

	public void insertResource( String type,String path,String name,byte[] data ) throws GadgetResourceException {
		if( !REGEX_NAME.matcher( type ).matches() )
			throw new GadgetResourceException( 
					"invalid type: "+type,
					"ams_gadgetResourceInvalidType" );
		
		if( !REGEX_NAME.matcher( name ).matches() )
			throw new GadgetResourceException( 
					"invalid name:" +name,
					"ams_gadgetResourceInvalidName" );
		
		if( !REGEX_PATH.matcher( path ).matches() )
			throw new GadgetResourceException( 
					"invalid path: "+path,
					"ams_gadgetResourceInvalidPath" );
		
		if( name.getBytes().length > 255 )
			throw new GadgetResourceException( 
					"too long name,name must be under 255 byte: "+name,
					"ams_gadgetResourceTooLongName");
		
		if( path.getBytes().length > 512 )
			throw new GadgetResourceException( 
					"too long path,path must be under 512 byte: "+path,
					"ams_gadgetResourceTooLongPath");
		
		if( gadgetDAO.select( type,path,name ) != null )
			throw new GadgetResourceException( 
					"already existed resource at ["+path+"/"+name+"]",
					"ams_gadgetResourceNameAlreadyExisted");
		
		gadgetDAO.insert( type,path,name,data );
		
		log.info("insert gadget resource ["+type+"] "+path+name );
	}
	
	public void updateTextResource( String type,String path,String name,String data ) {
		try {
			updateResource( type, path, name, data.getBytes("UTF-8"));
		} catch( UnsupportedEncodingException ex ) {
			// ignore
		}
	}
	public void updateResource( String type,String path,String name,byte[] data ) throws GadgetResourceException {
		if( name.equals( type+".xml") && "/".equals( path ))
			validateGadgetData( type,path,name, data );
		
		gadgetDAO.update( type,path,name,data );
		
		log.info("update gadget resource ["+type+"] "+path+name );
	}
	public void updateResources( String type,InputStream in ) throws GadgetResourceException {
		gadgetDAO.deleteType( type );
		//gadgetIconDAO.deleteByType(type);

		byte[] data;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[5120];
			int reads = 0;
			while( !(( reads = in.read( buf )) < 0 ) )
				baos.write( buf,0,reads );
			
			data = baos.toByteArray();
		} catch( IOException ex ) {
			throw new GadgetResourceException( ex );
		}
		
		data = validateGadgetData( type,"/",type+".xml", data );
		
		insertResource( type,"/",type+".xml",data );
	}
	public void updateResources( String type,String moduleName,ZipInputStream zin ) throws GadgetResourceException {
		gadgetDAO.deleteType( type );
		//gadgetIconDAO.deleteByType(type);

		boolean findModule = false;
		try {
			ZipEntry entry;
			while( ( entry = getNextEntry( zin ) ) != null ) {
				if( !entry.isDirectory() ) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buf = new byte[5120];
					int reads = 0;
					while( !(( reads = zin.read( buf )) < 0 ) )
						baos.write( buf,0,reads );
					byte[] data = baos.toByteArray();
					
					String zipName = entry.getName();
					String path = "/"+zipName.substring( 0,zipName.lastIndexOf("/") +1 );
					String name = zipName.substring( path.length() -1 );
					
					try {
						if( path.length() > 1 && path.endsWith("/"))
							path = path.substring( 0,path.length() -1 );
						
						if("/".equals( path ) && ( moduleName+".xml").equalsIgnoreCase( name )) {
							findModule = true;
							name = type+".xml";
	
							data = validateGadgetData( type,path,name,data );
						}
						
						insertResource( type,path,name,data );
					} catch( Exception ex ) {
						throw new GadgetResourceArchiveException( path,name, 
								"It is an entry of an invalid archive.  error at ["+path+"/"+name+"]"+ex.getMessage(),
								"ams_gadgetResourceInvalidArchiveEntry",ex );
					}
				}

				zin.closeEntry();
			}
		} catch( GadgetResourceException ex ) {
			throw ex;
		} catch( Exception ex ) {
			log.error("",ex );
			
			throw new GadgetResourceException( 
					"It is an invalid archive.",
					"ams_gadgetResourceInvalidArchive");
		}

		if( !findModule )
			throw new GadgetResourceException( 
					"Not found gadget module( /"+moduleName+".xml"+ " ) in an uploaded archive.",
					"ams_gadgetResourceNotFoundGadgetModule" );
	}
	
	private ZipEntry getNextEntry( ZipInputStream zin ) throws IOException {
		try {
			return zin.getNextEntry();
		} catch( IllegalArgumentException ex ) {
			throw new GadgetResourceException(
				"invalid name",
				"ams_gadgetResourceInvalidName" );
		}
	}

	private byte[] validateGadgetData( String type,String path,String name,byte[] data ) {
		Document gadgetDoc;
		
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setValidating(false);
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			builder.setEntityResolver( NoOpEntityResolver.getInstance());
			gadgetDoc = builder.parse( new ByteArrayInputStream( data ) );
			
			Element moduleNode = gadgetDoc.getDocumentElement();
			NodeList contentNodes = moduleNode.getElementsByTagName("Content");
			//The preparations for Locale tag
			NodeList modulePrefsList = gadgetDoc.getElementsByTagName("ModulePrefs");
			
			if(!"Module".equals( moduleNode.getTagName()) ||
					contentNodes == null || contentNodes.getLength() == 0 ||
					modulePrefsList == null || modulePrefsList.getLength() == 0 ) {
				throw new GadgetResourceException(
						"It is an invalid gadget module. ",
						"ams_gadgetResourceInvalidGadgetModule");
			}
			
			Element iconElm = (Element) XPathAPI.selectSingleNode(gadgetDoc,
					"/Module/ModulePrefs/Icon");
			if (iconElm != null) {
				String iconUrl = iconElm.getTextContent();
				for (int i = 0; i < modulePrefsList.getLength(); i++) {
					Element modulePrefs = (Element) modulePrefsList.item(i);
					if (modulePrefs.hasAttribute("resource_url")) {
						iconUrl = modulePrefs.getAttribute("resource_url")
								+ iconUrl;
						break;
					}
				}
				gadgetIconDAO.insertUpdate(type, iconUrl);
			} else {
				gadgetIconDAO.insertUpdate(type, "");
			}
			
			return XmlUtil.dom2String( gadgetDoc ).getBytes("UTF-8");
		} catch( GadgetResourceException ex ) {
			throw ex;
		} catch( Exception ex ) {
			throw new GadgetResourceException(
					"It is an invalid gadget module. ",
					"ams_gadgetResourceInvalidGadgetModule",ex );
		}
	}

	public byte[] selectResourcesZip( String type ) throws GadgetResourceException {
		Map<String,Object> tree = new TreeMap<String,Object>();
		for( Gadget gadget : gadgetDAO.list( type ) ) {
			Stack<String> path = new Stack<String>();
			List<String> pl = Arrays.asList( gadget.getPath().substring(1).split("/"));
			Collections.reverse( pl );
			path.addAll( pl );

			putResource( tree,path,gadget.getName(),gadget.getData() );
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			ZipOutputStream zout = new ZipOutputStream( baos );
			popResource( tree,"",zout );
			zout.close();
		} catch( IOException ex ) {
			throw new GadgetResourceException( ex );
		}
		
		return baos.toByteArray();
	}

	private static void putResource( Map<String,Object> tree,Stack<String> path,
			String name,byte[] data ) {
		if( path.size() > 0 ) {
			String pathSeg = "#"+path.pop();
			if( !tree.containsKey( pathSeg ))
				tree.put( pathSeg,new TreeMap<String,Object>());

			putResource(( Map<String,Object>)tree.get( pathSeg ),path,name,data );
		} else if( name != null ){
			tree.put( name,data );
		}
	}
	private static void popResource( Map<String,Object> tree,String path,ZipOutputStream zout ) throws IOException {
		if( path.equals("/"))
			path = "";

		for( String key : tree.keySet() ) {
			Object value = tree.get( key );
			if( key.startsWith("#"))
				key = key.substring(1);

			ZipEntry entry = new ZipEntry( path +key );
			if( value instanceof Map ) {
				entry.setMethod( ZipEntry.STORED );
				entry.setSize(0);
				entry.setCrc(0);

//				zout.putNextEntry( entry );
				popResource( ( Map<String,Object>)value,path +key+"/",zout );
//				zout.closeEntry();
			} else {
				byte[] data = ( byte[] )tree.get( key );
				entry.setSize( data.length );

				zout.putNextEntry( entry );
				zout.write( data );
				zout.closeEntry();
			}
		}
	}

	public static void main( String[] args ) throws Exception {
		String longName = "";
		for( int i=0;i<510;i++ )
			longName += "a";

		ZipOutputStream zout = new ZipOutputStream( new FileOutputStream("test.zip"));
		ZipEntry ze = new ZipEntry( longName +"/a");
		zout.putNextEntry( ze );
		zout.write("test".getBytes());
		zout.closeEntry();
		zout.close();
	}

	public static class GadgetResourceException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public GadgetResourceException() {
			this( null );
		}
		public GadgetResourceException( Throwable cause ) {
			this("unexcepted error","ams_gadgetResourceUnknownError",cause );
		}
		public GadgetResourceException( String message,String id ) {
			this( message,id,null );
		}
		public GadgetResourceException( String message,String id,Throwable cause ) {
			super( message,cause );
			
			this.id = id;
		}
		
		private String id;
		public String getId() { return id; }
		
		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			try {
				json.put("message",getId() );
			} catch( JSONException ex ) {
				// ignore
			}
			
			return json;
		}
	}
	public static class GadgetResourceArchiveException extends GadgetResourceException {
		private static final long serialVersionUID = 1L;

		private String path;
		private String name;
		

		public GadgetResourceArchiveException( String path,String name,
				String message,String id,Throwable cause ) {
			super( message,id,cause );
			
			this.path = path;
			this.name = name;
		}
		
		public String getPath() { return path; }
		public String getName() { return name; }
		
		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			try {
				json.put("message",getId() );
				json.put("path",path );
				json.put("name",name );
				
				Throwable cause = getCause();
				if( cause != null && cause instanceof GadgetResourceException )
					json.put("cause",(( GadgetResourceException )cause ).getId());
			} catch( JSONException ex ) {
				// ignore
			}
			
			return json;
		}
	}
}
