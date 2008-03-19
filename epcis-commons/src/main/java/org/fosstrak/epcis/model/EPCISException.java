package org.accada.epcis.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for EPCISException complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;EPCISException&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;reason&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EPCISException", propOrder = { "reason" })
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
