/*
 * Copyright (c) 2006, 2007, ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the ETH Zurich nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
        assertTrue(version.startsWith("http://www.accada.org/releases/"));
    }
}
