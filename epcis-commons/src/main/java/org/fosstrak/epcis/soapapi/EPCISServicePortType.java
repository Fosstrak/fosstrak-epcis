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

public interface EPCISServicePortType extends java.rmi.Remote {
    public org.accada.epcis.soapapi.ArrayOfString getQueryNames(org.accada.epcis.soapapi.EmptyParms parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException;
    public org.accada.epcis.soapapi.VoidHolder subscribe(org.accada.epcis.soapapi.Subscribe parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.QueryTooComplexException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.InvalidURIException, org.accada.epcis.soapapi.SubscribeNotPermittedException, org.accada.epcis.soapapi.SubscriptionControlsException, org.accada.epcis.soapapi.QueryParameterException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException, org.accada.epcis.soapapi.DuplicateSubscriptionException, org.accada.epcis.soapapi.NoSuchNameException;
    public org.accada.epcis.soapapi.VoidHolder unsubscribe(org.accada.epcis.soapapi.Unsubscribe parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.NoSuchSubscriptionException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException;
    public org.accada.epcis.soapapi.ArrayOfString getSubscriptionIDs(org.accada.epcis.soapapi.GetSubscriptionIDs parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException, org.accada.epcis.soapapi.NoSuchNameException;
    public org.accada.epcis.soapapi.QueryResults poll(org.accada.epcis.soapapi.Poll parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.QueryTooComplexException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.QueryTooLargeException, org.accada.epcis.soapapi.QueryParameterException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException, org.accada.epcis.soapapi.NoSuchNameException;
    public java.lang.String getStandardVersion(org.accada.epcis.soapapi.EmptyParms parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException;
    public java.lang.String getVendorVersion(org.accada.epcis.soapapi.EmptyParms parms) throws java.rmi.RemoteException, org.accada.epcis.soapapi.ImplementationException, org.accada.epcis.soapapi.ValidationException, org.accada.epcis.soapapi.SecurityException;
}
