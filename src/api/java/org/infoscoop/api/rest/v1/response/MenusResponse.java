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

package org.infoscoop.api.rest.v1.response;

import org.infoscoop.api.rest.v1.response.model.Menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@JsonRootName("menuResponse")
@JsonInclude(Include.NON_DEFAULT)
@XStreamAlias("menus")
public class MenusResponse {

	@JsonProperty("topmenu")
	Menu topmenu;
	
	@JsonProperty("sidemenu")
	Menu sidemenu;

	public Menu getTopmenu() {
		return topmenu;
	}

	public void setTopmenu(Menu topmenu) {
		this.topmenu = topmenu;
	}

	public Menu getSidemenu() {
		return sidemenu;
	}

	public void setSidemenu(Menu sidemenu) {
		this.sidemenu = sidemenu;
	}
}
