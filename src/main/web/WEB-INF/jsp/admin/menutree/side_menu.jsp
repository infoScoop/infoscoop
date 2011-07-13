<%--
# infoScoop OpenSource
# Copyright (C) 2010 Beacon IT Inc.
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License version 3
# as published by the Free Software Foundation.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public
# License along with this program. If not, see
# <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>;.
--%>

<%@ page contentType="text/html; charset=UTF-8" %>

<div id="menu-side-bar" class="side-bar" >
</div>

<script>
	var switchTab = function(id){
		function buildFunc(){
			ISA_SiteAggregationMenu.treeMenu = new ISA_SiteAggregationMenu(id, false);
			ISA_SiteAggregationMenu.treeMenu.build();
		}
		var topmenuTab = document.getElementById("topmenu");
		var sidemenuTab = document.getElementById("sidemenu");
		if(id == "topmenu"){
			topmenuTab.className = "";
			topmenuTab.className = "sideBarTab-ui active";
			sidemenuTab.classname = "sideBarTab-ui";
		}else{
			sidemenuTab.className = "";
			sidemenuTab.className = "sideBarTab-ui active";
			topmenuTab.className = "sideBarTab-ui";
		}
	}
</script>