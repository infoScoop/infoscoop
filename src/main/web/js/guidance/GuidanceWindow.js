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

IS_Guidance = {};
IS_Guidance.GuidanceWindow = {
    
    initialize: function(id) {
        this.id = id;
        this.GuidanceParams = {}
        this.GuidanceParams.menuItemList = parent.IS_Guidance.menuItemList;
        this.GuidanceParams.topMenuIdList = parent.IS_Guidance.topMenuIdList;
        this.GuidanceParams.serviceMenuMap = parent.IS_Guidance.serviceMenuMap;
        this.initialized = true;
    },
    
    render: function(renderTo){
        var displayLimit = 7;
        var tabCategory = $("#" + this.id + " .nav.nav-tabs");
        var tabContents = $("#" + this.id + " .tab-content");
        
        $.each(this.GuidanceParams.topMenuIdList, function(idx, id){
            var target = tabCategory;
            
            if(idx == displayLimit){
                // create drop down if idx > 5
                var anchor = $("<a>").attr({
                    "class": "dropdown-toggle",
                    "data-toggle": "dropdown",
                    "href": "#",
                    "aria-expanded": "false",
                    "role": "button"
                })
                .text(IS_R.lb_others)
                .append(
                    $("<span>").attr("class", "caret")
                )
                var dropdownLi = $("<li>").attr({
                    "role": "presentation",
                    "class": "dropdown"
                })
                .append(anchor)
                .appendTo(target);
                $("<ul>").attr({
                    "class": "dropdown-menu",
                    "role": "menu"
                }).appendTo(dropdownLi);
            }
            
            target = (idx < displayLimit)? target : $("#" + this.id + " .nav.nav-tabs .dropdown-menu");
            
            var li = $("<li>").attr({
                "class": (idx == 0)? "active" : "",
                "role": "presentation"
            }).append(
                $("<a>").attr("href", "#gdtab_" + id).text(this.GuidanceParams.menuItemList[id].title)
            );
            target.append(li);
            
            // add tab-contents
            this.createTabContent(id, tabContents, idx==0)
        }.bind(this));
        
        $("#" + this.id + " .tab-bar a").click(function (e) {
            e.preventDefault()
            $(this).tab('show')
        })
        
    },
    
    createTabContent: function(id, target, isActive){
        var tabContent = $("<div>").attr({
            "role": "tabpanel",
            "class": (isActive)? "tab-pane fade in active": "tab-pane fade",
            "id": "gdtab_" + id
        }).appendTo(target);
        var row = $("<div>").attr("class", "row").appendTo(tabContent);
        
        var topMenuItem = this.GuidanceParams.menuItemList[id];
        if(topMenuItem.children){
            $(topMenuItem.children).each(function(idx, menuItem){
                if(menuItem.type)
                    this.addThumbnailWidget(menuItem, row);
            }.bind(this));
        }
    },
    
    addThumbnailWidget: function(menuItem, target){
        var isAdded = parent.IS_Portal.isChecked(menuItem);
        
        var cell = $("<div>").attr("class", "col-xs-6 col-sm-4 col-md-4 col-lg-3");
        var thumbnail = $("<div>").attr("class", "thumbnail").appendTo(cell);
        var img = $("<img>").attr({
            "src": menuItem.thumbnail,
            "data-holder-rendered": "true",
            "class": "img-thumbnail"
        }).appendTo(thumbnail);
        var caption = $("<div>").attr("class", "caption").appendTo(thumbnail);;
        var title = $("<h3>").text(menuItem.title).appendTo(caption);
        var buttonPart = $("<p>").appendTo(caption);
        var button = $("<button>").attr({
            "type": "button",
            "class": isAdded? "btn btn-success" : "btn btn-primary",
            "aria-label": "Left Align"
        }).appendTo(buttonPart);
        
        this.changeButtonStyle(button, isAdded);
        
        button.data("menuItem", menuItem)
        button.bind("click", this.doDropItem.bind(this));
        
        target.append(cell);
    },
    
    doDropItem: function(e){
        if(!IS_Portal.canAddWidget() || $(e.target).hasClass("disable")) return;
        
        var button = $(e.target);
        var menuItem = button.data("menuItem");
        
        // シングルトンガジェットの場合は追加済みに
        if( !/true/i.test( menuItem.multi ) ){
            this.changeButtonStyle(button, true);
        }
        
        var targetColumnNum = 1;
        var parentItem = menuItem.parent;
        var p_id;
        var divParent;
        
        if(parentItem){
            p_id = IS_Portal.currentTabId+"_p_" + parentItem.id;
            divParent = $("#" + p_id);
        }
        
        var widgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(menuItem, targetColumnNum);
        addWidgetFunc( IS_Portal.currentTabId );
        
        function addWidgetFunc( tabId ) {
            widget = IS_WidgetsContainer.addWidget( tabId, widgetConf , false, false, null);

            //Send to Server
            IS_Widget.setWidgetLocationCommand(widget); //Add SiblingId
            IS_Portal.widgetDropped( widget );
        }
        
        e.preventDefault();
    },
    
    changeButtonStyle: function(button, isAdded){
        if(isAdded){
            button.removeClass("btn-primary").addClass("btn-success").addClass("disable");
            var icon = $("<span>").attr({
                "class": "glyphicon glyphicon-ok",
                "aria-hidden": "true"
            });
            button.text("Added");
            button.attr("disabled", true);
            button.prepend(icon);
        }else{
            button.removeClass("btn-success").removeClass("disable").addClass("btn-primary");
            button.removeAttr("disabled");
            button.text("Add it now");
        }
    }
}
