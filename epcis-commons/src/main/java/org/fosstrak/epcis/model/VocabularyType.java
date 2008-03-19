package org.accada.epcis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * <p>
 * Java class for VocabularyType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;VocabularyType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;VocabularyElementList&quot; type=&quot;{urn:epcglobal:epcis-masterdata:xsd:1}VocabularyElementListType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;extension&quot; type=&quot;{urn:epcglobal:epcis-masterdata:xsd:1}VocabularyExtensionType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;any/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name=&quot;type&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}anyURI&quot; /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VocabularyType", namespace = "urn:epcglobal:epcis-masterdata:xsd:1", propOrder = {
        "vocabularyElementList", "extension", "any" })
public class VocabularyType {

    @XmlElement(name = "VocabularyElementList")
    protected VocabularyElementListType vocabularyElementList;
    protected VocabularyExtensionType extension;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(required = true)
    protected String type;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the vocabularyElementList property.
     * 
     * @return possible object is {@link VocabularyElementListType }
     */
    public VocabularyElementListType getVocabularyElementList() {
        return vocabularyElementList;
    }

    /**
     * Sets the value of the vocabularyElementList property.
     * 
     * @param value
     *            allowed object is {@link VocabularyElementListType }
     */
    public void setVocabularyElementList(VocabularyElementListType value) {
        this.vocabularyElementList = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return possible object is {@link VocabularyExtensionType }
     */
    public VocabularyExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *            allowed object is {@link VocabularyExtensionType }
     */
    public void setExtension(VocabularyExtensionType value) {
        this.extension = value;
    }

    /**
     * Gets the value of the any property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the any property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getAny().add(newItem);
     * </pre>
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Element }
     * {@link Object }
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
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
     * Gets a map that contains attributes that aren't bound to any typed
     * property on this class.
     * <p>
     * the map is keyed by the name of the attribute and the value is the string
     * value of the attribute. the map returned by this method is live, and you
     * can add new attribute by updating the map directly. Because of this
     * design, there's no setter.
     * 
     * @return always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
