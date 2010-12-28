worldclock1 = new worldclock();
worldclock1.dispclock();

function TimeZone(id_prefix, title, offset, imgFile, st_func){
	this.id_prefix = id_prefix;
	this.title = title;
	this.offset = offset;
	this.imgURL = baseURL + "/imgs/" + imgFile;

	this.getDstOffset = function getDstOffset(ct){
		if(st_func != null){
			return st_func(ct);
		} else {
			return 0;
		}
	}
}

function worldclock(){
	var container = _gel("worldclock");
	var prefs = new gadgets.Prefs();
	prefs.setDontEscape_();

	var contentLink = prefs.getString("contentLink");

	if(contentLink && contentLink.length > 0){
		container.onclick = function(){
			window.open(contentLink);
		}
		container.style.cursor = "pointer";
	}

	var tz_jp = new TimeZone("jp", "TYO", 9, "jp.png");
	var tz_cn = new TimeZone("cn", "SHA", 8, "cn.png");
	var tz_uk = new TimeZone("uk", "LDN", 0, "gb.png",
			function(t){
				var yy = t.getFullYear();
				var mar = new Date(yy, 2, 31);
				var lastsundaymar  = 31 - mar.getDay();
				var oct = new Date(yy,10,7);
				var lastsundayoct = 31 - oct.getDay();
				var lndstbegin = new Date(yy, 2, lastsundaymar, 1, 0, 0);
				//End of Summer time at London is wrriten as 1:00am, but 2:00 becomes 1:00 again
				var lndstend = new Date(yy, 9, lastsundayoct, 1, 0, 0);

				if (t.getTime() >= lndstbegin.getTime() && t.getTime() <= lndstend.getTime() ){
					return 3600000;
				} else {
					return 0;
				}
			});
	var tz_et = new TimeZone("us", "NYC", -5, "us.png",
			function(t){
				var yy = t.getFullYear();
				var nydstbegin;
				var nydstend;

				if(yy < 2007){
					var apr = new Date(yy, 3, 7);
					var firstsunday = 7 - apr.getDay();
					var oct = new Date(yy, 9, 31);
					var lastsunday = 31 - oct.getDay();
					nydstbegin = new Date(yy, 3, firstsunday, 2, 0, 0);
					nydstend = new Date(yy, 9, lastsunday, 1, 0, 0);
				} else {
					var mar = new Date(yy, 2, 7);
					var secondsunday = 14 - mar.getDay();
					var nov = new Date(yy,10,7);
					var firstsunday = 7 - nov.getDay();
					nydstbegin = new Date(yy, 2, secondsunday, 2, 0, 0);
					//End of Summer time at New York is written as 2:00am, but 2:00 becomes 1:00 again
					nydstend = new Date(yy, 10, firstsunday, 1, 0, 0);
				}

				if (t.getTime() >= nydstbegin.getTime() && t.getTime() <= nydstend.getTime() ){
					return 3600000;
				} else {
					return 0;
				}
			});

	var tzArray = new Array(tz_jp, tz_uk, tz_et, tz_cn);

	this.dispclock  = function dispclock(){

		for(cnt = 0; cnt < tzArray.length; cnt++){

			var isLast = false;
			if(cnt != tzArray.length -1){
				isLast = true;
			}

			disptime(tzArray[cnt], isLast);
		}

		setTimeout('worldclock1.dispclock()', 1000);

		gadgets.window.adjustHeight();
	}

	function disptime(tz, isLast){
		var t = calctime(tz);

		if(document.getElementById(tz.id_prefix + '_date') == null){
			var parentDiv = document.getElementById("worldclock");
			var countryDiv = document.createElement("div");
			countryDiv.className = "country";

			var img = document.createElement("img");
			img.src = tz.imgURL;

			countryDiv.appendChild(img);

			var titleSpan = document.createElement("span");
			titleSpan.innerHTML = "&nbsp;&nbsp;" + tz.title + ":";
			countryDiv.appendChild(titleSpan);

			parentDiv.appendChild(countryDiv);

			var dateDiv = document.createElement("div");
			dateDiv.id = tz.id_prefix + "_date";
			dateDiv.className = "date";
			dateDiv.innerHTML = t.date;

			parentDiv.appendChild(dateDiv);

			var div = document.createElement("div");
			var timeSpan = document.createElement("span");
			timeSpan.id = tz.id_prefix + "_time";
			timeSpan.className = "time";
			timeSpan.innerHTML = t.time;

			div.appendChild(timeSpan);

			var dstSpan = document.createElement("span");
			dstSpan.id = tz.id_prefix + "_dst";
			dstSpan.className = t.dst;

			var dstImg = document.createElement("img");
			dstImg.src = baseURL + "/imgs/dst.gif";
			dstSpan.appendChild(dstImg);

			div.appendChild(dstSpan);
			parentDiv.appendChild(div);

			if(isLast){
				var hr = document.createElement("hr");
				parentDiv.appendChild(hr);
			}
		}else{
			document.getElementById(tz.id_prefix + '_date').innerHTML = t.date;
			document.getElementById(tz.id_prefix + '_time').innerHTML = t.time;
			document.getElementById(tz.id_prefix + '_dst').className = t.dst;
		}
	}

	function calctime(tz) {
		var localTime = new Date();
		var localOffset = localTime.getTimezoneOffset() * 60000;
		var t = new Date();
		t.setTime(localTime.getTime() + localOffset + (tz.offset * 3600000));

		var dstOffset = tz.getDstOffset(t);

		if(dstOffset > 0){
			t.setTime(t.getTime() + dstOffset);
		}

		return {
			date : t.getFullYear() + "/" + f(t.getMonth() + 1) + "/" + f(t.getDate()),
			time : f(t.getHours()) + ":" + f(t.getMinutes()) + ":" + f(t.getSeconds()),
			dst  : dstOffset > 0 ? "show" : "hide"
		};
	}

	function f(v){
		return v < 10 ? v = "0" + v : v;
	}
}
