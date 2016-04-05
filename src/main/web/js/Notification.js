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
		if(!this.notificationCenter.NotificationCenter('option', 'disabled')) {
			//hide
			$jq('body').addClass('notification_center_slideout').one('webkitAnimationEnd mozAnimationEnd oAnimationEnd oanimationend animationend',function(){
				$jq('body').removeClass('notification_center_slidein').removeClass('notification_center_slideout');
				$jq("#notification-center").hide();
			});
			this.notificationCenter.NotificationCenter('clearList');
			this.notificationCenter.NotificationCenter('disable');
		} else {
			// show
			this.lastNoticeDate = new Date();
			this.notificationCenter.NotificationCenter('loadContents');

			$jq("#notification-center").css({
				display: "block",
				height: $jq(window).height()+'px'
			});
			$jq('body').addClass('notification_center_slidein');
			$jq('.notification_icon_badge').hide();
			$jq('.notification_icon').removeClass('notification_icon_arrival').attr({src: hostPrefix+'/skin/imgs/bell-white.png'});

			var commnad = new IS_Commands.UpdateNoticeConfirmDateCommand();
			IS_Request.CommandQueue.addCommand(commnad);

//			$jq(window).on('resize', this._resizeHeight)
			this.notificationCenter.NotificationCenter('enable');
		}
	}

	this._resizeHeight = function() {
		var h = Math.max.apply( null, [document.body.clientHeight , document.body.scrollHeight, document.documentElement.scrollHeight, document.documentElement.clientHeight] );
		$jq('#notification-center').css('height', h+'px');
	}

	this.checkNotifications = function() {
		// lastNoticeDate
		if(!this.lastNoticeDate) {
			this.lastNoticeDate = new Date();
			var commnad = new IS_Commands.UpdateNoticeConfirmDateCommand();
			IS_Request.CommandQueue.addCommand(commnad);
		}

		// Request
		this.notificationCenter.NotificationCenter('callArrivalNotification', this.lastNoticeDate);
	}
}