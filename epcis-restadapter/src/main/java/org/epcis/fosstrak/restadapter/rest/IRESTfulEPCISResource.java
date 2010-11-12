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
package org.epcis.fosstrak.restadapter.rest;

import org.epcis.fosstrak.restadapter.model.Resource;
import javax.ws.rs.core.UriInfo;

/**
 *
 * Interface for the EPCIS REST Adapter business logic
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch
 *
 *
 */
public interface IRESTfulEPCISResource {

    /**
     * Returns a representation of the EPCIS REST Adapter home resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getRESTfulEPCIS(UriInfo context);

    /**
     * Returns a representation of the EPCIS REST Adapter config resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getConfig(UriInfo context);

    /**
     * Returns a representation of the reset EPCIS REST Adapter resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getResetRestfulEpcis(UriInfo context);

    /**
     * POST method for reseting the EPCIS REST Adapter Database
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource doResetRestfulEpcis(UriInfo context);

    /**
     * Returns a representation of the reload finder resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getReloadFinder(UriInfo context);

    /**
     * POST method for reloading the EPCIS REST Adapter Finder
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource doReloadFinder(UriInfo context);

    /**
     * Returns a representation of the config EPCIS URL resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getEpcisUrl(UriInfo context);

    /**
     * PUT method for updating or creating an instance of EPCIS URL
     *
     *
     * @param context
     * @param url
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource setEpcisUrl(UriInfo context, String url);

    /**
     * Returns a representation of the config FEED URL resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getFeedUrl(UriInfo context);

    /**
     * PUT method for updating or creating an instance of FEED URL
     *
     *
     * @param context
     * @param url
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource setFeedUrl(UriInfo context, String url);

    /**
     * Returns a representation of the about resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getAbout(UriInfo context);

    /**
     * Returns a representation of the version resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getAuthors(UriInfo context);

    /**
     * Returns a representation of the author resource according to the requested mime type
     *
     *
     * @param context
     * @param id
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getAuthor(UriInfo context, String id);

    /**
     * Returns a representation of the querynames resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getQueryNames(UriInfo context);

    /**
     * Returns a representation of the business locations resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getVersion(UriInfo context);

    /**
     * Returns a representation of the EPCIS REST Adapter version resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getRest(UriInfo context);

    /**
     * Returns a representation of the EPCIS standard version resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getStandard(UriInfo context);

    /**
     * Returns a representation of the EPCIS vendor (implementor) version resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getVendor(UriInfo context);
}
