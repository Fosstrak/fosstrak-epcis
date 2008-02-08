package org.accada.epcis.soap;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * <p>
 * Java class for TypeOfServiceTransaction.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name=&quot;TypeOfServiceTransaction&quot;&gt;
 *   &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;&gt;
 *     &lt;enumeration value=&quot;RequestingServiceTransaction&quot;/&gt;
 *     &lt;enumeration value=&quot;RespondingServiceTransaction&quot;/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 */
@XmlEnum
public enum TypeOfServiceTransaction {

    @XmlEnumValue("RequestingServiceTransaction")
    REQUESTING_SERVICE_TRANSACTION("RequestingServiceTransaction"), @XmlEnumValue("RespondingServiceTransaction")
    RESPONDING_SERVICE_TRANSACTION("RespondingServiceTransaction");
    private final String value;

    TypeOfServiceTransaction(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeOfServiceTransaction fromValue(String v) {
        for (TypeOfServiceTransaction c : TypeOfServiceTransaction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
