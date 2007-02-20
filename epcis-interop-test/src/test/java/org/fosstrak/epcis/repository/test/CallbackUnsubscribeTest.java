/*
 * Copyright (c) 2006, 2007, ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the ETH Zurich nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;
import org.accada.epcis.utils.QueryCallbackListener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test for unsubscribing queries (SE44).
 * 
 * @author Marco Steybe
 */
public class CallbackUnsubscribeTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/requests/";

    private QueryControlClient client = new QueryControlClient();

    /**
     * Tests if we receive a notification for a subscribed query, and we receive
     * no further notification after the query is unsubscribed.
     * 
     * @throws IOException
     *             If an I/O excpetion occured.
     * @throws ServiceException
     *             If the EPCIS query service encountered a problem.
     * @throws ParserConfigurationException
     *             If the parser for parsing the response could not be
     *             configured.
     * @throws SAXException
     *             If the response could not be parsed.
     */
    public void testSE44() throws IOException, ServiceException,
            ParserConfigurationException, SAXException {
        final String query = "Test-EPCIS10-SE44-Request-1-Subscribe.xml";

        // subscribe a query
        InputStream fis = new FileInputStream(PATH + query);
        client.subscribe(fis);
        fis.close();

        // start subscription response listener
        QueryCallbackListener listener = QueryCallbackListener.getInstance();
        if (!listener.isRunning()) {
            listener.start();
        }
        System.out.println("waiting ...");
        synchronized (listener) {
            try {
                listener.wait(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String resp1 = listener.fetchResponse();
        assertNotNull(resp1);

        // parse response to make sure we got back a result
        Document epcis = parseResponse(resp1);
        Node eventList = epcis.getElementsByTagName("EventList").item(0);
        assertTrue(eventList.hasChildNodes());

        // unsubscribe the query and wait for any response
        client.unsubscribe("QuerySE44");
        System.out.println("waiting ...");
        synchronized (listener) {
            try {
                listener.wait(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String resp2 = listener.fetchResponse();
        assertNull("No response expected, but received: " + resp2, resp2);
        listener.stopRunning();
    }

    /**
     * Parses a string into an XML Document.
     * 
     * @param resp
     *            The string to be parsed.
     * @return The parsed XML Document.
     * @throws ParserConfigurationException
     *             If the parser could not be configured.
     * @throws SAXException
     *             If a parse error occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    private Document parseResponse(final String resp)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource xmlInput = new InputSource(new StringReader(resp));
        return builder.parse(xmlInput);
    }

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        try {
            client.unsubscribe("QuerySE44");
        } catch (NoSuchSubscriptionException e) {
        }
    }
}
