package org.accada.epcis.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ManifestItem complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;ManifestItem&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;MimeTypeQualifierCode&quot; type=&quot;{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}MimeTypeQualifier&quot;/&gt;
 *         &lt;element name=&quot;UniformResourceIdentifier&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}anyURI&quot;/&gt;
 *         &lt;element name=&quot;Description&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;LanguageCode&quot; type=&quot;{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Language&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManifestItem", propOrder = {
        "mimeTypeQualifierCode", "uniformResourceIdentifier", "description", "languageCode" })
public class ManifestItem {

    @XmlElement(name = "MimeTypeQualifierCode", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
    protected String mimeTypeQualifierCode;
    @XmlElement(name = "UniformResourceIdentifier", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
    protected String uniformResourceIdentifier;
    @XmlElement(name = "Description", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected String description;
    @XmlElement(name = "LanguageCode", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
    protected String languageCode;

    /**
     * Gets the value of the mimeTypeQualifierCode property.
     * 
     * @return possible object is {@link String }
     */
    public String getMimeTypeQualifierCode() {
        return mimeTypeQualifierCode;
    }

    /**
     * Sets the value of the mimeTypeQualifierCode property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setMimeTypeQualifierCode(String value) {
        this.mimeTypeQualifierCode = value;
    }

    /**
     * Gets the value of the uniformResourceIdentifier property.
     * 
     * @return possible object is {@link String }
     */
    public String getUniformResourceIdentifier() {
        return uniformResourceIdentifier;
    }

    /**
     * Sets the value of the uniformResourceIdentifier property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setUniformResourceIdentifier(String value) {
        this.uniformResourceIdentifier = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return possible object is {@link String }
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the languageCode property.
     * 
     * @return possible object is {@link String }
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Sets the value of the languageCode property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setLanguageCode(String value) {
        this.languageCode = value;
    }

}
