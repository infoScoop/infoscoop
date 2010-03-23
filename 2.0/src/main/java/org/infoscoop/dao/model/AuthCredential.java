package org.infoscoop.dao.model;


import org.infoscoop.dao.model.base.BaseAuthcredential;
import org.infoscoop.util.StringUtil;
import org.json.JSONException;
import org.json.JSONObject;



public class AuthCredential extends BaseAuthcredential {
	private static final long serialVersionUID = 1L;
	public static final Integer LOGIN_AUTH_CREDENTIAL = new Integer(-1);
	public static final Integer COMMON_AUTH_CREDENTIAL = new Integer(0);

/*[CONSTRUCTOR MARKER BEGIN]*/
	public AuthCredential () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public AuthCredential (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public AuthCredential (
		java.lang.Long id,
		java.lang.String uid,
		java.lang.Integer sysNum,
		java.lang.String authtype,
		java.lang.String authuid,
		java.lang.String authpasswd) {

		super (
			id,
			uid,
			sysNum,
			authtype,
			authuid,
			authpasswd);
	}

	public Object toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", this.getId());
		json.put("authType", this.getAuthType());
		json.put("sysNum", this.getSysNum());
		json.put("authUid", this.getAuthUid());
		json.put("authPasswd", this.getAuthPasswd());
		if(this.getAuthDomain() != null){
			json.put("authDomain", this.getAuthDomain());
		}
		return json;
	}

/*[CONSTRUCTOR MARKER END]*/

	public String getAuthPasswd() {
		return StringUtil.getNullSafe( super.getAuthPasswd() );
	}
}