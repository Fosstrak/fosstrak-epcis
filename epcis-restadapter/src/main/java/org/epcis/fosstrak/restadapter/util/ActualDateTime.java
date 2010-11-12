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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Class to help the handle with different time formats
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 *
 */
public class ActualDateTime {

    /**
     * Get the actual calendar as XMLGregorianCalendar
     *
     *
     * @return
     */
    public static XMLGregorianCalendar GET_NOW_XMLGC() {
        XMLGregorianCalendar res = null;

        try {

            // Calendar now = TimeParser.parseAsCalendar(GET_NOW());
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            DatatypeFactory   datatypeFactory   = DatatypeFactory.newInstance();

            res = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return res;
    }

    /**
     * Get the indicated calendar as XMLGregorianCalendar
     *
     *
     * @param time
     *
     * @return
     */
    public static XMLGregorianCalendar GET_TIME_XMLGC(String time) {
        XMLGregorianCalendar res = null;

        try {
            Calendar          cal     = TimeParser.parseAsCalendar(time);
            long              millis  = cal.getTimeInMillis();
            GregorianCalendar gregCal = new GregorianCalendar();

            gregCal.setTimeInMillis(millis);

            DatatypeFactory fact = DatatypeFactory.newInstance();

            res = fact.newXMLGregorianCalendar(gregCal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return res;
    }

    /**
     * Get the actual time zone offset as string
     *
     *
     * @return
     */
    public static String GET_TZO() {
        StringBuilder res                = new StringBuilder();
        Date          date               = GregorianCalendar.getInstance().getTime();
        int           timeZoneOffsetHour = date.getTimezoneOffset() / 60;

        if (timeZoneOffsetHour >= 0) {
            res.append("+");
        } else {
            res.append("+");
            timeZoneOffsetHour *= -1;
        }

        int timeZoneOffsetMin = java.lang.Math.abs(date.getTimezoneOffset()) - (timeZoneOffsetHour * 60);

        if (timeZoneOffsetHour < 10) {
            res.append("0" + timeZoneOffsetHour);
        } else {
            res.append(timeZoneOffsetHour);
        }

        res.append(":");

        if (timeZoneOffsetMin < 10) {
            res.append("0" + timeZoneOffsetMin);
        } else {
            res.append(timeZoneOffsetMin);
        }

        return res.toString();
    }

    /**
     * Get the actual date as string
     *
     *
     * @return
     */
    public static String GET_NOW() {
        StringBuilder res  = new StringBuilder();
        Date          date = GregorianCalendar.getInstance().getTime();

        res.append(date.getYear() + 1900);
        res.append("-");

        int month = date.getMonth() + 1;

        if (month < 10) {
            res.append("0" + month);
        } else {
            res.append(month);
        }

        res.append("-");

        int day = date.getDate();

        if (day < 10) {
            res.append("0" + day);
        } else {
            res.append(day);
        }

        res.append("T");

        int hours = date.getHours();

        if (hours < 10) {
            res.append("0" + hours);
        } else {
            res.append(hours);
        }

        res.append(":");

        int minutes = date.getMinutes();

        if (minutes < 10) {
            res.append("0" + minutes);
        } else {
            res.append(minutes);
        }

        res.append(":");

        int seconds = date.getSeconds();

        if (seconds < 10) {
            res.append("0" + seconds);
        } else {
            res.append(seconds);
        }

        return res.toString();
    }

    /**
     * Get the actual date with the time zone offset as string
     *
     *
     * @return
     */
    public static String GET_NOW_WITH_TZO() {
        return GET_NOW() + GET_TZO();

    }


    /**
     * Method description
     *
     *
     * @param args
     */
    public static void main(String[] args) {
        String now = ActualDateTime.GET_NOW_WITH_TZO();

        System.out.println(now);

        String now2 = ActualDateTime.GET_TIME_XMLGC(now).toString();

        System.out.println(now2);

        now = "2009-07-01T01:00:00+02:00";
        System.out.println(now);
        now2 = ActualDateTime.GET_TIME_XMLGC(now).toString();
        System.out.println(now2);

        now = "2009-07-31T23:59:59+02:00";
        System.out.println(now);
        now2 = ActualDateTime.GET_TIME_XMLGC(now).toString();
        System.out.println(now2);

        now = "2009-01-01T01:00:00+02:00";
        System.out.println(now);
        now2 = ActualDateTime.GET_TIME_XMLGC(now).toString();
        System.out.println(now2);

        now = "2009-12-31T23:59:59+02:00";
        System.out.println(now);
        now2 = ActualDateTime.GET_TIME_XMLGC(now).toString();
        System.out.println(now2);
    }
}
