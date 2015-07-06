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

package org.infoscoop.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

/**
 * A utility class related to character string.
 * 
 * @author Eiichi Sakurai
 */
public class StringUtil {
	public static String getNullSafe( String str ) {
		if( str == null )
			return "";
		
		return str;
	}
	/**
	 * We substitute string character according to a substitution map.
	 * @param inStr Input character string
	 * @param replaceMap Substitution map
	 * @return
	 */
	public static String replaceMap(String inStr, Map replaceMap) {
		String str = inStr;
		Iterator itr = replaceMap.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			String value = (String) replaceMap.get(key);
			if (key != null && value != null) {
				str = replaceStr(str, key, value);
			}
		}
		return str;
	}

	/**
	 * We substitute character string.
	 * @param inStr Input character string
	 * @param fromStr Character string that is target to substitute.
	 * @param toStr  Character string after substituted
	 */
	public static String replaceStr(
		String inStr,
		String fromStr,
		String toStr) {
		String str = inStr;
		int fromLen = fromStr.length();
		int toLen = toStr.length();
		int idx = 0;
		while (true) {
			idx = str.indexOf(fromStr, idx);
			if (idx < 0) {
				break;
			}
			str = str.substring(0, idx) + toStr + str.substring(idx + fromLen);
			idx += toLen;
		}
		return str;
	}
	
	/**
	 * @param originalString Original character string
	 * @param byteLength A necessary byte length
	 * @param charset Because we use DB2, we fix "UTF-8".
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getTruncatedString(String originalString, int byteLength, String charset) throws UnsupportedEncodingException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] originalBytes = originalString.getBytes(charset);
		if(originalBytes.length <= byteLength) return originalString;
		out.write(originalBytes, 0 , byteLength + 1);
		String result = new String(out.toByteArray(), charset);
		return result.substring(0, result.length()-1);
	}
	
	/**
	 * convert into Hexadecimal notation of Unicode.<br>
	 * example）a→\u0061
	 * @param str
	 * @return
	 */
	public static String toHexString(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			sb.append(toHexString(str.charAt(i)));
		}
		return sb.toString();
	}

	/**
	 * convert into Hexadecimal notation of Unicode.<br>
	 * example）a→\u0061
	 * @param ch
	 * @return
	 */
	public static String toHexString(char ch) {
		String hex = Integer.toHexString((int) ch);
		while (hex.length() < 4) {
			hex = "0" + hex;
		}
		hex = "\\u" + hex;
		return hex;
	}


	/**
	 * メールアドレス構文チェック
	 * @param mail
	 * @return
	 */
	public static boolean isValidEmail(String email){
		String mailFormat = "^[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+(\\.[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+)*+(.*)@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9\\-]+)+$";
		return email.matches(mailFormat);
	}
}
