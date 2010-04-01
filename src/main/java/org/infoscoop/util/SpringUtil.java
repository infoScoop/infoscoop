package org.infoscoop.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtil {
	private static ApplicationContext context;

	public static ApplicationContext getContext() {
		return context;
	}

	public static Object getBean(String name) {
		return context.getBean(name);
	}

	/**
	 * Initializing application context for a batch program, for example the migration tools 
	 * @param definitions Array of beacn definition files in classpath
	 */
	public synchronized static void initContext(String[] definitions){
		context = new ClassPathXmlApplicationContext(definitions);
	}
	
	/**
	 * Application context is set at starting of main web application. 
	 * @param ctx
	 */
	public static void setContext(ApplicationContext ctx) {
		context = ctx;
	}
}
