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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.captureclient.CaptureClient;
import org.accada.epcis.queryclient.QueryControlClient;

/**
 * This Test first inserts two events each containing two fieldname extensions
 * into the EPCIS Repository. It then sends a query asking for all events with a
 * fieldname extension greater the given value. If everything works as expected,
 * we have correctly inserted the event extension with the correct data type
 * into the database.
 * 
 * @author Marco Steybe
 */
public class EventFieldExtensionTest extends TestCase {

    private static String event1 = null;
    private static String event2 = null;
    private static String query = null;
    private static String response = null;

    /**
     * Tests event fieldname extensions.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an error in the service occured.
     */
    public void testExtension() throws IOException, ServiceException {

        // send event1
        CaptureClient captureClient = new CaptureClient();
        String resp = captureClient.capture(event1);
        assertEquals("HTTP/1.0 200 OK: Request succeeded.", resp);

        // send event2
        resp = captureClient.capture(event2);
        assertEquals("HTTP/1.0 200 OK: Request succeeded.", resp);

        // send query
        QueryControlClient queryClient = new QueryControlClient();
        queryClient.poll(new ByteArrayInputStream(query.getBytes()));

        // TODO check response!
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        StringBuilder sb = new StringBuilder();
        sb.append("<epcis:EPCISDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" xmlns:epcglobal=\"urn:epcglobal:xsd:1\" xsi:schemaLocation=\"urn:epcglobal:epcis:xsd:1 EPCglobal-epcis-1_0.xsd\" xmlns:hls=\"http://schema.hls.com/extension\" creationDate=\"2006-06-25T00:00:00Z\" schemaVersion=\"1.0\">");
        sb.append("<EPCISBody>");
        sb.append("<EventList>");
        sb.append("<ObjectEvent>");
        sb.append("<eventTime>2006-06-25T00:01:00Z</eventTime>");
        sb.append("<eventTimeZoneOffset>-06:00</eventTimeZoneOffset>");
        sb.append("<epcList>");
        sb.append("<epc>urn:epc:id:sgtin:0614141.107340.1</epc>");
        sb.append("</epcList>");
        sb.append("<action>ADD</action>");
        sb.append("<bizStep>urn:epcglobal:hls:bizstep:commissioning</bizStep>");
        sb.append("<disposition>urn:epcglobal:hls:disp:active</disposition>");
        sb.append("<readPoint>");
        sb.append("<id>urn:epcglobal:fmcg:loc:0614141073467.RP-1</id>");
        sb.append("</readPoint>");
        sb.append("<bizLocation>");
        sb.append("<id>urn:epcglobal:fmcg:loc:0614141073467.1</id>");
        sb.append("</bizLocation>");
        sb.append("<hls:temperature>49</hls:temperature>");
        sb.append("<hls:batchNumber>2</hls:batchNumber>");
        sb.append("</ObjectEvent>");
        sb.append("</EventList>");
        sb.append("</EPCISBody>");
        sb.append("</epcis:EPCISDocument>");
        event1 = sb.toString();

        sb = new StringBuilder();
        sb.append("<epcis:EPCISDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" xmlns:epcglobal=\"urn:epcglobal:xsd:1\" xsi:schemaLocation=\"urn:epcglobal:epcis:xsd:1 EPCglobal-epcis-1_0.xsd\" xmlns:hls=\"http://schema.hls.com/extension\" creationDate=\"2006-06-25T00:00:00Z\" schemaVersion=\"1.0\">");
        sb.append("<EPCISBody>");
        sb.append("<EventList>");
        sb.append("<ObjectEvent>");
        sb.append("<eventTime>2006-06-25T00:01:00Z</eventTime>");
        sb.append("<eventTimeZoneOffset>-06:00</eventTimeZoneOffset>");
        sb.append("<epcList>");
        sb.append("<epc>urn:epc:id:sgtin:0614141.107340.1</epc>");
        sb.append("</epcList>");
        sb.append("<action>ADD</action>");
        sb.append("<bizStep>urn:epcglobal:hls:bizstep:commissioning</bizStep>");
        sb.append("<disposition>urn:epcglobal:hls:disp:active</disposition>");
        sb.append("<readPoint>");
        sb.append("<id>urn:epcglobal:fmcg:loc:0614141073467.RP-1</id>");
        sb.append("</readPoint>");
        sb.append("<bizLocation>");
        sb.append("<id>urn:epcglobal:fmcg:loc:0614141073467.1</id>");
        sb.append("</bizLocation>");
        sb.append("<hls:temperature>48</hls:temperature>");
        sb.append("<hls:batchNumber>2</hls:batchNumber>");
        sb.append("</ObjectEvent>");
        sb.append("</EventList>");
        sb.append("</EPCISBody>");
        sb.append("</epcis:EPCISDocument>");
        event2 = sb.toString();

        sb = new StringBuilder();
        sb.append("<epcisq:poll xmlns:epcisq=\"urn:epcglobal:epcis-query:xsd:1\">");
        sb.append("<queryName>SimpleEventQuery</queryName>");
        sb.append("<params>");
        sb.append("<param>");
        sb.append("<name>GT_http://schema.hls.com/extension#temperature</name>");
        sb.append("<value>48</value>");
        sb.append("</param>");
        sb.append("</params>");
        sb.append("</epcisq:poll>");
        query = sb.toString();

        sb = new StringBuilder();
        sb.append("<ObjectEvent>\n");
        sb.append("  <eventTime>2006-06-25T00:01:00.000Z</eventTime>\n");
        sb.append("  <recordTime>2006-12-14T13:41:28.000Z</recordTime>\n");
        sb.append("  <eventTimeZoneOffset>-06:00</eventTimeZoneOffset>\n");
        sb.append("  <epcList/>\n");
        sb.append("  <action>ADD</action>\n");
        sb.append("  <bizStep>urn:epcglobal:hls:bizstep:commissioning</bizStep>\n");
        sb.append("  <disposition>urn:epcglobal:hls:disp:active</disposition>\n");
        sb.append("  <readPoint><id>urn:epcglobal:fmcg:loc:0614141073467.RP-1</id></readPoint>\n");
        sb.append("  <bizLocation><id>urn:epcglobal:fmcg:loc:0614141073467.1</id></bizLocation>\n");
        sb.append("  <bizTransactionList/>\n");
        sb.append("  <hls:temperature xmlns:hls=\"http://schema.hls.com/extension\">49</hls:temperature>\n");
        sb.append("  <hls:batchNumber xmlns:hls=\"http://schema.hls.com/extension\">2</hls:batchNumber>\n");
        sb.append("</ObjectEvent>");
        response = sb.toString();
    }
}
