package org.infoscoop.batch.migration.v_1_2_0_4_to_1_2_1_0;

/**
 * @deprecated 
 */
public interface CSVBeanFactory {
	public Object newBean( String[] record ) throws Exception;
}
