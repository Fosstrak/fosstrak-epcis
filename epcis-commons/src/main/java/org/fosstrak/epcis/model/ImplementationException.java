package org.fosstrak.epcis.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ImplementationException complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="ImplementationException">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:epcglobal:epcis-query:xsd:1}EPCISException">
 *       &lt;sequence>
 *         &lt;element name="severity" type="{urn:epcglobal:epcis-query:xsd:1}ImplementationExceptionSeverity"/>
 *         &lt;element name="queryName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subscriptionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImplementationException", propOrder = { "severity", "queryName", "subscriptionID" })
public class ImplementationException extends EPCISException {

    @XmlElement(required = true)
    protected ImplementationExceptionSeverity severity;
    protected String queryName;
    protected String subscriptionID;

    /**
     * Gets the value of the severity property.
     * 
     * @return possible object is {@link ImplementationExceptionSeverity }
     */
    public ImplementationExceptionSeverity getSeverity() {
        return severity;
    }

    /**
     * Sets the value of the severity property.
     * 
     * @param value
     *            allowed object is {@link ImplementationExceptionSeverity }
     */
    public void setSeverity(ImplementationExceptionSeverity value) {
        this.severity = value;
    }

    /**
     * Gets the value of the queryName property.
     * 
     * @return possible object is {@link String }
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * Sets the value of the queryName property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setQueryName(String value) {
        this.queryName = value;
    }

    /**
     * Gets the value of the subscriptionID property.
     * 
     * @return possible object is {@link String }
     */
    public String getSubscriptionID() {
        return subscriptionID;
    }

    /**
     * Sets the value of the subscriptionID property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setSubscriptionID(String value) {
        this.subscriptionID = value;
    }

}
