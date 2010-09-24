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

package org.fosstrak.epcis.repository.test;

import java.io.FileInputStream;
import java.io.InputStream;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.fosstrak.epcis.queryclient.QueryControlClient;
import org.fosstrak.epcis.soap.ImplementationExceptionResponse;
import org.fosstrak.epcis.soap.NoSuchSubscriptionExceptionResponse;
import org.fosstrak.epcis.utils.QueryCallbackListener;

/**
 * Tests for an ImplementationException with severity SEVERE (SE51, SE69). In
 * order to trigger such an ImplementationException we start off with a dataset
 * that contains invalid data.
 * 
 * @author Marco Steybe
 */
public class ImplementationErrorTest extends FosstrakInteropTestCase {

    private static final String PATH = "src/test/resources/queries/webservice/requests/";
    private static final String PATH_TO_DATASET = "src/test/resources/dbunit/invalid_test_data.xml";
    private static final String DEFAULT_QUERY_URL = "http://localhost:8080/epcis-repository/query";

    private static QueryControlClient client = new QueryControlClient(DEFAULT_QUERY_URL);

    @Override
    protected IDataSet getDataSet() throws Exception {
        InputStream is = new FileInputStream(PATH_TO_DATASET);
        return new XmlDataSet(is);
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    /**
     * Tests if ImplementationException is raised.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE51() throws Exception {
        final String query = "Test-EPCIS10-SE51-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            fis.close();
            fail("ImplementationException expected");
        } catch (ImplementationExceptionResponse e) {
            // ok
            fis.close();
        }
    }

    /**
     * Tests if ImplementationException is raised (callback). TODO
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void _testSE69() throws Exception {
        // subscribe query
        final String query = "Test-EPCIS10-SE69-Request-1-Subscribe.xml";
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

        client.unsubscribe("QuerySE69"); // clean up
        assertTrue(resp.contains("ImplementationException"));
    }

    /**
     * {@inheritDoc}
     */
    protected void _tearDown() throws Exception {
        // make sure the query is unsubscribed!
        try {
            client.unsubscribe("QuerySE69");
        } catch (NoSuchSubscriptionExceptionResponse e) {
        }
    }
}
