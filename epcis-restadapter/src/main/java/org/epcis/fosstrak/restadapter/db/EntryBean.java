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

/**
 *
 * The bean for a AtomPub Feed Entry
 *
 * @author Mathias Muellermathias.mueller(at)unifr.ch
 *
 */
public class EntryBean {

    private String id;
    private String title;
    private String summary;
    private String summaryXML;
    private String updated;
    private String published;
    private String link;

    /**
     * Constructs a new Entry Bean
     *
     *
     * @param id
     * @param title
     * @param summary
     * @param summaryXML
     * @param updated
     * @param published
     * @param link
     */
    public EntryBean(String id, String title, String summary, String summaryXML, String updated, String published, String link) {
        this.id         = id;
        this.title      = title;
        this.summary    = summary;
        this.summaryXML = summaryXML;
        this.updated    = updated;
        this.published  = published;
        this.link       = link;
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
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return the updated
     */
    public String getUpdated() {
        return updated;
    }

    /**
     * @param updated the updated to set
     */
    public void setUpdated(String updated) {
        this.updated = updated;
    }

    /**
     * @return the published
     */
    public String getPublished() {
        return published;
    }

    /**
     * @param published the published to set
     */
    public void setPublished(String published) {
        this.published = published;
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
     * @return the summaryXML
     */
    public String getSummaryXML() {
        return summaryXML;
    }

    /**
     * @param summaryXML the summaryXML to set
     */
    public void setSummaryXML(String summaryXML) {
        this.summaryXML = summaryXML;
    }
}
