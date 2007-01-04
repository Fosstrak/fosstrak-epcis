/**
 * 
 */
package org.accada.epcis.queryclient;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.accada.epcis.soapapi.DuplicateSubscriptionException;
import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.InvalidURIException;
import org.accada.epcis.soapapi.NoSuchNameException;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;
import org.accada.epcis.soapapi.QueryParameterException;
import org.accada.epcis.soapapi.QueryTooComplexException;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.soapapi.SecurityException;
import org.accada.epcis.soapapi.SubscribeNotPermittedException;
import org.accada.epcis.soapapi.SubscriptionControlsException;
import org.accada.epcis.soapapi.ValidationException;

/**
 * @author Marco Steybe
 * 
 */
public interface QueryClientInterface {

    /**
     * Runs the query given in XML at the EPCIS repository.
     * 
     * @param xmlQuery
     *            The query in its XML representation, given as InputStream.
     * @return Depending on the actual implementation, returns a QueryResults
     *         object or the XML representation of the result, given as String.
     * @throws ServiceException
     * @throws QueryTooComplexException
     * @throws ImplementationException
     * @throws QueryTooLargeException
     * @throws QueryParameterException
     * @throws ValidationException
     * @throws SecurityException
     * @throws NoSuchNameException
     * @throws RemoteException
     * @throws IOException
     */
    public Object runQuery(InputStream xmlQuery) throws ServiceException,
                                                QueryTooComplexException,
                                                ImplementationException,
                                                QueryTooLargeException,
                                                QueryParameterException,
                                                ValidationException,
                                                SecurityException,
                                                NoSuchNameException,
                                                RemoteException, IOException;

    /**
     * Registers the query given in XML at the EPCIS repository.
     * 
     * @param subscr
     *            the
     * @throws ServiceException
     * @throws RemoteException
     * @throws NoSuchNameException
     * @throws DuplicateSubscriptionException
     * @throws SecurityException
     * @throws ValidationException
     * @throws QueryParameterException
     * @throws SubscriptionControlsException
     * @throws SubscribeNotPermittedException
     * @throws InvalidURIException
     * @throws ImplementationException
     * @throws QueryTooComplexException
     * @throws IOException
     */
    public void subscribeQuery(InputStream xmlQuery)
                                                    throws ServiceException,
                                                    QueryTooComplexException,
                                                    ImplementationException,
                                                    InvalidURIException,
                                                    SubscribeNotPermittedException,
                                                    SubscriptionControlsException,
                                                    QueryParameterException,
                                                    ValidationException,
                                                    SecurityException,
                                                    DuplicateSubscriptionException,
                                                    NoSuchNameException,
                                                    RemoteException,
                                                    IOException;

    /**
     * Removes the registered query with the given ID from the service.
     * 
     * @param subscriptionId
     *            The ID from the query registered at this service.
     * @throws ServiceException
     * @throws RemoteException
     * @throws SecurityException
     * @throws ValidationException
     * @throws NoSuchSubscriptionException
     * @throws ImplementationException
     */
    public void unsubscribeQuery(String subscriptionId)
                                                       throws ServiceException,
                                                       ImplementationException,
                                                       NoSuchSubscriptionException,
                                                       ValidationException,
                                                       SecurityException,
                                                       RemoteException;

    /**
     * Query for subscribed IDs.
     * 
     * @return All IDs subscribed to a query.
     * @throws ServiceException
     * @throws RemoteException
     * @throws NoSuchNameException
     * @throws SecurityException
     * @throws ValidationException
     * @throws ImplementationException
     */
    public String[] querySubscriptionIds() throws ServiceException,
                                          ImplementationException,
                                          ValidationException,
                                          SecurityException,
                                          NoSuchNameException, RemoteException;

    /**
     * Query the service for the supported standard version.
     * 
     * @return The supported version of the standard.
     * @throws ServiceException
     * @throws ImplementationException
     * @throws ValidationException
     * @throws SecurityException
     * @throws RemoteException
     */
    public String queryStandardVersion() throws ServiceException,
                                        ImplementationException,
                                        ValidationException, SecurityException,
                                        RemoteException;

    /**
     * Query the service for its version.
     * 
     * @return The version of the service.
     * @throws ServiceException
     * @throws ImplementationException
     * @throws ValidationException
     * @throws SecurityException
     * @throws RemoteException
     */
    public String queryVendorVersion() throws ServiceException,
                                      ImplementationException,
                                      ValidationException, SecurityException,
                                      RemoteException;

    /**
     * Query the service for its queries.
     * 
     * @return The subscribed query names.
     * @throws ServiceException
     * @throws ImplementationException
     * @throws ValidationException
     * @throws SecurityException
     * @throws RemoteException
     */
    public String[] queryNames() throws ServiceException,
                                ImplementationException, ValidationException,
                                SecurityException, RemoteException;
}
