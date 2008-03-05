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

package org.accada.epcis.repository.query;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.accada.epcis.repository.EpcisConstants;
import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.QueryTooLargeExceptionResponse;
import org.accada.epcis.soap.model.ActionType;
import org.accada.epcis.soap.model.AggregationEventType;
import org.accada.epcis.soap.model.AttributeType;
import org.accada.epcis.soap.model.BusinessLocationType;
import org.accada.epcis.soap.model.BusinessTransactionListType;
import org.accada.epcis.soap.model.BusinessTransactionType;
import org.accada.epcis.soap.model.EPC;
import org.accada.epcis.soap.model.EPCISEventType;
import org.accada.epcis.soap.model.EPCListType;
import org.accada.epcis.soap.model.IDListType;
import org.accada.epcis.soap.model.ImplementationException;
import org.accada.epcis.soap.model.ImplementationExceptionSeverity;
import org.accada.epcis.soap.model.ObjectEventType;
import org.accada.epcis.soap.model.QuantityEventType;
import org.accada.epcis.soap.model.QueryParam;
import org.accada.epcis.soap.model.QueryParams;
import org.accada.epcis.soap.model.QueryTooLargeException;
import org.accada.epcis.soap.model.ReadPointType;
import org.accada.epcis.soap.model.SubscriptionControls;
import org.accada.epcis.soap.model.TransactionEventType;
import org.accada.epcis.soap.model.VocabularyElementListType;
import org.accada.epcis.soap.model.VocabularyElementType;
import org.accada.epcis.soap.model.VocabularyType;
import org.accada.epcis.utils.TimeParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO: javadoc
 * 
 * @author Marco Steybe
 */
public class QueryOperationsBackend {

    private static final Log LOG = LogFactory.getLog(QueryOperationsBackend.class);

    private static final String SQL_SELECT_AGGREGATIONEVENT_EXTENSIONS = "SELECT ext.fieldname, ext.prefix, ext.intValue, ext.floatValue, ext.dateValue, ext.strValue FROM event_AggregationEvent_extensions AS ext WHERE ext.event_id=?";
    private static final String SQL_SELECT_OBJECTEVENT_EXTENSIONS = "SELECT ext.fieldname, ext.prefix, ext.intValue, ext.floatValue, ext.dateValue, ext.strValue FROM event_ObjectEvent_extensions AS ext WHERE event_id=?";
    private static final String SQL_SELECT_QUANTITYEVENT_EXTENSIONS = "SELECT ext.fieldname, ext.prefix, ext.intValue, ext.floatValue, ext.dateValue, ext.strValue FROM event_QuantityEvent_extensions AS ext WHERE event_id=?";
    private static final String SQL_SELECT_TRANSACTIONEVENT_EXTENSIONS = "SELECT ext.fieldname, ext.prefix, ext.intValue, ext.floatValue, ext.dateValue, ext.strValue FROM event_TransactionEvent_extensions AS ext WHERE event_id=?";

    private static final String SQL_SELECT_AGGREGATIONEVENT_BIZTRANS = "SELECT bizTrans.uri AS bizTrans, bizTransType.uri AS bizTransType FROM event_AggregationEvent_bizTrans AS eventBizTrans JOIN BizTransaction ON eventBizTrans.bizTrans_id=BizTransaction.id JOIN voc_BizTrans AS bizTrans ON BizTransaction.bizTrans=bizTrans.id JOIN voc_BizTransType AS bizTransType ON BizTransaction.type=bizTransType.id WHERE eventBizTrans.event_id=?";
    private static final String SQL_SELECT_OBJECTEVENT_BIZTRANS = "SELECT bizTrans.uri AS bizTrans, bizTransType.uri AS bizTransType FROM event_ObjectEvent_bizTrans AS eventBizTrans JOIN BizTransaction ON eventBizTrans.bizTrans_id=BizTransaction.id JOIN voc_BizTrans AS bizTrans ON BizTransaction.bizTrans=bizTrans.id JOIN voc_BizTransType AS bizTransType ON BizTransaction.type=bizTransType.id WHERE eventBizTrans.event_id=?";
    private static final String SQL_SELECT_QUANTITYEVENT_BIZTRANS = "SELECT bizTrans.uri AS bizTrans, bizTransType.uri AS bizTransType FROM event_QuantityEvent_bizTrans AS eventBizTrans JOIN BizTransaction ON eventBizTrans.bizTrans_id=BizTransaction.id JOIN voc_BizTrans AS bizTrans ON BizTransaction.bizTrans=bizTrans.id JOIN voc_BizTransType AS bizTransType ON BizTransaction.type=bizTransType.id WHERE eventBizTrans.event_id=?";
    private static final String SQL_SELECT_TRANSACTIONEVENT_BIZTRANS = "SELECT bizTrans.uri AS bizTrans, bizTransType.uri AS bizTransType FROM event_TransactionEvent_bizTrans AS eventBizTrans JOIN BizTransaction ON eventBizTrans.bizTrans_id=BizTransaction.id JOIN voc_BizTrans AS bizTrans ON BizTransaction.bizTrans=bizTrans.id JOIN voc_BizTransType AS bizTransType ON BizTransaction.type=bizTransType.id WHERE eventBizTrans.event_id=?";

    private static final String SQL_SELECT_AGGREGATIONEVENT_EPCS = "SELECT epc FROM event_AggregationEvent_epcs WHERE event_id=?";
    private static final String SQL_SELECT_OBJECTEVENT_EPCS = "SELECT epc FROM event_ObjectEvent_epcs WHERE event_id=?";
    private static final String SQL_SELECT_QUANTITYEVENT_EPCS = "SELECT epc FROM event_QuantityEvent_epcs WHERE event_id=?";
    private static final String SQL_SELECT_TRANSACTIONEVENT_EPCS = "SELECT epc FROM event_TransactionEvent_epcs WHERE event_id=?";

    private static final String SQL_EXISTS_SUBSCRIPTION = "SELECT EXISTS (SELECT subscriptionid FROM subscription WHERE subscriptionid=?)";

    private static Map<String, String> vocTablenameMap;

    static {
        vocTablenameMap = new HashMap<String, String>(5);
        vocTablenameMap.put(EpcisConstants.BUSINESS_STEP_ID, "voc_BizStep");
        vocTablenameMap.put(EpcisConstants.BUSINESS_TRANSACTION, "voc_BizTrans");
        vocTablenameMap.put(EpcisConstants.DISPOSITION_ID, "voc_Disposition");
        vocTablenameMap.put(EpcisConstants.READ_POINT_ID, "voc_ReadPoint");
        vocTablenameMap.put(EpcisConstants.BUSINESS_LOCATION_ID, "voc_BizLoc");
    }

    protected static String getVocabularyTablename(String vocabularyType) {
        return vocTablenameMap.get(vocabularyType);
    }

    public void runSimpleEventQuery(final QueryOperationsSession session, final SimpleEventQuerySql eventQuery,
            final List<Object> eventList) throws SQLException, ImplementationExceptionResponse,
            QueryTooLargeExceptionResponse {
        String eventType = eventQuery.getEventType();

        // prepare and execute the main SQL SELECT query
        String sqlSelect = eventQuery.getSqlString();
        PreparedStatement selectEventsStmt = session.getConnection().prepareStatement(sqlSelect);
        List<Object> sqlParams = eventQuery.getSqlParams();
        LOG.debug("SQL: " + sqlSelect);
        for (int i = 0; i < sqlParams.size(); i++) {
            selectEventsStmt.setObject(i + 1, sqlParams.get(i));
            if (LOG.isDebugEnabled()) {
                LOG.debug("     param" + i + " = " + sqlParams.get(i));
            }
        }
        ResultSet rs = selectEventsStmt.executeQuery();

        // prepare the required remaining SQL queries
        String selectExtensions = null;
        String selectEpcs = null;
        String selectBizTrans = null;
        if (EpcisConstants.AGGREGATION_EVENT.equals(eventType)) {
            selectExtensions = SQL_SELECT_AGGREGATIONEVENT_EXTENSIONS;
            selectEpcs = SQL_SELECT_AGGREGATIONEVENT_EPCS;
            selectBizTrans = SQL_SELECT_AGGREGATIONEVENT_BIZTRANS;
        } else if (EpcisConstants.OBJECT_EVENT.equals(eventType)) {
            selectExtensions = SQL_SELECT_OBJECTEVENT_EXTENSIONS;
            selectEpcs = SQL_SELECT_OBJECTEVENT_EPCS;
            selectBizTrans = SQL_SELECT_OBJECTEVENT_BIZTRANS;
        } else if (EpcisConstants.QUANTITY_EVENT.equals(eventType)) {
            selectExtensions = SQL_SELECT_QUANTITYEVENT_EXTENSIONS;
            selectEpcs = SQL_SELECT_QUANTITYEVENT_EPCS;
            selectBizTrans = SQL_SELECT_QUANTITYEVENT_BIZTRANS;
        } else if (EpcisConstants.TRANSACTION_EVENT.equals(eventType)) {
            selectExtensions = SQL_SELECT_TRANSACTIONEVENT_EXTENSIONS;
            selectEpcs = SQL_SELECT_TRANSACTIONEVENT_EPCS;
            selectBizTrans = SQL_SELECT_TRANSACTIONEVENT_BIZTRANS;
        }
        PreparedStatement selectExtensionsStmt = session.getPreparedStatement(selectExtensions);
        PreparedStatement selectEpcsStmt = session.getPreparedStatement(selectEpcs);
        PreparedStatement selectBizTransStmt = session.getPreparedStatement(selectBizTrans);

        // cycle through result set and fill an event list
        int actEventCount = 0;
        while (rs.next()) {
            actEventCount++;
            int eventId = rs.getInt(1);
            Timestamp eventTime = rs.getTimestamp(2);
            Timestamp recordTime = rs.getTimestamp(3);
            String eventTimeZoneOffset = rs.getString(4);
            String readPointId = rs.getString(5);
            ReadPointType readPoint = null;
            if (readPointId != null) {
                readPoint = new ReadPointType();
                readPoint.setId(readPointId);
            }
            String bizLocationId = rs.getString(6);
            BusinessLocationType bizLocation = null;
            if (bizLocationId != null) {
                bizLocation = new BusinessLocationType();
                bizLocation.setId(bizLocationId);
            }
            String bizStep = rs.getString(7);
            String disposition = rs.getString(8);
            // fetch biz transactions
            if (LOG.isDebugEnabled()) {
                LOG.debug("SQL: " + selectBizTrans);
                LOG.debug("     param1 = " + eventId);
            }
            selectBizTransStmt.setInt(1, eventId);
            BusinessTransactionListType bizTransList = readBizTransactionsFromResult(selectBizTransStmt.executeQuery());

            EPCISEventType event = null;
            if (EpcisConstants.AGGREGATION_EVENT.equals(eventType)) {
                AggregationEventType aggrEvent = new AggregationEventType();
                aggrEvent.setReadPoint(readPoint);
                aggrEvent.setBizLocation(bizLocation);
                aggrEvent.setBizStep(bizStep);
                aggrEvent.setDisposition(disposition);
                aggrEvent.setAction(ActionType.valueOf(rs.getString(9)));
                aggrEvent.setParentID(rs.getString(10));
                aggrEvent.setBizTransactionList(bizTransList);
                // fetch EPCs
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: " + selectEpcs);
                    LOG.debug("     param1 = " + eventId);
                }
                selectEpcsStmt.setInt(1, eventId);
                aggrEvent.setChildEPCs(readEpcsFromResult(selectEpcsStmt.executeQuery()));
                // fetch and fill extensions
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: " + selectExtensions);
                    LOG.debug("     param1 = " + eventId);
                }
                selectExtensionsStmt.setInt(1, eventId);
                readExtensionsFromResult(selectExtensionsStmt.executeQuery(), aggrEvent.getAny());
                event = aggrEvent;
            } else if (EpcisConstants.OBJECT_EVENT.equals(eventType)) {
                ObjectEventType objEvent = new ObjectEventType();
                objEvent.setReadPoint(readPoint);
                objEvent.setBizLocation(bizLocation);
                objEvent.setBizStep(bizStep);
                objEvent.setDisposition(disposition);
                objEvent.setAction(ActionType.valueOf(rs.getString(9)));
                objEvent.setBizTransactionList(bizTransList);
                // fetch EPCs
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: " + selectEpcs);
                    LOG.debug("     param1 = " + eventId);
                }
                selectEpcsStmt.setInt(1, eventId);
                objEvent.setEpcList(readEpcsFromResult(selectEpcsStmt.executeQuery()));
                // fetch and fill extensions
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: " + selectExtensions);
                    LOG.debug("     param1 = " + eventId);
                }
                selectExtensionsStmt.setInt(1, eventId);
                readExtensionsFromResult(selectExtensionsStmt.executeQuery(), objEvent.getAny());
                event = objEvent;
            } else if (EpcisConstants.QUANTITY_EVENT.equals(eventType)) {
                QuantityEventType quantEvent = new QuantityEventType();
                quantEvent.setReadPoint(readPoint);
                quantEvent.setBizLocation(bizLocation);
                quantEvent.setBizStep(bizStep);
                quantEvent.setDisposition(disposition);
                quantEvent.setEpcClass(rs.getString(9));
                quantEvent.setQuantity(rs.getInt(10));
                quantEvent.setBizTransactionList(bizTransList);
                // fetch and fill extensions
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: " + selectExtensions);
                    LOG.debug("     param1 = " + eventId);
                }
                selectExtensionsStmt.setInt(1, eventId);
                readExtensionsFromResult(selectExtensionsStmt.executeQuery(), quantEvent.getAny());
                event = quantEvent;
            } else if (EpcisConstants.TRANSACTION_EVENT.equals(eventType)) {
                TransactionEventType transEvent = new TransactionEventType();
                transEvent.setReadPoint(readPoint);
                transEvent.setBizLocation(bizLocation);
                transEvent.setBizStep(bizStep);
                transEvent.setDisposition(disposition);
                transEvent.setAction(ActionType.valueOf(rs.getString(9)));
                transEvent.setParentID(rs.getString(10));
                transEvent.setBizTransactionList(bizTransList);
                // fetch EPCs
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: " + selectEpcs);
                    LOG.debug("     param1 = " + eventId);
                }
                selectEpcsStmt.setInt(1, eventId);
                transEvent.setEpcList(readEpcsFromResult(selectEpcsStmt.executeQuery()));
                // fetch and fill extensions
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: " + selectExtensions);
                    LOG.debug("     param1 = " + eventId);
                }
                selectExtensionsStmt.setInt(1, eventId);
                readExtensionsFromResult(selectExtensionsStmt.executeQuery(), transEvent.getAny());
                event = transEvent;
            } else {
                String msg = "Invalid eventType: " + eventType;
                LOG.error(msg);
                ImplementationException ie = new ImplementationException();
                ie.setReason(msg);
                throw new ImplementationExceptionResponse(msg, ie);
            }
            event.setEventTime(timestampToXmlCalendar(eventTime));
            event.setRecordTime(timestampToXmlCalendar(recordTime));
            event.setEventTimeZoneOffset(eventTimeZoneOffset);
            eventList.add(event);
        }
        int maxEventCount = eventQuery.getMaxEventCount();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Event query returned " + actEventCount + " events (maxEventCount is " + maxEventCount + ")");
        }
        if (maxEventCount > -1 && actEventCount > maxEventCount) {
            // according to spec, this must result in a QueryTooLargeException
            String msg = "The query returned more results than specified by 'maxEventCount'";
            LOG.info("USER ERROR: " + msg);
            QueryTooLargeException e = new QueryTooLargeException();
            e.setReason(msg);
            throw new QueryTooLargeExceptionResponse(msg, e);
        }
    }

    public void runMasterDataQuery(final QueryOperationsSession session, final MasterDataQuerySql mdQuery,
            final List<VocabularyType> vocList) throws SQLException, ImplementationExceptionResponse,
            QueryTooLargeExceptionResponse {

        List<String> vocabularyTypes = mdQuery.getVocabularyTypes();
        List<Object> sqlParams = mdQuery.getSqlParams();
        int maxElementCount = mdQuery.getMaxElementCount();
        boolean includeAttributes = mdQuery.getIncludeAttributes();
        boolean includeChildren = mdQuery.getIncludeChildren();

        for (String vocType : vocabularyTypes) {
            String sqlSelect = mdQuery.getSqlForVocabulary(vocType);
            PreparedStatement ps = session.getConnection().prepareStatement(sqlSelect);
            LOG.debug("SQL: " + sqlSelect);
            for (int i = 0; i < sqlParams.size(); i++) {
                ps.setObject(i + 1, sqlParams.get(i));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("     param" + i + " = " + sqlParams.get(i));
                }
            }
            ResultSet rs = ps.executeQuery();

            // fetch matching vocabulary element uris
            List<String> vocElemUris = new ArrayList<String>();
            int actVocElemCount = 0;
            while (rs.next()) {
                actVocElemCount++;
                if (maxElementCount > -1 && actVocElemCount > maxElementCount) {
                    // according to spec, this must result in a
                    // QueryTooLargeException
                    String msg = "The query returned more results than specified by 'maxElementCount'";
                    LOG.info("USER ERROR: " + msg);
                    QueryTooLargeException e = new QueryTooLargeException();
                    e.setReason(msg);
                    throw new QueryTooLargeExceptionResponse(msg, e);
                }
                vocElemUris.add(rs.getString(1));
            }
            rs.close();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Masterdata query returned " + actVocElemCount + " vocabularies (maxElementCount is "
                        + maxElementCount + ")");
            }

            // populate the VocabularyElementList
            VocabularyElementListType vocElems = new VocabularyElementListType();
            for (String vocElemUri : vocElemUris) {
                VocabularyElementType vocElem = new VocabularyElementType();
                vocElem.setId(vocElemUri);
                if (includeAttributes) {
                    fetchAttributes(session, vocType, vocElemUri, mdQuery.getIncludedAttributeNames(),
                            vocElem.getAttribute());
                }
                if (includeChildren) {
                    IDListType children = fetchChildren(session, vocType, vocElemUri);
                    vocElem.setChildren(children);
                }
                vocElems.getVocabularyElement().add(vocElem);
            }

            // add the vocabulary element to the vocabulary list
            if (!vocElems.getVocabularyElement().isEmpty()) {
                VocabularyType voc = new VocabularyType();
                voc.setType(vocType);
                voc.setVocabularyElementList(vocElems);
                vocList.add(voc);
            }
        }
    }

    /**
     * Checks if the given subscription ID already exists.
     * 
     * @param subscriptionID
     *            The subscription ID to be checked.
     * @return <code>true</code> if the given subscription ID already exists,
     *         <code>false</code> otherwise.
     * @throws SQLException
     *             If a problem with the database occurred.
     */
    public boolean fetchExistsSubscriptionId(final QueryOperationsSession session, final String subscriptionID)
            throws SQLException {
        PreparedStatement stmt = session.getPreparedStatement(SQL_EXISTS_SUBSCRIPTION);
        stmt.setString(1, subscriptionID);
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL: " + SQL_EXISTS_SUBSCRIPTION);
            LOG.debug("     param1 = " + subscriptionID);
        }
        ResultSet rs = stmt.executeQuery();
        rs.first();
        return rs.getBoolean(1);
    }

    /**
     * @param session
     * @param vocElemUri
     * @param attribute
     * @throws SQLException
     */
    private void fetchAttributes(final QueryOperationsSession session, final String vocType, final String vocUri,
            final List<String> filterAttrNames, final List<AttributeType> attributes) throws SQLException {
        String vocTablename = vocTablenameMap.get(vocType);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT attribute, value FROM ").append(vocTablename).append(" AS voc, ");
        sql.append(vocTablename).append("_attr AS attr WHERE voc.id=attr.id AND voc.uri=?");
        if (filterAttrNames != null && !filterAttrNames.isEmpty()) {
            // filter by attribute names
            sql.append(" AND attribute IN (?");
            for (int i = 1; i < filterAttrNames.size(); i++) {
                sql.append(",?");
            }
            sql.append(")");
        }
        PreparedStatement ps = session.getPreparedStatement(sql.toString());
        ps.setString(1, vocUri);
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL: " + sql.toString());
            LOG.debug("     param1 = " + vocUri);
        }
        if (filterAttrNames != null && !filterAttrNames.isEmpty()) {
            for (int i = 0; i < filterAttrNames.size(); i++) {
                ps.setString(i + 2, filterAttrNames.get(i));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("     param" + i + 2 + " = " + filterAttrNames.get(i));
                }
            }
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            AttributeType attr = new AttributeType();
            attr.setId(rs.getString(1));
            attr.getContent().add(rs.getString(2));
            attributes.add(attr);
        }
        rs.close();
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
    private IDListType fetchChildren(final QueryOperationsSession session, final String vocType, final String vocUri)
            throws SQLException, ImplementationExceptionResponse {
        IDListType children = new IDListType();
        String vocTablename = vocTablenameMap.get(vocType);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT uri FROM ").append(vocTablename).append(" AS voc WHERE voc.uri LIKE ?");
        PreparedStatement ps = session.getPreparedStatement(sql.toString());
        String uri = vocUri + "_%";
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL: " + sql.toString());
            LOG.debug("     param1 = " + uri);
        }
        ps.setString(1, uri);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            children.getId().add(rs.getString("uri"));
        }
        return (children.getId().isEmpty()) ? null : children;
    }

    /**
     * Retrieves a list of business transactions (an instance of
     * BusinessTransactionListType) from the given result set.
     * 
     * @param rs
     *            The result of the SQL query.
     * @return A List of qualified XML elements
     * @throws SQLException
     *             If a database access error occurred.
     */
    private BusinessTransactionListType readBizTransactionsFromResult(final ResultSet rs) throws SQLException,
            ImplementationExceptionResponse {
        BusinessTransactionListType list = new BusinessTransactionListType();
        while (rs.next()) {
            BusinessTransactionType btrans = new BusinessTransactionType();
            btrans.setValue(rs.getString(1));
            btrans.setType(rs.getString(2));
            list.getBizTransaction().add(btrans);
        }
        return list.getBizTransaction().isEmpty() ? null : list;
    }

    /**
     * Retrieves a list of EPCs (an instance of EPCListType) from the given
     * result set.
     * 
     * @param rs
     *            The result of the SQL query.
     * @return A List of qualified XML elements
     * @throws SQLException
     *             If a database access error occurred.
     */
    private EPCListType readEpcsFromResult(final ResultSet rs) throws SQLException {
        EPCListType epcs = new EPCListType();
        while (rs.next()) {
            EPC epc = new EPC();
            epc.setValue(rs.getString(1));
            epcs.getEpc().add(epc);
        }
        return epcs.getEpc().isEmpty() ? null : epcs;
    }

    /**
     * Fetches the qualified XML elements representing extensions for event
     * fields from the given result set and populates the given List.
     * 
     * @param rs
     *            The result of the SQL query.
     * @throws SQLException
     *             If a database access error occurred.
     */
    private void readExtensionsFromResult(final ResultSet rs, final List<Object> extensions) throws SQLException {
        while (rs.next()) {
            String fieldname = rs.getString(1);
            String[] parts = fieldname.split("#");
            if (parts.length != 2) {
                throw new SQLException(
                        "Fieldname extension has invalid format: required 'namespace#localname' but was " + fieldname);
            }
            String namespace = parts[0];
            String localPart = parts[1];
            String prefix = rs.getString(2);
            String value = rs.getString(3);
            if (value == null) {
                value = rs.getString(4);
                if (value == null) {
                    value = rs.getString(5);
                    if (value == null) {
                        value = rs.getString(6);
                        if (value == null) {
                            throw new SQLException("No valid extension value found");
                        }
                    }
                }
            }
            JAXBElement<String> elem = new JAXBElement<String>(new QName(namespace, localPart, prefix), String.class,
                    value);
            extensions.add(elem);
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
    public Map<String, QuerySubscriptionScheduled> fetchSubscriptions(final QueryOperationsSession session)
            throws SQLException, ImplementationExceptionResponse {
        String query = "SELECT * FROM subscription";
        LOG.debug("SQL: " + query);
        Statement stmt = session.getConnection().createStatement();
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
                    storedSubscription = new QuerySubscriptionScheduled(subscrId, params, dest,
                            Boolean.valueOf(exportifempty), initrectime, new GregorianCalendar(), sched, queryName);
                } else {
                    storedSubscription = new QuerySubscriptionTriggered(subscrId, params, dest,
                            Boolean.valueOf(exportifempty), initrectime, new GregorianCalendar(), queryName, trigger,
                            sched);
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
        return subscribedMap;
    }

    /**
     * @param connection
     * @param queryParams
     * @param dest
     * @param subscrId
     * @param controls
     * @param trigger
     * @param newSubscription
     * @param queryName
     * @param schedule
     * @throws SQLException
     * @throws ImplementationExceptionResponse
     */
    public void storeSupscriptions(final QueryOperationsSession session, QueryParams queryParams, String dest,
            String subscrId, SubscriptionControls controls, String trigger, QuerySubscriptionScheduled newSubscription,
            String queryName, Schedule schedule) throws SQLException, ImplementationExceptionResponse {
        String insert = "INSERT INTO subscription (subscriptionid, "
                + "params, dest, sched, trigg, initialrecordingtime, "
                + "exportifempty, queryname, lastexecuted) VALUES " + "((?), (?), (?), (?), (?), (?), (?), (?), (?))";
        PreparedStatement stmt = session.getConnection().prepareStatement(insert);
        LOG.debug("QUERY: " + insert);
        try {
            stmt.setString(1, subscrId);
            LOG.debug("       query param 1: " + subscrId);

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(outStream);
            out.writeObject(queryParams);
            ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
            stmt.setBinaryStream(2, inStream, inStream.available());
            LOG.debug("       query param 2: [" + inStream.available() + " bytes]");

            stmt.setString(3, dest.toString());
            LOG.debug("       query param 3: " + dest);

            outStream = new ByteArrayOutputStream();
            out = new ObjectOutputStream(outStream);
            out.writeObject(schedule);
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            stmt.setBinaryStream(4, inStream, inStream.available());
            LOG.debug("       query param 4: [" + inStream.available() + " bytes]");

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
    }

    /**
     * @param connection
     * @param subscrId
     * @throws SQLException
     */
    public void deleteSubscription(final QueryOperationsSession session, String subscrId) throws SQLException {
        String delete = "DELETE FROM subscription WHERE subscriptionid=?";
        PreparedStatement ps = session.getConnection().prepareStatement(delete);
        ps.setString(1, subscrId);
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL: " + delete);
            LOG.debug("     param1 = " + subscrId);
        }
        ps.executeUpdate();
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
            String msg = "Unable to instantiate an XML representation for a date/time datatype.";
            ImplementationException iex = new ImplementationException();
            iex.setReason(msg);
            iex.setSeverity(ImplementationExceptionSeverity.SEVERE);
            throw new ImplementationExceptionResponse(msg, iex, e);
        }
    }

    /**
     * Opens a new session for the database transaction.
     * <p>
     * TODO: we should use connection pooling and caching of PreparedStatements!
     * 
     * @param dataSource
     *            The DataSource object to retrieve the database connection
     *            from.
     * @return A QueryOperationsSession instantiated with the database
     *         connection retrieved from the given DataSource.
     * @throws SQLException
     *             If an SQL error occurred.
     */
    public QueryOperationsSession openSession(final DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        LOG.debug("Database connection for session established");
        return new QueryOperationsSession(connection);
    }
}
