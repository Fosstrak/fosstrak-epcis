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
 * &lt;complexType name=&quot;ImplementationException&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{urn:epcglobal:epcis-query:xsd:1}EPCISException&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;severity&quot; type=&quot;{urn:epcglobal:epcis-query:xsd:1}ImplementationExceptionSeverity&quot;/&gt;
 *         &lt;element name=&quot;queryName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;subscriptionID&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
