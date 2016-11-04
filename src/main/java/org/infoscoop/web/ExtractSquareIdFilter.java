package org.infoscoop.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.IAccount;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.SquareAliasDAO;
import org.infoscoop.dao.model.SquareAlias;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.infoscoop.properties.InfoScoopProperties;

public class ExtractSquareIdFilter implements Filter{

	private Log log = LogFactory.getLog(this.getClass());

	public static final String RESERVED_WORDS_WWW = "www";
	
	@SuppressWarnings("unchecked")
	private List<String> needConvertWords = Arrays.asList(new String[]{"mysquare", RESERVED_WORDS_WWW});
	
	// エイリアス変換後
	public static String SESSION_ATTR_CURRENT_SQUARE_ID_CONVERTED = "cg-current-square-id-conved";
	// エイリアス変換前
	public static String SESSION_ATTR_CURRENT_SQUARE_ID_ORG = "cg-current-square-id-org";
	// 要変換フラグ
	public static String SESSION_FLAG_NEED_CONVERT_ID = "cg-need-convert-id";

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpReq = (HttpServletRequest) request;
		
		// リクエストヘッダからスクエアID取得
		InfoScoopProperties props = InfoScoopProperties.getInstance();
		String hostName = props.getProperty("hostname");

		String host = httpReq.getHeader("HOST");
		String headerSquareId = (host != null && host.length() > 0) ? host.replaceAll(hostName, "") : null;
		if(headerSquareId != null && headerSquareId.length() > 0) {
			headerSquareId = (headerSquareId.charAt(headerSquareId.length()-1) == '.') ? headerSquareId.substring(0, headerSquareId.length()-1) : headerSquareId;
		} else {
			headerSquareId = RESERVED_WORDS_WWW;
		}


		// ユーザセッション確立しているかどうかの確認用（ログイン前ならnull）
		String uid = (String)httpReq.getSession().getAttribute("Uid");
		
		HttpSession session = httpReq.getSession();
		String currentSquareIdOrg = (String)session.getAttribute(SESSION_ATTR_CURRENT_SQUARE_ID_ORG);
		
		// 要変換フラグ。以前のフィルタから渡される
		boolean needConvertId = session.getAttribute(SESSION_FLAG_NEED_CONVERT_ID) != null;
		
		if(headerSquareId.equals(currentSquareIdOrg)){
			// スクエアが変更されていない場合、負荷軽減のためセッション内の値を利用
			String convertedSquareId = (String)session.getAttribute(SESSION_ATTR_CURRENT_SQUARE_ID_CONVERTED);
			UserContext.instance().getUserInfo().setCurrentSquareId(convertedSquareId);
		} else {
			// スクエアが変更された、あるいは初回アクセスの場合、セッション内ではなく現在のサブドメインを利用
			
			String convertedSquareId = headerSquareId;
			
			if(needConvertWords.contains(convertedSquareId)){
				if(uid != null){
					// 既にユーザセッションが存在する場合、このセッションで変換を実行する
					needConvertId = true;
				}else{
					// ユーザセッション確立後（コールバック後）に変換が必要な場合、フラグを立てる
					session.setAttribute(SESSION_FLAG_NEED_CONVERT_ID, "true");
				}
			}else{
				// エイリアス情報があれば変換
				SquareAlias alias = SquareAliasDAO.newInstance().getByName(headerSquareId);
				if(alias != null && alias.getSquareId() != null){
					convertedSquareId = alias.getSquareId();
					session.removeAttribute(SESSION_FLAG_NEED_CONVERT_ID);
				}
			}
			session.setAttribute(SESSION_ATTR_CURRENT_SQUARE_ID_CONVERTED, convertedSquareId);
			session.setAttribute(SESSION_ATTR_CURRENT_SQUARE_ID_ORG, headerSquareId);
			
			UserContext.instance().getUserInfo().setCurrentSquareId(convertedSquareId);
		}
		
		if(uid != null && needConvertId){
			// セッションが確立しており、変換フラグがたっている場合、ユーザ情報を基に予約語 "www", "mysquare" をコンバートする
			AuthenticationService service = AuthenticationService.getInstance();
			
			IAccount account;
			String convertedSquareId;
			try {
				account = service.getAccountManager().getUser(uid);

				switch (headerSquareId) {
					case "mysquare":
						convertedSquareId = account.getMySquareId();
						break;
					case "www":
						convertedSquareId = account.getDefaultSquareId();
						break;
					default:
						convertedSquareId = headerSquareId;
						break;
				}
				// コンバートされたスクエアIDをセッションに格納
				session.setAttribute(SESSION_ATTR_CURRENT_SQUARE_ID_CONVERTED, convertedSquareId);
				session.removeAttribute(SESSION_FLAG_NEED_CONVERT_ID);
				UserContext.instance().getUserInfo().setCurrentSquareId(convertedSquareId);
			} catch (Exception e) {
				log.error("get account process failed. [uid=" + uid + "]" , e);
				throw new ServletException(e);
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {		
	}

}
