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
			<label>スクエア名</label>
			<span class="required-label">［必須］</span>
			<input type="text" id="square-name" name="square-name" class="form-control" placeholder="UNIRITAスクエア"maxlength="100" onChange="checkValue(this)"/>
		</div>
		<div class="form-group">
			<label>スクエアの説明</label>
			<textarea id="square-description" class="form-control"name="square-description" placeholder="スクエアの説明を記入してください。"></textarea>
		</div>
		<div class="form-group">
			<label>スクエアのメンバー（メールアドレスを1行ごとに記入）</label>
			<textarea id="square-member" class="form-control" name="square-member" placeholder="unirita@unirita.co.jp"></textarea>
		</div>
	</div>
	<div class="form-group col-sm-12">
		<input type="hidden" id="square-source" name="square-source" />
		<button type="submit" class="btn btn-primary col-sm-12">スクエアを作成する</button>
	</div>
</form>