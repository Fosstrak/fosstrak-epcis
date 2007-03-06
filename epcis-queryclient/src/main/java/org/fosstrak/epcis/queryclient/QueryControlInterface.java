/*
 * Copyright (C) 2007, ETH Zurich
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.accada.epcis.queryclient;

import java.rmi.RemoteException;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.accada.epcis.soapapi.QueryParam;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.SubscriptionControls;
import org.apache.axis.types.URI;

/**
 * @author Marco Steybe
 */
public interface QueryControlInterface {

    /**
     * Performs a poll operation at the repository's Query Controls Module, i.e.
     * runs a query with the given name and parameters.
     * 
     * @param queryName
     *            The name of the query to be executed.
     * @param params
     *            The parameters of the query to be executed.
     * @return The QueryResults.
     * @throws ServiceException
     *             If an exception whithin the repository's Query Controls
     *             Module occured.
     * @throws RemoteException
     *             If an exception communicating with the repository's Query
     *             Controls Module occured.
     */
    QueryResults poll(final String queryName, final QueryParam[] params)
            throws ServiceException, RemoteException;

    /**
     * Performs a subscribe operation at the repository's Query Controls Module,
     * i.e. subscribes a query for later execution.
     * 
     * @param queryName
     *            The name of the query to be executed.
     * @param params
     *            The parameters of the query to be executed.
     * @param dest
     *            The destination address where QueryResults will be sent once
     *            the subscribed query gets executed.
     * @param controls
     *            The SubscriptionControls.
     * @param subscriptionId
     *            The ID of the subscription.
     * @throws ServiceException
     *             If an exception whithin the repository's Query Controls
     *             Module occured.
     * @throws RemoteException
     *             If an exception communicating with the repository's Query
     *             Controls Module occured.
     */
    void subscribe(final String queryName, final QueryParam[] params,
            final URI dest, final SubscriptionControls controls,
            final String subscriptionId) throws ServiceException,
            RemoteException;

    /**
     * Perform an unsubscribe operation at the repository's Query Controls
     * Module, i.e. unsubscribes a previously subscribed query.
     * 
     * @param subscriptionId
     *            The ID of the query to be unsubscribed.
     * @throws ServiceException
     *             If an exception whithin the repository's Query Controls
     *             Module occured.
     * @throws RemoteException
     *             If an exception communicating with the repository's Query
     *             Controls Module occured.
     */
    void unsubscribe(final String subscriptionId) throws ServiceException,
            RemoteException;

    /**
     * Retrieves the names of queries that can be coped with.
     * 
     * @return A List of query names.
     * @throws ServiceException
     *             If an exception whithin the repository's Query Controls
     *             Module occured.
     * @throws RemoteException
     *             If an exception communicating with the repository's Query
     *             Controls Module occured.
     */
    List<String> getQueryNames() throws ServiceException, RemoteException;

    /**
     * Retrieves the ID of a subscribed query.
     * 
     * @param queryName
     *            The name of the query.
     * @return A List of IDs.
     * @throws ServiceException
     *             If an exception whithin the repository's Query Controls
     *             Module occured.
     * @throws RemoteException
     *             If an exception communicating with the repository's Query
     *             Controls Module occured.
     */
    List<String> getSubscriptionIds(final String queryName)
            throws ServiceException, RemoteException;

    /**
     * Retrieves the standard version implemented by this implementation.
     * 
     * @return The implemented standard version.
     * @throws ServiceException
     *             If an exception whithin the repository's Query Controls
     *             Module occured.
     * @throws RemoteException
     *             If an exception communicating with the repository's Query
     *             Controls Module occured.
     */
    String getStandardVersion() throws ServiceException, RemoteException;

    /**
     * Retrieves the vendor version.
     * 
     * @return The vendor version.
     * @throws ServiceException
     *             If an exception whithin the repository's Query Controls
     *             Module occured.
     * @throws RemoteException
     *             If an exception communicating with the repository's Query
     *             Controls Module occured.
     */
    String getVendorVersion() throws ServiceException, RemoteException;
}
