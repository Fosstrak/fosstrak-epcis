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

package org.fosstrak.epcis.queryclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBElement;

import org.fosstrak.epcis.gui.AuthenticationOptionsChangeEvent;
import org.fosstrak.epcis.gui.AuthenticationOptionsChangeListener;
import org.fosstrak.epcis.model.AggregationEventType;
import org.fosstrak.epcis.model.ArrayOfString;
import org.fosstrak.epcis.model.BusinessTransactionType;
import org.fosstrak.epcis.model.EPC;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.GetSubscriptionIDs;
import org.fosstrak.epcis.model.ObjectEventType;
import org.fosstrak.epcis.model.Poll;
import org.fosstrak.epcis.model.QuantityEventType;
import org.fosstrak.epcis.model.QueryParam;
import org.fosstrak.epcis.model.QueryParams;
import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.model.Subscribe;
import org.fosstrak.epcis.model.TransactionEventType;
import org.fosstrak.epcis.model.Unsubscribe;
import org.fosstrak.epcis.utils.TimeParser;

/**
 * Implements a class to interface with the EPCIS query client. Also offers some
 * helper methods to convert between different formats and for debug output.
 * 
 * @author David Gubler
 */
public class QueryClientGuiHelper implements AuthenticationOptionsChangeListener {

    private static final String PROPERTY_FILE = "/queryclient.properties";
    private static final String PROP_QUERY_URL = "default.url";
    private static final String DEFAULT_QUERY_URL = "http://demo.fosstrak.org/epcis/query";

    private QueryControlClient queryClient;

    /**
     * Holds the query parameters.
     */
    private List<QueryParam> internalQueryParams = new ArrayList<QueryParam>();

    private QueryClientGui mainWindow;

    private String defaultEndpointAddress;

    private boolean configurationChanged;

    /**
     * Constructor. Takes the service endpoint address and a JTextArea used for
     * debug output as arguments.
     * 
     * @param queryUrl
     *            The URL of the query web service.
     * @param area
     *            The text area where debug output will be written to.
     */
    public QueryClientGuiHelper(QueryClientGui mainWindow) {
        this.mainWindow = mainWindow;
        queryClient = new QueryControlClient();
        Properties props = loadProperties();
        defaultEndpointAddress = props.getProperty(PROP_QUERY_URL, DEFAULT_QUERY_URL);
        try {
            new URL(defaultEndpointAddress);
        } catch (Exception e) {
            defaultEndpointAddress = DEFAULT_QUERY_URL;
        }
    }

    /**
     * Gets the default repository endpoint address as specified in the
     * application properties file.
     */
    public String getDefaultEndpointAddress() {
        return defaultEndpointAddress;
    }

    /**
     * @return The query client properties.
     */
    private Properties loadProperties() {
        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream(PROPERTY_FILE);
        if (is != null) {
	        try {
	            props.load(is);
	            is.close();
	        } catch (IOException e) {
	            System.out.println("Unable to load queryclient properties from "
	                    + QueryControlClient.class.getResource(PROPERTY_FILE).toString() + ". Using defaults.");
	        }
        }
        else {
            System.out.println("Unable to load queryclient properties from "
                    + PROPERTY_FILE + ". Using defaults.");
        }
        return props;
    }

    /**
     * Converts the values in a calendar object into a nicely formatted string.
     * 
     * @param cal
     *            with the Calendar-Date
     * @return String
     */
    private String prettyStringCalendar(final Calendar cal) {
        if (cal == null) {
            return null;
        }
        // set to current timezone
        cal.setTimeZone(TimeZone.getDefault());
        return TimeParser.format(cal);
    }

    /**
     * Prints the results from a query invocation to the debug window and
     * returns a two-dimensional array in a format suitable for a JTable object.
     * 
     * @param eventList
     *            The result list containing the matching events.
     * @return A two-dimensional array containing the matching events in a
     *         format suitable for displaying in a JTable object.
     */
    private Object[][] processEvents(final List<Object> eventList) {
        int nofEvents = eventList.size();
        Object[][] table = new Object[nofEvents][12];
        int row = 0;

        debug("\n\n" + nofEvents + " events returned by the server:\n\n");
        for (Object o : eventList) {
            if (o instanceof JAXBElement<?>) {
                o = ((JAXBElement<?>) o).getValue();
            }
            EPCISEventType event = (EPCISEventType) o;
            debug("[ EPCISEvent ]\n");
            String eventTime = prettyStringCalendar(event.getEventTime().toGregorianCalendar());
            debug("eventTime:\t" + eventTime + "\n");
            table[row][1] = eventTime;
            String recordTime = prettyStringCalendar(event.getRecordTime().toGregorianCalendar());
            debug("recordTime:\t" + recordTime + "\n");
            table[row][2] = recordTime;
            debug("timeZoneOffset:\t" + event.getEventTimeZoneOffset() + "\n");

            if (event instanceof ObjectEventType) {
                debug("[ ObjectEvent ]\n");
                ObjectEventType e = (ObjectEventType) event;
                table[row][0] = "Object";
                debug("epcList:\t");
                table[row][5] = "";
                for (EPC epc : e.getEpcList().getEpc()) {
                    debug(" '" + epc.getValue() + "'");
                    table[row][5] = table[row][5] + "'" + epc.getValue() + "' ";
                }
                debug("\n");
                debug("action:\t\t" + e.getAction().toString() + "\n");
                table[row][6] = e.getAction().toString();
                debug("bizStep:\t" + e.getBizStep() + "\n");
                table[row][7] = e.getBizStep();
                debug("disposition:\t" + e.getDisposition() + "\n");
                table[row][8] = e.getDisposition();
                if (e.getReadPoint() != null) {
                    debug("readPoint:\t" + e.getReadPoint().getId() + "\n");
                    table[row][9] = e.getReadPoint().getId();
                } else {
                    debug("readPoint:\tnull\n");
                }
                if (e.getBizLocation() != null) {
                    debug("bizLocation:\t" + e.getBizLocation().getId() + "\n");
                    table[row][10] = e.getBizLocation().getId();
                } else {
                    debug("bizLocation:\tnull\n");
                }
                if (e.getBizTransactionList() != null) {
                    debug("bizTrans:\tType, ID\n");
                    table[row][11] = "";
                    for (BusinessTransactionType bizTrans : e.getBizTransactionList().getBizTransaction()) {
                        debug("\t'" + bizTrans.getType() + "', '" + bizTrans.getValue() + "'\n");
                        table[row][11] = table[row][11] + "'" + bizTrans.getType() + ", " + bizTrans.getValue()
                                + "' ; ";
                    }
                    if (!"".equals(table[row][11])) {
                        // remove last "; "
                        table[row][11] = ((String) table[row][11]).substring(0, ((String) table[row][11]).length() - 2);
                    }
                } else {
                    debug("bizTrans:\tnull\n");
                }
                debug("\n");

            } else if (event instanceof TransactionEventType) {
                debug("[ TransactionEvent ]\n");
                TransactionEventType e = (TransactionEventType) event;
                table[row][0] = "Transaction";
                debug("parentID:\t" + e.getParentID() + "\n");
                table[row][3] = e.getParentID();
                debug("epcList:\t");
                table[row][5] = "";
                for (EPC epc : e.getEpcList().getEpc()) {
                    debug(" '" + epc.getValue() + "'");
                    table[row][5] = table[row][5] + "'" + epc.getValue() + "' ";
                }
                debug("\n");
                debug("action:\t\t" + e.getAction().toString() + "\n");
                table[row][6] = e.getAction().toString();
                debug("bizStep:\t" + e.getBizStep() + "\n");
                table[row][7] = e.getBizStep();
                debug("disposition:\t" + e.getDisposition() + "\n");
                table[row][8] = e.getDisposition();
                if (e.getReadPoint() != null) {
                    debug("readPoint:\t" + e.getReadPoint().getId() + "\n");
                    table[row][9] = e.getReadPoint().getId();
                } else {
                    debug("readPoint:\tnull\n");
                }
                if (e.getBizLocation() != null) {
                    debug("bizLocation:\t" + e.getBizLocation().getId() + "\n");
                    table[row][10] = e.getBizLocation().getId();
                } else {
                    debug("bizLocation:\tnull\n");
                }
                if (e.getBizTransactionList() != null) {
                    debug("bizTrans:\tType, ID\n");
                    table[row][11] = "";
                    for (BusinessTransactionType bizTrans : e.getBizTransactionList().getBizTransaction()) {
                        debug("\t'" + bizTrans.getType() + "', '" + bizTrans.getValue() + "'\n");
                        table[row][11] = table[row][11] + "'" + bizTrans.getType() + ", " + bizTrans.getValue()
                                + "' ; ";
                    }
                    if (!"".equals(table[row][11])) {
                        // remove last "; "
                        table[row][11] = ((String) table[row][11]).substring(0, ((String) table[row][11]).length() - 2);
                    }
                } else {
                    debug("bizTrans:\tnull\n");
                }
                debug("\n");

            } else if (event instanceof AggregationEventType) {
                debug("[ AggregationEvent ]\n");
                AggregationEventType e = (AggregationEventType) event;
                table[row][0] = "Aggregation";
                debug("parentID:\t" + e.getParentID() + "\n");
                table[row][3] = e.getParentID();
                debug("childEPCs:\t");
                table[row][5] = "";
                for (EPC epc : e.getChildEPCs().getEpc()) {
                    debug(" '" + epc.getValue() + "'");
                    table[row][5] = table[row][5] + "'" + epc.getValue() + "' ";
                }
                debug("\n");
                debug("action:\t\t" + e.getAction().toString() + "\n");
                table[row][6] = e.getAction().toString();
                debug("bizStep:\t" + e.getBizStep() + "\n");
                table[row][7] = e.getBizStep();
                debug("disposition:\t" + e.getDisposition() + "\n");
                table[row][8] = e.getDisposition();
                if (e.getReadPoint() != null) {
                    debug("readPoint:\t" + e.getReadPoint().getId() + "\n");
                    table[row][9] = e.getReadPoint().getId();
                } else {
                    debug("readPoint:\tnull\n");
                }
                if (e.getBizLocation() != null) {
                    debug("bizLocation:\t" + e.getBizLocation().getId() + "\n");
                    table[row][10] = e.getBizLocation().getId();
                } else {
                    debug("bizLocation:\tnull\n");
                }
                if (e.getBizTransactionList() != null) {
                    debug("bizTrans:\tType, ID\n");
                    table[row][11] = "";
                    for (BusinessTransactionType bizTrans : e.getBizTransactionList().getBizTransaction()) {
                        debug("\t'" + bizTrans.getType() + "', '" + bizTrans.getValue() + "'\n");
                        table[row][11] = table[row][11] + "'" + bizTrans.getType() + ", " + bizTrans.getValue()
                                + "' ; ";
                    }
                    if (!"".equals(table[row][11])) {
                        // remove last "; "
                        table[row][11] = ((String) table[row][11]).substring(0, ((String) table[row][11]).length() - 2);
                    }
                } else {
                    debug("bizTrans:\tnull\n");
                }
                debug("\n");

            } else if (event instanceof QuantityEventType) {
                debug("[ QuantityEvent ]\n");
                QuantityEventType e = (QuantityEventType) event;
                table[row][0] = "Quantity";
                debug("quantity:\t" + e.getQuantity() + "\n");
                table[row][4] = Integer.valueOf(e.getQuantity());
                debug("ecpClass:\t" + e.getEpcClass() + "\n");
                table[row][5] = e.getEpcClass();
                debug("bizStep:\t" + e.getBizStep() + "\n");
                table[row][7] = e.getBizStep();
                debug("disposition:\t" + e.getDisposition() + "\n");
                table[row][8] = e.getDisposition();
                if (e.getReadPoint() != null) {
                    debug("readPoint:\t" + e.getReadPoint().getId() + "\n");
                    table[row][9] = e.getReadPoint().getId();
                } else {
                    debug("readPoint:\tnull\n");
                }
                if (e.getBizLocation() != null) {
                    debug("bizLocation:\t" + e.getBizLocation().getId() + "\n");
                    table[row][10] = e.getBizLocation().getId();
                } else {
                    debug("bizLocation:\tnull\n");
                }
                if (e.getBizTransactionList() != null) {
                    debug("bizTrans:\tType, ID\n");
                    table[row][11] = "";
                    for (BusinessTransactionType bizTrans : e.getBizTransactionList().getBizTransaction()) {
                        debug("\t'" + bizTrans.getType() + "', '" + bizTrans.getValue() + "'\n");
                        table[row][11] = table[row][11] + "'" + bizTrans.getType() + ", " + bizTrans.getValue()
                                + "' ; ";
                    }
                    if (!"".equals(table[row][11])) {
                        // remove last "; "
                        table[row][11] = ((String) table[row][11]).substring(0, ((String) table[row][11]).length() - 2);
                    }
                } else {
                    debug("bizTrans:\tnull\n");
                }
                debug("\n");
            }
            row++;
        }
        return table;
    }

    /**
     * Reset the query arguments.
     */
    public void clearParameters() {
        internalQueryParams.clear();
    }

    /**
     * Add a new query parameter.
     * 
     * @param param
     *            The query parameter to add.
     */
    public void addParameter(final QueryParam param) {
        internalQueryParams.add(param);
    }

    /**
     * Run the query with the currently set query arguments Returns the results
     * in a format that is suitable for JTable.
     * 
     * @return The pretty-printed query results.
     * @throws Exception
     *             If any Exception occurred while invoking the query service.
     */
    public Object[][] runQuery() throws Exception {
        configureServiceIfNecessary();
        QueryParams queryParams = new QueryParams();
        queryParams.getParam().addAll(internalQueryParams);
        debug("Number of query parameters: " + queryParams.getParam().size() + "\n");
        for (QueryParam queryParam : internalQueryParams) {
        	if (queryParam.getValue() instanceof ArrayOfString) {
        		debug(queryParam.getName() + " " + ((ArrayOfString) queryParam.getValue()).getString() + "\n");
        	} else {
        		debug(queryParam.getName() + " " + queryParam.getValue() + "\n");
        	}
        }

        Poll poll = new Poll();
        poll.setQueryName("SimpleEventQuery");
        poll.setParams(queryParams);

        debug("running query...\n");
        QueryResults results = queryClient.poll(poll);
        debug("done\n");

        // print to debug window and return result
        if (results != null && results.getResultsBody() != null && results.getResultsBody().getEventList() != null) {
            return processEvents(results.getResultsBody().getEventList().getObjectEventOrAggregationEventOrQuantityEvent());
        } else {
            return null;
        }
    }

    /**
     * Registers the Query by the server.
     * 
     * @param subscribe
     *            The Subscribe object containing the query.
     * @throws Exception
     *             If any Exception occurred while invoking the query service.
     */
    public void subscribeQuery(final Subscribe subscribe) throws Exception {
        configureServiceIfNecessary();
        QueryParams queryParams = new QueryParams();
        queryParams.getParam().addAll(internalQueryParams);
        debug("Number of query parameters: " + queryParams.getParam().size() + "\n");
        for (QueryParam queryParam : internalQueryParams) {
            debug(queryParam.getName() + " " + queryParam.getValue() + "\n");
        }
        subscribe.setParams(queryParams);
        queryClient.subscribe(subscribe);
    }

    /**
     * Removes a registersQuery by the server.
     * 
     * @param subscriptionID
     *            The ID of the query to be unsubscribed.
     */
    public void unsubscribeQuery(final String subscriptionID) throws Exception {
        configureServiceIfNecessary();
        JFrame frame = new JFrame();
        if (subscriptionID.equals("")) {
            JOptionPane.showMessageDialog(frame, "Please specify a SubscriptionID", "Service is responding",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Unsubscribe parms = new Unsubscribe();
        parms.setSubscriptionID(subscriptionID);
        queryClient.unsubscribe(parms.getSubscriptionID());
        JOptionPane.showMessageDialog(frame, "Successfully unsubscribed.", "Service is responding",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Query the service for the supported standard version.
     * 
     * @return String
     * @throws Exception
     *             If any Exception occurred while invoking the query service.
     */
    public String queryStandardVersion() throws Exception {
        configureServiceIfNecessary();
        return queryClient.getStandardVersion();
    }

    /**
     * Query the service for subscriptions.
     */
    public void querySubscriptionIDs() throws Exception {
        configureServiceIfNecessary();
        String title = "Service is responding";
        StringBuilder msg = new StringBuilder();
        GetSubscriptionIDs parms = new GetSubscriptionIDs();
        parms.setQueryName("SimpleEventQuery");
        List<String> subscriptionIDs = queryClient.getSubscriptionIds(parms.getQueryName());
        if (subscriptionIDs != null && !subscriptionIDs.isEmpty()) {
            msg.append("The following subscription IDs were found in the repository:\n");
            for (String s : subscriptionIDs) {
                msg.append("- ").append(s).append("\n");
            }
        } else {
            msg.append("There are no subscribed queries.");
        }
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, msg.toString(), title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Query the service for its version.
     * 
     * @return String
     * @throws Exception
     *             If any Exception occurred while invoking the query service.
     */
    public String queryVendorVersion() throws Exception {
        configureServiceIfNecessary();
        return queryClient.getVendorVersion();
    }

    /**
     * Query the service for its queries.
     * 
     * @return String
     * @throws Exception
     *             If any Exception occurred while invoking the query service.
     */
    public List<String> queryNames() throws Exception {
        configureServiceIfNecessary();
        return queryClient.getQueryNames();
    }

    /**
     * Converts a space-separated list of strings to an ArrayOfString.
     * 
     * @param txt
     *            A space-separated list of strings.
     * @return An ArrayOfString object containing single string tokens.
     */
    public ArrayOfString stringListToArray(final String txt) {
        List<String> tokens = Arrays.asList(txt.split(" "));
        ArrayOfString strings = new ArrayOfString();
        strings.getString().addAll(tokens);
        return strings;
    }

    /**
     * Forces the client to reconfigure the service if the any of its parameters
     * (authentication, endpoint address) have been changed.
     * 
     * @throws Exception
     */
    private void configureServiceIfNecessary() throws Exception {
        if (!queryClient.isServiceConfigured() || configurationChanged) {
            // validate the URL and get auth options from the main window
            queryClient.configureService(new URL(mainWindow.getAddress()), mainWindow.getAuthenticationOptions());
            configurationChanged = false;
        }
    }

    /**
     * Set whether or not the configuration has changed. The components in the
     * configuration panel call this method when their state is updated.
     * 
     * @param configurationComplete
     *            Indicates whether or not the information present is enough to
     *            enable the client to connect to a repository (e.g. both a
     *            username and password are available if Basic authentication is
     *            chosen).
     */
    public void configurationChanged(AuthenticationOptionsChangeEvent ace) {
        this.configurationChanged = true;
        mainWindow.setButtonsEnabled(ace.isComplete());
    }

    /**
     * Writes a message to the debug window.
     * 
     * @param msg
     *            The message.
     */
    void debug(String msg) {
        mainWindow.debug(msg);
        if (!msg.endsWith("\n")) {
            mainWindow.debug("\n");
        }
    }
}
