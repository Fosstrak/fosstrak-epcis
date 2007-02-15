package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.NoSuchNameException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.utils.QueryResultsParser;

/**
 * @author Marco Steybe
 */
public class ProductReceiptTest extends TestCase {

    private static final String REQ_PATH = "src/test/resources/queries/webservice/requests/";
    private static final String REQ_PREFIX = "Test-EPCIS10-SE";
    private static final String REQ_SUFFIX = "-Request.xml";
    private static final String RESP_PATH = "src/test/resources/queries/webservice/responses/";
    private static final String RESP_PREFIX = "Test-EPCIS10-SE";
    private static final String RESP_SUFFIX = "-Response.xml";

    private QueryControlClient client = new QueryControlClient();

    public void testSE4() throws IOException, ServiceException {
        int testNr = 4;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testSE5() throws IOException, ServiceException {
        fail("Authentication not supported!");
    }

    public void testSE6() throws IOException, ServiceException {
        int testNr = 6;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.poll(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testSE7() throws IOException, ServiceException {
        int testNr = 7;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        try {
            client.poll(fis);
            // fail
            fis.close();
            fail("NoSuchNameException expected!");
        } catch (NoSuchNameException e) {
            // success
            fis.close();
        }
    }
}
