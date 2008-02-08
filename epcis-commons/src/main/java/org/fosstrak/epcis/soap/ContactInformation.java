package org.accada.epcis.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ContactInformation complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;ContactInformation&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;Contact&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;EmailAddress&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;FaxNumber&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;TelephoneNumber&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;ContactTypeIdentifier&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContactInformation", propOrder = {
        "contact", "emailAddress", "faxNumber", "telephoneNumber", "contactTypeIdentifier" })
public class ContactInformation {

    @XmlElement(name = "Contact", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
    protected String contact;
    @XmlElement(name = "EmailAddress", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected String emailAddress;
    @XmlElement(name = "FaxNumber", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected String faxNumber;
    @XmlElement(name = "TelephoneNumber", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected String telephoneNumber;
    @XmlElement(name = "ContactTypeIdentifier", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected String contactTypeIdentifier;

    /**
     * Gets the value of the contact property.
     * 
     * @return possible object is {@link String }
     */
    public String getContact() {
        return contact;
    }

    /**
     * Sets the value of the contact property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setContact(String value) {
        this.contact = value;
    }

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return possible object is {@link String }
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the faxNumber property.
     * 
     * @return possible object is {@link String }
     */
    public String getFaxNumber() {
        return faxNumber;
    }

    /**
     * Sets the value of the faxNumber property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setFaxNumber(String value) {
        this.faxNumber = value;
    }

    /**
     * Gets the value of the telephoneNumber property.
     * 
     * @return possible object is {@link String }
     */
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    /**
     * Sets the value of the telephoneNumber property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setTelephoneNumber(String value) {
        this.telephoneNumber = value;
    }

    /**
     * Gets the value of the contactTypeIdentifier property.
     * 
     * @return possible object is {@link String }
     */
    public String getContactTypeIdentifier() {
        return contactTypeIdentifier;
    }

    /**
     * Sets the value of the contactTypeIdentifier property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setContactTypeIdentifier(String value) {
        this.contactTypeIdentifier = value;
    }

}
