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

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * Tests if time strings are correctly parsed and Calendars are correctly
 * formatted using TimeParser.
 * 
 * @author Marco Steybe
 */
public class TimeParserTest extends TestCase {

    // date and times to be tested
    private static final String T0 = "2007-11-28";
    private static final String T1 = "2007-11-28T09:55:07";
    private static final String T2 = "2007-11-28T09:55:07.07Z";
    private static final String T3 = "2007-11-28T09:55:07-01:00";
    private static final String T4 = "2007-11-28T09:55:07+01:30";
    private static final String T5 = "2007-11-28T09:55:07.11166Z";
    private static final String T6 = "2007-11-28T09:55:07.11111Z";

    /**
     * Test method for
     * {@link org.accada.epcis.utils.TimeParser#parseAsCalendar(java.lang.String)}.
     * 
     * @throws ParseException
     *             If an error parsing a date occurred.
     */
    public void testParse() throws ParseException {
        Calendar calAct = null;
        try {
            calAct = TimeParser.parseAsCalendar(T0);
            fail("Expected ParseException!");
        } catch (ParseException e) {
            // ok
            assertEquals("expected delimiter 'T' at position 10", e.getMessage());
        }

        calAct = TimeParser.parseAsCalendar(T1);
        Calendar calExp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calExp.setTimeInMillis(0);
        calExp.setLenient(false);
        calExp.set(2007, 11 - 1, 28, 9, 55, 7);
        assertEquals(calExp, calAct);

        calAct = TimeParser.parseAsCalendar(T2);
        calExp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calExp.setTimeInMillis(0);
        calExp.setLenient(false);
        calExp.set(2007, 11 - 1, 28, 9, 55, 7);
        calExp.set(Calendar.MILLISECOND, 70);
        assertEquals(calExp, calAct);

        calAct = TimeParser.parseAsCalendar(T3);
        calExp = Calendar.getInstance(TimeZone.getTimeZone("GMT-01:00"));
        calExp.setTimeInMillis(0);
        calExp.setLenient(false);
        calExp.set(2007, 11 - 1, 28, 9, 55, 7);
        assertEquals(calExp, calAct);

        calAct = TimeParser.parseAsCalendar(T4);
        calExp = Calendar.getInstance(TimeZone.getTimeZone("GMT+01:30"));
        calExp.setTimeInMillis(0);
        calExp.setLenient(false);
        calExp.set(2007, 11 - 1, 28, 9, 55, 7);
        assertEquals(calExp, calAct);

        calAct = TimeParser.parseAsCalendar(T5);
        calExp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calExp.setTimeInMillis(0);
        calExp.setLenient(false);
        calExp.set(2007, 11 - 1, 28, 9, 55, 7);
        calExp.set(Calendar.MILLISECOND, 112);
        assertEquals(calExp, calAct);

        calAct = TimeParser.parseAsCalendar(T6);
        calExp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calExp.setTimeInMillis(0);
        calExp.setLenient(false);
        calExp.set(2007, 11 - 1, 28, 9, 55, 7);
        calExp.set(Calendar.MILLISECOND, 111);
        assertEquals(calExp, calAct);
    }

    /**
     * Test method for
     * {@link org.accada.epcis.utils.TimeParser#format(java.util.Calendar)}.
     */
    public void testFormat() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(0);
        cal.setLenient(false);
        cal.set(2007, 11 - 1, 28, 9, 55, 7);
        assertEquals(T1 + ".000Z", TimeParser.format(cal));

        cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(0);
        cal.setLenient(false);
        cal.set(2007, 11 - 1, 28, 9, 55, 7);
        cal.set(Calendar.MILLISECOND, 70);
        assertEquals(T2.substring(0, T2.length() - 1) + "0Z", TimeParser.format(cal));

        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-01:00"));
        cal.setTimeInMillis(0);
        cal.setLenient(false);
        cal.set(2007, 11 - 1, 28, 9, 55, 7);
        assertEquals(T3.substring(0, T3.length() - 6) + ".000-01:00", TimeParser.format(cal));
    }

}
