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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.utils.QueryCallbackListener;
import org.accada.epcis.utils.QueryResultsParser;

/**
 * Test for initialRecordTime (SE66).
 * 
 * @author Marco Steybe
 */
public class CallbackRecordTimeTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/";

    private QueryControlClient client = new QueryControlClient();

    /**
     * Tests if setting the initialRecordTime parameter has effect.
     * 
     * @throws Exception
     *             If an error executing the test occured.
     */
    public void testSE66() throws Exception {

        // run first query
        String query = "Test-EPCIS10-SE66-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + "requests/" + query);
        client.subscribe(fis);
        fis.close();

        // wait for response callback
        QueryCallbackListener listener = QueryCallbackListener.getInstance();
        if (!listener.isRunning()) {
            listener.start();
        }
        System.out.println("waiting ...");
        synchronized (listener) {
            try {
                listener.wait(2 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String resp1 = listener.fetchResponse();
        assertNotNull(resp1);

        // parse and compare response
        InputStream is = new ByteArrayInputStream(resp1.getBytes());
        QueryResults actResults = QueryResultsParser.parseQueryResults(is);
        is.close();
        query = "Test-EPCIS10-SE66-Response-1-2-QueryResults.xml";
        fis = new FileInputStream(PATH + "responses/" + query);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        try {
            QueryResultsParser.compareResults(expResults, actResults);
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
        client.unsubscribe("QuerySE66");

        // run second query
        query = "Test-EPCIS10-SE66-Request-2-Subscribe.xml";
        fis = new FileInputStream(PATH + "requests/" + query);
        client.subscribe(fis);
        fis.close();

        // wait for response callback
        System.out.println("waiting ...");
        synchronized (listener) {
            try {
                listener.wait(2 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String resp2 = listener.fetchResponse();
        assertNotNull(resp2);

        // parse and compare response
        is = new ByteArrayInputStream(resp2.getBytes());
        actResults = QueryResultsParser.parseQueryResults(is);
        is.close();
        query = "Test-EPCIS10-SE66-Response-1-3-QueryResults.xml";
        fis = new FileInputStream(PATH + "responses/" + query);
        expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        try {
            QueryResultsParser.compareResults(expResults, actResults);
        } catch (AssertionError e) {
            fail(e.getMessage());
        }

        client.unsubscribe("QuerySE66");
        listener.stopRunning();
    }

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        try {
            client.unsubscribe("QuerySE66");
        } catch (NoSuchSubscriptionException e) {
        }
        super.tearDown();
    }
}
