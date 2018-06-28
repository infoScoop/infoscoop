package org.infoscoop.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.infoscoop.util.SpringUtil;

public class InvitationService implements InvitationServiceInterface {
	
	public static InvitationServiceInterface getHandle() {
		return (InvitationServiceInterface) SpringUtil.getBean("InvitationService");
	}
	
	@Override
	public List<String> doInvitation(List<String> uidList, HttpServletRequest request, String squareId) {
		throw new UnsupportedOperationException();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List getInvitingUsers() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void cancelInvitation(List<String> uidList) throws Exception {
		throw new UnsupportedOperationException();
	}

}
