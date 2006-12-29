/**
 * EPCISServiceBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.accada.epcis.soapapi;

public class EPCISServiceBindingStub extends org.apache.axis.client.Stub implements org.accada.epcis.soapapi.EPCISServicePortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[7];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getQueryNames");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetQueryNames"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "EmptyParms"), org.accada.epcis.soapapi.EmptyParms.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ArrayOfString"));
        oper.setReturnClass(org.accada.epcis.soapapi.ArrayOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetQueryNamesResult"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"),
                      "org.accada.epcis.soapapi.ImplementationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"),
                      "org.accada.epcis.soapapi.ValidationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"),
                      "org.accada.epcis.soapapi.SecurityException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("subscribe");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Subscribe"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Subscribe"), org.accada.epcis.soapapi.Subscribe.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "VoidHolder"));
        oper.setReturnClass(org.accada.epcis.soapapi.VoidHolder.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscribeResult"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooComplexException"),
                      "org.accada.epcis.soapapi.QueryTooComplexException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooComplexException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"),
                      "org.accada.epcis.soapapi.ImplementationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "InvalidURIException"),
                      "org.accada.epcis.soapapi.InvalidURIException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "InvalidURIException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscribeNotPermittedException"),
                      "org.accada.epcis.soapapi.SubscribeNotPermittedException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscribeNotPermittedException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscriptionControlsException"),
                      "org.accada.epcis.soapapi.SubscriptionControlsException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscriptionControlsException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryParameterException"),
                      "org.accada.epcis.soapapi.QueryParameterException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryParameterException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"),
                      "org.accada.epcis.soapapi.ValidationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"),
                      "org.accada.epcis.soapapi.SecurityException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "DuplicateSubscriptionException"),
                      "org.accada.epcis.soapapi.DuplicateSubscriptionException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "DuplicateSubscriptionException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException"),
                      "org.accada.epcis.soapapi.NoSuchNameException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("unsubscribe");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Unsubscribe"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Unsubscribe"), org.accada.epcis.soapapi.Unsubscribe.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "VoidHolder"));
        oper.setReturnClass(org.accada.epcis.soapapi.VoidHolder.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "UnsubscribeResult"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"),
                      "org.accada.epcis.soapapi.ImplementationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchSubscriptionException"),
                      "org.accada.epcis.soapapi.NoSuchSubscriptionException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchSubscriptionException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"),
                      "org.accada.epcis.soapapi.ValidationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"),
                      "org.accada.epcis.soapapi.SecurityException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSubscriptionIDs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetSubscriptionIDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetSubscriptionIDs"), org.accada.epcis.soapapi.GetSubscriptionIDs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ArrayOfString"));
        oper.setReturnClass(org.accada.epcis.soapapi.ArrayOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetSubscriptionIDsResult"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"),
                      "org.accada.epcis.soapapi.ImplementationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"),
                      "org.accada.epcis.soapapi.ValidationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"),
                      "org.accada.epcis.soapapi.SecurityException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException"),
                      "org.accada.epcis.soapapi.NoSuchNameException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException"), 
                      true
                     ));
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("poll");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Poll"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Poll"), org.accada.epcis.soapapi.Poll.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryResults"));
        oper.setReturnClass(org.accada.epcis.soapapi.QueryResults.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryResults"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooComplexException"),
                      "org.accada.epcis.soapapi.QueryTooComplexException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooComplexException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"),
                      "org.accada.epcis.soapapi.ImplementationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooLargeException"),
                      "org.accada.epcis.soapapi.QueryTooLargeException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooLargeException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryParameterException"),
                      "org.accada.epcis.soapapi.QueryParameterException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryParameterException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"),
                      "org.accada.epcis.soapapi.ValidationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"),
                      "org.accada.epcis.soapapi.SecurityException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException"),
                      "org.accada.epcis.soapapi.NoSuchNameException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException"), 
                      true
                     ));
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getStandardVersion");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetStandardVersion"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "EmptyParms"), org.accada.epcis.soapapi.EmptyParms.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetStandardVersionResult"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"),
                      "org.accada.epcis.soapapi.ImplementationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"),
                      "org.accada.epcis.soapapi.ValidationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"),
                      "org.accada.epcis.soapapi.SecurityException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"), 
                      true
                     ));
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVendorVersion");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetVendorVersion"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "EmptyParms"), org.accada.epcis.soapapi.EmptyParms.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetVendorVersionResult"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"),
                      "org.accada.epcis.soapapi.ImplementationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"),
                      "org.accada.epcis.soapapi.ValidationException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"),
                      "org.accada.epcis.soapapi.SecurityException",
                      new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException"), 
                      true
                     ));
        _operations[6] = oper;

    }

    public EPCISServiceBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public EPCISServiceBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public EPCISServiceBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "BusinessScope");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.Scope[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Scope");
            qName2 = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Scope");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "BusinessService");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.BusinessService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ContactInformation");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.ContactInformation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "CorrelationInformation");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.CorrelationInformation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "DocumentIdentification");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.DocumentIdentification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Language");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Manifest");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.Manifest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ManifestItem");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.ManifestItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "MimeTypeQualifier");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Partner");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.Partner.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "PartnerIdentification");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.PartnerIdentification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Scope");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.Scope.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ServiceTransaction");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.ServiceTransaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocument");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.StandardBusinessDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocumentHeader");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.StandardBusinessDocumentHeader.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "TypeOfServiceTransaction");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.TypeOfServiceTransaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "AttributeType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.AttributeType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "EPCISMasterDataBodyExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISMasterDataBodyExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "EPCISMasterDataBodyType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISMasterDataBodyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "EPCISMasterDataDocumentExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISMasterDataDocumentExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "EPCISMasterDataDocumentType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISMasterDataDocumentType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "EPCISMasterDataHeaderExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISMasterDataHeaderExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "IDListType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI");
            qName2 = new javax.xml.namespace.QName("", "id");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "VocabularyElementExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.VocabularyElementExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "VocabularyElementListType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.VocabularyElementType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "VocabularyElementType");
            qName2 = new javax.xml.namespace.QName("", "VocabularyElement");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "VocabularyElementType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.VocabularyElementType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "VocabularyExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.VocabularyExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "VocabularyListType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.VocabularyType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "VocabularyType");
            qName2 = new javax.xml.namespace.QName("", "Vocabulary");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-masterdata:xsd:1", "VocabularyType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.VocabularyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ArrayOfString");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.ArrayOfString.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "DuplicateNameException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.DuplicateNameException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "DuplicateSubscriptionException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.DuplicateSubscriptionException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "EmptyParms");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EmptyParms.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "EPCISException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "EPCISQueryBodyType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISQueryBodyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "EPCISQueryDocumentExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISQueryDocumentExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "EPCISQueryDocumentType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISQueryDocumentType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "GetSubscriptionIDs");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.GetSubscriptionIDs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.ImplementationException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ImplementationExceptionSeverity");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.ImplementationExceptionSeverity.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "InvalidURIException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.InvalidURIException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchNameException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.NoSuchNameException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "NoSuchSubscriptionException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.NoSuchSubscriptionException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Poll");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.Poll.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryParam");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.QueryParam.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryParameterException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.QueryParameterException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryParams");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.QueryParam[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryParam");
            qName2 = new javax.xml.namespace.QName("", "param");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryResults");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.QueryResults.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryResultsBody");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.QueryResultsBody.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryResultsExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.QueryResultsExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QuerySchedule");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.QuerySchedule.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryScheduleExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.QueryScheduleExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooComplexException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.QueryTooComplexException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "QueryTooLargeException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.QueryTooLargeException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SecurityException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.SecurityException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Subscribe");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.Subscribe.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscribeNotPermittedException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.SubscribeNotPermittedException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscriptionControls");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.SubscriptionControls.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscriptionControlsException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.SubscriptionControlsException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "SubscriptionControlsExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.SubscriptionControlsExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "Unsubscribe");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.Unsubscribe.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "ValidationException");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.ValidationException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis-query:xsd:1", "VoidHolder");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.VoidHolder.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "ActionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.ActionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "AggregationEventExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.AggregationEventExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "AggregationEventType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.AggregationEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "BusinessLocationExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.BusinessLocationExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "BusinessLocationIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "BusinessLocationType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.BusinessLocationType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "BusinessStepIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "BusinessTransactionIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "BusinessTransactionListType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.BusinessTransactionType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "BusinessTransactionType");
            qName2 = new javax.xml.namespace.QName("", "bizTransaction");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "BusinessTransactionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.BusinessTransactionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "BusinessTransactionTypeIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "DispositionIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCClassType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISBodyExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISBodyExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISBodyType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISBodyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISDocumentExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISDocumentExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISDocumentType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISDocumentType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISEventExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISEventExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISEventListExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISEventListExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISEventType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISHeaderExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISHeaderExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCISHeaderType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPCISHeaderType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EPCListType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPC[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:epcglobal:xsd:1", "EPC");
            qName2 = new javax.xml.namespace.QName("", "epc");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "EventListType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EventListType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "ObjectEventExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.ObjectEventExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "ObjectEventType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.ObjectEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "ParentIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "QuantityEventExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.QuantityEventExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "QuantityEventType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.QuantityEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "ReadPointExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.ReadPointExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "ReadPointIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "ReadPointType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.ReadPointType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "TransactionEventExtensionType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.TransactionEventExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:epcis:xsd:1", "TransactionEventType");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.TransactionEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:xsd:1", "Document");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.Document.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:epcglobal:xsd:1", "EPC");
            cachedSerQNames.add(qName);
            cls = org.accada.epcis.soapapi.EPC.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public org.accada.epcis.soapapi.ArrayOfString getQueryNames(org.accada.epcis.soapapi.EmptyParms parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getQueryNames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.accada.epcis.soapapi.ArrayOfString) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.accada.epcis.soapapi.ArrayOfString) org.apache.axis.utils.JavaUtils.convert(_resp, org.accada.epcis.soapapi.ArrayOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ImplementationException) {
              throw (org.accada.epcis.soapapi.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ValidationException) {
              throw (org.accada.epcis.soapapi.ValidationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.SecurityException) {
              throw (org.accada.epcis.soapapi.SecurityException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.accada.epcis.soapapi.VoidHolder subscribe(org.accada.epcis.soapapi.Subscribe parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.QueryTooComplexException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.InvalidURIException, org.accada.epcis.soapapi.SubscribeNotPermittedException, org.accada.epcis.soapapi.SubscriptionControlsException, org.accada.epcis.soapapi.QueryParameterException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException, org.accada.epcis.soapapi.DuplicateSubscriptionException, org.accada.epcis.soapapi.NoSuchNameException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "subscribe"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.accada.epcis.soapapi.VoidHolder) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.accada.epcis.soapapi.VoidHolder) org.apache.axis.utils.JavaUtils.convert(_resp, org.accada.epcis.soapapi.VoidHolder.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.QueryTooComplexException) {
              throw (org.accada.epcis.soapapi.QueryTooComplexException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ImplementationException) {
              throw (org.accada.epcis.soapapi.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.InvalidURIException) {
              throw (org.accada.epcis.soapapi.InvalidURIException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.SubscribeNotPermittedException) {
              throw (org.accada.epcis.soapapi.SubscribeNotPermittedException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.SubscriptionControlsException) {
              throw (org.accada.epcis.soapapi.SubscriptionControlsException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.QueryParameterException) {
              throw (org.accada.epcis.soapapi.QueryParameterException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ValidationException) {
              throw (org.accada.epcis.soapapi.ValidationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.SecurityException) {
              throw (org.accada.epcis.soapapi.SecurityException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.DuplicateSubscriptionException) {
              throw (org.accada.epcis.soapapi.DuplicateSubscriptionException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.NoSuchNameException) {
              throw (org.accada.epcis.soapapi.NoSuchNameException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.accada.epcis.soapapi.VoidHolder unsubscribe(org.accada.epcis.soapapi.Unsubscribe parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.NoSuchSubscriptionException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "unsubscribe"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.accada.epcis.soapapi.VoidHolder) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.accada.epcis.soapapi.VoidHolder) org.apache.axis.utils.JavaUtils.convert(_resp, org.accada.epcis.soapapi.VoidHolder.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ImplementationException) {
              throw (org.accada.epcis.soapapi.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.NoSuchSubscriptionException) {
              throw (org.accada.epcis.soapapi.NoSuchSubscriptionException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ValidationException) {
              throw (org.accada.epcis.soapapi.ValidationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.SecurityException) {
              throw (org.accada.epcis.soapapi.SecurityException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.accada.epcis.soapapi.ArrayOfString getSubscriptionIDs(org.accada.epcis.soapapi.GetSubscriptionIDs parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException, org.accada.epcis.soapapi.NoSuchNameException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getSubscriptionIDs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.accada.epcis.soapapi.ArrayOfString) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.accada.epcis.soapapi.ArrayOfString) org.apache.axis.utils.JavaUtils.convert(_resp, org.accada.epcis.soapapi.ArrayOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ImplementationException) {
              throw (org.accada.epcis.soapapi.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ValidationException) {
              throw (org.accada.epcis.soapapi.ValidationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.SecurityException) {
              throw (org.accada.epcis.soapapi.SecurityException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.NoSuchNameException) {
              throw (org.accada.epcis.soapapi.NoSuchNameException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.accada.epcis.soapapi.QueryResults poll(org.accada.epcis.soapapi.Poll parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.QueryTooComplexException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.QueryTooLargeException, org.accada.epcis.soapapi.QueryParameterException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException, org.accada.epcis.soapapi.NoSuchNameException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "poll"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.accada.epcis.soapapi.QueryResults) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.accada.epcis.soapapi.QueryResults) org.apache.axis.utils.JavaUtils.convert(_resp, org.accada.epcis.soapapi.QueryResults.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.QueryTooComplexException) {
              throw (org.accada.epcis.soapapi.QueryTooComplexException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ImplementationException) {
              throw (org.accada.epcis.soapapi.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.QueryTooLargeException) {
              throw (org.accada.epcis.soapapi.QueryTooLargeException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.QueryParameterException) {
              throw (org.accada.epcis.soapapi.QueryParameterException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ValidationException) {
              throw (org.accada.epcis.soapapi.ValidationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.SecurityException) {
              throw (org.accada.epcis.soapapi.SecurityException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.NoSuchNameException) {
              throw (org.accada.epcis.soapapi.NoSuchNameException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String getStandardVersion(org.accada.epcis.soapapi.EmptyParms parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getStandardVersion"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ImplementationException) {
              throw (org.accada.epcis.soapapi.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ValidationException) {
              throw (org.accada.epcis.soapapi.ValidationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.SecurityException) {
              throw (org.accada.epcis.soapapi.SecurityException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String getVendorVersion(org.accada.epcis.soapapi.EmptyParms parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getVendorVersion"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ImplementationException) {
              throw (org.accada.epcis.soapapi.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.ValidationException) {
              throw (org.accada.epcis.soapapi.ValidationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.accada.epcis.soapapi.SecurityException) {
              throw (org.accada.epcis.soapapi.SecurityException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

}
