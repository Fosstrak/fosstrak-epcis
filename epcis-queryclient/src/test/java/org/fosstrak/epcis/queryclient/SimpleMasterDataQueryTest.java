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

import org.accada.epcis.model.QueryResults;
import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.NoSuchNameExceptionResponse;
import org.accada.epcis.soap.QueryParameterExceptionResponse;
import org.accada.epcis.soap.QueryTooComplexExceptionResponse;
import org.accada.epcis.soap.QueryTooLargeExceptionResponse;
import org.accada.epcis.soap.SecurityExceptionResponse;
import org.accada.epcis.soap.ValidationExceptionResponse;
import org.accada.epcis.utils.QueryResultsParser;

/**
 * A simple test utility class for quickly testing masterdata queries against
 * the Accada EPCIS query module.
 * <p>
 * Note: keep the methods in this class static in order to prevent them from
 * being executed when building the project with Maven.
 * 
 * @author Marco Steybe
 */
public class SimpleMasterDataQueryTest {

    private static QueryControlClient client = new QueryControlClient();

    /**
     * Creates a simple EPCIS masterdata query, sends it to the EPCIS query
     * service for processing and prints the response to System.out.
     */
    public static void testPoll() throws QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse,
            QueryParameterExceptionResponse, IOException, ImplementationExceptionResponse {
        StringBuilder sb = new StringBuilder();
        sb.append("<epcisq:Poll xmlns:epcisq=\"urn:epcglobal:epcis-query:xsd:1\">");
        sb.append("<queryName>SimpleMasterDataQuery</queryName>");
        sb.append("<params>");
        sb.append("<param>");
        sb.append("<name>includeAttributes</name>");
        sb.append("<value>true</value>");
        sb.append("</param>");
        sb.append("<param>");
        sb.append("<name>includeChildren</name>");
        sb.append("<value>true</value>");
        sb.append("</param>");
        sb.append("<param>");
        sb.append("<name>EQ_name</name>");
        sb.append("<value><string>urn:epcglobal:fmcg:loc:0614141073467</string></value>");
        sb.append("</param>");
        sb.append("</params>");
        sb.append("</epcisq:Poll>");

        QueryResults results = client.poll(sb.toString());
        QueryResultsParser.queryResultsToXml(results, System.out);
    }

    public static void main(String[] args) throws Exception {
        testPoll();
    }
}
