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
import org.accada.epcis.soap.SecurityExceptionResponse;
import org.accada.epcis.soap.SubscribeNotPermittedExceptionResponse;
import org.accada.epcis.soap.SubscriptionControlsExceptionResponse;
import org.accada.epcis.soap.ValidationExceptionResponse;

/**
 * A simple test utility class for sending subscriptions (available from file
 * names) to a repository.
 * <p>
 * Note: keep the methods in this class static in order to prevent them from
 * being executed when building the project with Maven.
 * 
 * @author Marco Steybe
 */
public class SimpleSubscriptionsTest {

    private static QueryControlClient client = new QueryControlClient();

    public static void subscribeFromFile(String filename) throws IOException, SubscribeNotPermittedExceptionResponse,
            DuplicateSubscriptionExceptionResponse, ImplementationExceptionResponse, QueryTooComplexExceptionResponse,
            SecurityExceptionResponse, InvalidURIExceptionResponse, ValidationExceptionResponse,
            NoSuchNameExceptionResponse, SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse {
        InputStream is = new FileInputStream(filename);
        client.subscribe(is);
    }

    public static void unsubscribeFromFile(String filename) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchSubscriptionExceptionResponse, IOException {
        InputStream is = new FileInputStream(filename);
        client.unsubscribe(is);
    }

    public static void main(String[] args) throws Exception {
        // change this flag, if you want to run unsubscribe
        boolean doSubscribe = true;
        String dir = "D:/test/subscriptions";

        /*
         * NOTE: to subscribe, use the subscribe.xml extension (e.g.,
         * test1_subscribe.xml) in file names; to unsubscribe, use the
         * unsubscribe.xml extension. The files must be located in the specified
         * directory
         */

        String ext = "subscribe.xml";
        if (!doSubscribe) {
            ext = "unsubscribe.xml";
        }
        List<String> xmlFiles = listFileNames(dir, ext);
        for (String fileName : xmlFiles) {
            System.out.println(fileName);
            if (doSubscribe) {
                subscribeFromFile(fileName);
            } else {
                unsubscribeFromFile(fileName);
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
