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

package org.infoscoop.widgetconf;

import org.infoscoop.util.Xml2JsonListener;

public class I18NListener implements Xml2JsonListener {
	private I18NConverter i18n;

	public I18NListener(I18NConverter i18n) {
		this.i18n = i18n;
	}

	public String text(String text) throws Exception {
		return i18n.replace(text);
	}
	

}
