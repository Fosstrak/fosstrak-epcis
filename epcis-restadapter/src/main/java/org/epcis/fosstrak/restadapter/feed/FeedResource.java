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
package org.epcis.fosstrak.restadapter.feed;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.db.EntryBean;
import org.epcis.fosstrak.restadapter.db.FeedBean;
import org.epcis.fosstrak.restadapter.db.InternalDatabase;
import org.epcis.fosstrak.restadapter.util.URI;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import static javax.ws.rs.core.MediaType.*;

/**
 * 
 * Class to handle the AtomPub Feeds
 * @author Mathias Mueller mathias.mueller(at)unifr.ch
 *
 */
@Produces(APPLICATION_ATOM_XML)
@Consumes(APPLICATION_ATOM_XML)
@Path(FeedResource.FEED_URI)
public class FeedResource {

    public static final String      FEED_URI         = "/feed";
    public static final String      ID               = "id";
    public static final String      FEED_SIZE        = "size";
    public static final String      HUMAN            = "humanreadable";
    public static final String      MACHINE          = "machinereadable";
    public static final String      FEED_ID_URI      = "/{" + ID + "}";
    public static final String      FEED_HUMAN_URI   = FEED_ID_URI + "/" + HUMAN;
    public static final String      FEED_MACHINE_URI = FEED_ID_URI + "/" + MACHINE;
    private static final int        HUMAN_READABLE   = 1;
    private static final int        MACHINE_READABLE = 0;
    private static InternalDatabase FEED_DB          = InternalDatabase.getInstance();

    /**
     * Gets the List of Feeds
     *
     *
     * @param urlId
     *
     * @return
     */
    @GET
    @Produces(TEXT_HTML)
    public String getFeedRest() {
        StringBuilder res = new StringBuilder();

        res.append("<h1>The List of Feeds:</h1>");
        res.append("<ul>");

        String baseFeedURL = Config.GET_FEED_URL();

        for (String feedID : FEED_DB.getFeedIDs()) {
            String myFeedURL = URI.addSubPath(baseFeedURL, feedID);

            res.append("<li><a href='" + myFeedURL + "'>Feed " + feedID + "</a></li>");
        }

        res.append("</ul>");

        // Show a List of Subscriptions
        return res.toString();
    }

    /**
     * Gets a machine readable Feed
     *
     *
     * @param urlId
     * @param urlSize
     *
     * @return
     */
    @GET
    @Path(FEED_MACHINE_URI)
    public Feed getFeedMachine(@PathParam(ID) String urlId, @DefaultValue(Config.DEFAULT_FEED_SIZE + "")
    @QueryParam(FEED_SIZE) String urlSize) {
        return getFeedInRightFormat(urlId, urlSize, MACHINE_READABLE);
    }

    /**
     * Gets a human readable Feed
     *
     *
     * @param urlId
     * @param urlSize
     *
     * @return
     */
    @GET
    @Path(FEED_HUMAN_URI)
    public Feed getFeedHuman(@PathParam(ID) String urlId, @DefaultValue(Config.DEFAULT_FEED_SIZE + "")
    @QueryParam(FEED_SIZE) String urlSize) {
        return getFeedInRightFormat(urlId, urlSize, HUMAN_READABLE);
    }

    /**
     * Gets a Feed
     *
     *
     * @param urlId
     * @param urlSize
     *
     * @return
     */
    @GET
    @Path(FEED_ID_URI)
    public Feed getFeed(@PathParam(ID) String urlId, @DefaultValue(Config.DEFAULT_FEED_SIZE + "")
    @QueryParam(FEED_SIZE) String urlSize) {
        return getFeedInRightFormat(urlId, urlSize, HUMAN_READABLE);
    }

    private Feed getFeedInRightFormat(String urlId, String urlSize, int format) {
        String id   = urlId;
        int    size = Config.DEFAULT_FEED_SIZE;

        try {
            size = Integer.parseInt(urlSize);
        } catch (Exception ex) {
            ex.printStackTrace();

            // continue with default size
        }

        Feed   feed   = null;
        Abdera abdera = AtomPub.getAbdera();

        feed = abdera.getFactory().newFeed();
        feed.setTitle("Unspecified Feed");
        feed.setSubtitle("");
        feed.addAuthor("");

        boolean isFeedExisting = false;

        for (String feedID : FEED_DB.getFeedIDs()) {
            if (feedID.equals(id)) {
                isFeedExisting = true;
            }
        }

        if (isFeedExisting) {
            FeedBean feedBean = FEED_DB.getFeed(id);

            feed.setId(id);
            feed.setTitle(feedBean.getTitle());
            feed.setSubtitle(feedBean.getSubtitle());
            feed.addAuthor(feedBean.getAuthor());
            feed.addCategory(feedBean.getCategory());
            feed.addContributor(feedBean.getContributor());
            feed.addLink(feedBean.getLink());
            feed.setLogo(feedBean.getLogo());

            Date myUpdatedDate = new Date();

            feed.setUpdated(myUpdatedDate);

            int entrySize = java.lang.Math.min(size, feedBean.getEntries().size());

            for (int i = 0; i < entrySize; i++) {

                // for (EntryBean e : feedBean.getEntries()) {
                EntryBean entryBean = feedBean.getEntries().get(i);
                Entry     entry     = feed.addEntry();

                entry.setId(entryBean.getId());
                entry.setTitle(entryBean.getTitle());

                if (format == HUMAN_READABLE) {
                    entry.setSummaryAsHtml(entryBean.getSummary());
                }

                if (format == MACHINE_READABLE) {
                    entry.setSummaryAsHtml(entryBean.getSummaryXML());
                }

                // entry.setUpdated(e.getUpdated());
                // entry.setPublished(e.getPublished());
                entry.addLink(entryBean.getLink());
            }

            if ((feed.getEntries().size() == 0) && (size != 0)) {
                Entry entry = feed.addEntry();

                entry.setId("nosubsription");
                entry.setTitle("Empty Feed");
                entry.setSummaryAsHtml("<h2>No events captured for this feed yet.</h2>");
                entry.setUpdated(new Date());
                entry.setPublished(new Date());
                entry.addLink(Config.GET_RESTFUL_EPCIS_URL());
            }
        } else {
            Entry entry = feed.addEntry();

            entry.setId("unspecified");
            entry.setTitle("Unspecified Feed");
            entry.setSummaryAsHtml("<h2>There is no subscription for this feed yet.</h2>");
            entry.setUpdated(new Date());
            entry.setPublished(new Date());
            entry.addLink(Config.GET_RESTFUL_EPCIS_URL());
        }

        return feed;
    }

    /**
     * Adds a Feed
     *
     *
     * @param urlId
     * @param myNewFeed as XML Feed
     *
     * @return
     */
    @PUT
    @Path(FEED_ID_URI)
    public Response addFeed(@PathParam(ID) String urlId, Feed myNewFeed) {
        String id       = myNewFeed.getId() + "";
        String title    = myNewFeed.getTitle();
        String subtitle = myNewFeed.getSubtitle();
        String author   = "";

        try {
            author = myNewFeed.getAuthors().get(0).getName();
        } catch (Exception ex) {
            ex.printStackTrace();

            // continue
        }

        String category = "";

        try {
            category = myNewFeed.getCategories().get(0).getTerm();
        } catch (Exception ex) {
            ex.printStackTrace();

            // continue
        }

        String contributor = "";

        try {
            contributor = myNewFeed.getContributors().get(0).getName();
        } catch (Exception ex) {
            ex.printStackTrace();

            // continue
        }

        String link = "";

        try {
            link = myNewFeed.getLinks().get(0).getHref().toASCIIString();
        } catch (Exception ex) {
            ex.printStackTrace();

            // continue
        }

        String   logo     = myNewFeed.getLogo().toASCIIString();

        FeedBean feedBean = new FeedBean(id, title, subtitle, author, category, contributor, link, logo);

        FEED_DB.addFeed(feedBean);

        return Response.ok().build();
    }
    /**
     * Add an XML Entry to a Feed
     *
     *
     * @param entry as Feed Entry
     * @param id
     *
     * @return
     */
    @POST
    @Path(FEED_MACHINE_URI)
    public Response addEntryXML(Entry entry, @PathParam("id") String id) {

//      System.out.println("adding xml entry with id = " + id);
//      System.out.println(entry);
        return Response.ok().build();
    }

    /**
     * Add an HTML Entry to a Feed
     *
     *
     * @param entry as Feed Entry
     * @param id
     *
     * @return
     */
    @POST
    @Path(FEED_ID_URI)
    public Response addEntry(Entry entry, @PathParam("id") String id) {

        Feed    feed           = null;

        boolean isFeedExisting = false;

        for (String feedId : FEED_DB.getFeedIDs()) {
            if (feedId.equals(id)) {
                isFeedExisting = true;
            }
        }

        if (!isFeedExisting) {
            Abdera abdera = AtomPub.getAbdera();

            feed = abdera.getFactory().newFeed();
            feed.setTitle("Unspecified Feed");
            feed.setSubtitle("Feed with ID " + id);
        }

        int    entryID = FEED_DB.getNextEntryID(id);
        String link    = "";

        try {
            link = entry.getLinks().get(0).getHref().getPath();
        } catch (Exception ex) {

            // ok, just continue
        }

        EntryBean entryBean = new EntryBean(entryID + "", entry.getTitle(), entry.getSummary(), entry.getContent(), entry.getUpdated().toString(), entry.getPublished().toString(), link);

        FEED_DB.addEntryToFeed(id, entryBean);

        return Response.ok("OK").build();
    }
}
