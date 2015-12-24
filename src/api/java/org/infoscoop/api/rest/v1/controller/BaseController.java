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

package org.infoscoop.api.rest.v1.controller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.api.dao.OAuth2ProviderAccessTokenDAO;
import org.infoscoop.api.dao.model.OAuth2ProviderAccessToken;
import org.infoscoop.api.rest.v1.response.ErrorResponse;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public abstract class BaseController{
	private static Log log = LogFactory.getLog(BaseController.class);
    protected String API_VERSION = "v1";

	@Autowired
	private OAuth2ProviderAccessTokenDAO oAuth2ProviderAccessTokenDAO;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleException(Exception ex) throws JSONException {
		log.error("unexpected error occurred.", ex);
		
		// TODO: アプリケーションのエラーコードを返すかどうか
		return new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	protected String getSquareId() {
		AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
		String key = authenticationKeyGenerator.extractKey((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication());
		OAuth2ProviderAccessToken oAuth2ProviderAccessToken = oAuth2ProviderAccessTokenDAO.getAccessTokenByAuthenticationId(key);

		return oAuth2ProviderAccessToken.getSquareId();
	}
}
