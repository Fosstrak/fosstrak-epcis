/*
 * Copyright (C) 2010 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org) and
 * was developed as part of the webofthings.com initiative.
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
package org.epcis.fosstrak.restadapter.util;

import java.net.*;

/**
 * Class to detect the actual IP address.
 *
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>, Mathias Mueller mathias.mueller(at)unifr.ch
 *
 *
 */
public class IPAddress {

    public static final String LOCALHOST = "localhost";

    /**
     * Set the actual IP address to an URL containing localhost
     *
     *
     * @param uri
     *
     * @return
     */
    public static String TRANSFORM_LOCALHOST_TO_IP(String uri) {
        if (uri.contains(LOCALHOST)) {
            uri = uri.replaceFirst(LOCALHOST, GET_MY_IP());
        }

        return uri;
    }

    /**
     * Get the actal IP address
     *
     *
     * @return
     */
    public static String GET_LOCAL_IP() {
        String res = "";
        InetAddress myIP;

        try {
            myIP = InetAddress.getLocalHost();
            res = myIP.getHostAddress();

            


        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }

        return res;
    }

    /**
     * Get the actual IP address and make sure it is not localhost (linux)
     *
     *
     * @return
     */
    public static String GET_MY_IP() {
        String res = "";
        InetAddress myIP;

        try {
            myIP = new NetworkConfiguration().guessMostSuitablePublicAddress();
            res = myIP.getHostAddress();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return res;
    }

}
