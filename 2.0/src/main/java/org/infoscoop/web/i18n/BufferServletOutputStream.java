/*
 * $Id: BufferServletOutputStream.java,v 1.1 2007/12/27 01:59:29 komatsu Exp $
 *
 * Beacon-IT inicio Project
 * Copyright (c) 2004 by Beacon Information Technology, Inc.
 * 163-1507 Tokyo-to, Shinjuku-ku, Nishi-Shinjuku 1-6-1 Shinjuku L-Tower
 * All rights reserved.
 * ====================================================================
 */
package org.infoscoop.web.i18n;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * It is a ServletOutputStream only as for writing in it in OutputStream which it handed by a constructer.
 * @author Atsuhiko Kimura
 */
public class BufferServletOutputStream extends ServletOutputStream {
	private OutputStream os;

	public BufferServletOutputStream(OutputStream os) {
		this.os = os;
	}

	/**
	 * Writes the incoming data to both the output streams.
	 *
	 * @param value The int data to write.
	 * @throws IOException
	 */
	public void write(int value) throws IOException {
		os.write(value);
	}

	/**
	 * Writes the incoming data to both the output streams.
	 *
	 * @param value The bytes to write to the streams.
	 * @throws IOException
	 */
	public void write(byte[] value) throws IOException {
		os.write(value);
	}

	/**
	 * Writes the incoming data to both the output streams.
	 *
	 * @param b The bytes to write out to the streams.
	 * @param off The offset into the byte data where writing should begin.
	 * @param len The number of bytes to write.
	 * @throws IOException
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		os.write(b, off, len);
	}
}
