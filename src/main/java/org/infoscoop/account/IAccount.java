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

package org.infoscoop.account;

import java.util.List;
import java.util.Map;

public interface IAccount {
	String getUid();
	String getName();
	String getMail();
	String getGroupName();
	String getDefaultSquareId();
	String getMySquareId();
	IGroup[] getGroups();
	List<String> getMails();
	List<String> getBelongids();
	List<Map<String, String>> getAttributes();
	public boolean isAdmin();
	public void setAdmin(boolean isAdmin);
	boolean isEnableAddSquareUser();
}
