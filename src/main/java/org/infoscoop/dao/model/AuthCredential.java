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

package org.infoscoop.dao.model;


import org.infoscoop.dao.model.base.BaseAuthCredential;
import org.infoscoop.util.StringUtil;
import org.json.JSONException;
import org.json.JSONObject;



public class AuthCredential extends BaseAuthCredential {
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
		java.lang.Integer fkDomainId,
		java.lang.String uid,
		java.lang.String authType,
		java.lang.String authUid) {

		super (
			id,
			fkDomainId,
			uid,
			authType,
			authUid);
	}

/*[CONSTRUCTOR MARKER END]*/

	public String getAuthPasswd() {
		return StringUtil.getNullSafe( super.getAuthPasswd() );
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
}
