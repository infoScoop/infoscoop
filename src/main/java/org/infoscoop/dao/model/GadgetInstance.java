package org.infoscoop.dao.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.infoscoop.account.DomainManager;
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
		java.lang.String title) {

		super (
			id,
			type,
			title);
	}

/*[CONSTRUCTOR MARKER END]*/
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		super.initialize();
		super.setFkDomainId(DomainManager.getContextDomainId());
	}
	
	Map<String, String> userPrefs ;
	
	public Map<String, String> getUserPrefs(){
		if(this.userPrefs != null){
			return this.userPrefs;
		}else{
			this.userPrefs = new UPMap(this);
			if (super.getGadgetInstanceUserPrefs() != null) {
				for (GadgetInstanceUserpref up : super
						.getGadgetInstanceUserPrefs()) {
					userPrefs.put(up.getId().getName(), up.getValue());
				}
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
					new org.infoscoop.dao.model.GadgetInstanceUserprefPK(
							this.self, name));
			up.setValue(value);
			if (this.self.getGadgetInstanceUserPrefs() == null)
				this.self
						.setGadgetInstanceUserPrefs(new HashSet<GadgetInstanceUserpref>());
			this.self.addTogadgetInstanceUserPrefs(up);
			return name;
		}
	}
	
	public String getGadgetType(){
		String type = super.getType();
		return (type.indexOf("upload_") == 0 ? "g_" + type + "/gadget" : type);
	}
}