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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.accada.epcis.soapapi.BusinessTransactionType;
import org.accada.epcis.utils.TimeParser;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * CaptureOperationsModule implements the core capture operations. Converts XML
 * events delivered by HTTP POST into SQL and inserts them into the database.
 * 
 * @author David Gubler
 * @author Alain Remund
 * @author Marco Steybe
 */
public class CaptureOperationsModule {

    /**
     * The log to write to.
     */
    private static final Logger LOG = Logger.getLogger(CaptureOperationsModule.class);

    /**
     * The XSD schema which validates the incoming messages.
     */
    private Schema schema;

    /**
     * Whether we should insert new vocabulary or throw an error message.
     */
    private boolean insertMissingVoc = true;

    /**
     * Whether the dbReset operation is allowed or not.
     */
    private boolean dbResetAllowed = false;

    /**
     * The name of the SQL script used to clean and refill the database with
     * test data.
     */
    private String dbResetScript = null;

    /**
     * The ObjectEvent-query without data.
     */
    private final String objectEventInsert = "INSERT INTO event_ObjectEvent ("
            + "eventTime, recordTime, eventTimeZoneOffset, bizStep, " + "disposition, readPoint, bizLocation, action"
            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * The AggregationEvent-query without data.
     */
    private final String aggregationEventInsert = "INSERT INTO event_AggregationEvent ("
            + "eventTime, recordTime, eventTimeZoneOffset, bizStep, "
            + "disposition, readPoint, bizLocation, action, parentID " + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * The QuantityEvent-query without data.
     */
    private final String quantityEventInsert = "INSERT INTO event_QuantityEvent ("
            + "eventTime, recordTime, eventTimeZoneOffset, bizStep, "
            + "disposition, readPoint, bizLocation, epcClass, quantity" + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * The TransactionEvent-query without data.
     */
    private final String transactionEventInsert = "INSERT INTO event_TransactionEvent ("
            + "eventTime, recordTime, eventTimeZoneOffset, bizStep, "
            + "disposition, readPoint, bizLocation, action, parentID" + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * The DataSource holding the database.
     */
    private DataSource db = null;

    /**
     * Returns a simple information page.
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     * @param req
     *            The HttpServletRequest.
     * @param rsp
     *            The HttpServletResponse.
     * @throws IOException
     *             If an error occurred while writing the response.
     */
    public void doGet(final HttpServletRequest req, final HttpServletResponse rsp) throws IOException {
        final PrintWriter out = rsp.getWriter();

        // return an HTML info page
        rsp.setContentType("text/html");

        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
        out.println("   \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        out.println("<html>");
        out.println("<head><title>EPCIS Capture Service</title></head>");
        out.println("<body>");
        out.println("<p>This service captures EPCIS events sent to it using HTTP POST requests.<br />");
        out.println("The payload of the HTTP POST request is expected to be an XML document conforming to the EPCISDocument schema.</p>");
        out.println("<p>For further information refer to the xml schema files or check the Example <br />");
        out.println("in 'EPC Information Services (EPCIS) Version 1.0 Specification', Section 9.6.</p>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

    /**
     * Implements the EPCIS capture operation. Takes HTTP POST request, extracts
     * the payload into an XML document, validates the document against the
     * EPCIS schema, and captures the EPCIS events given in the document. Errors
     * are caught and returned as simple plaintext messages via HTTP.
     * 
     * @param req
     *            The HttpServletRequest.
     * @param rsp
     *            The HttpServletResponse.
     * @throws IOException
     *             If an error occurred while validating the request or writing
     *             the response.
     */
    public void doPost(final HttpServletRequest req, final HttpServletResponse rsp) throws IOException {
        LOG.info("EPCIS Capture Interface invoked.");
        rsp.setContentType("text/plain");
        final PrintWriter out = rsp.getWriter();

        InputStream is = null;
        // check if we have a POST request with form parameters
        if ("application/x-www-form-urlencoded".equalsIgnoreCase(req.getContentType())) {
            // check if the 'event' or 'dbReset' form parameter are given
            String event = req.getParameter("event");
            String dbReset = req.getParameter("dbReset");
            if (event != null) {
                LOG.info("Found deprecated 'event=' parameter. Refusing to process request.");
                String msg = "Starting from version 0.2.2, the EPCIS repository does not accept the EPCISDocument in the HTTP POST form parameter 'event' anymore. Please provide the EPCISDocument as HTTP POST payload instead.";
                rsp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.println(msg);
            } else if (dbReset != null && dbReset.equalsIgnoreCase("true")) {
                LOG.debug("Found 'dbReset' parameter set to 'true'.");
                rsp.setContentType("text/plain");
                if (dbResetAllowed) {
                    try {
                        Connection dbconnection = null;
                        try {
                            dbconnection = db.getConnection();
                            LOG.debug("DB connection opened.");
                            dbReset(dbconnection);
                        } finally {
                            if (dbconnection != null) {
                                dbconnection.close();
                                LOG.debug("DB connection closed.");
                            }
                        }
                        String msg = "db reset successfull";
                        LOG.info(msg);
                        rsp.setStatus(HttpServletResponse.SC_OK);
                        out.println(msg);
                    } catch (final SQLException e) {
                        String msg = "An error involving the database occurred.";
                        LOG.error(msg, e);
                        rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.println(msg);
                    } catch (final Exception e) {
                        String msg = "An unknown error occurred.";
                        LOG.error(msg, e);
                        rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.println(msg);
                    }
                } else {
                    String msg = "'dbReset' operation not allowed!";
                    LOG.info(msg);
                    rsp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.println(msg);
                }
            }
            out.flush();
            out.close();
            return;
        } else {
            is = req.getInputStream();
        }

        // parse the payload as XML document
        Document document;
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(is);
        } catch (SAXException e) {
            String msg = "Unable to parse the payload as XML document: "
                    + (e.getException() == null ? e.getMessage() : e.getException().getMessage());
            LOG.info(msg, e);
            rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("We're unable to capture the EPCIS events.");
            out.println("The data you provided is not a valid XML document.");
            out.flush();
            out.close();
            return;
        } catch (ParserConfigurationException e) {
            String msg = "XML document builder not correctly configured.";
            LOG.error(msg, e);
            rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.flush();
            out.close();
            return;
        }
        LOG.debug("Payload successfully parsed as XML document.");

        // validate the XML document against the EPCISDocument schema
        if (schema != null) {
            Validator validator = schema.newValidator();
            try {
                validator.validate(new DOMSource(document), null);
            } catch (SAXException e) {
                String msg = "XML document is not a valid EPCISDocument";
                LOG.info(msg, e);
                rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("We're unable to capture the EPCIS events.");
                out.println("The XML document you provided is not conform to the EPCISDocument schema.");
                out.flush();
                out.close();
                return;
            }
            LOG.info("Incoming capture request was successfully validated against the EPCISDocument schema.");
        } else {
            LOG.warn("Schema validator unavailable. Unable to validate EPCIS capture event against schema!");
        }

        // handle the capture operation
        Connection dbconnection = null;
        try {
            try {
                dbconnection = db.getConnection();
                LOG.debug("DB connection opened.");
                handleDocument(dbconnection, document);
            } finally {
                if (dbconnection != null) {
                    dbconnection.close();
                }
                LOG.debug("DB connection closed.");
            }
            // return OK
            LOG.info("EPCIS Capture Interface request succeeded.");
            rsp.setStatus(HttpServletResponse.SC_OK);
            out.println("Capture request succeeded.");
        } catch (final SAXException e) {
            String msg = "An error processing the XML document occurred.";
            LOG.error(msg, e);
            rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(msg);
        } catch (final SQLException e) {
            String msg = "An error involving the database occurred.";
            LOG.error(msg, e);
            rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(msg);
        } catch (final UnsupportedOperationException e) {
            String msg = e.getMessage();
            LOG.info(msg, e);
            rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(msg);
        } catch (final Exception e) {
            String msg = "An unknown error occurred.";
            LOG.error(msg, e);
            rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(msg);
        }
        out.flush();
        out.close();
    }

    /**
     * Cleans all event data from the epcis repository database.
     * 
     * @param dbconnection
     *            The database connection.
     * @throws SQLException
     *             If an error with the database occurred.
     * @throws IOException
     *             If an exception reading from the SQL script occurred.
     */
    private void dbReset(final Connection dbconnection) throws SQLException, IOException {
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
     * Parses the entire document and handles the supplied events.
     * 
     * @param dbconnection
     *            The database connection.
     * @param document
     *            The XML Document to parse and handle.
     * @throws SQLException
     *             If an error with the database occurred.
     * @throws SAXException
     *             If an error parsing the document occurred.
     */
    private void handleDocument(final Connection dbconnection, final Document document) throws SQLException,
            SAXException {
        NodeList eventList = document.getElementsByTagName("EventList");
        NodeList events = eventList.item(0).getChildNodes();

        // walk through all supplied events
        for (int i = 0; i < events.getLength(); i++) {
            Node eventNode = events.item(i);
            String nodeName = eventNode.getNodeName();

            if (nodeName.equals("ObjectEvent") || nodeName.equals("AggregationEvent")
                    || nodeName.equals("QuantityEvent") || nodeName.equals("TransactionEvent")) {
                LOG.debug("processing event " + i + ": '" + nodeName + "'.");
                handleEvent(dbconnection, eventNode);
            } else if (!nodeName.equals("#text") && !nodeName.equals("#comment")) {
                throw new SAXException("Encountered unknown event '" + nodeName + "'.");
            }
        }
    }

    /**
     * Takes an XML document node, parses it as EPCIS event and inserts the data
     * into the database. The parse routine is generic for all event types; the
     * query generation part has some if/elses to take care of different event
     * parameters.
     * 
     * @param dbconnection
     *            The database connection.
     * @param eventNode
     *            The current event node.
     * @throws SAXException
     *             If an error parsing the XML occurs.
     * @throws SQLException
     *             If an error connecting to the DB occurs.
     */
    private void handleEvent(final Connection dbconnection, final Node eventNode) throws SAXException, SQLException {
        if (eventNode != null && eventNode.getChildNodes().getLength() == 0) {
            throw new SAXException("Event element '" + eventNode.getNodeName() + "' has no children elements.");
        }
        Node curEventNode = null;

        // A lot of the initialized variables have type URI. This type isn't to
        // compare with the URI-Type of the standard. In fact, most of the
        // variables having type URI are declared as Vocabularies in the
        // Standard. Commonly, we use String for the Standard-Type URI.

        Timestamp eventTime = null;
        String eventTimeZoneOffset = null;
        String action = null;
        String parentID = null;
        Long quantity = null;
        URI bizStep = null;
        URI disposition = null;
        URI readPoint = null;
        URI bizLocation = null;
        URI epcClass = null;

        URI[] epcs = null;
        List<BusinessTransactionType> bizTransactionList = null;
        List<EventFieldExtension> fieldNameExtList = new ArrayList<EventFieldExtension>();

        try {
            for (int i = 0; i < eventNode.getChildNodes().getLength(); i++) {
                curEventNode = eventNode.getChildNodes().item(i);
                String nodeName = curEventNode.getNodeName();

                if (nodeName.equals("#text") || nodeName.equals("#comment")) {
                    // ignore text or comments
                    LOG.debug("  ignoring text or comment: '" + curEventNode.getTextContent().trim() + "'");
                    continue;
                }

                LOG.debug("  handling event field: '" + nodeName + "'");
                if (nodeName.equals("eventTime")) {
                    String xmlTime = curEventNode.getTextContent();
                    LOG.debug("    eventTime in xml is '" + xmlTime + "'");
                    try {
                        eventTime = TimeParser.parseAsTimestamp(xmlTime);
                    } catch (ParseException e) {
                        throw new SAXException("Invalid date/time (must be ISO8601).", e);
                    }
                    LOG.debug("    eventTime parsed as '" + eventTime + "'");
                } else if (nodeName.equals("eventTimeZoneOffset")) {
                    eventTimeZoneOffset = curEventNode.getTextContent();
                } else if (nodeName.equals("epcList") || nodeName.equals("childEPCs")) {
                    epcs = handleEpcs(curEventNode);
                } else if (nodeName.equals("bizTransactionList")) {
                    bizTransactionList = handleBizTransactions(curEventNode);
                } else if (nodeName.equals("action")) {
                    action = curEventNode.getTextContent();
                    if (!action.equals("ADD") && !action.equals("OBSERVE") && !action.equals("DELETE")) {
                        throw new SAXException("Encountered illegal 'action' value: " + action);
                    }
                } else if (nodeName.equals("bizStep")) {
                    bizStep = new URI(curEventNode.getTextContent());
                } else if (nodeName.equals("disposition")) {
                    disposition = new URI(curEventNode.getTextContent());
                } else if (nodeName.equals("readPoint")) {
                    Element attrElem = (Element) curEventNode;
                    Node id = attrElem.getElementsByTagName("id").item(0);
                    readPoint = new URI(id.getTextContent());
                } else if (nodeName.equals("bizLocation")) {
                    Element attrElem = (Element) curEventNode;
                    Node id = attrElem.getElementsByTagName("id").item(0);
                    bizLocation = new URI(id.getTextContent());
                } else if (nodeName.equals("epcClass")) {
                    epcClass = new URI(curEventNode.getTextContent());
                } else if (nodeName.equals("quantity")) {
                    quantity = new Long(curEventNode.getTextContent());
                } else if (nodeName.equals("parentID")) {
                    parentID = curEventNode.getTextContent();
                } else {
                    String[] parts = nodeName.split(":");
                    if (parts.length == 2) {
                        LOG.debug("    treating unknown event field as extension.");
                        String prefix = parts[0];
                        String localname = parts[1];
                        // String namespace =
                        // document.getDocumentElement().getAttribute("xmlns:" +
                        // prefix);
                        String namespace = curEventNode.lookupNamespaceURI(prefix);
                        String value = curEventNode.getTextContent();
                        fieldNameExtList.add(new EventFieldExtension(prefix, namespace, localname, value));
                    } else {
                        // this is not a valid extension
                        throw new SAXException("    encountered unknown event field: '" + nodeName + "'.");
                    }
                }
            }
        } catch (MalformedURIException e) {
            throw new SAXException("  event field '" + curEventNode.getNodeName() + "' is not of type URI: "
                    + curEventNode.getTextContent(), e);
        }

        // preparing query
        String nodeName = eventNode.getNodeName();
        PreparedStatement ps = null;
        try {
            if (nodeName.equals("AggregationEvent")) {
                ps = dbconnection.prepareStatement(aggregationEventInsert);
            } else if (nodeName.equals("ObjectEvent")) {
                ps = dbconnection.prepareStatement(objectEventInsert);
            } else if (nodeName.equals("QuantityEvent")) {
                ps = dbconnection.prepareStatement(quantityEventInsert);
            } else if (nodeName.equals("TransactionEvent")) {
                ps = dbconnection.prepareStatement(transactionEventInsert);
            } else {
                throw new SAXException("Encountered unknown event element '" + nodeName + "'.");
            }

            // parameters 1-7 of the sql query are shared by all events

            ps.setTimestamp(1, eventTime);
            // according to the specification: recordTime is the time of capture
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            // note: for testing it is handy to set recordTime=eventTime
            // ps.setTimestamp(2, eventTime);
            ps.setString(3, eventTimeZoneOffset);
            if (bizStep != null) {
                ps.setLong(4, insertVocabulary(dbconnection, "voc_BizStep", bizStep));
            } else {
                ps.setNull(4, java.sql.Types.BIGINT);
            }
            if (disposition != null) {
                ps.setLong(5, insertVocabulary(dbconnection, "voc_Disposition", disposition));
            } else {
                ps.setNull(5, java.sql.Types.BIGINT);
            }
            if (readPoint != null) {
                ps.setLong(6, insertVocabulary(dbconnection, "voc_ReadPoint", readPoint));
            } else {
                ps.setNull(6, java.sql.Types.BIGINT);
            }
            if (bizLocation != null) {
                ps.setLong(7, insertVocabulary(dbconnection, "voc_BizLoc", bizLocation));
            } else {
                ps.setNull(7, java.sql.Types.BIGINT);
            }

            // special handling for QuantityEvent
            if (nodeName.equals("QuantityEvent")) {
                if (epcClass != null) {
                    ps.setLong(8, insertVocabulary(dbconnection, "voc_EPCClass", epcClass));
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
                if (nodeName.equals("AggregationEvent") || nodeName.equals("TransactionEvent")) {
                    ps.setString(9, parentID);
                }
            }

            // insert event into database
            ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        long eventId = getLastAutoIncrementedId(dbconnection, "event_" + nodeName);

        if (!fieldNameExtList.isEmpty()) {
            for (EventFieldExtension ext : fieldNameExtList) {

                String insert = "INSERT INTO event_" + eventNode.getNodeName() + "_extensions "
                        + "(event_id, fieldname, prefix, " + ext.getValueColumnName() + ") VALUES (?, ? ,?, ?)";
                LOG.debug("QUERY: " + insert);
                ps = null;

                try {
                    ps = dbconnection.prepareStatement(insert);
                    ps.setLong(1, eventId);
                    LOG.debug("       query param 1: " + eventId);
                    ps.setString(2, ext.getFieldname());
                    LOG.debug("       query param 2: " + ext.getFieldname());
                    ps.setString(3, ext.getPrefix());
                    LOG.debug("       query param 3: " + ext.getPrefix());
                    if (ext.getIntValue() != null) {
                        ps.setInt(4, ext.getIntValue());
                        LOG.debug("       query param 4: " + ext.getIntValue());
                    } else if (ext.getFloatValue() != null) {
                        ps.setFloat(4, ext.getFloatValue());
                        LOG.debug("       query param 4: " + ext.getFloatValue());
                    } else if (ext.getDateValue() != null) {
                        ps.setTimestamp(4, ext.getDateValue());
                        LOG.debug("       query param 4: " + ext.getDateValue());
                    } else {
                        ps.setString(4, ext.getStrValue());
                        LOG.debug("       query param 4: " + ext.getStrValue());
                    }
                } finally {
                    if (ps != null) {
                        ps.executeUpdate();
                    }
                }
            }
        }

        // check if the event has any EPCs
        if (epcs != null && !nodeName.equals("QuantityEvent")) {
            // preparing statement for insertion of associated EPCs
            String insert = "INSERT INTO event_" + nodeName + "_EPCs (event_id, epc) VALUES (?, ?)";
            LOG.debug("QUERY: " + insert);
            ps = null;
            try {
                ps = dbconnection.prepareStatement(insert);

                // insert all EPCs in the EPCs array
                for (int i = 0; i < epcs.length; i++) {
                    ps.setLong(1, eventId);
                    LOG.debug("       query param 1: " + eventId);
                    ps.setString(2, epcs[i].toString());
                    LOG.debug("       query param 2: " + epcs[i].toString());
                    ps.addBatch();
                }

                ps.executeBatch();
            } finally {
                if (ps != null) {
                    ps.close();
                }
            }
        }

        // check if the event has any bizTransactions
        if (bizTransactionList != null) {
            // preparing statement for insertion of associated EPCs
            String insert = "INSERT INTO event_" + nodeName + "_bizTrans (event_id, bizTrans_id) VALUES (?, ?)";
            LOG.debug("QUERY: " + insert);
            ps = null;
            try {
                ps = dbconnection.prepareStatement(insert);
                // insert all BizTransactions into the BusinessTransaction-Table
                // and connect it with the "event_<event-name>_bizTrans"-Table
                for (final BusinessTransactionType bizTrans : bizTransactionList) {
                    ps.setLong(1, eventId);
                    LOG.debug("       query param 1: " + eventId);
                    long bTrans = insertBusinessTransaction(dbconnection, bizTrans);
                    ps.setLong(2, bTrans);
                    LOG.debug("       query param 2: " + bTrans);
                    ps.addBatch();
                }
                ps.executeBatch();
            } finally {
                if (ps != null) {
                    ps.close();
                }
            }
        }
    }

    /**
     * Retrieves the last inserted ID chosen by the autoIncrement functionality
     * in the table with the given name.
     * 
     * @param dbconnection
     *            The database connection.
     * @param tableName
     *            The name of the table for which the last inserted ID should be
     *            retrieved.
     * @return The last auto incremented ID.
     * @throws SQLException
     *             If an SQL problem with the database occurred.
     */
    private long getLastAutoIncrementedId(final Connection dbconnection, final String tableName) throws SQLException {
        String stmt = "SELECT LAST_INSERT_ID() as id FROM " + tableName;
        PreparedStatement ps = null;
        try {
            ps = dbconnection.prepareStatement(stmt);
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
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    /**
     * Inserts vocabulary into the database by searching for already existing
     * entries; if found, the corresponding ID is returned. If not found, the
     * vocabulary is extended if "insertmissingvoc" is true; otherwise an
     * SQLException is thrown
     * 
     * @param dbconnection
     *            The database connection.
     * @param tableName
     *            The name of the vocabulary table.
     * @param uri
     *            The vocabulary adapting the URI to be inserted into the
     *            vocabulary table.
     * @return The ID of an already existing vocabulary table with the given
     *         uri.
     * @throws SQLException
     *             If an SQL problem with the database occurred or if we are not
     *             allowed to insert a missing vocabulary.
     */
    private long insertVocabulary(final Connection dbconnection, final String tableName, final URI uri)
            throws SQLException {
        String stmt = "SELECT id FROM " + tableName + " WHERE uri=?";
        PreparedStatement ps = null;
        try {
            ps = dbconnection.prepareStatement(stmt);
            ps.setString(1, uri.toString());
            ResultSet rs = null;
            try {
                rs = ps.executeQuery();
                if (rs.next()) {
                    // the uri already exists
                    return rs.getLong("id");
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        // the uri does not yet exist: insert it if allowed. According to
        // the specs, some vocabulary is not allowed to be extended; this is
        // currently ignored here
        if (!insertMissingVoc) {
            throw new SQLException("Not allowed to add new vocabulary - use " + "existing vocabulary");
        } else {
            ps = null;
            try {
                stmt = "INSERT INTO " + tableName + " (uri) VALUES (?)";
                LOG.debug("QUERY: " + stmt);
                LOG.debug("       query param 1: " + uri.toString());
                ps = dbconnection.prepareStatement(stmt);
                ps.setString(1, uri.toString());
                ps.executeUpdate();
            } finally {
                if (ps != null) {
                    ps.close();
                }
            }
        }

        // get last auto_increment value and return it
        return getLastAutoIncrementedId(dbconnection, tableName);
    }

    /**
     * Parses the xml tree for epc nodes and returns a list of EPC URIs.
     * 
     * @param epcNode
     *            The parent Node from which EPC URIs should be extracted.
     * @return An array of vocabularies containing all the URIs found in the
     *         given node.
     * @throws MalformedURIException
     *             If a string is not parsable as URI.
     * @throws SAXParseException
     *             If an unknown tag (no &lt;epc&gt;) is encountered.
     */
    private URI[] handleEpcs(final Node epcNode) throws MalformedURIException, SAXParseException {
        List<URI> epcList = new ArrayList<URI>();

        for (int i = 0; i < epcNode.getChildNodes().getLength(); i++) {
            Node curNode = epcNode.getChildNodes().item(i);
            if (curNode.getNodeName().equals("epc")) {
                epcList.add(new URI(curNode.getTextContent()));
            } else {
                if (curNode.getNodeName() != "#text" && curNode.getNodeName() != "#comment") {
                    throw new SAXParseException("Unknown XML tag: " + curNode.getNodeName(), null);
                }
            }
        }

        URI[] epcs = new URI[epcList.size()];
        epcList.toArray(epcs);

        return epcs;
    }

    /**
     * Parses the xml tree for epc nodes and returns a List of BizTransaction
     * URIs with their corresponding type.
     * 
     * @param bizNode
     *            The parent Node from which BizTransaction URIs should be
     *            extracted.
     * @return A List of BizTransaction.
     * @throws MalformedURIException
     *             If a string is not parsable as URI.
     * @throws SAXParseException
     *             If an unknown tag (no &lt;epc&gt;) is encountered.
     */
    private List<BusinessTransactionType> handleBizTransactions(final Node bizNode) throws MalformedURIException,
            SAXParseException {
        final List<BusinessTransactionType> bizList = new ArrayList<BusinessTransactionType>();

        for (int i = 0; i < bizNode.getChildNodes().getLength(); i++) {
            Node curNode = bizNode.getChildNodes().item(i);
            if (curNode.getNodeName().equals("bizTransaction")) {
                String bizTransType = curNode.getAttributes().item(0).getTextContent();
                String bizTrans = curNode.getTextContent();
                BusinessTransactionType bt = new BusinessTransactionType(bizTrans);
                bt.setType(new URI(bizTransType));
                bizList.add(bt);
            } else {
                if (!curNode.getNodeName().equals("#text") && !curNode.getNodeName().equals("#comment")) {
                    throw new SAXParseException("Unknown XML tag: " + curNode.getNodeName(), null);
                }
            }
        }
        return bizList;
    }

    /**
     * Inserts the BusinessTransactionType and the BusinessTransactionID into
     * the BusinessTransaction-Table if necessary.
     * 
     * @param dbconnection
     *            The database connection.
     * @param bizTrans
     *            The BusinessTransaction to be inserted.
     * @return The ID from the BusinessTransaction-table.
     * @throws SQLException
     *             If an SQL problem with the database occurred.
     */
    private long insertBusinessTransaction(final Connection dbconnection, final BusinessTransactionType bizTrans)
            throws SQLException {
        final long id = insertVocabulary(dbconnection, "voc_BizTrans", bizTrans);
        final long type = insertVocabulary(dbconnection, "voc_BizTransType", bizTrans.getType());

        String stmt = "SELECT id FROM BizTransaction WHERE bizTrans=? AND type=?";
        PreparedStatement ps = null;
        try {
            ps = dbconnection.prepareStatement(stmt);
            ps.setLong(1, id);
            ps.setLong(2, type);

            ResultSet rs = null;
            try {
                rs = ps.executeQuery();
                if (rs.next()) {
                    // the BusinessTransaction already exists
                    return rs.getLong("id");
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        ps = null;
        try {
            // insert the BusinessTransaction
            stmt = "INSERT INTO BizTransaction (bizTrans, type) VALUES (?, ?)";
            LOG.debug("QUERY: " + stmt);
            LOG.debug("       query param 1: " + id);
            LOG.debug("       query param 2: " + type);
            ps = dbconnection.prepareStatement(stmt);
            ps.setLong(1, id);
            ps.setLong(2, type);
            ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return getLastAutoIncrementedId(dbconnection, "BizTransaction");
    }

    public DataSource getDb() {
        return db;
    }

    public void setDb(DataSource db) {
        this.db = db;
    }

    public boolean isDbResetAllowed() {
        return dbResetAllowed;
    }

    public void setDbResetAllowed(boolean dbResetAllowed) {
        this.dbResetAllowed = dbResetAllowed;
    }

    public String getDbResetScript() {
        return dbResetScript;
    }

    public void setDbResetScript(String dbResetScript) {
        this.dbResetScript = dbResetScript;
    }

    public boolean isInsertMissingVoc() {
        return insertMissingVoc;
    }

    public void setInsertMissingVoc(boolean insertMissingVoc) {
        this.insertMissingVoc = insertMissingVoc;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

}
