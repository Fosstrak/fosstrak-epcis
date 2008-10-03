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

package org.fosstrak.epcis.queryclient;

import java.net.URL;

import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.utils.QueryResultsParser;

/**
 * A simple test utility class for demonstrating how to send masterdata queries
 * to the Fosstrak EPCIS query module.
 * 
 * @author Marco Steybe
 */
public class SimpleMasterDataQueryTest {

    // Note: keep the methods in this class static in order to prevent them from
    // being executed when building the project with Maven.

    /**
     * Creates and returns a simple EPCIS masterdata query in its XML form.
     */
    public static String createXmlQuery() {
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
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        // configure query service
        QueryControlClient client = new QueryControlClient();
        client.configureService(new URL("http://demo.fosstrak.org/epcis/query"), null);

        // create a query in its XML form
        String xmlQuery = createXmlQuery();
        // send the query to the query service
        QueryResults results = client.poll(xmlQuery);
        // print the results to System.out
        QueryResultsParser.queryResultsToXml(results, System.out);
    }
}
