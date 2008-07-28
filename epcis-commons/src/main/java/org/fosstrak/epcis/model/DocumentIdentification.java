package org.fosstrak.epcis.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for DocumentIdentification complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;DocumentIdentification&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;Standard&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;TypeVersion&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;InstanceIdentifier&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;Type&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;MultipleType&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}boolean&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;CreationDateAndTime&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}dateTime&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentIdentification", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", propOrder = {
        "standard", "typeVersion", "instanceIdentifier", "type", "multipleType", "creationDateAndTime" })
public class DocumentIdentification {

    @XmlElement(name = "Standard", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
    protected String standard;
    @XmlElement(name = "TypeVersion", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
    protected String typeVersion;
    @XmlElement(name = "InstanceIdentifier", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
    protected String instanceIdentifier;
    @XmlElement(name = "Type", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
    protected String type;
    @XmlElement(name = "MultipleType", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected Boolean multipleType;
    @XmlElement(name = "CreationDateAndTime", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
    protected XMLGregorianCalendar creationDateAndTime;

    /**
     * Gets the value of the standard property.
     * 
     * @return possible object is {@link String }
     */
    public String getStandard() {
        return standard;
    }

    /**
     * Sets the value of the standard property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setStandard(String value) {
        this.standard = value;
    }

    /**
     * Gets the value of the typeVersion property.
     * 
     * @return possible object is {@link String }
     */
    public String getTypeVersion() {
        return typeVersion;
    }

    /**
     * Sets the value of the typeVersion property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setTypeVersion(String value) {
        this.typeVersion = value;
    }

    /**
     * Gets the value of the instanceIdentifier property.
     * 
     * @return possible object is {@link String }
     */
    public String getInstanceIdentifier() {
        return instanceIdentifier;
    }

    /**
     * Sets the value of the instanceIdentifier property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setInstanceIdentifier(String value) {
        this.instanceIdentifier = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return possible object is {@link String }
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the multipleType property.
     * 
     * @return possible object is {@link Boolean }
     */
    public Boolean isMultipleType() {
        return multipleType;
    }

    /**
     * Sets the value of the multipleType property.
     * 
     * @param value
     *            allowed object is {@link Boolean }
     */
    public void setMultipleType(Boolean value) {
        this.multipleType = value;
    }

    /**
     * Gets the value of the creationDateAndTime property.
     * 
     * @return possible object is {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getCreationDateAndTime() {
        return creationDateAndTime;
    }

    /**
     * Sets the value of the creationDateAndTime property.
     * 
     * @param value
     *            allowed object is {@link XMLGregorianCalendar }
     */
    public void setCreationDateAndTime(XMLGregorianCalendar value) {
        this.creationDateAndTime = value;
    }

}
