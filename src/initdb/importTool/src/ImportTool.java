
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.infoscoop.dao.model.Account;
import org.infoscoop.dao.model.Adminrole;
import org.infoscoop.dao.model.Forbiddenurls;
import org.infoscoop.dao.model.Gadget;
import org.infoscoop.dao.model.GadgetIcon;
import org.infoscoop.dao.model.HOLIDAYSPK;
import org.infoscoop.dao.model.Holidays;
import org.infoscoop.dao.model.I18NPK;
import org.infoscoop.dao.model.I18n;
import org.infoscoop.dao.model.I18nlocale;
import org.infoscoop.dao.model.OAuthCertificate;
import org.infoscoop.dao.model.Portaladmins;
import org.infoscoop.dao.model.Portallayout;
import org.infoscoop.dao.model.Properties;
import org.infoscoop.dao.model.Proxyconf;
import org.infoscoop.dao.model.Searchengine;
import org.infoscoop.dao.model.Siteaggregationmenu;
import org.infoscoop.dao.model.WidgetConf;
import org.infoscoop.initdb.dao.model.TABLAYOUTPK;
import org.infoscoop.initdb.dao.model.TabLayout;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVReader;

public class ImportTool {
	public static enum TABLES {
		I18N( new I18nFactory()),
		I18NLOCALE( new I18nLocaleFactory()),
		ADMINROLE( new AdminroleFactory()),
		PORTALADMINS( new PortalAdminsFactory()),
		PORTALLAYOUT( new PortalLayoutFactory()),
		PROPERTIES( new PropertiesFactory()),
		PROXYCONF( new ProxyConfFactory()),
		SEARCHENGINE( new SearchEngineFactory()),
		SITEAGGREGATIONMENU( new SiteAggregationMenuFactory()),
		TABLAYOUT( new TabLayoutFactory()),
		WIDGETCONF( new WidgetConfFactory()),
		HOLIDAYS( new HolidaysFactory()),
		FORBIDDENURLS( new ForbiddenUrlsFactory()),
		GADGET( new GadgetFactory()),
		ACCOUNT( new AccountFactory()),
		GADGETICON( new GadgetIconFactory()),
		OAUTH_CERTIFICATE( new OAuthCertificateFactory())
		;

		public static final Map<TABLES,String> DIRECTORY_MAP;
		static {
			String basedir = "data/";

			DIRECTORY_MAP = new HashMap<TABLES, String>();
			for( TABLES table : TABLES.values() )
				DIRECTORY_MAP.put( table,basedir+table.name().toLowerCase());
			DIRECTORY_MAP.put( TABLES.PROXYCONF,basedir+"proxyconfig");
			DIRECTORY_MAP.put( TABLES.WIDGETCONF,basedir+"widgetconfig");
			DIRECTORY_MAP.put( TABLES.I18NLOCALE,basedir+"i18nLocale");
			DIRECTORY_MAP.put( TABLES.ACCOUNT,basedir+"accounts");
		}

		private CSVBeanFactory<?> factory;

		private TABLES( CSVBeanFactory<?> factory ) {
			this.factory = factory;
		}

		public Object newBean( CSVField[] values ) {
			return factory.newBean( values );
		}
		
		public boolean isPKAutoIncrement(){
			return factory.isPKAutoIncrement();
		}
				
		public Object getUpdateObject(Session session, Object bean) {
			Criteria c = factory.createUniqueCriteria(session, bean);
			Object obj = c.uniqueResult();
			if(obj == null)
				return null;
			factory.setData(obj, bean);
			return obj;
		}
	}

	private static final ApplicationContext context = new ClassPathXmlApplicationContext(
			new String[] { "datasource.xml" });

	public static void main( String[] args ) throws Exception {
		ImportTool importTool = new ImportTool();

//		Set<TABLES> accepts = new HashSet<TABLES>( Arrays.asList( TABLES.values() ));
		List<TABLES> accepts = new ArrayList<TABLES>();
		String lang = null;

		if( args.length > 0 ) {
			for( int i=0;i<args.length;i++ ) {
				String arg = args[i].toUpperCase();

				TABLES table;
				try {
					table = TABLES.valueOf( arg );

					accepts.add( table );
				} catch( IllegalArgumentException ex ) {
					if( arg.equalsIgnoreCase("-lang") && i+1 < args.length ) {
						lang = args[++i];
					} else {
						System.out.println("unknown table name : "+args[i]+"");
						System.out.println("table names are :"+Arrays.toString( TABLES.values())+"");
						return;
					}
				}
			}
			System.out.println("import tables "+accepts );
		}

		if( accepts.size() == 0 ) {
			System.out.println("import ALL tables.");
			accepts.addAll( Arrays.asList( TABLES.values() ));
		}

		if( lang != null )
			System.out.println("lang set to ["+lang+"]");

		for( TABLES table : accepts ) {
			try {
				importTool.importTable( table,lang );
			} catch( IOException ex ) {
				System.out.println( ex.getMessage());
				ex.printStackTrace();

				return;
			}
		}
		System.out.println( accepts.size()+" tables imported");
	}

	private ImportTool() {
	}

	private void importTable( TABLES table,String lang ) throws IOException {
		System.out.println("import table "+table );

		ImportTable instance = new ImportTable( table,lang );

		SessionFactory sessionFactory = ( SessionFactory )context.getBean("sessionFactory");

		Collection<Object> beans = new ArrayList<Object>();
		long c = 0;
		for( CSVField[] values : instance.parseCSV() ){
			try{
				c++;
				beans.add( table.newBean( values ) );
			}catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Exception occuerd at line " + c + ". values=" + Arrays.toString(values));
				throw e;
			}
		}

		org.hibernate.Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();

			int n = 0;
			for( Object bean : beans ) {
				if(!table.isPKAutoIncrement())
					session.saveOrUpdate( bean );
				else{
					Object o = table.getUpdateObject(session, bean);
					if(o !=null)
						session.update(o);
					else{
						session.save(bean);
					}
				}
				n++;
			}

			transaction.commit();
			System.out.println( n +" record imported");
		} catch( RuntimeException ex ) {
			if( transaction != null )
				transaction.rollback();

			throw ex;
		} finally {
			if( session != null )
				session.close();
		}
	}
}

class ImportTable {
	private File importCsvFile;
	private File langDir;
	private File baseDir;

	public ImportTable( ImportTool.TABLES table,String lang ) throws IOException {
		String base = ImportTool.TABLES.DIRECTORY_MAP.get( table );
		if( lang != null )
			importCsvFile = new File( base+"/"+lang+"/import.csv");

		if( importCsvFile == null || !importCsvFile.exists() )
			importCsvFile = new File( base+"/import.csv");

		if( !importCsvFile.exists() )
			throw new FileNotFoundException("["+base+"/import.csv] File is not found.");

		baseDir = importCsvFile.getParentFile();

		if( lang != null )
			langDir = new File( baseDir,lang );
	}
	public List<CSVField[]> parseCSV() throws IOException {
		BufferedReader r = new BufferedReader( new InputStreamReader(
				new FileInputStream( importCsvFile ),"UTF-8"));

		r.mark(1);
		int hoge = r.read();
		if( hoge != 65279 ) {
			r.reset();
		}

		List<CSVField[]> result = new ArrayList<CSVField[]>();

		CSVReader csv = new CSVReader( r );
		String[] fields;
		while(( fields = csv.readNext() ) != null ) {
			if(fields.length == 1 && "".equals(fields[0])){
				continue;
			}

			CSVField[] csvFields = new CSVField[ fields.length ];
			for( int i=0;i<fields.length;i++ ) {
				String field = fields[i];

				String externalFilePath = null;

				Pattern pattern = Pattern.compile("\\s*<LOB FILE='(.+)' />");
				Matcher matcher = pattern.matcher( field );
				if( matcher.matches())
					externalFilePath = matcher.group(1);

				Pattern lobPattern = Pattern.compile("(.+\\.lob)");
				Matcher lobMatcher = lobPattern.matcher( field );
				if( lobMatcher.matches() )
					externalFilePath = lobMatcher.group(1);

				if( externalFilePath != null ) {
					csvFields[i] = getExternalFile( externalFilePath );
				} else {
					csvFields[i] = new CSVField( fields[i] );
				}
			}

			result.add( csvFields );
		}

		return result;
	}

	public CSVField getExternalFile( String source ) throws IOException {
		File dataFile = findFile( source );

		InputStream in = new BufferedInputStream( new FileInputStream( dataFile ) );
		byte[] buf = new byte[5120];
		int reads = 0;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while( ( reads = in.read( buf ) ) >= 0 ) {
			baos.write( buf,0,reads );
		}
		in.close();
		baos.close();

		return new CSVField( baos.toByteArray() );
	}
	public File findFile( String path ) throws IOException {
		File file =  new File( langDir,path );
		if( !file.exists())
			file = new File( baseDir,path );

		if( file.exists()) {
//			System.out.println("File find at: "+file.getAbsolutePath() );
		} else {
//			System.out.println("File not found: "+file.getAbsolutePath() );
		}

		return file;
	}
}

class CSVField {
	private byte[] bytes;
	private String string;

	public CSVField( String string ) {
		this.string = string;
	}
	public CSVField( byte[] bytes ) {
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		if( bytes == null ) {
			try {
				bytes = string.getBytes("UTF-8");
			} catch( UnsupportedEncodingException ex ) {
				throw new RuntimeException( ex );
			}
		}

		return bytes;
	}
	public String toString() {
		if( string == null ) {
			try {
				string = new String( bytes,"UTF-8");
			} catch( UnsupportedEncodingException ex ) {
				throw new RuntimeException( ex );
			}
		}

		return string;
	}

	public Integer toInt() {
		try {
			return new Integer( toString() );
		} catch( NumberFormatException ex ) {
			return null;
		}
	}
}

abstract class CSVBeanFactory<T> {
	abstract public T newBean( CSVField[] values );
	public void setData(Object obj, Object bean) {		
	}
	
	public boolean isPKAutoIncrement() {
		return false;
	}
	public Criteria createUniqueCriteria(org.hibernate.Session session, Object obj){
		return null;
	}
}

// type,id,country,lang,message
class I18nFactory extends CSVBeanFactory<I18n>{
	public I18n newBean( CSVField[] values ) {
		I18NPK pk = new I18NPK();
		pk.setType( values[0].toString() );
		pk.setId( values[1].toString() );
		pk.setCountry( values[2].toString() );
		pk.setLang( values[3].toString() );

		I18n i18n = new I18n( pk );
		if( "".equals( values[4].toString() ) )
			values[4] = new CSVField("-");
		i18n.setMessage( values[4].toString() );

		return i18n;
	}
}

// type,country,lang
class I18nLocaleFactory extends CSVBeanFactory<I18nlocale> {
	public I18nlocale newBean( CSVField[] values ) {
		I18nlocale i18nLocale = new I18nlocale();
		i18nLocale.setType( values[1].toString() );
		i18nLocale.setCountry( values[2].toString() );
		i18nLocale.setLang( values[3].toString() );

		return i18nLocale;
	}

	@Override
	public Criteria createUniqueCriteria(Session session, Object obj) {
		I18nlocale i18nlocale = (I18nlocale)obj;
		Criteria c = session.createCriteria(I18nlocale.class);
		c.add(Expression.conjunction()
		    	.add( Expression.eq(I18nlocale.PROP_TYPE, i18nlocale.getType() ) )
		    	.add( Expression.eq(I18nlocale.PROP_COUNTRY, i18nlocale.getCountry() ) )
		    	.add( Expression.eq(I18nlocale.PROP_LANG, i18nlocale.getLang() ) )
		    	);

		return c;
	}

	@Override
	public boolean isPKAutoIncrement() {
		return true;
	}

	@Override
	public void setData(Object obj, Object bean) {
		I18nlocale _obj = (I18nlocale)obj;
		I18nlocale _bean = (I18nlocale)bean;
		_obj.setType(_bean.getType());
		_obj.setLang(_bean.getLang());
		_obj.setCountry(_bean.getCountry());
	}
	
}

class AdminroleFactory extends CSVBeanFactory<Adminrole> {
	public Adminrole newBean( CSVField[] values ) {
		Adminrole adminrole = new Adminrole();
		adminrole.setRoleid( values[1].toString() );
		adminrole.setName(values[2].toString());
		adminrole.setPermission( values[3].toString() );
		adminrole.setAllowdelete( values[4].toInt());

		return adminrole;
	}
	
	public void setData(Object obj, Object bean) {
		Adminrole _obj = (Adminrole)obj;
		Adminrole _bean = (Adminrole)obj;
		_obj.setRoleid(_bean.getRoleid());
		_obj.setName(_bean.getName());
		_obj.setPermission(_bean.getPermission());
		_obj.setAllowdelete(_bean.getAllowdelete());
	}
	
	public boolean isPKAutoIncrement() {
		return true;
	}
	public Criteria createUniqueCriteria(org.hibernate.Session session, Object obj){
		Adminrole adminrole = (Adminrole)obj;
		Criteria c = session.createCriteria(Adminrole.class);
		c.add(Expression.eq(Adminrole.PROP_ROLEID,  adminrole.getRoleid()));

		return c;
	}
}

// uid
class PortalAdminsFactory extends CSVBeanFactory<Portaladmins> {
	public Portaladmins newBean( CSVField[] values ) {
		Portaladmins portalAdmin = new Portaladmins();
		portalAdmin.setUid(values[1].toString());
		portalAdmin.setRoleid( values[2].toString() );

		return portalAdmin;
	}

	public void setData(Object obj, Object bean) {
		Portaladmins _obj = (Portaladmins)obj;
		Portaladmins _bean = (Portaladmins)obj;
		_obj.setRoleid(_bean.getRoleid());
		_obj.setUid(_bean.getUid());
		_obj.setRoleid(_bean.getRoleid());
	}
	
	public boolean isPKAutoIncrement() {
		return true;
	}
	public Criteria createUniqueCriteria(org.hibernate.Session session, Object obj){
		Portaladmins portaladmins = (Portaladmins)obj;
		Criteria c = session.createCriteria(Portaladmins.class);
		c.add(Expression.eq(Portaladmins.PROP_UID,  portaladmins.getUid()));

		return c;
	}
}

// name,layout
class PortalLayoutFactory extends CSVBeanFactory<Portallayout> {
	public Portallayout newBean( CSVField[] values ) {
		Portallayout portalLayout = new Portallayout();
		portalLayout.setName( values[0].toString() );
		portalLayout.setLayout( values[1].toString() );

		return portalLayout;
	}
}

// id,value,description
class PropertiesFactory extends CSVBeanFactory<Properties> {
	private RSAPrivateKey privateKey;

	public PropertiesFactory() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize( 512, new SecureRandom());
			KeyPair keyPair = keyPairGenerator.generateKeyPair();

			privateKey = ( RSAPrivateKey )keyPair.getPrivate();
		} catch( Exception ex ) {
			throw new RuntimeException( ex );
		}
	}
	public Properties newBean( CSVField[] values ) {
		Properties property = new Properties();
		property.setId( values[0].toString() );
		property.setCategory( values[1].toString() );
		property.setAdvanced( values[2].toInt() );

		if("keyManagerModulus".equals( property.getId())) {
			property.setValue( privateKey.getModulus().toString(16));
		} else if("keyManagerExponent".equals( property.getId())) {
			property.setValue( privateKey.getPrivateExponent().toString(16));
		} else {
			property.setValue( values[3].toString() );
		}
		property.setDatatype( values[4].toString() );
		property.setEnumvalue( values[5].toString() );
		property.setRequired( values[6].toInt() );
		property.setRegex( values[7].toString() );
		property.setRegexmsg( values[8].toString() );

		return property;
	}
}

// temp,data,lastmodified

class ProxyConfFactory extends CSVBeanFactory<Proxyconf> {
	public Proxyconf newBean( CSVField[] values ) {
		Proxyconf proxyConf = new Proxyconf();
		proxyConf.setTemp( values[0].toInt() );
		proxyConf.setData( values[1].toString() );

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SS");
		try {
			proxyConf.setLastmodified( format.parse( values[2].toString() ) );
		} catch( ParseException ex ) {
			throw new RuntimeException( ex );
		}

		return proxyConf;
	}
}

// temp,data

class SearchEngineFactory extends CSVBeanFactory<Searchengine> {
	public Searchengine newBean( CSVField[] values ) {
		Searchengine searchEngine = new Searchengine();
		searchEngine.setTemp( values[0].toInt() );
		searchEngine.setData( values[1].toString() );

		return searchEngine;
	}
}

// type,temp,data

class SiteAggregationMenuFactory extends CSVBeanFactory<Siteaggregationmenu> {
	public Siteaggregationmenu newBean( CSVField[] values ) {
		Siteaggregationmenu siteAggregationMenu = new Siteaggregationmenu( values[0].toString() );
		siteAggregationMenu.setData( values[1].toString() );

		return siteAggregationMenu;
	}
}

// tabId,roleOrder,role,rolename,principalType,defaultUid,widgets,layout,widgetsLastmodified,tabNumber,deleteFlag

class TabLayoutFactory extends CSVBeanFactory<TabLayout> {
	public TabLayout newBean( CSVField[] values ) {
		TABLAYOUTPK pk = new TABLAYOUTPK();
		pk.setTabid( values[0].toString() );
		pk.setRoleorder( values[1].toInt() );
		pk.setTemp( values[11].toInt() );

		TabLayout tabLayout = new TabLayout( pk );
		tabLayout.setRole( values[2].toString() );
		tabLayout.setRolename( values[3].toString() );
		tabLayout.setPrincipaltype( values[4].toString() );
		tabLayout.setDefaultuid( values[5].toString() );
		tabLayout.setWidgets( values[6].toString() );
		tabLayout.setLayout( values[7].toString() );
		if( "".equals( values[8].toString() ) )
			values[8] = new CSVField("-");

		tabLayout.setWidgetslastmodified( values[8].toString() );
		tabLayout.setTabnumber( values[9].toInt() );
		tabLayout.setDeleteflag( values[10].toInt() );
//		tabLayout.setWorkinguid( values[12].toString() );

		return tabLayout;
	}
}

// type,data

class WidgetConfFactory extends CSVBeanFactory<WidgetConf> {
	public WidgetConf newBean( CSVField[] values ) {
		WidgetConf widgetConf = new WidgetConf();
		widgetConf.setType( values[0].toString() );
		widgetConf.setData( values[1].toString() );

		return widgetConf;
	}
}

class HolidaysFactory extends CSVBeanFactory<Holidays> {
	public Holidays newBean( CSVField[] values ) {
		HOLIDAYSPK key = new HOLIDAYSPK( values[0].toString(),values[1].toString() );
		Holidays holiday = new Holidays( key );
		holiday.setData( values[2].toString() );

		return holiday;
	}
}
class ForbiddenUrlsFactory extends CSVBeanFactory<Forbiddenurls> {
	public Forbiddenurls newBean( CSVField[] values ) {
		Forbiddenurls forbiddenUrl = new Forbiddenurls();
		forbiddenUrl.setUrl( values[1].toString() );

		return forbiddenUrl;
	}
}

class GadgetFactory extends CSVBeanFactory<Gadget> {
	public Gadget newBean( CSVField[] values ) {
		Gadget gadget = new Gadget();
		gadget.setType( values[1].toString());
		gadget.setPath( values[2].toString());
		gadget.setName( values[3].toString());
		gadget.setData( values[4].getBytes() );
		gadget.setLastmodified( new Date() );

		return gadget;
	}
	
	@Override
	public boolean isPKAutoIncrement() {
		return true;
	}

	public Criteria createUniqueCriteria(org.hibernate.Session session, Object obj){
		Gadget gadget = (Gadget)obj;
		Criteria c = session.createCriteria(Gadget.class);
		c.add(Expression.conjunction()
		    	.add( Expression.eq(Gadget.PROP_TYPE, gadget.getType() ) )
		    	.add( Expression.eq(Gadget.PROP_PATH, gadget.getPath() ) )
		    	.add( Expression.eq(Gadget.PROP_NAME, gadget.getName() ) )
		    	);

		return c;
		
	}
	
	@Override
	public void setData(Object obj, Object bean) {
		Gadget _obj = (Gadget)obj;
		Gadget _bean = (Gadget)bean;
		_obj.setName(_bean.getName());
		_obj.setType(_bean.getType());
		_obj.setPath(_bean.getPath());
		_obj.setData(_bean.getData());
		_obj.setLastmodified(new Date() );
	}
}

class AccountFactory extends CSVBeanFactory<Account> {
	public Account newBean( CSVField[] values ) {
		Account account = new Account();
		account.setUid( values[0].toString() );
		account.setName( values[1].toString() );
		account.setPasswordPlainText( values[2].toString() );

		return account;
	}
}

class GadgetIconFactory extends CSVBeanFactory<GadgetIcon> {
	public GadgetIcon newBean( CSVField[] values ) {
		GadgetIcon gadgetIcon = new GadgetIcon();
		gadgetIcon.setType( values[0].toString() );
		gadgetIcon.setUrl( values[1].toString() );

		return gadgetIcon;
	}
}

class OAuthCertificateFactory extends CSVBeanFactory<OAuthCertificate> {
	public OAuthCertificate newBean( CSVField[] values ) {
		OAuthCertificate certificate = new OAuthCertificate();
		certificate.setConsumerKey(values[0].toString());
		certificate.setPrivateKey(values[1].toString());
		certificate.setCertificate(values[2].toString());

		return certificate;
	}
}