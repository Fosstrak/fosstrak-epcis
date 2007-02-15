/**
 * EPCISServiceBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.accada.epcis.soapapi;

public class EPCISServiceBindingSkeleton implements org.accada.epcis.soapapi.EPCISServicePortType, org.apache.axis.wsdl.Skeleton {
    private org.accada.epcis.soapapi.EPCISServicePortType impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetQueryNames"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "EmptyParms"), org.accada.epcis.soapapi.EmptyParms.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getQueryNames", _params, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetQueryNamesResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ArrayOfString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getQueryNames"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getQueryNames") == null) {
            _myOperations.put("getQueryNames", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getQueryNames")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _fault.setClassName("org.accada.epcis.soapapi.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Subscribe"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Subscribe"), org.accada.epcis.soapapi.Subscribe.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("subscribe", _params, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscribeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "VoidHolder"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "subscribe"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("subscribe") == null) {
            _myOperations.put("subscribe", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("subscribe")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("QueryTooComplexExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooComplexException"));
        _fault.setClassName("org.accada.epcis.soapapi.QueryTooComplexException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooComplexException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("InvalidURIExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "InvalidURIException"));
        _fault.setClassName("org.accada.epcis.soapapi.InvalidURIException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "InvalidURIException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SubscribeNotPermittedExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscribeNotPermittedException"));
        _fault.setClassName("org.accada.epcis.soapapi.SubscribeNotPermittedException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscribeNotPermittedException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SubscriptionControlsExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscriptionControlsException"));
        _fault.setClassName("org.accada.epcis.soapapi.SubscriptionControlsException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscriptionControlsException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("QueryParameterExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryParameterException"));
        _fault.setClassName("org.accada.epcis.soapapi.QueryParameterException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryParameterException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _fault.setClassName("org.accada.epcis.soapapi.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("DuplicateSubscriptionExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "DuplicateSubscriptionException"));
        _fault.setClassName("org.accada.epcis.soapapi.DuplicateSubscriptionException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "DuplicateSubscriptionException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("NoSuchNameExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException"));
        _fault.setClassName("org.accada.epcis.soapapi.NoSuchNameException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Unsubscribe"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Unsubscribe"), org.accada.epcis.soapapi.Unsubscribe.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("unsubscribe", _params, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "UnsubscribeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "VoidHolder"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "unsubscribe"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("unsubscribe") == null) {
            _myOperations.put("unsubscribe", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("unsubscribe")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("NoSuchSubscriptionExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchSubscriptionException"));
        _fault.setClassName("org.accada.epcis.soapapi.NoSuchSubscriptionException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchSubscriptionException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _fault.setClassName("org.accada.epcis.soapapi.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetSubscriptionIDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetSubscriptionIDs"), org.accada.epcis.soapapi.GetSubscriptionIDs.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getSubscriptionIDs", _params, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetSubscriptionIDsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ArrayOfString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getSubscriptionIDs"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getSubscriptionIDs") == null) {
            _myOperations.put("getSubscriptionIDs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getSubscriptionIDs")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _fault.setClassName("org.accada.epcis.soapapi.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("NoSuchNameExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException"));
        _fault.setClassName("org.accada.epcis.soapapi.NoSuchNameException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Poll"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Poll"), org.accada.epcis.soapapi.Poll.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("poll", _params, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryResults"));
        _oper.setReturnType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryResults"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "poll"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("poll") == null) {
            _myOperations.put("poll", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("poll")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("QueryTooComplexExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooComplexException"));
        _fault.setClassName("org.accada.epcis.soapapi.QueryTooComplexException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooComplexException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("QueryTooLargeExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooLargeException"));
        _fault.setClassName("org.accada.epcis.soapapi.QueryTooLargeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooLargeException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("QueryParameterExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryParameterException"));
        _fault.setClassName("org.accada.epcis.soapapi.QueryParameterException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryParameterException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _fault.setClassName("org.accada.epcis.soapapi.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("NoSuchNameExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException"));
        _fault.setClassName("org.accada.epcis.soapapi.NoSuchNameException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetStandardVersion"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "EmptyParms"), org.accada.epcis.soapapi.EmptyParms.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getStandardVersion", _params, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetStandardVersionResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getStandardVersion"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getStandardVersion") == null) {
            _myOperations.put("getStandardVersion", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getStandardVersion")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _fault.setClassName("org.accada.epcis.soapapi.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetVendorVersion"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "EmptyParms"), org.accada.epcis.soapapi.EmptyParms.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getVendorVersion", _params, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetVendorVersionResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getVendorVersion"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("getVendorVersion") == null) {
            _myOperations.put("getVendorVersion", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getVendorVersion")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _fault.setClassName("org.accada.epcis.soapapi.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _fault.setClassName("org.accada.epcis.soapapi.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"));
        _oper.addFault(_fault);
    }

    public EPCISServiceBindingSkeleton() {
        this.impl = new org.accada.epcis.repository.QueryOperationsModule();
    }

    public EPCISServiceBindingSkeleton(org.accada.epcis.soapapi.EPCISServicePortType impl) {
        this.impl = impl;
    }
    public org.accada.epcis.soapapi.ArrayOfString getQueryNames(org.accada.epcis.soapapi.EmptyParms parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException
    {
        org.accada.epcis.soapapi.ArrayOfString ret = impl.getQueryNames(parms);
        return ret;
    }

    public org.accada.epcis.soapapi.VoidHolder subscribe(org.accada.epcis.soapapi.Subscribe parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.QueryTooComplexException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.InvalidURIException, org.accada.epcis.soapapi.SubscribeNotPermittedException, org.accada.epcis.soapapi.SubscriptionControlsException, org.accada.epcis.soapapi.QueryParameterException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException, org.accada.epcis.soapapi.DuplicateSubscriptionException, org.accada.epcis.soapapi.NoSuchNameException
    {
        org.accada.epcis.soapapi.VoidHolder ret = impl.subscribe(parms);
        return ret;
    }

    public org.accada.epcis.soapapi.VoidHolder unsubscribe(org.accada.epcis.soapapi.Unsubscribe parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.NoSuchSubscriptionException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException
    {
        org.accada.epcis.soapapi.VoidHolder ret = impl.unsubscribe(parms);
        return ret;
    }

    public org.accada.epcis.soapapi.ArrayOfString getSubscriptionIDs(org.accada.epcis.soapapi.GetSubscriptionIDs parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException, org.accada.epcis.soapapi.NoSuchNameException
    {
        org.accada.epcis.soapapi.ArrayOfString ret = impl.getSubscriptionIDs(parms);
        return ret;
    }

    public org.accada.epcis.soapapi.QueryResults poll(org.accada.epcis.soapapi.Poll parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.QueryTooComplexException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.QueryTooLargeException, org.accada.epcis.soapapi.QueryParameterException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException, org.accada.epcis.soapapi.NoSuchNameException
    {
        org.accada.epcis.soapapi.QueryResults ret = impl.poll(parms);
        return ret;
    }

    public java.lang.String getStandardVersion(org.accada.epcis.soapapi.EmptyParms parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException
    {
        java.lang.String ret = impl.getStandardVersion(parms);
        return ret;
    }

    public java.lang.String getVendorVersion(org.accada.epcis.soapapi.EmptyParms parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException
    {
        java.lang.String ret = impl.getVendorVersion(parms);
        return ret;
    }

}
