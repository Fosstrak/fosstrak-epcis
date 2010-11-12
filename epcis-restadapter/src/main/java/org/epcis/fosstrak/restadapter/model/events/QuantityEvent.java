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
package org.epcis.fosstrak.restadapter.model.events;

import org.epcis.fosstrak.restadapter.model.Entry;
import org.epcis.fosstrak.restadapter.ws.generated.EPCISEventType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The JAXB class to handle Quantity Events of the EPCIS REST Adapter model
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 */
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class QuantityEvent extends EPCISEvent {


    public QuantityEvent() {}

    /**
     * Constructs the event class.
     *
     *
     * @param event
     * @param index
     */
    public QuantityEvent(EPCISEventType event) {
        super(event);
        initFillSpecificData();
        super.setUp();
    }

    protected String getEpcClass() {
        String res = "";

        if (epcClass != null) {
            res = epcClass;
        }

        return res;
    }

    protected String getQuantity() {
        String res = "";

        if (quantity != Integer.MIN_VALUE) {
            res = "" + quantity;
        }

        return res;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "quantity")
    public Entry getQuantityEntry() {
        return quantityEntry;
    }

    /**
     * Method description
     *
     *
     * @param quantityEntry
     */
    public void setQuantityEntry(Entry quantityEntry) {
        this.quantityEntry = quantityEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "epcClass")
    public Entry getEpcClassEntry() {
        return epcClassEntry;
    }

    /**
     * Method description
     *
     *
     * @param epcClassEntry
     */
    public void setEpcClassEntry(Entry epcClassEntry) {
        this.epcClassEntry = epcClassEntry;
    }

    /**
     * Method description
     *
     */
    @Override
    public void initFillSpecificData() {
        if (!getQuantity().equals("")) {
            quantityEntry = new Entry();
            quantityEntry.setValue(getQuantity());
        }

        if (!getEpcClass().equals("")) {
            epcClassEntry = new Entry();
            epcClassEntry.setValue(getEpcClass());
        }
    }

    /**
     * Method description
     *
     *
     * @param otherEvent
     *
     * @return
     */
    @Override
    public boolean isSubclassLikeEvent(EPCISEvent otherEvent) {
        boolean res = true;

        if (!(otherEvent instanceof QuantityEvent)) {
            return false;
        }

        QuantityEvent otherSubclassEvent = (QuantityEvent) otherEvent;

        try {
            if (!(getEpcClassEntry().getValue().equals(otherSubclassEvent.getEpcClassEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            if (!(getQuantityEntry().getValue().equals(otherSubclassEvent.getQuantityEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        return res;
    }
}
