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

import org.accada.epcis.queryclient.QueryClientInterface;
import org.accada.epcis.queryclient.QueryClientSoapImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test for unsubscribing queries (SE44).
 * 
 * @author Marco Steybe
 */
public class UnsubscribeTest extends TestCase {

    private static final String PATH = "src/test/resources/queries/webservice/requests/";

    private QueryClientInterface client = new QueryClientSoapImpl();
    private SubscriptionNotification subscription = new SubscriptionNotification();

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
        client.subscribeQuery(fis);
        fis.close();

        // wait for response (1 min)
        String resp1 = subscription.waitForNotification(60 * 1000 + 1);
        assertNotNull(resp1);

        // parse response to make sure we got back a result
        Document epcis = parseResponse(resp1);
        Node eventList = epcis.getElementsByTagName("EventList").item(0);
        assertTrue(eventList.hasChildNodes());

        // unsubscribe the query
        client.unsubscribeQuery("QuerySE44");

        // wait for response (1 min)
        String resp2 = subscription.waitForNotification(60 * 1000 + 1);
        assertNull("No response expected, but received: " + resp2, resp2);
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
}