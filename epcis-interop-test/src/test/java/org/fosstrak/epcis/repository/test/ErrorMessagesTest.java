package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryClientInterface;
import org.accada.epcis.queryclient.QueryClientSoapImpl;
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

    private QueryClientInterface client;
    private SubscriptionNotification subscription;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        client = new QueryClientSoapImpl();
        subscription = new SubscriptionNotification();
    }

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
            client.runQuery(fis);
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
            client.runQuery(fis);
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
            client.runQuery(fis);
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
            client.subscribeQuery(fis);
            fis.close();
            client.unsubscribeQuery("QuerySE52"); // clean up
            fail("InvalidURIException expected");
        } catch (InvalidURIException e) {
            fis.close();
            assertEquals(e.getReason(),"unknown protocol: htto");
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
        client.subscribeQuery(fis);
        fis.close();

        // subscribe second query
        final String query2 = "Test-EPCIS10-SE53-Request-2-Subscribe.xml";
        fis = new FileInputStream(PATH + query2);
        try {
            client.subscribeQuery(fis);
            // fail
            fis.close();
            client.unsubscribeQuery("QuerySE53"); // clean up
            fail("DuplicateSubscriptionException expected");
        } catch (DuplicateSubscriptionException e) {
            assertEquals(e.getReason(),"QuerySE53 already exists. Choose a different subscriptionID.");
            fis.close();
            client.unsubscribeQuery("QuerySE53"); // clean up
            
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
        client.subscribeQuery(fis);
        fis.close();

        // try to unsubscribe a non existing query
        try {
            client.unsubscribeQuery("QuerySE54-2");
            // fail
            client.unsubscribeQuery("QuerySE54-1"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (NoSuchSubscriptionException e) {
            // ok
        	client.unsubscribeQuery("QuerySE54-1");
            assertEquals(e.getReason(), "There is no subscription with ID 'QuerySE54-2'");
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
            client.subscribeQuery(fis);
            // fail
            fis.close();
            client.unsubscribeQuery("QuerySE55"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
        	assertEquals(e.getReason(), "The value for 'second' is out of range in the query schedule.");
            fis.close();
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
            client.subscribeQuery(fis);
            // fail
            fis.close();
            client.unsubscribeQuery("QuerySE56"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
        	assertEquals(e.getReason(), "The value for 'second' is out of range in the query schedule.");
            fis.close();
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
            client.subscribeQuery(fis);
            // fail
            fis.close();
            client.unsubscribeQuery("QuerySE57"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
        	assertEquals(e.getReason(), "The value 'a' for parameter 'second' is invalid in the query schedule.");
            fis.close();
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
            client.subscribeQuery(fis);
            // fail
            fis.close();
            client.unsubscribeQuery("QuerySE58"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
        	assertEquals(e.getReason(), "The value for 'DayOfWeek' is out of range in the query schedule.");
            fis.close();
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
            client.subscribeQuery(fis);
            // fail
            fis.close();
            client.unsubscribeQuery("QuerySE59"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
        	assertEquals(e.getReason(), "The value 'x' for parameter 'DayOfWeek' is invalid in the query schedule.");
            fis.close();
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
            client.subscribeQuery(fis);
            // fail
            fis.close();
            client.unsubscribeQuery("QuerySE60"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
        	assertEquals(e.getReason(), "The value for 'minute' is out of range in the query schedule.");
            fis.close();
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
            client.subscribeQuery(fis);
            // fail
            fis.close();
            client.unsubscribeQuery("QuerySE61"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
        	assertEquals(e.getReason(), "The value for 'minute' is out of range in the query schedule.");
            fis.close();
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
            client.subscribeQuery(fis);
            // fail
            fis.close();
            client.unsubscribeQuery("QuerySE62"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
        	assertEquals(e.getReason(), "The value 'a' for parameter 'minute' is invalid in the query schedule.");
            fis.close();
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
            client.subscribeQuery(fis);
            // fail
            fis.close();
            client.unsubscribeQuery("QuerySE63"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
        	assertEquals(e.getReason(), "The value for 'hour' is out of range in the query schedule.");
            fis.close();
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
            client.subscribeQuery(fis);
            // fail
            fis.close();
            client.unsubscribeQuery("QuerySE64"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
        	assertEquals(e.getReason(), "The value for 'hour' is out of range in the query schedule.");
            fis.close();
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
            client.subscribeQuery(fis);
            // fail
            fis.close();
            client.unsubscribeQuery("QuerySE65"); // clean up
            fail("NoSuchSubscriptionException expected");
        } catch (SubscriptionControlsException e) {
            // ok
        	assertEquals(e.getReason(), "The value 'a' for parameter 'hour' is invalid in the query schedule.");
            fis.close();
        }
    }

    /**
     * Tests if QueryTooLargeException is raised (callback).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE68() throws RemoteException, ServiceException, IOException {
        // subscribe query
        final String query = "Test-EPCIS10-SE68-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        client.subscribeQuery(fis);
        fis.close();
        
        // wait for response (1 min)
        String resp = subscription.waitForNotification(120 * 1000 + 1);
        client.unsubscribeQuery("QuerySE68"); // clean up
        System.out.println("TODO SE68: check response: should contain QueryTooLargeException: ");
        System.out.println(resp);
        assertNotNull(resp);
    }

    /**
     * Tests if ImplementationException is raised (callback).
     * 
     * @throws RemoteException
     *             If an Axis error occured.
     * @throws ServiceException
     *             If an error in the EPCIS query service occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    public void testSE69() throws RemoteException, ServiceException,
            IOException {
        // subscribe query
        final String query = "Test-EPCIS10-SE69-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        
        client.subscribeQuery(fis);   
        fis.close();

        // wait for response (1 min)
        String resp = subscription.waitForNotification(60 * 1000 + 2);
        client.unsubscribeQuery("QuerySE69"); // clean up
        System.out.println("TODO SE69: check response: should contain ImplementationException: ");
        System.out.println(resp);

        assertNotNull(resp);
        
        
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
            client.runQuery(fis);
            // fail
            fis.close();
            fail("QueryParameterException expected");
        } catch (QueryParameterException e) {
            // ok
            fis.close();
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
            client.runQuery(fis);
            // fail
            fis.close();
            fail("QueryParameterException expected");
        } catch (QueryParameterException e) {
            // ok
            fis.close();
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
            client.runQuery(fis);
            // fail
            fis.close();
            fail("QueryParameterException expected");
        } catch (QueryParameterException e) {
            // ok
            fis.close();
        }
    }

    /**
     * Tests if SecurityException is raised.
     */
    public void testSE74() {
        fail("No security implemented!");
    }

}