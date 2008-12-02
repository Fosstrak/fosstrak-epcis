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

package org.fosstrak.epcis.captureclient;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.fosstrak.epcis.model.ActionType;
import org.fosstrak.epcis.model.BusinessLocationType;
import org.fosstrak.epcis.model.EPC;
import org.fosstrak.epcis.model.EPCISBodyType;
import org.fosstrak.epcis.model.EPCISDocumentType;
import org.fosstrak.epcis.model.EPCListType;
import org.fosstrak.epcis.model.EventListType;
import org.fosstrak.epcis.model.ObjectEventType;
import org.fosstrak.epcis.model.ReadPointType;

/**
 * A simple test utility class for demonstrating how to capture EPCIS events to
 * the Fosstrak EPCIS capture interface
 * 
 * @author Marco Steybe
 */
public class SimpleCaptureTest {

    public static void main(String[] args) throws Exception {
        // configure the capture service
        String captureUrl = "http://demo.fosstrak.org/epcis/capture";
        CaptureClient client = new CaptureClient(captureUrl);

        // create a request in its XML form and send it to the repository
        System.out.println("Sending events:");
        String xml = createEventsXml();
        System.out.println(xml);
        int htmlResponseCode = client.capture(xml);
        if (htmlResponseCode == 200) {
            System.out.println("Capture of events successful");
        }

        // create a request using the API and send it to the repository
        EPCISDocumentType epcisDoc = createEpcisDoc();
        htmlResponseCode = client.capture(epcisDoc);
        if (htmlResponseCode == 200) {
            System.out.println("Capture of events successful");
        }
    }

    /**
     * Constructs and returns a simple EPCIS event XML String.
     */
    private static String createEventsXml() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<epcis:EPCISDocument xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" creationDate=\"2008-03-06T11:42:15.016+01:00\" schemaVersion=\"1.0\" xmlns:my_ns=\"http://my.unique.namespace\">\n");
        sb.append("<EPCISBody>\n");
        sb.append("  <EventList>\n");
        sb.append("    <ObjectEvent>\n");
        sb.append("      <eventTime>2008-11-09T14:30:17Z</eventTime>\n");
        sb.append("      <eventTimeZoneOffset>+00:00</eventTimeZoneOffset>\n");
        sb.append("      <epcList>\n");
        sb.append("        <epc>urn:epc:id:sgtin:0057000.123780.7788</epc>\n");
        sb.append("      </epcList>\n");
        sb.append("      <action>ADD</action>\n");
        sb.append("      <bizStep>urn:fosstrak:demo:bizstep:fmcg:production</bizStep>\n");
        sb.append("      <disposition>urn:fosstrak:demo:disp:fmcg:pendingQA</disposition>\n");
        sb.append("      <readPoint>\n");
        sb.append("        <id>urn:fosstrak:demo:fmcg:ssl:0037000.00729.210,432</id>\n");
        sb.append("      </readPoint>\n");
        sb.append("      <bizLocation>\n");
        sb.append("        <id>urn:fosstrak:demo:fmcg:ssl:0037000.00729.210</id>\n");
        sb.append("      </bizLocation>\n");
        sb.append("      <my_ns:my_extensionfield>My Extension</my_ns:my_extensionfield>\n");
        sb.append("    </ObjectEvent>\n");
        sb.append("  </EventList>\n");
        sb.append("</EPCISBody>\n");
        sb.append("</epcis:EPCISDocument>");
        return sb.toString();
    }

    /**
     * Constructs and returns a simple EPCIS event XML using the API objects
     * from the EPCIS schema.
     */
    private static EPCISDocumentType createEpcisDoc() throws IOException, JAXBException {
        ObjectEventType objEvent = new ObjectEventType();

        // get the current time and set the eventTime
        XMLGregorianCalendar now = null;
        try {
            DatatypeFactory dataFactory = DatatypeFactory.newInstance();
            now = dataFactory.newXMLGregorianCalendar(new GregorianCalendar());
            objEvent.setEventTime(now);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        // get the current time zone and set the eventTimeZoneOffset
        if (now != null) {
            int timezone = now.getTimezone();
            int h = Math.abs(timezone / 60);
            int m = Math.abs(timezone % 60);
            DecimalFormat format = new DecimalFormat("00");
            String sign = (timezone < 0) ? "-" : "+";
            objEvent.setEventTimeZoneOffset(sign + format.format(h) + ":" + format.format(m));
        }

        // set EPCs
        EPC epc = new EPC();
        epc.setValue("urn:epc:id:sgtin:0057000.123780.7788");
        EPCListType epcList = new EPCListType();
        epcList.getEpc().add(epc);
        objEvent.setEpcList(epcList);

        // set action
        objEvent.setAction(ActionType.ADD);

        // set bizStep
        objEvent.setBizStep("urn:fosstrak:demo:bizstep:fmcg:production");

        // set disposition
        objEvent.setDisposition("urn:fosstrak:demo:disp:fmcg:pendingQA");

        // set readPoint
        ReadPointType readPoint = new ReadPointType();
        readPoint.setId("urn:fosstrak:demo:fmcg:ssl:0037000.00729.210,432");
        objEvent.setReadPoint(readPoint);

        // set bizLocation
        BusinessLocationType bizLocation = new BusinessLocationType();
        bizLocation.setId("urn:fosstrak:demo:fmcg:ssl:0037000.00729.210");
        objEvent.setBizLocation(bizLocation);

        // create the EPCISDocument containing the ObjectEvent
        EPCISDocumentType epcisDoc = new EPCISDocumentType();
        EPCISBodyType epcisBody = new EPCISBodyType();
        EventListType eventList = new EventListType();
        eventList.getObjectEventOrAggregationEventOrQuantityEvent().add(objEvent);
        epcisBody.setEventList(eventList);
        epcisDoc.setEPCISBody(epcisBody);
        epcisDoc.setSchemaVersion(new BigDecimal("1.0"));
        epcisDoc.setCreationDate(now);

        return epcisDoc;
    }
}
