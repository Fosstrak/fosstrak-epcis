/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Accada (www.accada.org).
 *
 * Accada is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Accada is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Accada; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.accada.epcis.captureclient;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple test utility class for sending a single or multiple capture
 * request(s) to the Accada EPCIS capture interface and bootstrapping the
 * capture module.
 * <p>
 * Note: keep the methods in this class static in order to prevent them from
 * being executed when building the project with Maven.
 * 
 * @author Marco Steybe
 */
public class SimpleCaptureTest {

    private static CaptureClient client = new CaptureClient();

    /**
     * Creates a simple EPCIS query, sends it to the EPCIS query service for
     * processing and prints the response to System.out.
     */
    public static void testCapture() throws IOException {
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
        sb.append("<bizStep>urn:accada:demo:bizstep:fmcg:production</bizStep>");
        sb.append("<disposition>urn:accada:demo:disp:fmcg:pendingQA</disposition>");
        sb.append("<readPoint>");
        sb.append("<id>urn:accada:demo:fmcg:ssl:0037000.00729.210,432</id>");
        sb.append("</readPoint>");
        sb.append("<bizLocation>");
        sb.append("<id>urn:accada:demo:fmcg:ssl:0037000.00729.210</id>");
        sb.append("</bizLocation>");
        sb.append("</ObjectEvent>");
        sb.append("</EventList>");
        sb.append("</EPCISBody>");
        sb.append("</epcis:EPCISDocument>");

        System.out.println(client.capture(sb.toString()));
    }

    public static boolean testCaptureFromFile(String filename) throws IOException {
        System.out.println(filename);
        InputStream is = new FileInputStream(filename);
        int responseCode = client.capture(is);
        if (responseCode == 200) {
            return true;
        }
        return false;
    }

    public static boolean testCaptureFromDir(String dirname) throws IOException {
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
                boolean ok = testCaptureFromFile(f.getPath());
                if (!ok) {
                    allOk = false;
                }
            }
        }
        return allOk;
    }

    public static void main(String[] args) throws Exception {
        // test.testCapture();
        boolean ok = testCaptureFromDir("D:/test");
        if (ok) {
            System.out.println("Capture of all events successful");
        } else {
            System.out.println("Capture of at least one event failed");
        }
    }
}
