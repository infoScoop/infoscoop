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

var is_jsonRole = {
    "id":"1a997cf0d575ac445da7a6b63e029d30b3cbd796e0c9d64ac54d3bc864d33d79",
    "tabId":"0",
    "tabName":"Home",
    "columnsWidth":"",
    "tabNumber":0,
    "role":"default",
    "principalType":"OrganizationPrincipal",
    "roleOrder":0,
    "roleName":"defaultRole",
    "defaultUid":"default",
    "widgetsLastmodified":"-",
    "staticPanel":{
        "p_1_w_1":{
            "id":"p_1_w_1",
            "menuId":"",
            "href":"",
            "title":"はてなブックマーク - 人気エントリー",
            "type":"RssReader",
            "column":"1",
            "ignoreHeader":false,
            "noBorder":false,
            "refreshInterval":null,
            "disabled":false,
            "properties":{
                "url":"http://b.hatena.ne.jp/hotentry.rss"
            }
        },
        "p_1_w_5":{
            "id":"p_1_w_5",
            "menuId":"",
            "href":"",
            "title":"Calendar",
            "type":"Calendar",
            "column":"3",
            "ignoreHeader":true,
            "noBorder":false,
            "refreshInterval":null,
            "disabled":false,
            "properties":{}
        }
    },
    "layout":"<DIV>\r\n\t<DIV style=\"FLOAT: left; WIDTH: 74.5%\">\r\n\t\t<DIV style=\"HEIGHT: 190px\">\r\n\t\t\t<DIV style=\"FLOAT: left; WIDTH: 100%; HEIGHT: 100%\">\r\n\t\t\t\t<DIV id=\"p_1_w_1\" class=\"static_column\" style=\"MARGIN-LEFT: 2px; HEIGHT: 190px\"><\/DIV>\r\n\t\t\t<\/DIV>\r\n\t\t<\/DIV>\r\n\t<\/DIV>\r\n\t<DIV style=\"FLOAT: right; WIDTH: 25%; HEIGHT: 190px\">\r\n\t\t<DIV id=\"p_1_w_5\" class=\"static_column\" style=\"MARGIN-LEFT: 2px; HEIGHT: 190px\"><\/DIV>\r\n\t<\/DIV>\r\n<\/DIV>\r\n<DIV style=\"CLEAR: both; display:none;\"/>\r\n",
    "dynamicPanel":{},
    "adjustToWindowHeight":false,
    "disabledDynamicPanel":false,
    "isDefault":"true"
}

IS_CommonAreaDesign = Class.create();
IS_CommonAreaDesign.prototype = {
    initialize: function(renderTo) {
        this.content = $jq("#" + renderTo);
        this.colorPicker = $jq(".color-picker", this.content).colorpicker({showOn:'button'});
        this.jsonRole = $jq.extend(true,{}, is_jsonRole);
        
        // adjust static layouts
        $jq(".staticLayout>[class!=template] .static_column, .staticLayout>[class!=template] div", this.content).each(function(){
            var heightStr = $jq(this).css("height");
            var height = parseInt(heightStr);
            if(!isNaN(height)){
                $jq(this).css("height", (height*0.3) + "px");
            }
        });
        
        /** init layoutSelect **/
        
        var tabNumber = IS_Portal.currentTabId.substr(3);
        var adjustToWindowHeight = IS_Customization["staticPanel"+tabNumber].adjustToWindowHeight;
        
//      var currentStaticColCount = $jq("#staticAreaContainer .static_column").length;
        // TODO: debug
        var currentStaticColCount = 3;
        
        $jq("#staticLayouts"+(!adjustToWindowHeight ? "AdjustHeight":"")).hide();
        var targetAreaId = "staticLayouts"+(adjustToWindowHeight ? "AdjustHeight":"");
        $jq("#" + targetAreaId+ ">div").hide();
        
        // create gadgets-num button
        var columnsCountList = [];
        $jq("#" + targetAreaId+ ">div").each(function(idx, staticLayout){
            staticLayout = $jq(staticLayout);
            
            var columnLength = $jq(".static_column", $jq(">:not(.template)", staticLayout)).length;
            
            staticLayout.addClass("gadgets-" + columnLength);
            
            if($jq.inArray(columnLength, columnsCountList) == -1)
                columnsCountList.push(columnLength);
        });
        
        columnsCountList.sort(function(a,b){return a>b});
        
        $jq.each(columnsCountList, function(idx, gadgetsNum){
            var parent = $jq(".gadgetsnum_buttonset", this.content);
            var idPrefix = "gadgetsnum_buttonset_";
            ;
            var radioButton = $jq("<input>")
                .attr("id", idPrefix + gadgetsNum)
                .attr("type", "radio")
                .attr("name", idPrefix + "radio")
                .appendTo(parent);
            
            if(gadgetsNum == 0){
                radioButton.attr("checked", true);
            }
            else if(gadgetsNum == currentStaticColCount){
                $jq("input", parent).attr("checked", false);
                radioButton.attr("checked", true);
            }
            
            $jq("<label>").attr("for", idPrefix + gadgetsNum).text(gadgetsNum).appendTo(parent);
            
            radioButton.click(function(){
                var id = $jq(this).attr("id");
                var number = id.replace(/^gadgetsnum_buttonset_(.+)$/, "$1");
                
                $jq("#" + targetAreaId+ ">div").hide();
                $jq("#" + targetAreaId + " .gadgets-" + number).css("display", "inline-block");
            });
        });
        
        $jq(".gadgetsnum_buttonset", this.content).buttonset();
        
        // show default
        if($jq("#" + targetAreaId + " .gadgets-" + currentStaticColCount).length > 0){
            $jq("#" + targetAreaId + " .gadgets-" + currentStaticColCount).css("display", "inline-block");
        }else{
            $jq("#" + targetAreaId + " .gadgets-" + columnsCountList[0]).css("display", "inline-block");
        }
        
        $jq(".staticLayout"+(adjustToWindowHeight ? "AdjustHeight":""), this.content).on("click", {self:this}, function(e){
            if(!confirm("テンプレートを選択すると、今までの設定は破棄されます。"))
                return;
            
            var self = e.data.self;
            var selectedContent = $jq(this);
            var layoutTemplate = $jq($jq.parseHTML(selectedContent.html())).closest(".template");
            var newNode = (layoutTemplate.length > 0) ? layoutTemplate : selectedContent.clone(true);
            self.setIdentifier(newNode);
            self.jsonRole.layout = newNode.html();
            self.jsonRole.staticPanel = {};
            
            $jq(".static-degign-area", self.content).html(self.jsonRole.layout);
            
            self.prepareStaticArea();
            
//            reloadStaticGadgets();
//            adjustStaticWidgetHeight();
        });
    },
    setIdentifier: function(htmlEl){
        htmlEl = $jq(htmlEl);
        $jq(".static_column", htmlEl).each(function(idx, el){
            el = $jq(el);
            if(!el.attr("id")){
                var datetime = new Date().getTime();
                var idPrefix = "p_" + datetime + "_w_";
                
                el.attr("id", idPrefix + idx);
            }
        });
        return htmlEl;
    },
    changeDesignMode: function(){
        this.content.show();
        this.displayDesignOptionPanel();
    },
    showStaticDesignArea: function(){
        var panelNumber = IS_Portal.currentTabId.substring(3);
        var staticPanel = $jq("#static-panel" + panelNumber);
        staticPanel.hide();
        
        var staticDesignArea = $jq(".static-degign-area", this.content);
        var currentStaticPanel = $jq(this.jsonRole.staticPanel);
        
        staticDesignArea.html(this.jsonRole.layout);
        this.prepareStaticArea();
        
        for(var i in this.jsonRole.staticPanel){
            var widgetOpt = this.jsonRole.staticPanel[i];
            // mofidy widget id for temporary design.
            widgetOpt.id = "temp_" + widgetOpt.id;
            this.displayStaticGadget(widgetOpt);
        }
        
        staticDesignArea.animate({opacity: "toggle"});
    },
    displayDesignOptionPanel: function(){
        $jq(".design-option", this.content).animate(
            {height: "toggle", opacity: "toggle"},
            {
                duration: "slow",
                complete: this.showStaticDesignArea.bind(this)
            }
        );
    },
    displayStaticGadget: function(widgetOpt){
        var containerId = widgetOpt.id;
        var container = $jq("#" + containerId);
        if(container.size() == 0) {
            return;
        }
        var realContainer = $jq("#s_"+containerId);
        if(realContainer.size > 0) {
            container.parentNode.removeChild(container);
            container = realContainer;
        }else{
            container.attr("id", "s_" + containerId);
        }
        
        widgetOpt.property = $jq.extend(true,{}, widgetOpt.properties)
        
        var widget = new IS_Widget(false, widgetOpt);
        widget.panelType = "StaticPanel";
        widget.tabId = IS_Portal.currentTabId;
        
        widget.build();
        container.append(widget.elm_widget);
        
        widget.loadContents();
    },
    prepareStaticArea: function(){
        var tabId = IS_Portal.currentTabId.replace("tab","");
        
        if($jq('.static-degign-area .static_column', this.content).size() == 0){
            var modified = false;
            $jq('.static-degign-area .column[id]', this.content).each(function(j){
                modified = true;
                $jq(this).addClass("static_column");
            });
            
            if(modified)
                this.jsonRole.layout = $jq('.static-degign-area').html();
        }
        
        $jq('.static-degign-area .static_column', this.content).each(function(j, staticColumn){
            var containerId = "temp_" + $jq(staticColumn).attr("id");
            $jq(staticColumn).attr("id", containerId);
            var staticColumn = $jq(staticColumn).data("containerId", $jq(this).attr("id"));
            
            var widgetJSON = this.jsonRole.staticPanel[containerId];
            if(!widgetJSON)
                widgetJSON = {type:"notAvailable", id: containerId};
            
            var edit_cover = $jq("<div></div>")
                .attr("id", "edit_div_" + j)
                .addClass("edit-static-gadget")
                .hide()
                .click(function(e){
                    return function(){
                        // TODO: モーダルを開く
                        alert(e.value);
                    }
                }({value:widgetJSON})).appendTo(staticColumn);
            staticColumn.mouseover(function(){
                var $this = $jq(this);
                edit_cover.text(($this.attr("id") == $this.data("containerId")) ? "New" : "Edit").show();
            })
            .mouseout(function(){
                edit_cover.hide();
            });
        }.bind(this));
        $jq("#layout").val($jq(".static-degign-area", this.content).html());
    },
    apply: function(){
        // TODO: save common area design.
    },
    cancel: function(){
        // TODO: change normal mode.
    }
}
