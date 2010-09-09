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

import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.queryclient.QueryControlClient;
import org.fosstrak.epcis.soap.QueryParameterExceptionResponse;
import org.fosstrak.epcis.soap.QueryTooLargeExceptionResponse;
import org.fosstrak.epcis.utils.QueryResultsComparator;
import org.fosstrak.epcis.utils.QueryResultsParser;

/**
 * Tests all the simple event queries from the interoperability tests (SE10-43, SE73).
 * 
 * @author Marco Steybe
 */
public class EventQueryTest extends FosstrakInteropTestCase {

    private static final String REQ_PATH = "src/test/resources/queries/webservice/requests/";
    private static final String REQ_PREFIX = "Test-EPCIS10-SE";
    private static final String REQ_SUFFIX = "-Request-1-poll.xml";
    private static final String RESP_PATH = "src/test/resources/queries/webservice/responses/";
    private static final String RESP_PREFIX = "Test-EPCIS10-SE";
    private static final String RESP_SUFFIX = "-Response-1-poll.xml";

    private static QueryControlClient client = new QueryControlClient();

    /**
     * Tests the GE_eventTime attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE10() throws Exception {
        int testNr = 10;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();
//        QueryResultsParser.queryResultsToXml(actResults, System.out);

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
//        QueryResultsParser.queryResultsToXml(expResults, System.out);
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the LT_eventTime attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE11() throws Exception {
        int testNr = 11;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the GE_recordTime attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE12() throws Exception {
        int testNr = 12;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the LT_recordTime attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE13() throws Exception {
        int testNr = 13;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the EQ_action attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE14() throws Exception {
        int testNr = 14;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the EQ_disposition attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE15() throws Exception {
        int testNr = 15;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the EQ_readPoint attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE16() throws Exception {
        int testNr = 16;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        // response received
        assertNotNull(actResults);

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the WD_readPoint attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE17() throws Exception {
        int testNr = 17;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the EQ_bizLocation attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE18() throws Exception {
        int testNr = 18;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the WD_bizLocation attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE19() throws Exception {
        int testNr = 19;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the EQ_bizTransaction attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE20() throws Exception {
        int testNr = 20;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the MATCH_epc attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE21() throws Exception {
        int testNr = 21;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the MATCH_parentID attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE22() throws Exception {
        int testNr = 22;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the MATCH_anyEPC attribute (old spec version: MATCH_childEPC).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE23() throws Exception {
        int testNr = 23;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the MATCH_epcClass attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE24() throws Exception {
        int testNr = 24;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the EQ_quantity attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE25() throws Exception {
        int testNr = 25;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the GT_quantity attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE26() throws Exception {
        int testNr = 26;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the GE_quantity attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE27() throws Exception {
        int testNr = 27;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        try {
            assertTrue(QueryResultsComparator.identical(expResults, actResults));
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the LT_quantity attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE28() throws Exception {
        int testNr = 28;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the LE_quantity attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE29() throws Exception {
        int testNr = 29;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the EQ_fieldname extension field.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE30() throws Exception {
        int testNr = 30;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the GT_fieldname attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE31() throws Exception {
        int testNr = 31;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the LT_fieldname attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE32() throws Exception {
        int testNr = 32;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the EXISTS_fieldname attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE33() throws Exception {
        int testNr = 33;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the HASATTR_fieldname positive case.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE34() throws Exception {
        int testNr = 34;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the EQATTR_fieldname_attrname attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE35() throws Exception {
        int testNr = 35;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the orderDirection. TODO test Order direction
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE36() throws Exception {
        int testNr = 36;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests a combination of attributes.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE37() throws Exception {
        int testNr = 37;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests a combination of attributes.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE38() throws Exception {
        int testNr = 38;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests the eventCountLimit. TODO assert that cases 1-5 match
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE39() throws Exception {
        int testNr = 39;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Test the maxEventCounts attribute.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE40() throws Exception {
        int testNr = 40;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        try {
            client.poll(fis);
            fail("should raise a QueryTooLargeException");
        } catch (QueryTooLargeExceptionResponse e) {
            // success
        }
        fis.close();
    }

    /**
     * Test impossible eventCount limits.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE41() throws Exception {
        int testNr = 41;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        try {
            client.poll(fis);
            fail("should raise a QueryParameterException");
        } catch (QueryParameterExceptionResponse e) {
            // success
        }
        fis.close();
    }

    /**
     * Test the OR operator of attributes.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE42() throws Exception {
        int testNr = 42;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Test the AND + OR operators of attributes.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE43() throws Exception {
        int testNr = 43;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Tests empty value.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE73() throws Exception {
        int testNr = 73;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + "-Response-1-QueryResults.xml";
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }
}
