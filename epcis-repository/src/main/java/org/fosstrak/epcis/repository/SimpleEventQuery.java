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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.accada.epcis.repository.QueryOperationsModule.EventType;

/**
 * TODO: javadoc
 * 
 * @author Marco Steybe
 */
public class SimpleEventQuery {

    private static final String SQL_SELECT_FROM_AGGREGATIONEVENT = "SELECT DISTINCT event_AggregationEvent.id, eventTime, recordTime, eventTimeZoneOffset, readPoint.uri AS readPoint, bizLocation.uri AS bizLocation, bizStep.uri AS bizStep, disposition.uri AS disposition, action, parentID FROM event_AggregationEvent LEFT JOIN voc_BizStep AS bizStep ON event_AggregationEvent.bizStep=bizStep.id LEFT JOIN voc_Disposition AS disposition ON event_AggregationEvent.disposition=disposition.id LEFT JOIN voc_ReadPoint AS readPoint ON event_AggregationEvent.readPoint=readPoint.id LEFT JOIN voc_BizLoc AS bizLocation ON event_AggregationEvent.bizLocation=bizLocation.id";
    private static final String SQL_SELECT_FROM_OBJECTEVENT = "SELECT DISTINCT event_ObjectEvent.id, eventTime, recordTime, eventTimeZoneOffset, readPoint.uri AS readPoint, bizLocation.uri AS bizLocation, bizStep.uri AS bizStep, disposition.uri AS disposition, action FROM event_ObjectEvent LEFT JOIN voc_BizStep AS bizStep ON event_ObjectEvent.bizStep=bizStep.id LEFT JOIN voc_Disposition AS disposition ON event_ObjectEvent.disposition=disposition.id LEFT JOIN voc_ReadPoint AS readPoint ON event_ObjectEvent.readPoint=readPoint.id LEFT JOIN voc_BizLoc AS bizLocation ON event_ObjectEvent.bizLocation=bizLocation.id";
    private static final String SQL_SELECT_FROM_QUANTITYEVENT = "SELECT DISTINCT event_QuantityEvent.id, eventTime, recordTime, eventTimeZoneOffset, readPoint.uri AS readPoint, bizLocation.uri AS bizLocation, bizStep.uri AS bizStep, disposition.uri AS disposition, epcClass.uri AS epcClass, quantity FROM event_QuantityEvent LEFT JOIN voc_BizStep AS bizStep ON event_QuantityEvent.bizStep=bizStep.id LEFT JOIN voc_Disposition AS disposition ON event_QuantityEvent.disposition=disposition.id LEFT JOIN voc_ReadPoint AS readPoint ON event_QuantityEvent.readPoint=readPoint.id LEFT JOIN voc_BizLoc AS bizLocation ON event_QuantityEvent.bizLocation=bizLocation.id LEFT JOIN voc_EPCClass AS epcClass ON event_QuantityEvent.epcClass=epcClass.id";
    private static final String SQL_SELECT_FROM_TRANSACTIONEVENT = "SELECT DISTINCT event_TransactionEvent.id, eventTime, recordTime, eventTimeZoneOffset, readPoint.uri AS readPoint, bizLocation.uri AS bizLocation, bizStep.uri AS bizStep, disposition.uri AS disposition, action, parentID FROM event_TransactionEvent LEFT JOIN voc_BizStep AS bizStep ON event_TransactionEvent.bizStep=bizStep.id LEFT JOIN voc_Disposition AS disposition ON event_TransactionEvent.disposition=disposition.id LEFT JOIN voc_ReadPoint AS readPoint ON event_TransactionEvent.readPoint=readPoint.id LEFT JOIN voc_BizLoc AS bizLocation ON event_TransactionEvent.bizLocation=bizLocation.id";

    private static final String SQL_WHERE_BASE = " WHERE 1";

    private EventType eventType;
    private StringBuilder sqlSelectFrom;
    private StringBuilder sqlWhereClause;
    private StringBuilder sqlOrderAndLimit;
    private List<Object> sqlQueryParams;
    private int maxEventCount = -1;

    private boolean joinedEpcs;
    private boolean joinedExtensions;
    private boolean joinedBizTransacitions;

    private static Map<String, String> attributeTablenameMap = new HashMap<String, String>(7);
    private static Map<String, String> vocabularyTypeMap = new HashMap<String, String>(7);

    private static Map<Operation, String> operationMap = new HashMap<Operation, String>();

    static {
        attributeTablenameMap.put("bizLocation.attribute", "voc_BizLoc_attr");
        attributeTablenameMap.put("bizStep.attribute", "voc_BizStep_attr");
        attributeTablenameMap.put("bizTransType.attribute", "voc_BizTransType_attr");
        attributeTablenameMap.put("bizTrans.attribute", "voc_BizTrans_attr");
        attributeTablenameMap.put("disposition.attribute", "voc_Disposition_attr");
        attributeTablenameMap.put("readPoint.attribute", "voc_ReadPoint_attr");
        attributeTablenameMap.put("epcClass.attribute", "voc_EPCClass_attr");
        
        vocabularyTypeMap.put("bizLocation", "bizLocation.uri");
        vocabularyTypeMap.put("bizStep", "bizStep.uri");
        vocabularyTypeMap.put("bizTransType", "bizTransType.uri");
        vocabularyTypeMap.put("bizTrans", "bizTrans.uri");
        vocabularyTypeMap.put("disposition", "disposition.uri");
        vocabularyTypeMap.put("readPoint", "readPoint.uri");
        vocabularyTypeMap.put("epcClass", "epcClass.uri");

        operationMap.put(Operation.EQ, "=");
    }

    public enum Operation {
        EQ, LT, GT, LE, GE, HASATTR, EQATTR, MATCH
    }

    public SimpleEventQuery(EventType eventType) {
        this.eventType = eventType;
        resetQuery();
    }

    public void addEventQueryParam(String eventField, String comparator, Object value) {
        // handle "special cases" first
        if ("epcList".equals(eventField) || "childEPCs".equals(eventField)) {
            if (!joinedEpcs) {
                sqlSelectFrom.append(" JOIN event_").append(eventType.name()).append("_EPCs AS epc");
                sqlSelectFrom.append(" ON event_").append(eventType.name()).append(".id=epc.event_id");
                joinedEpcs = true;
            }
            eventField = "epc.epc";
        } else if (eventField.startsWith("extension")) {
            if (!joinedExtensions) {
                sqlSelectFrom.append(" JOIN event_").append(eventType.name()).append("_extensions AS extension");
                sqlSelectFrom.append(" ON event_").append(eventType.name()).append(".id=extension.event_id");
                joinedExtensions = true;
            }
        } else if (eventField.startsWith("bizTrans")) {
            if (!joinedBizTransacitions) {
                sqlSelectFrom.append(" JOIN event_").append(eventType.name()).append("_bizTrans AS bizTransList");
                sqlSelectFrom.append(" ON event_").append(eventType.name()).append(".id=bizTransList.event_id");
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

        if (value == null && "EXISTS".equals(comparator)) {
            if (eventField.startsWith("epc") || eventField.startsWith("bizTransList")) {
                // EXISTS-query already coped with by JOIN - nothing to do
            } else {
                // check if the given event field exists
                sqlWhereClause.append(" AND ?");
                sqlQueryParams.add(eventField);
            }
        } else if (value instanceof List) {
            // multi-value query parameter, e.g. "action", vocabulary types
            List<Object> paramValues = (List<Object>) value;
            if ("LIKE".equals(comparator) && !paramValues.isEmpty()) {
                List<String> stringValues = (List<String>) value;
                sqlWhereClause.append(" AND (0");
                for (String val : stringValues) {
                    sqlWhereClause.append(" OR ").append(eventField).append(" LIKE ?");
                    sqlQueryParams.add(val.replaceAll("\\*", "%"));
                }
                sqlWhereClause.append(")");
            } else if ("IN".equals(comparator) && !paramValues.isEmpty()) {
                sqlWhereClause.append(" AND ").append(eventField).append(" IN (?");
                sqlQueryParams.add(paramValues.get(0));
                for (int i = 1; i < paramValues.size(); i++) {
                    sqlWhereClause.append(",?");
                    sqlQueryParams.add(paramValues.get(i));
                }
                sqlWhereClause.append(")");
            }
        } else {
            // single-value parameter, e.g. eventTime, recordTime, parentID
            sqlWhereClause.append(" AND ").append(eventField).append(" ").append(comparator).append(" ?");
            Object val = value;
            if ("LIKE".equals(comparator)) {
                val = ((String) value).replaceAll("\\*", "%");
            }
            sqlQueryParams.add(val);
        }

    }

    public void addOrdering(String orderBy, String orderDirection) {
        sqlOrderAndLimit.append(" ORDER BY ").append(orderBy);
        if (orderDirection != null) {
            sqlOrderAndLimit.append(" ").append(orderDirection);
        }
    }

    public void addLimit(int limit) {
        sqlOrderAndLimit.append(" LIMIT ").append(limit);
    }

    public void setMaxEventCount(int maxEventCount) {
        this.maxEventCount = maxEventCount;
        addLimit(maxEventCount + 1);
    }

    public int getMaxEventCount() {
        return maxEventCount;
    }

    public void resetQuery() {
        switch (eventType) {
        case AggregationEvent:
            sqlSelectFrom = new StringBuilder(SQL_SELECT_FROM_AGGREGATIONEVENT);
            break;
        case ObjectEvent:
            sqlSelectFrom = new StringBuilder(SQL_SELECT_FROM_OBJECTEVENT);
            break;
        case QuantityEvent:
            sqlSelectFrom = new StringBuilder(SQL_SELECT_FROM_QUANTITYEVENT);
            break;
        case TransactionEvent:
            sqlSelectFrom = new StringBuilder(SQL_SELECT_FROM_TRANSACTIONEVENT);
            break;
        }
        sqlWhereClause = new StringBuilder(SQL_WHERE_BASE);
        sqlQueryParams = new ArrayList<Object>(4);
        sqlOrderAndLimit = new StringBuilder();
        joinedEpcs = false;
        joinedExtensions = false;
        joinedBizTransacitions = false;
        maxEventCount = -1;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getSqlString() {
        return sqlSelectFrom.append(sqlWhereClause).append(sqlOrderAndLimit).toString();
    }

    public List<Object> getSqlParams() {
        return sqlQueryParams;
    }
}
