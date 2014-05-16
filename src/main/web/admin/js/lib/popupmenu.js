/*
  popupmenu.js - simple JavaScript popup menu library.

  Copyright (C) 2007 Jiro Nishiguchi <jiro@cpan.org> All rights reserved.
  This is free software with ABSOLUTELY NO WARRANTY.

  You can redistribute it and/or modify it under the modified BSD license.

  Usage:
    var popup = new PopupMenu();
    popup.add(menuText, function(target){ ... });
    popup.addSeparator();
    popup.bind('targetElement');
    popup.bind(); // target is document;
*/
var PopupMenu = function() {
    this.init();
}
PopupMenu.SEPARATOR = 'PopupMenu.SEPARATOR';
PopupMenu.current = null;
PopupMenu.addEventListener = function(element, name, observer, capture) {
    if (typeof element == 'string') {
        element = document.getElementById(element);
    }
    if (element.addEventListener) {
        element.addEventListener(name, observer, capture);
    } else if (element.attachEvent) {
        element.attachEvent('on' + name, observer);
    }
};
PopupMenu.prototype = {
    init: function() {
        this.items  = [];
        this.width  = 0;
        this.height = 0;
		this.doc = document;
    },
    setSize: function(width, height) {
        this.width  = width;
        this.height = height;
        if (this.element) {
            var self = this;
            with (this.element.style) {
                if (self.width)  width  = self.width  + 'px';
                if (self.height) height = self.height + 'px';
            }
        }
    },
	setDoc: function(doc){
		this.doc = doc;
	},
    bind: function(element) {
        var self = this;
        if (!element) {
            element = this.doc;
        } else if (typeof element == 'string') {
            element = this.doc.getElementById(element);
        }
        this.target = element;
		/*
        this.target.oncontextmenu = function(e) {
            self.show.call(self, e);
            return false;
        };
        */
		var func = function(e){
			e.preventDefault();
			e.stopPropagation();
			
            self.show.call(self, e);
		}
		$jq(this.target).contextmenu(func);
        
        var listener = function() { self.hide.call(self) };
        PopupMenu.addEventListener(this.doc, 'click', listener, true);
    },
    add: function(text, callback) {
        this.items.push({ text: text, callback: callback });
    },
    addSeparator: function() {
        this.items.push(PopupMenu.SEPARATOR);
    },
    setPos: function(e) {
        if (!this.element) return;
        if (!e) e = window.event;
        var x, y;
        if (window.opera) {
            x = e.clientX;
            y = e.clientY;
        }else if (this.doc.all) {
			tempLeft = this.doc.documentElement.scrollLeft || this.doc.body.scrollLeft;
			tempTop = this.doc.documentElement.scrollTop || this.doc.body.scrollTop;
			x = tempLeft + e.clientX;
			y = tempTop + e.clientY;
        } else if (this.doc.layers || this.doc.getElementById) {
            x = e.pageX;
            y = e.pageY;
        }
        
        this.element.style.top  = y + 'px';
        this.element.style.left = x + 'px';
    },
    show: function(e) {
        if (PopupMenu.current && PopupMenu.current != this) return;
        PopupMenu.current = this;
        if (this.element) {
            this.setPos(e);
            this.element.style.display = '';
        } else {
            this.element = this.createMenu(this.items);
            this.setPos(e);
            this.doc.body.appendChild(this.element);
        }
    },
    hide: function() {
        PopupMenu.current = null;
        if (this.element) this.element.style.display = 'none';
    },
    createMenu: function(items) {
        var self = this;
        var menu = this.doc.createElement('div');
        with (menu.style) {
            if (self.width)  width  = self.width  + 'px';
            if (self.height) height = self.height + 'px';
            border     = "1px solid gray";
            background = '#FFFFFF';
            color      = '#000000';
            position   = 'absolute';
            display    = 'block';
            padding    = '2px';
            cursor     = 'default';
			zIndex	   = 10000;
        }
        for (var i = 0; i < items.length; i++) {
            var item;
            if (items[i] == PopupMenu.SEPARATOR) {
                item = this.createSeparator();
            } else {
                item = this.createItem(items[i]);
            }
            menu.appendChild(item);
        }
        return menu;
    },
    createItem: function(item) {
        var self = this;
        var elem = this.doc.createElement('div');
        elem.style.padding = '4px';
        var callback = item.callback;
        PopupMenu.addEventListener(elem, 'click', function(_callback) {
            return function() {
                self.hide();
                _callback(self.target);
            };
        }(callback), true);
        PopupMenu.addEventListener(elem, 'mouseover', function(e) {
            elem.style.background = '#B6BDD2';
			Event.stop(e);
        }, true);
        PopupMenu.addEventListener(elem, 'mouseout', function(e) {
            elem.style.background = '#FFFFFF';
        }, true);
        elem.appendChild(this.doc.createTextNode(item.text));
        return elem;
    },
    createSeparator: function() {
        var sep = this.doc.createElement('div');
        with (sep.style) {
            borderTop = '1px dotted #CCCCCC';
            fontSize  = '0px';
            height    = '0px';
        }
        return sep;
    }
};

