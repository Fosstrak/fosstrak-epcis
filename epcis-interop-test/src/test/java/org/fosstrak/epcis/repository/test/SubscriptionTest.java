package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryClientInterface;
import org.accada.epcis.queryclient.QueryClientSoapImpl;
import org.accada.epcis.soapapi.QueryResults;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * TestSuite for Errors SE49 - SE74.
 * 
 * @author Andrea Grössbauer
 */
public class SubscriptionTest extends TestCase {

    private static final Logger LOG = Logger.getLogger(ErrorMessagesTest.class);

    QueryClientInterface client;

    static String pathToQueries = "src/test/resources/queries/webservice/requests/";
    static String testPrefix = "Test-EPCIS10-SE";
    static String pathToResp = "src/test/resources/queries/webservice/responses/";
    static String req = "-Request-";
    static String resp = "-Response-";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        PropertyConfigurator.configure("src/test/resources/conf/log4j.properties");
        client = new QueryClientSoapImpl();
    }

    public void testSE44() throws IOException, ServiceException {
        int testNr = 44;

        // subscribe a query
        String subscr = pathToQueries + testPrefix + testNr + req
                + "1-Subscribe.xml";
        LOG.info("query taken from " + subscr);
        InputStream fis = new FileInputStream(subscr);
        client.subscribeQuery(fis);
        fis.close();

        // wait for 1 minute
        try {
            Thread.sleep(1000 * 60);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // get response
        // TODO

        // unsubscribe the query
        String unsubscr = pathToQueries + testPrefix + testNr + req
                + "2-Unsubscribe.xml";
        LOG.info("query taken from " + unsubscr);
        fis = new FileInputStream(unsubscr);
        client.subscribeQuery(fis);
        // Andrea: Marco nicht unsubscribe ?
        fis.close();

        // wait for 1 minute
        try {
            Thread.sleep(1000 * 60);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // get response
        // TODO

        // TODO compare expected and actual results
        String resp1 = pathToResp + testPrefix + testNr + resp
                + "1-SubscribeResult.xml";
        LOG.info("response taken from " + resp1);
        fis = new FileInputStream(resp1);
        QueryResults expResults1 = ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();

        String resp2 = pathToResp + testPrefix + testNr + resp
                + "2-QueryResults.xml";
        LOG.info("response taken from " + resp2);
        fis = new FileInputStream(resp2);
        QueryResults expResults2 = ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();

        String resp3 = pathToResp + testPrefix + testNr + resp
                + "UnsubscribeResult.xml";
        LOG.info("response taken from " + resp3);
        fis = new FileInputStream(resp3);
        QueryResults expResults3 = ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();

    }

}