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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.fosstrak.epcis.model.EPCISDocumentType;
import org.fosstrak.epcis.model.ObjectFactory;

/**
 * This client provides access to the repository's Capture Operations Module
 * through the capture interface. EPCISEvents will be sent to the module using
 * HTTP POST requests.
 * 
 * @author Marco Steybe
 */
public class CaptureClient {

    private static final String PROPERTY_FILE = "/captureclient.properties";
    private static final String PROPERTY_CAPTURE_URL = "default.url";

    /**
     * The URL String at which the Capture Operations Module listens.
     */
    private String captureUrl;

    private Properties props = new Properties();

    /**
     * Constructs a new CaptureClient which connects to the Capture Operations
     * Module listening at the default URL.
     */
    public CaptureClient() {
        this(null);
    }

    /**
     * Constructs a new CaptureClient which connects to the Capture Operations
     * Module listening at the given URL.
     * 
     * @param url
     *            The URL at which the capture service listens.
     */
    public CaptureClient(final String url) {
        // load properties
        InputStream is = this.getClass().getResourceAsStream(PROPERTY_FILE);
        if (is == null) {
            throw new RuntimeException("Unable to load properties from file " + PROPERTY_FILE);
        }
        try {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties from file " + PROPERTY_FILE);
        }

        // set the URL
        if (url != null) {
            captureUrl = url;
        } else {
            captureUrl = props.getProperty(PROPERTY_CAPTURE_URL);
        }
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
    public int capture(final InputStream xmlStream) throws IOException {
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
    public int capture(final String eventXml) throws IOException {
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
    public int capture(final EPCISDocumentType epcisDoc) throws IOException, JAXBException {
        StringWriter writer = new StringWriter();
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBContext context = JAXBContext.newInstance("org.fosstrak.epcis.model");
        JAXBElement<EPCISDocumentType> item = objectFactory.createEPCISDocument(epcisDoc);
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
    public int dbReset() throws IOException {
        String formParam = "dbReset=true";
        return doPost(formParam, "application/x-www-form-urlencoded");

    }

    /**
     * Opens a connection to the capture module.
     * 
     * @param contentType
     *            The HTTP content-type, e.g., <code>text/xml</code>
     * @return The HTTP connection object.
     * @throws IOException
     */
    private HttpURLConnection getConnection(final String contentType) throws IOException {
        URL serviceUrl = new URL(captureUrl);
        HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();
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
    private int doPost(final String data, final String contentType) throws IOException {
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
    private int doPost(final InputStream data, final String contentType) throws IOException {
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

    /**
     * @param url
     *            The new URL String to which this client should connect.
     */
    public void setCaptureUrl(final String url) {
        this.captureUrl = url;
    }
}
