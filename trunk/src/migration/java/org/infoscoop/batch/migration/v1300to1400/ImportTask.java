package org.infoscoop.batch.migration.v1300to1400;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.infoscoop.batch.migration.SQLTask;

import au.com.bytecode.opencsv.CSVReader;


public class ImportTask extends Task {
	private String source;
	public void setSource( String source ) {
		this.source = source;
	}
	
	private Class workerClass;
	public void setBeanFactory( String workerClassName ) {
		try {
			this.workerClass = Class.forName( workerClassName );
		} catch( ClassNotFoundException ex ) {
			throw new RuntimeException( ex );
		}
	}
	
	private boolean useTransaction;
	public void setUseTransaction( boolean useTransaction ) {
		this.useTransaction = useTransaction;
	}
	
	public void execute() throws BuildException {
		SessionFactory sessionFactory = ( SessionFactory )SQLTask.getContext().getBean("sessionFactory");
		Session session = sessionFactory.openSession();
		
		try {
			
			CSVBeanFactory factory = ( CSVBeanFactory )workerClass.newInstance();

			List<CSVField[]> records = parseCSV( source );
			session.getTransaction().begin();
			for( int i=0;i<records.size();i++ ) {
				CSVField[] record = ( CSVField[] )records.get( i );
				try {
					session.saveOrUpdate( factory.newBean( record ));
				} catch( Exception ex ) {
					log("import record failre.",Project.MSG_ERR );
					for( int j=0;j<record.length;j++ )
						log( record[j].toString(),Project.MSG_ERR );
					
					throw ex;
				}
			}
			session.getTransaction().commit();
			
			log(" total "+records.size()+" records imported.",Project.MSG_INFO );
		} catch( Exception ex ) {
			log("error:"+ ex.getMessage()+" cause "+ex.getCause() );
			StackTraceElement[] traces = ex.getStackTrace();
			for( int i=0;i<traces.length;i++ )
				log("\t"+traces[i] );
			
			throw new BuildException( ex );
		} finally {
			session.close();
		}
	}
	
	private List<CSVField[]> parseCSV( String importDel ) throws IOException {

		File importDelFile = new File( importDel );
		BufferedReader r = new BufferedReader( new InputStreamReader(
				new FileInputStream( importDelFile ),"UTF-8"));
		
		r.mark(1);
		int hoge = r.read();
		if( hoge != 65279 ) {
			r.reset();
		}
		
		List<CSVField[]> result = new ArrayList<CSVField[]>();
		
		CSVReader csv = new CSVReader( r );
		String[] fields;
		while(( fields = csv.readNext() ) != null ) {
			CSVField[] csvFields = new CSVField[ fields.length ];
			for( int i=0;i<fields.length;i++ ) {
				String field = fields[i];
				
				String externalFilePath = null;
				
				Pattern pattern = Pattern.compile("\\s*<XDS FIL='(.+)' />");
				Matcher matcher = pattern.matcher( field );
				if( matcher.matches())
					externalFilePath = matcher.group(1);
				
				Pattern lobPattern = Pattern.compile("(.+\\.lob)");
				Matcher lobMatcher = lobPattern.matcher( field );
				if( lobMatcher.matches() )
					externalFilePath = lobMatcher.group(1);
				
				if( externalFilePath != null ) {
					csvFields[i] = getExternalFile( importDelFile,externalFilePath );
				} else {
					csvFields[i] = new CSVField( fields[i] );
				}
			}
			
			result.add( csvFields );
		}
		
		return result;
	}
	
	private CSVField getExternalFile( File importDelFile,String source ) throws IOException {
		File dataFile =  new File( importDelFile.getParentFile(),source);
		
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
}