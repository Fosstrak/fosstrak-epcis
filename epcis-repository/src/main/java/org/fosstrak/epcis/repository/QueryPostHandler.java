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

package org.accada.epcis.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.ImplementationExceptionSeverity;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.log4j.Logger;

/**
 * This is an Axis message chain handler which handles outgoing SOAP messages
 * when they leave the EPCIS query service (QueryOperationsModule). This handler
 * performs the cleanup such as closing the database connection and storing the
 * subscriptions back to the servlet context.
 * 
 * @author Marco Steybe
 */
public class QueryPostHandler extends BasicHandler {

    private static final long serialVersionUID = -1909122676446369360L;
    private static final Logger LOG = Logger.getLogger(QueryPostHandler.class);

    /**
     * Invokes this SoapPostHandler which performs post processing for the EPCIS
     * query service, such as closing the database.
     * 
     * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
     * @param msgContext
     *            the MessageContext to process with this InitHandler.
     * @throws ImplementationException
     *             If an error reading the configuration or setting up the
     *             database occured.
     */
    public void invoke(final MessageContext msgContext)
            throws ImplementationException {
        try {
            Connection dbconnection = (Connection) msgContext.getProperty("dbconnection");
            dbconnection.close();
            LOG.info("Database connection successfully closed.");

            Map<String, QuerySubscriptionScheduled> subscribedMap = (HashMap<String, QuerySubscriptionScheduled>) msgContext.getProperty("subscribedMap");
            HttpServlet servlet = (HttpServlet) msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLET);
            servlet.getServletContext().setAttribute("subscribedMap",
                    subscribedMap);
            LOG.info("Subscriptions stored to servlet context.");
        } catch (SQLException e) {
            ImplementationException iex = new ImplementationException();
            String msg = "Unable to close the database connection: "
                    + e.getMessage();
            iex.setReason(msg);
            iex.setStackTrace(e.getStackTrace());
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw iex;
        }
    }

}
