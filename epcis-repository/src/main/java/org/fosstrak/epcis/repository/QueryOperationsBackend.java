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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.accada.epcis.repository.QueryOperationsModule.EventType;
import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.QueryTooLargeExceptionResponse;
import org.accada.epcis.soap.model.ActionType;
import org.accada.epcis.soap.model.AggregationEventType;
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

    public void runEventQuery(final QueryOperationsSession session, final SimpleEventQuery eventQuery,
            final List<Object> eventList) throws SQLException,
            ImplementationExceptionResponse, QueryTooLargeExceptionResponse {
        EventType eventType = eventQuery.getEventType();

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
        switch (eventType) {
        case AggregationEvent:
            selectExtensions = SQL_SELECT_AGGREGATIONEVENT_EXTENSIONS;
            selectEpcs = SQL_SELECT_AGGREGATIONEVENT_EPCS;
            selectBizTrans = SQL_SELECT_AGGREGATIONEVENT_BIZTRANS;
            break;
        case ObjectEvent:
            selectExtensions = SQL_SELECT_OBJECTEVENT_EXTENSIONS;
            selectEpcs = SQL_SELECT_OBJECTEVENT_EPCS;
            selectBizTrans = SQL_SELECT_OBJECTEVENT_BIZTRANS;
            break;
        case QuantityEvent:
            selectExtensions = SQL_SELECT_QUANTITYEVENT_EXTENSIONS;
            selectEpcs = SQL_SELECT_QUANTITYEVENT_EPCS;
            selectBizTrans = SQL_SELECT_QUANTITYEVENT_BIZTRANS;
            break;
        case TransactionEvent:
            selectExtensions = SQL_SELECT_TRANSACTIONEVENT_EXTENSIONS;
            selectEpcs = SQL_SELECT_TRANSACTIONEVENT_EPCS;
            selectBizTrans = SQL_SELECT_TRANSACTIONEVENT_BIZTRANS;
            break;
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
            BusinessTransactionListType bizTransList = getBizTransactionsFromResult(selectBizTransStmt.executeQuery());

            EPCISEventType event = null;
            switch (eventType) {
            case AggregationEvent:
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
                aggrEvent.setChildEPCs(getEpcsFromResult(selectEpcsStmt.executeQuery()));
                // fetch and fill extensions
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: " + selectExtensions);
                    LOG.debug("     param1 = " + eventId);
                }
                selectExtensionsStmt.setInt(1, eventId);
                fillExtensionsFromResult(selectExtensionsStmt.executeQuery(), aggrEvent.getAny());
                event = aggrEvent;
                break;
            case ObjectEvent:
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
                objEvent.setEpcList(getEpcsFromResult(selectEpcsStmt.executeQuery()));
                // fetch and fill extensions
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: " + selectExtensions);
                    LOG.debug("     param1 = " + eventId);
                }
                selectExtensionsStmt.setInt(1, eventId);
                fillExtensionsFromResult(selectExtensionsStmt.executeQuery(), objEvent.getAny());
                event = objEvent;
                break;
            case QuantityEvent:
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
                fillExtensionsFromResult(selectExtensionsStmt.executeQuery(), quantEvent.getAny());
                event = quantEvent;
                break;
            case TransactionEvent:
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
                transEvent.setEpcList(getEpcsFromResult(selectEpcsStmt.executeQuery()));
                // fetch and fill extensions
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: " + selectExtensions);
                    LOG.debug("     param1 = " + eventId);
                }
                selectExtensionsStmt.setInt(1, eventId);
                fillExtensionsFromResult(selectExtensionsStmt.executeQuery(), transEvent.getAny());
                event = transEvent;
                break;
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
     *            A possibly empty list of attribute names which should filter
     *            the retrieved attributes.
     * @return The attributes, a mapping from attribute name to attribute value.
     * @throws SQLException
     *             If an error accessing the database occurred.
     */
    public Map<String, String> fetchAttributes(final QueryOperationsSession session, final String vocTableName,
            final String vocName, final List<String> filterAttrNames) throws SQLException {
        Map<String, String> attributes = new HashMap<String, String>();

        List<String> queryArgs = new ArrayList<String>(filterAttrNames.size());
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT attribute, value FROM voc_").append(vocTableName).append("_attr");
        sql.append(" AS attrTable WHERE attrTable.id=(SELECT id FROM ");
        sql.append("voc_").append(vocTableName).append(" WHERE uri=?)");
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
        LOG.debug("SQL: " + query);
        PreparedStatement ps = session.getConnection().prepareStatement(query);
        int i = 1;
        for (String arg : queryArgs) {
            ps.setString(i++, arg);
            if (LOG.isDebugEnabled()) {
                LOG.debug("     param" + i + " = " + arg);
            }
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
    public List<String> fetchVocabularies(final QueryOperationsSession session, final String table,
            final List<String> filterVocNames, final List<String> filterVocNamesWd,
            final Map<String, List<String>> filterAttrs, final List<String> attrs, final int maxElementCount)
            throws SQLException, ImplementationExceptionResponse, QueryTooLargeExceptionResponse {
        List<String> vocs = new ArrayList<String>();

        StringBuilder sql = new StringBuilder();
        List<String> queryArgs = new ArrayList<String>();
        sql.append("SELECT DISTINCT uri FROM voc_").append(table).append(" AS vocTable");
        // filter by attribute
        if (attrs.size() > 0 || filterAttrs.size() > 0) {
            sql.append(", voc_").append(table).append("_attr AS attrTable WHERE vocTable.id=attrTable.id");
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
        LOG.debug("SQL: " + query);
        PreparedStatement ps = session.getConnection().prepareStatement(query);
        int i = 1;
        for (String arg : queryArgs) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("     param" + i + " = " + arg);
            }
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
    public IDListType fetchChildren(final QueryOperationsSession session, final String vocTableName, final String vocUri) throws SQLException, ImplementationExceptionResponse {
        IDListType children = new IDListType();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT uri FROM voc_").append(vocTableName).append(
                " AS vocTable WHERE vocTable.uri LIKE ?;");
        String query = sql.toString();
        PreparedStatement ps = session.getConnection().prepareStatement(query);
        String arg = vocUri + "_%";
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL: " + query);
            LOG.debug("     param1 = " + arg);
        }
        ps.setString(1, arg);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            children.getId().add(rs.getString("uri"));
        }
        return (children.getId().isEmpty()) ? null : children;
    }

    /**
     * Retrieves all vocabulary table names for the given vocabulary names.
     * 
     * @param uris
     *            A possibly empty array of vocabulary names.
     * @return A mapping from the table name to the vocabulary name.
     * @throws SQLException
     *             If an error accessing the database occurred.
     * @throws ImplementationException
     *             If an error converting a String to an URI occurred.
     */
    public Map<String, String> fetchVocabularyTableNames(final QueryOperationsSession session, final List<String> uris)
            throws SQLException, ImplementationExceptionResponse {
        Map<String, String> tableNames = new HashMap<String, String>();

        List<String> queryArgs = new ArrayList<String>(uris.size());
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT table_name, uri FROM Vocabularies");
        if (uris.size() > 0) {
            sql.append(" WHERE uri IN (");
            listOfStringToSql(uris, sql, queryArgs);
            sql.append(")");
        }
        sql.append(";");
        String query = sql.toString();
        LOG.debug("SQL: " + query);
        PreparedStatement ps = session.getConnection().prepareStatement(query);
        int i = 1;
        for (String arg : queryArgs) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("     param" + i + " = " + arg);
            }
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
     * Retrieves a list of business transactions (an instance of
     * BusinessTransactionListType) from the given result set.
     * 
     * @param rs
     *            The result of the SQL query.
     * @return A List of qualified XML elements
     * @throws SQLException
     *             If a database access error occurred.
     */
    private BusinessTransactionListType getBizTransactionsFromResult(final ResultSet rs) throws SQLException,
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
    private EPCListType getEpcsFromResult(final ResultSet rs) throws SQLException {
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
    private void fillExtensionsFromResult(final ResultSet rs, final List extensions) throws SQLException {
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
        if (strings == null || strings.isEmpty() || sql == null || queryArgs == null) {
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
     * Logs an SQL statement where the question marks are replaced with their
     * actual parameter values.
     * 
     * @param sqlTemplate
     *            The SQL query including the question marks.
     * @param parameterValues
     *            The parameter values to be substituted for the question marks.
     */
    private void logPreparedStatement(String sqlTemplate, List<?> parameterValues) {
        if (sqlTemplate != null) {
            String[] parts = sqlTemplate.split("\\?");
            StringBuffer buf = new StringBuffer();
            if (parts.length == parameterValues.size() || parts.length - 1 == parameterValues.size()) {
                for (int i = 0; i < parameterValues.size(); i++) {
                    buf.append(parts[i]);
                    buf.append(parameterValues.get(i).toString());
                }
                if (parts.length - 1 == parameterValues.size()) {
                    // we have one more part to append at the end
                    buf.append(parts[parts.length - 1]);
                }
            } else {
                // number of parameter values does not match number of question
                // marks! will result in an SQL error when the query is
                // executed! trace a note ...
                LOG.trace("Number missmatch when trying to log a prepared statement: number of parameter values does not match number of question marks in the query string");
            }
            LOG.debug("SQL: " + buf.toString());
        }
    }

    /**
     * TODO: javadoc
     * <p>
     * From this class, the actual SQL query for an EPCIS event will be
     * assembled. E.g. the following EventSqlParameter: columnName="eventTime",
     * sqlOp=">=", sqlExpr="?" should translate into the following SQL query
     * substring: " AND (eventTime >= ?)" and should be appended to the SQL
     * event query base string.
     * 
     * @author Marco Steybe
     */
    public static class EventQueryParameter {
        private String eventField;
        private String comparator;
        private Object value;

        /**
         * @param eventField
         * @param comparator
         * @param value
         */
        public EventQueryParameter(String eventField, String comparator, Object value) {
            this.eventField = eventField;
            this.comparator = comparator;
            this.value = value;
        }

        /**
         * @return the eventField
         */
        public String getEventField() {
            return eventField;
        }

        /**
         * @return the comparator
         */
        public String getComparator() {
            return comparator;
        }

        /**
         * @return the value
         */
        public Object getValue() {
            return value;
        }
    }

    /**
     * Opens a new session for the database transaction.
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
