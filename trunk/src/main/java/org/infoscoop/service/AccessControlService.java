package org.infoscoop.service;

public interface AccessControlService {
	void registerRssSite(String url, boolean isPublic, String[] roleList);
	void registerRssItem(String rssUrl, String url, boolean isPublic, String roleList[]);
	
	boolean checkVisibility(String url);
	String[] checkVisibility(String[] urlList);
}
