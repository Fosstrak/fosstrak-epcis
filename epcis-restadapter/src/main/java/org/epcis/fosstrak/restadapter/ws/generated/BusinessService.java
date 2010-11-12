package org.epcis.fosstrak.restadapter.ws.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for BusinessService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BusinessService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BusinessServiceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServiceTransaction" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}ServiceTransaction" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BusinessService", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", propOrder = {
    "businessServiceName",
    "serviceTransaction"
})
public class BusinessService {

    @XmlElement(name = "BusinessServiceName", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected String businessServiceName;
    @XmlElement(name = "ServiceTransaction", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected ServiceTransaction serviceTransaction;

    /**
     * Gets the value of the businessServiceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessServiceName() {
        return businessServiceName;
    }

    /**
     * Sets the value of the businessServiceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessServiceName(String value) {
        this.businessServiceName = value;
    }

    /**
     * Gets the value of the serviceTransaction property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceTransaction }
     *     
     */
    public ServiceTransaction getServiceTransaction() {
        return serviceTransaction;
    }

    /**
     * Sets the value of the serviceTransaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceTransaction }
     *     
     */
    public void setServiceTransaction(ServiceTransaction value) {
        this.serviceTransaction = value;
    }
}
