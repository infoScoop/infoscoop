package org.infoscoop.util;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class NoOpEntityResolver implements EntityResolver {
	private static final NoOpEntityResolver instance = new NoOpEntityResolver();

	private NoOpEntityResolver() {
	}

	public static NoOpEntityResolver getInstance() {
		return instance;
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		return new InputSource(new StringReader(""));
	}
}
