/*
 * Copyright (C) 2010 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org) and
 * was developed as part of the webofthings.com initiative.
 *
 * Fosstrak is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Fosstrak is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Fosstrak; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */
package org.epcis.fosstrak.restadapter.model.epc;

import org.epcis.fosstrak.restadapter.model.Entry;
import java.util.List;

/**
 * This interface represents an EPC number.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
public interface EPC {

    public static final String COMPANY_PREFIX             = "Company Prefix";
    public static final String LOCATION_REFERENCE         = "Location Reference";
    public static final String EXTENSION_COMPONENT        = "Extension Component";
    public static final String SERIAL_REFERENCE           = "Serial Reference";
    public static final String SERIAL_NUMBER              = "Serial Number";
    public static final String ITEM_REFERENCE             = "Item Reference";
    public static final String ASSET_TYPE                 = "Asset Type";
    public static final String INDIVIDUAL_ASSET_REFERENCE = "Individual Asset Reference";
    public static final String SERVICE_REFERENCE          = "Service Reference";
    public static final String DOCUMENT_TYPE              = "Document Type";
    public static final String SGLN_PREFIX                = "urn:epc:id:sgln:";
    public static final String SGTIN_PREFIX               = "urn:epc:id:sgtin:";
    public static final String SSCC_PREFIX                = "urn:epc:id:sscc:";
    public static final String GRAI_PREFIX                = "urn:epc:id:grai:";
    public static final String GIAI_PREFIX                = "urn:epc:id:giai:";
    public static final String GSRN_PREFIX                = "urn:epc:id:gsrn:";
    public static final String GDTI_PREFIX                = "urn:epc:id:gdti:";

    /**
     * Get a list of the components of this EPC
     *
     *
     * @return
     */
    public List<Entry> getComponents();

    /**
     * Method description
     *
     *
     * @return
     */
    public String getName();
}
