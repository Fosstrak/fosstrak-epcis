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
 * MasterData specific body that contains Vocabularies.
 */
public class EPCISMasterDataBodyType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.accada.epcis.soapapi.VocabularyType[] vocabularyList;

    private org.accada.epcis.soapapi.EPCISMasterDataBodyExtensionType extension;

    private org.apache.axis.message.MessageElement [] _any;

    public EPCISMasterDataBodyType() {
    }

    public EPCISMasterDataBodyType(
           org.accada.epcis.soapapi.VocabularyType[] vocabularyList,
           org.accada.epcis.soapapi.EPCISMasterDataBodyExtensionType extension,
           org.apache.axis.message.MessageElement [] _any) {
           this.vocabularyList = vocabularyList;
           this.extension = extension;
           this._any = _any;
    }


    /**
     * Gets the vocabularyList value for this EPCISMasterDataBodyType.
     * 
     * @return vocabularyList
     */
    public org.accada.epcis.soapapi.VocabularyType[] getVocabularyList() {
        return vocabularyList;
    }


    /**
     * Sets the vocabularyList value for this EPCISMasterDataBodyType.
     * 
     * @param vocabularyList
     */
    public void setVocabularyList(org.accada.epcis.soapapi.VocabularyType[] vocabularyList) {
        this.vocabularyList = vocabularyList;
    }


    /**
     * Gets the extension value for this EPCISMasterDataBodyType.
     * 
     * @return extension
     */
    public org.accada.epcis.soapapi.EPCISMasterDataBodyExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this EPCISMasterDataBodyType.
     * 
     * @param extension
     */
    public void setExtension(org.accada.epcis.soapapi.EPCISMasterDataBodyExtensionType extension) {
        this.extension = extension;
    }


    /**
     * Gets the _any value for this EPCISMasterDataBodyType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this EPCISMasterDataBodyType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EPCISMasterDataBodyType)) return false;
        EPCISMasterDataBodyType other = (EPCISMasterDataBodyType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.vocabularyList==null && other.getVocabularyList()==null) || 
             (this.vocabularyList!=null &&
              java.util.Arrays.equals(this.vocabularyList, other.getVocabularyList()))) &&
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
        if (getVocabularyList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVocabularyList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVocabularyList(), i);
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
        new org.apache.axis.description.TypeDesc(EPCISMasterDataBodyType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "EPCISMasterDataBodyType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vocabularyList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VocabularyList"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "VocabularyType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "Vocabulary"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "EPCISMasterDataBodyExtensionType"));
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
