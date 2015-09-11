<%--
# infoScoop OpenSource
# Copyright (C) 2010 Beacon IT Inc.
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License version 3
# as published by the Free Software Foundation.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public
# License along with this program. If not, see
# <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
--%>

<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" errorPage="/jspError.jsp" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <jsp:include page="/WEB-INF/jsp/admin/tiles/page_head.jsp" flush="false"/>
    <style>
        body {
            background: transparent !important;
        }
        #properties {
            background-color: transparent;
            padding: 0px;
        }
        td {
            min-width: 200px;
        }
        input, textarea {
            max-width: 280px;
        }
        :root *> .modalConfigSetContent, .modalConfigSet {
            width: auto;
        }
        #formCancel {
            display: none;
        }
    </style>
	<script>
	    var widgetJSON = {"id":"w_1438933992534","menuId":"","href":"http://portal.nifty.com/","title":"デイリーポータルＺ：ヘッドライン","type":"RssReader","column":"","ignoreHeader":false,"noBorder":false,"refreshInterval":null,"disabled":false,"properties":{"url":"http://portal.nifty.com/rss/headline.rdf","doLineFeed":"false","showDatetime":"true","itemsnum":"3","showLatestNews":"false","openWidget":"true","scrollMode":"scroll","detailDisplayMode":"inline","itemDisplay":"auto","iframeview":"false","authType":"","titleFilter":"","creatorFilter":"","categoryFilter":""}};
	    $jq(function(){
	        var gadgetSettings = new ISA_GadgetSettings();
	        IS_SiteAggregationMenu.init();
	        ISA_loadProperties(gadgetSettings.build.bind(gadgetSettings));
	        ISA_GadgetSettings.gadgetSettings = gadgetSettings;
	    });
	    
	    var ISA_GadgetSettings = IS_Class.create();
	    ISA_GadgetSettings.prototype.classDef = function() {
	        this.initialize = function() {
	            this.container = $jq("#container");
	        };
	        
	        this.build = function() {

                var url = adminHostPrefix + "/services/widgetConf/getWidgetConfJson";
                var opt = {
                    method: 'get' ,
                    asynchronous: true,
                    onSuccess: function(response){
                        ISA_SiteAggregationMenu.setWidgetConf(JSON.parse(response.responseText));
                    }.bind(this),
                    on404: function(t) {
                        this.container.html("<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_widgetNotFound+"</span>");
                    }.bind(this),
                    onFailure: function(t) {
                        this.container.html("<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_FailedLoadingWidget+"</span>");
                    }.bind(this),
                    onException: function(r, t){
                        this.container.html("<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_FailedLoadingWidget+"</span>");
                        throw t;
                    }.bind(this)
                };
                AjaxRequest.invoke(url, opt);
	        };
	        
	        this.displayContents = function(widgetJSON, callback){
	            $jq("#loading-message").hide();
	            this.reset();
	            
	            var editorFormObj = new ISA_CommonModals.EditorForm(this.container.get(0), function(widgetJSON){
	                widgetJSON.properties = {};
	                widgetJSON.type = ISA_CommonModals.EditorForm.getSelectType();
	                widgetJSON.properties = ISA_CommonModals.EditorForm.getProperty(widgetJSON);
	                widgetJSON.ignoreHeader = ISA_CommonModals.EditorForm.isIgnoreHeader();
	                if(!widgetJSON.ignoreHeader) is_deleteProperty(widgetJSON, "ignoreHeader"); //delete widgetJSON.ignoreHeader;
	                widgetJSON.noBorder = ISA_CommonModals.EditorForm.isNoBorder();
	                if(!widgetJSON.noBorder) is_deleteProperty(widgetJSON, "noBorder"); //delete widgetJSON.noBorder;
	                
	                widgetJSON.title = ISA_Admin.trim($("formTitle").value);
	                widgetJSON.href =  $("formHref").value;
	                var formUseRefreshInterval = $jq("#formUseRefreshInterval").prop("checked");
	                widgetJSON.refreshInterval = (formUseRefreshInterval)? parseInt($jq("#formRefreshInterval").val()) : null;
	                callback(widgetJSON);
	            },{
	                menuFieldSetLegend:ISA_R.alb_widgetHeaderSettings,
	                setDefaultValue: false,
	                disableMiniBrowserHeight: true,
	                showIgnoreHeaderForm:true,
	                showNoBorderForm:true,
	                displayACLFieldSet:false,
	                disableDisplayRadio:true,
	                omitTypeList:['Ranking','Ticker','MultiRssReader','FragmentMiniBrowser','Calendar','Information','Information2','Message'],
	                renderTo: this.container.get(0)
	            });
	            
	            editorFormObj.showEditorForm(widgetJSON);
	        }
	        
	        this.reset = function(){
	            this.container.empty();
	        }
	    }
	</script>
	<body class="infoScoop">
        <div id="properties"></div>
	    <div id="loading-message">Loading...</div>
		<div id="container"></div>
	</body>
</html>