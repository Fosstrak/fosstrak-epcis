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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.accada.epcis.soap.DuplicateSubscriptionExceptionResponse;
import org.accada.epcis.soap.EPCISServicePortType;
import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.InvalidURIExceptionResponse;
import org.accada.epcis.soap.NoSuchNameExceptionResponse;
import org.accada.epcis.soap.NoSuchSubscriptionExceptionResponse;
import org.accada.epcis.soap.QueryParameterExceptionResponse;
import org.accada.epcis.soap.QueryTooComplexExceptionResponse;
import org.accada.epcis.soap.QueryTooLargeExceptionResponse;
import org.accada.epcis.soap.SecurityExceptionResponse;
import org.accada.epcis.soap.SubscribeNotPermittedExceptionResponse;
import org.accada.epcis.soap.SubscriptionControlsExceptionResponse;
import org.accada.epcis.soap.ValidationExceptionResponse;
import org.accada.epcis.soap.model.ActionType;
import org.accada.epcis.soap.model.AggregationEventType;
import org.accada.epcis.soap.model.ArrayOfString;
import org.accada.epcis.soap.model.AttributeType;
import org.accada.epcis.soap.model.BusinessLocationType;
import org.accada.epcis.soap.model.BusinessTransactionListType;
import org.accada.epcis.soap.model.BusinessTransactionType;
import org.accada.epcis.soap.model.DuplicateSubscriptionException;
import org.accada.epcis.soap.model.EPC;
import org.accada.epcis.soap.model.EPCListType;
import org.accada.epcis.soap.model.EmptyParms;
import org.accada.epcis.soap.model.EventListType;
import org.accada.epcis.soap.model.GetSubscriptionIDs;
import org.accada.epcis.soap.model.IDListType;
import org.accada.epcis.soap.model.ImplementationException;
import org.accada.epcis.soap.model.ImplementationExceptionSeverity;
import org.accada.epcis.soap.model.InvalidURIException;
import org.accada.epcis.soap.model.NoSuchNameException;
import org.accada.epcis.soap.model.NoSuchSubscriptionException;
import org.accada.epcis.soap.model.ObjectEventType;
import org.accada.epcis.soap.model.Poll;
import org.accada.epcis.soap.model.QuantityEventType;
import org.accada.epcis.soap.model.QueryParam;
import org.accada.epcis.soap.model.QueryParameterException;
import org.accada.epcis.soap.model.QueryParams;
import org.accada.epcis.soap.model.QueryResults;
import org.accada.epcis.soap.model.QueryResultsBody;
import org.accada.epcis.soap.model.QuerySchedule;
import org.accada.epcis.soap.model.QueryTooComplexException;
import org.accada.epcis.soap.model.QueryTooLargeException;
import org.accada.epcis.soap.model.ReadPointType;
import org.accada.epcis.soap.model.Subscribe;
import org.accada.epcis.soap.model.SubscribeNotPermittedException;
import org.accada.epcis.soap.model.SubscriptionControls;
import org.accada.epcis.soap.model.SubscriptionControlsException;
import org.accada.epcis.soap.model.TransactionEventType;
import org.accada.epcis.soap.model.Unsubscribe;
import org.accada.epcis.soap.model.ValidationException;
import org.accada.epcis.soap.model.VocabularyElementListType;
import org.accada.epcis.soap.model.VocabularyElementType;
import org.accada.epcis.soap.model.VocabularyListType;
import org.accada.epcis.soap.model.VocabularyType;
import org.accada.epcis.soap.model.VoidHolder;
import org.accada.epcis.utils.TimeParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

    private static final Log LOG = LogFactory.getLog(QueryOperationsModule.class);

    /**
     * The version of the standard that this service is implementing.
     */
    private static final String STD_VERSION = "1.0";

    /**
     * The version of this service implementation. The empty string indicates
     * that the implementation implements only standard functionality with no
     * vendor extensions.
     */
    private static final String VDR_VERSION = "";

    /**
     * The database dependent identifier quotation sign.
     * <p>
     * TODO: We should not use this delimiter to quote everything. Usually,
     * delimited identifiers are used when the user needs to use an SQL reserved
     * word as an identifier (e.g., a table column name called 'type').
     */
    private static final String delimiter = "`";

    /**
     * Basic SQL query string for transaction events.
     */
    private static final String transactionEventQueryBase = "SELECT SQL_CALC_FOUND_ROWS DISTINCT " + delimiter
            + "event_TransactionEvent" + delimiter
            + ".id, eventTime, recordTime, eventTimeZoneOffset, action, parentID, " + delimiter + "voc_BizStep"
            + delimiter + ".uri AS bizStep, " + delimiter + "voc_Disposition" + delimiter + ".uri AS disposition, "
            + delimiter + "voc_ReadPoint" + delimiter + ".uri AS readPoint, " + delimiter + "voc_BizLoc" + delimiter
            + ".uri AS bizLocation " + "FROM " + delimiter + "event_TransactionEvent" + delimiter + " LEFT JOIN "
            + delimiter + "voc_BizStep" + delimiter + " ON " + delimiter + "event_TransactionEvent" + delimiter
            + ".bizStep = " + delimiter + "voc_BizStep" + delimiter + ".id " + "LEFT JOIN " + delimiter
            + "voc_Disposition" + delimiter + " ON " + delimiter + "event_TransactionEvent" + delimiter
            + ".disposition = " + delimiter + "voc_Disposition" + delimiter + ".id " + "LEFT JOIN " + delimiter
            + "voc_ReadPoint" + delimiter + " ON " + delimiter + "event_TransactionEvent" + delimiter + ".readPoint = "
            + delimiter + "voc_ReadPoint" + delimiter + ".id " + "LEFT JOIN " + delimiter + "voc_BizLoc" + delimiter
            + " ON " + delimiter + "event_TransactionEvent" + delimiter + ".bizLocation = " + delimiter + "voc_BizLoc"
            + delimiter + ".id " + "LEFT JOIN " + delimiter + "event_TransactionEvent_extensions" + delimiter + " ON "
            + delimiter + "event_TransactionEvent" + delimiter + ".id = " + delimiter
            + "event_TransactionEvent_extensions" + delimiter + ".event_id " + "WHERE 1 ";

    /**
     * Basic SQL query string for quantity events.
     */
    private static final String quantityEventQueryBase = "SELECT SQL_CALC_FOUND_ROWS DISTINCT " + delimiter
            + "event_QuantityEvent" + delimiter + ".id, eventTime, recordTime, eventTimeZoneOffset, " + delimiter
            + "voc_EPCClass" + delimiter + ".uri AS epcClass, quantity, " + delimiter + "voc_BizStep" + delimiter
            + ".uri AS bizStep, " + delimiter + "voc_Disposition" + delimiter + ".uri AS disposition, " + delimiter
            + "voc_ReadPoint" + delimiter + ".uri AS readPoint, " + delimiter + "voc_BizLoc" + delimiter
            + ".uri AS bizLocation " + "FROM " + delimiter + "event_QuantityEvent" + delimiter + " LEFT JOIN "
            + delimiter + "voc_BizStep" + delimiter + " ON " + delimiter + "event_QuantityEvent" + delimiter
            + ".bizStep = " + delimiter + "voc_BizStep" + delimiter + ".id " + "LEFT JOIN " + delimiter
            + "voc_Disposition" + delimiter + " ON " + delimiter + "event_QuantityEvent" + delimiter
            + ".disposition = " + delimiter + "voc_Disposition" + delimiter + ".id " + "LEFT JOIN " + delimiter
            + "voc_ReadPoint" + delimiter + " ON " + delimiter + "event_QuantityEvent" + delimiter + ".readPoint = "
            + delimiter + "voc_ReadPoint" + delimiter + ".id " + "LEFT JOIN " + delimiter + "voc_BizLoc" + delimiter
            + " ON " + delimiter + "event_QuantityEvent" + delimiter + ".bizLocation = " + delimiter + "voc_BizLoc"
            + delimiter + ".id " + "LEFT JOIN " + delimiter + "voc_EPCClass" + delimiter + " ON " + delimiter
            + "event_QuantityEvent" + delimiter + ".epcClass = " + delimiter + "voc_EPCClass" + delimiter + ".id "
            + "LEFT JOIN " + delimiter + "event_QuantityEvent_extensions" + delimiter + " ON " + delimiter
            + "event_QuantityEvent" + delimiter + ".id = " + delimiter + "event_QuantityEvent_extensions" + delimiter
            + ".event_id " + "WHERE 1 ";

    /**
     * Basic SQL query string for aggregation events.
     */
    private static final String aggregationEventQueryBase = "SELECT SQL_CALC_FOUND_ROWS DISTINCT " + delimiter
            + "event_AggregationEvent" + delimiter
            + ".id, eventTime, recordTime, eventTimeZoneOffset, parentID, action, " + delimiter + "voc_BizStep"
            + delimiter + ".uri AS bizStep, " + delimiter + "voc_Disposition" + delimiter + ".uri AS disposition, "
            + delimiter + "voc_ReadPoint" + delimiter + ".uri AS readPoint, " + delimiter + "voc_BizLoc" + delimiter
            + ".uri AS bizLocation " + "FROM " + delimiter + "event_AggregationEvent" + delimiter + " LEFT JOIN "
            + delimiter + "voc_BizStep" + delimiter + " ON " + delimiter + "event_AggregationEvent" + delimiter
            + ".bizStep = " + delimiter + "voc_BizStep" + delimiter + ".id " + "LEFT JOIN " + delimiter
            + "voc_Disposition" + delimiter + " ON " + delimiter + "event_AggregationEvent" + delimiter
            + ".disposition  = " + delimiter + "voc_Disposition" + delimiter + ".id " + "LEFT JOIN " + delimiter
            + "voc_ReadPoint" + delimiter + " ON " + delimiter + "event_AggregationEvent" + delimiter + ".readPoint = "
            + delimiter + "voc_ReadPoint" + delimiter + ".id " + "LEFT JOIN " + delimiter + "voc_BizLoc" + delimiter
            + " ON " + delimiter + "event_AggregationEvent" + delimiter + ".bizLocation = " + delimiter + "voc_BizLoc"
            + delimiter + ".id " + "LEFT JOIN " + delimiter + "event_AggregationEvent_extensions" + delimiter + " ON "
            + delimiter + "event_AggregationEvent" + delimiter + ".id = " + delimiter
            + "event_AggregationEvent_extensions" + delimiter + ".event_id " + "WHERE 1 ";

    /**
     * Basic SQL query string for object events.
     */
    private static final String objectEventQueryBase = "SELECT SQL_CALC_FOUND_ROWS DISTINCT " + delimiter
            + "event_ObjectEvent" + delimiter + ".id, eventTime, recordTime, eventTimeZoneOffset, action, " + delimiter
            + "voc_BizStep" + delimiter + ".uri AS bizStep, " + delimiter + "voc_Disposition" + delimiter
            + ".uri AS disposition, " + delimiter + "voc_ReadPoint" + delimiter + ".uri AS readPoint, " + delimiter
            + "voc_BizLoc" + delimiter + ".uri AS bizLocation " + "FROM " + delimiter + "event_ObjectEvent" + delimiter
            + " LEFT JOIN " + delimiter + "voc_BizStep" + delimiter + " ON " + delimiter + "event_ObjectEvent"
            + delimiter + ".bizStep = " + delimiter + "voc_BizStep" + delimiter + ".id " + "LEFT JOIN " + delimiter
            + "voc_Disposition" + delimiter + " ON " + delimiter + "event_ObjectEvent" + delimiter + ".disposition = "
            + delimiter + "voc_Disposition" + delimiter + ".id " + "LEFT JOIN " + delimiter + "voc_ReadPoint"
            + delimiter + " ON " + delimiter + "event_ObjectEvent" + delimiter + ".readPoint = " + delimiter
            + "voc_ReadPoint" + delimiter + ".id " + "LEFT JOIN " + delimiter + "voc_BizLoc" + delimiter + " ON "
            + delimiter + "event_ObjectEvent" + delimiter + ".bizLocation = " + delimiter + "voc_BizLoc" + delimiter
            + ".id " + "LEFT JOIN " + delimiter + "event_ObjectEvent_extensions" + delimiter + " ON " + delimiter
            + "event_ObjectEvent" + delimiter + ".id = " + delimiter + "event_ObjectEvent_extensions" + delimiter
            + ".event_id " + "WHERE 1";

    /**
     * The names of all the implemented queries.
     */
    private static final List<String> QUERYNAMES = new ArrayList<String>();

    static {
        QUERYNAMES.add("SimpleEventQuery");
        QUERYNAMES.add("SimpleMasterDataQuery");
    }

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
     * The seconds to wait to check a trigger condition.
     */
    private String triggerConditionSeconds;

    /**
     * The minutes to wait to check a trigger condition.
     */
    private String triggerConditionMinutes = null;

    @Resource
    private WebServiceContext context;

    private DataSource dataSource;

    private Connection connection;

    /**
     * Returns whether subscriptionID already exists in DB.
     * 
     * @param subscrId
     *            The id to be looked up.
     * @return <code>true</code> if subscriptionID already exists in DB,
     *         <code>false</code> otherwise.
     * @throws SQLException
     *             If a problem with the database occurred.
     */
    private boolean fetchExistsSubscriptionId(final String subscrId) throws SQLException {
        String query = "SELECT EXISTS(SELECT subscriptionid FROM " + delimiter + "subscription" + delimiter
                + " WHERE subscriptionid = (?))";
        PreparedStatement pstmt = connection.prepareStatement(query);
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
     * Executes the SQL query given in the PreparedStatement and retrieves all
     * EPC objects from the result set.
     * 
     * @param stmt
     *            The PreparedStatement to be executed.
     * @return an array of EPC objects.
     * @throws SQLException
     *             If a database access error occurred.
     */
    private EPCListType fetchEPCs(final PreparedStatement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery();
        EPCListType epcs = new EPCListType();
        while (rs.next()) {
            EPC epc = new EPC();
            epc.setValue(rs.getString("epc"));
            epcs.getEpc().add(epc);
        }
        return epcs.getEpc().isEmpty() ? null : epcs;
    }

    /**
     * Executes the SQL query in the given PreparedStatement and retrieves all
     * MessageElement objects, i.e. all fieldname extensions, from the result
     * set.
     * 
     * @param stmt
     *            The PreparedStatement to be executed.
     * @param tableName
     *            The name of the table with the field extensions of the event.
     * @return an array of MessageElement objects.
     * @throws SQLException
     *             If a database access error occurred.
     */
    private void fetchExtensions(final String tableName, final PreparedStatement stmt, List<Object> extensions)
            throws SQLException {
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String fieldname = rs.getString("fieldname");
            String[] parts = fieldname.split("#");
            if (parts.length != 2) {
                throw new SQLException("Column 'fieldname' in table '" + tableName + "' has invalid format: "
                        + fieldname);
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
                throw new SQLException("All value columns in '" + tableName + "' are null.");
            }
            JAXBElement<String> elem = new JAXBElement<String>(new javax.xml.namespace.QName(namespace, localPart,
                    prefix), String.class, value);
            extensions.add(elem);
        }
    }

    /**
     * Executes the SQL query in the given PreparedStatement and retrieves all
     * BusinessTransactionType objects from the result set.
     * 
     * @param stmt
     *            The PreparedStatement to be executed.
     * @return an array of BusinessTransactionType objects.
     * @throws SQLException
     *             If a database access error occurred.
     * @throws ImplementationException
     *             If an error retrieving or transforming data from the query
     *             result occurred.
     */
    private BusinessTransactionListType fetchBizTransactions(final PreparedStatement stmt) throws SQLException,
            ImplementationExceptionResponse {
        ResultSet rs = stmt.executeQuery();
        BusinessTransactionListType list = new BusinessTransactionListType();
        while (rs.next()) {
            String uri = rs.getString("uri");
            BusinessTransactionType btrans = new BusinessTransactionType();
            btrans.setValue(uri);
            btrans.setType(rs.getString("typeuri"));
            list.getBizTransaction().add(btrans);
        }
        return list.getBizTransaction().isEmpty() ? null : list;
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
     * @throws ImplementationExceptionResponse
     *             Problem with data or on implementation side. (i.e. uri value
     *             in DB is actually not an uri)
     * @throws QueryTooLargeExceptionResponse
     *             If the rows returned by the query is larger than specified or
     *             larger than this implementation is willing to accept.
     * @throws QueryTooComplexExceptionResponse
     *             If the query takes too long to return.
     */
    private void runObjectEventQuery(final PreparedStatement objectEventQuery, EventListType eventList)
            throws SQLException, ImplementationExceptionResponse, QueryTooLargeExceptionResponse,
            QueryTooComplexExceptionResponse {
        if (objectEventQuery == null) {
            return;
        }

        // run the query and get all ObjectEvents
        ResultSet rs = executeStatement(objectEventQuery, maxQueryTime);
        // TODO: checkQueryRows();

        // prepare some queries
        String bizTransQuery = "SELECT " + delimiter + "voc_BizTrans" + delimiter + ".uri, " + delimiter
                + "voc_BizTransType" + delimiter + ".uri AS typeuri FROM ((" + delimiter + "BizTransaction" + delimiter
                + " JOIN " + delimiter + "event_ObjectEvent_bizTrans" + delimiter + " ON " + delimiter
                + "BizTransaction" + delimiter + ".id = " + delimiter + "event_ObjectEvent_bizTrans" + delimiter
                + ".bizTrans_id" + ") JOIN " + delimiter + "voc_BizTrans" + delimiter + " ON " + delimiter
                + "BizTransaction" + delimiter + ".bizTrans = " + delimiter + "voc_BizTrans" + delimiter + ".id)"
                + " LEFT OUTER JOIN " + delimiter + "voc_BizTransType" + delimiter + " ON " + delimiter
                + "BizTransaction" + delimiter + ".type = " + delimiter + "voc_BizTransType" + delimiter + ".id"
                + " WHERE " + delimiter + "event_ObjectEvent_bizTrans" + delimiter + ".event_id = ?";
        String epcQuery = "SELECT epc FROM " + delimiter + "event_ObjectEvent_EPCs" + delimiter + " WHERE event_id = ?";
        String extQuery = "SELECT * FROM " + delimiter + "event_ObjectEvent_extensions" + delimiter
                + " WHERE event_id = ?";
        PreparedStatement bizTransStmt = connection.prepareStatement(bizTransQuery);
        PreparedStatement epcStmt = connection.prepareStatement(epcQuery);
        PreparedStatement extStmt = connection.prepareStatement(extQuery);

        while (rs.next()) {
            ObjectEventType objectEvent = new ObjectEventType();
            int eventId = rs.getInt("id");

            // set EventTime, RecordTime, and EventTimezoneOffset
            Timestamp time = rs.getTimestamp("eventTime");
            objectEvent.setEventTime(createXmlCalendarFromSqlTimestamp(time));
            time = rs.getTimestamp("recordTime");
            objectEvent.setRecordTime(createXmlCalendarFromSqlTimestamp(time));
            objectEvent.setEventTimeZoneOffset(rs.getString("eventTimeZoneOffset"));

            // set action
            ActionType action = ActionType.valueOf(rs.getString("action"));
            objectEvent.setAction(action);

            // set all URIs
            objectEvent.setBizStep(rs.getString("bizStep"));
            objectEvent.setDisposition(rs.getString("disposition"));
            if (rs.getString("readPoint") != null) {
                ReadPointType rp = new ReadPointType();
                rp.setId(rs.getString("readPoint"));
                objectEvent.setReadPoint(rp);
            }
            if (rs.getString("bizLocation") != null) {
                BusinessLocationType blt = new BusinessLocationType();
                blt.setId(rs.getString("bizLocation"));
                objectEvent.setBizLocation(blt);
            }

            // set business transactions
            bizTransStmt.setInt(1, eventId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("QUERY: " + bizTransStmt.toString());
            }
            objectEvent.setBizTransactionList(fetchBizTransactions(bizTransStmt));

            // set EPCs
            epcStmt.setInt(1, eventId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("QUERY: " + epcStmt.toString());
            }
            objectEvent.setEpcList(fetchEPCs(epcStmt));

            // set field extensions
            extStmt.setInt(1, eventId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("QUERY: " + extStmt.toString());
            }
            fetchExtensions("event_ObjectEvent_extensions", extStmt, objectEvent.getAny());

            eventList.getObjectEventOrAggregationEventOrQuantityEvent().add(objectEvent);
        }
        rs.close();
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
    private void runAggregationEventQuery(final PreparedStatement aggregationEventQuery, EventListType eventList)
            throws SQLException, ImplementationExceptionResponse, QueryTooLargeExceptionResponse,
            QueryTooComplexExceptionResponse {
        if (aggregationEventQuery == null) {
            return;
        }

        // run the query and get all AggregationEvents
        ResultSet rs = executeStatement(aggregationEventQuery, maxQueryTime);
        // TODO: checkQueryRows();

        // prepare some queries
        String bizTransQuery = "SELECT " + delimiter + "voc_BizTrans" + delimiter + ".uri, " + delimiter
                + "voc_BizTransType" + delimiter + ".uri AS typeuri FROM ((" + delimiter + "BizTransaction" + delimiter
                + " JOIN " + delimiter + "event_AggregationEvent_bizTrans" + delimiter + " ON " + delimiter
                + "BizTransaction" + delimiter + ".id = " + delimiter + "event_AggregationEvent_bizTrans" + delimiter
                + ".bizTrans_id" + ") JOIN " + delimiter + "voc_BizTrans" + delimiter + " ON " + delimiter
                + "BizTransaction" + delimiter + ".bizTrans = " + delimiter + "voc_BizTrans" + delimiter + ".id)"
                + " LEFT OUTER JOIN " + delimiter + "voc_BizTransType" + delimiter + " ON " + delimiter
                + "BizTransaction" + delimiter + ".type = " + delimiter + "voc_BizTransType" + delimiter + ".id"
                + " WHERE " + delimiter + "event_AggregationEvent_bizTrans" + delimiter + ".event_id = ?";
        String epcQuery = "SELECT epc FROM " + delimiter + "event_AggregationEvent_EPCs" + delimiter
                + " WHERE event_id = ?";
        String extQuery = "SELECT * FROM " + delimiter + "event_AggregationEvent_extensions" + delimiter
                + " WHERE event_id = ?";
        PreparedStatement bizTransStmt = connection.prepareStatement(bizTransQuery);
        PreparedStatement epcStmt = connection.prepareStatement(epcQuery);
        PreparedStatement extStmt = connection.prepareStatement(extQuery);

        while (rs.next()) {
            AggregationEventType aggrEvent = new AggregationEventType();
            int eventId = rs.getInt("id");

            // set EventTime, RecordTime, and EventTimezoneOffset
            Timestamp time = rs.getTimestamp("eventTime");
            aggrEvent.setEventTime(createXmlCalendarFromSqlTimestamp(time));
            time = rs.getTimestamp("recordTime");
            aggrEvent.setRecordTime(createXmlCalendarFromSqlTimestamp(time));
            aggrEvent.setEventTimeZoneOffset(rs.getString("eventTimeZoneOffset"));

            // set action
            ActionType action = ActionType.valueOf(rs.getString("action"));
            aggrEvent.setAction(action);

            // set all URIs
            aggrEvent.setParentID(rs.getString("parentID"));
            aggrEvent.setBizStep(rs.getString("bizStep"));
            aggrEvent.setDisposition(rs.getString("disposition"));
            if (rs.getString("readPoint") != null) {
                ReadPointType rpt = new ReadPointType();
                rpt.setId(rs.getString("readPoint"));
                aggrEvent.setReadPoint(rpt);
            }
            if (rs.getString("bizLocation") != null) {
                BusinessLocationType blt = new BusinessLocationType();
                blt.setId(rs.getString("bizLocation"));
                aggrEvent.setBizLocation(blt);
            }

            // set business transactions
            bizTransStmt.setInt(1, eventId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("QUERY: " + bizTransStmt.toString());
            }
            aggrEvent.setBizTransactionList(fetchBizTransactions(bizTransStmt));

            // set EPCs
            epcStmt.setInt(1, eventId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("QUERY: " + epcStmt.toString());
            }
            aggrEvent.setChildEPCs(fetchEPCs(epcStmt));

            // set field extensions
            extStmt.setInt(1, eventId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("QUERY: " + extStmt.toString());
            }
            // AggregationEventExtensionType ext = new
            // AggregationEventExtensionType();
            fetchExtensions("event_AggregationEvent_extensions", extStmt, aggrEvent.getAny());
            // aggrEvent.setExtension(ext);

            eventList.getObjectEventOrAggregationEventOrQuantityEvent().add(aggrEvent);
        }
        rs.close();
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
    private void runQuantityEventQuery(final PreparedStatement quantityEventQuery, EventListType eventList)
            throws SQLException, ImplementationExceptionResponse, QueryTooLargeExceptionResponse,
            QueryTooComplexExceptionResponse {
        if (quantityEventQuery == null) {
            return;
        }

        // run the query and get all QuantityEvents
        ResultSet rs = executeStatement(quantityEventQuery, maxQueryTime);
        // TODO: checkQueryRows();

        // prepare some queries
        String bizTransQuery = "SELECT " + delimiter + "voc_BizTrans" + delimiter + ".uri, " + delimiter
                + "voc_BizTransType" + delimiter + ".uri AS typeuri FROM ((" + delimiter + "BizTransaction" + delimiter
                + " JOIN " + delimiter + "event_QuantityEvent_bizTrans" + delimiter + " ON " + delimiter
                + "BizTransaction" + delimiter + ".id = " + delimiter + "event_QuantityEvent_bizTrans" + delimiter
                + ".bizTrans_id" + ") JOIN " + delimiter + "voc_BizTrans" + delimiter + " ON " + delimiter
                + "BizTransaction" + delimiter + ".bizTrans = " + delimiter + "voc_BizTrans" + delimiter + ".id)"
                + " LEFT OUTER JOIN " + delimiter + "voc_BizTransType" + delimiter + " ON " + delimiter
                + "BizTransaction" + delimiter + ".type = " + delimiter + "voc_BizTransType" + delimiter + ".id"
                + " WHERE " + delimiter + "event_QuantityEvent_bizTrans" + delimiter + ".event_id = ?";
        String extQuery = "SELECT * FROM " + delimiter + "event_QuantityEvent_extensions" + delimiter
                + " WHERE event_id = ?";
        PreparedStatement bizTransStmt = connection.prepareStatement(bizTransQuery);
        PreparedStatement extStmt = connection.prepareStatement(extQuery);

        while (rs.next()) {
            QuantityEventType quantEvent = new QuantityEventType();
            int eventId = rs.getInt("id");

            // set EventTime, RecordTime, and EventTimezoneOffset
            Timestamp time = rs.getTimestamp("eventTime");
            quantEvent.setEventTime(createXmlCalendarFromSqlTimestamp(time));
            time = rs.getTimestamp("recordTime");
            quantEvent.setRecordTime(createXmlCalendarFromSqlTimestamp(time));
            quantEvent.setEventTimeZoneOffset(rs.getString("eventTimeZoneOffset"));

            // set EPCClass
            quantEvent.setEpcClass(rs.getString("epcClass"));

            // set quantity
            quantEvent.setQuantity(rs.getInt("quantity"));

            // set all URIs
            quantEvent.setBizStep(rs.getString("bizStep"));
            quantEvent.setDisposition(rs.getString("disposition"));
            if (rs.getString("readPoint") != null) {
                ReadPointType rpt = new ReadPointType();
                rpt.setId(rs.getString("readPoint"));
                quantEvent.setReadPoint(rpt);
            }
            if (rs.getString("bizLocation") != null) {
                BusinessLocationType blt = new BusinessLocationType();
                blt.setId(rs.getString("bizLocation"));
                quantEvent.setBizLocation(blt);
            }

            // set business transactions
            bizTransStmt.setInt(1, eventId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("QUERY: " + bizTransStmt.toString());
            }
            quantEvent.setBizTransactionList(fetchBizTransactions(bizTransStmt));

            // set field extensions
            extStmt.setInt(1, eventId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("QUERY: " + extStmt.toString());
            }
            // QuantityEventExtensionType ext = new
            // QuantityEventExtensionType();
            fetchExtensions("event_QuantityEvent_extensions", extStmt, quantEvent.getAny());
            // quantEvent.setExtension(ext);

            eventList.getObjectEventOrAggregationEventOrQuantityEvent().add(quantEvent);
        }
        rs.close();
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
    private void runTransactionEventQuery(final PreparedStatement transactionEventQuery, EventListType eventList)
            throws SQLException, ImplementationExceptionResponse, QueryTooLargeExceptionResponse,
            QueryTooComplexExceptionResponse {
        if (transactionEventQuery == null) {
            return;
        }

        // run the query and get all TransactionEvents
        ResultSet rs = executeStatement(transactionEventQuery, maxQueryTime);
        // TODO: checkQueryRows();

        // prepare some queries
        String bizTransQuery = "SELECT " + delimiter + "voc_BizTrans" + delimiter + ".uri, " + delimiter
                + "voc_BizTransType" + delimiter + ".uri AS typeuri FROM ((" + delimiter + "BizTransaction" + delimiter
                + " JOIN " + delimiter + "event_TransactionEvent_bizTrans" + delimiter + " ON " + delimiter
                + "BizTransaction" + delimiter + ".id = " + delimiter + "event_TransactionEvent_bizTrans" + delimiter
                + ".bizTrans_id" + ") JOIN " + delimiter + "voc_BizTrans" + delimiter + " ON " + delimiter
                + "BizTransaction" + delimiter + ".bizTrans = " + delimiter + "voc_BizTrans" + delimiter + ".id)"
                + " LEFT OUTER JOIN " + delimiter + "voc_BizTransType" + delimiter + " ON " + delimiter
                + "BizTransaction" + delimiter + ".type = " + delimiter + "voc_BizTransType" + delimiter + ".id"
                + " WHERE " + delimiter + "event_TransactionEvent_bizTrans" + delimiter + ".event_id = ?";
        String epcQuery = "SELECT epc FROM " + delimiter + "event_TransactionEvent_EPCs" + delimiter
                + " WHERE event_id = ?";
        String extQuery = "SELECT * FROM " + delimiter + "event_TransactionEvent_extensions" + delimiter
                + " WHERE event_id = ?";
        PreparedStatement bizTransStmt = connection.prepareStatement(bizTransQuery);
        PreparedStatement epcStmt = connection.prepareStatement(epcQuery);
        PreparedStatement extStmt = connection.prepareStatement(extQuery);

        while (rs.next()) {
            TransactionEventType transEvent = new TransactionEventType();
            int eventId = rs.getInt("id");

            // set EventTime, RecordTime, and EventTimezoneOffset
            Timestamp time = rs.getTimestamp("eventTime");
            transEvent.setEventTime(createXmlCalendarFromSqlTimestamp(time));
            time = rs.getTimestamp("recordTime");
            transEvent.setRecordTime(createXmlCalendarFromSqlTimestamp(time));
            transEvent.setEventTimeZoneOffset(rs.getString("eventTimeZoneOffset"));

            // set action
            ActionType action = ActionType.valueOf(rs.getString("action"));
            transEvent.setAction(action);

            // set all URIs
            transEvent.setParentID(rs.getString("parentID"));
            transEvent.setBizStep(rs.getString("bizStep"));
            transEvent.setDisposition(rs.getString("disposition"));
            if (rs.getString("readPoint") != null) {
                ReadPointType rpt = new ReadPointType();
                rpt.setId(rs.getString("readPoint"));
                transEvent.setReadPoint(rpt);
            }
            if (rs.getString("bizLocation") != null) {
                BusinessLocationType blt = new BusinessLocationType();
                blt.setId(rs.getString("bizLocation"));
                transEvent.setBizLocation(blt);
            }

            // set business transactions
            bizTransStmt.setInt(1, eventId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("QUERY: " + bizTransStmt.toString());
            }
            transEvent.setBizTransactionList(fetchBizTransactions(bizTransStmt));

            // set EPCs
            epcStmt.setInt(1, eventId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("QUERY: " + epcStmt.toString());
            }
            transEvent.setEpcList(fetchEPCs(epcStmt));

            // set field extensions
            extStmt.setInt(1, eventId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("QUERY: " + extStmt.toString());
            }
            // TransactionEventExtensionType ext = new
            // TransactionEventExtensionType();
            fetchExtensions("event_TransactionEvent_extensions", extStmt, transEvent.getAny());
            // transEvent.setExtension(ext);

            eventList.getObjectEventOrAggregationEventOrQuantityEvent().add(transEvent);
        }
        rs.close();
    }

    /**
     * Retrieves the number of rows resulted from the execution of the last
     * query and throws a QueryTooLargeExsception if the number exceeds the
     * value specified by the 'maxEventCount' argument or the implementation's
     * global 'maxNrOfRows' parameter.
     * 
     * @throws SQLException
     *             If a problem accessing the database occurred.
     * @throws QueryTooLargeException
     *             If the rows returned by the query is larger than specified or
     *             larger than this implementation is willing to accept.
     */
    private void checkQueryRows() throws SQLException, QueryTooLargeExceptionResponse {
        // check the number of rows calculated for the query
        Statement stmt = connection.createStatement();
        ResultSet rows = stmt.executeQuery("SELECT FOUND_ROWS() AS rowcount;");
        int rowCount = maxQueryRows;
        if (rows.first()) {
            rowCount = rows.getInt(1);
        }
        LOG.debug("rowCount: " + rowCount);
        LOG.debug("maxQueryRows: " + maxQueryRows);
        LOG.debug("maxEventCount: " + maxEventCount);
        if (rowCount > maxQueryRows || (maxEventCount > -1 && rowCount > maxEventCount)) {
            String msg = "The query returned more results (" + rowCount + ") than ";
            if (rowCount > maxQueryRows) {
                msg = msg + "this implementation is willing to handle (" + maxQueryRows + ").";
            } else {
                msg = msg + "specified by parameter 'maxEventCount' (" + maxEventCount + ").";
            }
            LOG.info("USER ERROR: " + msg);
            QueryTooLargeException e = new QueryTooLargeException();
            e.setReason(msg);
            throw new QueryTooLargeExceptionResponse(msg, e);
        }
    }

    /**
     * Appends the String values from the given <code>strings</code> List into
     * an SQL IN (?,?,..) notation, intended to be used with PreparedStatement.
     * The correct number of '?' will be added to the given SQL StringBuilder,
     * and the String values from the <code>strings</code> List will be added
     * to the <code>queryArgs</code> List.
     * <p>
     * TODO: This method is superfluous if the query arguments were directly
     * appended to the <code>queryArgs</code> List and the correct number of
     * '?' were appended to the SQL query in the end.
     * 
     * @param strings
     *            A List of strings to be appended to the <code>queryArgs</code>
     *            List.
     * @param sql
     *            The SQL query which will be appended with the appropriate
     *            number of '?'.
     * @param queryArgs
     *            The <code>queryArgs</code> List to which the query
     *            parameters will be appended to.
     */
    private void listOfStringToSql(final List<String> strings, final StringBuilder sql, final List<String> queryArgs) {
        if (strings.isEmpty()) {
            return;
        }
        Iterator<String> it = strings.iterator();
        while (it.hasNext()) {
            sql.append('?');
            queryArgs.add(it.next());
            if (it.hasNext()) {
                sql.append(',');
            }
        }
    }

    /**
     * Create an SQL query string from the given query parameters.
     * <p>
     * Note: the CXF framework always returns an instance of org.w3c.dom.Element
     * for the query parameter value given in the <code>queryParams</code>
     * argument, because the spec defines this value to be of type
     * <code>anyType</code>. CXF <i>does not</i> resolve the type of the
     * query parameter value from the query name as Axis does! However, if the
     * user specifies the XML type in the request, then CXF returns an instance
     * of the corresponding type.
     * <p>
     * Consider the following example of a query parameter:
     * 
     * <pre>
     * &lt;param&gt;
     *   &lt;name&gt;GE_eventTime&lt;/name&gt;
     *   &lt;value&gt;2007-07-07T07:07:07+02:00&lt;/value&gt;
     * &lt;/param&gt;
     * </pre>
     * 
     * For the query parameter value, CXF will return an instance of
     * org.w3c.dom.Element containing the text value
     * "2007-07-07T07:07:07+02:00". However, if the user provides the following
     * instead, CXF will return an instance of
     * javax.xml.datatype.XMLGregorianCalendar.
     * 
     * <pre>
     * &lt;param&gt;
     *   &lt;name&gt;GE_eventTime&lt;/name&gt;
     *   &lt;value
     *       xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
     *       xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot;
     *       xsi:type=&quot;xs:dateTime&quot;&gt;
     *     2007-07-07T07:07:07+02:00
     *   &lt;/value&gt;
     * &lt;/param&gt;
     * </pre>
     * 
     * As a consequence, we always first need to check if the value is an
     * instance of Element, and if so, we need to parse it manually according to
     * the semantics of the parameter name.
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
     *             If an error in the implementation occurred.
     */
    private PreparedStatement createEventQuery(final QueryParams queryParams, final String eventType)
            throws SQLException, QueryParameterExceptionResponse, ImplementationExceptionResponse {

        StringBuilder query;
        if (eventType.equals("ObjectEvent")) {
            query = new StringBuilder(objectEventQueryBase);
        } else if (eventType.equals("AggregationEvent")) {
            query = new StringBuilder(aggregationEventQueryBase);
        } else if (eventType.equals("QuantityEvent")) {
            query = new StringBuilder(quantityEventQueryBase);
        } else if (eventType.equals("TransactionEvent")) {
            query = new StringBuilder(transactionEventQueryBase);
        } else {
            ImplementationException iex = new ImplementationException();
            String msg = "Invalid event type encountered: " + eventType;
            LOG.error(msg);
            iex.setReason(msg);
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw new ImplementationExceptionResponse(msg, iex);
        }

        String orderBy = "";
        String orderDirection = "";
        int limit = -1;
        maxEventCount = -1;
        List<String> params = new LinkedList<String>();
        List<String> queryArgs = new ArrayList<String>();

        for (QueryParam param : queryParams.getParam()) {
            String paramName = param.getName();
            Object paramValue = param.getValue();

            // check if empty param value is provided
            if ((paramValue == null || (paramValue instanceof String && paramValue.toString().equals("")))
                    && !paramName.startsWith("EXISTS_")) {
                // ignore this parameter
                LOG.debug("Ignoring parameter '" + paramName + "' as no corresponding parameter value was provided!");
                continue;
            }

            // check if this parameter has already been provided
            if (params.contains(paramName)) {
                String msg = "Two or more inputs are provided for the same parameter '" + paramName + "'.";
                LOG.info("USER ERROR: " + msg);
                throw new QueryParameterExceptionResponse(msg);
            } else {
                params.add(paramName);
            }

            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Handling '" + paramName + "' query parameter with parameter value instance of '"
                            + paramValue.getClass() + "'");
                }
                // TODO: for the sake of code readability: outsource all the
                // code fragments in the different cases into separate methods
                // e.g. handleEventType, handleEventTime, etc.

                // TODO: for all cases: the casts of paramValue to a specific
                // class (e.g. cast to ArrayOfString or Calendar) may fail if
                // the Web service framework is unable to resolve the type of
                // the value (i.e. because the XML type of the paramValue is
                // xsd:anyType) -> cast to Element and parse manually (c.f.
                // handleEventType)
                if (paramName.equals("eventType")) {
                    // check if current eventType argument is contained in the
                    // 'eventType' query parameter, otherwise return
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().contains(eventType)) {
                        return null;
                    }

                } else if (paramName.equals("GE_eventTime") || paramName.equals("LT_eventTime")
                        || paramName.equals("GE_recordTime") || paramName.equals("LT_recordTime")) {
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
                    Timestamp ts = parseAsTimestamp(paramValue, paramName);
                    queryArgs.add(ts.toString());

                } else if (paramName.equals("EQ_action")) {
                    if (!eventType.equals("QuantityEvent")) {
                        ArrayOfString aos = parseAsArrayOfString(paramValue);
                        // check if parameter value provided
                        if (!aos.getString().isEmpty()) {
                            checkActionValues(aos.getString());
                            query.append(" AND (action IN (");
                            listOfStringToSql(aos.getString(), query, queryArgs);
                            query.append(")) ");
                        }
                    } else {
                        // QuantityEvents have no action so no event matches!
                        query.append(" AND 0 ");
                    }

                } else if (paramName.equals("EQ_bizStep") || paramName.equals("EQ_disposition")
                        || paramName.equals("EQ_readPoint") || paramName.equals("EQ_bizLocation")) {
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    // check if parameter value provided
                    if (!aos.getString().isEmpty()) {
                        if (paramName.equals("EQ_bizStep")) {
                            query.append(" AND (").append(delimiter).append("voc_BizStep").append(delimiter).append(
                                    ".uri IN (");
                        } else if (paramName.equals("EQ_disposition")) {
                            query.append(" AND (" + delimiter + "voc_Disposition" + delimiter + ".uri IN (");
                        } else if (paramName.equals("EQ_readPoint")) {
                            query.append(" AND (").append(delimiter).append("voc_ReadPoint").append(delimiter).append(
                                    ".uri IN (");
                        } else if (paramName.equals("EQ_bizLocation")) {
                            query.append(" AND (").append(delimiter).append("voc_BizLoc").append(delimiter).append(
                                    ".uri IN (");
                        }
                        listOfStringToSql(aos.getString(), query, queryArgs);
                        query.append(")) ");
                    }

                } else if (paramName.equals("WD_readPoint") || paramName.equals("WD_bizLocation")) {
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    // check if parameter value provided
                    if (!aos.getString().isEmpty()) {
                        String tablename = delimiter + "voc_ReadPoint" + delimiter;
                        if (paramName.equals("WD_bizLocation")) {
                            tablename = delimiter + "voc_BizLoc" + delimiter;
                        }
                        // the % allows any possible ending, which should
                        // implement
                        // the semantics of "With Descendant"
                        query.append(" AND (");
                        for (String attr : aos.getString()) {
                            query.append(tablename);
                            query.append(".uri LIKE ? OR ");
                            queryArgs.add(attr + "%");
                        }
                        query.append("0) ");
                    }

                } else if (paramName.startsWith("EQ_bizTransaction_")) {
                    // this query is subdivided into several subqueries

                    // subquery for selecting IDs from voc_BizTransType
                    // type extracted from parameter name
                    String type = paramName.substring(18);
                    String vocBizTransTypeId = "SELECT id FROM " + delimiter + "voc_BizTransType" + delimiter
                            + " WHERE uri=\"" + type + "\"";

                    // subquery for selecting IDs from voc_BizTrans
                    StringBuilder temp = new StringBuilder();
                    temp.append("(SELECT id AS vocBizTransId FROM ").append(delimiter).append("voc_BizTrans").append(
                            delimiter).append(" WHERE ").append(delimiter).append("voc_BizTrans").append(delimiter).append(
                            ".uri IN (");
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (aos.getString().size() > 0) {
                        for (String s : aos.getString()) {
                            temp.append("\"").append(s).append("\",");
                        }
                    }
                    // remove last comma
                    temp.delete(temp.length() - 1, temp.length());
                    temp.append(")) AS SelectedVocBizTrans");
                    String vocBizTransIds = temp.toString();

                    // subquery for selecting IDs from BizTransaction
                    String selectedBizTrans = "(SELECT id AS bizTransId, bizTrans FROM " + delimiter + "BizTransaction"
                            + delimiter + " bt WHERE bt.type=(" + vocBizTransTypeId + ")) AS SelectedBizTrans";
                    String bizTransId = "SELECT bizTransId FROM " + vocBizTransIds + " INNER JOIN " + selectedBizTrans
                            + " ON SelectedBizTrans.bizTrans=SelectedVocBizTrans.vocBizTransId";

                    query.append(" AND (").append(delimiter).append("event_").append(eventType).append(delimiter).append(
                            ".id IN (");
                    query.append("SELECT event_id AS id ");
                    query.append("FROM ").append(delimiter).append("event_").append(eventType).append("_bizTrans").append(
                            delimiter).append(" ");
                    query.append("INNER JOIN (");
                    query.append(bizTransId);
                    query.append(") AS BizTransIds ");
                    query.append("ON BizTransIds.bizTransId=").append(delimiter).append(
                            "event_" + eventType + "_bizTrans").append(delimiter).append(".bizTrans_id");
                    query.append("))");

                } else if (paramName.equals("MATCH_epc") || paramName.equals("MATCH_anyEPC")) {
                    if (eventType.equals("QuantityEvent")) {
                        // the parameter is not allowed for QuantityEvent,
                        // exclude event from result set
                        query.append(" AND 0 ");
                    } else {
                        ArrayOfString aos = parseAsArrayOfString(paramValue);
                        // check if parameter value provided
                        if (!aos.getString().isEmpty()) {
                            query.append(" AND (").append(delimiter).append("event_");
                            query.append(eventType);
                            query.append(delimiter).append(".id IN (");
                            query.append("SELECT event_id FROM ").append(delimiter).append("event_");
                            query.append(eventType);
                            query.append("_EPCs").append(delimiter).append(" WHERE ");
                            for (String epc : aos.getString()) {
                                String val = epc.replaceAll("\\*", "%");
                                query.append("epc LIKE '").append(val).append("' OR ");
                            }
                            query.append("0");
                            if (paramName.equals("MATCH_anyEPC")
                                    && (eventType.equals("AggregationEvent") || eventType.equals("TransactionEvent"))) {
                                // also look in parentID field
                                query.append(" OR (parentID IN (");
                                listOfStringToSql(aos.getString(), query, queryArgs);
                                query.append("))");
                            }
                            query.append("))");
                        }
                    }

                } else if (paramName.equals("MATCH_parentID")) {
                    if (eventType.equals("AggregationEvent") || eventType.equals("TransactionEvent")) {
                        ArrayOfString aos = parseAsArrayOfString(paramValue);
                        // check if parameter value provided
                        if (!aos.getString().isEmpty()) {
                            query.append(" AND (parentID IN (");

                            listOfStringToSql(aos.getString(), query, queryArgs);
                            query.append("))");
                        }
                    } else {
                        // the parameter is not allowed for other events,
                        // exclude event from result set
                        query.append(" AND 0 ");
                    }

                } else if (paramName.equals("MATCH_epcClass")) {
                    if (eventType.equals("QuantityEvent")) {
                        ArrayOfString aos = parseAsArrayOfString(paramValue);
                        // check if parameter value provided
                        if (!aos.getString().isEmpty()) {
                            query.append(" AND (epcClass IN (");
                            query.append("SELECT id FROM ").append(delimiter).append("voc_EPCClass").append(delimiter).append(
                                    " WHERE uri IN (");
                            listOfStringToSql(aos.getString(), query, queryArgs);
                            query.append(")))");
                        }
                    } else {
                        // the parameter is not allowed for other events,
                        // exclude event from result set
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
                        Integer quantity = parseAsInteger(paramValue);
                        queryArgs.add(parseAsInteger(paramValue).toString());
                    } else {
                        query.append(" AND 0 ");
                    }

                } else if (paramName.startsWith("GT_") || paramName.startsWith("GE_") || paramName.startsWith("EQ_")
                        || paramName.startsWith("LE_") || paramName.startsWith("LT_")) {

                    // check if this is an extension field
                    String fieldname = paramName.substring(3);
                    String[] parts = fieldname.split("#");
                    if (parts.length != 2) {
                        String msg = "The parameter " + paramName + " cannot be recognised.";
                        LOG.info("USER ERROR: " + msg);
                        QueryParameterException e = new QueryParameterException();
                        e.setReason(msg);
                        throw new QueryParameterExceptionResponse(msg, e);
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
                        Integer intVal = parseAsInteger(paramValue);
                        where = "intValue" + op + intVal;
                    } catch (NumberFormatException e1) {
                        try {
                            Float floatVal = parseAsFloat(paramValue);
                            where = "floatValue" + op + floatVal;
                        } catch (NumberFormatException e2) {
                            try {
                                Timestamp ts = parseAsTimestamp(paramValue, paramName);
                                where = "dateValue" + op + "\"" + ts.toString() + "\"";
                            } catch (QueryParameterExceptionResponse e) {
                                try {
                                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("strValue IN (");
                                    if (!aos.getString().isEmpty()) {
                                        for (String s : aos.getString()) {
                                            sb.append("'").append(s).append("',");
                                        }
                                        // remove last comma
                                        sb.delete(sb.length() - 1, sb.length());
                                    }
                                    where = sb.toString();
                                } catch (ClassCastException e3) {
                                    String strVal = parseAsString(paramValue);
                                    where = "strValue" + op + "\"" + strVal + "\"";
                                }
                            }
                        }
                    }
                    query.append(" AND ").append(delimiter).append("event_" + eventType + "_extensions").append(
                            delimiter).append(".");
                    query.append(where);
                    query.append(" AND ").append(delimiter).append("event_" + eventType + "_extensions").append(
                            delimiter).append(".fieldname=\"");
                    query.append(fieldname);
                    query.append("\"");

                } else if (paramName.startsWith("EXISTS_")) {
                    String fieldname = paramName.substring(7);
                    if (fieldname.equals("childEPCs")) {
                        if (eventType.equals("AggregationEvent")) {
                            query.append(" AND (").append(delimiter).append("event_AggregationEvent").append(delimiter).append(
                                    ".id IN (");
                            query.append("SELECT event_id FROM ").append(delimiter).append(
                                    "event_AggregationEvent_EPCs").append(delimiter);
                            query.append("))");
                        } else {
                            query.append(" AND 0");
                        }
                    } else if (fieldname.equals("epcList")) {
                        query.append(" AND (").append(delimiter).append("event_" + eventType).append(delimiter).append(
                                ".id IN (");
                        query.append("SELECT event_id FROM ").append(delimiter).append("event_" + eventType + "_EPCs").append(
                                delimiter);
                        query.append("))");
                    } else if (fieldname.equals("bizTransactionList")) {
                        query.append(" AND (").append(delimiter).append("event_" + eventType).append(delimiter).append(
                                ".id IN (");
                        query.append("SELECT event_id FROM ").append(delimiter).append(
                                "event_" + eventType + "_bizTrans").append(delimiter);
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
                            query.append(" AND (").append(delimiter).append("event_" + eventType).append(delimiter).append(
                                    ".id IN (");
                            query.append("SELECT event_id FROM ").append(delimiter).append(
                                    "event_" + eventType + "_extensions").append(delimiter).append(" ");
                            query.append("WHERE fieldname='" + fieldname + "'");
                            query.append("))");
                        }
                    }

                } else if (paramName.startsWith("HASATTR_") || paramName.startsWith("EQATTR_")) {
                    // parse fieldname and attrname from paramName
                    String fieldname = paramName.substring(8);
                    String attrname = null;
                    if (paramName.startsWith("EQATTR_")) {
                        fieldname = paramName.substring(7);
                        String[] parts = fieldname.split("_");
                        if (parts.length > 2) {
                            String msg = "Parameter '" + paramName
                                    + "' is invalid as it does not follow the pattern 'EQATTR_fieldname_attrname'.";
                            LOG.info("USER ERROR: " + msg);
                            QueryParameterException e = new QueryParameterException();
                            e.setReason(msg);
                            throw new QueryParameterExceptionResponse(msg, e);
                        } else if (parts.length == 2) {
                            // restrict also by attrname
                            fieldname = parts[0];
                            attrname = parts[1];
                        }
                    }

                    // get correct tablename for voc table
                    String tablename = null;
                    String biztrans = null;
                    if (fieldname.equalsIgnoreCase("epcClass") && eventType.equals("QuantityEvent")) {
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
                            String msg = "Attributes for fieldname extensions not implemented.";
                            LOG.warn(msg);
                            throw new UnsupportedOperationException(msg);
                        }
                    }

                    // construct the query
                    if (tablename != null) {
                        if (biztrans != null) {
                            query.append(" AND ").append(delimiter).append("event_");
                            query.append(eventType);
                            query.append(delimiter).append(".id IN (SELECT event_id FROM ").append(delimiter).append(
                                    "event_");
                            query.append(eventType);
                            query.append("_BizTrans").append(delimiter).append(
                                    " AS btrans WHERE btrans.bizTrans_id IN (");
                            query.append("SELECT id FROM ").append(delimiter).append("BizTransaction").append(delimiter).append(
                                    " WHERE ").append(delimiter).append("BizTransaction").append(delimiter).append(".");
                            query.append(biztrans);
                            query.append(" IN (SELECT id FROM ").append(delimiter).append("voc_");
                            query.append(tablename);
                            query.append(delimiter).append(" WHERE ");
                        } else {
                            query.append(" AND ");
                        }
                        query.append(delimiter).append("voc_");
                        query.append(tablename);
                        query.append(delimiter).append(".id IN (SELECT id FROM ").append(delimiter).append("voc_");
                        query.append(tablename);
                        query.append("_attr").append(delimiter).append(" WHERE ").append(delimiter).append("voc_");
                        query.append(tablename);
                        query.append("_attr").append(delimiter).append(".attribute");
                        if (attrname != null) {
                            query.append("=? AND ").append(delimiter).append("voc_");
                            query.append(tablename);
                            query.append("_attr").append(delimiter).append(".value");
                            queryArgs.add(attrname);
                        }
                        query.append(" IN (");
                        ArrayOfString aos = parseAsArrayOfString(paramValue);
                        listOfStringToSql(aos.getString(), query, queryArgs);
                        query.append("))");
                        if (biztrans != null) {
                            query.append(")))");
                        }
                    }

                } else if (paramName.equals("orderBy")) {
                    orderBy = parseAsString(paramValue);

                } else if (paramName.equals("orderDirection")) {
                    orderDirection = parseAsString(paramValue);

                } else if (paramName.equals("eventCountLimit")) {
                    limit = parseAsInteger(paramValue);

                } else if (paramName.equals("maxEventCount")) {
                    maxEventCount = parseAsInteger(paramValue).intValue();
                } else {
                    String msg = "The parameter " + paramName + " cannot be recognised.";
                    LOG.info("USER ERROR: " + msg);
                    QueryParameterException e = new QueryParameterException();
                    e.setReason(msg);
                    throw new QueryParameterExceptionResponse(msg, e);
                }
            } catch (ClassCastException e) {
                String msg = "The type of the value for query parameter '" + paramName + "': " + paramValue
                        + " is invalid.";
                LOG.info("USER ERROR: " + msg);
                LOG.debug(msg, e);
                QueryParameterException qpe = new QueryParameterException();
                qpe.setReason(msg);
                throw new QueryParameterExceptionResponse(msg, qpe, e);
            }
        }

        if (maxEventCount > -1 && limit > -1) {
            String msg = "Paramters 'maxEventCount' and 'eventCountLimit' are mutually exclusive.";
            LOG.info("USER ERROR: " + msg);
            QueryParameterException e = new QueryParameterException();
            e.setReason(msg);
            throw new QueryParameterExceptionResponse(msg, e);
        }

        if (orderBy.equals("") && limit > -1) {
            String msg = "Parameter 'eventCountLimit' may only be used when 'orderBy' is specified.";
            LOG.info("USER ERROR: " + msg);
            QueryParameterException e = new QueryParameterException();
            e.setReason(msg);
            throw new QueryParameterExceptionResponse(msg, e);
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
        LOG.debug("QUERY: " + q);
        PreparedStatement ps = connection.prepareStatement(q);
        for (int i = 0; i < queryArgs.size(); i++) {
            ps.setString(i + 1, (String) queryArgs.get(i));
            LOG.debug("       query param " + (i + 1) + ": " + queryArgs.get(i));
        }
        return ps;
    }

    /**
     * Parses the given query parameter value as String.
     * 
     * @param queryParamValue
     *            The query parameter value to be parsed as String.
     * @return The Float holding the value of the query parameter.
     */
    private String parseAsString(Object queryParamValue) throws ClassCastException {
        try {
            return (String) queryParamValue;
        } catch (ClassCastException e) {
            // trying to parse manually
            Element elem = (Element) queryParamValue;
            return elem.getTextContent();
        }
    }

    /**
     * Parses the given query parameter value as Float.
     * 
     * @param queryParamValue
     *            The query parameter value to be parsed as Float.
     * @return The Float holding the value of the query parameter.
     * @throws NumberFormatException
     *             If the query parameter value cannot be parsed as Float.
     */
    private Float parseAsFloat(Object queryParamValue) throws ClassCastException, NumberFormatException {
        try {
            return (Float) queryParamValue;
        } catch (ClassCastException e) {
            // trying to parse manually
            Element elem = (Element) queryParamValue;
            return Float.valueOf(elem.getTextContent());
        }
    }

    /**
     * Parses the given query parameter value as Timestamp.
     * 
     * @param queryParamValue
     *            The query parameter value to be parsed as Timestamp.
     * @param queryParamName
     *            The query parameter name.
     * @return The Timestamp holding the value of the query parameter.
     * @throws QueryParameterExceptionResponse
     *             If the query parameter value cannot be parsed as Timestamp.
     */
    private Timestamp parseAsTimestamp(Object queryParamValue, String queryParamName) throws ClassCastException,
            QueryParameterExceptionResponse {
        Timestamp ts;
        if (queryParamValue instanceof Calendar) {
            // Axis returns a Calendar instance
            ts = TimeParser.convert((Calendar) queryParamValue);
        } else if (queryParamValue instanceof XMLGregorianCalendar) {
            // CXF returns an XMLGregorianCalendar instance if the
            // XML type is
            // specified
            ts = TimeParser.convert(((XMLGregorianCalendar) queryParamValue).toGregorianCalendar());
        } else {
            // try to parse the value manually
            String date = null;
            if (queryParamValue instanceof Element) {
                // CXF returns an Element instance if no XML type
                // was specified in the request
                Element elem = (Element) queryParamValue;
                date = elem.getTextContent();
            } else {
                date = queryParamValue.toString();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Trying to parse the value (" + date + ") for parameter " + queryParamName + " as date/time");
            }
            try {
                ts = TimeParser.parseAsTimestamp(date);
            } catch (ParseException e) {
                String msg = "Unable to parse the value for query parameter '" + queryParamName + "' as date/time";
                LOG.warn(msg, e);
                QueryParameterException qpe = new QueryParameterException();
                qpe.setReason(msg);
                throw new QueryParameterExceptionResponse(msg, qpe, e);
            }
        }
        return ts;
    }

    /**
     * Parses the given query parameter value as Integer.
     * 
     * @param queryParamValue
     *            The query parameter value to be parsed as Integer.
     * @return The Integer holding the value of the query parameter.
     * @throws NumberFormatException
     *             If the query parameter value cannot be parsed as Integer.
     */
    private Integer parseAsInteger(Object queryParamValue) throws ClassCastException, NumberFormatException {
        try {
            return (Integer) queryParamValue;
        } catch (ClassCastException e) {
            // trying to parse manually
            Element elem = (Element) queryParamValue;
            return Integer.valueOf(elem.getTextContent());
        }
    }

    /**
     * Parses the given query parameter value into an ArrayOfString object.
     * 
     * @param queryParamValue
     *            The value of the query parameter to be parsed.
     * @return The ArrayOfString object representing the list of strings from
     *         the given parameter value.
     * @throws ClassCastException
     *             If the given paramValue instance cannot be cast to either an
     *             ArrayOfString or Element class.
     */
    private ArrayOfString parseAsArrayOfString(Object queryParamValue) throws ClassCastException {
        try {
            return (ArrayOfString) queryParamValue;
        } catch (ClassCastException e) {
            // trying to parse manually
            Element elem = (Element) queryParamValue;
            NodeList strings = elem.getChildNodes();
            ArrayOfString aos = new ArrayOfString();
            for (int i = 0; i < strings.getLength(); i++) {
                if ("string".equals(strings.item(i).getNodeName())) {
                    String s = strings.item(i).getTextContent();
                    aos.getString().add(s);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("ArrayOfString parsed to: " + aos.getString());
            }
            return aos;
        }
    }

    /**
     * @see org.accada.epcis.soapapi.EPCISServicePortType#getQueryNames(org.accada.epcis.soapapi.EmptyParms)
     * @param parms
     *            An empty parameter.
     * @return An ArrayOfString containing the names of all implemented queries.
     */
    public ArrayOfString getQueryNames(final EmptyParms parms) {
        LOG.debug("Invoking 'getQueryNames'");
        ArrayOfString qNames = new ArrayOfString();
        qNames.getString().addAll(QUERYNAMES);
        return qNames;
    }

    /**
     * Subscribes a query.
     * 
     * @see org.accada.epcis.soap.EPCISServicePortType#subscribe(org.accada.epcis.soap.model.Subscribe)
     * @param parms
     *            A Subscribe object containing the query to be subscribed..
     * @return Nothing.
     * @throws ImplementationException
     *             If a problem with the EPCIS implementation occurred.
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
    public VoidHolder subscribe(final Subscribe parms) throws DuplicateSubscriptionExceptionResponse,
            ImplementationExceptionResponse, QueryTooComplexExceptionResponse, SecurityExceptionResponse,
            InvalidURIExceptionResponse, ValidationExceptionResponse, SubscribeNotPermittedExceptionResponse,
            NoSuchNameExceptionResponse, SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse {
        LOG.debug("Invoking 'subscribe'");
        initConnection();

        QueryParams qParams = parms.getParams();
        String dest = parms.getDest();
        String subscrId = parms.getSubscriptionID();
        SubscriptionControls controls = parms.getControls();
        String triggerURI = controls.getTrigger();
        QuerySubscriptionScheduled newSubscription = null;
        String queryName = parms.getQueryName();
        Schedule schedule = null;
        GregorianCalendar initialRecordTime = parms.getControls().getInitialRecordTime().toGregorianCalendar();
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
                InvalidURIException e = new InvalidURIException();
                e.setReason(msg);
                throw new InvalidURIExceptionResponse(msg, e);
            }
            try {
                new URL(dest.toString());
            } catch (MalformedURLException ex) {
                String msg = "Destination URI is invalid: " + ex.getMessage();
                LOG.info("USER ERROR: " + msg);
                InvalidURIException e = new InvalidURIException();
                e.setReason(msg);
                throw new InvalidURIExceptionResponse(msg, e, ex);
            }

            // check query name
            if (!QUERYNAMES.contains(queryName)) {
                String msg = "Illegal query name '" + queryName + "'.";
                LOG.info("USER ERROR: " + msg);
                NoSuchNameException e = new NoSuchNameException();
                e.setReason(msg);
                throw new NoSuchNameExceptionResponse(msg, e);
            }

            // SimpleMasterDataQuery only valid for polling
            if (queryName.equals("SimpleMasterDataQuery")) {
                String msg = "Subscription not allowed for SimpleMasterDataQuery.";
                LOG.info("USER ERROR: " + msg);
                SubscribeNotPermittedException e = new SubscribeNotPermittedException();
                e.setReason(msg);
                throw new SubscribeNotPermittedExceptionResponse(msg, e);
            }

            // subscriptionID mustn't be empty.
            if (subscrId == null || subscrId.equals("")) {
                String msg = "SubscriptionID is empty. Choose a valid subscriptionID.";
                LOG.info(msg);
                ValidationException e = new ValidationException();
                e.setReason(msg);
                throw new ValidationExceptionResponse(msg, e);
            }

            // subscriptionID mustn't exist yet.
            if (fetchExistsSubscriptionId(subscrId)) {
                String msg = "SubscriptionID '" + subscrId + "' already exists. Choose a different subscriptionID.";
                LOG.info("USER ERROR: " + msg);
                DuplicateSubscriptionException e = new DuplicateSubscriptionException();
                e.setReason(msg);
                throw new DuplicateSubscriptionExceptionResponse(msg, e);
            }

            // trigger and schedule may no be used together, but one of them
            // must be set
            if (controls.getSchedule() != null && controls.getTrigger() != null) {
                String msg = "Schedule and trigger mustn't be used together.";
                LOG.info("USER ERROR: " + msg);
                SubscriptionControlsException e = new SubscriptionControlsException();
                e.setReason(msg);
                throw new SubscriptionControlsExceptionResponse(msg, e);
            }
            if (controls.getSchedule() == null && controls.getTrigger() == null) {
                String msg = "Either schedule or trigger has to be set.";
                LOG.info("USER ERROR: " + msg);
                SubscriptionControlsException e = new SubscriptionControlsException();
                e.setReason(msg);
                throw new SubscriptionControlsExceptionResponse(msg, e);
            }
            if (controls.getSchedule() != null) {
                // Scheduled Query -> parse schedule
                schedule = new Schedule(controls.getSchedule());
                newSubscription = new QuerySubscriptionScheduled(subscrId, qParams, dest, controls.isReportIfEmpty(),
                        initialRecordTime, initialRecordTime, schedule, queryName);
            } else {
                // -> Trigger
                // need to set schedule which says how often the trigger
                // condition is checked.
                QuerySchedule qSchedule = new QuerySchedule();
                qSchedule.setSecond(triggerConditionSeconds);
                if (triggerConditionMinutes != null) {
                    qSchedule.setMinute(triggerConditionMinutes);
                }
                schedule = new Schedule(qSchedule);
                QuerySubscriptionTriggered trigger = new QuerySubscriptionTriggered(subscrId, qParams, dest,
                        controls.isReportIfEmpty(), initialRecordTime, initialRecordTime, queryName, triggerURI,
                        schedule);
                newSubscription = trigger;
            }

            // load subscriptions
            Map<String, QuerySubscriptionScheduled> subscribedMap = loadSubscriptions();

            // store the Query to the database
            String insert = "INSERT INTO subscription (subscriptionid, "
                    + "params, dest, sched, trigg, initialrecordingtime, "
                    + "exportifempty, queryname, lastexecuted) VALUES "
                    + "((?), (?), (?), (?), (?), (?), (?), (?), (?))";
            PreparedStatement stmt = connection.prepareStatement(insert);
            LOG.debug("QUERY: " + insert);
            try {
                stmt.setString(1, subscrId);
                LOG.debug("       query param 1: " + subscrId);

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                ObjectOutput out = new ObjectOutputStream(outStream);
                out.writeObject(qParams);
                ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
                stmt.setBinaryStream(2, inStream, inStream.available());
                LOG.debug("       query param 2: [" + inStream.available() + " bytes]");

                stmt.setString(3, dest.toString());
                LOG.debug("       query param 3: " + dest.toString());

                outStream = new ByteArrayOutputStream();
                out = new ObjectOutputStream(outStream);
                out.writeObject(schedule);
                inStream = new ByteArrayInputStream(outStream.toByteArray());
                stmt.setBinaryStream(4, inStream, inStream.available());
                LOG.debug("       query param 4: [" + inStream.available() + " bytes]");

                String trigger = null;
                if (triggerURI != null) {
                    trigger = triggerURI.toString();
                }
                stmt.setString(5, trigger);
                LOG.debug("       query param 5: " + trigger);

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
                String msg = "Unable to store the subscription to the database: " + e.getMessage();
                LOG.error(msg);
                ImplementationException iex = new ImplementationException();
                iex.setReason(msg);
                iex.setSeverity(ImplementationExceptionSeverity.ERROR);
                throw new ImplementationExceptionResponse(msg, iex, e);
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
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw new ImplementationExceptionResponse(msg, iex, e);
        } finally {
            releaseConnection();
        }
    }

    /**
     * This method loads all the stored queries from the database, starts them
     * again and stores everything in a HasMap.
     * 
     * @return A Map mapping query names to scheduled query subscriptions.
     * @throws SQLException
     *             If a problem with the database occurred.
     * @throws ImplementationException
     *             If a problem with the EPCIS implementation occurred.
     */
    private Map<String, QuerySubscriptionScheduled> fetchSubscriptions() throws SQLException,
            ImplementationExceptionResponse {
        String query = "SELECT * FROM subscription";
        LOG.debug("QUERY: " + query);
        Statement stmt = connection.createStatement();
        QuerySubscriptionScheduled storedSubscription;
        GregorianCalendar initrectime = new GregorianCalendar();

        ResultSet rs = stmt.executeQuery(query);
        Map<String, QuerySubscriptionScheduled> subscribedMap = new HashMap<String, QuerySubscriptionScheduled>();
        while (rs.next()) {
            try {
                String subscrId = rs.getString("subscriptionid");

                ObjectInput in = new ObjectInputStream(rs.getBinaryStream("params"));

                QueryParam[] paramArray = (QueryParam[]) in.readObject();
                // convert QueryParam[] to QueryParams
                QueryParams params = new QueryParams();
                for (int i = 0; i < paramArray.length; i++) {
                    params.getParam().add(paramArray[i]);
                }
                String dest = rs.getString("dest");

                in = new ObjectInputStream(rs.getBinaryStream("sched"));
                Schedule sched = (Schedule) in.readObject();

                initrectime.setTime(rs.getTimestamp("initialrecordingtime"));

                boolean exportifempty = rs.getBoolean("exportifempty");

                String queryName = rs.getString("queryname");
                String trigger = rs.getString("trigg");

                if (trigger == null || trigger.length() == 0) {
                    storedSubscription = new QuerySubscriptionScheduled(subscrId, params, dest, exportifempty,
                            initrectime, new GregorianCalendar(), sched, queryName);
                } else {
                    storedSubscription = new QuerySubscriptionTriggered(subscrId, params, dest, exportifempty,
                            initrectime, new GregorianCalendar(), queryName, trigger, sched);
                }
                subscribedMap.put(subscrId, storedSubscription);
            } catch (SQLException e) {
                // sql exceptions are passed on
                throw e;
            } catch (Exception e) {
                // all other exceptions are caught
                String msg = "Unable to restore subscribed queries from the database.";
                LOG.error(msg, e);
                ImplementationException iex = new ImplementationException();
                iex.setReason(msg);
                iex.setSeverity(ImplementationExceptionSeverity.ERROR);
                throw new ImplementationExceptionResponse(msg, iex, e);
            }
        }
        rs.close();
        return subscribedMap;
    }

    /**
     * Stops a subscribed query from further invocations.
     * 
     * @see org.accada.epcis.soap.EPCISServicePortType#unsubscribe(org.accada.epcis.soapapi.Unsubscribe)
     * @param parms
     *            An Unsubscribe object containing the ID of the query to be
     *            unsubscribed.
     * @return Nothing.
     * @throws ImplementationException
     *             If a problem with the EPCIS implementation occurred.
     * @throws NoSuchSubscriptionException
     *             If the suscription id is not subscribed.
     */
    public VoidHolder unsubscribe(final Unsubscribe parms) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchSubscriptionExceptionResponse {
        LOG.debug("Invoking 'unsubscribe'");
        initConnection();
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
                String delete = "DELETE FROM subscription WHERE " + "subscriptionid = (?)";
                PreparedStatement stmt = connection.prepareStatement(delete);
                stmt.setString(1, subscrId);
                LOG.debug("QUERY: " + delete);
                LOG.debug("        query param 1: " + subscrId);

                stmt.executeUpdate();
                return new VoidHolder();
            } else {
                String msg = "There is no subscription with ID '" + subscrId + "'.";
                LOG.info("USER ERROR: " + msg);
                NoSuchSubscriptionException e = new NoSuchSubscriptionException();
                e.setReason(msg);
                throw new NoSuchSubscriptionExceptionResponse(msg, e);
            }
        } catch (SQLException e) {
            ImplementationException iex = new ImplementationException();
            String msg = "SQL error during query execution: " + e.getMessage();
            LOG.error(msg, e);
            iex.setReason(msg);
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw new ImplementationExceptionResponse(msg, iex, e);
        } finally {
            releaseConnection();
        }
    }

    /**
     * Returns an ArrayOfString containing IDs of all subscribed queries.
     * 
     * @see org.accada.epcis.soap.EPCISServicePortType#getSubscriptionIDs(org.accada.epcis.soap.model.GetSubscriptionIDs)
     * @param parms
     *            An empty parameter.
     * @return An ArrayOfString containing IDs of all subscribed queries.
     * @throws ImplementationException
     *             If a problem with the EPCIS implementation occurred.
     */
    public ArrayOfString getSubscriptionIDs(GetSubscriptionIDs parms) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse {
        LOG.debug("Invoking 'getSubscriptionIDs'");
        try {
            Map<String, QuerySubscriptionScheduled> subscribedMap = loadSubscriptions();
            Set<String> temp = subscribedMap.keySet();
            ArrayOfString arrOfStr = new ArrayOfString();
            arrOfStr.getString().addAll(temp);
            return arrOfStr;
        } catch (SQLException e) {
            ImplementationException iex = new ImplementationException();
            String msg = "SQL error during query execution: " + e.getMessage();
            LOG.error(msg, e);
            iex.setReason(msg);
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw new ImplementationExceptionResponse(msg, iex, e);
        }
    }

    /**
     * Runs (polls) a query.
     * 
     * @see org.accada.epcis.soap.EPCISServicePortType#poll(org.accada.epcis.soap.model.Poll)
     * @param parms
     *            The query to poll.
     * @return A QueryResults object containing the result of the query.
     * @throws ImplementationException
     *             If a problem with the EPCIS implementation occurred.
     * @throws QueryTooLargeException
     *             If the query is too large.
     * @throws QueryParameterException
     *             If one of the query parameters is invalid.
     * @throws NoSuchNameException
     *             If an invalid query type was provided.
     * @throws QueryTooComplexException
     *             If a query takes too long to return.
     */
    public QueryResults poll(Poll parms) throws ImplementationExceptionResponse, QueryTooComplexExceptionResponse,
            QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse,
            NoSuchNameExceptionResponse, QueryParameterExceptionResponse {
        LOG.debug("Invoking 'poll'");
        initConnection();

        // query type must be implemented.
        if (!QUERYNAMES.contains(parms.getQueryName())) {
            String msg = "Invalid query name '" + parms.getQueryName() + "' provided.";
            LOG.info("USER ERROR: " + msg);
            NoSuchNameException e = new NoSuchNameException();
            e.setReason(msg);
            throw new NoSuchNameExceptionResponse(msg, e);
        }

        String queryName = parms.getQueryName();
        if (queryName.equals("SimpleEventQuery")) {
            try {
                QueryParams queryParams = parms.getParams();
                EventListType eventList = new EventListType();

                PreparedStatement ps = createEventQuery(queryParams, "ObjectEvent");
                runObjectEventQuery(ps, eventList);

                ps = createEventQuery(queryParams, "AggregationEvent");
                runAggregationEventQuery(ps, eventList);

                ps = createEventQuery(queryParams, "QuantityEvent");
                runQuantityEventQuery(ps, eventList);

                ps = createEventQuery(queryParams, "TransactionEvent");
                runTransactionEventQuery(ps, eventList);

                QueryResultsBody resultsBody = new QueryResultsBody();
                if (!eventList.getObjectEventOrAggregationEventOrQuantityEvent().isEmpty()) {
                    resultsBody.setEventList(eventList);
                }

                QueryResults results = new QueryResults();
                results.setResultsBody(resultsBody);
                results.setQueryName(queryName);

                LOG.info("poll request for '" + queryName + "' succeeded");
                return results;
            } catch (SQLException e) {
                ImplementationException iex = new ImplementationException();
                String msg = "SQL error during query execution: " + e.getMessage();
                LOG.error(msg, e);
                iex.setReason(msg);
                iex.setSeverity(ImplementationExceptionSeverity.ERROR);
                throw new ImplementationExceptionResponse(msg, iex, e);
            } finally {
                releaseConnection();
            }
        } else if (queryName.equals("SimpleMasterDataQuery")) {
            QueryParams queryParams = parms.getParams();
            try {
                QueryResults results = createMasterDataQuery(queryParams);

                LOG.info("poll request for '" + queryName + "' succeeded");
                return results;
            } catch (SQLException e) {
                ImplementationException iex = new ImplementationException();
                String msg = "SQL error during query execution: " + e.getMessage();
                LOG.error(msg, e);
                iex.setReason(msg);
                iex.setSeverity(ImplementationExceptionSeverity.ERROR);
                throw new ImplementationExceptionResponse(msg, iex, e);
            } finally {
                releaseConnection();
            }
        } else {
            releaseConnection();
            String msg = "Unsupported query name '" + parms.getQueryName() + "' provided.";
            LOG.info("USER ERROR: " + msg);
            NoSuchNameException e = new NoSuchNameException();
            e.setReason(msg);
            throw new NoSuchNameExceptionResponse(msg, e);
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
     *             If an error accessing the database occurred.
     * @throws QueryParameterException
     *             If one of the provided QueryParam is invalid.
     * @throws ImplementationException
     *             If a service implementation error occurred.
     * @throws QueryTooLargeException
     *             If the query is too large to be executed.
     */
    private QueryResults createMasterDataQuery(final QueryParams queryParams) throws SQLException,
            QueryParameterExceptionResponse, ImplementationExceptionResponse, QueryTooLargeExceptionResponse {

        // populate a sorted map with the given parameters
        SortedMap<String, Object> params = new TreeMap<String, Object>();
        for (QueryParam param : queryParams.getParam()) {
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
            QueryParameterException qpe = new QueryParameterException();
            qpe.setReason(msg);
            throw new QueryParameterExceptionResponse(msg, qpe, e);
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
            QueryParameterException qpe = new QueryParameterException();
            qpe.setReason(msg);
            throw new QueryParameterExceptionResponse(msg, qpe, e);
        }

        // fetch vocabulary table names
        List<String> uris = new ArrayList<String>();
        if (params.containsKey("vocabularyName")) {
            Object val = params.remove("vocabularyName");
            uris = ((ArrayOfString) val).getString();
        }
        Map<String, String> tableNames = fetchVocabularyTableNames(uris);

        // filter vocabularies by name
        List<String> filterVocNames = new ArrayList<String>();
        if (params.containsKey("EQ_name")) {
            Object val = params.remove("EQ_name");
            filterVocNames = ((ArrayOfString) val).getString();
        }

        // filter vocabularies by name with desendants
        List<String> filterVocNamesWd = new ArrayList<String>();
        if (params.containsKey("WD_name")) {
            Object val = params.remove("WD_name");
            filterVocNamesWd = ((ArrayOfString) val).getString();
        }

        // filter vocabularies by attribute name
        List<String> filterVocAttrNames = new ArrayList<String>();
        if (params.containsKey("HASATTR")) {
            Object val = params.remove("HASATTR");
            filterVocAttrNames = ((ArrayOfString) val).getString();
        }

        // filter vocabularies by attribute value
        Map<String, List<String>> filterAttrs = new HashMap<String, List<String>>();
        for (String param : params.keySet()) {
            if (param.startsWith("EQATTR_")) {
                String attrname = param.substring(7);
                Object val = params.remove(param);
                List<String> values = ((ArrayOfString) val).getString();
                filterAttrs.put(attrname, values);
            }
        }

        // filter attributes by name
        List<String> filterAttrNames = new ArrayList<String>();
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
            List<String> vocs = fetchVocabularies(vocTableName, filterVocNames, filterVocNamesWd, filterAttrs,
                    filterVocAttrNames, maxElementCount);

            // handle each vocabulary element
            for (String voc : vocs) {
                VocabularyElementType vocElem = new VocabularyElementType();
                vocElem.setId(voc);

                List<AttributeType> attrList = vocElem.getAttribute();
                if (includeAttributes) {

                    // fetch all attributes for current vocabulary element
                    Map<String, String> attrMap = fetchAttributes(vocTableName, voc.toString(), filterAttrNames);

                    // handle each attribute element
                    for (String attrId : attrMap.keySet()) {
                        AttributeType attr = new AttributeType();
                        attr.setId(attrId);
                        String attrValue = attrMap.get(attrId);
                        // TODO attr value must be set with a text message
                        // element; is this correct?
                        Element elem = new DOMElement(attrValue);
                        elem.setNodeValue(attrValue);
                        attr.getAny().add(elem);
                        attrList.add(attr);
                    }
                }

                IDListType idList = null;
                if (includeChildren) {
                    // fetch all children for current vocabulary element
                    idList = new IDListType();
                    fetchChildren(vocTableName, voc.toString(), idList.getId());
                }
                vocElem.setChildren(idList);
                vocElemList.add(vocElem);
            }
            VocabularyElementListType vocElems = null;
            if (vocElemList.size() > 0) {
                vocElems = new VocabularyElementListType();
                vocElems.getVocabularyElement().addAll(vocElemList);

                VocabularyType voc = new VocabularyType();
                voc.setVocabularyElementList(vocElems);
                voc.setType(tableNames.get(vocTableName));
                vocList.add(voc);
            }
        }
        VocabularyListType vocListType = new VocabularyListType();
        vocListType.getVocabulary().addAll(vocList);

        QueryResultsBody resultsBody = new QueryResultsBody();
        resultsBody.setVocabularyList(vocListType);

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
     *             If an error accessing the database occurred.
     * @throws ImplementationException
     *             If an error converting a String to an URI occurred.
     * @throws QueryTooLargeException
     *             If the actual number of returned vocabularies would exceed
     *             the given maxElementCount.
     */
    private List<String> fetchVocabularies(final String table, final List<String> filterVocNames,
            final List<String> filterVocNamesWd, final Map<String, List<String>> filterAttrs, final List<String> attrs,
            final int maxElementCount) throws SQLException, ImplementationExceptionResponse,
            QueryTooLargeExceptionResponse {
        List<String> vocs = new ArrayList<String>();

        StringBuilder sql = new StringBuilder();
        List<String> queryArgs = new ArrayList<String>();
        sql.append("SELECT DISTINCT uri FROM ").append(delimiter);
        sql.append("voc_").append(table);
        sql.append(delimiter).append(" AS vocTable");
        // filter by attribute
        if (attrs.size() > 0 || filterAttrs.size() > 0) {
            sql.append(", ").append(delimiter);
            sql.append("voc_").append(table).append("_attr");
            sql.append(delimiter).append(" AS attrTable ");
            sql.append("WHERE vocTable.id=attrTable.id");
            if (attrs.size() > 0) {
                // filter by attribute name
                sql.append(" AND attrTable.attribute IN (");
                listOfStringToSql(attrs, sql, queryArgs);
                sql.append(")");
            }
            if (filterAttrs.size() > 0) {
                // filter by attribute name & value
                for (String attrname : filterAttrs.keySet()) {
                    sql.append(" AND attrTable.attribute=?");
                    queryArgs.add(attrname);
                    sql.append(" AND attrTable.value IN (");
                    listOfStringToSql(filterAttrs.get(attrname), sql, queryArgs);
                    sql.append(")");
                }
            }
        } else {
            sql.append(" WHERE 1");
        }
        if (!filterVocNames.isEmpty()) {
            // filter by voc name
            sql.append(" AND vocTable.uri IN (");
            listOfStringToSql(filterVocNames, sql, queryArgs);
            sql.append(")");
        }
        if (!filterVocNamesWd.isEmpty()) {
            sql.append(" AND (");
            for (String uri : filterVocNamesWd) {
                sql.append("vocTable.uri LIKE ? OR ");
                queryArgs.add(uri + "%");
            }
            sql.append("0)");
        }
        sql.append(";");

        String query = sql.toString();
        PreparedStatement ps = connection.prepareStatement(query);
        LOG.debug("QUERY: " + query);
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
                throw new QueryTooLargeExceptionResponse(msg, qtle);
            }
            vocs.add(rs.getString("uri"));
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
     *             If an error accessing the databse occurred.
     * @throws ImplementationException
     *             If an error converting a String to an URI occurred.
     */
    private Map<String, String> fetchVocabularyTableNames(final List<String> uris) throws SQLException,
            ImplementationExceptionResponse {
        Map<String, String> tableNames = new HashMap<String, String>();

        List<String> queryArgs = new ArrayList<String>(uris.size());
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT table_name, uri FROM ");
        sql.append(delimiter).append("Vocabularies").append(delimiter);
        if (uris.size() > 0) {
            sql.append(" WHERE uri IN (");
            listOfStringToSql(uris, sql, queryArgs);
            sql.append(")");
        }
        sql.append(";");
        String query = sql.toString();
        LOG.debug("QUERY: " + query);
        PreparedStatement ps = connection.prepareStatement(query);
        int i = 1;
        for (String arg : queryArgs) {
            LOG.debug("       query param " + i + ": " + arg);
            ps.setString(i++, arg);
        }
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String tableName = rs.getString("table_name");
            String uri = rs.getString("uri");
            tableNames.put(tableName, uri);
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
     *             If an error accessing the database occurred.
     */
    private Map<String, String> fetchAttributes(final String vocTableName, final String vocName,
            final List<String> filterAttrNames) throws SQLException {
        Map<String, String> attributes = new HashMap<String, String>();

        List<String> queryArgs = new ArrayList<String>(filterAttrNames.size());
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT attribute, value FROM ").append(delimiter);
        sql.append("voc_").append(vocTableName).append("_attr").append(delimiter);
        sql.append(" AS attrTable WHERE attrTable.id=(SELECT id FROM ").append(delimiter);
        sql.append("voc_").append(vocTableName).append(delimiter).append(" WHERE uri=?)");
        queryArgs.add(vocName);
        if (filterAttrNames.size() > 0) {
            // filter by attribute names
            sql.append(" AND attribute IN (");
            listOfStringToSql(filterAttrNames, sql, queryArgs);
            sql.append(");");
        } else {
            sql.append(";");
        }

        String query = sql.toString();
        PreparedStatement ps = connection.prepareStatement(query);
        LOG.debug("QUERY: " + query);
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
     * @throws SQLException
     *             If a DB access error occurred.
     * @throws ImplementationException
     *             If a String could not be converted into an URI.
     */
    private void fetchChildren(final String vocTableName, final String vocUri, List<String> children)
            throws SQLException, ImplementationExceptionResponse {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT uri FROM ").append(delimiter);
        sql.append("voc_").append(vocTableName).append(delimiter);
        sql.append(" AS vocTable WHERE vocTable.uri LIKE ?;");

        String query = sql.toString();
        PreparedStatement ps = connection.prepareStatement(query);
        String arg = vocUri + "_%";
        LOG.debug("QUERY: " + query);
        LOG.debug("       query param 1: " + arg);
        ps.setString(1, arg);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            children.add(rs.getString("uri"));
        }
    }

    /**
     * Returns the standard version.
     * 
     * @see org.accada.epcis.soap.EPCISServicePortType#getStandardVersion(org.accada.epcis.soap.model.EmptyParms)
     * @param parms
     *            An empty parameter.
     * @return The standard version.
     */
    public String getStandardVersion(EmptyParms arg0) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse {
        LOG.debug("Invoking 'getStandardVersion'");
        return STD_VERSION;
    }

    /**
     * Returns the vendor version.
     * 
     * @see org.accada.epcis.soap.EPCISServicePortType#getVendorVersion(org.accada.epcis.soap.model.EmptyParms)
     * @param parms
     *            An empty parameter.
     * @return The vendor version. The empty string indicates that the
     *         implementation implements only standard functionality with no
     *         vendor extensions.
     */
    public String getVendorVersion(EmptyParms arg0) throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse {
        LOG.debug("Invoking 'getVendorVersion'");
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
    private void checkActionValues(final List<String> actions) throws QueryParameterExceptionResponse {
        for (String action : actions) {
            if (!(action.equalsIgnoreCase("ADD") || action.equalsIgnoreCase("OBSERVE") || action.equalsIgnoreCase("DELETE"))) {
                String msg = "Invalid value for parameter EQ_action: " + action
                        + ". Must be one of ADD, OBSERVE, or DELETE.";
                LOG.info("USER ERROR: " + msg);
                QueryParameterException qpe = new QueryParameterException();
                qpe.setReason(msg);
                throw new QueryParameterExceptionResponse(msg, qpe);
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
     *             If the query takes longer than the given timeout.
     * @throws SQLException
     *             If the execution of the query threw an exception.
     */
    private ResultSet executeStatement(final PreparedStatement ps, final long timeout)
            throws QueryTooComplexExceptionResponse, SQLException {
        if (timeout > 0) {
            // start query execution in a new thread
            Query query = new Query(ps);
            query.start();

            // wait some time for the query to execute
            synchronized (query) {
                try {
                    query.wait(timeout);
                } catch (InterruptedException e) {
                    // continue
                }
            }

            // check if the query returned before the timeout
            ResultSet rs = query.getResultSet();
            query.checkException();
            if (rs == null) {
                // query has not yet finished and takes too long
                String msg = "Execution of a query takes longer than this implementation is willing to accept.";
                LOG.info("USER ERROR: " + msg);
                QueryTooComplexException e = new QueryTooComplexException();
                e.setReason(msg);
                throw new QueryTooComplexExceptionResponse(msg, e);
            }

            // query returned before timeout
            if (LOG.isDebugEnabled()) {
                rs.last();
                int rowcount = rs.getRow();
                rs.beforeFirst();
                BigDecimal bd = new BigDecimal(query.getExecutionTime()).divide(new BigDecimal(1000));
                String time = bd.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
                LOG.debug(rowcount + " rows fetched (" + time + "s).");
            }
            return query.getResultSet();
        } else {
            // timeout value is actually set to an invalid value!
            String msg = "Execution of a query takes longer than this implementation is willing to accept.";
            LOG.info("USER ERROR: " + msg);
            QueryTooComplexException e = new QueryTooComplexException();
            e.setReason(msg);
            throw new QueryTooComplexExceptionResponse(msg, e);
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

    /**
     * Saves the map with the subscriptions to the message context.
     * 
     * @param subscriptions
     *            The map with the subscriptions.
     */
    private void saveSubscriptions(final Map<String, QuerySubscriptionScheduled> subscriptions) {
        MessageContext msgContext = context.getMessageContext();
        ServletContext ctx = (ServletContext) msgContext.get(MessageContext.SERVLET_CONTEXT);
        ctx.setAttribute("subscribedMap", subscriptions);
    }

    /**
     * Retrieves the map with the subscriptions from the servlet context.
     * 
     * @return The map with the subscriptions.
     * @throws ImplementationException
     *             If the map could not be reloaded.
     * @throws SQLException
     *             If a database error occurred.
     */
    private Map<String, QuerySubscriptionScheduled> loadSubscriptions() throws ImplementationExceptionResponse,
            SQLException {
        MessageContext msgContext = context.getMessageContext();
        ServletContext ctx = (ServletContext) msgContext.get(MessageContext.SERVLET_CONTEXT);
        Map<String, QuerySubscriptionScheduled> subscriptions = (HashMap<String, QuerySubscriptionScheduled>) ctx.getAttribute("subscribedMap");
        if (subscriptions == null) {
            subscriptions = fetchSubscriptions();
        }
        return subscriptions;
    }

    /**
     * Creates a new XMLGregorianCalendar from the given java.sql.Timestamp.
     * 
     * @param time
     *            The time to convert.
     * @return The converted calendar.
     * @throws ImplementationExceptionResponse
     */
    private XMLGregorianCalendar createXmlCalendarFromSqlTimestamp(Timestamp time)
            throws ImplementationExceptionResponse {
        try {
            DatatypeFactory factory = DatatypeFactory.newInstance();
            Calendar cal = TimeParser.convert(time);
            return factory.newXMLGregorianCalendar((GregorianCalendar) cal);
        } catch (DatatypeConfigurationException e) {
            String msg = "Unable to instantiate an XML representation for a date/time datatype.";
            ImplementationException iex = new ImplementationException();
            iex.setReason(msg);
            iex.setSeverity(ImplementationExceptionSeverity.SEVERE);
            throw new ImplementationExceptionResponse(msg, iex, e);
        }
    }

    private void initConnection() throws ImplementationExceptionResponse {
        try {
            connection = getDataSource().getConnection();
            LOG.debug("Database connection established");
        } catch (SQLException e) {
            ImplementationException iex = new ImplementationException();
            String msg = "SQL error when initializing database connection";
            LOG.error(msg, e);
            iex.setReason(msg);
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw new ImplementationExceptionResponse(msg, iex, e);
        }
    }

    private void releaseConnection() throws ImplementationExceptionResponse {
        try {
            connection.close();
            LOG.debug("Database connection closed");
        } catch (SQLException e) {
            ImplementationException iex = new ImplementationException();
            String msg = "SQL error when releasing database connection";
            LOG.error(msg, e);
            iex.setReason(msg);
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw new ImplementationExceptionResponse(msg, iex, e);
        }
    }

    /**
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource
     *            the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return the maxQueryRows
     */
    public int getMaxQueryRows() {
        return maxQueryRows;
    }

    /**
     * @param maxQueryRows
     *            the maxQueryRows to set
     */
    public void setMaxQueryRows(int maxQueryRows) {
        this.maxQueryRows = maxQueryRows;
    }

    /**
     * @return the maxQueryTime
     */
    public int getMaxQueryTime() {
        return maxQueryTime;
    }

    /**
     * @param maxQueryTime
     *            the maxQueryTime to set
     */
    public void setMaxQueryTime(int maxQueryTime) {
        this.maxQueryTime = maxQueryTime;
    }

    /**
     * @return the triggerConditionSeconds
     */
    public String getTriggerConditionSeconds() {
        return triggerConditionSeconds;
    }

    /**
     * @param triggerConditionSeconds
     *            the triggerConditionSeconds to set
     */
    public void setTriggerConditionSeconds(String triggerConditionSeconds) {
        this.triggerConditionSeconds = triggerConditionSeconds;
    }

    /**
     * @return the triggerConditionMinutes
     */
    public String getTriggerConditionMinutes() {
        return triggerConditionMinutes;
    }

    /**
     * @param triggerConditionMinutes
     *            the triggerConditionMinutes to set
     */
    public void setTriggerConditionMinutes(String triggerConditionMinutes) {
        this.triggerConditionMinutes = triggerConditionMinutes;
    }
}
