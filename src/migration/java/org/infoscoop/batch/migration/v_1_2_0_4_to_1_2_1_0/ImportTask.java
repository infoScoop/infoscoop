package org.infoscoop.batch.migration.v_1_2_0_4_to_1_2_1_0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
			
			List records = parseCSV( source );
			session.getTransaction().begin();
			for( int i=0;i<records.size();i++ ) {
				try {
					session.saveOrUpdate( factory.newBean( ( String[] )records.get( i )));
				} catch( Exception ex ) {
					log("import record failre.",Project.MSG_ERR );
					String[] record = ( String[] )records.get( i );
					for( int j=0;j<record.length;j++ )
						log( record[j],Project.MSG_ERR );
					
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
	
	private List parseCSV( String importDel ) throws IOException {
		File importDelFile = new File( importDel );
		BufferedReader r = new BufferedReader( new InputStreamReader(
				new FileInputStream( importDelFile ),"UTF-8"));
		
		r.mark(1);
		int hoge = r.read();
		if( hoge != 65279 ) {
			r.reset();
		}
		
		List result = new ArrayList();
		
		CSVReader csv = new CSVReader( r );
		String[] fields;
		while(( fields = csv.readNext() ) != null ) {
			for( int i=0;i<fields.length;i++ ) {
				String field = fields[i];
				
				String externalFilePath = null;
				
				Pattern pattern = Pattern.compile("\\s*<XDS FIL='(.+)' />\\s*");
				Matcher matcher = pattern.matcher( field );
				if( matcher.matches())
					externalFilePath = matcher.group(1);
				
				Pattern lobPattern = Pattern.compile("(.+\\.lob)");
				Matcher lobMatcher = lobPattern.matcher( field );
				if( lobMatcher.matches() )
					externalFilePath = lobMatcher.group(1);
				
				if( externalFilePath != null )
					fields[i] = getExternalFile( importDelFile,externalFilePath );
			}
			
			result.add( fields );
		}
		
		return result;
	}
	
	private String getExternalFile( File importDelFile,String source ) throws IOException {
		File dataFile =  new File( importDelFile.getParentFile(),source);
		
		FileInputStream in = new FileInputStream( dataFile );
		BufferedReader reader = new BufferedReader(
				new InputStreamReader( in,"UTF-8"));
		reader.mark(1);
		if( reader.read() != 65279 )
			reader.reset();

		StringBuffer buf = new StringBuffer();
		String line = reader.readLine();
		while( line != null ) {
			buf.append( line );
			line = reader.readLine();
			if( line != null )
				line = "\r\n"+line;
		}
		
		return buf.toString();
	}
}