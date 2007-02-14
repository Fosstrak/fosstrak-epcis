package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.accada.epcis.captureclient.CaptureInterfaceClient;

/**
 * @author Marco Steybe
 * @author Andrea Groessbauer
 */
public class CaptureData extends TestCase {

    /**
     * The XML files from which the capture events are taken.
     */
    private static final String COMMISSION_CASES_XML = "src/test/resources/events/1_case_commissioning_events.xml";
    private static final String COMMISSION_PALLETS_XML = "src/test/resources/events/2_pallet_commissioning_events.xml";
    private static final String PACK_CASES_XML = "src/test/resources/events/3_pack_cases_events.xml";
    private static final String REICEIVE_PALLET_XML = "src/test/resources/events/4_receive_pallets_events.xml";
    private static final String PICK_ORDER_XML = "src/test/resources/events/5_pick_order_events.xml";
    private static final String SHIP_ORDER_XML = "src/test/resources/events/6_ship_order_events.xml";
    private static final String RECEIVE_ORDER_XML = "src/test/resources/events/7_receive_order_events.xml";
    private static final String STORE_INVENTORY_XML = "src/test/resources/events/8_store_inventory_events.xml";

    private CaptureInterfaceClient client = new CaptureInterfaceClient();

    /**
     * Testing events: Test 1.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testCommissionCases() throws IOException {
        // read the events
        InputStream fis = new FileInputStream(COMMISSION_CASES_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send the data
        String response = client.sendEvent(eventXml);
        assertEquals("200 OK: Request succeeded.", response);
    }

    /**
     * Testing events: Test 2.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testCommissionPallets() throws IOException {
        // read the events
        InputStream fis = new FileInputStream(COMMISSION_PALLETS_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send the data
        String response = client.sendEvent(eventXml);
        assertEquals("200 OK: Request succeeded.", response);
    }

    /**
     * Testing events: Test 3.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testPackCases() throws IOException {
        // read the events
        InputStream fis = new FileInputStream(PACK_CASES_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send data
        String response = client.sendEvent(eventXml);
        assertEquals("200 OK: Request succeeded.", response);
    }

    /**
     * Testing events: Test 4.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testReceivePallets() throws IOException {
        // read the events
        InputStream fis = new FileInputStream(REICEIVE_PALLET_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send data
        String response = client.sendEvent(eventXml);
        assertEquals("200 OK: Request succeeded.", response);
    }

    /**
     * Testing events: Test 5.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testPickOrder() throws IOException {
        // read the events
        InputStream fis = new FileInputStream(PICK_ORDER_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send data
        String response = client.sendEvent(eventXml);
        assertEquals("200 OK: Request succeeded.", response);
    }

    /**
     * Testing events: Test 6.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testShipOrder() throws IOException {
        // read the events
        InputStream fis = new FileInputStream(SHIP_ORDER_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send data
        String response = client.sendEvent(eventXml);
        assertEquals("200 OK: Request succeeded.", response);
    }

    /**
     * Testing events: Test 7.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testReceiveOrder() throws IOException {
        // read the events
        InputStream fis = new FileInputStream(RECEIVE_ORDER_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send data
        String response = client.sendEvent(eventXml);
        assertEquals("200 OK: Request succeeded.", response);
    }

    /**
     * Testing events: Test 8.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testStoreInventory() throws IOException {
        // read the events
        InputStream fis = new FileInputStream(STORE_INVENTORY_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send data
        String response = client.sendEvent(eventXml);
        assertEquals("200 OK: Request succeeded.", response);
    }

}
