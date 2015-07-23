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

IS_Guidance = Class.create();
IS_Guidance.prototype = {
    initialize: function() {
        if(!IS_Portal.useMultitenantMode)
            return;
        
        IS_TreeMenu.types.guidance = new IS_TreeMenu("guidance");
        
        //Call return value of MakeMenuFilter
        IS_Guidance.setMenu = function(url, a,b,c){
            this.setMenu(url, a, b, c,true );
        }.bind(this);
        IS_Guidance.setServiceMenu = function(url, a, c, b){
            return this.setMenu(url, a, b, c, true );
        }.bind(this);
        
        IS_Guidance.menuItemList = this.menuItemList = [];
        IS_Guidance.topMenuIdList = this.topMenuIdList = [];
        IS_Guidance.menuItemTreeMap = this.menuItemTreeMap = [];
        IS_Guidance.serviceMenuMap = this.serviceMenuMap = [];
        
        var guidancePath = "/guidance/guidance.xml";
        var guidanceXmlUrl = (staticContentURL == ".") ? hostPrefix + guidancePath : staticContentURL + guidancePath;
        this.guidanceMenuItem = new IS_TreeMenu.MenuItem({
                id:"_is_guidance",
                serviceURL: guidanceXmlUrl
            });
        this.guidanceMenuItem.setOwner(IS_TreeMenu.types.guidance);
        
        // modal window
        var anchor = document.createElement("a");
        anchor.id = "is-guidance";
        anchor.href = "guidance/guidance-modal.jsp";
        document.body.appendChild(anchor);
        
        this.guidanceModal = new Control.Modal(anchor.id, { 
            overlayOpacity: 0.55,  
            className: 'is-guidance',
            iframe: true,
            height: getWindowSize(false) - 100,
            width: getWindowSize(true) - 100,
            fade: true  
        });
        
        this.loadGuidance();
        IS_EventDispatcher.addListener('windowResized', null, this.resizeModal.bind(this));
    },
    
    loadGuidance: function(){
        var opt = {
            method: 'get',
            requestHeaders:["siteTopId",this.guidanceMenuItem.id, "menuType","guidance"],
            onSuccess: function(response) {
                this.start();
            }.bind(this),
            on404: function(t) {
                msg.error(IS_R.lb_guidance_notfound + " - [" +  t.status + "]" + t.statusText);
            },
            onFailure: function(t) {
                msg.error(IS_R.lb_guidance_on_failure + " - [" +  t.status + "]" + t.statusText);
            },
            onException: function(r, t){
                msg.error(IS_R.lb_guidance_on_exception + " - " + getText(t));
            }
        };
        
        this.guidanceMenuItem.loadServiceMenu(opt);
    },
    
    start: function() {
        if(this.guidanceModal){
            this.guidanceModal.open();
            this.resizeModal()
        }
    },
    
    finish: function() {
        if(this.guidanceModal)
            this.guidanceModal.close();
    },
    
    setMenu: function( url,a,b,c,clear ) {
        var this_ = this;
        if( clear ) {
            $H( this.menuItemList ).keys().each( function( key ) {
                delete this_.menuItemList[ key ];
            });
            this.topMenuIdList.clear();
            $H( this.menuItemTreeMap ).keys().each( function( key ) {
                delete this_.menuItemTreeMap[ key ];
            });
        }

        $H( a ).values().collect( function( menuObj ) {
            this_.menuItemList[ menuObj.id ] = new IS_TreeMenu.MenuItem( menuObj );
        });
        if( clear ) {
            b.each( function( topMenuId ) {
                this_.topMenuIdList.push( topMenuId );
            });
        }
        $H( c ).each( function( entry ) {
            this_.menuItemTreeMap[ entry.key ] = entry.value;
        });

        var menuItems = [];
        $H( this.menuItemList ).each( function( entry ) {
            menuItems.push( entry.value );
        });
        this.menuItems = menuItems;

        menuItems.each( function( menuItem ) {
            if( typeof menuItem == "function" )
                return true;
            menuItem.setOwner( this_ );
        });
        return b.collect( function( topMenuId ) {
            return this_.menuItemList[ topMenuId ];
        });
    },
    
    resizeModal: function(){
        if(!this.guidanceModal.isOpen)
            return;
        
        this.guidanceModal.container.style.height = getWindowSize(false) - 100 + "px";
        this.guidanceModal.container.style.width = getWindowSize(true) - 100 + "px";
        this.guidanceModal.position();
    }
}
