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

import org.epcis.fosstrak.restadapter.util.URI;
import org.epcis.fosstrak.restadapter.ws.epcis.EPCISWebServiceClient;
import javax.ws.rs.core.UriInfo;
import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.http.HTTP;
import org.epcis.fosstrak.restadapter.http.HTTPStatusCodeMapper;
import org.epcis.fosstrak.restadapter.model.Form;
import org.epcis.fosstrak.restadapter.model.Entry;
import org.epcis.fosstrak.restadapter.model.Content;
import org.epcis.fosstrak.restadapter.model.Resource;
import org.epcis.fosstrak.restadapter.rest.IRESTfulEPCISResource;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.epcis.fosstrak.restadapter.db.InternalDatabase;
import static org.epcis.fosstrak.restadapter.config.URIConstants.*;

/**
 *
 * Business logic for the EPCIS REST Adapter Root
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 */
public class RESTfulEPCISBusinessLogic extends AbstractBusinessLogic implements IRESTfulEPCISResource {

    /**
     * Returns a representation of the EPCIS REST Adapter home resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getRESTfulEPCIS(UriInfo context) {
        String name = "Home";
        String path = "";

        String   description = "This is the Home Interface of the EPCIS REST Adapter.";
        Resource resource    = setUpResource(context, name, description, path);

        Content  links       = new Content();

        resource.setFields(links);

        addLink(context, links, "Query Form", EVENTQUERY);
        addLink(context, links, "Browsable Event Finder", FINDER_BUSINESS_LOCATIONS);
        addLink(context, links, "Subscription List", SUBSCRIPTIONS);
        addLink(context, links, "Capture Interface", CAPTURE);
        addLink(context, links, "Configuration", CONFIG);
        addLink(context, links, "About", ABOUT);

        return resource;
    }

    /**
     * Returns a representation of the config resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getConfig(UriInfo context) {
        String   name        = "Configuration";
        String   path        = CONFIG;
        String   description = "This is the Configuration Interface of the EPCIS REST Adapter.";
        Resource resource    = setUpResource(context, name, description, path);

        Content  links       = new Content();

        resource.setFields(links);

        addLink(context, links, "Configuration of the EPCIS URL", CONFIG_EPCIS);
        addLink(context, links, "Configuration of the FEED URL", CONFIG_FEED);
        addLink(context, links, "Reset the EPCIS REST Adapter", CONFIG_RESET_RESTFULEPCIS);
        addLink(context, links, "Reload the Finder Data", CONFIG_RELOAD_FINDER);

        return resource;
    }

    /**
     * GET method to reset the EPCIS REST Adapter Database
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getResetRestfulEpcis(UriInfo context) {
        String   name        = "Form to reset the EPCIS REST Adapter";
        String   path        = CONFIG_RESET_RESTFULEPCIS;
        String   description = "This is the Interface to reset the EPCIS REST Adapter.";
        Resource resource    = setUpResource(context, name, description, path);

        Form     form        = new Form();

        URI      uri         = new URI(context);

        form.setAction(uri.getRestURI(CONFIG_RESET_RESTFULEPCIS));
        form.setActionDescription("reset");
        form.setMethod(HTTP.POST);
        resource.setForm(form);

        return resource;
    }

    /**
     * POST method to reset the EPCIS REST Adapter Database
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource doResetRestfulEpcis(UriInfo context) {
        String name        = "Resource to reset the EPCIS REST Adapter";
        String path        = CONFIG_RESET_RESTFULEPCIS;
        String description = "This is the Interface to reset the EPCIS REST Adapter. The internal Database of the EPCIS REST Adapter will be reseted not the Database of the EPCIS. Feed Subscriptions, the Event Finder and the internal Feeds are deleted. ";
        Resource resource = setUpResource(context, name, description, path);

        Initializer.resetDatabase();

        Content content  = new Content();
        Entry   reloaded = new Entry();

        reloaded.setValue("Reset successful.");
        content.getContent().add(reloaded);
        resource.setFields(content);

        return resource;
    }

    /**
     * GET method for reloading the EPCIS REST Adapter Event Finder
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getReloadFinder(UriInfo context) {
        String   name        = "Form to reload the event finder";
        String   path        = CONFIG_RELOAD_FINDER;
        String   description = "This is the Interface to reload the EPCIS REST Adapter Event Finder.";
        Resource resource    = setUpResource(context, name, description, path);

        Form     form        = new Form();

        URI      uri         = new URI(context);

        form.setAction(uri.getRestURI(CONFIG_RELOAD_FINDER));
        form.setActionDescription("reload");
        form.setMethod(HTTP.POST);
        resource.setForm(form);

        return resource;
    }

    /**
     * POST method for reloading the EPCIS REST Adapter Finder
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource doReloadFinder(UriInfo context) {
        String   name        = "Resource to reload the event finder";
        String   path        = CONFIG_RELOAD_FINDER;
        String   description = "This is the Interface to reload the EPCIS REST Adapter Event Finder.";
        Resource resource    = setUpResource(context, name, description, path);

        Initializer.loadEventFinderEntriesFromEPCIS();

        Content content  = new Content();
        Entry   reloaded = new Entry();

        reloaded.setValue("Reload successful.");
        content.getContent().add(reloaded);
        resource.setFields(content);

        return resource;
    }

    /**
     * Returns a representation of the config EPCIS URL resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getEpcisUrl(UriInfo context) {
        String   name        = "EPCIS URL";
        String   path        = CONFIG_EPCIS;
        String   description = "This is the Interface to configure the EPCIS URL.";
        Resource resource    = setUpResource(context, name, description, path);

        String   value       = Config.GET_EPCIS_REPOSITORY_URL();

        Content  myUrl       = new Content();

        resource.setFields(myUrl);

        Entry entry = new Entry();

        entry.setName("The actual EPCIS URL");
        entry.setValue(value);
        myUrl.getContent().add(entry);

        Form   form   = new Form();
        URI    uri    = new URI(context);
        String action = uri.getRestURI(path);

        form.setAction(action);
        form.setMethod(HTTP.AJAX_PUT);
        form.setDescription("The EPCIS URL");
        form.setActionDescription("set epcis url");

        Entry input = new Entry();

        input.setId("EPCIS");
        input.setName("EPCIS URL");
        input.setValue(value);
        input.setValueRef(resource.getUri());

        form.getEntries().add(input);

        resource.setForm(form);

        return resource;
    }

    /**
     * PUT method for updating or creating an instance of EPCIS URL
     *
     *
     * @param context
     * @param url
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource setEpcisUrl(UriInfo context, String url) {
        String   name        = "EPCIS URL";
        String   path        = CONFIG_EPCIS;
        String   description = "This is the Interface to configure the EPCIS URL.";
        Resource resource    = setUpResource(context, name, description, path);

        int      start       = url.indexOf("=");

        url = url.substring(start + 1);

        Config.SET_EPCIS_REPOSITORY_URL(url);

        Initializer.loadEventFinderEntriesFromEPCIS();

        resource.setDescription("URL set to: " + url);

        return resource;
    }

    /**
     * Returns a representation of the config FEED URL resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getFeedUrl(UriInfo context) {
        String   name        = "Feed URL";
        String   path        = CONFIG_FEED;
        String   description = "This is the Interface to configure the FEED URL.";
        Resource resource    = setUpResource(context, name, description, path);

        String   value       = Config.GET_FEED_URL();

        Content  myUrl       = new Content();

        resource.setFields(myUrl);

        Entry entry = new Entry();

        entry.setName("The actual FEED URL");
        entry.setValue(value);
        myUrl.getContent().add(entry);

        Form   form   = new Form();
        URI    uri    = new URI(context);
        String action = uri.getRestURI(path);

        form.setAction(action);
        form.setMethod(HTTP.AJAX_PUT);
        form.setDescription("The FEED URL");
        form.setActionDescription("set feed url");

        Entry input = new Entry();

        input.setId("FEED");
        input.setName("FEED URL");
        input.setValue(value);
        input.setValueRef(resource.getUri());

        form.getEntries().add(input);

        resource.setForm(form);

        return resource;
    }

    /**
     * PUT method for updating or creating an instance of FEED URL
     *
     *
     * @param context
     * @param url
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource setFeedUrl(UriInfo context, String url) {
        String   name        = "Feed URL";
        String   path        = CONFIG_FEED;
        String   description = "This is the Interface to configure the FEED URL.";
        Resource resource    = setUpResource(context, name, description, path);

        int      start       = url.indexOf("=");

        url = url.substring(start + 1);

        Config.SET_FEED_URL(url);

        resource.setDescription("URL set to: " + url);

        return resource;
    }

    /**
     * Returns a representation of the about resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getAbout(UriInfo context) {
        String   name        = "About";
        String   path        = ABOUT;
        String   description = "This is the About Interface of the EPCIS REST Adapter.";
        Resource resource    = setUpResource(context, name, description, path);

        Content  links       = new Content();

        resource.setFields(links);

        addLink(context, links, "API Version Information", ABOUT_VERSION);
        addLink(context, links, "Supported Query Names", ABOUT_QUERYNAMES);
        addLink(context, links, "About the Authors", ABOUT_AUTHORS);

        return resource;
    }

    /**
     * Returns a representation of the authors resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getAuthors(UriInfo context) {
        String   name        = "About the Authors";
        String   description = "The Authors or the EPCIS REST Adapter.";
        String   path        = ABOUT_AUTHORS;
        Resource resource    = setUpResource(context, name, description, path);

        Content  links       = new Content();

        resource.setFields(links);

        for (int i = 0; i < Config.AUTHORS.length; i++) {
            addLink(context, links, Config.AUTHORS[i][0], ABOUT_AUTHOR, Config.AUTHORS[i][0]);
        }

        return resource;
    }

    /**
     * Returns a representation of the author resource according to the requested mime type
     *
     *
     * @param context
     * @param id
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getAuthor(UriInfo context, String id) {
        String     name        = id;
        String     description = "Presenting an Author of the EPCIS REST Adapter.";
        String     path        = ABOUT_AUTHOR;
        Resource   resource    = setUpResource(context, name, description, path, id);

        String[][] authors     = Config.AUTHORS;

        for (String[] author : authors) {
            if (author[0].equals(id)) {
                Content links = new Content();

                resource.setFields(links);

                Entry authorName = new Entry();

                authorName.setValue(author[1]);
                authorName.setName("Name");
                authorName.setValueRef(author[5]);
                links.getContent().add(authorName);

                Entry authorEmail = new Entry();

                authorEmail.setValue(author[2]);
                authorEmail.setName("Email");
                authorEmail.setValueRef("mailto:" + author[2]);
                links.getContent().add(authorEmail);

                Entry authorOrganisation = new Entry();

                authorOrganisation.setValue(author[3]);
                authorOrganisation.setName("Organisation");
                authorOrganisation.setValueRef(author[4]);
                links.getContent().add(authorOrganisation);
            }
        }

        return resource;
    }

    /**
     * Returns a representation of the querynames resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getQueryNames(UriInfo context) {
        String       name        = "Query Names";
        String       description = "A list of all supported Query Names.";
        String       path        = ABOUT_QUERYNAMES;
        Resource     resource    = setUpResource(context, name, description, path);

        List<String> values      = new ArrayList<String>();

        try {
            values = EPCISWebServiceClient.getQueryNames();
        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        Content links = new Content();

        resource.setFields(links);

        for (String s : values) {
            Entry link = new Entry();

            link.setValue(s);
            links.getContent().add(link);
        }

        return resource;
    }

    /**
     * Returns a representation of the version resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getVersion(UriInfo context) {
        String   name        = "Version";
        String   path        = ABOUT_VERSION;
        String   description = "This is the About Version Interface of the EPCIS REST Adapter.";
        Resource resource    = setUpResource(context, name, description, path);

        Content  links       = new Content();

        resource.setFields(links);

        addLink(context, links, "REST API Version", VERSION_REST);
        addLink(context, links, "EPCIS Standard Version", VERSION_STANDARD);
        addLink(context, links, "EPCIS Vendor's Implementation Version", VERSION_VENDOR);

        return resource;
    }

    /**
     * Returns a representation of the EPCIS REST Adapter version resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getRest(UriInfo context) {
        String   name        = "EPCIS REST Adapter Version";
        String   value       = Config.EPCIS_REST_ADAPTER_VERSION;
        String   description = "REST API Version";

        String   path        = VERSION_REST;
        Resource resource    = setUpResource(context, name, description, path);

        Content  links       = new Content();

        resource.setFields(links);

        Entry myVersion = new Entry();

        myVersion.setValue(value);
        links.getContent().add(myVersion);

        return resource;
    }

    /**
     * Returns a representation of the EPCIS standard version resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getStandard(UriInfo context) {
        String name  = "Standard Version";
        String path  = VERSION_STANDARD;
        String value = "undefined";

        try {
            value = EPCISWebServiceClient.getStandardVersion();
        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        String   description = "EPCIS Standard Version.";
        Resource resource    = setUpResource(context, name, description, path);

        Content  links       = new Content();

        resource.setFields(links);

        Entry myVersion = new Entry();

        myVersion.setValue(value);
        links.getContent().add(myVersion);

        return resource;
    }

    /**
     * Returns a representation of the EPCIS vendor (implementor) version resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getVendor(UriInfo context) {
        String name = "Vendor Version";
        String path = VERSION_VENDOR;
        String value;

        try {
            value = EPCISWebServiceClient.getVendorVersion();
        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        String   description = "EPCIS Vendor's Implementation Version.";
        Resource resource    = setUpResource(context, name, description, path);

        Content  links       = new Content();

        resource.setFields(links);

        Entry myVersion = new Entry();

        myVersion.setValue(value);
        links.getContent().add(myVersion);

        return resource;
    }
}
