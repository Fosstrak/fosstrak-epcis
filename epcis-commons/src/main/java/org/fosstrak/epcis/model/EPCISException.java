package org.fosstrak.epcis.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for EPCISException complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="EPCISException">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reason" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EPCISException", propOrder = { "reason" })
@XmlSeeAlso( {
        SecurityException.class, NoSuchNameException.class, InvalidURIException.class,
        SubscriptionControlsException.class, SubscribeNotPermittedException.class, QueryParameterException.class,
        QueryTooComplexException.class, QueryTooLargeException.class, DuplicateSubscriptionException.class,
        ValidationException.class, ImplementationException.class, NoSuchSubscriptionException.class,
        DuplicateNameException.class })
public class EPCISException {

    @XmlElement(required = true)
    protected String reason;

    /**
     * Gets the value of the reason property.
     * 
     * @return possible object is {@link String }
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setReason(String value) {
        this.reason = value;
    }

}
