package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.DuplicateSubscriptionException;
import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.InvalidURIException;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;
import org.accada.epcis.soapapi.QueryParameterException;
import org.accada.epcis.soapapi.QueryTooComplexException;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.soapapi.SubscriptionControlsException;

/**
 * Tests for exceptions and error messages (SE49-SE65, SE68-SE72, SE74)
 * 
 * @author Andrea Grössbauer
 * @author Marco Steybe
 */
public class ErrorMessagesTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/requests/";

    private QueryControlClient client = new QueryControlClient();

    /**
     * Tests if QueryTooComplexException is raised.
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE49() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE49-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            fis.close();
            fail("QueryTooComplexException expected");
        } catch (QueryTooComplexException e) {
            // ok
            fis.close();
        }
    }

    /**
     * Tests if QueryTooLargeException is raised.
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE50() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE50-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            fis.close();
            fail("QueryTooLargeException expected");
        } catch (QueryTooLargeException e) {
            // ok
            fis.close();
        }
    }

    /**
     * Tests if ImplementationException is raised.
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE51() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE51-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            fis.close();
            fail("ImplementationException expected");
        } catch (ImplementationException e) {
            // ok
            fis.close();
        }
    }

    /**
     * Tests if InvalidURIException is raised.
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE52() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE52-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            fis.close();
            client.unsubscribe("QuerySE52"); // clean up
            fail("InvalidURIException expected");
        } catch (InvalidURIException e) {
            fis.close();
            assertEquals("Destination URI is invalid: unknown protocol: htto",
                    e.getReason());
        }
    }

    /**
     * Tests if DuplicateSubscriptionException is raised.
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE53() throws RemoteException, ServiceException,
            IOException {
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
        } catch (DuplicateSubscriptionException e) {
            fis.close();
            client.unsubscribe("QuerySE53"); // clean up
            assertEquals(
                    "SubscriptionID 'QuerySE53' already exists. Choose a different subscriptionID.",
                    e.getReason());
        }
    }

    /**
     * Tests if NoSuchSubscriptionException is raised.
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE54() throws RemoteException, ServiceException,
            IOException {
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
        } catch (NoSuchSubscriptionException e) {
            // ok
            client.unsubscribe("QuerySE54-1");
            assertEquals("There is no subscription with ID 'QuerySE54-2'.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (second value out of
     * range).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE55() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE55-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE55"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value for 'second' is out of range in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (second value out of
     * range).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE56() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE56-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE56"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value for 'second' is out of range in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (second value invalid).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE57() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE57-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE57"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value 'a' for parameter 'second' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (dayOfWeek value out of
     * range).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE58() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE58-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE58"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value for 'DayOfWeek' is out of range in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (dayOfWeek value
     * invalid).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE59() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE59-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE59"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value 'x' for parameter 'DayOfWeek' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (minute value out of
     * range).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE60() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE60-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE60"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value for 'minute' is out of range in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (minute value out of
     * range).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE61() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE61-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE61"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value for 'minute' is out of range in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (minute value invalid).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE62() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE62-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE62"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value 'a' for parameter 'minute' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (hour value out of
     * range).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE63() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE63-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE63"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value for 'hour' is out of range in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (hour value out of
     * range).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE64() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE64-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE64"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value for 'hour' is out of range in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if SubscriptionControlsException is raised (hour value out of
     * range).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE65() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE65-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.subscribe(fis);
            // fail
            fis.close();
            client.unsubscribe("QuerySE65"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
            fis.close();
            assertEquals(
                    "The value 'a' for parameter 'hour' is invalid in the query schedule.",
                    e.getReason());
        }
    }

    /**
     * Tests if QueryParameterException is raised (parameter name not defined).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE70() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE70-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("QueryParameterException expected");
        } catch (QueryParameterException e) {
            // ok
            fis.close();
            assertEquals("The parameter EQ_abcd cannot be recognised.",
                    e.getReason());
        }
    }

    /**
     * Tests if QueryParameterException is raised (invalid parameter value).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE71() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE71-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("QueryParameterException expected");
        } catch (QueryParameterException e) {
            // ok
            fis.close();
            assertEquals(
                    "The input value for parameter GE_quantity (3.1459) of eventType QuantityEvent is not of the type required.",
                    e.getReason());
        }
    }

    /**
     * Tests if QueryParameterException is raised (multiple occurences of same
     * parameter).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE72() throws RemoteException, ServiceException,
            IOException {
        final String query = "Test-EPCIS10-SE72-Request-1-Poll.xml";
        InputStream fis = new FileInputStream(PATH + query);
        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("QueryParameterException expected");
        } catch (QueryParameterException e) {
            // ok
            fis.close();
            assertEquals(
                    "Two or more inputs are provided for the same parameter 'EQ_bizStep'.",
                    e.getReason());
        }
    }

}