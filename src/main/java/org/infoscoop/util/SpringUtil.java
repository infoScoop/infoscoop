/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

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

	public static Object getBean(Class c) {
		return context.getBean(c);
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
