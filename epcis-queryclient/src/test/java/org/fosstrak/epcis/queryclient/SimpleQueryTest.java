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

import java.io.IOException;

import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.NoSuchNameExceptionResponse;
import org.accada.epcis.soap.QueryParameterExceptionResponse;
import org.accada.epcis.soap.QueryTooComplexExceptionResponse;
import org.accada.epcis.soap.QueryTooLargeExceptionResponse;
import org.accada.epcis.soap.SecurityExceptionResponse;
import org.accada.epcis.soap.ValidationExceptionResponse;
import org.accada.epcis.soap.model.QueryResults;
import org.accada.epcis.utils.QueryResultsParser;

/**
 * A simple test utility class for quickly testing event queries against the
 * Accada EPCIS query module.
 * 
 * @author Marco Steybe
 */
public class SimpleQueryTest {

    private static QueryControlClient client = new QueryControlClient();

    /**
     * Creates a simple EPCIS query, sends it to the EPCIS query service for
     * processing and prints the response to System.out.
     */
    public static void testQuery() throws QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse,
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

    /**
     * Used to manually start this test.
     * 
     * @param args
     *            nothing expected.
     */
    public static void main(String[] args) throws Exception {
        testQuery();
    }
}
