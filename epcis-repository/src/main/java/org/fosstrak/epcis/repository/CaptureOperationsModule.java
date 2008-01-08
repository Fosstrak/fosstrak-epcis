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

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
     * The DataSource holding the database.
     */
    private DataSource db = null;

    /**
     * An object that interacts with the database.
     */
    private CaptureOperationsBackend captureOperationsBackend;

    /**
     * Resets the database.
     * 
     * @throws SQLException
     *             If something goes wrong resetting the database.
     * @throws IOException
     *             If something goes wrong reading the reset script.
     * @throws UnsupportedOperationsException
     *             If database resets are not allowed.
     */
    public void doDbReset() throws SQLException, IOException {
        if (dbResetAllowed) {
            Connection dbconnection = null;
            try {
                dbconnection = db.getConnection();
                LOG.debug("DB connection opened.");
                captureOperationsBackend.dbReset(dbconnection, dbResetScript);
            } finally {
                if (dbconnection != null) {
                    dbconnection.close();
                    LOG.debug("DB connection closed.");
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Implements the EPCIS capture operation. Takes an input stream, extracts
     * the payload into an XML document, validates the document against the
     * EPCIS schema, and captures the EPCIS events given in the document.
     * 
     * @throws IOException
     *             If an error occurred while validating the request or writing
     *             the response.
     * @throws ParserConfigurationException
     * @throws MalformedURIException
     *             if a URI is malformed or invalid
     * @throws SAXException
     *             If the XML document is malformed or invalid
     * @throws SQLException
     *             A database exception
     */

    public void doCapture(InputStream in) throws SAXException, IOException, SQLException, MalformedURIException {

        // parse the payload as XML document
        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(in);
            LOG.debug("Payload successfully parsed as XML document.");

            // validate the XML document against the EPCISDocument schema
            if (schema != null) {
                Validator validator = schema.newValidator();
                validator.validate(new DOMSource(document), null);
                LOG.info("Incoming capture request was successfully validated against the EPCISDocument schema.");
            } else {
                LOG.warn("Schema validator unavailable. Unable to validate EPCIS capture event against schema!");
            }

        } catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }

        // handle the capture operation
        CaptureOperationsSession session = null;
        try {
            session = captureOperationsBackend.openSession(db);
            LOG.debug("DB connection opened.");
            handleDocument(session, document);
            session.commit();
            // return OK
            LOG.info("EPCIS Capture Interface request succeeded.");
        } catch (SAXException e) {
            LOG.error("EPCIS Capture Interface request failed: " + e.toString());
            session.rollback();
            throw e;
        } catch (IOException e) {
            LOG.error("EPCIS Capture Interface request failed: " + e.toString());
            session.rollback();
            throw e;
        } catch (SQLException e) {
            LOG.error("EPCIS Capture Interface request failed: " + e.toString());
            session.rollback();
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
            LOG.debug("DB connection closed.");
        }

    }

    /**
     * Parses the entire document and handles the supplied events.
     * 
     * @throws SQLException
     *             If an error with the database occurred.
     * @throws SAXException
     *             If an error parsing the document occurred.
     */
    private void handleDocument(CaptureOperationsSession dbconnection, Document document) throws SQLException,
            SAXException, IOException {
        NodeList eventList = document.getElementsByTagName("EventList");
        NodeList events = eventList.item(0).getChildNodes();

        // walk through all supplied events
        for (int i = 0; i < events.getLength(); i++) {
            Node eventNode = events.item(i);
            String nodeName = eventNode.getNodeName();

            if (nodeName.equals(Constants.OBJECT_EVENT) || nodeName.equals(Constants.AGGREGATION_EVENT)
                    || nodeName.equals(Constants.QUANTITY_EVENT) || nodeName.equals(Constants.TRANSACTION_EVENT)) {
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
     * @param eventNode
     *            The current event node.
     * @throws SAXException
     *             If an error parsing the XML occurs.
     * @throws SQLException
     *             If an error connecting to the DB occurs.
     */
    private void handleEvent(CaptureOperationsSession session, final Node eventNode) throws SAXException, SQLException,
            IOException {
        if (eventNode != null && eventNode.getChildNodes().getLength() == 0) {
            throw new SAXException("Event element '" + eventNode.getNodeName() + "' has no children elements.");
        }
        Node curEventNode = null;

        // A lot of the initialized variables have type URI. This type isn't to
        // compare with the URI-Type of the standard. In fact, most of the
        // variables having type URI are declared as Vocabularies in the
        // Standard. Commonly, we use String for the Standard-Type URI.

        Timestamp eventTime = null;
        Timestamp recordTime = new Timestamp(System.currentTimeMillis());
        String eventTimeZoneOffset = null;
        String action = null;
        String parentID = null;
        Long quantity = null;
        URI bizStep = null;
        URI disposition = null;
        URI readPoint = null;
        URI bizLocation = null;
        URI epcClass = null;

        List<String> epcs = null;
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
                } else if (nodeName.equals("recordTime")) {
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
        long eventId;
        Long bizStepId = bizStep != null ? getOrInsertVocabularyElement(session, new URI(
                Constants.BUSINESS_STEP_ID_VTYPE), bizStep) : null;
        Long dispositionId = disposition != null ? getOrInsertVocabularyElement(session, new URI(
                Constants.DISPOSITION_ID_VTYPE), disposition) : null;
        Long readPointId = readPoint != null ? getOrInsertVocabularyElement(session, new URI(
                Constants.READ_POINT_ID_VTYPE), readPoint) : null;
        Long bizLocationId = bizLocation != null ? getOrInsertVocabularyElement(session, new URI(
                Constants.BUSINESS_LOCATION_ID_VTYPE), bizLocation) : null;
        Long epcClassId = epcClass != null ? getOrInsertVocabularyElement(session, new URI(Constants.EPC_CLASS_VTYPE),
                epcClass) : null;

        if (nodeName.equals(Constants.AGGREGATION_EVENT)) {
            eventId = captureOperationsBackend.insertAggregationEvent(session, eventTime, recordTime,
                    eventTimeZoneOffset, bizStepId, dispositionId, readPointId, bizLocationId, action, parentID);
        } else if (nodeName.equals(Constants.OBJECT_EVENT)) {
            eventId = captureOperationsBackend.insertObjectEvent(session, eventTime, recordTime, eventTimeZoneOffset,
                    bizStepId, dispositionId, readPointId, bizLocationId, action);
        } else if (nodeName.equals(Constants.QUANTITY_EVENT)) {
            eventId = captureOperationsBackend.insertQuantityEvent(session, eventTime, recordTime, eventTimeZoneOffset,
                    bizStepId, dispositionId, readPointId, bizLocationId, epcClassId, quantity);
        } else if (nodeName.equals(Constants.TRANSACTION_EVENT)) {
            eventId = captureOperationsBackend.insertTransactionEvent(session, eventTime, recordTime,
                    eventTimeZoneOffset, bizStepId, dispositionId, readPointId, bizLocationId, action, parentID);
        } else {
            throw new SAXException("Encountered unknown event element '" + nodeName + "'.");
        }

        if (!fieldNameExtList.isEmpty()) {
            captureOperationsBackend.insertExtensionFieldsForEvent(session, eventId, nodeName, fieldNameExtList);
        }

        // check if the event has any EPCs
        if (epcs != null && !nodeName.equals(Constants.QUANTITY_EVENT)) {
            captureOperationsBackend.insertEpcsForEvent(session, eventId, nodeName, epcs);
        }

        // check if the event has any bizTransactions
        if (bizTransactionList != null) {
            captureOperationsBackend.insertBusinessTransactionsForEvent(session, eventId, nodeName, bizTransactionList);
        }
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
    private List<String> handleEpcs(final Node epcNode) throws MalformedURIException, SAXParseException {
        List<String> epcList = new ArrayList<String>();

        for (int i = 0; i < epcNode.getChildNodes().getLength(); i++) {
            Node curNode = epcNode.getChildNodes().item(i);
            if (curNode.getNodeName().equals("epc")) {
                epcList.add(new URI(curNode.getTextContent()).toString());
            } else {
                if (curNode.getNodeName() != "#text" && curNode.getNodeName() != "#comment") {
                    throw new SAXParseException("Unknown XML tag: " + curNode.getNodeName(), null);
                }
            }
        }
        return epcList;
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
     * Inserts vocabulary into the database by searching for already existing
     * entries; if found, the corresponding ID is returned. If not found, the
     * vocabulary is extended if "insertmissingvoc" is true; otherwise an
     * SQLException is thrown
     * 
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
    private Long getOrInsertVocabularyElement(CaptureOperationsSession session, URI vocabularyType,
            URI vocabularyElement) throws SQLException {
        Long vocabularyElementId = captureOperationsBackend.getVocabularyElement(session,
                String.valueOf(vocabularyType), String.valueOf(vocabularyElement));
        if (vocabularyElementId != null) {
            return vocabularyElementId;
        } else {
            // the uri does not yet exist: insert it if allowed. According to
            // the specs, some vocabulary is not allowed to be extended; this is
            // currently ignored here
            if (!insertMissingVoc) {
                throw new SQLException("Not allowed to add new vocabulary - use " + "existing vocabulary");
            } else {
                return captureOperationsBackend.insertVocabularyElement(session, String.valueOf(vocabularyType),
                        String.valueOf(vocabularyElement));
            }
        }
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

    public CaptureOperationsBackend getCaptureOperationsBackend() {
        return captureOperationsBackend;
    }

    public void setCaptureOperationsBackend(CaptureOperationsBackend captureOperationsBackend) {
        this.captureOperationsBackend = captureOperationsBackend;
    }

}
