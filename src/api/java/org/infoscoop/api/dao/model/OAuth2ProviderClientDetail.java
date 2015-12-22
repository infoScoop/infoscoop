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

package org.infoscoop.api.dao.model;

import org.infoscoop.api.dao.model.base.BaseOAuth2ProviderClientDetail;

public class OAuth2ProviderClientDetail extends BaseOAuth2ProviderClientDetail {
	private static final long serialVersionUID = 1L;

	public OAuth2ProviderClientDetail() {
		super();
	}
	
	public OAuth2ProviderClientDetail (java.lang.String id) {
		super(id);
	}
	
	public OAuth2ProviderClientDetail (java.lang.String id, String title, String secret, String squareId) {
		super(id,title,secret, squareId);
	}
}
