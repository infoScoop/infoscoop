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

var IS_Notification = IS_Class.create();
IS_Notification.prototype.classDef = function(){

    this.firstAccess = true;
	this.notificationCenter = $jq('#notification-center').NotificationCenter();

    this.initialize = function(){
		this.notificationCenter.NotificationCenter('disable');
		this.lastNoticeDate = IS_Portal.globalProperties.noticeConfirmDateTime;
		this.pollingRate = IS_Portal.notificationPollingRate;
		this.checkNotifications();
		setInterval(this.checkNotifications.bind(this), this.pollingRate);
    }

	this.clickNotificationIcon = function() {
		var notificationCenter = this.notificationCenter;
		if(!notificationCenter.NotificationCenter('option', 'disabled')) {
			//hide
			$jq('body').addClass('notification_center_slideout').one('webkitAnimationEnd mozAnimationEnd oAnimationEnd oanimationend animationend',function(){
				var notifCenter = $jq("#notification-center");
				// target: webkit
				if(notificationCenter.NotificationCenter('option', 'disabled')) {
					$jq('body').removeClass('notification_center_slidein').removeClass('notification_center_slideout');
					$jq("#notification-center").hide();
					$jq('#notification-hide-box').remove();
					notificationCenter.NotificationCenter('clearList');
				}
			});
			notificationCenter.NotificationCenter('disable');
		} else {
			// show
			notificationCenter.NotificationCenter('enable');

			this.lastNoticeDate = new Date();
			notificationCenter.NotificationCenter('loadContents');

			$jq("#notification-center").show();
			$jq('body').addClass('notification_center_slidein');
			$jq('.notification_icon_badge').hide();
			$jq('.notification_icon').removeClass('notification_icon_arrival').attr({src: hostPrefix+'/skin/imgs/bell-white.png'});

			var commnad = new IS_Commands.UpdateNoticeConfirmDateCommand();
			IS_Request.CommandQueue.addCommand(commnad);
			IS_Request.CommandQueue.fireRequest();

			var hideBox = $jq('<div/>').attr({id: 'notification-hide-box'});
			hideBox.one('tap click', this.clickNotificationIcon.bind(this));
			$jq('body').append(hideBox);

			this._ajustSize();
			$jq(window).on('resize', this._ajustSize);
		}
	}

	this._ajustSize = function() {
		var h = Math.max.apply( null, [document.body.clientHeight , document.body.scrollHeight, document.documentElement.scrollHeight, document.documentElement.clientHeight] );
		var w = (Math.max.apply( null, [document.body.clientWidth , document.documentElement.clientWidth] )) - 320;
		$jq('#notification-center').css('height', h+'px');
		$jq('#notification-hide-box').css('height', h+'px');
		$jq('#notification-hide-box').css('width', w+'px');
	}

	this.checkNotifications = function() {
		// lastNoticeDate
		if(!this.lastNoticeDate) {
			this.lastNoticeDate = new Date();
			var commnad = new IS_Commands.UpdateNoticeConfirmDateCommand();
			IS_Request.CommandQueue.addCommand(commnad);
			IS_Request.CommandQueue.fireRequest();
		}

		// Request
		this.notificationCenter.NotificationCenter('callArrivalNotification', this.lastNoticeDate);
	}
}