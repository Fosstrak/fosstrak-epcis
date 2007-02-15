package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;
import org.accada.epcis.utils.QueryCallbackListener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test for unsubscribing queries (SE44).
 * 
 * @author Marco Steybe
 */
public class CallbackUnsubscribeTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/requests/";

    private QueryControlClient client = new QueryControlClient();

    /**
     * Tests if we receive a notification for a subscribed query, and we receive
     * no further notification after the query is unsubscribed.
     * 
     * @throws IOException
     *             If an I/O excpetion occured.
     * @throws ServiceException
     *             If the EPCIS query service encountered a problem.
     * @throws ParserConfigurationException
     *             If the parser for parsing the response could not be
     *             configured.
     * @throws SAXException
     *             If the response could not be parsed.
     */
    public void testSE44() throws IOException, ServiceException,
            ParserConfigurationException, SAXException {
        final String query = "Test-EPCIS10-SE44-Request-1-Subscribe.xml";

        // subscribe a query
        InputStream fis = new FileInputStream(PATH + query);
        client.subscribe(fis);
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
        String resp1 = listener.fetchResponse();
        assertNotNull(resp1);

        // parse response to make sure we got back a result
        Document epcis = parseResponse(resp1);
        Node eventList = epcis.getElementsByTagName("EventList").item(0);
        assertTrue(eventList.hasChildNodes());

        // unsubscribe the query and wait for any response
        client.unsubscribe("QuerySE44");
        System.out.println("waiting ...");
        synchronized (listener) {
            try {
                listener.wait(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String resp2 = listener.fetchResponse();
        assertNull("No response expected, but received: " + resp2, resp2);
        listener.stopRunning();
    }

    /**
     * Parses a string into an XML Document.
     * 
     * @param resp
     *            The string to be parsed.
     * @return The parsed XML Document.
     * @throws ParserConfigurationException
     *             If the parser could not be configured.
     * @throws SAXException
     *             If a parse error occured.
     * @throws IOException
     *             If an I/O error occured.
     */
    private Document parseResponse(String resp)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource xmlInput = new InputSource(new StringReader(resp));
        return builder.parse(xmlInput);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            client.unsubscribe("QuerySE44");
        } catch (NoSuchSubscriptionException e) {
        }
    }

}