package org.accada.epcis.model;

import javax.xml.bind.annotation.XmlEnum;

/**
 * <p>
 * Java class for ActionType.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name=&quot;ActionType&quot;&gt;
 *   &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;&gt;
 *     &lt;enumeration value=&quot;ADD&quot;/&gt;
 *     &lt;enumeration value=&quot;OBSERVE&quot;/&gt;
 *     &lt;enumeration value=&quot;DELETE&quot;/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 */
@XmlEnum
public enum ActionType {

    ADD, DELETE, OBSERVE;

    public String value() {
        return name();
    }

    public static ActionType fromValue(String v) {
        return valueOf(v);
    }

}
