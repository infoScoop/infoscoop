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
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<form id="editRole" method="post" action="/" class="cssform" onsubmit="return false;">
	<div id="infoscoop" class="infoScoop">

		<div class="ui-widget ui-widget-content ui-corner-all" style="margin-top:10px;padding:3px;">
			<div class="ui-widget-header ui-corner-all ui-helper-clearfix" style="padding:5px;font-size:1.1em;">
				<span>%{alb_tabSetting}</span>
			</div>
			<div style="padding:5px;">
				<ul>
					<!--
					<li>
						<label>%{alb_roleName}</label>
						<input id="roleName" name="roleName"/>
					</li>
					<li>
						<label>%{alb_subject}</label>
						<div id="principalTypeDiv" name="principalTypeDiv"/>
					</li>
					<li>
						<label>%{alb_regularExpression}</label>
						<input id="role" name="role">
					</li>
					-->
					<li>
						<label>%{alb_roleName}:</label>
						<span id="roleName">&nbsp;</span>
					</li>
					<li>
						<label>%{alb_subject}:</label>
						<span id="principalTypeDiv">&nbsp;</span>
					</li>
					<li>
						<label>%{alb_regularExpression}:</label>
						<span id="role">&nbsp;</span>
					</li>
					<li>
						<label>%{alb_tabName}:</label>
						<input id="tabName" name="tabName" maxlength="80"/>
					</li>
					<li>
						<label>%{alb_selectDisplayArea}</label>
						<select id="areaType" name="areaType">
							<option value="0">%{alb_useBothArea}</option>
							<option value="1">%{alb_disableCustomizedArea}</option>
							<option value="2">%{alb_adjustToWindowHeight}</option>
						</select>
					</li>
				</ul>
			</div>
		</div>

		<div style="clear:both;text-align:center;margin-bottom:10px;">
			<input class="submit_button" type="button" name="button" value="%{alb_backToList}" />
			<!--
			<input class="cancel_button" type="button" name="button" value="%{alb_cancel}" />
			-->
		</div>

		<table>
			<tr>
				<td id="portal-site-aggregation-menu"></td>
			</tr>
		</table>
		<div id="portal-tree-menu" style="float:left;"> </div>

		<div id="portal-maincontents-table" style="float:left;">
			<div id="fixedArea" class="ui-widget ui-widget-content ui-corner-all" style="padding:3px;">
				<div class="ui-widget-header ui-corner-all ui-helper-clearfix" style="padding:5px;font-size:1.1em;">
					<span>%{alb_fixedArea}</span>
				</div>

				<div style="padding:5px;">
					<div style="width:100%">
						<input id="select_layout_link" type="button" value="%{alb_selectLayout}">
						<input id="edit_layout_link" type="button" value="%{alb_editHTML}">
						<div id="staticAreaContainer">&nbsp;</div>
					</div>
				</div>
				<div style="clear:both;"></div>
			</div>
			
			<div id="customizedArea" class="ui-widget ui-widget-content ui-corner-all" style="display:none;">
				<div class="ui-widget-header ui-corner-all ui-helper-clearfix" style="padding:5px;font-size:1.1em;">
					<span>%{alb_customizedArea}</span>
				</div>

				<div style="padding:5px;">
					<div>
						<span>%{alb_column}</span>
						<select name="numberOfColumns" id="numberOfColumns">
							<option value="1"/>1</option>
							<option value="2"/>2</option>
							<option value="3"/>3</option>
							<option value="4"/>4</option>
							<option value="5"/>5</option>
							<option value="6"/>6</option>
							<option value="7"/>7</option>
							<option value="8"/>8</option>
							<option value="9"/>9</option>
							<option value="10"/>10</option>
						</select>
					</div>
					<div id="personarizeAreaContainer" style="padding:5px;">
						<div id="panels">
							<div id="tab-container"></div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div style="clear:both;text-align:center;">
			<input class="submit_button" type="button" name="button" value="%{alb_backToList}" />
			<!--
			<input class="cancel_button" type="button" name="button" value="%{alb_cancel}" />
			-->
		</div>
	</div>
</form>
