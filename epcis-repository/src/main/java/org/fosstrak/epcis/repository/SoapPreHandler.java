/**
 * 
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
public class SoapPreHandler extends BasicHandler {

    /**
     * Invokes this SoapPreHandler which performs the initialization for the
     * EPCIS query service, such as reading configuration and setting up the
     * database.
     * 
     * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
     * @param msgContext
     *            the MessageContext to process with this InitHandler.
     * @throws ImplementationException
     *             If an error reading the configuration or setting up the
     *             database occured.
     */
    public void invoke(MessageContext msgContext)
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
            final Logger LOG = Logger.getLogger("SoapPreHandler.class");

            Context initContext = new InitialContext();
            Context env = (Context) initContext.lookup("java:comp/env");
            DataSource db = (DataSource) env.lookup("jdbc/EPCISDB");
            Connection dbconnection = db.getConnection();
            LOG.info("Connection to database successfully established.");

            String delimiter = dbconnection.getMetaData().getIdentifierQuoteString();
            LOG.info("Resolved string delimiter used to quote SQL identifiers as '"
                    + delimiter + "'.");

            Map<String, SubscriptionScheduled> subscribedMap = (HashMap<String, SubscriptionScheduled>) ctx.getAttribute("subscribedMap");

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

}
