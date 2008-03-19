package org.accada.epcis.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Element;

/**
 * <p>
 * Java class for EventListType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;EventListType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;choice maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;&gt;
 *         &lt;element name=&quot;ObjectEvent&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}ObjectEventType&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;AggregationEvent&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}AggregationEventType&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;QuantityEvent&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}QuantityEventType&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;TransactionEvent&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}TransactionEventType&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;extension&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}EPCISEventListExtensionType&quot;/&gt;
 *         &lt;any/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventListType", namespace = "urn:epcglobal:epcis:xsd:1", propOrder = { "objectEventOrAggregationEventOrQuantityEvent" })
public class EventListType {

    @XmlElementRefs( {
            @XmlElementRef(name = "TransactionEvent", type = JAXBElement.class),
            @XmlElementRef(name = "extension", type = JAXBElement.class),
            @XmlElementRef(name = "QuantityEvent", type = JAXBElement.class),
            @XmlElementRef(name = "AggregationEvent", type = JAXBElement.class),
            @XmlElementRef(name = "ObjectEvent", type = JAXBElement.class) })
    @XmlAnyElement(lax = true)
    protected List<Object> objectEventOrAggregationEventOrQuantityEvent;

    /**
     * Gets the value of the objectEventOrAggregationEventOrQuantityEvent
     * property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the
     * objectEventOrAggregationEventOrQuantityEvent property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getObjectEventOrAggregationEventOrQuantityEvent().add(newItem);
     * </pre>
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link TransactionEventType }{@code >}
     * {@link JAXBElement }{@code <}{@link EPCISEventListExtensionType }{@code >}
     * {@link JAXBElement }{@code <}{@link QuantityEventType }{@code >}
     * {@link Object } {@link Element } {@link JAXBElement }{@code <}{@link AggregationEventType }{@code >}
     * {@link JAXBElement }{@code <}{@link ObjectEventType }{@code >}
     */
    public List<Object> getObjectEventOrAggregationEventOrQuantityEvent() {
        if (objectEventOrAggregationEventOrQuantityEvent == null) {
            objectEventOrAggregationEventOrQuantityEvent = new ArrayList<Object>();
        }
        return this.objectEventOrAggregationEventOrQuantityEvent;
    }

}
