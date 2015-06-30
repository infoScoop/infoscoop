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

IS_Square = Class.create();
IS_Square.prototype = {
	initialize: function() {
		if(!IS_Portal.useMultitenantMode)
			return;

		var anchor = document.createElement("a");
		anchor.id = "is-square";
		anchor.href = "square/square-modal.jsp";
		document.body.appendChild(anchor);

		this.squareModal = new Control.Modal(anchor.id, {
			overlayOpacity: 0.55,
			className: 'is-square',
			iframe: true,
			height: getWindowSize(false) - 180,
			width: getWindowSize(true) - 280,
			fade: true
		});

		IS_EventDispatcher.addListener('windowResized', null, this.resizeModal.bind(this));
	},

	start: function() {
		if(this.squareModal) {
			this.squareModal.open();
			this.resizeModal();
		}
	},

	finish: function() {
		if(this.squareModal)
			this.squareModal.close();
	},

	resizeModal: function(){
        if(!this.squareModal.isOpen)
            return;
        
        this.squareModal.container.style.height = getWindowSize(false) - 180 + "px";
        this.squareModal.container.style.width = getWindowSize(true) - 280 + "px";
        this.squareModal.position();
    }
}