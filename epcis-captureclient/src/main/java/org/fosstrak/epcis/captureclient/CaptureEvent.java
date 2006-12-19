package org.accada.epcis.captureclient;

import java.util.ArrayList;

/**
 * Implements an example event object for the EPCIS Query Interface Client.
 * @author David Gubler
 *
 */
public class CaptureEvent {
    /**
     * Human readable description to be shown in the GUI.
     */
    private String desc = null;

    /**
     * Type of the event.
     * 0 = ObjectEvent
     * 1 = AggregationEvent
     * 2 = QuantityEvent
     * 3 = TransactionEvent
     */
    private int type = 0;

    /**
     * The time at which the event occured.
     * Null will insert the time when the example is selected.
     * Used by all events.
     */
    private String eventTime = null;

    /**
     * Action value. Not used for QuantityEvent.
     * 0 = ADD
     * 1 = OBSERVE
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
     * List of EPCs. Mandatory for ObjectEvent and used by TransactionEvent,
     * not used by other events.
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
     * Sets the description.
     * @param description
     */
    public void setDescription(final String description) {
        desc = description;
    }

    /**
     * Gets the description.
     * @return Desctiption
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Sets the event type.
     * 0 = ObjectEvent
     * 1 = AggregationEvent
     * 2 = QuantityEvent
     * 3 = TransactionEvent
     * @param type
     */
    public void setType(final int t) {
        type = t;
    }

    /**
     * Gets the event type.
     * 0 = ObjectEvent
     * 1 = AggregationEvent
     * 2 = QuantityEvent
     * 3 = TransactionEvent
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the event time.
     * Use ISO8601, i.e.
     * 2006-05-23T17:45:25
     * @param time
     */
    public void setEventTime(final String time) {
        eventTime = time;
    }

    /**
     * Gets the event time.
     * @return time
     */
    public String getEventTime() {
        return eventTime;
    }

    /**
     * Sets the action value.
     * 0 = ADD
     * 1 = OBSERVE
     * 2 = DELETE
     * @param action
     */
    public void setAction(final int a) {
        action = a;
    }

    /**
     * Gets the action value.
     * 0 = ADD
     * 1 = OBSERVE
     * 2 = DELETE
     * @return action
     */
    public int getAction() {
        return action;
    }

    /**
     * Sets the business step.
     * @param step
     */
    public void setBizStep(final String step) {
        bizStep = step;
    }

    /**
     * Gets the business step.
     * @return step
     */
    public String getBizStep() {
        return bizStep;
    }

    /**
     * Sets the disposition.
     * @param disp
     */
    public void setDisposition(final String disp) {
        disposition = disp;
    }

    /**
     * Gets the disposition.
     * @return disp
     */
    public String getDisposition() {
        return disposition;
    }

    /**
     * Sets the read point.
     * @param readp
     */
    public void setReadPoint(final String readp) {
        readPoint = readp;
    }

    /**
     * Gets the read point.
     * @return readp
     */
    public String getReadPoint() {
        return readPoint;
    }

    /**
     * Sets the business location.
     * @param location
     */
    public void setBizLocation(final String location) {
        bizLocation = location;
    }

    /**
     * Gets the business location.
     * @return location
     */
    public String getBizLocation() {
        return bizLocation;
    }

    /*
     * from here a have to adapt it to the new version
     */

    /**
     * Sets the business transaction.
     * @param transaction
     */
    public void setBizTransaction(final String type, final String id) {
        BizTransaction bizTrans = new BizTransaction(type, id);
        businessTransactions.add(bizTrans);
    }

    /**
     * Gets the business transaction.
     * @return transaction
     */
    public ArrayList<BizTransaction> getBizTransaction() {
        return businessTransactions;
    }

    /*
     * up to here
     */


    /**
     * Sets the list of EPCs (for object and transaction events).
     * @param list
     */
    public void setEpcList(final String list) {
        epcList = list;
    }

    /**
     * Gets the list of EPCs (for object and transaction events).
     * @return list
     */
    public String getEpcList() {
        return epcList;
    }

    /**
     * Sets the list of children EPCs (for aggregation events).
     * @param epcs
     */
    public void setChildEPCs(final String epcs) {
        childEPCs = epcs;
    }

    /**
     * Gets the list of children EPCs (for aggregation events).
     * @return epcs
     */
    public String getChildEPCs() {
        return childEPCs;
    }

    /**
     * Sets the id of the parent object (for aggregation events).
     * @param id
     */
    public void setParentID(final String id) {
        parentID = id;
    }

    /**
     * Gets the id of the parent object (for aggregation events).
     * @return id
     */
    public String getParentID() {
        return parentID;
    }

    /**
     * Sets the EPC class (for quantity events).
     * @param cls
     */
    public void setEpcClass(final String cls) {
        epcClass = cls;
    }

    /**
     * Gets the EPC class (for quantity events).
     * @return cls
     */
    public String getEpcClass() {
        return epcClass;
    }

    /**
     * Sets the quantity (for quantity events).
     * @param n
     */
    public void setQuantity(final int n) {
        quantity = n;
    }

    /**
     * Gets the quantity (for quantity events).
     * @return quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * A BusinessTransaction conists of two elements, the BizTransTypeID and
     * the BizTransID which are Vocabularies.
     *
     * @author Alain Remund
     *
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
