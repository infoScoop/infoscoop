package org.infoscoop.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.IAccountManager;
import org.infoscoop.context.UserContext;
import org.infoscoop.service.InvitationService;
import org.infoscoop.service.SquareService;
import org.infoscoop.util.RequestUtil;
import org.infoscoop.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SquareServlet extends HttpServlet {
	private static Log log = LogFactory.getLog(SquareServlet.class);

	private static final long serialVersionUID = 1646514470595445974L;
	private static final String CREATE_PATH = "/doCreate";
	private static final String GET_PATH = "/doGetBelongSquare";
	private static final String CHANGE_PATH = "/doChange";
	private static final String MYSQUARE_PATH = "/mySquare";
	private static final String ERROR_MAX_SQUARE = "500_01";

	public void init() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String action = ((HttpServletRequest)request).getPathInfo();

		HttpSession session = request.getSession();
		String uid = (String)session.getAttribute("Uid");
		String currentSquareId = UserContext.instance().getUserInfo().getCurrentSquareId();

		// get square name
		if(GET_PATH.equals(action)) {
			try {
				Map<String, Object> map = SquareService.getHandle().getBelongSquaresNames(uid, currentSquareId);
				ObjectMapper mapper = new ObjectMapper();

				response.setContentType("text/json;charset=utf-8");
				response.getWriter().write(mapper.writeValueAsString(map));
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Cache-Control", "no-cache");
			} catch (Exception e) {
				log.error("Failed getting Square names", e);
				response.sendError(500, e.getMessage());
			}
		}
		// move
		if(CHANGE_PATH.equals(action)) {
			String squareId =  request.getParameter("square-id");
			response.sendRedirect(SquareService.getRedirectHostUrlWithSquareAlias(request.getScheme(), squareId, request.getContextPath()+"/index.jsp"));
		}
		else if(MYSQUARE_PATH.equals(action)){
			IAccountManager accountManager = AuthenticationService.getInstance().getAccountManager();
			IAccount account;
			try {
				account = accountManager.getUser(uid);
				String mySquareId = account.getMySquareId();

				// set mysquare id to default square id
				accountManager.updateDefaultSquare(uid, mySquareId);

				response.sendRedirect(RequestUtil.createRedirectHostUrl(request.getScheme(), "mysquare", request.getContextPath()+"/index.jsp"));
			} catch (Exception e) {
				log.error("Get account information failed. " + e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
	}

	protected  void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String action = ((HttpServletRequest)request).getPathInfo();

		HttpSession session = request.getSession();
		String uid = (String)session.getAttribute("Uid");

		// create
		if(CREATE_PATH.equals(action)) {
			String squareName = request.getParameter("square-name");
			String squareDesc = request.getParameter("square-description");
			String squareSource = request.getParameter("square-source");
			String squareMember = request.getParameter("square-member");

			if(squareSource == null
					|| squareSource.length() == 0
					|| !SquareService.getHandle().existsSquare(squareSource)) {
				squareSource = SquareService.SQUARE_ID_DEFAULT;
			}

			// validation max owned square
			try {
				if(SquareService.getHandle().isReachMaxSquare(uid)){
					// error max owned square
					log.error("To reach the maximum square");
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.setHeader("Pragma", "no-cache");
					response.setHeader("Cache-Control", "no-cache");
					PrintWriter out = response.getWriter();
					out.write(ERROR_MAX_SQUARE);
				} else {
					// create square
					String squareId = SquareService.generateSquareId();
						// mail invitation user
						List<String> emailList = new ArrayList<String>();
						List<String> errorEmailList = new ArrayList<String>();
						BufferedReader reader = new BufferedReader(new StringReader(squareMember));
						String email;
						while((email = reader.readLine()) != null){
							String emailTrim = StringUtil.trimSpace(email);
							if(emailTrim.length() > 0) {
								if(!StringUtil.isValidEmail(emailTrim)){
									errorEmailList.add(emailTrim);
								}
								emailList.add(emailTrim);
							}
						}

						if(errorEmailList.size() > 0){
							JSONObject json = new JSONObject();
							json.put("errorEmails", new JSONArray(errorEmailList));
							response.setStatus(HttpStatus.BAD_REQUEST.value());
							response.setHeader("Pragma", "no-cache");
							response.setHeader("Cache-Control", "no-cache");
							return;
						}

						SquareService.getHandle().createSquare(squareId, squareName, squareDesc, squareSource, uid);

						// relation user - square
						AuthenticationService service = AuthenticationService.getInstance();
						IAccountManager manager = service.getAccountManager();
						manager.addSquareId(uid, squareId);

						// mail invitation user
						InvitationService.getHandle().doInvitation(emailList, request, squareId);

						// move created square
						response.setHeader("X-IS-SQUAREID", squareId);
				}
			} catch( Exception e ) {
				log.error("",e);
				response.sendError(500, e.getMessage());
			}

		}

		// update

		// delete
	}
}