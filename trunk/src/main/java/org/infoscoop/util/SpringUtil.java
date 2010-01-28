package org.infoscoop.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtil {
	private static AbstractXmlApplicationContext context;

	public synchronized static void initContext(String[] definitions){
		context = new ClassPathXmlApplicationContext(definitions);
	}
	
	public static ApplicationContext getContext() {
		return context;
	}

	public static Object getBean(String name) {
		return context.getBean(name);
	}
}
