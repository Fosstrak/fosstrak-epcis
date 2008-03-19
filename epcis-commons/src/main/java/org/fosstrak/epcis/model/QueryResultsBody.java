package org.accada.epcis.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for QueryResultsBody complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;QueryResultsBody&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;choice&gt;
 *         &lt;element name=&quot;EventList&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}EventListType&quot;/&gt;
 *         &lt;element name=&quot;VocabularyList&quot; type=&quot;{urn:epcglobal:epcis-masterdata:xsd:1}VocabularyListType&quot;/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryResultsBody", propOrder = { "eventList", "vocabularyList" })
public class QueryResultsBody {

    @XmlElement(name = "EventList")
    protected EventListType eventList;
    @XmlElement(name = "VocabularyList")
    protected VocabularyListType vocabularyList;

    /**
     * Gets the value of the eventList property.
     * 
     * @return possible object is {@link EventListType }
     */
    public EventListType getEventList() {
        return eventList;
    }

    /**
     * Sets the value of the eventList property.
     * 
     * @param value
     *            allowed object is {@link EventListType }
     */
    public void setEventList(EventListType value) {
        this.eventList = value;
    }

    /**
     * Gets the value of the vocabularyList property.
     * 
     * @return possible object is {@link VocabularyListType }
     */
    public VocabularyListType getVocabularyList() {
        return vocabularyList;
    }

    /**
     * Sets the value of the vocabularyList property.
     * 
     * @param value
     *            allowed object is {@link VocabularyListType }
     */
    public void setVocabularyList(VocabularyListType value) {
        this.vocabularyList = value;
    }

}
