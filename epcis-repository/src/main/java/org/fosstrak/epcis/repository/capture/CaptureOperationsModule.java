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

package org.accada.epcis.repository.capture;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.accada.epcis.repository.EpcisConstants;
import org.accada.epcis.repository.model.Action;
import org.accada.epcis.repository.model.AggregationEvent;
import org.accada.epcis.repository.model.BaseEvent;
import org.accada.epcis.repository.model.BusinessLocationId;
import org.accada.epcis.repository.model.BusinessStepId;
import org.accada.epcis.repository.model.BusinessTransaction;
import org.accada.epcis.repository.model.BusinessTransactionId;
import org.accada.epcis.repository.model.BusinessTransactionTypeId;
import org.accada.epcis.repository.model.DispositionId;
import org.accada.epcis.repository.model.EPCClass;
import org.accada.epcis.repository.model.EventFieldExtension;
import org.accada.epcis.repository.model.ObjectEvent;
import org.accada.epcis.repository.model.QuantityEvent;
import org.accada.epcis.repository.model.ReadPointId;
import org.accada.epcis.repository.model.TransactionEvent;
import org.accada.epcis.repository.model.VocabularyElement;
import org.accada.epcis.utils.TimeParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionFactoryImplementor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * CaptureOperationsModule implements the core capture operations. Converts XML
 * events delivered by HTTP POST into SQL and inserts them into the database.
 * <p>
 * TODO: the parsing of the xml inputstream should be done in the
 * CaptureOperationsServlet; this class should implement EpcisCaptureInterface
 * such that CaptureOperationsServlet can call its capture method and provide it
 * with the parsed events.
 * 
 * @author David Gubler
 * @author Alain Remund
 * @author Marco Steybe
 */
public class CaptureOperationsModule {

    private static final Log LOG = LogFactory.getLog(CaptureOperationsModule.class);

    private static final Map<String, Class<?>> vocClassMap = new HashMap<String, Class<?>>();

    static {
        vocClassMap.put(EpcisConstants.BUSINESS_LOCATION_ID, BusinessLocationId.class);
        vocClassMap.put(EpcisConstants.BUSINESS_STEP_ID, BusinessStepId.class);
        vocClassMap.put(EpcisConstants.BUSINESS_TRANSACTION, BusinessTransactionId.class);
        vocClassMap.put(EpcisConstants.BUSINESS_TRANSACTION_TYPE_ID, BusinessTransactionTypeId.class);
        vocClassMap.put(EpcisConstants.DISPOSITION_ID, DispositionId.class);
        vocClassMap.put(EpcisConstants.EPC_CLASS, EPCClass.class);
        vocClassMap.put(EpcisConstants.READ_POINT_ID, ReadPointId.class);
    }

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
     * Interface to the database.
     */
    private SessionFactory sessionFactory;

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
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    SessionFactory sessionFactory = session.getSessionFactory();
                    Connection dbconnection = ((SessionFactoryImplementor) sessionFactory).getConnectionProvider().getConnection();
                    LOG.info("Running db reset script.");
                    Statement stmt = dbconnection.createStatement();
                    if (dbResetScript != null) {
                        BufferedReader reader = new BufferedReader(new FileReader(dbResetScript));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stmt.addBatch(line);
                        }
                    }
                    stmt.executeBatch();
                    tx.commit();
                } catch (Exception e) {
                    LOG.error("dbReset failed: " + e.toString(), e);
                    if (tx != null) {
                        tx.rollback();
                    }
                    throw new SQLException(e.toString());
                }
            } finally {
                if (session != null) {
                    session.close();
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
     * @throws SAXException
     *             If the XML document is malformed or invalid
     */

    public void doCapture(InputStream in, Principal principal) throws SAXException, IOException {

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
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                LOG.debug("DB connection opened.");
                handleDocument(session, document);
                tx.commit();
                // return OK
                LOG.info("EPCIS Capture Interface request succeeded.");
            } catch (SAXException e) {
                LOG.error("EPCIS Capture Interface request failed: " + e.toString());
                if (tx != null) {
                    tx.rollback();
                }
                throw e;
            } catch (IOException e) {
                LOG.error("EPCIS Capture Interface request failed: " + e.toString());
                if (tx != null) {
                    tx.rollback();
                }
                throw e;
            } catch (Exception e) {
                // Hibernate throws RuntimeExceptions, so don't let them
                // (or anything else) escape without clean up
                LOG.error("EPCIS Capture Interface request failed: " + e.toString(), e);
                if (tx != null) {
                    tx.rollback();
                }
                throw new IOException(e.toString());
            }
        } finally {
            if (session != null) {
                session.close();
            }
            sessionFactory.getStatistics().logSummary();
            LOG.debug("DB connection closed.");
        }
    }

    /**
     * Parses the entire document and handles the supplied events.
     * 
     * @throws SAXException,
     *             IOException If an error parsing the document occurred.
     */
    private void handleDocument(Session session, Document document) throws SAXException, IOException {
        NodeList eventList = document.getElementsByTagName("EventList");
        NodeList events = eventList.item(0).getChildNodes();

        // walk through all supplied events
        int eventCount = 0;
        for (int i = 0; i < events.getLength(); i++) {
            Node eventNode = events.item(i);
            String nodeName = eventNode.getNodeName();

            if (nodeName.equals(EpcisConstants.OBJECT_EVENT) || nodeName.equals(EpcisConstants.AGGREGATION_EVENT)
                    || nodeName.equals(EpcisConstants.QUANTITY_EVENT)
                    || nodeName.equals(EpcisConstants.TRANSACTION_EVENT)) {
                LOG.debug("processing event " + i + ": '" + nodeName + "'.");
                handleEvent(session, eventNode);
                eventCount++;
                if (eventCount % 50 == 0) {
                    session.flush();
                    session.clear();
                }
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
     * @throws SAXException,
     *             IOException If an error parsing the XML occurs.
     */
    private void handleEvent(Session session, final Node eventNode) throws SAXException, IOException {
        if (eventNode == null) {
            // nothing to do
            return;
        } else if (eventNode.getChildNodes().getLength() == 0) {
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
        String parentId = null;
        Long quantity = null;
        String bizStepUri = null;
        String dispositionUri = null;
        String readPointUri = null;
        String bizLocationUri = null;
        String epcClassUri = null;

        List<String> epcs = null;
        List<BusinessTransaction> bizTransList = null;
        List<EventFieldExtension> fieldNameExtList = new ArrayList<EventFieldExtension>();

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
                bizTransList = handleBizTransactions(session, curEventNode);
            } else if (nodeName.equals("action")) {
                action = curEventNode.getTextContent();
                if (!action.equals("ADD") && !action.equals("OBSERVE") && !action.equals("DELETE")) {
                    throw new SAXException("Encountered illegal 'action' value: " + action);
                }
            } else if (nodeName.equals("bizStep")) {
                bizStepUri = curEventNode.getTextContent();
            } else if (nodeName.equals("disposition")) {
                dispositionUri = curEventNode.getTextContent();
            } else if (nodeName.equals("readPoint")) {
                Element attrElem = (Element) curEventNode;
                Node id = attrElem.getElementsByTagName("id").item(0);
                readPointUri = id.getTextContent();
            } else if (nodeName.equals("bizLocation")) {
                Element attrElem = (Element) curEventNode;
                Node id = attrElem.getElementsByTagName("id").item(0);
                bizLocationUri = id.getTextContent();
            } else if (nodeName.equals("epcClass")) {
                epcClassUri = curEventNode.getTextContent();
            } else if (nodeName.equals("quantity")) {
                quantity = Long.valueOf(curEventNode.getTextContent());
            } else if (nodeName.equals("parentID")) {
                parentId = curEventNode.getTextContent();
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
                    EventFieldExtension evf = new EventFieldExtension(prefix, namespace, localname, value);
                    fieldNameExtList.add(evf);
                } else {
                    // this is not a valid extension
                    throw new SAXException("    encountered unknown event field: '" + nodeName + "'.");
                }
            }
        }

        String nodeName = eventNode.getNodeName();
        VocabularyElement bizStep = bizStepUri != null ? getOrInsertVocabularyElement(session,
                EpcisConstants.BUSINESS_STEP_ID, String.valueOf(bizStepUri)) : null;
        VocabularyElement disposition = dispositionUri != null ? getOrInsertVocabularyElement(session,
                EpcisConstants.DISPOSITION_ID, String.valueOf(dispositionUri)) : null;
        VocabularyElement readPoint = readPointUri != null ? getOrInsertVocabularyElement(session,
                EpcisConstants.READ_POINT_ID, String.valueOf(readPointUri)) : null;
        VocabularyElement bizLocation = bizLocationUri != null ? getOrInsertVocabularyElement(session,
                EpcisConstants.BUSINESS_LOCATION_ID, String.valueOf(bizLocationUri)) : null;
        VocabularyElement epcClass = epcClassUri != null ? getOrInsertVocabularyElement(session,
                EpcisConstants.EPC_CLASS, String.valueOf(epcClassUri)) : null;

        BaseEvent be;
        if (nodeName.equals(EpcisConstants.AGGREGATION_EVENT)) {
            AggregationEvent ae = new AggregationEvent();
            ae.setParentId(parentId);
            ae.setChildEpcs(epcs);
            ae.setAction(Action.valueOf(action));
            be = ae;
        } else if (nodeName.equals(EpcisConstants.OBJECT_EVENT)) {
            ObjectEvent oe = new ObjectEvent();
            oe.setAction(Action.valueOf(action));
            if (epcs != null && epcs.size() > 0) {
                oe.setEpcList(epcs);
            }
            be = oe;
        } else if (nodeName.equals(EpcisConstants.QUANTITY_EVENT)) {
            QuantityEvent qe = new QuantityEvent();
            qe.setEpcClass((EPCClass) epcClass);
            if (quantity != null) {
                qe.setQuantity(quantity.longValue());
            }
            be = qe;
        } else if (nodeName.equals(EpcisConstants.TRANSACTION_EVENT)) {
            TransactionEvent te = new TransactionEvent();
            te.setParentId(parentId);
            te.setEpcList(epcs);
            te.setAction(Action.valueOf(action));
            be = te;
        } else {
            throw new SAXException("Encountered unknown event element '" + nodeName + "'.");
        }

        be.setEventTime(eventTime);
        be.setRecordTime(recordTime);
        be.setEventTimeZoneOffset(eventTimeZoneOffset);
        be.setBizStep((BusinessStepId) bizStep);
        be.setDisposition((DispositionId) disposition);
        be.setBizLocation((BusinessLocationId) bizLocation);
        be.setReadPoint((ReadPointId) readPoint);
        if (bizTransList != null && bizTransList.size() > 0) {
            be.setBizTransList(bizTransList);
        }
        if (!fieldNameExtList.isEmpty()) {
            be.setExtensions(fieldNameExtList);
        }

        session.save(be);
    }

    /**
     * Parses the xml tree for epc nodes and returns a list of EPC URIs.
     * 
     * @param epcNode
     *            The parent Node from which EPC URIs should be extracted.
     * @return An array of vocabularies containing all the URIs found in the
     *         given node.
     * @throws SAXException
     *             If an unknown tag (no &lt;epc&gt;) is encountered.
     */
    private List<String> handleEpcs(final Node epcNode) throws SAXException {
        List<String> epcList = new ArrayList<String>();

        for (int i = 0; i < epcNode.getChildNodes().getLength(); i++) {
            Node curNode = epcNode.getChildNodes().item(i);
            if (curNode.getNodeName().equals("epc")) {
                epcList.add(curNode.getTextContent());
            } else {
                if (curNode.getNodeName() != "#text" && curNode.getNodeName() != "#comment") {
                    throw new SAXException("Unknown XML tag: " + curNode.getNodeName(), null);
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
     * @throws SAXException
     *             If an unknown tag (no &lt;epc&gt;) is encountered.
     */
    private List<BusinessTransaction> handleBizTransactions(Session session, Node bizNode) throws SAXException {
        List<BusinessTransaction> bizTransactionList = new ArrayList<BusinessTransaction>();

        for (int i = 0; i < bizNode.getChildNodes().getLength(); i++) {
            Node curNode = bizNode.getChildNodes().item(i);
            if (curNode.getNodeName().equals("bizTransaction")) {
                String bizTransTypeUri = curNode.getAttributes().item(0).getTextContent();
                String bizTransUri = curNode.getTextContent();
                BusinessTransactionId bizTrans = (BusinessTransactionId) getOrInsertVocabularyElement(session,
                        EpcisConstants.BUSINESS_TRANSACTION, bizTransUri.toString());
                BusinessTransactionTypeId type = (BusinessTransactionTypeId) getOrInsertVocabularyElement(session,
                        EpcisConstants.BUSINESS_TRANSACTION_TYPE_ID, bizTransTypeUri.toString());

                Criteria c0 = session.createCriteria(BusinessTransaction.class);
                c0.add(Restrictions.eq("bizTransaction", bizTrans));
                c0.add(Restrictions.eq("type", type));
                BusinessTransaction bizTransaction = (BusinessTransaction) c0.uniqueResult();

                if (bizTransaction == null) {
                    bizTransaction = new BusinessTransaction();
                    bizTransaction.setBizTransaction(bizTrans);
                    bizTransaction.setType(type);
                    session.save(bizTransaction);
                }

                bizTransactionList.add(bizTransaction);

            } else {
                if (!curNode.getNodeName().equals("#text") && !curNode.getNodeName().equals("#comment")) {
                    throw new SAXException("Unknown XML tag: " + curNode.getNodeName(), null);
                }
            }
        }
        return bizTransactionList;
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
     * @throws UnsupportedOperationException
     *             If we are not allowed to insert a missing vocabulary.
     */
    public VocabularyElement getOrInsertVocabularyElement(Session session, String vocabularyType,
            String vocabularyElement) throws SAXException {
        Class<?> c = vocClassMap.get(vocabularyType);
        Criteria c0 = session.createCriteria(c);
        c0.setCacheable(true);
        c0.add(Restrictions.eq("uri", vocabularyElement));
        VocabularyElement ve = (VocabularyElement) c0.uniqueResult();
        if (ve == null) {
            // the uri does not yet exist: insert it if allowed. According to
            // the specs, some vocabulary is not allowed to be extended; this is
            // currently ignored here
            if (!insertMissingVoc) {
                throw new UnsupportedOperationException("Not allowed to add new vocabulary - use "
                        + "existing vocabulary");
            } else {
                // VocabularyElement subclasses should always have public
                // zero-arg constructor to avoid problems here
                try {
                    ve = (VocabularyElement) c.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                ve.setUri(vocabularyElement);
                session.save(ve);
                session.flush();

            }
        }
        return ve;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
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
