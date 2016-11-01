package org.infoscoop.account.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.account.AuthenticationService;
import org.infoscoop.account.IAccount;
import org.infoscoop.account.IAccountManager;
import org.infoscoop.account.simple.AccountAttributeName;
import org.infoscoop.dao.model.Square;
import org.infoscoop.properties.InfoScoopProperties;
import org.infoscoop.service.SquareService;

public class AccountHelper {
	private static Log log = LogFactory.getLog(AccountHelper.class);
	private static final String PASSWORD_POLICY = "password.policy";

	public static String getPasswordPolicy() {
		return InfoScoopProperties.getInstance().getProperty(PASSWORD_POLICY);
	}

	public static boolean isValidPassword(String password) {
		boolean result = false;

		if(password != null && password.length() > 0){
			Pattern ptn = Pattern.compile(getPasswordPolicy());
			Matcher mc = ptn.matcher(password);
			if(mc.matches()) {
				result = true;
			}
		}

		return result;
	}

	public static boolean isNotValidFirstName(String firstName) {
		boolean result = false;

		// 文字列チェック
		if(firstName != null && firstName.length() > 0 && firstName.length() < 101) {
			// 空白チェック
			Pattern ptn = Pattern.compile("\\(\\S*\\)");
			Matcher mc = ptn.matcher(firstName);
			if(mc.matches()) {
				result = true;
			}
		}

		return result;
	}

	public static boolean isNotValidGivenName(String givenName) {
		boolean result = true;

		// 文字列チェック
		if(givenName != null && givenName.length() > 0 && givenName.length() < 101) {
			// 空白チェック
			Pattern ptn = Pattern.compile("\\(\\S*\\)");
			Matcher mc = ptn.matcher(givenName);
			if(!mc.matches()) {
				result = false;
			}
		}

		return result;
	}

	public static boolean isNotValidEmail(String email) {
		boolean result = false;

		// 文字列チェック
		if(email != null && email.length() > 0 && email.length() < 151) {
			// メールアドレスの正しさ
			String mailFormat = "^[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+(\\.[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+)*+(.*)@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9\\-]+)+$";
			Pattern ptn = Pattern.compile(mailFormat);
			Matcher mc = ptn.matcher(email);
			if(!mc.matches()) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * 指定メールアドレスのユーザがスクエアに存在するかチェック
	 * 
	 * @param uid
	 * @param squareid
	 * @return
	 * @throws Exception
	 */
	public static boolean isExistsUserInSquare(String uid, String squareid) throws Exception{
		Map<String, String> searchConditionMap = new HashMap<String, String>();
		searchConditionMap.put("user_id", uid);
		searchConditionMap.put("user_belong_square", squareid);
		
		List<IAccount> users = searchUser(searchConditionMap);
		return users.size() > 0;
	}

	/**
	 * ユーザが存在するかチェック
	 *
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public static boolean isExistsUser(String uid) throws Exception{
		Map<String, String> searchConditionMap = new HashMap<String, String>();
		searchConditionMap.put("user_id", uid);

		List<IAccount> users = searchUser(searchConditionMap);
		return users.size() > 0;
	}

	/**
	 * 指定メールアドレスのユーザが存在するかチェック
	 * 
	 * @param mail
	 * @return
	 * @throws Exception 
	 */
	public static boolean isExistsAddress(String mail) throws Exception{
		Map<String, String> searchConditionMap = new HashMap<String, String>();
		searchConditionMap.put("user_email", mail);
		
		List<IAccount> users = searchUser(searchConditionMap);
		return users.size() > 0;
	}
	
	/**
	 * 
	 * @param searchConditionMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<IAccount> searchUser(Map<String, String> searchConditionMap) throws Exception{
		AuthenticationService authService = AuthenticationService.getInstance();
		IAccountManager accountManager = authService.getAccountManager();
		
		List<IAccount> users = accountManager.searchUser(searchConditionMap);
		return users;
	}
	
	@SuppressWarnings("unchecked")
	public static List<IAccount> searchUsersBySquareId(String squareId) throws Exception{
		AuthenticationService authService = AuthenticationService.getInstance();
		IAccountManager accountManager = authService.getAccountManager();
		Map<String, String> searchConditionMap = new HashMap<String, String>();
		searchConditionMap.put("user_belong_square", squareId);
		
		return accountManager.searchUser(searchConditionMap);
	}

	public static void deleteBelongToBySquareId(String squareId) throws Exception{
		AuthenticationService authService = AuthenticationService.getInstance();
		IAccountManager accountManager = authService.getAccountManager();
		List<IAccount> users = searchUsersBySquareId(squareId);
		for( IAccount user : users ) {
			accountManager.removeSquareId(user.getUid(), squareId);
		}
	}

	public static boolean isUpdateUser(String uid, String squareId) throws Exception {
		IAccountManager manager = AuthenticationService.getInstance().getAccountManager();
		String updatePermission = manager.getAccountAttributeValue(uid, AccountAttributeName.UPDATE_PERMISSION);
		String registeredSquare = manager.getAccountAttributeValue(uid, AccountAttributeName.REGISTERED_SQUARE);
		boolean result = false;

		switch (updatePermission) {
			case "2":
				result = true;
				break;
			case "1":
				if(squareId.equals(registeredSquare)) result = true;
		}

		return result;
	}
}
