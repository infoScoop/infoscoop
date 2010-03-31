package org.infoscoop.widgetconf;

import org.infoscoop.util.Xml2JsonListener;

public class I18NListener implements Xml2JsonListener {
	private I18NConverter i18n;

	public I18NListener(I18NConverter i18n) {
		this.i18n = i18n;
	}

	public String text(String text) throws Exception {
		return i18n.replace(text);
	}
	

}
