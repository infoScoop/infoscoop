/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.acl;

import java.io.Serializable;
import java.security.Principal;

public class ISPrincipal implements Principal, Serializable{
	public static String UID_PRINCIPAL = "UIDPrincipal";
	
	private String type;
	private String name;
	private String displayName;
	
	public ISPrincipal(String type, String name){
		this.type = type;
		this.name = name;
	}
	
	public String getType(){
		return this.type;
	}
	public String getName(){
		return this.name;
	}
	
	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}
	public String getDisplayName(){
		return this.displayName;
	}
	
	public String toString(){
		return "ISPrincipal:{type="+ type+ ", name=" + name + "}";
	}

	public boolean equals(Object obj) {
		if(obj == null)return false;
		if(obj instanceof ISPrincipal)return false;
		
		ISPrincipal p = (ISPrincipal)obj;
		boolean isNameEquals = false;
		if(this.name == null){
			if(p.getName() == null)
				isNameEquals = true;
		}else{
			isNameEquals = this.name.equals(p.getName());
		}
		boolean isTypeEquals = false;
		if(this.type == null){
			if(p.getType() == null)
				isTypeEquals = true;
		}else{
			isTypeEquals = this.type.equals(p.getType());
		}
		return isNameEquals && isTypeEquals;
	}

	public int hashCode() {
		if( this.name == null && this.type == null)return 0;
		return ( this.name + this.type ).hashCode();
	}
	
}
