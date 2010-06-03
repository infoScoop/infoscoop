package org.infoscoop.batch.migration.v200to210;

import org.infoscoop.batch.migration.CSVBeanFactory;
import org.infoscoop.batch.migration.CSVField;
import org.infoscoop.dao.model.Searchengine;

public class SearchEngineFactory implements CSVBeanFactory {
	
	public Object newBean(CSVField[] values) throws Exception {
		Searchengine searchEngine = new Searchengine();
		searchEngine.setTemp(values[0].toInt());
		searchEngine.setData(values[1].toString());

		return searchEngine;
	}
}
