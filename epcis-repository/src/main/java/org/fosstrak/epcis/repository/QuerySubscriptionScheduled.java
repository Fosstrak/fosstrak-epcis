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

package org.accada.epcis.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.timer.Timer;

import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.model.ImplementationException;
import org.accada.epcis.soap.model.QueryParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Special case of Subscription (from subscribe() on query interface) where the
 * subscription is scheduled.
 * 
 * @author Alain Remund
 * @author Arthur van Dorp
 */
public class QuerySubscriptionScheduled extends QuerySubscription implements NotificationListener, Serializable {

    private static final Log LOG = LogFactory.getLog(QuerySubscriptionScheduled.class);

    /**
     * Generated unique ID for serialization.
     */
    private static final long serialVersionUID = 6243380509125848077L;

    /**
     * Schedule indicating when subscription query is to be executed.
     */
    protected Schedule schedule;

    /**
     * Whether to continue with sending results.
     */
    protected Boolean doItAgain = true;

    /**
     * Constructor to be used when recreating from storage.
     * 
     * @param subscriptionID
     *            The subscription ID.
     * @param queryParams
     *            Query parameters.
     * @param dest
     *            The destination URI.
     * @param reportIfEmpty
     *            Whether to report when nothing changed.
     * @param initialRecordTime
     *            The initial record time.
     * @param lastTimeExecuted
     *            The last time the query was executed.
     * @param schedule
     *            The query Schedule.
     * @param queryName
     *            The query name.
     * @throws ImplementationException
     *             If the Scheduler could not be started.
     */
    public QuerySubscriptionScheduled(final String subscriptionID, final QueryParams queryParams, final String dest,
            final Boolean reportIfEmpty, final GregorianCalendar initialRecordTime,
            final GregorianCalendar lastTimeExecuted, final Schedule schedule, final String queryName)
            throws ImplementationExceptionResponse {
        super(subscriptionID, queryParams, dest, reportIfEmpty, initialRecordTime, lastTimeExecuted, queryName);
        this.schedule = schedule;
        if (LOG.isDebugEnabled()) {
            Date nextSchedule = schedule.nextScheduledTime().getTime();
            LOG.debug("Next scheduled time for the subscribed query is '" + nextSchedule + "'.");
            LOG.debug("URI to which to send results for the subscribed query is " + dest.toString());
        }
        startThread();
    }

    /**
     * Starts a Timer to get this query executed in specific time intervals.
     * 
     * @throws ImplementationException
     *             If the next scheduled date cannot be evaluated.
     */
    private void startThread() throws ImplementationExceptionResponse {
        Timer nextAction = new Timer();
        nextAction.addNotificationListener(this, null, nextAction);

        Date nextSchedule = schedule.nextScheduledTime().getTime();
        nextAction.addNotification("SubscriptionSchedule", "Please do the query", null, nextSchedule);
        nextAction.start();
    }

    /**
     * Stops the re-execution of the schedule. This method is called when a
     * subscribed query get's unsubscribed.
     */
    public void stopSubscription() {
        doItAgain = false;
    }

    /**
     * The Object has definitely been destroyed. This may take a while.
     */
    protected void finalize() {
        LOG.debug("A subscribed query has been garbage collected.");
    }

    /**
     * This method is handles a notification when the Timer for the schedule
     * times out.
     * 
     * @see javax.management.NotificationListener#handleNotification(javax.management.Notification,
     *      java.lang.Object)
     * @param pNotification
     *            The Notification.
     * @param pHandback
     *            A Timer stating the time when the Notification should be
     *            invoked.
     */
    public void handleNotification(final Notification pNotification, final Object pHandback) {
        if (pHandback == null) {
            LOG.error("The timer stating the next scheduled query execution time is null!");
            return;
        }

        if (!doItAgain) {
            ((Timer) pHandback).stop();
        } else {
            try {
                // execute the query
                executeQuery();

                // determine next scheduled execution time
                Date nextSchedule = schedule.nextScheduledTime().getTime();
                LOG.debug("Next scheduled time for the subscribed query is '" + nextSchedule + "'.");
                ((Timer) pHandback).addNotification("SubscriptionSchedule", "Please do the query", (Timer) pHandback,
                        nextSchedule);

            } catch (ImplementationExceptionResponse e) {
                String msg = "The next scheduled date for the subscribed query with ID '" + getSubscriptionID()
                        + "' cannot be evaluated: " + e.getMessage();
                LOG.error(msg, e);
            }
        }
    }

}
