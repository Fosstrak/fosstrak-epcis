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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
 * This is an Axis message chain handler which handles incoming SOAP messages
 * before they reach the EPCIS query service (QueryOperationsModule). This
 * handler performs the initialization for the query service, such as reading
 * configuration properties, setting up the database, and loading the
 * subscriptions from the servlet context. On fault in the query service, this
 * handler also performs the cleanup such as closing the database connection and
 * storing the subscriptions back to the servlet context.
 * 
 * @author Marco Steybe
 */
public class QueryInitHandler extends BasicHandler {

    private static final long serialVersionUID = 8796489572394952784L;

    // don't declare this logger 'static final' because this would generate a
    // 'logger not initialized' message
    private Logger log = null;

    /**
     * Invokes this QueryInitHandler which performs the initialization for the
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
            log = Logger.getLogger("QueryInitHandler.class");

            // read application properties
            String appConfigFile = ctx.getInitParameter("appConfigFile");
            Properties properties = new Properties();
            try {
                InputStream is = new FileInputStream(servletPath
                        + appConfigFile);
                properties.load(is);
                is.close();
            } catch (IOException e) {
                log.error("Unable to load application properties from "
                        + servletPath + appConfigFile);
            }

            // read db connection parameters
            Context initContext = new InitialContext();
            Context env = (Context) initContext.lookup("java:comp/env");
            DataSource db = (DataSource) env.lookup("jdbc/EPCISDB");
            Connection dbconnection = db.getConnection();
            log.info("Connection to database successfully established.");

            String delimiter = dbconnection.getMetaData().getIdentifierQuoteString();
            log.debug("Resolved string delimiter used to quote SQL identifiers as '"
                    + delimiter + "'.");

            Map<String, QuerySubscriptionScheduled> subscribedMap = (HashMap<String, QuerySubscriptionScheduled>) ctx.getAttribute("subscribedMap");
            if (subscribedMap != null && log.isDebugEnabled()) {
                log.debug("Restored " + subscribedMap.size() + " subscriptions from servlet context.");
            }

            msgContext.setProperty("dbconnection", dbconnection);
            msgContext.setProperty("delimiter", delimiter);
            msgContext.setProperty("subscribedMap", subscribedMap);
            msgContext.setProperty("properties", properties);
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
            iex.setSeverity(ImplementationExceptionSeverity.SEVERE);
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
        log.info("There was an Axis Fault, likely an intended 'USER ERROR'. Check log for stacktrace.");

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
