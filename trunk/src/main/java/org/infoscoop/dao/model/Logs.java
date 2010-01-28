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
		java.lang.String date) {

		super (
			id,
			uid,
			type,
			url,
			urlKey,
			rssurl,
			rssurlKey,
			date);
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