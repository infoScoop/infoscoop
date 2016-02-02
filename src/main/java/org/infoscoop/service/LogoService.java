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

package org.infoscoop.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.LogoDAO;
import org.infoscoop.dao.model.Logo;
import org.infoscoop.util.SpringUtil;

public class LogoService {
	private static Log log = LogFactory.getLog(LogoService.class);

	private LogoDAO logoDAO;

	public static LogoService getHandle(){
		return (LogoService) SpringUtil.getBean("LogoService");
	}

	public LogoDAO getLogoDAO() {
		return logoDAO;
	}

	public void setLogoDAO(LogoDAO logoDAO) {
		this.logoDAO = logoDAO;
	}

	public void saveLogo(byte[] image, String type, String kind) {
		String squareId = UserContext.instance().getUserInfo().getCurrentSquareId();
		Logo logo = this.logoDAO.getBySquareIdAndKind(squareId, kind);

		String typeRef = type;
		if(typeRef.equals("image/x-icon"))
			typeRef = "image/vnd.microsoft.icon";

		if(logo != null) {
			// update
			logo.setType(typeRef);
			logo.setLogo(image);
			this.logoDAO.update(logo);
		} else {
			// insert
			this.logoDAO.insert(squareId, image, typeRef, kind);
		}
	}

	public Logo getLogo(String kind) {
		String squareId = UserContext.instance().getUserInfo().getCurrentSquareId();
		Logo logo = this.logoDAO.getBySquareIdAndKind(squareId, kind);

		return logo;
	}

	public boolean isExistsLogoImage(String kind) {
		boolean result = false;
		Logo logo = this.getLogo(kind);
		if(logo != null)
			result = true;
		return result;
	}
}