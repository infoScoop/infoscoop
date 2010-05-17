package org.infoscoop.account;

public class PrincipalDef {
	private String label;
	private String type;

	/**
	 * Definition of principal.
	 * @param type used for ISPrincipal#type
	 * @param label is describe ISPrincipal type when you set access control on administrator tool.
	 */
	public PrincipalDef(String type, String label){
		this.type = type;
		this.label = label;
	}

	public String getType(){
		return this.type;
	}
	public String getLabel(){
		return this.label;
	}

}
