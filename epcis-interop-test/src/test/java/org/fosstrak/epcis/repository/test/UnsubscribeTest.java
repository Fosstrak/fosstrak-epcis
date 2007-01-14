package org.accada.epcis.repository.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

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

    /**
     * The port on which this test listens for notifications (results for
     * subscribed queries).
     */
    private static final int PORT = 9999;

    private static final String PATH = "src/test/resources/queries/webservice/requests/";
    private static final String REQUEST = "Test-EPCIS10-SE44-Request-1-Subscribe.xml";

    QueryClientInterface client = new QueryClientSoapImpl();

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

        // subscribe a query
        InputStream fis = new FileInputStream(PATH + REQUEST);
        client.subscribeQuery(fis);
        fis.close();

        // wait for response (1 min)
        String resp1 = waitForResponse(60 * 1000 + 1);

        // parse response to make sure we got back a result
        Document epcis = parseResponse(resp1);
        Node eventList = epcis.getElementsByTagName("EventList").item(0);
        assertTrue(eventList.hasChildNodes());

        // unsubscribe the query
        String subscriptionId = "QuerySE44";
        client.unsubscribeQuery(subscriptionId);

        // wait for response (1 min)
        try {
            String resp2 = waitForResponse(60 * 1000 + 1);
            fail("No response expected, but received: " + resp2);
        } catch (SocketTimeoutException e) {
            // ok
        }
    }

    /**
     * Listen for notifications for <timeToWait> ms. If the <timeToWait> is
     * expired, a SocketTimeoutException is thrown.
     * 
     * @param timeToWait
     *            The time to wait for notifications (in milliseconds).
     * @return The response received without http headers.
     * @throws IOException
     *             If an I/O error occured.
     * @throws SocketTimeoutException
     *             If the <timeToWait> expired before a notification came in.
     */
    private String waitForResponse(int timeToWait) throws IOException,
            SocketTimeoutException {
        // listen on localhost/port for incoming messages
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress("127.0.0.1", PORT));
        ssc.socket().setSoTimeout(timeToWait);
        SocketChannel sc = ssc.accept();
        ByteBuffer bb = ByteBuffer.allocate(131072);
        sc.read(bb);
        sc.close();
        ssc.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(bb.array());
        assertTrue("Query response contains no data!", bais.available() > 0);

        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(bais));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        int xmlIndex = sb.indexOf("<?xml");
        assertTrue("Query response contains no XML message!", xmlIndex != -1);
        int index = sb.indexOf("<", xmlIndex + 1);
        return sb.substring(index).trim();
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