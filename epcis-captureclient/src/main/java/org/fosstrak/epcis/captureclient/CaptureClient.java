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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
            throw new RuntimeException("Unable to load properties from file "
                    + PROPERTY_FILE);
        }
        try {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties from file "
                    + PROPERTY_FILE);
        }

        // set the URL
        if (url != null) {
            captureUrl = url;
        } else {
            captureUrl = props.getProperty(PROPERTY_CAPTURE_URL);
        }
    }

    /**
     * Captures an event given as input stream.
     */
    public String capture(final InputStream eventXml) throws IOException {
        byte[] xml = new byte[eventXml.available()];
        eventXml.read(xml);
        return capture(xml);
    }

    /**
     * Captures an event given as byte array.
     */
    public String capture(final byte[] eventXml) throws IOException {
        String event = new String(eventXml);
        return capture(event);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.captureclient.CaptureInterface#capture(java.lang.String)
     */
    public String capture(final String eventXml) throws IOException {
        byte[] data = ("event=" + eventXml).getBytes();
        return postData(data);
    }

    /**
     * Invokes the dbReset operation in the Capture Module deleting all event
     * data in the EPCIS database. This operation is only allowed if the
     * corresponding property is set in the repository's configuration.
     * 
     * @return The response from the capture module.
     * @throws IOException
     *             If a communication error occured.
     */
    public String dbReset() throws IOException {
        byte[] data = ("dbReset=true").getBytes();
        return postData(data);
    }

    /**
     * Send data to the capture service using HTTP POST.
     * 
     * @param data
     *            The data to send.
     * @return The HTTP response message
     * @throws IOException
     *             If an error on the HTTP layer occured.
     */
    private String postData(final byte[] data) throws IOException {

        // the url where the capture interface listens
        URL serviceUrl = new URL(captureUrl);

        // open an http connection
        HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();

        // post the data
        connection.setDoOutput(true);
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
     * @return The URL String at which the Capture Operations Module listens.
     */
    public String getCaptureUrl() {
        return captureUrl;
    }

    /**
     * @param url
     *            The new URL String to which this client should connect.
     */
    public void setCaptureUrl(String url) {
        this.captureUrl = url;
    }
}
