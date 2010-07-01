package org.infoscoop.batch.migration.v1300to1400;

/**
 * @deprecated
 */
public interface CSVBeanFactory {
	public Object newBean( CSVField[] record ) throws Exception;
}
