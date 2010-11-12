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

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * This class provides useful methods for network configuration.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
public class NetworkConfiguration {
    private String[] bannedInterfaces = {"vmnet", "lo", "eth1", "vmnet8"};
    private Enumeration<NetworkInterface> eNI;

    /** Creates a new instance of <code>NetworkConfiguration</code> */
    public NetworkConfiguration() throws Exception {
        try {
            eNI = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex) {
            ex.printStackTrace();
            throw new Exception("Unable" +
                    "to get any network interface for this " +
                    "host. Is this computer networked ?");
        }
    }

    /** This method gets the first non local host address it finds
     * of all the network interfaces connected to the current computer. */
    public InetAddress firstNonLocalhostAddress()
    throws Exception {
        NetworkInterface cNI;
        Enumeration<InetAddress> eIA;
        InetAddress cIA;
        InetAddress foundIA = null;

        while(eNI.hasMoreElements() && foundIA == null){
            cNI = eNI.nextElement();
            eIA = cNI.getInetAddresses();

            while(eIA.hasMoreElements()){
                cIA = eIA.nextElement();
                if(!cIA.isLoopbackAddress()
                && !(cIA instanceof Inet6Address)) {
                    foundIA = cIA;
                    break;
                }
            }
        }
        if(foundIA == null) {
            throw new Exception("Unable to find a non"
                    + "localhost address.");
        }
        return foundIA;
    }

    /** This method attempts to get the most suitable publicly exposed address
     * The InetAddress it returns definitely exists on this host
     * but might not be the one to bound the services to.
     */
    public InetAddress guessMostSuitablePublicAddress()
    throws Exception {
        NetworkInterface cNI;
        Enumeration<InetAddress> eIA;
        InetAddress cIA;
        InetAddress foundIA = null;

        while(eNI.hasMoreElements() && foundIA == null){
            cNI = eNI.nextElement();
            eIA = cNI.getInetAddresses();

            if(!isBanned(cNI.getDisplayName())) {
                while(eIA.hasMoreElements()){
                    cIA = eIA.nextElement();
                    if(!cIA.isLoopbackAddress()
                    && !(cIA instanceof Inet6Address)) {
                        foundIA = cIA;
                        break;
                    }
                }
            }
        }
        if(foundIA == null) {
            throw new Exception("Unable to find a non"
                    + "localhost address.");
        }
        return foundIA;
    }

    /** This method determines whether the name the the interface
     * is contained in any of the banneds names. */
    private boolean isBanned(String interfaceName) {
        interfaceName = interfaceName.toLowerCase();
        for(String currentBannedWord: bannedInterfaces) {
            if(interfaceName.contains(currentBannedWord)) {
                return true;
            }
        }
        return false;
    }

}
