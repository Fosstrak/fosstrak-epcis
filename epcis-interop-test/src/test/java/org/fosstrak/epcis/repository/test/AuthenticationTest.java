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

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.utils.QueryResultsParser;

/**
 * @author Marco Steybe
 */
public class AuthenticationTest extends TestCase {

    private static final String REQ_PATH = "src/test/resources/queries/webservice/requests/";
    private static final String REQ_PREFIX = "Test-EPCIS10-SE";
    private static final String REQ_SUFFIX = "-Request.xml";
    private static final String RESP_PATH = "src/test/resources/queries/webservice/responses/";
    private static final String RESP_PREFIX = "Test-EPCIS10-SE";
    private static final String RESP_SUFFIX = "-Response.xml";

    private QueryControlClient client = new QueryControlClient();

    /**
     * Test SE1.
     * 
     * @throws IOException
     *             IF an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testSE1() throws IOException, ServiceException {
        int testNr = 1;
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
     * Test SE2.
     * 
     * @throws IOException
     *             IF an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testSE2() throws IOException, ServiceException {
        fail("Authentication not supported!");
    }

    /**
     * Test SE3.
     * 
     * @throws IOException
     *             IF an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testSE3() throws IOException, ServiceException {
        int testNr = 3;
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
}
