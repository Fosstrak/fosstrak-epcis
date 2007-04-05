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

public class EventListType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.accada.epcis.soapapi.ObjectEventType[] objectEvent;

    private org.accada.epcis.soapapi.AggregationEventType[] aggregationEvent;

    private org.accada.epcis.soapapi.QuantityEventType[] quantityEvent;

    private org.accada.epcis.soapapi.TransactionEventType[] transactionEvent;

    private org.accada.epcis.soapapi.EPCISEventListExtensionType extension;

    private org.apache.axis.message.MessageElement [] _any;

    public EventListType() {
    }

    public EventListType(
           org.accada.epcis.soapapi.ObjectEventType[] objectEvent,
           org.accada.epcis.soapapi.AggregationEventType[] aggregationEvent,
           org.accada.epcis.soapapi.QuantityEventType[] quantityEvent,
           org.accada.epcis.soapapi.TransactionEventType[] transactionEvent,
           org.accada.epcis.soapapi.EPCISEventListExtensionType extension,
           org.apache.axis.message.MessageElement [] _any) {
           this.objectEvent = objectEvent;
           this.aggregationEvent = aggregationEvent;
           this.quantityEvent = quantityEvent;
           this.transactionEvent = transactionEvent;
           this.extension = extension;
           this._any = _any;
    }


    /**
     * Gets the objectEvent value for this EventListType.
     * 
     * @return objectEvent
     */
    public org.accada.epcis.soapapi.ObjectEventType[] getObjectEvent() {
        return objectEvent;
    }


    /**
     * Sets the objectEvent value for this EventListType.
     * 
     * @param objectEvent
     */
    public void setObjectEvent(org.accada.epcis.soapapi.ObjectEventType[] objectEvent) {
        this.objectEvent = objectEvent;
    }

    public org.accada.epcis.soapapi.ObjectEventType getObjectEvent(int i) {
        return this.objectEvent[i];
    }

    public void setObjectEvent(int i, org.accada.epcis.soapapi.ObjectEventType _value) {
        this.objectEvent[i] = _value;
    }


    /**
     * Gets the aggregationEvent value for this EventListType.
     * 
     * @return aggregationEvent
     */
    public org.accada.epcis.soapapi.AggregationEventType[] getAggregationEvent() {
        return aggregationEvent;
    }


    /**
     * Sets the aggregationEvent value for this EventListType.
     * 
     * @param aggregationEvent
     */
    public void setAggregationEvent(org.accada.epcis.soapapi.AggregationEventType[] aggregationEvent) {
        this.aggregationEvent = aggregationEvent;
    }

    public org.accada.epcis.soapapi.AggregationEventType getAggregationEvent(int i) {
        return this.aggregationEvent[i];
    }

    public void setAggregationEvent(int i, org.accada.epcis.soapapi.AggregationEventType _value) {
        this.aggregationEvent[i] = _value;
    }


    /**
     * Gets the quantityEvent value for this EventListType.
     * 
     * @return quantityEvent
     */
    public org.accada.epcis.soapapi.QuantityEventType[] getQuantityEvent() {
        return quantityEvent;
    }


    /**
     * Sets the quantityEvent value for this EventListType.
     * 
     * @param quantityEvent
     */
    public void setQuantityEvent(org.accada.epcis.soapapi.QuantityEventType[] quantityEvent) {
        this.quantityEvent = quantityEvent;
    }

    public org.accada.epcis.soapapi.QuantityEventType getQuantityEvent(int i) {
        return this.quantityEvent[i];
    }

    public void setQuantityEvent(int i, org.accada.epcis.soapapi.QuantityEventType _value) {
        this.quantityEvent[i] = _value;
    }


    /**
     * Gets the transactionEvent value for this EventListType.
     * 
     * @return transactionEvent
     */
    public org.accada.epcis.soapapi.TransactionEventType[] getTransactionEvent() {
        return transactionEvent;
    }


    /**
     * Sets the transactionEvent value for this EventListType.
     * 
     * @param transactionEvent
     */
    public void setTransactionEvent(org.accada.epcis.soapapi.TransactionEventType[] transactionEvent) {
        this.transactionEvent = transactionEvent;
    }

    public org.accada.epcis.soapapi.TransactionEventType getTransactionEvent(int i) {
        return this.transactionEvent[i];
    }

    public void setTransactionEvent(int i, org.accada.epcis.soapapi.TransactionEventType _value) {
        this.transactionEvent[i] = _value;
    }


    /**
     * Gets the extension value for this EventListType.
     * 
     * @return extension
     */
    public org.accada.epcis.soapapi.EPCISEventListExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this EventListType.
     * 
     * @param extension
     */
    public void setExtension(org.accada.epcis.soapapi.EPCISEventListExtensionType extension) {
        this.extension = extension;
    }


    /**
     * Gets the _any value for this EventListType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this EventListType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EventListType)) return false;
        EventListType other = (EventListType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.objectEvent==null && other.getObjectEvent()==null) || 
             (this.objectEvent!=null &&
              java.util.Arrays.equals(this.objectEvent, other.getObjectEvent()))) &&
            ((this.aggregationEvent==null && other.getAggregationEvent()==null) || 
             (this.aggregationEvent!=null &&
              java.util.Arrays.equals(this.aggregationEvent, other.getAggregationEvent()))) &&
            ((this.quantityEvent==null && other.getQuantityEvent()==null) || 
             (this.quantityEvent!=null &&
              java.util.Arrays.equals(this.quantityEvent, other.getQuantityEvent()))) &&
            ((this.transactionEvent==null && other.getTransactionEvent()==null) || 
             (this.transactionEvent!=null &&
              java.util.Arrays.equals(this.transactionEvent, other.getTransactionEvent()))) &&
            ((this.extension==null && other.getExtension()==null) || 
             (this.extension!=null &&
              this.extension.equals(other.getExtension()))) &&
            ((this._any==null && other.get_any()==null) || 
             (this._any!=null &&
              java.util.Arrays.equals(this._any, other.get_any())));
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
        if (getObjectEvent() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getObjectEvent());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getObjectEvent(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAggregationEvent() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAggregationEvent());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAggregationEvent(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getQuantityEvent() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getQuantityEvent());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getQuantityEvent(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTransactionEvent() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTransactionEvent());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTransactionEvent(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getExtension() != null) {
            _hashCode += getExtension().hashCode();
        }
        if (get_any() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(get_any());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(get_any(), i);
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
        new org.apache.axis.description.TypeDesc(EventListType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EventListType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ObjectEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "ObjectEventType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aggregationEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "AggregationEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "AggregationEventType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantityEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "QuantityEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "QuantityEventType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "TransactionEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "TransactionEventType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISEventListExtensionType"));
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
