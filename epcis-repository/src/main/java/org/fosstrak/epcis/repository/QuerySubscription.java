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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.accada.epcis.soap.EPCISServicePortType;
import org.accada.epcis.soap.EPCglobalEPCISService;
import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.QueryTooLargeExceptionResponse;
import org.accada.epcis.soap.model.EPCISQueryBodyType;
import org.accada.epcis.soap.model.EPCISQueryDocumentType;
import org.accada.epcis.soap.model.EventListType;
import org.accada.epcis.soap.model.Poll;
import org.accada.epcis.soap.model.QueryParam;
import org.accada.epcis.soap.model.QueryParams;
import org.accada.epcis.soap.model.QueryResults;
import org.accada.epcis.soap.model.QueryTooLargeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    private static final Log LOG = LogFactory.getLog(QuerySubscription.class);

    /**
     * SubscriptionID.
     */
    protected String subscriptionID;

    /**
     * Destination URI to send results to.
     */
    protected String dest;

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
     * Query parameters.
     */
    private QueryParams queryParams;

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
    public QuerySubscription(final String subscriptionID, final QueryParams queryParams, final String dest,
            final Boolean reportIfEmpty, final GregorianCalendar initialRecordTime,
            final GregorianCalendar lastTimeExecuted, final String queryName) {
        LOG.debug("Constructing Query Subscription with ID '" + subscriptionID + "'.");

        this.queryParams = queryParams;
        this.subscriptionID = subscriptionID;
        this.dest = dest;
        this.initialRecordTime = initialRecordTime;
        this.reportIfEmpty = reportIfEmpty;
        this.queryName = queryName;
        this.lastTimeExecuted = lastTimeExecuted;

        // update/add GE_recordTime restriction to query params (we only need to
        // return results not previously returned!)
        updateRecordTime(queryParams, initialRecordTime);
    }

    /**
     * Updates the subscription in the database. This is required in order to
     * correctly re-initialize the subscriptions, especially the
     * lastTimeExecuted field, after a context restart.
     * 
     * @param lastTimeExecuted
     *            The new lastTimeExecuted.
     */
    private void updateSubscription(final GregorianCalendar lastTimeExecuted) {

        try {
            // open a database connection
            Context initContext = new InitialContext();
            Context env = (Context) initContext.lookup("java:comp/env");
            DataSource db = (DataSource) env.lookup("jdbc/EPCISDB");
            Connection dbconnection = db.getConnection();

            // update the subscription in the database
            String update = "UPDATE subscription SET lastexecuted=(?), params=(?)" + " WHERE subscriptionid=(?);";
            PreparedStatement stmt = dbconnection.prepareStatement(update);
            LOG.debug("QUERY: " + update);
            Timestamp ts = new Timestamp(lastTimeExecuted.getTimeInMillis());
            String time = ts.toString();
            stmt.setString(1, time);
            LOG.debug("       query param 1: " + time);

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(outStream);
            out.writeObject(queryParams);
            ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
            stmt.setBinaryStream(2, inStream, inStream.available());
            LOG.debug("       query param 2: [" + inStream.available() + " bytes]");

            stmt.setString(3, subscriptionID);
            LOG.debug("       query param 3: " + subscriptionID);

            stmt.executeUpdate();

            // close the database connection
            dbconnection.close();
        } catch (SQLException e) {
            String msg = "An SQL error occurred while updating the subscriptions in the database.";
            LOG.error(msg, e);
        } catch (IOException e) {
            String msg = "Unable to update the subscription in the database: " + e.getMessage();
            LOG.error(msg, e);
        } catch (NamingException e) {
            String msg = "Unable to read configuration, check META-INF/context.xml for Resource 'jdbc/EPCISDB'.";
            LOG.error(msg, e);
        }
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
    private void updateRecordTime(final QueryParams queryParams, final GregorianCalendar initialRecordTime) {
        // update or add GE_recordTime restriction
        boolean foundRecordTime = false;
        for (QueryParam p : this.queryParams.getParam()) {
            if (p.getName().equalsIgnoreCase("GE_recordTime")) {
                LOG.debug("Updating query parameter 'GE_recordTime' with value '" + initialRecordTime.getTime() + "'.");
                p.setValue(initialRecordTime);
                foundRecordTime = true;
                break;
            }
        }
        if (!foundRecordTime) {
            LOG.debug("Adding query parameter 'GE_recordTime' with value '" + initialRecordTime.getTime() + "'.");
            QueryParam newParam = new QueryParam();
            newParam.setName("GE_recordTime");
            newParam.setValue(initialRecordTime);
            this.queryParams.getParam().add(newParam);
        }

        // update the subscription in the db
        updateSubscription(initialRecordTime);
    }

    /**
     * Runs the query assigned to this subscription. Advances lastTimeExecuted.
     */
    public void executeQuery() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("--------------------------------------------");
            LOG.debug("Executing subscribed query '" + subscriptionID + "' which has " + queryParams.getParam().size()
                    + " parameters:");
            for (QueryParam p : queryParams.getParam()) {
                LOG.debug(" param name:  " + p.getName());
                Object val = p.getValue();
                if (val instanceof GregorianCalendar) {
                    LOG.debug(" param value: " + ((GregorianCalendar) val).getTime());
                } else {
                    LOG.debug(" param value: " + val);
                }
            }
        }

        // poll the query
        Poll poll = new Poll();
        poll.setQueryName(queryName);
        poll.setParams(queryParams);
        try {
            QueryResults result = null;
            try {
                // initialize the query service
                EPCglobalEPCISService s = new EPCglobalEPCISService();
                EPCISServicePortType epcisQueryService = s.getEPCglobalEPCISServicePort();

                // send the query and get current time
                GregorianCalendar cal = new GregorianCalendar();
                result = epcisQueryService.poll(poll);

                // set new lastTimeExecuted (must be <= to time when query is
                // executed, otherwise we loose results)
                int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
                cal.add(Calendar.MILLISECOND, offset);
                cal.add(Calendar.SECOND, 1);
                this.lastTimeExecuted = cal;
            } catch (QueryTooLargeExceptionResponse e) {
                // send exception back to client
                EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
                QueryTooLargeException qtle = new QueryTooLargeException();
                qtle.setQueryName(queryName);
                qtle.setSubscriptionID(subscriptionID);
                qtle.setReason(e.getMessage());
                queryBody.setQueryTooLargeException(qtle);
                serializeAndSend(queryBody);
                return;
            } catch (ImplementationExceptionResponse e) {
                // send exception back to client
                EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
                e.getFaultInfo().setSubscriptionID(subscriptionID);
                queryBody.setImplementationException(e.getFaultInfo());
                serializeAndSend(queryBody);
                return;
            } catch (Exception e) {
                String msg = "An error retrieving the EPCIS query service occurred: " + e.getMessage();
                LOG.error(msg, e);
            }
            result.setSubscriptionID(subscriptionID);
            EventListType eventList = result.getResultsBody().getEventList();

            // check if we have an empty result list
            boolean isEmpty = false;
            isEmpty = (eventList == null) ? true
                    : eventList.getObjectEventOrAggregationEventOrQuantityEvent().isEmpty();
            if (!reportIfEmpty && isEmpty) {
                LOG.debug("Query returned no results, nothing to report.");
                return;
            }

            EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
            queryBody.setQueryResults(result);

            serializeAndSend(queryBody);

        } catch (IOException e) {
            String msg = "An error opening a connection to '" + dest
                    + "' or serializing and sending contents occurred: " + e.getMessage();
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
     *             If a serialization or sending error occurred.
     */
    protected void serializeAndSend(final EPCISQueryBodyType body) throws IOException {
        EPCISQueryDocumentType queryDoc = new EPCISQueryDocumentType();
        try {
            DatatypeFactory factory = DatatypeFactory.newInstance();
            queryDoc.setCreationDate(factory.newXMLGregorianCalendar((GregorianCalendar) GregorianCalendar.getInstance()));
            queryDoc.setEPCISBody(body);
        } catch (DatatypeConfigurationException e) {
            // Never mind. Just ignore setting the creation date.
        }

        // serialize the response
        String data = null;
        try {
            JAXBContext context = JAXBContext.newInstance(EPCISQueryDocumentType.class);
            StringWriter writer = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(queryDoc, writer);
            data = writer.toString();
        } catch (JAXBException e) {
            throw new IOException(e.getMessage());
        }

        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // OutputStreamWriter writer = new OutputStreamWriter(baos);
        // SerializationContext serContext = new SerializationContext(writer);
        // QName queryDocXMLType =
        // EPCISQueryDocumentType.getTypeDesc().getXmlType();
        // serContext.setWriteXMLType(queryDocXMLType);
        // serContext.serialize(queryDocXMLType, new NullAttributes(), queryDoc,
        // queryDocXMLType,
        // EPCISQueryDocumentType.class, false, true);
        // writer.flush();
        // String data = baos.toString();

        // set up connection and send data to given destination
        URL serviceUrl = new URL(dest.toString());
        LOG.debug("Sending results of subscribed query with ID '" + subscriptionID + "' to '" + serviceUrl + "'.");
        int responseCode = sendData(serviceUrl, data);
        LOG.debug("Response " + responseCode);
    }

    /**
     * Sends the given data String to the specified URL.
     * <p>
     * TODO: some http/https error handling would be nice!
     * 
     * @param url
     *            The URL to send the data to.
     * @param data
     *            The data to send.
     * @return The HTTP response code.
     * @throws IOException
     *             If a communication error occurred.
     */
    private int sendData(final URL url, final String data) throws IOException {
        data.concat("\n");
        // setup connection
        // this will be an instance of javax.net.ssl.HttpURLConnection if the
        // https protocol is used in the url
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
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
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
