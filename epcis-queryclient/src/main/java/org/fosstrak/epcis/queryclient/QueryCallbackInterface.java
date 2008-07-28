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

import org.fosstrak.epcis.model.ImplementationException;
import org.fosstrak.epcis.model.QueryResults;
import org.fosstrak.epcis.model.QueryTooLargeException;

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
