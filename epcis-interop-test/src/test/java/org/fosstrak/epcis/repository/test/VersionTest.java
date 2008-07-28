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

package org.fosstrak.epcis.repository.test;

import junit.framework.TestCase;

import org.fosstrak.epcis.queryclient.QueryControlClient;

/**
 * Test for getStandardVersion() and getVendorVersion() (SE47 and SE67).
 * 
 * @author Marco Steybe
 */
public class VersionTest extends TestCase {

    private static QueryControlClient client = new QueryControlClient();

    /**
     * Tests if the supported Standard Version is "1.0".
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE47() throws Exception {
        String stdVersion = client.getStandardVersion();
        assertEquals(stdVersion, "1.0");
    }

    /**
     * Tests if the vendor version is defined.
     * 
     * @throws Exception
     *             Any exception, caught by the JUnit framework.
     */
    public void testSE67() throws Exception {
        String version = client.getVendorVersion();
        assertTrue(version.startsWith(""));
    }
}
