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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.fosstrak.epcis.model.EPCISQueryBodyType;
import org.fosstrak.epcis.model.EPCISQueryDocumentType;
import org.fosstrak.epcis.model.EventListType;
import org.fosstrak.epcis.model.ImplementationException;
import org.fosstrak.epcis.model.ObjectFactory;
import org.fosstrak.epcis.model.Poll;
import org.fosstrak.epcis.model.QueryParam;
import org.fosstrak.epcis.model.QueryParams;
import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.model.QueryTooLargeException;
import org.fosstrak.epcis.repository.EpcisQueryCallbackInterface;
import org.fosstrak.epcis.soap.EPCISServicePortType;
import org.fosstrak.epcis.soap.EPCglobalEPCISService;
import org.fosstrak.epcis.soap.ImplementationExceptionResponse;
import org.fosstrak.epcis.soap.NoSuchNameExceptionResponse;
import org.fosstrak.epcis.soap.QueryParameterExceptionResponse;
import org.fosstrak.epcis.soap.QueryTooComplexExceptionResponse;
import org.fosstrak.epcis.soap.QueryTooLargeExceptionResponse;
import org.fosstrak.epcis.soap.SecurityExceptionResponse;
import org.fosstrak.epcis.soap.ValidationExceptionResponse;
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
public class QuerySubscription implements EpcisQueryCallbackInterface, Serializable {

    private static final long serialVersionUID = -3066828914403000033L;

    private static final Log LOG = LogFactory.getLog(QuerySubscription.class);

    // the parameters from the subscribed query
    protected String subscriptionID;
    protected String dest;
    protected Calendar initialRecordTime;
    protected Boolean reportIfEmpty;
    protected String queryName;
    private QueryParams queryParams;
    private Calendar lastTimeExecuted;

    private Properties properties;

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
            final Boolean reportIfEmpty, final Calendar initialRecordTime,
            final Calendar lastTimeExecuted, final String queryName) {
        LOG.debug("Constructing Query Subscription with ID '" + subscriptionID + "'");
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
     * <p>
     * TODO: This is a back-end method: move this method to the
     * QueryOperationsBackend and delegate to it (thus we would need a reference
     * to the QueryOperationsBackend in this class).
     * 
     * @param lastTimeExecuted
     *            The new lastTimeExecuted.
     */
    private void updateSubscription(final Calendar lastTimeExecuted) {
        String jndiName = getProperties().getProperty("jndi.datasource.name", "java:comp/env/jdbc/EPCISDB");
        try {
            // open a database connection
            Context ctx = new InitialContext();
            DataSource db = (DataSource) ctx.lookup(jndiName);
            Connection dbconnection = db.getConnection();

            // update the subscription in the database
            String update = "UPDATE subscription SET lastexecuted=(?), params=(?)" + " WHERE subscriptionid=(?);";
            PreparedStatement stmt = dbconnection.prepareStatement(update);
            LOG.debug("SQL: " + update);
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
            dbconnection.commit();

            // close the database connection
            dbconnection.close();
        } catch (SQLException e) {
            String msg = "An SQL error occurred while updating the subscriptions in the database.";
            LOG.error(msg, e);
        } catch (IOException e) {
            String msg = "Unable to update the subscription in the database: " + e.getMessage();
            LOG.error(msg, e);
        } catch (NamingException e) {
            String msg = "Unable to find JNDI data source with name " + jndiName;
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
    private void updateRecordTime(final QueryParams queryParams, final Calendar initialRecordTime) {
        // update or add GE_recordTime restriction
        boolean foundRecordTime = false;
        for (QueryParam p : this.queryParams.getParam()) {
            if (p.getName().equalsIgnoreCase("GE_recordTime")) {
                LOG.debug("Updating query parameter 'GE_recordTime' with value '" + initialRecordTime + "'.");
                p.setValue(initialRecordTime.getTimeInMillis());
                foundRecordTime = true;
                break;
            }
        }
        if (!foundRecordTime) {
            LOG.debug("Adding query parameter 'GE_recordTime' with value '" + initialRecordTime + "'.");
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
            LOG.debug("Executing subscribed query '" + subscriptionID + "' with " + queryParams.getParam().size()
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
        QueryResults result = null;
        try {
            // get current time and send the query
            GregorianCalendar cal = new GregorianCalendar();
            result = executePoll(poll);
            LOG.debug("Subscribed query '" + subscriptionID + "' has been executed");

            // set new lastTimeExecuted (must be <= to time when query is
            // executed, otherwise we loose results)
            // cal.add(Calendar.SECOND, 1);
            this.lastTimeExecuted = cal;
        } catch (QueryTooLargeExceptionResponse e) {
            // send exception back to client
            QueryTooLargeException qtle = e.getFaultInfo();
            if (qtle == null) {
                qtle = new QueryTooLargeException();
                qtle.setQueryName(queryName);
                qtle.setSubscriptionID(subscriptionID);
                qtle.setReason(e.getMessage());
                LOG.info("USER ERROR: " + qtle.getReason());
            }
            callbackQueryTooLargeException(qtle);
            return;
        } catch (ImplementationExceptionResponse e) {
            // send exception back to client
            ImplementationException ie = e.getFaultInfo();
            if (ie == null) {
                ie = new ImplementationException();
                ie.setQueryName(queryName);
                ie.setReason(e.getMessage());
                ie.setSubscriptionID(subscriptionID);
                LOG.info("USER ERROR: " + ie.getReason());
            }
            callbackImplementationException(ie);
            return;
        } catch (Exception e) {
            String msg = "An unexpected error occurred while executing a subscribed query";
            LOG.error(msg + ": " + e.getMessage(), e);
            // send exception back to client
            ImplementationException ie = new ImplementationException();
            ie.setQueryName(queryName);
            ie.setReason(msg);
            ie.setSubscriptionID(subscriptionID);
            callbackImplementationException(ie);
            return;
        }
        result.setSubscriptionID(subscriptionID);
        EventListType eventList = result.getResultsBody().getEventList();

        // check if we have an empty result list
        boolean isEmpty = false;
        isEmpty = (eventList == null) ? true : eventList.getObjectEventOrAggregationEventOrQuantityEvent().isEmpty();
        if (!reportIfEmpty.booleanValue() && isEmpty) {
            LOG.debug("Subscribed query '" + subscriptionID + "' returned no results, nothing to report.");
            return;
        }

        callbackResults(result);

        // update query params with new lastTimeExecuted
        updateRecordTime(queryParams, lastTimeExecuted);
    }

    /**
     * Poll a query using local transport.
     */
    protected QueryResults executePoll(Poll poll) throws ImplementationExceptionResponse,
            QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse, NoSuchNameExceptionResponse, QueryParameterExceptionResponse {
        // we use CXF's local transport feature here
        EPCglobalEPCISService service = new EPCglobalEPCISService();
        QName portName = new QName("urn:epcglobal:epcis:wsdl:1", "EPCglobalEPCISServicePortLocal");
        service.addPort(portName, "http://schemas.xmlsoap.org/soap/", "local://query");
        EPCISServicePortType servicePort = service.getPort(portName, EPCISServicePortType.class);

        // the same using CXF API
        // JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        // factory.setAddress("local://query");
        // factory.setServiceClass(EPCISServicePortType.class);
        // EPCISServicePortType servicePort = (EPCISServicePortType)
        // factory.create();

        return servicePort.poll(poll);
    }

    /**
     * {@inheritDoc}
     */
    public void callbackResults(final QueryResults results) {
        callbackObject(results);
    }

    /**
     * {@inheritDoc}
     */
    public void callbackImplementationException(ImplementationException ie) {
        callbackObject(ie);
    }

    /**
     * {@inheritDoc}
     */
    public void callbackQueryTooLargeException(QueryTooLargeException qtle) {
        callbackObject(qtle);
    }

    /**
     * Serializes and sends the given object back to the client. The Object must
     * be an instance of QueryResults, QueryTooLargeException, or
     * ImplementationException.
     * 
     * @param o
     *            The object to be sent back to the client. An instance of
     *            QueryResults, QueryTooLargeException, or
     *            ImplementationException.
     */
    private void callbackObject(final Object o) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Callback " + o + " at " + new Date());
        }
        // create the EPCIS document
        EPCISQueryDocumentType epcisDoc = new EPCISQueryDocumentType();
        epcisDoc.setSchemaVersion(BigDecimal.valueOf(1.0));
        try {
            DatatypeFactory dataFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar now = dataFactory.newXMLGregorianCalendar(new GregorianCalendar());
            epcisDoc.setCreationDate(now);
        } catch (DatatypeConfigurationException e) {
            // oh well - don't care about setting the creation date
        }
        EPCISQueryBodyType epcisBody = new EPCISQueryBodyType();
        if (o instanceof QueryResults) {
            epcisBody.setQueryResults((QueryResults) o);
        } else if (o instanceof QueryTooLargeException) {
            epcisBody.setQueryTooLargeException((QueryTooLargeException) o);
        } else if (o instanceof ImplementationException) {
            epcisBody.setImplementationException((ImplementationException) o);
        } else {
            epcisBody = null;
        }
        epcisDoc.setEPCISBody(epcisBody);

        // serialize the response
        String data;
        try {
            data = marshalQueryDoc(epcisDoc);
        } catch (JAXBException e) {
            String msg = "An error serializing contents occurred: " + e.getMessage();
            LOG.error(msg, e);
            return;
        }

        // set up connection and send data to given destination
        try {
            URL serviceUrl = new URL(dest.toString());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Sending results of subscribed query '" + subscriptionID + "' to '" + serviceUrl + "'");
                if (data.length() < 10 * 1024) {
                    LOG.debug("Sending data:\n" + data);
                } else {
                    LOG.debug("Sending data: [" + data.length() + " bytes]");
                }
            }
            int responseCode;
            try {
                responseCode = sendData(serviceUrl, data);
            } catch (Exception e) {
                LOG.warn("Unable to send results of subscribed query '" + subscriptionID + "' to '" + serviceUrl
                        + "', retrying in 3 sec ...");
                // wait 3 seconds and try again
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    // never mind
                }
                try {
                    responseCode = sendData(serviceUrl, data);
                } catch (Exception e2) {
                    LOG.warn("Unable to send results of subscribed query '" + subscriptionID + "' to '" + serviceUrl
                            + "', retrying in 3 sec ...");
                    // wait 3 seconds and try again
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                        // never mind
                    }
                    responseCode = sendData(serviceUrl, data);
                }
            }
            LOG.debug("Response " + responseCode);
        } catch (IOException e) {
            String msg = "Unable to send results of subscribed query '" + subscriptionID + "' to '" + dest + "': "
                    + e.getMessage();
            LOG.error(msg, e);
            return;
        }
    }

    /**
     * Marshals the given EPCIS query document into it's XML representation.
     * 
     * @param epcisDoc
     *            The EPCISQueryDocumentType to marshal.
     * @return The marshaled EPCISQueryDocumentType XML String.
     */
    private String marshalQueryDoc(EPCISQueryDocumentType epcisDoc) throws JAXBException {
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBContext context = JAXBContext.newInstance("org.fosstrak.epcis.model");
        JAXBElement<EPCISQueryDocumentType> item = objectFactory.createEPCISQueryDocument(epcisDoc);
        LOG.debug("Serializing " + item + " into XML");
        StringWriter writer = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(item, writer);
        return writer.toString();
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
     *             If a communication error occurred.
     */
    private int sendData(final URL url, final String data) throws IOException {
        HttpURLConnection connection;
        if ("HTTPS".equalsIgnoreCase(url.getProtocol()) && trustAllCertificates()) {
            connection = getAllTrustingConnection(url);
        } else {
            connection = getConnection(url);
        }
        connection.setRequestMethod("POST");
        connection.setRequestProperty("content-type", "text/xml");
        connection.setRequestProperty("content-length", "" + data.length());
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // send data
        Writer out = new OutputStreamWriter(connection.getOutputStream());
        out.write(data);
        out.flush();
        out.close();

        // get response code
        int responseCode = connection.getResponseCode();

        // disconnect and return
        connection.disconnect();
        return responseCode;
    }

    /**
     * Opens a connection to the given URL.
     * <p>
     * The URL.openConnection() method returns an instance of
     * javax.net.ssl.HttpsURLConnection, which extends
     * java.net.HttpURLConnection, if the HTTPS protocol is used in the URL.
     * Thus, we support both the HTTP and HTTPS binding of the query callback
     * interface.
     * <p>
     * Note: By default, accessing an HTTPS URL using the URL class results in
     * an exception if the destination's certificate chain cannot be validated.
     * In this case you can manually import the destination's certificate into
     * the Java runtime's trust store, or, if you want to disable the validation
     * of certificates for testing purposes, use
     * {@link getAllTrustingConnection(URL)}.
     * 
     * @param url
     *            The URL on which a connection will be opened.
     * @return A HttpURLConnection connection object.
     * @throws IOException
     *             If an I/O error occurred.
     */
    private HttpURLConnection getConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    /**
     * Retrieves an "all-trusting" HTTP URL connection object, by disabling the
     * validation of certificates and overriding the default trust manager with
     * one that trusts all certificates.
     * 
     * @param url
     *            The URL on which a connection will be opened.
     * @return A HttpURLConnection connection object.
     * @throws IOException
     *             If an I/O error occurred.
     */
    private HttpURLConnection getAllTrustingConnection(URL url) throws IOException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            LOG.error("Unable to install the all-trusting trust manager", e);
        }
        return getConnection(url);
    }

    /**
     * @return Whether to trust a certificate whose certificate chain cannot be
     *         validated when delivering results via Query Callback Interface.
     */
    private boolean trustAllCertificates() {
        Properties properties = getProperties();
        return Boolean.parseBoolean(properties.getProperty("trustAllCertificates", "false"));
    }

    /**
     * Loads the application's properties file from the class path if it has not
     * already done so.
     * 
     * @return A populated Properties instance.
     */
    private Properties getProperties() {
        if (properties == null) {
            // read application properties from classpath
            String resource = "/application.properties";
            InputStream is = this.getClass().getResourceAsStream(resource);
            properties = new Properties();
            try {
                properties.load(is);
                is.close();
            } catch (IOException e) {
                LOG.error("Unable to load application properties from classpath:" + resource + " ("
                        + this.getClass().getResource(resource) + ")", e);
            }
        }
        return properties;
    }

    /**
     * @return The initial record time.
     */
    public Calendar getInitialRecordTime() {
        return initialRecordTime;
    }

    /**
     * @return the subscriptionID
     */
    public String getSubscriptionID() {
        return subscriptionID;
    }
}
