package org.infoscoop.web;

public interface HttpStatusCode {
	String HEADER_NAME = "MSDPortal-Status";
	
	String MSD_SC_CONTENT_PARSE_ERROR = "10550";

	String MSD_SC_TIMEOUT = "10408";

	/**
	 * An error code when forced reloading was necessary at initialization of the customized information.
	 */
	String MSD_FORCE_RELOAD = "10997";
	
	/**
	 * An error code when logged in by the other browser and did it, and session ID changed.
	 */
	String MSD_INVALID_SESSION = "10998";

	/**
	 * An error code when session time-out occurred.
	 */
	String MSD_SESSION_TIMEOUT = "10999";
}
