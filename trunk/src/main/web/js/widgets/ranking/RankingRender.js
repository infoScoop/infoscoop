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

IS_Widget.Ranking.RankingRender = IS_Class.create();
IS_Widget.Ranking.RankingRender.prototype.classDef = function() {
	var widget;
	var body;
	var self = this;
	var rss = {items:[]};
	var cacheId;
	var isError = false;

	function buildRssItems(response){
		rss = undefined;
		rss = IS_Widget.parseRss(response);
		
		if (!rss || !rss.items) {
			rss = {items:[]};
			rss.items = new Array();
		}
		self.displayContents();
		self.isSuccess = true;
	}
	
	function getTextFromXml(xml){
		var doc = is_createDocumentFromText(xml);
		var node = doc.firstChild;
		var text = node.firstChild.nodeValue;
		return text;
	}
	
	this.initialize = function(urlStr, title, bodyEl, widgetObj, idx){
		widget = widgetObj;
		this.url = urlStr;
		this.title = title;
		cacheId = widget.id + "_" + idx;
		body = bodyEl;
		body.innerHTML = "Loading...";
		//this.loadContents();
	}
	
	this.displayContents = function () {
		IS_Event.unloadCache(cacheId);
		
		var contentsTable = document.createElement("table");
		contentsTable.cellPadding = "1";
		contentsTable.cellSpacing = "0";
		contentsTable.setAttribute("width", "99%");
		contentsTable.style.tableLayout = "fixed";
		
		var tbodyEl = document.createElement("tbody");
		contentsTable.appendChild(tbodyEl);
		
		if (rss.items.length == 0) {
			var rsslink = document.createElement("div");
			rsslink.className = "rssItem";

			rsslink.innerHTML = IS_R.lb_noMessage;
			
			var itemTr = document.createElement("tr");
			tbodyEl.appendChild(itemTr);
			var itemTd = document.createElement("td");
			itemTr.appendChild(itemTd);
			itemTd.appendChild(rsslink);
			
		} else {
			var firstCount = -1;
			var preCount = -1;
			for ( var i=0; i<rss.items.length ; i++ ) {
				var rssItem = rss.items[i];
				
				var hasCount = !!(rssItem.report_count);
				var count = hasCount ? parseInt(getTextFromXml(rssItem.report_count)) : 0;
				
				var itemTr = document.createElement("tr");
				tbodyEl.appendChild(itemTr);
				
				//Prepare ranking
				var rankDiv = document.createElement("div");
				rankDiv.className = "rssItem";
				rankDiv.style.whiteSpace = "nowrap";
				if(hasCount && preCount != count) {

					rankDiv.innerHTML = IS_R.getResource(IS_R.lb_rankingUnit, [(i + 1)]);
					preCount = count;
				} else {
					rankDiv.innerHTML = "&nbsp;";
				}
				var rankTd = document.createElement("td");
				rankTd.style.verticalAlign = "top";
				rankTd.appendChild(rankDiv);
				rankTd.style.width = "2.5em"; // Up to three digits
//				rankTd.width = "1%";
				
				//Prepare title link
				var rsslink = document.createElement("div");
				rsslink.className = "rssItem";
				

				var rssTitle = ((rssItem.title.length == 0)? IS_R.lb_notitle : rssItem.title);
				
				if(rssItem.report_rssUrl) {
					var rssIcon = document.createElement('a');
					var rssUrl = getTextFromXml(rssItem.report_rssUrl);
					rssIcon.href = rssUrl;
					rssIcon.title = rssUrl;
					rssIcon.className = "rankingRssIcon";
					rssIcon.target = "_blank";
					rsslink.appendChild(rssIcon);
				}
				if(rssItem.link && rssItem.link.length > 0) {
					var aTag = document.createElement('a');
					aTag.href = rssItem.link;
					aTag.innerHTML = rssTitle;
					//aTag.target="ifrm";
					var aTagOnclick = function(aTag){
						return function(e){
							var startDateTime = (rssItem.rssDate)? rssItem.rssDate.getTime() : "";
							IS_Widget.contentClicked(rssItem.link,self.url,rssItem.title,startDateTime,aTag);
						}
					}(aTag);
					IS_Event.observe(aTag, "click", aTagOnclick, false, cacheId);
					rsslink.appendChild(aTag);
				} else {
					rsslink.appendChild(document.createTextNode(rssTitle));
				}
				
				//Count
				var countTd = document.createElement("td");
				countTd.align = "right";
				countTd.valing = "middle";
				countTd.width = "75px";
				if(hasCount && (i == 0 || firstCount > -1)) {
					if(!isNaN(count)) {
						if(firstCount == -1)
							firstCount = count;
						if(count == 0) {
							var countDiv = document.createElement("div");
							countDiv.className = "rankingCountWhite";
							countDiv.style.width = "72px";
							countDiv.style.borderWidth = "1px";
							countDiv.innerHTML = "&nbsp;";
							countTd.appendChild(countDiv);
						} else {
							var width = Math.round(count/firstCount * 72);
							var countDiv = document.createElement("div");
							countDiv.className = "rankingCount";
							countDiv.style.width = width + "px";
							countDiv.innerHTML = "&nbsp;";
							countTd.appendChild(countDiv);
							if(count != firstCount) {
								var countDivWhite = document.createElement("div");
								countDivWhite.className = "rankingCountWhite";
								countDivWhite.style.width = (72 - width) + "px";
								countDivWhite.innerHTML = "&nbsp;";
								countTd.appendChild(countDivWhite);
							} else {
								countDiv.style.borderWidth = "1px";
							}
						}
					}
				}
			
				var titleTd = document.createElement("td");
				
				rsslink.style.lineHeight = "1.2em";
				rsslink.style.height = "1.15em";
				rsslink.style.overflow = "hidden";
				rsslink.title = rssTitle;
				titleTd.appendChild(rsslink);
				itemTr.appendChild(rankTd);
				itemTr.appendChild(titleTd);
				itemTr.appendChild(countTd);
			}
		}
		
		//Show date period
		if(rss.report_reportConf && rss.report_createDate) {
			try {
				//var reportConf = dojo.dom.createDocumentFromText(rss.report_reportConf);
				var reportConf = is_createDocumentFromText(rss.report_reportConf);
				var createDate = parseW3CDTFDate(getTextFromXml(rss.report_createDate)).date;
				var spanNode = reportConf.getElementsByTagName("span")[0];
				var span = parseInt(spanNode.firstChild.nodeValue);
				var startDate = new Date();
				startDate.setTime(createDate.getTime() - ((span - 1) * 24 * 3600 * 1000));
				createDate = createDate.getFullYear() + "/" + (createDate.getMonth() + 1) + "/" + createDate.getDate();
				startDate = startDate.getFullYear() + "/" + (startDate.getMonth() + 1) + "/" + startDate.getDate();
				var spanTr = document.createElement("tr");
				var spanTd = document.createElement("td");
				spanTd.colSpan = "3";
				spanTd.align = "right";
				spanTd.style.fontSize = "70%";
				spanTd.innerHTML = startDate + " - " + createDate;
				spanTr.appendChild(spanTd);
				tbodyEl.appendChild(spanTr);
			} catch(e) {

				msg.warn( IS_R.getResource( IS_R.ms_rankingDurationFailed,[self.title,e]));
			}
		}
		
		if(body.firstChild) {
			body.replaceChild(contentsTable, body.firstChild);
		} else {
			body.appendChild(contentsTable);
		}
		
		isError = false;
	};

	this.loadContents = function () {
		var opt = {
		    method: 'get' ,
		    asynchronous:true,
		    ifModified : true,
		    timeout : -1,
		    onSuccess: function(t){
				if(t.getResponseHeader("MSDPortal-AuthType"))
					this.on403(t);
				else
					buildRssItems(t);
			},
			on1223: buildRssItems,
			on304: function(t) {},
		    on404: function(t) {
				if(!self.isSuccess){

					body.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+IS_R.lb_notfound+"</span>";
				}

				msg.error( IS_R.getResource( IS_R.ms_rankingNoInfo,[self.url]));
				isError = true;
		    },
			on403: function(t){
				if(!self.isSuccess){

					body.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+IS_R.ms_noPermission+"</span>";
				}

				msg.error(IS_R.getResource(IS_R.ms_RankingNoPermission, [self.title]));
				isError = true;
			},		
			on10408: function(r, t){
				if(!self.isSuccess){

					body.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+IS_R.lb_readingtimeout+"</span>";
				}

				msg.error( IS_R.ms_rankingTimeout);
				isError = true;
			},    
		    onFailure: function(t) {
				if(!self.isSuccess){

					body.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+IS_R.ms_getdatafailed +"</span>";
				}

				msg.error( IS_R.getResource( IS_R.ms_rankingonFailure,[self.url,t.status,t.statusText]));
				isError = true;
		    },
			onException: function(r, t){
				if(!self.isSuccess){

					body.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+IS_R.ms_getdatafailed+"</span>";
				}

				msg.error( IS_R.getResource( IS_R.ms_rankingonException,[self.title, getText(t)]));
				isError = true;
			},
			onComplete: function(r) {
				IS_EventDispatcher.newEvent('loadComplete', self.url, null);
			}
		};	
		
		AjaxRequest.invoke(is_getProxyUrl(self.url, "RssReader"), opt);
	};
};
