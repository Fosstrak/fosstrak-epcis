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

package org.accada.epcis.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * This class implements a simple web server listening for responses from the
 * EPCIS Query Callback interface. The server is not multi-threaded, so it will
 * only accept one request at a time. It will only allow one instance
 * (singleton) and will be bound to a predefined port on localhost.
 * 
 * @author Marco Steybe
 */
public final class QueryCallbackListener extends Thread {

    private static final int PORT = 8899;

    private static QueryCallbackListener instance = null;

    private ServerSocket server = null;

    private boolean isRunning = false;

    private String response = null;

    /**
     * Instantiates a new SubscriptionResponseListener listening on the given
     * port.
     * 
     * @throws IOException
     *             If an error setting up the communication socket occurred.
     */
    private QueryCallbackListener() throws IOException {
        System.out.println("listening for query callbacks on port " + PORT + " ...");
        server = new ServerSocket(PORT);
    }

    /**
     * @return The only instance of this class (singleton).
     * @throws IOException
     *             If an error setting up the communication socket occurred.
     */
    public static QueryCallbackListener getInstance() throws IOException {
        if (instance == null) {
            instance = new QueryCallbackListener();
        }
        return instance;
    }

    /**
     * Keeps this listener running until {@link #stopRunning()} is called.
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
        isRunning = true;
        while (isRunning) {
            Socket client = null;
            try {
                client = server.accept();
                handleConnection(client);
            } catch (SocketException e) {
                // server socket closed (stopRunning was called)
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Handles an incoming HTTP connection, reading the contents, and parsing it
     * as XML.
     * 
     * @param client
     *            The client Socket.
     * @throws IOException
     *             If an I/O error occurred.
     */
    private void handleConnection(final Socket client) throws IOException {
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        InputStream is = client.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        // read content length
        String prefix = "content-length: ";
        String inputLine = in.readLine().toLowerCase();
        while (!inputLine.startsWith(prefix)) {
            // continue reading ...
            inputLine = in.readLine().toLowerCase();
        }

        // parse content length
        String length = inputLine.substring(prefix.length());
        int len = Integer.parseInt(length);

        inputLine = in.readLine();
        while (!inputLine.equals("")) {
            // continue reading ...
            inputLine = in.readLine();
        }

        // read, decode, and parse xml content (UTF-8 encoded!)
        byte[] xml = new byte[len];
        is.read(xml);
        ByteBuffer buf = ByteBuffer.wrap(xml);
        Charset charset = Charset.forName("UTF-8");
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = decoder.decode(buf);
        parseResponse(charBuffer.toString().trim());

        // write response
        out.write("HTTP/1.0 200 OK\n\n");
        out.flush();

        // notify everyone waiting on us
        synchronized (this) {
            this.notifyAll();
        }

        out.close();
        in.close();
    }

    /**
     * Extracts the XML contents from the given String.
     * 
     * @param resp
     *            The response from which the XML contents should be extracted.
     */
    private void parseResponse(final String resp) {
        if (resp.startsWith("<?xml")) {
            // remove xml declaration
            int index = resp.indexOf("?>") + 2;
            if (index >= 0) {
                response = resp.substring(index).trim();
            }
        }
    }

    /**
     * @return The received XML response.
     */
    public String fetchResponse() {
        String resp = this.response;
        this.response = null; // reset
        return resp;
    }

    /**
     * @return Wheter this thread is running.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Stops this thread from running.
     */
    public void stopRunning() {
        isRunning = false;
        instance = null;
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
