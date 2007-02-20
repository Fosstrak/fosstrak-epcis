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
import java.util.List;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;

/**
 * Test for getSubscriptionID() (SE46).
 * 
 * @author Marco Steybe
 */
public class SubscriptionIdsTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/requests/";
    private static final String REQUEST_1 = "Test-EPCIS10-SE46-Request-1-Subscribe.xml";
    private static final String REQUEST_2 = "Test-EPCIS10-SE46-Request-2-Subscribe.xml";

    private QueryControlClient client = new QueryControlClient();

    /**
     * Tests if the getSubscriptionIDs() function returns the correct values for
     * two subscribed queries.
     * 
     * @throws IOException
     *             If a problem reading the query xml occured.
     * @throws ServiceException
     *             If an EPCIS query service error occured.
     */
    public void testSE46() throws IOException, ServiceException {

        // subscribe the first query
        InputStream fis = new FileInputStream(PATH + REQUEST_1);
        client.subscribe(fis);
        fis.close();

        // subscribe the second query
        fis = new FileInputStream(PATH + REQUEST_2);
        client.subscribe(fis);
        fis.close();

        // get subscription IDs
        List<String> subscriptionIds = client.getSubscriptionIds("dummy");
        assertTrue(subscriptionIds.contains("QuerySE46-1"));
        assertTrue(subscriptionIds.contains("QuerySE46-2"));
    }

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        try {
            client.unsubscribe("QuerySE46-1");
        } catch (NoSuchSubscriptionException e) {
        }
        try {
            client.unsubscribe("QuerySE46-2");
        } catch (NoSuchSubscriptionException e) {
        }
        super.tearDown();
    }
}
