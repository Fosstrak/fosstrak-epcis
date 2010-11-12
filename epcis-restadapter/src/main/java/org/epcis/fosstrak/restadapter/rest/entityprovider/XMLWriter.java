/*
 * Copyright (C) 2010 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org) and
 * was developed as part of the webofthings.com initiative.
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
package org.epcis.fosstrak.restadapter.rest.entityprovider;

import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.model.Resource;
import org.epcis.fosstrak.restadapter.representation.PrettyPrint;
import org.epcis.fosstrak.restadapter.util.ActualDateTime;
import org.epcis.fosstrak.restadapter.util.FileSchemaOutputResolver;
import org.epcis.fosstrak.restadapter.ws.generated.EPCISQueryBodyType;
import org.epcis.fosstrak.restadapter.ws.generated.EPCISQueryDocumentType;
import org.epcis.fosstrak.restadapter.ws.generated.ObjectFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;

/**
 * Class description
 * The XML Entity Provider Class
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 *
 */
@Provider
@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class XMLWriter implements MessageBodyWriter<Resource> {

    @Context
    HttpServletRequest context;

    /**
     * Test for using the right provider to a request
     *
     *
     * @param type
     * @param genericType
     * @param annotations
     * @param mediaType
     *
     * @return
     */
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        boolean isAssignable       = Resource.class.isAssignableFrom(type);
        boolean isMediaTypeCorrect = false;
        boolean isSafari           = true;

        isSafari = HTMLWebPageSmartPhoneWriter.isSmartPhone(context);

        if (mediaType != null) {
            isMediaTypeCorrect = mediaType.equals(MediaType.valueOf(MediaType.APPLICATION_XML));
            isMediaTypeCorrect = isMediaTypeCorrect || mediaType.equals(MediaType.valueOf(MediaType.TEXT_XML));
        }

        return (isAssignable && isMediaTypeCorrect && !isSafari);
    }

    /**
     * Size test, not used, as the size is not known a priori
     *
     *
     * @param resource
     * @param type
     * @param genericType
     * @param annotations
     * @param mediaType
     *
     * @return
     */
    public long getSize(Resource resource, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /**
     * Method to create the representation of the resource
     *
     *
     * @param resource
     * @param type
     * @param genericType
     * @param annotations
     * @param mediaType
     * @param httpHeaders
     * @param entityStream
     *
     * @throws IOException
     * @throws WebApplicationException
     */
    public void writeTo(Resource resource, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try {

            // EPCIS REST Adapter specific data
            if (mediaType.equals(MediaType.valueOf(MediaType.APPLICATION_XML)) || (resource.getQueryResults() == null)) {
                JAXBContext myContext  = JAXBContext.newInstance(Resource.class);
                Marshaller  marshaller = myContext.createMarshaller();

                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                // write XMLSchema
                if (Config.IS_PRINT_XMLSCHEMA) {
                    SchemaOutputResolver schemaOutputResolver = new FileSchemaOutputResolver();

                    myContext.generateSchema(schemaOutputResolver);
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                marshaller.marshal(resource, byteArrayOutputStream);

                String xml = byteArrayOutputStream.toString();

                // no real need to pretty print again, but the indication with the PrettyPrint class is smaller (two spaces instead of four) which makes it more compact and human readable friendlier with a lot of nested elements.
                xml = PrettyPrint.prettyPrintXML(xml);
                entityStream.write(xml.getBytes());

                // marshaller.marshal(resource, entityStream);
            }    // Only data which is compliant to the EPCGlobal EPCIS Query Results XML Schema
                    else {

                // EPCIS specific data
                ObjectFactory objectFactory = new ObjectFactory();

                // create the EPCISDocument containing the Event
                EPCISQueryDocumentType epcisQueryDocumentType = new EPCISQueryDocumentType();
                EPCISQueryBodyType     epcisQueryBodyType     = new EPCISQueryBodyType();

                epcisQueryBodyType.setQueryResults(resource.getQueryResults());
                epcisQueryDocumentType.setEPCISBody(epcisQueryBodyType);
                epcisQueryDocumentType.setSchemaVersion(new BigDecimal("1.0"));
                epcisQueryDocumentType.setCreationDate(ActualDateTime.GET_NOW_XMLGC());

                JAXBContext                         jaxbContext = JAXBContext.newInstance("org.epcis.fosstrak.restadapter.ws.generated");
                JAXBElement<EPCISQueryDocumentType> item        = objectFactory.createEPCISQueryDocument(epcisQueryDocumentType);
                Marshaller marshaller = jaxbContext.createMarshaller();

                marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                marshaller.marshal(item, byteArrayOutputStream);

                String xml = byteArrayOutputStream.toString();

                // no real need to pretty print again, but the indication with the PrettyPrint class is smaller (two spaces instead of four) which makes it more compact and human readable friendlier with a lot of nested elements.
                xml = PrettyPrint.prettyPrintXML(xml);
                entityStream.write(xml.getBytes());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
