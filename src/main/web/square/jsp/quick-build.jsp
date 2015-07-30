<%--
# infoScoop Calendar
# Copyright (C) 2010 Beacon IT Inc.
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see
# <http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>.
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<form  id="quick-build" action="../squaresrv/doCreate" method="post">
	<div class="well col-sm-12">
		<div class="form-group">
			<label>%{lb_square_name}</label>
			<span class="required-label">［%{lb_required}］</span>
			<input type="text" id="square-name" name="square-name" class="form-control" placeholder="%{lb_square_name}"maxlength="100" onChange="checkValue(this)"/>
		</div>
		<div class="form-group">
			<label>%{lb_square_desc}</label>
			<textarea id="square-description" class="form-control"name="square-description" placeholder="%{lb_plz_input_square_desc}"></textarea>
		</div>
		<div class="form-group">
			<label>%{lb_input_square_member_per_line}</label>
			<textarea id="square-member" class="form-control" name="square-member" placeholder="%{lb_email_address}"></textarea>
		</div>
	</div>
	<div class="form-group col-sm-12">
		<input type="hidden" id="square-source" name="square-source" />
		<button type="submit" class="btn btn-primary col-sm-12">%{lb_create_square}</button>
	</div>
</form>