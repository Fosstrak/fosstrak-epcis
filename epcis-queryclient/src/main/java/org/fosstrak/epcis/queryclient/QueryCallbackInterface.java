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

import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryTooLargeException;

/**
 * @author Marco Steybe
 */
public interface QueryCallbackInterface {

    /**
     * Performs a callback for a standing query. When the callback returns, the
     * given QueryResults object will be populated with a result of a standing
     * query.
     * 
     * @param resultData
     *            The QueryResults object to be populated.
     */
    void callbackResults(QueryResults resultData);

    /**
     * Performs a callback for a standing query when the query threw a
     * QueryTooLargeException. When the callback returns, the given
     * QueryTooLargeException object will be populated with the corresponding
     * exception.
     * 
     * @param e
     *            The QueryTooLargeException to be populated
     */
    void callbackQueryTooLargeException(QueryTooLargeException e);

    /**
     * Performs a callback for a standing query when the query threw a
     * ImplementationException. When the callback returns, the given
     * ImplementationException object will be populated with the corresponding
     * exception.
     * 
     * @param e
     *            The ImplementationException to be populated
     */
    void callbackImplementationException(ImplementationException e);
}
