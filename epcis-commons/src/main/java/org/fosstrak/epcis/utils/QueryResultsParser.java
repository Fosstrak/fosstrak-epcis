package org.accada.epcis.utils;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.accada.epcis.soapapi.ActionType;
import org.accada.epcis.soapapi.AggregationEventExtensionType;
import org.accada.epcis.soapapi.AggregationEventType;
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
import org.accada.epcis.soapapi.VocabularyType;
import org.apache.axis.message.MessageElement;
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
public class QueryResultsParser {

    private static final Logger LOG = Logger.getLogger(QueryResultsParser.class);


    /**
     * A helper method to parse and convert the XML representation of an EPCIS
     * query results into a QueryResults object.
     * 
     * @param xmlQueryResults
     * @return
     */
    public static QueryResults parseQueryResults(InputStream xmlQueryResults) {
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
            throw new UnsupportedOperationException("Only queries of type "
                    + "SimpleEventQuery are supported.");
            // TODO (marco) handle SimpleMasterDataQuery here
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

            // TODO marco: parse vocabulary from xml
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

    private static ObjectEventType[] handleObjectEvents(NodeList objectEventList) {
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
            Node temperature = objectEvent.getElementsByTagName("hls:temperature").item(0);
            if (temperature != null) {
                MessageElement me = new MessageElement("temperature", "hls",
                        "http://schema.hls.com/extension");
                me.setValue(temperature.getTextContent());
                messages.add(me);
            }
            Node batchNumber = objectEvent.getElementsByTagName("hls:batchNumber").item(0);
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

    private static TransactionEventType[] handleTransactionEvents(
            NodeList transEventList) {
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

    private static AggregationEventType[] handleAggregationEvents(
            NodeList aggrEventList) {
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

    private static QuantityEventType[] handleQuantityEvents(NodeList quantityEventList) {
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

    private static ActionType handleAction(Node actionNode) {
        ActionType action = null;
        if (actionNode != null) {
            String actionStr = actionNode.getTextContent();
            action = ActionType.fromValue(actionStr);
        }
        return action;
    }

    /**
     * @param epcArray
     * @param epcListNode
     */
    private static EPC[] handleEpcList(Node epcListNode) {
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

    private static BusinessTransactionType[] handleBizTransList(Node bizTransListNode) {
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

    private static URI handleUri(Node node) {
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
}
