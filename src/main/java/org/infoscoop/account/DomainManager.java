package org.infoscoop.account;

public class DomainManager {
	private static ThreadLocal<Integer> contextDomainId = new ThreadLocal<Integer>();
	
	/**
	 * get the subject persion from all registered SecurityAgent.
	 * @param uid
	 * @return
	 */
	public static void registerContextDomainId(Integer DomainId){
		
		contextDomainId.set(DomainId);
	}

	/**
	 * delete the Subject that registered in a current thread.
	 */
	public static void clearContextDomainId(){
		contextDomainId.set(null);
	}

	/**
	 * get the Subject that registered in a current thread.
	 * @return
	 */
	public static Integer getContextDomainId(){
		return contextDomainId.get();
	}
}
