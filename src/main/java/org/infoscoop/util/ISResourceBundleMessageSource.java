package org.infoscoop.util;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.context.support.ResourceBundleMessageSource;

public class ISResourceBundleMessageSource extends ResourceBundleMessageSource {
	public ResourceBundle getResourceBundle(String type, Locale locale){
		return super.getResourceBundle("i18n_" + type, locale);
	}

}
