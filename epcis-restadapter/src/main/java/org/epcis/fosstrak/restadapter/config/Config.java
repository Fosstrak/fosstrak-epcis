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

import org.epcis.fosstrak.restadapter.db.InternalDatabase;
import org.epcis.fosstrak.restadapter.util.URI;

/**
 * Class to configure the EPCIS REST Adapter
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 */
public class Config {

    // EPCIS default URL (can be changed for the Web UI)
    private static String EPCIS_REPOSITORY_URL = "http://localhost:8080/epcis-repository-0.4.2";
    // Version of the EPCIS REST Adapter
    public static final String EPCIS_REST_ADAPTER_VERSION = "1";
    // EPCIS vocabulary
    public static final String SIMPLE_EVENT_QUERY = "SimpleEventQuery";
    public static final String SIMPLE_MASTER_DATA = "SimpleMasterData";
    // EPCIS REST Adapter vocabulary
    public static final String EQUALS = "=";
    public static final String GREATER = "+";
    public static final String LESS = "-";
    public static final String INTERVAL = ",";
    public static final String SEPARATOR = ",";
    public static final String CONCATENATOR = "+";
    public static final String APPENDER = "&";
    public static final String SLASH = "/";
    public static final String QUESTION_MARK = "?";
    public static final String STAR = "*";
    public static final String ABOVE = ">";
    public static final String FEWER = "<";
    public static final String PARAM_VALUE_SEPARATOR = ";";
    public static final String PARAM_START = "(";
    public static final String PARAM_END = ")";
    public static final String NO_VALUE = "blank";
    public static final String OBJECT_EVENT = "ObjectEvent";
    public static final String AGGREGATION_EVENT = "AggregationEvent";
    public static final String QUANTITY_EVENT = "QuantityEvent";
    public static final String TRANSACTION_EVENT = "TransactionEvent";
    public static final String ADD = "ADD";
    public static final String OBSERVE = "OBSERVE";
    public static final String DELETE = "DELETE";
    public static final String CAPTURE_TimeZoneOffset_USAGE = "+/-hh:mm";
    public static final String TIME_USAGE = "yyyy-mm-ddThh:mm:ss";
    public static final String NOW = "now";
    public static final String CAPTURE_EventTime_USAGE = TIME_USAGE + " or '" + NOW + "' for the actual time";
//  public static final String CAPTURE_RecordTime_USAGE = CAPTURE_EventTime_USAGE;
    public static final String CAPTURE_BusinessStep_USAGE = "Business Step ID";
    public static final String CAPTURE_ReadPoint_USAGE = "Read Point ID";
    public static final String CAPTURE_BusinessLocation_USAGE = "Business Location ID";
    public static final String CAPTURE_Disposition_USAGE = "Disposition ID";
    public static final String CAPTURE_Type_USAGE = OBJECT_EVENT + ", " + AGGREGATION_EVENT + ", " + QUANTITY_EVENT + " or " + TRANSACTION_EVENT;
    public static final String CAPTURE_Epc_USAGE = "Only for Object, Aggregation and Transaction Event - Comma separated list of EPC's";
    public static final String CAPTURE_Action_USAGE = "Only for Object, Aggregation and Transaction Event - " + ADD + ", " + OBSERVE + " or " + DELETE;
    public static final String CAPTURE_ParentID_USAGE = "Only for Aggregation and Transaction Event - Parent ID";
    public static final String CAPTURE_EpcClass_USAGE = "Only for Quantity Event - EPC Class";
    public static final String CAPTURE_Quantity_USAGE = "Only for Quantity Event - Quantity an Integer Value";
    public static final String CAPTURE_BusinessTransaction_USAGE = "Comma separated list of Business Transaction's. A Business Transaction is encoded as follows: type" + PARAM_START + "value" + PARAM_END;
//  public static final String QUERY_TimeZoneOffset_USAGE = CAPTURE_TimeZoneOffset_USAGE;
    private static final String COMMA_SEPARATED_LIST_USAGE = " (comma separated list for multiple values)";
    private static final String TIME_USAGE_INDICATIONS = " - Constrain as follows: 'time1,time2' between time1 and time2, '&gt;time' greater than time, '&lt;time' less than time";
    private static final String QUANTITY_USAGE_INDICATIONS = " - Constrain as follows: 'quantity1,quantity2' between quantity1 and quantity2, '&gt;quantity' greater than quantity, '&lt;quantity' less than quantity";
    private static final String EPC_USAGE_INDICATIONS = " - Use a STAR [*] to specify all epc's maching the specific pattern";
    public static final String QUERY_EventTime_USAGE = TIME_USAGE + TIME_USAGE_INDICATIONS;
    public static final String QUERY_RecordTime_USAGE = QUERY_EventTime_USAGE;
    public static final String QUERY_BusinessStep_USAGE = CAPTURE_BusinessStep_USAGE + COMMA_SEPARATED_LIST_USAGE;
    public static final String QUERY_ReadPoint_USAGE = "Available " + CAPTURE_ReadPoint_USAGE + "'s" + COMMA_SEPARATED_LIST_USAGE;
    public static final String QUERY_BusinessLocation_USAGE = "Available " + CAPTURE_BusinessLocation_USAGE + "'s" + COMMA_SEPARATED_LIST_USAGE;
    public static final String QUERY_Disposition_USAGE = CAPTURE_Disposition_USAGE + COMMA_SEPARATED_LIST_USAGE;
    public static final String QUERY_Type_USAGE = CAPTURE_Type_USAGE + COMMA_SEPARATED_LIST_USAGE;
    public static final String QUERY_Epc_USAGE = CAPTURE_Epc_USAGE + EPC_USAGE_INDICATIONS;
    public static final String QUERY_Action_USAGE = CAPTURE_Action_USAGE + COMMA_SEPARATED_LIST_USAGE;
    public static final String QUERY_ParentID_USAGE = CAPTURE_ParentID_USAGE;
    public static final String QUERY_EpcClass_USAGE = CAPTURE_EpcClass_USAGE + EPC_USAGE_INDICATIONS;
    public static final String QUERY_Quantity_USAGE = CAPTURE_Quantity_USAGE + QUANTITY_USAGE_INDICATIONS;
    public static final String QUERY_BusinessTransaction_USAGE = "Comma separated list of Business Transaction's. A Business Transaction is encoded as follows: type" + PARAM_START + "value1,...,valueN" + PARAM_END + " where N ranges from any finite number greater or equal than 1";
    public static final String Query_ORDERDIRECTION_USAGE = "ASC or DESC (default is ASC if omitted)";
    public static final String QUERY_ReadPointDescendant_USAGE = "";
    public static final String QUERY_BusinessLocationDescendant_USAGE = "";
    public static final String QUERY_AnyEPC_USAGE = "Only for Events containing EPC's" + EPC_USAGE_INDICATIONS;
    public static final String Query_Fieldname_USAGE = "";
    public static final String Query_OrderBy_USAGE = "eventTime, recordTime or quantity";
    public static final String Query_EventCountLimit_USAGE = "Restricts the result to the first N queries. If this field is used then the ordering is specified by the mandatory fields orderBy and orderDirection";
    public static final String Query_MaxEventCount_USAGE = "If used, the result will include at most this many events. If the query would otherwise return more than this number of events a QueryTooLargeException is raised. Mutually exclusive to the event count limit field";
    // Author information
    public static final String SOFTENG = "Software Engineering Group, Departement of Informatics, University of Fribourg Switzerland";
    public static final String SOFTENG_HP = "http://diuf.unifr.ch/softeng/";
    public static final String DSGROUP = "Distributed Systems Group, ETH Zurich Switzerland";
    public static final String DSGROUP_HP = "http://www.vs.inf.ethz.ch/";
    public static final String MATHIAS[] = {
        "mathias.mueller", "Main Developer: Mathias Mueller", "mathias.mueller@unifr.ch", SOFTENG, SOFTENG_HP, "http://diuf.unifr.ch/people/muellmat/"
    };
    public static final String PATRIK[] = {
        "patrik.fuhrer", "Developer: Patrik Fuhrer", "patrik.fuhrer@unifr.ch", SOFTENG, SOFTENG_HP, "http://diuf.unifr.ch/people/fuhrer/"
    };
    public static final String DOMINIQUE[] = {
        "dominique.guinard", "Project Lead, Developer: Dominique Guinard", "dguinard@ethz.ch", DSGROUP, DSGROUP_HP, "http://www.guinard.org"
    };
    public static final String AUTHORS[][] = {
        DOMINIQUE, MATHIAS, PATRIK
    };
    /**
     * This Media (MIME) type is a fix to handle WebKit browsers which have
     *   a strange way of handling favorite mime types.
     *   Adding qs=2 specifies that the server would prefer serving HTML if possible.
     *   if we do not add that, WebKit will ask for ... XML!
     *   More info on: :
     *   http://jersey.576304.n2.nabble.com/ImplicitProduces-annotation-td5230574.html
     */
    public static final String TEXT_HTML_WEBKIT_SAFE = "text/html;qs=2";

    /**
     * This media type is for JSONp (JSON with padding) which prevents
     * from violating the same-origin-policy exception.
     */
    public static final String APPLICATION_JSONP = "application/x-javascript";

    // EPCIS REST Adapter URL (will be set on the first request to be deployable everywhere)
    private static String RESTFUL_EPCIS_URL;
    private static String RESTFUL_EPCIS_URL_NO_VERSION;

    /**
     * Gets the EPCIS REST Adapter URL
     *
     *
     * @return
     */
    public static String GET_RESTFUL_EPCIS_URL() {
        return RESTFUL_EPCIS_URL;
    }

    /**
     * Sets the EPCIS REST Adapter URL
     *
     *
     * @param url
     */
    public static void SET_RESTFUL_EPCIS_URL(String url) {
        RESTFUL_EPCIS_URL = url;
    }

    /**
     * Gets the EPCIS REST Adapter URL without a version number.
     *
     *
     * @return
     */
    public static String GET_RESTFUL_EPCIS_URL_NO_VERSION() {
        return RESTFUL_EPCIS_URL_NO_VERSION;
    }

    /**
     * Sets the EPCIS REST Adapter URL without a version number.
     *
     *
     * @param url
     */
    public static void SET_RESTFUL_EPCIS_URL_NO_VERSION(String url) {
        RESTFUL_EPCIS_URL_NO_VERSION = url;
    }
    // FEED URL (will be set on the first request to be deployable everywhere)
    private static String FEED_URL;

    /**
     * Gets the FEED URL
     *
     *
     * @return
     */
    public static String GET_FEED_URL() {
        return FEED_URL;
    }

    /**
     * Sets the FEED URL
     *
     *
     * @param url
     */
    public static void SET_FEED_URL(String url) {
        FEED_URL = url;
    }

    /**
     * Gets the EPCIS REST Adapter Repository URL
     *
     *
     * @return
     */
    public static String GET_EPCIS_REPOSITORY_URL() {
        return EPCIS_REPOSITORY_URL;
    }

    /**
     * Sets the EPCIS REST Adapter Repository URL
     *
     *
     * @param url
     */
    public static void SET_EPCIS_REPOSITORY_URL(String url) {
        EPCIS_REPOSITORY_URL = url;
        //Persist this in the database.
        InternalDatabase.getInstance().storeConfigurationEntry("epcisurl", url);
    }

    /**
     * Gets the EPCIS REST Adapter Repository Query URL
     *
     *
     * @return
     */
    public static String GET_EPCIS_REPOSITORY_QUERY_URL() {
        return GET_EPCIS_REPOSITORY_URL() + "/query";
    }

    /**
     * Sets the EPCIS REST Adapter Repository Query URL
     *
     *
     * @return
     */
    public static String GET_EPCIS_REPOSITORY_CAPTURE_URL() {
        return GET_EPCIS_REPOSITORY_URL() + "/capture";
    }

    // Server Resource URL (images, javascript etc) will be made absolute that the url's can be used elsewhere. The URL's are therefore valid in feeds and in in the links for the different representations (HTML, XML, JSON).
    /**
     * Gets the EPCIS REST Adapter Resources URL
     *
     *
     * @return
     */
    public static String GET_RESOURCES_URL() {
        return URI.addSubPath(GET_RESTFUL_EPCIS_URL(), "../../resources/");
    }
    // Fosstrak EPCIS public URL
    public static String FOSSTRAK_REPOSITORY_URL = "http://demo.fosstrak.org/epcis";
    // Default EPCIS and FEED URL
    public static String EPCIS_REPOSITORY_URL_DEFAULT = EPCIS_REPOSITORY_URL;
    public static String RESTFUL_FEED_URL_DEFAULT = FEED_URL;
    // Common Feed vocabulary
    public static final String FEED_TITLE = "Feed for the Electronic Product Code Information Service Query Subscription";
    public static final String FEED_AUTHORS = "EPCIS REST Adapter Team";
    public static final String FEED_CATEGORY = "Electronic Product Code Information Service Feed";
    public static final String FEED_CONTRIBUTOR = "EPCIS REST Adapter, Fosstrak Project";
    public static final int DEFAULT_FEED_SIZE = 100;
    // Configuration for the XML Schema generation from JAXB annotated classes
    public static final boolean IS_PRINT_XMLSCHEMA = false;
    public static final String XMLSCHEMA_FILEPATH = "/tmp/epcis-restadapter/doc/";
}
