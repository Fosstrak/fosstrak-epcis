/*
 * Copyright (C) 2010 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org) and
 * was developed as part of the webofthings.com initiative.
 *
 * Fosstrak is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Fosstrak is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Fosstrak; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */
package org.epcis.fosstrak.restadapter.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author www.fosstrak.org
 * Modified by mathias.mueller(at)unifr.ch
 *
 * This utility class was taken from fosstrak version 0.4.2
 * and slightly modified for the use with the EPCIS REST Adapter.
 *
 * Copyright (C) 2007 ETH Zurich
 *
 */
public final class TimeParser {

    private static final DecimalFormat XX_FORMAT   = new DecimalFormat("00");
    private static final DecimalFormat XXX_FORMAT  = new DecimalFormat("000");
    private static final DecimalFormat XXXX_FORMAT = new DecimalFormat("0000");

    private TimeParser() {}

    /**
     * Method description
     *
     *
     * @param text
     *
     * @return
     *
     * @throws ParseException
     */
    public static Calendar parseAsCalendar(final String text) throws ParseException {
        return parse(text);
    }

    /**
     * Method description
     *
     *
     * @param text
     *
     * @return
     *
     * @throws ParseException
     */
    public static Date parseAsDate(final String text) throws ParseException {
        return parse(text).getTime();
    }

    /**
     * Method description
     *
     *
     * @param text
     *
     * @return
     *
     * @throws ParseException
     */
    public static Timestamp parseAsTimestamp(final String text) throws ParseException {
        return convert(parse(text));
    }

    private static Calendar parse(final String text) throws ParseException {
        try {
            String time = text;

            if ((time == null) || (time.length() == 0)) {
                throw new IllegalArgumentException("Date/Time string may not be null or empty.");
            }

            time = time.trim();

            char sign;
            int  curPos;

            if (time.startsWith("-")) {
                sign   = '-';
                curPos = 1;
            } else if (time.startsWith("+")) {
                sign   = '+';
                curPos = 1;
            } else {
                sign   = '+';    // no sign specified, implied '+'
                curPos = 0;
            }

            int    year, month, day, hour, min, sec, ms;
            String tzID;
            char   delimiter;

            // parse year
            try {
                year = Integer.parseInt(time.substring(curPos, curPos + 4));
            } catch (NumberFormatException e) {
                throw new ParseException("Year (YYYY) has wrong format: " + e.getMessage(), curPos);
            }

            curPos    += 4;
            delimiter = '-';

            if ((curPos >= time.length()) || (time.charAt(curPos) != delimiter)) {
                throw new ParseException("expected delimiter '" + delimiter + "' at position " + curPos, curPos);
            }

            curPos++;

            // parse month
            try {
                month = Integer.parseInt(time.substring(curPos, curPos + 2));
            } catch (NumberFormatException e) {
                throw new ParseException("Month (MM) has wrong format: " + e.getMessage(), curPos);
            }

            curPos    += 2;
            delimiter = '-';

            if ((curPos >= time.length()) || (time.charAt(curPos) != delimiter)) {
                throw new ParseException("expected delimiter '" + delimiter + "' at position " + curPos, curPos);
            }

            curPos++;

            // parse day
            try {
                day = Integer.parseInt(time.substring(curPos, curPos + 2));
            } catch (NumberFormatException e) {
                throw new ParseException("Day (DD) has wrong format: " + e.getMessage(), curPos);
            }

            curPos    += 2;
            delimiter = 'T';

            if ((curPos >= time.length()) || (time.charAt(curPos) != delimiter)) {
                throw new ParseException("expected delimiter '" + delimiter + "' at position " + curPos, curPos);
            }

            curPos++;

            // parse hours
            try {
                hour = Integer.parseInt(time.substring(curPos, curPos + 2));
            } catch (NumberFormatException e) {
                throw new ParseException("Hour (hh) has wrong format: " + e.getMessage(), curPos);
            }

            curPos    += 2;
            delimiter = ':';

            if ((curPos >= time.length()) || (time.charAt(curPos) != delimiter)) {
                throw new ParseException("expected delimiter '" + delimiter + "' at position " + curPos, curPos);
            }

            curPos++;

            // parse minute
            try {
                min = Integer.parseInt(time.substring(curPos, curPos + 2));
            } catch (NumberFormatException e) {
                throw new ParseException("Minute (mm) has wrong format: " + e.getMessage(), curPos);
            }

            curPos    += 2;
            delimiter = ':';

            if ((curPos >= time.length()) || (time.charAt(curPos) != delimiter)) {
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

            if ((curPos < time.length()) && (time.charAt(curPos) == delimiter)) {
                curPos++;

                try {

                    // read all digits (number of digits unknown)
                    StringBuilder millis = new StringBuilder();

                    while ((curPos < time.length()) && isNumeric(time.charAt(curPos))) {
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
            if ((curPos < time.length()) && ((time.charAt(curPos) == '+') || (time.charAt(curPos) == '-'))) {

                // offset to UTC specified in the format +00:00/-00:00
                tzID = "GMT" + time.substring(curPos);
            } else if ((curPos < time.length()) && time.substring(curPos).equals("Z")) {
                tzID = "UTC";
            } else {

                // throw new ParseException("invalid time zone designator",
                // curPos);
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

            if ((sign == '-') || (year == 0)) {

                // not CE, need to set era (BCE) and adjust year
                cal.set(Calendar.YEAR, year + 1);
                cal.set(Calendar.ERA, GregorianCalendar.BC);
            } else {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.ERA, GregorianCalendar.AD);
            }

            cal.set(Calendar.MONTH, month - 1);    // month is 0-based
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
            cal.set(Calendar.SECOND, sec);
            cal.set(Calendar.MILLISECOND, ms);

            // the following will trigger an IllegalArgumentException if any of
            // the set values are illegal or out of range
            cal.getTime();

            return cal;
        } catch (StringIndexOutOfBoundsException e) {
            throw new ParseException("date/time value has invalid format", -1);
        }
    }

    /**
     * Method description
     *
     *
     * @param date
     *
     * @return
     */
    public static String format(final Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        cal.setTimeInMillis(date.getTime());

        return format(cal);
    }

    /**
     * Method description
     *
     *
     * @param ts
     *
     * @return
     */
    public static String format(final Timestamp ts) {
        return format(convert(ts));
    }

    /**
     * Method description
     *
     *
     * @param cal
     *
     * @return
     */
    public static String format(final Calendar cal) {
        if (cal == null) {
            throw new IllegalArgumentException("argument can not be null");
        }

        // determine era and adjust year if necessary
        int year = cal.get(Calendar.YEAR);

        if (cal.isSet(Calendar.ERA) && (cal.get(Calendar.ERA) == GregorianCalendar.BC)) {
            year = 0 - year + 1;
        }

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
            int hours   = Math.abs((offset / (60 * 1000)) / 60);
            int minutes = Math.abs((offset / (60 * 1000)) % 60);

            buf.append((offset < 0)
                       ? '-'
                       : '+');
            buf.append(XX_FORMAT.format(hours));
            buf.append(':');
            buf.append(XX_FORMAT.format(minutes));
        } else {
            buf.append('Z');
        }

        return buf.toString();
    }

    private static boolean isNumeric(final char c) {
        return (((c >= '0') && (c <= '9'))
                ? true
                : false);
    }

    /**
     * Method description
     *
     *
     * @param ts
     *
     * @return
     */
    public static Calendar convert(final Timestamp ts) {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));

        cal.setTimeInMillis(ts.getTime());

        return cal;
    }

    /**
     * Adds 1001 Milliseconds to the Time
     *
     *
     * @param cal
     *
     * @return
     */
    public static Timestamp convert(final Calendar cal) {
        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * Method description
     *
     *
     * @param time
     *
     * @return
     */
    public static String addOneSecondToTime(String time) {
        Calendar cal;

        try {
            cal = TimeParser.parseAsCalendar(time);

            long timeInMillis = cal.getTimeInMillis();

            timeInMillis = timeInMillis + 1001;
            cal.setTimeInMillis(timeInMillis);
            time = TimeParser.format(cal);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return time;
    }

    /**
     * Method description
     *
     *
     * @param args
     */
    public static void main(String[] args) {
        String test = "2009-01-01T01:01:01";

        System.out.println(test);
        System.out.println(TimeParser.addOneSecondToTime(test));

        test = "2009-12-31T23:59:01";
        System.out.println(test);
        System.out.println(TimeParser.addOneSecondToTime(test));

        test = "2009-01-09T12:00:00";
        System.out.println(test);
        System.out.println(TimeParser.addOneSecondToTime(test));
    }
}
