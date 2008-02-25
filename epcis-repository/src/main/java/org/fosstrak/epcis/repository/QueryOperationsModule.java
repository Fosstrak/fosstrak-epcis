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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.accada.epcis.soap.DuplicateSubscriptionExceptionResponse;
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
import org.accada.epcis.soap.model.ArrayOfString;
import org.accada.epcis.soap.model.AttributeType;
import org.accada.epcis.soap.model.DuplicateSubscriptionException;
import org.accada.epcis.soap.model.EventListType;
import org.accada.epcis.soap.model.IDListType;
import org.accada.epcis.soap.model.ImplementationException;
import org.accada.epcis.soap.model.ImplementationExceptionSeverity;
import org.accada.epcis.soap.model.InvalidURIException;
import org.accada.epcis.soap.model.NoSuchNameException;
import org.accada.epcis.soap.model.NoSuchSubscriptionException;
import org.accada.epcis.soap.model.QueryParam;
import org.accada.epcis.soap.model.QueryParameterException;
import org.accada.epcis.soap.model.QueryParams;
import org.accada.epcis.soap.model.QueryResults;
import org.accada.epcis.soap.model.QueryResultsBody;
import org.accada.epcis.soap.model.QuerySchedule;
import org.accada.epcis.soap.model.QueryTooLargeException;
import org.accada.epcis.soap.model.SubscribeNotPermittedException;
import org.accada.epcis.soap.model.SubscriptionControls;
import org.accada.epcis.soap.model.SubscriptionControlsException;
import org.accada.epcis.soap.model.ValidationException;
import org.accada.epcis.soap.model.VocabularyElementListType;
import org.accada.epcis.soap.model.VocabularyElementType;
import org.accada.epcis.soap.model.VocabularyListType;
import org.accada.epcis.soap.model.VocabularyType;
import org.accada.epcis.utils.TimeParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class QueryOperationsModule implements EpcisQueryControlInterface {

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
     * Basic SQL query string for transaction events.
     */
    private static final String transactionEventQueryBase = "SELECT SQL_CALC_FOUND_ROWS DISTINCT event_TransactionEvent.id, eventTime, recordTime, eventTimeZoneOffset, action, parentID, voc_BizStep.uri AS bizStep, voc_Disposition.uri AS disposition, voc_ReadPoint.uri AS readPoint, voc_BizLoc.uri AS bizLocation FROM event_TransactionEvent LEFT JOIN voc_BizStep ON event_TransactionEvent.bizStep = voc_BizStep.id LEFT JOIN voc_Disposition ON event_TransactionEvent.disposition = voc_Disposition.id LEFT JOIN voc_ReadPoint ON event_TransactionEvent.readPoint = voc_ReadPoint.id LEFT JOIN voc_BizLoc ON event_TransactionEvent.bizLocation = voc_BizLoc.id LEFT JOIN event_TransactionEvent_extensions ON event_TransactionEvent.id = event_TransactionEvent_extensions.event_id WHERE 1 ";

    /**
     * Basic SQL query string for quantity events.
     */
    private static final String quantityEventQueryBase = "SELECT SQL_CALC_FOUND_ROWS DISTINCT event_QuantityEvent.id, eventTime, recordTime, eventTimeZoneOffset, voc_EPCClass.uri AS epcClass, quantity, voc_BizStep.uri AS bizStep, voc_Disposition.uri AS disposition, voc_ReadPoint.uri AS readPoint, voc_BizLoc.uri AS bizLocation FROM event_QuantityEvent LEFT JOIN voc_BizStep ON event_QuantityEvent.bizStep = voc_BizStep.id LEFT JOIN voc_Disposition ON event_QuantityEvent.disposition = voc_Disposition.id LEFT JOIN voc_ReadPoint ON event_QuantityEvent.readPoint = voc_ReadPoint.id LEFT JOIN voc_BizLoc ON event_QuantityEvent.bizLocation = voc_BizLoc.id LEFT JOIN voc_EPCClass ON event_QuantityEvent.epcClass = voc_EPCClass.id LEFT JOIN event_QuantityEvent_extensions ON event_QuantityEvent.id = event_QuantityEvent_extensions.event_id WHERE 1 ";

    /**
     * Basic SQL query string for aggregation events.
     */
    private static final String aggregationEventQueryBase = "SELECT SQL_CALC_FOUND_ROWS DISTINCT event_AggregationEvent.id, eventTime, recordTime, eventTimeZoneOffset, parentID, action, voc_BizStep.uri AS bizStep, voc_Disposition.uri AS disposition, voc_ReadPoint.uri AS readPoint, voc_BizLoc.uri AS bizLocation FROM event_AggregationEvent LEFT JOIN voc_BizStep ON event_AggregationEvent.bizStep = voc_BizStep.id LEFT JOIN voc_Disposition ON event_AggregationEvent.disposition  = voc_Disposition.id LEFT JOIN voc_ReadPoint ON event_AggregationEvent.readPoint = voc_ReadPoint.id LEFT JOIN voc_BizLoc ON event_AggregationEvent.bizLocation = voc_BizLoc.id LEFT JOIN event_AggregationEvent_extensions ON event_AggregationEvent.id = event_AggregationEvent_extensions.event_id WHERE 1 ";

    /**
     * Basic SQL query string for object events.
     */
    private static final String objectEventQueryBase = "SELECT SQL_CALC_FOUND_ROWS DISTINCT event_ObjectEvent.id, eventTime, recordTime, eventTimeZoneOffset, action, voc_BizStep.uri AS bizStep, voc_Disposition.uri AS disposition, voc_ReadPoint.uri AS readPoint, voc_BizLoc.uri AS bizLocation FROM event_ObjectEvent LEFT JOIN voc_BizStep ON event_ObjectEvent.bizStep = voc_BizStep.id LEFT JOIN voc_Disposition ON event_ObjectEvent.disposition = voc_Disposition.id LEFT JOIN voc_ReadPoint ON event_ObjectEvent.readPoint = voc_ReadPoint.id LEFT JOIN voc_BizLoc ON event_ObjectEvent.bizLocation = voc_BizLoc.id LEFT JOIN event_ObjectEvent_extensions ON event_ObjectEvent.id = event_ObjectEvent_extensions.event_id WHERE 1";

    /**
     * The names of all the implemented queries.
     */
    private static final List<String> QUERYNAMES = new ArrayList<String>();

    static {
        QUERYNAMES.add("SimpleEventQuery");
        QUERYNAMES.add("SimpleMasterDataQuery");
    }

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

    private ServletContext servletContext;

    private DataSource dataSource;

    private Connection connection;

    private QueryOperationsBackend backend = new QueryOperationsBackend();

    public enum EventType {
        AggregationEvent, ObjectEvent, QuantityEvent, TransactionEvent
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
    private List<SimpleEventQuery> constructSimpleEventQueries(final QueryParams queryParams) throws SQLException,
            QueryParameterExceptionResponse, ImplementationExceptionResponse {
        SimpleEventQuery aggrEventQuery = new SimpleEventQuery(EventType.AggregationEvent);
        SimpleEventQuery objEventQuery = new SimpleEventQuery(EventType.ObjectEvent);
        SimpleEventQuery quantEventQuery = new SimpleEventQuery(EventType.QuantityEvent);
        SimpleEventQuery transEventQuery = new SimpleEventQuery(EventType.TransactionEvent);

        boolean includeAggrEvents = true;
        boolean includeObjEvents = true;
        boolean includeQuantEvents = true;
        boolean includeTransEvents = true;

        String orderBy = null;
        String orderDirection = null;
        int eventCountLimit = -1;
        int maxEventCount = -1;

        // a sorted List of query parameter names - keeps track of the processed
        // names in order to cope with duplicates
        List<String> sortedParamNames = new ArrayList<String>();

        for (QueryParam param : queryParams.getParam()) {
            String paramName = param.getName();
            Object paramValue = param.getValue();

            // check for null values
            if (paramName == null || "".equals(paramName)) {
                String msg = "Missing name for a query parameter";
                LOG.info("USER ERROR: " + msg);
                throw new QueryParameterExceptionResponse(msg);
            }
            if (paramValue == null) {
                String msg = "Missing value for query parameter '" + paramName + "'";
                LOG.info("USER ERROR: " + msg);
                throw new QueryParameterExceptionResponse(msg);
            }
            // check if the current query parameter has already been provided
            int index = Collections.binarySearch(sortedParamNames, paramName);
            if (index < 0) {
                // we have not yet seen this query parameter name - ok
                sortedParamNames.add(-index - 1, paramName);
            } else {
                // we have already handled this query parameter name - not ok
                String msg = "Query parameter '" + paramName + "' provided more than once";
                LOG.info("USER ERROR: " + msg);
                throw new QueryParameterExceptionResponse(msg);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Handling query parameter: " + paramName);
            }
            try {
                if (paramName.equals("eventType")) {
                    // by default all event types will be included
                    List<String> eventTypeStrings = parseAsArrayOfString(paramValue).getString();
                    if (!eventTypeStrings.isEmpty()) {
                        // check if valid event types are provided
                        List<EventType> eventTypes = new ArrayList<EventType>(eventTypeStrings.size());
                        for (String eventTypeString : eventTypeStrings) {
                            try {
                                eventTypes.add(EventType.valueOf(eventTypeString));
                            } catch (IllegalArgumentException e) {
                                String msg = "Unsupported eventType: " + eventTypeString;
                                LOG.info("USER ERROR: " + msg);
                                QueryParameterException qpe = new QueryParameterException();
                                qpe.setReason(msg);
                                throw new QueryParameterExceptionResponse(msg, qpe);
                            }
                        }

                        // check for excluded event types
                        if (!eventTypes.contains(EventType.AggregationEvent)) {
                            includeAggrEvents = false;
                        }
                        if (!eventTypes.contains(EventType.ObjectEvent)) {
                            includeObjEvents = false;
                        }
                        if (!eventTypes.contains(EventType.QuantityEvent)) {
                            includeQuantEvents = false;
                        }
                        if (!eventTypes.contains(EventType.TransactionEvent)) {
                            includeTransEvents = false;
                        }
                    }
                } else if (paramName.equals("GE_eventTime") || paramName.equals("LT_eventTime")
                        || paramName.equals("GE_recordTime") || paramName.equals("LT_recordTime")) {
                    Timestamp ts = parseAsTimestamp(paramValue, paramName);
                    String comparator = (paramName.startsWith("GE")) ? ">=" : "<";
                    String eventField = paramName.substring(3, paramName.length());
                    aggrEventQuery.addEventQueryParam(eventField, comparator, ts);
                    objEventQuery.addEventQueryParam(eventField, comparator, ts);
                    quantEventQuery.addEventQueryParam(eventField, comparator, ts);
                    transEventQuery.addEventQueryParam(eventField, comparator, ts);

                } else if (paramName.equals("EQ_action")) {
                    // QuantityEvents have no "action" field, thus exclude them
                    includeQuantEvents = false;
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        checkActionValues(aos.getString());
                        aggrEventQuery.addEventQueryParam("action", "IN", aos.getString());
                        objEventQuery.addEventQueryParam("action", "IN", aos.getString());
                        transEventQuery.addEventQueryParam("action", "IN", aos.getString());
                    }

                } else if (paramName.equals("EQ_bizStep") || paramName.equals("EQ_disposition")
                        || paramName.equals("EQ_readPoint") || paramName.equals("EQ_bizLocation")) {
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        String eventField = paramName.substring(3, paramName.length());
                        aggrEventQuery.addEventQueryParam(eventField, "IN", aos.getString());
                        objEventQuery.addEventQueryParam(eventField, "IN", aos.getString());
                        quantEventQuery.addEventQueryParam(eventField, "IN", aos.getString());
                        transEventQuery.addEventQueryParam(eventField, "IN", aos.getString());
                    }

                } else if (paramName.equals("WD_readPoint") || paramName.equals("WD_bizLocation")) {
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        // append a "*" to each of the parameter values - this
                        // should implement the semantics of "With Descendant"
                        // TODO: should???
                        CollectionUtils.transform(aos.getString(), new StringTransformer());
                        String eventField = paramName.substring(3, paramName.length());
                        aggrEventQuery.addEventQueryParam(eventField, "LIKE", aos.getString());
                        objEventQuery.addEventQueryParam(eventField, "LIKE", aos.getString());
                        quantEventQuery.addEventQueryParam(eventField, "LIKE", aos.getString());
                        transEventQuery.addEventQueryParam(eventField, "LIKE", aos.getString());
                    }

                } else if (paramName.startsWith("EQ_bizTransaction_")) {
                    // type extracted from parameter name
                    String bizTransType = paramName.substring(18);
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        aggrEventQuery.addEventQueryParam("bizTransList.type", "=", aos.getString());
                        objEventQuery.addEventQueryParam("bizTransList.type", "=", aos.getString());
                        quantEventQuery.addEventQueryParam("bizTransList.type", "=", aos.getString());
                        transEventQuery.addEventQueryParam("bizTransList.type", "=", aos.getString());
                        aggrEventQuery.addEventQueryParam("bizTransList.bizTrans", "IN", aos.getString());
                        objEventQuery.addEventQueryParam("bizTransList.bizTrans", "IN", aos.getString());
                        quantEventQuery.addEventQueryParam("bizTransList.bizTrans", "IN", aos.getString());
                        transEventQuery.addEventQueryParam("bizTransList.bizTrans", "IN", aos.getString());
                    }

                } else if (paramName.equals("MATCH_epc") || paramName.equals("MATCH_anyEPC")) {
                    // QuantityEvents have no field for EPCs, thus exclude them
                    includeQuantEvents = false;
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        aggrEventQuery.addEventQueryParam("childEPCs", "LIKE", aos.getString());
                        objEventQuery.addEventQueryParam("epcList", "LIKE", aos.getString());
                        transEventQuery.addEventQueryParam("epcList", "LIKE", aos.getString());
                        if (paramName.equals("MATCH_anyEPC")) {
                            // for AggregationEvent and TransactionEvent also
                            // look into "parentID" field
                            aggrEventQuery.addEventQueryParam("parentID", "LIKE", aos.getString());
                            transEventQuery.addEventQueryParam("parentID", "LIKE", aos.getString());
                        }
                    }

                } else if (paramName.equals("MATCH_parentID")) {
                    includeQuantEvents = false;
                    includeObjEvents = false;
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        aggrEventQuery.addEventQueryParam("parentID", "LIKE", aos.getString());
                        transEventQuery.addEventQueryParam("parentID", "LIKE", aos.getString());
                    }

                } else if (paramName.equals("MATCH_epcClass")) {
                    includeAggrEvents = false;
                    includeObjEvents = false;
                    includeTransEvents = false;
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        quantEventQuery.addEventQueryParam("epcClass", "IN", aos.getString());
                    }

                } else if (paramName.endsWith("_quantity")) {
                    includeAggrEvents = false;
                    includeObjEvents = false;
                    includeTransEvents = false;
                    String op = parseComparator(paramName);
                    quantEventQuery.addEventQueryParam("quantity", op, parseAsInteger(paramValue));

                } else if (paramName.startsWith("GT_") || paramName.startsWith("GE_") || paramName.startsWith("EQ_")
                        || paramName.startsWith("LE_") || paramName.startsWith("LT_")) {
                    // must be an event field extension
                    String fieldname = paramName.substring(3);
                    String[] parts = fieldname.split("#");
                    if (parts.length != 2) {
                        String msg = "Invalid parameter " + paramName;
                        LOG.info("USER ERROR: " + msg);
                        QueryParameterException e = new QueryParameterException();
                        e.setReason(msg);
                        throw new QueryParameterExceptionResponse(msg, e);
                    }
                    String op = parseComparator(paramName);
                    String eventField;
                    Object value;
                    try {
                        value = parseAsInteger(paramValue);
                        eventField = "extension.intValue";
                    } catch (NumberFormatException e1) {
                        try {
                            value = parseAsFloat(paramValue);
                            eventField = "extension.floatValue";
                        } catch (NumberFormatException e2) {
                            try {
                                value = parseAsTimestamp(paramValue, paramName);
                                eventField = "extension.dateValue";
                            } catch (QueryParameterExceptionResponse e) {
                                value = parseAsString(paramValue);
                                eventField = "extension.strValue";
                            }
                        }
                    }
                    aggrEventQuery.addEventQueryParam(eventField, op, value);
                    objEventQuery.addEventQueryParam(eventField, op, value);
                    quantEventQuery.addEventQueryParam(eventField, op, value);
                    transEventQuery.addEventQueryParam(eventField, op, value);
                    aggrEventQuery.addEventQueryParam("extension.fieldname", "=", fieldname);
                    objEventQuery.addEventQueryParam("extension.fieldname", "=", fieldname);
                    quantEventQuery.addEventQueryParam("extension.fieldname", "=", fieldname);
                    transEventQuery.addEventQueryParam("extension.fieldname", "=", fieldname);

                } else if (paramName.startsWith("EXISTS_")) {
                    String fieldname = paramName.substring(7);
                    if (fieldname.equals("childEPCs")) {
                        includeObjEvents = false;
                        includeQuantEvents = false;
                        includeTransEvents = false;
                        aggrEventQuery.addEventQueryParam("childEPCs", "EXISTS", null);
                    } else if (fieldname.equals("epcList")) {
                        includeAggrEvents = false;
                        includeQuantEvents = false;
                        objEventQuery.addEventQueryParam("epcList", "EXISTS", null);
                        transEventQuery.addEventQueryParam("epcList", "EXISTS", null);
                    } else if (fieldname.equals("action")) {
                        includeQuantEvents = false;
                        aggrEventQuery.addEventQueryParam("action", "EXISTS", null);
                        objEventQuery.addEventQueryParam("action", "EXISTS", null);
                        transEventQuery.addEventQueryParam("action", "EXISTS", null);
                    } else if (fieldname.equals("parentID")) {
                        includeObjEvents = false;
                        includeQuantEvents = false;
                        aggrEventQuery.addEventQueryParam("parentID", "EXISTS", null);
                        transEventQuery.addEventQueryParam("parentID", "EXISTS", null);
                    } else if (fieldname.equals("quantity") || fieldname.equals("epcClass")) {
                        includeAggrEvents = false;
                        includeObjEvents = false;
                        includeTransEvents = false;
                        quantEventQuery.addEventQueryParam(fieldname, "EXISTS", null);
                    } else if (fieldname.equals("eventTime") || fieldname.equals("recordTime")
                            || fieldname.equals("eventTimeZoneOffset") || fieldname.equals("bizStep")
                            || fieldname.equals("disposition") || fieldname.equals("readPoint")
                            || fieldname.equals("bizLocation") || fieldname.equals("bizTransList")) {
                        aggrEventQuery.addEventQueryParam(fieldname, "EXISTS", null);
                        objEventQuery.addEventQueryParam(fieldname, "EXISTS", null);
                        quantEventQuery.addEventQueryParam(fieldname, "EXISTS", null);
                        transEventQuery.addEventQueryParam(fieldname, "EXISTS", null);
                    } else {
                        // lets see if we have an extension fieldname
                        String[] parts = fieldname.split("#");
                        if (parts.length != 2) {
                            String msg = "Invalid parameter " + paramName;
                            LOG.info("USER ERROR: " + msg);
                            QueryParameterException e = new QueryParameterException();
                            e.setReason(msg);
                            throw new QueryParameterExceptionResponse(msg, e);
                        }
                        aggrEventQuery.addEventQueryParam("extension.fieldname", "=", fieldname);
                        objEventQuery.addEventQueryParam("extension.fieldname", "=", fieldname);
                        quantEventQuery.addEventQueryParam("extension.fieldname", "=", fieldname);
                        transEventQuery.addEventQueryParam("extension.fieldname", "=", fieldname);
                    }

                } else if (paramName.startsWith("HASATTR_")) {
                    // restrict by attribute name
                    String fieldname = paramName.substring(8);
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    String eventField = fieldname + ".attribute";
                    aggrEventQuery.addEventQueryParam(eventField, "IN", aos.getString());
                    objEventQuery.addEventQueryParam(eventField, "IN", aos.getString());
                    quantEventQuery.addEventQueryParam(eventField, "IN", aos.getString());
                    transEventQuery.addEventQueryParam(eventField, "IN", aos.getString());

                } else if (paramName.startsWith("EQATTR_")) {
                    String fieldname = paramName.substring(7);
                    String attrname = null;
                    String[] parts = fieldname.split("_");
                    if (parts.length > 2) {
                        String msg = "Query parameter has invalid format: " + paramName
                                + ". Expected: EQATTR_fieldname_attrname";
                        LOG.info("USER ERROR: " + msg);
                        QueryParameterException e = new QueryParameterException();
                        e.setReason(msg);
                        throw new QueryParameterExceptionResponse(msg, e);
                    } else if (parts.length == 2) {
                        fieldname = parts[0];
                        attrname = parts[1];
                    }
                    // restrict by attribute name
                    String eventField = fieldname + ".attribute";
                    aggrEventQuery.addEventQueryParam(eventField, "=", attrname);
                    objEventQuery.addEventQueryParam(eventField, "=", attrname);
                    quantEventQuery.addEventQueryParam(eventField, "=", attrname);
                    transEventQuery.addEventQueryParam(eventField, "=", attrname);
                    // restrict by attribute value
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    eventField = eventField + ".value";
                    aggrEventQuery.addEventQueryParam(eventField, "IN", aos.getString());
                    objEventQuery.addEventQueryParam(eventField, "IN", aos.getString());
                    quantEventQuery.addEventQueryParam(eventField, "IN", aos.getString());
                    transEventQuery.addEventQueryParam(eventField, "IN", aos.getString());

                } else if (paramName.equals("orderBy")) {
                    orderBy = parseAsString(paramValue);
                    if (!"eventTime".equals(orderBy) && !"recordTime".equals(orderBy) && !"quantity".equals(orderBy)) {
                        String[] parts = orderBy.split("#");
                        if (parts.length != 2) {
                            String msg = "orderBy must be one of eventTime, recordTime, quantity, or an extension field";
                            LOG.info("USER ERROR: " + msg);
                            QueryParameterException e = new QueryParameterException();
                            e.setReason(msg);
                            throw new QueryParameterExceptionResponse(msg, e);
                        }
                    }

                } else if (paramName.equals("orderDirection")) {
                    orderDirection = parseAsString(paramValue);
                    if (!"ASC".equals(orderDirection) && !"DESC".equals(orderDirection)) {
                        String msg = "orderDirection must be one of ASC or DESC";
                        LOG.info("USER ERROR: " + msg);
                        QueryParameterException e = new QueryParameterException();
                        e.setReason(msg);
                    }

                } else if (paramName.equals("eventCountLimit")) {
                    eventCountLimit = parseAsInteger(paramValue);

                } else if (paramName.equals("maxEventCount")) {
                    maxEventCount = parseAsInteger(paramValue).intValue();

                } else {
                    String msg = "Unknown query parameter: " + paramName;
                    LOG.info("USER ERROR: " + msg);
                    QueryParameterException e = new QueryParameterException();
                    e.setReason(msg);
                    throw new QueryParameterExceptionResponse(msg, e);
                }
            } catch (ClassCastException e) {
                String msg = "The type of the value for query parameter '" + paramName + "': " + paramValue
                        + " is invalid";
                LOG.info("USER ERROR: " + msg);
                LOG.debug(msg, e);
                QueryParameterException qpe = new QueryParameterException();
                qpe.setReason(msg);
                throw new QueryParameterExceptionResponse(msg, qpe, e);
            }
        }

        // some more user input checks
        if (maxEventCount > -1 && eventCountLimit > -1) {
            String msg = "Paramters 'maxEventCount' and 'eventCountLimit' are mutually exclusive";
            LOG.info("USER ERROR: " + msg);
            QueryParameterException e = new QueryParameterException();
            e.setReason(msg);
            throw new QueryParameterExceptionResponse(msg, e);
        }
        if (orderBy == null && eventCountLimit > -1) {
            String msg = "eventCountLimit may only be used when 'orderBy' is specified";
            LOG.info("USER ERROR: " + msg);
            QueryParameterException e = new QueryParameterException();
            e.setReason(msg);
            throw new QueryParameterExceptionResponse(msg, e);
        }
        if (orderBy != null) {
            aggrEventQuery.addOrdering(orderBy, orderDirection);
            objEventQuery.addOrdering(orderBy, orderDirection);
            quantEventQuery.addOrdering(orderBy, orderDirection);
            transEventQuery.addOrdering(orderBy, orderDirection);
        }
        if (eventCountLimit > -1) {
            aggrEventQuery.addLimit(eventCountLimit);
            objEventQuery.addLimit(eventCountLimit);
            quantEventQuery.addLimit(eventCountLimit);
            transEventQuery.addLimit(eventCountLimit);
        }
        if (maxEventCount > -1) {
            aggrEventQuery.setMaxEventCount(maxEventCount);
            objEventQuery.addLimit(maxEventCount + 1);
            quantEventQuery.addLimit(maxEventCount + 1);
            transEventQuery.addLimit(maxEventCount + 1);
        }

        List<SimpleEventQuery> eventQueries = new ArrayList<SimpleEventQuery>(4);
        if (includeAggrEvents) {
            eventQueries.add(aggrEventQuery);
        }
        if (includeObjEvents) {
            eventQueries.add(objEventQuery);
        }
        if (includeQuantEvents) {
            eventQueries.add(quantEventQuery);
        }
        if (includeTransEvents) {
            eventQueries.add(transEventQuery);
        }
        return eventQueries;
    }

    /**
     * @param paramName
     * @return
     */
    private String parseComparator(String paramName) {
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
        return op;
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
            return elem.getTextContent().trim();
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
            return Float.valueOf(elem.getTextContent().trim());
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
            // XML type is specified
            ts = TimeParser.convert(((XMLGregorianCalendar) queryParamValue).toGregorianCalendar());
        } else {
            // try to parse the value manually
            String date = null;
            if (queryParamValue instanceof Element) {
                // CXF returns an Element instance if no XML type
                // was specified in the request
                Element elem = (Element) queryParamValue;
                date = elem.getTextContent().trim();
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
            return Integer.valueOf(elem.getTextContent().trim());
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
                    String s = strings.item(i).getTextContent().trim();
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
     * Runs a MasterDataQuery for the given QueryParam array and returns the
     * QueryResults.
     * <p>
     * TODO: apply refactoring similar to
     * {@link constructSimpleEventQueries(QueryParams)}
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
    private QueryResults createMasterDataQuery(final QueryOperationsSession session, final QueryParams queryParams)
            throws SQLException, QueryParameterExceptionResponse, ImplementationExceptionResponse,
            QueryTooLargeExceptionResponse {

        // populate a sorted map with the given parameters
        SortedMap<String, Object> params = new TreeMap<String, Object>();
        for (QueryParam param : queryParams.getParam()) {
            params.put(param.getName(), param.getValue());
        }

        // check for parameter 'includeAttributes'
        boolean includeAttributes = false;
        try {
            Object val = params.remove("includeAttributes");
            includeAttributes = Boolean.parseBoolean(parseAsString(val));
            // defaults to 'false' if an invalid value is provided!
        } catch (NullPointerException e) {
            String msg = "Invalid MasterDataQuery: missing required parameter 'includeAttributes' or invalid value provided";
            LOG.info("USER ERROR: " + msg, e);
            QueryParameterException qpe = new QueryParameterException();
            qpe.setReason(msg);
            throw new QueryParameterExceptionResponse(msg, qpe, e);
        }

        // check for parameter 'includeChildren'
        boolean includeChildren = false;
        try {
            Object val = params.remove("includeChildren");
            includeChildren = Boolean.parseBoolean(parseAsString(val));
            // defaults to 'false' if an invalid value is provided!
        } catch (NullPointerException e) {
            String msg = "Invalid MasterDataQuery: missing required parameter 'includeChildren' or invalid value provided";
            LOG.info("USER ERROR: " + msg);
            QueryParameterException qpe = new QueryParameterException();
            qpe.setReason(msg);
            throw new QueryParameterExceptionResponse(msg, qpe, e);
        }

        // fetch vocabulary table names
        List<String> uris = new ArrayList<String>();
        if (params.containsKey("vocabularyName")) {
            Object val = params.remove("vocabularyName");
            uris = parseAsArrayOfString(val).getString();
        }
        Map<String, String> tableNames = backend.fetchVocabularyTableNames(session, uris);

        // filter vocabularies by name
        List<String> filterVocNames = new ArrayList<String>();
        if (params.containsKey("EQ_name")) {
            Object val = params.remove("EQ_name");
            filterVocNames = parseAsArrayOfString(val).getString();
        }

        // filter vocabularies by name with descendants
        List<String> filterVocNamesWd = new ArrayList<String>();
        if (params.containsKey("WD_name")) {
            Object val = params.remove("WD_name");
            filterVocNamesWd = parseAsArrayOfString(val).getString();
        }

        // filter vocabularies by attribute name
        List<String> filterVocAttrNames = new ArrayList<String>();
        if (params.containsKey("HASATTR")) {
            Object val = params.remove("HASATTR");
            filterVocAttrNames = parseAsArrayOfString(val).getString();
        }

        // filter vocabularies by attribute value
        Map<String, List<String>> filterAttrs = new HashMap<String, List<String>>();
        for (String param : params.keySet()) {
            if (param.startsWith("EQATTR_")) {
                String attrname = param.substring(7);
                Object val = params.remove(param);
                List<String> values = parseAsArrayOfString(val).getString();
                filterAttrs.put(attrname, values);
            }
        }

        // filter attributes by name
        List<String> filterAttrNames = new ArrayList<String>();
        if (params.containsKey("attributeNames") && includeAttributes) {
            Object val = params.remove("attributeNames");
            filterAttrNames = parseAsArrayOfString(val).getString();
        }

        // filter number of returned elements
        int maxElementCount = -1;
        if (params.containsKey("maxElementCount")) {
            Object val = params.remove("maxElementCount");
            maxElementCount = parseAsInteger(val).intValue();
        }

        List<VocabularyType> vocList = new ArrayList<VocabularyType>();
        // handle each vocabulary table
        for (String vocTableName : tableNames.keySet()) {
            List<VocabularyElementType> vocElemList = new ArrayList<VocabularyElementType>();

            // fetch all vocabularies filtered by the given arguments
            List<String> vocs = backend.fetchVocabularies(session, vocTableName, filterVocNames, filterVocNamesWd,
                    filterAttrs, filterVocAttrNames, maxElementCount);

            // handle each vocabulary element
            for (String voc : vocs) {
                VocabularyElementType vocElem = new VocabularyElementType();
                vocElem.setId(voc);

                List<AttributeType> attrList = vocElem.getAttribute();
                if (includeAttributes) {

                    // fetch all attributes for current vocabulary element
                    Map<String, String> attrMap = backend.fetchAttributes(session, vocTableName, voc.toString(),
                            filterAttrNames);

                    // handle each attribute element
                    for (String attrId : attrMap.keySet()) {
                        AttributeType attr = new AttributeType();
                        attr.setId(attrId);
                        String attrValue = attrMap.get(attrId);
                        attr.getContent().add(attrValue);
                        attrList.add(attr);
                    }
                }

                // fetch all children for current vocabulary element
                IDListType idList = backend.fetchChildren(session, vocTableName, voc.toString());
                vocElem.setChildren(idList);
                vocElemList.add(vocElem);
            }
            if (!vocElemList.isEmpty()) {
                VocabularyElementListType vocElems = new VocabularyElementListType();
                vocElems.getVocabularyElement().addAll(vocElemList);
                VocabularyType voc = new VocabularyType();
                voc.setVocabularyElementList(vocElems);
                voc.setType(tableNames.get(vocTableName));
                vocList.add(voc);
            }
        }
        QueryResultsBody resultsBody = null;
        if (!vocList.isEmpty()) {
            VocabularyListType vocListType = new VocabularyListType();
            vocListType.getVocabulary().addAll(vocList);
            resultsBody = new QueryResultsBody();
            resultsBody.setVocabularyList(vocListType);
        }

        QueryResults results = new QueryResults();
        results.setQueryName("SimpleMasterDataQuery");
        results.setResultsBody(resultsBody);
        return results;
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
     * Saves the map with the subscriptions to the message context.
     * 
     * @param subscriptions
     *            The map with the subscriptions.
     */
    private void saveSubscriptions(final Map<String, QuerySubscriptionScheduled> subscriptions) {
        servletContext.setAttribute("subscribedMap", subscriptions);
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
    private Map<String, QuerySubscriptionScheduled> loadSubscriptions(QueryOperationsSession session)
            throws ImplementationExceptionResponse, SQLException {
        Map<String, QuerySubscriptionScheduled> subscriptions = (HashMap<String, QuerySubscriptionScheduled>) servletContext.getAttribute("subscribedMap");
        if (subscriptions == null) {
            subscriptions = backend.fetchSubscriptions(session);
        }
        return subscriptions;
    }

    /**
     * Creates a new XMLGregorianCalendar from the given java.sql.Timestamp.
     * 
     * @param time
     *            The timestamp to convert.
     * @return The XML calendar object representing the given timestamp.
     * @throws ImplementationExceptionResponse
     *             If an error occurred when parsing the given timestamp into a
     *             calendar instance.
     */
    private XMLGregorianCalendar timestampToXmlCalendar(Timestamp time) throws ImplementationExceptionResponse {
        try {
            DatatypeFactory factory = DatatypeFactory.newInstance();
            Calendar cal = TimeParser.convert(time);
            return factory.newXMLGregorianCalendar((GregorianCalendar) cal);
        } catch (DatatypeConfigurationException e) {
            String msg = "Unable to instantiate an XML representation for a date/time datatype";
            ImplementationException iex = new ImplementationException();
            iex.setReason(msg);
            iex.setSeverity(ImplementationExceptionSeverity.SEVERE);
            throw new ImplementationExceptionResponse(msg, iex, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getQueryNames() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.debug("Invoking 'getQueryNames'");
        return QUERYNAMES;
    }

    /**
     * {@inheritDoc}
     */
    public String getStandardVersion() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.debug("Invoking 'getStandardVersion'");
        return STD_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getSubscriptionIDs(String queryName) throws NoSuchNameExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse {
        try {
            LOG.debug("Invoking 'getSubscriptionIDs'");
            QueryOperationsSession session = backend.openSession(dataSource);

            // FIXME: filter by queryName!
            Map<String, QuerySubscriptionScheduled> subscribedMap = loadSubscriptions(session);
            Set<String> temp = subscribedMap.keySet();
            List list = new ArrayList();
            list.addAll(temp);
            return list;
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
     * {@inheritDoc}
     */
    public String getVendorVersion() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.debug("Invoking 'getVendorVersion'");
        return VDR_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    public QueryResults poll(String queryName, QueryParams queryParams) throws NoSuchNameExceptionResponse,
            QueryParameterExceptionResponse, QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse {
        try {
            LOG.debug("Invoking 'poll'");
            QueryOperationsSession session = backend.openSession(dataSource);

            if (queryName.equals("SimpleEventQuery")) {
                EventListType eventList = new EventListType();
                List<SimpleEventQuery> eventQueries = constructSimpleEventQueries(queryParams);
                // run queries sequentially
                // TODO: might want to run them in parallel!
                for (SimpleEventQuery eventQuery : eventQueries) {
                    backend.runEventQuery(session, eventQuery,
                            eventList.getObjectEventOrAggregationEventOrQuantityEvent());
                }

                QueryResultsBody resultsBody = new QueryResultsBody();
                resultsBody.setEventList(eventList);
                QueryResults results = new QueryResults();
                results.setResultsBody(resultsBody);
                results.setQueryName(queryName);

                LOG.info("poll request for '" + queryName + "' succeeded");
                session.close();
                return results;
            } else if (queryName.equals("SimpleMasterDataQuery")) {
                QueryResults results = createMasterDataQuery(session, queryParams);
                LOG.info("poll request for '" + queryName + "' succeeded");
                session.close();
                return results;
            } else {
                session.close();
                String msg = "Unsupported query name '" + queryName + "' provided";
                LOG.info("USER ERROR: " + msg);
                NoSuchNameException e = new NoSuchNameException();
                e.setReason(msg);
                throw new NoSuchNameExceptionResponse(msg, e);
            }
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
     * {@inheritDoc}
     */
    public void subscribe(String queryName, QueryParams params, String dest, SubscriptionControls controls,
            String subscriptionID) throws NoSuchNameExceptionResponse, InvalidURIExceptionResponse,
            DuplicateSubscriptionExceptionResponse, QueryParameterExceptionResponse, QueryTooComplexExceptionResponse,
            SubscriptionControlsExceptionResponse, SubscribeNotPermittedExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse, ImplementationExceptionResponse {

        try {
            LOG.debug("Invoking 'subscribe'");
            QueryOperationsSession session = backend.openSession(dataSource);

            String triggerURI = controls.getTrigger();
            QuerySubscriptionScheduled newSubscription = null;
            Schedule schedule = null;
            GregorianCalendar initialRecordTime = controls.getInitialRecordTime().toGregorianCalendar();
            if (initialRecordTime == null) {
                initialRecordTime = new GregorianCalendar();
            }

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
                String msg = "Illegal query name '" + queryName + "'";
                LOG.info("USER ERROR: " + msg);
                NoSuchNameException e = new NoSuchNameException();
                e.setReason(msg);
                throw new NoSuchNameExceptionResponse(msg, e);
            }

            // SimpleMasterDataQuery only valid for polling
            if (queryName.equals("SimpleMasterDataQuery")) {
                String msg = "Subscription not allowed for SimpleMasterDataQuery";
                LOG.info("USER ERROR: " + msg);
                SubscribeNotPermittedException e = new SubscribeNotPermittedException();
                e.setReason(msg);
                throw new SubscribeNotPermittedExceptionResponse(msg, e);
            }

            // subscriptionID mustn't be empty.
            if (subscriptionID == null || subscriptionID.equals("")) {
                String msg = "SubscriptionID is empty. Choose a valid subscriptionID";
                LOG.info(msg);
                ValidationException e = new ValidationException();
                e.setReason(msg);
                throw new ValidationExceptionResponse(msg, e);
            }

            // subscriptionID mustn't exist yet.
            if (backend.fetchExistsSubscriptionId(session, subscriptionID)) {
                String msg = "SubscriptionID '" + subscriptionID
                        + "' already exists. Choose a different subscriptionID";
                LOG.info("USER ERROR: " + msg);
                DuplicateSubscriptionException e = new DuplicateSubscriptionException();
                e.setReason(msg);
                throw new DuplicateSubscriptionExceptionResponse(msg, e);
            }

            // trigger and schedule may no be used together, but one of them
            // must be set
            if (controls.getSchedule() != null && controls.getTrigger() != null) {
                String msg = "Schedule and trigger mustn't be used together";
                LOG.info("USER ERROR: " + msg);
                SubscriptionControlsException e = new SubscriptionControlsException();
                e.setReason(msg);
                throw new SubscriptionControlsExceptionResponse(msg, e);
            }
            if (controls.getSchedule() == null && controls.getTrigger() == null) {
                String msg = "Either schedule or trigger has to be set";
                LOG.info("USER ERROR: " + msg);
                SubscriptionControlsException e = new SubscriptionControlsException();
                e.setReason(msg);
                throw new SubscriptionControlsExceptionResponse(msg, e);
            }
            if (controls.getSchedule() != null) {
                // Scheduled Query -> parse schedule
                schedule = new Schedule(controls.getSchedule());
                newSubscription = new QuerySubscriptionScheduled(subscriptionID, params, dest,
                        controls.isReportIfEmpty(), initialRecordTime, initialRecordTime, schedule, queryName);
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
                QuerySubscriptionTriggered trigger = new QuerySubscriptionTriggered(subscriptionID, params, dest,
                        controls.isReportIfEmpty(), initialRecordTime, initialRecordTime, queryName, triggerURI,
                        schedule);
                newSubscription = trigger;
            }

            // load subscriptions
            Map<String, QuerySubscriptionScheduled> subscribedMap = loadSubscriptions(session);

            // store the Query to the database, the local hash map, and the
            // application context
            backend.storeSupscriptions(session, params, dest, subscriptionID, controls, triggerURI, newSubscription,
                    queryName, schedule);
            subscribedMap.put(subscriptionID, newSubscription);
            saveSubscriptions(subscribedMap);
            session.close();
        } catch (SQLException e) {
            String msg = "SQL error during query execution: " + e.getMessage();
            LOG.error(msg, e);
            ImplementationException iex = new ImplementationException();
            iex.setReason(msg);
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw new ImplementationExceptionResponse(msg, iex, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unsubscribe(String subscriptionID) throws NoSuchSubscriptionExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse {
        LOG.debug("Invoking 'unsubscribe'");
        try {
            QueryOperationsSession session = backend.openSession(dataSource);
            Map<String, QuerySubscriptionScheduled> subscribedMap = loadSubscriptions(session);
            if (subscribedMap.containsKey(subscriptionID)) {
                // remove subscription from local hash map
                QuerySubscriptionScheduled toDelete = subscribedMap.get(subscriptionID);
                toDelete.stopSubscription();
                subscribedMap.remove(subscriptionID);
                saveSubscriptions(subscribedMap);

                // delete subscription from database
                backend.deleteSubscription(session, subscriptionID);
            } else {
                String msg = "There is no subscription with ID '" + subscriptionID + "'";
                LOG.info("USER ERROR: " + msg);
                NoSuchSubscriptionException e = new NoSuchSubscriptionException();
                e.setReason(msg);
                throw new NoSuchSubscriptionExceptionResponse(msg, e);
            }
            session.close();
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
     * A Transformer which expects a String instance, appends a "*" to the end
     * of the String, and returns the new String.
     * 
     * @author Marco Steybe
     */
    private static class StringTransformer implements Transformer {
        public Object transform(Object o) {
            if (o instanceof String) {
                o = ((String) o).concat("*");
            }
            return o;
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

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
