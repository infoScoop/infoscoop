package org.infoscoop.util.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

public class TextView extends AbstractView {
	private String body;

	public void setResponseBody(String body) {
		this.body = body;
	}

	public String getContentType() {
		String contentType = super.getContentType();
		if(contentType == null)
			return "text/plain; charset=UTF-8";
		return contentType;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> map,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType(super.getContentType());
		response.getWriter().write(body);
	}
}
