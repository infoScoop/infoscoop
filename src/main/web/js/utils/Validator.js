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

var IS_Validator = {};
/**
 * 
 * @param {String} value
 * @param {Object} opt 
 *  label: label that gives error message
 *  required : true, in case of required item
 *  regex : regex specifies regular expressions by string
 *  regexMsg : message for the case of failure in checking regular expressions
 *  maxLength : maxLength specifies the number of characters by numeric value
 *  maxBytes : maxBytes specifies the number of bytes by numeric value
 *  format : charset,regexp,datefmt
 * @return error message, or return null if no problems are found
 */
IS_Validator.validate = function(value, opt){
	var prefix = opt.label ? "["+opt.label+"] " : "";
	if(value == null || value.length == 0) {
		if(opt.required)
			
			return prefix + IS_R.ms_requiredItem2;
		return null;
	}
	if(opt.regex && !new RegExp(opt.regex).test(value)){

		return prefix + (opt.regexMsg ? opt.regexMsg : IS_R.ms_invalidFormat);

	}
	if(opt.maxLength && value.length > opt.maxLength){

		return prefix + IS_R.getResource(IS_R.ms_inputTooLong, [opt.maxLength]);;
	}
	if(opt.maxInt && parseInt(value) > opt.maxInt){

		return prefix + IS_R.getResource(IS_R.ms_inputOverMaxValue, [opt.maxInt]);
	}
	if(opt.datatype == 'int'){
		var error = value > 2147483647;
		if(error) return prefix + IS_R.ms_overMaxIntValue;
	}
	if(opt.maxBytes) {
		var error = IS_Validator.checkByServer(value, 'maxLength', opt.maxBytes);
		if(error) return prefix + error;
	}
	if(opt.format){
		var error = IS_Validator.checkByServer(value, opt.format);
		if(error) return prefix + error;
	}
	return null;
}
/**
 * 
 * @param {Object} value
 * @param {String} method :<valication type> maxlength,regexp,charset,datefmt
 * @param {Number} length :<max length to check the length>
 * @return error message, or return null if no problems are found
 */
IS_Validator.checkByServer = function(value, method, length){
	var error = false;
	var url = hostPrefix + "/validation";
	var ajaxOpt = {
		method: 'post' ,
		asynchronous:false,
		parameters: "method=" + method + "&text=" + encodeURIComponent(value) + ( (length) ? "&length=" + length : ""),
		onSuccess: function(response){
			var result = eval(response.responseText);
			if(!result){
				error = IS_Validator.getErrorMsg(method, length);
			}
		},
		onFailure: function(t) {

			error = IS_R.ms_validateError + t.status + " - " + t.statusText;
		},
		onException: function(r, t){

			error = IS_R.ms_validateError + getErrorMessage(t);
		}
	};
	AjaxRequest.invoke(url, ajaxOpt);
	return error;
}
IS_Validator.getErrorMsg = function(method, length){
	switch(method){
		case 'maxLength':

			return IS_R.getResource(IS_R.ms_inputTooLong2, [length]);
		case 'regexp':

			return IS_R.ms_invalidRegexp;
		case 'charset':

			return IS_R.ms_invalidCharset;
		case 'datefmt':

			return IS_R.ms_invalidDateFormat;
	}

	return IS_R.ms_invalidFormat;
}
