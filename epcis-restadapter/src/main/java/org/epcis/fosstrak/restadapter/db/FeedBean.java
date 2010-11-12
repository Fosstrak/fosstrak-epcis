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
package org.epcis.fosstrak.restadapter.db;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * The bean for a Feed
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 *
 */
public class FeedBean {

    private String          id;
    private String          title;
    private String          subtitle;
    private String          author;
    private String          category;
    private String          contributor;
    private String          link;
    private String          logo;

    private List<EntryBean> entries = new ArrayList<EntryBean>();

    /**
     * Constructs a new Feed Bean
     *
     *
     * @param id
     * @param title
     * @param subtitle
     * @param author
     * @param category
     * @param contributor
     * @param link
     * @param logo
     */
    public FeedBean(String id, String title, String subtitle, String author, String category, String contributor, String link, String logo) {
        this.id          = id;
        this.title       = title;
        this.subtitle    = subtitle;
        this.author      = author;
        this.category    = category;
        this.contributor = contributor;
        this.link        = link;
        this.logo        = logo;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the subtitle
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * @param subtitle the subtitle to set
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the contributor
     */
    public String getContributor() {
        return contributor;
    }

    /**
     * @param contributor the contributor to set
     */
    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return the entries
     */
    public List<EntryBean> getEntries() {
        return entries;
    }

    /**
     * @param entries the entries to set
     */
    public void setEntries(List<EntryBean> entries) {
        this.entries = entries;
    }

    /**
     * @return the logo
     */
    public String getLogo() {
        return logo;
    }

    /**
     * @param logo the logo to set
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }
}
