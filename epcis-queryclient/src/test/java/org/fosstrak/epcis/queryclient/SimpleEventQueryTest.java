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
 * A simple test utility class for demonstrating how to send simple event
 * queries to the Fosstrak EPCIS query service.
 * 
 * @author Marco Steybe
 */
public class SimpleEventQueryTest {

    // Note: keep the methods in this class static in order to prevent them from
    // being executed when building the project with Maven.

    /**
     * Creates and returns a simple EPCIS query in its XML form.
     */
    public static String createXmlQuery() {
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
        return sb.toString();
    }

    /**
     * Creates and returns a simple EPCIS query Poll object.
     */
    public static Poll createPoll() {
        // construct the query parameters
        QueryParam queryParam1 = new QueryParam();
        queryParam1.setName("eventType");
        ArrayOfString queryParamValue1 = new ArrayOfString();
        queryParamValue1.getString().add("ObjectEvent");
        queryParam1.setValue(queryParamValue1);

        QueryParam queryParam2 = new QueryParam();
        queryParam2.setName("MATCH_epc");
        ArrayOfString queryParamValue2 = new ArrayOfString();
        queryParamValue2.getString().add("urn:epc:id:sgtin:0000001.000001.0001");
        queryParam2.setValue(queryParamValue2);

        // add the query parameters to the list of parameters
        QueryParams queryParams = new QueryParams();
        queryParams.getParam().add(queryParam1);
        queryParams.getParam().add(queryParam2);

        // create the Poll object
        Poll poll = new Poll();
        poll.setQueryName("SimpleEventQuery");
        poll.setParams(queryParams);
        return poll;
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

        // create a query Poll object and send it to the query service
        Poll poll = createPoll();
        results = client.poll(poll);
        QueryResultsParser.queryResultsToXml(results, System.out);
    }
}
