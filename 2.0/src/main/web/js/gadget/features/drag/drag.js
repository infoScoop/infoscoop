_IG_Drag = function() {
	Draggables.addObserver( {
		onStart : this.dragObserver_onStart.bind( this ),
		onEnd : this.dragObserver_onEnd.bind( this ),
		onDrag : this.dragObserver_onDrag.bind( this )
	} );
}

_IG_Drag.prototype = {
	_onDragStart : function(newSource) {
//		console.info("onDragStart: "+newSource )
		if( this.onDragStart ) this.onDragStart( newSource );
	},
	_onDragEnd : function(source, target) {
//		console.info("onDragEnd: "+source.id+","+((target) ? target.id : target));
		this.onDragEnd( source,target );
	},
	_onDragTargetHit : function(newTarget, lastTarget) { 
//		console.info("onDragTargetHit: "+newTarget.id+","+((lastTarget) ? lastTarget.id : lastTarget));
		if( this.onDragTargetHit ) this.onDragTargetHit( newTarget,lastTarget );
	},
	_onDragTargetLost : function(lastTarget) { 
//		console.info("onDragTargetLost: "+lastTarget )
		if( this.onDragTargetLost ) this.onDragTargetLost( lastTarget )
	},
	_onDragClick : function(target) { 
//		console.info("onDragClick: "+target)
		if( this.onDragClick ) this.onDragClick( target )
	},
	
	sources : {},
	addSource : function() {
		var source = {
			id : null,
			element : null,
			surrogate : null
		}
		
		var args = $A( arguments );
		var surrogate;
		if( args.length == 1 ) {
			source.element = $( args.shift());
			source.id = ( source.element )? source.element.id : undefined;
		} else {
			source.id = args.shift();
			source.element = $( args.shift());
			source.surrogate = args.shift();
		}
		
		if( !source.element.style.cursor )
			source.element.style.cursor = "pointer";
		
		var draggableOpt = {
			handle : source.element
		};
		
		var div = document.createElement("div");
		div.id = "surrogate_"+new Date().getTime()+source.id;
		source.ghost = div;
		
		var self = this;
		source.reposition = function( source ) {
			var pos = Position.cumulativeOffset(source.element);
			var offset = [self._IG_Drag_surrogateOffsetX,self._IG_Drag_surrogateOffsetY,
				source.element._IG_Drag_surrogateOffsetX,source.element._IG_Drag_surrogateOffsetY]
			for( var i=0;i<offset.length;i++) {
				if( !offset[i] )
					offset[i] = 0
			}
			source.ghost.style.left = pos[0] +offset[0] +offset[2];
			source.ghost.style.top = pos[1] +offset[1] +offset[3];
		}
		div.style.position = "absolute";
		div.style.visibility = "hidden";
		div.style.whiteSpace = "nowrap";
		div.style.cursor = "move";
		
		source.reposition( source );
		document.body.appendChild( div );
		
		var draggable = new Draggable( source.ghost,draggableOpt );
		source.draggable = draggable;
		this.sources[source.id] = source;
		
		Draggables.register( draggable );
		
		Event.observe( source.element,"click",this._onDragClick.bind( this,source.element ));
	},
	removeSource : function(sid) {
		var source = this.sources[sid]
		if( !source )
			return;
		
		Draggables.unregister( source.draggable );
		delete this.sources[source.id];
		
		document.body.removeChild( source.ghost );
		Draggables.removeObserver( source.element );
		if( source.element.style.cursor == "pointer")
			source.element.style.cursor = "";
	},
	removeAllSources: function() {
		var this_ = this;
		$H( this.sources ).each( function( entry ) {
			this_.removeSource( entry.key );
		});
	},
	getSourceByElement : function( element ) {
		return $H( this.sources ).values().find( function( source ) {
			return ( source.element == element )||( source.ghost == element )
		});
	},
	targets : [],
	addTarget : function() {
		var target = {
			id : null,
			element : null
		}
		
		var args = $A( arguments );
		if( args.length == 1 ) {
			target.element = $( args.shift());
			target.id = ( target.element )? target.element.id : undefined;
		} else {
			target.id = args.shift();
			target.element = $( args.shift());
		}
		
		this.targets.push( target );
		Droppables.add( target.element,{
			onDrop : this.droppable_onDrop.bind( this ),
			onHover : this.droppable_onHover.bind( this )
		} );
	},
	removeTarget : function(tid) {
		var target = this.targets.find( function( t ) { return t.id == tid; } );
		if( !target )
			return;
		
		Droppables.remove( target.element );
		delete this.targets[target.id];
	},
	removeAllTargets: function() {
		var this_ = this;
		this.targets.each( function( target ) {
			this_.removeTarget( target.id );
		});
	},
	getTargetByElement : function( element ) {
		while( element ) {
			var target = this.targets.find( function( t ) {
				return t.element == element;
			} );
			
			if( target )
				return target;
			
			element = element.parentNode;
		}
	},
	dragObserver_onStart : function( eventName, draggable, event ) {
		var source = this.getSourceByElement( draggable.handle );
		if( !source )
			return;
		
		source.ghost.innerHTML = ( source.surrogate? source.surrogate : source.element.innerHTML );
		source.ghost.style.visibility = "visible";
		
		this.isDragging = true;
		this.surrogate = source.ghost;
		this.curSource = source;
		this.surrogateInitialX = Event.pointerX( event );
		this.surrogateInitialY = Event.pointerY( event );
		
		this._onDragStart( source.element );
	},
	dragObserver_onEnd : function( eventName, draggable, event ) {
		var source = this.getSourceByElement( draggable.handle )
		if( !source )
			return;
		
		source.reposition( source );
		source.ghost.style.visibility = "hidden";
		
		this.isDragged = false;
		this.hasDragged = false;
		this.curSource = undefined;
		this.curTagetId = undefined;
		this.surrogateInitialX = undefined;
		this.surrogateInitialY = undefined;
		//this._onDragEnd( source.element,this.lastTarget );
	},
	dragObserver_onDrag : function( eventName, draggable, event ) {
		if( !this.getSourceByElement( draggable.handle ) )
			return;
		
		this.hasDragged = true;
		
		var hit = Droppables.drops.find( function( drop ){
			return Position.within( drop.element,Event.pointerX( event ),Event.pointerY( event ) )
		})
		this.targetChange( hit? hit.element : null );
	},
	droppable_onDrop : function( sourceElement,targetElement,event ) {
		var target = this.getTargetByElement( targetElement );
		var source = this.getSourceByElement( sourceElement );
		
		if( !target || !source )
			return;
		
		this._onDragEnd( source.element,target.element );
	},
	droppable_onHover : function( sourceElement,targetElement,overlap ) {
		if( !this.getSourceByElement( sourceElement ))
			return;
		
		this.targetChange( targetElement );
	},
	
	targetChange : function( targetElement ) {
		if( !this.lastTarget ) {
			this.lastTarget = targetElement;
		} else if( targetElement == this.lastTarget ) {
			return;
		}
		
		var target = this.getTargetByElement( targetElement )
		if( !target ) {
			if( this.lastTarget ) {
				this._onDragTargetLost( this.lastTarget );
				this.lastTarget = undefined;
				this.curTagetId = undefined;
			}
			
			return;
		} else {
			this.curTagetId = target.id;
		}
		
		this._onDragTargetHit( targetElement, this.lastTarget );
		if( targetElement ) this.lastTarget = targetElement;
	}
}