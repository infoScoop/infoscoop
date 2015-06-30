package org.infoscoop.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.IAccountManager;
import org.infoscoop.service.SquareService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

public class SquareServlet extends HttpServlet {
	private static Log log = LogFactory.getLog(SquareServlet.class);

	private static final long serialVersionUID = 1646514470595445974L;
	private static final String CREATE_PATH = "/doCreate";

	public void init() {}

	protected  void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String action = ((HttpServletRequest)request).getPathInfo();

		HttpSession session = request.getSession();
		String uid = (String)session.getAttribute("Uid");

		// create
		if(CREATE_PATH.equals(action)) {
			String squareName = request.getParameter("square-name");
			String squareDesc = request.getParameter("square-description");
			String squareSource = request.getParameter("square-source");

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

				// move created square
				changeCurrentSquare(squareId, request);
			} catch( Exception e ) {
				log.error("",e);
				response.sendError(500, e.getMessage());
			}
		}

		// update

		// move

		// delete
	}

	private void changeCurrentSquare(String squareId, HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("CurrentSquareId", squareId);
	}
}
