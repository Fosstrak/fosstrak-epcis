/*
 * Copyright (C) 2007, ETH Zurich
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.accada.epcis.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.accada.epcis.soapapi.ActionType;
import org.accada.epcis.soapapi.AggregationEventExtensionType;
import org.accada.epcis.soapapi.AggregationEventType;
import org.accada.epcis.soapapi.AttributeType;
import org.accada.epcis.soapapi.BusinessLocationType;
import org.accada.epcis.soapapi.BusinessTransactionType;
import org.accada.epcis.soapapi.EPC;
import org.accada.epcis.soapapi.EPCISEventExtensionType;
import org.accada.epcis.soapapi.EPCISEventListExtensionType;
import org.accada.epcis.soapapi.EventListType;
import org.accada.epcis.soapapi.ObjectEventExtensionType;
import org.accada.epcis.soapapi.ObjectEventType;
import org.accada.epcis.soapapi.QuantityEventExtensionType;
import org.accada.epcis.soapapi.QuantityEventType;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryResultsBody;
import org.accada.epcis.soapapi.QueryResultsExtensionType;
import org.accada.epcis.soapapi.ReadPointType;
import org.accada.epcis.soapapi.TransactionEventExtensionType;
import org.accada.epcis.soapapi.TransactionEventType;
import org.accada.epcis.soapapi.VocabularyElementType;
import org.accada.epcis.soapapi.VocabularyType;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.NullAttributes;
import org.apache.axis.message.Text;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses the XML representation of an EPCIS query results into a QueryResults
 * object for use with axis.
 * 
 * @author Marco Steybe
 */
public final class QueryResultsParser {

    private static final Logger LOG = Logger.getLogger(QueryResultsParser.class);

    /**
     * Empty default contructor. Utility classes should not have public
     * constructors.
     */
    private QueryResultsParser() {
    }

    /**
     * A helper method to parse and convert the XML representation of an EPCIS
     * query results into a QueryResults object.
     * 
     * @param xmlQueryResults
     *            The InputStream containing the XML representation of a
     *            QueryResults object.
     * @return The parsed QueryResults object.
     */
    public static QueryResults parseQueryResults(
            final InputStream xmlQueryResults) {
        QueryResults queryResults = null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document epcisq;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            epcisq = builder.parse(xmlQueryResults);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse the XML query.", e);
        }
        Element queryName = (Element) epcisq.getElementsByTagName("queryName").item(
                0);

        if (!queryName.getTextContent().equals("SimpleEventQuery")) {
            Element vocList = (Element) epcisq.getElementsByTagName(
                    "VocabularyList").item(0);
            VocabularyType[] vocs = null;
            if (vocList != null) {
                NodeList vocsList = vocList.getElementsByTagName("Vocabulary");
                if (vocsList != null && vocsList.getLength() != 0) {
                    vocs = handleVocabularies(vocsList);
                }
            }

            EventListType eventList = null;
            QueryResultsBody queryResultsBody = new QueryResultsBody(eventList,
                    vocs);

            // no extensions implemented!
            QueryResultsExtensionType queryResultsExtension = null;

            String queryNam = queryName.getTextContent();
            String subscriptionId = null;
            MessageElement[] message = null;
            queryResults = new QueryResults(queryNam, subscriptionId,
                    queryResultsBody, queryResultsExtension, message);
        } else {
            Element eventList = (Element) epcisq.getElementsByTagName(
                    "EventList").item(0);
            ObjectEventType[] objectEvents = null;
            AggregationEventType[] aggrEvents = null;
            QuantityEventType[] quantEvents = null;
            TransactionEventType[] transEvents = null;
            if (eventList != null) {
                NodeList objectEventsList = eventList.getElementsByTagName("ObjectEvent");
                if (objectEventsList != null
                        && objectEventsList.getLength() != 0) {
                    objectEvents = handleObjectEvents(objectEventsList);
                }

                NodeList aggregationEventsList = eventList.getElementsByTagName("AggregationEvent");
                if (aggregationEventsList != null
                        && aggregationEventsList.getLength() != 0) {
                    aggrEvents = handleAggregationEvents(aggregationEventsList);
                }

                NodeList quantityEventsList = eventList.getElementsByTagName("QuantityEvent");
                if (quantityEventsList != null
                        && quantityEventsList.getLength() != 0) {
                    quantEvents = handleQuantityEvents(quantityEventsList);
                }

                NodeList transactionEventsList = eventList.getElementsByTagName("TransactionEvent");
                if (transactionEventsList != null
                        && transactionEventsList.getLength() != 0) {
                    transEvents = handleTransactionEvents(transactionEventsList);
                }
            }

            EPCISEventListExtensionType epcisEventList = null;
            MessageElement[] message = null;
            EventListType eventListType = new EventListType(objectEvents,
                    aggrEvents, quantEvents, transEvents, epcisEventList,
                    message);

            VocabularyType[] vocabulary = null;
            QueryResultsBody queryResultsBody = new QueryResultsBody(
                    eventListType, vocabulary);

            // no extensions implemented!
            QueryResultsExtensionType queryResultsExtension = null;

            String queryNam = queryName.getTextContent();
            String subscriptionId = null;
            queryResults = new QueryResults(queryNam, subscriptionId,
                    queryResultsBody, queryResultsExtension, message);
        }
        return queryResults;
    }

    /**
     * Parses the object event inside the QueryResults event list.
     * 
     * @param objectEventList
     *            The NodeList parsed from the XML containing the object events.
     * @return An array of ObjectEventType.
     */
    private static ObjectEventType[] handleObjectEvents(
            final NodeList objectEventList) {
        Vector<ObjectEventType> list = new Vector<ObjectEventType>();

        for (int i = 0; i < objectEventList.getLength(); i++) {
            Element objectEvent = (Element) objectEventList.item(i);

            // parse <eventTime>
            Node eventTimeNode = objectEvent.getElementsByTagName("eventTime").item(
                    0);
            Calendar eventTime = handleTime(eventTimeNode);

            // parse <recordTime>
            Node recordTimeNode = objectEvent.getElementsByTagName("recordTime").item(
                    0);
            Calendar recordTime = handleTime(recordTimeNode);

            // parse <eventTimeZoneOffset>
            Node eventTimeZoneOffsetNode = objectEvent.getElementsByTagName(
                    "eventTimeZoneOffset").item(0);
            String eventTimeZoneOffset = eventTimeZoneOffsetNode.getTextContent();

            // TODO parse extension
            EPCISEventExtensionType baseExtension = null;

            // TODO parse message element
            List<MessageElement> messages = new ArrayList<MessageElement>();
            Node temperature = objectEvent.getElementsByTagName(
                    "hls:temperature").item(0);
            if (temperature != null) {
                MessageElement me = new MessageElement("temperature", "hls",
                        "http://schema.hls.com/extension");
                me.setValue(temperature.getTextContent());
                messages.add(me);
            }
            Node batchNumber = objectEvent.getElementsByTagName(
                    "hls:batchNumber").item(0);
            if (batchNumber != null) {
                MessageElement me = new MessageElement("batchNumber", "hls",
                        "http://schema.hls.com/extension");
                me.setValue(batchNumber.getTextContent());
                messages.add(me);
            }
            MessageElement[] message = new MessageElement[messages.size()];
            message = messages.toArray(message);

            // parse <epcList>
            Node epcListNode = objectEvent.getElementsByTagName("epcList").item(
                    0);
            EPC[] epcList = handleEpcList(epcListNode);

            // parse <action>
            Node actionNode = objectEvent.getElementsByTagName("action").item(0);
            ActionType action = handleAction(actionNode);

            // parse <bizStep>
            Node bizStepNode = objectEvent.getElementsByTagName("bizStep").item(
                    0);
            URI bizStep = handleUri(bizStepNode);

            // parse <disposition>
            Node dispNode = objectEvent.getElementsByTagName("disposition").item(
                    0);
            URI disposition = handleUri(dispNode);

            // parse <readPoint>
            Node readPointNode = objectEvent.getElementsByTagName("readPoint").item(
                    0);
            ReadPointType readPoint = null;
            if (readPointNode != null) {
                Element readPointElement = (Element) readPointNode;
                Node idNode = readPointElement.getElementsByTagName("id").item(
                        0);
                URI id = handleUri(idNode);
                readPoint = new ReadPointType(id, null, null);
            }

            // parse <bizLocation>
            Node bizLocNode = objectEvent.getElementsByTagName("bizLocation").item(
                    0);
            BusinessLocationType bizLocation = null;
            if (bizLocNode != null) {
                Element bizLocElement = (Element) bizLocNode;
                Node idNode = bizLocElement.getElementsByTagName("id").item(0);
                URI id = handleUri(idNode);
                bizLocation = new BusinessLocationType(id, null, null);
            }

            // parse <bizTransactionList>
            Node bizTransListNode = objectEvent.getElementsByTagName(
                    "bizTransactionList").item(0);
            BusinessTransactionType[] bizTransList = handleBizTransList(bizTransListNode);

            // TODO parse extension
            ObjectEventExtensionType extension = null;

            ObjectEventType objectEventType = new ObjectEventType(eventTime,
                    recordTime, eventTimeZoneOffset, baseExtension, epcList,
                    action, bizStep, disposition, readPoint, bizLocation,
                    bizTransList, extension, message);
            list.add(objectEventType);
        }
        ObjectEventType[] objectEvent = new ObjectEventType[list.size()];
        return list.toArray(objectEvent);
    }

    /**
     * Parses the transaction event inside the QueryResults event list.
     * 
     * @param transEventList
     *            The NodeList parsed from the XML containing the transaction
     *            events.
     * @return An array of TransactionEventType.
     */
    private static TransactionEventType[] handleTransactionEvents(
            final NodeList transEventList) {
        Vector<TransactionEventType> list = new Vector<TransactionEventType>();

        for (int i = 0; i < transEventList.getLength(); i++) {
            Element transEvent = (Element) transEventList.item(i);

            try {
                // parse <eventTime>
                Node eventTimeNode = transEvent.getElementsByTagName(
                        "eventTime").item(0);
                Calendar eventTime = handleTime(eventTimeNode);

                // parse <recordTime>
                Node recordTimeNode = transEvent.getElementsByTagName(
                        "recordTime").item(0);
                Calendar recordTime = handleTime(recordTimeNode);

                // parse <eventTimeZoneOffset>
                Node eventTimeZoneOffsetNode = transEvent.getElementsByTagName(
                        "eventTimeZoneOffset").item(0);
                String eventTimeZoneOffset = eventTimeZoneOffsetNode.getTextContent();

                // TODO parse extension
                EPCISEventExtensionType baseExtension = null;

                // TODO parse message element
                MessageElement[] message = null;

                // parse <bizTransactionList>
                Node bizTransListNode = transEvent.getElementsByTagName(
                        "bizTransactionList").item(0);
                BusinessTransactionType[] bizTransList = handleBizTransList(bizTransListNode);

                // parse <parentID>
                Node parentIdNode = transEvent.getElementsByTagName("parentID").item(
                        0);
                URI parentID = handleUri(parentIdNode);

                // parse <epcList>
                Node epcListNode = transEvent.getElementsByTagName("epcList").item(
                        0);
                EPC[] epcList = handleEpcList(epcListNode);

                // parse <action>
                Node actionNode = transEvent.getElementsByTagName("action").item(
                        0);
                ActionType action = handleAction(actionNode);

                // parse <bizStep>
                Node bizStepNode = transEvent.getElementsByTagName("bizStep").item(
                        0);
                URI bizStep = handleUri(bizStepNode);

                // parse <disposition>
                Node dispNode = transEvent.getElementsByTagName("disposition").item(
                        0);
                URI disposition = handleUri(dispNode);

                // parse <readPoint>
                Node readPointNode = transEvent.getElementsByTagName(
                        "readPoint").item(0);
                ReadPointType readPoint = null;
                if (readPointNode != null) {
                    Element readPointElement = (Element) readPointNode;
                    Node idNode = readPointElement.getElementsByTagName("id").item(
                            0);
                    URI id = handleUri(idNode);
                    readPoint = new ReadPointType(id, null, null);
                }

                // parse <bizLocation>
                Node bizLocNode = transEvent.getElementsByTagName("bizLocation").item(
                        0);
                BusinessLocationType bizLocation = null;
                if (bizLocNode != null) {
                    Element bizLocElement = (Element) bizLocNode;
                    Node idNode = bizLocElement.getElementsByTagName("id").item(
                            0);
                    URI id = handleUri(idNode);
                    bizLocation = new BusinessLocationType(id, null, null);
                }

                // TODO parse extension
                TransactionEventExtensionType extension = null;

                TransactionEventType event = new TransactionEventType(
                        eventTime, recordTime, eventTimeZoneOffset,
                        baseExtension, bizTransList, parentID, epcList, action,
                        bizStep, disposition, readPoint, bizLocation,
                        extension, message);

                list.add(event);

            } catch (DOMException e) {
                LOG.error("Parsing DOM tree went wrong at event nr: " + i);
            }
        }
        TransactionEventType[] objectEvent = new TransactionEventType[list.size()];
        return list.toArray(objectEvent);
    }

    /**
     * Parses the aggregation event inside the QueryResults event list.
     * 
     * @param aggrEventList
     *            The NodeList parsed from the XML containing the aggregation
     *            events.
     * @return An array of AggregationEventType.
     */
    private static AggregationEventType[] handleAggregationEvents(
            final NodeList aggrEventList) {
        Vector<AggregationEventType> list = new Vector<AggregationEventType>();

        for (int i = 0; i < aggrEventList.getLength(); i++) {
            Element aggrEvent = (Element) aggrEventList.item(i);

            // parse <eventTime>
            Node eventTimeNode = aggrEvent.getElementsByTagName("eventTime").item(
                    0);
            Calendar eventTime = handleTime(eventTimeNode);

            // parse <recordTime>
            Node recordTimeNode = aggrEvent.getElementsByTagName("recordTime").item(
                    0);
            Calendar recordTime = handleTime(recordTimeNode);

            // parse <eventTimeZoneOffset>
            Node eventTimeZoneOffsetNode = aggrEvent.getElementsByTagName(
                    "eventTimeZoneOffset").item(0);
            String eventTimeZoneOffset = eventTimeZoneOffsetNode.getTextContent();

            // TODO parse extension
            EPCISEventExtensionType baseExtension = null;

            // TODO parse message element
            MessageElement[] message = null;

            // parse <parentID>
            Node parentIdNode = aggrEvent.getElementsByTagName("parentID").item(
                    0);
            URI parentID = handleUri(parentIdNode);

            // parse <childEPCs>
            Node childEpcList = aggrEvent.getElementsByTagName("childEPCs").item(
                    0);
            EPC[] childEpcs = handleEpcList(childEpcList);

            // parse <action>
            Node actionNode = aggrEvent.getElementsByTagName("action").item(0);
            ActionType action = handleAction(actionNode);

            // parse <bizStep>
            Node bizStepNode = aggrEvent.getElementsByTagName("bizStep").item(0);
            URI bizStep = handleUri(bizStepNode);

            // parse <disposition>
            Node dispNode = aggrEvent.getElementsByTagName("disposition").item(
                    0);
            URI disposition = handleUri(dispNode);

            // parse <readPoint>
            Node readPointNode = aggrEvent.getElementsByTagName("readPoint").item(
                    0);
            ReadPointType readPoint = null;
            if (readPointNode != null) {
                Element readPointElement = (Element) readPointNode;
                Node idNode = readPointElement.getElementsByTagName("id").item(
                        0);
                URI id = handleUri(idNode);
                readPoint = new ReadPointType(id, null, null);
            }

            // parse <bizLocation>
            Node bizLocNode = aggrEvent.getElementsByTagName("bizLocation").item(
                    0);
            BusinessLocationType bizLocation = null;
            if (bizLocNode != null) {
                Element bizLocElement = (Element) bizLocNode;
                Node idNode = bizLocElement.getElementsByTagName("id").item(0);
                URI id = handleUri(idNode);
                bizLocation = new BusinessLocationType(id, null, null);
            }

            // parse <bizTransactionList>
            Node bizTransListNode = aggrEvent.getElementsByTagName(
                    "bizTransactionList").item(0);
            BusinessTransactionType[] bizTransList = handleBizTransList(bizTransListNode);

            // TODO parse extension
            AggregationEventExtensionType extension = null;

            AggregationEventType aggrEventType = new AggregationEventType(
                    eventTime, recordTime, eventTimeZoneOffset, baseExtension,
                    parentID, childEpcs, action, bizStep, disposition,
                    readPoint, bizLocation, bizTransList, extension, message);
            list.add(aggrEventType);
        }
        AggregationEventType[] aggrEvent = new AggregationEventType[list.size()];
        return list.toArray(aggrEvent);
    }

    /**
     * Parses the quantity event inside the QueryResults event list.
     * 
     * @param quantityEventList
     *            The NodeList parsed from the XML containing the quantity
     *            events.
     * @return An array of QuantityEventType.
     */
    private static QuantityEventType[] handleQuantityEvents(
            final NodeList quantityEventList) {
        Vector<QuantityEventType> list = new Vector<QuantityEventType>();

        for (int i = 0; i < quantityEventList.getLength(); i++) {
            Element quantityEvent = (Element) quantityEventList.item(i);

            // parse <eventTime>
            Node eventTimeNode = quantityEvent.getElementsByTagName("eventTime").item(
                    0);
            Calendar eventTime = handleTime(eventTimeNode);

            // parse <recordTime>
            Node recordTimeNode = quantityEvent.getElementsByTagName(
                    "recordTime").item(0);
            Calendar recordTime = handleTime(recordTimeNode);

            // parse <eventTimeZoneOffset>
            Node eventTimeZoneOffsetNode = quantityEvent.getElementsByTagName(
                    "eventTimeZoneOffset").item(0);
            String eventTimeZoneOffset = eventTimeZoneOffsetNode.getTextContent();

            // TODO parse extension
            EPCISEventExtensionType baseExtension = null;

            // TODO parse message element
            MessageElement[] message = null;

            // parse <epcClass>
            Node epcClassNode = quantityEvent.getElementsByTagName("epcClass").item(
                    0);
            URI epcClass = handleUri(epcClassNode);

            // parse <quantity>
            Node quantityNode = quantityEvent.getElementsByTagName("quantity").item(
                    0);
            int quantity = Integer.parseInt(quantityNode.getTextContent());

            // parse <bizStep>
            Node bizStepNode = quantityEvent.getElementsByTagName("bizStep").item(
                    0);
            URI bizStep = handleUri(bizStepNode);

            // parse <disposition>
            Node dispNode = quantityEvent.getElementsByTagName("disposition").item(
                    0);
            URI disposition = handleUri(dispNode);

            // parse <readPoint>
            Node readPointNode = quantityEvent.getElementsByTagName("readPoint").item(
                    0);
            ReadPointType readPoint = null;
            if (readPointNode != null) {
                Element readPointElement = (Element) readPointNode;
                Node idNode = readPointElement.getElementsByTagName("id").item(
                        0);
                URI id = handleUri(idNode);
                readPoint = new ReadPointType(id, null, null);
            }

            // parse <bizLocation>
            Node bizLocNode = quantityEvent.getElementsByTagName("bizLocation").item(
                    0);
            BusinessLocationType bizLocation = null;
            if (bizLocNode != null) {
                Element bizLocElement = (Element) bizLocNode;
                Node idNode = bizLocElement.getElementsByTagName("id").item(0);
                URI id = handleUri(idNode);
                bizLocation = new BusinessLocationType(id, null, null);
            }

            // parse <bizTransactionList>
            Node bizTransListNode = quantityEvent.getElementsByTagName(
                    "bizTransactionList").item(0);
            BusinessTransactionType[] bizTransList = handleBizTransList(bizTransListNode);

            // TODO parse extension
            QuantityEventExtensionType extension = null;

            QuantityEventType quantityEventType = new QuantityEventType(
                    eventTime, recordTime, eventTimeZoneOffset, baseExtension,
                    epcClass, quantity, bizStep, disposition, readPoint,
                    bizLocation, bizTransList, extension, message);
            list.add(quantityEventType);
        }
        QuantityEventType[] quantityEvent = new QuantityEventType[list.size()];
        return list.toArray(quantityEvent);
    }

    /**
     * Parses the vocabularies of an event.
     * 
     * @param vocList
     *            The NodeList parsed from the XML containing the vocabularies .
     * @return An array of VocabularyType.
     */
    private static VocabularyType[] handleVocabularies(final NodeList vocList) {
        List<VocabularyType> vocabularies = new ArrayList<VocabularyType>();

        for (int i = 0; i < vocList.getLength(); i++) {
            Element vocElem = (Element) vocList.item(i);

            // get 'type' attribute
            Node vocTypeNode = vocElem.getAttributeNode("type");
            URI vocType = handleUri(vocTypeNode);

            // parse <VocabularyElement>
            List<VocabularyElementType> vocElementList = new ArrayList<VocabularyElementType>();
            NodeList vocElementNodeList = vocElem.getElementsByTagName("VocabularyElement");
            for (int j = 0; j < vocElementNodeList.getLength(); j++) {
                Element vocElementElem = (Element) vocElementNodeList.item(j);

                Node vocIdNode = vocElementElem.getAttributeNode("id");
                URI vocId = handleUri(vocIdNode);

                // parse <attribute>
                List<AttributeType> attrList = new ArrayList<AttributeType>();
                NodeList attrNodeList = vocElementElem.getElementsByTagName("attribute");
                for (int k = 0; k < attrNodeList.getLength(); k++) {
                    Element attrElem = (Element) attrNodeList.item(k);

                    Node attrIdNode = attrElem.getAttributeNode("id");
                    URI attrId = handleUri(attrIdNode);

                    String attrStringVal = attrElem.getTextContent();
                    MessageElement[] attrVal = new MessageElement[] {
                        new MessageElement(new Text(attrStringVal))
                    };

                    AttributeType attr = new AttributeType();
                    attr.setId(attrId);
                    attr.set_any(attrVal);
                    attrList.add(attr);
                }
                AttributeType[] attrs = null;
                if (attrList.size() > 0) {
                    attrs = new AttributeType[attrList.size()];
                    attrs = attrList.toArray(attrs);
                }

                // parse <children>
                List<URI> childList = new ArrayList<URI>();
                NodeList childNodeList = vocElementElem.getElementsByTagName("children");
                for (int k = 0; k < childNodeList.getLength(); k++) {
                    Element childElem = (Element) childNodeList.item(k);

                    NodeList childIdList = childElem.getElementsByTagName("id");
                    for (int l = 0; l < childIdList.getLength(); l++) {
                        Node childIdNode = childIdList.item(l);
                        URI childId = handleUri(childIdNode);
                        childList.add(childId);
                    }
                }
                URI[] children = null;
                if (childList.size() > 0) {
                    children = new URI[childList.size()];
                    children = childList.toArray(children);
                }

                VocabularyElementType vocElement = new VocabularyElementType();
                vocElement.setAttribute(attrs);
                vocElement.setChildren(children);
                vocElement.setExtension(null);
                vocElement.setId(vocId);

                vocElementList.add(vocElement);
            }

            VocabularyElementType[] vocElements = new VocabularyElementType[vocElementList.size()];
            vocElements = vocElementList.toArray(vocElements);

            VocabularyType voc = new VocabularyType();
            voc.setType(vocType);
            voc.setVocabularyElementList(vocElements);
            vocabularies.add(voc);
        }
        VocabularyType[] vocs = new VocabularyType[vocabularies.size()];
        return vocabularies.toArray(vocs);
    }

    /**
     * Parses an action value.
     * 
     * @param actionNode
     *            The XML Node containign the action value.
     * @return An ActionType.
     */
    private static ActionType handleAction(final Node actionNode) {
        ActionType action = null;
        if (actionNode != null) {
            String actionStr = actionNode.getTextContent();
            action = ActionType.fromValue(actionStr);
        }
        return action;
    }

    /**
     * Parses a list of EPCs.
     * 
     * @param epcListNode
     *            The XML Node containing the list of EPCs.
     * @return An array of EPC.
     */
    private static EPC[] handleEpcList(final Node epcListNode) {
        EPC[] epcList = null;
        if (epcListNode != null) {
            Element epcListElement = (Element) epcListNode;
            NodeList epcs = epcListElement.getElementsByTagName("epc");
            epcList = new EPC[epcs.getLength()];
            for (int j = 0; j < epcs.getLength(); j++) {
                epcList[j] = new EPC(epcs.item(j).getTextContent());
            }
        }
        return epcList;
    }

    /**
     * Parses an event field containing a time value.
     * 
     * @param eventTimeNode
     *            The Node with the time value.
     * @return A Calendar representing the time value.
     */
    private static Calendar handleTime(final Node eventTimeNode) {
        Calendar cal = null;
        if (eventTimeNode != null) {
            String eventTimeStr = eventTimeNode.getTextContent();
            try {
                cal = TimeParser.parseAsCalendar(eventTimeStr);
            } catch (ParseException e) {
                String msg = "Time '" + eventTimeStr + "' cannot be parsed.";
                LOG.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }
        return cal;
    }

    /**
     * Parses a list of business transactions.
     * 
     * @param bizTransListNode
     *            The XML Node containing the list of business transactions.
     * @return An array of BusinessTransactionType.
     */
    private static BusinessTransactionType[] handleBizTransList(
            final Node bizTransListNode) {
        BusinessTransactionType[] bizTransList = null;
        if (bizTransListNode != null) {
            List<BusinessTransactionType> bizList = new Vector<BusinessTransactionType>();
            Element bizTransElement = (Element) bizTransListNode;
            NodeList bizTransNodeList = bizTransElement.getElementsByTagName("bizTransaction");
            for (int i = 0; i < bizTransNodeList.getLength(); i++) {
                URI value = handleUri(bizTransNodeList.item(i));
                BusinessTransactionType bizTrans = new BusinessTransactionType(
                        value);
                Node type = bizTransNodeList.item(i).getAttributes().getNamedItem(
                        "type");
                bizTrans.setType(handleUri(type));
                bizList.add(bizTrans);
            }
            bizTransList = new BusinessTransactionType[bizList.size()];
            bizTransList = bizList.toArray(bizTransList);
        } else {
            bizTransList = new BusinessTransactionType[0];
        }
        return bizTransList;
    }

    /**
     * Parses an URI value.
     * 
     * @param node
     *            The XML Node containing the URI value.
     * @return The parsed URI.
     */
    private static URI handleUri(final Node node) {
        URI uri = null;
        if (node != null) {
            try {
                uri = new URI(node.getTextContent().trim());
            } catch (MalformedURIException e) {
                throw new RuntimeException("URI '" + node.getTextContent()
                        + "' is not valid.", e);
            }
        }
        return uri;
    }

    /**
     * Compares the two given QueryResults with each other. Throws an
     * AssertionError if the two QueryResults are not equal. The error contains
     * information about where the two objects differ.
     * 
     * @param expResults
     *            The expected QueryResults object.
     * @param actResults
     *            The actual QueryResults object.
     */
    public static void compareResults(final QueryResults expResults,
            final QueryResults actResults) {
        assertEquals(expResults == null, actResults == null);
        assertEquals(expResults.get_any(), actResults.get_any());
        assertEquals(expResults.getExtension(), actResults.getExtension());
        assertEquals(expResults.getQueryName(), actResults.getQueryName());
        assertEquals(expResults.getSubscriptionID(),
                actResults.getSubscriptionID());

        VocabularyType[] expVocabularies = expResults.getResultsBody().getVocabularyList();
        VocabularyType[] actVocabularies = actResults.getResultsBody().getVocabularyList();
        assertEquals(expVocabularies == null, actVocabularies == null);
        if (actVocabularies != null) {
            compareVocabularies(expVocabularies, actVocabularies);
        }

        EventListType actEvents = actResults.getResultsBody().getEventList();
        EventListType expEvents = expResults.getResultsBody().getEventList();
        assertEquals(actEvents == null, expEvents == null);
        if (actEvents != null) {
            compareEvents(expEvents, actEvents);
        }
    }

    /**
     * Compares the two given VocabularyType arrays with each other.
     * 
     * @param expVocs
     *            The expected VocabularyType array.
     * @param actVocs
     *            The actual VocabularyType array.
     */
    private static void compareVocabularies(final VocabularyType[] expVocs,
            final VocabularyType[] actVocs) {
        assertEquals(expVocs.length, actVocs.length);
        for (int i = 0; i < expVocs.length; i++) {
            assertEquals(expVocs[i].getType(), actVocs[i].getType());

            VocabularyElementType[] expVocList = expVocs[i].getVocabularyElementList();
            VocabularyElementType[] actVocList = actVocs[i].getVocabularyElementList();
            assertEquals(expVocList == null, actVocList == null);
            assertEquals(expVocList.length, actVocList.length);

            // make map to ensure same order of elements
            Map<URI, VocabularyElementType> expVocMap = new HashMap<URI, VocabularyElementType>();
            for (int k = 0; k < expVocList.length; k++) {
                expVocMap.put(expVocList[k].getId(), expVocList[k]);
            }
            for (int j = 0; j < actVocList.length; j++) {
                VocabularyElementType actVoc = actVocList[j];
                VocabularyElementType expVoc = expVocMap.get(actVoc.getId());
                assertEquals(expVoc == null, actVoc == null);
                assertEquals(expVoc.getId(), actVoc.getId());

                AttributeType[] expAttrs = expVoc.getAttribute();
                AttributeType[] actAttrs = actVoc.getAttribute();
                assertEquals(expAttrs == null, actAttrs == null);
                if (expAttrs != null) {
                    assertEquals(expAttrs.length, actAttrs.length);
                    Map<URI, AttributeType> expAttrMap = new HashMap<URI, AttributeType>();
                    for (int k = 0; k < expAttrs.length; k++) {
                        expAttrMap.put(expAttrs[k].getId(), expAttrs[k]);
                    }
                    for (int k = 0; k < actAttrs.length; k++) {
                        AttributeType actAttr = actAttrs[k];
                        AttributeType expAttr = expAttrMap.get(actAttr.getId());
                        assertEquals(expAttr == null, expAttr == null);
                        assertEquals(expAttr.getId(), actAttr.getId());

                        MessageElement[] expME = expAttr.get_any();
                        MessageElement[] actME = actAttr.get_any();
                        assertEquals(expME == null, actME == null);
                        if (expME != null) {
                            assertEquals(expME.length, actME.length);
                            for (int l = 0; l < expME.length; l++) {
                                assertEquals(expME[l].getNodeValue().trim(),
                                        actME[l].getNodeValue().trim());
                            }
                        }
                    }
                }

                URI[] expChildren = expVoc.getChildren();
                URI[] actChildren = actVoc.getChildren();
                assertEquals(expChildren == null, actChildren == null);
                if (expChildren != null) {
                    assertEquals(expChildren.length, actChildren.length);
                    List<URI> expChildIdList = Arrays.asList(expChildren);
                    for (int k = 0; k < actChildren.length; k++) {
                        assertTrue(expChildIdList.contains(actChildren[k]));
                    }
                }
            }
        }
    }

    /**
     * Compare the two given EventListType with each other.
     * 
     * @param actEvents
     *            The expected EventListType.
     * @param expEvents
     *            The actual EventListType.
     */
    private static void compareEvents(final EventListType actEvents,
            final EventListType expEvents) {
        // compare ObjectEvent
        ObjectEventType[] actObjectEvent = actEvents.getObjectEvent();
        ObjectEventType[] expObjectEvent = expEvents.getObjectEvent();

        assertEquals(expObjectEvent == null, actObjectEvent == null);
        if (actObjectEvent != null) {
            assertEquals(expObjectEvent.length, actObjectEvent.length);
            for (int i = 0; i < actObjectEvent.length; i++) {
                assertEquals(expObjectEvent[i].getAction(),
                        actObjectEvent[i].getAction());
                assertEquals(expObjectEvent[i].getBaseExtension(),
                        actObjectEvent[i].getBaseExtension());
                assertEquals(expObjectEvent[i].getBizLocation(),
                        actObjectEvent[i].getBizLocation());
                assertEquals(expObjectEvent[i].getBizStep(),
                        actObjectEvent[i].getBizStep());
                assertEquals(expObjectEvent[i].getDisposition(),
                        actObjectEvent[i].getDisposition());
                assertEquals(expObjectEvent[i].getEventTime().compareTo(
                        actObjectEvent[i].getEventTime()), 0);
                assertEquals(expObjectEvent[i].getExtension(),
                        actObjectEvent[i].getExtension());
                assertEquals(expObjectEvent[i].getReadPoint(),
                        actObjectEvent[i].getReadPoint());
                // assertEquals(expObjectEvent[i].getRecordTime(),
                // actObjectEvent[i].getRecordTime());

                MessageElement[] actME = actObjectEvent[i].get_any();
                MessageElement[] expME = expObjectEvent[i].get_any();
                assertEquals(expME.length, actME.length);
                for (int j = 0; j < actME.length; j++) {
                    assertEquals(expME[j].getValue(), actME[j].getValue());
                    assertEquals(expME[j].getNamespaceURI(),
                            actME[j].getNamespaceURI());
                    assertEquals(expME[j].getPrefix(), actME[j].getPrefix());
                    assertEquals(expME[j].getLocalName(),
                            actME[j].getLocalName());
                }

                EPC[] actEpcs = actObjectEvent[i].getEpcList();
                EPC[] expEpcs = expObjectEvent[i].getEpcList();
                assertEquals(expEpcs.length, actEpcs.length);
                for (int j = 0; j < actEpcs.length; j++) {
                    assertEquals(expEpcs[j].get_value(), actEpcs[j].get_value());
                }

                BusinessTransactionType[] actBizTrans = actObjectEvent[i].getBizTransactionList();
                BusinessTransactionType[] expBizTrans = expObjectEvent[i].getBizTransactionList();
                assertEquals(expBizTrans.length, actBizTrans.length);
                for (int j = 0; j < actBizTrans.length; j++) {
                    assertEquals(expBizTrans[j].getType(),
                            actBizTrans[j].getType());
                    // assertEquals(expBizTrans[j].getValue(),
                    // actBizTrans[j].getValue());
                }
            }
        }

        // compare AggregationEvent
        AggregationEventType[] actAggrEvent = actEvents.getAggregationEvent();
        AggregationEventType[] expAggrEvent = expEvents.getAggregationEvent();

        assertEquals(expAggrEvent == null, actAggrEvent == null);
        if (actAggrEvent != null) {
            assertEquals(expAggrEvent.length, actAggrEvent.length);
            for (int i = 0; i < actAggrEvent.length; i++) {
                assertEquals(expAggrEvent[i].get_any(),
                        actAggrEvent[i].get_any());
                assertEquals(expAggrEvent[i].getAction(),
                        actAggrEvent[i].getAction());
                assertEquals(expAggrEvent[i].getBaseExtension(),
                        actAggrEvent[i].getBaseExtension());
                assertEquals(expAggrEvent[i].getBizLocation(),
                        actAggrEvent[i].getBizLocation());
                assertEquals(expAggrEvent[i].getBizStep(),
                        actAggrEvent[i].getBizStep());
                assertEquals(expAggrEvent[i].getDisposition(),
                        actAggrEvent[i].getDisposition());
                assertEquals(expAggrEvent[i].getEventTime().compareTo(
                        actAggrEvent[i].getEventTime()), 0);
                assertEquals(expAggrEvent[i].getExtension(),
                        actAggrEvent[i].getExtension());
                assertEquals(expAggrEvent[i].getParentID(),
                        actAggrEvent[i].getParentID());
                assertEquals(expAggrEvent[i].getReadPoint(),
                        actAggrEvent[i].getReadPoint());
                // assertEquals(expObjectEvent[i].getRecordTime(),
                // actObjectEvent[i].getRecordTime());

                EPC[] actEpcs = actAggrEvent[i].getChildEPCs();
                EPC[] expEpcs = expAggrEvent[i].getChildEPCs();
                assertEquals(expEpcs.length, actEpcs.length);
                for (int j = 0; j < actEpcs.length; j++) {
                    assertEquals(expEpcs[j].get_value(), actEpcs[j].get_value());
                }

                BusinessTransactionType[] actBizTrans = actAggrEvent[i].getBizTransactionList();
                BusinessTransactionType[] expBizTrans = expAggrEvent[i].getBizTransactionList();
                assertEquals(actBizTrans.length, expBizTrans.length);
                for (int j = 0; j < actBizTrans.length; j++) {
                    assertEquals(expBizTrans[j].getType(),
                            actBizTrans[j].getType());
                    // assertEquals(expBizTrans[j].getValue(),
                    // actBizTrans[j].getValue());
                }
            }
        }

        // compare TransactionEvent
        TransactionEventType[] actTransEvent = actEvents.getTransactionEvent();
        TransactionEventType[] expTransEvent = expEvents.getTransactionEvent();

        assertEquals(expTransEvent == null, actTransEvent == null);
        if (actTransEvent != null) {
            assertEquals(expTransEvent.length, actTransEvent.length);
            for (int i = 0; i < actTransEvent.length; i++) {
                assertEquals(expTransEvent[i].get_any(),
                        actTransEvent[i].get_any());
                assertEquals(expTransEvent[i].getAction(),
                        actTransEvent[i].getAction());
                assertEquals(expTransEvent[i].getBaseExtension(),
                        actTransEvent[i].getBaseExtension());
                assertEquals(expTransEvent[i].getBizLocation(),
                        actTransEvent[i].getBizLocation());
                assertEquals(expTransEvent[i].getBizStep(),
                        actTransEvent[i].getBizStep());
                assertEquals(expTransEvent[i].getDisposition(),
                        actTransEvent[i].getDisposition());
                assertEquals(expTransEvent[i].getEventTime().compareTo(
                        actTransEvent[i].getEventTime()), 0);
                assertEquals(expTransEvent[i].getExtension(),
                        actTransEvent[i].getExtension());
                assertEquals(expTransEvent[i].getParentID(),
                        actTransEvent[i].getParentID());
                assertEquals(expTransEvent[i].getReadPoint(),
                        actTransEvent[i].getReadPoint());
                // assertEquals(expTransEvent[i].getRecordTime(),
                // actTransEvent[i].getRecordTime());

                EPC[] actEpcs = actTransEvent[i].getEpcList();
                EPC[] expEpcs = expTransEvent[i].getEpcList();
                assertEquals(expEpcs.length, actEpcs.length);
                for (int j = 0; j < actEpcs.length; j++) {
                    assertEquals(expEpcs[j].get_value(), actEpcs[j].get_value());
                }

                BusinessTransactionType[] actBizTrans = actTransEvent[i].getBizTransactionList();
                BusinessTransactionType[] expBizTrans = expTransEvent[i].getBizTransactionList();
                assertEquals(actBizTrans.length, expBizTrans.length);
                for (int j = 0; j < actBizTrans.length; j++) {
                    assertEquals(expBizTrans[j].getType(),
                            actBizTrans[j].getType());
                    // assertEquals(expBizTrans[j].getValue(),
                    // actBizTrans[j].getValue());
                }
            }
        }

        // compare QuantityEvent
        QuantityEventType[] actQuantEvent = actEvents.getQuantityEvent();
        QuantityEventType[] expQuantEvent = expEvents.getQuantityEvent();

        assertEquals(expQuantEvent == null, actQuantEvent == null);
        if (actQuantEvent != null) {
            assertEquals(expQuantEvent.length, actQuantEvent.length);
            for (int i = 0; i < actQuantEvent.length; i++) {
                assertEquals(expQuantEvent[i].get_any(),
                        actQuantEvent[i].get_any());
                assertEquals(expQuantEvent[i].getBaseExtension(),
                        actQuantEvent[i].getBaseExtension());
                assertEquals(expQuantEvent[i].getBizLocation(),
                        actQuantEvent[i].getBizLocation());
                assertEquals(expQuantEvent[i].getBizStep(),
                        actQuantEvent[i].getBizStep());
                assertEquals(expQuantEvent[i].getDisposition(),
                        actQuantEvent[i].getDisposition());
                assertEquals(expQuantEvent[i].getEventTime().compareTo(
                        actQuantEvent[i].getEventTime()), 0);
                assertEquals(expQuantEvent[i].getEpcClass(),
                        actQuantEvent[i].getEpcClass());
                assertEquals(expQuantEvent[i].getExtension(),
                        actQuantEvent[i].getExtension());
                assertEquals(expQuantEvent[i].getQuantity(),
                        actQuantEvent[i].getQuantity());
                assertEquals(expQuantEvent[i].getReadPoint(),
                        actQuantEvent[i].getReadPoint());
                // assertEquals(expQuantEvent[i].getRecordTime(),
                // actQuantEvent[i].getRecordTime());

                BusinessTransactionType[] actBizTrans = actQuantEvent[i].getBizTransactionList();
                BusinessTransactionType[] expBizTrans = expQuantEvent[i].getBizTransactionList();
                assertEquals(actBizTrans.length, expBizTrans.length);
                for (int j = 0; j < actBizTrans.length; j++) {
                    assertEquals(expBizTrans[j].getType(),
                            actBizTrans[j].getType());
                    // assertEquals(expBizTrans[j].getValue(),
                    // actBizTrans[j].getValue());
                }
            }
        }
    }

    /**
     * Ensures that the two given objects are equal, throws an AssertionError if
     * not.
     * 
     * @param expected
     *            The expected object.
     * @param actual
     *            The actual object.
     */
    private static void assertEquals(final Object expected, final Object actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        throw new AssertionError("expected:<" + expected + "> but was:<"
                + actual + ">");
    }

    /**
     * Ensures that the given boolean value is <code>true</code>, throws an
     * AssertionError if not.
     * 
     * @param check
     *            the boolean value to check.
     */
    private static void assertTrue(final boolean check) {
        if (!check) {
            throw new AssertionError();
        }
    }

    /**
     * Converts the given QueryResults object into its XML representation.
     * 
     * @param results
     *            The QueryResults object to be converted.
     * @return The XML representation of the QueryResults.
     * @throws IOException
     *             If an error serializing the QueryResults object occured.
     */
    public static String queryResultsToXML(final QueryResults results)
            throws IOException {
        // serialize the response
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);
        SerializationContext serContext = new SerializationContext(writer);
        QName xmlType = QueryResults.getTypeDesc().getXmlType();
        serContext.setWriteXMLType(xmlType);
        serContext.serialize(xmlType, new NullAttributes(), results, xmlType,
                QueryResults.class, false, true);
        writer.flush();
        return baos.toString();
    }
}
