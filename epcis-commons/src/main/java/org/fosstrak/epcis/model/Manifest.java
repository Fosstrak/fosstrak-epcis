package org.accada.epcis.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Manifest complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;Manifest&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;NumberOfItems&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}integer&quot;/&gt;
 *         &lt;element name=&quot;ManifestItem&quot; type=&quot;{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}ManifestItem&quot; maxOccurs=&quot;unbounded&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Manifest", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", propOrder = {
        "numberOfItems", "manifestItem" })
public class Manifest {

    @XmlElement(name = "NumberOfItems", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
    protected BigInteger numberOfItems;
    @XmlElement(name = "ManifestItem", namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", required = true)
    protected List<ManifestItem> manifestItem;

    /**
     * Gets the value of the numberOfItems property.
     * 
     * @return possible object is {@link BigInteger }
     */
    public BigInteger getNumberOfItems() {
        return numberOfItems;
    }

    /**
     * Sets the value of the numberOfItems property.
     * 
     * @param value
     *            allowed object is {@link BigInteger }
     */
    public void setNumberOfItems(BigInteger value) {
        this.numberOfItems = value;
    }

    /**
     * Gets the value of the manifestItem property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the manifestItem property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getManifestItem().add(newItem);
     * </pre>
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManifestItem }
     */
    public List<ManifestItem> getManifestItem() {
        if (manifestItem == null) {
            manifestItem = new ArrayList<ManifestItem>();
        }
        return this.manifestItem;
    }

}
