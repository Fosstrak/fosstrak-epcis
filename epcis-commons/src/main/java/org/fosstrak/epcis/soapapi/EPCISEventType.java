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
 * base type for all EPCIS events.
 */
public abstract class EPCISEventType  implements java.io.Serializable {
    private java.util.Calendar eventTime;

    private java.util.Calendar recordTime;

    private java.lang.String eventTimeZoneOffset;

    private org.accada.epcis.soapapi.EPCISEventExtensionType baseExtension;

    public EPCISEventType() {
    }

    public EPCISEventType(
           java.util.Calendar eventTime,
           java.util.Calendar recordTime,
           java.lang.String eventTimeZoneOffset,
           org.accada.epcis.soapapi.EPCISEventExtensionType baseExtension) {
           this.eventTime = eventTime;
           this.recordTime = recordTime;
           this.eventTimeZoneOffset = eventTimeZoneOffset;
           this.baseExtension = baseExtension;
    }


    /**
     * Gets the eventTime value for this EPCISEventType.
     * 
     * @return eventTime
     */
    public java.util.Calendar getEventTime() {
        return eventTime;
    }


    /**
     * Sets the eventTime value for this EPCISEventType.
     * 
     * @param eventTime
     */
    public void setEventTime(java.util.Calendar eventTime) {
        this.eventTime = eventTime;
    }


    /**
     * Gets the recordTime value for this EPCISEventType.
     * 
     * @return recordTime
     */
    public java.util.Calendar getRecordTime() {
        return recordTime;
    }


    /**
     * Sets the recordTime value for this EPCISEventType.
     * 
     * @param recordTime
     */
    public void setRecordTime(java.util.Calendar recordTime) {
        this.recordTime = recordTime;
    }


    /**
     * Gets the eventTimeZoneOffset value for this EPCISEventType.
     * 
     * @return eventTimeZoneOffset
     */
    public java.lang.String getEventTimeZoneOffset() {
        return eventTimeZoneOffset;
    }


    /**
     * Sets the eventTimeZoneOffset value for this EPCISEventType.
     * 
     * @param eventTimeZoneOffset
     */
    public void setEventTimeZoneOffset(java.lang.String eventTimeZoneOffset) {
        this.eventTimeZoneOffset = eventTimeZoneOffset;
    }


    /**
     * Gets the baseExtension value for this EPCISEventType.
     * 
     * @return baseExtension
     */
    public org.accada.epcis.soapapi.EPCISEventExtensionType getBaseExtension() {
        return baseExtension;
    }


    /**
     * Sets the baseExtension value for this EPCISEventType.
     * 
     * @param baseExtension
     */
    public void setBaseExtension(org.accada.epcis.soapapi.EPCISEventExtensionType baseExtension) {
        this.baseExtension = baseExtension;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EPCISEventType)) return false;
        EPCISEventType other = (EPCISEventType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eventTime==null && other.getEventTime()==null) || 
             (this.eventTime!=null &&
              this.eventTime.equals(other.getEventTime()))) &&
            ((this.recordTime==null && other.getRecordTime()==null) || 
             (this.recordTime!=null &&
              this.recordTime.equals(other.getRecordTime()))) &&
            ((this.eventTimeZoneOffset==null && other.getEventTimeZoneOffset()==null) || 
             (this.eventTimeZoneOffset!=null &&
              this.eventTimeZoneOffset.equals(other.getEventTimeZoneOffset()))) &&
            ((this.baseExtension==null && other.getBaseExtension()==null) || 
             (this.baseExtension!=null &&
              this.baseExtension.equals(other.getBaseExtension())));
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
        if (getEventTime() != null) {
            _hashCode += getEventTime().hashCode();
        }
        if (getRecordTime() != null) {
            _hashCode += getRecordTime().hashCode();
        }
        if (getEventTimeZoneOffset() != null) {
            _hashCode += getEventTimeZoneOffset().hashCode();
        }
        if (getBaseExtension() != null) {
            _hashCode += getBaseExtension().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EPCISEventType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISEventType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "eventTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "recordTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventTimeZoneOffset");
        elemField.setXmlName(new javax.xml.namespace.QName("", "eventTimeZoneOffset"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseExtension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "baseExtension"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISEventExtensionType"));
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
