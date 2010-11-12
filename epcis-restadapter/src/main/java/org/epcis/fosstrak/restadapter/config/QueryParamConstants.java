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
package org.epcis.fosstrak.restadapter.config;

/**
 * Class holding EPCIS REST Adapter Constants
 * @author Mathias Mueller mathias.mueller(at)unifr.ch
 *
 */
public class QueryParamConstants {

    // standard EPCIS vocabulary
    public static final String eventType              = "eventType";
    public static final String GE_eventTime           = "GE_eventTime";
    public static final String LT_eventTime           = "LT_eventTime";
    public static final String GE_recordTime          = "GE_recordTime";
    public static final String LT_recordTime          = "LT_recordTime";
    public static final String EQ_action              = "EQ_action";
    public static final String EQ_bizStep             = "EQ_bizStep";
    public static final String EQ_disposition         = "EQ_disposition";
    public static final String EQ_readPoint           = "EQ_readPoint";
    public static final String WD_readPoint           = "WD_readPoint";
    public static final String EQ_bizLocation         = "EQ_bizLocation";
    public static final String WD_bizLocation         = "WD_bizLocation";
    public static final String EQ_bizTransaction_type = "EQ_bizTransaction_type";
    public static final String MATCH_epc              = "MATCH_epc";
    public static final String MATCH_parentID         = "MATCH_parentID";
    public static final String MATCH_anyEPC           = "MATCH_anyEPC";
    public static final String MATCH_epcClass         = "MATCH_epcClass";
    public static final String EQ_quantity            = "EQ_quantity";
    public static final String GT_quantity            = "GT_quantity";
    public static final String GE_quantity            = "GE_quantity";
    public static final String LT_quantity            = "LT_quantity";
    public static final String LE_quantity            = "LE_quantity";
    public static final String EQ_fieldname           = "EQ_fieldname";

//  public static final String EQ_fieldname = "EQ_google.com#location";
    public static final String GT_fieldname              = "GT_fieldname";
    public static final String GE_fieldname              = "GE_fieldname";
    public static final String LT_fieldname              = "LT_fieldname";
    public static final String LE_fieldname              = "LE_fieldname";
    public static final String EXISTS_fieldname          = "EXISTS_fieldname";
    public static final String HASATTR_fieldname         = "HASATTR_fieldname";
    public static final String EQATTR_fieldname_attrname = "EQATTR_fieldname_attrname";
    public static final String orderBy                   = "orderBy";
    public static final String orderDirection            = "orderDirection";
    public static final String eventCountLimit           = "eventCountLimit";
    public static final String maxEventCount             = "maxEventCount";

    // EPCIS REST Adapter vocabulary
    public static final String EVENT_TYPE_REST                   = "type";
    public static final String EVENT_TIME_REST                   = "time";
    public static final String RECORD_TIME_REST                  = "recordtime";
    public static final String ACTION_REST                       = "action";
    public static final String BUSINESS_STEP_REST                = "step";
    public static final String DISPOSITION_REST                  = "disposition";
    public static final String READ_POINT_REST                   = "reader";
    public static final String READ_POINT_DESCENDANT_REST        = "readerdescendant";
    public static final String BUSINESS_LOCATION_REST            = "location";
    public static final String BUSINESS_LOCATION_DESCENDANT_REST = "locationdescendant";
    public static final String BUSINESS_TRANSACTION_TYPE_REST    = "transaction";
    public static final String EPC_REST                          = "epc";
    public static final String PARENT_ID_REST                    = "parentid";
    public static final String ANY_EPC_REST                      = "anyepc";
    public static final String EPC_CLASS_REST                    = "epcclass";
    public static final String QUANTITY_REST                     = "quantity";
    public static final String FIELDNAME_REST                    = "fieldname";

//  public static final String EXISTS_FIELDNAME_REST = "fieldnameexists";    // fieldname=exists
//  public static final String HASATTR_FIELDNAME_REST = "fieldnamehasattributes";
//  public static final String EQATTR_FIELDNAME_ATTRNAME_REST = "fieldnameequalsattributesattrname";
    public static final String ORDER_BY_REST          = "order";
    public static final String ORDER_DIRECTION_REST   = "ordering";
    public static final String EVENT_COUNT_LIMIT_REST = "eventlimit";
    public static final String MAX_EVENT_COUNT_REST   = "maxeventlimit";

    public static final String EVENT_TIME_REST_GE     = "time greather or equal than";
    public static final String EVENT_TIME_REST_LT     = "time less than";
    public static final String RECORD_TIME_REST_GE    = "record time greather or equal than";
    public static final String RECORD_TIME_REST_LT    = "record time less than";
    public static final String QUANTITY_REST_GT       = "quantity greather than";
    public static final String QUANTITY_REST_LT       = "quantity less than";
}
