package org.accada.epcis.queryclient;

import java.util.Vector;

import org.accada.epcis.soapapi.QueryParam;

/**
 * Implements an example query object for the EPCIS Query Interface Client.
 * @author David Gubler
 *
 */
public class Query {
    /**
     * Human-readable description.
     */
    private String desc;

    /**
     * Wheter this example should return ObjectEvents or not.
     */
    private boolean returnObjectEvents = false;

    /**
     * Wheter this example should return AggregationEvents or not.
     */
    private boolean returnAggregationEvents = false;

    /**
     * Wheter this example should return QuantityEvents or not.
     */
    private boolean returnQuantityEvents = false;

    /**
     * Wheter this example should return TransacitonEvents or not.
     */
    private boolean returnTransactionEvents = false;

    /**
     * Vector that holds all other query parameters.
     */
    private Vector<QueryParam> queryParameters = new Vector<QueryParam>();

    /**
     * Sets the description.
     * @param description
     */
    public void setDescription(final String description) {
        desc = description;
    }

    /**
     * Gets the description.
     * @return
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Sets if object events should be returned or not.
     * @param objectEvents
     */
    public void setReturnObjectEvents(final boolean objectEvents) {
        returnObjectEvents = objectEvents;
    }

    /**
     * Gets if object events should be returned or not.
     * @return
     */
    public boolean getReturnObjectEvents() {
        return returnObjectEvents;
    }

    /**
     * Sets if aggregation events should be returned or not.
     * @param aggregationEvents
     */
    public void setReturnAggregationEvents(final boolean aggregationEvents) {
        returnAggregationEvents = aggregationEvents;
    }

    /**
     * Gets if aggregation events should be returned or not.
     * @return
     */
    public boolean getReturnAggregationEvents() {
        return returnAggregationEvents;
    }

    /**
     * Sets if quantity events should be returned or not.
     * @param quantityEvents
     */
    public void setReturnQuantityEvents(final boolean quantityEvents) {
        returnQuantityEvents = quantityEvents;
    }

    /**
     * Gets if quantity events should be returned or not.
     * @return
     */
    public boolean getReturnQuantityEvents() {
        return returnQuantityEvents;
    }

    /**
     * Sets if transaction events should be returned or not.
     * @param transactionEvents
     */
    public void setReturnTransactionEvents(final boolean transactionEvents) {
        returnTransactionEvents = transactionEvents;
    }

    /**
     * Gets if transaction events should be returned or not.
     * @return
     */
    public boolean getReturnTransactionEvents() {
        return returnTransactionEvents;
    }

    /**
     * Gets the vector that holds all other query parameters.
     * @return
     */
    public Vector<QueryParam> getQueryParametersVector() {
        return queryParameters;
    }
}
