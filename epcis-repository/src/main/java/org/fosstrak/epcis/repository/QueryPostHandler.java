/**
 * 
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
    public void invoke(MessageContext msgContext)
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
            String msg = "Unable to close the database connection: " + e.getMessage();
            iex.setReason(msg);
            iex.setStackTrace(e.getStackTrace());
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw iex;
        }
    }

}
