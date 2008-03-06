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

package org.accada.epcis.repository.query;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.SubscriptionControlsExceptionResponse;
import org.accada.epcis.soap.model.ImplementationException;
import org.accada.epcis.soap.model.QuerySchedule;
import org.accada.epcis.soap.model.SubscriptionControlsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is a simple schedule which can return a "next scheduled time" after now
 * or a given time. It is meant to be instantiated with a EPCIS QuerySchedule
 * but could be extended to be used otherwise.
 * 
 * @author Arthur van Dorp
 * @author Marco Steybe
 */
public class Schedule implements Serializable {

    private static final Log LOG = LogFactory.getLog(Schedule.class);

    /**
     * Auto-generated UID for serialization.
     */
    private static final long serialVersionUID = -2930237937444822557L;

    /**
     * The valid second-values. Caveat: Empty means all seconds are valid.
     */
    private TreeSet<Integer> seconds = new TreeSet<Integer>();
    private TreeSet<Integer> minutes = new TreeSet<Integer>();
    private TreeSet<Integer> hours = new TreeSet<Integer>();
    private TreeSet<Integer> daysOfMonth = new TreeSet<Integer>();
    // months are 0-based
    private TreeSet<Integer> months = new TreeSet<Integer>();
    private TreeSet<Integer> daysOfWeek = new TreeSet<Integer>();

    /**
     * Parameterless constructor for use with serialization.
     */
    Schedule() {
    }

    /**
     * Constructor for creating a new schedule according to the parameters in
     * the given QuerySchedule 'schedule'.
     * 
     * @param schedule
     *            The EPCIS style schedule to be used for constructing this
     *            schedule.
     * @throws SubscriptionControlsException
     *             If invalid data is part of the Schedule.
     */
    public Schedule(final QuerySchedule schedule) throws SubscriptionControlsExceptionResponse {

        // ease handling of null values in the query schedule
        if (schedule.getSecond() == null) {
            schedule.setSecond("");
        }
        if (schedule.getMinute() == null) {
            schedule.setMinute("");
        }
        if (schedule.getHour() == null) {
            schedule.setHour("");
        }
        if (schedule.getDayOfMonth() == null) {
            schedule.setDayOfMonth("");
        }
        if (schedule.getMonth() == null) {
            schedule.setMonth("");
        }
        if (schedule.getDayOfWeek() == null) {
            schedule.setDayOfWeek("");
        }

        // retrieve the values from the given query schedule
        String[] second = schedule.getSecond().split(",");
        String[] minute = schedule.getMinute().split(",");
        String[] hour = schedule.getHour().split(",");
        String[] dayOfMonth = schedule.getDayOfMonth().split(",");
        String[] month = schedule.getMonth().split(",");
        String[] dayOfWeek = schedule.getDayOfWeek().split(",");

        // parse numbers and ranges, check and add values
        handleValues(second, "second", 0, 59);
        handleValues(minute, "minute", 0, 59);
        handleValues(hour, "hour", 0, 23);
        handleValues(dayOfMonth, "dayOfMonth", 1, 31);
        // months given in QuerySchedule are 1-based
        // but month values held in global variable "months" are 0-based!
        handleValues(month, "month", 1, 12);
        handleValues(dayOfWeek, "dayOfWeek", 1, 7);

        // check for invalid month/dayOfMonth combinations, e.g. 30.2., 31.4.
        if (!months.isEmpty()
                && (months.first() == months.last() && months.first().intValue() == 1 && (daysOfMonth.first().intValue() == 30 || daysOfMonth.first().intValue() == 31))) {
            throw new SubscriptionControlsExceptionResponse(
                    "Invalid query schedule: impossible month/dayOfMonth combination, e.g. February 30.");
        }
        if (!months.isEmpty()
                && daysOfMonth.first().intValue() == 31
                && !months.contains(Integer.valueOf(0)) // months w. 31 days are
                // always ok
                && !months.contains(Integer.valueOf(2)) && !months.contains(Integer.valueOf(4))
                && !months.contains(Integer.valueOf(6)) && !months.contains(Integer.valueOf(7))
                && !months.contains(Integer.valueOf(9)) && !months.contains(Integer.valueOf(11))) {
            throw new SubscriptionControlsExceptionResponse(
                    "Invalid query schedule: impossible month/dayOfMonth combination, e.g. April 31.");
        }
    }

    /**
     * Calculates the next scheduled time after now.
     * 
     * @return The next scheduled time after now.
     * @throws ImplementationException
     *             Almost any kind of error.
     */
    public GregorianCalendar nextScheduledTime() throws ImplementationExceptionResponse {
        GregorianCalendar cal = new GregorianCalendar();
        // start at the next second to avoid multiple results
        cal.add(SECOND, 1);
        return nextScheduledTime(cal);
    }

    /**
     * Calculates the next scheduled time after the given time. Algorithm idea:<br> -
     * start with biggest time unit (i.e. year) of the given time <br> - if the
     * time unit is valid (e.g. the time unit matches the <br>
     * scheduled time, this is implicitly true if the time in the <br>
     * schedule was omitted) *and* there exists a valid smaller time <br>
     * unit, *then* return this time unit <br> - do this recursively for all
     * time units <br> - month needs to be special cased because of dayOfWeek
     * 
     * @param time
     *            Time after which next scheduled time should be returned.
     * @return The next scheduled time after 'time'.
     * @throws ImplementationException
     *             Almost any kind of error.
     */
    public GregorianCalendar nextScheduledTime(final GregorianCalendar time) throws ImplementationExceptionResponse {
        GregorianCalendar nextSchedule = (GregorianCalendar) time.clone();
        // look at year
        while (!monthMadeValid(nextSchedule)) {
            nextSchedule.roll(YEAR, true);
            setFieldsToMinimum(nextSchedule, MONTH);

        }
        return nextSchedule;
    }

    /**
     * Returns true if the month and all smaller time units have been
     * successfully set to valid values.
     * 
     * @param nextSchedule
     *            The current candidate for the result.
     * @return True if month and smaller units successfully set to valid values.
     * @throws ImplementationException
     *             Almost any kind of error.
     */
    private boolean monthMadeValid(final GregorianCalendar nextSchedule) throws ImplementationExceptionResponse {
        // check if the month of the current time is valid, i.e. there is a
        // month value in the schedule equal to the month value of the current
        // time
        while (!months.isEmpty() && !months.contains(Integer.valueOf(nextSchedule.get(MONTH)))) {
            // no, month value of the current time is invalid
            // roll the month (set it to the next value)
            if (!setFieldToNextValidRoll(nextSchedule, MONTH, DAY_OF_MONTH)) {
                return false;
            }
        }
        // now we're in a valid month, make smaller units valid as well or go to
        // next month
        while (!dayMadeValid(nextSchedule)) {
            // no valid day for this month, try next
            if (!setFieldToNextValidRoll(nextSchedule, MONTH, DAY_OF_MONTH)) {
                return false;
            }
            // reset all smaller units to minimum
            if (!setFieldsToMinimum(nextSchedule, DAY_OF_MONTH)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the day and all smaller units have been successfully set
     * to valid values within the set month.
     * 
     * @param nextSchedule
     *            The current candidate for the result.
     * @return True if day and smaller units successfully set to valid values.
     * @throws ImplementationException
     *             Almost any kind of error.
     */
    private boolean dayMadeValid(final GregorianCalendar nextSchedule) throws ImplementationExceptionResponse {
        if (!daysOfMonth.contains(Integer.valueOf(nextSchedule.get(DAY_OF_MONTH))) && !daysOfMonth.isEmpty()) {
            if (!setFieldToNextValidRoll(nextSchedule, DAY_OF_MONTH, HOUR_OF_DAY)) {
                return false;
            }
        }

        // Check and make this also a valid day of week.
        while (!daysOfWeek.contains(Integer.valueOf(nextSchedule.get(DAY_OF_WEEK))) && !daysOfWeek.isEmpty()) {
            if (!setFieldToNextValidRoll(nextSchedule, DAY_OF_MONTH, HOUR_OF_DAY)) {
                return false;
            } else if (!daysOfWeek.contains(Integer.valueOf(nextSchedule.get(DAY_OF_WEEK)))) {
                dayMadeValid(nextSchedule);
            }
        }

        // Now we're in a valid day, make smaller units
        // valid as well or go to next day.
        while (!hourMadeValid(nextSchedule)) {
            // No valid hour for this day, try next day.
            if (!setFieldToNextValidRoll(nextSchedule, DAY_OF_MONTH, HOUR_OF_DAY)) {
                return false;
            }
            // Reset all smaller units to min.
            if (!setFieldsToMinimum(nextSchedule, HOUR_OF_DAY)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the hour and all smaller units have been successfully set
     * to valid values within the set day.
     * 
     * @param nextSchedule
     *            The current candidate for the result.
     * @return True if hour and smaller units successfully set to valid values.
     * @throws ImplementationException
     *             Almost any error.
     */
    private boolean hourMadeValid(final GregorianCalendar nextSchedule) throws ImplementationExceptionResponse {
        if (!hours.contains(Integer.valueOf(nextSchedule.get(HOUR_OF_DAY))) && !hours.isEmpty()) {
            if (!setFieldToNextValidRoll(nextSchedule, HOUR_OF_DAY, MINUTE)) {
                return false;
            }
        }

        // Now we're in a valid hour, make smaller units
        // valid as well or go to next hour.
        while (!minuteMadeValid(nextSchedule)) {
            // No valid minute for this hour, try next hour.
            if (!setFieldToNextValidRoll(nextSchedule, HOUR_OF_DAY, MINUTE)) {
                return false;
            }
            // Reset all smaller units to min.
            if (!setFieldsToMinimum(nextSchedule, MINUTE)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the minute and all smaller units have been successfully
     * set to valid values within the set hour.
     * 
     * @param nextSchedule
     *            The current candidate for the result.
     * @return True if minute and smaller units successfully set to valid
     *         values.
     * @throws ImplementationException
     *             Almost any error.
     */
    private boolean minuteMadeValid(final GregorianCalendar nextSchedule) throws ImplementationExceptionResponse {
        if (!minutes.contains(Integer.valueOf(nextSchedule.get(MINUTE))) && !minutes.isEmpty()) {

            if (!setFieldToNextValidRoll(nextSchedule, MINUTE, SECOND)) {
                return false;
            }
        }

        // Now we're in a valid minute, make smaller units
        // valid as well or go to next minute.
        while (!secondMadeValid(nextSchedule)) {
            // No valid second for this minute, try next minute.

            if (!setFieldToNextValidRoll(nextSchedule, MINUTE, SECOND)) {
                return false;
            }
            // Reset all smaller units to min.
            if (!setFieldToMinimum(nextSchedule, SECOND)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the second have been successfully set to valid values
     * within the set minute.
     * 
     * @param nextSchedule
     *            The current candidate for the result.
     * @return True if second successfully set to valid values.
     * @throws ImplementationException
     *             Almost any error.
     */
    private boolean secondMadeValid(final GregorianCalendar nextSchedule) throws ImplementationExceptionResponse {
        // check whether the second value of the current time is a valid
        // scheduled second
        if (!seconds.isEmpty() && !seconds.contains(Integer.valueOf(nextSchedule.get(SECOND)))) {
            // no current second is not scheduled
            // set is to the next scheduled second
            return setToNextScheduledValue(nextSchedule, SECOND);
        }
        return true;
    }

    /**
     * Sets the specified field of the given callendar to the next scheduled
     * value. Returns whether the new value has been set and is valid.
     * 
     * @param cal
     *            Calendar to adjust.
     * @param field
     *            Field to adjust.
     * @return Returns whether the new value has been set and is valid.
     * @throws ImplementationException
     *             Almost any error.
     */
    private boolean setToNextScheduledValue(final GregorianCalendar cal, final int field)
            throws ImplementationExceptionResponse {
        int next;
        TreeSet<Integer> vals = getValues(field);
        if (vals.isEmpty()) {
            next = cal.get(field) + 1;
        } else {
            try {
                // get next scheduled value which is bigger than current
                int incrValue = cal.get(field) + 1;
                next = vals.tailSet(new Integer(incrValue)).first().intValue();
            } catch (NoSuchElementException nse) {
                // there is no bigger scheduled value
                return false;
            }
        }
        if (next > cal.getActualMaximum(field) || next < cal.getActualMinimum(field)) {
            return false;
        }
        // all is well, set it to next
        cal.set(field, next);
        return true;
    }

    /**
     * Sets the field of a GregorianCalender to its next valid value, but first
     * sets all smaller fields to their minima and rolls the datefield is
     * defined as the next possible value according to the calendar type used
     * possibly superseded by the defined values in the schedule we have.
     * Returns whether the new value has been set and is valid.
     * 
     * @param cal
     *            Calendar to adjust.
     * @param field
     *            Field to adjust.<br>
     *            TODO: smallerField wouldn't be necessary.
     * @param smallerField
     *            Field from where on to minimize.
     * @return Returns whether the new value has been set and is valid.
     * @throws ImplementationException
     *             Almost any error.
     */
    private boolean setFieldToNextValidRoll(final GregorianCalendar cal, final int field, final int smallerField)
            throws ImplementationExceptionResponse {
        setFieldsToMinimum(cal, smallerField);
        return setToNextScheduledValue(cal, field);
    }

    /**
     * Sets the field of a GregorianCalender to its minimum, which is defined as
     * the minimal possible value according to the calendar type possibly
     * superseded by the defined values in the schedule we have. Returns whether
     * the new value has been set and is valid.
     * 
     * @param cal
     *            Calendar to adjust.
     * @param field
     *            Field to adjust.
     * @return Returns whether the new value has been set and is valid.
     * @throws ImplementationException
     *             Almost any error.
     */
    private boolean setFieldToMinimum(final GregorianCalendar cal, final int field)
            throws ImplementationExceptionResponse {
        int min;
        TreeSet<Integer> values = getValues(field);
        if (values.isEmpty()) {
            min = cal.getActualMinimum(field);
        } else {
            min = Math.max(values.first().intValue(), cal.getActualMinimum(field));
            if (min > cal.getActualMaximum(field)) {
                min = cal.getActualMaximum(field);
                if (!values.contains(Integer.valueOf(min)) || min < cal.getActualMinimum(field)
                        || min > cal.getActualMaximum(field)) {
                    return false;
                }
            }
        }
        cal.set(field, min);
        return true;
    }

    /**
     * Sets the given field of a GregorianCalender and all smaller fields (not
     * WEEK_OF_DAY) to their minimum, which is defined as the minimal possible
     * value according to the calendar type used possibly superseded by the
     * defined values in the schedule we have. Returns whether the new values
     * have been set and are all valid.
     * 
     * @param cal
     *            The Calendar instance to adjust.
     * @param largestField
     *            This field and smaller ones are reset
     * @return True if setting to min worked for all values.
     * @throws ImplementationException
     *             Various errors.
     */
    private boolean setFieldsToMinimum(final GregorianCalendar cal, final int largestField)
            throws ImplementationExceptionResponse {
        boolean result = true;
        switch (largestField) {
        case (MONTH):
            result = setFieldToMinimum(cal, MONTH) && result;
        case (DAY_OF_MONTH):
            result = setFieldToMinimum(cal, DAY_OF_MONTH) && result;
        case (HOUR_OF_DAY):
            result = setFieldToMinimum(cal, HOUR_OF_DAY) && result;
        case (MINUTE):
            result = setFieldToMinimum(cal, MINUTE) && result;
        case (SECOND):
            result = setFieldToMinimum(cal, SECOND) && result;
            break;
        default:
            String msg = "Invalid field: " + largestField;
            ImplementationExceptionResponse iex = new ImplementationExceptionResponse(msg);
            LOG.error(msg, iex);
            throw iex;

        }
        return result;
    }

    /**
     * Returns the values belonging to the given field of a GregorianCalendar.
     * 
     * @param field
     *            The field id of a GregorianCalendar.
     * @see GregorianCalendar
     * @return The corresponding schedule values.
     * @throws ImplementationException
     *             In case of a access to an unknown field.
     */
    private TreeSet<Integer> getValues(final int field) throws ImplementationExceptionResponse {
        switch (field) {
        case (DAY_OF_WEEK):
            return daysOfWeek;
        case (MONTH):
            return months;
        case (DAY_OF_MONTH):
            return daysOfMonth;
        case (HOUR_OF_DAY):
            return hours;
        case (MINUTE):
            return minutes;
        case (SECOND):
            return seconds;
        default:
            String msg = "Invalid field: " + field;
            ImplementationExceptionResponse iex = new ImplementationExceptionResponse(msg);
            LOG.error(msg, iex);
            throw iex;
        }
    }

    /**
     * Checks whether the given values, which are either numbers or ranges, are
     * valid (parsable as Integer) and adds the value to the correct set of
     * values (e.g. seconds).
     * 
     * @param values
     *            The numbers and ranges to be checked and added.
     * @param type
     *            The name of the schedule element, e.g. 'second'.
     * @param min
     *            The minimum allowed value.
     * @param max
     *            The maximum allowed value.
     * @throws SubscriptionControlsException
     *             If one of the given values is invalid, i.e. does not lie
     *             between the <code>min</code> and <code>max</code> value.
     */
    private void handleValues(final String[] values, final String type, final int min, final int max)
            throws SubscriptionControlsExceptionResponse {
        // we put values into this sorted set
        TreeSet<Integer> vals = new TreeSet<Integer>();
        for (String v : values) {
            try {
                if (v.startsWith("[")) {
                    // it's a range
                    String[] range = v.substring(1, v.length() - 1).split("-");
                    int start = Integer.parseInt(range[0]);
                    int end = Integer.parseInt(range[1]);
                    // check range
                    if (start < min || end > max || start > end) {
                        throw new SubscriptionControlsExceptionResponse("The value for '" + type
                                + "' is out of range in the query schedule.");
                    }
                    // add all values in the range
                    for (int value = start; value <= end; value++) {
                        vals = addValue(value, type, vals);
                    }
                } else if (!v.equals("")) {
                    // it's a single value
                    int value = Integer.parseInt(v);
                    // check value
                    if (value < min || value > max) {
                        throw new SubscriptionControlsExceptionResponse("The value for '" + type
                                + "' is out of range in the query schedule.");
                    }
                    // add value
                    vals = addValue(value, type, vals);
                }
            } catch (Exception e) {
                String msg = "The value '" + v + "' for parameter '" + type + "' is invalid in the query schedule.";
                LOG.info("USER ERROR: " + msg + e.getMessage());
                throw new SubscriptionControlsExceptionResponse(msg);
            }
        }

        if (type.equals("second")) {
            this.seconds = vals;
        } else if (type.equals("minute")) {
            this.minutes = vals;
        } else if (type.equals("hour")) {
            this.hours = vals;
        } else if (type.equals("dayOfMonth")) {
            this.daysOfMonth = vals;
        } else if (type.equals("month")) {
            this.months = vals;
        } else if (type.equals("dayOfWeek")) {
            this.daysOfWeek = vals;
        }
    }

    /**
     * Adds a schedule value to the given set of values with some special
     * treatment for 'month' and 'dayOfWeek'.
     * 
     * @param value
     *            The value to be added.
     * @param type
     *            The name of the schedule element, e.g. 'second'.
     * @param vals
     *            The set of values to which the value should be added.
     * @return The modified set of values.
     */
    private TreeSet<Integer> addValue(final int value, final String type, final TreeSet<Integer> vals) {
        if (type.equals("dayOfWeek")) {
            vals.add(new Integer((value % 7) + 1));
        } else if (type.equals("month")) {
            vals.add(new Integer(value - 1));
        } else {
            vals.add(new Integer(value));
        }
        return vals;
    }

    /**
     * @return The days of month from this schedule.
     */
    public TreeSet<Integer> getDaysOfMonth() {
        return daysOfMonth;
    }

    /**
     * @return The days of week from this schedule.
     */
    public TreeSet<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    /**
     * @return The hours from this schedule.
     */
    public TreeSet<Integer> getHours() {
        return hours;
    }

    /**
     * @return The minutes from this schedule.
     */
    public TreeSet<Integer> getMinutes() {
        return minutes;
    }

    /**
     * @return The months from this schedule.
     */
    public TreeSet<Integer> getMonths() {
        return months;
    }

    /**
     * @return The seconds from this schedule.
     */
    public TreeSet<Integer> getSeconds() {
        return seconds;
    }
}
