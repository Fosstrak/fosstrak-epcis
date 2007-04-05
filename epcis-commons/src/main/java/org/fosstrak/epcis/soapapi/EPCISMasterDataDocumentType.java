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
 * MasterData document that contains a Header and a Body.
 */
public class EPCISMasterDataDocumentType  extends org.accada.epcis.soapapi.Document  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.accada.epcis.soapapi.EPCISHeaderType EPCISHeader;

    private org.accada.epcis.soapapi.EPCISMasterDataBodyType EPCISBody;

    private org.accada.epcis.soapapi.EPCISMasterDataDocumentExtensionType extension;

    private org.apache.axis.message.MessageElement [] _any;

    public EPCISMasterDataDocumentType() {
    }

    public EPCISMasterDataDocumentType(
           java.math.BigDecimal schemaVersion,
           java.util.Calendar creationDate,
           org.accada.epcis.soapapi.EPCISHeaderType EPCISHeader,
           org.accada.epcis.soapapi.EPCISMasterDataBodyType EPCISBody,
           org.accada.epcis.soapapi.EPCISMasterDataDocumentExtensionType extension,
           org.apache.axis.message.MessageElement [] _any) {
        super(
            schemaVersion,
            creationDate);
        this.EPCISHeader = EPCISHeader;
        this.EPCISBody = EPCISBody;
        this.extension = extension;
        this._any = _any;
    }


    /**
     * Gets the EPCISHeader value for this EPCISMasterDataDocumentType.
     * 
     * @return EPCISHeader
     */
    public org.accada.epcis.soapapi.EPCISHeaderType getEPCISHeader() {
        return EPCISHeader;
    }


    /**
     * Sets the EPCISHeader value for this EPCISMasterDataDocumentType.
     * 
     * @param EPCISHeader
     */
    public void setEPCISHeader(org.accada.epcis.soapapi.EPCISHeaderType EPCISHeader) {
        this.EPCISHeader = EPCISHeader;
    }


    /**
     * Gets the EPCISBody value for this EPCISMasterDataDocumentType.
     * 
     * @return EPCISBody
     */
    public org.accada.epcis.soapapi.EPCISMasterDataBodyType getEPCISBody() {
        return EPCISBody;
    }


    /**
     * Sets the EPCISBody value for this EPCISMasterDataDocumentType.
     * 
     * @param EPCISBody
     */
    public void setEPCISBody(org.accada.epcis.soapapi.EPCISMasterDataBodyType EPCISBody) {
        this.EPCISBody = EPCISBody;
    }


    /**
     * Gets the extension value for this EPCISMasterDataDocumentType.
     * 
     * @return extension
     */
    public org.accada.epcis.soapapi.EPCISMasterDataDocumentExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this EPCISMasterDataDocumentType.
     * 
     * @param extension
     */
    public void setExtension(org.accada.epcis.soapapi.EPCISMasterDataDocumentExtensionType extension) {
        this.extension = extension;
    }


    /**
     * Gets the _any value for this EPCISMasterDataDocumentType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this EPCISMasterDataDocumentType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EPCISMasterDataDocumentType)) return false;
        EPCISMasterDataDocumentType other = (EPCISMasterDataDocumentType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.EPCISHeader==null && other.getEPCISHeader()==null) || 
             (this.EPCISHeader!=null &&
              this.EPCISHeader.equals(other.getEPCISHeader()))) &&
            ((this.EPCISBody==null && other.getEPCISBody()==null) || 
             (this.EPCISBody!=null &&
              this.EPCISBody.equals(other.getEPCISBody()))) &&
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
        int _hashCode = super.hashCode();
        if (getEPCISHeader() != null) {
            _hashCode += getEPCISHeader().hashCode();
        }
        if (getEPCISBody() != null) {
            _hashCode += getEPCISBody().hashCode();
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
        new org.apache.axis.description.TypeDesc(EPCISMasterDataDocumentType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "EPCISMasterDataDocumentType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EPCISHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("", "EPCISHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISHeaderType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EPCISBody");
        elemField.setXmlName(new javax.xml.namespace.QName("", "EPCISBody"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "EPCISMasterDataBodyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "EPCISMasterDataDocumentExtensionType"));
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
