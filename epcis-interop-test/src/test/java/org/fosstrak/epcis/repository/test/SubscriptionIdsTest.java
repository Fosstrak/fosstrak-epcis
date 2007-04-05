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
