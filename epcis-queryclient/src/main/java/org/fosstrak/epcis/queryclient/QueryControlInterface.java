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
