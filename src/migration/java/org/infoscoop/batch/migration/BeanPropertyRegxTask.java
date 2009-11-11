package org.infoscoop.batch.migration;

import java.io.*;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.sql.*;

import javax.sql.DataSource;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.lf5.LogLevel;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;

import au.com.bytecode.opencsv.CSVReader;

public class BeanPropertyRegxTask implements HibernateBeansTask.BeanTask {
	public BeanPropertyRegxTask() {
		// TODO Auto-generated constructor stub
	}

	private String regx;
	public void setRegx( String regx ) throws Exception {
		this.regx = regx;
	}
	
	private String replace;
	public void setReplace( String replace ) throws Exception {
		this.replace = replace;
	}
	
	private String property;
	public void setProperty( String property ) {
		this.property = property;
	}
	
	public void setFile( String file ) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file));
		this.regx = br.readLine();
	}
	
	
	private Method getter;
	private Method setter;
	public void execute( Project project,Object object ) throws BuildException {
		try {
			if( getter == null || setter == null ) {
				Class targetClass = object.getClass();
				String pn = property.substring(0,1).toUpperCase().concat( property.substring(1));
				getter = targetClass.getMethod("get"+pn,new Class[0]);
				setter = targetClass.getMethod("set"+pn,new Class[]{ String.class });
				
				if( getter == null || setter == null )
					throw new BuildException("not found property: "+property );
			}
			
			String xml = ( String )getter.invoke( object );
			
			Pattern pattern = Pattern.compile(this.regx, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(xml);
			String result = matcher.replaceFirst(this.replace);

			project.log("Result: \n"+result,Project.MSG_INFO );
			setter.invoke( object,result );
		} catch( Exception ex ) {
			throw new BuildException( ex );
		}
	}
}
