package org.accada.epcis.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for QueryParameterException complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;QueryParameterException&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{urn:epcglobal:epcis-query:xsd:1}EPCISException&quot;&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryParameterException")
public class QueryParameterException extends EPCISException {

}
