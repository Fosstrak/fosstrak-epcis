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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO: Javadoc
 * 
 * @author Marco Steybe
 */
public class QueryOperationsSession {

    private static final Log LOG = LogFactory.getLog(QueryOperationsSession.class);

    private Connection connection;

    private Map<String, PreparedStatement> namedStatements = new HashMap<String, PreparedStatement>();

    public QueryOperationsSession(final Connection connection) {
        this.connection = connection;
    }

    public PreparedStatement getPreparedStatement(final String sql) throws SQLException {
        PreparedStatement ps = namedStatements.get(sql);
        if (ps == null) {
            ps = connection.prepareStatement(sql);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Prepared SQL statement: " + sql);
            }
            namedStatements.put(sql, ps);
        }
        ps.clearParameters();
        return ps;
    }

    public Connection getConnection() {
        return connection;
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    public void close() throws SQLException {
        for (PreparedStatement ps : namedStatements.values()) {
            try {
                ps.close();
            } catch (SQLException e) {
                LOG.warn("Error closing prepared statement: " + e.toString() + ". Will continue ... ");
            }
        }
        connection.close();
        LOG.debug("Database connection for session closed");
    }
}
