package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryClientInterface;
import org.accada.epcis.queryclient.QueryClientSoapImpl;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;

/**
 * Test for getSubscriptionID() (SE46).
 * 
 * @author Marco Steybe
 */
public class SubscriptionIDTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/requests/";
    private static final String REQUEST_1 = "Test-EPCIS10-SE46-Request-1-Subscribe.xml";
    private static final String REQUEST_2 = "Test-EPCIS10-SE46-Request-2-Subscribe.xml";

    QueryClientInterface client = new QueryClientSoapImpl();

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
        client.subscribeQuery(fis);
        fis.close();

        // subscribe the second query
        fis = new FileInputStream(PATH + REQUEST_2);
        client.subscribeQuery(fis);
        fis.close();

        // get subscription IDs
        String[] subscriptionIds = client.querySubscriptionIds();
        List<String> subscrList = Arrays.asList(subscriptionIds);
        assertTrue(subscrList.contains("QuerySE46-1"));
        assertTrue(subscrList.contains("QuerySE46-2"));
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        try {
            client.unsubscribeQuery("QuerySE46-1");
        } catch (NoSuchSubscriptionException e) {
        }
        try {
            client.unsubscribeQuery("QuerySE46-2");
        } catch (NoSuchSubscriptionException e) {
        }
        super.tearDown();
    }
}