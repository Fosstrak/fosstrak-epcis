/**
 *
 */
package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryClientInterface;
import org.accada.epcis.queryclient.QueryClientSoapImpl;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.soapapi.SubscribeNotPermittedException;
import org.accada.epcis.utils.QueryResultsParser;

/**
 * @author Marco Steybe
 */
public class MasterDataQueryTest extends TestCase {

    QueryClientInterface client = new QueryClientSoapImpl();

    static String REQ_PATH = "src/test/resources/queries/webservice/requests/";
    static String REQ_PREFIX = "Test-EPCIS10-MD";
    static String REQ_SUFFIX = "-Request.xml";
    static String RESP_PATH = "src/test/resources/queries/webservice/responses/";
    static String RESP_PREFIX = "Test-EPCIS10-MD";
    static String RESP_SUFFIX = "-Response.xml";

    public void testMD1() throws IOException, ServiceException {
        int testNr = 1;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testMD2() throws IOException, ServiceException {
        int testNr = 2;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testMD3() throws IOException, ServiceException {
        int testNr = 3;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testMD4() throws IOException, ServiceException {
        int testNr = 4;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testMD5() throws IOException, ServiceException {
        int testNr = 5;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testMD6() throws IOException, ServiceException {
        int testNr = 6;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testMD7() throws IOException, ServiceException {
        int testNr = 7;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testMD8() throws IOException, ServiceException {
        int testNr = 8;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);

        try {
            client.runQuery(fis);
            // fail
            fis.close();
            fail("QueryTooLargeException expected!");
        } catch (QueryTooLargeException e) {
            // ok
            fis.close();
        }
    }

    public void testMD9() throws IOException, ServiceException {
        int testNr = 9;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testMD10() throws IOException, ServiceException {
        int testNr = 10;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testMD11() throws IOException, ServiceException {
        int testNr = 11;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testMD12() throws IOException, ServiceException {
        int testNr = 12;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        QueryResultsParser.compareResults(expResults, actResults);
    }

    public void testMD13() throws IOException, ServiceException {
        int testNr = 13;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        try {
            client.subscribeQuery(fis);
            fis.close();

            fail("SubscribeNotPermittedException expected");
        } catch (SubscribeNotPermittedException e) {
            assertEquals("Subscription not allowed for SimpleMasterDataQuery.",
                    e.getReason());
        }
    }
}
