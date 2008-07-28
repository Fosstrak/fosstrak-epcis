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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
 * A simple test utility class for sending a single or multiple capture
 * request(s) to the Fosstrak EPCIS capture interface and bootstrapping the
 * capture module.
 * 
 * @author Marco Steybe
 */
public class SimpleCaptureTest {

    /**
     * Constructs a simple EPCIS event XML String, sends it to the EPCIS
     * repository at the specified URL String, and returns the response from the
     * repository.
     */
    public static boolean capture(String captureUrl) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<epcis:EPCISDocument xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" creationDate=\"2008-03-06T11:42:15.016+01:00\" schemaVersion=\"1.0\">");
        sb.append("<EPCISBody>");
        sb.append("<EventList>");
        sb.append("<ObjectEvent>");
        sb.append("<eventTime>2006-09-20T06:36:17Z</eventTime>");
        sb.append("<eventTimeZoneOffset>+00:00</eventTimeZoneOffset>");
        sb.append("<epcList>");
        sb.append("<epc>urn:epc:id:sgtin:0057000.123780.7788</epc>");
        sb.append("</epcList>");
        sb.append("<action>ADD</action>");
        sb.append("<bizStep>urn:fosstrak:demo:bizstep:fmcg:production</bizStep>");
        sb.append("<disposition>urn:fosstrak:demo:disp:fmcg:pendingQA</disposition>");
        sb.append("<readPoint>");
        sb.append("<id>urn:fosstrak:demo:fmcg:ssl:0037000.00729.210,432</id>");
        sb.append("</readPoint>");
        sb.append("<bizLocation>");
        sb.append("<id>urn:fosstrak:demo:fmcg:ssl:0037000.00729.210</id>");
        sb.append("</bizLocation>");
        sb.append("</ObjectEvent>");
        sb.append("</EventList>");
        sb.append("</EPCISBody>");
        sb.append("</epcis:EPCISDocument>");

        CaptureClient client = new CaptureClient(captureUrl);
        int responseCode = client.capture(sb.toString());
        if (responseCode == 200) {
            return true;
        }
        return false;
    }

    /**
     * Constructs a simple EPCIS event XML using the API objects from the EPCIS
     * schema, sends it to the repository at the specified URL String, and
     * returns the response from the repository.
     */
    public static boolean captureWithApi(String captureUrl) throws IOException, JAXBException {
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

        // send the EPCISDocument to the repository using the CaptureClient
        CaptureClient client = new CaptureClient(captureUrl);
        int responseCode = client.capture(epcisDoc);
        if (responseCode == 200) {
            return true;
        }
        return false;
    }

    public static boolean captureFromFile(String filename, String captureUrl) throws IOException {
        CaptureClient client = new CaptureClient(captureUrl);
        System.out.println("capturing file: " + filename);
        System.out.println("capturing url: " + client.getCaptureUrl());
        InputStream is = new FileInputStream(filename);
        int responseCode = client.capture(is);
        if (responseCode == 200) {
            return true;
        }
        return false;
    }

    public static boolean captureFromDir(String dirname, String captureUrl) throws IOException {
        // capture all xml files in the given folder
        File dir = new File(dirname);
        boolean allOk = true;
        if (dir.isDirectory()) {
            for (File f : dir.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    if (file.getName().endsWith(".xml")) {
                        return true;
                    }
                    return false;
                }
            })) {
                boolean ok = captureFromFile(f.getPath(), captureUrl);
                if (!ok) {
                    allOk = false;
                }
            }
        }
        return allOk;
    }

    public static void main(String[] args) throws Exception {
        String captureUrl = null; // use default
        boolean ok = captureWithApi(captureUrl);
        // boolean ok = capture(captureUrl);
        // boolean ok = captureFromFile("D:/test/test.xml", captureUrl);
        // boolean ok = captureFromDir("D:/test/events", captureUrl);
        if (ok) {
            System.out.println("Capture of all events successful");
        } else {
            System.out.println("Capture of at least one event failed");
        }
    }
}
