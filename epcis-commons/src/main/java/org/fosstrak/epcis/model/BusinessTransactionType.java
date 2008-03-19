package org.accada.epcis.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>
 * Java class for BusinessTransactionType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;BusinessTransactionType&quot;&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base=&quot;&lt;urn:epcglobal:epcis:xsd:1&gt;BusinessTransactionIDType&quot;&gt;
 *       &lt;attribute name=&quot;type&quot; type=&quot;{urn:epcglobal:epcis:xsd:1}BusinessTransactionTypeIDType&quot; /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BusinessTransactionType", namespace = "urn:epcglobal:epcis:xsd:1", propOrder = { "value" })
public class BusinessTransactionType {

    @XmlValue
    protected String value;
    @XmlAttribute
    protected String type;

    /**
     * Gets the value of the value property.
     * 
     * @return possible object is {@link String }
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setValue(String value) {
        this.value = value;
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

}
