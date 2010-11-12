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
package org.epcis.fosstrak.restadapter.logic;

import org.epcis.fosstrak.restadapter.util.URI;
import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.model.Form;
import org.epcis.fosstrak.restadapter.util.TimeParser;
import org.epcis.fosstrak.restadapter.ws.epcis.query.SEQuery;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.Action;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.AnyEpc;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.BizStep;
import org.epcis.fosstrak.restadapter.config.QueryParamConstants;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.Disposition;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.Epc;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.EpcClass;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.EventCountLimit;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.EventTimeGE;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.EventTimeLT;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.EventType;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.FieldnameEQ;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.FieldnameEQATTR;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.FieldnameExists;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.FieldnameGT;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.FieldnameHASATTR;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.FieldnameLT;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.Location;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.LocationWD;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.MaxEventCount;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.OrderBy;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.OrderDirection;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.ParentID;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.Quantity;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.QuantityGT;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.QuantityLT;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.ReadPoint;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.ReadPointWD;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.RecordTimeGE;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.RecordTimeLT;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.Transaction;
import java.text.ParseException;

/**
 * Method to handle the query params
 * @author Mathias Mueller mathias.mueller(at)unifr.ch
 *
 */
public abstract class AbstractQueryParamBusinessLogic extends AbstractEventFinderBusinessLogic {

    private String eventTime;
    private String recordTime;
    private String eventType;
    private String action;
    private String bizStep;
    private String disposition;
    private String readPoint;
    private String readPointWD;
    private String bizLocation;
    private String bizLocationWD;
    private String bizTransaction;
    private String epc;
    private String parentID;
    private String anyEPC;
    private String epcClass;
    private String quantity;
    private String fieldname;
    private String orderBy;
    private String orderDirection;
    private String eventCountLimit;
    private String maxEventCount;

    /**
     * inits the class
     *
     *
     * @param eventTime
     * @param recordTime
     * @param eventType
     * @param action
     * @param bizStep
     * @param disposition
     * @param readPoint
     * @param readPointWD
     * @param bizLocation
     * @param bizLocationWD
     * @param bizTransaction
     * @param epc
     * @param parentID
     * @param anyEPC
     * @param epcClass
     * @param quantity
     * @param fieldname
     * @param orderBy
     * @param orderDirection
     * @param eventCountLimit
     * @param maxEventCount
     */
    public void initAbstractQueryParamBusinessLogic(String eventTime, String recordTime, String eventType, String action, String bizStep, String disposition, String readPoint, String readPointWD, String bizLocation, String bizLocationWD, String bizTransaction, String epc, String parentID, String anyEPC, String epcClass, String quantity, String fieldname, String orderBy, String orderDirection, String eventCountLimit, String maxEventCount) {

        if ((eventTime != null) && eventTime.equals("")) {
            eventTime = null;
        }

        if ((recordTime != null) && recordTime.equals("")) {
            recordTime = null;
        }

        if ((eventType != null) && eventType.equals("")) {
            eventType = null;
        }

        if ((action != null) && action.equals("")) {
            action = null;
        }

        if ((bizStep != null) && bizStep.equals("")) {
            bizStep = null;
        }

        if ((disposition != null) && disposition.equals("")) {
            disposition = null;
        }

        if ((readPoint != null) && readPoint.equals("")) {
            readPoint = null;
        }

        if ((readPointWD != null) && readPointWD.equals("")) {
            readPointWD = null;
        }

        if ((bizLocation != null) && bizLocation.equals("")) {
            bizLocation = null;
        }

        if ((bizLocationWD != null) && bizLocationWD.equals("")) {
            bizLocationWD = null;
        }

        if ((bizTransaction != null) && bizTransaction.equals("")) {
            bizTransaction = null;
        }

        if ((epc != null) && epc.equals("")) {
            epc = null;
        }

        if ((parentID != null) && parentID.equals("")) {
            parentID = null;
        }

        if ((anyEPC != null) && anyEPC.equals("")) {
            anyEPC = null;
        }

        if ((epcClass != null) && epcClass.equals("")) {
            epcClass = null;
        }

        if ((quantity != null) && quantity.equals("")) {
            quantity = null;
        }

        if ((fieldname != null) && fieldname.equals("")) {
            fieldname = null;
        }

        if ((orderBy != null) && orderBy.equals("")) {
            orderBy = null;
        }

        if ((orderDirection != null) && orderDirection.equals("")) {
            orderDirection = null;
        }

        if ((eventCountLimit != null) && eventCountLimit.equals("")) {
            eventCountLimit = null;
        }

        if ((maxEventCount != null) && maxEventCount.equals("")) {
            maxEventCount = null;
        }

        if (eventTime != null) {
            eventTime = URI.unescapeURL(eventTime);
        }

        if (recordTime != null) {
            recordTime = URI.unescapeURL(recordTime);
        }

        if (eventType != null) {
            eventType = URI.unescapeURL(eventType);
        }

        if (action != null) {
            action = URI.unescapeURL(action);
        }

        if (bizStep != null) {
            bizStep = URI.unescapeURL(bizStep);
        }

        if (disposition != null) {
            disposition = URI.unescapeURL(disposition);
        }

        if (readPoint != null) {
            readPoint = URI.unescapeURL(readPoint);
        }

        if (readPointWD != null) {
            readPointWD = URI.unescapeURL(readPointWD);
        }

        if (bizLocation != null) {
            bizLocation = URI.unescapeURL(bizLocation);
        }

        if (bizLocationWD != null) {
            bizLocationWD = URI.unescapeURL(bizLocationWD);
        }

        if (bizTransaction != null) {
            bizTransaction = URI.unescapeURL(bizTransaction);
        }

        if (epc != null) {
            epc = URI.unescapeURL(epc);
        }

        if (parentID != null) {
            parentID = URI.unescapeURL(parentID);
        }

        if (anyEPC != null) {
            anyEPC = URI.unescapeURL(anyEPC);
        }

        if (epcClass != null) {
            epcClass = URI.unescapeURL(epcClass);
        }

        if (quantity != null) {
            quantity = URI.unescapeURL(quantity);
        }

        if (fieldname != null) {
            fieldname = URI.unescapeURL(fieldname);
        }

        if (orderBy != null) {
            orderBy = URI.unescapeURL(orderBy);
        }

        if (orderDirection != null) {
            orderDirection = URI.unescapeURL(orderDirection);
        }

        if (eventCountLimit != null) {
            eventCountLimit = URI.unescapeURL(eventCountLimit);
        }

        if (maxEventCount != null) {
            maxEventCount = URI.unescapeURL(maxEventCount);
        }

        this.eventTime       = eventTime;
        this.recordTime      = recordTime;
        this.eventType       = eventType;
        this.action          = action;
        this.bizStep         = bizStep;
        this.disposition     = disposition;
        this.readPoint       = readPoint;
        this.readPointWD     = readPointWD;
        this.bizLocation     = bizLocation;
        this.bizLocationWD   = bizLocationWD;
        this.bizTransaction  = bizTransaction;
        this.epc             = epc;
        this.parentID        = parentID;
        this.anyEPC          = anyEPC;
        this.epcClass        = epcClass;
        this.quantity        = quantity;
        this.fieldname       = fieldname;
        this.orderBy         = orderBy;
        this.orderDirection  = orderDirection;
        this.eventCountLimit = eventCountLimit;
        this.maxEventCount   = maxEventCount;
    }

    /**
     * Creates the SEQuery from the query params
     *
     *
     * @return
     *
     * @throws ParseException
     * @throws QueryParameterExceptionResponse
     */
    public SEQuery getSEQuery() throws ParseException, QueryParameterExceptionResponse {
        SEQuery seQuery = null;

        seQuery = new SEQuery();

        if (getEventTime() != null) {
            String t1 = getEventTime();
            String t2 = null;

            try {
                t2 = t1.split(Config.INTERVAL)[1];
                t1 = t1.split(Config.INTERVAL)[0];
            } catch (Exception ex) {

                // ok no init of t2 needed
            }

            boolean isDone     = false;
            boolean isInterval = false;
            boolean isFuture   = false;
            boolean isPast     = false;

            if (t2 != null) {
                isInterval = true;
            }

            if (t1.startsWith(Config.ABOVE)) {
                isFuture = true;
            }

            if (t1.startsWith(Config.FEWER)) {
                isPast = true;
            }

            if (isFuture || isPast) {
                try {
                    t1 = t1.substring(1);
                } catch (Exception ex) {
                    // ok
                }
            }

            if (isInterval &&!isDone) {
                seQuery.setEventTimeGE(new EventTimeGE(t1));
                t2 = TimeParser.addOneSecondToTime(t2);
                seQuery.setEventTimeLT(new EventTimeLT(t2));
                isDone = true;
            }

            if (isFuture &&!isDone) {
                t1 = TimeParser.addOneSecondToTime(t1);
                seQuery.setEventTimeGE(new EventTimeGE(t1));
                isDone = true;
            }

            if (isPast &&!isDone) {
                seQuery.setEventTimeLT(new EventTimeLT(t1));
                isDone = true;
            }

            if (!isDone) {
                seQuery.setEventTimeGE(new EventTimeGE(t1));
                t1 = TimeParser.addOneSecondToTime(t1);
                seQuery.setEventTimeLT(new EventTimeLT(t1));
                isDone = true;
            }
        }

        if (getRecordTime() != null) {
            String t1 = getRecordTime();
            String t2 = null;

            try {
                t2 = t1.split(Config.INTERVAL)[1];
                t1 = t1.split(Config.INTERVAL)[0];
            } catch (Exception ex) {
                // ok no init of t2 needed
            }

            boolean isDone     = false;
            boolean isInterval = false;
            boolean isFuture   = false;
            boolean isPast     = false;

            if (t2 != null) {
                isInterval = true;
            }

            if (t1.startsWith(Config.ABOVE)) {
                isFuture = true;
            }

            if (t1.startsWith(Config.FEWER)) {
                isPast = true;
            }

            if (isFuture || isPast) {
                try {
                    t1 = t1.substring(1);
                } catch (Exception ex) {

                    // ok
                }
            }

            if (isInterval &&!isDone) {
                seQuery.setRecordTimeGE(new RecordTimeGE(t1));
                t2 = TimeParser.addOneSecondToTime(t2);
                seQuery.setRecordTimeLT(new RecordTimeLT(t2));
                isDone = true;
            }

            if (isFuture &&!isDone) {
                t1 = TimeParser.addOneSecondToTime(t1);
                seQuery.setRecordTimeGE(new RecordTimeGE(t1));
                isDone = true;
            }

            if (isPast &&!isDone) {
                seQuery.setRecordTimeLT(new RecordTimeLT(t1));
                isDone = true;
            }

            if (!isDone) {
                seQuery.setRecordTimeGE(new RecordTimeGE(t1));
                t1 = TimeParser.addOneSecondToTime(t1);
                seQuery.setRecordTimeLT(new RecordTimeLT(t1));
                isDone = true;
            }
        }

        if (getEventType() != null) {
            seQuery.setEventType(new EventType(getEventType()));
        }

        if (getAction() != null) {
            seQuery.setAction(new Action(getAction()));
        }

        if (getBizStep() != null) {
            seQuery.setBizStep(new BizStep(getBizStep()));
        }

        if (getDisposition() != null) {
            seQuery.setDisposition(new Disposition(getDisposition()));
        }

        if (getReadPoint() != null) {
            seQuery.setReadPoint(new ReadPoint(getReadPoint()));
        }

        if (getReadPointWD() != null) {
            seQuery.setReadPointWD(new ReadPointWD(getReadPointWD()));
        }

        if (getBizLocation() != null) {
            seQuery.setLocation(new Location(getBizLocation()));
        }

        if (getBizLocationWD() != null) {
            seQuery.setLocationWD(new LocationWD(getBizLocationWD()));
        }

        if (getBizTransaction() != null) {
            seQuery.setTransaction(new Transaction(getBizTransaction()));
        }

        if (getEpc() != null) {
            seQuery.setEpc(new Epc(getEpc()));
        }

        if (getParentID() != null) {
            seQuery.setParentID(new ParentID(getParentID()));
        }

        if (getAnyEPC() != null) {
            seQuery.setAnyEpc(new AnyEpc(getAnyEPC()));
        }

        if (getEpcClass() != null) {
            seQuery.setEpcClass(new EpcClass(getEpcClass()));
        }

        if (getQuantity() != null) {
            String q1 = getQuantity();
            String q2 = null;

            try {
                q2 = q1.split(Config.INTERVAL)[1];
                q1 = q1.split(Config.INTERVAL)[0];
            } catch (Exception ex) {

                // ok no init of t2 needed
            }

            boolean isDone     = false;
            boolean isInterval = false;
            boolean isMore     = false;
            boolean isLess     = false;

            if (q2 != null) {
                isInterval = true;
            }

            if (q1.startsWith(Config.ABOVE)) {
                isMore = true;
            }

            if (q1.startsWith(Config.FEWER)) {
                isLess = true;
            }

            if (isMore || isLess) {
                try {
                    q1 = q1.substring(1);
                } catch (Exception ex) {

                    // continue
                }
            }

            if (isInterval &&!isDone) {
                try {
                    int quantity1 = Integer.parseInt(q1) - 1;
                    int quantity2 = Integer.parseInt(q2) + 1;

                    seQuery.setQuantityGT(new QuantityGT(quantity1 + ""));
                    seQuery.setQuantityLT(new QuantityLT(quantity2 + ""));
                } catch (Exception ex) {

                    // continue and let epcis handle exception
                    seQuery.setQuantityGT(new QuantityGT(q1));
                    seQuery.setQuantityLT(new QuantityLT(q2));
                }

                isDone = true;
            }

            if (isMore &&!isDone) {
                seQuery.setQuantityGT(new QuantityGT(q1));
                isDone = true;
            }

            if (isLess &&!isDone) {
                seQuery.setQuantityLT(new QuantityLT(q1));
                isDone = true;
            }

            if (!isDone) {

                // its equal
                seQuery.setQuantity(new Quantity(q1));
                isDone = true;
            }
        }

        if (getFieldname() != null) {
            String  value  = getFieldname();
            boolean isDone = false;

            if (getFieldname().equals("exists") &&!isDone) {
                seQuery.setFieldnameExists(new FieldnameExists(value));
                isDone = true;
            }

            if (getFieldname().startsWith(">") &&!isDone) {
                value = value.substring(1);
                seQuery.setFieldnameGT(new FieldnameGT(value));
                isDone = true;
            }

            if (getFieldname().startsWith("<") &&!isDone) {
                value = value.substring(1);
                seQuery.setFieldnameLT(new FieldnameLT(value));
                isDone = true;
            }

            if (getFieldname().startsWith("attr:") &&!isDone) {
                value = value.substring(5);
                seQuery.setFieldnameHASATTR(new FieldnameHASATTR(value));
                isDone = true;
            }

            if (getFieldname().startsWith("attrname:") &&!isDone) {
                value = value.substring(9);
                seQuery.setFieldnameEQATTR(new FieldnameEQATTR(value));
                isDone = true;
            }

            if (!isDone) {
                seQuery.setFieldname(new FieldnameEQ(getFieldname()));
            }
        }

        if (getOrderBy() != null) {
            seQuery.setOrderBy(new OrderBy(getOrderBy()));
        }

        if (getOrderDirection() != null) {
            seQuery.setOrderDirection(new OrderDirection(getOrderDirection()));
        }

        if (getEventCountLimit() != null) {
            seQuery.setEventCountLimit(new EventCountLimit(getEventCountLimit()));
        }

        if (getMaxEventCount() != null) {
            seQuery.setMaxEventCount(new MaxEventCount(getMaxEventCount()));
        }

        return seQuery;
    }

    /**
     * Create the form from the query params
     *
     *
     * @return
     */
    public Form getForm() {
        Form   form = new Form();
        String formValue;

        formValue = "";

        if (getEventTime() != null) {
            formValue = getEventTime();
        }

        form.addEntry("Event Time", QueryParamConstants.EVENT_TIME_REST, formValue, Config.QUERY_EventTime_USAGE);
        formValue = "";

        if (getRecordTime() != null) {
            formValue = getRecordTime();
        }

        form.addEntry("Record Time", QueryParamConstants.RECORD_TIME_REST, formValue, Config.QUERY_RecordTime_USAGE);
        formValue = "";

        if (getEventType() != null) {
            formValue = getEventType();
        }

        form.addEntry("Type", QueryParamConstants.EVENT_TYPE_REST, formValue, Config.QUERY_Type_USAGE);
        formValue = "";

        if (getAction() != null) {
            formValue = getAction();
        }

        form.addEntry("Action", QueryParamConstants.ACTION_REST, formValue, Config.QUERY_Action_USAGE);
        formValue = "";

        if (getBizStep() != null) {
            formValue = getBizStep();
        }

        form.addEntry("Business Step", QueryParamConstants.BUSINESS_STEP_REST, formValue, Config.QUERY_BusinessStep_USAGE);
        formValue = "";

        if (getDisposition() != null) {
            formValue = getDisposition();
        }

        form.addEntry("Disposition", QueryParamConstants.DISPOSITION_REST, formValue, Config.QUERY_Disposition_USAGE);
        formValue = "";

        if (getReadPoint() != null) {
            formValue = getReadPoint();
        }

        form.addEntry("Read Point", QueryParamConstants.READ_POINT_REST, formValue, Config.QUERY_ReadPoint_USAGE);
        formValue = "";

        if (getReadPointWD() != null) {
            formValue = getReadPointWD();
        }

        form.addEntry("Read Point Descendent of", QueryParamConstants.READ_POINT_DESCENDANT_REST, formValue, Config.QUERY_ReadPointDescendant_USAGE);
        formValue = "";

        if (getBizLocation() != null) {
            formValue = getBizLocation();
        }

        form.addEntry("Business Location", QueryParamConstants.BUSINESS_LOCATION_REST, formValue, Config.QUERY_BusinessLocation_USAGE);
        formValue = "";

        if (getBizLocationWD() != null) {
            formValue = getBizLocationWD();
        }

        form.addEntry("Business Location Descendent of", QueryParamConstants.BUSINESS_LOCATION_DESCENDANT_REST, formValue, Config.QUERY_BusinessLocationDescendant_USAGE);
        formValue = "";

        if (getBizTransaction() != null) {
            formValue = getBizTransaction();
        }

        form.addEntry("Business Transaction", QueryParamConstants.BUSINESS_TRANSACTION_TYPE_REST, formValue, Config.QUERY_BusinessTransaction_USAGE);
        formValue = "";

        if (getEpc() != null) {
            formValue = getEpc();
        }

        form.addEntry("EPC", QueryParamConstants.EPC_REST, formValue, Config.QUERY_Epc_USAGE);
        formValue = "";

        if (getParentID() != null) {
            formValue = getParentID();
        }

        form.addEntry("Parent ID", QueryParamConstants.PARENT_ID_REST, formValue, Config.QUERY_ParentID_USAGE);
        formValue = "";

        if (getAnyEPC() != null) {
            formValue = getAnyEPC();
        }

        form.addEntry("Any EPC", QueryParamConstants.ANY_EPC_REST, formValue, Config.QUERY_AnyEPC_USAGE);
        formValue = "";

        if (getEpcClass() != null) {
            formValue = getEpcClass();
        }

        form.addEntry("EPC Class", QueryParamConstants.EPC_CLASS_REST, formValue, Config.QUERY_EpcClass_USAGE);
        formValue = "";

        if (getQuantity() != null) {
            formValue = getQuantity();
        }

        form.addEntry("Quantity", QueryParamConstants.QUANTITY_REST, formValue, Config.QUERY_Quantity_USAGE);
        formValue = "";

//      if (getFieldname() != null) {
//          formValue = getFieldname();
//      }
//
//      form.addEntry("Fieldname", QueryParamConstants.FIELDNAME_REST, formValue, Config.Query_Fieldname_USAGE);
//      formValue = "";

        if (getOrderBy() != null) {
            formValue = getOrderBy();
        }

        form.addEntry("Order By", QueryParamConstants.ORDER_BY_REST, formValue, Config.Query_OrderBy_USAGE);
        formValue = "";

        if (getOrderDirection() != null) {
            formValue = getOrderDirection();
        }

        form.addEntry("Order Direction", QueryParamConstants.ORDER_DIRECTION_REST, formValue, Config.Query_ORDERDIRECTION_USAGE);
        formValue = "";

        if (getEventCountLimit() != null) {
            formValue = getEventCountLimit();
        }

        form.addEntry("Event Count Limit", QueryParamConstants.EVENT_COUNT_LIMIT_REST, formValue, Config.Query_EventCountLimit_USAGE);
        formValue = "";

        if (getMaxEventCount() != null) {
            formValue = getMaxEventCount();
        }

        form.addEntry("Max Event Count", QueryParamConstants.MAX_EVENT_COUNT_REST, formValue, Config.Query_MaxEventCount_USAGE);

        return form;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getEventTime() {
        return eventTime;
    }

    /**
     * Method description
     *
     *
     * @param eventTime
     */
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getRecordTime() {
        return recordTime;
    }

    /**
     * Method description
     *
     *
     * @param recordTime
     */
    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Method description
     *
     *
     * @param eventType
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getAction() {
        return action;
    }

    /**
     * Method description
     *
     *
     * @param action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getBizStep() {
        return bizStep;
    }

    /**
     * Method description
     *
     *
     * @param bizStep
     */
    public void setBizStep(String bizStep) {
        this.bizStep = bizStep;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getDisposition() {
        return disposition;
    }

    /**
     * Method description
     *
     *
     * @param disposition
     */
    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getReadPoint() {
        return readPoint;
    }

    /**
     * Method description
     *
     *
     * @param readPoint
     */
    public void setReadPoint(String readPoint) {
        this.readPoint = readPoint;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getReadPointWD() {
        return readPointWD;
    }

    /**
     * Method description
     *
     *
     * @param readPointWD
     */
    public void setReadPointWD(String readPointWD) {
        this.readPointWD = readPointWD;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getBizLocation() {
        return bizLocation;
    }

    /**
     * Method description
     *
     *
     * @param bizLocation
     */
    public void setBizLocation(String bizLocation) {
        this.bizLocation = bizLocation;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getBizLocationWD() {
        return bizLocationWD;
    }

    /**
     * Method description
     *
     *
     * @param bizLocationWD
     */
    public void setBizLocationWD(String bizLocationWD) {
        this.bizLocationWD = bizLocationWD;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getBizTransaction() {
        return bizTransaction;
    }

    /**
     * Method description
     *
     *
     * @param bizTransaction
     */
    public void setBizTransaction(String bizTransaction) {
        this.bizTransaction = bizTransaction;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getEpc() {
        return epc;
    }

    /**
     * Method description
     *
     *
     * @param epc
     */
    public void setEpc(String epc) {
        this.epc = epc;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getParentID() {
        return parentID;
    }

    /**
     * Method description
     *
     *
     * @param parentID
     */
    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getAnyEPC() {
        return anyEPC;
    }

    /**
     * Method description
     *
     *
     * @param anyEPC
     */
    public void setAnyEPC(String anyEPC) {
        this.anyEPC = anyEPC;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getEpcClass() {
        return epcClass;
    }

    /**
     * Method description
     *
     *
     * @param epcClass
     */
    public void setEpcClass(String epcClass) {
        this.epcClass = epcClass;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * Method description
     *
     *
     * @param quantity
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getFieldname() {
        return fieldname;
    }

    /**
     * Method description
     *
     *
     * @param fieldname
     */
    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * Method description
     *
     *
     * @param orderBy
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getOrderDirection() {
        return orderDirection;
    }

    /**
     * Method description
     *
     *
     * @param orderDirection
     */
    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getEventCountLimit() {
        return eventCountLimit;
    }

    /**
     * Method description
     *
     *
     * @param eventCountLimit
     */
    public void setEventCountLimit(String eventCountLimit) {
        this.eventCountLimit = eventCountLimit;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getMaxEventCount() {
        return maxEventCount;
    }

    /**
     * Method description
     *
     *
     * @param maxEventCount
     */
    public void setMaxEventCount(String maxEventCount) {
        this.maxEventCount = maxEventCount;
    }
}
