package org.fosstrak.epcis.model;

import javax.xml.bind.annotation.XmlEnum;

/**
 * <p>
 * Java class for ImplementationExceptionSeverity.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name=&quot;ImplementationExceptionSeverity&quot;&gt;
 *   &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}NCName&quot;&gt;
 *     &lt;enumeration value=&quot;ERROR&quot;/&gt;
 *     &lt;enumeration value=&quot;SEVERE&quot;/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 */
@XmlEnum
public enum ImplementationExceptionSeverity {

    ERROR, SEVERE;

    public String value() {
        return name();
    }

    public static ImplementationExceptionSeverity fromValue(String v) {
        return valueOf(v);
    }

}
