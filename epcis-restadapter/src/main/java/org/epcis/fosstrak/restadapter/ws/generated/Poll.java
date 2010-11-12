package org.epcis.fosstrak.restadapter.ws.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for Poll complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Poll">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="queryName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="params" type="{urn:epcglobal:epcis-query:xsd:1}QueryParams"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "Poll", namespace = "")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Poll", propOrder = {
    "queryName",
    "params"
})
public class Poll {

    @XmlElement(required = true)
    protected String queryName;
    @XmlElement(required = true)
    protected QueryParams params;

    /**
     * Gets the value of the queryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * Sets the value of the queryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryName(String value) {
        this.queryName = value;
    }

    /**
     * Gets the value of the params property.
     * 
     * @return
     *     possible object is
     *     {@link QueryParams }
     *     
     */
    public QueryParams getParams() {
        return params;
    }

    /**
     * Sets the value of the params property.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryParams }
     *     
     */
    public void setParams(QueryParams value) {
        this.params = value;
    }
}
