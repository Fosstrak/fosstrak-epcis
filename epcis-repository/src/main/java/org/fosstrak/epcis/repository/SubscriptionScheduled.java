/*
 * Copyright (c) 2006 ETH Zurich, Switzerland. All rights reserved. For copying
 * and distribution information, please see the file LICENSE.
 */

/**
 */
package org.accada.epcis.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.timer.Timer;

import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.QueryParam;
import org.apache.axis.types.URI;
import org.apache.log4j.Logger;

/**
 * Special case of Subscription (from subscribe() on query interface) where the
 * subscription is scheduled.
 * 
 * @author Alain Remund, Arthur van Dorp
 */
public class SubscriptionScheduled extends Subscription implements
        NotificationListener, Serializable {

    private static final Logger LOG = Logger.getLogger(SubscriptionScheduled.class);

    /**
     * Generated unique ID for serialization.
     */
    private static final long serialVersionUID = 6243380509125848077L;

    /**
     * Schedule indicating when subscription query is to be executed.
     */
    private Schedule schedule;

    /**
     * Whether to continue with sending results.
     */
    private Boolean doItAgain = true;

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
    public SubscriptionScheduled(final String subscriptionID,
            final QueryParam[] queryParams, final URI dest,
            final Boolean reportIfEmpty,
            final GregorianCalendar initialRecordTime,
            final GregorianCalendar lastTimeExecuted, final Schedule schedule,
            final String queryName) throws ImplementationException {
        super(subscriptionID, queryParams, dest, reportIfEmpty,
              initialRecordTime, lastTimeExecuted, queryName);
        this.schedule = schedule;
        if (LOG.isDebugEnabled()) {
            Date nextSchedule = schedule.nextScheduledTime().getTime();
            LOG.debug("Next scheduled time for the subscribed query is '"
                    + nextSchedule + "'.");
        }
        startThread();
    }

    /**
     * Starts a Timer to get this query executed in specific time intervals.
     * 
     * @throws ImplementationException
     *             If the next scheduled date cannot be evaluated.
     */
    private void startThread() throws ImplementationException {
        Timer nextAction = new Timer();
        nextAction.addNotificationListener(this, null, nextAction);

        Date nextSchedule = schedule.nextScheduledTime().getTime();
        nextAction.addNotification("SubscriptionSchedule",
                "Please do the query", null, nextSchedule);
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
    public void handleNotification(final Notification pNotification,
            final Object pHandback) {
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
                LOG.debug("Next scheduled time for the subscribed query is '"
                        + nextSchedule + "'.");
                ((Timer) pHandback).addNotification("SubscriptionSchedule",
                        "Please do the query", (Timer) pHandback, nextSchedule);

            } catch (ImplementationException e) {
                String msg = "The next scheduled date for the subscribed query with ID '"
                        + subscriptionID
                        + "' cannot be evaluated: "
                        + e.getReason();
                LOG.error(msg, e);
            }
        }
    }

}
