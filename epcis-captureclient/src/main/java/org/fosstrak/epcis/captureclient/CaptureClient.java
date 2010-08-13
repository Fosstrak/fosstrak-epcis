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

package org.fosstrak.epcis.captureclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.fosstrak.epcis.model.Document;
import org.fosstrak.epcis.model.EPCISDocumentType;
import org.fosstrak.epcis.model.EPCISMasterDataDocumentType;
import org.fosstrak.epcis.model.ObjectFactory;
import org.fosstrak.epcis.utils.AuthenticationType;

/**
 * This client provides access to the repository's Capture Operations Module
 * through the capture interface. EPCISEvents will be sent to the module using
 * HTTP POST requests.
 * 
 * @author Marco Steybe
 */
public class CaptureClient implements X509TrustManager, HostnameVerifier {

    private static final String PROPERTY_FILE = "/captureclient.properties";
    private static final String PROPERTY_CAPTURE_URL = "default.url";
    private static final String DEFAULT_CAPTURE_URL = "http://demo.fosstrak.org/epcis/capture";

    /**
     * The URL String at which the Capture Operations Module listens.
     */
    private String captureUrl;
    
    private Object[] authOptions;

    /**
     * Constructs a new CaptureClient which connects to the Capture Operations
     * Module listening at the default URL, with no authentication.
     */
    public CaptureClient() {
        this(null, null);
    }

    /**
     * Constructs a new CaptureClient which connects to the Capture Operations
     * Module listening at the specified URL, with no authentication.
     */
    public CaptureClient(String url) {
    	this(url, null);
    }
    
    /**
     * Constructs a new CaptureClient which connects to the Capture Operations
     * Module listening at the given URL.
     * 
     * @param url The URL at which the capture service listens.
     * @param authOptions The authentication options:
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
    public CaptureClient(final String url, Object[] authOptions) {
    	// set the URL
    	if (url != null) {
    		captureUrl = url;
    	} else {
    		Properties props = loadProperties();
    		if (props != null) {
    			captureUrl = props.getProperty(PROPERTY_CAPTURE_URL);
    		}
    		if (captureUrl == null) {
    			captureUrl = DEFAULT_CAPTURE_URL;
    		}
    	}
    	this.authOptions = authOptions;
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
	            System.out.println("Unable to load properties from "
	                    + PROPERTY_FILE + ". Using defaults.");
	        }
        }
        else {
            System.out.println("Unable to load properties from file "
                    + PROPERTY_FILE + ". Using defaults.");
        }
        return props;
    }

    /**
     * Send the XML available from the given InputStream to the repository for
     * capturing.
     * 
     * @param xmlStream
     *            A stream providing an EPCISDocument which contains a list of
     *            events inside the EPCISBody element.
     * @return The HTTP response code from the repository.
     * @throws IOException
     *             If an error sending the document occurred.
     */
    public int capture(final InputStream xmlStream) throws Exception {
        return doPost(xmlStream, "text/xml");
    }

    /**
     * Send the given XML String to the repository for capturing.
     * 
     * @param eventXml
     *            The XML String consisting of an EPCISDocument which in turn
     *            contains a list of events inside the EPCISBody element.
     * @return The HTTP response code from the repository.
     * @throws IOException
     *             If an error sending the document occurred.
     */
    public int capture(final String eventXml) throws Exception {
        return doPost(eventXml, "text/xml");
    }

    /**
     * Send the given EPCISDocumentType to the repository for capturing.
     * 
     * @param epcisDoc
     *            The EPCISDocument containing a list of events inside the
     *            EPCISBody element.
     * @return The HTTP response code from the repository.
     * @throws IOException
     *             If an error sending the document occurred.
     * @throws JAXBException
     *             If an error serializing the given document into XML occurred.
     */
    public int capture(final Document epcisDoc) throws Exception {
        StringWriter writer = new StringWriter();
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBContext context = JAXBContext.newInstance("org.fosstrak.epcis.model");
        JAXBElement<? extends Document> item;
        if (epcisDoc instanceof EPCISDocumentType) {
            item = objectFactory.createEPCISDocument((EPCISDocumentType) epcisDoc);
        } else {
            item = objectFactory.createEPCISMasterDataDocument((EPCISMasterDataDocumentType) epcisDoc);
        }
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(item, writer);
        return capture(writer.toString());
    }

    /**
     * Invokes the dbReset operation in the Capture Module deleting all event
     * data in the EPCIS database. This operation is only allowed if the
     * corresponding property is set in the repository's configuration.
     * 
     * @return The response from the capture module.
     * @throws IOException
     *             If a communication error occurred.
     */
    public int dbReset() throws Exception {
        String formParam = "dbReset=true";
        return doPost(formParam, "application/x-www-form-urlencoded");

    }

    private boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

    /**
     * Opens a connection to the capture module.
     * 
     * @param contentType
     *            The HTTP content-type, e.g., <code>text/xml</code>
     * @return The HTTP connection object.
     * @throws IOException
     */
    private HttpURLConnection getConnection(final String contentType) throws Exception {

    	URL serviceUrl = new URL(captureUrl);
    	HttpURLConnection connection;
    	SSLContext sslContext = null;
    	
    	if (authOptions != null) {

    		if (AuthenticationType.BASIC.equals(authOptions[0])) {

    			// logger.debug("Authenticating via Basic as: " +
    			// authenticationOptions[1]);

    			final String username = (String)authOptions[1];
    			final String password = (String)authOptions[2];
    			
    			if (isEmpty(username) || isEmpty(password)) {
    				throw new Exception("Authentication method " + authOptions[0] + " requires a valid user name and password");
    			}

    			Authenticator.setDefault(new Authenticator() {
    				@Override
    				protected PasswordAuthentication getPasswordAuthentication() {
    					return new PasswordAuthentication(username, password.toCharArray());
    				}
    			});
    			
    		}
    		else if (AuthenticationType.HTTPS_WITH_CLIENT_CERT.equals(authOptions[0])) {

    			// logger.debug("Authenticating with certificate in file: " +
    			// authenticationOptions[1]);

    			if (!"HTTPS".equalsIgnoreCase(serviceUrl.getProtocol())) {
    				throw new Exception("Authentication method " + authOptions[0] + " requires the use of HTTPS");
    			}

    			String keyStoreFile = (String)authOptions[1];
    			String password = (String)authOptions[2];
    			
    			if (isEmpty(keyStoreFile) || isEmpty(password)) {
    				throw new Exception("Authentication method " + authOptions[0] + " requires a valid keystore (PKCS12 or JKS) and password");
    			}

    			KeyStore keyStore = KeyStore.getInstance(keyStoreFile.endsWith(".p12") ? "PKCS12" : "JKS");
    			keyStore.load(new FileInputStream(new File(keyStoreFile)), password.toCharArray());

    			Authenticator.setDefault(null);
    	    	sslContext = getSSLContext(keyStore, password.toCharArray());
    	    	
    		}
    		else {
    			Authenticator.setDefault(null);
    		}
    	}
    	else {
    		Authenticator.setDefault(null);
    	}

    	connection = (HttpURLConnection)serviceUrl.openConnection();
    	if (sslContext != null && connection instanceof HttpsURLConnection) {
    		HttpsURLConnection httpsConnection = (HttpsURLConnection)connection;
    		httpsConnection.setHostnameVerifier(this);
			httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
    	}
    	connection.setRequestProperty("content-type", contentType);
    	connection.setRequestMethod("POST");
    	connection.setDoInput(true);
    	connection.setDoOutput(true);
    	
    	return connection;
    }
        
    /**
     * Send data to the repository's capture operation using HTTP POST. The data
     * will be sent using the given content-type.
     * 
     * @param data
     *            The data to send.
     * @return The HTTP response message
     * @throws IOException
     *             If an error on the HTTP layer occurred.
     */
    private int doPost(final String data, final String contentType) throws Exception {
        HttpURLConnection connection = getConnection(contentType);
        // write the data
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(data);
        wr.flush();
        wr.close();

        return connection.getResponseCode();
    }

    /**
     * Send data to the repository's capture operation using HTTP POST. The data
     * will be sent using the given content-type.
     * 
     * @param data
     *            The data to send.
     * @return The HTTP response message from the repository.
     * @throws IOException
     *             If an error on the HTTP layer occurred.
     */
    private int doPost(final InputStream data, final String contentType) throws Exception {
        HttpURLConnection connection = getConnection(contentType);
        // read from input and write to output
        OutputStream os = connection.getOutputStream();
        int b;
        while ((b = data.read()) != -1) {
            os.write(b);
        }
        os.flush();
        os.close();

        return connection.getResponseCode();
    }

    /**
     * @return The URL String at which the Capture Operations Module listens.
     */
    public String getCaptureUrl() {
        return captureUrl;
    }

    public Object[] getAuthOptions() {
    	return authOptions;
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
    
    // HostnameVerifier methods: Note that this client will believe the authenticity
    // of any DNS name it is given. Again, probably OK for the nature of this
    // client, but generally not a good idea.
    
	public boolean verify(String arg0, SSLSession arg1) {
		return true;
	}

	private SSLContext getSSLContext(KeyStore keyStore, char[] password) throws Exception {
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, password);
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(keyManagerFactory.getKeyManagers(), new TrustManager[] { this }, new SecureRandom());
		return context;
	}

}
