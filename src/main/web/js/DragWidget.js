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

if(typeof Effect == 'undefined')
  throw("dragdrop.js requires including script.aculo.us' effects.js library");

var IS_Droppables = {
  drops: [],
  dropMap: {},
  groups: [],

  remove: function(){IS_DropGroup.common.remove.apply(IS_DropGroup.common, arguments)},
  add: function(){IS_DropGroup.common.add.apply(IS_DropGroup.common, arguments)},
  
  findDeepestChild: function(drops) {
    deepest = drops[0];
      
    for (i = 1; i < drops.length; ++i)
      if (Element.isParent(drops[i].element, deepest.element))
        deepest = drops[i];
    
    return deepest;
  },

  nextDeepestChild: function(deepest, drops) {
    for (var parent = deepest.element; true; ){
      parent = parent.parentNode;
      if(!parent || parent == document.body) break;
      for (i = 0; i < drops.length; ++i)
        if(drops[i].element == parent)
          return drops[i];
    }
    
    return null;
  },

  isContained: function(element, drop) {
    var containmentNode;
    if(drop.tree) {
      containmentNode = element.treeNode; 
    } else {
      containmentNode = element.parentNode;
    }
    return drop._containers.detect(function(c) { return containmentNode == c });
  },
  
  //Determine if it is on the target
  isAffected: function(point, element, drop, widgetType) {
  	var classNames = Element.classNames(element);
    return (
      (drop.element!=element) &&
      ((!drop._containers) ||
        this.isContained(element, drop)) &&
      ((!drop.accept) ||
        ( ((drop.accept instanceof Function) && drop.accept(element, widgetType, classNames)) ||
          (!(drop.accept instanceof Function) && 
            (classNames.detect( 
              function(v) { return drop.accept.include(v) } ) ))) ));
  },
  //Processing of returning Class if it is out of target
  deactivate: function(drop) {
    if(drop.hoverclass)
      Element.removeClassName(drop.element, drop.hoverclass);
    this.last_active = null;
  },

  //Changing Class if it is on the target
  activate: function(drop) {
    if(drop.hoverclass)
      Element.addClassName(drop.element, drop.hoverclass);
    this.last_active = drop;
  },
  
  //Called at dragging
  show: function(point, element, dragMode, widgetType) {
    var affected = [];
	// Check to the nearest items only
	var nearDropTarget = IS_Droppables.getNearDropTarget(element, point);
	
	for(var i=0;i<this.groups.length;i++){
		var panelTarget = this.groups[i].panelTarget;
		if(panelTarget && panelTarget.length > 0){
			panelTarget.each(function(drop){
				if(IS_Droppables.isAffected(point, element, drop, widgetType))
		      	  affected.push(drop);
			})
		}
	}
	
	if (nearDropTarget != null && nearDropTarget.length > 0) {
		nearDropTarget.each(function(drop){
			if(IS_Droppables.isAffected(point, element, drop, widgetType))
				affected.push(drop);
		})
	}
        
    if(affected.length>0) {
      drop = IS_Droppables.findDeepestChild(affected);
	  
      if(this.last_active && this.last_active != drop){
        //Processing if it is out of target(Cancel combining and etc...)
        if(this.last_active.outHover){
          this.last_active.outHover(element, this.last_active.element, Position.overlap(this.last_active.overlap, this.last_active.element));
        }
        this.deactivate(this.last_active);
      }
	
      for(var next = drop; true; ){
        if(next.onHover && next.onHover(element, next.element, dragMode, point, Position.overlap(next.overlap, next.element))) break;
        next = this.nextDeepestChild(next, affected);
        if(!next) break;
        drop = next;
      }
      
      IS_Droppables.activate(drop);
    }
  },

  showByElement: function(dropElement, point, element, dragMode, widgetType){
  	var activeDrop = null;
	for(var i =0; i<IS_Droppables.groups.length; i++){
		var group = IS_Droppables.groups[i];
		var drops = group.dropMap[dropElement.id];
		if(!drops) continue;
		drops.each(function(drop){
			if(IS_Droppables.isAffected(null, element, drop, widgetType)){
	      	  activeDrop = drop;
			  throw $break;
			}
		});
	}
	if(activeDrop){
		activeDrop.onHover(element, activeDrop.element, dragMode, point, Position.overlap(activeDrop.overlap, activeDrop.element));
		IS_Droppables.activate(activeDrop);
	}
  },

  fire: function(event, element, dropObject, widgetType) {
    if(!this.last_active) return;
    Position.prepare();
	
      if (this.last_active.onDrop) 
        this.last_active.onDrop(element, this.last_active.element, dropObject, event);
  },

  reset: function() {
    if(this.last_active)
      this.deactivate(this.last_active);
  }
}


IS_Droppables.replaceLocation = function(element,widget, x, y){
	if(widget.widgetType == "MultiRssReader"){
		var min = 10000000;
		var subNearGhost = null;
		var subCol = widget.elm_widgetContent.firstChild;
		var widgetGhost = IS_Draggable.ghost;
		var scrollOffset = IS_Portal.tabs[IS_Portal.currentTabId].panel.scrollTop;//replaceLocation is called in MultiRssReader. MultiRssReader must be in the panel.
		if( widgetGhost.parentNode != subCol ||
			( Browser.isSafari1 && !Element.visible( widgetGhost )) ){
			//Use existing logic if it goes inside MultiRssReader first time
			for (var j=0; j<subCol.childNodes.length; j++ ) {
				var div = subCol.childNodes[j];
				if (div == widgetGhost) {
					continue;
				}
				var position = Position.cumulativeOffset(div);
				var left = position[0];
				var top = position[1] - scrollOffset;
				var tmp = Math.sqrt(Math.pow(x-left,2)+ Math.pow(y-top,2));
				
				if (isNaN(tmp)) {
					continue;
				}
				if ( tmp < min ) {
					min = tmp;
					subNearGhost = div;
					subNearGhost.subCol = subCol;
				}
			}
			widgetGhost.style.display = "block";//for Safari
	
			if (subNearGhost != null && widgetGhost.nextSibling != subNearGhost) {
				subNearGhost.parentNode.insertBefore(widgetGhost,subNearGhost);
				widgetGhost.subCol = subNearGhost.subCol;
			}
			
			if( Browser.isSafari1 ) {
				if( (widgetGhost.nextSibling && element.id == widgetGhost.nextSibling.id)||
			 		(widgetGhost.previousSibling && element.id == widgetGhost.previousSibling.id)){
					widgetGhost.style.display = "none";
				}
			}
		}else{
			//Mouse cursor must fit into the height of MultiRssReader that does not display ghost in the case of moving inside MultiRssReader
			//Mouse is passed Multi and onHover to panel if it does not fit into.
			var ghostY = findPosY(widgetGhost) - scrollOffset;
			//Use the height of next widget of ghost as the base if the ghost is moved to down.
			//Mouse does not psas trough Multi if the ghost is moved to down within the height of next widget 
			var offsetY = widgetGhost.nextSibling ? widgetGhost.nextSibling.offsetHeight : widgetGhost.offsetHeight/2;
			var ghostBranch = ghostY + offsetY;

			var subNearGhost;
			if(y < ghostY){
				//Move to top if the cursor goes over ghost
				subNearGhost = widgetGhost.previousSibling;
			}else if(y < ghostBranch){
				//Leave the ghost if there is mouse curosr higher than base point
			}else{
				//Move the ghost to bottom if there is mouse curosr lower than base point 
				if( widgetGhost.nextSibling )
					subNearGhost = widgetGhost.nextSibling.nextSibling;
			}
			
			if( subNearGhost &&
				( !Browser.isSafari1 ||( subNearGhost.id != element.id )&&
				( !subNearGhost.previousSibling || subNearGhost.previousSibling.id != element.id )) ){
				widgetGhost.parentNode.insertBefore(widgetGhost, subNearGhost);
				widgetGhost.subCol = subCol;
			}
		}
	}
}

//Keep the coordinate of Droppable except ghost at start dragging and sort in 50px all around block
//It can be faster as check only the Droppable in 50px all around block while dragging
IS_Droppables.findDroppablesPos = function(element){
	var droppablesPositions = [];
	var maxY = maxX = 0;
	var scrollOffset = IS_Portal.tabs[IS_Portal.currentTabId].panel.scrollTop;
	for(var i =0; i<IS_Droppables.groups.length; i++){
		var group = IS_Droppables.groups[i];
		var inPanel = IS_DropGroup.common != group;//if not common, this group is IS_Widget.RssReader.dropGroup. This group is in the panel.
		var droppables = group.getDroppables(element);
		if(!droppables) continue;
		droppables.each(function(drop){
			var dropElement = drop.element;
			if(dropElement != document.body) {
				var pos = {drop:drop};
				//findWizPos is done in getDroppables of RssReader 
				if(dropElement.posLeft) pos.x = dropElement.posLeft;
				if(dropElement.posTop) pos.y = dropElement.posTop;
				if(!pos.x || !pos.y){
					var offset = Position.cumulativeOffset(dropElement);
					if(!pos.x) pos.x = offset[0];
					if(!pos.y) pos.y = offset[1];
				}
				if(inPanel){
					pos.y -= scrollOffset;
				}
				
				if(dropElement != element) {
					var startY = Math.floor(pos.y/50);
					var endY = Math.floor((pos.y + dropElement.offsetHeight)/50);
					var startX = Math.floor(pos.x/50);
					var endX = Math.floor((pos.x + dropElement.offsetWidth)/50);
					for(var x=startX; x<=endX; x++){
						if(!droppablesPositions[x]) droppablesPositions[x] = [];
						for(var y = startY; y <= endY; y++){
							if(!droppablesPositions[x][y]) droppablesPositions[x][y] = [];
								droppablesPositions[x][y].push(pos);
						}
					}
				}
			}
		});
	}
	IS_Droppables.positions = droppablesPositions;//Keep Droppable in xy coordinate 50px
}

IS_Droppables.getNearDropTarget = function(element, point){
	var x = point[0];
	var y = point[1];
	
	var ghost = IS_Draggable.ghost;
	Position.prepare();
	var ghostY = ((ghost.parentNode)? Position.cumulativeOffset(ghost)[1] : 0) -Position.realOffset( element )[1];
	//Adjust Y axis of mouse pointer if ghost is displayed.
	//Do not need Adjusting if ghostY==0 as ghost is not displayed.
	if(ghostY > 0){
		var ghostHeight = ghost.offsetHeight;
		var offsetY = ghostHeight/2;
		if(ghost.nextSibling && ghost.nextSibling.offsetHeight/2 < offsetY){
			//Use the height if the next widget is smaller than the ghost.
			//The mouse pointer is passed to the next widget if the ghost and the next widget is swaped.
			// This can be happened if the heigth of ghost is used
			offsetY = ghost.nextSibling.offsetHeight/2;
		}
		var ghostBrunch = ghostY + offsetY;
		if(y < ghostY){	
		} else if(y < ghostBrunch){
			//Mouse pointer is in the gap beween ghost and the previous widget if under the ghostBrunch
			//Therefore, make the panel for target.
			y = ghostY - 1;
		} else {
			//Move the mouse pointer to the top for offsetY value if over the ghostBrunch
			//There should be next widget on that position at strat dragging, the next widget is target
			y -= offsetY;
		}
	}
	
	var nearDropTarget = [];
	var targetElementList = [];
	
	var blockX = Math.floor(x/50);
	var blockY = Math.floor(y/50);
	
	if(IS_Droppables.positions[blockX] && IS_Droppables.positions[blockX][blockY]){
		IS_Droppables.positions[blockX][blockY].each(function(pos){
			var drop = pos.drop;
			var dropElement = drop.element;
			var effectiveHeight = (drop.marginBottom)? dropElement.offsetHeight - drop.marginBottom : dropElement.offsetHeight;
			var effectiveWidth = dropElement.offsetWidth;
			if (y >= pos.y &&
			    y <  pos.y + effectiveHeight &&
			    x >= pos.x &&
				x <  pos.x + effectiveWidth){
				nearDropTarget.push(drop);
			}
		});
	}
	
	return nearDropTarget;
}

/**
 * Use inheritance if necesarry
 * 
 */
var IS_DroppableOptions = {
	accept: "",
	onDrop: function(element) {
	},
	onHover: function(element, dropElement, dragMode, point) {
		var x = point[0] - element.boxLeftDiff;
		var y = point[1];//Leave y axis as getNearDropTarget

		var min = 10000000;
		var nearGhost = null;// widget near widget ghost
		var widgetGhost = IS_Draggable.ghost;
		widgetGhost.style.display = "block";//for Safari
		var scrollOffset = IS_Portal.tabs[IS_Portal.currentTabId].panel.scrollTop;//IS_Portal.columnsObjs must be in the panel.
		for ( var i=1; i <= IS_Portal.tabs[IS_Portal.currentTabId].numCol; i++ ) {
			var col = IS_Portal.columnsObjs[IS_Portal.currentTabId]["col_dp_" + i];
			for (var j=0; j<col.childNodes.length; j++ ) {
				var div = col.childNodes[j];
				if (div == widgetGhost) {
					continue;
				}
				
				var left = div.posLeft;//Coordinate exclude ghost
				var top = div.posTop - scrollOffset;
				
				var tmp = Math.sqrt(Math.pow(x-left,2)+ Math.pow(y-top,2));
				if (isNaN(tmp)) {
					continue;
				}
				
				if ( tmp < min ) {
					min = tmp;
					nearGhost = div;
					nearGhost.col = col;
				}
				
			}
		}
		
		if (nearGhost != null && widgetGhost.nextSibling != nearGhost) {
			widgetGhost.style.display = "block";
			nearGhost.parentNode.insertBefore(widgetGhost,nearGhost);
			widgetGhost.col = nearGhost.col;
		}
	},
	outHover: function(element) {
	}
}

/**
 * Display confirm at marging 
 */
IS_Droppables.mergeConfirm = function(element, lastActiveElement, draggedWidget, event, originFunc, cancelFunc, targetTitle, modalOption){
	if (!modalOption && IS_Portal.mergeconfirm) {
		var self = this;
		var widgetGhost = IS_Draggable.ghost;
		var ghostParent = widgetGhost.parentNode;
		var ghostNextSibling = widgetGhost.nextSibling;
		
		var contentPane = document.createElement("div");
		Element.addClassName(contentPane, "preference");
		var modal = new Control.Modal('', {
			className: 'preference',
			closeOnClick: false,
		    afterOpen:function(){
		    	this.container.update(contentPane);
		    },
		    afterClose:function(){
		    	this.destroy();
		    }
		});
		var dialogPane = document.createElement("div");
		contentPane.appendChild(dialogPane);
		
		var messagePane = document.createElement("div");
		messagePane.style.padding = "5px";
		dialogPane.appendChild(messagePane);
		
		var message = document.createElement("h3");
		message.appendChild( document.createTextNode(
			IS_R.getResource(IS_R.ms_mergeConfirm,[targetTitle, draggedWidget.title]) ));
		messagePane.appendChild(message);
		
		var mergeOpt = new Object();
		mergeOpt[ IS_R.lb_doMerge ] = "merge";
		mergeOpt[ IS_R.lb_doNotMerge ] = "cancel";
		var options = $H( mergeOpt );
		
		var optionPane = document.createElement("div");
		optionPane.style.textAlign = "center";
		dialogPane.appendChild(optionPane);
		
		var optionList = document.createElement("div");
		optionPane.appendChild(optionList);
		
		var checkDiv = document.createElement("div");
		optionPane.appendChild(checkDiv);
		
		var checkbox = document.createElement("input");
		checkbox.type = "checkbox";
		checkDiv.appendChild(checkbox);
		
		var checkLabel = document.createElement("label");
		checkLabel.innerHTML = IS_R.lb_notConfirm;
		
		checkDiv.appendChild(checkLabel);
		options.each( function( entry,index ) {
			var option = document.createElement("input");
			option.type = "button";
			option.value = entry.key;
			option.style.margin = "5px";
			
			optionList.appendChild(option);
			
			function handleClick(event){
				IS_Portal.mergeconfirm = getBooleanValue(checkbox.checked) ? false : true;
				IS_Widget.setPreferenceCommand("mergeconfirm", IS_Portal.mergeconfirm);
				
				var clickedElement = Event.element(event);
				Control.Modal.close();

				modalOption = entry.value;
				IS_Droppables.isConfirm = false;
				originFunc.call(self, element, lastActiveElement, draggedWidget, event, modalOption);
				
				IS_Event.stopObserving(clickedElement, 'click', handleClick);
			}
			IS_Event.observe(option, 'click', handleClick);
		});
		
		if(draggedWidget.isBuilt)
			draggedWidget.elm_widget.style.display = "none";
			
		modal.open();
		modal.position();
		// Flag indicate if Confirm is displayed
		IS_Droppables.isConfirm = true;
		
		return false;
	} else if(modalOption && modalOption == "cancel"){
		// Return to the original place
		if (draggedWidget.isBuilt) {
			draggedWidget.elm_widget.style.display = "";
			if (draggedWidget.elm_widget.beforeParent) {
				if (draggedWidget.elm_widget.beforeNextSibling) {
					draggedWidget.elm_widget.beforeParent.insertBefore(draggedWidget.elm_widget, draggedWidget.elm_widget.beforeNextSibling);
				}
				else {
					draggedWidget.elm_widget.beforeParent.appendChild(draggedWidget.elm_widget);
				}
			}
		}
		cancelFunc.call(self);
		Element.remove(IS_Draggable.ghost);
		return false;
	}
	return true;
}

var IS_DropGroup = function(options){
	this.drops = [];
	this.dropMap = {};
	this.remove = function(element) {
		this.drops = this.drops.reject(function(d) { return d.element==$(element) });
		this.dropMap[$(element).id] = null;
	},
	this.add = function(element) {
			element = $(element);
			var options = Object.extend({
				greedy: true,
				hoverclass: null,
				tree: false
			}, arguments[1] || {});
	
			// cache containers
			if(options.containment) {
				options._containers = [];
				var containment = options.containment;
				if((typeof containment == 'object') && 
					(containment.constructor == Array)) {
					containment.each( function(c) { options._containers.push($(c)) });
				} else {
					options._containers.push($(containment));
				}
			}
			
			if(options.accept && !(options.accept instanceof Function)) options.accept = [options.accept].flatten();
		
		// CPU usage is increased in Firefox. IE may be fixed 
		// Widget style is broke up if deleted position = 'relative'(IE)
			options.element = element;
		
		if(element == document.body){
			if(!this.panelTarget)
				this.panelTarget = [];
			
			this.panelTarget.push(options);
		}
		
		if(!this.dropMap[element.id])
			this.dropMap[element.id] = [];
		this.dropMap[element.id].push(options);
		
			this.drops.push(options);
	};
	this.getDroppables = function(){
		return options.getDroppables.apply(this, arguments);
	};
	this.getDropObjByElement = function(element){
		return this.dropMap[element.id];
	},
	IS_Droppables.groups.push(this);
}

IS_DropGroup.common = new IS_DropGroup({
	getDroppables : function(element){
		return this.drops;
	}
});

var IS_Draggables = {
  drags: [],
  observers: [],
  
  //Register draggable object. Give clcik event here
  register: function(draggable) {
    if(this.drags.length == 0) {
      this.eventMouseUp   = this.endDrag.bindAsEventListener(this);
      this.eventMouseMove = this.updateDrag.bindAsEventListener(this);
      
      Event.observe(document, "mouseup", this.eventMouseUp);
    }
    this.drags.push(draggable);
  },
  
  //Delete draggble object (call expressly)
  unregister: function(draggable) {
    this.drags = this.drags.reject(function(d) { return d==draggable });
    if(this.drags.length == 0) {
      Event.stopObserving(document, "mouseup", this.eventMouseUp);
      Event.stopObserving(document, "mousemove", this.eventMouseMove);
    }
  },
  
  //Called at dragStart
  activate: function(draggable) {
    if(draggable.options.delay) { 
      this._timeout = setTimeout(function() { 
        IS_Draggables._timeout = null; 
        window.focus(); 
        IS_Draggables.activeDraggable = draggable; 
      }.bind(this), draggable.options.delay); 
    } else {
      window.focus(); // allows keypress events if window isn't currently focused, fails for Safari
      this.activeDraggable = draggable;
    }
	
	this.eventSelectStart = function(){return false;};
	Event.observe(document, "selectstart", this.eventSelectStart);
	Event.observe(document, "mousemove", this.eventMouseMove);

	if(this.msgTimer) clearTimeout(this.msgTimer);
	this.msgTimer = setTimeout(IS_Portal.displayMsgBar.bind(this, "mergemode_nomerge", IS_R.ms_dragNoMerge), 300);
  },
  
  //Called at dragEnd
  deactivate: function() {
    this.activeDraggable = null;
	Event.stopObserving(document, "mousemove", this.eventMouseMove);
  },
  
  //Always called at mouseMove
  updateDrag: function(event) {
    if(!this.activeDraggable) return;
    var pointer = [Event.pointerX(event), Event.pointerY(event)];
    // Mozilla-based browsers fire successive mousemove events with
    // the same coordinates, prevent needless redrawing (moz bug?)
    if(this._lastPointer && (this._lastPointer.inspect() == pointer.inspect())) return;
    this._lastPointer = pointer;
    this.activeDraggable.updateDrag(event, pointer);
  },
  
  //Called at dragEnd
  endDrag: function(event) {
    if(this._timeout) { 
      clearTimeout(this._timeout); 
      this._timeout = null; 
    }
    if(!this.activeDraggable) return;
    this._lastPointer = null;
    this.activeDraggable.endDrag(event);
    this.activeDraggable = null;
	
	if(this.msgTimer) clearTimeout(this.msgTimer);
	IS_Portal.unDisplayMsgBar("mergemode_nomerge");
	Event.stopObserving(document, "selectstart", this.eventSelectStart);
  },
  
  notify: function(eventName, draggable, event) {  // 'onStart', 'onEnd', 'onDrag'
    if(this[eventName+'Count'] > 0)
      this.observers.each( function(o) {
        if(o[eventName]) o[eventName](eventName, draggable, event);
      });
    if(draggable.options[eventName]) draggable.options[eventName](draggable, event);
  }
}

/*--------------------------------------------------------------------------*/

var IS_Draggable = Class.create();
IS_Draggable._dragging    = {};

IS_Draggable.prototype = {
  initialize: function(element) {
    var defaults = {
      handle: false,
      reverteffect: function(element, top_offset, left_offset) {
        var dur = Math.sqrt(Math.abs(top_offset^2)+Math.abs(left_offset^2))*0.02;
        new Effect.Move(element, { x: -left_offset, y: -top_offset, duration: dur,
          queue: {scope:'_draggable', position:'end'}
        });
      },

      zindex: 1000,
      revert: false,
      scroll: fixedPortalHeader ? false : window,
      scrollPanel: fixedPortalHeader,//if true, set scroll property to the current panel.
      scrollSensitivity: 20,
      scrollSpeed: 15,
      snap: false,  // false, or xy or [x,y] or function(x,y){ return [x,y] }
      delay: 0
    };
    
    
    var options = Object.extend(defaults, arguments[1] || {});

    this.element = $(element);
    
    if(options.handle && (typeof options.handle == 'string'))
      this.handle = this.element.down('.'+options.handle, 0);
    
    if(!this.handle) this.handle = $(options.handle);
    if(!this.handle) this.handle = this.element;
    
    if(options.scroll && !options.scroll.scrollTo && !options.scroll.outerHTML) {
      options.scroll = $(options.scroll);
      this._isScrollChild = Element.childOf(this.element, options.scroll);
    }
	
	// Widget style is broke up if deleted position = 'relative'(IE)
    //Element.makePositioned(this.element); // fix IE    

    this.delta    = this.currentDelta();
    this.options  = options;
    this.dragging = false;   

    this.eventMouseDown = this.initDrag.bindAsEventListener(this);
    Event.observe(this.handle, "mousedown", this.eventMouseDown);
    
    IS_Draggables.register(this);
  },
  
  destroy: function() {
    Event.stopObserving(this.handle, "mousedown", this.eventMouseDown);
    IS_Draggables.unregister(this);
  },
  
  currentDelta: function(element) {
    if(!element)
      element = this.element;
    return([
      parseInt(Element.getStyle(element,'left') || '0'),
      parseInt(Element.getStyle(element,'top') || '0')]);
  },
  
  //Always called at dragStart
  initDrag: function(event) {
 	var divIFrame = $("portal-iframe");
	
	if(!this.options.ignoreDisplayPanel){
		if( isHidePanel() )
			return;
	}
	
	if(IS_Draggable._lastDraggingTime){
		var span = new Date().getTime() - IS_Draggable._lastDraggingTime;
		if(span < 1000) return;
	}
	IS_Draggable._lastDraggingTime = new Date().getTime();
	
    if(Event.isLeftClick(event)) {    
      // abort on form elements, fixes a Firefox issue
      var src = Event.element(event);
      if((tag_name = src.tagName.toUpperCase()) && (
        tag_name=='INPUT' ||
        tag_name=='SELECT' ||
        tag_name=='OPTION' ||
        tag_name=='BUTTON' ||
        tag_name=='TEXTAREA')) return;
        
      var pointer = [Event.pointerX(event), Event.pointerY(event)];
	  
	  //added boxLeftDiff,boxTopDiff
      var pos     = Position.cumulativeOffset(this.element);
	  
      this.offset = [0,1].map( function(i) { return (pointer[i] - pos[i]) });
      this.element.boxLeftDiff = this.offset[0];
      this.element.boxTopDiff = this.offset[1];
      
      IS_Draggables.activate(this);
	  
	  Position.prepare();
	  IS_Draggables.updateDrag(event, pointer);
	  
      Event.stop(event);
    }
  },
  
  startDrag: function(event, pointer) {
	  document.body.style.overflow = 'hidden';
    this.dragging = true;
	IS_Portal.isItemDragging = true;
	
    IS_Portal.showDragOverlay(this.options.handle ? Element.getStyle(this.options.handle, "cursor") : null);
    
    if(this.options.scrollPanel){
      this.options.scroll = IS_Portal.tabs[IS_Portal.currentTabId].panel;
    }
	
    if(this.options.zindex) {
      this.originalZ = parseInt(Element.getStyle(this.element,'z-index') || 0);
      this.element.style.zIndex = this.options.zindex;
    }
    
      if(IS_Draggable.dummyElement){
	  	IS_Draggable.dummyElement.innerHTML = "";
	  }else{
		// Create dummy in innetHTML by defalut
		var dummyElement = document.createElement("div");
		document.body.appendChild(dummyElement);
		dummyElement.style.position = "absolute";
		dummyElement.style.zIndex = 10000;
		IS_Draggable.dummyElement = dummyElement;
	  }
	  
	  var dummyContent;
	  if(this.options.getDummy){
		  var dummyContent = this.options.getDummy(this.element);
		  dummyContent.style.height = "100%";
		  IS_Draggable.dummyElement.appendChild(dummyContent);
	  } else{
		  var dummyContent = document.createElement(this.element.nodeName? this.element.nodeName : "div");
		  dummyContent.className = this.element.className;
		  $(dummyContent).addClassName("dummy");
		  dummyContent.innerHTML = this.element.innerHTML;
		  dummyContent.style.height = "100%";
		  IS_Draggable.dummyElement.appendChild(dummyContent);
		}
	  
	  dummyContent.style.marginLeft = "0em";
	  dummyContent.style.paddingLeft = "0em";
	  dummyContent.style.marginTop = "0em";
	  
	  var elementHeight = this.element.offsetHeight + "px";
	  var dummyStyle = IS_Draggable.dummyElement.style;
	  dummyStyle.display = "";
	  dummyStyle.height = elementHeight;
	  dummyStyle.width = (this.options.dummyWidth)? this.options.dummyWidth : this.element.offsetWidth + "px";
	  Element.setOpacity(IS_Draggable.dummyElement, 0.7);
	  	
	  var pos = Position.cumulativeOffset(this.element);
	  var offset = Position.realOffset(this.element);
      dummyStyle.left = pos[0] - offset[0] + 'px';
	  dummyStyle.top = pos[1] + 'px';
	  this.scrollOffset = offset;

	if(this.options.ghosting) {
	  this.ghost = document.createElement("div");
	  this.ghost.id = "widgetGhost";
	  this.ghost.col = this.element.parentNode;
	  IS_Draggable.ghost = this.ghost;
	  
	  // Slow if it is substituted by height before append.
	  this.ghost.style.height = elementHeight;
    }
    
    if(this.options.scroll) {
      if (this.options.scroll == window) {
		this.originalScrollLeft = Position.deltaX;
		this.originalScrollTop = Position.deltaY;
		Event.observe(window, "scroll", this.scrollPositionPrepare);
      } else {
        this.originalScrollLeft = this.options.scroll.scrollLeft;
        this.originalScrollTop = this.options.scroll.scrollTop;
      }
    }
    
    if(this.options.viewport ) {
    	dummyStyle.left = pos[0] - this.options.viewport.scrollLeft + 'px';
    	dummyStyle.top = pos[1] - this.options.viewport.scrollTop + 'px';
    }
    
	IS_Droppables.findDroppablesPos(this.element);
    IS_Draggables.notify('onStart', this, event);
	
	Event.observe(document, 'keydown', IS_Draggables.keyEvent.keyDownHandler, false);
	Event.observe(document, 'keyup', IS_Draggables.keyEvent.keyUpHandler, false);
	
	if(this.options.move)
		this.element.style.display = "none";
  },
  
  //Processing of dragging
  updateDrag: function(event, pointer) {
  	var isFirstUpdating = false;
    if(!this.dragging) {
		isFirstUpdating = true;
		this.startDrag(event, pointer);
	}
	
	var now = new Date().getTime();
	if(IS_Droppables.span) {
		IS_Droppables.span += now - IS_Droppables.lastTime;
		IS_Droppables.lastTime = now;
	}
	if(!this.options.disableDragEvent){
		if(isFirstUpdating && this.options.startDroppableElement){
			var startDroppableElement = this.options.startDroppableElement;
			if(typeof startDroppableElement == "function")
				startDroppableElement = startDroppableElement();
			IS_Droppables.showByElement(startDroppableElement, pointer, this.element, this.options.dragMode, this.options.widgetType);
		} else if(!IS_Droppables.span || IS_Droppables.span > 100){
			IS_Droppables.span = 1;
			IS_Droppables.show(pointer, this.element, this.options.dragMode, this.options.widgetType);
		}
    	IS_Draggables.notify('onDrag', this, event);
    }
	
	if(!isFirstUpdating)
    	this.draw(pointer);
    
    if(this.options.scroll) {
      this.stopScrolling();
      
      var p;
      if (this.options.scroll == window) {
        with(this._getWindowScroll(this.options.scroll)){
			p = [ left, top, left+width, top+height ];
		}
      } else {
        p = Position.page(this.options.scroll);
        p[0] += this.options.scroll.scrollLeft + Position.deltaX;
        p[1] += this.options.scroll.scrollTop + Position.deltaY;
        p[2] = p[0]+this.options.scroll.offsetWidth;
        p[3] = p[1]+this.options.scroll.offsetHeight;
      }
      var speed = [0,0];
      if(pointer[0] < (p[0]+this.options.scrollSensitivity)) speed[0] = pointer[0]-(p[0]+this.options.scrollSensitivity);
      if(pointer[1] < (p[1]+this.options.scrollSensitivity)) speed[1] = pointer[1]-(p[1]+this.options.scrollSensitivity);
      if(pointer[0] > (p[2]-this.options.scrollSensitivity)) speed[0] = pointer[0]-(p[2]-this.options.scrollSensitivity);
      if(pointer[1] > (p[3]-this.options.scrollSensitivity)) speed[1] = pointer[1]-(p[3]-this.options.scrollSensitivity);
      this.startScrolling(speed);
    }
    
    // fix AppleWebKit rendering
    if(navigator.appVersion.indexOf('AppleWebKit')>0) window.scrollBy(0,0);
    
    Event.stop(event);
  },
  
  //Processing of dragEnd
  finishDrag: function(event, success) {
    this.dragging = false;
	
	IS_Portal.isItemDragging = false;
	IS_Portal.hideDragOverlay();
	
	// fix 908
	if( Browser.isFirefox3 || Browser.isChrome )
  	  IS_Draggables.deactivate(this);
	
	//Craete object for passing to onDrop
	var dropObject;
	if(this.options.getDropObject){
		dropObject = this.options.getDropObject();
	}
	
    if (success) {
		IS_Droppables.fire(event, this.element, dropObject, this.options.widgetType);
	}
    IS_Draggables.notify('onEnd', this, event);
	
	if(this.options.move && !IS_Droppables.isConfirm)
		this.element.style.display = "";
	
	//Delete moved:ghost afetr onDrop
    if(this.options.ghosting) {
	  
      IS_Draggable.dummyElement.style.display = "none";
      if(this.ghost.parentNode && !IS_Droppables.isConfirm)
	  	Element.remove(this.ghost);
      this.ghost = null;
    }
	
	if(this.options.scroll)
		Event.stopObserving(window, "scroll", this.scrollPositionPrepare);
	
    var revert = this.options.revert;
    if(revert && typeof revert == 'function') revert = revert(this.element);
    
    var d = this.currentDelta();
    if(revert && this.options.reverteffect) {
      this.options.reverteffect(this.element, 
        d[1]-this.delta[1], d[0]-this.delta[0]);
    } else {
      this.delta = d;
    }

    if(this.options.zindex)
      this.element.style.zIndex = this.originalZ;

	Event.stopObserving(document, 'keydown', IS_Draggables.keyEvent.keyDownHandler, false);
	Event.stopObserving(document, 'keyup', IS_Draggables.keyEvent.keyUpHandler, false);
	IS_Draggables.keyEvent.reset();
      
// Too late to do this here.
	if( !Browser.isFirefox3 )
    	IS_Draggables.deactivate(this);
    IS_Droppables.reset();
	
  },
  
  endDrag: function(event) {
    if(!this.dragging) return;
    this.stopScrolling();
    this.finishDrag(event, true);
	document.body.style.overflow = '';
    Event.stop(event);
  },
  
  //Draw dragging
  draw: function(point, element, dummyElement) {
    var scrollTop
    if(this.options.scroll == window){
        scrollTop = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop;
    }else{
        scrollTop = this.options.scroll.scrollTop;
    }
    scrollTop = parseInt(scrollTop);
    
  	point = [point[0]-document.documentElement.scrollLeft, point[1]-scrollTop];
  	if(!element || !dummyElement)
		element = dummyElement = IS_Draggable.dummyElement;

    var pos = Position.cumulativeOffset(element);
	
    if(this.options.ghosting) {
        pos[0] -= Position.deltaX; pos[1] -= Position.deltaY;
    }
    
    var d = this.currentDelta(dummyElement);
    pos[0] -= d[0]; pos[1] -= d[1];
    
    if(this.options.scroll && (this.options.scroll != window && this._isScrollChild)) {
      pos[0] -= this.options.scroll.scrollLeft-this.originalScrollLeft;
      pos[1] -= this.options.scroll.scrollTop-this.originalScrollTop;
    }
    
    if( this.options.viewport ) {
    	pos[0] += this.options.viewport.scrollLeft;
    	pos[1] += this.options.viewport.scrollTop;
    }
    
    var p = [0,1].map(function(i){
      return (point[i]-pos[i]-this.offset[i]);
    }.bind(this));
    
    var style = dummyElement.style;
    style.left = p[0] + "px";
    style.top  = p[1] + "px";
    if(style.visibility=="hidden") style.visibility = ""; // fix gecko rendering
  },
  
  scrollPositionPrepare: function() {
  	Position.prepare();
  },
  
  stopScrolling: function() {
    if(this.scrollInterval) {
      clearInterval(this.scrollInterval);
      this.scrollInterval = null;
      IS_Draggables._lastScrollPointer = null;
    }
  },
  
  startScrolling: function(speed) {
    if(!(speed[0] || speed[1])) return;
    this.scrollSpeed = [speed[0]*this.options.scrollSpeed,speed[1]*this.options.scrollSpeed];
    this.lastScrolled = new Date();
    this.scrollInterval = setInterval(this.scroll.bind(this), 50);
  },
  
  scroll: function() {
    var current = new Date();
    var delta = current - this.lastScrolled;
    this.lastScrolled = current;
    if(this.options.scroll == window) {
      with (this._getWindowScroll(this.options.scroll)) {
        if (this.scrollSpeed[0] || this.scrollSpeed[1]) {
          var d = delta / 1000;
          this.options.scroll.scrollTo( left + d*this.scrollSpeed[0], top + d*this.scrollSpeed[1] );
        }
      }
    } else {
      this.options.scroll.scrollLeft += this.scrollSpeed[0] * delta / 1000;
      this.options.scroll.scrollTop  += this.scrollSpeed[1] * delta / 1000;
    }
    
    Position.prepare();
	
	if(!this.options.disableDragEvent){
    	IS_Droppables.show(IS_Draggables._lastPointer, this.element, this.options.dragMode, this.options.widgetType);
    	IS_Draggables.notify('onDrag', this);
	}
	
    if (this._isScrollChild) {
      IS_Draggables._lastScrollPointer = IS_Draggables._lastScrollPointer || $A(IS_Draggables._lastPointer);
      IS_Draggables._lastScrollPointer[0] += this.scrollSpeed[0] * delta / 1000;
      IS_Draggables._lastScrollPointer[1] += this.scrollSpeed[1] * delta / 1000;
      if (IS_Draggables._lastScrollPointer[0] < 0)
        IS_Draggables._lastScrollPointer[0] = 0;
      if (IS_Draggables._lastScrollPointer[1] < 0)
        IS_Draggables._lastScrollPointer[1] = 0;
      this.draw(IS_Draggables._lastScrollPointer);
    }
    
    //if fixedPortalHeader, recompute the position of droppable elements. Because scrollTop of the panel changes when scrolling.
    if(this.options.scrollPanel)
      IS_Droppables.findDroppablesPos(this.element);
    
    if(this.options.change) this.options.change(this);
  },
  
  _getWindowScroll: function(w) {
    var T, L, W, H;
    with (w.document) {
      if (w.document.documentElement && documentElement.scrollTop) {
        T = documentElement.scrollTop;
        L = documentElement.scrollLeft;
      } else if (w.document.body) {
        T = body.scrollTop;
        L = body.scrollLeft;
      }
      if (w.innerWidth) {
        W = w.innerWidth;
        H = w.innerHeight;
      } else if (w.document.documentElement && documentElement.clientWidth) {
        W = documentElement.clientWidth;
        H = documentElement.clientHeight;
      } else {
        W = body.offsetWidth;
        H = body.offsetHeight
      }
    }
    return { top: T, left: L, width: W, height: H };
  }
}

if( Browser.isSafari1 ) {
	IS_Draggable.prototype.initialize = ( function() {
		var initialize = IS_Draggable.prototype.initialize;
		
		return function() {
			initialize.apply( this,$A( arguments ));
			
			this.options.scrollSensitivity = 100;
		}
	})();
	
	IS_Draggable.prototype.startDrag = ( function() {
		var startDrag = IS_Draggable.prototype.startDrag;
		
		return function() {
			startDrag.apply( this,$A( arguments ));
			
			Element.show( this.element );
			Element.setOpacity( this.element,0.3 );
			
			var bar = $("autoScrollHoldBar");
			if( !bar ) {
				var bar = IS_Widget.RssReader.RssItemRender.createTable(1,1);
				bar.id = "autoScrollHoldBar";
				bar.style.width = 0;
				bar.style.height = 0;
				bar.style.top = 0;
				bar.style.position = "absolute";
				
				document.body.appendChild( bar );
			}
			
			bar.style.display = "";
			bar.style.height = document.documentElement.offsetHeight+'px';
		}
	})();
	
	IS_Draggable.prototype.updateDrag = ( function() {
		var updateDrag = IS_Draggable.prototype.updateDrag;
		
		return function() {
			updateDrag.apply( this,$A( arguments ));
			
			var bar = $("autoScrollHoldBar");
			var y = parseInt(( IS_Draggable.dummyElement.style.top+"").match(/(\d+)/)[1]) +
				IS_Draggable.dummyElement.offsetHeight;
			
			if( bar.offsetHeight < y )
				bar.style.height = y+'px';
		}
	})();
	
	IS_Draggable.prototype.finishDrag = ( function() {
		var finishDrag = IS_Draggable.prototype.finishDrag;
		
		return function() {
			Element.setOpacity( this.element,1 );
		  	
			finishDrag.apply( this,$A( arguments ));
			
			var bar = $("autoScrollHoldBar");
			bar.style.height = '1px';
			bar.style.display = "none"
		}
	})();
}

IS_Draggables.keyEvent = new function(){
	var self = this;
	this.keyDownEventList = [];
	this.keyUpEventList = [];
	this.isPressing = {};
	
	this.keyDownHandler = function(e){
		Event.stop(e);
		if(self.keyPressed) return;
		self.keyPressed = true;
		setPressKey(e);
		for(var i=0;i<self.keyDownEventList.length;i++){
			self.keyDownEventList[i].call(self);
		}
	};
	this.keyUpHandler = function(e){
		self.keyPressed = false;
		setPressKey(e);
		for(var i=0;i<self.keyUpEventList.length;i++){
			self.keyUpEventList[i].call(self);
		}
	};
	this.addKeyDownEvent = function(func){
		self.keyDownEventList.push(func);
	};
	this.addKeyUpEvent = function(func){
		self.keyUpEventList.push(func);
	};
	this.reset = function(){
		self.isPressing.ctrl = false;
		self.keyPressed = false;
	};
	function setPressKey(e){
		if (!Browser.isIE) {  
			self.isPressing.ctrl = typeof e.modifiers == 'undefined' ? e.ctrlKey : e.modifiers & Event.CONTROL_MASK; 
		} else { 
			self.isPressing.ctrl = event.ctrlKey; 
		}
	}
}

// Returns true if child is contained within element
Element.isParent = function(child, element) {
  if (!child.parentNode || child == element) return false;
  if (child.parentNode == element) return true;
  return Element.isParent(child.parentNode, element);
}

Element.findChildren = function(element, only, recursive, tagName) {    
  if(!element.hasChildNodes()) return null;
  tagName = tagName.toUpperCase();
  if(only) only = [only].flatten();
  var elements = [];
  $A(element.childNodes).each( function(e) {
    if(e.tagName && e.tagName.toUpperCase()==tagName &&
      (!only || (Element.classNames(e).detect(function(v) { return only.include(v) }))))
        elements.push(e);
    if(recursive) {
      var grandchildren = Element.findChildren(e, only, recursive, tagName);
      if(grandchildren) elements.push(grandchildren);
    }
  });

  return (elements.length>0 ? elements.flatten() : []);
}

Element.offsetSize = function (element, type) {
  return element['offset' + ((type=='vertical' || type=='height') ? 'Height' : 'Width')];
}
