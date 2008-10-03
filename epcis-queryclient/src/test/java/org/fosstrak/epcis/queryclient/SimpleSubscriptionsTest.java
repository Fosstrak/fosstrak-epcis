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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.fosstrak.epcis.soap.DuplicateSubscriptionExceptionResponse;
import org.fosstrak.epcis.soap.ImplementationExceptionResponse;
import org.fosstrak.epcis.soap.InvalidURIExceptionResponse;
import org.fosstrak.epcis.soap.NoSuchNameExceptionResponse;
import org.fosstrak.epcis.soap.NoSuchSubscriptionExceptionResponse;
import org.fosstrak.epcis.soap.QueryParameterExceptionResponse;
import org.fosstrak.epcis.soap.QueryTooComplexExceptionResponse;
import org.fosstrak.epcis.soap.SecurityExceptionResponse;
import org.fosstrak.epcis.soap.SubscribeNotPermittedExceptionResponse;
import org.fosstrak.epcis.soap.SubscriptionControlsExceptionResponse;
import org.fosstrak.epcis.soap.ValidationExceptionResponse;

/**
 * A simple test utility class for sending subscriptions to a repository.
 * 
 * @author Marco Steybe
 */
public class SimpleSubscriptionsTest {

    // Note: keep the methods in this class static in order to prevent them from
    // being executed when building the project with Maven.

    public static void subscribeFromFile(String filename, QueryControlClient client) throws IOException,
            SubscribeNotPermittedExceptionResponse, DuplicateSubscriptionExceptionResponse,
            ImplementationExceptionResponse, QueryTooComplexExceptionResponse, SecurityExceptionResponse,
            InvalidURIExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse,
            SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse {
        InputStream is = new FileInputStream(filename);
        client.subscribe(is);
    }

    public static void unsubscribeFromFile(String filename, QueryControlClient client)
            throws ImplementationExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse,
            NoSuchSubscriptionExceptionResponse, IOException {
        InputStream is = new FileInputStream(filename);
        client.unsubscribe(is);
    }

    public static void main(String[] args) throws Exception {
        // configure query service
        QueryControlClient client = new QueryControlClient();
        client.configureService(new URL("http://demo.fosstrak.org/epcis/query"), null);

        // change this flag, if you want to run unsubscribe
        boolean doSubscribe = false;
        String dir = "D:/test/subscriptions";

        /*
         * NOTE: to subscribe, use the subscribe.xml extension (e.g.,
         * test1_subscribe.xml) in file names; to unsubscribe, use the
         * unsubscribe.xml extension. The files must be located in the specified
         * directory
         */

        String ext = "_subscribe.xml";
        if (!doSubscribe) {
            ext = "_unsubscribe.xml";
        }
        List<String> xmlFiles = listFileNames(dir, ext);
        for (String fileName : xmlFiles) {
            System.out.println(fileName);
            if (doSubscribe) {
                subscribeFromFile(fileName, client);
            } else {
                unsubscribeFromFile(fileName, client);
            }
        }
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
}
