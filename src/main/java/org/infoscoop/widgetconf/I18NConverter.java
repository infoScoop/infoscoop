package org.infoscoop.widgetconf;

import java.util.*;
import java.util.regex.Matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class to substitute a globalization message.
 * @author a-kimura
 *
 */
public class I18NConverter {
	private static Log logger = LogFactory.getLog( I18NConverter.class );
	
	private static enum BidiKey {
		START_EDGE,
		END_EDGE,
		DIR,
		REVERSE_DIR
	}
	
	private Map replaceMap = new HashMap();
	
	private MessageBundle.Direction direction;
	private Map<BidiKey,String> bidiReplaceMap = new HashMap<BidiKey,String>();
	
	public I18NConverter( Locale locale,Collection<MessageBundle> resourceBundles ) {
		List<MessageBundle> bundles = new ArrayList<MessageBundle>();
		for( MessageBundle bundle : resourceBundles ) {
			String lang = bundle.getLocale().getLanguage();
			String country = bundle.getLocale().getCountry();
			
			if( ( lang.equalsIgnoreCase( locale.getLanguage()) || lang.equalsIgnoreCase("ALL"))&&
				( country.equalsIgnoreCase( locale.getCountry()) || country.equalsIgnoreCase("ALL")))
				bundles.add( bundle );
		}
		
		if( bundles.size() == 0 && resourceBundles.size() > 0 )
			bundles.add( resourceBundles.toArray( new MessageBundle[]{})[0] );
		
		Collections.sort( bundles );
		
		MessageBundle.Direction direction = null;
		this.replaceMap = new HashMap<String,String>();
		for( MessageBundle bundle : bundles ) {
			try {
				replaceMap.putAll( bundle.getMessages());
				
				direction = bundle.getDirection();
			} catch( Exception ex ) {
				logger.error("illegal message bundle",ex );
			}
		}
		
		this.direction = direction;
		bidiReplaceMap.putAll( getBidiReplaces( direction ));
	}
	
	public String replace(String str) {
		if (this.replaceMap == null)
			return str;
		
		for (Iterator it = this.replaceMap.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			String value = (String) this.replaceMap.get(key);
			
			str = str.replaceAll("__MSG_" + key + "__",Matcher.quoteReplacement( value ));
		}
		
		for( BidiKey key : bidiReplaceMap.keySet() )
			str = str.replaceAll("__BIDI_" +key +"__", bidiReplaceMap.get( key ));
		
		return str;
	}
	
	public Map getMsgs() {
		return this.replaceMap;
	}
	
	private static Map<BidiKey,String> getBidiReplaces( MessageBundle.Direction direction ) {
		Map<BidiKey,String> replaces = new HashMap<BidiKey,String>();
		
		boolean l = ( direction == MessageBundle.Direction.LTR );
		replaces.put( BidiKey.START_EDGE ,( l ? "left":"right") );
		replaces.put( BidiKey.END_EDGE ,( l ? "right":"left") );
		replaces.put( BidiKey.DIR ,( l ? "ltr":"rtl") );
		replaces.put( BidiKey.REVERSE_DIR ,( l? "rtl":"ltr") );
		
		return replaces;
	}
	
	public MessageBundle.Direction getDirection() {
		return direction;
	}
}
