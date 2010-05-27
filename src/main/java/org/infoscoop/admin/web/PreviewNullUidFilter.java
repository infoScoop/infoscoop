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

package org.infoscoop.admin.web;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class PreviewNullUidFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		SessionWrapHttpServletRequest reqwrapper = new SessionWrapHttpServletRequest(
				(HttpServletRequest) request);
		filterChain.doFilter(reqwrapper, response);
	}

	public void init(FilterConfig config) throws ServletException {
	}

	static class SessionWrapHttpServletRequest extends
			HttpServletRequestWrapper {
		HttpSession session;

		public SessionWrapHttpServletRequest(HttpServletRequest request) {
			super(request);
			HttpSession currentSession = request.getSession(false);
			if (currentSession != null)
				this.session = new NullUidHttpSessionWrapper(currentSession);
		}

		public HttpSession getSession() {
			return this.session;
		}

		public HttpSession getSession(boolean create) {
			if (create && (this.session == null || !this.session.isNew())) {
				this.session = new NullUidHttpSessionWrapper(super
						.getSession(true));
			}
			return this.session;
		}
	}

	static class NullUidHttpSessionWrapper implements HttpSession {
		private HttpSession session;

		public NullUidHttpSessionWrapper(HttpSession session) {
			this.session = session;
		}

		public Object getAttribute(String name) {
			if (name != null && name.equals("Uid"))
				return null;
			return session.getAttribute(name);
		}

		public Enumeration getAttributeNames() {
			return session.getAttributeNames();
		}

		public long getCreationTime() {
			return session.getCreationTime();
		}

		public String getId() {
			return session.getId();
		}

		public long getLastAccessedTime() {
			return session.getLastAccessedTime();
		}

		public int getMaxInactiveInterval() {
			return session.getMaxInactiveInterval();
		}

		public ServletContext getServletContext() {
			return session.getServletContext();
		}

		public HttpSessionContext getSessionContext() {
			return session.getSessionContext();
		}

		public Object getValue(String arg0) {
			return session.getValue(arg0);
		}

		public String[] getValueNames() {
			return session.getValueNames();
		}

		public void invalidate() {
			session.invalidate();
		}

		public boolean isNew() {
			return session.isNew();
		}

		public void putValue(String arg0, Object arg1) {
			session.putValue(arg0, arg1);
		}

		public void removeAttribute(String arg0) {
			session.removeAttribute(arg0);
		}

		public void removeValue(String arg0) {
			session.removeValue(arg0);
		}

		public void setAttribute(String arg0, Object arg1) {
			session.setAttribute(arg0, arg1);
		}

		public void setMaxInactiveInterval(int arg0) {
			session.setMaxInactiveInterval(arg0);
		}
	}
}
