package org.infoscoop.util;

import org.json.JSONString;

public class JSONScript implements JSONString {
	private String script;

	public JSONScript(String script) {
		this.script = script;
	}

	public String toJSONString() {
		return script;
	}

}
