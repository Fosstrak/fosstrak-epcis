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

import junit.framework.TestCase;

import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.queryclient.QueryControlClient;
import org.fosstrak.epcis.soap.NoSuchNameExceptionResponse;
import org.fosstrak.epcis.utils.QueryResultsComparator;
import org.fosstrak.epcis.utils.QueryResultsParser;

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

    private static QueryControlClient client = new QueryControlClient();

    /**
     * Test SE4.
     * <p>
     * FIXME: this should be a Subscribe, not a Poll!
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE4() throws Exception {
        int testNr = 4;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Test SE5.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE5() throws Exception {
        fail("Authentication not supported!");
    }

    /**
     * Test SE6.
     * <p>
     * FIXME: this should be a Subscribe, not a Poll!
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE6() throws Exception {
        int testNr = 6;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseResults(fis);
        fis.close();
        assertTrue(QueryResultsComparator.identical(expResults, actResults));
    }

    /**
     * Test SE7.
     * <p>
     * FIXME: this should be a Subscribe, not a Poll!
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE7() throws Exception {
        int testNr = 7;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("NoSuchNameException expected!");
        } catch (NoSuchNameExceptionResponse e) {
            // success
            fis.close();
        }
    }
}
