/*
 * Copyright (C) 2010 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org) and
 * was developed as part of the webofthings.com initiative.
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
package org.epcis.fosstrak.restadapter.ws.epcis.query;

import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.*;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParams;
import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.config.QueryParamConstants;
import java.util.LinkedList;
import java.util.List;

/**
 * Class description
 * Class to build a Simple Event Query
 *
 *
 * @version        1.0, 09/08/07
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 * This project is a collaboration between:
 * Software Engineering Group, Departement of Informatics
 * University of Fribourg, Switzerland
 * and
 * Institute for Pervasive Computing
 * ETH Zurich, Switzerland
 * Project team: Mathias Mueller, Patrik Fuhrer, Dominique Guinard
 * (c) University of Fribourg, ETH Zurich
 *
 */
public class SEQuery {

    private QueryParams             queryParams      = null;
    private List<AbstractParameter> parameters       = null;
    private EventType               eventType        = null;
    private EventTimeGE             eventTimeGE      = null;
    private EventTimeLT             eventTimeLT      = null;
    private RecordTimeGE            recordTimeGE     = null;
    private RecordTimeLT            recordTimeLT     = null;
    private Action                  action           = null;
    private BizStep                 bizStep          = null;
    private Disposition             disposition      = null;
    private ReadPoint               readPoint        = null;
    private ReadPointWD             readPointWD      = null;
    private Location                location         = null;
    private LocationWD              locationWD       = null;
    private Transaction             transaction      = null;
    private Epc                     epc              = null;
    private ParentID                parentID         = null;
    private AnyEpc                  anyEpc           = null;
    private EpcClass                epcClass         = null;
    private Quantity                quantity         = null;
    private QuantityGT              quantityGT       = null;
    private QuantityLT              quantityLT       = null;
    private FieldnameEQ             fieldname        = null;
    private FieldnameExists         fieldnameExists  = null;
    private FieldnameHASATTR        fieldnameHASATTR = null;
    private FieldnameEQATTR         fieldnameEQATTR  = null;
    private FieldnameLT             fieldnameLT      = null;
    private FieldnameGT             fieldnameGT      = null;
    private OrderBy                 orderBy          = null;
    private OrderDirection          orderDirection   = null;
    private EventCountLimit         eventCountLimit  = null;
    private MaxEventCount           maxEventCount    = null;

    /**
     * Constructs a Simple Event Query
     *
     */
    public SEQuery() {
        parameters = new LinkedList<AbstractParameter>();
    }

    /**
     * Overrided toString method (content are the EPCIS REST Adapter query parameters)
     *
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder           res      = new StringBuilder();
        List<AbstractParameter> myParams = getParameters();

        for (AbstractParameter param : myParams) {
            if (param != null) {

                if (param.getName().equals(QueryParamConstants.ORDER_BY_REST) || param.getName().equals(QueryParamConstants.ORDER_DIRECTION_REST)) {
                    res.append("");
                } else {
                    res.append(Config.APPENDER + param.getName() + Config.EQUALS + param.getValue());
                }
            }
        }

        String result = res.toString();

        if (result.length() > 1) {
            return result.substring(1);
        }

        return result;
    }

    /**
     * Like the overrided toString method (content are the EPCIS REST Adapter query parameters). But prints it human readable (pretty).
     *
     *
     * @return
     */
    public String toReadableString() {
        StringBuilder           res      = new StringBuilder();
        List<AbstractParameter> myParams = getParameters();

        for (AbstractParameter param : myParams) {
            if (param != null) {
                res.append(Config.SEPARATOR + " " + param.getName() + " " + Config.EQUALS + " " + param.getValue());
            }
        }

        String result = res.toString();

        if (result.length() > 2) {
            return result.substring(2);
        }

        if (result.length() <= 2) {
            return "*";
        }

        return result;
    }

    /**
     * Returns the Query Parameters
     *
     *
     * @return
     */
    public QueryParams getQueryParams() {
        if (queryParams == null) {
            queryParams = buildQueryParams();
        }

        return queryParams;
    }

    /**
     * Builds the Query Parameters
     *
     *
     * @return
     */
    public QueryParams buildQueryParams() {
        queryParams = new QueryParams();

        try {
            if (getOrderBy() == null) {
                setOrderBy(new OrderBy(OrderBy.BY_RECORD_TIME));
            }

            if (getOrderDirection() == null) {
                setOrderDirection(new OrderDirection(OrderDirection.ASCENDING));
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            // continue its save
        }

        for (AbstractParameter param : getParameters()) {
            if (param != null) {
                queryParams.getParam().add(param.getQueryParam());
            }
        }

        return queryParams;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Action getAction() {
        return action;
    }

    /**
     * Method description
     *
     *
     * @param action
     */
    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public BizStep getBizStep() {
        return bizStep;
    }

    /**
     * Method description
     *
     *
     * @param bizStep
     */
    public void setBizStep(BizStep bizStep) {
        this.bizStep = bizStep;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Disposition getDisposition() {
        return disposition;
    }

    /**
     * Method description
     *
     *
     * @param disposition
     */
    public void setDisposition(Disposition disposition) {
        this.disposition = disposition;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public ReadPoint getReadPoint() {
        return readPoint;
    }

    /**
     * Method description
     *
     *
     * @param readPoint
     */
    public void setReadPoint(ReadPoint readPoint) {
        this.readPoint = readPoint;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public ReadPointWD getReadPointWD() {
        return readPointWD;
    }

    /**
     * Method description
     *
     *
     * @param readPointWD
     */
    public void setReadPointWD(ReadPointWD readPointWD) {
        this.readPointWD = readPointWD;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Method description
     *
     *
     * @param Location
     */
    public void setLocation(Location Location) {
        this.location = Location;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public LocationWD getLocationWD() {
        return locationWD;
    }

    /**
     * Method description
     *
     *
     * @param LocationWD
     */
    public void setLocationWD(LocationWD LocationWD) {
        this.locationWD = LocationWD;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * Method description
     *
     *
     * @param Transaction
     */
    public void setTransaction(Transaction Transaction) {
        this.transaction = Transaction;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Epc getEpc() {
        return epc;
    }

    /**
     * Method description
     *
     *
     * @param epc
     */
    public void setEpc(Epc epc) {
        this.epc = epc;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public ParentID getParentID() {
        return parentID;
    }

    /**
     * Method description
     *
     *
     * @param parentID
     */
    public void setParentID(ParentID parentID) {
        this.parentID = parentID;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public AnyEpc getAnyEpc() {
        return anyEpc;
    }

    /**
     * Method description
     *
     *
     * @param anyEpc
     */
    public void setAnyEpc(AnyEpc anyEpc) {
        this.anyEpc = anyEpc;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public EpcClass getEpcClass() {
        return epcClass;
    }

    /**
     * Method description
     *
     *
     * @param epcClass
     */
    public void setEpcClass(EpcClass epcClass) {
        this.epcClass = epcClass;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Quantity getQuantity() {
        return quantity;
    }

    /**
     * Method description
     *
     *
     * @param quantity
     */
    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public FieldnameEQ getFieldname() {
        return fieldname;
    }

    /**
     * Method description
     *
     *
     * @param fieldname
     */
    public void setFieldname(FieldnameEQ fieldname) {
        this.fieldname = fieldname;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public OrderBy getOrderBy() {
        return orderBy;
    }

    /**
     * Method description
     *
     *
     * @param orderBy
     */
    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    /**
     * Method description
     *
     *
     * @param orderDirection
     */
    public void setOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public EventCountLimit getEventCountLimit() {
        return eventCountLimit;
    }

    /**
     * Method description
     *
     *
     * @param eventCountLimit
     */
    public void setEventCountLimit(EventCountLimit eventCountLimit) {
        this.eventCountLimit = eventCountLimit;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public MaxEventCount getMaxEventCount() {
        return maxEventCount;
    }

    /**
     * Method description
     *
     *
     * @param maxEventCount
     */
    public void setMaxEventCount(MaxEventCount maxEventCount) {
        this.maxEventCount = maxEventCount;
    }

    /**
     * Get the List of Query Parameters
     *
     *
     * @return
     */
    public List<AbstractParameter> getParameters() {
        parameters.clear();
        parameters.add(eventType);
        parameters.add(eventTimeGE);
        parameters.add(eventTimeLT);
        parameters.add(recordTimeGE);
        parameters.add(recordTimeLT);
        parameters.add(action);
        parameters.add(bizStep);
        parameters.add(disposition);
        parameters.add(readPoint);
        parameters.add(readPointWD);
        parameters.add(location);
        parameters.add(locationWD);

        if (transaction != null) {
            parameters.addAll(transaction.getQueryParamValue());
        }

        parameters.add(epc);
        parameters.add(parentID);
        parameters.add(anyEpc);
        parameters.add(epcClass);
        parameters.add(quantity);
        parameters.add(quantityGT);
        parameters.add(quantityLT);
        parameters.add(fieldname);
        parameters.add(fieldnameEQATTR);
        parameters.add(fieldnameExists);
        parameters.add(fieldnameGT);
        parameters.add(fieldnameLT);
        parameters.add(fieldnameHASATTR);
        parameters.add(orderBy);
        parameters.add(orderDirection);
        parameters.add(eventCountLimit);
        parameters.add(maxEventCount);

        return parameters;
    }

    /**
     * Method description
     *
     *
     * @param parameters
     */
    public void setParameters(List<AbstractParameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Method description
     *
     *
     * @param eventType
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * @return the eventTimeGE
     */
    public EventTimeGE getEventTimeGE() {
        return eventTimeGE;
    }

    /**
     * @param eventTimeGE the eventTimeGE to set
     */
    public void setEventTimeGE(EventTimeGE eventTimeGE) {
        this.eventTimeGE = eventTimeGE;
    }

    /**
     * @return the eventTimeLT
     */
    public EventTimeLT getEventTimeLT() {
        return eventTimeLT;
    }

    /**
     * @param eventTimeLT the eventTimeLT to set
     */
    public void setEventTimeLT(EventTimeLT eventTimeLT) {
        this.eventTimeLT = eventTimeLT;
    }

    /**
     * @return the recordTimeGE
     */
    public RecordTimeGE getRecordTimeGE() {
        return recordTimeGE;
    }

    /**
     * @param recordTimeGE the recordTimeGE to set
     */
    public void setRecordTimeGE(RecordTimeGE recordTimeGE) {
        this.recordTimeGE = recordTimeGE;
    }

    /**
     * @return the recordTimeLT
     */
    public RecordTimeLT getRecordTimeLT() {
        return recordTimeLT;
    }

    /**
     * @param recordTimeLT the recordTimeLT to set
     */
    public void setRecordTimeLT(RecordTimeLT recordTimeLT) {
        this.recordTimeLT = recordTimeLT;
    }

    /**
     * @return the quantityGT
     */
    public QuantityGT getQuantityGT() {
        return quantityGT;
    }

    /**
     * @param quantityGT the quantityGT to set
     */
    public void setQuantityGT(QuantityGT quantityGT) {
        this.quantityGT = quantityGT;
    }

    /**
     * @return the quantityLT
     */
    public QuantityLT getQuantityLT() {
        return quantityLT;
    }

    /**
     * @param quantityLT the quantityLT to set
     */
    public void setQuantityLT(QuantityLT quantityLT) {
        this.quantityLT = quantityLT;
    }

    /**
     * @return the fieldnameExists
     */
    public FieldnameExists getFieldnameExists() {
        return fieldnameExists;
    }

    /**
     * @param fieldnameExists the fieldnameExists to set
     */
    public void setFieldnameExists(FieldnameExists fieldnameExists) {
        this.fieldnameExists = fieldnameExists;
    }

    /**
     * @return the fieldnameHASATTR
     */
    public FieldnameHASATTR getFieldnameHASATTR() {
        return fieldnameHASATTR;
    }

    /**
     * @param fieldnameHASATTR the fieldnameHASATTR to set
     */
    public void setFieldnameHASATTR(FieldnameHASATTR fieldnameHASATTR) {
        this.fieldnameHASATTR = fieldnameHASATTR;
    }

    /**
     * @return the fieldnameEQATTR
     */
    public FieldnameEQATTR getFieldnameEQATTR() {
        return fieldnameEQATTR;
    }

    /**
     * @param fieldnameEQATTR the fieldnameEQATTR to set
     */
    public void setFieldnameEQATTR(FieldnameEQATTR fieldnameEQATTR) {
        this.fieldnameEQATTR = fieldnameEQATTR;
    }

    /**
     * @return the fieldnameLT
     */
    public FieldnameLT getFieldnameLT() {
        return fieldnameLT;
    }

    /**
     * @param fieldnameLT the fieldnameLT to set
     */
    public void setFieldnameLT(FieldnameLT fieldnameLT) {
        this.fieldnameLT = fieldnameLT;
    }

    /**
     * @return the fieldnameGT
     */
    public FieldnameGT getFieldnameGT() {
        return fieldnameGT;
    }

    /**
     * @param fieldnameGT the fieldnameGT to set
     */
    public void setFieldnameGT(FieldnameGT fieldnameGT) {
        this.fieldnameGT = fieldnameGT;
    }
}
