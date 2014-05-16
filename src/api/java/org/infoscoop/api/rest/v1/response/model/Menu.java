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

package org.infoscoop.api.rest.v1.response.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Menu {
	
    @XStreamAlias("items")
	@JsonProperty("items")
    Map<String, Map<String,Object>> items;

    @XStreamAlias("siteTopArray")
	@JsonProperty("siteTopArray")
    List<String> siteTopArray;

    @XStreamAlias("keyMap")
	@JsonProperty("keyMap")
    Map<String,List<String>> keyMap;
	
	public Menu(Map<String, Map<String,Object>> items, List<String> siteTopArray, Map<String,List<String>> keyMap){
		this.items = items;
		this.siteTopArray = siteTopArray;
		this.keyMap = keyMap;
	}

	public Map<String, Map<String,Object>> getItems() {
		return items;
	}

	public void setItems(Map<String, Map<String,Object>> items) {
		this.items = items;
	}

	public List<String> getSiteTopArray() {
		return siteTopArray;
	}

	public void setSiteTopArray(List<String> siteTopArray) {
		this.siteTopArray = siteTopArray;
	}

	public Map<String,List<String>> getKeyMap() {
		return keyMap;
	}

	public void setKeyMap(Map<String,List<String>> keyMap) {
		this.keyMap = keyMap;
	}
}
