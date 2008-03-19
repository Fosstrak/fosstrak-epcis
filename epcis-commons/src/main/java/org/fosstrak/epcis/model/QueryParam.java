package org.accada.epcis.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for QueryParam complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;QueryParam&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;name&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;value&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryParam", propOrder = { "name", "value" })
public class QueryParam implements Serializable {

    private static final long serialVersionUID = 5307098446483898350L;

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected Object value;

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return possible object is {@link Object }
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *            allowed object is {@link Object }
     */
    public void setValue(Object value) {
        this.value = value;
    }

}
