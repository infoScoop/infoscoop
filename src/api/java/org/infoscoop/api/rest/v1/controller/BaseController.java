/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.infoscoop.api.rest.v1.controller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.infoscoop.api.rest.v1.response.ErrorResponse;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public abstract class BaseController{
	private static Log log = LogFactory.getLog(BaseController.class);
    protected String API_VERSION = "v1";
	
	/**
	 * 不正なリクエストの処理を行います
	 * @param ex 
	 * @return
	 * @throws JSONException
	 */
	@ExceptionHandler(Exception.class)
	public ErrorResponse handleException(Exception ex) throws JSONException {
		log.error("unexpected error occurred.", ex);
		
		// TODO: アプリケーションのエラーコードを返すかどうか
		return new ErrorResponse(ex.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
	}
}
