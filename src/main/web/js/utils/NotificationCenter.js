;(function($) {

$.widget("infoscoop.NotificationCenter", {
	_create: function() {},
	_newArrivalArray: [],
	_oldArrivalArray: [],

	_createContent: function(content) {
		var base = $jq('<div/>').addClass('notification_item');
		var titleBase = $jq('<div/>');
		var title = $jq('<span/>')
		var logo = $jq('<img/>');
		switch(content.type){
			case 'SERVICE':
				logo.attr({src: 'logosrv/getFavicon?' + content.squareId}).error(function(){
					$jq(this).attr({ src: 'favicon.ico'});
				});
				title.text(content.serviceSquareName);
				break;
			default:
				logo.attr({src: 'favicon.ico'});
				title.text(IS_R.lb_notification_system_notice);
		}
		titleBase.append(logo);
		titleBase.append(title);

		var date = new Date(content.lastmodified);
		var postDate = $jq('<span/>').addClass('notification_post_date').text(date.toLocaleString());
		var content = $jq('<div/>').addClass('notification_content').text(content.body);
		base.append(titleBase.append(postDate));
		base.append(content);

		return base;
	},

	callArrivalNotification: function(date) {
		var headers = [];
		headers.push( ["MSDPortal-Cache", "No-Cache"] );

		var opt = {
			method:'get',
			asynchronous:true,
			requestHeaders: headers.flatten(),
			onSuccess:function(response){
				this._newArrivalArray = $jq.parseJSON(response.responseText);

				if(this._newArrivalArray.length > 0) {
					$jq('.notification_icon_badge').text(this._newArrivalArray.length).show();
					$jq('.notification_icon').addClass('notification_icon_arrival').attr({src: hostPrefix+'/skin/imgs/bell.png'})
				}
			}.bind(this),
			onFailure: function(t) {
				msg.error(IS_R.getResource( IS_R.lb_checkNewArriedMessageFailure +'{0} -- {1}',[t.status, t.statusText]));
			},
			onException: function(r, t){
				msg.error(IS_R.getResource( IS_R.lb_checkNewArriedMessageFailure +'{0}',[getErrorMessage(t)]));
			}
		};

		var queryString = Date.parse(date);
		AjaxRequest.invoke('notification?referenceDate='+queryString, opt);
	},

	_callOldNotifications: function() {
		var defer = $jq.Deferred();

		var headers = [];
		headers.push( ["MSDPortal-Cache", "No-Cache"] );

		var opt = {
			method:'get',
			asynchronous:true,
			requestHeaders: headers.flatten(),
			onSuccess: defer.resolve.bind(this),
			onFailure: function(t) {
				msg.error(IS_R.getResource( IS_R.lb_checkNewArriedMessageFailure +'{0} -- {1}',[t.status, t.statusText]));
			},
			onException: function(r, t){
				msg.error(IS_R.getResource( IS_R.lb_checkNewArriedMessageFailure +'{0}',[getErrorMessage(t)]));
			}
		};

		AjaxRequest.invoke('notification?limit=5&offset=' + this._oldArrivalArray.length, opt);
		return defer.promise();
	},

	_createOldViewBtn: function() {
		var base = $jq('<div/>').attr({'id': 'notification_old_view_btn'}).addClass('notification_old_view_btn');
		var btn = $jq('<a/>').attr({'href': '#'}).text(IS_R.lb_notification_load_olditem);
		base.append(btn);
		base.on('tap click', this._loadOldContents.bind(this));

		return base;
	},

	_createNoItemLabel: function() {
		var base = $jq('<div/>').attr({'id': 'notification_noitem_label'}).addClass('notification_noitem_label');
		var label = $jq('<label/>').text(IS_R.lb_notification_no_item);
		base.append(label);
		return base;
	},

	_loadOldContents: function() {
		var self = this;
		var element = this.element;
		var promise = self._callOldNotifications();
		var btn = $jq('#notification_old_view_btn');
		var label = $jq('#notification_noitem_label');

		promise.done(function(response){
			var data = $jq.parseJSON(response.responseText);
			if((data.length > 0 || this._oldArrivalArray.length > 0)
				&& label.length > 0) {
				label.remove();
				$jq('#notification_noitem_label_hr').remove();
			}
			if(data.length == 0) btn.remove();

			for(var i = 0; i < data.length; i++) {
				btn.before(self._createContent(data[i]));
				btn.before($jq('<hr/>'));
				self._oldArrivalArray.push(data[i]);
			}
		});

	},

	loadContents: function() {
		var self = this;
		var element = this.element;
		var data = self._newArrivalArray;
		var btn = $jq('#notification_old_view_btn');

		element.empty();
		for(var i = 0; i < data.length; i++) {
			element.append(self._createContent(data[i]));
			element.append($jq('<hr/>'));
			self._oldArrivalArray.push(data[i]);
		}

		if(data.length == 0) {
			element.append(self._createNoItemLabel());
			element.append($jq('<hr/>').attr({id: 'notification_noitem_label_hr'}));
		}

		// old notif view
		element.append(self._createOldViewBtn());

		self._newArrivalArray.clear();
	},

	clearList: function() {
		var self = this;
		self._newArrivalArray.clear();
		self._oldArrivalArray.clear();
	}
});

})(jQuery);