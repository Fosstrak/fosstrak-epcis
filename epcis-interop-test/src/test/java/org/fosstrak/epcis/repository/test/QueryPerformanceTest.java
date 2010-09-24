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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.queryclient.QueryControlClient;

/**
 * Runs some sample queries against a given state of a database including 1000
 * ObjectEvents and measures the response times.
 * 
 * @author Marco Steybe
 */
public class QueryPerformanceTest extends FosstrakInteropTestCase {

    private static final String PERFORMANCE_QUERY_1 = "src/test/resources/queries/PerformanceQuery1.xml";
	private static final String PATH_TO_DATASET = "src/test/resources/dbunit/perf_test_data.xml";
    private static final String DEFAULT_QUERY_URL = "http://localhost:8080/epcis-repository/query";
	
	private static QueryControlClient client = new QueryControlClient(DEFAULT_QUERY_URL);

    public void testPerformance() throws Exception {
        long t1 = System.currentTimeMillis();
        QueryResults res = client.poll(new FileInputStream(PERFORMANCE_QUERY_1));
        long t2 = System.currentTimeMillis();

        // make sure there is a result
        assertNotNull(res);
        assertNotNull(res.getResultsBody());
        assertNotNull(res.getResultsBody().getEventList());
        List<Object> events = res.getResultsBody().getEventList().getObjectEventOrAggregationEventOrQuantityEvent();
        assertNotNull(events);
        int numOfObjEvents = events.size();
        assertEquals("query must return 1000 events", 1000, numOfObjEvents);

        // output response time
        System.out.println("response time initial run: " + (t2 - t1) + "ms");

        // run the same query several times
        int count = 10;
        System.out.println("running " + count + " tests ... ");
        long sum = 0;
        long avg = 0;
        long max = 0;
        long min = Long.MAX_VALUE;
        for (int i = 0; i < count; i++) {
            t1 = System.currentTimeMillis();
            client.poll(new FileInputStream(PERFORMANCE_QUERY_1));
            t2 = System.currentTimeMillis();
            sum = sum + t2 - t1;
            max = Math.max(t2 - t1, max);
            min = Math.min(t2 - t1, min);
            System.out.println(t2 - t1 + "ms");
        }
        avg = sum / count;

        // output response time
        System.out.println("----- results -----");
        System.out.println("min: " + min + "ms");
        System.out.println("max: " + max + "ms");
        System.out.println("avg: " + avg + "ms");
    }
    
    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
    	return DatabaseOperation.CLEAN_INSERT;
    }
    
    @Override
    protected IDataSet getDataSet() throws Exception {
        InputStream is = new FileInputStream(PATH_TO_DATASET);
        return new XmlDataSet(is);
    }
}
