package org.infoscoop.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.IAccountManager;
import org.infoscoop.service.InvitationService;
import org.infoscoop.service.SquareService;
import org.infoscoop.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SquareServlet extends HttpServlet {
	private static Log log = LogFactory.getLog(SquareServlet.class);

	private static final long serialVersionUID = 1646514470595445974L;
	private static final String CREATE_PATH = "/doCreate";
	private static final String GET_PATH = "/doGetBelongSquare";
	private static final String CHANGE_PATH = "/doChange";

	public void init() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String action = ((HttpServletRequest)request).getPathInfo();

		HttpSession session = request.getSession();
		String uid = (String)session.getAttribute("Uid");
		String currentSquareId = (String)session.getAttribute("CurrentSquareId");

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
				squareSource = "default";
			}

			// create square
			String squareId = (UUID.randomUUID().toString()).replaceAll("-", "");
			try {
				SquareService.getHandle().createSquare(squareId, squareName, squareDesc, squareSource, uid);

				// relation user - square
				AuthenticationService service = AuthenticationService.getInstance();
				IAccountManager manager = service.getAccountManager();
				manager.addSquareId(uid, squareId);

				// mail invitation user
				List<String> emailList = new ArrayList<String>();
				List<String> errorEmailList = new ArrayList<String>();
				BufferedReader reader = new BufferedReader(new StringReader(squareMember));
				String email;
				while((email = reader.readLine()) != null){
					if(!StringUtil.isValidEmail(email)){
						errorEmailList.add(email);
					}
					emailList.add(email.trim());
				}

				if(errorEmailList.size() > 0){
					JSONObject json = new JSONObject();
					json.put("errorEmails", new JSONArray(errorEmailList));
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setHeader("Pragma", "no-cache");
					response.setHeader("Cache-Control", "no-cache");
					return;
				}

				// mail invitation user
				InvitationService.getHandle().doInvitation(emailList, request);

				// move created square
				changeCurrentSquare(squareId, request);
			} catch( Exception e ) {
				log.error("",e);
				response.sendError(500, e.getMessage());
			}
		}

		// update

		// move
		if(CHANGE_PATH.equals(action)) {
			String squareId =  request.getParameter("square-id");
			changeCurrentSquare(squareId, request);
		}

		// delete
	}

	private void changeCurrentSquare(String squareId, HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("CurrentSquareId", squareId);
	}
}