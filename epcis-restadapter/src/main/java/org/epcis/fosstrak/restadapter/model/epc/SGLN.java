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
package org.epcis.fosstrak.restadapter.model.epc;

import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.model.Entry;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Serialized Global Location Number
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 */
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class SGLN implements EPC {

    public static final String NAME = "Serialized Global Location Number";
    public static final String ID   = SGLN_PREFIX;    // "urn:epc:id:sgln:";
    private String             epc;
    private String             companyPrefix;
    private String             locationReference;
    private String             extensionComponent;

    /**
     * Constructs Serialized Global Location Number
     *
     *
     * @param epc
     */
    public SGLN(String epc) {
        this.epc = epc;
        initEPC();
    }

    private void initEPC() {
        String part[] = epc.split("\\.");

        if (part.length == 3) {
            companyPrefix      = part[0];
            locationReference  = part[1];
            extensionComponent = part[2];
        }
    }

    /**
     * Get a list of the components of this EPC
     *
     *
     * @return
     */
    @XmlElement(name = "component")
    public List<Entry> getComponents() {
        List<Entry> res    = new ArrayList<Entry>();

        Entry       entry1 = new Entry();
        Entry       entry2 = new Entry();
        Entry       entry3 = new Entry();

        entry1.setName(COMPANY_PREFIX);
        entry1.setValue(companyPrefix);
        entry1.setValueRef(ID + companyPrefix + "." + Config.STAR);

        entry2.setName(LOCATION_REFERENCE);
        entry2.setValue(locationReference);
        entry2.setValueRef(ID + companyPrefix + "." + locationReference + "." + Config.STAR);

        entry3.setName(EXTENSION_COMPONENT);
        entry3.setValue(extensionComponent);
        entry3.setValueRef(ID + companyPrefix + "." + locationReference + "." + extensionComponent);

        if (entry1.getValue() != null) {
            res.add(entry1);
        }

        if (entry2.getValue() != null) {
            res.add(entry2);
        }

        if (entry3.getValue() != null) {
            res.add(entry3);
        }

        return res;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getName() {
        return NAME;
    }
}
