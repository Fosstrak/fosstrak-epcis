/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org).
 *
 * Fosstrak is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Fosstrak is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Fosstrak; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.fosstrak.epcis.repository.capture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO: Javadoc
 * <p>
 * TODO: Can we get rid of this class as we now use Hibernate?
 * 
 * @author Sean Wellington
 */
public class CaptureOperationsSession {

    private static final Log LOG = LogFactory.getLog(CaptureOperationsSession.class);

    private Connection connection;

    private Map<String, PreparedStatement> inserts = new LinkedHashMap<String, PreparedStatement>();
    private Map<String, PreparedStatement> batchInserts = new LinkedHashMap<String, PreparedStatement>();
    private Map<String, PreparedStatement> selects = new HashMap<String, PreparedStatement>();

    public CaptureOperationsSession(final Connection connection) {
        this.connection = connection;
    }

    public PreparedStatement getInsert(final String sql) throws SQLException {
        PreparedStatement ps = inserts.get(sql);
        if (ps == null) {
            ps = connection.prepareStatement(sql);
            inserts.put(sql, ps);
        }
        ps.clearParameters();
        return ps;
    }

    public PreparedStatement getBatchInsert(final String sql) throws SQLException {
        PreparedStatement ps = batchInserts.get(sql);
        if (ps == null) {
            ps = connection.prepareStatement(sql);
            batchInserts.put(sql, ps);
        }
        ps.clearParameters();
        return ps;
    }

    public PreparedStatement getSelect(final String sql) throws SQLException {
        PreparedStatement ps = selects.get(sql);
        if (ps == null) {
            ps = connection.prepareStatement(sql);
            selects.put(sql, ps);
        }
        ps.clearParameters();
        return ps;
    }

    public Connection getConnection() {
        return connection;
    }

    public void commit() throws SQLException {
        for (PreparedStatement ps : batchInserts.values()) {
            ps.executeBatch();
        }
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    public void close() throws SQLException {
        for (PreparedStatement ps : inserts.values()) {
            try {
                ps.close();
            } catch (SQLException e) {
                LOG.warn("Error closing PreparedStatement: " + e.toString() + ". Will continue ... ");
            }
        }
        for (PreparedStatement ps : batchInserts.values()) {
            try {
                ps.close();
            } catch (SQLException e) {
                LOG.warn("Error closing PreparedStatement: " + e.toString() + ". Will continue ... ");
            }
        }
        for (PreparedStatement ps : selects.values()) {
            try {
                ps.close();
            } catch (SQLException e) {
                LOG.warn("Error closing PreparedStatement: " + e.toString() + ". Will continue ... ");
            }
        }
        connection.close();
    }

}
