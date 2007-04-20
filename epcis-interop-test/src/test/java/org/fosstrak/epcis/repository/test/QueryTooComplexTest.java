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
import org.accada.epcis.soapapi.QueryTooComplexException;

/**
 * Tests for QueryTooLargeException (SE49). Note 'maxQueryExecutionTime'
 * property in application.properties must be set to 0 and context must be
 * reloaded.
 * 
 * @author Marco Steybe
 */
public class QueryTooComplexTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/requests/";

    private QueryControlClient client = new QueryControlClient();

    /**
     * Reset database.
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        CaptureClient captureClient = new CaptureClient();
        captureClient.purgeRepository();
        CaptureData captureData = new CaptureData();
        captureData.captureAll();
    }

    /**
     * Tests if QueryTooComplexException is raised.
     * 
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE49() throws IOException, ServiceException {
        System.out.println("SETUP: 'maxQueryExecutionTime' property must be set to 0!");
        final String query = "Test-EPCIS10-SE49-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            fis.close();
            fail("QueryTooComplexException expected");
        } catch (QueryTooComplexException e) {
            // ok
            fis.close();
        }
    }
}
