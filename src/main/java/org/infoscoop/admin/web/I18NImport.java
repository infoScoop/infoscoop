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

package org.infoscoop.admin.web;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.infoscoop.context.UserContext;
import org.infoscoop.dao.I18NDAO;
import org.infoscoop.dao.model.I18NPK;
import org.infoscoop.dao.model.I18n;


/**
 * Analysis ths CSV records, and register in I18N<br>
 * 
 * @param lineNumber
 * @param messageArray
 * @param type
 * @param country
 * @param lang
 * @param insertI18nList
 * @param resultList
 */
public class I18NImport {
	public static final String I18N_IMPORT_MESSAGE_EMPTY = "i18n.import.message.empty";
	public static final String I18N_IMPORT_MESSAGE_INVALID_LENGTH = "i18n.import.message.invalid.length";
	public static final String I18N_IMPORT_ID_EMPTY = "i18n.import.id.empty";
	public static final String I18N_IMPORT_DEFAULT_NOTFOUND = "i18n.import.default.notfound";
	public static final String I18N_IMPORT_ID_INVALID = "i18n.import.id.invalid";
	public static final String I18N_IMPORT_ID_INVALID_LENGTH = "i18n.import.data.invalid.length";
	
	public static final int I18N_MAX_ID = 512;
	public static final int I18N_MAX_MESSAGE = 2048;
	
	private boolean isDefault;
	private boolean isError;
	
	private I18n i18n;
	private int lineNumber;
	private String id;
	
	private String statusMessageId;
	
	public I18NImport(int lineNumber, String[] csvRecord, String type, String country, String lang, List defaultIdList) throws UnsupportedEncodingException {
		boolean isDefault = ("ALL".equalsIgnoreCase(country) && "ALL".equalsIgnoreCase(lang));
		
		this.lineNumber = lineNumber;
		
		this.id = csvRecord[0];
		
		String message;
		if (csvRecord.length < 2 || csvRecord[1] == null
				|| csvRecord[1].trim().length() == 0) {
			
			this.isError = true;
			this.statusMessageId = I18N_IMPORT_MESSAGE_EMPTY;
		} else if (this.id.trim().length() == 0) {
			this.isError = true;
			this.statusMessageId = I18N_IMPORT_ID_EMPTY;
		} else if (this.id.trim().getBytes("UTF-8").length > I18N_MAX_ID) {
			this.isError = true;
			this.statusMessageId = I18N_IMPORT_ID_INVALID_LENGTH;
		} else if (csvRecord[1].trim().getBytes("UTF-8").length > I18N_MAX_MESSAGE) {
			this.isError = true;
			this.statusMessageId = I18N_IMPORT_MESSAGE_INVALID_LENGTH;
		} else if (!this.id.matches("^[a-zA-Z0-9_]+$")) {
			this.isError = true;
			this.statusMessageId = I18N_IMPORT_ID_INVALID;
//		} else if (!isDefault && !I18NService.getHandle().isDefaultExists(type, this.id)) {
		} else if (!isDefault && !defaultIdList.contains(this.id)) {
			// If it isn't "ALL_ALL", register only the one that "ALL_ALL" exists.
			
			this.isError = true;
			this.statusMessageId = I18N_IMPORT_DEFAULT_NOTFOUND;
		} else {
			
			message = csvRecord[1];
			this.i18n = new I18n();
			String squareid = UserContext.instance().getUserInfo().getCurrentSquareId();
			this.i18n.setId(new I18NPK(country, this.id, lang, type, squareid));
			this.i18n.setMessage(message);
		}
		
	}
	
	public boolean execInsertUpdate(I18NDAO dao){
		boolean isUpdated = false;
		
		I18n exists = dao.selectByPK(this.i18n.getId());
		if(exists != null){
			exists.setId(this.i18n.getId());
			exists.setMessage(this.i18n.getMessage());
			this.i18n = exists;
			
			isUpdated = true;
		}
		dao.insertUpdate(this.i18n);
		
		return isUpdated;
	}
	
	public I18n getI18n() {
		return this.i18n;
	}

	public boolean isDefault() {
		return this.isDefault;
	}

	public boolean isError() {
		return this.isError;
	}
	
	public String getStatusMessageId() {
		return this.statusMessageId;
	}
	
	public String getId() {
		return this.id;
	}

	public int getLineNumber() {
		return this.lineNumber;
	}
}
