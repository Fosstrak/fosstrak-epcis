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

package org.accada.epcis.queryclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.xml.bind.JAXBElement;

import org.accada.epcis.model.AggregationEventType;
import org.accada.epcis.model.ArrayOfString;
import org.accada.epcis.model.BusinessTransactionType;
import org.accada.epcis.model.EPC;
import org.accada.epcis.model.EPCISEventType;
import org.accada.epcis.model.GetSubscriptionIDs;
import org.accada.epcis.model.ObjectEventType;
import org.accada.epcis.model.Poll;
import org.accada.epcis.model.QuantityEventType;
import org.accada.epcis.model.QueryParam;
import org.accada.epcis.model.QueryParams;
import org.accada.epcis.model.QueryResults;
import org.accada.epcis.model.Subscribe;
import org.accada.epcis.model.TransactionEventType;
import org.accada.epcis.model.Unsubscribe;
import org.accada.epcis.utils.TimeParser;

/**
 * Implements a Class to interface with the EPCIS query client. Also offers some
 * helper methods to convert between different formats and for debug output.
 * 
 * @author David Gubler
 */
public class QueryClientGuiHelper {

    private static final String PROPERTY_FILE = "/queryclient.properties";
    private static final String PROP_QUERY_URL = "default.url";
    private static final String DEFAULT_QUERY_URL = "http://demo.accada.org/epcis/query";

    private QueryControlClient queryClient;

    /**
     * Holds the query parameters.
     */
    private List<QueryParam> internalQueryParams = new ArrayList<QueryParam>();

    /**
     * All debug output is written into this text field.
     */
    private JTextArea debugTextArea;

    /**
     * Constructor. Takes the service endpoint address and a JTextArea used for
     * debug output as arguments.
     * 
     * @param queryUrl
     *            The URL of the query web service.
     * @param area
     *            The text area where debug output will be written to.
     */
    public QueryClientGuiHelper(final String queryUrl, final JTextArea area) {
        debugTextArea = area;
        queryClient = new QueryControlClient(queryUrl);
    }

    public String updateEndpointAddress(final String address) {
        String endpointAddress;
        if (address == null) {
            Properties props = loadProperties();
            endpointAddress = props.getProperty(PROP_QUERY_URL, DEFAULT_QUERY_URL);
        } else {
            endpointAddress = address;
        }

        // check if the endpointAddress is valid
        try {
            new URL(endpointAddress);
        } catch (Exception e) {
            System.out.println("Invalid endpoint address provided. Using default: " + DEFAULT_QUERY_URL);
            endpointAddress = DEFAULT_QUERY_URL;
        }
        setEndpointAddress(endpointAddress);
        return endpointAddress;
    }

    /**
     * Sets the service endpoint address.
     * 
     * @param queryUrl
     *            The URL of the query web service.
     */
    private void setEndpointAddress(final String queryUrl) {
        queryClient.setEndpointAddress(queryUrl);
    }

    /**
     * @return The service endpoint address
     */
    public String getEndpointAddress() {
        return queryClient.getEndpointAddress();
    }

    /**
     * @return The query client properties.
     */
    private Properties loadProperties() {
        InputStream is = getClass().getResourceAsStream(PROPERTY_FILE);
        Properties props = new Properties();
        try {
            props.load(is);
            is.close();
        } catch (IOException e) {
            System.out.println("Unable to load queryclient properties from "
                    + QueryControlClient.class.getResource(PROPERTY_FILE).toString() + ". Using defaults.");
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

        debugTextArea.append("\n\n" + nofEvents + " events returned by the server:\n\n");
        for (Object o : eventList) {
            if (o instanceof JAXBElement<?>) {
                o = ((JAXBElement<?>) o).getValue();
            }
            EPCISEventType event = (EPCISEventType) o;
            debugTextArea.append("[ EPCISEvent ]\n");
            String eventTime = prettyStringCalendar(event.getEventTime().toGregorianCalendar());
            debugTextArea.append("eventTime:\t" + eventTime + "\n");
            table[row][1] = eventTime;
            String recordTime = prettyStringCalendar(event.getRecordTime().toGregorianCalendar());
            debugTextArea.append("recordTime:\t" + recordTime + "\n");
            table[row][2] = recordTime;
            debugTextArea.append("timeZoneOffset:\t" + event.getEventTimeZoneOffset() + "\n");

            if (event instanceof ObjectEventType) {
                debugTextArea.append("[ ObjectEvent ]\n");
                ObjectEventType e = (ObjectEventType) event;
                table[row][0] = "Object";
                debugTextArea.append("epcList:\t");
                table[row][5] = "";
                for (EPC epc : e.getEpcList().getEpc()) {
                    debugTextArea.append(" '" + epc.getValue() + "'");
                    table[row][5] = table[row][5] + "'" + epc.getValue() + "' ";
                }
                debugTextArea.append("\n");
                debugTextArea.append("action:\t\t" + e.getAction().toString() + "\n");
                table[row][6] = e.getAction().toString();
                debugTextArea.append("bizStep:\t" + e.getBizStep() + "\n");
                table[row][7] = e.getBizStep();
                debugTextArea.append("disposition:\t" + e.getDisposition() + "\n");
                table[row][8] = e.getDisposition();
                if (e.getReadPoint() != null) {
                    debugTextArea.append("readPoint:\t" + e.getReadPoint().getId() + "\n");
                    table[row][9] = e.getReadPoint().getId();
                } else {
                    debugTextArea.append("readPoint:\tnull\n");
                }
                if (e.getBizLocation() != null) {
                    debugTextArea.append("bizLocation:\t" + e.getBizLocation().getId() + "\n");
                    table[row][10] = e.getBizLocation().getId();
                } else {
                    debugTextArea.append("bizLocation:\tnull\n");
                }
                if (e.getBizTransactionList() != null) {
                    debugTextArea.append("bizTrans:\tType, ID\n");
                    table[row][11] = "";
                    for (BusinessTransactionType bizTrans : e.getBizTransactionList().getBizTransaction()) {
                        debugTextArea.append("\t'" + bizTrans.getType() + "', '" + bizTrans.getValue() + "'\n");
                        table[row][11] = table[row][11] + "'" + bizTrans.getType() + ", " + bizTrans.getValue()
                                + "' ; ";
                    }
                    if (!"".equals(table[row][11])) {
                        // remove last "; "
                        table[row][11] = ((String) table[row][11]).substring(0, ((String) table[row][11]).length() - 2);
                    }
                } else {
                    debugTextArea.append("bizTrans:\tnull\n");
                }
                debugTextArea.append("\n");

            } else if (event instanceof TransactionEventType) {
                debugTextArea.append("[ TransactionEvent ]\n");
                TransactionEventType e = (TransactionEventType) event;
                table[row][0] = "Transaction";
                debugTextArea.append("parentID:\t" + e.getParentID() + "\n");
                table[row][3] = e.getParentID();
                debugTextArea.append("epcList:\t");
                table[row][5] = "";
                for (EPC epc : e.getEpcList().getEpc()) {
                    debugTextArea.append(" '" + epc.getValue() + "'");
                    table[row][5] = table[row][5] + "'" + epc.getValue() + "' ";
                }
                debugTextArea.append("\n");
                debugTextArea.append("action:\t\t" + e.getAction().toString() + "\n");
                table[row][6] = e.getAction().toString();
                debugTextArea.append("bizStep:\t" + e.getBizStep() + "\n");
                table[row][7] = e.getBizStep();
                debugTextArea.append("disposition:\t" + e.getDisposition() + "\n");
                table[row][8] = e.getDisposition();
                if (e.getReadPoint() != null) {
                    debugTextArea.append("readPoint:\t" + e.getReadPoint().getId() + "\n");
                    table[row][9] = e.getReadPoint().getId();
                } else {
                    debugTextArea.append("readPoint:\tnull\n");
                }
                if (e.getBizLocation() != null) {
                    debugTextArea.append("bizLocation:\t" + e.getBizLocation().getId() + "\n");
                    table[row][10] = e.getBizLocation().getId();
                } else {
                    debugTextArea.append("bizLocation:\tnull\n");
                }
                if (e.getBizTransactionList() != null) {
                    debugTextArea.append("bizTrans:\tType, ID\n");
                    table[row][11] = "";
                    for (BusinessTransactionType bizTrans : e.getBizTransactionList().getBizTransaction()) {
                        debugTextArea.append("\t'" + bizTrans.getType() + "', '" + bizTrans.getValue() + "'\n");
                        table[row][11] = table[row][11] + "'" + bizTrans.getType() + ", " + bizTrans.getValue()
                                + "' ; ";
                    }
                    if (!"".equals(table[row][11])) {
                        // remove last "; "
                        table[row][11] = ((String) table[row][11]).substring(0, ((String) table[row][11]).length() - 2);
                    }
                } else {
                    debugTextArea.append("bizTrans:\tnull\n");
                }
                debugTextArea.append("\n");

            } else if (event instanceof AggregationEventType) {
                debugTextArea.append("[ AggregationEvent ]\n");
                AggregationEventType e = (AggregationEventType) event;
                table[row][0] = "Aggregation";
                debugTextArea.append("parentID:\t" + e.getParentID() + "\n");
                table[row][3] = e.getParentID();
                debugTextArea.append("childEPCs:\t");
                table[row][5] = "";
                for (EPC epc : e.getChildEPCs().getEpc()) {
                    debugTextArea.append(" '" + epc.getValue() + "'");
                    table[row][5] = table[row][5] + "'" + epc.getValue() + "' ";
                }
                debugTextArea.append("\n");
                debugTextArea.append("action:\t\t" + e.getAction().toString() + "\n");
                table[row][6] = e.getAction().toString();
                debugTextArea.append("bizStep:\t" + e.getBizStep() + "\n");
                table[row][7] = e.getBizStep();
                debugTextArea.append("disposition:\t" + e.getDisposition() + "\n");
                table[row][8] = e.getDisposition();
                if (e.getReadPoint() != null) {
                    debugTextArea.append("readPoint:\t" + e.getReadPoint().getId() + "\n");
                    table[row][9] = e.getReadPoint().getId();
                } else {
                    debugTextArea.append("readPoint:\tnull\n");
                }
                if (e.getBizLocation() != null) {
                    debugTextArea.append("bizLocation:\t" + e.getBizLocation().getId() + "\n");
                    table[row][10] = e.getBizLocation().getId();
                } else {
                    debugTextArea.append("bizLocation:\tnull\n");
                }
                if (e.getBizTransactionList() != null) {
                    debugTextArea.append("bizTrans:\tType, ID\n");
                    table[row][11] = "";
                    for (BusinessTransactionType bizTrans : e.getBizTransactionList().getBizTransaction()) {
                        debugTextArea.append("\t'" + bizTrans.getType() + "', '" + bizTrans.getValue() + "'\n");
                        table[row][11] = table[row][11] + "'" + bizTrans.getType() + ", " + bizTrans.getValue()
                                + "' ; ";
                    }
                    if (!"".equals(table[row][11])) {
                        // remove last "; "
                        table[row][11] = ((String) table[row][11]).substring(0, ((String) table[row][11]).length() - 2);
                    }
                } else {
                    debugTextArea.append("bizTrans:\tnull\n");
                }
                debugTextArea.append("\n");

            } else if (event instanceof QuantityEventType) {
                debugTextArea.append("[ QuantityEvent ]\n");
                QuantityEventType e = (QuantityEventType) event;
                table[row][0] = "Quantity";
                debugTextArea.append("quantity:\t" + e.getQuantity() + "\n");
                table[row][4] = Integer.valueOf(e.getQuantity());
                debugTextArea.append("ecpClass:\t" + e.getEpcClass() + "\n");
                table[row][5] = e.getEpcClass();
                debugTextArea.append("bizStep:\t" + e.getBizStep() + "\n");
                table[row][7] = e.getBizStep();
                debugTextArea.append("disposition:\t" + e.getDisposition() + "\n");
                table[row][8] = e.getDisposition();
                if (e.getReadPoint() != null) {
                    debugTextArea.append("readPoint:\t" + e.getReadPoint().getId() + "\n");
                    table[row][9] = e.getReadPoint().getId();
                } else {
                    debugTextArea.append("readPoint:\tnull\n");
                }
                if (e.getBizLocation() != null) {
                    debugTextArea.append("bizLocation:\t" + e.getBizLocation().getId() + "\n");
                    table[row][10] = e.getBizLocation().getId();
                } else {
                    debugTextArea.append("bizLocation:\tnull\n");
                }
                if (e.getBizTransactionList() != null) {
                    debugTextArea.append("bizTrans:\tType, ID\n");
                    table[row][11] = "";
                    for (BusinessTransactionType bizTrans : e.getBizTransactionList().getBizTransaction()) {
                        debugTextArea.append("\t'" + bizTrans.getType() + "', '" + bizTrans.getValue() + "'\n");
                        table[row][11] = table[row][11] + "'" + bizTrans.getType() + ", " + bizTrans.getValue()
                                + "' ; ";
                    }
                    if (!"".equals(table[row][11])) {
                        // remove last "; "
                        table[row][11] = ((String) table[row][11]).substring(0, ((String) table[row][11]).length() - 2);
                    }
                } else {
                    debugTextArea.append("bizTrans:\tnull\n");
                }
                debugTextArea.append("\n");
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
        QueryParams queryParams = new QueryParams();
        queryParams.getParam().addAll(internalQueryParams);
        debugTextArea.append("Number of query parameters: " + queryParams.getParam().size() + "\n");
        for (QueryParam queryParam : internalQueryParams) {
            debugTextArea.append(queryParam.getName() + " " + queryParam.getValue() + "\n");
        }

        Poll poll = new Poll();
        poll.setQueryName("SimpleEventQuery");
        poll.setParams(queryParams);

        debugTextArea.append("running query...\n");
        QueryResults results = queryClient.poll(poll);
        debugTextArea.append("done\n");

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
        QueryParams queryParams = new QueryParams();
        queryParams.getParam().addAll(internalQueryParams);
        debugTextArea.append("Number of query parameters: " + queryParams.getParam().size() + "\n");
        for (QueryParam queryParam : internalQueryParams) {
            debugTextArea.append(queryParam.getName() + " " + queryParam.getValue() + "\n");
        }
        subscribe.setParams(queryParams);
        queryClient.subscribe(subscribe.getQueryName(), subscribe.getParams(), subscribe.getDest(),
                subscribe.getControls(), subscribe.getSubscriptionID());
    }

    /**
     * Removes a registersQuery by the server.
     * 
     * @param subscriptionID
     *            The ID of the query to be unsubscribed.
     */
    public void unsubscribeQuery(final String subscriptionID) {
        try {
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
        } catch (Exception e) {
            JFrame frame = new JFrame();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            JOptionPane.showMessageDialog(frame, "Sorry, the Service returned an Error:\n" + stacktrace,
                    "Service is not responding", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    /**
     * Query the service for the supported standard version.
     * 
     * @return String
     * @throws Exception
     *             If any Exception occurred while invoking the query service.
     */
    public String queryStandardVersion() throws Exception {
        return queryClient.getStandardVersion();
    }

    /**
     * Query the service for subscriptions.
     */
    public void querySubscriptionIDs() {
        String title = "Service is responding";
        StringBuilder msg = new StringBuilder();
        try {
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
        } catch (Exception e) {
            title = "Service is not responding";
            msg.append("Could not retrieve subscription IDs from repository.");
            e.printStackTrace();
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
}
