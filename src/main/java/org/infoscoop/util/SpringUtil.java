package org.infoscoop.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtil {
	private static ApplicationContext context;

	public static ApplicationContext getContext() {
		return context;
	}

	public static Object getBean(String name) {
		return context.getBean(name);
	}

	public static void setContext(ApplicationContext ctx) {
		context = ctx;
	}
}
