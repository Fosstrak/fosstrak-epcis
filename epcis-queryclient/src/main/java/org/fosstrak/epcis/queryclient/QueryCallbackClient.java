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
