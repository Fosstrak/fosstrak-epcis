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

import java.util.List;
import java.util.Vector;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The JAXB Class as Roots Content Model
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Content {

    private String      description;
    private List<Entry> content;

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
    @XmlElementWrapper(name = "restfulEpcisContent")
    public List<Entry> getContent() {
        if (content == null) {
            content = new Vector<Entry>();
        }

        return content;
    }

    /**
     * Method description
     *
     *
     * @param links
     */
    public void setContent(List<Entry> links) {
        this.content = links;
    }
}
