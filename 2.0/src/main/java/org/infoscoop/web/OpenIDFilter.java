package org.infoscoop.web;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OpenIDFilter implements Filter {
	private static Log log = LogFactory.getLog(OpenIDFilter.class);
	private Collection<String> excludePaths = new HashSet<String>();
	
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {
		// basically just check for openId parameters
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("Uid");
        if (uid == null){
        	uid = (String) session.getAttribute("openid");
			if(log.isInfoEnabled())
				log.info(uid + " is logged in by openid.");
        }
        if (uid == null && !isExcludePath(request.getServletPath())){
        	try {
        		String loginUrl = request.getRequestURI().lastIndexOf("/admin/") > 0 ?  "../openid_login.jsp" : "openid_login.jsp";//Handling with management page may be problem

        		response.sendRedirect(loginUrl);
        		return;

        	} catch(Exception e) {
        		log.error("", e);
			}
		}else{
			session.setAttribute("Uid", uid);
		}
		chain.doFilter(servletRequest, servletResponse);
	}

	public void init(FilterConfig config) throws ServletException {

		String excludePathStr = config.getInitParameter("excludePath");
		if(excludePathStr != null){
			String[] pathArray = excludePathStr.split(",");
			for(int i = 0; i < pathArray.length; i++){
				String path = pathArray[i].trim();
				if( path.endsWith("*")) {
					excludePaths.add( path.substring(0,path.length() -1 ));
				} else {
					excludePaths.add( path );
				}
			}
		}
	}

	private boolean isExcludePath( String path ) {
		if( excludePaths.contains( path ))
			return true;

		for( String p : excludePaths ) {
			if( path.startsWith( p ))
				return true;
		}

		return false;
	}

}
