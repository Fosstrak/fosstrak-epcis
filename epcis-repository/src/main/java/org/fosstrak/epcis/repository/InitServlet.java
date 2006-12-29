package org.accada.epcis.repository;

import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.PropertyConfigurator;

/**
 * InitServlet is a generic Servlet loaded on startup for initialization
 * purposes. TODO: this servlet can be removed ... (we use the context to
 * initialize everything)
 * 
 * @author Marco Steybe
 */
public class InitServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes this servlet.
     * 
     * @throws ServletException
     *             If some proeprty could not be loaded.
     */
    public void init() throws ServletException {
        String servletPath = getServletContext().getRealPath("/");

        // init LOG4J
        String log4jConfigFile = getServletConfig().getInitParameter(
                "log4jConfigFile");
        if (log4jConfigFile != null) {
            // if no log4j properties file found, then do not try
            // to load it (the application runs without logging)
            PropertyConfigurator.configure(servletPath + log4jConfigFile);
        }

        // init properties
        String propFile = getServletConfig().getInitParameter("propertiesFile");
        try {
            Properties props = new Properties();
            // XXX why does the following return null???
            // InputStream in =
            // getServletContext().getResourceAsStream(servletPath + propFile);
            FileInputStream in = new FileInputStream(servletPath + propFile);
            props.load(in);
            getServletContext().setAttribute("props", props);
        } catch (Exception e) {
            throw new ServletException("error loading properties file from "
                    + servletPath + propFile, e);
        }
    }
}
