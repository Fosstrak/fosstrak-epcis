/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org).
 *
 * Fosstrak is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Fosstrak is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Fosstrak; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.fosstrak.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.fosstrak.epcis.captureclient.CaptureClient;

/**
 * Test if CaptureOperationsModule works correctly. Inserts events required by
 * the other tests.
 * 
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

    private CaptureClient client = new CaptureClient();

    /**
     * Testing events: Test 1.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testCommissionCases() throws Exception {
        // read the events
        InputStream fis = new FileInputStream(COMMISSION_CASES_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send the data
        int response = client.capture(eventXml);
        assertEquals(200, response);
    }

    /**
     * Testing events: Test 2.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testCommissionPallets() throws Exception {
        // read the events
        InputStream fis = new FileInputStream(COMMISSION_PALLETS_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send the data
        int response = client.capture(eventXml);
        assertEquals(200, response);
    }

    /**
     * Testing events: Test 3.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testPackCases() throws Exception {
        // read the events
        InputStream fis = new FileInputStream(PACK_CASES_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send data
        int response = client.capture(eventXml);
        assertEquals(200, response);
    }

    /**
     * Testing events: Test 4.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testReceivePallets() throws Exception {
       // read the events
        InputStream fis = new FileInputStream(REICEIVE_PALLET_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send data
        int response = client.capture(eventXml);
        assertEquals(200, response);
    }

    /**
     * Testing events: Test 5.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testPickOrder() throws Exception {
        // read the events
        InputStream fis = new FileInputStream(PICK_ORDER_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send data
        int response = client.capture(eventXml);
        assertEquals(200, response);
    }

    /**
     * Testing events: Test 6.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testShipOrder() throws Exception {
        // read the events
        InputStream fis = new FileInputStream(SHIP_ORDER_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send data
        int response = client.capture(eventXml);
        assertEquals(200, response);
    }

    /**
     * Testing events: Test 7.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testReceiveOrder() throws Exception {
        // read the events
        InputStream fis = new FileInputStream(RECEIVE_ORDER_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send data
        int response = client.capture(eventXml);
        assertEquals(200, response);
    }

    /**
     * Testing events: Test 8.
     * 
     * @throws IOException
     *             Error Reading file
     */
    public void testStoreInventory() throws Exception {
        // read the events
        InputStream fis = new FileInputStream(STORE_INVENTORY_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send data
        int response = client.capture(eventXml);
        assertEquals(200, response);
    }

    public void captureAll() throws Exception {
        this.testCommissionCases();
        this.testCommissionPallets();
        this.testPackCases();
        this.testPickOrder();
        this.testReceiveOrder();
        this.testReceivePallets();
        this.testShipOrder();
        this.testStoreInventory();
    }
}
