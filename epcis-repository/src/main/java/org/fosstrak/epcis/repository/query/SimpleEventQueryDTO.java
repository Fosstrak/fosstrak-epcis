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

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: javadoc
 * 
 * @author Marco Steybe
 */
public class SimpleEventQueryDTO {

    private List<EventQueryParam> eventQueryParams;

    protected String eventType;
    private int maxEventCount = -1;
    private int limit = -1;
    private String orderBy = null;
    private OrderDirection orderDirection = null;
    private boolean isAnyEpc = false;

    public enum OrderDirection {
        ASC, DESC
    }

    public enum Operation {
        EQ, LT, GT, LE, GE, HASATTR, EQATTR, MATCH, WD, EXISTS
    }

    public SimpleEventQueryDTO(String eventType) {
        this.eventType = eventType;
        resetQuery();
    }

    public void addEventQueryParam(String eventField, Operation op, Object value) {
        eventQueryParams.add(new EventQueryParam(eventField, op, value));
    }

    public void addEventQueryParam(EventQueryParam queryParam) {
        eventQueryParams.add(queryParam);
    }

    public String getEventType() {
        return eventType;
    }

    public int getLimit() {
        return limit;
    }

    public int getMaxEventCount() {
        return maxEventCount;
    }

    public List<EventQueryParam> getEventQueryParams() {
        return eventQueryParams;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    public boolean isAnyEpc() {
        return isAnyEpc;
    }

    public void setIsAnyEpc(boolean isAnyEpc) {
        this.isAnyEpc = isAnyEpc;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setMaxEventCount(int maxEventCount) {
        this.maxEventCount = maxEventCount;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public void setOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    public void resetQuery() {
        eventQueryParams = new ArrayList<EventQueryParam>();
        maxEventCount = -1;
        limit = -1;
        orderBy = null;
        orderDirection = null;
        isAnyEpc = false;
    }

    public static class EventQueryParam {
        private String eventField;
        private Operation op;
        private Object value;

        public EventQueryParam() {
        }

        public EventQueryParam(String eventField, Operation op, Object value) {
            this.eventField = eventField;
            this.op = op;
            this.value = value;
        }

        public String getEventField() {
            return eventField;
        }

        public void setEventField(String eventField) {
            this.eventField = eventField;
        }

        public Operation getOp() {
            return op;
        }

        public void setOp(Operation op) {
            this.op = op;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
