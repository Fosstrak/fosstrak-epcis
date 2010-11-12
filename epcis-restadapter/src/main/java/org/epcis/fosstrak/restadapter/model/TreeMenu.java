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

import org.epcis.fosstrak.restadapter.util.URI;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The JAXB tree menu of a resource model
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class TreeMenu {

    private List<Entry> link = new Vector<Entry>();
    private int         depth;

    private void addNextSubLink(Entry rl) {
        getLink().add(depth++, rl);
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public ListIterator<Entry> getLinksInProperOrder() {
        return getLink().listIterator();
    }

    /**
     * Method description
     *
     *
     * @param hostURI
     * @param restRelUrl
     */
    public void calculateAndSetTreeMenuFromURL(String hostURI, String restRelUrl) {
        String baseRelUrl = "";

        depth = 0;
        setLink(new Vector<Entry>());

        if (restRelUrl.startsWith("/")) {
            restRelUrl = restRelUrl.substring(1);
        }

        String[] parts = restRelUrl.split("/");

        for (String s : parts) {
            Entry entryLink = new Entry();

            entryLink.setValue(URI.unescapeURL(s));

            String ref = URI.addSubPath(hostURI, baseRelUrl + "/" + s);

            // if it do not contains a query parameter add a slash at the end
            if (!s.contains("?")) {
                ref = ref + "/";
            }

            entryLink.setValueRef(ref);
            baseRelUrl = URI.addSubPath(baseRelUrl, s);
            addNextSubLink(entryLink);
        }

    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    public List<Entry> getLink() {
        if (link == null) {
            setLink(new Vector<Entry>());
        }

        return link;
    }

    /**
     * Method description
     *
     *
     * @param links
     */
    public void setLink(List<Entry> links) {
        this.link = links;
    }
}
