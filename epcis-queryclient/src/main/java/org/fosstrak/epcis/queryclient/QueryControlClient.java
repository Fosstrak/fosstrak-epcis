/**
 * 
 */
package org.accada.epcis.queryclient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ServiceException;

import org.accada.epcis.soapapi.ArrayOfString;
import org.accada.epcis.soapapi.EPCISServiceBindingStub;
import org.accada.epcis.soapapi.EPCglobalEPCISServiceLocator;
import org.accada.epcis.soapapi.EmptyParms;
import org.accada.epcis.soapapi.GetSubscriptionIDs;
import org.accada.epcis.soapapi.Poll;
import org.accada.epcis.soapapi.QueryParam;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QuerySchedule;
import org.accada.epcis.soapapi.QueryScheduleExtensionType;
import org.accada.epcis.soapapi.Subscribe;
import org.accada.epcis.soapapi.SubscriptionControls;
import org.accada.epcis.soapapi.SubscriptionControlsExtensionType;
import org.accada.epcis.soapapi.Unsubscribe;
import org.accada.epcis.utils.TimeParser;
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This query client is a wrapper for EPCISServiceBindingStub which performs the
 * calls to the Query Operations Module. Additionally this client provides some
 * convenience methods for polling and subscribing queries given in XML form.
 * 
 * @author Marco Steybe
 */
public class QueryControlClient implements QueryControlInterface {

    private static final String PROPERTY_FILE = "/queryclient.properties";

    private static final String PROPERTY_QUERY_URL = "default.url";

    /**
     * The URL String at which the Query Operations Module listens.
     */
    private String queryUrl = null;

    /**
     * The locator for the service.
     */
    private EPCglobalEPCISServiceLocator service;

    /**
     * Constructs a new QueryClient which connects to the repository's Query
     * Operations Module listening at a default url address.
     */
    public QueryControlClient() {
        this(null);
    }

    /**
     * Constructs a new QueryClient which connects to the repository's Query
     * Operations Module listening at the given url address.
     * 
     * @param address
     *            The URL String the query module is listening at.
     */
    public QueryControlClient(final String address) {
        // read properties
        Properties props = new Properties();
        InputStream is = this.getClass().getResourceAsStream(PROPERTY_FILE);
        if (is == null) {
            throw new RuntimeException("Unable to load properties from file "
                    + PROPERTY_FILE);
        }
        try {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties from file "
                    + PROPERTY_FILE);
        }

        // set query address
        if (address != null) {
            this.queryUrl = address;
        } else {
            this.queryUrl = props.getProperty(PROPERTY_QUERY_URL);
        }

        this.service = new EPCglobalEPCISServiceLocator();
        this.service.setEPCglobalEPCISServicePortEndpointAddress(queryUrl);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#getQueryNames()
     */
    public List<String> getQueryNames() throws ServiceException,
            RemoteException {
        EPCISServiceBindingStub stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();
        ArrayOfString temp = stub.getQueryNames(new EmptyParms());
        List<String> names = Arrays.asList(temp.getString());
        return names;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#getStandardVersion()
     */
    public String getStandardVersion() throws ServiceException, RemoteException {
        EPCISServiceBindingStub stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();
        String stdVersion = stub.getStandardVersion(new EmptyParms());
        return stdVersion;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#getSubscriptionIds(java.lang.String)
     */
    public List<String> getSubscriptionIds(final String queryName)
            throws ServiceException, RemoteException {
        EPCISServiceBindingStub stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();
        GetSubscriptionIDs parms = new GetSubscriptionIDs(queryName);
        ArrayOfString res = stub.getSubscriptionIDs(parms);
        List<String> ids = Arrays.asList(res.getString());
        return ids;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#getVendorVersion()
     */
    public String getVendorVersion() throws ServiceException, RemoteException {
        EPCISServiceBindingStub stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();
        String vdrVersion = stub.getVendorVersion(null);
        return vdrVersion;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#poll(java.lang.String,
     *      org.accada.epcis.soapapi.QueryParam[])
     */
    public QueryResults poll(final String queryName, final QueryParam[] params)
            throws ServiceException, RemoteException {
        EPCISServiceBindingStub stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();
        Poll poll = new Poll(queryName, params);
        QueryResults results = stub.poll(poll);
        return results;
    }

    /**
     * Wraps the query given in its XML representation into a SOAP message and
     * sends it directly to the repository's Query Operations Module using HTTP
     * POST. The query results will be unwrapped from the SOAP response message.
     * 
     * @param query
     *            The query in its XML form (will be wrapped into a SOAP request
     *            message).
     * @return The query results in its XML form (unwrapped from a SOAP response
     *         message).
     * @throws IOException
     *             If an error on the transport layer (HTTP) occured.
     */
    public String pollDirect(final String query) throws IOException {
        String soapReq = wrapIntoSoapMessage(query);
        String soapResp = doPost(soapReq.getBytes());
        String queryResp = unwrapFromSoapMessage(soapResp);
        return queryResp;
    }

    /**
     * Parses the query given in its XML representation and sends it to the
     * Query Operations Module. Same operation as the method with the
     * InputStream argument.
     * 
     * @param query
     *            The query in its XML form.
     * @return The QueryResults as it is returned from the repository's Query
     *         Operations Module.
     * @throws RemoteException
     *             If an error communicating with the Query Operations Module
     *             occured.
     * @throws ServiceException
     *             If an error within the Query Operations Module occured.
     */
    public QueryResults poll(final String query) throws RemoteException,
            ServiceException {
        InputStream is = new ByteArrayInputStream(query.getBytes());
        return poll(is);
    }

    /**
     * Parses the query given in its XML representation and sends it to the
     * Query Operations Module.
     * 
     * @param query
     *            The query in its XML form.
     * @return The QueryResults as it is returned from the repository's Query
     *         Operations Module.
     * @throws RemoteException
     *             If an error communicating with the Query Operations Module
     *             occured.
     * @throws ServiceException
     *             If an error within the Query Operations Module occured.
     */
    public QueryResults poll(final InputStream query) throws RemoteException,
            ServiceException {
        Document epcisq = parseAsDocument(query);
        String queryName = epcisq.getElementsByTagName("queryName").item(0).getTextContent();
        Element paramElems = (Element) epcisq.getElementsByTagName("params").item(
                0);
        QueryParam[] params = parseQueryParams(paramElems);

        QueryResults response = this.poll(queryName, params);
        return response;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#subscribe(java.lang.String,
     *      org.accada.epcis.soapapi.QueryParam[], java.net.URI,
     *      org.accada.epcis.soapapi.SubscriptionControls, java.lang.String)
     */
    public void subscribe(final String queryName, final QueryParam[] params,
            final URI dest, final SubscriptionControls controls,
            final String subscriptionId) throws ServiceException,
            RemoteException {
        EPCISServiceBindingStub stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();
        Subscribe subscribe = new Subscribe(queryName, params, dest, controls,
                subscriptionId);
        stub.subscribe(subscribe);
    }

    /**
     * Parses the query given in its XML representation and sends it to the
     * Query Operations Module. Same operation as the method with the
     * InputStream argument.
     * 
     * @param query
     *            The query in its XML form.
     * @throws RemoteException
     *             If an error communicating with the Query Operations Module
     *             occured.
     * @throws ServiceException
     *             If an error within the Query Operations Module occured.
     */
    public void subscribe(final String query) throws RemoteException,
            ServiceException {
        InputStream is = new ByteArrayInputStream(query.getBytes());
        subscribe(is);
    }

    /**
     * Parses the query given in its XML representation and sends it to the
     * Query Operations Module.
     * 
     * @param query
     *            The query in its XML form.
     * @throws RemoteException
     *             If an error communicating with the Query Operations Module
     *             occured.
     * @throws ServiceException
     *             If an error within the Query Operations Module occured.
     */
    public void subscribe(final InputStream query) throws RemoteException,
            ServiceException {
        Document epcisq = parseAsDocument(query);
        String queryName = epcisq.getElementsByTagName("queryName").item(0).getTextContent();
        Element params = (Element) epcisq.getElementsByTagName("params").item(0);
        QueryParam[] queryParams = parseQueryParams(params);

        URI dest = parseURI(epcisq.getElementsByTagName("dest").item(0));
        Element controlsElement = (Element) epcisq.getElementsByTagName(
                "controls").item(0);
        SubscriptionControls controls = parseSubscriptionControls(controlsElement);
        String subscrId = null;
        Node subscribeIdNode = epcisq.getElementsByTagName("subscriptionID").item(
                0);
        if (subscribeIdNode != null) {
            subscrId = subscribeIdNode.getTextContent();
        }

        this.subscribe(queryName, queryParams, dest, controls, subscrId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#unsubscribe(java.lang.String)
     */
    public void unsubscribe(final String subscriptionId)
            throws ServiceException, RemoteException {
        EPCISServiceBindingStub stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();
        Unsubscribe parms = new Unsubscribe(subscriptionId);
        stub.unsubscribe(parms);
    }

    /**
     * @return The URL String at which the Query Operations Module listens.
     */
    public String getQueryUrl() {
        return queryUrl;
    }

    /**
     * Wraps the given query String into a SOAP envelope.
     * 
     * @param query
     *            The query to be wrapped into the SOAP body.
     * @return The SOAP envelope containing the query.
     */
    private String wrapIntoSoapMessage(final String query) {
        StringBuffer soap = new StringBuffer();
        soap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        soap.append("<soapenv:Envelope ");
        soap.append("xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        soap.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        soap.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
        soap.append("<soapenv:Body>");
        soap.append(query);
        soap.append("</soapenv:Body>\n");
        soap.append("</soapenv:Envelope>");
        return soap.toString();
    }

    /**
     * Extracts the contents of the body of the given SOAP envelope.
     * 
     * @param soapMsg
     *            The SOAP envelope.
     * @return The contents of the body of the SOAP envelope.
     */
    private String unwrapFromSoapMessage(final String soapMsg) {
        int beginIndex = soapMsg.indexOf("<soapenv:Body>")
                + "<soapenv:Body>".length();
        int endIndex = soapMsg.lastIndexOf("</soapenv:Body>");
        return soapMsg.substring(beginIndex, endIndex);
    }

    /**
     * Sends the given data to the repository's Query Operations Module using
     * HTTP POST. The data must be a SOAP envelope.
     * 
     * @param data
     *            The data to be sent.
     * @return The response from the repository's Query Operations Module.
     * @throws IOException
     *             If an error on the transport layer (HTTP) occured.
     */
    private String doPost(final byte[] data) throws IOException {
        // the url where the query interface listens
        URL serviceUrl = new URL(queryUrl);

        // open an http connection
        HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();

        // post the data
        connection.setDoOutput(true);
        connection.addRequestProperty("SOAPAction", "");
        OutputStream out = connection.getOutputStream();
        out.write(data);
        out.flush();
        out.close();

        // get response
        String response = "HTTP/1.0 " + connection.getResponseCode() + " "
                + connection.getResponseMessage() + ": ";

        // read and return response
        InputStream in = null;
        try {
            in = connection.getInputStream();
        } catch (IOException e) {
            in = connection.getErrorStream();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine()) != null) {
            response = response + line + "\n";
        }
        return response.trim();
    }

    /**
     * Creates a DOM Document from the given XML query used for further parsing.
     * 
     * @param query
     *            The query to be parsed.
     * @return The query parsed as Document.
     */
    private Document parseAsDocument(final InputStream query) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document epcisq;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            epcisq = builder.parse(query);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse the XML query.", e);
        }
        return epcisq;
    }

    /**
     * Processes the query parameters.
     * 
     * @param params
     *            The query parameter Element.
     * @return The query parameters.
     */
    private QueryParam[] parseQueryParams(final Element params) {
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

    /**
     * Parses the value of a parameter element.
     * 
     * @param valueElement
     *            The Element to be parsed.
     * @return The parsed parameter value.
     */
    private Object parseParamValue(final Element valueElement) {
        Object paramValue = null;
        // check if we have an array of strings
        NodeList stringNodes = valueElement.getElementsByTagName("string");
        int size = stringNodes.getLength();
        if (size > 0) {
            String[] strings = new String[size];
            boolean[] noHackAroundBugs = new boolean[size];
            for (int i = 0; i < size; i++) {
                String string = stringNodes.item(i).getTextContent();
                strings[i] = string;
                noHackAroundBugs[i] = true;
            }
            paramValue = new ArrayOfString(strings, noHackAroundBugs);
        } else {
            // check if we have an Integer
            try {
                paramValue = Integer.parseInt(valueElement.getTextContent());
            } catch (Exception e) {
                // check if we have a time value
                try {
                    paramValue = parseTime(valueElement);
                } catch (Exception e1) {
                    // check if we have an URI
                    try {
                        paramValue = parseURI(valueElement);
                    } catch (Exception e2) {
                        // ok lets take it as String
                        paramValue = valueElement.getTextContent();
                    }
                }
            }
        }
        return paramValue;
    }

    /**
     * Parses the given subscription controls Element into a
     * SubscriptionControls object.
     * 
     * @param controlsNode
     *            The subscription controls Element.
     * @return The parsed SubscriptionControls.
     */
    private SubscriptionControls parseSubscriptionControls(
            final Element controlsNode) {
        Element scheduleNode = (Element) controlsNode.getElementsByTagName(
                "schedule").item(0);
        QuerySchedule schedule = parseQuerySchedule(scheduleNode);

        URI trigger = null;
        Node triggerNode = controlsNode.getElementsByTagName("trigger").item(0);
        if (triggerNode != null) {
            trigger = parseURI(triggerNode);
        }

        Node timeNode = controlsNode.getElementsByTagName("initialRecordTime").item(
                0);
        Calendar initialRecordTime = null;
        try {
            initialRecordTime = parseTime(timeNode);
        } catch (ParseException e) {
            String msg = "Unable to parse time value for 'initialRecordTime': "
                    + e.getMessage();
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

    /**
     * Parses the given schedule Element into a QuerySchedule.
     * 
     * @param scheduleNode
     *            The schedule Element to be parsed.
     * @return The parsed QuerySchedule.
     */
    private QuerySchedule parseQuerySchedule(final Element scheduleNode) {
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
     * Parses the given uri Node into a URI.
     * 
     * @param node
     *            The uri Node to be parsed.
     * @return The parsed URI.
     */
    private URI parseURI(final Node node) {
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
     * @exception ParseException
     *                If the date/time could not be parsed.
     */
    private Calendar parseTime(final Node eventTimeNode) throws ParseException {
        Calendar cal = null;
        if (eventTimeNode != null) {
            String eventTimeStr = eventTimeNode.getTextContent();
            cal = TimeParser.parseAsCalendar(eventTimeStr);
        }
        return cal;
    }
}
