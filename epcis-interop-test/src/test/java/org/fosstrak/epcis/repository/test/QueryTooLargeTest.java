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

import org.dbunit.operation.DatabaseOperation;
import org.fosstrak.epcis.queryclient.QueryControlClient;
import org.fosstrak.epcis.soap.QueryTooLargeExceptionResponse;
import org.fosstrak.epcis.utils.QueryCallbackListener;

/**
 * Tests for QueryTooLargeException (SE50, SE68). TODO: this cannot yet be
 * tested automatically, idea: need a repository running with a number of x
 * events in the database and the application property 'maxQueryResultRows' set
 * to less than x.
 * 
 * @author Marco Steybe
 */
public class QueryTooLargeTest extends FosstrakInteropTestCase {

	private static final String PATH = "src/test/resources/queries/webservice/requests/";
    private static final String DEFAULT_QUERY_URL = "http://localhost:8080/epcis-repository/query";

    private static QueryControlClient client = new QueryControlClient(DEFAULT_QUERY_URL);

	@Override
	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.NONE;
	}

	/**
	 * Tests if QueryTooLargeException is raised.
	 * 
	 * @throws Exception
	 *             Any exception, caught by the JUnit framework.
	 */
	public void _testSE50() throws Exception {
		final String query = "Test-EPCIS10-SE50-Request-1-Poll.xml";
		InputStream fis = new FileInputStream(PATH + query);
		try {
			client.poll(fis);
			fis.close();
			fail("QueryTooLargeException expected");
		} catch (QueryTooLargeExceptionResponse e) {
			// ok
			fis.close();
		}
	}

	/**
	 * Tests if QueryTooLargeException is raised (callback).
	 * 
	 * @throws Exception
	 *             Any exception, caught by the JUnit framework.
	 */
	public void _testSE68() throws Exception {
		// subscribe query
		final String query = "Test-EPCIS10-SE68-Request-1-Subscribe.xml";
		InputStream fis = new FileInputStream(PATH + query);
		client.subscribe(fis);
		fis.close();

		// start subscription response listener
		QueryCallbackListener listener = QueryCallbackListener.getInstance();
		if (!listener.isRunning()) {
			listener.start();
		}
		System.out.println("waiting ...");
		synchronized (listener) {
			try {
				listener.wait(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		String resp = listener.fetchResponse();
		assertNotNull(resp);

		client.unsubscribe("QuerySE68"); // clean up
		assertTrue(resp.contains("QueryTooLargeException"));
	}
}
