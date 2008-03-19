package org.accada.epcis.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for VocabularyListType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;VocabularyListType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;Vocabulary&quot; type=&quot;{urn:epcglobal:epcis-masterdata:xsd:1}VocabularyType&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VocabularyListType", namespace = "urn:epcglobal:epcis-masterdata:xsd:1", propOrder = { "vocabulary" })
public class VocabularyListType {

    @XmlElement(name = "Vocabulary", required = true)
    protected List<VocabularyType> vocabulary;

    /**
     * Gets the value of the vocabulary property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the vocabulary property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getVocabulary().add(newItem);
     * </pre>
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VocabularyType }
     */
    public List<VocabularyType> getVocabulary() {
        if (vocabulary == null) {
            vocabulary = new ArrayList<VocabularyType>();
        }
        return this.vocabulary;
    }

}
