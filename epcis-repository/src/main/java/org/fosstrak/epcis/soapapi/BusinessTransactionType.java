/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Accada (www.accada.org).
 *
 * Accada is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Accada is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Accada; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.accada.epcis.soapapi;


/**
 * BusinessTransactionType.java<br>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006
 * (06:55:48 PDT) WSDL2Java emitter and has been hand edited to ensure proper
 * serialization.
 */
public class BusinessTransactionType extends org.apache.axis.types.URI
        implements java.io.Serializable, org.apache.axis.encoding.SimpleType {

    private static final long serialVersionUID = -3930773846550143369L;

    private org.apache.axis.types.URI type; // attribute

    public BusinessTransactionType() {
    }

    // Simple Types must have a String constructor
    public BusinessTransactionType(java.lang.String _value)
            throws MalformedURIException {
        super(_value);
    }

    public BusinessTransactionType(org.apache.axis.types.URI _value) {
        super(_value);
    }

    /**
     * Gets the type value for this BusinessTransactionType.
     * 
     * @return type
     */
    public org.apache.axis.types.URI getType() {
        return type;
    }

    /**
     * Sets the type value for this BusinessTransactionType.
     * 
     * @param type
     */
    public void setType(org.apache.axis.types.URI type) {
        this.type = type;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BusinessTransactionType)) return false;
        BusinessTransactionType other = (BusinessTransactionType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj)
                && ((this.type == null && other.getType() == null) || (this.type != null && this.type.equals(other.getType())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
            BusinessTransactionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName(
                "urn:epcglobal:epcis:xsd:1", "BusinessTransactionType"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("type");
        attrField.setXmlName(new javax.xml.namespace.QName("", "type"));
        attrField.setXmlType(new javax.xml.namespace.QName(
                "urn:epcglobal:epcis:xsd:1", "BusinessTransactionTypeIDType"));
        typeDesc.addFieldDesc(attrField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
            java.lang.String mechType, java.lang.Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.SimpleSerializer(_javaType,
                _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
            java.lang.String mechType, java.lang.Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.SimpleDeserializer(_javaType,
                _xmlType, typeDesc);
    }

}
