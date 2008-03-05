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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.accada.epcis.repository.EpcisConstants;
import org.accada.epcis.repository.model.EventFieldExtension;
import org.accada.epcis.soap.model.BusinessTransactionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The CaptureOperationsBackend provides persistence functionality for the
 * CaptureOperationsModule. It offers the functionality to store the different
 * EPCIS events to the underlying database. A CaptureOperationsSession object
 * which wraps the JDBC Connection is required to be passed into each of the
 * methods in this class.
 * <p>
 * TODO: Can we get rid of this class as we now use Hibernate?
 * 
 * @author Alain Remund
 * @author Marco Steybe
 * @author Sean Wellington
 */
public class CaptureOperationsBackend {

    private static final Log LOG = LogFactory.getLog(CaptureOperationsBackend.class);

    /**
     * The ObjectEvent-query without data.
     */
    private static final String SQL_INSERT_OBJECTEVENT = "INSERT INTO event_ObjectEvent ("
            + "eventTime, recordTime, eventTimeZoneOffset, bizStep, disposition, readPoint, bizLocation, action"
            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * The AggregationEvent-query without data.
     */
    private static final String SQL_INSERT_AGGREGATIONEVENT = "INSERT INTO event_AggregationEvent ("
            + "eventTime, recordTime, eventTimeZoneOffset, bizStep, disposition, readPoint, bizLocation, action, parentID "
            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * The QuantityEvent-query without data.
     */
    private static final String SQL_INSERT_QUANTITYEVENT = "INSERT INTO event_QuantityEvent ("
            + "eventTime, recordTime, eventTimeZoneOffset, bizStep, disposition, readPoint, bizLocation, epcClass, quantity"
            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * The TransactionEvent-query without data.
     */
    private static final String SQL_INSERT_TRANSACTIONEVENT = "INSERT INTO event_TransactionEvent ("
            + "eventTime, recordTime, eventTimeZoneOffset, bizStep, disposition, readPoint, bizLocation, action, parentID"
            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final Map<String, String> VOCABTYPE_TABLENAME_MAP;

    static {
        VOCABTYPE_TABLENAME_MAP = new HashMap<String, String>();
        VOCABTYPE_TABLENAME_MAP.put(EpcisConstants.DISPOSITION_ID, "voc_Disposition");
        VOCABTYPE_TABLENAME_MAP.put(EpcisConstants.READ_POINT_ID, "voc_ReadPoint");
        VOCABTYPE_TABLENAME_MAP.put(EpcisConstants.EPC_CLASS, "voc_EPCClass");
        VOCABTYPE_TABLENAME_MAP.put(EpcisConstants.BUSINESS_LOCATION_ID, "voc_BizLoc");
        VOCABTYPE_TABLENAME_MAP.put(EpcisConstants.BUSINESS_STEP_ID, "voc_BizStep");
        VOCABTYPE_TABLENAME_MAP.put(EpcisConstants.BUSINESS_TRANSACTION_TYPE_ID, "voc_BizTransType");
        VOCABTYPE_TABLENAME_MAP.put(EpcisConstants.BUSINESS_TRANSACTION, "voc_BizTrans");
    }

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
    public void dbReset(final Connection dbconnection, final String dbResetScript) throws SQLException, IOException {
        LOG.info("Running db reset script.");
        Statement stmt = null;
        try {
            stmt = dbconnection.createStatement();
            if (dbResetScript != null) {
                BufferedReader reader = new BufferedReader(new FileReader(dbResetScript));
                String line;
                while ((line = reader.readLine()) != null) {
                    stmt.addBatch(line);
                }
            }
            stmt.executeBatch();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

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
    public CaptureOperationsSession openSession(final DataSource dataSource) throws SQLException {
        return new CaptureOperationsSession(dataSource.getConnection());
    }

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
    public long insertObjectEvent(final CaptureOperationsSession session, final Timestamp eventTime,
            final Timestamp recordTime, final String eventTimeZoneOffset, final Long bizStepId,
            final Long dispositionId, final Long readPointId, final Long bizLocationId, final String action)
            throws SQLException {
        return insertEvent(session, eventTime, recordTime, eventTimeZoneOffset, bizStepId, dispositionId, readPointId,
                bizLocationId, action, null, null, null, EpcisConstants.OBJECT_EVENT);
    }

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
    public long insertTransactionEvent(final CaptureOperationsSession session, final Timestamp eventTime,
            final Timestamp recordTime, final String eventTimeZoneOffset, final Long bizStepId,
            final Long dispositionId, final Long readPointId, final Long bizLocationId, final String action,
            final String parentId) throws SQLException {
        return insertEvent(session, eventTime, recordTime, eventTimeZoneOffset, bizStepId, dispositionId, readPointId,
                bizLocationId, action, parentId, null, null, EpcisConstants.TRANSACTION_EVENT);
    }

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
    public long insertAggregationEvent(final CaptureOperationsSession session, final Timestamp eventTime,
            final Timestamp recordTime, final String eventTimeZoneOffset, final Long bizStepId,
            final Long dispositionId, final Long readPointId, final Long bizLocationId, final String action,
            final String parentId) throws SQLException {
        return insertEvent(session, eventTime, recordTime, eventTimeZoneOffset, bizStepId, dispositionId, readPointId,
                bizLocationId, action, parentId, null, null, EpcisConstants.AGGREGATION_EVENT);
    }

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
    public long insertQuantityEvent(final CaptureOperationsSession session, final Timestamp eventTime,
            final Timestamp recordTime, final String eventTimeZoneOffset, final Long bizStepId,
            final Long dispositionId, final Long readPointId, final Long bizLocationId, final Long epcClassId,
            final long quantity) throws SQLException {
        return insertEvent(session, eventTime, recordTime, eventTimeZoneOffset, bizStepId, dispositionId, readPointId,
                bizLocationId, null, null, epcClassId, quantity, EpcisConstants.QUANTITY_EVENT);

    }

    /**
     * Inserts a new EPCIS event into the database by supplying a
     * PreparedStatement with the given parameters.
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
     * @param epcClassId
     *            The event's 'EpcClassID' parameter.
     * @param quantity
     *            The event's 'quantity' parameter.
     * @param eventName
     *            The name of the event.
     * @return The database primary key of the inserted EPCIS event.
     * @throws SQLException
     *             If an SQL exception occurred.
     */
    private long insertEvent(final CaptureOperationsSession session, final Timestamp eventTime,
            final Timestamp recordTime, final String eventTimeZoneOffset, final Long bizStepId,
            final Long dispositionId, final Long readPointId, final Long bizLocationId, final String action,
            final String parentId, final Long epcClassId, final Long quantity, final String eventName)
            throws SQLException {

        PreparedStatement ps;
        if (eventName.equals(EpcisConstants.AGGREGATION_EVENT)) {
            ps = session.getInsert(SQL_INSERT_AGGREGATIONEVENT);
        } else if (eventName.equals(EpcisConstants.OBJECT_EVENT)) {
            ps = session.getInsert(SQL_INSERT_OBJECTEVENT);
        } else if (eventName.equals(EpcisConstants.QUANTITY_EVENT)) {
            ps = session.getInsert(SQL_INSERT_QUANTITYEVENT);
        } else if (eventName.equals(EpcisConstants.TRANSACTION_EVENT)) {
            ps = session.getInsert(SQL_INSERT_TRANSACTIONEVENT);
        } else {
            throw new SQLException("Encountered unknown event element '" + eventName + "'.");
        }

        // parameters 1-7 of the sql query are shared by all events

        ps.setTimestamp(1, eventTime);
        // according to the specification: recordTime is the time of capture
        ps.setTimestamp(2, recordTime != null ? recordTime : new Timestamp(System.currentTimeMillis()));
        // note: for testing it is handy to set recordTime=eventTime
        // ps.setTimestamp(2, eventTime);
        ps.setString(3, eventTimeZoneOffset);
        if (bizStepId != null) {
            ps.setLong(4, bizStepId.longValue());
        } else {
            ps.setNull(4, java.sql.Types.BIGINT);
        }
        if (dispositionId != null) {
            ps.setLong(5, dispositionId.longValue());
        } else {
            ps.setNull(5, java.sql.Types.BIGINT);
        }
        if (readPointId != null) {
            ps.setLong(6, readPointId);
        } else {
            ps.setNull(6, java.sql.Types.BIGINT);
        }
        if (bizLocationId != null) {
            ps.setLong(7, bizLocationId);
        } else {
            ps.setNull(7, java.sql.Types.BIGINT);
        }

        // special handling for QuantityEvent
        if (eventName.equals("QuantityEvent")) {
            if (epcClassId != null) {
                ps.setLong(8, epcClassId);
            } else {
                ps.setNull(8, java.sql.Types.BIGINT);
            }
            if (quantity != null) {
                ps.setLong(9, quantity.longValue());
            } else {
                ps.setNull(9, java.sql.Types.BIGINT);
            }
        } else {
            // all other events have action
            ps.setString(8, action);

            // AggregationEvent and TransactionEvent have a parentID field
            if (eventName.equals("AggregationEvent") || eventName.equals("TransactionEvent")) {
                ps.setString(9, parentId);
            }
        }

        ps.executeUpdate();

        return getLastAutoIncrementedId(session, "event_" + eventName);
    }

    /**
     * Retrieves the last inserted ID chosen by the autoIncrement functionality
     * in the table with the given name.
     * 
     * @param session
     *            The database session.
     * @param tableName
     *            The name of the table for which the last inserted ID should be
     *            retrieved.
     * @return The last auto incremented ID.
     * @throws SQLException
     *             If an SQL problem with the database occurred.
     */
    private long getLastAutoIncrementedId(final CaptureOperationsSession session, final String tableName)
            throws SQLException {
        String stmt = "SELECT LAST_INSERT_ID() as id FROM " + tableName;
        PreparedStatement ps = session.getSelect(stmt);
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            rs.next();
            return rs.getLong("id");
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

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
            final List<String> epcs) throws SQLException {
        // preparing statement for insertion of associated EPCs
        String insert = "INSERT INTO event_" + eventType + "_EPCs (event_id, epc) VALUES (?, ?)";
        PreparedStatement ps = session.getBatchInsert(insert);
        LOG.debug("INSERT: " + insert);

        // insert all EPCs in the EPCs array
        for (String epc : epcs) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("       insert param 1: " + eventId);
                LOG.debug("       insert param 2: " + epc.toString());
            }
            ps.setLong(1, eventId);
            ps.setString(2, epc.toString());
            ps.addBatch();
        }
    }

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
            final String vocabularyElement) throws SQLException {
        String stmt = "SELECT id FROM " + VOCABTYPE_TABLENAME_MAP.get(vocabularyType) + " WHERE uri=?";
        PreparedStatement ps = session.getSelect(stmt);
        ps.setString(1, vocabularyElement.toString());
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            if (rs.next()) {
                // the uri already exists
                return Long.valueOf(rs.getLong("id"));
            } else {
                return null;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

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
    public long insertVocabularyElement(final CaptureOperationsSession session, final String vocabularyType,
            final String vocabularyElement) throws SQLException {
        String tableName = VOCABTYPE_TABLENAME_MAP.get(vocabularyType);
        String stmt = "INSERT INTO " + tableName + " (uri) VALUES (?)";
        if (LOG.isDebugEnabled()) {
            LOG.debug("INSERT: " + stmt);
            LOG.debug("       insert param 1: " + vocabularyElement.toString());
        }

        PreparedStatement ps = session.getInsert(stmt);
        ps.setString(1, vocabularyElement.toString());
        ps.executeUpdate();

        // get last auto_increment value and return it
        return getLastAutoIncrementedId(session, tableName);

    }

    /**
     * Retrieves the business transaction with the given type and the given URI
     * from the database.
     * 
     * @param session
     *            The database session.
     * @param bizTrans
     *            The business transaction URI to insert.
     * @param bizTransType
     *            The type of the business transaction to insert.
     * @return The ID (primary key) of the matching business transaction, or
     *         <code>null</code> if none was found.
     * @throws SQLException
     *             If an SQL error occurred.
     */
    private Long getBusinessTransaction(final CaptureOperationsSession session, final String bizTrans,
            final String bizTransType) throws SQLException {
        String stmt = "select id from BizTransaction where bizTrans = (select id from voc_BizTrans where uri = ?) and type = (select id from voc_BizTransType where uri = ?);";
        PreparedStatement ps = session.getSelect(stmt);
        ps.setString(1, bizTrans.toString());
        ps.setString(2, bizTransType.toString());

        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            if (rs.next()) {
                // the BusinessTransaction already exists
                return rs.getLong("id");
            } else {
                return null;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

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
            final String bizTransType) throws SQLException {

        final long id = getOrInsertVocabularyElement(session, EpcisConstants.BUSINESS_TRANSACTION, bizTrans);
        final long type = getOrInsertVocabularyElement(session, EpcisConstants.BUSINESS_TRANSACTION_TYPE_ID,
                bizTransType);

        String stmt = "INSERT INTO BizTransaction (bizTrans, type) VALUES (?, ?)";
        if (LOG.isDebugEnabled()) {
            LOG.debug("INSERT: " + stmt);
            LOG.debug("       insert param 1: " + bizTrans);
            LOG.debug("       insert param 2: " + bizTransType);
        }

        PreparedStatement ps = session.getInsert(stmt);
        ps.setLong(1, id);
        ps.setLong(2, type);
        ps.executeUpdate();

        return getLastAutoIncrementedId(session, "BizTransaction");
    }

    private long getOrInsertVocabularyElement(final CaptureOperationsSession session, final String vocabularyType,
            final String vocabularyElement) throws SQLException {
        Long vocabularyElementId = getVocabularyElement(session, vocabularyType, vocabularyElement);
        if (vocabularyElementId != null) {
            return vocabularyElementId;
        } else {
            return insertVocabularyElement(session, vocabularyType, vocabularyElement);
        }
    }

    public void insertBusinessTransactionsForEvent(final CaptureOperationsSession session, final long eventId,
            final String eventType, final List<BusinessTransactionType> btts) throws SQLException {
        // preparing statement for insertion of associated EPCs

        List<Long> btIds = new ArrayList<Long>();
        for (BusinessTransactionType btt : btts) {
            btIds.add(getOrInsertBizTransaction(session, btt.getValue(), btt.getType()));
        }

        String insert = "INSERT INTO event_" + eventType + "_bizTrans (event_id, bizTrans_id) VALUES (?, ?)";
        if (LOG.isDebugEnabled()) {
            LOG.debug("INSERT: " + insert);
        }
        PreparedStatement ps = session.getBatchInsert(insert);
        // insert all BizTransactions into the BusinessTransaction-Table
        // and connect it with the "event_<event-name>_bizTrans"-Table
        for (long btId : btIds) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("       insert param 1: " + eventId);
                LOG.debug("       insert param 2: " + btId);
            }
            ps.setLong(1, eventId);
            ps.setLong(2, btId);
            ps.addBatch();
        }
    }

    private Long getOrInsertBizTransaction(final CaptureOperationsSession session, final String bizTrans,
            final String bizTransType) throws SQLException {
        Long bizTransactionId = getBusinessTransaction(session, bizTrans, bizTransType);
        if (bizTransactionId != null) {
            return bizTransactionId;
        } else {
            return insertBusinessTransaction(session, bizTrans, bizTransType);
        }
    }

    public void insertExtensionFieldsForEvent(final CaptureOperationsSession session, final long eventId,
            final String eventType, final List<EventFieldExtension> exts) throws SQLException {
        for (EventFieldExtension ext : exts) {
            String insert = "INSERT INTO event_" + eventType + "_extensions " + "(event_id, fieldname, prefix, "
                    + ext.getValueColumnName() + ") VALUES (?, ? ,?, ?)";
            PreparedStatement ps = session.getBatchInsert(insert);
            if (LOG.isDebugEnabled()) {
                LOG.debug("INSERT: " + insert);
                LOG.debug("       insert param 1: " + eventId);
                LOG.debug("       insert param 2: " + ext.getFieldname());
                LOG.debug("       insert param 3: " + ext.getPrefix());
                LOG.debug("       insert param 4: " + ext.getStrValue());
            }
            ps.setLong(1, eventId);
            ps.setString(2, ext.getFieldname());
            ps.setString(3, ext.getPrefix());
            if (ext.getIntValue() != null) {
                ps.setInt(4, ext.getIntValue().intValue());
            } else if (ext.getFloatValue() != null) {
                ps.setFloat(4, ext.getFloatValue().floatValue());
            } else if (ext.getDateValue() != null) {
                ps.setTimestamp(4, ext.getDateValue());
            } else {
                ps.setString(4, ext.getStrValue());
            }
            ps.addBatch();
        }

    }
}
