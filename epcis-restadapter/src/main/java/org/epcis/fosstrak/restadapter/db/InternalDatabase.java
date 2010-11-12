/*
 * Copyright (C) 2010 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org) and
 * was developed as part of the webofthings.com initiative.
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
package org.epcis.fosstrak.restadapter.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epcis.fosstrak.restadapter.db.exceptions.NotFoundInDBException;

/**
 * The internal MySQL Database of the EPCIS REST Adapter.
 * NOTE: This class is really suboptimal and is candidate for a complete re-write
 * for the next release!
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
public class InternalDatabase {

    private static String TABLE_SUBSCRIPTION = "subscriptions";
    private static String TABLE_LOCATION = "locations";
    private static String TABLE_READER = "readers";
    private static String TABLE_LOCATION_READER_CONNECTION = "connections";
    private static String TABLE_FEED = "feeds";
    private static String TABLE_ENTRIES = "entries";
    private static String TABLE_CONFIGURATION = "configuration";
    private static String DB_NAME = "epcis-restadapter";
    private static boolean IS_RESET_DB = false;
    private String sqliteHome;
    private static String DRIVER = "org.sqlite.JDBC";
    private static InternalDatabase instance = null;

    /**
     * @return the IS_RESET_DB
     */
    public static boolean GET_IS_RESET_DB() {
        return IS_RESET_DB;
    }

    /**
     * @param aIS_RESET_DB the IS_RESET_DB to set
     *
     * @param isResetDB
     */
    public static void SET_IS_RESET_DB(boolean isResetDB) {
        IS_RESET_DB = isResetDB;
    }

    private InternalDatabase() {
        sqliteHome = System.getProperty("sqlite.system.home");
        Logger.getLogger(InternalDatabase.class.getName()).log(Level.INFO, "sqliteHome home is: {0}", sqliteHome);
        initializeIfNotPresent();
    }

    /**
     * Get the Singleton Instance
     *
     *
     * @return
     */
    public synchronized static InternalDatabase getInstance() {
        if (instance == null) {
            instance = new InternalDatabase();
        }

        return instance;
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection("jdbc:sqlite:" + sqliteHome + DB_NAME + ".db");
        } catch (SQLException ex) {
            Logger.getLogger(InternalDatabase.class.getName()).log(Level.INFO, "An error occured while connecting to the database", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InternalDatabase.class.getName()).log(Level.SEVERE, "Database driver not found when trying "
                    + "to create feeds database.", ex);
        }
        return connection;
    }

    /**
     * Resets the RESTful EPCIS internal Database
     *
     */
    public synchronized void resetDB() {
        boolean isResetBefore = GET_IS_RESET_DB();
        SET_IS_RESET_DB(true);
        initDB();
        SET_IS_RESET_DB(isResetBefore);
    }

    /**
     * This method checks whether the DB already exist, if it does not then it
     * will create it.
     */
    private synchronized void initializeIfNotPresent() {
        try {
            getConfigurationEntry("lastInitialized");
        } catch (NotFoundInDBException ex) {
            initDB();
            storeConfigurationEntry("lastInitialized", Long.toString(Calendar.getInstance().getTimeInMillis()));
        }
    }

    /** This method will initialize the DB */
    private void initDB() {
        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();

            if (GET_IS_RESET_DB()) {
                statement.executeUpdate("drop table if exists " + TABLE_SUBSCRIPTION + ";");
                statement.executeUpdate("drop table if exists " + TABLE_LOCATION + ";");
                statement.executeUpdate("drop table if exists " + TABLE_READER + ";");
                statement.executeUpdate("drop table if exists " + TABLE_LOCATION_READER_CONNECTION + ";");
                statement.executeUpdate("drop table if exists " + TABLE_FEED + ";");
                statement.executeUpdate("drop table if exists " + TABLE_ENTRIES + ";");
                statement.executeUpdate("drop table if exists " + TABLE_CONFIGURATION + ";");
            }

            statement.executeUpdate("create table " + TABLE_SUBSCRIPTION + " (id, alias);");
            statement.executeUpdate("create table " + TABLE_LOCATION + " (location);");
            statement.executeUpdate("create table " + TABLE_READER + " (reader);");
            statement.executeUpdate("create table " + TABLE_LOCATION_READER_CONNECTION + " (location, reader);");
            statement.executeUpdate("create table " + TABLE_FEED + " (id, title, subtitle, author, category, contributor, link, logo);");
            statement.executeUpdate("create table " + TABLE_ENTRIES + " (id, feed_id, summary, summaryMXL, updated, published, link, title);");
            statement.executeUpdate("create table " + TABLE_CONFIGURATION + " (key, value);");
        } catch (Exception ex) {
            // ex.printStackTrace();
            // do nothing as the db already exists
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                // ex.printStackTrace();
                // just try to close the connection if it exists
            }
        }
    }

    /**
     * This method returns a configuration entry stored in the database
     * given its key.
     * @param key The key to the configuration value.
     * @return The stored configuration value as a String.
     */
    public synchronized String getConfigurationEntry(String key) throws NotFoundInDBException {
        Connection connection = null;
        String result = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("select * from " + TABLE_CONFIGURATION + " where key='" + key + "';");
            if (rs.next()) {
                result = rs.getString("value");
                rs.close();
            } else {
                throw new NotFoundInDBException("Configuration Entry " + key + "not found in database!");
            }


        } catch (SQLException ex) {
            Logger.getLogger(InternalDatabase.class.getName()).log(Level.SEVERE, "Unable to load configuration entry: " + key, ex);
            throw new NotFoundInDBException("Configuration Entry " + key + "not found in database!");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InternalDatabase.class.getName()).log(Level.SEVERE, "Unable to load database driver!", ex);
        } finally {

            // close DB connection
            try {

                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

    /**
     * This method stores a configuration entry to the database. If the entry
     * already exist it updates it.
     * @param key The key to the configuration entry.
     * @param value The value of the entry.
     */
    public synchronized void storeConfigurationEntry(String key, String value) {
        //First we delete the entry if it already exist
        deleteConfigurationValue(key);

        //then we add an entry:
        Connection connection = null;
        try {
            Class.forName(DRIVER);
            connection = getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into " + TABLE_CONFIGURATION + " values ('" + key + "', '" + value + "');");
        } catch (SQLException ex) {
            Logger.getLogger(InternalDatabase.class.getName()).log(Level.SEVERE, "Unable to add entry to the configuration table.", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InternalDatabase.class.getName()).log(Level.SEVERE, "Unable to load database driver!", ex);
        }
        // close DB connection
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method deletes a configuration value from the database. It
     * does not raise an exception in case the value did not exist.
     * @param key The key of the entry to be deleted.
     */
    public synchronized void deleteConfigurationValue(String key) {
        Connection connection = null;
        try {
            Class.forName(DRIVER);
            connection = getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from " + TABLE_CONFIGURATION + " where key='" + key + "'");
        } catch (SQLException ex) {
            Logger.getLogger(InternalDatabase.class.getName()).log(Level.INFO, "Trying to delete a non existing configuration entry from database.", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InternalDatabase.class.getName()).log(Level.SEVERE, "Unable to load database driver!", ex);
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Tests if the ID is contained in the subscription
     *
     *
     * @param id
     *
     * @return
     */
    public synchronized boolean containsSubscriptionID(String id) {
        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from " + TABLE_SUBSCRIPTION + ";");

            while (rs.next()) {
                String temp = rs.getString("id");

                if (temp.equals(id)) {
                    return true;
                }
            }

            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Gets the Subscription Alias of the ID
     *
     *
     * @param id
     *
     * @return
     */
    public synchronized String getSubscriptionAlias(String id) {
        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from " + TABLE_SUBSCRIPTION + ";");

            while (rs.next()) {
                String temp = rs.getString("id");

                if (temp.equals(id)) {
                    return rs.getString("alias");
                }
            }

            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Gets the next free Alias
     *
     *
     * @return
     */
    public synchronized int getNextFreeAlias() {
        int res = -1;
        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from " + TABLE_SUBSCRIPTION + ";");

            while (rs.next()) {
                String temp = rs.getString("alias");

                try {
                    int i = Integer.parseInt(temp);

                    if (res <= i) {
                        res = i;
                    }
                } catch (Exception ex) {
                    // ignore it
                }
            }

            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return res + 1;
    }

    /**
     * Deletes the Subscription with the ID
     *
     *
     * @param id
     */
    public synchronized void deleteSubscription(String id) {
        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();
            String myAlias = getSubscriptionAlias(id);

            statement.executeUpdate("delete from " + TABLE_SUBSCRIPTION + " where id='" + id + "'");
            statement.executeUpdate("insert into " + TABLE_SUBSCRIPTION + " values ('-" + id + "', '" + myAlias + "');");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Adds a new Subscription for ID with Alias
     *
     *
     * @param id
     * @param alias
     */
    public synchronized void insertSubscription(String id, String alias) {
        boolean insert = true;
        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from " + TABLE_SUBSCRIPTION + ";");

            while (rs.next()) {
                String temp = rs.getString("id");

                if (temp.equals(id)) {
                    insert = false;
                }
            }

            rs.close();

            if (insert) {
                statement = connection.createStatement();
                statement.executeUpdate("insert into " + TABLE_SUBSCRIPTION + " values ('" + id + "', '" + alias + "');");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Prints some content of the internal Database
     *
     */
    public synchronized void printDB() {
        System.out.println("Start printing database:");

        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from " + TABLE_SUBSCRIPTION + ";");

            while (rs.next()) {
                String temp = "id: " + rs.getString("id");

                temp += ", alias: " + rs.getString("alias");
                System.out.println(temp);
            }

            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("Finished printing database.");
    }

    /**
     * Sets the locations to the internal Database
     *
     *
     * @param locations
     */
    public synchronized void setLocations(Collection<String> locations) {
        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();

            statement.executeUpdate("drop table if exists " + TABLE_LOCATION + ";");
            statement.executeUpdate("create table " + TABLE_LOCATION + " (location);");

            for (String location : locations) {
                statement.executeUpdate("insert into " + TABLE_LOCATION + " values ('" + location + "');");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Sets the readers to the internal Database
     *
     *
     * @param readers
     */
    public synchronized void setReaders(Collection<String> readers) {
        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();

            statement.executeUpdate("drop table if exists " + TABLE_READER + ";");
            statement.executeUpdate("create table " + TABLE_READER + " (reader);");

            for (String reader : readers) {
                statement.executeUpdate("insert into " + TABLE_READER + " values ('" + reader + "');");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Sets for each location its corresponding readers to the internal Database
     *
     *
     * @param locationReaderConnections
     */
    public synchronized void setLocationReaderConnections(Collection<LocationReaderConnection> locationReaderConnections) {
        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();

            statement.executeUpdate("drop table if exists " + TABLE_LOCATION_READER_CONNECTION + ";");
            statement.executeUpdate("create table " + TABLE_LOCATION_READER_CONNECTION + " (location, reader);");

            for (LocationReaderConnection locationReaderConnection : locationReaderConnections) {
                String location = locationReaderConnection.getLocation();
                String reader = locationReaderConnection.getReader();

                statement.executeUpdate("insert into " + TABLE_LOCATION_READER_CONNECTION + " values ('" + location + "', '" + reader + "');");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Gets all readers available in the internal Database
     *
     *
     * @return
     */
    public synchronized List<String> getReaders() {
        Connection connection = null;
        List<String> readers = new ArrayList<String>();

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from " + TABLE_LOCATION_READER_CONNECTION + ";");

            while (rs.next()) {
                String loc = rs.getString("location");
                String read = rs.getString("reader");

                if (!readers.contains(read)) {
                    readers.add(read);
                }
            }

            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return readers;
    }

    /**
     * Gets all locations available in the internal Database
     *
     *
     * @return
     */
    public synchronized List<String> getLocations() {
        Connection connection = null;
        List<String> locations = new ArrayList<String>();

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from " + TABLE_LOCATION + ";");

            while (rs.next()) {
                String loc = rs.getString("location");

                locations.add(loc);
            }

            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return locations;
    }

    /**
     * Get all readers of a certain location
     *
     *
     * @param location
     *
     * @return
     */
    public synchronized List<String> getReaders(String location) {
        Connection connection = null;
        List<String> readers = new ArrayList<String>();

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from " + TABLE_LOCATION_READER_CONNECTION + ";");

            while (rs.next()) {
                String loc = rs.getString("location");
                String read = rs.getString("reader");

                if (loc.equals(location)) {
                    readers.add(read);
                }
            }

            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return readers;
    }

    /**
     * Get the list of Feed IDs
     *
     *
     * @return
     */
    public List<String> getFeedIDs() {
        Connection connection = null;
        List<String> res = new ArrayList<String>();

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from " + TABLE_FEED + ";");

            while (rs.next()) {
                String id = rs.getString("id");

                res.add(id);
            }

            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return res;
    }

    /**
     * Adds a new Feed to the internal Database
     *
     *
     * @param feed
     */
    public synchronized void addFeed(FeedBean feed) {
        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();

            statement.executeUpdate("insert into " + TABLE_FEED + " values ('" + feed.getId() + "', '" + feed.getTitle() + "', '" + feed.getSubtitle() + "', '" + feed.getAuthor() + "', '" + feed.getCategory() + "', '" + feed.getContributor() + "', '" + feed.getLink() + "', '" + feed.getLogo() + "');");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Adds a new Entry to a Feed to the internal Database
     *
     *
     * @param id
     * @param entry
     */
    public synchronized void addEntryToFeed(String id, EntryBean entry) {
        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();

            statement.executeUpdate("insert into " + TABLE_ENTRIES + " values ('" + entry.getId() + "', '" + id + "', '" + entry.getSummary() + "', '" + entry.getSummaryXML() + "', '" + entry.getUpdated() + "', '" + entry.getPublished() + "', '" + entry.getLink() + "', '" + entry.getTitle() + "');");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Gets the next free entry ID
     *
     *
     * @param feedId
     *
     * @return
     */
    public int getNextEntryID(String feedId) {
        Connection connection = null;
        int res = 0;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();

            // ResultSet rs = statement.executeQuery("select count('id') from " + TABLE_FEED + " where id='" + feedId + "';");
            ResultSet rs = statement.executeQuery("select * from " + TABLE_ENTRIES + " where feed_id='" + feedId + "';");
            int counter = 0;

            while (rs.next()) {
                counter++;
            }

            res = counter;
            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return res;
    }

    /**
     * Get the Feed for the ID
     *
     *
     * @param id
     *
     * @return
     */
    public synchronized FeedBean getFeed(String id) {
        Connection connection = null;
        FeedBean feed = null;

        try {
            Class.forName(DRIVER);
            connection = getConnection();

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from " + TABLE_FEED + " where id='" + id + "';");

            if (rs.next()) {

                // String id = rs.getString("id");
                String title = rs.getString("title");
                String subtitle = rs.getString("subtitle");
                String author = rs.getString("author");
                String category = rs.getString("category");
                String contributor = rs.getString("contributor");
                String link = rs.getString("link");
                String logo = rs.getString("logo");

                feed = new FeedBean(id, title, subtitle, author, category, contributor, link, logo);
            }

            if (feed != null) {
                Statement statement2 = connection.createStatement();
                ResultSet rs2 = statement2.executeQuery("select * from " + TABLE_ENTRIES + " where feed_id='" + id + "' order by id desc;");

                while (rs2.next()) {
                    EntryBean entry = new EntryBean(rs2.getString(1), rs2.getString(8), rs2.getString(3), rs2.getString(4), rs2.getString(5), rs2.getString(6), rs2.getString(7));

                    feed.getEntries().add(entry);
                }
            }

            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            // close DB connection
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return feed;
    }
}
