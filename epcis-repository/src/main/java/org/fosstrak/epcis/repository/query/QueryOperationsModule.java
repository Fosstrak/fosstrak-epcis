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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.fosstrak.epcis.model.ArrayOfString;
import org.fosstrak.epcis.model.DuplicateSubscriptionException;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.EventListType;
import org.fosstrak.epcis.model.ImplementationException;
import org.fosstrak.epcis.model.ImplementationExceptionSeverity;
import org.fosstrak.epcis.model.InvalidURIException;
import org.fosstrak.epcis.model.NoSuchNameException;
import org.fosstrak.epcis.model.NoSuchSubscriptionException;
import org.fosstrak.epcis.model.QueryParam;
import org.fosstrak.epcis.model.QueryParameterException;
import org.fosstrak.epcis.model.QueryParams;
import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.model.QueryResultsBody;
import org.fosstrak.epcis.model.QuerySchedule;
import org.fosstrak.epcis.model.QueryTooLargeException;
import org.fosstrak.epcis.model.SubscribeNotPermittedException;
import org.fosstrak.epcis.model.SubscriptionControls;
import org.fosstrak.epcis.model.SubscriptionControlsException;
import org.fosstrak.epcis.model.ValidationException;
import org.fosstrak.epcis.model.VocabularyListType;
import org.fosstrak.epcis.repository.EpcisConstants;
import org.fosstrak.epcis.repository.EpcisQueryControlInterface;
import org.fosstrak.epcis.repository.query.SimpleEventQueryDTO.EventQueryParam;
import org.fosstrak.epcis.repository.query.SimpleEventQueryDTO.Operation;
import org.fosstrak.epcis.repository.query.SimpleEventQueryDTO.OrderDirection;
import org.fosstrak.epcis.soap.DuplicateSubscriptionExceptionResponse;
import org.fosstrak.epcis.soap.ImplementationExceptionResponse;
import org.fosstrak.epcis.soap.InvalidURIExceptionResponse;
import org.fosstrak.epcis.soap.NoSuchNameExceptionResponse;
import org.fosstrak.epcis.soap.NoSuchSubscriptionExceptionResponse;
import org.fosstrak.epcis.soap.QueryParameterExceptionResponse;
import org.fosstrak.epcis.soap.QueryTooComplexExceptionResponse;
import org.fosstrak.epcis.soap.QueryTooLargeExceptionResponse;
import org.fosstrak.epcis.soap.SecurityExceptionResponse;
import org.fosstrak.epcis.soap.SubscribeNotPermittedExceptionResponse;
import org.fosstrak.epcis.soap.SubscriptionControlsExceptionResponse;
import org.fosstrak.epcis.soap.ValidationExceptionResponse;
import org.fosstrak.epcis.utils.TimeParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
     * The names of all the implemented queries.
     */
    private static final List<String> QUERYNAMES;
    static {
        QUERYNAMES = new ArrayList<String>(2);
        QUERYNAMES.add("SimpleEventQuery");
        QUERYNAMES.add("SimpleMasterDataQuery");
    }

    /**
     * The version of this service implementation. The empty string indicates
     * that the implementation implements only standard functionality with no
     * vendor extensions.
     */
    private String serviceVersion = "";

    /**
     * The maximum number of rows a query can return.
     */
    private int maxQueryRows;

    /**
     * The maximum timeout to wait for a query to return.
     */
    private int maxQueryTime;

    // time to wait for checking trigger conditions
    private String triggerConditionSeconds;
    private String triggerConditionMinutes;

    private ServletContext servletContext;
    private DataSource dataSource;
    private QueryOperationsBackend backend;

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
    private List<SimpleEventQueryDTO> constructSimpleEventQueries(final QueryParams queryParams) throws SQLException,
            QueryParameterExceptionResponse {
        SimpleEventQueryDTO aggrEventQuery = new SimpleEventQueryDTO(EpcisConstants.AGGREGATION_EVENT);
        SimpleEventQueryDTO objEventQuery = new SimpleEventQueryDTO(EpcisConstants.OBJECT_EVENT);
        SimpleEventQueryDTO quantEventQuery = new SimpleEventQueryDTO(EpcisConstants.QUANTITY_EVENT);
        SimpleEventQueryDTO transEventQuery = new SimpleEventQueryDTO(EpcisConstants.TRANSACTION_EVENT);

        boolean includeAggrEvents = true;
        boolean includeObjEvents = true;
        boolean includeQuantEvents = true;
        boolean includeTransEvents = true;

        String orderBy = null;
        OrderDirection orderDirection = null;
        int eventCountLimit = -1;
        int maxEventCount = -1;

        // a sorted List of query parameter names - keeps track of the processed
        // names in order to cope with duplicates
        List<String> sortedParamNames = new ArrayList<String>();

        int nofEventFieldExtensions = 0;
        for (QueryParam param : queryParams.getParam()) {
            String paramName = param.getName();
            Object paramValue = param.getValue();

            // check for null values
            if (paramName == null || "".equals(paramName)) {
                String msg = "Missing name for a query parameter";
                throw queryParameterException(msg, null);
            }
            if (paramValue == null) {
                String msg = "Missing value for query parameter '" + paramName + "'";
                throw queryParameterException(msg, null);
            }
            // check if the current query parameter has already been provided
            int index = Collections.binarySearch(sortedParamNames, paramName);
            if (index < 0) {
                // we have not yet seen this query parameter name - ok
                sortedParamNames.add(-index - 1, paramName);
            } else {
                // we have already handled this query parameter name - not ok
                String msg = "Query parameter '" + paramName + "' provided more than once";
                throw queryParameterException(msg, null);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Handling query parameter: " + paramName);
            }
            try {
                if (paramName.equals("eventType")) {
                    // by default all event types will be included
                    List<String> eventTypes = parseAsArrayOfString(paramValue).getString();
                    if (!eventTypes.isEmpty()) {
                        // check if valid event types are provided
                        checkEventTypes(eventTypes);

                        // check for excluded event types
                        if (!eventTypes.contains(EpcisConstants.AGGREGATION_EVENT)) {
                            includeAggrEvents = false;
                        }
                        if (!eventTypes.contains(EpcisConstants.OBJECT_EVENT)) {
                            includeObjEvents = false;
                        }
                        if (!eventTypes.contains(EpcisConstants.QUANTITY_EVENT)) {
                            includeQuantEvents = false;
                        }
                        if (!eventTypes.contains(EpcisConstants.TRANSACTION_EVENT)) {
                            includeTransEvents = false;
                        }
                    }
                } else if (paramName.equals("GE_eventTime") || paramName.equals("LT_eventTime")
                        || paramName.equals("GE_recordTime") || paramName.equals("LT_recordTime")) {
                	Calendar cal = parseAsCalendar(paramValue, paramName);
                    Operation op = Operation.valueOf(paramName.substring(0, 2));
                    String eventField = paramName.substring(3, paramName.length()) + "Ms";
                    aggrEventQuery.addEventQueryParam(eventField, op, cal.getTimeInMillis());
                    objEventQuery.addEventQueryParam(eventField, op, cal.getTimeInMillis());
                    quantEventQuery.addEventQueryParam(eventField, op, cal.getTimeInMillis());
                    transEventQuery.addEventQueryParam(eventField, op, cal.getTimeInMillis());

                } else if (paramName.equals("EQ_action")) {
                    // QuantityEvents have no "action" field, thus exclude them
                    includeQuantEvents = false;
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        checkActionValues(aos.getString());
                        aggrEventQuery.addEventQueryParam("action", Operation.EQ, aos.getString());
                        objEventQuery.addEventQueryParam("action", Operation.EQ, aos.getString());
                        transEventQuery.addEventQueryParam("action", Operation.EQ, aos.getString());
                    }

                } else if (paramName.equals("EQ_bizStep") || paramName.equals("EQ_disposition")
                        || paramName.equals("EQ_readPoint") || paramName.equals("EQ_bizLocation")) {
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        String eventField = paramName.substring(3, paramName.length());
                        aggrEventQuery.addEventQueryParam(eventField, Operation.EQ, aos.getString());
                        objEventQuery.addEventQueryParam(eventField, Operation.EQ, aos.getString());
                        quantEventQuery.addEventQueryParam(eventField, Operation.EQ, aos.getString());
                        transEventQuery.addEventQueryParam(eventField, Operation.EQ, aos.getString());
                    }

                } else if (paramName.equals("WD_readPoint") || paramName.equals("WD_bizLocation")) {
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        // append a "*" to each of the parameter values - this
                        // should implement the semantics of "With Descendant"
                        // TODO: should???
                        CollectionUtils.transform(aos.getString(), new StringTransformer());
                        String eventField = paramName.substring(3, paramName.length());
                        aggrEventQuery.addEventQueryParam(eventField, Operation.WD, aos.getString());
                        objEventQuery.addEventQueryParam(eventField, Operation.WD, aos.getString());
                        quantEventQuery.addEventQueryParam(eventField, Operation.WD, aos.getString());
                        transEventQuery.addEventQueryParam(eventField, Operation.WD, aos.getString());
                    }

                } else if (paramName.startsWith("EQ_bizTransaction_")) {
                    // type extracted from parameter name
                    String bizTransType = paramName.substring(18);
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        aggrEventQuery.addEventQueryParam("bizTransList.type", Operation.EQ, bizTransType);
                        objEventQuery.addEventQueryParam("bizTransList.type", Operation.EQ, bizTransType);
                        quantEventQuery.addEventQueryParam("bizTransList.type", Operation.EQ, bizTransType);
                        transEventQuery.addEventQueryParam("bizTransList.type", Operation.EQ, bizTransType);
                        aggrEventQuery.addEventQueryParam("bizTransList.bizTrans", Operation.EQ, aos.getString());
                        objEventQuery.addEventQueryParam("bizTransList.bizTrans", Operation.EQ, aos.getString());
                        quantEventQuery.addEventQueryParam("bizTransList.bizTrans", Operation.EQ, aos.getString());
                        transEventQuery.addEventQueryParam("bizTransList.bizTrans", Operation.EQ, aos.getString());
                    }

                } else if (paramName.equals("MATCH_epc") || paramName.equals("MATCH_anyEPC")) {
                    // QuantityEvents have no field for EPCs, thus exclude them
                    includeQuantEvents = false;
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        aggrEventQuery.addEventQueryParam("childEPCs", Operation.MATCH, aos.getString());
                        objEventQuery.addEventQueryParam("epcList", Operation.MATCH, aos.getString());
                        transEventQuery.addEventQueryParam("epcList", Operation.MATCH, aos.getString());
                        if (paramName.equals("MATCH_anyEPC")) {
                            // AggregationEvent and TransactionEvent need
                            // special treatment ("parentID" field)
                            aggrEventQuery.setIsAnyEpc(true);
                            transEventQuery.setIsAnyEpc(true);
                        }
                    }

                } else if (paramName.equals("MATCH_parentID")) {
                    includeQuantEvents = false;
                    includeObjEvents = false;
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        aggrEventQuery.addEventQueryParam("parentID", Operation.MATCH, aos.getString());
                        transEventQuery.addEventQueryParam("parentID", Operation.MATCH, aos.getString());
                    }

                } else if (paramName.equals("MATCH_epcClass")) {
                    includeAggrEvents = false;
                    includeObjEvents = false;
                    includeTransEvents = false;
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    if (!aos.getString().isEmpty()) {
                        quantEventQuery.addEventQueryParam("epcClass", Operation.MATCH, aos.getString());
                    }

                } else if (paramName.endsWith("_quantity")) {
                    includeAggrEvents = false;
                    includeObjEvents = false;
                    includeTransEvents = false;
                    Operation op = Operation.valueOf(paramName.substring(0, paramName.indexOf('_')));
                    quantEventQuery.addEventQueryParam("quantity", op, parseAsInteger(paramValue));

                } else if (paramName.startsWith("GT_") || paramName.startsWith("GE_") || paramName.startsWith("EQ_")
                        || paramName.startsWith("LE_") || paramName.startsWith("LT_")) {
                    // must be an event field extension
                    String fieldname = paramName.substring(3);
                    String[] parts = fieldname.split("#");
                    if (parts.length != 2) {
                        String msg = "Invalid parameter " + paramName;
                        throw queryParameterException(msg, null);
                    }
                    nofEventFieldExtensions++;
                    String eventFieldExtBase = "extension" + nofEventFieldExtensions;
                    EventQueryParam queryParam = parseExtensionField(eventFieldExtBase, paramName, paramValue);
                    aggrEventQuery.addEventQueryParam(queryParam);
                    objEventQuery.addEventQueryParam(queryParam);
                    quantEventQuery.addEventQueryParam(queryParam);
                    transEventQuery.addEventQueryParam(queryParam);
                    String eventFieldExt = eventFieldExtBase + ".fieldname";
                    aggrEventQuery.addEventQueryParam(eventFieldExt, Operation.EQ, fieldname);
                    objEventQuery.addEventQueryParam(eventFieldExt, Operation.EQ, fieldname);
                    quantEventQuery.addEventQueryParam(eventFieldExt, Operation.EQ, fieldname);
                    transEventQuery.addEventQueryParam(eventFieldExt, Operation.EQ, fieldname);

                } else if (paramName.startsWith("EXISTS_")) {
                    String fieldname = paramName.substring(7);
                    if (fieldname.equals("childEPCs")) {
                        includeObjEvents = false;
                        includeQuantEvents = false;
                        includeTransEvents = false;
                        aggrEventQuery.addEventQueryParam("childEPCs", Operation.EXISTS, null);
                    } else if (fieldname.equals("epcList")) {
                        includeAggrEvents = false;
                        includeQuantEvents = false;
                        objEventQuery.addEventQueryParam("epcList", Operation.EXISTS, null);
                        transEventQuery.addEventQueryParam("epcList", Operation.EXISTS, null);
                    } else if (fieldname.equals("action")) {
                        includeQuantEvents = false;
                        aggrEventQuery.addEventQueryParam("action", Operation.EXISTS, null);
                        objEventQuery.addEventQueryParam("action", Operation.EXISTS, null);
                        transEventQuery.addEventQueryParam("action", Operation.EXISTS, null);
                    } else if (fieldname.equals("parentID")) {
                        includeObjEvents = false;
                        includeQuantEvents = false;
                        aggrEventQuery.addEventQueryParam("parentID", Operation.EXISTS, null);
                        transEventQuery.addEventQueryParam("parentID", Operation.EXISTS, null);
                    } else if (fieldname.equals("quantity") || fieldname.equals("epcClass")) {
                        includeAggrEvents = false;
                        includeObjEvents = false;
                        includeTransEvents = false;
                        quantEventQuery.addEventQueryParam(fieldname, Operation.EXISTS, null);
                    } else if (fieldname.equals("eventTime") || fieldname.equals("recordTime")
                            || fieldname.equals("eventTimeZoneOffset") || fieldname.equals("bizStep")
                            || fieldname.equals("disposition") || fieldname.equals("readPoint")
                            || fieldname.equals("bizLocation") || fieldname.equals("bizTransList")) {
                        aggrEventQuery.addEventQueryParam(fieldname, Operation.EXISTS, null);
                        objEventQuery.addEventQueryParam(fieldname, Operation.EXISTS, null);
                        quantEventQuery.addEventQueryParam(fieldname, Operation.EXISTS, null);
                        transEventQuery.addEventQueryParam(fieldname, Operation.EXISTS, null);
                    } else {
                        // lets see if we have an extension fieldname
                        String[] parts = fieldname.split("#");
                        if (parts.length != 2) {
                            String msg = "Invalid parameter " + paramName;
                            throw queryParameterException(msg, null);
                        }
                        nofEventFieldExtensions++;
                        String eventFieldExt = "extension" + nofEventFieldExtensions + ".fieldname";
                        aggrEventQuery.addEventQueryParam(eventFieldExt, Operation.EQ, fieldname);
                        objEventQuery.addEventQueryParam(eventFieldExt, Operation.EQ, fieldname);
                        quantEventQuery.addEventQueryParam(eventFieldExt, Operation.EQ, fieldname);
                        transEventQuery.addEventQueryParam(eventFieldExt, Operation.EQ, fieldname);
                    }

                } else if (paramName.startsWith("HASATTR_")) {
                    // restrict by attribute name
                    String fieldname = paramName.substring(8);
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    String eventField = fieldname + ".attribute";
                    aggrEventQuery.addEventQueryParam(eventField, Operation.EQ, aos.getString());
                    objEventQuery.addEventQueryParam(eventField, Operation.EQ, aos.getString());
                    quantEventQuery.addEventQueryParam(eventField, Operation.EQ, aos.getString());
                    transEventQuery.addEventQueryParam(eventField, Operation.EQ, aos.getString());

                } else if (paramName.startsWith("EQATTR_")) {
                    String fieldname = paramName.substring(7);
                    String attrname = null;
                    String[] parts = fieldname.split("_");
                    if (parts.length > 2) {
                        String msg = "Query parameter has invalid format: " + paramName
                                + ". Expected: EQATTR_fieldname_attrname";
                        throw queryParameterException(msg, null);
                    } else if (parts.length == 2) {
                        fieldname = parts[0];
                        attrname = parts[1];
                    }
                    // restrict by attribute name
                    String eventField = fieldname + ".attribute";
                    aggrEventQuery.addEventQueryParam(eventField, Operation.EQ, attrname);
                    objEventQuery.addEventQueryParam(eventField, Operation.EQ, attrname);
                    quantEventQuery.addEventQueryParam(eventField, Operation.EQ, attrname);
                    transEventQuery.addEventQueryParam(eventField, Operation.EQ, attrname);
                    // restrict by attribute value
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    eventField = eventField + ".value";
                    aggrEventQuery.addEventQueryParam(eventField, Operation.EQ, aos.getString());
                    objEventQuery.addEventQueryParam(eventField, Operation.EQ, aos.getString());
                    quantEventQuery.addEventQueryParam(eventField, Operation.EQ, aos.getString());
                    transEventQuery.addEventQueryParam(eventField, Operation.EQ, aos.getString());

                } else if (paramName.equals("orderBy")) {
                    orderBy = parseAsString(paramValue);
                    if (!"eventTime".equals(orderBy) && !"recordTime".equals(orderBy) && !"quantity".equals(orderBy)) {
                        String[] parts = orderBy.split("#");
                        if (parts.length != 2) {
                            String msg = "orderBy must be one of eventTime, recordTime, quantity, or an extension field";
                            throw queryParameterException(msg, null);
                        }
                    }

                } else if (paramName.equals("orderDirection")) {
                    orderDirection = OrderDirection.valueOf(parseAsString(paramValue));

                } else if (paramName.equals("eventCountLimit")) {
                    eventCountLimit = parseAsInteger(paramValue).intValue();

                } else if (paramName.equals("maxEventCount")) {
                    maxEventCount = parseAsInteger(paramValue).intValue();

                } else {
                    String msg = "Unknown query parameter: " + paramName;
                    throw queryParameterException(msg, null);
                }
            } catch (ClassCastException e) {
                String msg = "Type of value invalid for query parameter '" + paramName + "': " + paramValue;
                throw queryParameterException(msg, e);
            } catch (IllegalArgumentException e) {
                String msg = "Unparseable value for query parameter '" + paramName + "'. " + e.getMessage();
                throw queryParameterException(msg, e);
            }
        }

        // some more user input checks
        if (maxEventCount > -1 && eventCountLimit > -1) {
            String msg = "Paramters 'maxEventCount' and 'eventCountLimit' are mutually exclusive";
            throw queryParameterException(msg, null);
        }
        if (orderBy == null && eventCountLimit > -1) {
            String msg = "'eventCountLimit' may only be used when 'orderBy' is specified";
            throw queryParameterException(msg, null);
        }
        if (orderBy == null && orderDirection != null) {
            String msg = "'orderDirection' may only be used when 'orderBy' is specified";
            throw queryParameterException(msg, null);
        }
        if (orderBy != null) {
            aggrEventQuery.setOrderBy(orderBy);
            objEventQuery.setOrderBy(orderBy);
            quantEventQuery.setOrderBy(orderBy);
            transEventQuery.setOrderBy(orderBy);
            if (orderDirection != null) {
                aggrEventQuery.setOrderDirection(orderDirection);
                objEventQuery.setOrderDirection(orderDirection);
                quantEventQuery.setOrderDirection(orderDirection);
                transEventQuery.setOrderDirection(orderDirection);
            }
        }
        if (eventCountLimit > -1) {
            aggrEventQuery.setLimit(eventCountLimit);
            objEventQuery.setLimit(eventCountLimit);
            quantEventQuery.setLimit(eventCountLimit);
            transEventQuery.setLimit(eventCountLimit);
        }
        if (maxEventCount > -1) {
            aggrEventQuery.setMaxEventCount(maxEventCount);
            objEventQuery.setMaxEventCount(maxEventCount);
            quantEventQuery.setMaxEventCount(maxEventCount);
            transEventQuery.setMaxEventCount(maxEventCount);
        }

        List<SimpleEventQueryDTO> eventQueries = new ArrayList<SimpleEventQueryDTO>(4);
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
     * Parses the given parameter value as an extension field. The type of the
     * extension field value which can be integer, float, date, or list of
     * string.
     */
    private EventQueryParam parseExtensionField(String eventFieldExtBase, String paramName, Object paramValue) {
        Operation op = Operation.valueOf(paramName.substring(0, 2));
        String eventField;
        Object value;

        // 1. try to parse the value as int
        try {
            value = parseAsInteger(paramValue);
            eventField = eventFieldExtBase + ".intValue";
            return new EventQueryParam(eventField, op, value);
        } catch (NumberFormatException e) {}

        // 2. try to parse the value as float
        try {
            value = parseAsFloat(paramValue);
            eventField = eventFieldExtBase + ".floatValue";
            return new EventQueryParam(eventField, op, value);
        } catch (NumberFormatException e) {}

        // 3. try to parse the value as date
        try {
            value = parseAsCalendar(paramValue, paramName);
            eventField = eventFieldExtBase + ".dateValue";
            return new EventQueryParam(eventField, op, value);
        } catch (QueryParameterExceptionResponse e) {}

        // 4. try to parse the value as array of string
        try {
            ArrayOfString aos = parseAsArrayOfString(paramValue);
            if (!aos.getString().isEmpty()) {
                value = aos.getString();
                eventField = eventFieldExtBase + ".strValue";
                return new EventQueryParam(eventField, op, value);
            }
        } catch (Throwable t) {}

        // last effort: parse the value as string
        value = parseAsString(paramValue);
        eventField = eventFieldExtBase + ".strValue";
        return new EventQueryParam(eventField, op, value);
    }

    /**
     * Checks if the given List contains valid event type strings, i.e.,
     * AggregationEvent, ObjectEvent, QuantityEvent, or TransactionEvent
     * 
     * @param eventTypes
     *            The List of Strings to check.
     * @throws QueryParameterExceptionResponse
     *             If one of the values in the given List is not one of the
     *             valid event types.
     */
    private void checkEventTypes(List<String> eventTypes) throws QueryParameterExceptionResponse {
        for (String eventType : eventTypes) {
            if (!EpcisConstants.EVENT_TYPES.contains(eventType)) {
                String msg = "Unsupported eventType: " + eventType;
                throw queryParameterException(msg, null);
            }
        }
    }

    /**
     * Parses the given query parameter value as String.
     * 
     * @param queryParamValue
     *            The query parameter value to be parsed as String.
     * @return The Float holding the value of the query parameter.
     */
    private String parseAsString(Object queryParamValue) throws ClassCastException {
        if (queryParamValue instanceof String) {
            return (String) queryParamValue;
        } else if (queryParamValue instanceof Element) {
            Element elem = (Element) queryParamValue;
            return elem.getTextContent().trim();
        } else {
            return queryParamValue.toString();
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
    private Float parseAsFloat(Object queryParamValue) throws NumberFormatException {
        if (queryParamValue instanceof Float) {
            return (Float) queryParamValue;
        } else if (queryParamValue instanceof Element) {
            Element elem = (Element) queryParamValue;
            return Float.valueOf(elem.getTextContent().trim());
        } else {
            return Float.valueOf(queryParamValue.toString());
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
    private Calendar parseAsCalendar(Object queryParamValue, String queryParamName)
            throws QueryParameterExceptionResponse {
        Calendar cal;
        if (queryParamValue instanceof Calendar) {
            // Axis returns a Calendar instance
            cal = (Calendar) queryParamValue;
        } else if (queryParamValue instanceof XMLGregorianCalendar) {
            // CXF returns an XMLGregorianCalendar instance if the
            // XML type is specified
            cal = ((XMLGregorianCalendar) queryParamValue).toGregorianCalendar();
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
                cal = TimeParser.parseAsCalendar(date);
            } catch (ParseException e) {
                String msg = "Unable to parse the value for query parameter '" + queryParamName + "' as date/time";
                throw queryParameterException(msg, e);
            }
        }
        return cal;
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
    private Integer parseAsInteger(Object queryParamValue) throws NumberFormatException {
        if (queryParamValue instanceof Integer) {
            return (Integer) queryParamValue;
        } else if (queryParamValue instanceof Element) {
            Element elem = (Element) queryParamValue;
            return Integer.valueOf(elem.getTextContent().trim());
        } else {
            return Integer.valueOf(queryParamValue.toString());
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
     * @throws QueryParameterExceptionResponse
     *             If the given value does not correspond to valid ArrayOfString
     *             syntax, i.e., a list of matching
     *             <code>&lt;string&gt;</code> <code>&lt;/string&gt;</code>
     *             tags are expected.
     */
    private ArrayOfString parseAsArrayOfString(Object queryParamValue) throws ClassCastException,
            QueryParameterExceptionResponse {
        try {
            return (ArrayOfString) queryParamValue;
        } catch (ClassCastException e) {
            // trying to parse manually
            Element elem = (Element) queryParamValue;
            NodeList strings = elem.getChildNodes();
            ArrayOfString aos = new ArrayOfString();
            for (int i = 0; i < strings.getLength(); i++) {
                if (strings.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    if ("string".equalsIgnoreCase(strings.item(i).getNodeName())) {
                        String s = strings.item(i).getTextContent().trim();
                        aos.getString().add(s);
                    } else {
                        String msg = "Invalid ArrayOfString syntax: matching <string> </string> tags expected";
                        throw queryParameterException(msg, null);
                    }
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
    private MasterDataQueryDTO constructMasterDataQuery(final QueryParams queryParams)
            throws QueryParameterExceptionResponse {
        MasterDataQueryDTO mdQuery = new MasterDataQueryDTO();

        // a sorted List of query parameter names - keeps track of the processed
        // names in order to cope with duplicates
        List<String> sortedParamNames = new ArrayList<String>();

        Boolean includeAttributes = null;
        Boolean includeChildren = null;
        List<String> vocabularyTypes = null;
        List<String> includedAttributeNames = null;

        for (QueryParam param : queryParams.getParam()) {
            String paramName = param.getName();
            Object paramValue = param.getValue();

            // check for null value
            if (paramName == null || "".equals(paramName)) {
                String msg = "Missing name for a query parameter";
                throw queryParameterException(msg, null);
            }
            if (paramValue == null) {
                String msg = "Missing value for query parameter '" + paramName + "'";
                throw queryParameterException(msg, null);
            }
            // check if the current query parameter has already been provided
            int index = Collections.binarySearch(sortedParamNames, paramName);
            if (index < 0) {
                // we have not yet seen this query parameter name - ok
                sortedParamNames.add(-index - 1, paramName);
            } else {
                // we have already handled this query parameter name - not ok
                String msg = "Query parameter '" + paramName + "' provided more than once";
                throw queryParameterException(msg, null);
            }

            try {
                if (paramName.equals("includeAttributes")) {
                    // defaults to 'false' if an invalid value is provided!
                    includeAttributes = Boolean.valueOf(parseAsString(paramValue));
                    mdQuery.setIncludeAttributes(includeAttributes.booleanValue());

                } else if (paramName.equals("includeChildren")) {
                    // defaults to 'false' if an invalid value is provided!
                    includeChildren = Boolean.valueOf(parseAsString(paramValue));
                    mdQuery.setIncludeChildren(includeChildren.booleanValue());

                } else if (paramName.equals("maxElementCount")) {
                    int maxElementCount = parseAsInteger(paramValue).intValue();
                    mdQuery.setMaxElementCount(maxElementCount);

                } else if (paramName.equals("vocabularyName")) {
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    vocabularyTypes = aos.getString();

                } else if (paramName.equals("attributeNames")) {
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    includedAttributeNames = aos.getString();

                } else if (paramName.equals("EQ_name")) {
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    mdQuery.setVocabularyEqNames(aos.getString());

                } else if (paramName.equals("WD_name")) {
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    mdQuery.setVocabularyWdNames(aos.getString());

                } else if (paramName.equals("HASATTR")) {
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    mdQuery.setAttributeNames(aos.getString());

                } else if (paramName.startsWith("EQATTR_")) {
                    String attrName = paramName.substring(7);
                    ArrayOfString aos = parseAsArrayOfString(paramValue);
                    mdQuery.addAttributeNameAndValues(attrName, aos.getString());

                }
            } catch (ClassCastException e) {
                String msg = "The type of the value for query parameter '" + paramName + "': " + paramValue
                        + " is invalid";
                throw queryParameterException(msg, e);
            }
        }

        // check for missing parameters
        if (includeAttributes == null || includeChildren == null) {
            String missing = (includeAttributes == null) ? " includeAttributes" : "";
            missing += (includeChildren == null) ? " includeChildren" : "";
            String msg = "Missing required masterdata query parameter(s):" + missing;
            throw queryParameterException(msg, null);
        }
        if (includeAttributes.booleanValue() && includedAttributeNames != null) {
            mdQuery.setIncludedAttributeNames(includedAttributeNames);
        }

        if (vocabularyTypes == null) {
            // include all vocabularies
            vocabularyTypes = EpcisConstants.VOCABULARY_TYPES;
        }
        mdQuery.setVocabularyTypes(vocabularyTypes);

        return mdQuery;
    }

    /**
     * Writes the given message and exception to the application's log file,
     * creates a QueryParameterException from the given message, and returns a
     * new QueryParameterExceptionResponse. Use this method to conveniently
     * return a user error message back to the requesting service caller, e.g.:
     * 
     * <pre>
     * String msg = &quot;unable to parse query parameter&quot;
     * throw new queryParameterException(msg, null);
     * </pre>
     * 
     * @param msg
     *            A user error message.
     * @param e
     *            An internal exception - this exception will not be delivered
     *            back to the service caller as it contains application specific
     *            information. It will be used to print some details about the
     *            user error to the log file (useful for debugging).
     * @return A new QueryParameterExceptionResponse containing the given user
     *         error message.
     */
    private QueryParameterExceptionResponse queryParameterException(String msg, Exception e) {
        LOG.info("QueryParameterException: " + msg);
        if (LOG.isTraceEnabled() && e != null) {
            LOG.trace("Exception details: " + e.getMessage(), e);
        }
        QueryParameterException qpe = new QueryParameterException();
        qpe.setReason(msg);
        return new QueryParameterExceptionResponse(msg, qpe);
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
                        + " - must be one of ADD, OBSERVE, or DELETE";
                throw queryParameterException(msg, null);
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
    @SuppressWarnings("unchecked")
    private Map<String, QuerySubscriptionScheduled> loadSubscriptions(QueryOperationsSession session)
            throws ImplementationExceptionResponse, SQLException {
        LOG.debug("Retrieving subscriptions from application context");
        Object subscribedMap = servletContext.getAttribute("subscribedMap");
        Map<String, QuerySubscriptionScheduled> subscriptions = (HashMap<String, QuerySubscriptionScheduled>) subscribedMap;
        if (subscriptions == null) {
            LOG.debug("Subscriptions not found - retrieving subscriptions from database");
            subscriptions = backend.fetchSubscriptions(session);
        }
        return subscriptions;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getQueryNames() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.info("Invoking 'getQueryNames'");
        return QUERYNAMES;
    }

    /**
     * {@inheritDoc}
     */
    public String getStandardVersion() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.info("Invoking 'getStandardVersion'");
        return STD_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getSubscriptionIDs(String queryName) throws NoSuchNameExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse {
        try {
            LOG.info("Invoking 'getSubscriptionIDs'");
            QueryOperationsSession session = null;
            try {
                session = backend.openSession(dataSource);

                // TODO: filter by queryName?!
                Map<String, QuerySubscriptionScheduled> subscribedMap = loadSubscriptions(session);
                Set<String> temp = subscribedMap.keySet();
                return new ArrayList<String>(temp);
            } finally {
                if (session != null) {
                    session.close();
                }
                LOG.debug("DB connection closed");
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
    public String getVendorVersion() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.info("Invoking 'getVendorVersion'");
        return serviceVersion;
    }

    /**
     * {@inheritDoc}
     */
    public QueryResults poll(String queryName, QueryParams queryParams) throws NoSuchNameExceptionResponse,
            QueryParameterExceptionResponse, QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse {
        try {
            LOG.info("Invoking 'poll'");
            QueryOperationsSession session = null;
            try {
                session = backend.openSession(dataSource);
                QueryResultsBody resultsBody = null;
                if (queryName.equals("SimpleEventQuery")) {
                    LOG.info("This is a SimpleEventQuery");
                    EventListType eventList = new EventListType();
                    List<SimpleEventQueryDTO> eventQueries = constructSimpleEventQueries(queryParams);
                    // run queries sequentially
                    // TODO: might want to run them in parallel!
                    String orderBy = null;
                    OrderDirection orderDirection = null;
                    int limit = -1;
                    for (SimpleEventQueryDTO eventQuery : eventQueries) {
                        if (eventQuery.getOrderBy() != null) {
                            orderBy = eventQuery.getOrderBy();
                            orderDirection = eventQuery.getOrderDirection();
                            limit = eventQuery.getLimit();
                        }
                        backend.runSimpleEventQuery(session, eventQuery,
                                eventList.getObjectEventOrAggregationEventOrQuantityEvent());
                    }
                    eventList = checkOrdering(eventList, orderBy, orderDirection, limit);

                    resultsBody = new QueryResultsBody();
                    resultsBody.setEventList(eventList);
                } else if (queryName.equals("SimpleMasterDataQuery")) {
                    LOG.info("This is a SimpleMasterDataQuery");
                    VocabularyListType vocList = new VocabularyListType();
                    MasterDataQueryDTO mdQuery = constructMasterDataQuery(queryParams);
                    backend.runMasterDataQuery(session, mdQuery, vocList.getVocabulary());

                    resultsBody = new QueryResultsBody();
                    resultsBody.setVocabularyList(vocList);
                } else {
                    session.close();
                    String msg = "Unsupported query name '" + queryName + "' provided";
                    LOG.info("NoSuchNameException: " + msg);
                    NoSuchNameException e = new NoSuchNameException();
                    e.setReason(msg);
                    throw new NoSuchNameExceptionResponse(msg, e);
                }
                QueryResults results = new QueryResults();
                results.setResultsBody(resultsBody);
                results.setQueryName(queryName);

                LOG.info("poll request for '" + queryName + "' succeeded");
                return results;
            } finally {
                if (session != null) {
                    session.close();
                }
                LOG.debug("DB connection closed");
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
     * @param eventList
     * @param limit
     * @param orderDirection
     * @param orderBy
     * @return
     */
    private EventListType checkOrdering(EventListType eventList, String orderBy, OrderDirection orderDirection,
            int limit) {
        if (orderBy == null) {
            // no ordering specified
            return eventList;
        }
        if ("quantity".equals(orderBy)) {
            // order by quantity can only return QuantityEvents
            return eventList;
        }
        int size = eventList.getObjectEventOrAggregationEventOrQuantityEvent().size();
        if (limit > -1 && size == limit) {
            // there was only a single event type to be ordered - this has been
            // taken care of appropriately by the previous query
            return eventList;
        }
        LOG.debug("Need to apply sorting across the different event types (sortBy=" + orderBy + ")");
        boolean orderByEventTime = "eventTime".equals(orderBy);
        Comparator<Object> comparator = new EventComparator(orderByEventTime, orderDirection);
        Collections.sort(eventList.getObjectEventOrAggregationEventOrQuantityEvent(), comparator);
        if (limit > -1 && size > limit) {
            LOG.debug("Need to apply global limit to events (limit=" + limit + ")");
            // clear everything beyond limit
            eventList.getObjectEventOrAggregationEventOrQuantityEvent().subList(limit, size).clear();
        }
        return eventList;
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
            LOG.info("Invoking 'subscribe'");
            QueryOperationsSession session = null;
            try {
                session = backend.openSession(dataSource);
                String triggerURI = controls.getTrigger();
                QuerySubscriptionScheduled newSubscription = null;
                Schedule schedule = null;
                Calendar initialRecordTime;
                try {
                	initialRecordTime = controls.getInitialRecordTime().toGregorianCalendar();
                }catch(Exception e){
                	initialRecordTime = GregorianCalendar.getInstance();
                }

                // a few input sanity checks

                // dest may be null or empty. But we don't support pre-arranged
                // destinations and throw an InvalidURIException according to
                // the standard.
                if (dest == null || dest.toString().equals("")) {
                    String msg = "Destination URI is empty. This implementation doesn't support pre-arranged destinations.";
                    LOG.info("QueryParameterException: " + msg);
                    InvalidURIException e = new InvalidURIException();
                    e.setReason(msg);
                    throw new InvalidURIExceptionResponse(msg, e);
                }
                try {
                    new URL(dest.toString());
                } catch (MalformedURLException ex) {
                    String msg = "Destination URI is invalid: " + ex.getMessage();
                    LOG.info("InvalidURIException: " + msg);
                    InvalidURIException e = new InvalidURIException();
                    e.setReason(msg);
                    throw new InvalidURIExceptionResponse(msg, e, ex);
                }

                // check query name
                if (!QUERYNAMES.contains(queryName)) {
                    String msg = "Illegal query name '" + queryName + "'";
                    LOG.info("NoSuchNameException: " + msg);
                    NoSuchNameException e = new NoSuchNameException();
                    e.setReason(msg);
                    throw new NoSuchNameExceptionResponse(msg, e);
                }

                // SimpleMasterDataQuery only valid for polling
                if (queryName.equals("SimpleMasterDataQuery")) {
                    String msg = "Subscription not allowed for SimpleMasterDataQuery";
                    LOG.info("SubscribeNotPermittedException: " + msg);
                    SubscribeNotPermittedException e = new SubscribeNotPermittedException();
                    e.setReason(msg);
                    throw new SubscribeNotPermittedExceptionResponse(msg, e);
                }

                // subscriptionID cannot be empty
                if (subscriptionID == null || subscriptionID.equals("")) {
                    String msg = "SubscriptionID is empty. Choose a valid subscriptionID";
                    LOG.info(msg);
                    ValidationException e = new ValidationException();
                    e.setReason(msg);
                    throw new ValidationExceptionResponse(msg, e);
                }

                // check for already existing subscriptionID
                if (backend.fetchExistsSubscriptionId(session, subscriptionID)) {
                    String msg = "SubscriptionID '" + subscriptionID
                            + "' already exists. Choose a different subscriptionID";
                    LOG.info("DuplicateSubscriptionException: " + msg);
                    DuplicateSubscriptionException e = new DuplicateSubscriptionException();
                    e.setReason(msg);
                    throw new DuplicateSubscriptionExceptionResponse(msg, e);
                }

                // trigger and schedule may no be used together, but one of them
                // must be set
                if (controls.getSchedule() != null && controls.getTrigger() != null) {
                    String msg = "Schedule and trigger cannot be used together";
                    LOG.info("SubscriptionControlsException: " + msg);
                    SubscriptionControlsException e = new SubscriptionControlsException();
                    e.setReason(msg);
                    throw new SubscriptionControlsExceptionResponse(msg, e);
                }
                if (controls.getSchedule() == null && controls.getTrigger() == null) {
                    String msg = "Either schedule or trigger has to be provided";
                    LOG.info("SubscriptionControlsException: " + msg);
                    SubscriptionControlsException e = new SubscriptionControlsException();
                    e.setReason(msg);
                    throw new SubscriptionControlsExceptionResponse(msg, e);
                }
                if (controls.getSchedule() != null) {
                    LOG.debug("Received new scheduled query.");
                    // Scheduled Query -> parse schedule
                    schedule = new Schedule(controls.getSchedule());
                    newSubscription = new QuerySubscriptionScheduled(subscriptionID, params, dest,
                            Boolean.valueOf(controls.isReportIfEmpty()), initialRecordTime, initialRecordTime,
                            schedule, queryName);
                } else {
                    LOG.debug("Received new triggered query.");
                    // need to set schedule which says how often the trigger
                    // condition is checked.
                    QuerySchedule qSchedule = new QuerySchedule();
                    qSchedule.setSecond(triggerConditionSeconds);
                    if (triggerConditionMinutes != null) {
                        qSchedule.setMinute(triggerConditionMinutes);
                    }
                    schedule = new Schedule(qSchedule);
                    QuerySubscriptionTriggered trigger = new QuerySubscriptionTriggered(subscriptionID, params, dest,
                            Boolean.valueOf(controls.isReportIfEmpty()), initialRecordTime, initialRecordTime,
                            queryName, triggerURI, schedule);
                    newSubscription = trigger;
                }

                // load subscriptions
                Map<String, QuerySubscriptionScheduled> subscribedMap = loadSubscriptions(session);

                // store the Query to the database, the local hash map, and the
                // application context
                backend.storeSupscriptions(session, params, dest, subscriptionID, controls, triggerURI,
                        newSubscription, queryName, schedule);
                subscribedMap.put(subscriptionID, newSubscription);
                saveSubscriptions(subscribedMap);
            } finally {
                if (session != null) {
                    session.close();
                }
                LOG.debug("DB connection closed");
            }
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
        try {
            LOG.info("Invoking 'unsubscribe'");
            QueryOperationsSession session = null;
            try {
                session = backend.openSession(dataSource);
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
                    LOG.info("NoSuchSubscriptionException: " + msg);
                    NoSuchSubscriptionException e = new NoSuchSubscriptionException();
                    e.setReason(msg);
                    throw new NoSuchSubscriptionExceptionResponse(msg, e);
                }
            } finally {
                if (session != null) {
                    session.close();
                }
                LOG.debug("DB connection closed");
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

    /**
     * @param servletContext
     *            the servletContextservletContext to set
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * @return the serviceVersion
     */
    public String getServiceVersion() {
        return serviceVersion;
    }

    /**
     * @param serviceVersion
     *            the serviceVersion to set
     */
    public void setServiceVersion(String serviceVersion) {
        if (!"".equals(serviceVersion)) {
            // serviceVersion must be a valid URL
            try {
                new URL(serviceVersion);
            } catch (MalformedURLException e) {
                serviceVersion = "http://www.fosstrak.org/epcis/" + serviceVersion;
            }
        }
        this.serviceVersion = serviceVersion;
    }

    /**
     * @return the backend
     */
    public QueryOperationsBackend getBackend() {
        return backend;
    }

    /**
     * @param backend
     *            the backend to set
     */
    public void setBackend(QueryOperationsBackend backend) {
        this.backend = backend;
    }

    /**
     * Compares two EPCIS events according to their eventTime or recordTime.
     * Careful: the objects to be compared are instances of EPCISEvent,
     * otherwise a ClassCastException will be thrown.
     * 
     * @author Marco Steybe
     */
    public class EventComparator implements Comparator<Object> {
        private boolean orderByEventTime = false;
        private OrderDirection orderDirection = null;

        public EventComparator(boolean orderByEventTime, OrderDirection orderDirection) {
            this.orderByEventTime = orderByEventTime;
            this.orderDirection = orderDirection;
        }

        public int compare(Object o1, Object o2) {
            EPCISEventType event1 = (EPCISEventType) o1;
            EPCISEventType event2 = (EPCISEventType) o2;
            if (orderByEventTime) {
                if (orderDirection == OrderDirection.ASC) {
                    return event1.getEventTime().compare(event2.getEventTime());
                } else {
                    return event2.getEventTime().compare(event1.getEventTime());
                }
            } else {
                // order by recordTime
                if (orderDirection == OrderDirection.ASC) {
                    return event1.getRecordTime().compare(event2.getRecordTime());
                } else {
                    return event2.getEventTime().compare(event1.getEventTime());
                }
            }
        }
    }
}
