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

package org.fosstrak.epcis.queryclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fosstrak.epcis.model.ImplementationException;
import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.model.QueryTooLargeException;
import org.fosstrak.epcis.utils.QueryResultsParser;

/**
 * @author Marco Steybe
 */
public class QueryCallbackClient extends HttpServlet implements QueryCallbackInterface {

    private static final long serialVersionUID = 6250815925403597265L;
    private static String callbackResults = null;

    /**
     * {@inheritDoc}
     * 
     * @see org.fosstrak.epcis.queryclient.QueryCallbackInterface#callbackResults(org.fosstrak.epcis.soapapi.QueryResults)
     */
    public void callbackResults(QueryResults result) {
        InputStream is = new ByteArrayInputStream(callbackResults.getBytes());
        try {
            result = QueryResultsParser.parseResults(is);
        } catch (IOException e) {
            // TODO auto-generated catch block
            e.printStackTrace();
        }
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
     * @see org.fosstrak.epcis.queryclient.QueryCallbackInterface#callbackImplementationException(org.fosstrak.epcis.soapapi.ImplementationException)
     */
    public void callbackImplementationException(final ImplementationException e) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.fosstrak.epcis.queryclient.QueryCallbackInterface#callbackQueryTooLargeException(org.fosstrak.epcis.soapapi.QueryTooLargeException)
     */
    public void callbackQueryTooLargeException(final QueryTooLargeException e) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void doPost(final HttpServletRequest req, final HttpServletResponse rsp) throws ServletException,
            IOException {
        rsp.setContentType("text/plain");
        final PrintWriter out = rsp.getWriter();

        // get POST data
        try {
            callbackResults = (String) req.getParameterValues("callbackResults")[0];
        } catch (NullPointerException e) {
            throw new IOException("POST argument \"callbackResults=\" not found");
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
    public void doGet(final HttpServletRequest req, final HttpServletResponse rsp) throws ServletException, IOException {
        rsp.setContentType("text/xml");
        final PrintWriter out = rsp.getWriter();
        out.print(callbackResults);
    }
}
