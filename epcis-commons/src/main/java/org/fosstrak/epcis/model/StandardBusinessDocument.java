package org.accada.epcis.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Element;

/**
 * <p>
 * Java class for StandardBusinessDocument complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;StandardBusinessDocument&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref=&quot;{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}StandardBusinessDocumentHeader&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;any/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StandardBusinessDocument", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", propOrder = {
        "standardBusinessDocumentHeader", "any" })
public class StandardBusinessDocument {

    @XmlElement(name = "StandardBusinessDocumentHeader", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected StandardBusinessDocumentHeader standardBusinessDocumentHeader;
    @XmlAnyElement(lax = true)
    protected Object any;

    /**
     * Gets the value of the standardBusinessDocumentHeader property.
     * 
     * @return possible object is {@link StandardBusinessDocumentHeader }
     */
    public StandardBusinessDocumentHeader getStandardBusinessDocumentHeader() {
        return standardBusinessDocumentHeader;
    }

    /**
     * Sets the value of the standardBusinessDocumentHeader property.
     * 
     * @param value
     *            allowed object is {@link StandardBusinessDocumentHeader }
     */
    public void setStandardBusinessDocumentHeader(StandardBusinessDocumentHeader value) {
        this.standardBusinessDocumentHeader = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * @return possible object is {@link Element } {@link Object }
     */
    public Object getAny() {
        return any;
    }

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *            allowed object is {@link Element } {@link Object }
     */
    public void setAny(Object value) {
        this.any = value;
    }

}
