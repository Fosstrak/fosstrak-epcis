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

public class EPCglobalEPCISServiceLocator extends org.apache.axis.client.Service implements org.accada.epcis.soapapi.EPCglobalEPCISService {

    public EPCglobalEPCISServiceLocator() {
    }


    public EPCglobalEPCISServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EPCglobalEPCISServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EPCglobalEPCISServicePort
    private java.lang.String EPCglobalEPCISServicePort_address = "http://localhost:6060/axis/services/EPCglobalEPCISService";

    public java.lang.String getEPCglobalEPCISServicePortAddress() {
        return EPCglobalEPCISServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EPCglobalEPCISServicePortWSDDServiceName = "EPCglobalEPCISServicePort";

    public java.lang.String getEPCglobalEPCISServicePortWSDDServiceName() {
        return EPCglobalEPCISServicePortWSDDServiceName;
    }

    public void setEPCglobalEPCISServicePortWSDDServiceName(java.lang.String name) {
        EPCglobalEPCISServicePortWSDDServiceName = name;
    }

    public org.accada.epcis.soapapi.EPCISServicePortType getEPCglobalEPCISServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EPCglobalEPCISServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEPCglobalEPCISServicePort(endpoint);
    }

    public org.accada.epcis.soapapi.EPCISServicePortType getEPCglobalEPCISServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.accada.epcis.soapapi.EPCISServiceBindingStub _stub = new org.accada.epcis.soapapi.EPCISServiceBindingStub(portAddress, this);
            _stub.setPortName(getEPCglobalEPCISServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEPCglobalEPCISServicePortEndpointAddress(java.lang.String address) {
        EPCglobalEPCISServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.accada.epcis.soapapi.EPCISServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.accada.epcis.soapapi.EPCISServiceBindingStub _stub = new org.accada.epcis.soapapi.EPCISServiceBindingStub(new java.net.URL(EPCglobalEPCISServicePort_address), this);
                _stub.setPortName(getEPCglobalEPCISServicePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("EPCglobalEPCISServicePort".equals(inputPortName)) {
            return getEPCglobalEPCISServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:epcglobal:epcis:wsdl:1", "EPCglobalEPCISService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:epcglobal:epcis:wsdl:1", "EPCglobalEPCISServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EPCglobalEPCISServicePort".equals(portName)) {
            setEPCglobalEPCISServicePortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
