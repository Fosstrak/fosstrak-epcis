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

import java.rmi.RemoteException;
import java.util.List;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.captureclient.CaptureClient;
import org.accada.epcis.queryclient.QueryControlClient;

/**
 * Test for getQueryNames() (SE45).
 * 
 * @author Marco Steybe
 */
public class QueryNamesTest extends TestCase {

    private QueryControlClient client = new QueryControlClient();

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
     * Tests if the two query types "SimpleEventQuery" and
     * "SimpleMasterDataQuery" are supported by the implementation.
     * 
     * @throws ServiceException
     *             If an EPCIS query service error occurs.
     * @throws RemoteException
     *             If an Axis error occurs.
     */
    public void testSE45() throws ServiceException, RemoteException {
        List<String> queryNames = client.getQueryNames();

        // must contain SimpleEventQuery and SimpleMasterDataQuery
        assertTrue(queryNames.size() == 2);

        assertTrue(queryNames.contains("SimpleEventQuery"));
        assertTrue(queryNames.contains("SimpleMasterDataQuery"));
    }
}
