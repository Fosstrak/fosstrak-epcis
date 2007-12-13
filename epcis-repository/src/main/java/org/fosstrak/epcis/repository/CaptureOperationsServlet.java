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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * CaptureOperationsModule implements the core capture operations. Converts XML
 * events delivered by HTTP POST into SQL and inserts them into the database.
 * 
 * @author David Gubler
 * @author Alain Remund
 * @author Marco Steybe
 */
public class CaptureOperationsServlet extends HttpServlet {

    private static final long serialVersionUID = -1507276940747086042L;

    private static final Logger LOG = Logger.getLogger(CaptureOperationsServlet.class);

    private CaptureOperationsModule captureOperationsModule = new CaptureOperationsModule();

    /**
     * @see javax.servlet.GenericServlet#init()
     * @throws ServletException
     *             If the context could not be loaded.
     */
    @Override
    public void init() throws ServletException {
        // read configuration and set up database source
        try {
            Context initContext = new InitialContext();
            Context env = (Context) initContext.lookup("java:comp/env");
            captureOperationsModule.setDb((DataSource) env.lookup("jdbc/EPCISDB"));
        } catch (NamingException e) {
            String msg = "Unable to read configuration, check META-INF/context.xml for Resource 'jdbc/EPCISDB'.";
            LOG.error(msg, e);
            throw new ServletException(msg, e);
        }

        // load properties
        String servletPath = getServletContext().getRealPath("/");
        String appConfigFile = getServletContext().getInitParameter("appConfigFile");
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(servletPath + appConfigFile));
        } catch (IOException e) {
            LOG.error("Unable to load application properties from " + servletPath + appConfigFile);
        }
        captureOperationsModule.setInsertMissingVoc(Boolean.parseBoolean(properties.getProperty("insertMissingVoc",
                "true")));
        String dbResetAllowedStr = getServletContext().getInitParameter("dbResetAllowed");
        captureOperationsModule.setDbResetAllowed(Boolean.parseBoolean(dbResetAllowedStr));
        captureOperationsModule.setDbResetScript(servletPath + getServletContext().getInitParameter("dbResetScript"));

        // load log4j config
        String log4jConfigFile = getServletContext().getInitParameter("log4jConfigFile");
        if (log4jConfigFile != null) {
            // if no log4j properties file found, then do not try
            // to load it (the application runs without logging)
            PropertyConfigurator.configure(servletPath + log4jConfigFile);
        }

        // load the schema validator
        try {
            String schemaPath = servletPath + getServletContext().getInitParameter("schemaPath");
            String schemaFile = getServletContext().getInitParameter("schemaFile");
            File xsd = new File(schemaPath + System.getProperty("file.separator") + schemaFile);
            LOG.debug("Reading schema from '" + xsd.getAbsolutePath() + "'.");
            if (!xsd.exists()) {
                LOG.warn("Unable to find the schema file (check 'pathToSchemaFiles' parameter in META-INF/context.xml)");
                LOG.warn("Schema validation will not be available!");
            } else {
                // load the schema to validate against
                SchemaFactory schemaFact = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Source schemaSrc = new StreamSource(xsd);
                Schema schema = schemaFact.newSchema(schemaSrc);
                captureOperationsModule.setSchema(schema);
            }
        } catch (Exception e) {
            LOG.warn("Unable to load the schema validator.", e);
            LOG.warn("Schema validation will not be available!");
        }
    }

    /**
     * Returns a simple information page.
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     * @param req
     *            The HttpServletRequest.
     * @param rsp
     *            The HttpServletResponse.
     * @throws IOException
     *             If an error occurred while writing the response.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        captureOperationsModule.doGet(req, rsp);
    }

    /**
     * Implements the EPCIS capture operation. Takes HTTP POST request, extracts
     * the payload into an XML document, validates the document against the
     * EPCIS schema, and captures the EPCIS events given in the document. Errors
     * are caught and returned as simple plaintext messages via HTTP.
     * 
     * @param req
     *            The HttpServletRequest.
     * @param rsp
     *            The HttpServletResponse.
     * @throws IOException
     *             If an error occurred while validating the request or writing
     *             the response.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        captureOperationsModule.doPost(req, rsp);
    }

}
