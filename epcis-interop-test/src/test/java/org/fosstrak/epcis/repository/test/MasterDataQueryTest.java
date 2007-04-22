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
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.soapapi.SubscribeNotPermittedException;
import org.accada.epcis.utils.QueryResultsParser;

/**
 * Tests all the simple masterdata queries (MD1 - 13).
 * 
 * @author Marco Steybe
 */
public class MasterDataQueryTest extends TestCase {

    private static final String REQ_PATH = "src/test/resources/queries/webservice/requests/";
    private static final String REQ_PREFIX = "Test-EPCIS10-MD";
    private static final String REQ_SUFFIX = "-Request.xml";
    private static final String RESP_PATH = "src/test/resources/queries/webservice/responses/";
    private static final String RESP_PREFIX = "Test-EPCIS10-MD";
    private static final String RESP_SUFFIX = "-Response.xml";

    private QueryControlClient client = new QueryControlClient();

    /**
     * TEST MD1.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD1() throws IOException, ServiceException {
        int testNr = 1;
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
     * TEST MD2.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD2() throws IOException, ServiceException {
        int testNr = 2;
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
     * TEST MD3.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD3() throws IOException, ServiceException {
        int testNr = 3;
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
     * TEST MD4.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD4() throws IOException, ServiceException {
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
     * TEST MD5.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD5() throws IOException, ServiceException {
        int testNr = 5;
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
     * TEST MD6.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD6() throws IOException, ServiceException {
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
     * TEST MD7.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD7() throws IOException, ServiceException {
        int testNr = 7;
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
     * TEST MD8.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD8() throws IOException, ServiceException {
        int testNr = 8;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);

        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("QueryTooLargeException expected!");
        } catch (QueryTooLargeException e) {
            // ok
            fis.close();
        }
    }

    /**
     * TEST MD9.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD9() throws IOException, ServiceException {
        int testNr = 9;
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
     * TEST MD10.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD10() throws IOException, ServiceException {
        int testNr = 10;
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
     * TEST MD11.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD11() throws IOException, ServiceException {
        int testNr = 11;
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
     * TEST MD12.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD12() throws IOException, ServiceException {
        int testNr = 12;
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
     * TEST MD13.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testMD13() throws IOException, ServiceException {
        int testNr = 13;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        try {
            client.subscribe(fis);
            fis.close();

            fail("SubscribeNotPermittedException expected");
        } catch (SubscribeNotPermittedException e) {
            assertEquals("Subscription not allowed for SimpleMasterDataQuery.",
                    e.getReason());
        }
    }
}
