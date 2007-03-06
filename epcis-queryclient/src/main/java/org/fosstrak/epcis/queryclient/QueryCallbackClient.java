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

package org.accada.epcis.queryclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.utils.QueryResultsParser;

/**
 * @author Marco Steybe
 */
public class QueryCallbackClient extends HttpServlet implements
        QueryCallbackInterface {

    private static final long serialVersionUID = 6250815925403597265L;
    private static String callbackResults = null;

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryCallbackInterface#callbackResults(org.accada.epcis.soapapi.QueryResults)
     */
    public void callbackResults(QueryResults result) {
        InputStream is = new ByteArrayInputStream(callbackResults.getBytes());
        result = QueryResultsParser.parseQueryResults(is);
    }

    /**
     * Performs a callback for a standing query. When the callback returns, the
     * given String will be populated with an XML representation of a standing
     * query result.
     * 
     * @param result
     *            The String to be populated.
     */
    public static void callbackQueryResults(String result) {
        result = callbackResults;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryCallbackInterface#callbackImplementationException(org.accada.epcis.soapapi.ImplementationException)
     */
    public void callbackImplementationException(ImplementationException e) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.accada.epcis.queryclient.QueryCallbackInterface#callbackQueryTooLargeException(org.accada.epcis.soapapi.QueryTooLargeException)
     */
    public void callbackQueryTooLargeException(QueryTooLargeException e) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void doPost(final HttpServletRequest req,
            final HttpServletResponse rsp) throws ServletException, IOException {
        rsp.setContentType("text/plain");
        final PrintWriter out = rsp.getWriter();

        // get POST data
        try {
            callbackResults = (String) req.getParameterValues("callbackResults")[0];
        } catch (NullPointerException e) {
            throw new IOException(
                    "POST argument \"callbackResults=\" not found");
        }

        out.println("Callback OK.");
        out.flush();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void doGet(final HttpServletRequest req,
            final HttpServletResponse rsp) throws ServletException, IOException {
        rsp.setContentType("text/xml");
        final PrintWriter out = rsp.getWriter();
        out.print(callbackResults);
    }
}
