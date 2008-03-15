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

package org.accada.epcis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.accada.epcis.soap.model.EPCISQueryDocumentType;
import org.accada.epcis.soap.model.ObjectFactory;
import org.accada.epcis.soap.model.QueryResults;
import org.w3c.dom.Document;

/**
 * Parses the XML representation of an EPCIS query results into a QueryResults
 * object for use with axis.
 * 
 * @author Marco Steybe
 */
public final class QueryResultsParser {

    private static ObjectFactory factory = new ObjectFactory();

    /**
     * Empty default constructor. Utility classes should not have public
     * constructors.
     */
    private QueryResultsParser() {
    }

    /**
     * A helper method to parse and convert the XML representation of an EPCIS
     * query results into a QueryResults object.
     * 
     * @param xml
     *            The InputStream containing the XML representation of a
     *            QueryResults object.
     * @return The parsed QueryResults object.
     * @throws IOException
     *             If an error de-serializing the InputStream occurred.
     */
    public static QueryResults parseResults(final InputStream xml) throws IOException {
        // de-serialize the XML
        try {
            JAXBContext context = JAXBContext.newInstance(QueryResults.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            // setting schema to null will turn XML validation off
            // unmarshaller.setSchema(null);
            JAXBElement<QueryResults> results = (JAXBElement<QueryResults>) unmarshaller.unmarshal(xml);
            return results.getValue();
        } catch (JAXBException e) {
            // wrap JAXBException into IOException to keep the interface
            // JAXB-free
            IOException ioe = new IOException(e.getMessage());
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
    }

    /**
     * A helper method to parse and convert the XML representation of an EPCIS
     * query document into an QueryResults object.
     * 
     * @param xml
     *            The Reader containing the XML representation of an
     *            EPCISQueryDocumentType object.
     * @return The parsed QueryResults object.
     * @throws IOException
     *             If an error de-serializing the InputStream occurred.
     */
    public static QueryResults parseQueryDocResults(final Reader r) throws IOException {
        // de-serialize the XML
        try {
            JAXBContext context = JAXBContext.newInstance(EPCISQueryDocumentType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<EPCISQueryDocumentType> results = (JAXBElement<EPCISQueryDocumentType>) unmarshaller.unmarshal(r);
            return results.getValue().getEPCISBody().getQueryResults();
        } catch (JAXBException e) {
            // wrap JAXBException into IOException to keep the interface
            // JAXB-free
            IOException ioe = new IOException(e.getMessage());
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
    }

    /**
     * Marshals the given QueryResults object to its XML representations and
     * serializes it to the given OutputStream. Use as follows for printing a
     * QueryResults instance to standard output:
     * 
     * <pre>
     * QueryResults results = ...
     * QueryResultsParser.queryResultsToXml(results, System.out);
     * </pre>
     * 
     * @param results
     *            The QueryResults object to marshal into XML.
     * @param out
     *            The OutputStream to which the XML representation will be
     *            written to.
     * @throws IOException
     *             If an error marshaling the QueryResults object occurred.
     */
    public static void queryResultsToXml(final QueryResults results, OutputStream out) throws IOException {
        // serialize the response
        try {
            JAXBElement<QueryResults> item = factory.createQueryResults(results);
            JAXBContext context = JAXBContext.newInstance(QueryResults.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(item, out);
        } catch (JAXBException e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
    }

    /**
     * Marshals the given QueryResults object to its XML representations and
     * returns it as a String.
     * 
     * @param results
     *            The QueryResults object to marshal into XML.
     * @param out
     *            The OutputStream to which the XML representation will be
     *            written to.
     * @throws IOException
     *             If an error marshaling the QueryResults object occurred.
     */
    public static String queryResultsToXml(final QueryResults results) throws IOException {
        // serialize the response
        try {
            StringWriter writer = new StringWriter();
            JAXBElement<QueryResults> item = factory.createQueryResults(results);
            JAXBContext context = JAXBContext.newInstance(QueryResults.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(item, writer);
            return writer.toString();
        } catch (JAXBException e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
    }

    /**
     * Marshals the given QueryResults object to its XML representation and
     * returns a DOM Document of it.
     * 
     * @param results
     *            The QueryResults object to marshal into XML.
     * @param out
     *            The OutputStream to which the XML representation will be
     *            written to.
     * @throws IOException
     *             If an error marshaling the QueryResults object occurred.
     */
    public static Document queryResultsToDocument(final QueryResults results) throws IOException {
        // serialize the response
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            JAXBElement<QueryResults> item = factory.createQueryResults(results);
            JAXBContext context = JAXBContext.newInstance(QueryResults.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(item, doc);
            return doc;
        } catch (JAXBException e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        } catch (ParserConfigurationException e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
    }
}
