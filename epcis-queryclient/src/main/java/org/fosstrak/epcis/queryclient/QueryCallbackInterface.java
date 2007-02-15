/**
 * 
 */
package org.accada.epcis.queryclient;

import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryTooLargeException;

/**
 * @author Marco Steybe
 */
public interface QueryCallbackInterface {

    /**
     * Performs a callback for a standing query. When the callback returns, the
     * given QueryResults object will be populated with a result of a standing
     * query.
     * 
     * @param resultData
     *            The QueryResults object to be populated.
     */
    void callbackResults(QueryResults resultData);

    /**
     * Performs a callback for a standing query when the query threw a
     * QueryTooLargeException. When the callback returns, the given
     * QueryTooLargeException object will be populated with the corresponding
     * exception.
     * 
     * @param e
     *            The QueryTooLargeException to be populated
     */
    void callbackQueryTooLargeException(QueryTooLargeException e);

    /**
     * Performs a callback for a standing query when the query threw a
     * ImplementationException. When the callback returns, the given
     * ImplementationException object will be populated with the corresponding
     * exception.
     * 
     * @param e
     *            The ImplementationException to be populated
     */
    void callbackImplementationException(ImplementationException e);
}
