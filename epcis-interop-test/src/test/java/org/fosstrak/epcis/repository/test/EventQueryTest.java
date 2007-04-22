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
import org.accada.epcis.soapapi.QueryParameterException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.utils.QueryResultsParser;

/**
 * Tests all the simple event queries (SE10-43, SE73).
 * 
 * @author Marco Steybe
 */
public class EventQueryTest extends TestCase {

    private static final String REQ_PATH = "src/test/resources/queries/webservice/requests/";
    private static final String REQ_PREFIX = "Test-EPCIS10-SE";
    private static final String REQ_SUFFIX = "-Request-1-poll.xml";
    private static final String RESP_PATH = "src/test/resources/queries/webservice/responses/";
    private static final String RESP_PREFIX = "Test-EPCIS10-SE";
    private static final String RESP_SUFFIX = "-Response-1-poll.xml";

    private QueryControlClient client = new QueryControlClient();

    /**
     * Tests the GE_eventTime attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE10() throws IOException, ServiceException {
        int testNr = 10;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the LT_eventTime attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE11() throws IOException, ServiceException {
        int testNr = 11;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the GE_recordTime attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE12() throws IOException, ServiceException {
        int testNr = 12;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the LT_recordTime attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE13() throws IOException, ServiceException {
        int testNr = 13;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_action attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE14() throws IOException, ServiceException {
        int testNr = 14;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_disposition attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE15() throws IOException, ServiceException {
        int testNr = 15;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_readPoint attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE16() throws IOException, ServiceException {
        int testNr = 16;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        // response received
        assertNotNull(actResults);

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the WD_readPoint attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE17() throws IOException, ServiceException {
        int testNr = 17;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_bizLocation attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE18() throws IOException, ServiceException {
        int testNr = 18;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the WD_bizLocation attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE19() throws IOException, ServiceException {
        int testNr = 19;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_bizTransaction attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE20() throws IOException, ServiceException {
        int testNr = 20;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the MATCH_epc attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE21() throws IOException, ServiceException {
        int testNr = 21;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the MATCH_parentID attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE22() throws IOException, ServiceException {
        int testNr = 22;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the MATCH_childEPC attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE23() throws IOException, ServiceException {
        int testNr = 23;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the MATCH_epcClass attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE24() throws IOException, ServiceException {
        int testNr = 24;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_quantity attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE25() throws IOException, ServiceException {
        int testNr = 25;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the GT_quantity attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE26() throws IOException, ServiceException {
        int testNr = 26;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the GE_quantity attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE27() throws IOException, ServiceException {
        int testNr = 27;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        try {
            QueryResultsParser.compareResults(expResults, actResults);
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the LT_quantity attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE28() throws IOException, ServiceException {
        int testNr = 28;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the LE_quantity attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE29() throws IOException, ServiceException {
        int testNr = 29;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_fieldname extension field.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE30() throws IOException, ServiceException {
        int testNr = 30;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the GT_fieldname attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE31() throws IOException, ServiceException {
        int testNr = 31;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the LT_fieldname attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE32() throws IOException, ServiceException {
        int testNr = 32;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the EXISTS_fieldname attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE33() throws IOException, ServiceException {
        int testNr = 33;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the HASATTR_fieldname positive case.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE34() throws IOException, ServiceException {
        int testNr = 34;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the EQATTR_fieldname_attrname attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE35() throws IOException, ServiceException {
        int testNr = 35;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the orderDirection. TODO test Order direction
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE36() throws IOException, ServiceException {
        int testNr = 36;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests a combination of attributes.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE37() throws IOException, ServiceException {
        int testNr = 37;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests a combination of attributes.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE38() throws IOException, ServiceException {
        int testNr = 38;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests the eventCountLimit. TODO assert that cases 1-5 match
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE39() throws IOException, ServiceException {
        int testNr = 39;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Test the maxEventCounts attribute.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE40() throws IOException, ServiceException {
        int testNr = 40;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        try {
            client.poll(fis);
            fail("should raise a QueryTooLargeException");
        } catch (QueryTooLargeException e) {
            // success
        }
        fis.close();
    }

    /**
     * Test impossible eventCount limits.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE41() throws IOException, ServiceException {
        int testNr = 41;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        try {
            client.poll(fis);
            fail("should raise a QueryParameterException");
        } catch (QueryParameterException e) {
            // success
        }
        fis.close();
    }

    /**
     * Test the OR operator of attributes.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE42() throws IOException, ServiceException {
        int testNr = 42;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Test the AND + OR operators of attributes.
     * 
     * @throws IOException
     *             Filehandling Error
     * @throws ServiceException
     *             Something while executing the poll went wrong
     */
    public void testSE43() throws IOException, ServiceException {
        int testNr = 43;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    /**
     * Tests empty value.
     * 
     * @throws Exception
     *             Something went wrong
     */
    public void testSE73() throws Exception {
        int testNr = 73;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr
                + "-Response-1-QueryResults.xml";
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }
}
