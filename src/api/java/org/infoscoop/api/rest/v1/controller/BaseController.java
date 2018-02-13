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
import org.infoscoop.api.ISAPIException;
import org.infoscoop.api.rest.v1.response.ErrorResponse;
import org.infoscoop.util.spring.TextView;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public abstract class BaseController{
	private static Log log = LogFactory.getLog(BaseController.class);
    protected String API_VERSION = "v1";
	
    @ExceptionHandler(ISAPIException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public TextView handleBadRequest(Exception ex) throws JSONException, JsonProcessingException {
		log.error("bad request.", ex);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
		return createJsonResponseView(json);
	}
    
    @ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public TextView handleException(Exception ex) throws JSONException, JsonProcessingException {
		log.error("unexpected error occurred.", ex);
		ex.printStackTrace();
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
		return createJsonResponseView(json);
	}
    
    public TextView createXmlResponseView(String str){
		TextView view = new TextView();
		view.setResponseBody(str);
		view.setContentType("application/xml; charset=UTF-8");
		
		return view;
	}

    public TextView createJsonResponseView(String str){
		TextView view = new TextView();
		view.setResponseBody(str);
		view.setContentType("application/json; charset=UTF-8");
		
		return view;
	}
}
