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

IS_EventDispatcher = new Object();
IS_EventDispatcher.complexEventListenerList = new Array(); 
IS_EventDispatcher.eventListenerList = new Object();
IS_EventDispatcher.eventQueue = new Array();

/*
 * Register Event Listener
 * @type     name of the event 
 * @id       identifier of the component as HTML element in case of determining the source of the event
 * @listener callback function registered as listener
 * @obj      object associated with Listener
 * @isTemp   whether events are destroyed or not after they once are proceeded 
 */
IS_EventDispatcher.addListener = function(type, id, listener, obj, isTemp){
	if(!type || type == "") return;
	var eventId = (id) ? type + "_" + id : type;

	var listenerList = IS_EventDispatcher.eventListenerList[eventId];
	if(!listenerList || listenerList.length ==0){
		listenerList = new Array();
		IS_EventDispatcher.eventListenerList[eventId] = listenerList;
	}
	listenerList.push({id:eventId, listener:listener, obj:obj, isTemp:isTemp});
}

/*
 * Remove Event Listener
 * @type     name of the event
 * @id       identifier of the event component as HTML element
 * @listener callback function to spesify the listener
 */
IS_EventDispatcher.removeListener = function(type, id, listener){
	if(!type || type == "") return;
	var eventId = (id) ? type + "_" + id : type;
	var listenerList = IS_EventDispatcher.eventListenerList[eventId];
	if(listenerList){
		for(var i=0;i<listenerList.length;i++){
			if(listenerList[i].listener == listener){
				listenerList.remove(listenerList[i]);
				break;
			}
		}
	}
}

/*
 * Remove the list of Event Listener
 * @type     name of the event
 * @id       identifier of the event component as HTML element
 */
IS_EventDispatcher.removeListenerList = function(type, id){
	if(!type || type == "") return;
	var eventId = (id) ? type + "_" + id : type;
	var listenerList = IS_EventDispatcher.eventListenerList[eventId];
	if(listenerList){
		for(var i=0;i<listenerList.length;i++){
			listenerList.remove(listenerList[i]);
			break;
		}
	}
}

/*
 * Register Complex Event Lister
 * @childEventList
 *            list of event names
 *            event list specifies the pair of objects that include type and id
 *            example:{type:xxxEvent, id:div01}, {type:yyyEvent, id:div02}]
 * @id        identifier of the component as HTML element in case of determining the source of the event
 * @listener  callback function registered as listener
 * @obj       object associated with Listener
 * @isTemp    whether events are destroyed or not after they once are proceeded
 */
IS_EventDispatcher.addComplexListener = function(childEventList, listener, obj, isTemp){
	if(!childEventList || childEventList.length == 0) return;
	var syncEvent = new IS_EventDispatcher.ComplexEvent("dummy", "dummy", listener, obj, isTemp);
	for(var i = 0; i < childEventList.length; i++){
		if(childEventList[i].id){
			syncEvent.add(childEventList[i].type, childEventList[i].id);
		}else{
			syncEvent.add(childEventList[i].type);
		}
	}
	IS_EventDispatcher.complexEventListenerList.push(syncEvent);
}

/*
 * Register Complex Event Lister
 * @type     name of the event
 * @id       identifier of the component as HTML element in case of determining the source of the event
 * @childEventList
 *            list of event names
 *            event list specifies the pair of objects that include type and id
 *            example：[{type:xxxEvent, id:div01}, {type:yyyEvent, id:div02}]
 * @isTemp    whether events are destroyed or not after they once are proceeded
 */
IS_EventDispatcher.combineEvent = function(type, id, childEventList, isTemp){
	if(!childEventList || childEventList.length == 0) return;
	var syncEvent = new IS_EventDispatcher.ComplexEvent(type, id, null, null, isTemp);
	for(var i = 0; i < childEventList.length; i++){
		if(childEventList[i].id){
			syncEvent.add(childEventList[i].type, childEventList[i].id);
		}else{
			syncEvent.add(childEventList[i].type);
		}
	}
	IS_EventDispatcher.complexEventListenerList.push(syncEvent);
}

/*
 * Receive events that occurred
 * @type name of the event
 * @id   identifier of the component as HTML element
 * @obj  object passed to Listener
 */
IS_EventDispatcher.newEvent = function(type, id, obj){
	//Firstly, pass events to each Complex Event Listener; after getting all children events, generate the parent events
	var complexEventList = IS_EventDispatcher.complexEventListenerList;
	var completeEventList = [];
	for(var i = 0; i < complexEventList.length; i++){
		var complexEvent = complexEventList[i];
		if(!complexEvent.isComplete()){
			complexEvent.accept(type, id);
			if(complexEvent.isComplete(id)){
				completeEventList.push(complexEvent);
				/*if(complexEvent.callback){
					IS_EventDispatcher.executeFunction(complexEvent.callback, complexEvent.obj);
				}else if(complexEvent.type){
					IS_EventDispatcher.newEvent(complexEvent.type, complexEvent.id, null);
				}
				complexEvent.reset();
				if(complexEvent.isTemp){
					complexEventList.remove(complexEvent);
					i--;
				}*/
			}
		}
	}
	
	for(var i = 0; i < completeEventList.length; i++) {
		var complexEvent = completeEventList[i];
		if(complexEvent.callback){
			IS_EventDispatcher.executeFunction(complexEvent.callback, complexEvent.obj);
		}else if(complexEvent.type){
			IS_EventDispatcher.newEvent(complexEvent.type, complexEvent.id, null);
		}
		complexEvent.reset();
		if(complexEvent.isTemp){
			complexEventList.remove(complexEvent);
		}
	}

	/*
     * execute events that is stored in queue; Only the listener that execute synchronization is executed.
     * @_type name of the event
     * @_id   identifier of the component as HTML element
     * @_obj  object passed to Listener
     */
	function processEvent(_type, _id, _obj){
		var eventId = (_id) ? _type + "_" +  _id : _type;
		if(IS_EventDispatcher.eventListenerList[eventId]){
			var listenerList = IS_EventDispatcher.eventListenerList[eventId];
			for(var i = 0; i < listenerList.length;i++){
				try{
					IS_EventDispatcher.executeFunction(listenerList[i].listener,_obj);
					if(listenerList[i].isTemp){
						listenerList.remove(listenerList[i]);
						i--;
					}
				}catch(e){
					console.warn(e);
				}
			}
		}
		//IS_EventDispatcher.eventQueue.push({type:_type, id:_id, obj:_obj});
	};

	processEvent(type, id, obj);
}

/*
 * @private
 */
IS_EventDispatcher.executeFunction = function(func, obj){
	if(obj){
		// the type changes to "object" in case of passing to "this"
		func.call(this,obj);
	}else{
		func.call(this);
	}
}

/*
 * @private
 */
IS_EventDispatcher.ComplexEvent = function(_type, _id, _callback,  _obj, _isTemp){
	this.type = _type;
	this.id = _id;
	this.callback = _callback;
	this.obj = _obj;
	this.isTemp = _isTemp;
	
	this.eventList = [];

	this.add = function(_type){
		this.eventList.push({type:_type, isAccept:false});
	}
	
	this.add = function(_type, _componentId){
		var eventType = (_componentId && _componentId != "") ? _type + "_" + _componentId : _type;
		this.eventList.push({type:eventType, isAccept:false});
	};

	this.accept = function(_type, _componentId){
		for(var i = 0; i < this.eventList.length; i++){
			if(this.eventList[i].type == _type + "_" + _componentId){
				this.eventList[i].isAccept = true;
			}else if(this.eventList[i].type == _type){
				this.eventList[i].isAccept = true;
			}
		}
	};
	
	this.isComplete = function(){
		var complete = true;
		for(var i = 0; i < this.eventList.length; i++){
			if(!this.eventList[i].isAccept){
				complete = false;
				break;
			}
		}
		return complete;
	};

	this.reset = function(){
		for(var i = 0; i < this.eventList.length; i++){
			this.eventList[i].isAccept = false;
		}
	}
}

/*
IS_EventDispatcher.fire = function(){
	var queued = IS_EventDispatcher.eventQueue;
	for(var i = 0; i < queued.length; i++){
		var event = queued[i];
		if(IS_EventDispatcher.eventListenerList [event.type]){
			var listenerList = IS_EventDispatcher.eventListenerList[event.type];
			for(var i = 0; i < listenerList.length;i++){
				if(listenerList[i].obj){
					listenerList[i].listener.call(listenerList[i].obj, {type:_type, id:_id, obj:_obj});
				}else{
					listenerList[i].listener.call(this, {type:_type, id:_id, obj:_obj});
				}
				if(listenerList[i].isTemp){
					listenerList.remove(listenerList[i]);
				}
			}
		}
	}
	IS_EventDispatcher.eventQueue = [];
}
IS_EventDispatcher.repeat = function(freq){
	this.unrepeat();
	if (freq>0){
		this.freq=freq;
		this.repeater=setInterval('IS_EventDispatcher.fire()',freq);
	}
}

IS_EventDispatcher.unrepeat = function(){
	if (this.repeater){
		clearInterval(this.repeater);
	}
	this.repeater=null;
}

IS_EventDispatcher.repeat(200);
*/
//Event Wrapper for Memory leak 
IS_Event = new Object();
Object.extend(IS_Event, {
  KEY_BACKSPACE: 8,
  KEY_TAB:       9,
  KEY_RETURN:   13,
  KEY_ESC:      27,
  KEY_LEFT:     37,
  KEY_UP:       38,
  KEY_RIGHT:    39,
  KEY_DOWN:     40,
  KEY_DELETE:   46,

  element: function(event) {
    return event.target || event.srcElement;
  },

  isLeftClick: function(event) {
    return (((event.which) && (event.which == 1)) ||
            ((event.button) && (event.button == 1)));
  },

  pointerX: function(event) {
    return (event.pageX) || (event.clientX +
      (document.documentElement.scrollLeft || document.body.scrollLeft));
  },

  pointerY: function(event) {
    return (event.pageY) || (event.clientY +
      (document.documentElement.scrollTop || document.body.scrollTop));
  },

  stop: function(event) {
    if (event.preventDefault) {
      event.preventDefault();
      event.stopPropagation();
    } else {
      event.returnValue = false;
      event.cancelBubble = true;
    }
  },
  stopBubbling: function( event ) {
	if( event.stopPropagation){
		event.stopPropagation();
	} else {
		event.cancelBubble = true;
	}
  },

  // find the first node with the given tagName, starting from the
  // node the event was triggered on; traverses the DOM upwards
  findElement: function(event, tagName) {
    var element = IS_Event.element(event);
    while (element.parentNode && (!element.tagName ||
        (element.tagName.toUpperCase() != tagName.toUpperCase())))
      element = element.parentNode;
    return element;
  },

  observers: false,

  _observeAndCache: function(element, name, observer, useCapture, ids) {
    if (!ids) ids = "_";
    if (!this.observers) this.observers = {};
    if(Object.isArray(ids)){
      var relatedObservers = [];
      ids.each(function(id){
        if(!this.observers[id]) this.observers[id] = [];
		var o = [element, name, observer, useCapture, relatedObservers];
        this.observers[id].push(o);
		relatedObservers.push(o);
      }.bind(this));
    } else {
      if(!this.observers[ids]) this.observers[ids] = [];
      this.observers[ids].push([element, name, observer, useCapture]);
    }
    if (element.addEventListener) {
      element.addEventListener(name, observer, useCapture);
    } else if (element.attachEvent) {
      element.attachEvent('on' + name, observer);
    }
  },
  
  unloadAllCache: function() {
    if (!IS_Event.observers) return;
    for (var i in IS_Event.observers) {
      var observers = IS_Event.observers[i];
      if(observers && typeof observers != "function") {
        for (var j = 0; j < observers.length; j++) {
            IS_Event.stopObserving.apply(this, observers[j]);
            observers[j][0] = null;
        }
        IS_Event.observers[i] = null;
      }
    }
    IS_Event.observers = false;
    msg.debug("unloadAllCache");
  },

  unloadCache: function(id) {
    if (!IS_Event.observers) return;
    var observers = IS_Event.observers[id];
    if(!observers) return;
    for (var i = 0; i < observers.length; i++) {
	  if(!observers[i][0]) continue;
      IS_Event.stopObserving.apply(this, observers[i]);
      observers[i][0] = null;
      var relatedObservers = observers[i][4];
      if(relatedObservers){
        relatedObservers.each(function(robserver){
          if(robserver == observers[i]) return;
          IS_Event.stopObserving.apply(this, robserver);
          robserver[0] = null;
        }.bind(this));
      }
    }
    IS_Event.observers[id] = null;
//    msg.debug("unloadCache : id = " + id);
  },

  observe: function(element, name, observer, useCapture, id) {
    var element = $(element);
    useCapture = useCapture || false;

    if (name == 'keypress' &&
        (navigator.appVersion.match(/Konqueror|Safari|KHTML/)
        || element.attachEvent))
      name = 'keydown';

    this._observeAndCache(element, name, observer, useCapture, id);
  },

  stopObserving: function(element, name, observer, useCapture) {
    var element = $(element);
    useCapture = useCapture || false;
	if(!element) return;

    if (name == 'keypress' &&
        (navigator.appVersion.match(/Konqueror|Safari|KHTML/)
        || element.detachEvent))
      name = 'keydown';

    if (element.removeEventListener) {
      element.removeEventListener(name, observer, useCapture);
    } else if (element.detachEvent) {
      element.detachEvent('on' + name, observer);
    }
  }
});
Event.observe(window, "unload", IS_Event.unloadAllCache, false);
