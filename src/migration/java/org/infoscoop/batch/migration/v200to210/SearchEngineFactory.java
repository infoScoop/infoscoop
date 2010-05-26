package org.infoscoop.batch.migration.v200to210;

import org.infoscoop.dao.model.Searchengine;

class SearchEngineFactory implements CSVBeanFactory {
	public Searchengine newBean(CSVField[] values) {
		Searchengine searchEngine = new Searchengine();
		searchEngine.setTemp(values[0].toInt());
		searchEngine.setData(values[1].toString());

		return searchEngine;
	}
}
