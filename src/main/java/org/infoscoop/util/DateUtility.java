package org.infoscoop.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DateUtil.java
 * 
 * @author Atsuhiko Kimura
 */
public class DateUtility {
	private static final Log log = LogFactory.getLog(DateUtility.class);

	private static final String W3CDTF_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	private static SimpleDateFormat FULL_DATE = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
	
	private static SimpleDateFormat DEFAULT_DATE = new SimpleDateFormat( "EEE, d MMM yyyy HH:mm:ss Z" );
	
	private static SimpleDateFormat formatGMT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);


	private static SimpleDateFormat format1 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm");

	private static SimpleDateFormat format2 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ssZ");

	private static SimpleDateFormat format3 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss'Z'");
	
	// Fri Sep 14 11:05:53 JST 2007
	private static SimpleDateFormat imap4Date = new SimpleDateFormat(
			"EEE MMM d HH:mm:ss 'JST' yyyy", Locale.ENGLISH);
	
	public static DateFormat newW3CDFWithoutSecond(){
		return (DateFormat) format1.clone();
	}

	public static DateFormat newW3CDFWithoutT(){
		return (DateFormat) format2.clone();
	}
	
	public static DateFormat newImap4DateFormat(){
		return (DateFormat) imap4Date.clone();
	}
	
	public static DateFormat newGMTDateFormat(){
		return (DateFormat) formatGMT.clone();
	}
	
	/**
	 * We convert a date of java.util.Date type into a W3CDTF form.
	 * 
	 * @param date
	 * @return
	 */
	public static String getW3CDTFDate(Date date) {
		String str = new SimpleDateFormat( W3CDTF_FORMAT ).format(date);
		str = str.substring(0, str.length() - 2) + ":"
				+ str.substring(str.length() - 2);
		return str;
	}

	public static Date parseW3CDTFDate(String dateString){
		Matcher m = null;
		if ((m = W3CDTF_FORMAT1.matcher(dateString)).matches()) {
			TimeZone tz;
			if( "Z".equals( m.group(7))) {
				tz = TimeZone.getTimeZone("GMT+00:00");
			} else {
				tz = TimeZone.getTimeZone("GMT" + m.group(7));
			}
			Calendar c = Calendar.getInstance(tz);
			int year = Integer.parseInt(m.group(1));
			int month = Integer.parseInt(m.group(2)) - 1;
			int date = Integer.parseInt(m.group(3));
			int hour = Integer.parseInt(m.group(4));
			int minute = Integer.parseInt(m.group(5));
			int second = Integer.parseInt(m.group(6));
			c.set(year, month, date, hour, minute, second);
			c.set(Calendar.MILLISECOND, 0);
			return c.getTime();
		}
		if ((m = W3CDTF_FORMAT2.matcher(dateString)).matches()) {
			TimeZone tz;
			if( "Z".equals( m.group(6))) {
				tz = TimeZone.getTimeZone("GMT+00:00");
			} else {
				tz = TimeZone.getTimeZone("GMT" + m.group(6));
			}
			Calendar c = Calendar.getInstance(tz);
			int year = Integer.parseInt(m.group(1));
			int month = Integer.parseInt(m.group(2)) - 1;
			int date = Integer.parseInt(m.group(3));
			int hour = Integer.parseInt(m.group(4));
			int minute = Integer.parseInt(m.group(5));
			c.set(year, month, date, hour, minute, 0);
			c.set(Calendar.MILLISECOND, 0);
			return c.getTime();
		}
		if ((m = W3CDTF_FORMAT3.matcher(dateString)).matches()) {
			Calendar c = Calendar.getInstance();
			int year = Integer.parseInt(m.group(1));
			int month = Integer.parseInt(m.group(2)) - 1;
			int date = Integer.parseInt(m.group(3));
			c.set(year, month, date, 0, 0, 0);
			c.set(Calendar.MILLISECOND, 0);
			return c.getTime();
		}
		if ((m = W3CDTF_FORMAT4.matcher(dateString)).matches()) {
			Calendar c = Calendar.getInstance();
			int year = Integer.parseInt(m.group(1));
			int month = Integer.parseInt(m.group(2)) - 1;
			c.set(year, month, 1, 0, 0, 0);
			c.set(Calendar.MILLISECOND, 0);
			return c.getTime();
		}
		if ((m = W3CDTF_FORMAT5.matcher(dateString)).matches()) {
			Calendar c = Calendar.getInstance();
			int year = Integer.parseInt(m.group(1));
			c.set(year, 0, 1, 0, 0, 0);
			c.set(Calendar.MILLISECOND, 0);
			return c.getTime();
		}
		return null;
	}

	/**
	 * We convert the date of the W3CDTF form into java.util.Date type.
	 * 
	 * @param W3CDTFDate
	 * @return
	 */
	public static Date getDateFromW3CDTF(String w3cDTFDate) {
		if (w3cDTFDate == null || w3cDTFDate.length() == 0)
			return null;

		try {
			int year = Integer.parseInt(w3cDTFDate.substring(0, 4));
			int month = Integer.parseInt(w3cDTFDate.substring(5, 7));
			int day = Integer.parseInt(w3cDTFDate.substring(8, 10));
			int hour = Integer.parseInt(w3cDTFDate.substring(11, 13));
			int minute = Integer.parseInt(w3cDTFDate.substring(14, 16));
			int second = Integer.parseInt(w3cDTFDate.substring(17, 19));
			String zone = "GMT" + w3cDTFDate.substring(19);
			Calendar cal = Calendar.getInstance();
			cal.set(year, month - 1, day, hour, minute, second);
			cal.set(Calendar.MILLISECOND, 0);
			cal.setTimeZone(TimeZone.getTimeZone(zone));
			return cal.getTime();
		} catch (Exception e) {
			log.error(w3cDTFDate + " is invalid date string.", e);
		}

		return null;
	}

	private static final String YMD= "([0-9]{4})-([0-9]{2})-([0-9]{2})";
	private static final String ZONE = "(Z|(?:[\\\\+\\\\-][0-9]{2}:[0-9]{2}))";
	
	private static final Pattern W3CDTF_FORMAT1 = Pattern.compile( YMD+"[T ]([0-9]{2}):([0-9]{2}):([0-9]{2})"+ZONE );
	private static final Pattern W3CDTF_FORMAT2 = Pattern.compile( YMD+"[T ]([0-9]{2}):([0-9]{2})"+ZONE );
	private static final Pattern W3CDTF_FORMAT3 = Pattern.compile( YMD );
	private static final Pattern W3CDTF_FORMAT4 = Pattern.compile("([0-9]{4})-([0-9]{2})");
	private static final Pattern W3CDTF_FORMAT5 = Pattern.compile("([0-9]{4})");
	
	public static boolean isToday(Date date) {
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar then = new GregorianCalendar();
        
        then.setTime(date);
        return isSameDay(now, then);
    }

	public static boolean isSameDay(Calendar calA, Calendar calB) {
	        return (calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) &&
	                        calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR));
	}
	
}