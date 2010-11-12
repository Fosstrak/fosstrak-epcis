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

import org.epcis.fosstrak.restadapter.config.Config;
import javax.servlet.ServletContext;
import org.epcis.fosstrak.restadapter.logic.CaptureBusinessLogic;
import org.epcis.fosstrak.restadapter.logic.QueryBusinessLogic;
import org.epcis.fosstrak.restadapter.logic.RESTfulEPCISBusinessLogic;
import org.epcis.fosstrak.restadapter.logic.SubscriptionBusinessLogic;
import org.epcis.fosstrak.restadapter.model.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import static org.epcis.fosstrak.restadapter.config.URIConstants.*;
import static org.epcis.fosstrak.restadapter.config.QueryParamConstants.*;
import static javax.ws.rs.core.MediaType.*;

/**
 * EPCIS REST Adapter
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
@Path(EPCIS_ROOT)
@Produces({Config.TEXT_HTML_WEBKIT_SAFE, APPLICATION_JSON, APPLICATION_XHTML_XML, TEXT_XML, Config.APPLICATION_JSONP, "application/javascript"})
public class RESTfulEPCIS implements ICaptureResource, IQueryResource, IRESTfulEPCISResource, ISubscriptionResource {

    @Context
    ServletContext servletContext;

    /**
     * Returns a representation of the EPCIS REST Adapter home resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @GET
    public Resource getRESTfulEPCIS(@Context UriInfo context) {
        System.setProperty("sqlite.system.home", servletContext.getRealPath("/"));
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();
        return logic.getRESTfulEPCIS(context);
    }

    /**
     * Returns a representation of the config resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(CONFIG)
    @GET
    public Resource getConfig(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getConfig(context);
    }

    /**
     * Returns a representation of the reset EPCIS REST Adapter resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(CONFIG_RESET_RESTFULEPCIS)
    @GET
    public Resource getResetRestfulEpcis(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getResetRestfulEpcis(context);
    }

    /**
     * POST method for reseting the EPCIS REST Adapter Database
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(CONFIG_RESET_RESTFULEPCIS)
    @POST
    public Resource doResetRestfulEpcis(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.doResetRestfulEpcis(context);
    }

    /**
     * Returns a representation of the reload finder resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(CONFIG_RELOAD_FINDER)
    @GET
    public Resource getReloadFinder(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getReloadFinder(context);
    }

    /**
     * POST method for reloading the EPCIS REST Adapter Finder
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(CONFIG_RELOAD_FINDER)
    @POST
    public Resource doReloadFinder(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.doReloadFinder(context);
    }

    /**
     * Returns a representation of the config EPCIS URL resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(CONFIG_EPCIS)
    @GET
    public Resource getEpcisUrl(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getEpcisUrl(context);
    }

    /**
     * PUT method for updating or creating an instance of EPCIS URL
     *
     * @param content representation for the resource
     *
     * @param context
     * @param url
     * @return an HTTP Resource with content of the updated or created resource.
     */
    @Path(CONFIG_EPCIS)
    @PUT
//  @Consumes(TEXT_PLAIN)
    public Resource setEpcisUrl(@Context UriInfo context, String url) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.setEpcisUrl(context, url);
    }

    /**
     * Returns a representation of the config FEED URL resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(CONFIG_FEED)
    @GET
    public Resource getFeedUrl(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getFeedUrl(context);
    }

    /**
     * PUT method for updating or creating an instance of FEED URL
     *
     * @param content representation for the resource
     *
     * @param context
     * @param url
     * @return an HTTP Resource with content of the updated or created resource.
     */
    @Path(CONFIG_FEED)
    @PUT
//  @Consumes(TEXT_PLAIN)
    public Resource setFeedUrl(@Context UriInfo context, String url) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.setFeedUrl(context, url);
    }

    /**
     * Returns a representation of the about resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(ABOUT)
    @GET
    public Resource getAbout(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getAbout(context);
    }

    /**
     * Returns a representation of the version resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(ABOUT_VERSION)
    @GET
    public Resource getVersion(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getVersion(context);
    }

    /**
     * Returns a representation of the EPCIS REST Adapter version resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(VERSION_REST)
    @GET
    public Resource getRest(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getRest(context);
    }

    /**
     * Returns a representation of the EPCIS standard version resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(VERSION_STANDARD)
    @GET
    public Resource getStandard(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getStandard(context);
    }

    /**
     * Returns a representation of the EPCIS vendor (implementor) version resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(VERSION_VENDOR)
    @GET
    public Resource getVendor(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getVendor(context);
    }

    /**
     * Returns a representation of the authors resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(ABOUT_AUTHORS)
    @GET
    public Resource getAuthors(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getAuthors(context);
    }

    /**
     * Returns a representation of the author resource according to the requested mime type
     *
     * @param context
     * @param id
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(ABOUT_AUTHOR)
    @GET
    public Resource getAuthor(@Context UriInfo context, @PathParam(ID) String id) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getAuthor(context, id);
    }

    /**
     * Returns a representation of the querynames resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(ABOUT_QUERYNAMES)
    @GET
    public Resource getQueryNames(@Context UriInfo context) {
        RESTfulEPCISBusinessLogic logic = new RESTfulEPCISBusinessLogic();

        return logic.getQueryNames(context);
    }

    /**
     * Returns a representation of the selected read points resource returning all available read points according to the requested mime type
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(FINDER_ALL_READ_POINTS)
    @GET
    public Resource getAllReadPoints(@Context UriInfo context) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getAllReadPoints(context);
    }

    /**
     * Returns a representation of the business locations resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(FINDER_BUSINESS_LOCATIONS)
    @GET
    public Resource getBusinessLocations(@Context UriInfo context) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getBusinessLocations(context);
    }

    /**
     * Returns a representation of the selected business location resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(FINDER_BUSINESS_LOCATION)
    @GET
    public Resource getSelectedBusinessLocation(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getSelectedBusinessLocation(context, businessLocation);
    }

    /**
     * Returns a representation of the read points resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(FINDER_READ_POINTS)
    @GET
    public Resource getReadPoints(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getReadPoints(context, businessLocation);
    }

    /**
     * Returns a representation of the selected read point resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(FINDER_READ_POINT)
    @GET
    public Resource getSelectedReadPoint(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getSelectedReadPoint(context, businessLocation, readPoint);
    }

    /**
     * Returns a representation of the event times resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(FINDER_EVENT_TIMES)
    @GET
    public Resource getEventTimes(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getEventTimes(context, businessLocation, readPoint);
    }

    /**
     * Returns a representation of the selected event time resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(FINDER_EVENT_TIME)
    @GET
    public Resource getSelectedEventTime(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getSelectedEventTime(context, businessLocation, readPoint, eventTime);
    }

    /**
     * Returns a representation of the event resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(FINDER_EVENT)
    @GET
    public Resource getEvent(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("0") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getEvent(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event's record time resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_RECORD_TIME)
    @GET
    public Resource getRecordTime(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getRecordTime(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event's time zone offset resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_TIME_ZONE_OFFSET)
    @GET
    public Resource getTimeZoneOffset(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getTimeZoneOffset(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event's business step resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_BUSINESS_STEP)
    @GET
    public Resource getBusinessStep(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getBusinessStep(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event's action resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_ACTION)
    @GET
    public Resource getAction(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getAction(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event's event time resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_EVENT_TIME)
    @GET
    public Resource getEventTime(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getEventTime(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event's read point resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_READ_POINT)
    @GET
    public Resource getReadPoint(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getReadPoint(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event's business location resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_BUSINESS_LOCATION)
    @GET
    public Resource getBusinessLocation(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getBusinessLocation(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event's disposition resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_DISPOSITION)
    @GET
    public Resource getDisposition(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getDisposition(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event's event type resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_EVENT_TYPE)
    @GET
    public Resource getEventType(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getEventType(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event's epc's resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_EPCS)
    @GET
    public Resource getEpcs(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getEpcs(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of a event epc resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @param epc
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_EPC)
    @GET
    public Resource getEpc(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index, @PathParam(EPC) String epc) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getEpc(context, businessLocation, readPoint, eventTime, index, epc);
    }

    /**
     * Returns a representation of the event's business transaction's resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_BUSINESS_TRANSACTIONS)
    @GET
    public Resource getBusinessTransactions(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getBusinessTransactions(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of a event business transaction resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @param businessTransaction
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_BUSINESS_TRANSACTION)
    @GET
    public Resource getBusinessTransaction(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index, @PathParam(BUSINESS_TRANSACTION) String businessTransaction) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getBusinessTransaction(context, businessLocation, readPoint, eventTime, index, businessTransaction);
    }

    /**
     * Returns a representation of the event's parent id resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_PARENT_ID)
    @GET
    public Resource getParentID(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getParentID(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event's epc class resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_EPC_CLASS)
    @GET
    public Resource getEPCClass(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getEPCClass(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event's quantity resource according to the requested mime type
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENT_QUANTITY)
    @GET
    public Resource getQuantity(@Context UriInfo context, @PathParam(BUSINESS_LOCATION) String businessLocation, @PathParam(READ_POINT) String readPoint, @PathParam(EVENT_TIME) String eventTime, @QueryParam(INDEX_QP)
            @DefaultValue("1") String index) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getQuantity(context, businessLocation, readPoint, eventTime, index);
    }

    /**
     * Returns a representation of the event query creator resource according to the requested mime type
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
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(EVENTQUERY)
    @GET
    public Resource getEventQuery(@Context UriInfo context, @QueryParam(EVENT_TIME_REST) String eventTime, @QueryParam(RECORD_TIME_REST) String recordTime, @QueryParam(EVENT_TYPE_REST) String eventType, @QueryParam(ACTION_REST) String action, @QueryParam(BUSINESS_STEP_REST) String bizStep, @QueryParam(DISPOSITION_REST) String disposition, @QueryParam(READ_POINT_REST) String readPoint, @QueryParam(READ_POINT_DESCENDANT_REST) String readPointWD, @QueryParam(BUSINESS_LOCATION_REST) String bizLocation, @QueryParam(BUSINESS_LOCATION_DESCENDANT_REST) String bizLocationWD, @QueryParam(BUSINESS_TRANSACTION_TYPE_REST) String bizTransaction, @QueryParam(EPC_REST) String epc, @QueryParam(PARENT_ID_REST) String parentID, @QueryParam(ANY_EPC_REST) String anyEPC, @QueryParam(EPC_CLASS_REST) String epcClass, @QueryParam(QUANTITY_REST) String quantity, @QueryParam(FIELDNAME_REST) String fieldname, @QueryParam(ORDER_BY_REST) String orderBy, @QueryParam(ORDER_DIRECTION_REST) String orderDirection, @QueryParam(EVENT_COUNT_LIMIT_REST) String eventCountLimit, @QueryParam(MAX_EVENT_COUNT_REST) String maxEventCount) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        return logic.getEventQuery(context, eventTime, recordTime, eventType, action, bizStep, disposition, readPoint, readPointWD, bizLocation, bizLocationWD, bizTransaction, epc, parentID, anyEPC, epcClass, quantity, fieldname, orderBy, orderDirection, eventCountLimit, maxEventCount);
    }
    /**
     * Returns a representation of the query results resource according to the requested mime type
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
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Context
    HttpServletRequest myContext;

    @Path(EVENTQUERY_RESULTS)
    @GET
    public Resource getResults(@Context UriInfo context, @QueryParam(EVENT_TIME_REST) String eventTime, @QueryParam(RECORD_TIME_REST) String recordTime, @QueryParam(EVENT_TYPE_REST) String eventType, @QueryParam(ACTION_REST) String action, @QueryParam(BUSINESS_STEP_REST) String bizStep, @QueryParam(DISPOSITION_REST) String disposition, @QueryParam(READ_POINT_REST) String readPoint, @QueryParam(READ_POINT_DESCENDANT_REST) String readPointWD, @QueryParam(BUSINESS_LOCATION_REST) String bizLocation, @QueryParam(BUSINESS_LOCATION_DESCENDANT_REST) String bizLocationWD, @QueryParam(BUSINESS_TRANSACTION_TYPE_REST) String bizTransaction, @QueryParam(EPC_REST) String epc, @QueryParam(PARENT_ID_REST) String parentID, @QueryParam(ANY_EPC_REST) String anyEPC, @QueryParam(EPC_CLASS_REST) String epcClass, @QueryParam(QUANTITY_REST) String quantity, @QueryParam(FIELDNAME_REST) String fieldname, @QueryParam(ORDER_BY_REST) String orderBy, @QueryParam(ORDER_DIRECTION_REST) String orderDirection, @QueryParam(EVENT_COUNT_LIMIT_REST) String eventCountLimit, @QueryParam(MAX_EVENT_COUNT_REST) String maxEventCount) {
        QueryBusinessLogic logic = new QueryBusinessLogic();

        if (myContext != null) {
            if (myContext.getHeader("Accept").toLowerCase().contains(MediaType.TEXT_XML)) {
                return logic.getResultsXmlOnly(context, eventTime, recordTime, eventType, action, bizStep, disposition, readPoint, readPointWD, bizLocation, bizLocationWD, bizTransaction, epc, parentID, anyEPC, epcClass, quantity, fieldname, orderBy, orderDirection, eventCountLimit, maxEventCount);
            }
        }
        return logic.getResults(context, eventTime, recordTime, eventType, action, bizStep, disposition, readPoint, readPointWD, bizLocation, bizLocationWD, bizTransaction, epc, parentID, anyEPC, epcClass, quantity, fieldname, orderBy, orderDirection, eventCountLimit, maxEventCount);
    }

    /**
     * POST method for adding a subscription
     * Resource for subscribing to a FEED
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
     * @return an HTTP Resource with link to the existing or created feed.
     */
    @Path(EVENTQUERY_SUBSCRIPTION)
    @PUT
    public Resource addSubscription(@Context UriInfo context, @QueryParam(EVENT_TIME_REST) String eventTime, @QueryParam(RECORD_TIME_REST) String recordTime, @QueryParam(EVENT_TYPE_REST) String eventType, @QueryParam(ACTION_REST) String action, @QueryParam(BUSINESS_STEP_REST) String bizStep, @QueryParam(DISPOSITION_REST) String disposition, @QueryParam(READ_POINT_REST) String readPoint, @QueryParam(READ_POINT_DESCENDANT_REST) String readPointWD, @QueryParam(BUSINESS_LOCATION_REST) String bizLocation, @QueryParam(BUSINESS_LOCATION_DESCENDANT_REST) String bizLocationWD, @QueryParam(BUSINESS_TRANSACTION_TYPE_REST) String bizTransaction, @QueryParam(EPC_REST) String epc, @QueryParam(PARENT_ID_REST) String parentID, @QueryParam(ANY_EPC_REST) String anyEPC, @QueryParam(EPC_CLASS_REST) String epcClass, @QueryParam(QUANTITY_REST) String quantity, @QueryParam(FIELDNAME_REST) String fieldname, @QueryParam(ORDER_BY_REST) String orderBy, @QueryParam(ORDER_DIRECTION_REST) String orderDirection, @QueryParam(EVENT_COUNT_LIMIT_REST) String eventCountLimit, @QueryParam(MAX_EVENT_COUNT_REST) String maxEventCount) {
        SubscriptionBusinessLogic logic = new SubscriptionBusinessLogic();

        return logic.addSubscription(context, eventTime, recordTime, eventType, action, bizStep, disposition, readPoint, readPointWD, bizLocation, bizLocationWD, bizTransaction, epc, parentID, anyEPC, epcClass, quantity, fieldname, orderBy, orderDirection, eventCountLimit, maxEventCount);
    }

    /**
     * Returns a representation of the existing subscribtions list resource according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(SUBSCRIPTIONS)
    @GET
    public Resource getSubscriptions(@Context UriInfo context) {
        SubscriptionBusinessLogic logic = new SubscriptionBusinessLogic();

        return logic.getSubscriptions(context);
    }

    /**
     * Returns a representation a subscription resource (the link to a feed) according to the requested mime type
     *
     * @param context
     * @param id
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(SUBSCRIPTIONS_ID)
    @GET
    public Resource getSubscription(@Context UriInfo context, @PathParam(ID) String id) {
        SubscriptionBusinessLogic logic = new SubscriptionBusinessLogic();

        return logic.getSubscription(context, id);
    }

    /**
     * POST method for adding an entry to the corrsponding feed
     * Returns a representation of the according subscription resource (the link to a feed) according to the requested mime type
     *
     * @param context
     * @param id
     * @param entry
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(SUBSCRIPTIONS_ID)
    @Consumes(TEXT_XML)
    @POST
    public Resource addEntryToSubscription(@Context UriInfo context, @PathParam(ID) String id, String entry) {
        SubscriptionBusinessLogic logic = new SubscriptionBusinessLogic();

        return logic.addEntryToSubscription(context, id, entry);
    }

    /**
     * DELETE method for deleting a feed
     * Returns a representation of a link back to the subscriptions according to the requested mime type
     *
     * @param context
     * @param id
     * @param data
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(SUBSCRIPTIONS_ID)
    @DELETE
    public Resource unsubscribeSubscription(@Context UriInfo context, @PathParam(ID) String id, @DefaultValue("unsubscribe") String data) {
        SubscriptionBusinessLogic logic = new SubscriptionBusinessLogic();

        return logic.unsubscribeSubscription(context, id, data);
    }

    /**
     * Returns a representation of the capture resource (howto) according to the requested mime type
     *
     * @param context
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(CAPTURE)
    @GET
    public Resource getCapture(@Context UriInfo context) {
        CaptureBusinessLogic logic = new CaptureBusinessLogic();

        return logic.getCapture(context);
    }

    /**
     * POST method for adding a captured event
     *
     * @param context
     * @param event representation of an EPCIS Event whose XML Schema is definded in the EPCGlobal's standard for the EPCIS
     * @return an HTTP Resource with content of the added event resource.
     */
    @Path(CAPTURE)
    @POST
    public Resource addCapture(@Context UriInfo context, String event) {
        CaptureBusinessLogic logic = new CaptureBusinessLogic();

        return logic.addCapture(context, event);
    }

    /**
     * Returns a representation of the capture simulator resource
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    @Path(CAPTURE_SIMULATOR)
    @GET
    public Resource getCaptureSimulation(@Context UriInfo context) {
        CaptureBusinessLogic logic = new CaptureBusinessLogic();

        return logic.getCaptureSimulation(context);
    }

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
     * @return
     */
    @Path(CAPTURE_SIMULATOR)
    @Consumes(APPLICATION_FORM_URLENCODED)
    @POST
    public Resource addCaptureSimulation(@Context UriInfo context, @DefaultValue("")
            @FormParam(EVENT_TIME_REST) String eventTime, @DefaultValue("")
            @FormParam(TIME_ZONE_OFFSET) String timeZoneOffset, @DefaultValue("")
            @FormParam(BUSINESS_STEP_REST) String businessStep, @DefaultValue("")
            @FormParam(DISPOSITION_REST) String disposition, @DefaultValue("")
            @FormParam(READ_POINT_REST) String readPoint, @DefaultValue("")
            @FormParam(BUSINESS_LOCATION_REST) String businessLocation, @DefaultValue("")
            @FormParam(BUSINESS_TRANSACTION_TYPE_REST) String businessTransaction, @DefaultValue("")
            @FormParam(EVENT_TYPE_REST) String eventType, @DefaultValue("")
            @FormParam(EPC_REST) String epc, @DefaultValue("")
            @FormParam(ACTION_REST) String action, @DefaultValue("")
            @FormParam(PARENT_ID_REST) String parentId, @DefaultValue("")
            @FormParam(EPC_CLASS_REST) String epcClass, @DefaultValue("")
            @FormParam(QUANTITY_REST) String quantity) {
        CaptureBusinessLogic logic = new CaptureBusinessLogic();

        return logic.addCaptureSimulation(context, eventTime, timeZoneOffset, businessStep, disposition, readPoint, businessLocation, businessTransaction, eventType, epc, action, parentId, epcClass, quantity);
    }
}
