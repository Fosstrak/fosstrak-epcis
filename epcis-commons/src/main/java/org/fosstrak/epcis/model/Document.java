package org.fosstrak.epcis.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * EPCglobal document properties for all messages.
 * <p>
 * Java class for Document complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;Document&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;attribute name=&quot;creationDate&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}dateTime&quot; /&gt;
 *       &lt;attribute name=&quot;schemaVersion&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}decimal&quot; /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", namespace = "urn:epcglobal:xsd:1")
public abstract class Document {

    @XmlAttribute(required = true)
    protected XMLGregorianCalendar creationDate;
    @XmlAttribute(required = true)
    protected BigDecimal schemaVersion;

    /**
     * Gets the value of the creationDate property.
     * 
     * @return possible object is {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *            allowed object is {@link XMLGregorianCalendar }
     */
    public void setCreationDate(XMLGregorianCalendar value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the schemaVersion property.
     * 
     * @return possible object is {@link BigDecimal }
     */
    public BigDecimal getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * Sets the value of the schemaVersion property.
     * 
     * @param value
     *            allowed object is {@link BigDecimal }
     */
    public void setSchemaVersion(BigDecimal value) {
        this.schemaVersion = value;
    }

}
