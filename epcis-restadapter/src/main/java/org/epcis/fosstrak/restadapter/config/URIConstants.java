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
 * Class holding URI Constants
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>

 *
 */
public class URIConstants {

    public static final String EPCIS_ROOT                  = "/" + Config.EPCIS_REST_ADAPTER_VERSION;
    public static final String BUSINESS_LOCATION           = QueryParamConstants.BUSINESS_LOCATION_REST;
    public static final String READ_POINT                  = QueryParamConstants.READ_POINT_REST;
    public static final String EVENT_TIME                  = QueryParamConstants.EVENT_TIME_REST;
    public static final String RECORD_TIME                 = QueryParamConstants.RECORD_TIME_REST;
    public static final String BUSINESS_STEP               = QueryParamConstants.BUSINESS_STEP_REST;
    public static final String ACTION                      = QueryParamConstants.ACTION_REST;
    public static final String DISPOSITION                 = QueryParamConstants.DISPOSITION_REST;
    public static final String EVENT_TYPE                  = QueryParamConstants.EVENT_TYPE_REST;
    public static final String EPCS                        = "epcs";
    public static final String EPC                         = QueryParamConstants.EPC_REST;
    public static final String BUSINESS_TRANSACTIONS       = "transactions";
    public static final String BUSINESS_TRANSACTION        = QueryParamConstants.BUSINESS_TRANSACTION_TYPE_REST;
    public static final String PARENT_ID                   = QueryParamConstants.PARENT_ID_REST;
    public static final String EPC_CLASS                   = QueryParamConstants.EPC_CLASS_REST;
    public static final String QUANTITY                    = QueryParamConstants.QUANTITY_REST;
    public static final String TIME_ZONE_OFFSET            = "timezoneoffset";
    public static final String ID                          = "id";
    public static final String CONFIG                      = "/config";
    public static final String CONFIG_EPCIS                = CONFIG + "/epcisurl";
    public static final String CONFIG_FEED                 = CONFIG + "/feedurl";
    public static final String CONFIG_RESET_RESTFULEPCIS   = CONFIG + "/resetrestfulepcis";
    public static final String CONFIG_RELOAD_FINDER        = CONFIG + "/reloadfinder";
    public static final String ABOUT                       = "/about";
    public static final String ABOUT_VERSION               = ABOUT + "/version";
    public static final String VERSION_REST                = ABOUT_VERSION + "/rest";
    public static final String VERSION_STANDARD            = ABOUT_VERSION + "/standard";
    public static final String VERSION_VENDOR              = ABOUT_VERSION + "/vendor";
    public static final String ABOUT_AUTHORS               = ABOUT + "/authors";
    public static final String ABOUT_AUTHOR                = ABOUT_AUTHORS + "/{" + ID + "}";
    public static final String ABOUT_QUERYNAMES            = ABOUT + "/querynames";
    public static final String FINDER_ALL_READ_POINTS      = "/" + READ_POINT;
    public static final String FINDER_BUSINESS_LOCATIONS   = "/" + BUSINESS_LOCATION;
    public static final String FINDER_BUSINESS_LOCATION    = FINDER_BUSINESS_LOCATIONS + "/{" + BUSINESS_LOCATION + "}";
    public static final String FINDER_READ_POINTS          = FINDER_BUSINESS_LOCATION + "/" + READ_POINT;
    public static final String FINDER_READ_POINT           = FINDER_READ_POINTS + "/{" + READ_POINT + "}";
    public static final String FINDER_EVENT_TIMES          = FINDER_READ_POINT + "/" + EVENT_TIME;
    public static final String FINDER_EVENT_TIME           = FINDER_EVENT_TIMES + "/{" + EVENT_TIME + "}";
    public static final String FINDER_EVENT                = FINDER_EVENT_TIME + "/event";
    public static final String INDEX_QP                    = "index";
    public static final String FINDER_EVENT_ID             = FINDER_EVENT + "/{" + ID + "}";
    public static final String EVENT_EPCS                  = FINDER_EVENT + "/" + EPCS;
    public static final String EVENT_EPC                   = EVENT_EPCS + "/" + "{" + EPC + "}";
    public static final String EVENT_BUSINESS_TRANSACTIONS = FINDER_EVENT + "/" + BUSINESS_TRANSACTIONS;
    public static final String EVENT_BUSINESS_TRANSACTION  = EVENT_BUSINESS_TRANSACTIONS + "/" + "{" + BUSINESS_TRANSACTION + "}";
    public static final String EVENT_RECORD_TIME       = FINDER_EVENT + "/" + RECORD_TIME;
    public static final String EVENT_BUSINESS_STEP     = FINDER_EVENT + "/" + BUSINESS_STEP;
    public static final String EVENT_ACTION            = FINDER_EVENT + "/" + ACTION;
    public static final String EVENT_EVENT_TIME        = FINDER_EVENT + "/" + EVENT_TIME;
    public static final String EVENT_READ_POINT        = FINDER_EVENT + "/" + READ_POINT;
    public static final String EVENT_BUSINESS_LOCATION = FINDER_EVENT + "/" + BUSINESS_LOCATION;
    public static final String EVENT_DISPOSITION       = FINDER_EVENT + "/" + DISPOSITION;
    public static final String EVENT_EVENT_TYPE        = FINDER_EVENT + "/" + EVENT_TYPE;
    public static final String EVENT_PARENT_ID         = FINDER_EVENT + "/" + PARENT_ID;
    public static final String EVENT_EPC_CLASS         = FINDER_EVENT + "/" + EPC_CLASS;
    public static final String EVENT_QUANTITY          = FINDER_EVENT + "/" + QUANTITY;
    public static final String EVENT_TIME_ZONE_OFFSET  = FINDER_EVENT + "/" + TIME_ZONE_OFFSET;
    public static final String EVENTQUERY              = "/eventquery";
    public static final String EVENTQUERY_RESULTS      = EVENTQUERY + "/result";
    public static final String EVENTQUERY_SUBSCRIPTION = EVENTQUERY + "/subscription";
    public static final String SUBSCRIPTIONS           = "/subscriptions";
    public static final String SUBSCRIPTIONS_ID        = SUBSCRIPTIONS + "/{" + ID + "}";
    public static final String CAPTURE                 = "/capture";
    public static final String CAPTURE_SIMULATOR       = CAPTURE + "/simulator";
}
