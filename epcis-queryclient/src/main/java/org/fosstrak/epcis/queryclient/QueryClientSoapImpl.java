package org.accada.epcis.queryclient;

import java.io.FileInputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ServiceException;

import org.accada.epcis.soapapi.ActionType;
import org.accada.epcis.soapapi.AggregationEventExtensionType;
import org.accada.epcis.soapapi.AggregationEventType;
import org.accada.epcis.soapapi.ArrayOfString;
import org.accada.epcis.soapapi.BusinessLocationType;
import org.accada.epcis.soapapi.BusinessTransactionType;
import org.accada.epcis.soapapi.DuplicateSubscriptionException;
import org.accada.epcis.soapapi.EPC;
import org.accada.epcis.soapapi.EPCISEventExtensionType;
import org.accada.epcis.soapapi.EPCISEventListExtensionType;
import org.accada.epcis.soapapi.EPCISServiceBindingStub;
import org.accada.epcis.soapapi.EventListType;
import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.InvalidURIException;
import org.accada.epcis.soapapi.NoSuchNameException;
import org.accada.epcis.soapapi.ObjectEventExtensionType;
import org.accada.epcis.soapapi.ObjectEventType;
import org.accada.epcis.soapapi.Poll;
import org.accada.epcis.soapapi.QuantityEventExtensionType;
import org.accada.epcis.soapapi.QuantityEventType;
import org.accada.epcis.soapapi.QueryParam;
import org.accada.epcis.soapapi.QueryParameterException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryResultsBody;
import org.accada.epcis.soapapi.QueryResultsExtensionType;
import org.accada.epcis.soapapi.QuerySchedule;
import org.accada.epcis.soapapi.QueryScheduleExtensionType;
import org.accada.epcis.soapapi.QueryTooComplexException;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.soapapi.ReadPointType;
import org.accada.epcis.soapapi.SecurityException;
import org.accada.epcis.soapapi.Subscribe;
import org.accada.epcis.soapapi.SubscribeNotPermittedException;
import org.accada.epcis.soapapi.SubscriptionControls;
import org.accada.epcis.soapapi.SubscriptionControlsException;
import org.accada.epcis.soapapi.SubscriptionControlsExtensionType;
import org.accada.epcis.soapapi.TransactionEventExtensionType;
import org.accada.epcis.soapapi.TransactionEventType;
import org.accada.epcis.soapapi.ValidationException;
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
 * An adapter (according to the Class Adapter Pattern) for the QuerySoapClient
 * making it possible to send a query in xml representation.
 * 
 * @author Andrea Grössbauer
 * @author Marco Steybe
 */
public class QueryClientSoapImpl extends QueryClientBase {

    private static final Logger LOG = Logger.getLogger(QueryClientSoapImpl.class);

    /**
     * Holds the query parameters.
     */
    private Vector<QueryParam> queryParamsVector = new Vector<QueryParam>();

    /**
     * Constructs a new QueryClientSoapImpl.
     */
    public QueryClientSoapImpl() {
        super();
    }

    /**
     * Constructs a new QueryClientSoapImpl.
     * 
     * @param address
     *            The address at which the query service listens.
     */
    public QueryClientSoapImpl(final String address) {
        super(address);
    }

    private QueryParam[] handleParams(Element params) {
        NodeList paramList = params.getElementsByTagName("param");
        int nofParams = paramList.getLength();
        QueryParam[] queryParams = new QueryParam[nofParams];
        for (int i = 0; i < nofParams; i++) {
            Element param = (Element) paramList.item(i);
            Element name = (Element) param.getElementsByTagName("name").item(0);
            Element value = (Element) param.getElementsByTagName("value").item(
                    0);
            String paramName = name.getTextContent();
            Object paramValue = parseParamValue(value);
            QueryParam queryParam = new QueryParam(paramName, paramValue);
            queryParams[i] = queryParam;
        }
        return queryParams;
    }

    private Object parseParamValue(Element valueElement) {
        Object paramValue = null;
        // check if we have an array of strings
        NodeList stringNodes = valueElement.getElementsByTagName("string");
        int size = stringNodes.getLength();
        if (size > 0) {
            String[] strings = new String[size];
            boolean[] noHackAroundBugs = new boolean[size];
            for (int i = 0; i < size; i++) {
                String string = stringNodes.item(0).getTextContent();
                strings[i] = string;
                noHackAroundBugs[i] = true;

                if (LOG.isDebugEnabled()) {
                    LOG.debug("found parameter value <string>" + string
                            + "</string>");
                }
            }
            paramValue = new ArrayOfString(strings, noHackAroundBugs);
        } else {
            // check if we have an Integer
            try {
                paramValue = Integer.parseInt(valueElement.getTextContent());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("found parameter value (Integer) " + paramValue);
                }
            } catch (Exception e) {
                // check if we have a time value
                try {
                    paramValue = handleTime(valueElement);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("found parameter value (Calendar) "
                                + paramValue);
                    }
                } catch (Exception e1) {
                    // check if we have an URI
                    try {
                        paramValue = handleUri(valueElement);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("found parameter value (URI) "
                                    + paramValue);
                        }
                    } catch (Exception e2) {
                        // ok lets take it as String
                        paramValue = valueElement.getTextContent();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("found parameter value (String) "
                                    + paramValue);
                        }
                    }
                }
            }
        }
        return paramValue;
    }

    /**
     * @see org.accada.epcis.queryclient.QueryClientInterface#runQuery(java.io.InputStream)
     */
    public QueryResults runQuery(InputStream request) throws ServiceException,
            QueryTooComplexException, ImplementationException,
            QueryTooLargeException, QueryParameterException,
            ValidationException, SecurityException, NoSuchNameException,
            RemoteException {
        clearParameters();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document epcisq;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            epcisq = builder.parse(request);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse the XML query.", e);
        }
        String queryName = epcisq.getElementsByTagName("queryName").item(0).getTextContent();
        Element params = (Element) epcisq.getElementsByTagName("params").item(0);
        QueryParam[] queryParams = handleParams(params);

        Poll poll = new Poll(queryName, queryParams);

        if (LOG.isDebugEnabled()) {
            LOG.debug("submitting " + queryParams.length
                    + " query parameters to the query service:");

            for (int i = 0; i < queryParams.length; i++) {
                LOG.debug("param" + i + ": [" + queryParams[i].getName() + ", "
                        + queryParams[i].getValue() + "]");
            }
        }

        EPCISServiceBindingStub stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();

        QueryResults response = stub.poll(poll);
        return response;
    }

    /**
     * A helper method to parse and convert the XML representation of an EPCIS
     * query results into a QueryResults object.
     * 
     * @param xmlQueryResults
     * @return
     */
    public QueryResults convertXmlToQueryResults(InputStream xmlQueryResults) {
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

    private ObjectEventType[] handleObjectEvents(NodeList objectEventList) {
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
            MessageElement[] message = null;

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

    private TransactionEventType[] handleTransactionEvents(
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

    private AggregationEventType[] handleAggregationEvents(
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

    private QuantityEventType[] handleQuantityEvents(NodeList quantityEventList) {
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

    private ActionType handleAction(Node actionNode) {
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
    private EPC[] handleEpcList(Node epcListNode) {
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

    private Calendar handleTime(Node eventTimeNode) {
        Calendar cal = null;
        if (eventTimeNode != null) {
            String eventTimeStr = eventTimeNode.getTextContent();
            cal = Calendar.getInstance();
            SimpleDateFormat isoFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date date;
            try {
                date = isoFormat.parse(eventTimeStr);
                cal.setTime(date);
            } catch (ParseException e) {
                SimpleDateFormat rescueFormat = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss");
                try {
                    date = rescueFormat.parse(eventTimeStr);
                    cal.setTime(date);
                } catch (ParseException e1) {
                    throw new RuntimeException("Time '" + eventTimeStr
                            + "' cannot be parsed.", e);
                }
            }
        }
        return cal;
    }

    private BusinessTransactionType[] handleBizTransList(Node bizTransListNode) {
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

    private URI handleUri(Node node) {
        URI uri = null;
        if (node != null) {
            try {
                uri = new URI(node.getTextContent());
            } catch (MalformedURIException e) {
                throw new RuntimeException("URI '" + node.getTextContent()
                        + "' is not valid.", e);
            }
        }
        return uri;
    }

    public static void main(String[] args) throws Exception {
        QueryClientSoapImpl queryClient = new QueryClientSoapImpl("blabla");

        InputStream is = new FileInputStream(
                "D:\\Projects\\epcis\\test\\junit\\data\\queries\\webservice\\responses\\Test-EPCIS10-SE10-Response-1-poll.xml");
        QueryResults qr = queryClient.convertXmlToQueryResults(is);

        System.out.println(qr.getQueryName());
    }

    /**
     * @see org.accada.epcis.queryclient.QueryClientInterface#subscribeQuery(java.io.InputStream)
     */
    public void subscribeQuery(InputStream request) throws ServiceException,
            QueryTooComplexException, ImplementationException,
            InvalidURIException, SubscribeNotPermittedException,
            SubscriptionControlsException, QueryParameterException,
            ValidationException, SecurityException,
            DuplicateSubscriptionException, NoSuchNameException,
            RemoteException {
        clearParameters();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document epcisq;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            epcisq = builder.parse(request);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse the XML query.", e);
        }
        String queryName = epcisq.getElementsByTagName("queryName").item(0).getTextContent();
        Element params = (Element) epcisq.getElementsByTagName("params").item(0);
        QueryParam[] queryParams = handleParams(params);

        URI dest = handleUri(epcisq.getElementsByTagName("dest").item(0));
        Element controlsNode = (Element) epcisq.getElementsByTagName("controls").item(
                0);
        SubscriptionControls controls = handleControls(controlsNode);
        String subscrId = null;
        Node subscribeIdNode = epcisq.getElementsByTagName("subscriptionID").item(
                0);
        if (subscribeIdNode != null) {
            subscrId = subscribeIdNode.getTextContent();
        }

        Subscribe subscribe = new Subscribe(queryName, queryParams, dest,
                controls, subscrId);

        if (LOG.isDebugEnabled()) {
            LOG.debug("submitting " + queryParams.length
                    + " query parameters to the query service:");

            for (int i = 0; i < queryParams.length; i++) {
                LOG.debug("param" + i + ": [" + queryParams[i].getName() + ", "
                        + queryParams[i].getValue() + "]");
            }
        }

        EPCISServiceBindingStub stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();

        stub.subscribe(subscribe);
    }

    private SubscriptionControls handleControls(Element controlsNode) {
        Element scheduleNode = (Element) controlsNode.getElementsByTagName(
                "schedule").item(0);
        QuerySchedule schedule = handleSchedule(scheduleNode);

        URI trigger = null;
        Node triggerNode = controlsNode.getElementsByTagName("trigger").item(0);
        if (triggerNode != null) {
            trigger = handleUri(triggerNode);
        }

        Node timeNode = controlsNode.getElementsByTagName("initialRecordTime").item(
                0);

        Calendar initialRecordTime = handleTime(timeNode);

        String boolStr = controlsNode.getElementsByTagName("reportIfEmpty").item(
                0).getTextContent();
        boolean reportIfEmpty = Boolean.parseBoolean(boolStr);

        // TODO handle extension
        SubscriptionControlsExtensionType ext = null;

        // TODO handle message
        MessageElement[] msg = null;

        SubscriptionControls controls = new SubscriptionControls(schedule,
                trigger, initialRecordTime, reportIfEmpty, ext, msg);
        return controls;
    }

    private QuerySchedule handleSchedule(Element scheduleNode) {
        QuerySchedule schedule = null;
        if (scheduleNode != null) {
            String sec = null;
            Node secNode = scheduleNode.getElementsByTagName("second").item(0);
            if (secNode != null) {
                sec = secNode.getTextContent();
            }
            String min = null;
            Node minNode = scheduleNode.getElementsByTagName("minute").item(0);
            if (minNode != null) {
                min = minNode.getTextContent();
            }
            String hr = null;
            Node hrNode = scheduleNode.getElementsByTagName("hour").item(0);
            if (hrNode != null) {
                hr = hrNode.getTextContent();
            }
            String dom = null;
            Node domNode = scheduleNode.getElementsByTagName("dayOfMonth").item(
                    0);
            if (domNode != null) {
                dom = domNode.getTextContent();
            }
            String m = null;
            Node mNode = scheduleNode.getElementsByTagName("month").item(0);
            if (mNode != null) {
                m = mNode.getTextContent();
            }
            String dow = null;
            Node dowNode = scheduleNode.getElementsByTagName("dayOfWeek").item(
                    0);
            if (dowNode != null) {
                dow = dowNode.getTextContent();
            }

            // TODO handle extension
            QueryScheduleExtensionType extension = null;

            // TODO handle message
            MessageElement[] msg = null;

            schedule = new QuerySchedule(sec, min, hr, dom, m, dow, extension,
                    msg);
        }
        return schedule;
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
    public void addParameter(QueryParam param) {
        queryParamsVector.add(param);
    }
}
