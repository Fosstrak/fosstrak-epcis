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
package org.epcis.fosstrak.restadapter.representation;

import org.epcis.fosstrak.restadapter.model.Form;
import org.epcis.fosstrak.restadapter.model.Content;
import org.epcis.fosstrak.restadapter.model.Entry;
import org.epcis.fosstrak.restadapter.model.Resource;
import org.epcis.fosstrak.restadapter.model.events.AggregationEvent;
import org.epcis.fosstrak.restadapter.model.events.EPCISEvent;
import org.epcis.fosstrak.restadapter.model.epc.ElectronicProductCode;
import org.epcis.fosstrak.restadapter.model.events.ObjectEvent;
import org.epcis.fosstrak.restadapter.model.events.QuantityEvent;
import org.epcis.fosstrak.restadapter.model.events.TransactionEvent;
import java.util.List;


/**
 * Class to handle the higher level details of HTML to build the page from the model
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
public class HTMLBuilder {

    /**
     * Build the HTML from the model (resource)
     *
     *
     * @param resource
     *
     * @return
     */
    public String buildRepresentation(Resource resource) {
        StringBuilder res = new StringBuilder();

        appendDescription(res, resource.getDescription());
        appendForm(res, resource.getForm());
        appendLinks(res, resource.getFields());
        appendHorizontalLine(res);
        appendObjectEvents(res, resource.getObjectEvent());
        appendQuantityEvents(res, resource.getQuantityEvent());
        appendAggregationEvents(res, resource.getAggregationEvent());
        appendTransactionEvents(res, resource.getTransactionEvent());

        return res.toString();
    }

    private String buildEventRepresentation(AggregationEvent event) {
        StringBuilder res = new StringBuilder();

        res.append(buildCommonEventRepresentation(event));

        appendMember(res, event.getActionEntry());
        appendMember(res, event.getParentIDEntry());
        appendMember(res, event.getEpcsEntry());
        appendMemberEPC(res, event.getEpcEntry());

        return res.toString();
    }

    private String buildEventRepresentation(ObjectEvent event) {
        StringBuilder res = new StringBuilder();

        res.append(buildCommonEventRepresentation(event));

        appendMember(res, event.getActionEntry());
        appendMember(res, event.getEpcsEntry());
        appendMemberEPC(res, event.getEpcEntry());

        return res.toString();
    }

    private String buildEventRepresentation(QuantityEvent event) {
        StringBuilder res = new StringBuilder();

        res.append(buildCommonEventRepresentation(event));

        appendMember(res, event.getEpcClassEntry());
        appendMember(res, event.getQuantityEntry());

        return res.toString();
    }

    private String buildEventRepresentation(TransactionEvent event) {
        StringBuilder res = new StringBuilder();

        res.append(buildCommonEventRepresentation(event));

        appendMember(res, event.getActionEntry());
        appendMember(res, event.getParentIDEntry());
        appendMember(res, event.getEpcsEntry());
        appendMemberEPC(res, event.getEpcEntry());

        return res.toString();
    }

    private String buildCommonEventRepresentation(EPCISEvent event) {
        StringBuilder res = new StringBuilder();

        appendMember(res, event.getRestfulIDEntry());

        appendMember(res, event.getTypeEntry());
        appendMember(res, event.getEventTimeEntry());
        appendMember(res, event.getTimeZoneOffsetEntry());
        appendMember(res, event.getRecordTimeEntry());
        appendMember(res, event.getBizLocationEntry());
        appendMember(res, event.getReadPointEntry());
        appendMember(res, event.getDispositionEntry());
        appendMember(res, event.getBizStepEntry());
        appendMember(res, event.getBizTransactionsEntry());
        appendMemberBT(res, event.getBizTransactionEntry());

        return res.toString();
    }

    private void appendHorizontalLine(StringBuilder res) {
        res.append(HTMLStringBuilder.getHorizontalLine());
    }

    private void appendMember(StringBuilder res, Entry entry) {
        String link = HTMLStringBuilder.buildMemberTableEntry(entry);

        res.append(link);
    }

    private void appendEPCMember(StringBuilder res, ElectronicProductCode e) {
        String link = HTMLStringBuilder.buildEPCTable_TableEntry(e);

        res.append(link);
    }

    private void appendDescription(StringBuilder res, String description) {
        if (description != null) {
            res.append(HTMLStringBuilder.buildParagraphItalic(description));
        }
    }

    private void appendObjectEvent(StringBuilder res, ObjectEvent objectEvent) {
        if (objectEvent != null) {
            res.append(HTMLStringBuilder.beginTableVisible());
            res.append(buildEventRepresentation(objectEvent));
            res.append(HTMLStringBuilder.endTable());
            appendHorizontalLine(res);
        }
    }

    private void appendQuantityEvent(StringBuilder res, QuantityEvent quantityEvent) {
        if (quantityEvent != null) {
            res.append(HTMLStringBuilder.beginTableVisible());
            res.append(buildEventRepresentation(quantityEvent));
            res.append(HTMLStringBuilder.endTable());
            appendHorizontalLine(res);
        }
    }

    private void appendAggregationEvent(StringBuilder res, AggregationEvent aggregationEvent) {
        if (aggregationEvent != null) {
            res.append(HTMLStringBuilder.beginTableVisible());
            res.append(buildEventRepresentation(aggregationEvent));
            res.append(HTMLStringBuilder.endTable());
            appendHorizontalLine(res);

        }
    }

    private void appendTransactionEvent(StringBuilder res, TransactionEvent transactionEvent) {
        if (transactionEvent != null) {
            res.append(HTMLStringBuilder.beginTableVisible());
            res.append(buildEventRepresentation(transactionEvent));
            res.append(HTMLStringBuilder.endTable());
            appendHorizontalLine(res);
        }
    }

    private void appendLinks(StringBuilder res, Content links) {
        if (links != null) {
            res.append(HTMLStringBuilder.buildLinks(links));
        }
    }

    private void appendForm(StringBuilder res, Form form) {
        if (form != null) {
            res.append(HTMLStringBuilder.buildForm(form));
        }
    }

    private void appendObjectEvents(StringBuilder res, List<ObjectEvent> eventList) {
        if (eventList != null) {
            if (!eventList.isEmpty()) {
                res.append(HTMLStringBuilder.buildTitleFooter("Object Events:"));
            }

            for (ObjectEvent epcisEvent : eventList) {
                appendObjectEvent(res, epcisEvent);
            }
        }
    }

    private void appendAggregationEvents(StringBuilder res, List<AggregationEvent> eventList) {
        if (eventList != null) {
            if (!eventList.isEmpty()) {
                res.append(HTMLStringBuilder.buildTitleFooter("Aggregation Events:"));
            }

            for (AggregationEvent epcisEvent : eventList) {
                appendAggregationEvent(res, epcisEvent);
            }
        }
    }

    private void appendTransactionEvents(StringBuilder res, List<TransactionEvent> eventList) {
        if (eventList != null) {
            if (!eventList.isEmpty()) {
                res.append(HTMLStringBuilder.buildTitleFooter("Transaction Events:"));
            }

            for (TransactionEvent epcisEvent : eventList) {
                appendTransactionEvent(res, epcisEvent);
            }
        }
    }

    private void appendQuantityEvents(StringBuilder res, List<QuantityEvent> eventList) {
        if (eventList != null) {
            if (!eventList.isEmpty()) {
                res.append(HTMLStringBuilder.buildTitleFooter("Quantity Events:"));
            }

            for (QuantityEvent epcisEvent : eventList) {
                appendQuantityEvent(res, epcisEvent);
            }
        }
    }

    private void appendMemberBT(StringBuilder res, List<Entry> bizTransactionEntry) {
        for (Entry e : bizTransactionEntry) {
            appendMember(res, e);
        }
    }

    private void appendMemberEPC(StringBuilder res, List<ElectronicProductCode> epcEntry) {
        for (ElectronicProductCode e : epcEntry) {
            appendEPCMember(res, e);
        }
    }

}
