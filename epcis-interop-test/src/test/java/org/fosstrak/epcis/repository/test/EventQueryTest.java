package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryClientInterface;
import org.accada.epcis.queryclient.QueryClientSoapImpl;
import org.accada.epcis.soapapi.AggregationEventType;
import org.accada.epcis.soapapi.BusinessTransactionType;
import org.accada.epcis.soapapi.EPC;
import org.accada.epcis.soapapi.EventListType;
import org.accada.epcis.soapapi.ObjectEventType;
import org.accada.epcis.soapapi.QuantityEventType;
import org.accada.epcis.soapapi.QueryParameterException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryResultsBody;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.soapapi.TransactionEventType;
import org.accada.epcis.utils.QueryResultsParser;
import org.apache.axis.message.MessageElement;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author Marco Steybe
 */
public class EventQueryTest extends TestCase {

    private static final String REQ_PATH = "src/test/resources/queries/webservice/requests/";
    private static final String REQ_PREFIX = "Test-EPCIS10-SE";
    private static final String REQ_SUFFIX = "-Request-1-poll.xml";
    private static final String RESP_PATH = "src/test/resources/queries/webservice/responses/";
    private static final String RESP_PREFIX = "Test-EPCIS10-SE";
    private static final String RESP_SUFFIX = "-Response-1-poll.xml";

    QueryClientInterface client;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        PropertyConfigurator.configure("src/test/resources/conf/log4j.properties");
        client = new QueryClientSoapImpl();
    }
    
    /**
     * Tests the GE_eventTime attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE10() throws IOException, ServiceException {
        int testNr = 10;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the LT_eventTime attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE11() throws IOException, ServiceException {
        int testNr = 11;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the GE_recordTime attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE12() throws IOException, ServiceException {
        int testNr = 12;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the LT_recordTime attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE13() throws IOException, ServiceException {
        int testNr = 13;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_action attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE14() throws IOException, ServiceException {
        int testNr = 14;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_disposition attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE15() throws IOException, ServiceException {
        int testNr = 15;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_readPoint attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE16() throws IOException, ServiceException {
        int testNr = 16;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();
        
        // response received
        assertNotNull(actResults);
        
        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    
    /**
     * Tests the WD_readPoint attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE17() throws IOException, ServiceException {
        int testNr = 17;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_bizLocation attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE18() throws IOException, ServiceException {
        int testNr = 18;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the WD_bizLocation attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE19() throws IOException, ServiceException {
        int testNr = 19;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_bizTransaction attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE20() throws IOException, ServiceException {
        int testNr = 20;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the MATCH_epc attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE21() throws IOException, ServiceException {
        int testNr = 21;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the MATCH_parentID attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE22() throws IOException, ServiceException {
        int testNr = 22;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the MATCH_childEPC attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE23() throws IOException, ServiceException {
        int testNr = 23;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the MATCH_epcClass attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE24() throws IOException, ServiceException {
        int testNr = 24;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_quantity attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE25() throws IOException, ServiceException {
        int testNr = 25;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the GT_quantity attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE26() throws IOException, ServiceException {
        int testNr = 26;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the GE_quantity attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE27() throws IOException, ServiceException {
        int testNr = 27;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the LT_quantity attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE28() throws IOException, ServiceException {
        int testNr = 28;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the LE_quantity attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE29() throws IOException, ServiceException {
        int testNr = 29;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the EQ_fieldname extension field.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE30() throws IOException, ServiceException {
        int testNr = 30;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the GT_fieldname attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE31() throws IOException, ServiceException {
        int testNr = 31;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the LT_fieldname attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE32() throws IOException, ServiceException {
        int testNr = 32;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    
    /**
     * Tests the EXISTS_fieldname attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE33() throws IOException, ServiceException {
        int testNr = 33;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the HASATTR_fieldname positive case.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE34() throws IOException, ServiceException {
        int testNr = 34;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the EQATTR_fieldname_attrname attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE35() throws IOException, ServiceException {
        int testNr = 35;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the orderDirection.
     * TODO test Order direction
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE36() throws IOException, ServiceException {
        int testNr = 36;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests a combination of attributes.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE37() throws IOException, ServiceException {
        int testNr = 37;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests a combination of attributes.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE38() throws IOException, ServiceException {
        int testNr = 38;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests the eventCountLimit.
     * TODO assert that cases 1-5 match
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE39() throws IOException, ServiceException {
        int testNr = 39;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Test the maxEventCounts attribute.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE40() throws IOException, ServiceException {
        int testNr = 40;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        try {
            client.runQuery(fis);
            fail("should raise a QueryTooLargeException");
        } catch (QueryTooLargeException e) {
            // success
        }
        fis.close();
    }

    /**
     * Test impossible eventCount limits.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE41() throws IOException, ServiceException {
        int testNr = 41;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        try {
            client.runQuery(fis);
            fail("should raise a QueryParameterException");
        } catch (QueryParameterException e) {
            // success
        }
        fis.close();
    }

    /**
     * Test the OR operator of attributes.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE42() throws IOException, ServiceException {
        int testNr = 42;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Test the AND + OR operators of attributes.
     * 
     * @throws IOException
     * 			Filehandling Error
     * @throws ServiceException
     * 			Something while executing the poll went wrong
     */
    public void testSE43() throws IOException, ServiceException {
        int testNr = 43;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr + RESP_SUFFIX;
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    /**
     * Tests empty value
     * 
     * @throws Exception
     * 		Something went wrong
     */
    public void testSE73() throws Exception {
        int testNr = 73;
        String query = REQ_PATH + REQ_PREFIX + testNr + REQ_SUFFIX;
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = RESP_PATH + RESP_PREFIX + testNr
                + "-Response-1-QueryResults.xml";
        fis = new FileInputStream(resp);
        QueryResults expResults = QueryResultsParser.parseQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    private void compareResults(QueryResults expResults, QueryResults actResults) {
        assertEquals(expResults.get_any(), actResults.get_any());
        assertEquals(expResults.getExtension(), actResults.getExtension());
        assertEquals(expResults.getQueryName(), actResults.getQueryName());
        assertEquals(expResults.getSubscriptionID(),
                actResults.getSubscriptionID());

        EventListType actEvents = actResults.getResultsBody().getEventList();
        EventListType expEvents = expResults.getResultsBody().getEventList();

        // compare ObjectEvent
        ObjectEventType[] actObjectEvent = actEvents.getObjectEvent();
        ObjectEventType[] expObjectEvent = expEvents.getObjectEvent();

        assertEquals(expObjectEvent == null, actObjectEvent == null);
        if (actObjectEvent != null) {
            assertEquals(expObjectEvent.length, actObjectEvent.length);
            for (int i = 0; i < actObjectEvent.length; i++) {
                assertEquals(expObjectEvent[i].getAction(),
                        actObjectEvent[i].getAction());
                assertEquals(expObjectEvent[i].getBaseExtension(),
                        actObjectEvent[i].getBaseExtension());
                assertEquals(expObjectEvent[i].getBizLocation(),
                        actObjectEvent[i].getBizLocation());
                assertEquals(expObjectEvent[i].getBizStep(),
                        actObjectEvent[i].getBizStep());
                assertEquals(expObjectEvent[i].getDisposition(),
                        actObjectEvent[i].getDisposition());
                assertEquals(expObjectEvent[i].getEventTime().compareTo(
                        actObjectEvent[i].getEventTime()), 0);
                assertEquals(expObjectEvent[i].getExtension(),
                        actObjectEvent[i].getExtension());
                assertEquals(expObjectEvent[i].getReadPoint(),
                        actObjectEvent[i].getReadPoint());
                // assertEquals(expObjectEvent[i].getRecordTime(),
                // actObjectEvent[i].getRecordTime());

                MessageElement[] actME = actObjectEvent[i].get_any();
                MessageElement[] expME = expObjectEvent[i].get_any();
                assertEquals(expME.length, actME.length);
                for (int j = 0; j < actME.length; j++) {
                    assertEquals(expME[j].getValue(), actME[j].getValue());
                    assertEquals(expME[j].getNamespaceURI(),
                            actME[j].getNamespaceURI());
                    assertEquals(expME[j].getPrefix(), actME[j].getPrefix());
                    assertEquals(expME[j].getLocalName(),
                            actME[j].getLocalName());
                }

                EPC[] actEpcs = actObjectEvent[i].getEpcList();
                EPC[] expEpcs = expObjectEvent[i].getEpcList();
                assertEquals(expEpcs.length, actEpcs.length);
                for (int j = 0; j < actEpcs.length; j++) {
                    assertEquals(expEpcs[j].get_value(), actEpcs[j].get_value());
                }

                BusinessTransactionType[] actBizTrans = actObjectEvent[i].getBizTransactionList();
                BusinessTransactionType[] expBizTrans = expObjectEvent[i].getBizTransactionList();
                assertEquals(expBizTrans.length, actBizTrans.length);
                for (int j = 0; j < actBizTrans.length; j++) {
                    assertEquals(expBizTrans[j].getType(),
                            actBizTrans[j].getType());
                    // assertEquals(expBizTrans[j].getValue(),
                    // actBizTrans[j].getValue());
                }
            }
        }

        // compare AggregationEvent
        AggregationEventType[] actAggrEvent = actEvents.getAggregationEvent();
        AggregationEventType[] expAggrEvent = expEvents.getAggregationEvent();

        assertEquals(expAggrEvent == null, actAggrEvent == null);
        if (actAggrEvent != null) {
            assertEquals(expAggrEvent.length, actAggrEvent.length);
            for (int i = 0; i < actAggrEvent.length; i++) {
                assertEquals(expAggrEvent[i].get_any(),
                        actAggrEvent[i].get_any());
                assertEquals(expAggrEvent[i].getAction(),
                        actAggrEvent[i].getAction());
                assertEquals(expAggrEvent[i].getBaseExtension(),
                        actAggrEvent[i].getBaseExtension());
                assertEquals(expAggrEvent[i].getBizLocation(),
                        actAggrEvent[i].getBizLocation());
                assertEquals(expAggrEvent[i].getBizStep(),
                        actAggrEvent[i].getBizStep());
                assertEquals(expAggrEvent[i].getDisposition(),
                        actAggrEvent[i].getDisposition());
                assertEquals(expAggrEvent[i].getEventTime().compareTo(
                        actAggrEvent[i].getEventTime()), 0);
                assertEquals(expAggrEvent[i].getExtension(),
                        actAggrEvent[i].getExtension());
                assertEquals(expAggrEvent[i].getParentID(),
                        actAggrEvent[i].getParentID());
                assertEquals(expAggrEvent[i].getReadPoint(),
                        actAggrEvent[i].getReadPoint());
                // assertEquals(expObjectEvent[i].getRecordTime(),
                // actObjectEvent[i].getRecordTime());

                EPC[] actEpcs = actAggrEvent[i].getChildEPCs();
                EPC[] expEpcs = expAggrEvent[i].getChildEPCs();
                assertEquals(expEpcs.length, actEpcs.length);
                for (int j = 0; j < actEpcs.length; j++) {
                    assertEquals(expEpcs[j].get_value(), actEpcs[j].get_value());
                }

                BusinessTransactionType[] actBizTrans = actAggrEvent[i].getBizTransactionList();
                BusinessTransactionType[] expBizTrans = expAggrEvent[i].getBizTransactionList();
                assertEquals(actBizTrans.length, expBizTrans.length);
                for (int j = 0; j < actBizTrans.length; j++) {
                    assertEquals(expBizTrans[j].getType(),
                            actBizTrans[j].getType());
                    // assertEquals(expBizTrans[j].getValue(),
                    // actBizTrans[j].getValue());
                }
            }
        }

        // compare TransactionEvent
        TransactionEventType[] actTransEvent = actEvents.getTransactionEvent();
        TransactionEventType[] expTransEvent = expEvents.getTransactionEvent();

        assertEquals(expTransEvent == null, actTransEvent == null);
        if (actTransEvent != null) {
            assertEquals(expTransEvent.length, actTransEvent.length);
            for (int i = 0; i < actTransEvent.length; i++) {
                assertEquals(expTransEvent[i].get_any(),
                        actTransEvent[i].get_any());
                assertEquals(expTransEvent[i].getAction(),
                        actTransEvent[i].getAction());
                assertEquals(expTransEvent[i].getBaseExtension(),
                        actTransEvent[i].getBaseExtension());
                assertEquals(expTransEvent[i].getBizLocation(),
                        actTransEvent[i].getBizLocation());
                assertEquals(expTransEvent[i].getBizStep(),
                        actTransEvent[i].getBizStep());
                assertEquals(expTransEvent[i].getDisposition(),
                        actTransEvent[i].getDisposition());
                assertEquals(expTransEvent[i].getEventTime().compareTo(
                        actTransEvent[i].getEventTime()), 0);
                assertEquals(expTransEvent[i].getExtension(),
                        actTransEvent[i].getExtension());
                assertEquals(expTransEvent[i].getParentID(),
                        actTransEvent[i].getParentID());
                assertEquals(expTransEvent[i].getReadPoint(),
                        actTransEvent[i].getReadPoint());
                // assertEquals(expTransEvent[i].getRecordTime(),
                // actTransEvent[i].getRecordTime());

                EPC[] actEpcs = actTransEvent[i].getEpcList();
                EPC[] expEpcs = expTransEvent[i].getEpcList();
                assertEquals(expEpcs.length, actEpcs.length);
                for (int j = 0; j < actEpcs.length; j++) {
                    assertEquals(expEpcs[j].get_value(), actEpcs[j].get_value());
                }

                BusinessTransactionType[] actBizTrans = actTransEvent[i].getBizTransactionList();
                BusinessTransactionType[] expBizTrans = expTransEvent[i].getBizTransactionList();
                assertEquals(actBizTrans.length, expBizTrans.length);
                for (int j = 0; j < actBizTrans.length; j++) {
                    assertEquals(expBizTrans[j].getType(),
                            actBizTrans[j].getType());
                    // assertEquals(expBizTrans[j].getValue(),
                    // actBizTrans[j].getValue());
                }
            }
        }

        // compare QuantityEvent
        QuantityEventType[] actQuantEvent = actEvents.getQuantityEvent();
        QuantityEventType[] expQuantEvent = expEvents.getQuantityEvent();

        assertEquals(expQuantEvent == null, actQuantEvent == null);
        if (actQuantEvent != null) {
            assertEquals(expQuantEvent.length, actQuantEvent.length);
            for (int i = 0; i < actQuantEvent.length; i++) {
                assertEquals(expQuantEvent[i].get_any(),
                        actQuantEvent[i].get_any());
                assertEquals(expQuantEvent[i].getBaseExtension(),
                        actQuantEvent[i].getBaseExtension());
                assertEquals(expQuantEvent[i].getBizLocation(),
                        actQuantEvent[i].getBizLocation());
                assertEquals(expQuantEvent[i].getBizStep(),
                        actQuantEvent[i].getBizStep());
                assertEquals(expQuantEvent[i].getDisposition(),
                        actQuantEvent[i].getDisposition());
                assertEquals(expQuantEvent[i].getEventTime().compareTo(
                        actQuantEvent[i].getEventTime()), 0);
                assertEquals(expQuantEvent[i].getEpcClass(),
                        actQuantEvent[i].getEpcClass());
                assertEquals(expQuantEvent[i].getExtension(),
                        actQuantEvent[i].getExtension());
                assertEquals(expQuantEvent[i].getQuantity(),
                        actQuantEvent[i].getQuantity());
                assertEquals(expQuantEvent[i].getReadPoint(),
                        actQuantEvent[i].getReadPoint());
                // assertEquals(expQuantEvent[i].getRecordTime(),
                // actQuantEvent[i].getRecordTime());

                BusinessTransactionType[] actBizTrans = actQuantEvent[i].getBizTransactionList();
                BusinessTransactionType[] expBizTrans = expQuantEvent[i].getBizTransactionList();
                assertEquals(actBizTrans.length, expBizTrans.length);
                for (int j = 0; j < actBizTrans.length; j++) {
                    assertEquals(expBizTrans[j].getType(),
                            actBizTrans[j].getType());
                    // assertEquals(expBizTrans[j].getValue(),
                    // actBizTrans[j].getValue());
                }
            }
        }
    }
}
