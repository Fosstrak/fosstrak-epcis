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

import javax.management.Notification;
import javax.management.timer.Timer;

import org.fosstrak.epcis.model.ArrayOfString;
import org.fosstrak.epcis.model.Poll;
import org.fosstrak.epcis.model.QueryParam;
import org.fosstrak.epcis.model.QueryParams;
import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.soap.ImplementationExceptionResponse;
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

    private static final long serialVersionUID = 658402150914797471L;

    private static final Log LOG = LogFactory.getLog(QuerySubscriptionTriggered.class);

    private String trigger;

    public QuerySubscriptionTriggered(final String subscriptionID, final QueryParams queryParams, final String dest,
            final Boolean reportIfEmpty, final Calendar initialRecordTime,
            final Calendar lastTimeExecuted, final String queryName, final String trigger,
            final Schedule every10min) throws ImplementationExceptionResponse {
        super(subscriptionID, queryParams, dest, reportIfEmpty, initialRecordTime, lastTimeExecuted, every10min,
              queryName);
        this.trigger = trigger;
    }

    /**
     * {@inheritDoc} First checks on the trigger condition: if fulfilled then
     * execute Query.
     * 
     * @see org.fosstrak.epcis.repository.query.QuerySubscriptionScheduled#handleNotification(javax.management.Notification,
     *      java.lang.Object)
     */
    @Override
    public void handleNotification(final Notification pNotification, final Object pHandback) {
        if (pHandback == null) {
            LOG.error("The timer stating the next scheduled query execution time is null!");
            return;
        }
        Timer timer = (Timer) pHandback;

        if (!doItAgain.booleanValue()) {
            timer.stop();
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

                // send the query
                Poll poll = new Poll();
                poll.setParams(params);
                poll.setQueryName(queryName);
                QueryResults results = executePoll(poll);
                if (results != null && results.getResultsBody() != null
                        && results.getResultsBody().getEventList() != null) {
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
            setNextScheduledExecutionTime(timer);
        }
    }
}
