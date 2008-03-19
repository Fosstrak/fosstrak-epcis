package org.accada.epcis.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for CorrelationInformation complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;CorrelationInformation&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;RequestingDocumentCreationDateTime&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}dateTime&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;RequestingDocumentInstanceIdentifier&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;ExpectedResponseDateTime&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}dateTime&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CorrelationInformation", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", propOrder = {
        "requestingDocumentCreationDateTime", "requestingDocumentInstanceIdentifier", "expectedResponseDateTime" })
public class CorrelationInformation {

    @XmlElement(name = "RequestingDocumentCreationDateTime", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected XMLGregorianCalendar requestingDocumentCreationDateTime;
    @XmlElement(name = "RequestingDocumentInstanceIdentifier", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected String requestingDocumentInstanceIdentifier;
    @XmlElement(name = "ExpectedResponseDateTime", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected XMLGregorianCalendar expectedResponseDateTime;

    /**
     * Gets the value of the requestingDocumentCreationDateTime property.
     * 
     * @return possible object is {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getRequestingDocumentCreationDateTime() {
        return requestingDocumentCreationDateTime;
    }

    /**
     * Sets the value of the requestingDocumentCreationDateTime property.
     * 
     * @param value
     *            allowed object is {@link XMLGregorianCalendar }
     */
    public void setRequestingDocumentCreationDateTime(XMLGregorianCalendar value) {
        this.requestingDocumentCreationDateTime = value;
    }

    /**
     * Gets the value of the requestingDocumentInstanceIdentifier property.
     * 
     * @return possible object is {@link String }
     */
    public String getRequestingDocumentInstanceIdentifier() {
        return requestingDocumentInstanceIdentifier;
    }

    /**
     * Sets the value of the requestingDocumentInstanceIdentifier property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setRequestingDocumentInstanceIdentifier(String value) {
        this.requestingDocumentInstanceIdentifier = value;
    }

    /**
     * Gets the value of the expectedResponseDateTime property.
     * 
     * @return possible object is {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getExpectedResponseDateTime() {
        return expectedResponseDateTime;
    }

    /**
     * Sets the value of the expectedResponseDateTime property.
     * 
     * @param value
     *            allowed object is {@link XMLGregorianCalendar }
     */
    public void setExpectedResponseDateTime(XMLGregorianCalendar value) {
        this.expectedResponseDateTime = value;
    }

}
