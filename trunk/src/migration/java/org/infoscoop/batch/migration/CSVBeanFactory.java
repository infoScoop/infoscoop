package org.infoscoop.batch.migration;

public interface CSVBeanFactory {
	public Object newBean( CSVField[] record ) throws Exception;
}
