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
package org.epcis.fosstrak.restadapter.logic;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.db.InternalDatabase;
import org.epcis.fosstrak.restadapter.db.exceptions.NotFoundInDBException;
import org.epcis.fosstrak.restadapter.feed.FeedResource;
import org.epcis.fosstrak.restadapter.util.URI;
import javax.ws.rs.core.UriInfo;
import org.epcis.fosstrak.restadapter.config.Tools;

/**
 *
 * Class containing static methods used for the initialization of the logic
 *
 * @author Mathias Muellermathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
public class Initializer {

    /**
     * Initialize the EPCIS REST Adapter
     *
     *
     * @param context
     */
    public static void initializeRESTfulEPCIS(UriInfo context) {
        setURIs(context);
        Config.EPCIS_REPOSITORY_URL_DEFAULT = Config.GET_EPCIS_REPOSITORY_URL();
        Config.RESTFUL_FEED_URL_DEFAULT     = Config.GET_FEED_URL();
        loadEpcisUrl();
        loadEventFinderEntriesFromEPCIS();
    }

    /**
     * Sets the URIs
     *
     *
     * @param context
     */
    public static void setURIs(UriInfo context) {
        URI    uri     = new URI(context);
        String restURL = uri.getRestHomeURI();
        String feedURL = URI.addSubPath(uri.getRestHomeURI(), FeedResource.FEED_URI);

        Config.SET_RESTFUL_EPCIS_URL(restURL + Config.EPCIS_REST_ADAPTER_VERSION);
        Config.SET_RESTFUL_EPCIS_URL_NO_VERSION(Tools.stripTrailingSlash(restURL));
        Config.SET_FEED_URL(feedURL);
        loadEventFinderEntriesFromEPCIS();
    }

    /**
     * Resets the internal database
     *
     */
    public static void resetDatabase() {
        InternalDatabase.getInstance().resetDB();
        loadEventFinderEntriesFromEPCIS();
    }

    /**
     * Loads or reloads the event finder
     *
     */
    public static void loadEventFinderEntriesFromEPCIS() {
        new QueryBusinessLogic().reload();
    }

/**
 * This loads the value for the EPCIS URL. This is going to be either a default
 * value or a user-configured value.
 */
    private static void loadEpcisUrl() {
        InternalDatabase db = InternalDatabase.getInstance();
        try {
            Config.SET_EPCIS_REPOSITORY_URL(db.getConfigurationEntry("epcisurl"));
        } catch (NotFoundInDBException ex) {
            // the entry does not exist yet, return the default
            Config.SET_EPCIS_REPOSITORY_URL(Config.EPCIS_REPOSITORY_URL_DEFAULT);
        }
    }
}
