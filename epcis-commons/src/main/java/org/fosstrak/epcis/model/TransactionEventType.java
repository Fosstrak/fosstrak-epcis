package org.accada.epcis.model;

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
 * Transaction Event describes the association or disassociation of physical
 * objects to one or more business transactions.
 * <p>
 * Java class for TransactionEventType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;TransactionEventType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{urn:epcglobal:epcis:xsd:1}EPCISEventType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;bizTransactionList&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}BusinessTransactionListType&quot;/&gt;
 *         &lt;element name=&quot;parentID&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}ParentIDType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;epcList&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}EPCListType&quot;/&gt;
 *         &lt;element name=&quot;action&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}ActionType&quot;/&gt;
 *         &lt;element name=&quot;bizStep&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}BusinessStepIDType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;disposition&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}DispositionIDType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;readPoint&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}ReadPointType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;bizLocation&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}BusinessLocationType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;extension&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}TransactionEventExtensionType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;any/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlRootElement(name = "TransactionEvent", namespace = "")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionEventType", namespace = "urn:epcglobal:epcis:xsd:1", propOrder = {
        "bizTransactionList", "parentID", "epcList", "action", "bizStep", "disposition", "readPoint", "bizLocation",
        "extension", "any" })
public class TransactionEventType extends EPCISEventType {

    @XmlElement(required = true)
    protected BusinessTransactionListType bizTransactionList;
    protected String parentID;
    @XmlElement(required = true)
    protected EPCListType epcList;
    @XmlElement(required = true)
    protected ActionType action;
    protected String bizStep;
    protected String disposition;
    protected ReadPointType readPoint;
    protected BusinessLocationType bizLocation;
    protected TransactionEventExtensionType extension;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

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
     * Gets the value of the epcList property.
     * 
     * @return possible object is {@link EPCListType }
     */
    public EPCListType getEpcList() {
        return epcList;
    }

    /**
     * Sets the value of the epcList property.
     * 
     * @param value
     *            allowed object is {@link EPCListType }
     */
    public void setEpcList(EPCListType value) {
        this.epcList = value;
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
     * Gets the value of the extension property.
     * 
     * @return possible object is {@link TransactionEventExtensionType }
     */
    public TransactionEventExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *            allowed object is {@link TransactionEventExtensionType }
     */
    public void setExtension(TransactionEventExtensionType value) {
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
