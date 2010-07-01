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

package org.infoscoop.web;

public interface HttpStatusCode {
	String HEADER_NAME = "MSDPortal-Status";
	
	String MSD_SC_CONTENT_PARSE_ERROR = "10550";

	String MSD_SC_TIMEOUT = "10408";

	/**
	 * An error code when forced reloading was necessary at initialization of the customized information.
	 */
	String MSD_FORCE_RELOAD = "10997";
	
	/**
	 * An error code when logged in by the other browser and did it, and session ID changed.
	 */
	String MSD_INVALID_SESSION = "10998";

	/**
	 * An error code when session time-out occurred.
	 */
	String MSD_SESSION_TIMEOUT = "10999";
}
