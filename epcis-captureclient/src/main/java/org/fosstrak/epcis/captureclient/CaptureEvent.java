/*
 * Copyright (c) 2006, 2007, ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the ETH Zurich nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.accada.epcis.captureclient;

import java.util.ArrayList;

/**
 * Implements an example event object for the EPCIS Query Interface Client.
 * 
 * @author David Gubler
 */
public class CaptureEvent {
    /**
     * Human readable description to be shown in the GUI.
     */
    private String desc = null;

    /**
     * Type of the event.<br>
     * 0 = ObjectEvent<br>
     * 1 = AggregationEvent<br>
     * 2 = QuantityEvent<br>
     * 3 = TransactionEvent
     */
    private int type = 0;

    /**
     * The time at which the event occured. Null will insert the time when the
     * example is selected. Used by all events.
     */
    private String eventTime = null;

    /**
     * Action value. Not used for QuantityEvent.<br>
     * 0 = ADD<br>
     * 1 = OBSERVE<br>
     * 2 = DELETE
     */
    private int action = 1;

    /**
     * Business step. Optional for all events.
     */
    private String bizStep = "";

    /**
     * Disposition. Optional for all events.
     */
    private String disposition = "";

    /**
     * Read point. Optional for all events.
     */
    private String readPoint = "";

    /**
     * Business location. Optional for all events.
     */
    private String bizLocation = "";

    /**
     * Business transaction. Optional for all events except TransactionEvent.
     */
    private ArrayList<BizTransaction> businessTransactions = new ArrayList<BizTransaction>();

    /**
     * List of EPCs. Mandatory for ObjectEvent and used by TransactionEvent, not
     * used by other events.
     */
    private String epcList = "";

    /**
     * List of children EPCs. Mandatory for AggregationEvent except if
     * action=DELETE. Not used by other events.
     */
    private String childEPCs = "";

    /**
     * ID of the parent (URI, but not necessarily an EPC). Mandatory for
     * AggregationEvent, not used by other events.
     */
    private String parentID = "";

    /**
     * Class part of the EPCs. Mandatory for QuantityEvent, not used by other
     * events.
     */
    private String epcClass = "";

    /**
     * Quantity. Mandatory for QuantityEvent, not used by other events.
     */
    private int quantity = 0;

    /**
     * @param description
     *            Sets the description.
     */
    public void setDescription(final String description) {
        desc = description;
    }

    /**
     * @return The description of the event.
     */
    public String getDescription() {
        return desc;
    }

    /**
     * @param type
     *            Sets the event type.<br>
     *            0 = ObjectEvent<br>
     *            1 = AggregationEvent<br>
     *            2 = QuantityEvent<br>
     *            3 = TransactionEvent
     */
    public void setType(final int type) {
        this.type = type;
    }

    /**
     * @return The type of the event.<br>
     *         0 = ObjectEvent<br>
     *         1 = AggregationEvent<br>
     *         2 = QuantityEvent<br>
     *         3 = TransactionEvent
     */
    public int getType() {
        return type;
    }

    /**
     * @param time
     *            Sets the event time. Use ISO8601, i.e. 2006-05-23T17:45:25
     */
    public void setEventTime(final String time) {
        eventTime = time;
    }

    /**
     * @return The time of the event.
     */
    public String getEventTime() {
        return eventTime;
    }

    /**
     * @param action
     *            Sets the action value.<br>
     *            0 = ADD<br>
     *            1 = OBSERVE<br>
     *            2 = DELETE
     */
    public void setAction(final int action) {
        this.action = action;
    }

    /**
     * @return The action value.<br>
     *         0 = ADD<br>
     *         1 = OBSERVE<br>
     *         2 = DELETE
     */
    public int getAction() {
        return action;
    }

    /**
     * @param step
     *            Sets the business step.
     */
    public void setBizStep(final String step) {
        bizStep = step;
    }

    /**
     * @return The business step.
     */
    public String getBizStep() {
        return bizStep;
    }

    /**
     * @param disp
     *            Sets the disposition.
     */
    public void setDisposition(final String disp) {
        disposition = disp;
    }

    /**
     * @return The disposition.
     */
    public String getDisposition() {
        return disposition;
    }

    /**
     * @param readp
     *            Sets the read point.
     */
    public void setReadPoint(final String readp) {
        readPoint = readp;
    }

    /**
     * @return The read point.
     */
    public String getReadPoint() {
        return readPoint;
    }

    /**
     * @param location
     *            Sets the business location.
     */
    public void setBizLocation(final String location) {
        bizLocation = location;
    }

    /**
     * @return The business location.
     */
    public String getBizLocation() {
        return bizLocation;
    }

    /**
     * Sets the business transaction.
     * 
     * @param t
     *            The type of the business transaction.
     * @param id
     *            The id of the business transaction.
     */
    public void setBizTransaction(final String t, final String id) {
        BizTransaction bizTrans = new BizTransaction(t, id);
        businessTransactions.add(bizTrans);
    }

    /**
     * @return The business transaction.
     */
    public ArrayList<BizTransaction> getBizTransaction() {
        return businessTransactions;
    }

    /**
     * @param list
     *            Sets the list of EPCs (for object and transaction events).
     */
    public void setEpcList(final String list) {
        epcList = list;
    }

    /**
     * @return list The list of EPCs (for object and transaction events).
     */
    public String getEpcList() {
        return epcList;
    }

    /**
     * @param epcs
     *            Sets the list of children EPCs (for aggregation events).
     */
    public void setChildEPCs(final String epcs) {
        childEPCs = epcs;
    }

    /**
     * @return epcs The list of children EPCs (for aggregation events).
     */
    public String getChildEPCs() {
        return childEPCs;
    }

    /**
     * @param id
     *            Sets the id of the parent object (for aggregation events).
     */
    public void setParentID(final String id) {
        parentID = id;
    }

    /**
     * @return id The id of the parent object (for aggregation events).
     */
    public String getParentID() {
        return parentID;
    }

    /**
     * @param cls
     *            Sets the EPC class (for quantity events).
     */
    public void setEpcClass(final String cls) {
        epcClass = cls;
    }

    /**
     * @return cls The EPC class (for quantity events).
     */
    public String getEpcClass() {
        return epcClass;
    }

    /**
     * @param n
     *            Sets the quantity (for quantity events).
     */
    public void setQuantity(final int n) {
        quantity = n;
    }

    /**
     * @return quantity The quantity (for quantity events).
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * A BusinessTransaction conists of two elements, the BizTransTypeID and the
     * BizTransID which are Vocabularies.
     * 
     * @author Alain Remund
     */
    public class BizTransaction {
        /**
         * The private Variable for the BizTransTypeID.
         */
        private String type;

        /**
         * The private Variable for the BizTransID.
         */
        private String id;

        /**
         * Constructor for a new BusinessTransaction.
         * 
         * @param aType
         *            with the value of the BizTransTypeID.
         * @param aId
         *            with the value of the BizTransID.
         */
        public BizTransaction(final String aType, final String aId) {
            type = aType;
            id = aId;
        }

        /**
         * @return value of the BizTransTypeID.
         */
        public String getBizTransType() {
            return type;
        }

        /**
         * @return value of the BizTransID.
         */
        public String getBizTransID() {
            return id;
        }
    }
}
