package org.fosstrak.epcis.model;

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
 * &lt;complexType name="EventListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="ObjectEvent" type="{urn:epcglobal:epcis:xsd:1}ObjectEventType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="AggregationEvent" type="{urn:epcglobal:epcis:xsd:1}AggregationEventType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="QuantityEvent" type="{urn:epcglobal:epcis:xsd:1}QuantityEventType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TransactionEvent" type="{urn:epcglobal:epcis:xsd:1}TransactionEventType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="extension" type="{urn:epcglobal:epcis:xsd:1}EPCISEventListExtensionType"/>
 *         &lt;any processContents='lax' namespace='##other'/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventListType", namespace = "urn:epcglobal:epcis:xsd:1", propOrder = { "objectEventOrAggregationEventOrQuantityEvent" })
public class EventListType {

    @XmlElementRefs( {
            @XmlElementRef(name = "TransactionEvent", type = JAXBElement.class),
            @XmlElementRef(name = "AggregationEvent", type = JAXBElement.class),
            @XmlElementRef(name = "extension", type = JAXBElement.class),
            @XmlElementRef(name = "QuantityEvent", type = JAXBElement.class),
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
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link TransactionEventType }{@code >}
     * {@link Object } {@link JAXBElement }{@code <}{@link AggregationEventType }
     * {@code >} {@link Element } {@link JAXBElement }{@code <}
     * {@link EPCISEventListExtensionType }{@code >} {@link JAXBElement }{@code <}
     * {@link QuantityEventType }{@code >} {@link JAXBElement }{@code <}
     * {@link ObjectEventType }{@code >}
     */
    public List<Object> getObjectEventOrAggregationEventOrQuantityEvent() {
        if (objectEventOrAggregationEventOrQuantityEvent == null) {
            objectEventOrAggregationEventOrQuantityEvent = new ArrayList<Object>();
        }
        return this.objectEventOrAggregationEventOrQuantityEvent;
    }

}
