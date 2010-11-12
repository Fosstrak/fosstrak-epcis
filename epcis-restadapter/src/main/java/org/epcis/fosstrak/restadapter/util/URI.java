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
package org.epcis.fosstrak.restadapter.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.feed.FeedResource;
import org.epcis.fosstrak.restadapter.model.Entry;
import org.epcis.fosstrak.restadapter.rest.RESTfulEPCIS;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.epcis.fosstrak.restadapter.config.URIConstants;

/**
 * Helper Class to work with URIs
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
public class URI {

    /**
     * Constructs an URI
     *
     *
     * @param context
     */
    public URI(UriInfo context) {
        this.context = context;
    }
    private UriInfo context;

    /**
     * Escape the XML string
     *
     *
     * @param unescaped
     *
     * @return
     */
    public static String escapeURL(String unescaped) {
        String res = unescaped;

        res = res.replace("/", "*slash*");

        res = res.replace("&", "*amp*");

        return res;
    }

    /**
     * Unescape the XML string
     *
     *
     * @param escaped
     *
     * @return
     */
    public static String unescapeURL(String escaped) {
        String res = escaped;

        res = res.replace("*slash*", "/");

        res = res.replace("*amp*", "&");

        return res;
    }

    /**
     * Escape the XML string
     *
     *
     * @param url
     *
     * @return
     */
    public static String escapeAMP(String url) {
        url = url.replace("&gt;", "*greatertemp*");
        url = url.replace("&lt;", "*lesstemp*");
        url = url.replace("&amp;", "&");
        url = url.replace("&", "&amp;");
        url = url.replace("*greatertemp*", "&gt;");
        url = url.replace("*lesstemp*", "&lt;");

        return url;
    }

    /**
     * Add query parameters to an URI
     *
     *
     * @param uriAbsolute
     * @param name
     * @param value
     *
     * @return
     */
    public static String addQueryParameter(String uriAbsolute, String name, String value) {
        value = URI.escapeURL(value);

        String res = UriBuilder.fromUri(uriAbsolute).queryParam(name, value).build().toASCIIString();

        res = escapeAMP(res);

        return res;
    }

    /**
     * Build an Event Path ID (as URI)
     *
     *
     * @param bizLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return
     */
    public static String buildRESTfulEventPathIdLink(String bizLocation, String readPoint, String eventTime, String index) {
        bizLocation = escapeURL(bizLocation);
        readPoint = escapeURL(readPoint);

        String restHomeURI = Config.GET_RESTFUL_EPCIS_URL_NO_VERSION();

        String restRelURI = UriBuilder.fromResource(RESTfulEPCIS.class).path(URIConstants.FINDER_EVENT).build(bizLocation, readPoint, eventTime).getRawPath();

        String absoluteURI = restHomeURI + restRelURI;

        String res = addQueryParameter(absoluteURI, URIConstants.INDEX_QP, index);

        return res;
    }

    /**
     * Build an Event Path ID (as URI) link
     *
     *
     * @param id
     * @param entry
     * @param bizLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return
     */
    public static String buildEventIdLink(String id, Entry entry, String bizLocation, String readPoint, String eventTime, String index) {
        String res = buildEventIdLink(id, entry, bizLocation, readPoint, eventTime) + Config.QUESTION_MARK + URIConstants.INDEX_QP + Config.EQUALS + index;

        res = escapeAMP(res);

        return res;
    }

    /**
     * Build an Event Path ID (as URI) link
     *
     *
     * @param id
     * @param entry
     * @param bizLocation
     * @param readPoint
     * @param eventTime
     *
     * @return
     */
    public static String buildEventIdLink(String id, Entry entry, String bizLocation, String readPoint, String eventTime) {
        bizLocation = escapeURL(bizLocation);
        readPoint = escapeURL(readPoint);

        String restHomeURI = Config.GET_RESTFUL_EPCIS_URL_NO_VERSION();
        String restRelURI = UriBuilder.fromResource(RESTfulEPCIS.class).path(URIConstants.FINDER_EVENT_ID).build(bizLocation, readPoint, eventTime, id).getRawPath();

        return restHomeURI + restRelURI;
    }

    /**
     * Build an Event Path ID (as URI) link which is a EPCIS simple event query URI containing one parameter
     *
     *
     * @param name
     * @param value
     *
     * @return
     */
    public static String buildOneDimentionalQueryLinkFromParameter(String name, String value) {
        return Config.GET_RESTFUL_EPCIS_URL() + URIConstants.EVENTQUERY_RESULTS + Config.QUESTION_MARK + name + Config.EQUALS + URI.escapeURL(value);
    }

    /**
     * Append two URI parts to one URI
     *
     *
     * @param parentURI
     * @param childURI
     *
     * @return
     */
    public static String addSubPath(String parentURI, String childURI) {
        if (parentURI.endsWith("/")) {
            parentURI = parentURI.substring(0, parentURI.length() - 1);
        }

        if (childURI.startsWith("/")) {
            childURI = childURI.substring(1);
        }

        return parentURI + "/" + childURI;
    }

    private Object[] escapeObjects(Object... args) {
        for (int i = 0; i < args.length; i++) {
            String str = (String) args[i];

            str = escapeURL(str);
            args[i] = str;
        }

        return args;
    }

    /**
     * Get the EPCIS REST Adapter home URI
     *
     *
     * @return
     */
    public String getRestHomeURI() {
        String res = context.getBaseUri().toASCIIString();
        //res = IPAddress.TRANSFORM_LOCALHOST_TO_IP(res);

        return res;
    }

    /**
     * Get the EPCIS REST Adapter FEED URI
     *
     *
     * @param id
     *
     * @return
     */
    public String getFeedURI(String id) {
        String restHomeURI = getRestHomeURI();

        if (restHomeURI.endsWith("/")) {
            restHomeURI = restHomeURI.substring(0, restHomeURI.length() - 1);
        }

        String restRelURI = UriBuilder.fromResource(FeedResource.class).path(FeedResource.FEED_ID_URI).build(id).getRawPath();

        // System.out.println(restRelURI);
        return restHomeURI + restRelURI;
    }

    /**
     * Get a EPCIS REST Adapter REST URI
     *
     *
     * @param path
     * @param args
     *
     * @return
     */
    public String getRestURI(String path, Object... args) {
        args = escapeObjects(args);

        String restHomeURI = getRestHomeURI();

        if (restHomeURI.endsWith("/")) {
            restHomeURI = restHomeURI.substring(0, restHomeURI.length() - 1);
        }
        String restRelURI = UriBuilder.fromResource(RESTfulEPCIS.class).path(path).build(args).getRawPath();


        return restHomeURI + restRelURI;
    }

    /**
     * This method will extract the host URL of the current context
     * including the port number.
     * E.g. http://www.webofthings.com:8084
     * @return The host URL of the current context (e.g. http://www.webofthings.com:8084).
     */
    public String getHostURL() {
        StringBuilder hostUrl = new StringBuilder();
        URL tempUrl;
        try {
            tempUrl = context.getRequestUri().toURL();
            hostUrl.append(tempUrl.getProtocol());
            hostUrl.append("://");
            hostUrl.append(tempUrl.getHost());

            if (tempUrl.getPort() > 0) {
                hostUrl.append(":");
                hostUrl.append(tempUrl.getPort());
            }
            hostUrl.append("/");


        } catch (MalformedURLException ex) {
            Logger.getLogger(URI.class.getName()).log(Level.SEVERE, "Error while trying to extract"
                    + " host URL from current context.", ex);
        }
        return hostUrl.toString();
    }

    /**
     * This method will extract the rest URI of the current context
     * including the query parameters if any.
     * E.g. /rest/epcis-adapter/query?epc=...&location=...
     * @return The host URL of the current context (e.g. /rest/epcis-adapter/query?epc=...&location=...).
     */
    public String getRestURI() {
        StringBuilder restUri = new StringBuilder();
        URL tempUri;
        try {
            tempUri = context.getRequestUri().toURL();

            if (tempUri.getPath() != null) {
                restUri.append(tempUri.getPath());
            }

            if (tempUri.getQuery() != null) {
                restUri.append("?");
                restUri.append(tempUri.getQuery());
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(URI.class.getName()).log(Level.SEVERE, "Error while trying to extract REST URI from"
                    + " current context.", ex);
        }
        return restUri.toString();
    }
}
