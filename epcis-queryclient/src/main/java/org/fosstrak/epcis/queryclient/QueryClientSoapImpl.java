package org.accada.epcis.queryclient;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ServiceException;

import org.accada.epcis.soapapi.ArrayOfString;
import org.accada.epcis.soapapi.DuplicateSubscriptionException;
import org.accada.epcis.soapapi.EPCISServiceBindingStub;
import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.InvalidURIException;
import org.accada.epcis.soapapi.NoSuchNameException;
import org.accada.epcis.soapapi.Poll;
import org.accada.epcis.soapapi.QueryParam;
import org.accada.epcis.soapapi.QueryParameterException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QuerySchedule;
import org.accada.epcis.soapapi.QueryScheduleExtensionType;
import org.accada.epcis.soapapi.QueryTooComplexException;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.soapapi.SecurityException;
import org.accada.epcis.soapapi.Subscribe;
import org.accada.epcis.soapapi.SubscribeNotPermittedException;
import org.accada.epcis.soapapi.SubscriptionControls;
import org.accada.epcis.soapapi.SubscriptionControlsException;
import org.accada.epcis.soapapi.SubscriptionControlsExtensionType;
import org.accada.epcis.soapapi.ValidationException;
import org.accada.epcis.utils.TimeParser;
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.Logger;
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
        Calendar initialRecordTime = null;
        try {
            initialRecordTime = handleTime(timeNode);
        } catch (ParseException e) {
            String msg = "Unable to parse time value for 'initialRecordTime': "
                    + e.getMessage();
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }

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

    /**
     * Parses an event field containing a time value.
     * 
     * @param eventTimeNode
     *            The Node with the time value.
     * @return A Calendar representing the time value.
     */
    private Calendar handleTime(final Node eventTimeNode) throws ParseException {
        Calendar cal = null;
        if (eventTimeNode != null) {
            String eventTimeStr = eventTimeNode.getTextContent();
            cal = TimeParser.parseAsCalendar(eventTimeStr);
        }
        return cal;
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
