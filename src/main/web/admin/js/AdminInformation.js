var ISA_Information = IS_Class.create();

ISA_Information.prototype.classDef = function() {
	var container;
	var loadingMessage;
	
	this.initialize = function() {
		container = document.getElementById("information");

		loadingMessage = document.createElement('div');
		loadingMessage.innerHTML = "Loading...";
		loadingMessage.style.clear = "both";
		loadingMessage.style.cssFloat = "left";
		container.appendChild(loadingMessage);
		
	};
	
	this.build = function() {
		while (container.hasChildNodes())
			container.removeChild(container.firstChild);

		var self = this;
		console.log(adminHostPrefix);
		var url = adminHostPrefix + "/services/information/getUserCountListJSON";
		var opt = {
			method: 'get',
			asynchronous:true,
			onSuccess: this.display.bind(self),
			on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_staticInfoNotFound+"</span>";
				msg.error(ISA_R.ams_staticInfoNotFound + t.status + " - " + t.statusText);
			},
			onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingStaticInfo+"</span>";
				msg.error(ISA_R.ams_failedLoadingStaticInfo + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingStaticInfo+"</span>";
				msg.error(ISA_R.ams_failedLoadingStaticInfo + getErrorMessage(t));
				throw t;
			},
			onComplete: function(req, obj){
				ISA_Admin.requestComplete = true;
			},
			onRequest: function() {
				ISA_Admin.requestComplete = false;
			}
		};
		AjaxRequest.invoke(url, opt);
	};


	this.display = function(response) {
		IS_Event.unloadCache("_adminInfo");
		
		var json = eval("(" + response.responseText + ")");
		
		var activeUsersCount = json["activeUsersCount"];
		var totalUsersCount = json["totalUsersCount"];
		var todayAccessCount = json["todayAccessCount"];
		
		this.displayHeader();
		
		var dummy = document.createElement("div");
		dummy.innerHTML = '<table><tbody><tr id="proxyConfigHeader"><td colspan=2>'+ISA_R.alb_userInformation+'</td></tr><tbody></table>';
		var table = dummy.firstChild;
		
		container.appendChild( table );
		table.className = "proxyConfigList";
		table.style.width = "40%";
		
		var tbody = table.firstChild;
		
		tbody.appendChild( createUserCountRow(ISA_R.alb_activeUserNumber, activeUsersCount) );
		tbody.appendChild( createUserCountRow(ISA_R.alb_accessedUsersToday, todayAccessCount) );
		tbody.appendChild( createUserCountRow(ISA_R.alb_totalUsersNumber, totalUsersCount) );
		
		function createUserCountRow(title, value){
	 		var row = document.createElement("tr");
			var row_titleTd = document.createElement("td");
			var row_valueTd = document.createElement("td");
			row_titleTd.innerHTML = title;
			row_valueTd.innerHTML = value;
			row_valueTd.style.textAlign = "right";
			
			row.appendChild(row_titleTd);
			row.appendChild(row_valueTd);
			return row;
		}
	}
	
	this.displayHeader = function(){
		var controlDiv = document.createElement("div");
		controlDiv.style.textAlign = "right";
		
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		controlDiv.appendChild(refreshDiv);
		IS_Event.observe(refreshDiv, "click", this.build.bind(this), false, "_adminInfo");
		
		container.appendChild(controlDiv);
		
		var titleDiv = document.createElement("div");
		titleDiv.className = "proxyTitle";
		titleDiv.appendChild(document.createTextNode(ISA_R.alb_statisticsInformation));
		container.appendChild(titleDiv);
	}

}
