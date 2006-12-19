/* Copyright (c) 2006 ETH Zurich, Switzerland.
 * All rights reserved.
 *
 * For copying and distribution information, please see the file
 * LICENSE.
 */

/**
 */
package org.accada.epcis.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.timer.Timer;

import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.QueryParam;

/**
 * Special case of Subscription (from subscribe() on
 * query interface) where the subscription is scheduled.
 * @author Alain Remund, Arthur van Dorp
 */
public class SubscriptionScheduled extends Subscription implements
        NotificationListener, Serializable {

    /**
     * Generated unique ID for serialization.
     */
    private static final long serialVersionUID = 2312622721651298302L;

    /**
     * Schedule indicating when subscription query is to be executed.
     */
    private Schedule schedule;

    /**
     * Whether to continue with sending results.
     */
    private Boolean doItAgain = true;

//    /**
//     * Opens a connection to the database server.
//     * Uses the global variables dbserver, dbuser, dbpassword and sets
//     * dbconnection.
//     *
//     * BEWARE OF CODE DUPLICATION: this method is also implemented in the
//     * package org.autoidlabs.epcnet.epcisrep.querying2
//     * class EPCISServiceBindingImpl!
//     *
//     * @throws ImplementationException Matching various exception to one.
//     */
//    private void connectDB() throws ImplementationException {
//        try {
//            Context initContext = new InitialContext();
//            Context env = (Context) initContext.lookup("java:comp/env");
//            DataSource dataSource = (DataSource) env.lookup("jdbc/EPCISDB");
//
//            dbconnection = dataSource.getConnection();
//            q = dbconnection.getMetaData().getIdentifierQuoteString();
//        } catch (NamingException ne) {
//            ImplementationExceptionSeverity severity =
//                    ImplementationExceptionSeverity.fromString("ERROR");
//            ImplementationException iex = new ImplementationException();
//            iex.setReason("Could not get DataSource, check "
//                    + "META-INF/context.xml and "
//                    + "WEB-INF/web.xml (on server side) "
//                    + "for configuration errors ("
//                    + ne.getMessage() + ")");
//            iex.setSeverity(severity);
//            throw iex;
//        } catch (SQLException e) {
//            ImplementationExceptionSeverity severity =
//                    ImplementationExceptionSeverity.fromString("ERROR");
//            ImplementationException iex = new ImplementationException();
//            iex.setReason("could not connect to the database (" + e.getMessage()
//                            + ")");
//            iex.setSeverity(severity);
//            throw iex;
//        }
//    }

    /**
     * Constructor to be used when recreating from
     * storage.
     * @param queryParams Query parameters.
     * @param dest Destination URI.
     * @param reportIfEmpty Whether to report when nothing changed.
     */
    public SubscriptionScheduled(String subscriptionID,
            QueryParam[] queryParams,
            org.apache.axis.types.URI dest,
            Boolean reportIfEmpty,
            GregorianCalendar initialRecordTime,
            GregorianCalendar lastTimeExecuted,
            Schedule schedule,
            String queryName)
            throws ImplementationException{
        super(subscriptionID,
                queryParams, dest, reportIfEmpty,
                initialRecordTime, lastTimeExecuted, queryName);
        this.schedule = schedule;
        startThread();
    }

    /**
     * Starts a Timer to get this query executed again.
     * @throws ImplementationException If Scheduler craps out.
     */
    private void startThread() throws ImplementationException {
        Timer nextAction = new Timer();
        nextAction.addNotificationListener(this, null, nextAction);

        Date nextSchedule = schedule.nextScheduledTime().getTime();
        nextAction.addNotification("SubscriptionSchedule",
                "Please do the query", null, nextSchedule);
        nextAction.start();
    }

    /**
     * This Method stops the execution of the schedule and
     * (doesn't for now) removes it from the Database.
     */
    public void stopSubscription() {

        // we don't do this, because the service isn't working
        // stable any more.
//        try {
//            connectDB();
//            String delete = "DELETE FROM subscription WHERE "
//                + "subscriptionid = (?)";
//            PreparedStatement stmt;
//            stmt = dbconnection.prepareStatement(delete);
//            stmt.setString(1, subscriptionID);
//            if (stmt.executeUpdate() == 0) {
//                System.out.println("Ups, something went wrong by"
//                        + " deleting a Query.");
//            }
//        } catch (Exception e) {
//            System.out.println("Please delete Query " + this.subscriptionID
//                    + " in the Database.");
//            e.printStackTrace();
//        }
        doItAgain = false;
    }

    /**
     * The Object has definitely been destroyed. This may take a while.
     */
    protected void finalize() {
        System.out.println("A query has been garbage collected.");
    }

    /**
     * This Method is called after the given time.
     * @throws
     */
    public void handleNotification(Notification pNotification,
            Object pHandback) {
        if (pHandback == null) {
            System.out.println("pHandback in handleNotification is null.");
            return;
        }

        if (!doItAgain) {
            ((Timer) pHandback).stop();
        } else {
            System.out.println("We do a subscribed query. SubscriptionID is "
                    + this.subscriptionID);

            try {
                // set to next scheduled execution time.
                Date nextSchedule = schedule.nextScheduledTime().getTime();
                ((Timer) pHandback).addNotification(
                        "SubscriptionSchedule", "Please do the query",
                        (Timer) pHandback, nextSchedule);

                this.executeQuery();

                // we don't do this, because the service isn't working
                // stable any more.
//                // Update lastexectued in the Database
//                try {
//                    connectDB();
//                    String update = "UPDATE subscription SET lastexecuted "
//                        + "= (?) WHERE subscriptionid = (?)";
//                    PreparedStatement stmt =
//                        dbconnection.prepareStatement(update);
//                    SimpleDateFormat isoDateFormat =
//                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//                    GregorianCalendar cal = new GregorianCalendar();
//                    stmt.setString(1, isoDateFormat.format(cal.getTime()));
//                    stmt.setString(2, subscriptionID);
//                    stmt.executeUpdate();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }

            } catch (ImplementationException ie) {
                // We decided not to stop the Thread
//                System.out.println("Please delete Query "
//                        + this.subscriptionID
//                        + " in the Database.");
//                this.stopSubscription();
                ie.printStackTrace();
            }
            }
    }
}
