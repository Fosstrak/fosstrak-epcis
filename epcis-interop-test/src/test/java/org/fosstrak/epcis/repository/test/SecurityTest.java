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

package org.accada.epcis.repository.test;

import org.accada.epcis.captureclient.CaptureClient;

import junit.framework.TestCase;

/**
 * Tests for security, which is not implemented :-).
 * 
 * @author Andrea Grössbauer
 * @author Marco Steybe
 */
public class SecurityTest extends TestCase {

    /**
     * Reset database.
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        CaptureClient captureClient = new CaptureClient();
        captureClient.purgeRepository();
        CaptureData captureData = new CaptureData();
        captureData.captureAll();
    }

    /**
     * Tests if SecurityException is raised.
     */
    public void testSE74() {
        fail("No security implemented!");
    }
}
