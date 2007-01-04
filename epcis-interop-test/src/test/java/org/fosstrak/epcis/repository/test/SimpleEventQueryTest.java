/**
 *
 */
package org.accada.epcis.repository.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
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
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.soapapi.TransactionEventType;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author Marco Steybe
 *
 */
public class SimpleEventQueryTest extends TestCase {

    private static Logger LOG = Logger.getLogger(SimpleEventQueryTest.class);

    QueryClientInterface client;

    static String pathToQueries = "src/test/resources/queries/webservice/requests/";

    static String queryPrefix = "Test-EPCIS10-SE";

    static String querySuffix = "-Request-1-poll.xml";

    static String pathToResp = "src/test/resources/queries/webservice/responses/";

    static String respPrefix = "Test-EPCIS10-SE";

    static String respSuffix = "-Response-1-poll.xml";

    /**
     * Setup.
     */

    public void setUp() {
        PropertyConfigurator.configure("src/test/resources/conf/log4j.properties");
        client =
                new QueryClientSoapImpl(
                                        "http://localhost:8888/epcis-repository/query/EPCglobalEPCISService");
    }


    public void testSE10() throws IOException, ServiceException {
        int testNr = 10;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE11() throws IOException, ServiceException {
        int testNr = 11;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE12() throws IOException, ServiceException {
        int testNr = 12;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE13() throws IOException, ServiceException {
        int testNr = 13;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE14() throws IOException, ServiceException {
        int testNr = 14;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE15() throws IOException, ServiceException {
        int testNr = 15;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE16() throws IOException, ServiceException {
        int testNr = 16;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE17() throws IOException, ServiceException {
        int testNr = 17;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE18() throws IOException, ServiceException {
        int testNr = 18;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE19() throws IOException, ServiceException {
        int testNr = 19;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE20() throws IOException, ServiceException {
        int testNr = 20;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE21() throws IOException, ServiceException {
        int testNr = 21;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE22() throws IOException, ServiceException {
        int testNr = 22;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE23() throws IOException, ServiceException {
        int testNr = 23;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE24() throws IOException, ServiceException {
        int testNr = 24;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE25() throws IOException, ServiceException {
        int testNr = 25;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE26() throws IOException, ServiceException {
        int testNr = 26;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE27() throws IOException, ServiceException {
        int testNr = 27;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE28() throws IOException, ServiceException {
        int testNr = 28;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE29() throws IOException, ServiceException {
        int testNr = 29;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE30() throws IOException, ServiceException {
        int testNr = 30;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE31() throws IOException, ServiceException {
        int testNr = 31;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE32() throws IOException, ServiceException {
        int testNr = 32;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE33() throws IOException, ServiceException {
        int testNr = 33;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE34() throws IOException, ServiceException {
        int testNr = 34;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE35() throws IOException, ServiceException {
        int testNr = 35;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE36() throws IOException, ServiceException {
        int testNr = 36;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE37() throws IOException, ServiceException {
        int testNr = 37;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE38() throws IOException, ServiceException {
        int testNr = 38;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE39() throws IOException, ServiceException {
        int testNr = 39;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE40() throws IOException, ServiceException {
        int testNr = 40;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        try {
            client.runQuery(fis);
            fail("should raise a QueryTooLargeException");
        } catch (QueryTooLargeException e) {
            // success
        }
        fis.close();
    }


    public void testSE41() throws IOException, ServiceException {
        int testNr = 41;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        try {
            client.runQuery(fis);
            fail("should raise a QueryParameterException");
        } catch (QueryParameterException e) {
            // success
        }
        fis.close();
    }


    public void testSE42() throws IOException, ServiceException {
        int testNr = 42;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }


    public void testSE43() throws IOException, ServiceException {
        int testNr = 43;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) client.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        LOG.info("response taken from " + resp);
        fis = new FileInputStream(resp);
        QueryResults expResults =
                ((QueryClientSoapImpl) client).convertXmlToQueryResults(fis);
        fis.close();
        compareResults(expResults, actResults);
    }

    class XmlFileFilter implements FilenameFilter {

        private String prefix = null;

        private String suffix = null;

        public XmlFileFilter(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            if (name.endsWith(suffix) && name.startsWith(prefix)) {
                return true;
            }
            return false;
        }
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
                assertEquals(expObjectEvent[i].get_any(),
                             actObjectEvent[i].get_any());
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
                assertEquals(
                             expObjectEvent[i]
                                              .getEventTime()
                                              .compareTo(
                                                         actObjectEvent[i]
                                                                          .getEventTime()),
                             0);
                assertEquals(expObjectEvent[i].getExtension(),
                             actObjectEvent[i].getExtension());
                assertEquals(expObjectEvent[i].getReadPoint(),
                             actObjectEvent[i].getReadPoint());
                // assertEquals(expObjectEvent[i].getRecordTime(),
                // actObjectEvent[i].getRecordTime());

                EPC[] actEpcs = actObjectEvent[i].getEpcList();
                EPC[] expEpcs = expObjectEvent[i].getEpcList();
                assertEquals(expEpcs.length, actEpcs.length);
                for (int j = 0; j < actEpcs.length; j++) {
                    assertEquals(expEpcs[j].get_value(), actEpcs[j].get_value());
                }

                BusinessTransactionType[] actBizTrans =
                        actObjectEvent[i].getBizTransactionList();
                BusinessTransactionType[] expBizTrans =
                        expObjectEvent[i].getBizTransactionList();
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
                assertEquals(
                             expAggrEvent[i]
                                            .getEventTime()
                                            .compareTo(
                                                       actAggrEvent[i]
                                                                      .getEventTime()),
                             0);
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

                BusinessTransactionType[] actBizTrans =
                        actAggrEvent[i].getBizTransactionList();
                BusinessTransactionType[] expBizTrans =
                        expAggrEvent[i].getBizTransactionList();
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
                assertEquals(
                             expTransEvent[i]
                                             .getEventTime()
                                             .compareTo(
                                                        actTransEvent[i]
                                                                        .getEventTime()),
                             0);
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

                BusinessTransactionType[] actBizTrans =
                        actTransEvent[i].getBizTransactionList();
                BusinessTransactionType[] expBizTrans =
                        expTransEvent[i].getBizTransactionList();
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
                assertEquals(
                             expQuantEvent[i]
                                             .getEventTime()
                                             .compareTo(
                                                        actQuantEvent[i]
                                                                        .getEventTime()),
                             0);
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

                BusinessTransactionType[] actBizTrans =
                        actQuantEvent[i].getBizTransactionList();
                BusinessTransactionType[] expBizTrans =
                        expQuantEvent[i].getBizTransactionList();
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
