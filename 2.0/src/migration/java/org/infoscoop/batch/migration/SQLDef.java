/**
 * 
 */
package org.infoscoop.batch.migration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;

public class SQLDef{
	private String sql;
	private String schema;
	
	public SQLDef(String schema){
		this.schema = schema;
	}
	
	String text;
	public void addText( String text ) {
		sql = text;
		Matcher matcher = Pattern.compile("\\$\\{([^}]+)\\}").matcher( sql );
		if( matcher.find() ) {
			for( int j=1;j<=matcher.groupCount();j++ ) {
				String key = matcher.group(j);
				sql = sql.replaceAll("\\$\\{"+key+"\\}", schema.replaceAll("\\$", "\\\\\\$"));
			}
		}
	}
	
	public static void main(String args[]){
		String sql = "drop table ${SCHEMA_NAME}.authcredential_bak";
		Matcher matcher = Pattern.compile("\\$\\{([^}]+)\\}").matcher( sql );
		if( matcher.find() ) {
			System.out.println(matcher.groupCount());
			for( int j=1;j<=matcher.groupCount();j++ ) {
				String key = matcher.group(j);
				sql = sql.replaceAll("\\$\\{"+key+"\\}", "hoge#$".replaceAll("\\$", "\\\\\\$"));
			}
		}
	}

	public void setFile( String file ) {
		try {
			File f = new File( file );
			if( !f.exists())
				throw new FileNotFoundException( new File("").getAbsolutePath()+"/"+file );

			BufferedReader reader = new BufferedReader( new FileReader( file ));
			String line;
			StringBuffer buf = new StringBuffer();

			try {
				while (( line = reader.readLine() ) != null )
					buf.append( line );
			} finally {
				reader.close();
			}
			
			sql = buf.toString();
			Matcher matcher = Pattern.compile("\\$\\{([^}]+)\\}").matcher( sql );
			if( matcher.find() ) {
				for( int j=1;j<=matcher.groupCount();j++ ) {
					String key = matcher.group(j);
					sql = sql.replaceAll("\\$\\{"+key+"\\}", schema.replaceAll("\\$", "\\\\\\$"));
				}
			}
		} catch (IOException ex) {
			
			throw new BuildException( ex );
		}
	}
	
	
	public String getSQLString() throws Exception {
		return sql;
	}
}