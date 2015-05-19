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

import org.infoscoop.api.dao.model.base.BaseOAuth2ProviderRefreshToken;

public class OAuth2ProviderRefreshToken extends BaseOAuth2ProviderRefreshToken {
	private static final long serialVersionUID = 1L;

	public OAuth2ProviderRefreshToken() {
		super();
	}

	public OAuth2ProviderRefreshToken(OAuth2ProviderRefreshTokenPK id) {
		super(id);
	}

}
