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
import org.epcis.fosstrak.restadapter.model.events.AggregationEvent;
import org.epcis.fosstrak.restadapter.model.events.EPCISEvent;
import org.epcis.fosstrak.restadapter.model.epc.ElectronicProductCode;
import org.epcis.fosstrak.restadapter.model.events.ObjectEvent;
import org.epcis.fosstrak.restadapter.model.events.QuantityEvent;
import org.epcis.fosstrak.restadapter.model.events.TransactionEvent;
import org.epcis.fosstrak.restadapter.rest.IQueryResource;
import org.epcis.fosstrak.restadapter.ws.epcis.query.SEQuery;
import org.epcis.fosstrak.restadapter.ws.generated.Poll;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParams;
import org.epcis.fosstrak.restadapter.ws.generated.QueryResults;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import static org.epcis.fosstrak.restadapter.config.URIConstants.*;

/**
 *
 * Business logic for the Query
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 */
public class QueryBusinessLogic extends AbstractQueryParamBusinessLogic implements IQueryResource {

    @Context
    HttpServletRequest myContext;

    /**
     * Returns a representation of the selected read points resource returning all available read points according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getAllReadPoints(UriInfo context) {
        String name = "All Available Read Points";
        String path = FINDER_ALL_READ_POINTS;
        String description = "This is a list of all available read points in the EPCIS.";
        Resource resource = setUpResource(context, name, description, path);

        Content myContent = new Content();

        resource.setFields(myContent);

        List<String> locations = getAllReaders();

        for (String location : locations) {
            Entry myEntry = new Entry();

            myEntry.setValue(location);
            myContent.getContent().add(myEntry);
        }

        return resource;
    }

    /**
     * Returns a representation of the business locations resource according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getBusinessLocations(UriInfo context) {
        String name = "Business Locations";
        String path = FINDER_BUSINESS_LOCATIONS;
        String description = "This is a list of all business locations available in the EPCIS.";
        Resource resource = setUpResource(context, name, description, path);

        Content links = new Content();

        resource.setFields(links);

        List<String> locations = getLocations();

        for (String location : locations) {
            addLink(context, links, location, FINDER_BUSINESS_LOCATION, location);
        }

        Form form = new Form();

        URI uri = new URI(context);

        form.setAction(uri.getRestURI(CONFIG_RELOAD_FINDER));
        form.setActionDescription("reload list");
        form.setMethod(HTTP.POST);
        resource.setForm(form);

        return resource;
    }

    /**
     * Returns a representation of the selected  business locations resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getSelectedBusinessLocation(UriInfo context, String businessLocation) {
        businessLocation = URI.unescapeURL(businessLocation);

        String name = "Business Location";
        String path = FINDER_BUSINESS_LOCATION;
        String description = "About business location: " + businessLocation + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation);

        Content links = new Content();

        resource.setFields(links);

        addLink(context, links, "List of Read Points", FINDER_READ_POINTS, businessLocation);

        ElectronicProductCode electronicProductCode = new ElectronicProductCode();
        Entry myEPC = new Entry();

        myEPC.setValue(businessLocation);
        electronicProductCode.setEpc(myEPC);

        List<Entry> components = electronicProductCode.getComponents();

        for (Entry e : components) {
            e.setValueRef(null);
            resource.getFields().getContent().add(e);
        }

        return resource;
    }

    /**
     * Returns a representation of the read points resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getReadPoints(UriInfo context, String businessLocation) {
        businessLocation = URI.unescapeURL(businessLocation);

        String name = "Read Points";
        String description = "This is a list of all read points for the selected business location " + businessLocation + ".";
        String path = FINDER_READ_POINTS;
        Resource resource = setUpResource(context, name, description, path, businessLocation);

        Content links = new Content();

        resource.setFields(links);

        List<String> readers = getReaders(businessLocation);

        for (String reader : readers) {
            addLink(context, links, reader, FINDER_READ_POINT, businessLocation, reader);
        }

        Form form = new Form();

        URI uri = new URI(context);

        form.setAction(uri.getRestURI(CONFIG_RELOAD_FINDER));
        form.setActionDescription("reload list");
        form.setMethod(HTTP.POST);
        resource.setForm(form);

        return resource;
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
    public Resource getSelectedReadPoint(UriInfo context, String businessLocation, String readPoint) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);

        String name = "Read Point";
        String path = FINDER_READ_POINT;
        String description = "About read point: " + readPoint + " for business location " + businessLocation + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint);

        Content links = new Content();

        resource.setFields(links);

        addLink(context, links, "List of Event Times", FINDER_EVENT_TIMES, businessLocation, readPoint);

        ElectronicProductCode electronicProductCode = new ElectronicProductCode();
        Entry myEPC = new Entry();

        myEPC.setValue(readPoint);
        electronicProductCode.setEpc(myEPC);

        List<Entry> components = electronicProductCode.getComponents();

        for (Entry e : components) {
            e.setValueRef(null);
            resource.getFields().getContent().add(e);
        }

        return resource;
    }

    /**
     * Returns a representation of the event times resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getEventTimes(UriInfo context, String businessLocation, String readPoint) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);

        String name = "Event Times of selected Read Point";
        String path = FINDER_EVENT_TIMES;
        String description = "This is a list of all event times for the selected read point " + readPoint + " of business location " + businessLocation + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint);

        Content links = new Content();

        resource.setFields(links);

        Collection<String> times = getTimes(businessLocation, readPoint);

        for (String time : times) {
            addLink(context, links, time, FINDER_EVENT_TIME, businessLocation, readPoint, time);
        }

        return resource;
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
    public Resource getSelectedEventTime(UriInfo context, String businessLocation, String readPoint, String eventTime) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Event Time";
        String path = FINDER_EVENT_TIME;
        String description = "About event time: " + eventTime + " captured by read point " + readPoint + " for business location " + businessLocation + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        addLink(context, links, "Event matching the current selection", FINDER_EVENT, businessLocation, readPoint, eventTime);

        return resource;
    }

    /**
     * Returns a representation of the event resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getEvent(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Event";
        String path = FINDER_EVENT;
        String description = "This is the event for event time " + eventTime + " captured by read point " + readPoint + " for business location " + businessLocation + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        if (index.equals("0")) {
            if (businessLocation.equals(Config.NO_VALUE)) {
                businessLocation = null;
            }

            if (readPoint.equals(Config.NO_VALUE)) {
                readPoint = null;
            }

            Resource searchedEventsResource = getResults(context, eventTime, null, null, null, null, null, readPoint, null, businessLocation, null, null, null, null, null, null, null, null, null, null, null, null);

            resource.setQueryResults(searchedEventsResource.getQueryResults());
        } else {
            EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

            appendEvent(event, resource);
        }

        return resource;
    }

    /**
     * Returns a representation of the event's record time resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getRecordTime(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Record Time";
        String path = EVENT_RECORD_TIME;
        String description = "This is the record time of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        Entry link = event.getRecordTimeEntry();

        links.getContent().add(link);

        return resource;
    }

    /**
     * Returns a representation of the event's time zone offset resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getTimeZoneOffset(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Time Zone Offset";
        String path = EVENT_TIME_ZONE_OFFSET;
        String description = "This is the time zone offset of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        Entry link = event.getTimeZoneOffsetEntry();

        links.getContent().add(link);

        return resource;
    }

    /**
     * Returns a representation of the event's business step resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getBusinessStep(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Business Step";
        String path = EVENT_BUSINESS_STEP;
        String description = "This is the business step of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        Entry link = event.getBizStepEntry();

        links.getContent().add(link);

        return resource;
    }

    /**
     * Returns a representation of the event's action resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getAction(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Action";
        String path = EVENT_ACTION;
        String description = "This is the action of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        if (event instanceof ObjectEvent) {
            ObjectEvent myEvent = (ObjectEvent) event;
            Entry link = myEvent.getActionEntry();

            links.getContent().add(link);
        }

        if (event instanceof AggregationEvent) {
            AggregationEvent myEvent = (AggregationEvent) event;
            Entry link = myEvent.getActionEntry();

            links.getContent().add(link);
        }

        if (event instanceof TransactionEvent) {
            TransactionEvent myEvent = (TransactionEvent) event;
            Entry link = myEvent.getActionEntry();

            links.getContent().add(link);
        }

        return resource;
    }

    /**
     * Returns a representation of the event's event time resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getEventTime(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Event Time";
        String path = EVENT_EVENT_TIME;
        String description = "This is the event time of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        Entry link = event.getEventTimeEntry();

        links.getContent().add(link);

        return resource;
    }

    /**
     * Returns a representation of the event's read point resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getReadPoint(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Read Point";
        String path = EVENT_READ_POINT;
        String description = "This is the read point of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        Entry link = event.getReadPointEntry();

        links.getContent().add(link);

        return resource;
    }

    /**
     * Returns a representation of the event's business location resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getBusinessLocation(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Business Location";
        String path = EVENT_BUSINESS_LOCATION;
        String description = "This is the business location of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        Entry link = event.getBizLocationEntry();

        links.getContent().add(link);

        return resource;
    }

    /**
     * Returns a representation of the event's disposition resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getDisposition(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Disposition";
        String path = EVENT_DISPOSITION;
        String description = "This is the disposition of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        Entry link = event.getDispositionEntry();

        links.getContent().add(link);

        return resource;
    }

    /**
     * Returns a representation of the event's event type resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getEventType(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Event Type";
        String path = EVENT_EVENT_TYPE;
        String description = "This is the event type of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        Entry link = event.getTypeEntry();

        links.getContent().add(link);

        return resource;
    }

    /**
     * Returns a representation of the event's epc list resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getEpcs(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "EPCs";
        String path = EVENT_EPCS;
        String description = "This is the list of EPC's of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        if (event instanceof ObjectEvent) {
            ObjectEvent myEvent = (ObjectEvent) event;
            List<ElectronicProductCode> epcs = myEvent.getEpcEntry();

            for (ElectronicProductCode epc : epcs) {
                Entry link = epc.getEpc();

                links.getContent().add(link);

            }
        }

        if (event instanceof AggregationEvent) {
            AggregationEvent myEvent = (AggregationEvent) event;
            List<ElectronicProductCode> epcs = myEvent.getEpcEntry();

            for (ElectronicProductCode epc : epcs) {
                Entry link = epc.getEpc();

                links.getContent().add(link);

            }
        }

        if (event instanceof TransactionEvent) {
            TransactionEvent myEvent = (TransactionEvent) event;
            List<ElectronicProductCode> epcs = myEvent.getEpcEntry();

            for (ElectronicProductCode epc : epcs) {
                Entry link = epc.getEpc();

                links.getContent().add(link);

            }
        }

        return resource;
    }

    /**
     * Returns a representation of the event's epc resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @param epc
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getEpc(UriInfo context, String businessLocation, String readPoint, String eventTime, String index, String epc) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);
        epc = URI.unescapeURL(epc);

        String name = "EPC";
        String path = EVENT_EPC;
        String description = "This is an EPC of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime, epc);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        int epcIndex = 0;

        try {
            epcIndex = Integer.parseInt(epc);
            epcIndex--;
        } catch (Exception ex) {
            // stay at init
        }

        if (event instanceof ObjectEvent) {
            ObjectEvent myEvent = (ObjectEvent) event;
            List<ElectronicProductCode> epcs = myEvent.getEpcEntry();
            Entry link = new Entry();
            List<Entry> components = new ArrayList<Entry>();

            try {
                link = epcs.get(epcIndex).getEpc();
                components = epcs.get(epcIndex).getComponents();
            } catch (Exception ex) {

                // do not return any epc in entry
                link.setName("EPC");
                link.setDescription("There is no EPC under this URI.");
            }

            links.getContent().add(link);
            links.getContent().addAll(components);
        }

        if (event instanceof AggregationEvent) {
            AggregationEvent myEvent = (AggregationEvent) event;
            List<ElectronicProductCode> epcs = myEvent.getEpcEntry();
            Entry link = new Entry();
            List<Entry> components = new ArrayList<Entry>();

            try {
                link = epcs.get(epcIndex).getEpc();
                components = epcs.get(epcIndex).getComponents();
            } catch (Exception ex) {

                // do not return any epc in entry
                link.setName("EPC");
                link.setDescription("There is no EPC under this URI.");
            }

            links.getContent().add(link);
            links.getContent().addAll(components);
        }

        if (event instanceof TransactionEvent) {
            TransactionEvent myEvent = (TransactionEvent) event;
            List<ElectronicProductCode> epcs = myEvent.getEpcEntry();
            Entry link = new Entry();
            List<Entry> components = new ArrayList<Entry>();

            try {
                link = epcs.get(epcIndex).getEpc();
                components = epcs.get(epcIndex).getComponents();
            } catch (Exception ex) {

                // do not return any epc in entry
                link.setName("EPC");
                link.setDescription("There is no EPC under this URI.");
            }

            links.getContent().add(link);
            links.getContent().addAll(components);
        }

        return resource;
    }

    /**
     * Returns a representation of a event business transaction list resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getBusinessTransactions(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Business Transactions";
        String path = BUSINESS_TRANSACTIONS;
        String description = "This is the list of business transaction's of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        List<Entry> bizTransactions = event.getBizTransactionEntry();

        for (Entry entry : bizTransactions) {
            links.getContent().add(entry);
        }

        return resource;
    }

    /**
     * Returns a representation of a event business transaction resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     * @param businessTransaction
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getBusinessTransaction(UriInfo context, String businessLocation, String readPoint, String eventTime, String index, String businessTransaction) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);
        businessTransaction = URI.unescapeURL(businessTransaction);

        String name = "Business Transaction";
        String path = BUSINESS_TRANSACTION;
        String description = "This is a Business Transaction of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime, businessTransaction);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        int transactionIndex = 0;

        try {
            transactionIndex = Integer.parseInt(businessTransaction);
            transactionIndex--;
        } catch (Exception ex) {
            // stay at init
        }

        List<Entry> transactions = event.getBizTransactionEntry();
        Entry link = new Entry();

        try {
            link = transactions.get(transactionIndex);
        } catch (Exception ex) {

            // do not return any epc in entry
            link.setName("Business Transaction");
            link.setDescription("There is no transaction under this URI.");
        }

        links.getContent().add(link);


        return resource;
    }

    /**
     * Returns a representation of the event's parent id resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getParentID(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Parent ID";
        String path = EVENT_PARENT_ID;
        String description = "This is the parent ID of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        if (event instanceof AggregationEvent) {
            AggregationEvent myEvent = (AggregationEvent) event;
            Entry link = myEvent.getParentIDEntry();

            links.getContent().add(link);
        }

        if (event instanceof TransactionEvent) {
            TransactionEvent myEvent = (TransactionEvent) event;
            Entry link = myEvent.getParentIDEntry();

            links.getContent().add(link);
        }

        return resource;
    }

    /**
     * Returns a representation of the event's epc class resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getEPCClass(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "EPC Class";
        String path = EVENT_EPC_CLASS;
        String description = "This is the EPC class of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        if (event instanceof QuantityEvent) {
            QuantityEvent myEvent = (QuantityEvent) event;
            Entry link = myEvent.getEpcClassEntry();

            links.getContent().add(link);
        }

        return resource;
    }

    /**
     * Returns a representation of the event's quantity resource according to the requested mime type
     *
     *
     * @param context
     * @param businessLocation
     * @param readPoint
     * @param eventTime
     * @param index
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getQuantity(UriInfo context, String businessLocation, String readPoint, String eventTime, String index) {
        businessLocation = URI.unescapeURL(businessLocation);
        readPoint = URI.unescapeURL(readPoint);
        eventTime = URI.unescapeURL(eventTime);

        String name = "Quantity";
        String path = EVENT_QUANTITY;
        String description = "This is the quantity of the event matching business location: " + businessLocation + ", read point: " + readPoint + " and event time: " + eventTime + ".";
        Resource resource = setUpResource(context, name, description, path, businessLocation, readPoint, eventTime);

        Content links = new Content();

        resource.setFields(links);

        EPCISEvent event = getEvent(businessLocation, readPoint, eventTime, index);

        if (event instanceof QuantityEvent) {
            QuantityEvent myEvent = (QuantityEvent) event;
            Entry link = myEvent.getQuantityEntry();

            links.getContent().add(link);
        }

        return resource;
    }

    /**
     * Returns a representation of the event query creator resource according to the requested mime type
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
    public Resource getEventQuery(UriInfo context, String eventTime, String recordTime, String eventType, String action, String bizStep, String disposition, String readPoint, String readPointWD, String bizLocation, String bizLocationWD, String bizTransaction, String epc, String parentID, String anyEPC, String epcClass, String quantity, String fieldname, String orderBy, String orderDirection, String eventCountLimit, String maxEventCount) {
        String name = "Event Query";
        String description = "Welcome to the EPCIS RESTful Simple Event Query Interface. Usage is as follows: " + EVENTQUERY + "?argName=delimitedArgValues{&argNameN=delimitedArgValuesN}* : where argName is one of the supported arguments and delimitedArgValues is a delimited list of argument values.";
        String path = EVENTQUERY;
        Resource resource = setUpResource(context, name, description, path);

        initAbstractQueryParamBusinessLogic(eventTime, recordTime, eventType, action, bizStep, disposition, readPoint, readPointWD, bizLocation, bizLocationWD, bizTransaction, epc, parentID, anyEPC, epcClass, quantity, fieldname, orderBy, orderDirection, eventCountLimit, maxEventCount);

        Form form = getForm();

        URI uri = new URI(context);

        form.setAction(uri.getRestURI(EVENTQUERY_RESULTS));
        form.setActionDescription("query");
        form.setMethod(HTTP.GET);

        resource.setForm(form);

        return resource;
    }

    /**
     * Returns a representation of the query results resource according to the requested mime type
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
    public Resource getResultsXmlOnly(UriInfo context, String eventTime, String recordTime, String eventType, String action, String bizStep, String disposition, String readPoint, String readPointWD, String bizLocation, String bizLocationWD, String bizTransaction, String epc, String parentID, String anyEPC, String epcClass, String quantity, String fieldname, String orderBy, String orderDirection, String eventCountLimit, String maxEventCount) {
        String name = "Event Query Results";
        String path = EVENTQUERY_RESULTS;
        String description = "The list of events for the according simple event query: ";
        Resource resource = setUpResource(context, name, description, path);

        String queryName = Config.SIMPLE_EVENT_QUERY;

        initAbstractQueryParamBusinessLogic(eventTime, recordTime, eventType, action, bizStep, disposition, readPoint, readPointWD, bizLocation, bizLocationWD, bizTransaction, epc, parentID, anyEPC, epcClass, quantity, fieldname, orderBy, orderDirection, eventCountLimit, maxEventCount);

        QueryResults queryResults = null;

        SEQuery seQuery;
        try {
            seQuery = getSEQuery();

            QueryParams queryParams = seQuery.getQueryParams();
            Poll poll = new Poll();

            poll.setQueryName(queryName);
            poll.setParams(queryParams);

            queryResults = EPCISWebServiceClient.pollSEQuery(poll);

            resource.setQueryResults_XML_ONLY(queryResults);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resource;

    }

    public Resource getResults(UriInfo context, String eventTime, String recordTime, String eventType, String action, String bizStep, String disposition, String readPoint, String readPointWD, String bizLocation, String bizLocationWD, String bizTransaction, String epc, String parentID, String anyEPC, String epcClass, String quantity, String fieldname, String orderBy, String orderDirection, String eventCountLimit, String maxEventCount) {
        String name = "Event Query Results";
        String path = EVENTQUERY_RESULTS;
        String description = "The list of events for the according simple event query: ";
        Resource resource = setUpResource(context, name, description, path);

        String queryName = Config.SIMPLE_EVENT_QUERY;

        initAbstractQueryParamBusinessLogic(eventTime, recordTime, eventType, action, bizStep, disposition, readPoint, readPointWD, bizLocation, bizLocationWD, bizTransaction, epc, parentID, anyEPC, epcClass, quantity, fieldname, orderBy, orderDirection, eventCountLimit, maxEventCount);

        QueryResults queryResults = null;

        try {
            SEQuery seQuery = getSEQuery();

            description = description + seQuery.toReadableString() + ".";
            resource.setDescription(description);

            Form form = new Form();
            String subscriptionURL = resource.getUri();

            subscriptionURL = subscriptionURL.replace(EVENTQUERY_RESULTS, EVENTQUERY_SUBSCRIPTION);
            form.setAction(subscriptionURL);
            form.setMethod(HTTP.AJAX_PUT);
            form.setActionDescription("subscribe to this query");

            Entry input = new Entry();

            input.setId("SUBSCRIPTION");
            input.setValue(seQuery.toString());
            form.getEntries().add(input);
            resource.setForm(form);

            QueryParams queryParams = seQuery.getQueryParams();
            Poll poll = new Poll();

            poll.setQueryName(queryName);
            poll.setParams(queryParams);

            queryResults = EPCISWebServiceClient.pollSEQuery(poll);

            resource.setQueryResults(queryResults);

            resource.setPoll(poll);

        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        return resource;
    }
}
