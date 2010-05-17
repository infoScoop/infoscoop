/*
 * $Id: BufferHttpServletResponseWrapper.java,v 1.2 2009/05/20 06:36:06 nishiumi Exp $
 *
 * Beacon-IT inicio Project
 * Copyright (c) 2004 by Beacon Information Technology, Inc.
 * 163-1507 Tokyo-to, Shinjuku-ku, Nishi-Shinjuku 1-6-1 Shinjuku L-Tower
 * All rights reserved.
 * ====================================================================
 */
package org.infoscoop.web.i18n;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * If you wrap it in this class, we write in it on a memory first of all without writing in it at a real HTTP response.<br>
 * The data which wrote in can get by getByteContent() or getStringContent(). Please execute flushBuffer() before get it.<br>
 *
 * @author Atsuhiko Kimura
 */
public class BufferHttpServletResponseWrapper
	extends HttpServletResponseWrapper {
	private final Log log = LogFactory.getLog(this.getClass());

	/**
	 * We cache the printWriter so we can maintain a single instance
	 * of it no matter how many times it is requested.
	 */
	private PrintWriter bufWriter;
	private ByteArrayOutputStream bufOut;
	private int status = SC_OK;
	private Locale locale = null;

	/**
	 * Constructor
	 *
	 * @param response The servlet response
	 */
	public BufferHttpServletResponseWrapper(HttpServletResponse response) {
		super(response);
		bufOut = new ByteArrayOutputStream();
	}

	/**
	 * Set the date of a header
	 *
	 * @param name The header name
	 * @param value The date
	 */
	public void setDateHeader(String name, long value) {
		if (log.isDebugEnabled()) {
			log.debug("dateheader: " + name + ": " + value);
		}

		super.setDateHeader(name, value);
	}

	/**
	 * Set a header field
	 *
	 * @param name The header name
	 * @param value The header value
	 */
	public void setHeader(String name, String value) {
		if (log.isDebugEnabled()) {
			log.debug("header: " + name + ": " + value);
		}

		super.setHeader(name, value);
	}

	/**
	 * Set the int value of the header
	 *
	 * @param name The header name
	 * @param value The int value
	 */
	public void setIntHeader(String name, int value) {
		if (log.isDebugEnabled()) {
			log.debug("intheader: " + name + ": " + value);
		}

		super.setIntHeader(name, value);
	}

	/**
	 * We override this so we can catch the response status. Only
	 * responses with a status of 200 (<code>SC_OK</code>) will
	 * be cached.
	 */
	public void setStatus(int status) {
		super.setStatus(status);
		this.status = status;
	}

	/**
	 * We override this so we can catch the response status. Only
	 * responses with a status of 200 (<code>SC_OK</code>) will
	 * be cached.
	 */
	public void sendError(int status, String string) throws IOException {
		super.sendError(status, string);
		this.status = status;
	}

	/**
	 * We override this so we can catch the response status. Only
	 * responses with a status of 200 (<code>SC_OK</code>) will
	 * be cached.
	 */
	public void sendError(int status) throws IOException {
		super.sendError(status);
		this.status = status;
	}

	/**
	 * We override this so we can catch the response status. Only
	 * responses with a status of 200 (<code>SC_OK</code>) will
	 * be cached.
	 */
	public void setStatus(int status, String string) {
		super.setStatus(status, string);
		this.status = status;
	}

	public void sendRedirect(String location) throws IOException {
		//this.status = SC_MOVED_TEMPORARILY;
		super.sendRedirect(location);
	}

	/**
	 * Retrieves the captured HttpResponse status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set the locale
	 *
	 * @param value The locale
	 */
	public void setLocale(Locale value) {
		super.setLocale(value);
		this.locale = value;
	}

	/**
	 * Get an output stream
	 *
	 * @throws IOException
	 */
	public ServletOutputStream getOutputStream() throws IOException {
		return new BufferServletOutputStream(bufOut);
	}

	/**
	 * Get a print writer
	 *
	 * @throws IOException
	 */
	public PrintWriter getWriter() throws IOException {
		if (bufWriter == null) {
			//Create Writer if the character encoding is specified
			if (super.getCharacterEncoding() != null) {
				bufWriter =
					new PrintWriter(
						new OutputStreamWriter(
							getOutputStream(),
							super.getCharacterEncoding()));
			} else {
				bufWriter = new PrintWriter(getOutputStream());
			}
		}

		return bufWriter;
	}

	public void flushBuffer() throws IOException {
		if (bufOut != null) {
			bufOut.flush();
		}

		if (bufWriter != null) {
			bufWriter.flush();
		}
	}

	public byte[] getByteContent() {
		return bufOut.toByteArray();
	}

	public String getStringContent() throws IOException {
		if (super.getCharacterEncoding() != null) {
			return new String(
				bufOut.toByteArray(),
				super.getCharacterEncoding());
		}
		return bufOut.toString();
	}

	public int getContentLength() {
		return bufOut.size();
	}

	/**
	 * @return
	 */
	public Locale getLocale() {
		return locale;
	}

}
