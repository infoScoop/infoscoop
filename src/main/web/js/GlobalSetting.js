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

IS_Portal.prefsObj = [];
IS_Portal.prefsEls = [];
IS_Portal.buildGlobalSettingModal = function() {
	var currentMenu;
	var preferenceDiv = $("portal-preference");
	if(!preferenceDiv)
		return;
	var allPreference = $.A({
		className:'portal-user-menu-link'
		, href:'#'
		, title:IS_R.lb_setupAll}
	);
	preferenceDiv.appendChild(allPreference);
	allPreference.appendChild(
		$.DIV({id:'allPreference', className:'portal-user-menu-item-label'}
			, IS_R.lb_setupAll
		)
	);
	if( Browser.isIE && preferenceDiv.getAttribute('outside'))
		preferenceDiv.style.width = preferenceDiv.offsetWidth+"px";

	var prefPage;
	
	var allSettingBody;
	var rssReaderSettingBody;
	
	var booleanArray = [{value:"true",display_value:IS_R.lb_makeEffective}, {value:"false",display_value:IS_R.lb_invalidate}];
	var dateArray = [];
	for(var dateNum=0;dateNum<10;dateNum++){
		dateArray.push({value:dateNum + 1,display_value:dateNum + 1 + IS_R.lb_businessDate});
	}
	
	var createPreferenceBody = function(){
		var preferenceDiv = document.createElement("div");
		preferenceDiv.className = "preferencePage";
		
		var prefTable = document.createElement("table");
		prefTable.className = "preferenceTable";
		prefTable.cellPadding = 0;
		prefTable.cellSpacing = 0;
		
		var prefTBody = document.createElement("tbody");
		var prefTr = document.createElement("tr");

		var prefTd = document.createElement("td");

		prefPage = document.createElement("div");
		prefPage.style.height = "100%";
		
		prefPage.appendChild(buildBackgroundSetting());
		prefPage.appendChild(buildWidgetThemeSetting());
		prefPage.appendChild(buildShowAllSettingBody());
		prefPage.appendChild(buildRssSettingBody());
		prefPage.appendChild(buildCustomizeReset());

		
		prefTable.appendChild(prefTBody);
		prefTBody.appendChild(prefTr);

		prefTr.appendChild(prefTd);
		prefTd.appendChild(prefPage);
		prefPage.appendChild(document.createElement("div"));
		
		var preferenceHeader = document.createElement("div");
		preferenceHeader.className = "preferenceHeader";
		preferenceDiv.appendChild( preferenceHeader );
		
		var closeButton = document.createElement("div");
		closeButton.className = "preferenceClose command";
		closeButton.innerHTML = IS_R.lb_close;
		preferenceHeader.appendChild( closeButton );
		IS_Event.observe( closeButton,"click",function() { Control.Modal.close()} );
		
		var prefTitle = document.createElement("div");
		prefTitle.className = "pageTitle";
		prefTitle.innerHTML = IS_R.lb_setupAll;

		preferenceHeader.appendChild(prefTitle);
		
		preferenceDiv.appendChild(prefTable);
		
		return preferenceDiv;
		
	}
	function createFooterDiv( content ) {
		var footerDiv = document.createElement("div");
		footerDiv.style.width = "100%";
		footerDiv.style.textAlign = "right";
		
		footerDiv.appendChild( content );
		
		return footerDiv;
	}
	function createExecButton() {
		var execButton = document.createElement("input");
		execButton.type = "button";
		execButton.value = IS_R.lb_changeApply;
		Event.observe(execButton, "click", applyPreference );
		
		return createFooterDiv( execButton );
	}
	function applyPreference(){
		for(name in IS_Portal.prefsEls){
			if(IS_Portal.prefsObj && typeof IS_Portal.prefsEls[name] != "function"){
				IS_Portal.prefsObj[name] = IS_Portal.prefsEls[name].value;
			}
		}
		
		//Send command
		var isAllRefresh = false;
		if(IS_Portal.prefsObj.freshDays && IS_Portal.prefsObj.freshDays != ""){
			IS_Portal.freshDays = IS_Portal.prefsObj.freshDays;
			var tempFreshDays = IS_Portal.getFreshDays(IS_Portal.freshDays);
			
			freshDays = tempFreshDays;
			isAllRefresh = true;
			
			//Send to Server
			IS_Widget.setPreferenceCommand("freshDays", IS_Portal.freshDays);
		}
		if(IS_Portal.prefsObj.fontSize){
	//		IS_Portal.applyFontSize(IS_Portal.prefsObj.fontSize);
			setTimeout(IS_Portal.applyFontSize.bind(this, IS_Portal.prefsObj.fontSize), 10);
		}
		if(IS_Portal.prefsObj.mergeconfirm){
			IS_Portal.mergeconfirm = getBooleanValue(IS_Portal.prefsObj.mergeconfirm);
			IS_Widget.setPreferenceCommand("mergeconfirm", IS_Portal.mergeconfirm);
		}
		
		IS_Portal.applyPreference(IS_Portal.currentTabId, true, isAllRefresh);
		
		for(var tabId in IS_Portal.widgetLists){
			if(typeof IS_Portal.widgetLists[tabId] == "function") continue;
			
			if(tabId != IS_Portal.currentTabId){
				// It doesn't apply to non-active tab.
				if(IS_Portal.tabs[tabId].isBuilt){
					IS_Portal.tabs[tabId].applyPreference = true;
					IS_Portal.applyPreference(tabId, false, isAllRefresh);
				}
			}
		}
	}
	
	function buildShowAllSettingBody(){
		var wfs = createFieldSet(IS_R.lb_generalSetting);
		
		var freshDaysOpt = {name: "freshDays", display_name: IS_R.lb_freshdaysTerm};
		appendOption(wfs, freshDaysOpt, dateArray, IS_Portal.freshDays);
		
		var mergeconfirmOpt = {name: "mergeconfirm", display_name: IS_R.lb_mergeConfirmDialog};
		appendOption(wfs, mergeconfirmOpt, booleanArray, new String(IS_Portal.mergeconfirm));
		
		wfs.appendChild( createExecButton());
		
		return wfs;
	}
	
	function buildRssSettingBody(){
		var wfs = createFieldSet(IS_R.lb_rssViewSetting);
		
		var rssReaderConf = IS_Widget.getConfiguration("RssReader");
		if(!rssReaderConf){
			var msg = IS_R.ms_rssreaderUnreadable;
			wfs.innerHTML = msg;
			msg.warn(msg);
			return;
		}
		
		if(rssReaderConf.UserPref.doLineFeed){
			appendOption(wfs, rssReaderConf.UserPref.doLineFeed, booleanArray);
		}
		if(rssReaderConf.UserPref.showDatetime){
			appendOption(wfs, rssReaderConf.UserPref.showDatetime, booleanArray);
		}
		if(rssReaderConf.UserPref.detailDisplayMode){
			appendOption(wfs, rssReaderConf.UserPref.detailDisplayMode, rssReaderConf.UserPref.detailDisplayMode.EnumValue);
		}
		if(rssReaderConf.UserPref.itemDisplay){
			appendOption(wfs, rssReaderConf.UserPref.itemDisplay, rssReaderConf.UserPref.itemDisplay.EnumValue);
		}
		if(rssReaderConf.UserPref.scrollMode){
			appendOption(wfs, rssReaderConf.UserPref.scrollMode, rssReaderConf.UserPref.scrollMode.EnumValue);
		}
	
		wfs.appendChild( createExecButton());
		
		return wfs;
	}
	
	function buildCustomizeReset() {
		var fs = createFieldSet( IS_R.lb_initialize );
		
		var description = document.createElement("div");
		description.innerHTML = 
			IS_R.lb_clearConfigurationDesc1 +"<br/>"
			+IS_R.lb_clearConfigurationDesc2;
		fs.appendChild( description );
		
		var initButton = document.createElement("input");
		initButton.type = "button";
		initButton.value = IS_R.lb_clearConfigurationButton;
		IS_Event.observe( initButton,"click",function() {
			Control.Modal.close();
			
			if( !confirm( IS_R.ms_clearConfigurationConfirm ))
				return;
			
			IS_Request.asynchronous = false;
			IS_Request.CommandQueue.fireRequest();
			
			var opt = {
				method: 'get' ,
				asynchronous:false,
				onSuccess: function(req){
					window.location.reload( true );
				},
				onFailure: function(t) {
					var msg = IS_R.ms_clearConfigurationFailed;
					alert( msg );
					msg.error( msg );
				}
			};
			AjaxRequest.invoke(hostPrefix +  "/widsrv?reset=true", opt);
		});
		var initField = createField("",initButton );
		fs.appendChild( initField );
		
		return fs;
	}

	function buildBackgroundSetting() {
		var fs = createFieldSet( IS_R.lb_wallPaperSetting );
		var backgroundSettingDiv = $.DIV({id:'backgroundSettingDiv'}, $.DIV({}, IS_R.lb_selectWallPaperImage ) );
		
		var backgroundImages = [IS_Portal.theme.defaultTheme['background']['image']].concat(IS_Portal.theme.backgroundImages);
		var currentBackgroundImage = IS_Portal.theme.currentTheme['background'] && IS_Portal.theme.currentTheme['background']['image'] ? IS_Portal.theme.currentTheme['background']['image'] : IS_Portal.theme.defaultTheme['background']['image'];
		for(var i = 0; i < backgroundImages.length; i++){
			var radioBtn = $.INPUT({type:'radio',name:"backgroundImageRadio"});
			radioBtn.id = 'background_setting_' + backgroundImages[i];
			radioBtn.defaultChecked = currentBackgroundImage == backgroundImages[i];
			radioBtn.value = backgroundImages[i];
			
			backgroundSettingDiv.appendChild(
				$.DIV(
					{style:"styleFloat:left;cssFloat:left;textAlign:center"},
					$.LABEL(
						{'htmlFor':'background_setting_' + backgroundImages[i]},
						$.DIV({style:"width:35px;height:35px;cursor:pointer;overflow:hidden;margin:5px;background:url(" + backgroundImages[i] + ") repeat;"})
						  ),
					radioBtn
				  )
			  );
		}
		fs.appendChild( backgroundSettingDiv );
		fs.appendChild(
			$.DIV({style:"textAlign:right;clear:both;"},
				  $.INPUT({type:"button",value:IS_R.lb_changeApply,
					onclick:{
					  handler:function(e) {
						  var backgroundSettingDiv = $('backgroundSettingDiv');
						  var radioList = backgroundSettingDiv.getElementsByTagName('INPUT');
						  var imageUrl;
						  for(var i = 0; i < radioList.length ; i++){
							  if(radioList[i].checked){
								  imageUrl = radioList[i].value;
								  break;
							  }
						  }
						  
						  IS_Portal.theme.changeBackground({image:imageUrl});
					  }
					}})
					)
			);
		
		return fs;
	}
	
	function buildWidgetThemeSetting() {
		var fs = createFieldSet( IS_R.lb_widgetDesignSetting );
		var currentWidgetTheme = IS_Portal.theme.currentTheme['widget'] ? IS_Portal.theme.currentTheme['widget'] : IS_Portal.theme.defaultTheme['widget'];

		var widgetHeaderSettingDiv = $.DIV({id:'widgetHeaderSettingDiv'}, $.DIV({}, IS_R.lb_selectWidgetHeaderImage ) );
		
		var defaultWidgetHeaderImage = IS_Portal.theme.defaultTheme.widget.header['background']['image'];
		var widgetHeaderImages = [defaultWidgetHeaderImage].concat(IS_Portal.theme.widgetHeaderImages);
		var currentWidgetHeaderImage = currentWidgetTheme.header && currentWidgetTheme.header['background'] && currentWidgetTheme.header['background']['image'] ?
		  currentWidgetTheme.header['background']['image'] : defaultWidgetHeaderImage;
		for(var i = 0; i < widgetHeaderImages.length; i++){
			var radioBtn = $.INPUT({type:'radio',name:"widgetHeaderSettingRadio"});
			radioBtn.id = 'widget_header_setting_' + widgetHeaderImages[i];
			radioBtn.defaultChecked = currentWidgetHeaderImage == widgetHeaderImages[i];
			radioBtn.value = widgetHeaderImages[i];
			var sampleImgDiv = $.DIV({style:"width:80px;height:24px;cursor:pointer;margin-right:5px"});
			sampleImgDiv.style.background = "url(" + widgetHeaderImages[i] + ") repeat";
			widgetHeaderSettingDiv.appendChild(
				$.DIV(
					{style:"styleFloat:left;cssFloat:left;textAlign:center"},
					$.LABEL(
						{'htmlFor':'widget_header_setting_' + widgetHeaderImages[i]},
						sampleImgDiv
						  ),
					radioBtn
				  )
			  );
		}
		fs.appendChild( widgetHeaderSettingDiv );

		var subWidgetHeaderSettingDiv = $.DIV({id:'subWidgetHeaderSettingDiv',style:"clear:both;"}, $.DIV({},IS_R.lb_selectSubWidgetHeaderImage));
		var defaultSubWidgetHeaderColor = IS_Portal.theme.defaultTheme.widget.subheader['background']['color'];
		var subWidgetHeaderColors = [defaultSubWidgetHeaderColor].concat(IS_Portal.theme.subWidgetHeaderColors);
		var currentSubWidgetHeaderColor = currentWidgetTheme.subheader && currentWidgetTheme.subheader['background'] && currentWidgetTheme.subheader['background']['color'] ?
		  currentWidgetTheme.subheader['background']['color'] : defaultSubWidgetHeaderColor;
		for(var i = 0; i < subWidgetHeaderColors.length; i++){
			var radioBtn = $.INPUT({type:'radio',name:"subWidgetHeaderColorRadio"});
			radioBtn.id = 'sub_widget_header_setting_' + subWidgetHeaderColors[i];
			radioBtn.defaultChecked = currentSubWidgetHeaderColor == subWidgetHeaderColors[i];
			radioBtn.value = subWidgetHeaderColors[i];
			subWidgetHeaderSettingDiv.appendChild(
				$.DIV(
					{style:"styleFloat:left;cssFloat:left;textAlign:center"},
					$.LABEL(
						{'htmlFor':'sub_widget_header_setting_' + subWidgetHeaderColors[i]},
						$.DIV({style:"width:40px;height:20px;cursor:pointer;margin-right:5px;backgroundColor:" + subWidgetHeaderColors[i] + ";"})
						  ),
					radioBtn
				  )
			  );
		}
		
		fs.appendChild(subWidgetHeaderSettingDiv);

		var currentWithBorder = currentWidgetTheme.border && currentWidgetTheme.border.none ? true : false;
		fs.appendChild(
			$.DIV({style:"clear:both;"},
				  IS_R.lb_noDisplayFrameBorder,
				  $.INPUT({id:"is_preference_setting_with_border", type:"checkbox", defaultChecked:currentWithBorder})
					)
			);
		
		var currentBorderRadius = currentWidgetTheme.border && currentWidgetTheme.border.radius;
		fs.appendChild(
			$.DIV({style:"clear:both;display:" + (Browser.isIE8 ? 'none' : '') + ";"},
				  IS_R.lb_enableRoundCorner,
				  $.INPUT({id:"is_preference_setting_border_radius", type:"checkbox", defaultChecked:currentBorderRadius && currentBorderRadius!="0px"})
					)
			);
		
		fs.appendChild(
			$.DIV({style:"textAlign:right;clear:both;"},
				  $.INPUT({type:"button",value:IS_R.lb_changeApply,
					onclick:{
					  handler:function(e) {
						  var widgetHeaderSettingDiv = $('widgetHeaderSettingDiv');
						  var radioList = widgetHeaderSettingDiv.getElementsByTagName('INPUT');
						  var widgetHeaderImageUrl;
						  for(var i = 0; i < radioList.length ; i++){
							  if(radioList[i].checked){
								  widgetHeaderImageUrl = radioList[i].value;
								  break;
							  }
						  }
						  
						  var subWidgetHeaderSettingDiv = $('subWidgetHeaderSettingDiv');
						  var radioList = subWidgetHeaderSettingDiv.getElementsByTagName('INPUT');
						  var subWidgetHeaderColor;
						  for(var i = 0; i < radioList.length ; i++){
							  if(radioList[i].checked){
								  subWidgetHeaderColor = radioList[i].value;
								  break;
							  }
						  }
						  
						  var opt = {
							header:{background:{image:widgetHeaderImageUrl}},
							subheader:{background:{color:subWidgetHeaderColor}},
							border:{
							  none:$("is_preference_setting_with_border").checked,
							  radius:$("is_preference_setting_border_radius").checked ? '7px': '0'
							}
						  }
						  IS_Portal.theme.changeWidgetTheme(opt);
					  }
					}})
					)
			);
		return fs;
	}
	
	function createFieldSet(title){
		var fieldSet = document.createElement("fieldSet");
		var legEnd = document.createElement("legEnd");
		fieldSet.appendChild(legEnd);
		legEnd.innerHTML = title;
		return fieldSet;
	}
	
	function createField( labelContent, valueContent ){
		if( typeof( labelContent ) == "string")
			labelContent = document.createTextNode( labelContent );
		
		if( typeof( valueContent ) == "string")
			valueContent = document.createTextNode( valueContent );
		
		var itemTable = document.createElement("table");
		itemTable.cellPadding = "3px";
		itemTable.cellSpacing = 0;
		itemTable.style.width = "100%";
		
		var itemTBody = document.createElement("tbody");
		var itemTr = document.createElement("tr");
		itemTr.className = "option";
		var itemLeftTd = document.createElement("td");
		var itemRightTd = document.createElement("td");
		itemRightTd.className = "rightTd";
		
		itemTable.appendChild(itemTBody);
		itemTBody.appendChild(itemTr);
		itemTr.appendChild(itemLeftTd);
		itemTr.appendChild(itemRightTd);
		
		var titleLabel = document.createElement("label");
		titleLabel.style.fontWeight = "bold";
		titleLabel.appendChild( labelContent );
		
		itemLeftTd.appendChild(titleLabel);
		itemRightTd.appendChild( valueContent );
		
		return itemTable;
	}
	function appendOption( el, obj, selectOptions, selectValue) {
		var selectEl = document.createElement("select");
		selectEl.id = "pref_" + obj.name;
		selectEl.style.width = "150px";
		
		//Head is empty
		var optEl = document.createElement("option");
		optEl.setAttribute("value", "");
		optEl.appendChild(document.createTextNode(IS_R.lb_changeNotApply));
		selectEl.appendChild(optEl);
		
		for(var i=0;i<selectOptions.length;i++){
			if(typeof selectOptions[i] == "function") continue;
			optEl = document.createElement("option");
			optEl.setAttribute("value", selectOptions[i].value);
			optEl.appendChild(document.createTextNode(selectOptions[i].display_value));
			selectEl.appendChild(optEl);
			if(selectValue == selectOptions[i].value){
				optEl.selected =true;
			}
		}
		
		IS_Portal.prefsEls[obj.name] = selectEl;
		
		var field = createField( " "+obj.display_name,selectEl );
		el.appendChild( field );
		
		return field;
	}


	var showModal = function(){
		IS_Portal.currentModal.container.update(createPreferenceBody());
		IS_Portal.currentModal.open();
	}
	
	if(preferenceDiv){
		IS_Portal.currentModal = new Control.Modal('',{
			className: 'preference'
		});
		preferenceDiv.title = IS_R.lb_setupAll;
		Event.observe(preferenceDiv, "click", showModal);
		

		if(preferenceDiv.parentNode)//Setting of command bar width.
			preferenceDiv.parentNode.style.width = preferenceDiv.offsetWidth;
	}

}
