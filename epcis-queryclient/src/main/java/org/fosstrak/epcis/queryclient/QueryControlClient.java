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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.accada.epcis.soap.DuplicateSubscriptionExceptionResponse;
import org.accada.epcis.soap.EPCISServicePortType;
import org.accada.epcis.soap.EPCglobalEPCISService;
import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.InvalidURIExceptionResponse;
import org.accada.epcis.soap.NoSuchNameExceptionResponse;
import org.accada.epcis.soap.NoSuchSubscriptionExceptionResponse;
import org.accada.epcis.soap.QueryParameterExceptionResponse;
import org.accada.epcis.soap.QueryTooComplexExceptionResponse;
import org.accada.epcis.soap.QueryTooLargeExceptionResponse;
import org.accada.epcis.soap.SecurityExceptionResponse;
import org.accada.epcis.soap.SubscribeNotPermittedExceptionResponse;
import org.accada.epcis.soap.SubscriptionControlsExceptionResponse;
import org.accada.epcis.soap.ValidationExceptionResponse;
import org.accada.epcis.soap.model.EmptyParms;
import org.accada.epcis.soap.model.GetSubscriptionIDs;
import org.accada.epcis.soap.model.Poll;
import org.accada.epcis.soap.model.QueryParams;
import org.accada.epcis.soap.model.QueryResults;
import org.accada.epcis.soap.model.Subscribe;
import org.accada.epcis.soap.model.SubscriptionControls;
import org.accada.epcis.soap.model.Unsubscribe;
import org.apache.cxf.service.factory.ReflectionServiceFactoryBean;

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

    private static final QName SERVICE = new QName("urn:epcglobal:epcis:wsdl:1", "EPCglobalEPCISService");
    private static final QName PORT = new QName("urn:epcglobal:epcis:wsdl:1", "EPCglobalEPCISServicePort");

    private static URL WSDL_LOCATION;

    static {
        // TODO: do not hard code the wsdl location here! better to read it
        // from properties
        WSDL_LOCATION = ClassLoader.getSystemResource("wsdl/EPCglobal-epcis-query-1_0.wsdl");
    }

    /**
     * The URL String at which the Query Operations Module listens.
     */
    private String queryUrl = null;

    /**
     * The locator for the service.
     */
    private EPCISServicePortType servicePort;

    /**
     * Constructs a new QueryClient which connects to the repository's Query
     * Operations Module listening at a default url address.
     */
    public QueryControlClient() {
        this(WSDL_LOCATION);
    }

    /**
     * Constructs a new QueryClient which connects to the repository's Query
     * Operations Module listening at the given url address.
     * 
     * @param wsdlLocation
     *            The URL String the query module is listening at.
     */
    public QueryControlClient(final URL wsdlLocation) {
        // Service service = new Service(wsdlLocation, SERVICE);
        // servicePort = service.getPort(PORT, EPCISServicePortType.class);
        EPCglobalEPCISService service = new EPCglobalEPCISService(wsdlLocation, SERVICE);
        servicePort = service.getEPCglobalEPCISServicePort();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#getQueryNames()
     */
    public List<String> getQueryNames() throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse {
        return servicePort.getQueryNames(new EmptyParms()).getString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#getStandardVersion()
     */
    public String getStandardVersion() throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse {
        return servicePort.getStandardVersion(new EmptyParms());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#getSubscriptionIds(java.lang.String)
     */
    public List<String> getSubscriptionIds(final String queryName) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse {
        GetSubscriptionIDs parms = new GetSubscriptionIDs();
        parms.setQueryName(queryName);
        return servicePort.getSubscriptionIDs(parms).getString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#getVendorVersion()
     */
    public String getVendorVersion() throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse {
        return servicePort.getVendorVersion(new EmptyParms());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#poll(org.accada.epcis.soap.model.Poll)
     */
    public QueryResults poll(final Poll poll) throws ImplementationExceptionResponse, QueryTooComplexExceptionResponse,
            QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse,
            NoSuchNameExceptionResponse, QueryParameterExceptionResponse {
        return servicePort.poll(poll);
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
     *             If an error on the transport layer (HTTP) occurred.
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
     * @throws ServiceException
     *             If an error within the Query Operations Module occurred.
     * @throws IOException
     * @throws QueryParameterExceptionResponse
     * @throws NoSuchNameExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws QueryTooLargeExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws ImplementationExceptionResponse
     */
    public QueryResults poll(final String query) throws QueryTooComplexExceptionResponse,
            QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse,
            NoSuchNameExceptionResponse, QueryParameterExceptionResponse, IOException, ImplementationExceptionResponse {
        InputStream is = new ByteArrayInputStream(query.getBytes());
        return poll(is);
    }

    /**
     * Parses the query given in its XML representation and sends it to the
     * Query Operations Module.
     * 
     * @param queryStream
     *            The query in its XML form.
     * @return The QueryResults as it is returned from the repository's Query
     *         Operations Module.
     * @throws RemoteException
     *             If an error communicating with the Query Operations Module
     *             occurred.
     * @throws ServiceException
     *             If an error within the Query Operations Module occurred.
     * @throws QueryParameterExceptionResponse
     * @throws NoSuchNameExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws QueryTooLargeExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws ImplementationExceptionResponse
     * @throws IOException
     */
    public QueryResults poll(final InputStream queryStream) throws ImplementationExceptionResponse,
            QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse, NoSuchNameExceptionResponse, QueryParameterExceptionResponse, IOException {
        try {
            JAXBContext context = JAXBContext.newInstance("org.accada.epcis.soap.model");
            Unmarshaller unmarshaller = context.createUnmarshaller();
            // setting schema to null will turn XML validation off
            // unmarshaller.setSchema(null);
            JAXBElement<Poll> elem = (JAXBElement) unmarshaller.unmarshal(queryStream);
            Poll poll = elem.getValue();
            return poll(poll);
        } catch (JAXBException e) {
            // wrap JAXBException into IOException to keep the interface
            // JAXB-free
            IOException ioe = new IOException(e.getMessage());
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#subscribe(java.lang.String,
     *      org.accada.epcis.soapapi.QueryParam[], java.lang.String,
     *      org.accada.epcis.soapapi.SubscriptionControls, java.lang.String)
     */
    public void subscribe(final String queryName, final QueryParams params, final String dest,
            final SubscriptionControls controls, final String subscriptionId)
            throws DuplicateSubscriptionExceptionResponse, ImplementationExceptionResponse,
            QueryTooComplexExceptionResponse, SecurityExceptionResponse, InvalidURIExceptionResponse,
            ValidationExceptionResponse, SubscribeNotPermittedExceptionResponse, NoSuchNameExceptionResponse,
            SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse {
        Subscribe subscribe = new Subscribe();
        subscribe.setControls(controls);
        subscribe.setDest(dest);
        subscribe.setParams(params);
        subscribe.setQueryName(queryName);
        subscribe.setSubscriptionID(subscriptionId);
        servicePort.subscribe(subscribe);
    }

    /**
     * Parses the query given in its XML representation and sends it to the
     * Query Operations Module. Same operation as the method with the
     * InputStream argument.
     * 
     * @param query
     *            The query in its XML form.
     * @throws ServiceException
     *             If an error within the Query Operations Module occurred.
     * @throws QueryParameterExceptionResponse
     * @throws SubscriptionControlsExceptionResponse
     * @throws NoSuchNameExceptionResponse
     * @throws SubscribeNotPermittedExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws InvalidURIExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws ImplementationExceptionResponse
     * @throws DuplicateSubscriptionExceptionResponse
     * @throws IOException
     */
    public void subscribe(final String query) throws DuplicateSubscriptionExceptionResponse,
            ImplementationExceptionResponse, QueryTooComplexExceptionResponse, SecurityExceptionResponse,
            InvalidURIExceptionResponse, ValidationExceptionResponse, SubscribeNotPermittedExceptionResponse,
            NoSuchNameExceptionResponse, SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse,
            IOException {
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
     *             occurred.
     * @throws ServiceException
     *             If an error within the Query Operations Module occurred.
     * @throws QueryParameterExceptionResponse
     * @throws SubscriptionControlsExceptionResponse
     * @throws NoSuchNameExceptionResponse
     * @throws SubscribeNotPermittedExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws InvalidURIExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws ImplementationExceptionResponse
     * @throws DuplicateSubscriptionExceptionResponse
     * @throws IOException
     */
    public void subscribe(final InputStream query) throws DuplicateSubscriptionExceptionResponse,
            ImplementationExceptionResponse, QueryTooComplexExceptionResponse, SecurityExceptionResponse,
            InvalidURIExceptionResponse, ValidationExceptionResponse, SubscribeNotPermittedExceptionResponse,
            NoSuchNameExceptionResponse, SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse,
            IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(Poll.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            // setting schema to null will turn XML validation off
            // unmarshaller.setSchema(null);
            Subscribe subscribe = (Subscribe) unmarshaller.unmarshal(query);
            subscribe(subscribe.getQueryName(), subscribe.getParams(), subscribe.getDest(), subscribe.getControls(),
                    subscribe.getSubscriptionID());
        } catch (JAXBException e) {
            // wrap JAXBException into IOException to keep the interface
            // JAXB-free
            IOException ioe = new IOException(e.getMessage());
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryControlInterface#unsubscribe(java.lang.String)
     */
    public void unsubscribe(final String subscriptionId) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchSubscriptionExceptionResponse {
        Unsubscribe parms = new Unsubscribe();
        parms.setSubscriptionID(subscriptionId);
        servicePort.unsubscribe(parms);
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
        StringBuilder soap = new StringBuilder();
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
        int beginIndex = soapMsg.indexOf("<soapenv:Body>") + "<soapenv:Body>".length();
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
     *             If an error on the transport layer (HTTP) occurred.
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
        String response = "HTTP/1.0 " + connection.getResponseCode() + " " + connection.getResponseMessage() + ": ";

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
}
