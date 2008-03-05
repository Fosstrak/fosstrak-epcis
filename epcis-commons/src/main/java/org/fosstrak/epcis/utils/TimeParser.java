/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Accada (www.accada.org).
 *
 * Accada is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Accada is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Accada; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.accada.epcis.utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * The <code>TimeParser</code> utility class provides helper methods to deal
 * with date/time formatting using a specific ISO8601-compliant format (see <a
 * href="http://www.w3.org/TR/NOTE-datetime">ISO 8601</a>). <p/> The currently
 * supported format is:
 * 
 * <pre>
 *             &amp;plusmnYYYY-MM-DDThh:mm:ss[.S]TZD
 * </pre>
 * 
 * where:
 * 
 * <pre>
 *             &amp;plusmnYYYY = four-digit year with optional sign where values &lt;= 0 are
 *                     denoting years BCE and values &gt; 0 are denoting years CE,
 *                     e.g. -0001 denotes the year 2 BCE, 0000 denotes the year 1 BCE,
 *                     0001 denotes the year 1 CE, and so on...
 *             MM    = two-digit month (01=January, etc.)
 *             DD    = two-digit day of month (01 through 31)
 *             hh    = two digits of hour (00 through 23) (am/pm NOT allowed)
 *             mm    = two digits of minute (00 through 59)
 *             ss    = two digits of second (00 through 59)
 *             S     = optional one or more digits representing a decimal fraction of a second
 *             TZD   = time zone designator, Z for Zulu (i.e. UTC) or an offset from UTC
 *                     in the form of +hh:mm or -hh:mm
 * </pre>
 */
public final class TimeParser {

    /**
     * Miscellaneous numeric formats used in formatting.
     */
    private static final DecimalFormat XX_FORMAT = new DecimalFormat("00");
    private static final DecimalFormat XXX_FORMAT = new DecimalFormat("000");
    private static final DecimalFormat XXXX_FORMAT = new DecimalFormat("0000");

    /**
     * Empty private constructor to hide default constructor.
     */
    private TimeParser() {
    }

    /**
     * Parses an ISO8601-compliant date/time string into a <code>Calendar</code>.
     * 
     * @param text
     *            The date/time string to be parsed.
     * @return A <code>Calendar</code> representing the date/time.
     * @throws ParseException
     *             If the date/time could not be parsed.
     */
    public static Calendar parseAsCalendar(final String text) throws ParseException {
        return parse(text);
    }

    /**
     * Parses an ISO8601-compliant date/time string into a <code>Date</code>.
     * 
     * @param text
     *            The date/time string to be parsed.
     * @return A <code>Date</code> representing the date/time.
     * @throws ParseException
     *             If the date/time could not be parsed.
     */
    public static Date parseAsDate(final String text) throws ParseException {
        return parse(text).getTime();
    }

    /**
     * Parses an ISO8601-compliant date/time string into a
     * <code>Timestamp</code>.
     * 
     * @param text
     *            The date/time string to be parsed.
     * @return A <code>Timestamp</code> representing the date/time.
     * @throws ParseException
     *             If the date/time could not be parsed.
     */
    public static Timestamp parseAsTimestamp(final String text) throws ParseException {
        return convert(parse(text));
    }

    /**
     * Parses an ISO8601-compliant date/time string into a <code>Calendar</code>.
     * 
     * @param text
     *            The date/time string to be parsed.
     * @return A <code>Calendar</code> representing the date/time.
     * @throws ParseException
     *             If the date/time could not be parsed.
     */
    private static Calendar parse(final String text) throws ParseException {
        String time = text;
        if (time == null || time.length() == 0) {
            throw new IllegalArgumentException("Date/Time string may not be null or empty.");
        }
        time = time.trim();
        char sign;
        int curPos;
        if (time.startsWith("-")) {
            sign = '-';
            curPos = 1;
        } else if (time.startsWith("+")) {
            sign = '+';
            curPos = 1;
        } else {
            sign = '+'; // no sign specified, implied '+'
            curPos = 0;
        }

        int year, month, day, hour, min, sec, ms;
        String tzID;
        char delimiter;

        // parse year
        try {
            year = Integer.parseInt(time.substring(curPos, curPos + 4));
        } catch (NumberFormatException e) {
            throw new ParseException("Year (YYYY) has wrong format: " + e.getMessage(), curPos);
        }
        curPos += 4;
        delimiter = '-';
        if (curPos >= time.length() || time.charAt(curPos) != delimiter) {
            throw new ParseException("expected delimiter '" + delimiter + "' at position " + curPos, curPos);
        }
        curPos++;

        // parse month
        try {
            month = Integer.parseInt(time.substring(curPos, curPos + 2));
        } catch (NumberFormatException e) {
            throw new ParseException("Month (MM) has wrong format: " + e.getMessage(), curPos);
        }
        curPos += 2;
        delimiter = '-';
        if (curPos >= time.length() || time.charAt(curPos) != delimiter) {
            throw new ParseException("expected delimiter '" + delimiter + "' at position " + curPos, curPos);
        }
        curPos++;

        // parse day
        try {
            day = Integer.parseInt(time.substring(curPos, curPos + 2));
        } catch (NumberFormatException e) {
            throw new ParseException("Day (DD) has wrong format: " + e.getMessage(), curPos);
        }
        curPos += 2;
        delimiter = 'T';
        if (curPos >= time.length() || time.charAt(curPos) != delimiter) {
            throw new ParseException("expected delimiter '" + delimiter + "' at position " + curPos, curPos);
        }
        curPos++;

        // parse hours
        try {
            hour = Integer.parseInt(time.substring(curPos, curPos + 2));
        } catch (NumberFormatException e) {
            throw new ParseException("Hour (hh) has wrong format: " + e.getMessage(), curPos);
        }
        curPos += 2;
        delimiter = ':';
        if (curPos >= time.length() || time.charAt(curPos) != delimiter) {
            throw new ParseException("expected delimiter '" + delimiter + "' at position " + curPos, curPos);
        }
        curPos++;

        // parse minute
        try {
            min = Integer.parseInt(time.substring(curPos, curPos + 2));
        } catch (NumberFormatException e) {
            throw new ParseException("Minute (mm) has wrong format: " + e.getMessage(), curPos);
        }
        curPos += 2;
        delimiter = ':';
        if (curPos >= time.length() || time.charAt(curPos) != delimiter) {
            throw new ParseException("expected delimiter '" + delimiter + "' at position " + curPos, curPos);
        }
        curPos++;

        // parse second
        try {
            sec = Integer.parseInt(time.substring(curPos, curPos + 2));
        } catch (NumberFormatException e) {
            throw new ParseException("Second (ss) has wrong format: " + e.getMessage(), curPos);
        }
        curPos += 2;

        // parse millisecond
        delimiter = '.';
        if (curPos < time.length() && time.charAt(curPos) == delimiter) {
            curPos++;
            try {
                // read all digits (number of digits unknown)
                StringBuilder millis = new StringBuilder();
                while (curPos < time.length() && isNumeric(time.charAt(curPos))) {
                    millis.append(time.charAt(curPos));
                    curPos++;
                }
                // convert to milliseconds (max 3 digits)
                if (millis.length() == 1) {
                    ms = 100 * Integer.parseInt(millis.toString());
                } else if (millis.length() == 2) {
                    ms = 10 * Integer.parseInt(millis.toString());
                } else if (millis.length() >= 3) {
                    ms = Integer.parseInt(millis.substring(0, 3));
                    if (millis.length() > 3) {
                        // round
                        if (Integer.parseInt(String.valueOf(millis.charAt(3))) >= 5) {
                            ms++;
                        }
                    }
                } else {
                    ms = 0;
                }
            } catch (NumberFormatException e) {
                throw new ParseException("Millisecond (S) has wrong format: " + e.getMessage(), curPos);
            }
        } else {
            ms = 0;
        }

        // parse time zone designator (Z or +00:00 or -00:00)
        if (curPos < time.length() && (time.charAt(curPos) == '+' || time.charAt(curPos) == '-')) {
            // offset to UTC specified in the format +00:00/-00:00
            tzID = "GMT" + time.substring(curPos);
        } else if (curPos < time.length() && time.substring(curPos).equals("Z")) {
            tzID = "UTC";
        } else {
            // throw new ParseException("invalid time zone designator", curPos);
            // no time zone designator found, using default 'UTC'
            tzID = "UTC";
        }

        TimeZone tz = TimeZone.getTimeZone(tzID);
        // verify id of returned time zone (getTimeZone defaults to "UTC")
        if (!tz.getID().equals(tzID)) {
            throw new ParseException("invalid time zone '" + tzID + "'", curPos);
        }

        // initialize Calendar object
        Calendar cal = GregorianCalendar.getInstance(tz);
        cal.setLenient(false);
        if (sign == '-' || year == 0) {
            // not CE, need to set era (BCE) and adjust year
            cal.set(Calendar.YEAR, year + 1);
            cal.set(Calendar.ERA, GregorianCalendar.BC);
        } else {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.ERA, GregorianCalendar.AD);
        }
        cal.set(Calendar.MONTH, month - 1); // month is 0-based
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, sec);
        cal.set(Calendar.MILLISECOND, ms);

        // the following will trigger an IllegalArgumentException if any of
        // the set values are illegal or out of range
        cal.getTime();

        return cal;
    }

    /**
     * Formats a <code>Date</code> value into an ISO8601-compliant date/time
     * string.
     * 
     * @param date
     *            The time value to be formatted into a date/time string.
     * @return The formatted date/time string.
     */
    public static String format(final Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(date.getTime());
        return format(cal);
    }

    /**
     * Formats a <code>Timestamp</code> value into an ISO8601-compliant
     * date/time string.
     * 
     * @param ts
     *            The time value to be formatted into a date/time string.
     * @return The formatted date/time string.
     */
    public static String format(final Timestamp ts) {
        return format(convert(ts));
    }

    /**
     * Formats a <code>Calendar</code> value into an ISO8601-compliant
     * date/time string.
     * 
     * @param cal
     *            The time value to be formatted into a date/time string.
     * @return The formatted date/time string.
     */
    public static String format(final Calendar cal) {
        if (cal == null) {
            throw new IllegalArgumentException("argument can not be null");
        }

        // determine era and adjust year if necessary
        int year = cal.get(Calendar.YEAR);
        if (cal.isSet(Calendar.ERA) && cal.get(Calendar.ERA) == GregorianCalendar.BC) {
            /**
             * calculate year using astronomical system: year n BCE =>
             * astronomical year -n + 1
             */
            year = 0 - year + 1;
        }

        /**
         * the format of the date/time string is: YYYY-MM-DDThh:mm:ss.SSSTZD
         * note that we cannot use java.text.SimpleDateFormat for formatting
         * because it can't handle years <= 0 and TZD's
         */
        StringBuilder buf = new StringBuilder();
        // year ([-]YYYY)
        buf.append(XXXX_FORMAT.format(year));
        buf.append('-');
        // month (MM)
        buf.append(XX_FORMAT.format(cal.get(Calendar.MONTH) + 1));
        buf.append('-');
        // day (DD)
        buf.append(XX_FORMAT.format(cal.get(Calendar.DAY_OF_MONTH)));
        buf.append('T');
        // hour (hh)
        buf.append(XX_FORMAT.format(cal.get(Calendar.HOUR_OF_DAY)));
        buf.append(':');
        // minute (mm)
        buf.append(XX_FORMAT.format(cal.get(Calendar.MINUTE)));
        buf.append(':');
        // second (ss)
        buf.append(XX_FORMAT.format(cal.get(Calendar.SECOND)));
        buf.append('.');
        // millisecond (SSS)
        buf.append(XXX_FORMAT.format(cal.get(Calendar.MILLISECOND)));
        // time zone designator (Z or +00:00 or -00:00)
        TimeZone tz = cal.getTimeZone();
        // determine offset of timezone from UTC (incl. daylight saving)
        int offset = tz.getOffset(cal.getTimeInMillis());
        if (offset != 0) {
            int hours = Math.abs((offset / (60 * 1000)) / 60);
            int minutes = Math.abs((offset / (60 * 1000)) % 60);
            buf.append(offset < 0 ? '-' : '+');
            buf.append(XX_FORMAT.format(hours));
            buf.append(':');
            buf.append(XX_FORMAT.format(minutes));
        } else {
            buf.append('Z');
        }
        return buf.toString();
    }

    /**
     * Checks whether the given character is Numeric.
     * 
     * @param c
     *            The character to check.
     * @return <code>true</code> if the given character is numeric,
     *         <code>false</code> otherwise.
     */
    private static boolean isNumeric(final char c) {
        return (((c >= '0') && (c <= '9')) ? true : false);
    }

    /**
     * Converts an SQL timestamp value into a Calendar object.
     * 
     * @param ts
     *            The java.sql.Timestamp to convert.
     * @return The Calendar object representing the given timestamp.
     */
    public static Calendar convert(final Timestamp ts) {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(ts.getTime());
        return cal;
    }

    /**
     * Converts a Calendar object into an SQL Timestamp value.
     * 
     * @param cal
     *            The Calendar object to convert.
     * @return The java.sql.Timestamp representing the given Calendar value.
     */
    public static Timestamp convert(final Calendar cal) {
        return new Timestamp(cal.getTimeInMillis());
    }

}
