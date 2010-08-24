package org.infoscoop.dao.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.infoscoop.dao.model.base.BaseGadgetInstance;



public class GadgetInstance extends BaseGadgetInstance {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public GadgetInstance () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public GadgetInstance (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public GadgetInstance (
		java.lang.Integer id,
		java.lang.String type,
		java.lang.String title,
		java.lang.String href) {

		super (
			id,
			type,
			title,
			href);
	}

/*[CONSTRUCTOR MARKER END]*/
	/*
	private void addUserPref(String name, String value){
		GadgetInstanceUserpref up = new GadgetInstanceUserpref(
				new org.infoscoop.dao.model.GadgetInstanceUserprefPK(this, name));
		up.setValue(value);
		super.getGadgetInstanceUserPrefs().add(up);
	}
	*/
	
	Map<String, String> userPrefs ;
	/*
	public void setUserPrefs(Map<String, String> ups){
		Set<GadgetInstanceUserpref> userPrefs = new HashSet<GadgetInstanceUserpref>();
		
		for(Map.Entry<String, String> up : ups.entrySet()){
			GadgetInstanceUserpref giup = new GadgetInstanceUserpref(
					new org.infoscoop.dao.model.GadgetInstanceUserprefPK(this, up.getKey()));
			giup.setValue(up.getValue());
			userPrefs.add(giup);
		}
		super.setGadgetInstanceUserPrefs(userPrefs);
	}
	*/
	public Map<String, String> getUserPrefs(){
		if(this.userPrefs != null){
			return this.userPrefs;
		}else{
			this.userPrefs = new UPMap(this);
			for(GadgetInstanceUserpref up : super.getGadgetInstanceUserPrefs()){
				userPrefs.put(up.getId().getName(), up.getValue());
			}
			return this.userPrefs;
		}
	}
	
	public class UPMap extends HashMap<String, String>{
		GadgetInstance self;
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public UPMap(GadgetInstance gadgetInstance) {
			this.self = gadgetInstance;
		}

		@Override
		public String put(String name, String value) {
			super.put(name, value);
			GadgetInstanceUserpref up = new GadgetInstanceUserpref(
					new org.infoscoop.dao.model.GadgetInstanceUserprefPK(this.self, name));
			up.setValue(value);
			this.self.getGadgetInstanceUserPrefs().add(up);
			return name;
		}
	}
}