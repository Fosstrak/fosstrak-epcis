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

package org.accada.epcis.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.ImplementationExceptionSeverity;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author Marco Steybe
 */
public class QueryInitHandler extends BasicHandler {

    private static final long serialVersionUID = 8796489572394952784L;

    // don't declare this logger 'static final' because this would generate a
    // 'logger not initialized' message
    private Logger log = null;

    /**
     * Invokes this SoapPreHandler which performs the initialization for the
     * EPCIS query service, such as reading configuration and setting up the
     * database.
     * 
     * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
     * @param msgContext
     *            The MessageContext to process with this handler.
     * @throws ImplementationException
     *             If an error reading the configuration or setting up the
     *             database occured.
     */
    public void invoke(final MessageContext msgContext)
            throws ImplementationException {
        try {
            ServletContext ctx = ((HttpServlet) msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLET)).getServletContext();
            String servletPath = ctx.getRealPath("/");
            String log4jConfigFile = ctx.getInitParameter("log4jConfigFile");
            if (log4jConfigFile != null) {
                // if no log4j properties file found, then do not try
                // to load it (the application runs without logging)
                PropertyConfigurator.configure(servletPath + log4jConfigFile);
            }
            log = Logger.getLogger("SoapPreHandler.class");

            Context initContext = new InitialContext();
            Context env = (Context) initContext.lookup("java:comp/env");
            DataSource db = (DataSource) env.lookup("jdbc/EPCISDB");
            Connection dbconnection = db.getConnection();
            log.info("Connection to database successfully established.");

            String delimiter = dbconnection.getMetaData().getIdentifierQuoteString();
            log.debug("Resolved string delimiter used to quote SQL identifiers as '"
                    + delimiter + "'.");

            Map<String, QuerySubscriptionScheduled> subscribedMap = (HashMap<String, QuerySubscriptionScheduled>) ctx.getAttribute("subscribedMap");

            msgContext.setProperty("dbconnection", dbconnection);
            msgContext.setProperty("delimiter", delimiter);
            msgContext.setProperty("subscribedMap", subscribedMap);
        } catch (NamingException e) {
            ImplementationException iex = new ImplementationException();
            String msg = "Unable to read configuration, check META-INF/context.xml for Resource 'jdbc/EPCISDB'.";
            iex.setReason(msg);
            iex.setStackTrace(e.getStackTrace());
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw iex;
        } catch (SQLException e) {
            ImplementationException iex = new ImplementationException();
            String msg = "Unable to connect to the database: " + e.getMessage();
            iex.setReason(msg);
            iex.setStackTrace(e.getStackTrace());
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw iex;
        }
    }

    /**
     * Closes the database connection and stores the subscriptions to the
     * servlet context when the services runs into an exception. {@inheritDoc}
     * 
     * @see org.apache.axis.handlers.BasicHandler#onFault(org.apache.axis.MessageContext)
     */
    public void onFault(final MessageContext msgContext) {
        log.info("There was an Axis Fault. If this was not an intended 'USER ERROR' check the axis log for more details.");

        try {
            Connection dbconnection = (Connection) msgContext.getProperty("dbconnection");
            dbconnection.close();
            log.info("Database connection successfully closed.");
        } catch (SQLException e) {
            String msg = "Unable to close the database connection: "
                    + e.getMessage();
            log.error(msg, e);
        }

        Map<String, QuerySubscriptionScheduled> subscribedMap = (HashMap<String, QuerySubscriptionScheduled>) msgContext.getProperty("subscribedMap");
        HttpServlet servlet = (HttpServlet) msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLET);
        servlet.getServletContext().setAttribute("subscribedMap", subscribedMap);
        log.info("Subscriptions stored to servlet context.");
    }
}
