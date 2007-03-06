/*
 * Copyright (c) 2006, 2007, ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the ETH Zurich nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.accada.epcis.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.accada.epcis.soapapi.ActionType;
import org.accada.epcis.soapapi.AggregationEventType;
import org.accada.epcis.soapapi.ArrayOfString;
import org.accada.epcis.soapapi.AttributeType;
import org.accada.epcis.soapapi.BusinessLocationType;
import org.accada.epcis.soapapi.BusinessTransactionType;
import org.accada.epcis.soapapi.DuplicateSubscriptionException;
import org.accada.epcis.soapapi.EPC;
import org.accada.epcis.soapapi.EPCISServicePortType;
import org.accada.epcis.soapapi.EmptyParms;
import org.accada.epcis.soapapi.EventListType;
import org.accada.epcis.soapapi.GetSubscriptionIDs;
import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.ImplementationExceptionSeverity;
import org.accada.epcis.soapapi.InvalidURIException;
import org.accada.epcis.soapapi.NoSuchNameException;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;
import org.accada.epcis.soapapi.ObjectEventType;
import org.accada.epcis.soapapi.Poll;
import org.accada.epcis.soapapi.QuantityEventType;
import org.accada.epcis.soapapi.QueryParam;
import org.accada.epcis.soapapi.QueryParameterException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryResultsBody;
import org.accada.epcis.soapapi.QueryTooComplexException;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.soapapi.ReadPointType;
import org.accada.epcis.soapapi.Subscribe;
import org.accada.epcis.soapapi.SubscribeNotPermittedException;
import org.accada.epcis.soapapi.SubscriptionControls;
import org.accada.epcis.soapapi.SubscriptionControlsException;
import org.accada.epcis.soapapi.TransactionEventType;
import org.accada.epcis.soapapi.Unsubscribe;
import org.accada.epcis.soapapi.ValidationException;
import org.accada.epcis.soapapi.VocabularyElementType;
import org.accada.epcis.soapapi.VocabularyType;
import org.accada.epcis.soapapi.VoidHolder;
import org.accada.epcis.utils.TimeParser;
import org.apache.axis.MessageContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.Text;
import org.apache.axis.types.URI;
import org.apache.log4j.Logger;

/**
 * EPCIS Query Operations Module implementing the SOAP/HTTP binding of the Query
 * Control Interface. The implementation converts invocations from Axis into SQL
 * queries and returns the results back to the requesting client through Axis.
 * 
 * @author David Gubler
 * @author Alain Remund
 * @author Arthur van Dorp
 * @author Marco Steybe
 */
public class QueryOperationsModule implements EPCISServicePortType {

    private static final Logger LOG = Logger.getLogger(QueryOperationsModule.class);

    /**
     * The version of the standard that this service is implementing.
     */
    private static final String STD_VERSION = "1.0";

    /**
     * The version of this service implementation.
     */
    private static final String VDR_VERSION = "http://www.accada.org/releases/0.2/";

    /**
     * The connection to the database.
     */
    private Connection dbconnection = null;

    /**
     * The database dependent identifier quotation sign.
     */
    private String delimiter;

    /**
     * Before returning the Results of the query, it checks if the set is too
     * large. This value can be set by Query-Parameters.
     */
    private int maxEventCount;

    /**
     * The maximum number of rows a query can return.
     */
    private int maxQueryRows;

    /**
     * The maximum timeout to wait for a query to return.
     */
    private int maxQueryTime;

    /**
     * The names of all the implemented queries.
     */
    private final Set<String> queryNames = new HashSet<String>() {
        private static final long serialVersionUID = -3868728341409854448L;
        {
            add("SimpleEventQuery");
            add("SimpleMasterDataQuery");
        }
    };

    /**
     * Basic SQL query string for object events.
     */
    private String objectEventQueryBase = "SELECT SQL_CALC_FOUND_ROWS DISTINCT "
            + "`event_ObjectEvent`.id, eventTime, recordTime, "
            + "eventTimeZoneOffset, action, "
            + "`voc_BizStep`.uri AS bizStep, "
            + "`voc_Disposition`.uri AS disposition, "
            + "`voc_ReadPoint`.uri AS readPoint, "
            + "`voc_BizLoc`.uri AS bizLocation "
            + "FROM `event_ObjectEvent` "
            + "LEFT JOIN `voc_BizStep` ON `event_ObjectEvent`.bizStep = `voc_BizStep`.id "
            + "LEFT JOIN `voc_Disposition` ON `event_ObjectEvent`.disposition = `voc_Disposition`.id "
            + "LEFT JOIN `voc_ReadPoint` ON `event_ObjectEvent`.readPoint = `voc_ReadPoint`.id "
            + "LEFT JOIN `voc_BizLoc` ON `event_ObjectEvent`.bizLocation = `voc_BizLoc`.id "
            + "LEFT JOIN `event_ObjectEvent_extensions` ON `event_ObjectEvent`.id = `event_ObjectEvent_extensions`.event_id "
            + "WHERE 1";

    /**
     * Basic SQL query string for aggregation events.
     */
    private String aggregationEventQueryBase = "SELECT SQL_CALC_FOUND_ROWS DISTINCT "
            + "`event_AggregationEvent`.id, eventTime, recordTime, "
            + "eventTimeZoneOffset, parentID, action, "
            + "`voc_BizStep`.uri AS bizStep, "
            + "`voc_Disposition`.uri AS disposition, "
            + "`voc_ReadPoint`.uri AS readPoint, "
            + "`voc_BizLoc`.uri AS bizLocation "
            + "FROM `event_AggregationEvent` "
            + "LEFT JOIN `voc_BizStep` ON `event_AggregationEvent`.bizStep = `voc_BizStep`.id "
            + "LEFT JOIN `voc_Disposition` ON `event_AggregationEvent`.disposition  = `voc_Disposition`.id "
            + "LEFT JOIN `voc_ReadPoint` ON `event_AggregationEvent`.readPoint = `voc_ReadPoint`.id "
            + "LEFT JOIN `voc_BizLoc` ON `event_AggregationEvent`.bizLocation = `voc_BizLoc`.id "
            + "LEFT JOIN `event_AggregationEvent_extensions` ON `event_AggregationEvent`.id = `event_AggregationEvent_extensions`.event_id "
            + "WHERE 1 ";

    /**
     * Basic SQL query string for quantity events.
     */
    private String quantityEventQueryBase = "SELECT SQL_CALC_FOUND_ROWS DISTINCT "
            + "`event_QuantityEvent`.id, eventTime, recordTime, eventTimeZoneOffset, "
            + "`voc_EPCClass`.uri AS epcClass, quantity, "
            + "`voc_BizStep`.uri AS bizStep, "
            + "`voc_Disposition`.uri AS disposition, "
            + "`voc_ReadPoint`.uri AS readPoint, "
            + "`voc_BizLoc`.uri AS bizLocation "
            + "FROM `event_QuantityEvent` "
            + "LEFT JOIN `voc_BizStep` ON `event_QuantityEvent`.bizStep = `voc_BizStep`.id "
            + "LEFT JOIN `voc_Disposition` ON `event_QuantityEvent`.disposition = `voc_Disposition`.id "
            + "LEFT JOIN `voc_ReadPoint` ON `event_QuantityEvent`.readPoint = `voc_ReadPoint`.id "
            + "LEFT JOIN `voc_BizLoc` ON `event_QuantityEvent`.bizLocation = `voc_BizLoc`.id "
            + "LEFT JOIN `voc_EPCClass` ON `event_QuantityEvent`.epcClass = `voc_EPCClass`.id "
            + "LEFT JOIN `event_QuantityEvent_extensions` ON `event_QuantityEvent`.id = `event_QuantityEvent_extensions`.event_id "
            + "WHERE 1 ";

    /**
     * Basic SQL query string for transaction events.
     */
    private String transactionEventQueryBase = "SELECT SQL_CALC_FOUND_ROWS DISTINCT "
            + "`event_TransactionEvent`.id, eventTime, recordTime, "
            + "eventTimeZoneOffset, action, parentID, "
            + "`voc_BizStep`.uri AS bizStep, "
            + "`voc_Disposition`.uri AS disposition, "
            + "`voc_ReadPoint`.uri AS readPoint, "
            + "`voc_BizLoc`.uri AS bizLocation "
            + "FROM `event_TransactionEvent` "
            + "LEFT JOIN `voc_BizStep` ON `event_TransactionEvent`.bizStep = `voc_BizStep`.id "
            + "LEFT JOIN `voc_Disposition` ON `event_TransactionEvent`.disposition = `voc_Disposition`.id "
            + "LEFT JOIN `voc_ReadPoint` ON `event_TransactionEvent`.readPoint = `voc_ReadPoint`.id "
            + "LEFT JOIN `voc_BizLoc` ON `event_TransactionEvent`.bizLocation = `voc_BizLoc`.id "
            + "LEFT JOIN `event_TransactionEvent_extensions` ON `event_TransactionEvent`.id = `event_TransactionEvent_extensions`.event_id "
            + "WHERE 1 ";

    /**
     * Constructs a new QueryOperationsModule. Reads the properties from the
     * passed message context, which is set by the QueryInitHandler's invoke
     * method.
     */
    public QueryOperationsModule() {
        LOG.info("QueryOperationsModule invoked.");
        MessageContext msgContext = MessageContext.getCurrentContext();
        delimiter = (String) msgContext.getProperty("delimiter");
        LOG.debug("delimiter=" + delimiter);
        dbconnection = (Connection) msgContext.getProperty("dbconnection");
        Properties properties = (Properties) msgContext.getProperty("properties");
        maxQueryRows = Integer.parseInt(properties.getProperty(
                "maxQueryResultRows", "-1"));
        LOG.debug("maxQueryResultRows=" + maxQueryRows);
        maxQueryTime = Integer.parseInt(properties.getProperty(
                "maxQueryExecutionTime", "10000"));
        LOG.debug("maxQueryExecutionTime=" + maxQueryTime);
    }

    /**
     * Returns whether subscriptionID already exists in DB.
     * 
     * @param subscrId
     *            The id to be looked up.
     * @return <code>true</code> if subscriptionID already exists in DB,
     *         <code>false</code> otherwise.
     * @throws SQLException
     *             If a problem with the database occured.
     */
    private boolean fetchExistsSubscriptionId(final String subscrId)
            throws SQLException {
        String query = "SELECT EXISTS(SELECT subscriptionid FROM subscription WHERE subscriptionid = (?))";
        PreparedStatement pstmt = dbconnection.prepareStatement(query);
        pstmt.setString(1, subscrId);
        LOG.debug("QUERY: " + query);
        LOG.debug("       query param 1: " + subscrId);

        ResultSet rs = pstmt.executeQuery();
        rs.first();
        Boolean result = rs.getBoolean(1);
        rs.close();

        return result.booleanValue();
    }

    /**
     * Returns all EPCs associated to a certain event_id.
     * 
     * @param tableName
     *            The SQL name of the table to be searched
     * @param eventId
     *            is typically an 64bit integer. We use string here to avoid
     *            java vs. SQL type problems.
     * @return EPCs beloning to event_id
     * @throws SQLException
     *             If an error with the database occured.
     */
    private EPC[] fetchEPCs(final String tableName, final int eventId)
            throws SQLException {
        String query = "SELECT DISTINCT epc FROM " + delimiter + tableName
                + delimiter + " WHERE " + delimiter + "event_id" + delimiter
                + " = " + eventId;
        LOG.debug("QUERY: " + query);
        Statement stmt = dbconnection.createStatement();

        ResultSet rs = stmt.executeQuery(query);
        List<EPC> epcList = new ArrayList<EPC>();
        while (rs.next()) {
            EPC epc = new EPC(rs.getString("epc"));
            epcList.add(epc);
        }

        EPC[] epcs = new EPC[epcList.size()];
        epcs = epcList.toArray(epcs);
        return epcs;
    }

    /**
     * Retreives all MessageElement, i.e. all fieldname extensions for the given
     * event.
     * 
     * @param tableName
     *            The name of the table with the field extensions of the event.
     * @param eventId
     *            The ID of the event
     * @return All MessageElement (fieldname extensions) associated with the
     *         given eventId.
     * @throws SQLException
     *             If an error with the database occured.
     */
    private MessageElement[] fetchMessageElements(final String tableName,
            final int eventId) throws SQLException {
        String query = "SELECT * FROM " + delimiter + tableName + delimiter
                + " WHERE " + delimiter + "event_id" + delimiter + " = "
                + eventId;
        LOG.debug("QUERY: " + query);
        Statement stmt = dbconnection.createStatement();

        ResultSet rs = stmt.executeQuery(query);
        List<MessageElement> meList = new ArrayList<MessageElement>();
        while (rs.next()) {
            String fieldname = rs.getString("fieldname");
            String[] parts = fieldname.split("#");
            if (parts.length != 2) {
                throw new SQLException("Column 'fieldname' in table '"
                        + tableName + "' has invalid format: " + fieldname);
            }
            String namespace = parts[0];
            String localPart = parts[1];
            String prefix = rs.getString("prefix");
            String value = rs.getString("intValue");
            if (value == null) {
                value = rs.getString("floatValue");
            }
            if (value == null) {
                value = rs.getString("strValue");
            }
            if (value == null) {
                value = rs.getString("dateValue");
            }
            if (value == null) {
                throw new SQLException("All value columns in '" + tableName
                        + "' are null.");
            }
            MessageElement me = new MessageElement(localPart, prefix, namespace);
            me.setValue(value);
            LOG.debug("message element resolved to " + me.toString());
            meList.add(me);
        }
        MessageElement[] any = new MessageElement[meList.size()];
        any = meList.toArray(any);
        return any;
    }

    /**
     * Returns all bizTransactions associated to a certain event_id.
     * 
     * @param tableName
     *            The SQL name of the table to be searched.
     * @param eventId
     *            Typically an 64bit integer to identify the event.
     * @return bizTransactions associated to event_id
     * @throws SQLException
     *             DB problem.
     * @throws ImplementationException
     *             Several problems get matched to this exception.
     */
    private BusinessTransactionType[] fetchBizTransactions(
            final String tableName, final int eventId) throws SQLException,
            ImplementationException {
        String query = "SELECT DISTINCT "
                + "`voc_BizTrans`.uri, `voc_BizTransType`.uri AS typeuri"
                + " FROM ((`BizTransaction` JOIN `"
                + tableName
                + "` ON `BizTransaction`.id = `"
                + tableName
                + "`.bizTrans_id)"
                + "JOIN `voc_BizTrans` ON `BizTransaction`.bizTrans = `voc_BizTrans`.id)"
                + " LEFT OUTER JOIN `voc_BizTransType` ON `BizTransaction`.type = `voc_BizTransType`.id"
                + " WHERE `" + tableName + "`.event_id = " + eventId;
        LOG.debug("QUERY: " + query);
        Statement stmt = dbconnection.createStatement();

        ResultSet rs = stmt.executeQuery(query.replaceAll("`", delimiter));
        List<BusinessTransactionType> bizTransList = new ArrayList<BusinessTransactionType>();
        while (rs.next()) {
            String uriString = null;
            uriString = rs.getString("uri");
            URI uri = stringToUri(uriString);
            BusinessTransactionType btrans = new BusinessTransactionType(uri);
            uriString = rs.getString("typeuri");
            if (uriString != null) {
                uri = stringToUri(uriString);
                btrans.setType(uri);
            }
            bizTransList.add(btrans);
        }

        BusinessTransactionType[] bizTrans = new BusinessTransactionType[bizTransList.size()];
        bizTrans = bizTransList.toArray(bizTrans);
        return bizTrans;
    }

    /**
     * Convert a string to a URI. Exceptions are caught and a meaningful
     * ImplementationException is thrown instead. This method works on axis
     * URIs, not Java URIs. Make sure you have the right imports.
     * 
     * @param uriString
     *            String to convert to URI
     * @return URI
     * @throws ImplementationException
     *             Thrown when string not in URI format.
     */
    private URI stringToUri(final String uriString)
            throws ImplementationException {
        try {
            if (uriString == null) {
                return null;
            }
            URI uri = new URI(uriString);
            return uri;
        } catch (URI.MalformedURIException e) {
            String msg = "Malformed URI value: " + uriString;
            LOG.error(msg, e);
            ImplementationException iex = new ImplementationException();
            iex.setReason(msg);
            iex.setStackTrace(e.getStackTrace());
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw iex;
        }
    }

    /**
     * Executes an SQL query and returns an array of ObjectEventType.
     * 
     * @param objectEventQuery
     *            Query. Supposed to be created by createEventQuery.
     * @return ObjectEventType[]
     * @throws SQLException
     *             Problem on db backend. The Query must return ObjectEvents,
     *             otherwise this leads to an SQLException as well. is thrown.
     * @throws ImplementationException
     *             Problem with data or on implementation side. (i.e. uri value
     *             in DB is actually not an uri)
     * @throws QueryTooLargeException
     *             If the rows returned by the query is larger than specified or
     *             larger than this implementation is willing to accept.
     * @throws QueryTooComplexException
     *             If the query takes too long to return.
     */
    private ObjectEventType[] runObjectEventQuery(
            final PreparedStatement objectEventQuery) throws SQLException,
            ImplementationException, QueryTooLargeException,
            QueryTooComplexException {
        if (objectEventQuery == null) {
            return null;
        }

        // Calendar needed for converting timestamps
        Calendar cal = Calendar.getInstance();

        // run the query and get all ObjectEvents
        ResultSet rs = executeStatement(objectEventQuery, maxQueryTime);
        checkQueryRows();

        List<ObjectEventType> objectEventList = new ArrayList<ObjectEventType>();
        while (rs.next()) {
            ObjectEventType objectEvent = new ObjectEventType();
            int eventId = rs.getInt("id");

            // set EventTime, RecordTime, and EventTimezoneOffset
            cal.setTime(rs.getTimestamp("eventTime"));
            objectEvent.setEventTime((Calendar) cal.clone());
            cal.setTime(rs.getTimestamp("recordTime"));
            objectEvent.setRecordTime((Calendar) cal.clone());
            objectEvent.setEventTimeZoneOffset(rs.getString("eventTimeZoneOffset"));

            // set action
            ActionType action = ActionType.fromString(rs.getString("action"));
            objectEvent.setAction(action);

            // set all URIs
            objectEvent.setBizStep(stringToUri(rs.getString("bizStep")));
            objectEvent.setDisposition(stringToUri(rs.getString("disposition")));
            if (rs.getString("readPoint") != null) {
                ReadPointType rp = new ReadPointType();
                rp.setId(stringToUri(rs.getString("readPoint")));
                objectEvent.setReadPoint(rp);
            }
            if (rs.getString("bizLocation") != null) {
                BusinessLocationType blt = new BusinessLocationType();
                blt.setId(stringToUri(rs.getString("bizLocation")));
                objectEvent.setBizLocation(blt);
            }

            // set business transactions
            BusinessTransactionType[] btt = fetchBizTransactions(
                    "event_ObjectEvent_bizTrans", eventId);
            objectEvent.setBizTransactionList(btt);

            // set EPCs
            EPC[] epcs = fetchEPCs("event_ObjectEvent_EPCs", eventId);
            objectEvent.setEpcList(epcs);

            // set field extensions
            MessageElement[] any = fetchMessageElements(
                    "event_ObjectEvent_extensions", eventId);
            objectEvent.set_any(any);

            objectEventList.add(objectEvent);
        }
        rs.close();
        ObjectEventType[] objectEvents = {};
        objectEvents = objectEventList.toArray(objectEvents);

        return objectEvents;
    }

    /**
     * Executes a SQL Query and returns an array of AggregationEventType.
     * 
     * @param aggregationEventQuery
     *            The Query is supposed to be created by createEventQuery(...,
     *            "AggregationEvent").
     * @return AggregationEventType[]
     * @throws SQLException
     *             Must return AggregationEvents, otherwise an SQLException is
     *             thrown.
     * @throws ImplementationException
     *             May throw ImplementationException for various reasons (i.e.
     *             uri value in DB is actually not an uri)
     * @throws QueryTooLargeException
     *             If the rows returned by the query is larger than specified or
     *             larger than this implementation is willing to accept.
     * @throws QueryTooComplexException
     *             If a query takes too long to return.
     */
    private AggregationEventType[] runAggregationEventQuery(
            final PreparedStatement aggregationEventQuery) throws SQLException,
            ImplementationException, QueryTooLargeException,
            QueryTooComplexException {
        if (aggregationEventQuery == null) {
            return null;
        }

        // Calendar needed for converting timestamps
        Calendar cal = Calendar.getInstance();

        // run the query and get all AggregationEvents
        ResultSet rs = executeStatement(aggregationEventQuery, maxQueryTime);
        checkQueryRows();

        List<AggregationEventType> aggrEventList = new ArrayList<AggregationEventType>();
        while (rs.next()) {
            AggregationEventType aggrEvent = new AggregationEventType();
            int eventId = rs.getInt("id");

            // set EventTime, RecordTime, and EventTimezoneOffset
            cal.setTime(rs.getTimestamp("eventTime"));
            aggrEvent.setEventTime((Calendar) cal.clone());
            cal.setTime(rs.getTimestamp("recordTime"));
            aggrEvent.setRecordTime((Calendar) cal.clone());
            aggrEvent.setEventTimeZoneOffset(rs.getString("eventTimeZoneOffset"));

            // set action
            ActionType action = ActionType.fromString(rs.getString("action"));
            aggrEvent.setAction(action);

            // set all URIs
            aggrEvent.setParentID(stringToUri(rs.getString("parentID")));
            aggrEvent.setBizStep(stringToUri(rs.getString("bizStep")));
            aggrEvent.setDisposition(stringToUri(rs.getString("disposition")));
            if (rs.getString("readPoint") != null) {
                ReadPointType rpt = new ReadPointType();
                rpt.setId(stringToUri(rs.getString("readPoint")));
                aggrEvent.setReadPoint(rpt);
            }
            if (rs.getString("bizLocation") != null) {
                BusinessLocationType blt = new BusinessLocationType();
                blt.setId(stringToUri(rs.getString("bizLocation")));
                aggrEvent.setBizLocation(blt);
            }

            // set business transactions
            BusinessTransactionType[] bizTrans = fetchBizTransactions(
                    "event_AggregationEvent_bizTrans", eventId);
            aggrEvent.setBizTransactionList(bizTrans);

            // set associated EPCs
            EPC[] epcs = fetchEPCs("event_AggregationEvent_EPCs", eventId);
            aggrEvent.setChildEPCs(epcs);

            // TODO: marco: fetchMessageElements()

            aggrEventList.add(aggrEvent);
        }
        rs.close();
        AggregationEventType[] aggrEvents = new AggregationEventType[aggrEventList.size()];
        aggrEvents = aggrEventList.toArray(aggrEvents);
        return aggrEvents;
    }

    /**
     * Executes a SQL Query and returns an array of QuantityEventType.
     * 
     * @param quantityEventQuery
     *            The Query is supposed to be created by createEventQuery(...,
     *            "QuantityEvent").
     * @return QuantityEventType[]
     * @throws SQLException
     *             The Query must return QuantityEvents, otherwise an
     *             SQLException is thrown.
     * @throws ImplementationException
     *             May throw ImplementationException for various reasons (i.e.
     *             uri value in DB is actually not an uri)
     * @throws QueryTooLargeException
     *             If the rows returned by the query is larger than specified or
     *             larger than this implementation is willing to accept.
     * @throws QueryTooComplexException
     *             If a query takes too long to return.
     */
    private QuantityEventType[] runQuantityEventQuery(
            final PreparedStatement quantityEventQuery) throws SQLException,
            ImplementationException, QueryTooLargeException,
            QueryTooComplexException {
        if (quantityEventQuery == null) {
            return null;
        }

        // Calendar needed for converting timestamps
        Calendar cal = Calendar.getInstance();

        // run the query and get all QuantityEvents
        ResultSet rs = executeStatement(quantityEventQuery, maxQueryTime);
        checkQueryRows();

        List<QuantityEventType> quantEventList = new ArrayList<QuantityEventType>();
        while (rs.next()) {
            QuantityEventType quantEvent = new QuantityEventType();
            int eventId = rs.getInt("id");

            // set EventTime, RecordTime, and EventTimezoneOffset
            cal.setTime(rs.getTimestamp("eventTime"));
            quantEvent.setEventTime((Calendar) cal.clone());
            cal.setTime(rs.getTimestamp("recordTime"));
            quantEvent.setRecordTime((Calendar) cal.clone());
            quantEvent.setEventTimeZoneOffset(rs.getString("eventTimeZoneOffset"));

            // set EPCClass
            quantEvent.setEpcClass(stringToUri(rs.getString("epcClass")));

            // set quantity
            quantEvent.setQuantity(rs.getInt("quantity"));

            // set all URIs
            quantEvent.setBizStep(stringToUri(rs.getString("bizStep")));
            quantEvent.setDisposition(stringToUri(rs.getString("disposition")));
            if (rs.getString("readPoint") != null) {
                ReadPointType rpt = new ReadPointType();
                rpt.setId(stringToUri(rs.getString("readPoint")));
                quantEvent.setReadPoint(rpt);
            }
            if (rs.getString("bizLocation") != null) {
                BusinessLocationType blt = new BusinessLocationType();
                blt.setId(stringToUri(rs.getString("bizLocation")));
                quantEvent.setBizLocation(blt);
            }

            // set business transactions
            BusinessTransactionType[] bizTrans = fetchBizTransactions(
                    "event_QuantityEvent_bizTrans", eventId);
            quantEvent.setBizTransactionList(bizTrans);

            quantEventList.add(quantEvent);
        }
        rs.close();

        QuantityEventType[] quantEvents = new QuantityEventType[quantEventList.size()];
        quantEvents = quantEventList.toArray(quantEvents);
        return quantEvents;
    }

    /**
     * Executes a SQL Query and returns an array of TransactionEventType.
     * 
     * @param transactionEventQuery
     *            The Query is supposed to be created by createEventQuery(...,
     *            "TransactionEvent").
     * @return AggregationEventType[]
     * @throws SQLException
     *             The Query must return TransactionEvents, otherwise an
     *             SQLException is thrown.
     * @throws ImplementationException
     *             May throw ImplementationException for various reasons (i.e.
     *             uri value in DB is actually not an uri)
     * @throws QueryTooLargeException
     *             If the rows returned by the query is larger than specified or
     *             larger than this implementation is willing to accept.
     * @throws QueryTooComplexException
     *             If a query takes too long to return.
     */
    private TransactionEventType[] runTransactionEventQuery(
            final PreparedStatement transactionEventQuery) throws SQLException,
            ImplementationException, QueryTooLargeException,
            QueryTooComplexException {
        if (transactionEventQuery == null) {
            return null;
        }

        // Calendar needed for converting timestamps
        Calendar cal = Calendar.getInstance();

        // run the query and get all TransactionEvents
        ResultSet rs = executeStatement(transactionEventQuery, maxQueryTime);
        checkQueryRows();

        List<TransactionEventType> transEventList = new ArrayList<TransactionEventType>();
        while (rs.next()) {
            TransactionEventType transEvent = new TransactionEventType();
            int eventId = rs.getInt("id");

            // set EventTime, RecordTime, and EventTimezoneOffset
            cal.setTime(rs.getTimestamp("eventTime"));
            transEvent.setEventTime((Calendar) cal.clone());
            cal.setTime(rs.getTimestamp("recordTime"));
            transEvent.setRecordTime((Calendar) cal.clone());
            transEvent.setEventTimeZoneOffset(rs.getString("eventTimeZoneOffset"));

            // set action
            ActionType action = ActionType.fromString(rs.getString("action"));
            transEvent.setAction(action);

            // set all URIs
            transEvent.setParentID(stringToUri(rs.getString("parentID")));
            transEvent.setBizStep(stringToUri(rs.getString("bizStep")));
            transEvent.setDisposition(stringToUri(rs.getString("disposition")));
            if (rs.getString("readPoint") != null) {
                ReadPointType rpt = new ReadPointType();
                rpt.setId(stringToUri(rs.getString("readPoint")));
                transEvent.setReadPoint(rpt);
            }
            if (rs.getString("bizLocation") != null) {
                BusinessLocationType blt = new BusinessLocationType();
                blt.setId(stringToUri(rs.getString("bizLocation")));
                transEvent.setBizLocation(blt);
            }

            // set business transactions
            BusinessTransactionType[] bizTrans = fetchBizTransactions(
                    "event_TransactionEvent_bizTrans", eventId);
            transEvent.setBizTransactionList(bizTrans);

            // set associated EPCs
            EPC[] epcs = fetchEPCs("event_TransactionEvent_EPCs", eventId);
            transEvent.setEpcList(epcs);

            transEventList.add(transEvent);
        }
        rs.close();

        TransactionEventType[] transEvents = new TransactionEventType[transEventList.size()];
        transEvents = transEventList.toArray(transEvents);
        return transEvents;
    }

    /**
     * Retrieves the number of rows resulted from the execution of the last
     * query and throws a QueryTooLargeExsception if the number exceeds the
     * value specified by the 'maxEventCount' argument or the implementation's
     * global 'maxNrOfRows' parameter.
     * 
     * @throws SQLException
     *             If a problem accessing the database occured.
     * @throws QueryTooLargeException
     *             If the rows returned by the query is larger than specified or
     *             larger than this implementation is willing to accept.
     */
    private void checkQueryRows() throws SQLException, QueryTooLargeException {
        // check the number of rows calculated for the query
        Statement stmt = dbconnection.createStatement();
        ResultSet rows = stmt.executeQuery("SELECT FOUND_ROWS() AS rowcount;");
        int rowCount = maxQueryRows;
        if (rows.first()) {
            rowCount = rows.getInt(1);
        }
        if (rowCount > maxQueryRows
                || (maxEventCount > -1 && rowCount > maxEventCount)) {
            String msg = "The query returned more results (" + rowCount
                    + ") than ";
            if (rowCount > maxQueryRows) {
                msg = msg + "this implementation is willing to handle ("
                        + maxQueryRows + ").";
            } else {
                msg = msg + "specified by parameter 'maxEventCount' ("
                        + maxEventCount + ").";
            }
            LOG.info("USER ERROR: " + msg);
            QueryTooLargeException e = new QueryTooLargeException();
            e.setReason(msg);
            throw e;
        }
    }

    /**
     * Transforms an array of strings into sql IN (...) notation. Takes the
     * string array, the query string and the argument vector This function is
     * designed to be used with PreparedStatement
     * 
     * @param strings
     *            Array of strings to be added to sql IN (...) expression.
     * @param query
     *            The query which will be appended with the appropriate amounts
     *            of question marks for the parameters.
     * @param queryArgs
     *            The queryArgs vector which will take the additional query
     *            parameters specified in 'strings'.
     */
    private void stringArrayToSQL(final String[] strings,
            final StringBuffer query, final List<String> queryArgs) {
        int j = 0;
        while (j < strings.length - 1) {
            query.append("?,");
            queryArgs.add(strings[j]);
            j++;
        }
        if (strings.length > 0) {
            query.append("?");
            queryArgs.add(strings[j]);
        }
    }

    /**
     * Create an SQL query string from a list of Query Parameters.
     * 
     * @param queryParams
     *            The query parameters.
     * @param eventType
     *            Has to be one of the four basic event types "ObjectEvent",
     *            "AggregationEvent", "QuantityEvent", "TransactionEvent".
     * @return The prepared sql statement.
     * @throws SQLException
     *             Whenever something goes wrong when querying the db.
     * @throws QueryParameterException
     *             If one of the given QueryParam is invalid.
     * @throws ImplementationException
     *             If an error in the implementation occured.
     */
    private PreparedStatement createEventQuery(final QueryParam[] queryParams,
            final String eventType) throws SQLException,
            QueryParameterException, ImplementationException {

        StringBuffer query;
        if (eventType.equals("ObjectEvent")) {
            query = new StringBuffer(objectEventQueryBase);
        } else if (eventType.equals("AggregationEvent")) {
            query = new StringBuffer(aggregationEventQueryBase);
        } else if (eventType.equals("QuantityEvent")) {
            query = new StringBuffer(quantityEventQueryBase);
        } else if (eventType.equals("TransactionEvent")) {
            query = new StringBuffer(transactionEventQueryBase);
        } else {
            ImplementationException iex = new ImplementationException();
            String msg = "Invalid event type encountered: " + eventType;
            LOG.error(msg);
            iex.setReason(msg);
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw iex;
        }

        String orderBy = "";
        String orderDirection = "";
        int limit = -1;
        maxEventCount = -1;
        List<String> params = new LinkedList<String>();
        List<String> queryArgs = new ArrayList<String>();

        for (int i = 0; i < queryParams.length; i++) {
            String paramName = queryParams[i].getName();
            Object paramValue = queryParams[i].getValue();

            // check if this parameter has already been provided
            if (params.contains(paramName)) {
                String msg = "Two or more inputs are provided for the same parameter '"
                        + paramName + "'.";
                LOG.info("USER ERROR: " + msg);
                throw new QueryParameterException(msg);
            } else {
                params.add(paramName);
            }

            try {
                if (paramName.equals("eventType")) {
                    // check if the eventType argument (given to this method)
                    // matches one of the values of this 'eventType' parameter
                    String[] eventTypes = ((ArrayOfString) paramValue).getString();
                    List<String> eventTypeList = Arrays.asList(eventTypes);
                    if (eventTypeList.contains(eventType)) {
                        return null;
                    }

                } else if (paramName.equals("GE_eventTime")
                        || paramName.equals("LT_eventTime")
                        || paramName.equals("GE_recordTime")
                        || paramName.equals("LT_recordTime")) {
                    // restrict events by eventTime or recordTime
                    if (paramName.equals("GE_eventTime")) {
                        query.append(" AND (eventTime >= ?) ");
                    } else if (paramName.equals("LT_eventTime")) {
                        query.append(" AND (eventTime < ?) ");
                    } else if (paramName.equals("GE_recordTime")) {
                        query.append(" AND (recordTime >= ?) ");
                    } else if (paramName.equals("LT_recordTime")) {
                        query.append(" AND (recordTime < ?) ");
                    }
                    Calendar cal = (Calendar) paramValue;
                    Timestamp ts = new Timestamp(cal.getTimeInMillis());
                    queryArgs.add(ts.toString());

                } else if (paramName.equals("EQ_action")) {
                    if (!eventType.equals("QuantityEvent")) {
                        String[] actions = ((ArrayOfString) paramValue).getString();
                        checkActionValues(actions);
                        query.append(" AND (action IN (");
                        stringArrayToSQL(actions, query, queryArgs);
                        query.append(")) ");
                    } else {
                        // QuantityEvents have no action so no event matches!
                        query.append(" AND 0 ");
                    }

                } else if (paramName.equals("EQ_bizStep")
                        || paramName.equals("EQ_disposition")
                        || paramName.equals("EQ_readPoint")
                        || paramName.equals("EQ_bizLocation")) {
                    if (paramValue instanceof String
                            || paramValue.toString().equals("")) {
                        // empty parameter provided -> no binding on events
                        continue;
                    }
                    if (paramName.equals("EQ_bizStep")) {
                        query.append(" AND (`voc_BizStep`.uri IN (");
                    } else if (paramName.equals("EQ_disposition")) {
                        query.append(" AND (`voc_Disposition`.uri IN (");
                    } else if (paramName.equals("EQ_readPoint")) {
                        query.append(" AND (`voc_ReadPoint`.uri IN (");
                    } else if (paramName.equals("EQ_bizLocation")) {
                        query.append(" AND (`voc_BizLoc`.uri IN (");
                    }
                    stringArrayToSQL(((ArrayOfString) paramValue).getString(),
                            query, queryArgs);
                    query.append(")) ");

                } else if (paramName.equals("WD_readPoint")
                        || paramName.equals("WD_bizLocation")) {
                    if (paramValue instanceof String
                            || paramValue.toString().equals("")) {
                        // empty parameter provided -> no binding on events
                        continue;
                    }
                    String[] attrs = null;
                    try {
                        attrs = ((ArrayOfString) paramValue).getString();
                    } catch (ClassCastException e) {
                        // we have the URI directly (no ArrayOfString wrapper)
                        attrs = new String[1];
                        attrs[0] = paramValue.toString();
                    }
                    String tablename = "`voc_ReadPoint`";
                    if (paramName.equals("WD_bizLocation")) {
                        tablename = "`voc_BizLoc`";
                    }
                    // the % allows any possible ending, which should implement
                    // the semantics of "With Descendant"
                    query.append(" AND (");
                    for (int j = 0; j < attrs.length; j++) {
                        query.append(tablename);
                        query.append(".uri LIKE ? OR ");
                        queryArgs.add(attrs[j] + "%");
                    }
                    query.append("0) ");

                } else if (paramName.startsWith("EQ_bizTransaction_")) {
                    // this query is subdivided into several subqueries

                    // subquery for selecting IDs from voc_BizTransType
                    // type extracted from parameter name
                    String type = paramName.substring(18);
                    String vocBizTransTypeId = "SELECT id FROM `voc_BizTransType` WHERE uri=\""
                            + type + "\"";

                    // subquery for selecting IDs from voc_BizTrans
                    StringBuffer temp = new StringBuffer();
                    temp.append("(SELECT id AS vocBizTransId FROM `voc_BizTrans` WHERE `voc_BizTrans`.uri IN (");
                    String[] strings = ((ArrayOfString) paramValue).getString();
                    int j = 0;
                    while (j < strings.length - 1) {
                        temp.append("\"" + strings[j] + "\",");
                        j++;
                    }
                    if (strings.length > 0) {
                        temp.append("\"" + strings[j] + "\"");
                    }
                    temp.append(")) AS SelectedVocBizTrans");
                    String vocBizTransIds = temp.toString();

                    // subquery for selecting IDs from BizTransaction
                    String selectedBizTrans = "(SELECT id AS bizTransId, bizTrans FROM `BizTransaction` bt WHERE bt.type=("
                            + vocBizTransTypeId + ")) AS SelectedBizTrans";
                    String bizTransId = "SELECT bizTransId FROM "
                            + vocBizTransIds
                            + " INNER JOIN "
                            + selectedBizTrans
                            + " ON SelectedBizTrans.bizTrans=SelectedVocBizTrans.vocBizTransId";

                    query.append(" AND (`event_" + eventType + "`.id IN (");
                    query.append("SELECT event_id AS id ");
                    query.append("FROM `event_" + eventType + "_bizTrans` ");
                    query.append("INNER JOIN (");
                    query.append(bizTransId);
                    query.append(") AS BizTransIds ");
                    query.append("ON BizTransIds.bizTransId=`event_"
                            + eventType + "_bizTrans`.bizTrans_id");
                    query.append("))");

                } else if (paramName.equals("MATCH_epc")
                        || paramName.equals("MATCH_parentID")
                        || paramName.equals("MATCH_anyEPC")
                        || paramName.equals("MATCH_epcClass")) {
                    if ((paramName.equals("MATCH_epc") || paramName.equals("MATCH_anyEPC"))
                            && !eventType.equals("QuantityEvent")) {
                        String[] epcs = ((ArrayOfString) paramValue).getString();
                        query.append(" AND (`event_");
                        query.append(eventType);
                        query.append("`.id IN (");
                        query.append("SELECT event_id FROM `event_");
                        query.append(eventType);
                        query.append("_EPCs` WHERE ");
                        for (int j = 0; j < epcs.length; j++) {
                            String val = epcs[j].replaceAll("*", "%");
                            query.append("epc LIKE '" + val + "' OR ");
                        }
                        query.append("0))");

                    } else if ((paramName.equals("MATCH_parentID") || paramName.equals("MATCH_anyEPC"))
                            && (eventType.equals("AggregationEvent") || eventType.equals("TransactionEvent"))) {
                        query.append(" AND (parentID IN (");
                        String[] epcs = ((ArrayOfString) paramValue).getString();
                        stringArrayToSQL(epcs, query, queryArgs);
                        query.append("))");

                    } else if (paramName.equals("MATCH_epcClass")
                            && eventType.equals("QuantityEvent")) {
                        query.append(" AND (epcClass IN (");
                        query.append("SELECT id FROM `voc_EPCClass` WHERE uri IN (");
                        String[] epcs = ((ArrayOfString) paramValue).getString();
                        stringArrayToSQL(epcs, query, queryArgs);
                        query.append(")))");

                    } else {
                        // the parameter is not allowed for this queryType
                        query.append(" AND 0 ");
                    }

                } else if (paramName.equals("MATCH_childEPC")) {
                    // TODO: this parameter is only kept for consistency to an
                    // older draft version of the EPCIS specification and to the
                    // test kit. it should be removed!
                    if (eventType.equals("AggregationEvent")) {
                        query.append(" AND (`event_AggregationEvent`.id IN (");
                        query.append("SELECT event_id FROM `event_AggregationEvent_EPCs` WHERE epc IN (");
                        String[] epcs = ((ArrayOfString) paramValue).getString();
                        stringArrayToSQL(epcs, query, queryArgs);
                        query.append(")))");
                    } else {
                        query.append(" AND 0 ");
                    }

                } else if (paramName.endsWith("_quantity")) {
                    if (eventType.equals("QuantityEvent")) {
                        String op = "=";
                        if (paramName.startsWith("GT")) {
                            op = ">";
                        } else if (paramName.startsWith("GE")) {
                            op = ">=";
                        } else if (paramName.startsWith("LT")) {
                            op = "<";
                        } else if (paramName.startsWith("LE")) {
                            op = "<=";
                        }
                        query.append(" AND (quantity");
                        query.append(op);
                        query.append("?) ");
                        queryArgs.add(((Integer) paramValue).toString());
                    } else {
                        query.append(" AND 0 ");
                    }

                } else if (paramName.startsWith("GT_")
                        || paramName.startsWith("GE_")
                        || paramName.startsWith("EQ_")
                        || paramName.startsWith("LE_")
                        || paramName.startsWith("LT_")) {

                    // check if this is an extension field
                    String fieldname = paramName.substring(3);
                    String[] parts = fieldname.split("#");
                    if (parts.length != 2) {
                        String msg = "The parameter " + paramName
                                + " cannot be recognised.";
                        LOG.info("USER ERROR: " + msg);
                        throw new QueryParameterException(msg);
                    }

                    // extract the operand
                    String op = "=";
                    if (paramName.startsWith("GT_")) {
                        op = ">";
                    } else if (paramName.startsWith("GE_")) {
                        op = ">=";
                    } else if (paramName.startsWith("LE_")) {
                        op = "<=";
                    } else if (paramName.startsWith("LT_")) {
                        op = "<";
                    }

                    // determine WHERE clause (depending on param value)
                    String where;
                    try {
                        Integer intVal = (Integer) paramValue;
                        where = "intValue" + op + intVal;
                    } catch (ClassCastException e1) {
                        try {
                            Float floatVal = (Float) paramValue;
                            where = "floatValue" + op + floatVal;
                        } catch (ClassCastException e2) {
                            try {
                                Calendar cal = TimeParser.parseAsCalendar(paramValue.toString());
                                Timestamp ts = new Timestamp(
                                        cal.getTimeInMillis());
                                where = "dateValue" + op + "\"" + ts.toString()
                                        + "\"";
                            } catch (ParseException e) {
                                try {
                                    String[] strVals = ((ArrayOfString) paramValue).getString();
                                    StringBuffer sb = new StringBuffer();
                                    sb.append("strValue IN (");
                                    int j = 0;
                                    while (j < strVals.length - 1) {
                                        sb.append("'" + strVals[j] + "',");
                                        j++;
                                    }
                                    sb.append("'" + strVals[j] + "')");
                                    where = sb.toString();
                                } catch (ClassCastException e3) {
                                    String strVal = paramValue.toString();
                                    where = "strValue" + op + "\"" + strVal
                                            + "\"";
                                }
                            }
                        }
                    }
                    query.append(" AND `event_" + eventType + "_extensions`.");
                    query.append(where);
                    query.append(" AND `event_" + eventType
                            + "_extensions`.fieldname=\"");
                    query.append(fieldname);
                    query.append("\"");

                } else if (paramName.startsWith("EXISTS_")) {
                    String fieldname = paramName.substring(7);
                    if (fieldname.equals("childEPCs")) {
                        if (eventType.equals("AggregationEvent")) {
                            query.append(" AND (`event_AggregationEvent`.id IN (");
                            query.append("SELECT event_id FROM `event_AggregationEvent_EPCs`");
                            query.append("))");
                        } else {
                            query.append(" AND 0");
                        }
                    } else if (fieldname.equals("epcList")) {
                        query.append(" AND (`event_" + eventType + "`.id IN (");
                        query.append("SELECT event_id FROM `event_" + eventType
                                + "_EPCs`");
                        query.append("))");
                    } else if (fieldname.equals("bizTransactionList")) {
                        query.append(" AND (`event_" + eventType + "`.id IN (");
                        query.append("SELECT event_id FROM `event_" + eventType
                                + "_bizTrans`");
                        query.append("))");
                    } else {
                        // lets see if we have an extension fieldname
                        String[] parts = fieldname.split("#");
                        if (parts.length != 2) {
                            // nope, no extension fieldname, just check if
                            // the given type exists
                            query.append(" AND (?) ");
                            queryArgs.add(fieldname);
                        } else {
                            // yep, extension fieldname: check extension table
                            query.append(" AND (`event_" + eventType
                                    + "`.id IN (");
                            query.append("SELECT event_id FROM `event_"
                                    + eventType + "_extensions` ");
                            query.append("WHERE fieldname='" + fieldname + "'");
                            query.append("))");
                        }
                    }

                } else if (paramName.startsWith("HASATTR_")
                        || paramName.startsWith("EQATTR_")) {
                    // parse fieldname and attrname from paramName
                    String fieldname = paramName.substring(8);
                    String attrname = null;
                    if (paramName.startsWith("EQATTR_")) {
                        fieldname = paramName.substring(7);
                        String[] parts = fieldname.split("_");
                        if (parts.length > 2) {
                            String msg = "Parameter '"
                                    + paramName
                                    + "' is invalid as it does not follow the pattern 'EQATTR_fieldname_attrname'.";
                            LOG.info("USER ERROR: " + msg);
                            throw new QueryParameterException(msg);
                        } else if (parts.length == 2) {
                            // restrict also by attrname
                            fieldname = parts[0];
                            attrname = parts[1];
                        }
                    }

                    // get correct tablename for voc table
                    String tablename = null;
                    String biztrans = null;
                    if (fieldname.equalsIgnoreCase("epcClass")
                            && eventType.equals("QuantityEvent")) {
                        tablename = "EPCClass";
                    } else if (fieldname.equalsIgnoreCase("bizStep")) {
                        tablename = "BizStep";
                    } else if (fieldname.equalsIgnoreCase("disposition")) {
                        tablename = "Disposition";
                    } else if (fieldname.equalsIgnoreCase("readPoint")) {
                        tablename = "ReadPoint";
                    } else if (fieldname.equalsIgnoreCase("bizLocation")) {
                        tablename = "BizLoc";
                    } else if (fieldname.equalsIgnoreCase("bizTransaction")) {
                        tablename = "BizTrans";
                        biztrans = "bizTrans";
                    } else if (fieldname.equalsIgnoreCase("type")) {
                        tablename = "BizTransType";
                        biztrans = "type";
                    } else {
                        // try to parse fieldname as extension
                        String[] parts = fieldname.split("#");
                        if (parts.length == 2) {
                            // TODO: do we need attributes for fieldname
                            // extensions?
                            String msg = "Attributes for fieldname extensions not implemented.";
                            LOG.warn(msg);
                            throw new UnsupportedOperationException(msg);
                        }
                    }

                    // construct the query
                    if (tablename != null) {
                        if (biztrans != null) {
                            query.append(" AND `event_");
                            query.append(eventType);
                            query.append("`.id IN (SELECT event_id FROM `event_");
                            query.append(eventType);
                            query.append("_BizTrans` AS btrans WHERE btrans.bizTrans_id IN (");
                            query.append("SELECT id FROM `BizTransaction` WHERE `BizTransaction`.");
                            query.append(biztrans);
                            query.append(" IN (SELECT id FROM `voc_");
                            query.append(tablename);
                            query.append("` WHERE ");
                        } else {
                            query.append(" AND ");
                        }
                        query.append("`voc_");
                        query.append(tablename);
                        query.append("`.id IN (SELECT id FROM `voc_");
                        query.append(tablename);
                        query.append("_attr` WHERE `voc_");
                        query.append(tablename);
                        query.append("_attr`.attribute");
                        if (attrname != null) {
                            query.append("=? AND `voc_");
                            query.append(tablename);
                            query.append("_attr`.value");
                            queryArgs.add(attrname);
                        }
                        query.append(" IN (");
                        ArrayOfString attrs = (ArrayOfString) paramValue;
                        stringArrayToSQL(attrs.getString(), query, queryArgs);
                        query.append("))");
                        if (biztrans != null) {
                            query.append(")))");
                        }
                    }

                } else if (paramName.equals("orderBy")) {
                    orderBy = (String) paramValue;

                } else if (paramName.equals("orderDirection")) {
                    orderDirection = (String) paramValue;

                } else if (paramName.equals("eventCountLimit")) {
                    limit = (Integer) paramValue;

                } else if (paramName.equals("maxEventCount")) {
                    maxEventCount = ((Integer) paramValue).intValue();
                } else {
                    String msg = "The parameter " + paramName
                            + " cannot be recognised.";
                    LOG.info("USER ERROR: " + msg);
                    throw new QueryParameterException(msg);
                }
            } catch (ClassCastException e) {
                String msg = "The input value for parameter " + paramName
                        + " (" + paramValue + ") of eventType " + eventType
                        + " is not of the type required.";
                LOG.info("USER ERROR: " + msg);
                throw new QueryParameterException(msg);
            }
        }

        if (maxEventCount > -1 && limit > -1) {
            String msg = "Paramters 'maxEventCount' and 'eventCountLimit' are mutually exclusive.";
            LOG.info("USER ERROR: " + msg);
            throw new QueryParameterException(msg);
        }

        if (orderBy.equals("") && limit > -1) {
            String msg = "Parameter 'eventCountLimit' may only be used when 'orderBy' is specified.";
            LOG.info("USER ERROR: " + msg);
            throw new QueryParameterException(msg);
        }

        if (!orderBy.equals("")) {
            query.append(" ORDER BY ");
            query.append(orderBy);
            query.append(" " + orderDirection);
        }

        if (limit > -1 && limit <= maxQueryRows) {
            query.append(" LIMIT " + limit);
        } else if (maxEventCount > -1 && maxEventCount <= maxQueryRows) {
            query.append(" LIMIT " + maxEventCount);
        } else {
            query.append(" LIMIT " + maxQueryRows);
        }

        String q = query.toString();
        String qs = q.replaceAll("`", delimiter);
        LOG.debug("QUERY: " + qs);
        PreparedStatement ps = dbconnection.prepareStatement(qs);
        for (int i = 0; i < queryArgs.size(); i++) {
            ps.setString(i + 1, (String) queryArgs.get(i));
            LOG.debug("       query param " + (i + 1) + ": " + queryArgs.get(i));
        }
        return ps;
    }

    /**
     * @see org.accada.epcis.soapapi.EPCISServicePortType#getQueryNames(org.accada.epcis.soapapi.EmptyParms)
     * @param parms
     *            An empty parameter.
     * @return An ArrayOfString containing the names of all implemented queries.
     */
    public ArrayOfString getQueryNames(final EmptyParms parms) {
        ArrayOfString qNames = new ArrayOfString();
        String[] qNamesArray = new String[queryNames.size()];
        qNamesArray = queryNames.toArray(qNamesArray);
        qNames.setString(qNamesArray);
        return qNames;
    }

    /**
     * Subscribes a query.
     * 
     * @see org.accada.epcis.soapapi.EPCISServicePortType#subscribe(org.accada.epcis.soapapi.Subscribe)
     * @param parms
     *            A Subscribe object containing the query to be subscribed..
     * @return Nothing.
     * @throws ImplementationException
     *             If a problem with the EPCIS implementation occured.
     * @throws InvalidURIException
     *             If an invalid URI where the query results should be posted is
     *             provided.
     * @throws SubscribeNotPermittedException
     *             If a SimpleMasterDataQuery is provided which is only valid
     *             for polling.
     * @throws SubscriptionControlsException
     *             If one of the SubscriptionControls parameters is not set.
     * @throws ValidationException
     *             If the query is not valid.
     * @throws DuplicateSubscriptionException
     *             If a query with the given ID is already subscribed.
     * @throws NoSuchNameException
     *             If a query name is not implemented yet.
     */
    public VoidHolder subscribe(final Subscribe parms)
            throws ImplementationException, InvalidURIException,
            SubscribeNotPermittedException, SubscriptionControlsException,
            ValidationException, DuplicateSubscriptionException,
            NoSuchNameException {
        QueryParam[] qParams = parms.getParams();
        URI dest = parms.getDest();
        String subscrId = parms.getSubscriptionID();
        SubscriptionControls controls = parms.getControls();
        String queryName = parms.getQueryName();
        GregorianCalendar initialRecordTime = (GregorianCalendar) parms.getControls().getInitialRecordTime();
        if (initialRecordTime == null) {
            initialRecordTime = new GregorianCalendar();
        }

        try {
            // a few input sanity checks

            // dest may be null or empty. But we don't support pre-arranged
            // destinations and throw an InvalidURIException according to the
            // standard.
            if (dest == null || dest.toString().equals("")) {
                String msg = "Destination URI is empty. This implementation doesn't support pre-arranged destinations.";
                LOG.info("USER ERROR: " + msg);
                throw new InvalidURIException(msg);
            }
            try {
                new URL(dest.toString());
            } catch (MalformedURLException e) {
                String msg = "Destination URI is invalid: " + e.getMessage();
                LOG.info("USER ERROR: " + msg);
                throw new InvalidURIException(msg);
            }

            // check query name
            if (!queryNames.contains(queryName)) {
                String msg = "Illegal query name '" + queryName + "'.";
                LOG.info("USER ERROR: " + msg);
                throw new NoSuchNameException(msg);
            }

            // SimpleMasterDataQuery only valid for polling
            if (queryName.equals("SimpleMasterDataQuery")) {
                String msg = "Subscription not allowed for SimpleMasterDataQuery.";
                LOG.info("USER ERROR: " + msg);
                throw new SubscribeNotPermittedException(msg);
            }

            // subscriptionID mustn't be empty.
            if (subscrId == null || subscrId.equals("")) {
                String msg = "SubscriptionID is empty. Choose a valid subscriptionID.";
                LOG.info(msg);
                throw new ValidationException(msg);
            }

            // subscriptionID mustn't exist yet.
            if (fetchExistsSubscriptionId(subscrId)) {
                String msg = "SubscriptionID '"
                        + subscrId
                        + "' already exists. Choose a different subscriptionID.";
                LOG.info("USER ERROR: " + msg);
                throw new DuplicateSubscriptionException(msg);
            }

            // trigger and schedule may no be used together, but one of them
            // must be set
            if (controls.getSchedule() != null && controls.getTrigger() != null) {
                String msg = "Schedule and trigger mustn't be used together.";
                LOG.info("USER ERROR: " + msg);
                throw new SubscriptionControlsException(msg);
            }
            if (controls.getSchedule() == null && controls.getTrigger() == null) {
                String msg = "Either schedule or trigger has to be set.";
                LOG.info("USER ERROR: " + msg);
                throw new SubscriptionControlsException(msg);
            }
            if (controls.getSchedule() == null && controls.getTrigger() != null) {
                String msg = "Triggers are not supported.";
                LOG.info("USER ERROR: " + msg);
                throw new SubscriptionControlsException(msg);
            }

            // parse schedule
            Schedule schedule = new Schedule(controls.getSchedule());

            // load subscriptions
            Map<String, QuerySubscriptionScheduled> subscribedMap = loadSubscriptions();

            QuerySubscriptionScheduled newSubscription = new QuerySubscriptionScheduled(
                    subscrId, qParams, dest, controls.isReportIfEmpty(),
                    initialRecordTime, initialRecordTime, schedule, queryName);

            // store the Query to the database
            String insert = "INSERT INTO subscription (subscriptionid, "
                    + "params, dest, sched, trigg, initialrecordingtime, "
                    + "exportifempty, queryname, lastexecuted) VALUES "
                    + "((?), (?), (?), (?), (?), (?), (?), (?), (?))";
            PreparedStatement stmt = dbconnection.prepareStatement(insert);
            LOG.debug("QUERY: " + insert);
            try {
                stmt.setString(1, subscrId);
                LOG.debug("       query param 1: " + subscrId);

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                ObjectOutput out = new ObjectOutputStream(outStream);
                out.writeObject(qParams);
                ByteArrayInputStream inStream = new ByteArrayInputStream(
                        outStream.toByteArray());
                stmt.setBinaryStream(2, inStream, inStream.available());
                LOG.debug("       query param 2: [" + inStream.available()
                        + " bytes]");

                stmt.setString(3, dest.toString());
                LOG.debug("       query param 3: " + dest.toString());

                outStream = new ByteArrayOutputStream();
                out = new ObjectOutputStream(outStream);
                out.writeObject(schedule);
                inStream = new ByteArrayInputStream(outStream.toByteArray());
                stmt.setBinaryStream(4, inStream, inStream.available());
                LOG.debug("       query param 4: [" + inStream.available()
                        + " bytes]");

                stmt.setString(5, "");
                LOG.debug("       query param 5: ");

                Calendar cal = newSubscription.getInitialRecordTime();
                Timestamp ts = new Timestamp(cal.getTimeInMillis());
                String time = ts.toString();
                stmt.setString(6, time);
                LOG.debug("       query param 6: " + time);

                stmt.setBoolean(7, controls.isReportIfEmpty());
                LOG.debug("       query param 7: " + controls.isReportIfEmpty());

                stmt.setString(8, queryName);
                LOG.debug("       query param 8: " + queryName);

                stmt.setString(9, time);
                LOG.debug("       query param 9: " + time);

                stmt.executeUpdate();
            } catch (IOException e) {
                String msg = "Unable to store the subscription to the database: "
                        + e.getMessage();
                LOG.error(msg);
                ImplementationException iex = new ImplementationException();
                iex.setReason(msg);
                iex.setStackTrace(e.getStackTrace());
                iex.setSeverity(ImplementationExceptionSeverity.ERROR);
                throw iex;
            }

            // store the new Query to the HashMap
            subscribedMap.put(subscrId, newSubscription);
            saveSubscriptions(subscribedMap);

            return new VoidHolder();
        } catch (SQLException e) {
            String msg = "SQL error during query execution: " + e.getMessage();
            LOG.error(msg, e);
            ImplementationException iex = new ImplementationException();
            iex.setReason(msg);
            iex.setStackTrace(e.getStackTrace());
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw iex;
        }
    }

    /**
     * This method loads all the stored queries from the database, starts them
     * again and stores everything in a HasMap.
     * 
     * @return A Map mapping query names to scheduled query subscriptions.
     * @throws SQLException
     *             If a problem with the database occured.
     * @throws ImplementationException
     *             If a problem with the EPCIS implementation occured.
     */
    private Map<String, QuerySubscriptionScheduled> fetchSubscriptions()
            throws SQLException, ImplementationException {
        String query = "SELECT * FROM subscription";
        LOG.debug("QUERY: " + query);
        Statement stmt = dbconnection.createStatement();

        GregorianCalendar initrectime = new GregorianCalendar();

        ResultSet rs = stmt.executeQuery(query);
        Map<String, QuerySubscriptionScheduled> subscribedMap = new HashMap<String, QuerySubscriptionScheduled>();
        while (rs.next()) {
            try {
                String subscrId = rs.getString("subscriptionid");

                ObjectInput in = new ObjectInputStream(
                        rs.getBinaryStream("params"));

                QueryParam[] params = (QueryParam[]) in.readObject();
                URI dest = stringToUri(rs.getString("dest"));

                in = new ObjectInputStream(rs.getBinaryStream("sched"));
                Schedule sched = (Schedule) in.readObject();

                initrectime.setTime(rs.getTimestamp("initialrecordingtime"));

                boolean exportifempty = rs.getBoolean("exportifempty");

                String queryName = rs.getString("queryname");

                QuerySubscriptionScheduled newSubscription = new QuerySubscriptionScheduled(
                        subscrId, params, dest, exportifempty, initrectime,
                        new GregorianCalendar(), sched, queryName);
                subscribedMap.put(subscrId, newSubscription);
            } catch (SQLException e) {
                // sql exceptions are passed on
                throw e;
            } catch (Exception e) {
                // all other exceptions are caught
                String msg = "Unable to restore subscribed queries from the database.";
                LOG.error(msg, e);
                ImplementationException iex = new ImplementationException();
                iex.setReason(msg);
                iex.setStackTrace(e.getStackTrace());
                iex.setSeverity(ImplementationExceptionSeverity.SEVERE);
                throw iex;
            }
        }
        rs.close();
        return subscribedMap;
    }

    /**
     * Stops a subscribed query from further invocations.
     * 
     * @see org.accada.epcis.soapapi.EPCISServicePortType#unsubscribe(org.accada.epcis.soapapi.Unsubscribe)
     * @param parms
     *            An Unsubscribe object containing the ID of the query to be
     *            unsubscribed.
     * @return Nothing.
     * @throws ImplementationException
     *             If a problem with the EPCIS implementation occured.
     * @throws NoSuchSubscriptionException
     *             If the suscription id is not subscribed.
     */
    public VoidHolder unsubscribe(final Unsubscribe parms)
            throws ImplementationException, NoSuchSubscriptionException {
        try {
            Map<String, QuerySubscriptionScheduled> subscribedMap = loadSubscriptions();
            String subscrId = parms.getSubscriptionID();

            if (subscribedMap.containsKey(subscrId)) {
                // remove subscription from local hash map
                QuerySubscriptionScheduled toDelete = subscribedMap.get(subscrId);
                toDelete.stopSubscription();
                subscribedMap.remove(subscrId);
                saveSubscriptions(subscribedMap);

                // delete subscription from database
                String delete = "DELETE FROM subscription WHERE "
                        + "subscriptionid = (?)";
                PreparedStatement stmt = dbconnection.prepareStatement(delete);
                stmt.setString(1, subscrId);
                LOG.debug("QUERY: " + delete);
                LOG.debug("        query param 1: " + subscrId);

                stmt.executeUpdate();
                return new VoidHolder();
            } else {
                String msg = "There is no subscription with ID '" + subscrId
                        + "'.";
                LOG.info("USER ERROR: " + msg);
                throw new NoSuchSubscriptionException(msg);
            }
        } catch (SQLException e) {
            ImplementationException iex = new ImplementationException();
            String msg = "SQL error during query execution: " + e.getMessage();
            LOG.error(msg, e);
            iex.setReason(msg);
            iex.setStackTrace(e.getStackTrace());
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw iex;
        }
    }

    /**
     * Saves the map with the subscriptions to the message context.
     * 
     * @param subscribedMap
     *            The map with the subscriptions.
     */
    private void saveSubscriptions(
            final Map<String, QuerySubscriptionScheduled> subscribedMap) {
        MessageContext msgContext = MessageContext.getCurrentContext();
        msgContext.setProperty("subscribedMap", subscribedMap);
    }

    /**
     * Retrieves the map with the subscriptions from the servlet context.
     * 
     * @return The map with the subscriptions.
     * @throws ImplementationException
     *             If the map could not be reloaded.
     * @throws SQLException
     *             If a database error occured.
     */
    private Map<String, QuerySubscriptionScheduled> loadSubscriptions()
            throws ImplementationException, SQLException {
        MessageContext msgContext = MessageContext.getCurrentContext();
        Map<String, QuerySubscriptionScheduled> subscribedMap = (HashMap<String, QuerySubscriptionScheduled>) msgContext.getProperty("subscribedMap");
        if (subscribedMap == null) {
            subscribedMap = fetchSubscriptions();
        }
        return subscribedMap;
    }

    /**
     * Returns an ArrayOfString containing IDs of all subscribed queries.
     * 
     * @see org.accada.epcis.soapapi.EPCISServicePortType#getSubscriptionIDs(org.accada.epcis.soapapi.GetSubscriptionIDs)
     * @param parms
     *            An empty parameter.
     * @return An ArrayOfString containing IDs of all subscribed queries.
     * @throws ImplementationException
     *             If a problem with the EPCIS implementation occured.
     */
    public ArrayOfString getSubscriptionIDs(final GetSubscriptionIDs parms)
            throws ImplementationException {
        try {
            Map<String, QuerySubscriptionScheduled> subscribedMap = loadSubscriptions();
            String[] temp = {};
            temp = subscribedMap.keySet().toArray(temp);
            ArrayOfString arrOfStr = new ArrayOfString();
            arrOfStr.setString(temp);
            return arrOfStr;
        } catch (SQLException e) {
            ImplementationException iex = new ImplementationException();
            String msg = "SQL error during query execution: " + e.getMessage();
            LOG.error(msg, e);
            iex.setReason(msg);
            iex.setStackTrace(e.getStackTrace());
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw iex;
        }
    }

    /**
     * Runs (polls) a query.
     * 
     * @see org.accada.epcis.soapapi.EPCISServicePortType#poll(org.accada.epcis.soapapi.Poll)
     * @param parms
     *            The query to poll.
     * @return A QueryResults object containing the result of the query.
     * @throws ImplementationException
     *             If a problem with the EPCIS implementation occured.
     * @throws QueryTooLargeException
     *             If the query is too large.
     * @throws QueryParameterException
     *             If one of the query parameters is invalid.
     * @throws NoSuchNameException
     *             If an invalid query type was provided.
     * @throws QueryTooComplexException
     *             If a query takes too long to return.
     */
    public QueryResults poll(final Poll parms) throws ImplementationException,
            QueryTooLargeException, QueryParameterException,
            NoSuchNameException, QueryTooComplexException {

        // query type must be implemented.
        if (!queryNames.contains(parms.getQueryName())) {
            String msg = "Invalid query name '" + parms.getQueryName()
                    + "' provided.";
            LOG.info("USER ERROR: " + msg);
            throw new NoSuchNameException(msg);
        }

        String queryName = parms.getQueryName();
        if (queryName.equals("SimpleEventQuery")) {
            try {
                QueryParam[] queryParams = parms.getParams();

                PreparedStatement ps = createEventQuery(queryParams,
                        "ObjectEvent");
                ObjectEventType[] tempObjectEvent = runObjectEventQuery(ps);

                ps = createEventQuery(queryParams, "AggregationEvent");
                AggregationEventType[] tempAggrEvent = runAggregationEventQuery(ps);

                ps = createEventQuery(queryParams, "QuantityEvent");
                QuantityEventType[] tempQuantityEvent = runQuantityEventQuery(ps);

                ps = createEventQuery(queryParams, "TransactionEvent");
                TransactionEventType[] tempTransEvent = runTransactionEventQuery(ps);

                // construct QueryResults
                EventListType eventList = new EventListType();
                eventList.setObjectEvent(tempObjectEvent);
                eventList.setAggregationEvent(tempAggrEvent);
                eventList.setQuantityEvent(tempQuantityEvent);
                eventList.setTransactionEvent(tempTransEvent);

                QueryResultsBody resultsBody = new QueryResultsBody();
                resultsBody.setEventList(eventList);

                QueryResults results = new QueryResults();
                results.setResultsBody(resultsBody);
                results.setQueryName(queryName);

                LOG.info("poll request for '" + queryName + "' succeeded");
                return results;
            } catch (SQLException e) {
                ImplementationException iex = new ImplementationException();
                String msg = "SQL error during query execution: "
                        + e.getMessage();
                LOG.error(msg, e);
                iex.setReason(msg);
                iex.setStackTrace(e.getStackTrace());
                iex.setSeverity(ImplementationExceptionSeverity.ERROR);
                throw iex;
            }
        } else if (queryName.equals("SimpleMasterDataQuery")) {
            QueryParam[] queryParams = parms.getParams();
            try {
                QueryResults results = createMasterDataQuery(queryParams);

                LOG.info("poll request for '" + queryName + "' succeeded");
                return results;
            } catch (SQLException e) {
                ImplementationException iex = new ImplementationException();
                String msg = "SQL error during query execution: "
                        + e.getMessage();
                LOG.error(msg, e);
                iex.setReason(msg);
                iex.setStackTrace(e.getStackTrace());
                iex.setSeverity(ImplementationExceptionSeverity.ERROR);
                throw iex;
            }
        } else {
            String msg = "Unsupported query name '" + parms.getQueryName()
                    + "' provided.";
            LOG.info("USER ERROR: " + msg);
            throw new NoSuchNameException(msg);
        }
    }

    /**
     * Runs a MasterDataQuery for the given QueryParam array and returns the
     * QueryResults.
     * 
     * @param queryParams
     *            The parameters for running the MasterDataQuery.
     * @return The QueryResults.
     * @throws SQLException
     *             If an error accessing the database occured.
     * @throws QueryParameterException
     *             If one of the provided QueryParam is invalid.
     * @throws ImplementationException
     *             If a service implementation error occured.
     * @throws QueryTooLargeException
     *             If the query is too large to be executed.
     */
    private QueryResults createMasterDataQuery(final QueryParam[] queryParams)
            throws SQLException, QueryParameterException,
            ImplementationException, QueryTooLargeException {

        // populate a sorted map with the given parameters
        List<QueryParam> paramList = Arrays.asList(queryParams);
        SortedMap<String, Object> params = new TreeMap<String, Object>();
        for (QueryParam param : paramList) {
            params.put(param.getName(), param.getValue());
        }

        // check for parameter 'includeAttributes'
        boolean includeAttributes = false;
        try {
            Object val = params.remove("includeAttributes");
            includeAttributes = Boolean.parseBoolean(val.toString());
            // defaults to 'false' if an invalid value is provided!
        } catch (NullPointerException e) {
            String msg = "Invalid MasterDataQuery: missing required parameter 'includeAttributes' or invalid value provided.";
            LOG.info("USER ERROR: " + msg, e);
            throw new QueryParameterException(msg);
        }

        // check for parameter 'includeChildren'
        boolean includeChildren = false;
        try {
            Object val = params.remove("includeChildren");
            includeChildren = Boolean.parseBoolean(val.toString());
            // defaults to 'false' if an invalid value is provided!
        } catch (NullPointerException e) {
            String msg = "Invalid MasterDataQuery: missing required parameter 'includeChildren' or invalid value provided.";
            LOG.info("USER ERROR: " + msg);
            throw new QueryParameterException(msg);
        }

        // fetch vocabulary table names
        String[] uris = new String[0];
        if (params.containsKey("vocabularyName")) {
            Object val = params.remove("vocabularyName");
            uris = ((ArrayOfString) val).getString();
        }
        Map<String, URI> tableNames = fetchVocabularyTableNames(uris);

        // filter vocabularies by name
        String[] filterVocNames = new String[0];
        if (params.containsKey("EQ_name")) {
            Object val = params.remove("EQ_name");
            filterVocNames = ((ArrayOfString) val).getString();
        }

        // filter vocabularies by name with desendants
        String[] filterVocNamesWd = new String[0];
        if (params.containsKey("WD_name")) {
            Object val = params.remove("WD_name");
            filterVocNamesWd = ((ArrayOfString) val).getString();
        }

        // filter vocabularies by attribute name
        String[] filterVocAttrNames = new String[0];
        if (params.containsKey("HASATTR")) {
            Object val = params.remove("HASATTR");
            filterVocAttrNames = ((ArrayOfString) val).getString();
        }

        // filter vocabularies by attribute value
        Map<String, String[]> filterAttrs = new HashMap<String, String[]>();
        for (String param : params.keySet()) {
            if (param.startsWith("EQATTR_")) {
                String attrname = param.substring(7);
                Object val = params.remove(param);
                String[] values = ((ArrayOfString) val).getString();
                filterAttrs.put(attrname, values);
            }
        }

        // filter attributes by name
        String[] filterAttrNames = new String[0];
        if (params.containsKey("attributeNames") && includeAttributes) {
            Object val = params.remove("attributeNames");
            filterAttrNames = ((ArrayOfString) val).getString();
        }

        // filter number of returned elements
        int maxElementCount = -1;
        if (params.containsKey("maxElementCount")) {
            Object val = params.remove("maxElementCount");
            maxElementCount = ((Integer) val).intValue();
        }

        List<VocabularyType> vocList = new ArrayList<VocabularyType>();
        // handle each vocabulary table
        for (String vocTableName : tableNames.keySet()) {
            List<VocabularyElementType> vocElemList = new ArrayList<VocabularyElementType>();

            // fetch all vocabularies filtered by the given arguments
            List<URI> vocs = fetchVocabularies(vocTableName, filterVocNames,
                    filterVocNamesWd, filterAttrs, filterVocAttrNames,
                    maxElementCount);

            // handle each vocabulary element
            for (URI voc : vocs) {
                VocabularyElementType vocElem = new VocabularyElementType();
                vocElem.setId(voc);

                AttributeType[] attributes = null;
                if (includeAttributes) {
                    List<AttributeType> attrList = new ArrayList<AttributeType>();

                    // fetch all attributes for current vocabulary element
                    Map<String, String> attrMap = fetchAttributes(vocTableName,
                            voc.toString(), filterAttrNames);

                    // handle each attribute element
                    for (String attrId : attrMap.keySet()) {
                        AttributeType attr = new AttributeType();
                        attr.setId(stringToUri(attrId));
                        String attrValue = attrMap.get(attrId);
                        // attr value must be set with a text message element
                        MessageElement[] val = new MessageElement[] {
                            new MessageElement(new Text(attrValue))
                        };
                        attr.set_any(val);
                        attrList.add(attr);
                    }
                    if (attrList.size() > 0) {
                        attributes = new AttributeType[attrList.size()];
                        attributes = attrList.toArray(attributes);
                    }
                }
                vocElem.setAttribute(attributes);

                URI[] children = null;
                if (includeChildren) {
                    // fetch all children for current vocabulary element
                    List<URI> childrenList = fetchChildren(vocTableName,
                            voc.toString());
                    if (childrenList.size() > 0) {
                        children = new URI[childrenList.size()];
                        children = childrenList.toArray(children);
                    }
                }
                vocElem.setChildren(children);
                vocElemList.add(vocElem);
            }
            VocabularyElementType[] vocElems = null;
            if (vocElemList.size() > 0) {
                vocElems = new VocabularyElementType[vocElemList.size()];
                vocElems = vocElemList.toArray(vocElems);

                VocabularyType voc = new VocabularyType();
                voc.setVocabularyElementList(vocElems);
                voc.setType(tableNames.get(vocTableName));
                vocList.add(voc);
            }
        }
        VocabularyType[] vocs = null;
        if (vocList.size() > 0) {
            vocs = new VocabularyType[vocList.size()];
            vocs = vocList.toArray(vocs);
        }

        QueryResultsBody resultsBody = new QueryResultsBody();
        resultsBody.setVocabularyList(vocs);

        QueryResults results = new QueryResults();
        results.setQueryName("SimpleMasterDataQuery");
        results.setResultsBody(resultsBody);
        return results;
    }

    /**
     * Retrieves vocabularies filtered by the given arguments.
     * 
     * @param table
     *            The name of the vocabulary table in which to look.
     * @param filterVocNames
     *            A possibly empty array of vocabulary names which filter the
     *            returned vocabularies by name.
     * @param filterVocNamesWd
     *            A possibly empty array of vocabulary names which filter the
     *            returned vocabularies by the name of their descendants.
     * @param filterAttrs
     *            A possibly empty mapping of attribute names to attribute
     *            values which filter the returned vocabularies by their
     *            attribute names and values.
     * @param attrs
     *            A possibly empty array of attribute names which filter the
     *            returned vocabularies by their attribute names.
     * @param maxElementCount
     *            The maximum number of vocabularies that should be retrieved.
     *            If the actual number of vacabularies exceeds this number, a
     *            QueryTooLargeException is raised.
     * @return A List of vocabularies (URI) filtered by the given arguments.
     * @throws SQLException
     *             If an error accessing the database occured.
     * @throws ImplementationException
     *             If an error converting a String to an URI occured.
     * @throws QueryTooLargeException
     *             If the actual number of returned vocabularies would exceed
     *             the given maxElementCount.
     */
    private List<URI> fetchVocabularies(final String table,
            final String[] filterVocNames, final String[] filterVocNamesWd,
            final Map<String, String[]> filterAttrs, final String[] attrs,
            final int maxElementCount) throws SQLException,
            ImplementationException, QueryTooLargeException {
        List<URI> vocs = new ArrayList<URI>();

        StringBuffer sql = new StringBuffer();
        List<String> queryArgs = new ArrayList<String>();
        sql.append("SELECT DISTINCT uri FROM `voc_");
        sql.append(table);
        sql.append("` AS vocTable");
        // filter by attribute
        if (attrs.length > 0 || filterAttrs.size() > 0) {
            sql.append(", `voc_");
            sql.append(table);
            sql.append("_attr` AS attrTable ");
            sql.append("WHERE vocTable.id=attrTable.id");
            if (attrs.length > 0) {
                // filter by attribute name
                sql.append(" AND attrTable.attribute IN (");
                stringArrayToSQL(attrs, sql, queryArgs);
                sql.append(")");
            }
            if (filterAttrs.size() > 0) {
                // filter by attribute name & value
                for (String attrname : filterAttrs.keySet()) {
                    sql.append(" AND attrTable.attribute=?");
                    queryArgs.add(attrname);
                    sql.append(" AND attrTable.value IN (");
                    stringArrayToSQL(filterAttrs.get(attrname), sql, queryArgs);
                    sql.append(")");
                }
            }
        } else {
            sql.append(" WHERE 1");
        }
        if (filterVocNames.length > 0) {
            // filter by voc name
            sql.append(" AND vocTable.uri IN (");
            stringArrayToSQL(filterVocNames, sql, queryArgs);
            sql.append(")");
        }
        if (filterVocNamesWd.length > 0) {
            // filter by voc name with descendants
            sql.append(" AND (vocTable.uri LIKE ?");
            queryArgs.add(filterVocNamesWd[0] + "%");
            for (int i = 1; i < filterVocNamesWd.length; i++) {
                sql.append(" OR vocTable.uri LIKE ?");
                queryArgs.add(filterVocNamesWd[i] + "%");
            }
            sql.append(")");
        }
        sql.append(";");

        PreparedStatement ps = dbconnection.prepareStatement(sql.toString());
        LOG.debug("QUERY: " + sql);
        int i = 1;
        for (String arg : queryArgs) {
            LOG.debug("       query param " + i + ": " + arg);
            ps.setString(i++, arg);
        }
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            if (maxElementCount > -1 && count > maxElementCount) {
                String msg = "Actual number of vocabulary elements exceeds specified 'maxElementCount'.";
                LOG.info("USER ERROR: " + msg);
                QueryTooLargeException qtle = new QueryTooLargeException();
                qtle.setReason(msg);
                qtle.setQueryName("SimpleMasterDataQuery");
                throw qtle;
            }
            URI uri = stringToUri(rs.getString("uri"));
            vocs.add(uri);
            count++;
        }

        return vocs;
    }

    /**
     * Retrieves all vocabulary table names for the given vocabulary names.
     * 
     * @param uris
     *            A possibly empty array of vocabulary names.
     * @return A mapping from the table name to the vocabulary name.
     * @throws SQLException
     *             If an error accessing the databse occured.
     * @throws ImplementationException
     *             If an error converting a String to an URI occured.
     */
    private Map<String, URI> fetchVocabularyTableNames(final String[] uris)
            throws SQLException, ImplementationException {
        Map<String, URI> tableNames = new HashMap<String, URI>();

        List<String> queryArgs = new ArrayList<String>(uris.length);
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT table_name, uri FROM `vocabularies`");
        if (uris.length > 0) {
            sql.append(" WHERE uri IN (");
            stringArrayToSQL(uris, sql, queryArgs);
            sql.append(")");
        }
        sql.append(";");
        LOG.debug("QUERY: " + sql);
        PreparedStatement ps = dbconnection.prepareStatement(sql.toString());
        int i = 1;
        for (String arg : queryArgs) {
            LOG.debug("       query param " + i + ": " + arg);
            ps.setString(i++, arg);
        }
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String tableName = rs.getString("table_name");
            String uri = rs.getString("uri");
            tableNames.put(tableName, stringToUri(uri));
        }
        return tableNames;
    }

    /**
     * Retrieves all attributes for the given vocabulary name, filtered by the
     * given attribute names.
     * 
     * @param vocTableName
     *            The name of the vocabulary table in which to look for the
     *            attributes.
     * @param vocName
     *            The name (URI) of the vocabulary for which the attributes
     *            should be retrieved.
     * @param filterAttrNames
     *            A possibly empty array of attribute names which should filter
     *            the retrieved attributes.
     * @return The attributes, a mapping from attribute name to attribute value.
     * @throws SQLException
     *             If an error accessing the database occured.
     */
    private Map<String, String> fetchAttributes(final String vocTableName,
            final String vocName, final String[] filterAttrNames)
            throws SQLException {
        Map<String, String> attributes = new HashMap<String, String>();

        List<String> queryArgs = new ArrayList<String>(filterAttrNames.length);
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT attribute, value FROM `voc_");
        sql.append(vocTableName);
        sql.append("_attr` AS attrTable WHERE attrTable.id=(");
        sql.append("SELECT id FROM `voc_");
        sql.append(vocTableName);
        sql.append("` WHERE uri=?)");
        queryArgs.add(vocName);
        if (filterAttrNames.length > 0) {
            // filter by attribute names
            sql.append(" AND attribute IN (");
            stringArrayToSQL(filterAttrNames, sql, queryArgs);
            sql.append(");");
        } else {
            sql.append(";");
        }
        PreparedStatement ps = dbconnection.prepareStatement(sql.toString());
        LOG.debug("QUERY: " + sql);
        int i = 1;
        for (String arg : queryArgs) {
            LOG.debug("       query param " + i + ": " + arg);
            ps.setString(i++, arg);
        }
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String attr = rs.getString("attribute");
            String val = rs.getString("value");
            attributes.put(attr, val);
        }
        return attributes;
    }

    /**
     * Retrieves all children URI for the given vocabulary uri in the given
     * vocabulary table.
     * 
     * @param vocTableName
     *            The name of the vocabulary table in which to look for the
     *            children uris.
     * @param vocUri
     *            The vocabulary uri string for which the children should be
     *            retrieved.
     * @return A List of URI containing the children.
     * @throws SQLException
     *             If a DB access error occured.
     * @throws ImplementationException
     *             If a String could not be converted into an URI.
     */
    private List<URI> fetchChildren(final String vocTableName,
            final String vocUri) throws SQLException, ImplementationException {
        List<URI> children = new ArrayList<URI>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DISTINCT uri FROM `voc_");
        sql.append(vocTableName);
        sql.append("` AS vocTable WHERE vocTable.uri LIKE ?;");

        PreparedStatement ps = dbconnection.prepareStatement(sql.toString());
        String arg = vocUri + "_%";
        LOG.debug("QUERY: " + sql);
        LOG.debug("       query param 1: " + arg);
        ps.setString(1, arg);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            URI uri = stringToUri(rs.getString("uri"));
            children.add(uri);
        }
        return children;
    }

    /**
     * Returns the standard version.
     * 
     * @see org.accada.epcis.soapapi.EPCISServicePortType#getStandardVersion(org.accada.epcis.soapapi.EmptyParms)
     * @param parms
     *            An empty parameter.
     * @return The standard version.
     */
    public String getStandardVersion(final EmptyParms parms) {
        return STD_VERSION;
    }

    /**
     * Returns the vendor version.
     * 
     * @see org.accada.epcis.soapapi.EPCISServicePortType#getVendorVersion(org.accada.epcis.soapapi.EmptyParms)
     * @param parms
     *            An empty parameter.
     * @return The vendor version.
     */
    public String getVendorVersion(final EmptyParms parms) {
        return VDR_VERSION;
    }

    /**
     * Checks if the given action values are valid, i.e. all values must be one
     * of ADD, OBSERVE, or DELETE. Throws an exception if one of the values is
     * invalid.
     * 
     * @param actions
     *            The action values to be checked.
     * @throws QueryParameterException
     *             If one of the action values are invalid.
     */
    private void checkActionValues(final String[] actions)
            throws QueryParameterException {
        for (int i = 0; i < actions.length; i++) {
            if (!(actions[i].equalsIgnoreCase("ADD")
                    || actions[i].equalsIgnoreCase("OBSERVE") || actions[i].equalsIgnoreCase("DELETE"))) {
                String msg = "Invalid value for parameter EQ_action: "
                        + actions[i]
                        + ". Must be one of ADD, OBSERVE, or DELETE.";
                LOG.info("USER ERROR: " + msg);
                throw new QueryParameterException(msg);
            }
        }
    }

    /**
     * Executes the given PreparedStatement and throws a
     * QueryTooComplexException if the query takes longer than the given
     * timeout.
     * 
     * @param ps
     *            The PreparedStatement to be executed.
     * @param timeout
     *            The time to wait for the query to finish.
     * @return The ResultSet from query execution.
     * @throws QueryTooComplexException
     *             If the query takes longer than the gven timeout.
     * @throws SQLException
     *             If the execution of the query threw an exception.
     */
    private ResultSet executeStatement(final PreparedStatement ps,
            final long timeout) throws QueryTooComplexException, SQLException {
        if (timeout > 0) {
            // start query execution in a new thread
            Query query = new Query(ps);
            query.start();

            // wait some time for the query to execute
            synchronized (query) {
                try {
                    query.wait(timeout);
                } catch (InterruptedException e) {
                }
            }

            // check if the query returned before the timeout
            ResultSet rs = query.getResultSet();
            query.checkException();
            if (rs == null) {
                // query has not yet finished and takes too long
                String msg = "Execution of a query takes longer than this implementation is willing to accept.";
                LOG.info("USER ERROR: " + msg);
                throw new QueryTooComplexException(msg);
            }

            // query returned before timeout
            if (LOG.isDebugEnabled()) {
                BigDecimal bd = new BigDecimal(query.getExecutionTime()).divide(new BigDecimal(
                        1000));
                String time = bd.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
                LOG.debug("Query took " + time + " sec.");
            }
            return query.getResultSet();
        } else {
            // timeout value is actually set to an invalid value!
            String msg = "Execution of a query takes longer than this implementation is willing to accept.";
            LOG.info("USER ERROR: " + msg);
            throw new QueryTooComplexException(msg);
        }
    }

    /**
     * A Query which executes a given prepared statement upon Thread start and
     * measures its execution time.
     * 
     * @author Marco Steybe
     */
    private final class Query extends Thread {

        private PreparedStatement ps = null;
        private ResultSet rs = null;
        private long executionTime = 0;
        private SQLException sqlException = null;

        /**
         * Constructs a new Query which executes the given prepared statement
         * upon Thread start and measures the query execution time.
         * 
         * @param ps
         *            The PreparedStatement to be executed.
         */
        public Query(final PreparedStatement ps) {
            this.ps = ps;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Thread#run()
         */
        public void run() {
            try {
                long t1 = System.currentTimeMillis();
                rs = ps.executeQuery();
                long t2 = System.currentTimeMillis();
                executionTime = t2 - t1;
            } catch (SQLException e) {
                sqlException = e;
            }
        }

        /**
         * @return The executionTime.
         */
        public long getExecutionTime() {
            return executionTime;
        }

        /**
         * @return The ResultSet.
         */
        public ResultSet getResultSet() {
            return rs;
        }

        /**
         * @return The PreparedStatement.
         */
        public PreparedStatement getStatement() {
            return ps;
        }

        /**
         * Checks whether execution of the query threw an exception. If so, the
         * exception is retrown here.
         * 
         * @throws SQLException
         *             If the query threw an exception.
         */
        public void checkException() throws SQLException {
            // execution of query threw an exception
            if (sqlException != null) {
                throw sqlException;
            }
        }
    }
}
