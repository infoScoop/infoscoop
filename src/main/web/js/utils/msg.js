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

var msg=new Object();

msg.PRIORITY_DEBUG=  { id:1, lifetime:60, icon:imageURL+"debug.gif" };
msg.PRIORITY_LOW=    { id:2, lifetime:300, icon:imageURL+"information.gif" };
msg.PRIORITY_DEFAULT={ id:3, lifetime:600, icon:imageURL+"error.gif" };
msg.PRIORITY_HIGH=   { id:4, lifetime:-1, icon:imageURL+"exclamation.gif" };

msg.messages=new Array();
msg.currentStatus = 2;
msg.seq = 0;
msg.currentPriority;

msg.debug = function(message){
	new msg.Message(message,1);
}
msg.info = function(message){
	new msg.Message(message,2);
}
msg.warn = function(message){
	new msg.Message(message,3);
}
msg.error = function(message){
	new msg.Message(message,4);
}

msg.currentIcon;

msg.displayIcon = function(){
	if(msg.currentIcon) {
		var msgIcon = document.getElementById("messageIcon");
		if(msgIcon)	msgIcon.src = msg.currentIcon;
	}
}

msg.Message=function(message,priority,lifetime,icon){
	if(priority < messagePriority){
		return;
	}
	this.id="mssage_" + ++msg.seq;
	msg.messages[this.id]=this;
	this.message=message;
	this.priority=(priority) ? priority : msg.PRIORITY_DEFAULT.id;
	this.lifetime=(lifetime) ? lifetime : this.defaultLifetime();
	this.icon=(icon) ? icon : this.defaultIcon();
	this.date = new Date();
	
	if (this.lifetime>0){
		this.fader=setTimeout(
			"msg.messages['"+this.id+"'].clear()",
			this.lifetime*1000
			);
	}
	if(priority > msg.currentStatus){
		msg.currentStatus = priority;
		
		msg.currentIcon = this.icon;
		msg.displayIcon();
//		document.getElementById("messageIcon").src = this.icon;
	}
	if (!msg.suppressRender){
		if(!msg.currentPriority || msg.currentPriority < 0 || (priority == msg.currentPriority)){
			this.render();
		}
	}
}


msg.Message.prototype.clear=function(){
	if (this.pinned){
		this.expired=true;
	}else{
		this.unrender();
	}
}  
msg.Message.prototype.unrender=function(){
	if(msg.popupWindow && !msg.popupWindow.closed){
		var msgbar = msg.popupWindow.document.getElementById("msgbar");
		var divMessage = msg.popupWindow.document.getElementById("div_message_" + this.id);
		
//		msgbar.removeChild(divMessage);
		if(divMessage && divMessage.parentNode) divMessage.parentNode.removeChild(divMessage);
	}
	if(msg.messages[this.id]){
		for(i in msg.messages[this.id]){
			if(msg.messages[this.id] && msg.messages[this.id][i])
			  msg.messages[this.id][i] = null;
		}
	}
	delete msg.messages[this.id];
	
}

msg.Message.prototype.defaultLifetime=function(){
	if (this.priority<=msg.PRIORITY_DEBUG.id){
		return msg.PRIORITY_DEBUG.lifetime;
	}else if (this.priority<=msg.PRIORITY_LOW.id){
		return msg.PRIORITY_LOW.lifetime;
	}else if (this.priority==msg.PRIORITY_DEFAULT.id){
		return msg.PRIORITY_DEFAULT.lifetime;
	}else if (this.priority>=msg.PRIORITY_HIGH.id){
		return msg.PRIORITY_HIGH.lifetime;
	}
}

msg.Message.prototype.defaultIcon=function(){
	if (this.priority<=msg.PRIORITY_DEBUG.id){
		return msg.PRIORITY_DEBUG.icon;
	}else if (this.priority<=msg.PRIORITY_LOW.id){
	  return msg.PRIORITY_LOW.icon;
	}else if (this.priority==msg.PRIORITY_DEFAULT.id){
		return msg.PRIORITY_DEFAULT.icon;
	}else if (this.priority>=msg.PRIORITY_HIGH.id){
		return msg.PRIORITY_HIGH.icon;
	}
}

msg.Message.prototype.render=function(){
	if(msg.popupWindow && !msg.popupWindow.closed){
		var msgbar = msg.popupWindow.document.getElementById("msgbar");
		msgbar.innerHTML += this.buildMessage();
	}
}

msg.Message.prototype.buildMessage=function(){
	return "<div id='div_message_" + this.id + "' style='border-bottom:dashed 1px gray'><img src='" + this.icon +  "'/> " +  formatDate(this.date, "HH:mm:ss") + " : " + this.message + "</div>";
}

msg.showPopupDialog= function(e){
	this.popupWindow = window.open("messageConsole.html", "messageWindow", "width=400, height=400, scrollbars=yes, status=no");
	this.popupWindow.focus();
}

function msg_render(priority){
	var msgbar = msg.popupWindow.document.getElementById('msgbar');
	if(msgbar) msgbar.innerHTML = "";
	for (i in msg.messages){
		try{
			var message=msg.messages[i];
			if (message && message.id && (message instanceof msg.Message)){
				if(!priority || priority < 0 || message.priority == priority ){
					if(msgbar){
						msgbar.innerHTML += message.buildMessage();
					}
				}
			}
		}catch(e){
		}
	}
	msg.currentPriority = priority;
}

//If no FireBug is found, output debug by FireBug to message console
if(typeof console == "undefined") {
	console = msg;
	console.log = msg.debug;
}
