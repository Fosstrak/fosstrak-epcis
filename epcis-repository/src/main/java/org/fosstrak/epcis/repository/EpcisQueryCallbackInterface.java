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

package org.accada.epcis.repository;

import org.accada.epcis.model.ImplementationException;
import org.accada.epcis.model.QueryResults;
import org.accada.epcis.model.QueryTooLargeException;

/**
 * <p>
 * The EPCIS Query Callback Interface is the path by which an EPCIS service
 * delivers standing query results to a client.
 * <p>
 * Each time the EPCIS service executes a standing query according to a
 * <code>QuerySchedule</code>, it SHALL attempt to deliver results to the
 * subscriber by invoking one of the three methods of the Query Callback
 * Interface. If the query executed normally, the EPCIS service SHALL invoke the
 * {@link callbackResults(QueryResults) callbackResults} method. If the query
 * resulted in a <code>QueryTooLargeException</code> or
 * <code>ImplementationException</code>, the EPCIS service SHALL invoke the
 * corresponding method of the Query Callback Interface.
 * <p>
 * Note that "exceptions" in the Query Callback Interface are not exceptions in
 * the usual sense of an API exception, because they are not raised as a
 * consequence of a client invoking a method. Instead, the exception is
 * delivered to the recipient in a similar manner to a normal result, as an
 * argument to an interface method.
 */
public interface EpcisQueryCallbackInterface {

    /**
     * Delivers the results of a standing query to the subscriber when the query
     * executed normally.
     * 
     * @param resultData
     *            The results of the execution of a standing query.
     */
    public void callbackResults(QueryResults resultData);

    /**
     * Delivers the results of a standing query to the subscriber when the query
     * resulted in a <code>QueryTooLargeException</code>.
     * 
     * @param e
     *            The <code>QueryTooLargeException</code> thrown during
     *            execution of a standing query.
     */
    public void callbackQueryTooLargeException(QueryTooLargeException e);

    /**
     * Delivers the results of a standing query to the subscriber when the query
     * resulted in a <code>ImplementationException</code>.
     * 
     * @param e
     *            The <code>ImplementationException</code> thrown during
     *            execution of a standing query.
     */
    public void callbackImplementationException(ImplementationException e);
}
