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

import org.infoscoop.dao.model.base.BaseLogs;
import org.infoscoop.util.StringUtil;




public class Logs extends BaseLogs {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public Logs () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public Logs (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public Logs (
		java.lang.Long id,
		java.lang.String uid,
		java.lang.Integer type,
		java.lang.String url,
		java.lang.String urlKey,
		java.lang.String rssurl,
		java.lang.String rssurlKey,
		java.lang.String date,
		java.lang.String squareid) {

		super (
			id,
			uid,
			type,
			url,
			urlKey,
			rssurl,
			rssurlKey,
			date,
			squareid);
	}

/*[CONSTRUCTOR MARKER END]*/

	public String getUid() {
		return StringUtil.getNullSafe( super.getUid() );
	}
	public String getUrl() {
		return StringUtil.getNullSafe( super.getUrl() );
	}
	public String getUrlKey() {
		return StringUtil.getNullSafe( super.getUrlKey() );
	}
	public String getRssurl() {
		return StringUtil.getNullSafe( super.getRssurl() );
	}
	public String getRssurlKey() {
		return StringUtil.getNullSafe( super.getRssurlKey() );
	}
}
