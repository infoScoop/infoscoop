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

IS_Portal.buildAccountManagerModal = function(parent) {
	var accountManagerDiv = $.DIV({
		id: 'portal-account-manager',
		className: 'portal-user-menu-iteml'
	}, "");
	var accountManagerA = $.A({
		className: 'portal-user-menu-link',
		href: '#',
		title: 'アカウント管理'
	});
	var accountManagerLabel = $.DIV({
		id: 'account-manager-icon',
		className: 'portal-user-menu-item-label'
	}, 'アカウント管理');
	accountManagerA.appendChild(accountManagerLabel);
	accountManagerDiv.appendChild(accountManagerA);
	parent.appendChild(accountManagerDiv);

	IS_Event.observe(accountManagerDiv, "click", function(){
		 if(!window["IS_AccountManagerInstance"]){
			IS_AccountManagerInstance = new IS_AccountManager();
		}else{
			IS_AccountManagerInstance.start();
		}
	});
}

IS_AccountManager = Class.create();
IS_AccountManager.prototype = {
	initialize: function(){
		this.accountManagerModal = new Control.Modal('', {
			overlayOpacity: 0.55,
			className: 'is-account-manager',
			height: 400,
			width: 600,
			fade: true,
			afterClose: function(){
				var formDiv = $('account-manager-body');
				if(formDiv) {
					formDiv.purge();
					formDiv.innerHTML = '';
					this.createAccountManagerForm(formDiv);
				}
			}.bind(this)
		});

		this.loadAccountManager();
	},

	start: function() {
		if(this.accountManagerModal) {
			this.accountManagerModal.open();
		}
	},

	finish: function() {
		if(this.accountManagerModal)
			this.accountManagerModal.close();
	},

	loadAccountManager: function(){
		var contentsDiv = $.DIV({
			className: 'account-manager-contents'
		});
		this.accountManagerModal.container.appendChild(contentsDiv);

		// header
		var headerDiv = $.DIV({
			className: 'account-manager-header-bk'
		});
		var titleDiv = $.DIV({
			className: 'account-manager-header'
		},'アカウント管理');

		var cancelImg = $.DIV({
			className: 'account-manager-cancel-image'
		});
		titleDiv.appendChild(cancelImg);
		headerDiv.appendChild(titleDiv);
		contentsDiv.appendChild(headerDiv);

		// create form
		var formDiv = $.DIV({
			id: 'account-manager-body'
		});
		contentsDiv.appendChild(formDiv);
		this.createAccountManagerForm(formDiv);
		IS_Event.observe(cancelImg, "click", this.finish.bind(this), false);

		this.start();
	},

	createAccountManagerForm: function(formDiv){
		if(!formDiv) return;

		var formDef = IS_Customization.accountManagerForm;

		// create profile form
		if(formDef.profile) {
			var profileDef = formDef.profile
			var formTable = this._createCategoryFieldSet(formDiv, 'プロフィール設定', 'account-manager-profile');

			for(var i in profileDef) {
				var item = profileDef[i];
				this._createFormRow(formTable, item, i);
			}
			this._createSubmitBtnRow(formTable, this._submit, 'account-manager-profile');
		}

		// create password form
		if(formDef.password) {
			var formTable = this._createCategoryFieldSet(formDiv, 'パスワード設定', 'account-manager-password');
			this._createFormRow(formTable, {title:'新しいパスワード', type:'password'}, 'pass');
			this._createFormRow(formTable, {title:'新しいパスワード(確認用)', type:'password'}, 'ispass');
			this._createSubmitBtnRow(formTable, this._submitPW, 'account-manager-password');
		}
	},

	_createCategoryFieldSet: function(formDiv, categoryLabel, id){
		var fieldSet = $.FIELDSET({});
		var legend = $.LEGEND({}, categoryLabel);
		fieldSet.appendChild(legend);
		formDiv.appendChild(fieldSet);

		var formTable = $.DIV({
			id: id,
			className: 'account-manager-form-table'
		});
		fieldSet.appendChild(formTable);

		return formTable;
	},

	_createFormRow: function(formTable, item, key){
		var formRow = $.DIV({
			className:'account-manager-form-row'
		});

		var label = $.DIV({}, item.title);
		var formInputDiv = $.DIV();

		// switch types
		// TODO
		var formInput = $.INPUT({
			id: key,
			name: key,
			type: item.type
		});
		if(item.value)
			formInput.value = item.value;
		formInputDiv.appendChild(formInput);

		formRow.appendChild(label);
		formRow.appendChild(formInputDiv);
		formTable.appendChild(formRow);
	},

	_createSubmitBtnRow: function(formTable, func, mapKey){
		var formRow = $.DIV({
			className:'account-manager-form-row'
		});
		var dummyDiv = $.DIV();
		var btnDiv = $.DIV();
		var okBtn = $.BUTTON({
			className: 'is-button',
		}, IS_R.lb_changeApply);

		formRow.appendChild(dummyDiv);
		formRow.appendChild(btnDiv);
		btnDiv.appendChild(okBtn);
		formTable.appendChild(formRow);

		IS_Event.observe(okBtn, "click", func.bind(this, mapKey), false);
	},


	_submitPW: function() {
		var pwVal = $('pass').value;
		// validation

		var opt = {
			method:'post',
			asynchronous: true,
			postBody: "password=" + pwVal,
			onSuccess: function(){
				this.finish();
			}.bind(this)
		}
		AjaxRequest.invoke(hostPrefix + '/accountmanagersrv/doChangePW', opt);
	},

	_submit: function(formKey) {
		var inputForm = $(formKey).getElementsByTagName('input');
		var body = '';
		for(var i = 0; i < inputForm.length; i++) {
			body += inputForm[i].id + '=' + inputForm[i].value + '&';
		}

		// validation

		var opt = {
			method:'post',
			asynchronous: true,
			postBody: body,
			onSuccess: function(){
				location.reload();
			}
		}
		AjaxRequest.invoke(hostPrefix + '/accountmanagersrv/doChange', opt);
	}
}