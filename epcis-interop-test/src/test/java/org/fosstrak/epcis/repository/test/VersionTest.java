/*
 * Copyright (C) 2007, ETH Zurich
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.accada.epcis.repository.test;

import java.io.IOException;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryControlClient;

/**
 * Test for getStandardVersion() and getVendorVersion() (SE47 and SE67).
 * 
 * @author Marco Steybe
 */
public class VersionTest extends TestCase {

    private QueryControlClient client = new QueryControlClient();

    /**
     * Tests if the supported Standard Version is "1.0".
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an EPCIS query service error occured.
     */
    public void testSE47() throws IOException, ServiceException {
        String stdVersion = client.getStandardVersion();
        assertEquals(stdVersion, "1.0");
    }

    /**
     * Tests if the vendor version is defined.
     * 
     * @throws IOException
     *             If an I/O error occured.
     * @throws ServiceException
     *             If an EPCIS query service error occured.
     */
    public void testSE67() throws IOException, ServiceException {
        String version = client.getVendorVersion();
        assertTrue(version.startsWith(""));
    }
}
