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

package org.fosstrak.epcis.repository;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This RepositoryContextListener performs the initialization and termination
 * (shut-down) work required by the Fosstrak EPCIS repository application, such as
 * initializing the logging framework. This class receives notifications about
 * changes to the servlet context of the web application, e.g., when the servlet
 * context is loaded, or is about to be shut down.
 * 
 * @author Marco Steybe
 */
public class RepositoryContextListener implements ServletContextListener {

    private static final Log LOG = LogFactory.getLog(RepositoryContextListener.class);

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event) {
        ServletContext ctx = event.getServletContext();

        /*
         * Note: logging is initialized automatically by reading
         * logging.properties and log4j.properties from the classpath.
         * logging.properties is used to tell commons-logging to use LOG4J as
         * its underlying logging toolkit; log4j.properties is used to configure
         * LOG4J. To initialize LOG4J manually from LOG4J_CONFIG_LOCATION,
         * un-comment the following code (LOG4J_CONFIG_LOCATION =
         * "log4jConfigLocation") ...
         */
        // "log4jConfigLocation";
        // String path = ctx.getRealPath("/");
        // String log4jCfg = ctx.getInitParameter(LOG4J_CONFIG_LOCATION);
        // // initialize Log4j
        // if (log4jCfg != null) {
        // // if no log4j properties file found, then do not try
        // // to load it (the application runs without logging)
        // PropertyConfigurator.configure(path + log4jCfg);
        // }
        // log = LogFactory.getLog(this.getClass());

        // set a system property to configure CXF to use LOG4J
        System.setProperty("org.apache.cxf.Logger", "org.apache.cxf.common.logging.Log4jLogger");

        LOG.info("Starting Fosstrak EPCIS Repository application");

        if (LOG.isDebugEnabled()) {
            LOG.debug("Logging application context init-parameters:");
            Enumeration<?> e = ctx.getInitParameterNames();
            while (e.hasMoreElements()) {
                String param = (String) e.nextElement();
                LOG.debug(param + "=" + ctx.getInitParameter(param));
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event) {
        LOG.info("Fosstrak EPCIS Repository application shut down\n######################################");
        LogFactory.releaseAll();
    }
}
