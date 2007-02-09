package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryClientInterface;
import org.accada.epcis.queryclient.QueryClientSoapImpl;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;
import org.accada.epcis.utils.QueryCallbackListener;

/**
 * Tests for exceptions and error messages (SE49-SE65, SE68-SE72, SE74)
 * 
 * @author Andrea Grössbauer
 * @author Marco Steybe
 */
public class CallbackErrorMessagesTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/requests/";

    private QueryClientInterface client;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        client = new QueryClientSoapImpl();
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
    public void testSE68() throws RemoteException, ServiceException,
            IOException {
        // subscribe query
        final String query = "Test-EPCIS10-SE68-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + query);
        client.subscribeQuery(fis);
        fis.close();

        // start subscription response listener
        QueryCallbackListener listener = QueryCallbackListener.getInstance();
        if (!listener.isRunning()) {
            listener.start();
        }
        System.out.println("waiting ...");
        synchronized (listener) {
            try {
                listener.wait(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String resp = listener.fetchResponse();
        assertNotNull(resp);

        client.unsubscribeQuery("QuerySE68"); // clean up
        System.out.println("TODO SE68: check response: should contain QueryTooLargeException: ");
        System.out.println(resp);
        assertTrue(resp.contains("QueryTooLargeException"));
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

        // start subscription response listener
        QueryCallbackListener listener = QueryCallbackListener.getInstance();
        if (!listener.isRunning()) {
            listener.start();
        }
        System.out.println("waiting ...");
        synchronized (listener) {
            try {
                listener.wait(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String resp = listener.fetchResponse();
        assertNotNull(resp);

        client.unsubscribeQuery("QuerySE69"); // clean up
        System.out.println("TODO SE69: check response: should contain ImplementationException: ");
        System.out.println(resp);
        assertTrue(resp.contains("ImplementationException"));
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            client.unsubscribeQuery("QuerySE68");
        } catch (NoSuchSubscriptionException e) {
        }
        try {
            client.unsubscribeQuery("QuerySE69");
        } catch (NoSuchSubscriptionException e) {
        }
    }

}