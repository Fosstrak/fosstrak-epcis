/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org).
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

package org.fosstrak.epcis.repository.query;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.fosstrak.epcis.model.ImplementationException;
import org.fosstrak.epcis.model.QuerySchedule;
import org.fosstrak.epcis.model.SubscriptionControlsException;
import org.fosstrak.epcis.soap.ImplementationExceptionResponse;
import org.fosstrak.epcis.soap.SubscriptionControlsExceptionResponse;

/**
 * Tests for class Schedule.
 * 
 * @author Arthur van Dorp
 */
public class ScheduleTest extends TestCase {

    static {
        // provide the catalina.base property which is not available when the
        // application is not deployed, i.e., when running tests
        if (System.getenv("CATALINA_HOME") != null) {
            System.setProperty("catalina.base", System.getenv("CATALINA_HOME"));
        }
    }

    /**
     * Test for next scheduled year.
     * 
     * @throws ImplementationException
     *             If an error in the implementation occurred.
     * @throws SubscriptionControlsException
     *             If an error in the schedule occurred.
     */
    public void testNextScheduledYear() throws ImplementationExceptionResponse, SubscriptionControlsExceptionResponse {
        // scheduled time is 1.1. 01:00.00
        QuerySchedule qs = new QuerySchedule();
        qs.setSecond("0");
        qs.setMinute("0");
        qs.setHour("1");
        qs.setDayOfMonth("1");
        qs.setMonth("1");
        Schedule sched = new Schedule(qs);

        // current time is 1.6.2006 00:00.00
        GregorianCalendar start = new GregorianCalendar(2006, 5, 1, 0, 0, 0);

        // get next scheduled time
        GregorianCalendar act = sched.nextScheduledTime(start);

        // expected time is 1.1.2007 01:00.00
        GregorianCalendar exp = new GregorianCalendar(2007, 0, 1, 1, 0, 0);
        assertEquals(exp, act);
    }

    /**
     * Test for next scheduled half hour.
     * 
     * @throws ImplementationException
     *             If an error in the implementation occurred.
     * @throws SubscriptionControlsException
     *             If an error in the schedule occurred.
     */
    public void testNextScheduledHalfHour() throws ImplementationExceptionResponse,
            SubscriptionControlsExceptionResponse {
        // scheduled time is every half an hour
        // always at the top and the bottom of every hour
        QuerySchedule qs = new QuerySchedule();
        qs.setSecond("0");
        qs.setMinute("0,30");
        Schedule sched = new Schedule(qs);

        // current time is 1.6.2006 00:00.00
        GregorianCalendar start = new GregorianCalendar(2006, 5, 1, 0, 0, 0);

        // get next scheduled time
        GregorianCalendar act = sched.nextScheduledTime(start);

        // this is already a valid time!
        GregorianCalendar exp = (GregorianCalendar) start.clone();
        assertEquals(exp, act);

        // add a second to current time
        // current time is 1.6.2006 00:00.01
        start.add(Calendar.SECOND, 1);

        // get next scheduled time
        act = sched.nextScheduledTime(start);

        // expected time is 1.6.2006 00:30.00
        exp = new GregorianCalendar(2006, 5, 1, 0, 30, 0);
        assertEquals(exp, act);
    }

    /**
     * Test for next scheduled day of the week.
     * 
     * @throws ImplementationException
     *             If an error in the implementation occurred.
     * @throws SubscriptionControlsException
     *             If an error in the schedule occurred.
     */
    public void testNextScheduledDayOfWeek() throws ImplementationExceptionResponse,
            SubscriptionControlsExceptionResponse {
        // scheduled time is every July, at a Thursday, 17:15.59
        QuerySchedule qs = new QuerySchedule();
        qs.setMonth("7");
        qs.setDayOfMonth("[1-31],15,20"); // test duplicates!!
        qs.setDayOfWeek("4");
        qs.setHour("17");
        qs.setMinute("15");
        qs.setSecond("59");
        Schedule sched = new Schedule(qs);

        // current time is 14.7.2006 15:00.00
        GregorianCalendar start = new GregorianCalendar(2006, 6, 14, 15, 0, 0);

        // get next scheduled time
        GregorianCalendar act = sched.nextScheduledTime(start);

        // expected time is 20.7.2006 17:15.59
        GregorianCalendar exp = new GregorianCalendar(2006, 6, 20, 17, 15, 59);
        assertEquals(exp, act);
    }

    /**
     * Test for next scheduled leap year (Schaltjahr).
     * 
     * @throws ImplementationException
     *             If an error in the implementation occurred.
     * @throws SubscriptionControlsException
     *             If an error in the schedule occurred.
     */
    public void testNextScheduledLeapYear() throws ImplementationExceptionResponse,
            SubscriptionControlsExceptionResponse {
        // scheduled time is 29.2. 23:00.00 -> must be a leap year
        QuerySchedule qs = new QuerySchedule();
        qs.setMonth("2");
        qs.setDayOfMonth("29");
        qs.setHour("23");
        qs.setMinute("0");
        qs.setSecond("0");
        Schedule sched = new Schedule(qs);

        // current time is 1.1.2001
        GregorianCalendar start = new GregorianCalendar(2001, 0, 1);

        // get next scheduled time
        GregorianCalendar act = sched.nextScheduledTime(start);

        // expected time is 29.2.2004 23:00.00
        GregorianCalendar exp = new GregorianCalendar(2004, 1, 29, 23, 0, 0);
        assertEquals(exp, act);
    }

    /**
     * Test for next scheduled minute (used in the query tests!).
     * 
     * @throws ImplementationException
     *             If an error in the implementation occurred.
     * @throws SubscriptionControlsException
     *             If an error in the schedule occurred.
     */
    public void testNextScheduledMinute() throws ImplementationExceptionResponse, SubscriptionControlsExceptionResponse {
        // scheduled time is always at top of a minute
        QuerySchedule qs = new QuerySchedule();
        qs.setSecond("0");
        Schedule sched = new Schedule(qs);

        // current time is 8.2.2007 17:47.24
        GregorianCalendar start = new GregorianCalendar(2007, 1, 8, 17, 47, 24);

        // get next scheduled time
        GregorianCalendar act = sched.nextScheduledTime(start);

        // expected time is 8.2.2007 17:48.00
        GregorianCalendar exp = new GregorianCalendar(2007, 1, 8, 17, 48, 00);
        assertEquals(exp, act);
    }

    /**
     * Test for a complex next scheduled time.
     * 
     * @throws ImplementationException
     *             If an error in the implementation occurred.
     * @throws SubscriptionControlsException
     *             If an error in the schedule occurred.
     */
    public void testComplexNextScheduledTime() throws ImplementationExceptionResponse,
            SubscriptionControlsExceptionResponse {
        // scheduled time is 1., 10., 20., or 30. of a month,
        // at 07-11, 13-17, or 20 hours, 50.30 minutes
        QuerySchedule qs = new QuerySchedule();
        qs.setSecond("30");
        qs.setMinute("50");
        qs.setHour("[7-11],[13-17],20");
        qs.setDayOfMonth("1,10,20,30");
        Schedule sched = new Schedule(qs);

        // current time is 2.1.2010 08:30.00
        GregorianCalendar start = new GregorianCalendar(2010, 0, 2, 8, 30, 0);

        // get next scheduled time
        GregorianCalendar act = sched.nextScheduledTime(start);

        // expected time is 10.1. 07:50.30
        GregorianCalendar exp = new GregorianCalendar(2010, 0, 10, 7, 50, 30);
        assertEquals(exp, act);

        // add one second to scheduled time
        start = (GregorianCalendar) exp.clone();
        start.add(Calendar.SECOND, 1);

        // get next scheduled time
        act = sched.nextScheduledTime(start);

        // expected time is 10.1. 08:50.30
        exp = new GregorianCalendar(2010, 0, 10, 8, 50, 30);
        assertEquals(exp, act);

        // add some more time to scheduled time
        start = (GregorianCalendar) exp.clone();
        start.add(Calendar.HOUR, 3);
        start.add(Calendar.SECOND, 1);

        // get next scheduled time
        act = sched.nextScheduledTime(start);

        // expected time is 10.1. 13:50.30
        exp = new GregorianCalendar(2010, 0, 10, 13, 50, 30);
        assertEquals(exp, act);

        // add some more time to scheduled time
        start = (GregorianCalendar) exp.clone();
        start.add(Calendar.DAY_OF_MONTH, 1);

        // get next scheduled time
        act = sched.nextScheduledTime(start);

        // expected time is 20.1. 07:50.30
        exp = new GregorianCalendar(2010, 0, 20, 07, 50, 30);
        assertEquals(exp, act);
    }

    /**
     * Test for another complex next scheduled time.
     * 
     * @throws ImplementationException
     *             If an error in the implementation occurred.
     * @throws SubscriptionControlsException
     *             If an error in the schedule occurred.
     */
    public void testLeapYearDayOfWeekNextScheduledTime() throws ImplementationExceptionResponse,
            SubscriptionControlsExceptionResponse {

        QuerySchedule qs = new QuerySchedule();
        qs.setSecond("30");
        qs.setMinute("50");
        qs.setHour("[7-11],[13-17],20");
        qs.setDayOfMonth("29");
        qs.setMonth("2");
        qs.setDayOfWeek("1");
        Schedule schedule = new Schedule(qs);

        GregorianCalendar start = schedule.nextScheduledTime(new GregorianCalendar(2001, 0, 1, 0, 0, 0));
        GregorianCalendar result = schedule.nextScheduledTime(start);
        GregorianCalendar expected = new GregorianCalendar(2016, 1, 29, 7, 50, 30);
        assertEquals(result, expected);
    }

    /**
     * Test whether constructor throws an exception in case of an invalid
     * QuerySchedule.
     */
    public void testInvalidQuerySchedule() {

        QuerySchedule qsImpossibleSchedule = new QuerySchedule();
        qsImpossibleSchedule.setSecond("0");
        qsImpossibleSchedule.setMinute("0");
        qsImpossibleSchedule.setHour("0");
        qsImpossibleSchedule.setDayOfMonth("31");
        qsImpossibleSchedule.setMonth("6");
        try {
            new Schedule(qsImpossibleSchedule);
            fail("entering an invalid QuerySchedule should raise a SubscriptionControlsException");
        } catch (SubscriptionControlsExceptionResponse e) {
            // success
        }
    }
}
