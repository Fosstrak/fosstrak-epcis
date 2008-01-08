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

package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.captureclient.CaptureClient;
import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.utils.QueryCallbackListener;

/**
 * Tests for QueryTooLargeException (SE50). Note 'maxQueryResultRows' property
 * in application.properties must be set to < 125 and context must be reloaded.
 * 
 * @author Marco Steybe
 */
public class QueryTooLargeTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/requests/";

    private QueryControlClient client = new QueryControlClient();
    private CaptureClient capture = new CaptureClient();

    /**
     * {@inheritDoc} The property 'maxQueryResultRows' determines when a
     * QueryTooLargeException is thrown. Default it is set to 125. So fill in
     * another object event such that there are more than 125.
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        // construct the object event to insert
        StringBuilder sb = new StringBuilder();
        sb.append("<epcis:EPCISDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" xmlns:epcglobal=\"urn:epcglobal:xsd:1\" xsi:schemaLocation=\"urn:epcglobal:epcis:xsd:1 EPCglobal-epcis-1_0.xsd\" xmlns:hls=\"http://schema.hls.com/extension\" creationDate=\"2006-06-25T00:00:00Z\" schemaVersion=\"1.0\">");
        sb.append("<EPCISBody>");
        sb.append("<EventList>");
        sb.append("<ObjectEvent>");
        sb.append("<eventTime>2007-04-22T22:58:00Z</eventTime>");
        sb.append("<eventTimeZoneOffset>+02:00</eventTimeZoneOffset>");
        sb.append("<epcList>");
        sb.append("<epc>urn:epc:id:sgtin:0614141.107340.1</epc>");
        sb.append("</epcList>");
        sb.append("<action>ADD</action>");
        sb.append("<bizStep>urn:epcglobal:hls:bizstep:commissioning</bizStep>");
        sb.append("<disposition>urn:epcglobal:hls:disp:active</disposition>");
        sb.append("<readPoint>");
        sb.append("<id>urn:epcglobal:fmcg:loc:0614141073467.RP-1</id>");
        sb.append("</readPoint>");
        sb.append("<bizLocation>");
        sb.append("<id>urn:epcglobal:fmcg:loc:0614141073467.1</id>");
        sb.append("</bizLocation>");
        sb.append("</ObjectEvent>");
        sb.append("</EventList>");
        sb.append("</EPCISBody>");
        sb.append("</epcis:EPCISDocument>");
        String event = sb.toString();
        capture.capture(event);
    }

    /**
     * Tests if QueryTooLargeException is raised.
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occurred.
     * @throws IOException
     *             If an I/O error occurred.
     */
    public void testSE50() throws IOException, ServiceException {
        final String query = "Test-EPCIS10-SE50-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            fis.close();
            fail("QueryTooLargeException expected");
        } catch (QueryTooLargeException e) {
            // ok
            fis.close();
        }
    }

    /**
     * Tests if QueryTooLargeException is raised (callback).
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occurred.
     * @throws IOException
     *             If an I/O error occurred.
     */
    public void testSE68() throws IOException, ServiceException {
        // subscribe query
        final String query = "Test-EPCIS10-SE68-Request-1-Subscribe.xml";
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
        String resp = listener.fetchResponse();
        assertNotNull(resp);

        client.unsubscribe("QuerySE68"); // clean up
        assertTrue(resp.contains("QueryTooLargeException"));
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception {
        capture.dbReset();
    }
}
