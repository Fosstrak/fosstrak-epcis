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

package org.accada.epcis.repository.query;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import javax.xml.ws.Endpoint;

import org.accada.epcis.soap.EPCISServicePortType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.BusFactory;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

/**
 * This HttpServlet is used to initialize the QueryOperationsModule. It will
 * read the application's properties file from the class path, read the data
 * source from a JNDI name, and load the CXF Web service bus in order to set up
 * the QueryOperationsModule with the required values.
 * <p>
 * Note: this servlet is only required if you do not wire the application with
 * Spring! To use this servlet, and bypass Spring, replace
 * <code>WEB-INF/web.xml</code> with <code>WEB-INF/non-spring-web.xml</code>.
 * 
 * @author Marco Steybe
 */
public class QueryInitServlet extends CXFNonSpringServlet {

    private static final long serialVersionUID = -5839101192038037389L;

    private static final String APP_CONFIG_LOCATION = "appConfigLocation";
    private static final String PROP_MAX_QUERY_ROWS = "maxQueryResultRows";
    private static final String PROP_MAX_QUERY_TIME = "maxQueryExecutionTime";
    private static final String PROP_TRIGGER_CHECK_SEC = "trigger.condition.check.sec";
    private static final String PROP_TRIGGER_CHECK_MIN = "trigger.condition.check.min";
    private static final String PROP_SERVICE_VERSION = "service.version";
    private static final String PROP_JNDI_DATASOURCE_NAME = "jndi.datasource.name";

    private static final Log LOG = LogFactory.getLog(QueryInitServlet.class);

    private Properties properties;

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.cxf.transport.servlet.CXFNonSpringServlet#loadBus(javax.servlet.ServletConfig)
     */
    public void loadBus(ServletConfig servletConfig) throws ServletException {
        super.loadBus(servletConfig);
        BusFactory.setDefaultBus(getBus());
        if (LOG.isDebugEnabled()) {
            getBus().getInInterceptors().add(new LoggingInInterceptor());
            getBus().getOutInterceptors().add(new LoggingOutInterceptor());
            getBus().getOutFaultInterceptors().add(new LoggingOutInterceptor());
            getBus().getInFaultInterceptors().add(new LoggingInInterceptor());
        }
        EPCISServicePortType service = setupQueryOperationsModule(servletConfig);

        LOG.debug("Publishing query operations module service at /query");
        Endpoint.publish("/query", service);
    }

    private EPCISServicePortType setupQueryOperationsModule(ServletConfig servletConfig) {
        loadApplicationProperties(servletConfig);
        String jndiName = properties.getProperty(PROP_JNDI_DATASOURCE_NAME);
        DataSource dataSource = loadDataSource(jndiName);

        LOG.debug("Initializing query operations module");
        QueryOperationsModule module = new QueryOperationsModule();
        module.setMaxQueryRows(Integer.parseInt(properties.getProperty(PROP_MAX_QUERY_ROWS)));
        module.setMaxQueryTime(Integer.parseInt(properties.getProperty(PROP_MAX_QUERY_TIME)));
        module.setTriggerConditionMinutes(properties.getProperty(PROP_TRIGGER_CHECK_MIN));
        module.setTriggerConditionSeconds(properties.getProperty(PROP_TRIGGER_CHECK_SEC));
        module.setServiceVersion(properties.getProperty(PROP_SERVICE_VERSION));
        module.setDataSource(dataSource);
        module.setServletContext(servletConfig.getServletContext());
        module.setBackend(new QueryOperationsBackendSQL());

        LOG.debug("Initializing query operations web service");
        QueryOperationsWebService service = new QueryOperationsWebService(module);
        return service;
    }

    /**
     * Loads the application property file and populates a java.util.Properties
     * instance.
     * 
     * @param servletConfig
     *            The ServletConfig used to locate the application property
     *            file.
     */
    private void loadApplicationProperties(ServletConfig servletConfig) {
        properties = new Properties();

        // read application.properties from classpath
        String path = "/";
        String appConfigFile = "application.properties";
        InputStream is = QueryInitServlet.class.getResourceAsStream(path + appConfigFile);

        try {
            if (is == null) {
                // read properties from file specified in servlet context
                ServletContext ctx = servletConfig.getServletContext();
                path = ctx.getRealPath("/");
                appConfigFile = ctx.getInitParameter(APP_CONFIG_LOCATION);
                is = new FileInputStream(path + appConfigFile);
            }
            properties.load(is);
            is.close();
            LOG.info("Loaded application properties from " + path + appConfigFile);
        } catch (IOException e) {
            LOG.error("Unable to load application properties from " + path + appConfigFile, e);
        }
    }

    /**
     * Loads the data source from the application context via JNDI.
     * 
     * @param jndiName
     *            The name of the JNDI data source holding the connection to the
     *            database.
     * @return The application DataSource instance.
     */
    private DataSource loadDataSource(String jndiName) {
        DataSource dataSource = null;
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(jndiName);
            LOG.info("Loaded data source via JNDI from " + jndiName);
        } catch (NamingException e) {
            LOG.error("Unable to load data source via JNDI from " + jndiName, e);
        }
        return dataSource;
    }
}
