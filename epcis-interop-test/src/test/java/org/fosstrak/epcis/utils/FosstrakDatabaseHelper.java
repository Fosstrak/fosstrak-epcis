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

package org.fosstrak.epcis.utils;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.ext.mysql.MySqlConnection;

/**
 * This utility class provides helper methods to access and manipulate the
 * database required by the classes in the interop-test module.
 * 
 * @author Marco Steybe
 */
public class FosstrakDatabaseHelper {

    /**
     * Extracts the current state of the database into a flat DBUnit XML file.
     * 
     * @throws Exception
     *             If any errors occur connecting to the database, reading the
     *             database contents, or writing the XML file.
     */
    public static void exportDatabase() throws Exception {
        IDatabaseConnection connection = getDatabaseConnection();
        IDataSet fullDataSet = connection.createDataSet();
        XmlDataSet.write(fullDataSet, new FileOutputStream("src/test/resources/dbunit/export.xml"));
    }

    private static IDatabaseConnection getDatabaseConnection() throws ClassNotFoundException, SQLException,
            DatabaseUnitException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/epcis?autoReconnect=true",
                "epcis", "epcis");
        IDatabaseConnection connection = new MySqlConnection(jdbcConnection, "epcis");
        return connection;
    }

    /**
     * Extracts the XML DTD from the database schema.
     * 
     * @throws Exception
     *             If any errors occur connecting to the database, reading the
     *             database contents, or writing the DTD file.
     */
    public static void extractXmlSchema() throws Exception {
        IDatabaseConnection connection = getDatabaseConnection();
        FlatDtdDataSet.write(connection.createDataSet(), new FileOutputStream("src/test/resources/dbunit/export.dtd"));
    }

    public static void main(String... args) throws Exception {
        exportDatabase();
//        extractXmlSchema();
    }
    
    public static ITable getObjectEventByEpc(IDatabaseConnection connection, String epc) throws Exception {
        String sql = "SELECT event.eventTime, epc.epc FROM `event_objectevent_epcs` epc, `event_objectevent` event WHERE epc.event_id=event.id AND epc.epc='" + epc +"'";
        ITable table = connection.createQueryTable("SingleObjectEvent", sql);
        return table;
    }

    public static ITable getTransactionEventByEpc(IDatabaseConnection connection, String epc) throws Exception {
        String sql = "SELECT event.eventTime, epc.epc FROM `event_transactionevent_epcs` epc, `event_transactionevent` event WHERE epc.event_id=event.id AND epc.epc='" + epc +"'";
        ITable table = connection.createQueryTable("SingleTransactionEvent", sql);
        return table;
    }

    public static ITable getQuantityEventByEpcClass(IDatabaseConnection connection, String epcClass) throws Exception {
        String sql = "SELECT event.eventTime, epc.uri FROM `voc_epcclass` epc, `event_quantityevent` event WHERE epc.id=event.epcClass AND epc.uri='" + epcClass +"'";
        ITable table = connection.createQueryTable("SingleQuantityEvent", sql);
        return table;
    }

    public static ITable getAggregationEventByChildEpc(IDatabaseConnection connection, String epc) throws Exception {
        String sql = "SELECT event.eventTime, epc.epc FROM `event_aggregationevent_epcs` epc, `event_aggregationevent` event WHERE epc.event_id=event.id AND epc.epc='" + epc +"'";
        ITable table = connection.createQueryTable("SingleAggregationEvent", sql);
        return table;
    }
}
