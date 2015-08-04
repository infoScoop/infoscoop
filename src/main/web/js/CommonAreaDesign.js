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

IS_CommonAreaDesign = Class.create();
IS_CommonAreaDesign.prototype = {
    initialize: function(renderTo) {
        this.content = $jq("#" + renderTo);
        this.colorPicker = $jq(".color-picker", this.content).colorpicker({showOn:'button'});
        this.colorPicker.on("change.color", function(){
            $jq(".static-degign-area", this.content).css("background-color", this.colorPicker.val());
        }.bind(this));
        
        // adjust static layouts
        $jq(".staticLayout>[class!=template] .static_column, .staticLayout>[class!=template] div", this.content).each(function(){
            var heightStr = $jq(this).css("height");
            var height = parseInt(heightStr);
            if(!isNaN(height)){
                $jq(this).css("height", (height*0.3) + "px");
            }
        });
        
        $jq(".design-control .save", this.content).click(this.save.bind(this));
        $jq(".design-control .cancel", this.content).click(this.cancel.bind(this));
    },
    initLayoutSelect: function(){
        /** init layoutSelect **/
        
        var tabId = this.currentTabObj.id.substr(3);
       
        // static design area
        var staticDesignArea = $jq(".static-degign-area", this.content);
        staticDesignArea.html(this.jsonRole.layout);
       
        var adjustToWindowHeight = IS_Customization["staticPanel"+tabId].adjustToWindowHeight;
        var currentStaticColCount = $jq(".static-degign-area .static_column", this.content).length;
        
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
        
        $jq(".gadgetsnum_buttonset", this.content).empty();
        
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
        });
    },
    changeDesignMode: function(tabObj){
        // get current json role.
        var tabId = tabObj.id.substring(3);
        this.currentTabObj = tabObj;
        
        var opt = {
            method: "get",
            asynchronous: true,
            parameters: "tabId=" + tabId,
            onSuccess: function( response ) {
                this.jsonRole = eval("("+response.responseText+")");
                this.initLayoutSelect();
                this.content.show();
                this.displayDesignPanel();
            }.bind(this),
            onFailure: function( resp,obj ) {
                msg.error(IS_R.getResource(IS_R.ms_widgetonFailureAt, [self.widgetType, self.title,req.status,req.statusText]));
            },
            onException: function( resp,obj ) {
                msg.error(IS_R.getResource(IS_R.ms_widgetonExceptionAt, [self.widgetType, self.title,getText(obj)]));
            }
        }
        AjaxRequest.invoke("designsrv", opt );
    },
    changeNormalMode: function(){
        this.hideDesignPanel();
    },
    showStaticDesignArea: function(){
        var panelNumber = this.currentTabObj.id.substring(3);
        var staticPanel = $jq("#static-panel" + panelNumber);
        staticPanel.hide();
        
        var staticDesignArea = $jq(".static-degign-area", this.content);
        
        this.prepareStaticArea();
        
        for(var i in this.jsonRole.staticPanel){
            var widgetOpt = this.jsonRole.staticPanel[i];
            // mofidy widget id for temporary design.
            widgetOpt.id = "temp_" + widgetOpt.id;
            this.displayStaticGadget(widgetOpt);
        }
        
        staticDesignArea.show();
    },
    hideStaticDesignArea: function(){
        var panelNumber = this.currentTabObj.id.substring(3);
        var staticPanel = $jq("#static-panel" + panelNumber);
        staticPanel.show();
        
        var staticDesignArea = $jq(".static-degign-area", this.content);
        staticDesignArea.empty();
        staticDesignArea.hide();
    },
    displayDesignPanel: function(){
        $jq(".design-option", this.content).animate(
            {height: "show", opacity: "toggle"},
            {
                duration: "slow",
                complete: this.showStaticDesignArea.bind(this)
            }
        );
    },
    hideDesignPanel: function(){
        $jq(".design-option", this.content).animate(
            {height: "hide", opacity: "toggle"},
            {
                duration: "slow",
                complete: function(){
                    this.hideStaticDesignArea();
                    this.content.hide();
                }.bind(this)
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
        widget.tabId = this.currentTabObj.id;
        
        widget.build();
        container.append(widget.elm_widget);
        
        widget.loadContents();
    },
    prepareStaticArea: function(){
        var tabId = this.currentTabObj.id.replace("tab","");
        
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
                         if(!window["IS_CommonAreaWidgetModalInstance"]){
                            IS_CommonAreaWidgetModalInstance = new IS_CommonAreaWidgetModal();
                        }else{
                            IS_CommonAreaWidgetModalInstance.start();
                        }
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
    save: function(){
        // TODO: save common area design.
        var tabId = this.currentTabObj.id.substring(3);
        
        var opt = {
            method: "post",
            asynchronous: true,
            parameters: "tabId=" + tabId + "&" + Object.toJSON(this.jsonRole),
            onSuccess: function( response ) {
                this.jsonRole = eval("("+response.responseText+")");
                this.content.show();
                this.displayDesignPanel();
            }.bind(this),
            onFailure: function( resp,obj ) {
                msg.error(IS_R.getResource(IS_R.ms_widgetonFailureAt, [self.widgetType, self.title,req.status,req.statusText]));
            },
            onException: function( resp,obj ) {
                msg.error(IS_R.getResource(IS_R.ms_widgetonExceptionAt, [self.widgetType, self.title,getText(obj)]));
            }
        }
        AjaxRequest.invoke("designsrv", opt );
        
        this.changeNormalMode();
    },
    cancel: function(){
        this.changeNormalMode();
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
    }
}
