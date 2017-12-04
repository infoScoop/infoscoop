package org.infoscoop.request.method;

import org.apache.commons.httpclient.methods.PostMethod;

public class PatchMethod extends PostMethod {

	public PatchMethod(String url) {
		super(url);
	}

	@Override
	public String getName() {
		return "PATCH";
	}
}