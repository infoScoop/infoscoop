package org.infoscoop.dao.model;

public interface StaticGadget {

	GadgetInstance getGadgetInstance();

	String getContainerId();
	boolean isIgnoreHeaderBool();
	boolean isNoBorderBool();

}
