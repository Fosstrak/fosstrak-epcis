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

import java.util.Date;
import java.util.GregorianCalendar;

import javax.management.Notification;
import javax.management.timer.Timer;

import org.accada.epcis.soapapi.ArrayOfString;
import org.accada.epcis.soapapi.EPCISServiceBindingStub;
import org.accada.epcis.soapapi.EPCglobalEPCISServiceLocator;
import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.Poll;
import org.accada.epcis.soapapi.QueryParam;
import org.accada.epcis.soapapi.QueryResults;
import org.apache.axis.types.URI;
import org.apache.log4j.Logger;

/**
 * Implementation of triggers. The Schedule checks every once in a while on the
 * trigger condition. If the trigger condition is met the query associated with
 * the subscription is executed. The checking frequency may be changed in the
 * properties.
 * 
 * @author Andrea Grössbauer
 */
public class QuerySubscriptionTriggered extends QuerySubscriptionScheduled {

    private static final long serialVersionUID = 8922829364406110575L;

    private static final Logger LOG = Logger.getLogger(QuerySubscriptionTriggered.class);

    private URI trigger;

    public QuerySubscriptionTriggered(String subscriptionID,
            QueryParam[] queryParams, URI dest, Boolean reportIfEmpty,
            GregorianCalendar initialRecordTime,
            GregorianCalendar lastTimeExecuted, String queryName, URI aTrigger,
            Schedule every10min) throws ImplementationException {
        super(subscriptionID, queryParams, dest, reportIfEmpty,
              initialRecordTime, lastTimeExecuted, every10min, queryName);
        this.trigger = aTrigger;
    }

    /**
     * {@inheritDoc} First checks on the trigger condition: if fulfilled then
     * execute Query.
     * 
     * @see org.accada.epcis.repository.QuerySubscriptionScheduled#handleNotification(javax.management.Notification,
     *      java.lang.Object)
     */
    @Override
    public void handleNotification(Notification pNotification, Object pHandback) {
        if (pHandback == null) {
            LOG.error("The timer stating the next scheduled query execution time is null!");
            return;
        }
        if (!doItAgain) {
            ((Timer) pHandback).stop();
        } else {
            try {
                LOG.debug("Checking trigger condition ...");
                String queryName = "SimpleEventQuery";
                String[] epcs = {
                    trigger.toString()
                };
                QueryParam[] queryParam = {
                        new QueryParam("MATCH_anyEPC", new ArrayOfString(epcs,
                                null)),
                        new QueryParam("GE_recordTime", initialRecordTime)
                };

                // initialize the query service
                EPCglobalEPCISServiceLocator queryLocator = new EPCglobalEPCISServiceLocator();
                queryLocator.setEPCglobalEPCISServicePortEndpointAddress(queryUrl);
                EPCISServiceBindingStub epcisQueryService = (EPCISServiceBindingStub) queryLocator.getEPCglobalEPCISServicePort();

                // send the query
                QueryResults results = epcisQueryService.poll(new Poll(
                        queryName, queryParam));
                if (results.getResultsBody().getEventList() != null) {
                    LOG.debug("Trigger condition fulfilled!");
                    LOG.debug("Executing subscribed query associated with trigger event ...");
                    super.executeQuery();
                    LOG.debug("Triggered query successfully executed!");
                }
            } catch (Exception e) {
                String msg = "An error occured while checking trigger condition for query with subscriptionID '"
                        + subscriptionID + "': " + e.getMessage();
                LOG.error(msg, e);
            }

            // determine next scheduled execution time
            Date nextSchedule;
            try {
                nextSchedule = schedule.nextScheduledTime().getTime();
                LOG.debug("Next scheduled time for the subscribed query with subscriptionID '"
                        + subscriptionID + "' is '" + nextSchedule + "'.");
                ((Timer) pHandback).addNotification("SubscriptionSchedule",
                        "Please do the query", (Timer) pHandback, nextSchedule);
            } catch (ImplementationException e) {
                String msg = "Next scheduled time for the subscribed query with ID '"
                        + getSubscriptionID()
                        + "' cannot be evaluated: "
                        + e.getReason();
                LOG.error(msg, e);
            }
        }
    }

    /**
     * @return The trigger URI.
     */
    public URI getTrigger() {
        return this.getTrigger();
    }

}
