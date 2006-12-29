/**
 * EPCISServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.accada.epcis.soapapi;

public interface EPCISServicePortType extends java.rmi.Remote {
    public org.accada.epcis.soapapi.ArrayOfString getQueryNames(org.accada.epcis.soapapi.EmptyParms parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException;
    public org.accada.epcis.soapapi.VoidHolder subscribe(org.accada.epcis.soapapi.Subscribe parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.QueryTooComplexException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.InvalidURIException, org.accada.epcis.soapapi.SubscribeNotPermittedException, org.accada.epcis.soapapi.SubscriptionControlsException, org.accada.epcis.soapapi.QueryParameterException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException, org.accada.epcis.soapapi.DuplicateSubscriptionException, org.accada.epcis.soapapi.NoSuchNameException;
    public org.accada.epcis.soapapi.VoidHolder unsubscribe(org.accada.epcis.soapapi.Unsubscribe parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.NoSuchSubscriptionException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException;
    public org.accada.epcis.soapapi.ArrayOfString getSubscriptionIDs(org.accada.epcis.soapapi.GetSubscriptionIDs parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException, org.accada.epcis.soapapi.NoSuchNameException;
    public org.accada.epcis.soapapi.QueryResults poll(org.accada.epcis.soapapi.Poll parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.QueryTooComplexException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.QueryTooLargeException, org.accada.epcis.soapapi.QueryParameterException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException, org.accada.epcis.soapapi.NoSuchNameException;
    public java.lang.String getStandardVersion(org.accada.epcis.soapapi.EmptyParms parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException;
    public java.lang.String getVendorVersion(org.accada.epcis.soapapi.EmptyParms parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException;
}
