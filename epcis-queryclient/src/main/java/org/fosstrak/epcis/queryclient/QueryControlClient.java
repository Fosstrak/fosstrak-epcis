/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org).
 *
 * Fosstrak is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Fosstrak is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Fosstrak; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.fosstrak.epcis.queryclient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.ClientOnlyHTTPTransportFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.fosstrak.epcis.model.EmptyParms;
import org.fosstrak.epcis.model.GetSubscriptionIDs;
import org.fosstrak.epcis.model.Poll;
import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.model.Subscribe;
import org.fosstrak.epcis.model.Unsubscribe;
import org.fosstrak.epcis.soap.DuplicateSubscriptionExceptionResponse;
import org.fosstrak.epcis.soap.EPCISServicePortType;
import org.fosstrak.epcis.soap.ImplementationExceptionResponse;
import org.fosstrak.epcis.soap.InvalidURIExceptionResponse;
import org.fosstrak.epcis.soap.NoSuchNameExceptionResponse;
import org.fosstrak.epcis.soap.NoSuchSubscriptionExceptionResponse;
import org.fosstrak.epcis.soap.QueryParameterExceptionResponse;
import org.fosstrak.epcis.soap.QueryTooComplexExceptionResponse;
import org.fosstrak.epcis.soap.QueryTooLargeExceptionResponse;
import org.fosstrak.epcis.soap.SecurityExceptionResponse;
import org.fosstrak.epcis.soap.SubscribeNotPermittedExceptionResponse;
import org.fosstrak.epcis.soap.SubscriptionControlsExceptionResponse;
import org.fosstrak.epcis.soap.ValidationExceptionResponse;
import org.fosstrak.epcis.utils.AuthenticationType;

/**
 * This query client makes calls against the EPCIS query control interface and
 * also provides some convenience methods for polling and subscribing queries
 * given in XML form.
 * 
 * @author Marco Steybe
 */
public class QueryControlClient implements QueryControlInterface, X509TrustManager {

    private static final String PROPERTY_FILE = "/queryclient.properties";
    private static final String PROP_QUERY_URL = "default.url";
    private static final String DEFAULT_QUERY_URL = "http://demo.fosstrak.org/epcis/query";

    private static final QName SERVICE = new QName("urn:epcglobal:epcis:wsdl:1", "EPCglobalEPCISService");
    private static final QName PORT = new QName("urn:epcglobal:epcis:wsdl:1", "EPCglobalEPCISServicePort");

    private String queryUrl;

    /**
     * The locator for the service.
     */
    private EPCISServicePortType servicePort;

    /**
     * Whether or not this service is configured and ready to use.
     */
    private boolean serviceConfigured = false;

    /**
     * Constructs a new QueryControlClient using a default URL and no
     * authentication. You can also configure the service through
     * {@link #configureService(URL, Object[])} prior to calling any
     * QueryControlInterface service method.
     */
    public QueryControlClient() {
        this(null, null);
    }

    /**
     * Constructs a new QueryControlClient using the given URL and no
     * authentication. You can also configure the service through
     * {@link #configureService(URL, Object[])} prior to calling any
     * QueryControlInterface service method.
     */
    public QueryControlClient(String url) {
        this(url, null);
    }

    /**
     * Constructs a new QueryControlClient using the given URL and
     * authentication options. You can also configure the service through
     * {@link #configureService(URL, Object[])} prior to calling any
     * QueryControlInterface service method.
     * 
     * @param url
     *            the URL of the repository, or <code>null</code> if a default
     *            value should be loaded.
     * @param authenticationOptions
     *            The authentication options:
     *            <p>
     *            <table border="1">
     *            <tr>
     *            <td><code>authenticationOptions[0]</code></td>
     *            <td><code>[1]</code></td>
     *            <td><code>[2]</code></td>
     *            </tr>
     *            <tr>
     *            <td><code>AuthenticationType.BASIC</code></td>
     *            <td>username</td>
     *            <td>password</td>
     *            </tr>
     *            <tr>
     *            <td><code>AuthenticationType.HTTPS_WITH_CLIENT_CERT</code></td>
     *            <td>keystore file</td>
     *            <td>password</td>
     *            </tr>
     *            </table>
     */
    public QueryControlClient(String url, Object[] authenticationOptions) {
        if (url != null) {
            this.queryUrl = url;
        } else {
            Properties props = loadProperties();
            this.queryUrl = props.getProperty(PROP_QUERY_URL, DEFAULT_QUERY_URL);
            try {
                new URL(queryUrl);
            } catch (MalformedURLException e) {
                queryUrl = DEFAULT_QUERY_URL;
            }
        }
        try {
            configureService(new URL(queryUrl), authenticationOptions);
        } catch (Exception e) {
            throw new RuntimeException("unable to configure QueryControlClient: " + e.getMessage(), e);
        }
    }

    /**
     * @return The query client properties.
     */
    private Properties loadProperties() {
        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream(PROPERTY_FILE);
        if (is != null) {
            try {
                props.load(is);
                is.close();
            } catch (IOException e) {
                System.out.println("Unable to load queryclient properties from "
                        + QueryControlClient.class.getResource(PROPERTY_FILE).toString() + ". Using defaults.");
            }
        } else {
            System.out.println("Unable to load queryclient properties from " + PROPERTY_FILE + ". Using defaults.");
        }
        return props;
    }

    /**
     * @return whether or not this service is configured and ready to use.
     */
    public boolean isServiceConfigured() {
        return serviceConfigured;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    private boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

    /**
     * Configures the service to communicate with the given endpoint address
     * using the desired authentication method.
     * 
     * @param endpointAddress
     *            The endpoint address this client will communicate to.
     * @param authenticationOptions
     *            The authentication options:
     *            <p>
     *            <table border="1">
     *            <tr>
     *            <td><code>authenticationOptions[0]</code></td>
     *            <td><code>[1]</code></td>
     *            <td><code>[2]</code></td>
     *            </tr>
     *            <tr>
     *            <td><code>AuthenticationType.BASIC</code></td>
     *            <td>username</td>
     *            <td>password</td>
     *            </tr>
     *            <tr>
     *            <td><code>AuthenticationType.HTTPS_WITH_CLIENT_CERT</code></td>
     *            <td>keystore file</td>
     *            <td>password</td>
     *            </tr>
     *            </table>
     * @throws Exception
     */
    public void configureService(URL endpointAddress, Object[] authenticationOptions) throws Exception {
        // logger.debug("Configuring service to communicate with endpoint: " +
        // endpointAddress);
        serviceConfigured = false;

        // setup the CXF bus
        setUpBus();

        // instantiates a client proxy object from the EPCISServicePortType
        // interface using the JAX-WS API
        Service service = Service.create(SERVICE);
        service.addPort(PORT, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress.toString());
        servicePort = service.getPort(PORT, EPCISServicePortType.class);

        // turn off chunked transfer encoding
        Client client = ClientProxy.getClient(servicePort);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        httpConduit.setClient(httpClientPolicy);

        // set up any authentication
        if (authenticationOptions != null) {
            if (AuthenticationType.BASIC.equals(authenticationOptions[0])) {
                // logger.debug("Authenticating via Basic as: " +
                // authenticationOptions[1]);

                String username = (String) authenticationOptions[1];
                String password = (String) authenticationOptions[2];

                if (isEmpty(username) || isEmpty(password)) {
                    throw new Exception("Authentication method " + authenticationOptions[0]
                            + " requires a valid user name and password");
                }

                AuthorizationPolicy ap = httpConduit.getAuthorization();
                ap.setUserName(username);
                ap.setPassword(password);
            } else if (AuthenticationType.HTTPS_WITH_CLIENT_CERT.equals(authenticationOptions[0])) {
                // logger.debug("Authenticating with certificate in file: " +
                // authenticationOptions[1]);

                if (!"HTTPS".equalsIgnoreCase(endpointAddress.getProtocol())) {
                    throw new Exception("Authentication method " + authenticationOptions[0]
                            + " requires the use of HTTPS");
                }

                String keyStoreFile = (String) authenticationOptions[1];
                String password = (String) authenticationOptions[2];

                if (isEmpty(keyStoreFile) || isEmpty(password)) {
                    throw new Exception("Authentication method " + authenticationOptions[0]
                            + " requires a valid keystore (PKCS12 or JKS) and password");
                }

                KeyStore keyStore = KeyStore.getInstance(keyStoreFile.endsWith(".p12") ? "PKCS12" : "JKS");
                keyStore.load(new FileInputStream(new File(keyStoreFile)), password.toCharArray());
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
                keyManagerFactory.init(keyStore, password.toCharArray());

                TLSClientParameters tlscp = new TLSClientParameters();
                tlscp.setKeyManagers(keyManagerFactory.getKeyManagers());
                tlscp.setSecureRandom(new SecureRandom());
                tlscp.setDisableCNCheck(true);
                tlscp.setTrustManagers(new TrustManager[] { this });

                httpConduit.setTlsClientParameters(tlscp);
            }
        }

        /*
         * For instantiating a client proxy object using reflection (CXF simple
         * frontend), use the ClientProxyFactoryBean and provide it with the
         * service interface. The setter methods on the factory object can be
         * used to configure the client proxy.
         */
        // ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
        // factory.setServiceClass(EPCISServicePortType.class);
        // factory.setAddress(endpointAddress);
        // factory.setBindingId(SOAPBinding.SOAP11HTTP_BINDING);
        // servicePort = (EPCISServicePortType) factory.create();
        /*
         * For instantiating a client proxy object using the CXF-generated
         * service implementation, uncomment the following lines. This will
         * create the proxy from the WSDL file, using the endpointAddress from
         * the <wsdlsoap:address> element inside the WSDL document.
         */
        // EPCglobalEPCISService s = new EPCglobalEPCISService(wsdlLocation,
        // SERVICE);
        // servicePort = s.getEPCglobalEPCISServicePort();
        serviceConfigured = true;
    }

    private void setUpBus() {
        Bus bus = CXFBusFactory.getDefaultBus();
        ClientOnlyHTTPTransportFactory httpTransport = new ClientOnlyHTTPTransportFactory();
        // httpTransport = new ServletTransportFactory();
        httpTransport.setBus(bus);
        List<String> transportIds = Arrays.asList(new String[] {
                "http://schemas.xmlsoap.org/wsdl/soap/http", "http://schemas.xmlsoap.org/soap/http",
                "http://www.w3.org/2003/05/soap/bindings/HTTP/", "http://schemas.xmlsoap.org/wsdl/http/",
                "http://cxf.apache.org/transports/http/configuration", "http://cxf.apache.org/bindings/xformat", });
        httpTransport.setTransportIds(transportIds);
        httpTransport.registerWithBindingManager();
        // httpTransport.register();
    }

    // X509TrustManager methods: Note that this client will trust any server
    // you point it at. This is probably OK for the usage for which this program
    // is intended, but is hardly a robust implementation.

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.fosstrak.epcis.queryclient.QueryControlInterface#getQueryNames()
     */
    public List<String> getQueryNames() throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse {
        if (!serviceConfigured) {
            throw new QueryClientNotConfiguredException(
                    "Please configure service by calling configureService(URL, String[]).");
        }
        return servicePort.getQueryNames(new EmptyParms()).getString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.fosstrak.epcis.queryclient.QueryControlInterface#getStandardVersion()
     */
    public String getStandardVersion() throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse {
        if (!serviceConfigured) {
            throw new QueryClientNotConfiguredException(
                    "Please configure service by calling configureService(URL, String[]).");
        }
        return servicePort.getStandardVersion(new EmptyParms());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.fosstrak.epcis.queryclient.QueryControlInterface#getSubscriptionIds(java.lang.String)
     */
    public List<String> getSubscriptionIds(final String queryName) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse {
        if (!serviceConfigured) {
            throw new QueryClientNotConfiguredException(
                    "Please configure service by calling configureService(URL, String[]).");
        }
        GetSubscriptionIDs parms = new GetSubscriptionIDs();
        parms.setQueryName(queryName);
        return servicePort.getSubscriptionIDs(parms).getString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.fosstrak.epcis.queryclient.QueryControlInterface#getVendorVersion()
     */
    public String getVendorVersion() throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse {
        if (!serviceConfigured) {
            throw new QueryClientNotConfiguredException(
                    "Please configure service by calling configureService(URL, String[]).");
        }
        return servicePort.getVendorVersion(new EmptyParms());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.fosstrak.epcis.queryclient.QueryControlInterface#poll(org.fosstrak.epcis.model.Poll)
     */
    public QueryResults poll(final Poll poll) throws ImplementationExceptionResponse, QueryTooComplexExceptionResponse,
            QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse,
            NoSuchNameExceptionResponse, QueryParameterExceptionResponse {
        if (!serviceConfigured) {
            throw new QueryClientNotConfiguredException(
                    "Please configure service by calling configureService(URL, String[]).");
        }
        return servicePort.poll(poll);
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
            JAXBContext context = JAXBContext.newInstance("org.fosstrak.epcis.model");
            Unmarshaller unmarshaller = context.createUnmarshaller();
            // setting schema to null will turn XML validation off
            // unmarshaller.setSchema(null);
            JAXBElement<?> elem = (JAXBElement<?>) unmarshaller.unmarshal(queryStream);
            Poll poll = (Poll) elem.getValue();
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
     * @see org.fosstrak.epcis.queryclient.QueryControlInterface#subscribe(org.fosstrak.epcis.model.Subscribe)
     */
    public void subscribe(final Subscribe subscribe) throws DuplicateSubscriptionExceptionResponse,
            ImplementationExceptionResponse, QueryTooComplexExceptionResponse, SecurityExceptionResponse,
            InvalidURIExceptionResponse, ValidationExceptionResponse, SubscribeNotPermittedExceptionResponse,
            NoSuchNameExceptionResponse, SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse {
        if (!serviceConfigured) {
            throw new QueryClientNotConfiguredException(
                    "Please configure service by calling configureService(URL, String[]).");
        }
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
            JAXBContext context = JAXBContext.newInstance("org.fosstrak.epcis.model");
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<?> elem = (JAXBElement<?>) unmarshaller.unmarshal(query);
            Subscribe subscribe = (Subscribe) elem.getValue();
            subscribe(subscribe);
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
     * @see org.fosstrak.epcis.queryclient.QueryControlInterface#unsubscribe(java.lang.String)
     */
    public void unsubscribe(final String subscriptionId) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchSubscriptionExceptionResponse {
        if (!serviceConfigured) {
            throw new QueryClientNotConfiguredException(
                    "Please configure service by calling configureService(URL, String[]).");
        }
        Unsubscribe parms = new Unsubscribe();
        parms.setSubscriptionID(subscriptionId);
        servicePort.unsubscribe(parms);
    }

    /**
     * Parses the XML from the given input stream as an Unsubscribe object and
     * unsubscribes the specified subscription ID from the repository.
     * 
     * @param unsubscribeIs
     * @throws ImplementationExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws NoSuchSubscriptionExceptionResponse
     * @throws IOException
     */
    public void unsubscribe(final InputStream unsubscribeIs) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchSubscriptionExceptionResponse, IOException {
        try {
            JAXBContext context = JAXBContext.newInstance("org.fosstrak.epcis.model");
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<?> elem = (JAXBElement<?>) unmarshaller.unmarshal(unsubscribeIs);
            Unsubscribe unsubscribe = (Unsubscribe) elem.getValue();
            unsubscribe(unsubscribe.getSubscriptionID());
        } catch (JAXBException e) {
            // wrap JAXBException into IOException to keep the interface
            // JAXB-free
            IOException ioe = new IOException(e.getMessage());
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
    }
}
