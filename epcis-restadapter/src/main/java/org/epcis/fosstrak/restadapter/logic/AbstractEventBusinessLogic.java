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

import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.db.LocationReaderConnection;
import org.epcis.fosstrak.restadapter.model.Resource;
import org.epcis.fosstrak.restadapter.model.events.AggregationEvent;
import org.epcis.fosstrak.restadapter.model.events.EPCISEvent;
import org.epcis.fosstrak.restadapter.model.events.ObjectEvent;
import org.epcis.fosstrak.restadapter.model.events.QuantityEvent;
import org.epcis.fosstrak.restadapter.model.events.TransactionEvent;
import org.epcis.fosstrak.restadapter.util.TimeParser;
import org.epcis.fosstrak.restadapter.ws.epcis.EPCISWebServiceClient;
import org.epcis.fosstrak.restadapter.ws.epcis.query.SEQuery;
import org.epcis.fosstrak.restadapter.ws.generated.AggregationEventType;
import org.epcis.fosstrak.restadapter.ws.generated.BusinessLocationType;
import org.epcis.fosstrak.restadapter.ws.generated.EPCISEventType;
import org.epcis.fosstrak.restadapter.ws.generated.EventListType;
import org.epcis.fosstrak.restadapter.ws.generated.ImplementationExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.NoSuchNameExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.ObjectEventType;
import org.epcis.fosstrak.restadapter.ws.generated.Poll;
import org.epcis.fosstrak.restadapter.ws.generated.QuantityEventType;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParams;
import org.epcis.fosstrak.restadapter.ws.generated.QueryResults;
import org.epcis.fosstrak.restadapter.ws.generated.QueryTooComplexExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.QueryTooLargeExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.ReadPointType;
import org.epcis.fosstrak.restadapter.ws.generated.SecurityExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.TransactionEventType;
import org.epcis.fosstrak.restadapter.ws.generated.ValidationExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.EventTimeGE;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.EventTimeLT;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.EventType;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.Location;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.OrderBy;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.OrderDirection;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.ReadPoint;
import org.epcis.fosstrak.restadapter.ws.generated.QueryResultsBody;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Class containing Helper Methods.
 * Helper Methods Query EPCIS for BizLocations, ReadPoints and ReadTimes.
 * @author Mathias Mueller mathias.mueller(at)unifr.ch
 *
 */
public abstract class AbstractEventBusinessLogic extends AbstractBusinessLogic {

    /* Helper Methods Query EPCIS for BizLocations, ReadPoints and ReadTimes */

    /**
     * Query the EPCIS for a certain Event Type
     *
     *
     * @param eventTypeString
     *
     * @return
     *
     * @throws ParseException
     * @throws QueryParameterExceptionResponse
     * @throws ImplementationExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws QueryTooLargeExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws NoSuchNameExceptionResponse
     */
    public static QueryResults getEventTypeEvents(String eventTypeString) throws ParseException, QueryParameterExceptionResponse, ImplementationExceptionResponse, QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse {
        Poll poll = new Poll();

        poll.setQueryName(Config.SIMPLE_EVENT_QUERY);

        SEQuery   query     = new SEQuery();
        EventType eventType = new EventType(eventTypeString);

        query.setEventType(eventType);

        QueryParams queryParams = query.buildQueryParams();

        poll.setParams(queryParams);

        return EPCISWebServiceClient.pollSEQueryWithoutQueryTooLargeException(poll);
    }

    /**
     * Query the EPCIS with parameters: location, reader and time
     *
     *
     * @param businessLocationString
     * @param readPointString
     * @param eventTimeString
     *
     * @return
     *
     * @throws ParseException
     * @throws QueryParameterExceptionResponse
     * @throws ImplementationExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws QueryTooLargeExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws NoSuchNameExceptionResponse
     */
    public static QueryResults getFilteredEvents(String businessLocationString, String readPointString, String eventTimeString) throws ParseException, QueryParameterExceptionResponse, ImplementationExceptionResponse, QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse {
        Poll poll = new Poll();

        poll.setQueryName(Config.SIMPLE_EVENT_QUERY);

        SEQuery query   = new SEQuery();

        OrderBy orderBy = new OrderBy(OrderBy.BY_RECORD_TIME);

        query.setOrderBy(orderBy);

        OrderDirection orderDirection = new OrderDirection(OrderDirection.ASCENDING);

        query.setOrderDirection(orderDirection);

        if (businessLocationString != null) {
            if (!businessLocationString.equals(Config.NO_VALUE)) {
                Location location = new Location(businessLocationString);

                query.setLocation(location);
            }
        }

        if (readPointString != null) {
            if (!readPointString.equals(Config.NO_VALUE)) {
                ReadPoint readPoint = new ReadPoint(readPointString);

                query.setReadPoint(readPoint);
            }
        }

        if ((businessLocationString != null) && (readPointString != null) && (eventTimeString == null)) {
            OrderBy orderByEventTime = new OrderBy(OrderBy.BY_EVENT_TIME);

            query.setOrderBy(orderByEventTime);
        }

        if (eventTimeString != null) {
            if (!eventTimeString.equals(Config.NO_VALUE)) {
                EventTimeGE eventTimeGE = new EventTimeGE(eventTimeString);

                query.setEventTimeGE(eventTimeGE);

//              Calendar cal = TimeParser.parseAsCalendar(eventTimeString);
//              long timeInMillis = cal.getTimeInMillis();
//              timeInMillis = timeInMillis + 2000;
//              cal.setTimeInMillis(timeInMillis);
//              eventTimeString = TimeParser.format(cal);

                eventTimeString = TimeParser.addOneSecondToTime(eventTimeString);

                EventTimeLT eventTimeLT = new EventTimeLT(eventTimeString);

                query.setEventTimeLT(eventTimeLT);
            }
        }

        QueryParams queryParams = query.buildQueryParams();

        poll.setParams(queryParams);

        QueryResults queryResults = EPCISWebServiceClient.pollSEQueryWithoutQueryTooLargeException(poll);

        if (businessLocationString.equals(Config.NO_VALUE)) {
            QueryResults filteredQueryResults = new QueryResults();

            filteredQueryResults.setQueryName(Config.SIMPLE_EVENT_QUERY);

            QueryResultsBody queryResultsBody = new QueryResultsBody();
            EventListType    eventListType    = new EventListType();

            queryResultsBody.setEventList(eventListType);
            filteredQueryResults.setResultsBody(queryResultsBody);

            List<Object> eventList = queryResults.getResultsBody().getEventList().getObjectEventOrAggregationEventOrQuantityEvent();

            for (Object event : eventList) {
                JAXBElement    element        = (JAXBElement) event;
                EPCISEventType epcisEventType = (EPCISEventType) element.getValue();

                if (epcisEventType instanceof ObjectEventType) {
                    ObjectEventType      myEvent = (ObjectEventType) epcisEventType;
                    BusinessLocationType blt     = myEvent.getBizLocation();

                    if (blt == null) {
                        eventListType.getObjectEventOrAggregationEventOrQuantityEvent().add(event);
                    }
                }

                if (epcisEventType instanceof AggregationEventType) {
                    AggregationEventType myEvent = (AggregationEventType) epcisEventType;
                    BusinessLocationType blt     = myEvent.getBizLocation();

                    if (blt == null) {
                        eventListType.getObjectEventOrAggregationEventOrQuantityEvent().add(event);
                    }
                }

                if (epcisEventType instanceof QuantityEventType) {
                    QuantityEventType    myEvent = (QuantityEventType) epcisEventType;
                    BusinessLocationType blt     = myEvent.getBizLocation();

                    if (blt == null) {
                        eventListType.getObjectEventOrAggregationEventOrQuantityEvent().add(event);
                    }
                }

                if (epcisEventType instanceof TransactionEventType) {
                    TransactionEventType myEvent = (TransactionEventType) epcisEventType;
                    BusinessLocationType blt     = myEvent.getBizLocation();

                    if (blt == null) {
                        eventListType.getObjectEventOrAggregationEventOrQuantityEvent().add(event);
                    }
                }

                queryResults = filteredQueryResults;
            }

        }

        if (readPointString.equals(Config.NO_VALUE)) {
            QueryResults filteredQueryResults = new QueryResults();

            filteredQueryResults.setQueryName(Config.SIMPLE_EVENT_QUERY);

            QueryResultsBody queryResultsBody = new QueryResultsBody();
            EventListType    eventListType    = new EventListType();

            queryResultsBody.setEventList(eventListType);
            filteredQueryResults.setResultsBody(queryResultsBody);


            List<Object> eventList = queryResults.getResultsBody().getEventList().getObjectEventOrAggregationEventOrQuantityEvent();

            for (Object event : eventList) {
                JAXBElement    element        = (JAXBElement) event;
                EPCISEventType epcisEventType = (EPCISEventType) element.getValue();

                if (epcisEventType instanceof ObjectEventType) {
                    ObjectEventType myEvent = (ObjectEventType) epcisEventType;
                    ReadPointType   rpt     = myEvent.getReadPoint();

                    if (rpt == null) {
                        eventListType.getObjectEventOrAggregationEventOrQuantityEvent().add(event);
                    }
                }

                if (epcisEventType instanceof AggregationEventType) {
                    AggregationEventType myEvent = (AggregationEventType) epcisEventType;
                    ReadPointType        rpt     = myEvent.getReadPoint();

                    if (rpt == null) {
                        eventListType.getObjectEventOrAggregationEventOrQuantityEvent().add(event);
                    }
                }

                if (epcisEventType instanceof QuantityEventType) {
                    QuantityEventType myEvent = (QuantityEventType) epcisEventType;
                    ReadPointType     rpt     = myEvent.getReadPoint();

                    if (rpt == null) {
                        eventListType.getObjectEventOrAggregationEventOrQuantityEvent().add(event);
                    }
                }

                if (epcisEventType instanceof TransactionEventType) {
                    TransactionEventType myEvent = (TransactionEventType) epcisEventType;
                    ReadPointType        rpt     = myEvent.getReadPoint();

                    if (rpt == null) {
                        eventListType.getObjectEventOrAggregationEventOrQuantityEvent().add(event);
                    }
                }

                queryResults = filteredQueryResults;
            }

        }

        return queryResults;
    }

    /**
     * Query the EPCIS with parameters: location and reader
     *
     *
     * @param businessLocation
     * @param readPoint
     *
     * @return
     *
     * @throws ParseException
     * @throws QueryParameterExceptionResponse
     * @throws ImplementationExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws QueryTooLargeExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws NoSuchNameExceptionResponse
     */
    public static QueryResults getFilteredEvents(String businessLocation, String readPoint) throws ParseException, QueryParameterExceptionResponse, ImplementationExceptionResponse, QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse {
        return getFilteredEvents(businessLocation, readPoint, null);
    }

    /**
     * Query the EPCIS with parameters: location
     *
     *
     * @param businessLocation
     *
     * @return
     *
     * @throws ParseException
     * @throws QueryParameterExceptionResponse
     * @throws ImplementationExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws QueryTooLargeExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws NoSuchNameExceptionResponse
     */
    public static QueryResults getFilteredEvents(String businessLocation) throws ParseException, QueryParameterExceptionResponse, ImplementationExceptionResponse, QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse {
        return getFilteredEvents(businessLocation, null, null);
    }

    public static final int GO_FOR_BUSINESSLOCATION       = 1;
    public static final int GO_FOR_READPOINTS             = 2;
    public static final int GO_FOR_EVENTTIMES             = 3;
    public static final int GO_FOR_EMPTY_BUSINESSLOCATION = 4;
    public static final int GO_FOR_EMPTY_READPOINTS       = 5;

    /**
     * Fetches all location reader hierarchy connections of a query result
     *
     *
     * @param queryResults
     * @param goFor
     *
     * @return
     */
    public static Collection<LocationReaderConnection> filterResultsForLocationReaderConnections(QueryResults queryResults) {

        Collection<LocationReaderConnection> res    = new LinkedHashSet<LocationReaderConnection>();

        EventListType                        events = queryResults.getResultsBody().getEventList();

        for (Object event : events.getObjectEventOrAggregationEventOrQuantityEvent()) {

            JAXBElement    element        = (JAXBElement) event;
            EPCISEventType epcisEventType = (EPCISEventType) element.getValue();

            if (epcisEventType instanceof ObjectEventType) {
                ObjectEventType      myEvent    = (ObjectEventType) epcisEventType;

                BusinessLocationType locType    = myEvent.getBizLocation();
                ReadPointType        readerType = myEvent.getReadPoint();

                String               loc        = Config.NO_VALUE;
                String               reader     = Config.NO_VALUE;

                if (locType != null) {
                    loc = locType.getId();
                }

                if (readerType != null) {
                    reader = readerType.getId();
                }

                LocationReaderConnection conn = new LocationReaderConnection(loc, reader);

                res.add(conn);
            }

            if (epcisEventType instanceof AggregationEventType) {
                AggregationEventType myEvent    = (AggregationEventType) epcisEventType;

                BusinessLocationType locType    = myEvent.getBizLocation();
                ReadPointType        readerType = myEvent.getReadPoint();

                String               loc        = Config.NO_VALUE;
                String               reader     = Config.NO_VALUE;

                if (locType != null) {
                    loc = locType.getId();
                }

                if (readerType != null) {
                    reader = readerType.getId();
                }

                LocationReaderConnection conn = new LocationReaderConnection(loc, reader);

                res.add(conn);
            }

            if (epcisEventType instanceof QuantityEventType) {
                QuantityEventType    myEvent    = (QuantityEventType) epcisEventType;

                BusinessLocationType locType    = myEvent.getBizLocation();
                ReadPointType        readerType = myEvent.getReadPoint();

                String               loc        = Config.NO_VALUE;
                String               reader     = Config.NO_VALUE;

                if (locType != null) {
                    loc = locType.getId();
                }

                if (readerType != null) {
                    reader = readerType.getId();
                }

                LocationReaderConnection conn = new LocationReaderConnection(loc, reader);

                res.add(conn);
            }

            if (epcisEventType instanceof TransactionEventType) {
                TransactionEventType myEvent    = (TransactionEventType) epcisEventType;

                BusinessLocationType locType    = myEvent.getBizLocation();
                ReadPointType        readerType = myEvent.getReadPoint();

                String               loc        = Config.NO_VALUE;
                String               reader     = Config.NO_VALUE;

                if (locType != null) {
                    loc = locType.getId();
                }

                if (readerType != null) {
                    reader = readerType.getId();
                }

                LocationReaderConnection conn = new LocationReaderConnection(loc, reader);

                res.add(conn);
            }
        }

        return res;
    }

    /**
     * Fetches all times of a query result
     *
     *
     * @param queryResults
     * @param goFor
     *
     * @return
     */
    public static Collection<String> filterResultsForEventTimes(QueryResults queryResults) {

        Collection<String> res    = new LinkedHashSet<String>();

        EventListType      events = queryResults.getResultsBody().getEventList();

        for (Object event : events.getObjectEventOrAggregationEventOrQuantityEvent()) {

            JAXBElement          element        = (JAXBElement) event;
            EPCISEventType       epcisEventType = (EPCISEventType) element.getValue();

            XMLGregorianCalendar cal            = epcisEventType.getEventTime();

            if (cal != null) {
                String myEventTime = TimeParser.format(cal.toGregorianCalendar());

                res.add(myEventTime);
            }
        }

        return res;
    }



    /**
     * gets a specific event in a query result
     *
     *
     * @param queryResults
     * @param index
     *
     * @return
     */
    public static EPCISEvent getEPCISEvent(QueryResults queryResults, int index) {
        EPCISEvent    event     = null;
        EventListType events    = queryResults.getResultsBody().getEventList();
        List<Object>  eventList = events.getObjectEventOrAggregationEventOrQuantityEvent();

        if (!eventList.isEmpty()) {
            if (eventList.size() > 0) {
                if (index > eventList.size()) {
                    index = 1;
                }

                index--;

                Object         object         = eventList.get(index);
                JAXBElement    element        = (JAXBElement) object;
                EPCISEventType epcisEventType = (EPCISEventType) element.getValue();

                if (epcisEventType instanceof AggregationEventType) {
                    AggregationEvent aggregationEvent = new AggregationEvent(epcisEventType);

                    event = aggregationEvent;
                }

                if (epcisEventType instanceof ObjectEventType) {
                    ObjectEvent objectEvent = new ObjectEvent(epcisEventType);

                    event = objectEvent;
                }

                if (epcisEventType instanceof QuantityEventType) {
                    QuantityEvent quantityEvent = new QuantityEvent(epcisEventType);

                    event = quantityEvent;
                }

                if (epcisEventType instanceof TransactionEventType) {
                    TransactionEvent transactionEvent = new TransactionEvent(epcisEventType);

                    event = transactionEvent;
                }

                if (event != null) {
                    event.calculateRESTfulPathID();
                }
            }
        }

        return event;
    }

    /**
     * append an event to a resource
     *
     *
     * @param event
     * @param resource
     */
    public static void appendEvent(EPCISEvent event, Resource resource) {
        if (event instanceof AggregationEvent) {
            List<AggregationEvent> eventList = new LinkedList<AggregationEvent>();

            eventList.add((AggregationEvent) event);
            resource.setAggregationEvent(eventList);
        }

        if (event instanceof ObjectEvent) {
            List<ObjectEvent> eventList = new LinkedList<ObjectEvent>();

            eventList.add((ObjectEvent) event);
            resource.setObjectEvent(eventList);
        }

        if (event instanceof QuantityEvent) {
            List<QuantityEvent> eventList = new LinkedList<QuantityEvent>();

            eventList.add((QuantityEvent) event);
            resource.setQuantityEvent(eventList);
        }

        if (event instanceof TransactionEvent) {
            List<TransactionEvent> eventList = new LinkedList<TransactionEvent>();

            eventList.add((TransactionEvent) event);
            resource.setTransactionEvent(eventList);
        }
    }
}
