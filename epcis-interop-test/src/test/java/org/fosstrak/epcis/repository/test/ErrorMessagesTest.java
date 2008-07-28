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

import org.fosstrak.epcis.queryclient.QueryControlClient;
import org.fosstrak.epcis.soap.DuplicateSubscriptionExceptionResponse;
import org.fosstrak.epcis.soap.InvalidURIExceptionResponse;
import org.fosstrak.epcis.soap.NoSuchSubscriptionExceptionResponse;
import org.fosstrak.epcis.soap.QueryParameterExceptionResponse;
import org.fosstrak.epcis.soap.SubscriptionControlsExceptionResponse;

/**
 * Tests for exceptions and error messages (SE51-SE65, SE68-SE72, SE74).
 * 
 * @author Andrea Grössbauer
 * @author Marco Steybe
 */
public class ErrorMessagesTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/requests/";

    private static QueryControlClient client = new QueryControlClient();

    /**
     * Tests if InvalidURIException is raised.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE52() throws Exception {
        final String query = "Test-EPCIS10-SE52-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            fis.close();
            client.unsubscribe("QuerySE52"); // clean up
            fail("InvalidURIException expected");
        } catch (InvalidURIExceptionResponse e) {
            fis.close();
            assertEquals("Destination URI is invalid: unknown protocol: htto", e.getMessage());
        }
    }

    /**
     * Tests if DuplicateSubscriptionException is raised.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE53() throws Exception {
        // subscribe first query
        final String query = "Test-EPCIS10-SE53-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        client.subscribe(fis);
        fis.close();

        // subscribe second query
        final String query2 = "Test-EPCIS10-SE53-Request-2-Subscribe.xml";
        fis = new FileInputStream(PATH + query2);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE53"); // clean up
            fail("DuplicateSubscriptionException expected");
        } catch (DuplicateSubscriptionExceptionResponse e) {
            fis.close();
            client.unsubscribe("QuerySE53"); // clean up
            assertEquals("SubscriptionID 'QuerySE53' already exists. Choose a different subscriptionID.", e.getMessage());
        }
    }

    /**
     * Tests if NoSuchSubscriptionException is raised.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE54() throws Exception {
        final String query = "Test-EPCIS10-SE54-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        client.subscribe(fis);
        fis.close();

        // try to unsubscribe a non existing query
        try {
            client.unsubscribe("QuerySE54-2");
            // fail
            client.unsubscribe("QuerySE54-1"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (NoSuchSubscriptionExceptionResponse e) {
            // ok
            client.unsubscribe("QuerySE54-1");
            assertEquals("There is no subscription with ID 'QuerySE54-2'.", e.getMessage());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (second value out of
     * range).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE55() throws Exception {
        final String query = "Test-EPCIS10-SE55-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE55"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The value '61' for parameter 'second' is invalid in the query schedule.", e.getMessage());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (second value out of
     * range).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE56() throws Exception {
        final String query = "Test-EPCIS10-SE56-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE56"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The value '-1' for parameter 'second' is invalid in the query schedule.", e.getMessage());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (second value invalid).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE57() throws Exception {
        final String query = "Test-EPCIS10-SE57-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE57"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The value 'a' for parameter 'second' is invalid in the query schedule.", e.getMessage());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (dayOfWeek value out of
     * range).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE58() throws Exception {
        final String query = "Test-EPCIS10-SE58-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE58"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The value '[1-8]' for parameter 'dayOfWeek' is invalid in the query schedule.", e.getMessage());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (dayOfWeek value
     * invalid).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE59() throws Exception {
        final String query = "Test-EPCIS10-SE59-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE59"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The value 'x' for parameter 'dayOfWeek' is invalid in the query schedule.", e.getMessage());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (minute value out of
     * range).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE60() throws Exception {
        final String query = "Test-EPCIS10-SE60-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE60"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The value '-1' for parameter 'minute' is invalid in the query schedule.", e.getMessage());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (minute value out of
     * range).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE61() throws Exception {
        final String query = "Test-EPCIS10-SE61-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE61"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The value '61' for parameter 'minute' is invalid in the query schedule.", e.getMessage());
        }
    }

    /**
     * Tests if SubscriptionControlsExceptionResponse is raised (minute value invalid).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE62() throws Exception {
        final String query = "Test-EPCIS10-SE62-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE62"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The value 'a' for parameter 'minute' is invalid in the query schedule.", e.getMessage());
        }
    }

    /**
     * Tests if SubscriptionControlsExceptionResponse is raised (hour value out of
     * range).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE63() throws Exception {
        final String query = "Test-EPCIS10-SE63-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE63"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The value '24' for parameter 'hour' is invalid in the query schedule.", e.getMessage());
        }
    }

    /**
     * Tests if SubscriptionControlsExceptionResponse is raised (hour value out of
     * range).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE64() throws Exception {
        final String query = "Test-EPCIS10-SE64-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE64"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The value '-1' for parameter 'hour' is invalid in the query schedule.", e.getMessage());
        }
    }

    /**
     * Tests if SubscriptionControlsExceptionResponse is raised (hour value out of
     * range).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE65() throws Exception {
        final String query = "Test-EPCIS10-SE65-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE65"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The value 'a' for parameter 'hour' is invalid in the query schedule.", e.getMessage());
        }
    }

    /**
     * Tests if QueryParameterExceptionResponse is raised (parameter name not defined).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE70() throws Exception {
        final String query = "Test-EPCIS10-SE70-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("QueryParameterExceptionResponse expected");
        } catch (QueryParameterExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The parameter EQ_abcd cannot be recognised.", e.getMessage());
        }
    }

    /**
     * Tests if QueryParameterExceptionResponse is raised (invalid parameter value).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE71() throws Exception {
        final String query = "Test-EPCIS10-SE71-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("QueryParameterExceptionResponse expected");
        } catch (QueryParameterExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("The type of the value for query parameter 'GE_quantity': 3.1459 is invalid.", e.getMessage());
        }
    }

    /**
     * Tests if QueryParameterExceptionResponse is raised (multiple occurrences of same
     * parameter).
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE72() throws Exception {
        final String query = "Test-EPCIS10-SE72-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("QueryParameterExceptionResponse expected");
        } catch (QueryParameterExceptionResponse e) {
            // ok
            fis.close();
            assertEquals("Two or more inputs are provided for the same parameter 'EQ_bizStep'.", e.getMessage());
        }
    }
}
