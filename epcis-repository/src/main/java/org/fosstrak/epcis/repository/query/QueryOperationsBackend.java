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

package org.accada.epcis.repository.query;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.QueryTooLargeExceptionResponse;
import org.accada.epcis.soap.model.QueryParams;
import org.accada.epcis.soap.model.SubscriptionControls;
import org.accada.epcis.soap.model.VocabularyType;

/**
 * The QueryOperationsBackend provides the persistence functionality required by
 * the QueryOperationsModule. It offers methods to manage query subscriptions
 * and methods to execute EPCIS queries, i.e., simple event and masterdata
 * queries. A QueryOperationsSession object which holds the database connection
 * is required to be passed into each of the methods.
 * 
 * @author Marco Steybe
 */
public interface QueryOperationsBackend {

    /**
     * Executes a simple event query with the parameters given in the
     * SimpleEventQueryDTO. The resulting event list will be available in the
     * given <code>eventList</code> parameter.
     * 
     * @param session
     *            The QueryOperationsSession wrapping a database connection.
     * @param seQuery
     *            The SimpleEventQueryDTO containing the query parameters from
     *            which the database query will be constructed.
     * @param eventList
     *            A List of events matching the given query parameters.
     * @throws SQLException
     *             If an error with the database occurred.
     * @throws ImplementationExceptionResponse
     *             If an implementation specific error occurred.
     * @throws QueryTooLargeExceptionResponse
     *             If the query is too large to be executed.
     */
    public void runSimpleEventQuery(final QueryOperationsSession session, final SimpleEventQueryDTO seQuery,
            final List<Object> eventList) throws SQLException, ImplementationExceptionResponse,
            QueryTooLargeExceptionResponse;

    /**
     * Executes a masterdata query with the parameters given in the
     * MasterDataQueryDTO. The resulting vocabulary list will be available in
     * the given <code>vocList</code> parameter.
     * 
     * @param session
     *            The QueryOperationsSession wrapping a database connection.
     * @param mdQuery
     *            The MasterDataQueryDTO containing the query parameters from
     *            which the database query will be constructed.
     * @param vocList
     *            A List of vocabularies matching the given query parameters.
     * @throws SQLException
     *             If an error with the database occurred.
     * @throws ImplementationExceptionResponse
     *             If an implementation specific error occurred.
     * @throws QueryTooLargeExceptionResponse
     *             If the query is too large to be executed.
     */
    public void runMasterDataQuery(final QueryOperationsSession session, final MasterDataQueryDTO mdQuery,
            final List<VocabularyType> vocList) throws SQLException, ImplementationExceptionResponse,
            QueryTooLargeExceptionResponse;

    /**
     * Checks if the given subscription ID already exists.
     * 
     * @param session
     *            The QueryOperationsSession wrapping a database connection.
     * @param subscriptionID
     *            The subscription ID to be checked.
     * @return <code>true</code> if the given subscription ID already exists,
     *         <code>false</code> otherwise.
     * @throws SQLException
     *             If an error with the database occurred.
     */
    public boolean fetchExistsSubscriptionId(final QueryOperationsSession session, final String subscriptionID)
            throws SQLException;

    /**
     * Fetches all query subscription held in the database, starts them again
     * and stores everything in a HasMap.
     * 
     * @param session
     *            The QueryOperationsSession wrapping a database connection.
     * @return A Map mapping query names to scheduled query subscriptions.
     * @throws SQLException
     *             If an error with the database occurred.
     * @throws ImplementationExceptionResponse
     *             If an implementation specific error occurred.
     */
    public Map<String, QuerySubscriptionScheduled> fetchSubscriptions(final QueryOperationsSession session)
            throws SQLException, ImplementationExceptionResponse;

    /**
     * Stores a query subscription with the given parameters to the database.
     * 
     * @param session
     *            The QueryOperationsSession wrapping a database connection.
     * @param queryParams
     * @param dest
     * @param subscrId
     * @param controls
     * @param trigger
     * @param newSubscription
     * @param queryName
     * @param schedule
     * @throws SQLException
     *             If an error with the database occurred.
     * @throws ImplementationExceptionResponse
     *             If an implementation specific error occurred.
     */
    public void storeSupscriptions(final QueryOperationsSession session, QueryParams queryParams, String dest,
            String subscrId, SubscriptionControls controls, String trigger, QuerySubscriptionScheduled newSubscription,
            String queryName, Schedule schedule) throws SQLException, ImplementationExceptionResponse;

    /**
     * Deletes a query subscription from the database.
     * 
     * @param session
     *            The QueryOperationsSession wrapping a database connection.
     * @param subscrId
     *            The ID of the subscription to delete.
     * @throws SQLException
     *             If an error with the database occurred.
     */
    public void deleteSubscription(final QueryOperationsSession session, String subscrId) throws SQLException;

    /**
     * Opens a new session for the database transaction.
     * 
     * @param dataSource
     *            The DataSource object to retrieve the database connection
     *            from.
     * @return A QueryOperationsSession instantiated with the database
     *         connection retrieved from the given DataSource.
     * @throws SQLException
     *             If an error with the database occurred.
     */
    public QueryOperationsSession openSession(final DataSource dataSource) throws SQLException;

}