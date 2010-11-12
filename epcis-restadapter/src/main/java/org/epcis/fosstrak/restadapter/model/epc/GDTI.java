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
 * Global Document Type Identifier
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 */
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class GDTI implements EPC {

    public static final String NAME = "Global Document Type Identifier";
    public static final String ID   = GDTI_PREFIX;
    private String             epc;
    private String             companyPrefix;
    private String             documentType;
    private String             serialNumber;

    /**
     * Constructs Global Document Type Identifier
     *
     *
     * @param epc
     */
    public GDTI(String epc) {
        this.epc = epc;
        initEPC();
    }

    private void initEPC() {
        String part[] = epc.split("\\.");

        if (part.length == 3) {
            companyPrefix = part[0];
            documentType  = part[1];
            serialNumber  = part[2];
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

        entry2.setName(DOCUMENT_TYPE);
        entry2.setValue(documentType);
        entry2.setValueRef(ID + companyPrefix + "." + documentType + "." + Config.STAR);

        entry3.setName(SERIAL_NUMBER);
        entry3.setValue(serialNumber);
        entry3.setValueRef(ID + companyPrefix + "." + documentType + "." + serialNumber);

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
