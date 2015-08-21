package org.infoscoop.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.IAccountManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public class AccountManagerServlet extends HttpServlet{
	private static Log log = LogFactory.getLog(AccountManagerServlet.class);

	private static final long serialVersionUID = 1646514470595445974L;
	private static final String CHANGEPW_PATH = "/doChangePW";
	private static final String CHANGESQ_PATH = "/doChangeSQ";
	private static final String CHANGE_PATH = "/doChange";

	public void init() {}

	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response) throws ServletException, IOException {
		String action = ((HttpServletRequest) request).getPathInfo();
		IAccountManager accountManager = AuthenticationService.getInstance().getAccountManager();
		HttpSession session = request.getSession();
		String uid = (String)session.getAttribute("Uid");

		if (CHANGE_PATH.equals(action)) {
			// change
			Map<String, String[]> map = request.getParameterMap();
			try {
				String loginUserName = accountManager.updateUserProfile(uid, map);
				if(loginUserName != null)
					session.setAttribute("loginUserName", loginUserName);
			} catch(Exception e) {
				log.error("error update user infomation.", e);
				response.sendError(500);
			}
		} else if(CHANGEPW_PATH.equals(action)) {
			// change password
			String password = request.getParameter("password");
			try {
				accountManager.updatePassword(uid, password);
			} catch(Exception e) {
				log.error("error update password.", e);
				response.sendError(500);
			}
		} else if(CHANGESQ_PATH.equals(action)) {
			// change default square
			String squareId = request.getParameter("square");
			try {
				accountManager.updateDefaultSquare(uid, squareId);
			} catch (Exception e) {
				log.error("error update default square.", e);
				response.sendError(500);
			}
		}
	}
}
