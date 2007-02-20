/*
 * Copyright (c) 2006, 2007, ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the ETH Zurich nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.accada.epcis.repository.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.accada.epcis.captureclient.CaptureClient;

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
    public void testCommissionCases() throws IOException {
        // read the events
        InputStream fis = new FileInputStream(COMMISSION_CASES_XML);
        byte[] xml = new byte[fis.available()];
        fis.read(xml);

        String eventXml = new String(xml);

        // send the data
        String response = client.capture(eventXml);
        assertEquals("HTTP/1.0 200 OK: Request succeeded.", response);
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
        String response = client.capture(eventXml);
        assertEquals("HTTP/1.0 200 OK: Request succeeded.", response);
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
        String response = client.capture(eventXml);
        assertEquals("HTTP/1.0 200 OK: Request succeeded.", response);
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
        String response = client.capture(eventXml);
        assertEquals("HTTP/1.0 200 OK: Request succeeded.", response);
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
        String response = client.capture(eventXml);
        assertEquals("HTTP/1.0 200 OK: Request succeeded.", response);
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
        String response = client.capture(eventXml);
        assertEquals("HTTP/1.0 200 OK: Request succeeded.", response);
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
        String response = client.capture(eventXml);
        assertEquals("HTTP/1.0 200 OK: Request succeeded.", response);
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
        String response = client.capture(eventXml);
        assertEquals("HTTP/1.0 200 OK: Request succeeded.", response);
    }
}
