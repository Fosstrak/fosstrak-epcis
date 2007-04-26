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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.accada.epcis.soapapi.AggregationEventType;
import org.accada.epcis.soapapi.EPCISException;
import org.accada.epcis.soapapi.EPCISQueryBodyType;
import org.accada.epcis.soapapi.EPCISQueryDocumentType;
import org.accada.epcis.soapapi.EPCISServiceBindingStub;
import org.accada.epcis.soapapi.EPCglobalEPCISServiceLocator;
import org.accada.epcis.soapapi.EventListType;
import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.ObjectEventType;
import org.accada.epcis.soapapi.Poll;
import org.accada.epcis.soapapi.QuantityEventType;
import org.accada.epcis.soapapi.QueryParam;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.soapapi.TransactionEventType;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.NullAttributes;
import org.apache.axis.types.URI;
import org.apache.log4j.Logger;

/**
 * Implements a subscription to a query. Created upon using subscribe() on the
 * querying interface side.
 * 
 * @author Alain Remund
 * @author Arthur van Dorp
 * @author Marco Steybe
 */
public class QuerySubscription implements Serializable {

    /**
     * Generated ID for serialization. Adapt if you change this class in a
     * backwards incompatible way.
     */
    private static final long serialVersionUID = -401176555052383495L;

    private static final Logger LOG = Logger.getLogger(QuerySubscription.class);

    /**
     * SubscriptionID.
     */
    protected String subscriptionID;

    /**
     * Query parameters.
     */
    private QueryParam[] queryParams;

    /**
     * Destination URI to send results to.
     */
    protected URI dest;

    /**
     * Initial record time.
     */
    protected GregorianCalendar initialRecordTime;

    /**
     * Whether to send results if nothing new available.
     */
    protected Boolean reportIfEmpty;

    /**
     * queryName.
     */
    protected String queryName;

    /**
     * The URL at which the query service is available.
     */
    protected String queryUrl = null;

    /**
     * Last time the query got executed. Used to restrict results to new ones.
     */
    private GregorianCalendar lastTimeExecuted;

    /**
     * Constructor to be used when recreating from storage.
     * 
     * @param subscriptionID
     *            subscriptionID.
     * @param queryParams
     *            Query parameters.
     * @param dest
     *            Destination URI.
     * @param reportIfEmpty
     *            Whether to report when nothing changed.
     * @param initialRecordTime
     *            Time from when on events should be reported on first
     *            execution.
     * @param lastTimeExecuted
     *            Last time the query got executed.
     * @param queryName
     *            queryName.
     */
    public QuerySubscription(final String subscriptionID,
            final QueryParam[] queryParams, final URI dest,
            final Boolean reportIfEmpty,
            final GregorianCalendar initialRecordTime,
            final GregorianCalendar lastTimeExecuted, final String queryName) {
        LOG.debug("Constructing Query Subscription with ID '" + subscriptionID
                + "'.");

        this.queryParams = queryParams;
        this.subscriptionID = subscriptionID;
        this.dest = dest;
        this.initialRecordTime = initialRecordTime;
        this.reportIfEmpty = reportIfEmpty;
        this.queryName = queryName;
        this.lastTimeExecuted = lastTimeExecuted;

        // initialize the query URL
        MessageContext msgContext = MessageContext.getCurrentContext();
        this.queryUrl = (String) msgContext.getProperty(MessageContext.TRANS_URL);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initial record time is '" + lastTimeExecuted.getTime()
                    + "'.");
            LOG.debug("URL of the query service is '" + queryUrl + "'.");
        }

        // update/add GE_recordTime restriction to query params (we only need to
        // return results not previously returned!)
        updateRecordTime(queryParams, initialRecordTime);
    }

    /**
     * Updates or adds the 'GE_recordTime' query parameter in the given query
     * parameter array and sets its value to the given time.
     * 
     * @param queryParams
     *            The (old) query parameter array.
     * @param initialRecordTime
     *            The time to which the 'GE_recordTime' parameter will be
     *            updated.
     */
    private void updateRecordTime(QueryParam[] queryParams,
            GregorianCalendar initialRecordTime) {
        // update or add GE_recordTime restriction
        boolean foundRecordTime = false;
        List<QueryParam> tempParams = Arrays.asList(queryParams);
        for (QueryParam p : tempParams) {
            if (p.getName().equalsIgnoreCase("GE_recordTime")) {
                LOG.debug("Updating query parameter 'GE_recordTime' with value '"
                        + initialRecordTime.getTime() + "'.");
                p.setValue(initialRecordTime);
                foundRecordTime = true;
                break;
            }
        }
        this.queryParams = tempParams.toArray(queryParams);
        if (!foundRecordTime) {
            List<QueryParam> arrayList = new ArrayList<QueryParam>();
            arrayList.addAll(tempParams);
            LOG.debug("Adding query parameter 'GE_recordTime' with value '"
                    + initialRecordTime.getTime() + "'.");
            QueryParam newParam = new QueryParam();
            newParam.setName("GE_recordTime");
            newParam.setValue(initialRecordTime);
            arrayList.add(newParam);
            this.queryParams = arrayList.toArray(queryParams);
        }
    }

    /**
     * Runs the query assigned to this subscription. Advances lastTimeExecuted.
     */
    public void executeQuery() {
        LOG.debug("------------------------------------");
        LOG.debug("It's time to run a subscribed query.");

        // get new lastTimeExecuted (must be <= to time when query is executed,
        // otherwise we loose results)
        GregorianCalendar cal = new GregorianCalendar();
        int offset = TimeZone.getDefault().getRawOffset()
                + TimeZone.getDefault().getDSTSavings();
        cal.add(Calendar.MILLISECOND, offset);
        this.lastTimeExecuted = cal;

        // poll the query
        Poll poll = new Poll(queryName, queryParams);
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Executing subscribed query '" + subscriptionID
                        + "' which has " + queryParams.length + " parameters:");
                for (int i = 0; i < queryParams.length; i++) {
                    LOG.debug(" param name:  " + queryParams[i].getName());
                    Object val = queryParams[i].getValue();
                    if (val instanceof GregorianCalendar) {
                        LOG.debug(" param value: "
                                + ((GregorianCalendar) val).getTime());
                    } else {
                        LOG.debug(" param value: " + val);
                    }
                }
            }
            QueryResults result = null;
            try {
                // initialize the query service
                EPCglobalEPCISServiceLocator queryLocator = new EPCglobalEPCISServiceLocator();
                queryLocator.setEPCglobalEPCISServicePortEndpointAddress(queryUrl);
                EPCISServiceBindingStub epcisQueryService = (EPCISServiceBindingStub) queryLocator.getEPCglobalEPCISServicePort();

                // send the query
                result = epcisQueryService.poll(poll);
            } catch (QueryTooLargeException e) {
                // send exception back to client
                EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
                e.setSubscriptionID(subscriptionID);
                queryBody.setQueryTooLargeException(e);
                serializeAndSend(queryBody);
                return;
            } catch (ImplementationException e) {
                // send exception back to client
                EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
                e.setSubscriptionID(subscriptionID);
                queryBody.setImplementationException(e);
                serializeAndSend(queryBody);
                return;
            } catch (EPCISException e) {
                // log exception
                String msg = "An error executing a subscribed query occured: "
                        + e.getReason();
                LOG.error(msg, e);
            } catch (ServiceException e) {
                String msg = "An error retrieving the EPCIS query service occured: "
                        + e.getMessage();
                LOG.error(msg, e);
            }
            result.setSubscriptionID(subscriptionID);
            EventListType eventList = result.getResultsBody().getEventList();

            // check if we have an empty result list
            boolean isEmpty = false;
            if (eventList == null) {
                isEmpty = true;
            } else {
                AggregationEventType[] aggrEvents = eventList.getAggregationEvent();
                ObjectEventType[] objEvents = eventList.getObjectEvent();
                QuantityEventType[] quantEvents = eventList.getQuantityEvent();
                TransactionEventType[] transEvents = eventList.getTransactionEvent();
                if (aggrEvents == null && objEvents == null
                        && quantEvents == null && transEvents == null) {
                    isEmpty = true;
                } else {
                    int nofAggrEvents = (aggrEvents != null)
                            ? aggrEvents.length
                            : 0;
                    int nofObjEvents = (objEvents != null)
                            ? objEvents.length
                            : 0;
                    int nofQuantEvents = (quantEvents != null)
                            ? quantEvents.length
                            : 0;
                    int nofTransEvents = (transEvents != null)
                            ? transEvents.length
                            : 0;
                    if (nofAggrEvents == 0 && nofObjEvents == 0
                            && nofQuantEvents == 0 && nofTransEvents == 0) {
                        isEmpty = true;
                    }
                    LOG.debug("Subscribed query with ID '" + subscriptionID
                            + "' contains " + nofAggrEvents
                            + " AggregationEvents, " + nofObjEvents
                            + " ObjectEvents, " + nofQuantEvents
                            + " QuantityEvents, " + nofTransEvents
                            + " TransactionEvents.");
                }
            }

            if (!reportIfEmpty && isEmpty) {
                LOG.debug("Query returned no results, nothing to report.");
                return;
            }

            EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
            queryBody.setQueryResults(result);

            serializeAndSend(queryBody);

        } catch (IOException e) {
            String msg = "An error opening a connection to '" + dest
                    + "' or serializing and sending contents occured: "
                    + e.getMessage();
            LOG.error(msg, e);
        }

        // update query params with new lastTimeExecuted
        updateRecordTime(queryParams, lastTimeExecuted);
    }

    /**
     * Serializes and sends the query response back to the client.
     * 
     * @param body
     *            The body of the EPCISQueryDocumentType.
     * @throws IOException
     *             If a serialization or sending error occured.
     */
    protected void serializeAndSend(final EPCISQueryBodyType body)
            throws IOException {
        EPCISQueryDocumentType queryDoc = new EPCISQueryDocumentType();
        queryDoc.setCreationDate(new GregorianCalendar());
        queryDoc.setEPCISBody(body);

        // serialize the response
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);
        SerializationContext serContext = new SerializationContext(writer);
        QName queryDocXMLType = EPCISQueryDocumentType.getTypeDesc().getXmlType();
        serContext.setWriteXMLType(queryDocXMLType);
        serContext.serialize(queryDocXMLType, new NullAttributes(), queryDoc,
                queryDocXMLType, EPCISQueryDocumentType.class, false, true);
        writer.flush();
        String data = baos.toString();

        // set up connection and send data to given destination
        URL serviceUrl = new URL(dest.toString());
        LOG.debug("Sending results of subscribed query with ID '"
                + subscriptionID + "' to '" + serviceUrl + "'.");
        int responseCode = sendData(serviceUrl, data);
        LOG.debug("Response " + responseCode);
    }

    /**
     * Sends the given data String to the specified URL.
     * 
     * @param url
     *            The URL to send the data to.
     * @param data
     *            The data to send.
     * @return The HTTP response code.
     * @throws IOException
     *             If a communication error occured.
     */
    private int sendData(final URL url, final String data) throws IOException {
        data.concat("\n");
        // setup connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "text/plain");
        connection.setRequestProperty("Content-length", "" + data.length());
        // connection.setDoInput(true);
        connection.setDoOutput(true);

        // encode data
        // CharBuffer buf = CharBuffer.wrap(data);
        // Charset charset = Charset.forName("UTF-8");
        // CharsetEncoder encoder = charset.newEncoder();

        // send data
        OutputStreamWriter out = new OutputStreamWriter(
                connection.getOutputStream());
        LOG.debug("Sending data: " + data);
        out.write(data);
        out.flush();

        // get response
        // connection.getInputStream();
        int responseCode = connection.getResponseCode();

        // disconnect
        connection.disconnect();

        return responseCode;
    }

    /**
     * @return The initial record time.
     */
    public GregorianCalendar getInitialRecordTime() {
        return initialRecordTime;
    }

    /**
     * @return the subscriptionID
     */
    public String getSubscriptionID() {
        return subscriptionID;
    }
}
