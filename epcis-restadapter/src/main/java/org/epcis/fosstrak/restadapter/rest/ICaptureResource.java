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
 * Business Logic for the capture
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
public interface ICaptureResource {

    /**
     * Returns a representation of the capture resource (howto) according to the requested mime type
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getCapture(UriInfo context);

    /**
     * POST method for adding a captured event
     *
     *
     * @param context
     * @param event
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource addCapture(UriInfo context, String event);

    /**
     * Returns a representation of the capture simulator resource
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getCaptureSimulation(UriInfo context);

    /**
     * POST method for simulating a captured event in encoded in query params
     *
     *
     * @param context
     * @param eventTime
     * @param timeZoneOffset
     * @param businessStep
     * @param disposition
     * @param readPoint
     * @param businessLocation
     * @param businessTransaction
     * @param eventType
     * @param epc
     * @param action
     * @param parentId
     * @param epcClass
     * @param quantity
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource addCaptureSimulation(UriInfo context, String eventTime, String timeZoneOffset, String businessStep, String disposition, String readPoint, String businessLocation, String businessTransaction, String eventType, String epc, String action, String parentId, String epcClass, String quantity);
}
