package org.infoscoop.batch.migration.v200to210;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.infoscoop.dao.model.Proxyconf;

class ProxyConfFactory implements CSVBeanFactory {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd-HH.mm.ss.SSS");
	
	public ProxyConfFactory(){
	}

	public Proxyconf newBean(CSVField[] values) throws ParseException {
		Proxyconf proxyConf = new Proxyconf();
		proxyConf.setTemp(values[0].toInt());
		proxyConf.setData(values[1].toString());
		proxyConf.setLastmodified(parseDate(values[2]));
		return proxyConf;
	}

	private Date parseDate(CSVField value) throws ParseException {
		return DATE_FORMAT.parse(value.toString());
	}
}
