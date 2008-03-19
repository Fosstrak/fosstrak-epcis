package org.accada.epcis.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ServiceTransaction complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;ServiceTransaction&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;attribute name=&quot;IsApplicationErrorResponseRequested&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;IsAuthenticationRequired&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;IsIntegrityCheckRequired&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;IsNonRepudiationOfReceiptRequired&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;IsNonRepudiationRequired&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;Recurrence&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;TimeToAcknowledgeAcceptance&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;TimeToAcknowledgeReceipt&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;TimeToPerform&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;TypeOfServiceTransaction&quot; type=&quot;{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}TypeOfServiceTransaction&quot; /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceTransaction", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
public class ServiceTransaction {

    @XmlAttribute(name = "IsApplicationErrorResponseRequested")
    protected String isApplicationErrorResponseRequested;
    @XmlAttribute(name = "IsAuthenticationRequired")
    protected String isAuthenticationRequired;
    @XmlAttribute(name = "IsIntegrityCheckRequired")
    protected String isIntegrityCheckRequired;
    @XmlAttribute(name = "IsNonRepudiationOfReceiptRequired")
    protected String isNonRepudiationOfReceiptRequired;
    @XmlAttribute(name = "IsNonRepudiationRequired")
    protected String isNonRepudiationRequired;
    @XmlAttribute(name = "Recurrence")
    protected String recurrence;
    @XmlAttribute(name = "TimeToAcknowledgeAcceptance")
    protected String timeToAcknowledgeAcceptance;
    @XmlAttribute(name = "TimeToAcknowledgeReceipt")
    protected String timeToAcknowledgeReceipt;
    @XmlAttribute(name = "TimeToPerform")
    protected String timeToPerform;
    @XmlAttribute(name = "TypeOfServiceTransaction")
    protected TypeOfServiceTransaction typeOfServiceTransaction;

    /**
     * Gets the value of the isApplicationErrorResponseRequested property.
     * 
     * @return possible object is {@link String }
     */
    public String getIsApplicationErrorResponseRequested() {
        return isApplicationErrorResponseRequested;
    }

    /**
     * Sets the value of the isApplicationErrorResponseRequested property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setIsApplicationErrorResponseRequested(String value) {
        this.isApplicationErrorResponseRequested = value;
    }

    /**
     * Gets the value of the isAuthenticationRequired property.
     * 
     * @return possible object is {@link String }
     */
    public String getIsAuthenticationRequired() {
        return isAuthenticationRequired;
    }

    /**
     * Sets the value of the isAuthenticationRequired property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setIsAuthenticationRequired(String value) {
        this.isAuthenticationRequired = value;
    }

    /**
     * Gets the value of the isIntegrityCheckRequired property.
     * 
     * @return possible object is {@link String }
     */
    public String getIsIntegrityCheckRequired() {
        return isIntegrityCheckRequired;
    }

    /**
     * Sets the value of the isIntegrityCheckRequired property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setIsIntegrityCheckRequired(String value) {
        this.isIntegrityCheckRequired = value;
    }

    /**
     * Gets the value of the isNonRepudiationOfReceiptRequired property.
     * 
     * @return possible object is {@link String }
     */
    public String getIsNonRepudiationOfReceiptRequired() {
        return isNonRepudiationOfReceiptRequired;
    }

    /**
     * Sets the value of the isNonRepudiationOfReceiptRequired property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setIsNonRepudiationOfReceiptRequired(String value) {
        this.isNonRepudiationOfReceiptRequired = value;
    }

    /**
     * Gets the value of the isNonRepudiationRequired property.
     * 
     * @return possible object is {@link String }
     */
    public String getIsNonRepudiationRequired() {
        return isNonRepudiationRequired;
    }

    /**
     * Sets the value of the isNonRepudiationRequired property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setIsNonRepudiationRequired(String value) {
        this.isNonRepudiationRequired = value;
    }

    /**
     * Gets the value of the recurrence property.
     * 
     * @return possible object is {@link String }
     */
    public String getRecurrence() {
        return recurrence;
    }

    /**
     * Sets the value of the recurrence property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setRecurrence(String value) {
        this.recurrence = value;
    }

    /**
     * Gets the value of the timeToAcknowledgeAcceptance property.
     * 
     * @return possible object is {@link String }
     */
    public String getTimeToAcknowledgeAcceptance() {
        return timeToAcknowledgeAcceptance;
    }

    /**
     * Sets the value of the timeToAcknowledgeAcceptance property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setTimeToAcknowledgeAcceptance(String value) {
        this.timeToAcknowledgeAcceptance = value;
    }

    /**
     * Gets the value of the timeToAcknowledgeReceipt property.
     * 
     * @return possible object is {@link String }
     */
    public String getTimeToAcknowledgeReceipt() {
        return timeToAcknowledgeReceipt;
    }

    /**
     * Sets the value of the timeToAcknowledgeReceipt property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setTimeToAcknowledgeReceipt(String value) {
        this.timeToAcknowledgeReceipt = value;
    }

    /**
     * Gets the value of the timeToPerform property.
     * 
     * @return possible object is {@link String }
     */
    public String getTimeToPerform() {
        return timeToPerform;
    }

    /**
     * Sets the value of the timeToPerform property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setTimeToPerform(String value) {
        this.timeToPerform = value;
    }

    /**
     * Gets the value of the typeOfServiceTransaction property.
     * 
     * @return possible object is {@link TypeOfServiceTransaction }
     */
    public TypeOfServiceTransaction getTypeOfServiceTransaction() {
        return typeOfServiceTransaction;
    }

    /**
     * Sets the value of the typeOfServiceTransaction property.
     * 
     * @param value
     *            allowed object is {@link TypeOfServiceTransaction }
     */
    public void setTypeOfServiceTransaction(TypeOfServiceTransaction value) {
        this.typeOfServiceTransaction = value;
    }

}
