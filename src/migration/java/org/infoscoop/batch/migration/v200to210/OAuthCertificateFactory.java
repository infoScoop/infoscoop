package org.infoscoop.batch.migration.v200to210;

import org.infoscoop.batch.migration.CSVBeanFactory;
import org.infoscoop.batch.migration.CSVField;
import org.infoscoop.dao.model.OAuthCertificate;

public class OAuthCertificateFactory implements CSVBeanFactory {
	
	public Object newBean(CSVField[] values) throws Exception {
		OAuthCertificate oauthcert = new OAuthCertificate();
		oauthcert.setConsumerKey(values[0].toString());
		oauthcert.setPrivateKey(values[1].toString());
		oauthcert.setCertificate(values[2].toString());

		return oauthcert;
	}
}
