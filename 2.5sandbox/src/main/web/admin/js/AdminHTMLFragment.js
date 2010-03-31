/**
	Display frame for HTML fragment in modal
	@return Div including iframe for HTML fragment, return false if failed.
*/
ISA_WidgetConf.EditWidgetConf.displayFragmentModal = function( prefType, inputURL, filterEncoding, authType, callback) {

	var currentTargetElement;
	ISA_Admin.startIndicator();
	
	// Display for modal
	var fragmentDiv = document.createElement("div");
	
	var explainDiv = document.createElement("div");
	explainDiv.style.width = "100%";
	explainDiv.style.textAlign = "center";
	var explainText = document.createElement("span");
	explainText.innerHTML = "<div>"+ISA_R.ams_selectFragmentArea+"</div>";
	explainDiv.appendChild( explainText );
	fragmentDiv.appendChild( explainDiv );
	
	var iframe = document.createElement("iframe");
	iframe.id = "fragmentTargetFrame";
	iframe.height = '400px';
	iframe.className = "targetFrame";
	
	var xpathDiv = document.createElement("div");
	xpathDiv.style.textAlign = "right";
	
	var prefTable = document.createElement("table");
	var prefTBody = document.createElement("tbody");
	var prefXpathTr = document.createElement("tr");
	var prefXpathTitleTd = document.createElement("td");
	var prefXpathValueTd = document.createElement("td");
	prefXpathValueTd.style.width = "100%";
	
	prefTable.style.width = "100%";
	prefTable.cellPadding = 0;
	prefTable.cellSpacing = 3;
	prefTable.appendChild(prefTBody);
	prefTBody.appendChild(prefXpathTr);
	prefXpathTr.appendChild(prefXpathTitleTd);
	prefXpathTr.appendChild(prefXpathValueTd);
	
	var xpathText = document.createElement("div");
	xpathText.innerHTML = "<nobr><div style='font-size:85%;'>"+ISA_R.alb_selectedXPath+"</div></nobr>";
	prefXpathTitleTd.appendChild(xpathText);
	
	var xpath = document.createElement("input");
	xpath.id = "currentXPathForm";
	xpath.style.width = "100%";
	xpath.style.border = "1px solid #000000";
	xpath.style.backgroundColor = "#EEEEEE";
	xpath.readonly = true;
	xpath.value = "Now setting...";
	prefXpathValueTd.appendChild(xpath);
	
	
	fragmentDiv.appendChild( prefTable );
	
	fragmentDiv.appendChild( iframe );

	function getProxyUrl(url, filter, filterEncoding) {
		var encoding = (filterEncoding)? ("&filterEncoding=" + filterEncoding) : "";
		return proxyServerURL + "?url=" + encodeURIComponent(url) + "&filter=" + filter + encoding;
	}
	
	function loadIframe(authParameters){
		var _selectXPathPanel = $("selectXPathPanel");
		if (_selectXPathPanel.style.display == "none")
			_selectXPathPanel.style.display = "";
		
		if (_selectXPathPanel.firstChild) {
			_selectXPathPanel.replaceChild(fragmentDiv, _selectXPathPanel.firstChild);
		}else {
			_selectXPathPanel.appendChild(fragmentDiv);
		}
					
		iframe.src = getProxyUrl(inputURL, "URLReplace", filterEncoding) + ((authParameters) ? authParameters : "");
		iframe.setAttribute("frameborder", "0");
	
		IS_Event.observe(iframe, 'load', setEvent.bind(this, iframe), false, "addWidgetEdit");
	}

	function showAuthForm(_authType){
		
		IS_Request.createModalAuthFormDiv(
			ISA_R.alb_selectFragmentArea,
			$('inputSelectXPathButton'),
			function (_authUid, _authPassword){
				if( !_authUid && !_authPassword )
					return cancelXPathSelection();
				
				authUid = _authUid;
				authPassword = _authPassword;
				
				is_processUrlContents(inputURL,function( response ) {
					try{
						var resObj = eval(response.responseText);
						if(resObj[0] && resObj[0].isError){
							alert(resObj[0].errorMsg);
							
							return cancelXPathSelection();
						}
					}catch(e){};
					
					loadIframe("&authType=" +  _authType +  "&authuserid=" + encodeURIComponent(authUid) + "&authpassword=" + authPassword);
				},function(){}, ["authType", _authType, "authuserid",authUid,"authpassword",authPassword]);
			},
			false //isModal
			);
	}
	
	function cancelXPathSelection() {
		ISA_Admin.stopIndicator();
		
		var selectXPathPanel = $("selectXPathPanel");
		if(!selectXPathPanel) return;
		selectXPathPanel.style.display = "none";
	}
	function setIframeURL(response){
		var _authType = response.getResponseHeader("MSDPortal-AuthType");
		if(_authType){
			ISA_Admin.stopIndicator();
			showAuthForm(_authType);
		}else{
			loadIframe();
		}
	}
	if(authType && authType != 'basic' && authType != 'ntlm'){
		showAuthForm(authType);
	}else{
		var opt = {
		  method: 'get' ,
		  asynchronous:true,
		  onSuccess: setIframeURL,
		  on404: function(t) {
		      var msgbody = (ISA_R.ams_FailedGetInfo + t.status + " - " + t.statusText);
			  alert(msgbody);
			  msg.error(msgbody);
			  ISA_Admin.stopIndicator();
			  fragmentDiv = false;
		  },
		  on10408: function(r,t) {
			  var msgbody = (ISA_R.ams_FailedGetInfo + t.status + " - " + t.statusText);
			  alert(msgbody);
			  msg.error(msgbody);
			  ISA_Admin.stopIndicator();
			  fragmentDiv = false;
		  },
		  onFailure: function(t) {
			  var msgbody = (ISA_R.ams_FailedGetInfo + t.status + " - " + t.statusText);
			  alert(msgbody);
			  msg.error(msgbody);
			  ISA_Admin.stopIndicator();
			  fragmentDiv = false;
		  },
		  onException: function(r, t){
			  var msgbody = (ISA_R.ams_FailedGetInfo + t);
			  alert(msgbody);
			  msg.error(msgbody);
			  ISA_Admin.stopIndicator();
			  fragmentDiv = false;
		  }
		};
		AjaxRequest.invoke(is_getProxyUrl(inputURL, "NoOperation"), opt);
	}
	function setEvent(ifrm){
		try{
			var iframeDoc = Browser.isIE ? ifrm.contentWindow.document : ifrm.contentDocument;
		}catch(e){
			$("currentXPathForm").value = "";
			
			return cancelXPathSelection();
		}
		this.fragmentTitle = iframeDoc.title;
		
		var errorMsg = iframeDoc.getElementById('url_replace_error_msg');
		if(errorMsg){
			$("currentXPathForm").value = "";
			cancelXPathSelection();
			
			return alert(errorMsg.innerHTML);
		}
			
		var tables = iframeDoc.getElementsByTagName("table");
		for(var i=0; i < tables.length; i++){
			var table = tables[i];
			table.style.border = "1px solid gainsboro";
			table.style.padding = "1px";
			IS_Event.observe(table, 'mouseover', elementSelect.bind(this, table, '#0f0'), false, "addWidgetEdit");
			IS_Event.observe(table, 'mouseout', clearColor.bind(this, table), false, "addWidgetEdit");
		}
		
		var divs = iframeDoc.getElementsByTagName("div");
		for(var i=0; i < divs.length; i++){
			var div = divs[i];
			div.style.border = "1px solid gainsboro";
			div.style.padding = "1px";
			IS_Event.observe(div, 'mouseover', elementSelect.bind(this, div, '#00f'), false, "addWidgetEdit");
			IS_Event.observe(div, 'mouseout', clearColor.bind(this, div), false, "addWidgetEdit");
		}
		
		var body = iframeDoc.body;
		IS_Event.observe(body, 'mouseover', elementSelect.bind(this, body, '#f00'), false, "addWidgetEdit");
		IS_Event.observe(body, 'mouseout', clearColor.bind(this, body), false, "addWidgetEdit");
		
		var popup = new PopupMenu();
		popup.add(ISA_R.alb_cutSelectedEelement, function(target) {
			if(!currentTargetElement){
				alert(ISA_R.ams_fragmentNotSelected);
				return;
			}
			
			addFragmentWidgetInstance();
		});
		popup.setDoc(iframeDoc);
		popup.bind(iframeDoc);
		
		function elementSelect(targetNode, color, e){
			if(!PopupMenu.current){
				if(currentTargetElement)
					currentTargetElement.style.border = "1px solid gainsboro";
				
				if(targetNode){
	    			targetNode.style.border = "1px solid " + color;
				}
				Event.stop(e);
				currentTargetElement = targetNode;
				showXPath(currentTargetElement);
			}
		}
		
		function clearColor(targetNode){
			if(PopupMenu.current) return;
			
			if(targetNode)
    			targetNode.style.border = "1px solid gainsboro";
			targetElement = null;
		}
		
		Event.observe(iframeDoc.body, "click", stopEvent.bind(this), false);
		function stopEvent(e){
			popup.hide();
			Event.stop(e);
			
		}
		function showXPath(targetElement){
			$("currentXPathForm").value = makeXPath(targetElement);
		}
		$("currentXPathForm").value = "";
		ISA_Admin.stopIndicator();
	}
	
	function makeXPath(elm){
		
		if(!elm) return '/';
		
		var tagName = elm.tagName.toLowerCase();
		if(tagName == 'body'){
			return '/html/body';
		}else if(tagName == 'tbody'){
			tagName = "";
//			return makeXPath(elm.parentNode);
		}
		
		if(elm.id && 0 < elm.id.length){
			return '//'+tagName+'[@id=\''+elm.id+'\']';
		}
		
		var parent = elm.parentNode;
		if(!parent){
			return '/' + tagName;
		}
		
		var childNodes = parent.childNodes;
		var childLength = childNodes.length;
		var position = 1;
		for(var i = 0; i < childLength; i++){
			var child = childNodes[i];
			if(elm == child) break;
			var childTagName = child.tagName;
			if(childTagName && tagName == childTagName.toLowerCase()){
				position++;
			}
		}
		
		var path = makeXPath(parent) + '/' + tagName;
		if(1 < position) path += '[' + position + ']';
		return path;
	}
	
	function addFragmentWidgetInstance() {
		/*
		var thumbnail = "";
		var thumbnailDiv = $("addInstThumbnail_FragmentMiniBrowser");
		if(thumbnailDiv && 0 < thumbnailDiv.value.length){
			var trimValue = thumbnailDiv.value.replace(/ |　/, "");
			if(0 < trimValue.length)
			  thumbnail = thumbnailDiv.value;
		}
		*/
		var fragmentXPath = $("currentXPathForm").value;
		var xpath = (fragmentXPath)? fragmentXPath : '//body';
		
		$(prefType + "_xPath").value = xpath;
		
		Element.toggle("selectXPathPanel");
		callback();
	}
}
