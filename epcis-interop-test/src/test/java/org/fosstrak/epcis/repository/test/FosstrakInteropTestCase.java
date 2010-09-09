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

package org.fosstrak.epcis.repository.test;

import java.io.FileInputStream;
import java.io.InputStream;

import org.dbunit.JdbcBasedDBTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

/**
 * All test cases that require a clean state of the database should inherit from
 * this class. Doing so ensures that before running each test case the database
 * is reset with a predefined set of data. If you want to execute a database
 * operation other that {@code DatabaseOperation.CLEAN_INSERT} then you should
 * override {@link #getSetUpOperation()}.
 * 
 * @author Marco Steybe
 */
public class FosstrakInteropTestCase extends JdbcBasedDBTestCase {

    public FosstrakInteropTestCase() {
        super();
    }

    public FosstrakInteropTestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        IDatabaseConnection connection = getConnection();
        IDataSet dataSet = getDataSet();
        String op = getSetUpOperation().getClass().getSimpleName();

        // reset the database
        try {
            long t1 = System.currentTimeMillis();
            getSetUpOperation().execute(connection, dataSet);
            long t2 = System.currentTimeMillis();
            System.out.println("database reset (" + op + ") successful in " + (t2 - t1) + "ms");
        } finally {
            connection.close();
        }
    }
    
    @Override
    protected void tearDown() throws Exception {
        // noop
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        super.setUpDatabaseConfig(config);
        config.setProperty("http://www.dbunit.org/properties/datatypeFactory", new MySqlDataTypeFactory());
    }

    protected IDataSet getDataSet() throws Exception {
        InputStream is = new FileInputStream("src/test/resources/dbunit/epcis_test_data.xml");
        return new XmlDataSet(is);
    }

    @Override
    protected String getConnectionUrl() {
        return "jdbc:mysql://localhost:3306/epcis";
    }

    @Override
    protected String getDriverClass() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    protected String getPassword() {
        return "epcis";
    }

    @Override
    protected String getUsername() {
        return "epcis";
    }
    
    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.NONE;
    }
}
