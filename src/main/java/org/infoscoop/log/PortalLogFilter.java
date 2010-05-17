package org.infoscoop.log;

import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class PortalLogFilter extends Filter {
	public static final String LOGTYPE_OPTION = "LogType";
	public static final String ACCEPT_ON_MATCH_OPTION = "AcceptOnMatch";
	
	public static final String LOGTYPE_MAIN = "portal-main";
	public static final String LOGTYPE_ADMIN = "portal-admin";

	private String logType;
	private boolean acceptOnMatch;

	public int decide(LoggingEvent event) {
		String logType = (String) event.getMDC("logType");
		
		if(logType == null){
			return Filter.ACCEPT;
		}
		
		if (!logType.equalsIgnoreCase(this.logType)) {
			return Filter.NEUTRAL;
		} else {
			if (acceptOnMatch) {
				return Filter.ACCEPT;
			} else {
				return Filter.DENY;
			}
		}
	}

	public void setOption(String key, String value) {

		if (key.equalsIgnoreCase(LOGTYPE_OPTION)) {
			this.logType = value;
		} else if (key.equalsIgnoreCase(ACCEPT_ON_MATCH_OPTION)) {
			this.acceptOnMatch = OptionConverter.toBoolean(value, acceptOnMatch);
		}
	}

	public String getLogType() {
		return this.logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public void setAcceptOnMatch(boolean acceptOnMatch) {
		this.acceptOnMatch = acceptOnMatch;
	}

	public boolean getAcceptOnMatch() {
		return acceptOnMatch;
	}
}
