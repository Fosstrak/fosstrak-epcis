package org.accada.epcis.repository;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.QuerySchedule;
import org.accada.epcis.soapapi.SubscriptionControlsException;

/**
 * Tests for class Schedule.
 * 
 * @author Arthur van Dorp
 */
public class ScheduleTest extends TestCase {

    private Schedule simpleSchedule;
    private Schedule usualSchedule;
    private Schedule dayOfWeekSchedule;
    private Schedule leapYearSchedule;

    private GregorianCalendar firstJune2006;
    private GregorianCalendar firstJanuary20070100;
    private GregorianCalendar dayOfWeekStart;
    private GregorianCalendar leapYearStart;

    public void setUp() throws SubscriptionControlsException {
        // Basic test.
        QuerySchedule qsSimple = new QuerySchedule();
        qsSimple.setSecond("0");
        qsSimple.setMinute("0");
        qsSimple.setHour("1");
        qsSimple.setDayOfMonth("1");
        qsSimple.setMonth("1");
        simpleSchedule = new Schedule(qsSimple);

        // Normal use case
        QuerySchedule qsUsual = new QuerySchedule();
        qsUsual.setSecond("0");
        qsUsual.setMinute("0,30");
        usualSchedule = new Schedule(qsUsual);

        // leap years. Start in 2001 and look for 29th in 2004.
        QuerySchedule qsLeapYear = new QuerySchedule();
        qsLeapYear.setSecond("0");
        qsLeapYear.setMinute("0");
        qsLeapYear.setHour("23");
        qsLeapYear.setDayOfMonth("29");
        qsLeapYear.setMonth("2");
        leapYearSchedule = new Schedule(qsLeapYear);
        leapYearStart = new GregorianCalendar(2001, 0, 1);

        // impossibleSchedule // 30/31 of february
        // summerTimeSchedule
        // winterTimeSchedule
        firstJune2006 = new GregorianCalendar(2006, 5, 1, 0, 0, 0);
        firstJanuary20070100 = new GregorianCalendar(2007, 0, 1, 1, 0, 0);
    }

    /**
     * Test method for 'org.autoidlabs.epcnet.epcisrep.querying2.
     * accadaschedule.Schedule.nextScheduledTime(GregorianCalendar)' Mainly test
     * whether the returned Calendars are correct.
     * 
     * @throws ImplementationException
     */
    public void testSimpleNextScheduledTime()
            throws ImplementationException {
        GregorianCalendar result = simpleSchedule.nextScheduledTime(firstJune2006);
        assertEquals(firstJanuary20070100, result);
    }

    public void testUsualNextScheduledTime()
            throws ImplementationException {
        GregorianCalendar result = usualSchedule.nextScheduledTime(firstJune2006);
        // It's already a valid time.
        assertEquals(firstJune2006, result);
    }

    public void testDayOfWeekNextScheduledTime()
            throws ImplementationException, SubscriptionControlsException {
        // Check for proper use of weekdays
        QuerySchedule qsDayOfWeek = new QuerySchedule();
        qsDayOfWeek.setSecond("59");
        qsDayOfWeek.setMinute("15");
        qsDayOfWeek.setHour("17");
        qsDayOfWeek.setDayOfMonth("[1-31],15,20"); // Check also for
                                                    // duplicates.
        qsDayOfWeek.setMonth("7");
        qsDayOfWeek.setDayOfWeek("4");
        dayOfWeekSchedule = new Schedule(qsDayOfWeek);
        dayOfWeekStart = new GregorianCalendar(2006, 6, 14, 15, 0, 0);
        GregorianCalendar result = dayOfWeekSchedule.nextScheduledTime(dayOfWeekStart);
        GregorianCalendar expected = new GregorianCalendar(2006, 6, 20, 17, 15,
                59);
        assertEquals(expected, result);
    }

    public void testLeapYearNextScheduledTime()
            throws ImplementationException {
        GregorianCalendar result = leapYearSchedule.nextScheduledTime(leapYearStart);
        GregorianCalendar expected = new GregorianCalendar(2004, 1, 29, 23, 0,
                0);
        assertEquals(expected, result);
    }

    public void testNextScheduledMinute()
            throws ImplementationException, SubscriptionControlsException {

        QuerySchedule qs = new QuerySchedule();
        qs.setSecond("0");
        Schedule schedule = new Schedule(qs);

        GregorianCalendar time = new GregorianCalendar(2007, 2, 8, 17, 47, 24);
        GregorianCalendar act = schedule.nextScheduledTime(time);
        GregorianCalendar exp = new GregorianCalendar(2007, 2, 8, 17, 48, 00);
        assertEquals(act, exp);
    }

    public void testComplexNextScheduledTime()
            throws ImplementationException, SubscriptionControlsException {

        QuerySchedule qs = new QuerySchedule();
        qs.setSecond("30");
        qs.setMinute("50");
        qs.setHour("[7-11],[13-17],20");
        qs.setDayOfMonth("1,10,20,30");
        Schedule schedule = new Schedule(qs);

        GregorianCalendar time = new GregorianCalendar(2010, 0, 2, 8, 30, 0);
        GregorianCalendar result1 = schedule.nextScheduledTime(time);
        GregorianCalendar expected1 = new GregorianCalendar(2010, 0, 10, 7, 50,
                30);
        assertEquals(result1, expected1);

        time = (GregorianCalendar) result1.clone();
        time.add(Calendar.SECOND, 1);
        GregorianCalendar result2 = schedule.nextScheduledTime(time);
        GregorianCalendar expected2 = new GregorianCalendar(2010, 0, 10, 8, 50,
                30);
        assertEquals(result2, expected2);

        time = (GregorianCalendar) result2.clone();
        time.add(Calendar.HOUR, 10);
        GregorianCalendar result3 = schedule.nextScheduledTime(time);
        GregorianCalendar expected3 = new GregorianCalendar(2010, 0, 10, 20, 50,
                30);
        assertEquals(result3, expected3);
    }

    public void testLeapYearDayOfWeekNextScheduledTime()
            throws ImplementationException, SubscriptionControlsException {

        QuerySchedule qs = new QuerySchedule();
        qs.setSecond("30");
        qs.setMinute("50");
        qs.setHour("[7-11],[13-17],20");
        qs.setDayOfMonth("29");
        qs.setMonth("2");
        qs.setDayOfWeek("1");
        Schedule schedule = new Schedule(qs);
        
        GregorianCalendar start = schedule.nextScheduledTime(new GregorianCalendar(
                2001, 0, 1, 0, 0, 0));
        GregorianCalendar result = schedule.nextScheduledTime(start);
        GregorianCalendar expected = new GregorianCalendar(2016, 1, 29, 7, 50,
                30);
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
        } catch (SubscriptionControlsException e) {
            // success
        }
    }

    /**
     * Prints the date and time of the given Calendar.
     * 
     * @param cal
     *            The calendar to print.
     */
    private void printDateTime(GregorianCalendar cal) {
        System.out.println("Year: "
                + cal.get(Calendar.YEAR)
                // Calendar starts months with 0
                + " Month: " + (cal.get(Calendar.MONTH) + 1) + " Day: "
                + cal.get(Calendar.DAY_OF_MONTH) + " Hour: "
                + cal.get(Calendar.HOUR_OF_DAY) + " Minute: "
                + cal.get(Calendar.MINUTE) + " Second: "
                + cal.get(Calendar.SECOND)
                // Calendar week days start on sunday
                + " Weekday: " + (cal.get(Calendar.DAY_OF_WEEK) - 1));
    }

}
