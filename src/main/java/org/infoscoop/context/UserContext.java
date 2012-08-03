/* infoScoop OpenSource
 * Copyright (C) 2012 Beacon IT Inc.
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

package org.infoscoop.context;

public abstract class UserContext {
	private static ThreadLocal instance = new ThreadLocal() {
		protected Object initialValue(){
			return null;
		}
	};
	
	public abstract UserInfo getUserInfo();
	
    @SuppressWarnings("unchecked")  
    public static UserContext instance() {  
        UserContext ctx = (UserContext)instance.get();  
        if (ctx != null) {  
            return ctx;  
        }  
          
        instance.set(new UserContextImpl());  
        return (UserContext)instance.get();  
    } 
}

class UserContextImpl extends UserContext {  
    private UserInfo userInfo;  
    //get UserInfo  
    public UserInfo getUserInfo() {  
        if (userInfo == null) {  
            userInfo = new UserInfo();  
        }  
  
        return userInfo;  
    }  
} 
