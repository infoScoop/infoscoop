package org.infoscoop.api.rest.v1.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@JsonRootName("errorResponse")
@XStreamAlias("errorResponse")
public class ErrorResponse{
	
	@JsonProperty
	@XStreamAsAttribute
	String message;

	@JsonProperty
	@XStreamAsAttribute
	int code;
	
	public ErrorResponse(String message, int code) {
		this.message = message;
		this.code = code;
	}
}