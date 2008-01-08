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

package org.accada.epcis.captureclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * This client provides access to the repository's Capture Operations Module
 * through the capture interface. EPCISEvents will be sent to the module using
 * HTTP POST requests.
 * 
 * @author Marco Steybe
 */
public class CaptureClient implements CaptureInterface {

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
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.captureclient.CaptureInterface#capture(java.io.InputStream)
     */
    public int capture(final InputStream xmlStream) throws IOException {
        return doPost(xmlStream, "text/xml");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.captureclient.CaptureInterface#capture(java.lang.String)
     */
    public int capture(final String eventXml) throws IOException {
        return doPost(eventXml, "text/xml");
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
     * @return
     * @throws IOException
     */
    private HttpURLConnection openPostConnection(final String contentType) throws IOException {
        // the url where the capture interface listens
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
        HttpURLConnection connection = openPostConnection(contentType);
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
     * @return The HTTP response message
     * @throws IOException
     *             If an error on the HTTP layer occurred.
     */
    private int doPost(final InputStream data, final String contentType) throws IOException {
        HttpURLConnection connection = openPostConnection(contentType);
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
