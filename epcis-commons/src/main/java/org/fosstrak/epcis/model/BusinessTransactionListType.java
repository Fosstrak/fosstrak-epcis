package org.accada.epcis.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for BusinessTransactionListType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;BusinessTransactionListType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;bizTransaction&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}BusinessTransactionType&quot; maxOccurs=&quot;unbounded&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BusinessTransactionListType", namespace = "urn:epcglobal:epcis:xsd:1", propOrder = { "bizTransaction" })
public class BusinessTransactionListType {

    @XmlElement(required = true)
    protected List<BusinessTransactionType> bizTransaction;

    /**
     * Gets the value of the bizTransaction property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the bizTransaction property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getBizTransaction().add(newItem);
     * </pre>
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BusinessTransactionType }
     */
    public List<BusinessTransactionType> getBizTransaction() {
        if (bizTransaction == null) {
            bizTransaction = new ArrayList<BusinessTransactionType>();
        }
        return this.bizTransaction;
    }

}
