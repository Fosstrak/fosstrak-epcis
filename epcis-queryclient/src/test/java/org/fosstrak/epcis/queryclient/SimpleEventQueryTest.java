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

package org.accada.epcis.queryclient;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.accada.epcis.soap.DuplicateSubscriptionExceptionResponse;
import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.InvalidURIExceptionResponse;
import org.accada.epcis.soap.NoSuchNameExceptionResponse;
import org.accada.epcis.soap.NoSuchSubscriptionExceptionResponse;
import org.accada.epcis.soap.QueryParameterExceptionResponse;
import org.accada.epcis.soap.QueryTooComplexExceptionResponse;
import org.accada.epcis.soap.QueryTooLargeExceptionResponse;
import org.accada.epcis.soap.SecurityExceptionResponse;
import org.accada.epcis.soap.SubscribeNotPermittedExceptionResponse;
import org.accada.epcis.soap.SubscriptionControlsExceptionResponse;
import org.accada.epcis.soap.ValidationExceptionResponse;
import org.accada.epcis.soap.model.QueryResults;
import org.accada.epcis.utils.QueryResultsParser;

/**
 * A simple test utility class for quickly testing event queries against the
 * Accada EPCIS query module.
 * <p>
 * Note: keep the methods in this class static in order to prevent them from
 * being executed when building the project with Maven.
 * 
 * @author Marco Steybe
 */
public class SimpleEventQueryTest {

    private static QueryControlClient client = new QueryControlClient();

    /**
     * Creates a simple EPCIS query, sends it to the EPCIS query service for
     * processing and prints the response to System.out.
     */
    public static void testPoll() throws QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse,
            QueryParameterExceptionResponse, IOException, ImplementationExceptionResponse {
        StringBuilder sb = new StringBuilder();
        sb.append("<epcisq:Poll xmlns:epcisq=\"urn:epcglobal:epcis-query:xsd:1\">");
        sb.append("<queryName>SimpleEventQuery</queryName>");
        sb.append("<params>");
        sb.append("<param>");
        sb.append("<name>eventType</name>");
        sb.append("<value><string>ObjectEvent</string></value>");
        sb.append("</param>");
        sb.append("<param>");
        sb.append("<name>GE_eventTime</name>");
        sb.append("<value>2006-06-25T08:15:00+02:00</value>");
        sb.append("</param>");
        // uncomment or add your own query parameters ...
        // sb.append("<param>");
        // sb.append("<name>HASATTR_bizLocation</name>");
        // sb.append("<value><string>urn:epcglobal:fmcg:foo</string><string>urn:demo:ctry</string></value>");
        // sb.append("</param>");
        // sb.append("<param>");
        // sb.append("<name>EQATTR_bizLocation_urn:demo:ctry</name>");
        // sb.append("<value><string>US</string></value>");
        // sb.append("</param>");
        sb.append("</params>");
        sb.append("</epcisq:Poll>");

        QueryResults results = client.poll(sb.toString());
        QueryResultsParser.queryResultsToXml(results, System.out);
    }

    public static void testPollFromFile(String filename) throws ImplementationExceptionResponse,
            QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse, NoSuchNameExceptionResponse, QueryParameterExceptionResponse, IOException {
        InputStream is = new FileInputStream(filename);
        client.poll(is);
    }

    public static void testSubscribeFromFile(String filename) throws IOException,
            SubscribeNotPermittedExceptionResponse, DuplicateSubscriptionExceptionResponse,
            ImplementationExceptionResponse, QueryTooComplexExceptionResponse, SecurityExceptionResponse,
            InvalidURIExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse,
            SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse {
        InputStream is = new FileInputStream(filename);
        client.subscribe(is);
    }

    public static void testUnsubscribeFromFile(String filename) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchSubscriptionExceptionResponse, IOException {
        InputStream is = new FileInputStream(filename);
        client.unsubscribe(is);
    }

    private static List<String> listFileNames(String dirName, final String fileNameEndingFilter) {
        List<String> fileNames = new ArrayList<String>();
        File dir = new File(dirName);
        if (dir.isDirectory()) {
            for (File f : dir.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    if (file.getName().endsWith(fileNameEndingFilter)) {
                        return true;
                    }
                    return false;
                }
            })) {
                fileNames.add(f.getPath());
            }
        }
        return fileNames;
    }

    public static void main(String[] args) throws Exception {
        // testPoll();
        List<String> xmlFiles = listFileNames("D:/test", ".xml");
        for (String fileName : xmlFiles) {
            System.out.println(fileName);
            testPollFromFile(fileName);
        }
    }
}
