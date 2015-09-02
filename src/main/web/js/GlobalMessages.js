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

/**
 * Class execute show global messages.
 */
var IS_GlobalMessages = IS_Class.create();
IS_GlobalMessages.prototype.classDef = function(){
    
    this.firstAccess = true;
    
    this.initialize = function(){
        this.requestURL = IS_Portal.globalMessages.rssUrl;
        this.rssMaxCount = IS_Portal.globalMessages.rssMaxCount;
        this.pollingRate = IS_Portal.globalMessages.pollingRate;
        
        if(this.requestURL){
            this.proxyUrl = is_getProxyUrl(this.requestURL, "RssReader");
            this.checkMessages()
            setInterval(this.checkMessages.bind(this), this.pollingRate);
        }
    }
    
    this.checkMessages = function(){
        var nowDate = new Date();
        var logoffDatetime = parseInt( IS_Portal.logoffDateTime );
        var freshTime = (logoffDatetime <= 0 || isNaN( logoffDatetime ))? 0 : nowDate.getTime() - logoffDatetime;
        
        var freshTime = nowDate.getTime();
        if(this.latestDateTime){
            freshTime = this.latestDateTime;
        } else {
            // 1st request
            var logoffDatetime = parseInt( IS_Portal.logoffDateTime );
            freshTime = (logoffDatetime <= 0 || isNaN( logoffDatetime ))? 0 : nowDate.getTime() - logoffDatetime;
            freshTime = nowDate.getTime() - freshTime;
        }
        
        var headers = [];
        headers.push( ["X-IS-FRESHTIME", freshTime] );
        
        if( rssMaxCount !== undefined )
            headers.push( ["X-IS-RSSMAXCOUNT", encodeURIComponent(this.rssMaxCount)] );
        
        headers.push( ["MSDPortal-Cache", "No-Cache"] );
        
        var opt = {
            method:'get',
            asynchronous:true,
            requestHeaders: headers.flatten(),
            onSuccess:function(response){
                var rss = IS_Widget.parseRss( response );
                var latestItemCount = (rss)? rss.latestItemCount : 0;
                if (latestItemCount == 0)
                    return;
                
                if( rss.items.length > 0 && rss.items[0].rssDate )
                    this.latestDateTime = rss.items[0].rssDate.getTime();
                
                var msgListDiv = $jq("#message-list");
                var lastChild;
                for( var i=0;i<latestItemCount;i++ ) {
                    var rssItem = rss.items[i];
                    
                    var msg = $jq("<div>").addClass("msg-item information");
                    if(!lastChild){
                        lastChild = msg.prependTo(msgListDiv)
                    }else{
                        lastChild = msg.insertAfter(lastChild)
                    }
                    
                    $jq("<span>").text(rssItem.date).appendTo(msg);
                    $jq("<a>").attr({
                            href : rssItem.link,
                            target : "_blank"
                        })
                        .text(rssItem.title)
                        .appendTo(msg);
                }
                
                $jq("#message-bar").show();
                IS_EventDispatcher.newEvent("adjustedMessageBar");
            }.bind(this),
            onFailure: function(t) {
                msg.error(IS_R.getResource( IS_R.lb_checkNewArriedMessageFailure +'{0} -- {1}',[t.status, t.statusText]));
            },
            onException: function(r, t){
                msg.error(IS_R.getResource( IS_R.lb_checkNewArriedMessageFailure +'{0}',[getErrorMessage(t)]));
            }
        };
        
        AjaxRequest.invoke(this.proxyUrl, opt);
    }
}
