package org.infoscoop.batch.migration.v200to210;

public interface CSVBeanFactory {
	public Object newBean( CSVField[] record ) throws Exception;
}
