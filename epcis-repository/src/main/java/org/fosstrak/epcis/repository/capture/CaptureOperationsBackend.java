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

package org.accada.epcis.repository.capture;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.accada.epcis.repository.model.EventFieldExtension;
import org.accada.epcis.soap.model.BusinessTransactionType;

/**
 * The CaptureOperationsBackend provides the persistence functionality required
 * by the CaptureOperationsModule. It offers methods to store EPCIS events,
 * vocabularies, or EPCs to the database; and provides a method to retrieve
 * already existing vocabularies from the database. A CaptureOperationsSession
 * object which holds the database connection is required to be passed into each
 * of the methods.
 * 
 * @author Marco Steybe
 */
public interface CaptureOperationsBackend {

    /**
     * Runs the dbreset SQL script.
     * 
     * @param dbconnection
     *            The JDBC Connection object.
     * @param dbResetScript
     *            The filename of the SQL script to execute.
     * @throws SQLException
     *             If an error with the database occurred.
     * @throws IOException
     *             If an exception reading from the SQL script occurred.
     */
    public void dbReset(final Connection dbconnection, final String dbResetScript) throws SQLException, IOException;

    /**
     * Opens a new session for the database transaction.
     * 
     * @param dataSource
     *            The DataSource object to retrieve the database connection
     *            from.
     * @return A CaptureOperationsSession instantiated with the database
     *         connection retrieved from the given DataSource.
     * @throws SQLException
     *             If an SQL error occurred.
     */
    public CaptureOperationsSession openSession(final DataSource dataSource) throws SQLException;

    /**
     * Inserts a new EPCIS object event into the database.
     * 
     * @param session
     *            The database session.
     * @param eventTime
     *            The event's 'eventTime' parameter.
     * @param recordTime
     *            The event's 'recordTime' parameter.
     * @param eventTimeZoneOffset
     *            The event's 'eventTimeZoneOffset' parameter.
     * @param bizStepId
     *            The event's 'BusinessStepID' parameter.
     * @param dispositionId
     *            The event's 'DispositionID' parameter.
     * @param readPointId
     *            The event's 'ReadPointID' parameter.
     * @param bizLocationId
     *            The event's 'BusinessLocationID' parameter.
     * @param action
     *            The event's 'action' parameter.
     * @return The database primary key of the inserted EPCIS object event.
     * @throws SQLException
     *             If an SQL exception occurred.
     */
    public Long insertObjectEvent(final CaptureOperationsSession session, final Timestamp eventTime,
            final Timestamp recordTime, final String eventTimeZoneOffset, final Long bizStepId,
            final Long dispositionId, final Long readPointId, final Long bizLocationId, final String action)
            throws SQLException;

    /**
     * Inserts a new EPCIS transaction event into the database.
     * 
     * @param session
     *            The database session.
     * @param eventTime
     *            The event's 'eventTime' parameter.
     * @param recordTime
     *            The event's 'recordTime' parameter.
     * @param eventTimeZoneOffset
     *            The event's 'eventTimeZoneOffset' parameter.
     * @param bizStepId
     *            The event's 'BusinessStepID' parameter.
     * @param dispositionId
     *            The event's 'DispositionID' parameter.
     * @param readPointId
     *            The event's 'ReadPointID' parameter.
     * @param bizLocationId
     *            The event's 'BusinessLocationID' parameter.
     * @param action
     *            The event's 'action' parameter.
     * @param parentId
     *            The event's 'ParentID' parameter.
     * @return The database primary key of the inserted EPCIS transaction event.
     * @throws SQLException
     *             If an SQL exception occurred.
     */
    public Long insertTransactionEvent(final CaptureOperationsSession session, final Timestamp eventTime,
            final Timestamp recordTime, final String eventTimeZoneOffset, final Long bizStepId,
            final Long dispositionId, final Long readPointId, final Long bizLocationId, final String action,
            final String parentId) throws SQLException;

    /**
     * Inserts a new EPCIS aggregation event into the database.
     * 
     * @param session
     *            The database session.
     * @param eventTime
     *            The event's 'eventTime' parameter.
     * @param recordTime
     *            The event's 'recordTime' parameter.
     * @param eventTimeZoneOffset
     *            The event's 'eventTimeZoneOffset' parameter.
     * @param bizStepId
     *            The event's 'BusinessStepID' parameter.
     * @param dispositionId
     *            The event's 'DispositionID' parameter.
     * @param readPointId
     *            The event's 'ReadPointID' parameter.
     * @param bizLocationId
     *            The event's 'BusinessLocationID' parameter.
     * @param action
     *            The event's 'action' parameter.
     * @param parentId
     *            The event's 'ParentID' parameter.
     * @return The database primary key of the inserted EPCIS aggregation event.
     * @throws SQLException
     *             If an SQL exception occurred.
     */
    public Long insertAggregationEvent(final CaptureOperationsSession session, final Timestamp eventTime,
            final Timestamp recordTime, final String eventTimeZoneOffset, final Long bizStepId,
            final Long dispositionId, final Long readPointId, final Long bizLocationId, final String action,
            final String parentId) throws SQLException;

    /**
     * Inserts a new EPCIS quantity event into the database.
     * 
     * @param session
     *            The database session.
     * @param eventTime
     *            The event's 'eventTime' parameter.
     * @param recordTime
     *            The event's 'recordTime' parameter.
     * @param eventTimeZoneOffset
     *            The event's 'eventTimeZoneOffset' parameter.
     * @param bizStepId
     *            The event's 'BusinessStepID' parameter.
     * @param dispositionId
     *            The event's 'DispositionID' parameter.
     * @param readPointId
     *            The event's 'ReadPointID' parameter.
     * @param bizLocationId
     *            The event's 'BusinessLocationID' parameter.
     * @param epcClassId
     *            The event's 'EpcClassID' parameter.
     * @param quantity
     *            The event's 'quantity' parameter.
     * @return The database primary key of the inserted EPCIS quantity event.
     * @throws SQLException
     *             If an SQL exception occurred.
     */
    public Long insertQuantityEvent(final CaptureOperationsSession session, final Timestamp eventTime,
            final Timestamp recordTime, final String eventTimeZoneOffset, final Long bizStepId,
            final Long dispositionId, final Long readPointId, final Long bizLocationId, final Long epcClassId,
            final Long quantity) throws SQLException;

    /**
     * Inserts the given EPCs into the database and associates them with the
     * EPCIS event with the given type and ID (primary key).
     * 
     * @param session
     *            The database session.
     * @param eventId
     *            The ID (primary key) of the EPCIS event for which the EPCs
     *            should be inserted.
     * @param eventType
     *            The type of the EPCIS event for which the EPCs should be
     *            inserted.
     * @param epcs
     *            The List of EPCs to insert.
     * @throws SQLException
     *             If an SQL error occurred.
     */
    public void insertEpcsForEvent(final CaptureOperationsSession session, final long eventId, final String eventType,
            final List<String> epcs) throws SQLException;

    /**
     * Retrieves the ID (primary key) of the vocabulary with the given type that
     * matches the given URI string. If no vocabulary element with the given URI
     * exists, <code>null</code> will be returned.
     * 
     * @param session
     *            The database session.
     * @param vocabularyType
     *            The type of the vocabulary to retrieve. The type determines
     *            from which database table name the primary key will be
     *            retrieved.
     * @param vocabularyElement
     *            The URI string of the vocabulary element to retrieve.
     * @return The ID (primary key) of the matching vocabulary element, or
     *         <code>null</code> if none was found.
     * @throws SQLException
     *             If an SQL error occurred.
     */
    public Long getVocabularyElement(final CaptureOperationsSession session, final String vocabularyType,
            final String vocabularyElement) throws SQLException;

    /**
     * Inserts the vocabulary of the given type and the given vocabulary element
     * URI into the database.
     * 
     * @param session
     *            The database session.
     * @param vocabularyType
     *            The type of the vocabulary to insert. The type determines to
     *            which database table name the vocabulary will be inserted.
     * @param vocabularyElement
     *            The URI string of the vocabulary element to insert.
     * @return The ID (primary key) of the inserted vocabulary element.
     * @throws SQLException
     *             If an SQL error occurred.
     */
    public Long insertVocabularyElement(final CaptureOperationsSession session, final String vocabularyType,
            final String vocabularyElement) throws SQLException;

    /**
     * Inserts the BusinessTransactionType and the BusinessTransactionID into
     * the BusinessTransaction-Table if necessary.
     * 
     * @param session
     *            The database session.
     * @param bizTrans
     *            The BusinessTransaction to be inserted.
     * @param bizTransType
     *            The type of the business transaction to insert.
     * @return The ID from the BusinessTransaction-table.
     * @throws SQLException
     *             If an SQL problem with the database occurred.
     */
    public Long insertBusinessTransaction(final CaptureOperationsSession session, final String bizTrans,
            final String bizTransType) throws SQLException;

    public void insertBusinessTransactionsForEvent(final CaptureOperationsSession session, final long eventId,
            final String eventType, final List<BusinessTransactionType> btts) throws SQLException;

    public void insertExtensionFieldsForEvent(final CaptureOperationsSession session, final long eventId,
            final String eventType, final List<EventFieldExtension> exts) throws SQLException;

}