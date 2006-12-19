/* Copyright (c) 2006 ETH Zurich, Switzerland.
 * All rights reserved.
 *
 * For copying and distribution information, please see the file
 * LICENSE.
 */

/**
 *
 */
package org.accada.epcis.repository;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.NullAttributes;
import org.accada.epcis.soapapi.EPCISQueryBodyType;
import org.accada.epcis.soapapi.EPCISQueryDocumentType;
import org.accada.epcis.soapapi.EPCISServicePortType;
import org.accada.epcis.soapapi.EventListType;
import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.ImplementationExceptionSeverity;
import org.accada.epcis.soapapi.Poll;
import org.accada.epcis.soapapi.QueryParam;
import org.accada.epcis.soapapi.QueryResults;

/**
 * Implements a subscription to a query. Created upon
 * using subscribe() on the querying interface side.
 *
 * @author Alain Remund, Arthur van Dorp
 */
public class Subscription implements Serializable {

    /**
     * Generated ID for serialization. Adapt if you change
     * this class in a backwards incompatible way.
     */
    private static final long serialVersionUID = -7695867884113548051L;

    /**
     * Query parameters.
     */
    private QueryParam[] queryParams;

    /**
     * Destination URI to send results to.
     */
    private org.apache.axis.types.URI dest;

    /**
     * Initial record time.
     */
    private GregorianCalendar initialRecordTime;

    /**
     * Whether to send results if nothing new available.
     */
    private Boolean reportIfEmpty;

    /**
     * SubscriptionID.
     */
    protected String subscriptionID;

    /**
     * queryName.
     */
    private String queryName;

    /**
     * Last time the query got executed.
     * Used to restrict results to new ones.
     */
    private GregorianCalendar lastTimeQueryExecuted;

    /**
     * Constructor to be used when recreating from
     * storage.
     * @param subscriptionID subscriptionID.
     * @param queryParams Query parameters.
     * @param dest Destination URI.
     * @param reportIfEmpty Whether to report when nothing changed.
     * @param initialRecordTime Time from when on events should be
     * reported on first execution.
     * @param queryName queryName.
     */
    public Subscription(String subscriptionID,
            QueryParam[] queryParams,
            org.apache.axis.types.URI dest,
            Boolean reportIfEmpty,
            GregorianCalendar initialRecordTime,
            GregorianCalendar lastTimeExecuted,
            String queryName) {
        this.subscriptionID = subscriptionID;
        this.queryParams = queryParams;
        this.dest = dest;
        this.initialRecordTime = initialRecordTime;
        this.lastTimeQueryExecuted = lastTimeExecuted;
        this.reportIfEmpty = reportIfEmpty;
        this.queryName = queryName;
    }

    /**
     * Runs the query assigned to this subscription.
     * Advances lastTimeExecuted.
     * @throws ImplementationException For various reasons.
     */
    public void executeQuery() throws ImplementationException {
        QueryResults queryResults;
        EPCISQueryDocumentType queryDoc = new EPCISQueryDocumentType();
        EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
        queryDoc.setCreationDate(new GregorianCalendar());
        queryDoc.setEPCISBody(queryBody);

        Vector<QueryParam> actualParams = new Vector<QueryParam>();
        for (QueryParam p : queryParams) {
            actualParams.add(p);
        }
        // Add time restriction on events returned according to standard.
        QueryParam restrictTime = new QueryParam();
        restrictTime.setName("GE_recordTime");
        restrictTime.setValue(lastTimeQueryExecuted);
        actualParams.add(restrictTime);

        Poll pollParams = new Poll();
        pollParams.setQueryName(queryName);
        QueryParam[] actualParamsArray = {};
        actualParamsArray = actualParams.toArray(actualParamsArray);
        pollParams.setParams(actualParamsArray);
        lastTimeQueryExecuted = new GregorianCalendar();
        try {



            // Get a nice XML representation

            EPCISServicePortType EPCservice
                 = new EpcisQueryInterface();

            queryResults = EPCservice.poll(pollParams);
            EventListType qEvList
                = queryResults.getResultsBody().getEventList();
            if (!reportIfEmpty
             && (null == qEvList || qEvList.getAggregationEvent().length == 0)
             && (null == qEvList || qEvList.getObjectEvent().length == 0)
             && (null == qEvList || qEvList.getQuantityEvent().length == 0)
             && (null == qEvList || qEvList.getTransactionEvent().length == 0)
                    )
            {
                System.out.println("No results. Not sending anything.");
                return;
            }

            javax.xml.namespace.QName queryDocXMLType =
                EPCISQueryDocumentType.getTypeDesc().getXmlType();

            URL serviceUrl = new URL(dest.toString());
            HttpURLConnection connection = (HttpURLConnection) serviceUrl
                    .openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            OutputStream httpOut = connection.getOutputStream();


            OutputStreamWriter oWriter = new OutputStreamWriter(httpOut);

            SerializationContext serContext
                = new SerializationContext(oWriter);
            serContext.setWriteXMLType(queryDocXMLType);

            queryResults.setQueryName(queryName);
            queryResults.setSubscriptionID(subscriptionID);
            queryBody.setQueryResults(queryResults);
            queryDoc.setEPCISBody(queryBody);

            serContext.serialize(
                    queryDocXMLType,
                    new NullAttributes(),
                    queryDoc,
                    queryDocXMLType,
                    EPCISQueryDocumentType.class, false, true);

            // Send the result to dest

            oWriter.flush();
            httpOut.flush();
            oWriter.close();
            httpOut.close();

            System.out.println("Response code from destination: "
                    + connection.getResponseCode());

            // Close connection for easier monitoring with TCPMON
            connection.disconnect();

        } catch (Exception e) {
            System.out.println("Exception in executeQuery:" + e.getMessage());
            e.printStackTrace();
            ImplementationException ie = new ImplementationException(
                    "Exception in executeQuery occured: " + e.getMessage(),
                    ImplementationExceptionSeverity.fromString("ERROR"),
                    queryName,
                    subscriptionID);
            throw ie;
        }
    }

    /**
     * Returns the initial record time.
     * @return The initial record time.
     */
    public GregorianCalendar getInitialRecordTime() {
        return initialRecordTime;
    }

}
