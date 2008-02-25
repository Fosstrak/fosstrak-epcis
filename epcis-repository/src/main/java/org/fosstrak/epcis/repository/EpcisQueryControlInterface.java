/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Accada (www.accada.org).
 *
 * Accada is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Accada is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Accada; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.accada.epcis.repository;

import java.util.List;

import org.accada.epcis.soap.DuplicateSubscriptionExceptionResponse;
import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.InvalidURIExceptionResponse;
import org.accada.epcis.soap.NoSuchNameExceptionResponse;
import org.accada.epcis.soap.NoSuchSubscriptionExceptionResponse;
import org.accada.epcis.soap.QueryParameterExceptionResponse;
import org.accada.epcis.soap.QueryTooComplexExceptionResponse;
import org.accada.epcis.soap.QueryTooLargeExceptionResponse;
import org.accada.epcis.soap.SecurityExceptionResponse;
import org.accada.epcis.soap.SubscribeNotPermittedExceptionResponse;
import org.accada.epcis.soap.SubscriptionControlsExceptionResponse;
import org.accada.epcis.soap.ValidationExceptionResponse;
import org.accada.epcis.soap.model.InvalidURIException;
import org.accada.epcis.soap.model.QueryParameterException;
import org.accada.epcis.soap.model.QueryParams;
import org.accada.epcis.soap.model.QueryResults;
import org.accada.epcis.soap.model.SubscriptionControls;

/**
 * <p>
 * The EPCIS Query Control Interface provides a general framework by which
 * client applications may query EPCIS data. The interface provides both
 * on-demand queries, in which an explicit request from a client causes a query
 * to be executed and results returned in response, and standing queries, in
 * which a client registers ongoing interest in a query and thereafter receives
 * periodic delivery of results via the EPCIS Query Callback Interface without
 * making further requests. These two modes are informally referred to as "pull"
 * and "push", respectively.
 * </p>
 * <p>
 * Standing queries are made by making one or more subscriptions to a previously
 * defined query using the
 * {@link subscribe(String, QueryParams, URI, SubscriptionControls, String)
 * subscribe} method. Results will be delivered periodically via the Query
 * Callback Interface to a specified destination, until the subscription is
 * canceled using the {@link #unsubscribe(String) unsubscribe} method. On-demand
 * queries are made by executing a previously defined query using the
 * {@link poll(String, QueryParams) poll} method. Each invocation of the
 * {@link poll(String, QueryParams) poll} method returns a result directly to
 * the caller. In either case, if the query is parameterized, specific settings
 * for the parameters may be provided as arguments to
 * {@link subscribe(String, QueryParams, URI, SubscriptionControls, String)
 * subscribe} or {@link poll(String, QueryParams) poll}.
 * </p>
 */
public interface EpcisQueryControlInterface {

    /**
     * Registers a subscriber for a previously defined query having the
     * specified name. The <code>params</code> argument provides the values to
     * be used for any named parameters defined by the query. The
     * <code>dest</code> parameter specifies a destination where results from
     * the query are to be delivered, via the Query Callback Interface. The
     * <code>dest</code> parameter is a URI that both identifies a specific
     * binding of the Query Callback Interface to use and specifies addressing
     * information. The <code>controls</code> parameter controls how the
     * subscription is to be processed; in particular, it specifies the
     * conditions under which the query is to be invoked (e.g., specifying a
     * periodic schedule). The <code>subscriptionID</code> is an arbitrary
     * string that is copied into every response delivered to the specified
     * destination, and otherwise not interpreted by the EPCIS service. The
     * client may use the subscriptionID to identify from which subscription a
     * given result was generated, especially when several subscriptions are
     * made to the same destination. The <code>dest</code> argument MAY be
     * null or empty, in which case results are delivered to a pre-arranged
     * destination based on the authenticated identity of the caller. If the
     * EPCIS implementation does not have a destination pre-arranged for the
     * caller, or does not permit this usage, it SHALL raise an
     * <code>InvalidURIException</code>.
     * 
     * @param queryName
     *            The name of a previously defined query for which the
     *            subscriber will be registered.
     * @param params
     *            Provides the values to be used for any named parameters
     *            defined by the query.
     * @param dest
     *            Specifies a destination where results from the query are to be
     *            delivered, via the Query Callback Interface. It is a URI that
     *            both identifies a specific binding of the Query Callback
     *            Interface to use and specifies addressing information. May be
     *            <code>null</code> or empty, in which case results are
     *            delivered to a pre-arranged destination based on the
     *            authenticated identity of the caller.
     * @param controls
     *            Controls how the subscription is to be processed; in
     *            particular, it specifies the conditions under which the query
     *            is to be invoked (e.g., specifying a periodic schedule).
     * @param subscriptionID
     *            An arbitrary string that is copied into every response
     *            delivered to the specified destination, and otherwise not
     *            interpreted by the EPCIS service. The client may use the
     *            subscriptionID to identify from which subscription a given
     *            result was generated, especially when several subscriptions
     *            are made to the same destination.
     * @throws InvalidURIException
     *             If the EPCIS implementation does not have a destination
     *             pre-arranged for the caller, or does not permit this usage.
     * @throws QueryParameterException
     *             Under any of the following circumstances:
     *             <ul>
     *             <li>A parameter required by the specified query was omitted
     *             or was supplied with an empty value</li>
     *             <li>A parameter was supplied whose name does not correspond
     *             to any parameter name defined by the specified query</li>
     *             <li>Two parameters are supplied having the same name</li>
     *             <li>Any other constraint imposed by the specified query is
     *             violated. Such constraints may include restrictions on the
     *             range of values permitted for a given parameter, requirements
     *             that two or more parameters be mutually exclusive or must be
     *             supplied together, and so on. The specific constraints
     *             imposed by a given query are specified in the documentation
     *             for that query.</li>
     *             </ul>
     */
    public void subscribe(String queryName, QueryParams params, String dest, SubscriptionControls controls,
            String subscriptionID) throws NoSuchNameExceptionResponse, InvalidURIExceptionResponse,
            DuplicateSubscriptionExceptionResponse, QueryParameterExceptionResponse, QueryTooComplexExceptionResponse,
            SubscriptionControlsExceptionResponse, SubscribeNotPermittedExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse, ImplementationExceptionResponse;

    /**
     * Removes a previously registered subscription having the specified
     * <code>subscriptionID</code>.
     * 
     * @param subscriptionID
     *            The <code>subscriptionID</code> of a previously registered
     *            subscription.
     */
    public void unsubscribe(String subscriptionID) throws NoSuchSubscriptionExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse;

    /**
     * Invokes a previously defined query having the specified name, returning
     * the results. The <code>params</code> argument provides the values to be
     * used for any named parameters defined by the query.
     * 
     * @param queryName
     *            The name of a previously defined query to be invoked.
     * @param params
     *            Provides the values to be used for any named parameters
     *            defined by the query.
     * @return The results of the query invocation.
     * @throws QueryParameterException
     *             Under any of the following circumstances:
     *             <ul>
     *             <li>A parameter required by the specified query was omitted
     *             or was supplied with an empty value</li>
     *             <li>A parameter was supplied whose name does not correspond
     *             to any parameter name defined by the specified query</li>
     *             <li>Two parameters are supplied having the same name</li>
     *             <li>Any other constraint imposed by the specified query is
     *             violated. Such constraints may include restrictions on the
     *             range of values permitted for a given parameter, requirements
     *             that two or more parameters be mutually exclusive or must be
     *             supplied together, and so on. The specific constraints
     *             imposed by a given query are specified in the documentation
     *             for that query.</li>
     *             </ul>
     */
    public QueryResults poll(String queryName, QueryParams params) throws NoSuchNameExceptionResponse,
            QueryParameterExceptionResponse, QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse;

    /**
     * Returns a list of all query names available for use with the subscribe
     * and poll methods. This includes all pre-defined queries provided by the
     * implementation, including those specified in the EPCIS standard in
     * Section 8.2.7.
     * 
     * @return A list of all query names available for use with the subscribe
     *         and poll methods.
     */
    public List<String> getQueryNames() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse;

    /**
     * Returns a list of all <code>subscriptionID</code>s currently
     * subscribed to the specified named query.
     * 
     * @param queryName
     *            The name of a previously defined query for which the
     *            <code>subscriptionID</code>s should be returned.
     * @return A list of all <code>subscriptionID</code>s currently
     *         subscribed to the specified named query.
     */
    public List<String> getSubscriptionIDs(String queryName) throws NoSuchNameExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse;

    /**
     * Returns a string that identifies what version of the specification this
     * implementation complies with. The possible values for this string are
     * defined by EPCglobal. An implementation SHALL return a string
     * corresponding to a version of this specification to which the
     * implementation fully complies, and SHOULD return the string corresponding
     * to the latest version to which it complies. To indicate compliance with
     * this Version 1.0 of the EPCIS specification, the implementation SHALL
     * return the string <code>1.0</code>.
     * 
     * @return A string that identifies what version of the specification this
     *         implementation complies with.
     */
    public String getStandardVersion() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse;

    /**
     * Returns a string that identifies what vendor extensions this
     * implementation provides. The possible values of this string and their
     * meanings are vendor-defined, except that the empty string SHALL indicate
     * that the implementation implements only standard functionality with no
     * vendor extensions. When an implementation chooses to return a non-empty
     * string, the value returned SHALL be a URI where the vendor is the owning
     * authority. For example, this may be an HTTP URL whose authority portion
     * is a domain name owned by the vendor, a URN having a URN namespace
     * identifier issued to the vendor by IANA, an OID URN whose initial path is
     * a Private Enterprise Number assigned to the vendor, etc.
     * 
     * @return A string that identifies what vendor extensions this
     *         implementation provides.
     */
    public String getVendorVersion() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse;
}
