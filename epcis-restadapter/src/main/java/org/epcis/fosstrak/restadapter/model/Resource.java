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

import org.epcis.fosstrak.restadapter.model.events.AggregationEvent;
import org.epcis.fosstrak.restadapter.model.events.ObjectEvent;
import org.epcis.fosstrak.restadapter.model.events.QuantityEvent;
import org.epcis.fosstrak.restadapter.model.events.TransactionEvent;
import org.epcis.fosstrak.restadapter.ws.generated.AggregationEventType;
import org.epcis.fosstrak.restadapter.ws.generated.EPCISEventType;
import org.epcis.fosstrak.restadapter.ws.generated.EventListType;
import org.epcis.fosstrak.restadapter.ws.generated.ObjectEventType;
import org.epcis.fosstrak.restadapter.ws.generated.Poll;
import org.epcis.fosstrak.restadapter.ws.generated.QuantityEventType;
import org.epcis.fosstrak.restadapter.ws.generated.QueryResults;
import org.epcis.fosstrak.restadapter.ws.generated.TransactionEventType;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The Resource class of the EPCIS REST Adapter.
 * A JAXB Class as container acting as root for the other model classes.
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 */
@XmlType(propOrder = {
    "id", "uri", "name", "description", "treeMenu", "form", "fields", "objectEvent", "aggregationEvent", "quantityEvent", "transactionEvent", "queryResults", "poll"
})
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Resource {

    private String                 uri              = null;
    private String                 id               = null;
    private String                 name             = null;
    private String                 description      = null;
    private TreeMenu               treeMenu         = null;
    private Content                fields           = null;
    private Poll                   poll             = null;
    private QueryResults           queryResults     = null;
    private List<AggregationEvent> aggregationEvent = new LinkedList<AggregationEvent>();
    private List<ObjectEvent>      objectEvent      = new LinkedList<ObjectEvent>();
    private List<QuantityEvent>    quantityEvent    = new LinkedList<QuantityEvent>();
    private List<TransactionEvent> transactionEvent = new LinkedList<TransactionEvent>();
    private Form                   form             = null;

    /**
     * Method description
     *
     *
     * @return
     */
    public String getUri() {
        return uri;
    }

    /**
     * Method description
     *
     *
     * @param uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Method description
     *
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Method description
     *
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
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
    public TreeMenu getTreeMenu() {
        return treeMenu;
    }

    /**
     * Method description
     *
     *
     * @param treeMenu
     */
    public void setTreeMenu(TreeMenu treeMenu) {
        this.treeMenu = treeMenu;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Content getFields() {
        return fields;
    }

    /**
     * Method description
     *
     *
     * @param fields
     */
    public void setFields(Content fields) {
        this.fields = fields;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "queryResults")
    public QueryResults getQueryResults() {
        return queryResults;
    }

    /**
     * Method description
     *
     *
     * @param queryResults
     */
    public void setQueryResults_XML_ONLY(QueryResults queryResults) {
        this.queryResults = queryResults;
    }

    /**
     * Method description
     *
     *
     * @param queryResults
     */
    public void setQueryResults(QueryResults queryResults) {
        this.queryResults = queryResults;

        if (getQueryResults() != null) {
            EventListType events = queryResults.getResultsBody().getEventList();

            for (int i = 0; i < events.getObjectEventOrAggregationEventOrQuantityEvent().size(); i++) {
                JAXBElement    element = (JAXBElement) events.getObjectEventOrAggregationEventOrQuantityEvent().get(i);
                EPCISEventType event   = (EPCISEventType) element.getValue();

                if (event instanceof ObjectEventType) {
                    ObjectEvent myEvent = new ObjectEvent(event);

                    myEvent.calculateRESTfulPathID();
                    objectEvent.add(myEvent);
                }

                if (event instanceof AggregationEventType) {
                    AggregationEvent myEvent = new AggregationEvent(event);

                    myEvent.calculateRESTfulPathID();
                    aggregationEvent.add(myEvent);
                }

                if (event instanceof TransactionEventType) {
                    TransactionEvent myEvent = new TransactionEvent(event);

                    myEvent.calculateRESTfulPathID();
                    transactionEvent.add(myEvent);
                }

                if (event instanceof QuantityEventType) {
                    QuantityEvent myEvent = new QuantityEvent(event);

                    myEvent.calculateRESTfulPathID();
                    quantityEvent.add(myEvent);
                }
            }
        }
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Form getForm() {
        return form;
    }

    /**
     * Method description
     *
     *
     * @param form
     */
    public void setForm(Form form) {
        this.form = form;
    }



    /**
     * Method description
     *
     *
     * @return
     */
    public Poll getPoll() {
        return poll;
    }

    /**
     * Method description
     *
     *
     * @param poll
     */
    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElementWrapper(name = "aggregationEvents")
    public List<AggregationEvent> getAggregationEvent() {
        return aggregationEvent;
    }

    /**
     * Method description
     *
     *
     * @param aggregationEvent
     */
    public void setAggregationEvent(List<AggregationEvent> aggregationEvent) {
        this.aggregationEvent = aggregationEvent;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElementWrapper(name = "objectEvents")
    public List<ObjectEvent> getObjectEvent() {
        return objectEvent;
    }

    /**
     * Method description
     *
     *
     * @param objectEvent
     */
    public void setObjectEvent(List<ObjectEvent> objectEvent) {
        this.objectEvent = objectEvent;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElementWrapper(name = "quantityEvents")
    public List<QuantityEvent> getQuantityEvent() {
        return quantityEvent;
    }

    /**
     * Method description
     *
     *
     * @param quantityEvent
     */
    public void setQuantityEvent(List<QuantityEvent> quantityEvent) {
        this.quantityEvent = quantityEvent;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElementWrapper(name = "transactionEvents")
    public List<TransactionEvent> getTransactionEvent() {
        return transactionEvent;
    }

    /**
     * Method description
     *
     *
     * @param transactionEvent
     */
    public void setTransactionEvent(List<TransactionEvent> transactionEvent) {
        this.transactionEvent = transactionEvent;
    }
}
