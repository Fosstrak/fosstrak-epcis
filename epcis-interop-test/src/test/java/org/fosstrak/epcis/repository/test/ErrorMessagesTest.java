package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryClientInterface;
import org.accada.epcis.queryclient.QueryClientSoapImpl;
import org.accada.epcis.soapapi.AggregationEventType;
import org.accada.epcis.soapapi.BusinessTransactionType;
import org.accada.epcis.soapapi.EPC;
import org.accada.epcis.soapapi.EventListType;
import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.NoSuchNameException;
import org.accada.epcis.soapapi.ObjectEventType;
import org.accada.epcis.soapapi.QuantityEventType;
import org.accada.epcis.soapapi.QueryParameterException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryTooComplexException;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.soapapi.SecurityException;
import org.accada.epcis.soapapi.TransactionEventType;
import org.accada.epcis.soapapi.ValidationException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * TestSuite for Errors. SE44 -SE74
 * 
 * TODO !!!!!
 * 
 * @author Andrea Grössbauer
 */
public class ErrorMessagesTest extends TestCase {

    private static final Logger LOG = Logger.getLogger(ErrorMessagesTest.class);

    QueryClientInterface queryClient;

    static String pathToQueries = "src/test/resources/queries/webservice/requests/";
    static String queryPrefix = "Test-EPCIS10-SE";
    static String querySuffix = "-Request-1-poll.xml";

    static String requ1 = "-Request-1-";
    static String requ2 = "-Request-2-";

    static String querySuffixSubsc = "Subscribe.xml";
    static String querySuffixUnsubscr = "Unsubscribe.xml";
    static String pathToResp = "src/test/resources/queries/webservice/responses/";
    static String respPrefix = "Test-EPCIS10-SE";
    static String respSuffix = "-Response-1-poll.xml";

    static int startIndex = 44;
    static int endIndex = 74;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        PropertyConfigurator.configure("src/test/resources/conf/log4j.properties");
        queryClient = new QueryClientSoapImpl();
    }

    public void testSE49() throws ImplementationException,
            QueryTooLargeException, QueryParameterException,
            ValidationException, SecurityException, NoSuchNameException,
            RemoteException, ServiceException, IOException {
        int testNr = 16;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        try {
            InputStream fis = new FileInputStream(query);
            queryClient.runQuery(fis);
            fail("QueryTooComplexException expected");
        } catch (QueryTooComplexException e) {
            LOG.info("Test49 succeeded");
        }
    }

    public void testSE50() throws QueryTooComplexException,
            ImplementationException, QueryParameterException,
            ValidationException, SecurityException, NoSuchNameException,
            RemoteException, ServiceException, IOException {
        int testNr = 50;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        try {
            InputStream fis = new FileInputStream(query);
            queryClient.runQuery(fis);
            fail("QueryTooLargeException expected");
        } catch (QueryTooLargeException e) {
            LOG.info("Test50 succeeded");
        }
    }

    public void testSE51() throws QueryTooComplexException,
            QueryTooLargeException, QueryParameterException,
            ValidationException, SecurityException, NoSuchNameException,
            RemoteException, ServiceException, IOException {
        int testNr = 51;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        try {
            InputStream fis = new FileInputStream(query);
            queryClient.runQuery(fis);
            fail("ImplementationException expected");
        } catch (ImplementationException e) {
            LOG.info("Severity value received:" + e.getSeverity().toString());
            assertEquals(e.getSeverity().toString(), "SEVERE");
        }
    }

    public void testSE70() throws QueryTooComplexException,
            ImplementationException, QueryTooLargeException,
            ValidationException, SecurityException, NoSuchNameException,
            RemoteException, ServiceException, IOException {
        int testNr = 70;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        try {
            InputStream fis = new FileInputStream(query);
            queryClient.runQuery(fis);
            fail("QueryParameterException expected");
        } catch (QueryParameterException e) {
            LOG.info("Test70 succeeded");
        }
    }

    public void testSE71() throws QueryTooComplexException,
            ImplementationException, QueryTooLargeException,
            ValidationException, SecurityException, NoSuchNameException,
            RemoteException, ServiceException, IOException {
        int testNr = 71;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        try {
            InputStream fis = new FileInputStream(query);
            queryClient.runQuery(fis);
            fail("QueryParameterException expected");
        } catch (QueryParameterException e) {
            LOG.info("Test71 succeeded");
        }
    }

    public void testSE72() throws QueryTooComplexException,
            ImplementationException, QueryTooLargeException,
            ValidationException, SecurityException, NoSuchNameException,
            RemoteException, ServiceException, IOException {
        int testNr = 72;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        try {
            InputStream fis = new FileInputStream(query);
            queryClient.runQuery(fis);
            fail("QueryParameterException expected");
        } catch (QueryParameterException e) {
            LOG.info("Test72 succeeded");
        }
    }

    public void testSE73() throws Exception {
        int testNr = 73;
        String query = pathToQueries + queryPrefix + testNr + querySuffix;
        LOG.info("query taken from " + query);
        InputStream fis = new FileInputStream(query);
        QueryResults actResults = (QueryResults) queryClient.runQuery(fis);
        fis.close();

        String resp = pathToResp + respPrefix + testNr + respSuffix;
        fis = new FileInputStream(resp);
        QueryResults expResults = ((QueryClientSoapImpl) queryClient).convertXmlToQueryResults(fis);
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
                assertEquals(expObjectEvent[i].getEventTime().compareTo(
                        actObjectEvent[i].getEventTime()), 0);
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