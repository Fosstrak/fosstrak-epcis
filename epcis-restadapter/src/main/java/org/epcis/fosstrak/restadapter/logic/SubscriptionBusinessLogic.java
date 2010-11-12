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
import java.net.URISyntaxException;
import javax.ws.rs.core.UriInfo;
import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.config.URIConstants;
import org.epcis.fosstrak.restadapter.db.InternalDatabase;
import org.epcis.fosstrak.restadapter.feed.AtomPub;
import org.epcis.fosstrak.restadapter.feed.FeedResource;
import com.sun.jersey.atom.abdera.ContentHelper;
import org.epcis.fosstrak.restadapter.http.HTTP;
import org.epcis.fosstrak.restadapter.http.HTTPStatusCodeMapper;
import org.epcis.fosstrak.restadapter.model.Form;
import org.epcis.fosstrak.restadapter.model.Entry;
import org.epcis.fosstrak.restadapter.model.Content;
import org.epcis.fosstrak.restadapter.model.Resource;
import org.epcis.fosstrak.restadapter.representation.HTML;
import org.epcis.fosstrak.restadapter.rest.ISubscriptionResource;
import org.epcis.fosstrak.restadapter.rest.RESTfulEPCIS;
import org.epcis.fosstrak.restadapter.util.ActualDateTime;
import org.epcis.fosstrak.restadapter.ws.epcis.query.SEQuery;
import org.epcis.fosstrak.restadapter.ws.generated.ArrayOfString;
import org.epcis.fosstrak.restadapter.ws.generated.DuplicateSubscriptionExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.GetSubscriptionIDs;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParams;
import org.epcis.fosstrak.restadapter.ws.generated.QuerySchedule;
import org.epcis.fosstrak.restadapter.ws.generated.Subscribe;
import org.epcis.fosstrak.restadapter.ws.generated.SubscriptionControls;
import org.epcis.fosstrak.restadapter.ws.generated.Unsubscribe;
import org.epcis.fosstrak.restadapter.ws.generated.VoidHolder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Iterator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.abdera.Abdera;
import org.dom4j.Element;
import static org.epcis.fosstrak.restadapter.config.URIConstants.*;

/**
 *
 * Business logic for the subscription
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 */
public class SubscriptionBusinessLogic extends AbstractQueryParamBusinessLogic implements ISubscriptionResource {

    /**
     * POST method for adding a subscription
     * Returns a representation of the existing subscribtions list resource according to the requested mime type
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
    public Resource addSubscription(UriInfo context, String eventTime, String recordTime, String eventType, String action, String bizStep, String disposition, String readPoint, String readPointWD, String bizLocation, String bizLocationWD, String bizTransaction, String epc, String parentID, String anyEPC, String epcClass, String quantity, String fieldname, String orderBy, String orderDirection, String eventCountLimit, String maxEventCount) {
        String   name        = "Event Query Subscription";
        String   description = "The Interface for adding a Subscription for the according Simple Event Query.";
        String   path        = EVENTQUERY_SUBSCRIPTION;
        Resource resource    = setUpResource(context, name, description, path);

        String   queryName   = Config.SIMPLE_EVENT_QUERY;

        initAbstractQueryParamBusinessLogic(eventTime, recordTime, eventType, action, bizStep, disposition, readPoint, readPointWD, bizLocation, bizLocationWD, bizTransaction, epc, parentID, anyEPC, epcClass, quantity, fieldname, orderBy, orderDirection, eventCountLimit, maxEventCount);

        SEQuery seQuery = null;

        String id = "";
        String alias = "";

        try {
            seQuery = getSEQuery();

            QueryParams queryParams = seQuery.getQueryParams();

            id = seQuery.toString();

            // id = URI.escapeURL(id);
            alias = generateAlias(id);

            URI     uri                     = new URI(context);
            String  subscriptionURLAbsolute = uri.getRestURI(SUBSCRIPTIONS_ID, id);

            Content links                   = new Content();

            resource.setFields(links);
            addLink(context, links, name + " for Query: " + id, SUBSCRIPTIONS_ID, id);

            Subscribe subscribe = new Subscribe();

            subscribe.setSubscriptionID(id);
            subscribe.setQueryName(queryName);
            subscribe.setParams(queryParams);
            subscribe.setDest(subscriptionURLAbsolute);

            SubscriptionControls controls = new SubscriptionControls();

            controls.setReportIfEmpty(false);

            // controls.setInitialRecordTime(null);
            // controls.setInitialRecordTime("2009-06-30T13:504.168+02:00");
            controls.setInitialRecordTime(ActualDateTime.GET_NOW_XMLGC());

            QuerySchedule schedule = new QuerySchedule();

            schedule.setSecond("0,5,10,15,20,25,30,35,40,45,50,55");
            controls.setSchedule(schedule);
            subscribe.setControls(controls);

            VoidHolder voidHolder = EPCISWebServiceClient.subscribe(subscribe);

            InternalDatabase.getInstance().insertSubscription(id, alias);

            // InternalDatabase.getInstance().printDB();

            Abdera                       abdera       = AtomPub.getAbdera();
            org.apache.abdera.model.Feed feed         = abdera.getFactory().newFeed();
            ClientConfig                 clientConfig = new DefaultClientConfig();
            Client                       client       = Client.create(clientConfig);

            feed.setTitle(Config.FEED_TITLE + ": " + id);
            feed.setId(alias);
            feed.setSubtitle("Feed with ID " + alias);
            feed.addAuthor(Config.FEED_AUTHORS);
            feed.addCategory(Config.FEED_CATEGORY);
            feed.addContributor(Config.FEED_CONTRIBUTOR);

            String myQueryURL = URI.addSubPath(Config.GET_RESTFUL_EPCIS_URL(), URIConstants.EVENTQUERY_RESULTS + "?" + id);

            feed.addLink(myQueryURL);

            String logoIRI = URI.addSubPath(Config.GET_RESOURCES_URL(), "images/restfulepcislogo_small.png");

            feed.setLogo(logoIRI);

            URI          myURI     = new URI(context);
            String       myFeedURI = myURI.getFeedURI(alias);

            java.net.URI myUri;
            java.net.URI myUriXML;

            try {
                myUri    = new java.net.URI(myFeedURI);
                myUriXML = new java.net.URI(URI.addSubPath(myFeedURI, FeedResource.MACHINE));
            } catch (URISyntaxException ex) {
                ex.printStackTrace();

                throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
            }

            WebResource    webResource    = client.resource(myUri);
            ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_ATOM_XML).put(ClientResponse.class, feed);
            WebResource    webResourceXML    = client.resource(myUriXML);
            ClientResponse clientResponseXML = webResourceXML.type(MediaType.APPLICATION_ATOM_XML).put(ClientResponse.class, feed);

        } catch (DuplicateSubscriptionExceptionResponse duplicateSubscriptionExceptionResponse) {
            boolean isSubscriptionInEPCIS = InternalDatabase.getInstance().containsSubscriptionID(id);
            if (isSubscriptionInEPCIS) {
                InternalDatabase.getInstance().insertSubscription(id, alias);
            }
            // Everything is ok, subscription already existed. Just return the URL.
            return resource;
        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        return resource;
    }

    /**
     * Returns a representation of a subscription resource (the link to a feed) according to the requested mime type
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getSubscriptions(UriInfo context) {
        String   name        = "Subscriptions";
        String   description = "The List of all Subscription ID's for Simple Event Queries.";
        String   path        = SUBSCRIPTIONS;
        Resource resource    = setUpResource(context, name, description, path);

        Content  links       = new Content();

        resource.setFields(links);

        try {
            GetSubscriptionIDs getSubscriptionIDs = new GetSubscriptionIDs();

            getSubscriptionIDs.setQueryName(Config.SIMPLE_EVENT_QUERY);

            ArrayOfString subscriptions = EPCISWebServiceClient.getSubscriptionIDs(getSubscriptionIDs);

            for (String subscription : subscriptions.getString()) {
                addLink(context, links, subscription, SUBSCRIPTIONS_ID, subscription);
            }

        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        return resource;
    }

    private static InternalDatabase SUBSCRIPTIONS_DB = InternalDatabase.getInstance();

    private String generateID(String id) {
        return id;
    }

    private String generateAlias(String id) {
        return SUBSCRIPTIONS_DB.getNextFreeAlias() + "";

    }

    /**
     * Resource for subscribing to a FEED
     *
     *
     * @param context
     * @param id
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getSubscription(UriInfo context, String id) {
        id = URI.unescapeURL(id);

        String   name        = "Subscription";
        String   path        = SUBSCRIPTIONS_ID;
        String   description = "The Link to the Feed for the Subscription: " + id + ".";
        Resource resource    = setUpResource(context, name, description, path, id);

        Content  links       = new Content();

        resource.setFields(links);

        if (SUBSCRIPTIONS_DB.containsSubscriptionID(id)) {
            String alias               = SUBSCRIPTIONS_DB.getSubscriptionAlias(id);

            URI    uri                 = new URI(context);
            String url                 = uri.getFeedURI(alias);
            Entry  link                = new Entry();
            Entry  linkMachineReadable = new Entry();

            links.getContent().add(link);
            links.getContent().add(linkMachineReadable);
            link.setValue("FEED");
            link.setValueRef(url);
            linkMachineReadable.setValue("FEED (machine readable)");
            linkMachineReadable.setValueRef(URI.addSubPath(url, FeedResource.MACHINE));

            Form form = new Form();

            // String action = uri.getRestURI(path);
            String action = resource.getUri();

            form.setAction(action);
            form.setMethod(HTTP.AJAX_DELETE);
            form.setDescription("Unsubscribe a Query.");
            form.setActionDescription("unsubscribe");

            Entry input = new Entry();

            input.setId("UNSUBSCRIPTION");
            input.setName("Unsubscription");
            input.setValue(id);

            form.getEntries().add(input);

            resource.setForm(form);

        } else {
            resource.setDescription("There is no Feed Subscription under this URI.");

            Content content = new Content();
            Entry   entry   = new Entry();

            entry.setValue("Link to create the corresponding Feed.");

            String myId = URI.escapeURL(id);

            myId = myId.replace("*amp*", "&amp;");

            String accordingQueryURI = URI.addSubPath(Config.GET_RESTFUL_EPCIS_URL(), URIConstants.EVENTQUERY_RESULTS) + "?" + myId;

            entry.setValueRef(accordingQueryURI);
            content.getContent().add(entry);
            resource.setFields(content);
        }


        return resource;
    }

    /**
     * Business method for adding an entry to the corresponding feed
     * Returns a representation of the according subscription resource (the link to a feed) according to the requested mime type
     *
     * @param context
     * @param id
     * @param entry
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource addEntryToSubscription(UriInfo context, String id, String entry) {
        id = URI.unescapeURL(id);

        String alias = "-1";

        if (SUBSCRIPTIONS_DB.containsSubscriptionID(id)) {
            alias = SUBSCRIPTIONS_DB.getSubscriptionAlias(id);
        }

        String myRESTfulURL           = Config.GET_RESTFUL_EPCIS_URL();
        String humanReadableContent   = entry;
        String machineReadableContent = entry;

        URI    myURI                  = new URI(context);
        String myFeedURI              = myURI.getFeedURI(alias);

        try {

            // load the event as xml
            org.dom4j.Document document = org.dom4j.DocumentHelper.parseText(entry);

            // get relevant data
            String   time         = "";
            String   reader       = "blank";
            String   location     = "blank";
            Iterator timeList     = document.selectNodes("//eventTime").iterator();
            Iterator readerList   = document.selectNodes("//readPoint/id").iterator();
            Iterator locationList = document.selectNodes("//bizLocation/id").iterator();

            if (timeList.hasNext()) {
                Element element = (Element) timeList.next();

                time = element.getText();
            }

            if (readerList.hasNext()) {
                Element element = (Element) readerList.next();

                reader = element.getText();
            }

            if (locationList.hasNext()) {
                Element element = (Element) locationList.next();

                location = element.getText();
            }

            time     = URI.escapeURL(time);
            reader   = URI.escapeURL(reader);
            location = URI.escapeURL(location);

            // get EPCIS REST Adapter path id
            String restHomeURI = Config.GET_RESTFUL_EPCIS_URL_NO_VERSION();

            //stripping off the API version number
            UriBuilder builder = UriBuilder.fromResource(RESTfulEPCIS.class);
            String restRelativeURI = UriBuilder.fromResource(RESTfulEPCIS.class).path(URIConstants.FINDER_EVENT).build(location, reader, time).getRawPath();
            myRESTfulURL = URI.addSubPath(restHomeURI, restRelativeURI);

            // query the epcis to the event in html pure form
            java.net.URI uri = new java.net.URI(myRESTfulURL);

            // add the human readable event to the feed
            ClientConfig   clientConfig   = new DefaultClientConfig();
            Client         client         = Client.create(clientConfig);
            WebResource    webResource    = client.resource(uri);
            ClientResponse clientResponse = webResource.accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
            InputStream    response       = clientResponse.getEntityInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response));
            StringBuilder  stringBuilder  = new StringBuilder();
            String         line           = null;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            bufferedReader.close();
            humanReadableContent = stringBuilder.toString();
            humanReadableContent = humanReadableContent.replace("'", "\"");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String name        = "Subscription";
        String path        = SUBSCRIPTIONS_ID;
        String description = "The RESTful Interface to add an Entry to a Subscription. This URI is subscribed to the EPCIS and will be used to send new Entries for the according subscribed Simple Event Query.";
        Resource                      resource     = setUpResource(context, name, description, path, id);

        Abdera                        abdera       = AtomPub.getAbdera();
        org.apache.abdera.model.Feed  feed         = abdera.getFactory().newFeed();
        org.apache.abdera.model.Entry feedEntry    = feed.addEntry();
        org.apache.abdera.model.Entry feedEntryXML = feed.addEntry();
        ClientConfig                  clientConfig = new DefaultClientConfig();
        Client                        client       = Client.create(clientConfig);
        ContentHelper                 helper       = new ContentHelper(client.getProviders());

        String                        updated      = new Date().toString();

        feedEntry.setTitle("Event on " + updated);
        feedEntry.setSummaryAsHtml(humanReadableContent);
        feedEntry.setUpdated(new Date());
        feedEntry.setPublished(new Date());
        feedEntry.addLink(myRESTfulURL);
        feedEntry.setContentAsHtml("<pre>" + HTML.escapeXML(machineReadableContent) + "</pre>");

        feedEntryXML.setTitle("Event on " + updated);
        feedEntryXML.setUpdated(new Date());
        feedEntryXML.setPublished(new Date());
        feedEntryXML.addLink(myRESTfulURL);
        feedEntryXML.setContentAsHtml("<pre>" + HTML.escapeXML(machineReadableContent) + "</pre>");

        java.net.URI uri;
        java.net.URI uriXML;

        try {
            uri    = new java.net.URI(myFeedURI);
            uriXML = new java.net.URI(URI.addSubPath(myFeedURI, FeedResource.MACHINE));
        } catch (URISyntaxException ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        // add the human readable event to the feed
        WebResource    webResource    = client.resource(uri);
        ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_ATOM_XML).post(ClientResponse.class, feedEntry);

        // add the machine readable event to the feed
        WebResource    webResourceXML    = client.resource(uriXML);
        ClientResponse clientResponseXML = webResourceXML.type(MediaType.APPLICATION_ATOM_XML).post(ClientResponse.class, feedEntryXML);

        resource.setDescription("Finished with Status Code: " + clientResponse.getStatus() + ".");

        return resource;
    }

    /**
     * DELETE method for deleting a feed
     * Returns a representation of a link back to the subscriptions according to the requested mime type
     *
     * @param context
     * @param id
     * @param data
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource unsubscribeSubscription(UriInfo context, String id, String data) {
        id = URI.unescapeURL(id);

        String name        = "Subscription";
        String path        = SUBSCRIPTIONS_ID;
        String description = "Unsubscription failed.";
        URI    uri         = new URI(context);

        path = uri.getRestURI(path, id);

        Resource    resource    = setUpResource(context, name, description, path);

        Unsubscribe unsubscribe = new Unsubscribe();

        unsubscribe.setSubscriptionID(id);

        try {
            VoidHolder voidHolder = EPCISWebServiceClient.unsubscribe(unsubscribe);

            description = "Unsubscription successful.";
            resource.setDescription(description);
            SUBSCRIPTIONS_DB.deleteSubscription(id);

        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        return resource;
    }
}
