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

IS_CommonAreaWidgetModal = Class.create();
IS_CommonAreaWidgetModal.prototype = {
	initialize: function(){
		this.caWidgetModal = new Control.Modal($('is-commonarea-widgetmodal'), {
			overlayOpacity: 0.55,
			className: 'is-commonarea-widgetmodal',
			width: getWindowSize(true) - 200,
			fade: true
		});

		this.loadWidgetModal();
		 IS_EventDispatcher.addListener('windowResized', null, this.resizeModal.bind(this));
	},

	start: function() {
		if(this.caWidgetModal) {
			this.caWidgetModal.open();
		}
	},

	finish: function() {
		if(this.caWidgetModal)
			this.caWidgetModal.close();
	},

    resizeModal: function(){
        if(!this.caWidgetModal.isOpen)
            return;

        this.caWidgetModal.container.style.width = getWindowSize(true) - 200 + "px";
        this.caWidgetModal.position();
    },

	loadWidgetModal: function(){
		// header
		$jq("#commonarea-widgetmodal-cancel-image").on('click', this.finish.bind(this));

		$jq("#commonarea-widgetmodal-contents").tabs().addClass( "ui-tabs-vertical ui-helper-clearfix" );
		$jq("#commonarea-widgetmodal-contents li").removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );

		this.start();
	}
}