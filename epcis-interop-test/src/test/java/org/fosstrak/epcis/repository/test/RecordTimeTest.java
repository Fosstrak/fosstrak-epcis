package org.accada.epcis.repository.test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryClientInterface;
import org.accada.epcis.queryclient.QueryClientSoapImpl;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.utils.QueryCallbackListener;
import org.accada.epcis.utils.QueryResultsParser;

/**
 * Test for initialRecordTime (SE66).
 * 
 * @author Marco Steybe
 */
public class RecordTimeTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/";

    QueryClientInterface client = new QueryClientSoapImpl();

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
        client.subscribeQuery(fis);
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
        client.unsubscribeQuery("QuerySE66");

        // run second query
        query = "Test-EPCIS10-SE66-Request-2-Subscribe.xml";
        fis = new FileInputStream(PATH + "requests/" + query);
        client.subscribeQuery(fis);
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

        client.unsubscribeQuery("QuerySE66");
        listener.stopRunning();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        try {
            client.unsubscribeQuery("QuerySE66");
        } catch (NoSuchSubscriptionException e) {
        }
        super.tearDown();
    }
}