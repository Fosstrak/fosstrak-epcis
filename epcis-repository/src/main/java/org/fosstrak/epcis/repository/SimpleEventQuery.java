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

/**
 * TODO: javadoc
 * 
 * @author Marco Steybe
 */
public abstract class SimpleEventQuery {

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

    public SimpleEventQuery(String eventType) {
        this.eventType = eventType;
        resetQuery();
    }

    public abstract void addEventQueryParam(String eventField, Operation op, Object value);

    public String getEventType() {
        return eventType;
    }

    public int getLimit() {
        return limit;
    }

    public int getMaxEventCount() {
        return maxEventCount;
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
        maxEventCount = -1;
        limit = -1;
        orderBy = null;
        orderDirection = null;
        isAnyEpc = false;
    }
}
