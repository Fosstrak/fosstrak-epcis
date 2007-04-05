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

public interface EPCglobalEPCISService extends javax.xml.rpc.Service {
    public java.lang.String getEPCglobalEPCISServicePortAddress();

    public org.accada.epcis.soapapi.EPCISServicePortType getEPCglobalEPCISServicePort() throws javax.xml.rpc.ServiceException;

    public org.accada.epcis.soapapi.EPCISServicePortType getEPCglobalEPCISServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
