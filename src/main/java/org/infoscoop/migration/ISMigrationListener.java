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

package org.infoscoop.migration;

import javax.servlet.ServletContextEvent;

import liquibase.integration.servlet.LiquibaseServletListener;

import org.apache.commons.lang.StringUtils;

public class ISMigrationListener extends LiquibaseServletListener {
	final String DEFAULT_SCHEMA_NAME = "migration.defaultSchema";
	final String DEFAULT_SCHEMA_NAME_WITH_DOT = "migration.defaultSchemaWithDot";
	@Override
	public void contextInitialized(ServletContextEvent event) {
		String defaultSchema = event.getServletContext().getInitParameter("liquibase.schema.default");
		
		if(defaultSchema == null || StringUtils.isEmpty(defaultSchema)){
			System.setProperty(DEFAULT_SCHEMA_NAME_WITH_DOT, "");
			System.setProperty(DEFAULT_SCHEMA_NAME, "");
		}else{
			System.setProperty(DEFAULT_SCHEMA_NAME_WITH_DOT, defaultSchema + ".");
			System.setProperty(DEFAULT_SCHEMA_NAME, defaultSchema);
			
		}

		super.contextInitialized(event);
	}
}
