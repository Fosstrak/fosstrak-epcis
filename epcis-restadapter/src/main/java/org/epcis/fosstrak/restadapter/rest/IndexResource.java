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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.epcis.fosstrak.restadapter.config.Config;

/**
 * Business logic for the EPCIS REST Adapter Root
 * @author Mathias Mueller mathias.mueller(at)unifr.ch
 *
 */
@Path("*")
@Produces(MediaType.TEXT_HTML)
public class IndexResource {

    /**
     * Returns a representation of the EPCIS REST Adapter home resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @GET
    public String getRESTfulEPCIS(@Context UriInfo context) {
        StringBuilder res = new StringBuilder();

        res.append("<html>");
        res.append("<head>");
        res.append("<title>");
        res.append("RESTful Adapter for the Electronic Product Code Information Services");
        res.append("</title>");
        res.append("</head>");
        res.append("<body>");
        res.append("<h1>Welcome to the RESTful Adapter for the EPCIS</h1>");
        res.append("<h2>Deployed versions:</h1>");
        res.append("<p><a href='");
        res.append(Config.EPCIS_REST_ADAPTER_VERSION);
        res.append("'>");
        res.append(Config.EPCIS_REST_ADAPTER_VERSION);
        res.append("</a></p>");
        res.append("</body>");
        res.append("</html>");

        return res.toString();
    }
}
