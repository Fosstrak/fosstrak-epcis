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
 * Interface for the subscription logic
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch
 *
 *
 */
public interface ISubscriptionResource {

    /**
     * Resource for subscribing to a FEED
     *
     *
     * @param context
     * @param eventTime
     * @param recordTime
     * @param eventType
     * @param action
     * @param bizStep
     * @param disposition
     * @param readPoint
     * @param readPointWD
     * @param bizLocation
     * @param bizLocationWD
     * @param bizTransaction
     * @param epc
     * @param parentID
     * @param anyEPC
     * @param epcClass
     * @param quantity
     * @param fieldname
     * @param orderBy
     * @param orderDirection
     * @param eventCountLimit
     * @param maxEventCount
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource addSubscription(UriInfo context, String eventTime, String recordTime, String eventType, String action, String bizStep, String disposition, String readPoint, String readPointWD, String bizLocation, String bizLocationWD, String bizTransaction, String epc, String parentID, String anyEPC, String epcClass, String quantity, String fieldname, String orderBy, String orderDirection, String eventCountLimit, String maxEventCount);

    /**
     * POST method for adding a subscription
     * Returns a representation of the existing subscribtions list resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getSubscriptions(UriInfo context);

    /**
     * Returns a representation a subscription resource (the link to a feed) according to the requested mime type
     *
     *
     * @param context
     * @param id
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getSubscription(UriInfo context, String id);

    /**
     * POST method for adding an entry to the corrsponding feed
     * Returns a representation of the according subscription resource (the link to a feed) according to the requested mime type
     *
     *
     * @param context
     * @param id
     * @param entry
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource addEntryToSubscription(UriInfo context, String id, String entry);

    /**
     * DELETE method for deleting a feed
     * Returns a representation of a link back to the subscriptions according to the requested mime type
     *
     *
     * @param context
     * @param id
     * @param data
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource unsubscribeSubscription(UriInfo context, String id, String data);
}
