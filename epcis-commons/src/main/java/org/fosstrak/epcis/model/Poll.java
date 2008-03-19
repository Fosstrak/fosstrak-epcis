package org.accada.epcis.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Poll complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;Poll&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;queryName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;params&quot; type=&quot;{urn:epcglobal:epcis-query:xsd:1}QueryParams&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Poll", propOrder = { "queryName", "params" })
public class Poll {

    @XmlElement(required = true)
    protected String queryName;
    @XmlElement(required = true)
    protected QueryParams params;

    /**
     * Gets the value of the queryName property.
     * 
     * @return possible object is {@link String }
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * Sets the value of the queryName property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setQueryName(String value) {
        this.queryName = value;
    }

    /**
     * Gets the value of the params property.
     * 
     * @return possible object is {@link QueryParams }
     */
    public QueryParams getParams() {
        return params;
    }

    /**
     * Sets the value of the params property.
     * 
     * @param value
     *            allowed object is {@link QueryParams }
     */
    public void setParams(QueryParams value) {
        this.params = value;
    }

}
