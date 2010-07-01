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

package org.infoscoop.request;

public class ProxyAuthenticationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -960080902866345421L;

	public ProxyAuthenticationException(Exception e) {
		super(e);
	}

	public ProxyAuthenticationException(String msg) {
		super(msg);
	}

	public ProxyAuthenticationException(String msg, Exception e) {
		super(msg, e);
	}

}
