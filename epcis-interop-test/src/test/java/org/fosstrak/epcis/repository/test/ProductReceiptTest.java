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
import org.accada.epcis.soapapi.NoSuchNameException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.utils.QueryResultsParser;

/**
 * Test product receipt (SE 4-7).
 * 
 * @author Marco Steybe
 */
public class ProductReceiptTest extends TestCase {

    private static final String REQ_PATH = "src/test/resources/queries/webservice/requests/";
    private static final String REQ_PREFIX = "Test-EPCIS10-SE";
    private static final String REQ_SUFFIX = "-Request.xml";
    private static final String RESP_PATH = "src/test/resources/queries/webservice/responses/";
    private static final String RESP_PREFIX = "Test-EPCIS10-SE";
    private static final String RESP_SUFFIX = "-Response.xml";

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
     * Test SE4.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testSE4() throws IOException, ServiceException {
        int testNr = 4;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Test SE5.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testSE5() throws IOException, ServiceException {
        fail("Authentication not supported!");
    }

    /**
     * Test SE6.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testSE6() throws IOException, ServiceException {
        int testNr = 6;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Test SE7.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testSE7() throws IOException, ServiceException {
        int testNr = 7;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("NoSuchNameException expected!");
        } catch (NoSuchNameException e) {
            // success
            fis.close();
        }
    }
}
