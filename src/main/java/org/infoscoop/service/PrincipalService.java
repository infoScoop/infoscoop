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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoscoop.context.UserContext;
import org.infoscoop.dao.PrincipalDAO;
import org.infoscoop.dao.model.Principal;
import org.infoscoop.dao.model.Square;
import org.infoscoop.util.SpringUtil;

public class PrincipalService {
	private static Log log = LogFactory.getLog(PrincipalService.class);

	private PrincipalDAO principalDAO;

	public static PrincipalService getHandle(){
		return (PrincipalService) SpringUtil.getBean("PrincipalService");
	}

	public PrincipalDAO getPrincipalDAO() {
		return principalDAO;
	}

	public void setPrincipalDAO(PrincipalDAO principalDAO) {
		this.principalDAO = principalDAO;
	}
	
	public List<Principal> getPrincipals(){
		String squareId = UserContext.instance().getUserInfo().getCurrentSquareId();
		Square currentSquare = SquareService.getHandle().getEntity(squareId);
		String parentSquareId = currentSquare.getParentSquareId();
		String targetSquareId = (parentSquareId != null && parentSquareId.length() == 0)? parentSquareId : squareId;
		return principalDAO.getBySquareId(targetSquareId);
	}
}
