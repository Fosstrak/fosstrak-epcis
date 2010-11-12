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

import org.epcis.fosstrak.restadapter.config.URIConstants;
import org.epcis.fosstrak.restadapter.model.epc.ElectronicProductCode;
import org.epcis.fosstrak.restadapter.model.Entry;
import org.epcis.fosstrak.restadapter.util.URI;
import org.epcis.fosstrak.restadapter.ws.generated.EPC;
import org.epcis.fosstrak.restadapter.ws.generated.EPCISEventType;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The JAXB class to handle Transaction Events of the EPCIS REST Adapter model
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class TransactionEvent extends EPCISEvent {

    /**
     * Constructs the event class.
     *
     */
    public TransactionEvent() {}

    /**
     * Constructs a TransactionEvent.
     *
     *
     * @param event
     * @param index
     */
    public TransactionEvent(EPCISEventType event) {
        super(event);
        initFillSpecificData();
        super.setUp();
    }

    protected String getAction() {
        String res = "";

        if (action != null) {
            res = action.value();
        }

        return res;
    }

    protected List<ElectronicProductCode> getEpcList() {
        List<ElectronicProductCode> res = new LinkedList<ElectronicProductCode>();

        if (epcList != null) {
            for (EPC myEPC : epcList.getEpc()) {
                String                myStrEPC              = myEPC.getValue();
                ElectronicProductCode electronicProductCode = new ElectronicProductCode();
                Entry                 myEntry               = new Entry();

                myEntry.setValue(myStrEPC);
                electronicProductCode.setEpc(myEntry);
                res.add(electronicProductCode);
            }
        }

        return res;
    }

    protected String getParentID() {
        String res = "";

        if (parentID != null) {
            res = parentID;
        }

        return res;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "action")
    public Entry getActionEntry() {
        return actionEntry;
    }

    /**
     * Method description
     *
     *
     * @param actionEntry
     */
    public void setActionEntry(Entry actionEntry) {
        this.actionEntry = actionEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElementWrapper(name = "epcs")
    @XmlElement(name = "epc")
    public List<ElectronicProductCode> getEpcEntry() {
        return epcEntry;
    }

    /**
     * Method description
     *
     *
     * @param epcEntry
     */
    public void setEpcEntry(List<ElectronicProductCode> epcEntry) {
        this.epcEntry = epcEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "epcList")
    public Entry getEpcsEntry() {
        return epcsEntry;
    }

    /**
     * Method description
     *
     *
     * @param epcsEntry
     */
    public void setEpcsEntry(Entry epcsEntry) {
        this.epcsEntry = epcsEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "parentID")
    public Entry getParentIDEntry() {
        return parentIDEntry;
    }

    /**
     * Method description
     *
     *
     * @param parentIDEntry
     */
    public void setParentIDEntry(Entry parentIDEntry) {
        this.parentIDEntry = parentIDEntry;
    }

    /**
     * Method description
     *
     */
    @Override
    public void initFillSpecificData() {
        if (!getAction().equals("")) {
            actionEntry = new Entry();
            actionEntry.setValue(getAction());
        }

        if (!getParentID().equals("")) {
            parentIDEntry = new Entry();
            parentIDEntry.setValue(getParentID());
        }

        if (!getEpcList().isEmpty()) {
            epcsEntry = new Entry();
            epcsEntry.setName(Epcs);
            epcsEntry.setNameRef(URI.buildEventIdLink(URIConstants.EPCS, epcsEntry, bizLocationEntry.getValue(), readPointEntry.getValue(), eventTimeEntry.getValue()));
            setBizTransactionEntry(getBizTransactionList());
            epcEntry = getEpcList();
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

        if (!(otherEvent instanceof TransactionEvent)) {
            return false;
        }

        TransactionEvent otherSubclassEvent = (TransactionEvent) otherEvent;

        try {
            if (!(getParentIDEntry().getValue().equals(otherSubclassEvent.getParentIDEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            List<ElectronicProductCode> myEPCs    = getEpcEntry();
            List<ElectronicProductCode> otherEPCs = otherSubclassEvent.getEpcEntry();

            for (ElectronicProductCode myEPC : myEPCs) {
                boolean isContained = false;

                for (ElectronicProductCode otherEPC : otherEPCs) {
                    if ((myEPC.getEpc().getValue().equals(otherEPC.getEpc().getValue()))) {
                        isContained = true;
                    }
                }

                if (!isContained) {
                    return false;
                }
            }

            if (myEPCs.size() != otherEPCs.size()) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            if (!(getActionEntry().getValue().equals(otherSubclassEvent.getActionEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        return res;
    }
}
