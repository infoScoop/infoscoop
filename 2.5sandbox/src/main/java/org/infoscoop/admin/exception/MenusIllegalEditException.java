package org.infoscoop.admin.exception;

import java.util.List;

public class MenusIllegalEditException extends Exception{
	private static final long serialVersionUID = MenusIllegalEditException.class.getName().hashCode();
	List<String> errorSitetopIdList;
	
	public MenusIllegalEditException(List<String> errorSitetopIdList) {
		this.errorSitetopIdList = errorSitetopIdList;
	}
	
	public List<String> getErrorSitetopIdList() {
		return errorSitetopIdList;
	}
}
