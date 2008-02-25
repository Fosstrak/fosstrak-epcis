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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.xml.namespace.QName;

import org.accada.epcis.soap.EPCISServicePortType;
import org.accada.epcis.soap.EPCglobalEPCISService;
import org.accada.epcis.soap.model.AggregationEventType;
import org.accada.epcis.soap.model.ArrayOfString;
import org.accada.epcis.soap.model.BusinessTransactionType;
import org.accada.epcis.soap.model.EPC;
import org.accada.epcis.soap.model.EPCISEventType;
import org.accada.epcis.soap.model.EmptyParms;
import org.accada.epcis.soap.model.EventListType;
import org.accada.epcis.soap.model.GetSubscriptionIDs;
import org.accada.epcis.soap.model.ObjectEventType;
import org.accada.epcis.soap.model.Poll;
import org.accada.epcis.soap.model.QuantityEventType;
import org.accada.epcis.soap.model.QueryParam;
import org.accada.epcis.soap.model.QueryParams;
import org.accada.epcis.soap.model.QueryResults;
import org.accada.epcis.soap.model.Subscribe;
import org.accada.epcis.soap.model.TransactionEventType;
import org.accada.epcis.soap.model.Unsubscribe;
import org.accada.epcis.utils.TimeParser;

/**
 * Implements a Class to interface with the axis stubs for the EPCIS Query
 * Interface. Also offers some helper methods to convert between different
 * formats and for debug output.
 * <p>
 * TODO: refactor this GUI helper class to reuse the QueryControlClient (MVC
 * pattern!)<br>
 * FIXME: setEndpointAddress has no effect!
 * 
 * @author David Gubler
 */
public class QueryClientGuiHelper {
    
    private static QueryControlClient queryClient;

    /**
     * Holds the query parameters.
     */
    private Vector<QueryParam> queryParamsVector = new Vector<QueryParam>();

    /**
     * All debug output is written into this text field.
     */
    private JTextArea debugTextArea;

    /**
     * Constructor. Takes the service endpoint address and a JTextArea used for
     * debug output as an arguments.
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

    /**
     * Sets the service endpoint address.
     * 
     * @param queryUrl
     *            The URL of the query web service.
     */
    public void setEndpointAddress(final String queryUrl) {
        queryClient.setEndpointAddress(queryUrl);
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
    private Object[][] processEvents(final EventListType eventList) {
        int nofEvents = eventList.getObjectEventOrAggregationEventOrQuantityEvent().size();
        Object[][] table = new Object[nofEvents][12];
        int row = 0;

        debugTextArea.append("\n\nEvents returned by the server:\n\n");
        for (Object o : eventList.getObjectEventOrAggregationEventOrQuantityEvent()) {
            try {
                EPCISEventType event = (EPCISEventType) o;
                debugTextArea.append("[ EPCISEvent ]\n");
                String eventTime = prettyStringCalendar(event.getEventTime().toGregorianCalendar());
                debugTextArea.append("eventTime:\t" + eventTime + "\n");
                table[row][1] = eventTime;
                String recordTime = prettyStringCalendar(event.getRecordTime().toGregorianCalendar());
                debugTextArea.append("recordTime:\t" + recordTime + "\n");
                table[row][2] = recordTime;
                debugTextArea.append("eventTimeZoneOffset:\t" + event.getEventTimeZoneOffset());

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
                    debugTextArea.append("action:\t" + e.getAction().toString() + "\n");
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
                    debugTextArea.append("bizTransactions: Type, ID\n");
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
                    debugTextArea.append("action:\t" + e.getAction().toString() + "\n");
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
                    debugTextArea.append("bizTransactions: Type, ID\n");
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
                    debugTextArea.append("action:\t" + e.getAction().toString() + "\n");
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
                    debugTextArea.append("bizTransactions: Type, ID\n");
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
                    debugTextArea.append("\n");

                } else if (event instanceof QuantityEventType) {
                    debugTextArea.append("[ QuantityEvent ]\n");
                    QuantityEventType e = (QuantityEventType) event;
                    debugTextArea.append("ecpClass:\t" + e.getEpcClass() + "\n");
                    table[row][5] = e.getEpcClass();
                    debugTextArea.append("quantity:\t" + e.getQuantity() + "\n");
                    table[row][4] = e.getQuantity();
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
                    debugTextArea.append("bizTransactions: Type, ID\n");
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
                    debugTextArea.append("\n");
                }
            } catch (ClassCastException e) {
                // TODO
                // throw meaningful exception
            }
            row++;
        }
        return table;
    }

    /**
     * Reset the query arguments.
     */
    public void clearParameters() {
        queryParamsVector.clear();
    }

    /**
     * Add a new query parameter.
     * 
     * @param param
     *            The query parameter to add.
     */
    public void addParameter(final QueryParam param) {
        queryParamsVector.add(param);
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
        queryParams.getParam().addAll(queryParamsVector);
        debugTextArea.append("Number of query parameters: " + queryParams.getParam().size() + "\n");
        for (QueryParam queryParam : queryParamsVector) {
            debugTextArea.append(queryParam.getName() + " " + queryParam.getValue() + "\n");
        }

        Poll poll = new Poll();
        poll.setQueryName("SimpleEventQuery");
        poll.setParams(queryParams);

        debugTextArea.append("running query...\n");
        QueryResults results = queryClient.poll(poll);
        debugTextArea.append("done\n");

        // print to debug window and return result
        return processEvents(results.getResultsBody().getEventList());
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
        queryParams.getParam().addAll(queryParamsVector);
        debugTextArea.append("Number of query parameters: " + queryParams.getParam().size() + "\n");
        for (QueryParam queryParam : queryParamsVector) {
            debugTextArea.append(queryParam.getName() + " " + queryParam.getValue() + "\n");
        }
        subscribe.setParams(queryParams);
        queryClient.subscribe(subscribe.getQueryName(), subscribe.getParams(), subscribe.getDest(), subscribe.getControls(), subscribe.getSubscriptionID());
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
            JOptionPane.showMessageDialog(frame, "Sorry, the Service returned " + "an Error:\n" + stacktrace,
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
     * Query the service for the supported standard version.
     */
    public void querySubscriptionId() {
        String title = "Service is responding";
        StringBuilder msg = new StringBuilder();
        try {
            GetSubscriptionIDs parms = new GetSubscriptionIDs();
            parms.setQueryName("simpleQuery");
            List<String> subscriptionIDs = queryClient.getSubscriptionIds(parms.getQueryName());
            if (subscriptionIDs != null && !subscriptionIDs.isEmpty()) {
                msg.append("The Service found the following SubscriptionID(s):\n");
                for (String s : subscriptionIDs) {
                    msg.append("- ").append(s).append("\n");
                }
            } else {
                msg.append("There are no Subscribed Queries.");
            }
        } catch (Exception e) {
            title = "Service is not responding";
            msg.append("Sorry, the Service doesn't work");
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
