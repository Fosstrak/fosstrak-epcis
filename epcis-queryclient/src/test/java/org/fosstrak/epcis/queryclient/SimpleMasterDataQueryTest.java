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

import org.fosstrak.epcis.model.ArrayOfString;
import org.fosstrak.epcis.model.Poll;
import org.fosstrak.epcis.model.QueryParam;
import org.fosstrak.epcis.model.QueryParams;
import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.utils.QueryResultsParser;

/**
 * A simple test utility class for demonstrating how to send masterdata queries
 * to the Fosstrak EPCIS query module.
 * 
 * @author Marco Steybe
 */
public class SimpleMasterDataQueryTest {

    protected static final String LOCAL_EPCIS_QUERY_URL = "http://localhost:8080/epcis-repository/query";
    protected static final String DEMO_EPCIS_QUERY_URL = "http://demo.fosstrak.org/epcis/query";
    
    // Note: keep the methods in this class static in order to prevent them from
    // being executed when building the project with Maven.

    public static void main(String[] args) throws Exception {
        // configure the query service
        String queryUrl = LOCAL_EPCIS_QUERY_URL;
        QueryControlClient client = new QueryControlClient();
        client.configureService(new URL(queryUrl), null);

        // create a query in its XML form and send it to the repository
        String xmlQuery = createPollXml();
        System.out.println("Sending query:");
        System.out.println(xmlQuery);
        QueryResults results = client.poll(xmlQuery);
        // print the results to System.out
        QueryResultsParser.queryResultsToXml(results, System.out);
        
        // create a query Poll object and send it to the query service
        Poll poll = createPoll();
        System.out.println("Sending query:");
        results = client.poll(poll);
        QueryResultsParser.queryResultsToXml(results, System.out);
    }

    /**
     * Creates and returns a simple EPCIS masterdata query in its XML form.
     */
    public static String createPollXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<epcisq:Poll xmlns:epcisq=\"urn:epcglobal:epcis-query:xsd:1\">\n");
        sb.append("<queryName>SimpleMasterDataQuery</queryName>\n");
        sb.append("<params>\n");
        sb.append("  <param>\n");
        sb.append("    <name>includeAttributes</name>\n");
        sb.append("    <value>true</value>\n");
        sb.append("  </param>\n");
        sb.append("  <param>\n");
        sb.append("    <name>includeChildren</name>\n");
        sb.append("    <value>true</value>\n");
        sb.append("  </param>\n");
        sb.append("  <param>\n");
        sb.append("    <name>EQ_name</name>\n");
        sb.append("    <value><string>urn:epc:id:sgln:0614141.00729.shipping</string></value>\n");
        sb.append("  </param>\n");
        sb.append("</params>\n");
        sb.append("</epcisq:Poll>");
        return sb.toString();
    }

    /**
     * Creates and returns a simple EPCIS query Poll object.
     */
    private static Poll createPoll() {
        // construct the query parameters
        QueryParam queryParam1 = new QueryParam();
        queryParam1.setName("includeAttributes");
        queryParam1.setValue("true");

        QueryParam queryParam2 = new QueryParam();
        queryParam2.setName("includeChildren");
        queryParam2.setValue("true");

        QueryParam queryParam3 = new QueryParam();
        queryParam3.setName("EQ_name");
        ArrayOfString queryParamValue3 = new ArrayOfString();
        queryParamValue3.getString().add("urn:epc:id:sgln:0614141.00729.shipping");
        queryParam3.setValue(queryParamValue3);

        // add the query parameters to the list of parameters
        QueryParams queryParams = new QueryParams();
        queryParams.getParam().add(queryParam1);
        queryParams.getParam().add(queryParam2);
        queryParams.getParam().add(queryParam3);

        // create the Poll object
        Poll poll = new Poll();
        poll.setQueryName("SimpleMasterDataQuery");
        poll.setParams(queryParams);
        return poll;
    }
}
