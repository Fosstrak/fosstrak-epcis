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

public class ArrayOfString  implements java.io.Serializable {
    private java.lang.String[] string;

    private boolean[] doNotUseHackAroundBug;

    public ArrayOfString() {
    }

    public ArrayOfString(
           java.lang.String[] string,
           boolean[] doNotUseHackAroundBug) {
           this.string = string;
           this.doNotUseHackAroundBug = doNotUseHackAroundBug;
    }


    /**
     * Gets the string value for this ArrayOfString.
     * 
     * @return string
     */
    public java.lang.String[] getString() {
        return string;
    }


    /**
     * Sets the string value for this ArrayOfString.
     * 
     * @param string
     */
    public void setString(java.lang.String[] string) {
        this.string = string;
    }

    public java.lang.String getString(int i) {
        return this.string[i];
    }

    public void setString(int i, java.lang.String _value) {
        this.string[i] = _value;
    }


    /**
     * Gets the doNotUseHackAroundBug value for this ArrayOfString.
     * 
     * @return doNotUseHackAroundBug
     */
    public boolean[] getDoNotUseHackAroundBug() {
        return doNotUseHackAroundBug;
    }


    /**
     * Sets the doNotUseHackAroundBug value for this ArrayOfString.
     * 
     * @param doNotUseHackAroundBug
     */
    public void setDoNotUseHackAroundBug(boolean[] doNotUseHackAroundBug) {
        this.doNotUseHackAroundBug = doNotUseHackAroundBug;
    }

    public boolean getDoNotUseHackAroundBug(int i) {
        return this.doNotUseHackAroundBug[i];
    }

    public void setDoNotUseHackAroundBug(int i, boolean _value) {
        this.doNotUseHackAroundBug[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfString)) return false;
        ArrayOfString other = (ArrayOfString) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.string==null && other.getString()==null) || 
             (this.string!=null &&
              java.util.Arrays.equals(this.string, other.getString()))) &&
            ((this.doNotUseHackAroundBug==null && other.getDoNotUseHackAroundBug()==null) || 
             (this.doNotUseHackAroundBug!=null &&
              java.util.Arrays.equals(this.doNotUseHackAroundBug, other.getDoNotUseHackAroundBug())));
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
        if (getString() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getString());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getString(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDoNotUseHackAroundBug() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDoNotUseHackAroundBug());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDoNotUseHackAroundBug(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ArrayOfString.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ArrayOfString"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("string");
        elemField.setXmlName(new javax.xml.namespace.QName("", "string"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("doNotUseHackAroundBug");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DoNotUseHackAroundBug"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
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
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
