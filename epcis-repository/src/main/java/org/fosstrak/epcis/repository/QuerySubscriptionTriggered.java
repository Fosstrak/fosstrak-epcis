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

import org.accada.epcis.soap.EPCISServicePortType;
import org.accada.epcis.soap.EPCglobalEPCISService;
import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.model.ArrayOfString;
import org.accada.epcis.soap.model.Poll;
import org.accada.epcis.soap.model.QueryParam;
import org.accada.epcis.soap.model.QueryParams;
import org.accada.epcis.soap.model.QueryResults;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    private static final Log LOG = LogFactory.getLog(QuerySubscriptionTriggered.class);

    private String trigger;

    public QuerySubscriptionTriggered(final String subscriptionID, final QueryParams queryParams, final String dest,
            final Boolean reportIfEmpty, final GregorianCalendar initialRecordTime,
            final GregorianCalendar lastTimeExecuted, final String queryName, final String trigger,
            final Schedule every10min) throws ImplementationExceptionResponse {
        super(subscriptionID, queryParams, dest, reportIfEmpty, initialRecordTime, lastTimeExecuted, every10min,
              queryName);
        this.trigger = trigger;
    }

    /**
     * {@inheritDoc} First checks on the trigger condition: if fulfilled then
     * execute Query.
     * 
     * @see org.accada.epcis.repository.QuerySubscriptionScheduled#handleNotification(javax.management.Notification,
     *      java.lang.Object)
     */
    @Override
    public void handleNotification(final Notification pNotification, final Object pHandback) {
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
                QueryParams params = new QueryParams();

                // add MATCH_anyEPC query param
                QueryParam param = new QueryParam();
                param.setName("MATCH_anyEPC");
                ArrayOfString strings = new ArrayOfString();
                strings.getString().add(trigger);
                param.setValue(strings);
                params.getParam().add(param);

                // add GE_recordTime query param
                param = new QueryParam();
                param.setName("GE_recordTime");
                param.setValue(initialRecordTime);
                params.getParam().add(param);

                // initialize the query service
                EPCglobalEPCISService s = new EPCglobalEPCISService();
                EPCISServicePortType epcisQueryService = s.getEPCglobalEPCISServicePort();

                // send the query
                Poll poll = new Poll();
                poll.setParams(params);
                poll.setQueryName(queryName);
                QueryResults results = epcisQueryService.poll(poll);
                if (results.getResultsBody().getEventList() != null) {
                    LOG.debug("Trigger condition fulfilled!");
                    LOG.debug("Executing subscribed query associated with trigger event ...");
                    super.executeQuery();
                    LOG.debug("Triggered query successfully executed!");
                }
            } catch (Exception e) {
                String msg = "An error occurred while checking trigger condition for query with subscriptionID '"
                        + subscriptionID + "': " + e.getMessage();
                LOG.error(msg, e);
            }

            // determine next scheduled execution time
            Date nextSchedule;
            try {
                nextSchedule = schedule.nextScheduledTime().getTime();
                LOG.debug("Next scheduled time for the subscribed query with subscriptionID '" + subscriptionID
                        + "' is '" + nextSchedule + "'.");
                ((Timer) pHandback).addNotification("SubscriptionSchedule", "Please do the query", (Timer) pHandback,
                        nextSchedule);
            } catch (ImplementationExceptionResponse e) {
                String msg = "Next scheduled time for the subscribed query with ID '" + getSubscriptionID()
                        + "' cannot be evaluated: " + e.getMessage();
                LOG.error(msg, e);
            }
        }
    }
}
