package org.infoscoop.admin.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.IAccountManager;
import org.infoscoop.account.PrincipalDef;
import org.infoscoop.account.SessionCreateConfig;
import org.infoscoop.acl.ISPrincipal;
import org.infoscoop.util.SpringUtil;
import org.infoscoop.web.SessionManagerFilter;


public class PreviewImpersonationFilter implements Filter {
	private static Log log = LogFactory.getLog(PreviewImpersonationFilter.class);

	public static final String IS_PREVIEW = "isPreview";
	public static final String PRINCIPAL_PARAMS = "principalParams";
	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		request.setAttribute(IS_PREVIEW, Boolean.TRUE);

		Subject previewUser = new Subject();

		List<String> principals = new ArrayList<String>();

		String uidParam = request.getParameter(ISPrincipal.UID_PRINCIPAL);
		if(uidParam != null){
			principals.add(ISPrincipal.UID_PRINCIPAL);
			principals.add(uidParam);
			previewUser.getPrincipals().add(new ISPrincipal(ISPrincipal.UID_PRINCIPAL, uidParam));
		}
		for(PrincipalDef def: SessionCreateConfig.getInstance().getPrincipalDefs()){
			String[] principalValues = request.getParameterValues(def.getType());
			if(principalValues != null){
				for(int i = 0; i < principalValues.length;i++){
					if(log.isInfoEnabled())
						log.info("Set preview principal: PrincipalType=" + def.getType() + ", name=" + principalValues[i]  + ".");

					principals.add(def.getType());
					principals.add(principalValues[i]);
					previewUser.getPrincipals().add(new ISPrincipal(def.getType(), principalValues[i]));
				}
			}
		}

		// Principal retrieved from AccountManager set AuthenticationService
		AuthenticationService service= AuthenticationService.getInstance();
		IAccountManager manager = null;
		if (service != null)
			manager = service.getAccountManager();
		if(manager != null){
			for(PrincipalDef def : manager.getPrincipalDefs()){
				String roleType = def.getType();
				String[] principalValues = request.getParameterValues(roleType);

				for(int i = 0; principalValues != null && i < principalValues.length ; i++){
					if(log.isInfoEnabled())
						log.info("Set preview principal: PrincipalType=" + roleType + ", name=" + principalValues[i]  + ".");
					principals.add(def.getType());
					principals.add(principalValues[i]);
					previewUser.getPrincipals().add(new ISPrincipal(roleType, principalValues[i]));
				}
			}
		}


		request.setAttribute(PRINCIPAL_PARAMS, principals);

		SetPrincipalHttpServletRequest reqwrapper = new SetPrincipalHttpServletRequest(
				(HttpServletRequest) request, previewUser);
		filterChain.doFilter(reqwrapper, response);
	}

	public void init(FilterConfig config) throws ServletException {
	}

	static class SetPrincipalHttpServletRequest extends
			HttpServletRequestWrapper {

		private HttpSession session;
		private Subject previewUser;

		public SetPrincipalHttpServletRequest(HttpServletRequest request, Subject previewUser) {
			super(request);
			this.previewUser = previewUser;
		}

		public HttpSession getSession() {
			return getSession(true);
		}

		public HttpSession getSession(boolean create) {
			if (session != null)
				return session;
			HttpSession orgSession = super.getSession(create);
			if (orgSession != null) {
				session = new PreviewHttpSession(this, orgSession, previewUser);
				return session;
			}
			return null;
		}
	}

	public static class PreviewHttpSession implements HttpSession {
		private HttpSession session;
		private HttpServletRequest dummyRequest;
		private Object loginUser;

		public PreviewHttpSession(HttpServletRequest request, HttpSession session, Subject loginUser) {
			super();
			this.dummyRequest = request;
			this.session = session;
			this.loginUser = loginUser;

		}

		public Object getAttribute(String name) {
			if (name.equals(SessionManagerFilter.LOGINUSER_SUBJECT_ATTR_NAME))
				return this.loginUser;
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

		public void setAttribute(String name, Object value) {
			session.setAttribute(name, value);
		}

		public void setMaxInactiveInterval(int arg0) {
			session.setMaxInactiveInterval(arg0);
		}
	}
}
