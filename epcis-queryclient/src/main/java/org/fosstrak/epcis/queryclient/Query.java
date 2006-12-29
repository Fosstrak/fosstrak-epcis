package org.accada.epcis.queryclient;

import java.util.ArrayList;
import java.util.List;

import org.accada.epcis.soapapi.QueryParam;

/**
 * Implements an example query object for the EPCIS Query Interface Client.
 * 
 * @author David Gubler
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
    private List<QueryParam> queryParameters = new ArrayList<QueryParam>();

    /**
     * Sets the description.
     * 
     * @param description
     *            The describtion for the query.
     */
    public void setDescription(final String description) {
        desc = description;
    }

    /**
     * Gets the description.
     * 
     * @return The description of the query.
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Sets if object events should be returned or not.
     * 
     * @param objectEvents
     *            <code>true</code> if object events should be returned,
     *            <code>false</code> otherwise.
     */
    public void setReturnObjectEvents(final boolean objectEvents) {
        returnObjectEvents = objectEvents;
    }

    /**
     * Gets if object events should be returned or not.
     * 
     * @return <code>true</code> if the query service will return object
     *         events, <code>false</code> otherwise.
     */
    public boolean getReturnObjectEvents() {
        return returnObjectEvents;
    }

    /**
     * Sets if aggregation events should be returned or not.
     * 
     * @param aggregationEvents
     *            <code>true</code> if aggregation events should be returned,
     *            <code>false</code> otherwise.
     */
    public void setReturnAggregationEvents(final boolean aggregationEvents) {
        returnAggregationEvents = aggregationEvents;
    }

    /**
     * Gets if aggregation events should be returned or not.
     * 
     * @return <code>true</code> if the query service will return aggregation
     *         events, <code>false</code> otherwise.
     */
    public boolean getReturnAggregationEvents() {
        return returnAggregationEvents;
    }

    /**
     * Sets if quantity events should be returned or not.
     * 
     * @param quantityEvents
     *            <code>true</code> if quantity events should be returned,
     *            <code>false</code> otherwise.
     */
    public void setReturnQuantityEvents(final boolean quantityEvents) {
        returnQuantityEvents = quantityEvents;
    }

    /**
     * Gets if quantity events should be returned or not.
     * 
     * @return <code>true</code> if the query service will return quantity
     *         events, <code>false</code> otherwise.
     */
    public boolean getReturnQuantityEvents() {
        return returnQuantityEvents;
    }

    /**
     * Sets if transaction events should be returned or not.
     * 
     * @param transactionEvents
     *            <code>true</code> if transaction events should be returned,
     *            <code>false</code> otherwise.
     */
    public void setReturnTransactionEvents(final boolean transactionEvents) {
        returnTransactionEvents = transactionEvents;
    }

    /**
     * Gets if transaction events should be returned or not.
     * 
     * @return <code>true</code> if the query service will return transaction
     *         events, <code>false</code> otherwise.
     */
    public boolean getReturnTransactionEvents() {
        return returnTransactionEvents;
    }

    /**
     * Gets the vector that holds all other query parameters.
     * 
     * @return The List containing the query parameters.
     */
    public List<QueryParam> getQueryParameters() {
        return queryParameters;
    }
}
