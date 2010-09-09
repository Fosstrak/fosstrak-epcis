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

import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.fosstrak.epcis.captureclient.CaptureClient;
import org.fosstrak.epcis.utils.FosstrakDatabaseHelper;

/**
 * Tests the repository's <code>dbReset</code> operation using the
 * {@link CaptureClient}.
 * 
 * @author Marco Steybe
 */
public class DbResetOperationTest extends FosstrakInteropTestCase {

    private CaptureClient capture = new CaptureClient();
    
    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    public void testDummyDbResetOperation() throws Exception {
        ITable table = FosstrakDatabaseHelper.getObjectEventByEpc(getConnection(), "urn:epc:id:sgtin:0057000.123780.7788");
        assertEquals(0, table.getRowCount());
        
        long t1 = System.currentTimeMillis();
        int response = capture.dbReset();
        assertEquals(200, response);
        System.out.println("dbReset operation successful in " + (System.currentTimeMillis() - t1) + "ms");

        table = FosstrakDatabaseHelper.getObjectEventByEpc(getConnection(), "urn:epc:id:sgtin:0057000.123780.7788");
        assertEquals(1, table.getRowCount());
    }
}
