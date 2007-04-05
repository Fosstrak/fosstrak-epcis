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

import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;
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

    /**
     * No testing, just make sure that the 'maxQueryResultRows' property is set
     * to < 125!
     */
    public void testSetup() {
        // the property 'maxQueryResultRows' determines when a
        // QueryTooLargeException is thrown. For the other tests we need all
        // events, for ObjectEvent that is 125 events. In order for this test
        // to succeed we need to set the 'maxQueryResultRows' to less than 125
        System.out.println("SETUP: 'maxQueryResultRows' property must be set to < 125!");
    }

    /**
     * Tests if QueryTooLargeException is raised.
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
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
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
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
        // make sure the query is unsubscribed!
        try {
            client.unsubscribe("QuerySE68");
        } catch (NoSuchSubscriptionException e) {
        }
    }
}
