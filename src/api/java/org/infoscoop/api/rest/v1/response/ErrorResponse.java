package org.infoscoop.api.rest.v1.response;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

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