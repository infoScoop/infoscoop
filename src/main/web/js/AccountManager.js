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
		title: IS_R.lb_account_setting
	});
	var accountManagerLabel = $.DIV({
		id: 'account-manager-icon',
		className: 'portal-user-menu-item-label'
	}, IS_R.lb_account_setting);
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
		},IS_R.lb_account_setting);

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
			var formTable = this._createCategoryFieldSet(formDiv, IS_R.lb_account_setting_profile, 'account-manager-profile');

			for(var i in profileDef) {
				var item = profileDef[i];
				var formInput = $.INPUT({
					id: i,
					name: i,
					type: item.type,
					maxLength: 100
				});
				if(item.value)
					formInput.value = item.value;
				this._createFormRow(formTable, item.title, formInput);
			}
			this._createSubmitBtnRow(formTable, this._submit, 'account-manager-profile', profileDef);
		}

		// create default square form
		if(IS_Portal.useMultitenantMode && formDef.defaultsquare) {
			var squareDef = formDef.defaultsquare;
			var formTable = this._createCategoryFieldSet(formDiv,  IS_R.lb_account_setting_default_square, 'account-manager-default-square');

			var defaultSquare = squareDef.defaultSq;
			var select = $.SELECT({id: 'default-square-selecter', name: 'default-square-selecter', style: 'width: 150px;'});
			var defaultSquareOption = $.OPTION({value: defaultSquare.id}, defaultSquare.name);
			select.appendChild(defaultSquareOption);

			var belongSquare = squareDef.belong;
			if(belongSquare.length > 0) {
				for(var i = 0; i < belongSquare.length; i++) {
					var item = belongSquare[i];
					var squareOption = $.OPTION({value: item.id}, item.name);
					select.appendChild(squareOption);
				}
			}

			this._createFormRow(formTable, IS_R.lb_default_square, select);
			this._createSubmitBtnRow(formTable, this._submitSelect, 'default-square-selecter', defaultSquare);
		}

		// create password form
		if(formDef.password) {
			var formTable = this._createCategoryFieldSet(formDiv, IS_R.lb_account_setting_password, 'account-manager-password');
			var passInput = $.INPUT({
				id: 'pass',
				name: 'pass',
				type:'password'
			});
			var confirmInput = $.INPUT({
				id: 'confirm-pass',
				name: 'confirm-pass',
				type:'password'
			});
			this._createFormRow(formTable, IS_R.lb_new_password, passInput);
			this._createFormRow(formTable, IS_R.lb_new_password_confirm, confirmInput);

			// ToDo
			if(IS_Portal.passwordPolicy) {
				var formRow = $.DIV({
					className:'account-manager-form-caption'
				});
				formRow.innerHTML = IS_R.lb_password_policy;
				formTable.appendChild(formRow);
			}

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

	_createFormRow: function(formTable, title, formInput, inputFunc){
		var formRow = $.DIV({
			className:'account-manager-form-row'
		});

		var label = $.DIV({}, title);
		var formInputDiv = $.DIV();
		formInputDiv.appendChild(formInput);

		formRow.appendChild(label);
		formRow.appendChild(formInputDiv);
		formTable.appendChild(formRow);

		if(inputFunc)
			IS_Event.observe(formInput, "input", inputFunc.bind(formInput), false);
	},

	_createSubmitBtnRow: function(formTable, clickFunc, mapKey, formDef){
		var formRow = $.DIV({
			className:'account-manager-form-row'
		});
		var dummyDiv = $.DIV();
		var btnDiv = $.DIV();
		var okBtn = $.BUTTON({
			className: 'is-button'
		}, IS_R.lb_changeApply);
		formRow.appendChild(dummyDiv);
		formRow.appendChild(btnDiv);
		btnDiv.appendChild(okBtn);
		formTable.appendChild(formRow);

		IS_Event.observe(okBtn, "click", clickFunc.bind(this, mapKey, formDef), false);
	},

	_submitPW: function() {
		var pwVal = $('pass').value;
		var confirm = $('confirm-pass').value;

		// validation
		$('pass').setCustomValidity("");

		// check null
		if(!pwVal) {
			var message = IS_R.ms_plz_input_new_password;
			alert(message);
			$('pass').setCustomValidity(message);
			$('pass').value = '';
			$('confirm-pass').value = '';
			return false;
		}

		// check space
		if(/\s/.test(pwVal)) {
			var message = IS_R.ms_blank_not_allow;
			alert(message);
			$('pass').setCustomValidity(message);
			$('pass').value = '';
			$('confirm-pass').value = '';
			return false;
		}

		// check agree
		if(pwVal !== confirm){
			var message = IS_R.ms_password_and_confirm_not_match;
			alert(message);
			$('pass').setCustomValidity(message);
			$('confirm-pass').value = '';
			return false;
		}

		// check policy
		if(IS_Portal.passwordPolicy && !IS_Portal.passwordPolicy.test(pwVal)){
			// ToDo
			var message = IS_R.ms_password_policy_alert;
			alert(message);
			$('pass').setCustomValidity(message);
			$('pass').value = '';
			$('confirm-pass').value = '';
			return false;
		}

		var opt = {
			method:'post',
			asynchronous: true,
			postBody: "password=" + pwVal,
			onSuccess: function(){
				alert(IS_R.ms_password_change_complete);
				$('pass').setCustomValidity("");
				$('pass').value = '';
				$('confirm-pass').value = '';
			}.bind(this),
			onFailure  : function(t) {
				alert(IS_R.ms_password_change_on_failure)
				// TODO
				msg.error(IS_R.getResource(IS_R.ms_password_change_on_failure,[getErrorMessage(t)]));
			},
		}
		AjaxRequest.invoke(hostPrefix + '/accountmanagersrv/doChangePW', opt);
	},

	// ToDo
	// Limitation Default square change
	_submitSelect: function(formKey, formDef) {
		var val = $(formKey).value;
		if(val == formDef.id) return;

		var opt = {
			method:'post',
			asynchronous: true,
			postBody: 'square='+val,
			onSuccess: function(){
				var selectedSquare = $(formKey).options[$(formKey).selectedIndex];
				var defaultSquare = IS_Customization.accountManagerForm.defaultsquare;
				defaultSquare.belong.push({id: defaultSquare.defaultSq.id, name:defaultSquare.defaultSq.name});
				defaultSquare.defaultSq.id = selectedSquare.value;
				defaultSquare.defaultSq.name = selectedSquare.text;
				for(var i = 0; i < defaultSquare.belong.length; i++) {
					var item = defaultSquare.belong[i];
					if(item.id == selectedSquare.value) {
						defaultSquare.belong.splice(i, 1);
						break;
					}
				}
				alert(IS_R.ms_change_apply_on_success);
			},
			onFailure  : function(t) {
				alert(IS_R.ms_change_apply_on_failure);
				// TODO
				msg.error(IS_R.getResource(IS_R.ms_change_apply_on_failure,[getErrorMessage(t)]));
			},
		}
		AjaxRequest.invoke(hostPrefix + '/accountmanagersrv/doChangeSQ', opt);
	},

	_submit: function(formKey, formDef) {
		var inputForm = $(formKey).getElementsByTagName('input');
		var body = '';
		for(var i = 0; i < inputForm.length; i++) {
			var value = inputForm[i].value.trim();
			var type = inputForm[i].type;

			// validation
			if(!value) {
			    var message = IS_R.getResource(IS_R.ms_plz_input_any, [formDef[inputForm[i].id].title]);
				alert(message);
				return false;
			}

			if(type == 'email' && !/^[A-Za-z0-9]+[\w-]+@[\w\.-]+\.\w{2,}$/.test(value)) {
				var message = IS_R.getResource(IS_R.ms_invalidValue3, [formDef[inputForm[i].id].title]);
				alert(message);
				return false;
			}

			body += inputForm[i].id + '=' + inputForm[i].value + '&';
		}

		var opt = {
			method:'post',
			asynchronous: true,
			postBody: body,
			onSuccess: function(){
				alert(IS_R.ms_change_apply_on_success);
				location.reload();
			},
			onFailure  : function(t) {
				alert(IS_R.ms_change_apply_on_failure);
				// TODO
				msg.error(IS_R.getResource(IS_R.ms_change_apply_on_failure,[getErrorMessage(t)]));
			},
		}
		AjaxRequest.invoke(hostPrefix + '/accountmanagersrv/doChange', opt);
	}
}