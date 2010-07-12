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
import java.util.List;

/**
 * A simple test utility class which demonstrates how to send an EPCIS query
 * subscription to the Fosstrak EPCIS repository.
 * 
 * @author Marco Steybe
 */
public class SimpleSubscriptionsTest {

    protected static final String LOCAL_EPCIS_QUERY_URL = "http://localhost:8080/epcis-repository/query/query";
    protected static final String DEMO_EPCIS_QUERY_URL = "http://demo.fosstrak.org/epcis/query";

    // Note: keep the methods in this class static in order to prevent them from
    // being executed when building the project with Maven.

    public static void main(String[] args) throws Exception {
        // configure the query service
        String queryUrl = DEMO_EPCIS_QUERY_URL;
        QueryControlClient client = new QueryControlClient();
        client.configureService(new URL(queryUrl), null);

        // subscribe a query
        System.out.println("Sending subscription:");
        String mySubscrId = "mySubscription";
        String xml = createSubscriptionXml(mySubscrId);
        System.out.println(xml);
        client.subscribe(xml);

        // list subscription IDs
        System.out.println("Listing all subscribed queries:");
        List<String> subscrIds = client.getSubscriptionIds("SimpleEventQuery");
        for (String subscrId : subscrIds) {
            System.out.println(" - " + subscrId);
        }

        // TODO you should wait here and listen for callbacks on the specified address

        // unsubscribe a query
        System.out.println("Unsubscribing query subscription with ID: " + mySubscrId);
        client.unsubscribe(mySubscrId);

        // list subscription IDs ('mySubscription' should not be present anymore)
        System.out.println("Listing all subscribed queries:");
        subscrIds = client.getSubscriptionIds("SimpleEventQuery");
        for (String subscrId : subscrIds) {
            System.out.println(" - " + subscrId);
        }
    }

    private static String createSubscriptionXml(String subscriptionId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<epcisq:Subscribe xmlns:epcisq=\"urn:epcglobal:epcis-query:xsd:1\">\n");
        sb.append("<queryName>SimpleEventQuery</queryName>\n");
        sb.append("<params>\n");
        sb.append("  <param>\n");
        sb.append("    <name>eventType</name>\n");
        sb.append("    <value>\n");
        sb.append("      <string>ObjectEvent</string>\n");
        sb.append("    </value>\n");
        sb.append("  </param>\n");
        sb.append("  <param>\n");
        sb.append("    <name>MATCH_epc</name>\n");
        sb.append("    <value>\n");
        sb.append("      <string>urn:epc:id:sgtin:0614141.107346.2017</string>\n");
        sb.append("    </value>\n");
        sb.append("  </param>\n");
        sb.append("</params>\n");
        sb.append("<dest>http://localhost:8888/</dest> <!-- this is where query results will be delivered to -->\n");
        sb.append("<controls>\n");
        sb.append("  <schedule>\n");
        sb.append("    <second>0</second> <!-- every full minute -->\n");
        sb.append("  </schedule>\n");
        sb.append("  <initialRecordTime>2008-03-16T00:00:00+01:00</initialRecordTime>\n");
        sb.append("  <reportIfEmpty>false</reportIfEmpty>\n");
        sb.append("</controls>\n");
        sb.append("<subscriptionID>").append(subscriptionId).append("</subscriptionID>\n");
        sb.append("</epcisq:Subscribe>");
        return sb.toString();
    }
}
