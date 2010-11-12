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
package org.epcis.fosstrak.restadapter.model;

import org.epcis.fosstrak.restadapter.http.HTTP;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The JAXB form of a resource model
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 */
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Form {

    private String      action;
    private String      description;
    private String      method = HTTP.GET;
    private String      enctype;
    private String      actionDescription;
    private List<Entry> entries = new LinkedList<Entry>();

    /**
     * Method description
     *
     *
     * @param name
     * @param id
     * @param value
     * @param description
     */
    public void addEntry(String name, String id, String value, String description) {
        Entry entry = new Entry();

        entry.setName(name);
        entry.setId(id);
        entry.setValue(value);
        entry.setDescription(description);
        entries.add(entry);
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getAction() {
        return action;
    }

    /**
     * Method description
     *
     *
     * @param action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method description
     *
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getMethod() {
        return method;
    }

    /**
     * Method description
     *
     *
     * @param method
     */
    public void setMethod(String method) {
        if (method.equals(HTTP.GET) || method.equals(HTTP.POST) || method.equals(HTTP.PUT) || method.equals(HTTP.DELETE) || method.equals(HTTP.HEAD) || method.equals(HTTP.OPTIONS)) {
            this.method = method;
        }

        if (method.equals(HTTP.AJAX_GET) || method.equals(HTTP.AJAX_POST) || method.equals(HTTP.AJAX_PUT) || method.equals(HTTP.AJAX_DELETE) || method.equals(HTTP.AJAX_HEAD) || method.equals(HTTP.AJAX_OPTIONS)) {
            this.method = method;
        }
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getEnctype() {
        return enctype;
    }

    /**
     * Method description
     *
     *
     * @param enctype
     */
    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getActionDescription() {
        return actionDescription;
    }

    /**
     * Method description
     *
     *
     * @param actionDescription
     */
    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElementWrapper(name = "entries")
    @XmlElement(name = "entry")
    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * Method description
     *
     *
     * @param entries
     */
    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}
