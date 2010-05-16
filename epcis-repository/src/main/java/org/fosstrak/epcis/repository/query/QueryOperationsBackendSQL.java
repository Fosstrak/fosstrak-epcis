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

import org.fosstrak.epcis.model.ActionType;
import org.fosstrak.epcis.model.AggregationEventType;
import org.fosstrak.epcis.model.AttributeType;
import org.fosstrak.epcis.model.BusinessLocationType;
import org.fosstrak.epcis.model.BusinessTransactionListType;
import org.fosstrak.epcis.model.BusinessTransactionType;
import org.fosstrak.epcis.model.EPC;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.EPCListType;
import org.fosstrak.epcis.model.IDListType;
import org.fosstrak.epcis.model.ImplementationException;
import org.fosstrak.epcis.model.ImplementationExceptionSeverity;
import org.fosstrak.epcis.model.ObjectEventType;
import org.fosstrak.epcis.model.QuantityEventType;
import org.fosstrak.epcis.model.QueryParams;
import org.fosstrak.epcis.model.QueryTooLargeException;
import org.fosstrak.epcis.model.ReadPointType;
import org.fosstrak.epcis.model.SubscriptionControls;
import org.fosstrak.epcis.model.TransactionEventType;
import org.fosstrak.epcis.model.VocabularyElementListType;
import org.fosstrak.epcis.model.VocabularyElementType;
import org.fosstrak.epcis.model.VocabularyType;
import org.fosstrak.epcis.repository.EpcisConstants;
import org.fosstrak.epcis.repository.query.SimpleEventQueryDTO.EventQueryParam;
import org.fosstrak.epcis.repository.query.SimpleEventQueryDTO.Operation;
import org.fosstrak.epcis.soap.ImplementationExceptionResponse;
import org.fosstrak.epcis.soap.QueryTooLargeExceptionResponse;
import org.fosstrak.epcis.utils.TimeParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The QueryOperationsBackendSQL uses basic SQL statements (actually
 * <code>PreparedStatement</code>s) to implement the QueryOperationsBackend
 * interface.
 * 
 * @author Marco Steybe
 */
public class QueryOperationsBackendSQL implements QueryOperationsBackend {

    private static final Log LOG = LogFactory.getLog(QueryOperationsBackendSQL.class);

    private static final String SQL_SELECT_FROM_AGGREGATIONEVENT = "SELECT DISTINCT event_AggregationEvent.id, eventTime, recordTime, eventTimeZoneOffset, readPoint.uri AS readPoint, bizLocation.uri AS bizLocation, bizStep.uri AS bizStep, disposition.uri AS disposition, action, parentID FROM event_AggregationEvent LEFT JOIN voc_BizStep AS bizStep ON event_AggregationEvent.bizStep=bizStep.id LEFT JOIN voc_Disposition AS disposition ON event_AggregationEvent.disposition=disposition.id LEFT JOIN voc_ReadPoint AS readPoint ON event_AggregationEvent.readPoint=readPoint.id LEFT JOIN voc_BizLoc AS bizLocation ON event_AggregationEvent.bizLocation=bizLocation.id";
    private static final String SQL_SELECT_FROM_OBJECTEVENT = "SELECT DISTINCT event_ObjectEvent.id, eventTime, recordTime, eventTimeZoneOffset, readPoint.uri AS readPoint, bizLocation.uri AS bizLocation, bizStep.uri AS bizStep, disposition.uri AS disposition, action FROM event_ObjectEvent LEFT JOIN voc_BizStep AS bizStep ON event_ObjectEvent.bizStep=bizStep.id LEFT JOIN voc_Disposition AS disposition ON event_ObjectEvent.disposition=disposition.id LEFT JOIN voc_ReadPoint AS readPoint ON event_ObjectEvent.readPoint=readPoint.id LEFT JOIN voc_BizLoc AS bizLocation ON event_ObjectEvent.bizLocation=bizLocation.id";
    private static final String SQL_SELECT_FROM_QUANTITYEVENT = "SELECT DISTINCT event_QuantityEvent.id, eventTime, recordTime, eventTimeZoneOffset, readPoint.uri AS readPoint, bizLocation.uri AS bizLocation, bizStep.uri AS bizStep, disposition.uri AS disposition, epcClass.uri AS epcClass, quantity FROM event_QuantityEvent LEFT JOIN voc_BizStep AS bizStep ON event_QuantityEvent.bizStep=bizStep.id LEFT JOIN voc_Disposition AS disposition ON event_QuantityEvent.disposition=disposition.id LEFT JOIN voc_ReadPoint AS readPoint ON event_QuantityEvent.readPoint=readPoint.id LEFT JOIN voc_BizLoc AS bizLocation ON event_QuantityEvent.bizLocation=bizLocation.id LEFT JOIN voc_EPCClass AS epcClass ON event_QuantityEvent.epcClass=epcClass.id";
    private static final String SQL_SELECT_FROM_TRANSACTIONEVENT = "SELECT DISTINCT event_TransactionEvent.id, eventTime, recordTime, eventTimeZoneOffset, readPoint.uri AS readPoint, bizLocation.uri AS bizLocation, bizStep.uri AS bizStep, disposition.uri AS disposition, action, parentID FROM event_TransactionEvent LEFT JOIN voc_BizStep AS bizStep ON event_TransactionEvent.bizStep=bizStep.id LEFT JOIN voc_Disposition AS disposition ON event_TransactionEvent.disposition=disposition.id LEFT JOIN voc_ReadPoint AS readPoint ON event_TransactionEvent.readPoint=readPoint.id LEFT JOIN voc_BizLoc AS bizLocation ON event_TransactionEvent.bizLocation=bizLocation.id";

    private static final String SQL_SELECT_AGGREGATIONEVENT_EXTENSIONS = "SELECT ext.fieldname, ext.prefix, ext.intValue, ext.floatValue, ext.dateValue, ext.strValue FROM event_AggregationEvent_extensions AS ext WHERE ext.event_id=?";
    private static final String SQL_SELECT_OBJECTEVENT_EXTENSIONS = "SELECT ext.fieldname, ext.prefix, ext.intValue, ext.floatValue, ext.dateValue, ext.strValue FROM event_ObjectEvent_extensions AS ext WHERE event_id=?";
    private static final String SQL_SELECT_QUANTITYEVENT_EXTENSIONS = "SELECT ext.fieldname, ext.prefix, ext.intValue, ext.floatValue, ext.dateValue, ext.strValue FROM event_QuantityEvent_extensions AS ext WHERE event_id=?";
    private static final String SQL_SELECT_TRANSACTIONEVENT_EXTENSIONS = "SELECT ext.fieldname, ext.prefix, ext.intValue, ext.floatValue, ext.dateValue, ext.strValue FROM event_TransactionEvent_extensions AS ext WHERE event_id=?";

    private static final String SQL_SELECT_AGGREGATIONEVENT_BIZTRANS = "SELECT bizTrans.uri AS bizTrans, bizTransType.uri AS bizTransType FROM event_AggregationEvent_bizTrans AS eventBizTrans JOIN BizTransaction ON eventBizTrans.bizTrans_id=BizTransaction.id JOIN voc_BizTrans AS bizTrans ON BizTransaction.bizTrans=bizTrans.id JOIN voc_BizTransType AS bizTransType ON BizTransaction.type=bizTransType.id WHERE eventBizTrans.event_id=?";
    private static final String SQL_SELECT_OBJECTEVENT_BIZTRANS = "SELECT bizTrans.uri AS bizTrans, bizTransType.uri AS bizTransType FROM event_ObjectEvent_bizTrans AS eventBizTrans JOIN BizTransaction ON eventBizTrans.bizTrans_id=BizTransaction.id JOIN voc_BizTrans AS bizTrans ON BizTransaction.bizTrans=bizTrans.id JOIN voc_BizTransType AS bizTransType ON BizTransaction.type=bizTransType.id WHERE eventBizTrans.event_id=?";
    private static final String SQL_SELECT_QUANTITYEVENT_BIZTRANS = "SELECT bizTrans.uri AS bizTrans, bizTransType.uri AS bizTransType FROM event_QuantityEvent_bizTrans AS eventBizTrans JOIN BizTransaction ON eventBizTrans.bizTrans_id=BizTransaction.id JOIN voc_BizTrans AS bizTrans ON BizTransaction.bizTrans=bizTrans.id JOIN voc_BizTransType AS bizTransType ON BizTransaction.type=bizTransType.id WHERE eventBizTrans.event_id=?";
    private static final String SQL_SELECT_TRANSACTIONEVENT_BIZTRANS = "SELECT bizTrans.uri AS bizTrans, bizTransType.uri AS bizTransType FROM event_TransactionEvent_bizTrans AS eventBizTrans JOIN BizTransaction ON eventBizTrans.bizTrans_id=BizTransaction.id JOIN voc_BizTrans AS bizTrans ON BizTransaction.bizTrans=bizTrans.id JOIN voc_BizTransType AS bizTransType ON BizTransaction.type=bizTransType.id WHERE eventBizTrans.event_id=?";

    private static final String SQL_SELECT_AGGREGATIONEVENT_EPCS = "SELECT epc FROM event_AggregationEvent_EPCs WHERE event_id=?";
    private static final String SQL_SELECT_OBJECTEVENT_EPCS = "SELECT epc FROM event_ObjectEvent_EPCs WHERE event_id=?";
    private static final String SQL_SELECT_QUANTITYEVENT_EPCS = "SELECT epc FROM event_QuantityEvent_EPCs WHERE event_id=?";
    private static final String SQL_SELECT_TRANSACTIONEVENT_EPCS = "SELECT epc FROM event_TransactionEvent_EPCs WHERE event_id=?";

    private static final String SQL_EXISTS_SUBSCRIPTION = "SELECT EXISTS (SELECT subscriptionid FROM subscription WHERE subscriptionid=?)";

    private static Map<String, String> attributeTablenameMap;
    private static Map<String, String> vocabularyTablenameMap;
    private static Map<String, String> vocabularyTypeMap;

    private static Map<Operation, String> operationMap;

    static {
        attributeTablenameMap = new HashMap<String, String>(7);
        attributeTablenameMap.put("bizLocation.attribute", "voc_BizLoc_attr");
        attributeTablenameMap.put("bizStep.attribute", "voc_BizStep_attr");
        attributeTablenameMap.put("bizTransType.attribute", "voc_BizTransType_attr");
        attributeTablenameMap.put("bizTrans.attribute", "voc_BizTrans_attr");
        attributeTablenameMap.put("disposition.attribute", "voc_Disposition_attr");
        attributeTablenameMap.put("readPoint.attribute", "voc_ReadPoint_attr");
        attributeTablenameMap.put("epcClass.attribute", "voc_EPCClass_attr");

        vocabularyTablenameMap = new HashMap<String, String>(5);
        vocabularyTablenameMap.put(EpcisConstants.BUSINESS_STEP_ID, "voc_BizStep");
        vocabularyTablenameMap.put(EpcisConstants.BUSINESS_LOCATION_ID, "voc_BizLoc");
        vocabularyTablenameMap.put(EpcisConstants.BUSINESS_TRANSACTION_ID, "voc_BizTrans");
        vocabularyTablenameMap.put(EpcisConstants.BUSINESS_TRANSACTION_TYPE_ID, "voc_BizTransType");
        vocabularyTablenameMap.put(EpcisConstants.DISPOSITION_ID, "voc_Disposition");
        vocabularyTablenameMap.put(EpcisConstants.EPC_CLASS_ID, "voc_EPCClass");
        vocabularyTablenameMap.put(EpcisConstants.READ_POINT_ID, "voc_ReadPoint");

        vocabularyTypeMap = new HashMap<String, String>(7);
        vocabularyTypeMap.put("bizLocation", "bizLocation.uri");
        vocabularyTypeMap.put("bizStep", "bizStep.uri");
        vocabularyTypeMap.put("bizTransType", "bizTransType.uri");
        vocabularyTypeMap.put("bizTrans", "bizTrans.uri");
        vocabularyTypeMap.put("disposition", "disposition.uri");
        vocabularyTypeMap.put("readPoint", "readPoint.uri");
        vocabularyTypeMap.put("epcClass", "epcClass.uri");

        operationMap = new HashMap<Operation, String>(9);
        operationMap.put(Operation.EQ, "=");
        operationMap.put(Operation.GE, ">=");
        operationMap.put(Operation.LE, "<=");
        operationMap.put(Operation.GT, ">");
        operationMap.put(Operation.LT, "<");
        operationMap.put(Operation.MATCH, "LIKE");
        operationMap.put(Operation.WD, "LIKE");
        operationMap.put(Operation.EQATTR, "=");
        operationMap.put(Operation.HASATTR, "=");
    }

    private PreparedStatement prepareSimpleEventQuery(final QueryOperationsSession session, SimpleEventQueryDTO seQuery)
            throws SQLException, ImplementationExceptionResponse {

        StringBuilder sqlSelectFrom;
        StringBuilder sqlWhereClause = new StringBuilder(" WHERE 1");
        List<Object> sqlParams = new ArrayList<Object>();

        String eventType = seQuery.getEventType();
        if (EpcisConstants.AGGREGATION_EVENT.equals(eventType)) {
            sqlSelectFrom = new StringBuilder(SQL_SELECT_FROM_AGGREGATIONEVENT);
        } else if (EpcisConstants.OBJECT_EVENT.equals(eventType)) {
            sqlSelectFrom = new StringBuilder(SQL_SELECT_FROM_OBJECTEVENT);
        } else if (EpcisConstants.QUANTITY_EVENT.equals(eventType)) {
            sqlSelectFrom = new StringBuilder(SQL_SELECT_FROM_QUANTITYEVENT);
        } else if (EpcisConstants.TRANSACTION_EVENT.equals(eventType)) {
            sqlSelectFrom = new StringBuilder(SQL_SELECT_FROM_TRANSACTIONEVENT);
        } else {
            String msg = "Unknown event type: " + eventType;
            LOG.error(msg);
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            throw new ImplementationExceptionResponse(msg, ie);
        }

        boolean joinedEpcs = false;
        boolean joinedBizTransacitions = false;

        // construct the SQL query dynamically
        List<EventQueryParam> eventQueryParams = seQuery.getEventQueryParams();
        int nofEventFieldExtensions = 0;
        for (EventQueryParam queryParam : eventQueryParams) {
            String eventField = queryParam.getEventField();
            Operation op = queryParam.getOp();
            Object value = queryParam.getValue();

            // check if we need to do any JOINs
            if ("epcList".equals(eventField) || "childEPCs".equals(eventField) || "anyEPC".equals(eventField)) {
                // we have a query on EPCs, so we need to join the appropriate
                // "_EPCs" table
                if (!joinedEpcs) {
                    sqlSelectFrom.append(" JOIN event_").append(eventType).append("_EPCs AS epc");
                    sqlSelectFrom.append(" ON event_").append(eventType).append(".id=epc.event_id");
                    joinedEpcs = true;
                }
                // update the event field to search in
                eventField = "epc.epc";
            } else if (eventField.startsWith("extension")) {
                // we have a query on an extension field, so we need to join the
                // appropriate "_extensions" table

                /*
                 * For every extension condition there are two EventQueryParams,
                 * one for the name of the parameter and another one for the
                 * value. Example: extension.intValue extension.fieldname
                 * Therefore, the JOINs will be created once from every two
                 * extension conditions (the even ones)
                 */
                nofEventFieldExtensions++;
                if (nofEventFieldExtensions % 2 == 0) {
                    sqlSelectFrom.append(" JOIN event_").append(eventType).append("_extensions AS extension").append(
                            nofEventFieldExtensions / 2);
                    sqlSelectFrom.append(" ON event_").append(eventType).append(".id=extension").append(
                            nofEventFieldExtensions / 2).append(".event_id");
                }
            } else if (eventField.startsWith("bizTrans")) {
                // we have a query on business transactions, so we need to join
                // the appropriate "_bizTrans" and "bizTransList" tables
                if (!joinedBizTransacitions) {
                    sqlSelectFrom.append(" JOIN event_").append(eventType).append("_bizTrans AS bizTransList");
                    sqlSelectFrom.append(" ON event_").append(eventType).append(".id=bizTransList.event_id");
                    sqlSelectFrom.append(" JOIN BizTransaction ON bizTransList.bizTrans_id=BizTransaction.id");
                    sqlSelectFrom.append(" JOIN voc_BizTrans AS bizTrans ON BizTransaction.bizTrans=bizTrans.id");
                    sqlSelectFrom.append(" JOIN voc_BizTransType AS bizTransType ON BizTransaction.type=bizTransType.id");
                    joinedBizTransacitions = true;
                }
                if ("bizTransList.bizTrans".equals(eventField)) {
                    eventField = "bizTrans";
                } else if ("bizTransList.type".equals(eventField)) {
                    eventField = "bizTransType";
                }
            } else if (eventField.endsWith(".attribute")) {
                String attrTable = attributeTablenameMap.get(eventField);
                if (attrTable != null) {
                    String vocAlias = eventField.substring(0, eventField.indexOf("."));
                    sqlSelectFrom.append(" JOIN ").append(attrTable);
                    sqlSelectFrom.append(" ON ").append(attrTable).append(".id=").append(vocAlias).append(".id");
                    eventField = attrTable + ".attribute";
                }
            } else if (eventField.endsWith(".attribute.value")) {
                String attrTable = attributeTablenameMap.get(eventField.substring(0, eventField.length() - 6));
                eventField = attrTable + ".value";
            }
            String vocField = vocabularyTypeMap.get(eventField);
            if (vocField != null) {
                eventField = vocField;
            }

            // now check the provided event field, operation, and value and
            // update the SQL strings accordingly
            if (value == null && op == Operation.EXISTS) {
                if (eventField.startsWith("epc") || eventField.startsWith("bizTransList")) {
                    // EXISTS-query already coped with by JOIN - nothing to do
                } else {
                    // check if the given event field exists
                    sqlWhereClause.append(" AND ?");
                    sqlParams.add(eventField);
                }
            } else if (value != null) {
                if (value instanceof List<?>) {
                    // we have a multi-value query parameter, e.g. action, EPCs,
                    // vocabulary types
                    List<?> paramValues = (List<?>) value;
                    if (!paramValues.isEmpty()) {
                        if (op == Operation.MATCH || op == Operation.WD) {
                            // this results in a SQL "LIKE" query
                            sqlWhereClause.append(" AND (0");
                            for (Object paramValue : paramValues) {
                                String strValue = (String) paramValue;

                                // MATCH-params might be 'pure identity' EPC
                                // patterns
                                if (op == Operation.MATCH && !eventField.startsWith("epcClass")) {
                                    if (strValue.startsWith("urn:epc:idpat:")) {
                                        strValue = strValue.replace("urn:epc:idpat:", "urn:epc:id:");
                                    }
                                }
                                strValue = strValue.replaceAll("\\*", "%");

                                sqlWhereClause.append(" OR ").append(eventField).append(" LIKE ?");
                                sqlParams.add(strValue);
                                if (seQuery.isAnyEpc() && "epc.epc".equals(eventField)) {
                                    sqlWhereClause.append(" OR parentID LIKE ?");
                                    sqlParams.add(strValue);
                                }
                            }
                            sqlWhereClause.append(")");
                        } else {
                            // this results in a SQL "IN" query
                            sqlWhereClause.append(" AND ").append(eventField).append(" IN (?");
                            sqlParams.add(paramValues.get(0));
                            for (int i = 1; i < paramValues.size(); i++) {
                                sqlWhereClause.append(",?");
                                sqlParams.add(paramValues.get(i));
                            }
                            sqlWhereClause.append(")");
                        }
                    }
                } else {
                    // we have a single-value parameter, e.g. eventTime,
                    // recordTime, parentID
                    String sqlOp = operationMap.get(op);
                    sqlWhereClause.append(" AND ").append(eventField).append(" ").append(sqlOp).append(" ?");
                    sqlParams.add(value);
                }
            }
        }

        // construct the final SQL query string
        StringBuilder sql = sqlSelectFrom.append(sqlWhereClause);
        if (seQuery.getOrderBy() != null) {
            sql.append(" ORDER BY ").append(seQuery.getOrderBy());
            if (seQuery.getOrderDirection() != null) {
                sql.append(" ").append(seQuery.getOrderDirection().name());
            }
        }
        if (seQuery.getLimit() != -1) {
            sql.append(" LIMIT ").append(seQuery.getLimit());
        } else if (seQuery.getMaxEventCount() != -1) {
            sql.append(" LIMIT ").append(seQuery.getMaxEventCount() + 1);
        }
        String sqlSelect = sql.toString();

        PreparedStatement selectEventsStmt = session.getConnection().prepareStatement(sqlSelect);
        LOG.debug("SQL: " + sqlSelect);
        for (int i = 0; i < sqlParams.size(); i++) {
            selectEventsStmt.setObject(i + 1, sqlParams.get(i));
            if (LOG.isDebugEnabled()) {
                LOG.debug("     param" + i + " = " + sqlParams.get(i));
            }
        }
        return selectEventsStmt;
    }

    /**
     * {@inheritDoc}
     */
    public void runSimpleEventQuery(final QueryOperationsSession session, final SimpleEventQueryDTO seQuery,
            final List<Object> eventList) throws SQLException, ImplementationExceptionResponse,
            QueryTooLargeExceptionResponse {
        PreparedStatement selectEventsStmt = prepareSimpleEventQuery(session, seQuery);
        ResultSet rs = selectEventsStmt.executeQuery();

        String eventType = seQuery.getEventType();

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
                String msg = "Unknown event type: " + eventType;
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
        int maxEventCount = seQuery.getMaxEventCount();
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

    private PreparedStatement prepareMasterDataQuery(final QueryOperationsSession session, String vocType,
            MasterDataQueryDTO mdQuery) throws SQLException {

        StringBuilder sqlSelectFrom = new StringBuilder("SELECT uri FROM");
        StringBuilder sqlWhereClause = new StringBuilder(" WHERE 1");
        List<Object> sqlParams = new ArrayList<Object>();

        // get the values from the query DTO
        List<String> attributeNames = mdQuery.getAttributeNames();
        Map<String, List<String>> attributeNameAndValues = mdQuery.getAttributeNameAndValues();
        List<String> vocabularyEqNames = mdQuery.getVocabularyEqNames();
        List<String> vocabularyWdNames = mdQuery.getVocabularyWdNames();

        boolean joinedAttribute = false;
        String vocTablename = getVocabularyTablename(vocType);
        sqlSelectFrom.append(" ").append(vocTablename).append(",");
        if ("voc_Any".equals(vocTablename)) {
            // this is not a standard vocabulary, we need to restrict by vtype
            // in the voc_Any table
            sqlWhereClause.append(" AND voc_Any.vtype=?");
            sqlParams.add(vocType);
        }

        // filter by attribute names
        if (attributeNames != null && !attributeNames.isEmpty()) {
            if (!joinedAttribute) {
                sqlSelectFrom.append(" ").append(vocTablename).append("_attr,");
                sqlWhereClause.append(" AND ").append(vocTablename).append(".id=");
                sqlWhereClause.append(vocTablename).append("_attr.id");
            }

            sqlWhereClause.append(" AND ").append(vocTablename).append("_attr.attribute IN (?");
            sqlParams.add(attributeNames.get(0));
            for (int i = 1; i < attributeNames.size(); i++) {
                sqlWhereClause.append(",?");
                sqlParams.add(attributeNames.get(i));
            }
            sqlWhereClause.append(")");
        }

        // filter by attribute names and values
        if (attributeNameAndValues != null && !attributeNameAndValues.isEmpty()) {
            if (!joinedAttribute) {
                sqlSelectFrom.append(" ").append(vocTablename).append("_attr,");
                sqlWhereClause.append(" AND ").append(vocTablename).append(".id=");
                sqlWhereClause.append(vocTablename).append("_attr.id");
            }
            for (String attrName : attributeNameAndValues.keySet()) {
                sqlWhereClause.append(" AND ").append(vocTablename).append("_attr.attribute=?");
                sqlParams.add(attrName);
                sqlWhereClause.append(" AND ").append(vocTablename).append("_attr.value IN (?");
                List<String> attrValues = attributeNameAndValues.get(attrName);
                sqlParams.add(attrValues.get(0));
                for (int i = 1; i < attrValues.size(); i++) {
                    sqlWhereClause.append(",?");
                    sqlParams.add(attrValues.get(i));
                }
                sqlWhereClause.append(")");
            }
        }

        // filter by vocabulary names
        if (vocabularyEqNames != null && !vocabularyEqNames.isEmpty()) {
            sqlWhereClause.append(" AND ").append(vocTablename).append(".uri IN (?");
            sqlParams.add(vocabularyEqNames.get(0));
            for (int i = 1; i < vocabularyEqNames.size(); i++) {
                sqlWhereClause.append(",?");
                sqlParams.add(vocabularyEqNames.get(i));
            }
            sqlWhereClause.append(")");
        }
        if (vocabularyWdNames != null && !vocabularyWdNames.isEmpty()) {
            sqlWhereClause.append(" AND (0");
            for (String vocWdName : vocabularyWdNames) {
                sqlWhereClause.append(" OR ").append(vocTablename).append(".uri LIKE ?");
                sqlParams.add(vocWdName + "%");
            }
            sqlWhereClause.append(")");
        }

        // remove last comma
        sqlSelectFrom.delete(sqlSelectFrom.length() - 1, sqlSelectFrom.length());

        // set the complete query and pass it back to the caller
        String sqlSelect = sqlSelectFrom.append(sqlWhereClause).toString();

        PreparedStatement ps = session.getConnection().prepareStatement(sqlSelect);
        LOG.debug("SQL: " + sqlSelect);
        for (int i = 0; i < sqlParams.size(); i++) {
            ps.setObject(i + 1, sqlParams.get(i));
            if (LOG.isDebugEnabled()) {
                LOG.debug("     param" + i + " = " + sqlParams.get(i));
            }
        }
        return ps;
    }

    /**
     * {@inheritDoc}
     */
    public void runMasterDataQuery(final QueryOperationsSession session, final MasterDataQueryDTO mdQuery,
            final List<VocabularyType> vocList) throws SQLException, ImplementationExceptionResponse,
            QueryTooLargeExceptionResponse {
        // create and run a separate query for each vocabulary
        List<String> vocabularyTypes = mdQuery.getVocabularyTypes();
        for (String vocType : vocabularyTypes) {
            PreparedStatement ps = prepareMasterDataQuery(session, vocType, mdQuery);
            ResultSet rs = ps.executeQuery();

            int maxElementCount = mdQuery.getMaxElementCount();
            boolean includeAttributes = mdQuery.getIncludeAttributes();
            boolean includeChildren = mdQuery.getIncludeChildren();

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
     * {@inheritDoc}
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
        String vocTablename = getVocabularyTablename(vocType);
        StringBuilder sql = new StringBuilder();
        List<Object> sqlParams = new ArrayList<Object>();
        sql.append("SELECT attribute, value FROM ").append(vocTablename).append(" AS voc, ");
        sql.append(vocTablename).append("_attr AS attr WHERE voc.id=attr.id AND voc.uri=?");
        sqlParams.add(vocUri);
        if ("voc_Any".equals(vocTablename)) {
            sql.append(" AND voc.vtype=?");
            sqlParams.add(vocType);
        }
        if (filterAttrNames != null && !filterAttrNames.isEmpty()) {
            // filter by attribute names
            sql.append(" AND attribute IN (?");
            sqlParams.add(filterAttrNames.get(0));
            for (int i = 1; i < filterAttrNames.size(); i++) {
                sql.append(",?");
                sqlParams.add(filterAttrNames.get(i));
            }
            sql.append(")");
        }
        PreparedStatement ps = session.getPreparedStatement(sql.toString());
        LOG.debug("SQL: " + sql.toString());
        for (int i = 0; i < sqlParams.size(); i++) {
            ps.setObject(i + 1, sqlParams.get(i));
            if (LOG.isDebugEnabled()) {
                LOG.debug("     param" + i + " = " + sqlParams.get(i));
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
        String vocTablename = getVocabularyTablename(vocType);
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
     * {@inheritDoc}
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
                QueryParams params = (QueryParams) in.readObject();

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
     * {@inheritDoc}
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
            session.commit();
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
     * {@inheritDoc}
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
        session.commit();
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
     * {@inheritDoc}
     */
    public QueryOperationsSession openSession(final DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        LOG.debug("Database connection for session established");
        return new QueryOperationsSession(connection);
    }

    protected String getVocabularyTablename(String vocTypeId) {
        if (vocTypeId == null || "".equals(vocTypeId)) {
            return null;
        }
        String tablename = vocabularyTablenameMap.get(vocTypeId);
        if (tablename == null) {
            return "voc_Any";
        }
        return tablename;
    }
}
