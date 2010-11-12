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

import org.epcis.fosstrak.restadapter.model.Resource;
import org.epcis.fosstrak.restadapter.representation.HTMLPageIphoneBuilder;
import org.epcis.fosstrak.restadapter.representation.PrettyPrint;
import org.epcis.fosstrak.restadapter.util.URI;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.epcis.fosstrak.restadapter.config.Config;

/**
 * This is an entity provider class for smart phones. It currently handles
 * any WebKit-based mobile phone (e.g., Android, iOS)
 * that displays the words "mobile" and "webkit" in its user-agent string.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>, Mathias Mueller mathias.mueller(at)unifr.ch.
 *
 */
@Provider
@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
public class HTMLWebPageSmartPhoneWriter implements MessageBodyWriter<Resource> {

    public static final String USER_AGENT = "user-agent";
    public static final String IPHONE = "iPhone";
    public static final String MOBILE = "mobile";
    public static final String WEBKIT = "webkit";
    @Context
    HttpServletRequest context;

    /**
     * Method description
     *
     *
     * @param type
     * @param genericType
     * @param annotations
     * @param mediaType
     *
     * @param myContext
     *
     * @return
     */
    public static boolean isSmartPhone(HttpServletRequest myContext) {
        boolean isSmartPhone = false;
        String userAgent = myContext.getHeader(USER_AGENT).toLowerCase();

        if (myContext != null) {
            if (userAgent.contains(WEBKIT) && userAgent.contains(MOBILE)) {
                isSmartPhone = true;
            }
        } else {
            isSmartPhone = false;
        }

        return isSmartPhone;
    }

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
        boolean isAssignable = Resource.class.isAssignableFrom(type);
        boolean isMediaTypeCorrect = false;
        boolean isBrowserTypeCorrect = false;

        if (mediaType != null) {
            if (mediaType.equals(MediaType.valueOf(MediaType.TEXT_HTML))
                    || mediaType.equals(MediaType.valueOf(Config.TEXT_HTML_WEBKIT_SAFE))) {
                isMediaTypeCorrect = true;
            }

        }

        isBrowserTypeCorrect = isSmartPhone(context);

        return (isAssignable && isMediaTypeCorrect && isBrowserTypeCorrect);
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
        HTMLPageIphoneBuilder html = new HTMLPageIphoneBuilder();
        String representation = html.buildRepresentation(resource);

        representation = PrettyPrint.prettyPrintHTML(representation);
        representation = URI.escapeAMP(representation);
        entityStream.write(representation.getBytes());
    }
}
