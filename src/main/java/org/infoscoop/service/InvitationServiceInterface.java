package org.infoscoop.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface InvitationServiceInterface {
	public void doInvitation(List<String> uidList, HttpServletRequest request) throws Exception;
	public void cancelInvitation(List<String> uidList) throws Exception;
	public List getInvitingUsers();
	public boolean isExistsInvitationUser(String uid);
}
