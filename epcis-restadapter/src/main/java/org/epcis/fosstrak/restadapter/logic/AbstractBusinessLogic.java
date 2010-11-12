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
import org.epcis.fosstrak.restadapter.model.Entry;
import org.epcis.fosstrak.restadapter.model.Content;
import org.epcis.fosstrak.restadapter.model.Resource;
import org.epcis.fosstrak.restadapter.model.TreeMenu;
import javax.ws.rs.core.UriInfo;

/**
 * Class containing helper methods to initialize the Resources.
 * Encapsulates common Resource handling logic.
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 */
public abstract class AbstractBusinessLogic {

    private static boolean isInitialized = false;

    /**
     * Creates a resource.
     * Encapsulates common Resource handling logic.
     *
     *
     * @param context
     * @param name
     * @param description
     * @param path
     * @param args
     *
     * @return
     */
    public static Resource setUpResource(UriInfo context, String name, String description, String path, Object... args) {

        if (!isInitialized) {
            isInitialized = true;
            Initializer.initializeRESTfulEPCIS(context);
        }
        Resource resource = new Resource();

        resource.setName(name);
        resource.setDescription(description);

        TreeMenu treeMenu = new TreeMenu();

        URI currentContext = new URI(context);
        treeMenu.calculateAndSetTreeMenuFromURL(currentContext.getHostURL(), currentContext.getRestURI());
        resource.setTreeMenu(treeMenu);

        String url = context.getRequestUri().toASCIIString();

        resource.setUri(url);

        return resource;
    }

    /**
     * Adds a link to the resource
     *
     *
     * @param context
     * @param description
     * @param links
     * @param name
     * @param ref
     * @param args
     */
    public static void addLink(UriInfo context, String description, Content links, String name, String ref, Object... args) {
        Entry link = new Entry();

        link.setValue(name);
        link.setName(description);

        URI uri = new URI(context);

        link.setValueRef(uri.getRestURI(ref, args));
        links.getContent().add(link);
    }

    /**
     * Adds a link to the resource
     *
     *
     * @param context
     * @param links
     * @param name
     * @param ref
     * @param args
     */
    public static void addLink(UriInfo context, Content links, String name, String ref, Object... args) {
        Entry link = new Entry();

        link.setValue(name);

        URI uri = new URI(context);

        link.setValueRef(uri.getRestURI(ref, args));
        links.getContent().add(link);
    }

    /**
     * Adds a link to the resource (as absolute link)
     *
     *
     * @param links
     * @param name
     * @param ref
     */
    public static void addLinkAbsolute(Content links, String name, String ref) {
        Entry link = new Entry();

        link.setValue(name);
        link.setValueRef(ref);
        links.getContent().add(link);
    }
}
