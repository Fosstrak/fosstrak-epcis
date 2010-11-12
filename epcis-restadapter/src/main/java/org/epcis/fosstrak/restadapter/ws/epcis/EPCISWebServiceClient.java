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
package org.epcis.fosstrak.restadapter.ws.epcis;

import java.net.MalformedURLException;
import java.util.List;
import javax.xml.ws.BindingProvider;
import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.http.HTTPStatusCodeMapper;
import org.epcis.fosstrak.restadapter.model.events.AggregationEvent;
import org.epcis.fosstrak.restadapter.model.events.EPCISEvent;
import org.epcis.fosstrak.restadapter.model.events.ObjectEvent;
import org.epcis.fosstrak.restadapter.model.events.QuantityEvent;
import org.epcis.fosstrak.restadapter.model.events.TransactionEvent;
import org.epcis.fosstrak.restadapter.util.TimeParser;
import org.epcis.fosstrak.restadapter.ws.epcis.query.SEQuery;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.EventTimeGE;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.EventTimeLT;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.Location;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.OrderBy;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.OrderDirection;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.ReadPoint;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.RecordTimeGE;
import org.epcis.fosstrak.restadapter.ws.epcis.queryparam.RecordTimeLT;
import org.epcis.fosstrak.restadapter.ws.generated.AggregationEventType;
import org.epcis.fosstrak.restadapter.ws.generated.ArrayOfString;
import org.epcis.fosstrak.restadapter.ws.generated.DuplicateSubscriptionExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.EPCISEventType;
import org.epcis.fosstrak.restadapter.ws.generated.EPCISServicePortType;
import org.epcis.fosstrak.restadapter.ws.generated.EPCglobalEPCISService;
import org.epcis.fosstrak.restadapter.ws.generated.EmptyParms;
import org.epcis.fosstrak.restadapter.ws.generated.EventListType;
import org.epcis.fosstrak.restadapter.ws.generated.GetSubscriptionIDs;
import org.epcis.fosstrak.restadapter.ws.generated.ImplementationExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.InvalidURIExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.NoSuchNameExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.NoSuchSubscriptionExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.ObjectEventType;
import org.epcis.fosstrak.restadapter.ws.generated.Poll;
import org.epcis.fosstrak.restadapter.ws.generated.QuantityEventType;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParams;
import org.epcis.fosstrak.restadapter.ws.generated.QueryResults;
import org.epcis.fosstrak.restadapter.ws.generated.QueryResultsBody;
import org.epcis.fosstrak.restadapter.ws.generated.QueryTooComplexExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.QueryTooLargeExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.SecurityExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.Subscribe;
import org.epcis.fosstrak.restadapter.ws.generated.SubscribeNotPermittedExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.SubscriptionControlsExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.TransactionEventType;
import org.epcis.fosstrak.restadapter.ws.generated.Unsubscribe;
import org.epcis.fosstrak.restadapter.ws.generated.ValidationExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.VoidHolder;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;

/**
 * Class to use the EPCIS Web Service
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 *
 */
public class EPCISWebServiceClient {

    private static EPCISServicePortType getEPCISServicePortType() {
        URL wsdlLocation = null;

        try {
            wsdlLocation = new URL(Config.GET_RESOURCES_URL() + "wsdl/EPCglobal-epcis-query-1_0.wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        QName                 serviceName     = new QName("urn:epcglobal:epcis:wsdl:1", "EPCglobalEPCISService");
        EPCglobalEPCISService service         = new EPCglobalEPCISService(wsdlLocation, serviceName);
        EPCISServicePortType  port            = service.getEPCglobalEPCISServicePort();
        BindingProvider       bindingProvider = (BindingProvider) port;

        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, Config.GET_EPCIS_REPOSITORY_QUERY_URL());

        return port;
    }

    /**
     * Get the Query Names from the EPCIS
     *
     *
     * @return
     *
     * @throws SecurityExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws ImplementationExceptionResponse
     */
    public static List<String> getQueryNames() throws SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse {
        EPCISServicePortType port  = getEPCISServicePortType();
        EmptyParms           parms = new EmptyParms();
        ArrayOfString        aos   = port.getQueryNames(parms);
        List<String>         res   = aos.getString();

        return res;
    }

    /**
     * Get the Standard Version from the EPCIS
     *
     *
     * @return
     *
     * @throws SecurityExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws ImplementationExceptionResponse
     */
    public static String getStandardVersion() throws SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse {
        EPCISServicePortType port  = getEPCISServicePortType();
        EmptyParms           parms = new EmptyParms();

        return port.getStandardVersion(parms);
    }

    /**
     * Get the Vendor Version from the EPCIS
     *
     *
     * @return
     *
     * @throws SecurityExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws ImplementationExceptionResponse
     */
    public static String getVendorVersion() throws SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse {
        EPCISServicePortType port  = getEPCISServicePortType();
        EmptyParms           parms = new EmptyParms();

        return port.getVendorVersion(parms);
    }

    /**
     * Do a poll (SimpleEventQuery) to the EPCIS
     *
     *
     * @param poll
     *
     * @return
     *
     * @throws ImplementationExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws QueryTooLargeExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws NoSuchNameExceptionResponse
     * @throws QueryParameterExceptionResponse
     */
    public static QueryResults pollSEQuery(Poll poll) throws ImplementationExceptionResponse, QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse, QueryParameterExceptionResponse {
        EPCISServicePortType port = getEPCISServicePortType();

        return port.poll(poll);
    }

    /**
     * Do a poll (SimpleEventQuery) to the EPCIS, but no query too large exception shall be raised (Thus it will return a query result, nevertheless how huge it is. Internally splits the query to multiple queries.)
     *
     *
     * @param poll
     *
     * @return
     *
     * @throws ImplementationExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws NoSuchNameExceptionResponse
     * @throws QueryParameterExceptionResponse
     */
    public static QueryResults pollSEQueryWithoutQueryTooLargeException(Poll poll) {
        long lower            = 0;
        long upper            = GregorianCalendar.getInstance().getTimeInMillis();
        long oneMonthInMillis = 2592000000l;

        upper += oneMonthInMillis;

        QueryResults queryResults = new QueryResults();

        queryResults.setQueryName(Config.SIMPLE_EVENT_QUERY);

        QueryResultsBody queryResultsBody = new QueryResultsBody();
        EventListType    eventListType    = new EventListType();

        queryResultsBody.setEventList(eventListType);
        queryResults.setResultsBody(queryResultsBody);


        return pollSEQueryToQueryResultsWithBounds(queryResults, poll, lower, upper);
    }

    private static Poll getNewPollWithBounds(Poll poll, long lower, long upper) {
        Poll        res         = new Poll();
        QueryParams queryParams = new QueryParams();

        res.setParams(queryParams);
        res.setQueryName(Config.SIMPLE_EVENT_QUERY);
        res.getParams().getParam().addAll(poll.getParams().getParam());

        RecordTimeGE lowerBound;
        RecordTimeLT upperBound;

        try {
            lowerBound = new RecordTimeGE(parseDatefromLongToString(lower));
            upperBound = new RecordTimeLT(parseDatefromLongToString(upper));
        } catch (Exception ex) {
            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        res.getParams().getParam().add(lowerBound.getQueryParam());
        res.getParams().getParam().add(upperBound.getQueryParam());

        return res;
    }

    private static String parseDatefromLongToString(long date) {
        String res;

        try {
            long              millis  = date;
            GregorianCalendar gregCal = new GregorianCalendar();

            gregCal.setTimeInMillis(millis);

            DatatypeFactory fact = DatatypeFactory.newInstance();

            res = fact.newXMLGregorianCalendar(gregCal).toString();
        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        return res;
    }



    private static QueryResults pollSEQueryToQueryResultsWithBounds(QueryResults queryResults, Poll poll, long lower, long upper) {
        Poll         myPoll               = getNewPollWithBounds(poll, lower, upper);
        QueryResults queryResultsToAppend = null;

        try {
            queryResultsToAppend = pollSEQuery(myPoll);


        } catch (QueryTooLargeExceptionResponse ex) {


            long newFrontier = upper - ((upper - lower) / 2);


            pollSEQueryToQueryResultsWithBounds(queryResults, poll, lower, newFrontier);


            pollSEQueryToQueryResultsWithBounds(queryResults, poll, newFrontier, upper);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            List<Object> gotAlready = queryResults.getResultsBody().getEventList().getObjectEventOrAggregationEventOrQuantityEvent();
            List<Object> newOnes = new LinkedList<Object>();

            if (queryResultsToAppend != null) {
                newOnes = queryResultsToAppend.getResultsBody().getEventList().getObjectEventOrAggregationEventOrQuantityEvent();
            }

            gotAlready.addAll(newOnes);

            return queryResults;
        }
    }

    /**
     * Get all subscription IDs of the EPCIS
     *
     *
     * @param getSubscriptionIDs
     *
     * @return
     *
     * @throws ImplementationExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ValidationExceptionResponse
     * @throws NoSuchNameExceptionResponse
     */
    public static ArrayOfString getSubscriptionIDs(GetSubscriptionIDs getSubscriptionIDs) throws ImplementationExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse {
        EPCISServicePortType port = getEPCISServicePortType();

        return port.getSubscriptionIDs(getSubscriptionIDs);
    }

    /**
     * Subscribe to a query in the EPCIS
     *
     *
     * @param subscribe
     *
     * @return
     *
     * @throws DuplicateSubscriptionExceptionResponse
     * @throws ImplementationExceptionResponse
     * @throws ImplementationExceptionResponse
     * @throws InvalidURIExceptionResponse
     * @throws NoSuchNameExceptionResponse
     * @throws QueryParameterExceptionResponse
     * @throws QueryTooComplexExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws SubscribeNotPermittedExceptionResponse
     * @throws SubscriptionControlsExceptionResponse
     * @throws ValidationExceptionResponse
     */
    public static VoidHolder subscribe(Subscribe subscribe) throws DuplicateSubscriptionExceptionResponse, ImplementationExceptionResponse, ImplementationExceptionResponse, InvalidURIExceptionResponse, NoSuchNameExceptionResponse, QueryParameterExceptionResponse, QueryTooComplexExceptionResponse, SecurityExceptionResponse, SubscribeNotPermittedExceptionResponse, SubscriptionControlsExceptionResponse, ValidationExceptionResponse {
        EPCISServicePortType port = getEPCISServicePortType();

        return port.subscribe(subscribe);
    }

    /**
     * Unsubscribe to a query in the EPCIS
     *
     *
     * @param unsubscribe
     *
     * @return
     *
     * @throws ImplementationExceptionResponse
     * @throws NoSuchSubscriptionExceptionResponse
     * @throws SecurityExceptionResponse
     * @throws ValidationExceptionResponse
     */
    public static VoidHolder unsubscribe(Unsubscribe unsubscribe) throws ImplementationExceptionResponse, NoSuchSubscriptionExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse {
        EPCISServicePortType port = getEPCISServicePortType();

        return port.unsubscribe(unsubscribe);
    }

    /**
     * Get the event path ID of an event
     *
     *
     * @param event
     *
     * @return
     */
    public static int getEventPathID(EPCISEvent event) {

        String businessLocation = event.getBizLocationEntry().getValue();
        String readPoint        = event.getReadPointEntry().getValue();
        String eventTime        = event.getEventTimeEntry().getValue();

        int    res              = 1;

        try {
            Poll poll = new Poll();

            poll.setQueryName(Config.SIMPLE_EVENT_QUERY);

            SEQuery seQuery = new SEQuery();

            if (!businessLocation.equals(Config.NO_VALUE)) {
                seQuery.setLocation(new Location(businessLocation));
            }

            if (!businessLocation.equals(Config.NO_VALUE)) {
                seQuery.setReadPoint(new ReadPoint(readPoint));
            }

            seQuery.setEventTimeGE(new EventTimeGE(eventTime));
            seQuery.setEventTimeLT(new EventTimeLT(TimeParser.addOneSecondToTime(eventTime)));
            seQuery.setOrderBy(new OrderBy(OrderBy.BY_RECORD_TIME));

            // must be ascending to ensure id doesn't changes in the future
            seQuery.setOrderDirection(new OrderDirection(OrderDirection.ASCENDING));

            QueryParams queryParams = seQuery.buildQueryParams();

            poll.setParams(queryParams);

            QueryResults queryResults = pollSEQuery(poll);
            List<Object> results      = queryResults.getResultsBody().getEventList().getObjectEventOrAggregationEventOrQuantityEvent();

            int counter = 0;

            for (Object obj : results) {
                JAXBElement    element        = (JAXBElement) obj;
                EPCISEventType epcisEventType = (EPCISEventType) element.getValue();

                if (epcisEventType instanceof ObjectEventType) {
                    counter++;

                    ObjectEvent evt = new ObjectEvent(epcisEventType);

                    if (evt.isLikeEvent(event)) {
                        return counter;
                    }
                }

                if (epcisEventType instanceof AggregationEventType) {
                    counter++;

                    AggregationEvent evt = new AggregationEvent(epcisEventType);

                    if (evt.isLikeEvent(event)) {
                        return counter;
                    }
                }

                if (epcisEventType instanceof QuantityEventType) {
                    counter++;

                    QuantityEvent evt = new QuantityEvent(epcisEventType);

                    if (evt.isLikeEvent(event)) {
                        return counter;
                    }
                }

                if (epcisEventType instanceof TransactionEventType) {
                    counter++;

                    TransactionEvent evt = new TransactionEvent(epcisEventType);

                    if (evt.isLikeEvent(event)) {
                        return counter;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        return res;
    }
}
