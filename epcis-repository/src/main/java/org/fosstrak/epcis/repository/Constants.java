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

package org.accada.epcis.repository;

/**
 * @author Sean Wellington
 */
public final class Constants {

    // standard vocabularies
    public static final String READ_POINT_ID_VTYPE = "urn:epcglobal:epcis:vtype:ReadPoint";
    public static final String BUSINESS_LOCATION_ID_VTYPE = "urn:epcglobal:epcis:vtype:BusinessLocation";
    public static final String BUSINESS_STEP_ID_VTYPE = "urn:epcglobal:epcis:vtype:BusinessStep";
    public static final String DISPOSITION_ID_VTYPE = "urn:epcglobal:epcis:vtype:Disposition";
    public static final String BUSINESS_TRANSACTION_VTYPE = "urn:epcglobal:epcis:vtype:BusinessTransaction";
    public static final String BUSINESS_TRANSACTION_TYPE_ID_VTYPE = "urn:epcglobal:epcis:vtype:BusinessTransactionType";
    public static final String EPC_CLASS_VTYPE = "urn:epcglobal:epcis:vtype:EPCClass";

    // standard event types
    public static final String OBJECT_EVENT = "ObjectEvent";
    public static final String AGGREGATION_EVENT = "AggregationEvent";
    public static final String QUANTITY_EVENT = "QuantityEvent";
    public static final String TRANSACTION_EVENT = "TransactionEvent";

    /**
     * Hidden default constructor.
     */
    private Constants() {
    }

}
