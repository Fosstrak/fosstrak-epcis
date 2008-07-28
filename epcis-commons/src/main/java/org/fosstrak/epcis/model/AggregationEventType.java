package org.fosstrak.epcis.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Element;

/**
 * Aggregation Event captures an event that applies to objects that have a
 * physical association with one another.
 * <p>
 * Java class for AggregationEventType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;AggregationEventType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{urn:epcglobal:epcis:xsd:1}EPCISEventType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;parentID&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}ParentIDType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;childEPCs&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}EPCListType&quot;/&gt;
 *         &lt;element name=&quot;action&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}ActionType&quot;/&gt;
 *         &lt;element name=&quot;bizStep&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}BusinessStepIDType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;disposition&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}DispositionIDType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;readPoint&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}ReadPointType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;bizLocation&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}BusinessLocationType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;bizTransactionList&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}BusinessTransactionListType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;extension&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}AggregationEventExtensionType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;any/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlRootElement(name = "AggregationEvent", namespace = "")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AggregationEventType", namespace = "urn:epcglobal:epcis:xsd:1", propOrder = {
        "parentID", "childEPCs", "action", "bizStep", "disposition", "readPoint", "bizLocation", "bizTransactionList",
        "extension", "any" })
public class AggregationEventType extends EPCISEventType {

    protected String parentID;
    @XmlElement(required = true)
    protected EPCListType childEPCs;
    @XmlElement(required = true)
    protected ActionType action;
    protected String bizStep;
    protected String disposition;
    protected ReadPointType readPoint;
    protected BusinessLocationType bizLocation;
    protected BusinessTransactionListType bizTransactionList;
    protected AggregationEventExtensionType extension;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the parentID property.
     * 
     * @return possible object is {@link String }
     */
    public String getParentID() {
        return parentID;
    }

    /**
     * Sets the value of the parentID property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setParentID(String value) {
        this.parentID = value;
    }

    /**
     * Gets the value of the childEPCs property.
     * 
     * @return possible object is {@link EPCListType }
     */
    public EPCListType getChildEPCs() {
        return childEPCs;
    }

    /**
     * Sets the value of the childEPCs property.
     * 
     * @param value
     *            allowed object is {@link EPCListType }
     */
    public void setChildEPCs(EPCListType value) {
        this.childEPCs = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return possible object is {@link ActionType }
     */
    public ActionType getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *            allowed object is {@link ActionType }
     */
    public void setAction(ActionType value) {
        this.action = value;
    }

    /**
     * Gets the value of the bizStep property.
     * 
     * @return possible object is {@link String }
     */
    public String getBizStep() {
        return bizStep;
    }

    /**
     * Sets the value of the bizStep property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setBizStep(String value) {
        this.bizStep = value;
    }

    /**
     * Gets the value of the disposition property.
     * 
     * @return possible object is {@link String }
     */
    public String getDisposition() {
        return disposition;
    }

    /**
     * Sets the value of the disposition property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setDisposition(String value) {
        this.disposition = value;
    }

    /**
     * Gets the value of the readPoint property.
     * 
     * @return possible object is {@link ReadPointType }
     */
    public ReadPointType getReadPoint() {
        return readPoint;
    }

    /**
     * Sets the value of the readPoint property.
     * 
     * @param value
     *            allowed object is {@link ReadPointType }
     */
    public void setReadPoint(ReadPointType value) {
        this.readPoint = value;
    }

    /**
     * Gets the value of the bizLocation property.
     * 
     * @return possible object is {@link BusinessLocationType }
     */
    public BusinessLocationType getBizLocation() {
        return bizLocation;
    }

    /**
     * Sets the value of the bizLocation property.
     * 
     * @param value
     *            allowed object is {@link BusinessLocationType }
     */
    public void setBizLocation(BusinessLocationType value) {
        this.bizLocation = value;
    }

    /**
     * Gets the value of the bizTransactionList property.
     * 
     * @return possible object is {@link BusinessTransactionListType }
     */
    public BusinessTransactionListType getBizTransactionList() {
        return bizTransactionList;
    }

    /**
     * Sets the value of the bizTransactionList property.
     * 
     * @param value
     *            allowed object is {@link BusinessTransactionListType }
     */
    public void setBizTransactionList(BusinessTransactionListType value) {
        this.bizTransactionList = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return possible object is {@link AggregationEventExtensionType }
     */
    public AggregationEventExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *            allowed object is {@link AggregationEventExtensionType }
     */
    public void setExtension(AggregationEventExtensionType value) {
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

}
