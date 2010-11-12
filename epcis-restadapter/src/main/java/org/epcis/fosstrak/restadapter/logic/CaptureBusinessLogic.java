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
package org.epcis.fosstrak.restadapter.logic;

import org.epcis.fosstrak.restadapter.util.URI;
import javax.ws.rs.core.UriInfo;
import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.http.HTTP;
import org.epcis.fosstrak.restadapter.http.HTTPStatusCodeMapper;
import org.epcis.fosstrak.restadapter.model.Form;
import org.epcis.fosstrak.restadapter.model.Content;
import org.epcis.fosstrak.restadapter.model.Resource;
import org.epcis.fosstrak.restadapter.util.ActualDateTime;
import org.epcis.fosstrak.restadapter.ws.generated.ActionType;
import org.epcis.fosstrak.restadapter.ws.generated.AggregationEventType;
import org.epcis.fosstrak.restadapter.ws.generated.BusinessLocationType;
import org.epcis.fosstrak.restadapter.ws.generated.BusinessTransactionListType;
import org.epcis.fosstrak.restadapter.ws.generated.BusinessTransactionType;
import org.epcis.fosstrak.restadapter.ws.generated.EPC;
import org.epcis.fosstrak.restadapter.ws.generated.EPCISBodyType;
import org.epcis.fosstrak.restadapter.ws.generated.EPCISDocumentType;
import org.epcis.fosstrak.restadapter.ws.generated.EPCISEventType;
import org.epcis.fosstrak.restadapter.ws.generated.EPCListType;
import org.epcis.fosstrak.restadapter.ws.generated.EventListType;
import org.epcis.fosstrak.restadapter.ws.generated.ObjectEventType;
import org.epcis.fosstrak.restadapter.ws.generated.ObjectFactory;
import org.epcis.fosstrak.restadapter.ws.generated.QuantityEventType;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterException;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterExceptionResponse;
import org.epcis.fosstrak.restadapter.ws.generated.ReadPointType;
import org.epcis.fosstrak.restadapter.ws.generated.TransactionEventType;
import org.epcis.fosstrak.restadapter.config.QueryParamConstants;
import org.epcis.fosstrak.restadapter.config.URIConstants;
import org.epcis.fosstrak.restadapter.rest.ICaptureResource;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import static org.epcis.fosstrak.restadapter.config.URIConstants.*;

/**
 * This class is a simulator that can be used to capture events, i.e. to
 * send them to the EPCIS capture interface.
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
public class CaptureBusinessLogic extends AbstractBusinessLogic implements ICaptureResource {

    /**
     * Returns a representation of the capture resource (howto) according to the requested mime type
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getCapture(UriInfo context) {
        String name        = "Capture Interface";
        String path        = CAPTURE;
        String description = "The RESTful Interface for Capturing Events. Usage is as follows. This service captures EPCIS events sent to it using HTTP POST requests. The payload of the HTTP POST request is expected to be an XML document conforming to the EPCISDocument schema. For further information refer to the XML Schema files or check the example in 'EPC Information Services (EPCIS) Version 1.0 Specification', section 9.6.";
        Resource resource = setUpResource(context, name, description, path);

        Content  links    = new Content();

        resource.setFields(links);

        addLink(context, links, "Simulator to capture Events", CAPTURE_SIMULATOR);

        return resource;
    }

    /**
     * POST method for adding a captured event
     *
     *
     * @param context
     * @param event
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource addCapture(UriInfo context, String event) {
        String            name        = "Capture Interface";
        String            path        = CAPTURE;
        String            description = "The RESTful Interface for Capturing Events.";
        Resource          resource    = setUpResource(context, name, description, path);

        URL               url;
        HttpURLConnection connection = null;
        String            res        = "";

        try {
            url        = new URL(Config.GET_EPCIS_REPOSITORY_CAPTURE_URL());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", MediaType.TEXT_XML);
            connection.setRequestProperty("Content-Length", Integer.toString(event.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // send POST request
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());

            out.writeBytes(event);
            out.flush();
            out.close();

            // get Resource
            InputStream    inputStream = connection.getInputStream();
            BufferedReader in          = new BufferedReader(new InputStreamReader(inputStream));
            String         line;

            while ((line = in.readLine()) != null) {
                res += line;
            }

            in.close();
            resource.setDescription(res);
        } catch (Exception ex) {
            res = ex.getMessage();
            resource.setDescription(res);
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return resource;

    }

    /**
     * Returns a representation of the capture simulator resource
     *
     *
     * @param context
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource getCaptureSimulation(UriInfo context) {
        String   name        = "Capture Simulator";
        String   path        = CAPTURE_SIMULATOR;
        String   description = "The Simulator to capture Events.";
        Resource resource    = setUpResource(context, name, description, path);

        // for simulation purpose
        Form form = new Form();

        URI  uri  = new URI(context);

        form.setAction(uri.getRestURI(CAPTURE_SIMULATOR));
        form.setActionDescription("capture");
        form.setMethod(HTTP.POST);
        resource.setForm(form);

        form.addEntry("Event Time", QueryParamConstants.EVENT_TIME_REST, ActualDateTime.GET_NOW(), Config.CAPTURE_EventTime_USAGE);
        form.addEntry("Time Zone Offset", URIConstants.TIME_ZONE_OFFSET, ActualDateTime.GET_TZO(), Config.CAPTURE_TimeZoneOffset_USAGE);
        form.addEntry("Business Step", QueryParamConstants.BUSINESS_STEP_REST, "urn:epcglobal:cbv:bizstep:other", Config.CAPTURE_BusinessStep_USAGE);
        form.addEntry("Disposition", QueryParamConstants.DISPOSITION_REST, "urn:epcglobal:cbv:disp:sold", Config.CAPTURE_Disposition_USAGE);
        form.addEntry("Read Point", QueryParamConstants.READ_POINT_REST, "urn:epc:id:sgln:unifr.perolles.cafeteria", Config.CAPTURE_ReadPoint_USAGE);
        form.addEntry("Business Location", QueryParamConstants.BUSINESS_LOCATION_REST, "urn:epc:id:sgln:ch.unifr.perolles", Config.CAPTURE_BusinessLocation_USAGE);


        form.addEntry("Business Transaction", QueryParamConstants.BUSINESS_TRANSACTION_TYPE_REST, "", Config.CAPTURE_BusinessTransaction_USAGE);
        form.addEntry("Event Type", QueryParamConstants.EVENT_TYPE_REST, Config.OBJECT_EVENT, Config.CAPTURE_Type_USAGE);
        form.addEntry("EPC's", QueryParamConstants.EPC_REST, "urn:epc:id:sgtin:0123456.012345.2222", Config.CAPTURE_Epc_USAGE);
        form.addEntry("Action", QueryParamConstants.ACTION_REST, "OBSERVE", Config.CAPTURE_Action_USAGE);
        form.addEntry("Parent ID", QueryParamConstants.PARENT_ID_REST, "", Config.CAPTURE_ParentID_USAGE);
        form.addEntry("EPC Class", QueryParamConstants.EPC_CLASS_REST, "", Config.CAPTURE_EpcClass_USAGE);
        form.addEntry("Quantity", QueryParamConstants.QUANTITY_REST, "", Config.CAPTURE_Quantity_USAGE);

        return resource;
    }

    /**
     * POST method for simulating a captured event in encoded in query params
     *
     *
     * @param context
     * @param eventTime
     * @param timeZoneOffset
     * @param businessStep
     * @param disposition
     * @param readPoint
     * @param businessLocation
     * @param businessTransaction
     * @param eventType
     * @param epc
     * @param action
     * @param parentId
     * @param epcClass
     * @param quantity
     *
     * @return an instance of javax.ws.rs.core.Resource
     */
    public Resource addCaptureSimulation(UriInfo context, String eventTime, String timeZoneOffset, String businessStep, String disposition, String readPoint, String businessLocation, String businessTransaction, String eventType, String epc, String action, String parentId, String epcClass, String quantity) {


        ObjectFactory  objectFactory  = new ObjectFactory();

        EPCISEventType ePCISEventType = null;

        ActionType     actionType     = null;

        if (action.equals(Config.ADD)) {
            actionType = ActionType.ADD;
        }

        if (action.equals(Config.OBSERVE)) {
            actionType = ActionType.OBSERVE;
        }

        if (action.equals(Config.DELETE)) {
            actionType = ActionType.DELETE;
        }

        if ((actionType == null) &&!action.equals("")) {
            QueryParameterException         qpe = objectFactory.createQueryParameterException();
            QueryParameterExceptionResponse ex  = new QueryParameterExceptionResponse("Action type not valid for: " + action, qpe);

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        EPCListType ePCListType = objectFactory.createEPCListType();

        if (!epc.equals("")) {
            String[] epcs = epc.split(Config.SEPARATOR);

            for (String s : epcs) {
                EPC e = new EPC();

                e.setValue(s);
                ePCListType.getEpc().add(e);
            }
        }

        BusinessTransactionListType businessTransactionListType = objectFactory.createBusinessTransactionListType();

        if (!businessTransaction.equals("")) {
            String[] businessTransactions = businessTransaction.split(Config.SEPARATOR);

            for (String myBizTransaction : businessTransactions) {

                try {
                    String[] myData = myBizTransaction.split("\\(");

                    myData[1] = myData[1].split("\\)")[0];

                    BusinessTransactionType b = new BusinessTransactionType();

                    b.setType(myData[0]);
                    b.setValue(myData[1]);

                    businessTransactionListType.getBizTransaction().add(b);
                } catch (Exception ex) {
                    ex.printStackTrace();

                    QueryParameterException         qpe  = objectFactory.createQueryParameterException();
                    QueryParameterExceptionResponse myEx = new QueryParameterExceptionResponse("Business Transaction Encoding not valid for: " + myBizTransaction, qpe);

                    throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(myEx)).entity(myEx.getMessage()).build());
                }
            }
        }

        BusinessLocationType businessLocationType = objectFactory.createBusinessLocationType();

        businessLocationType.setId(businessLocation);

        ReadPointType readPointType = objectFactory.createReadPointType();

        readPointType.setId(readPoint);

        if (eventType.equals(Config.OBJECT_EVENT)) {
            ObjectEventType myEventType = objectFactory.createObjectEventType();

            ePCISEventType = myEventType;

            if (!businessLocation.equals("")) {
                myEventType.setBizLocation(businessLocationType);
            }

            if (!businessStep.equals("")) {
                myEventType.setBizStep(businessStep);
            }

            if (!businessTransaction.equals("")) {
                myEventType.setBizTransactionList(businessTransactionListType);
            }

            if (!disposition.equals("")) {
                myEventType.setDisposition(disposition);
            }

            if (!readPoint.equals("")) {
                myEventType.setReadPoint(readPointType);
            }

            if (!action.equals("")) {
                myEventType.setAction(actionType);
            }

            if (!epc.equals("")) {
                myEventType.setEpcList(ePCListType);
            }
        }

        if (eventType.equals(Config.AGGREGATION_EVENT)) {
            AggregationEventType myEventType = objectFactory.createAggregationEventType();

            ePCISEventType = myEventType;

            if (!businessLocation.equals("")) {
                myEventType.setBizLocation(businessLocationType);
            }

            if (!businessStep.equals("")) {
                myEventType.setBizStep(businessStep);
            }

            if (!businessTransaction.equals("")) {
                myEventType.setBizTransactionList(businessTransactionListType);
            }

            if (!disposition.equals("")) {
                myEventType.setDisposition(disposition);
            }

            if (!readPoint.equals("")) {
                myEventType.setReadPoint(readPointType);
            }

            if (!parentId.equals("")) {
                myEventType.setParentID(parentId);
            }

            if (!action.equals("")) {
                myEventType.setAction(actionType);
            }

            if (!epc.equals("")) {
                myEventType.setChildEPCs(ePCListType);
            }
        }

        if (eventType.equals(Config.TRANSACTION_EVENT)) {
            TransactionEventType myEventType = objectFactory.createTransactionEventType();

            ePCISEventType = myEventType;

            if (!businessLocation.equals("")) {
                myEventType.setBizLocation(businessLocationType);
            }

            if (!businessStep.equals("")) {
                myEventType.setBizStep(businessStep);
            }

            if (!businessTransaction.equals("")) {
                myEventType.setBizTransactionList(businessTransactionListType);
            }

            if (!disposition.equals("")) {
                myEventType.setDisposition(disposition);
            }

            if (!readPoint.equals("")) {
                myEventType.setReadPoint(readPointType);
            }

            if (!parentId.equals("")) {
                myEventType.setParentID(parentId);
            }

            if (!action.equals("")) {
                myEventType.setAction(actionType);
            }

            if (!epc.equals("")) {
                myEventType.setEpcList(ePCListType);
            }
        }

        if (eventType.equals(Config.QUANTITY_EVENT)) {
            QuantityEventType myEventType = objectFactory.createQuantityEventType();

            ePCISEventType = myEventType;

            if (!businessLocation.equals("")) {
                myEventType.setBizLocation(businessLocationType);
            }

            if (!businessStep.equals("")) {
                myEventType.setBizStep(businessStep);
            }

            if (!businessTransaction.equals("")) {
                myEventType.setBizTransactionList(businessTransactionListType);
            }

            if (!disposition.equals("")) {
                myEventType.setDisposition(disposition);
            }

            if (!readPoint.equals("")) {
                myEventType.setReadPoint(readPointType);
            }

            if (!quantity.equals("")) {
                myEventType.setQuantity(Integer.parseInt(quantity));
            }

            if (!epcClass.equals("")) {
                myEventType.setEpcClass(epcClass);
            }
        }

        if (ePCISEventType == null) {
            QueryParameterException qpe     = objectFactory.createQueryParameterException();
            String                  message = "Event type not valid for value: " + eventType;

            if (eventType.equals("")) {
                message = "Event type is required";
            }

            QueryParameterExceptionResponse ex = new QueryParameterExceptionResponse(message, qpe);

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        if (eventTime.equals(Config.NOW)) {
            eventTime = ActualDateTime.GET_NOW();
        }

        ePCISEventType.setEventTime(ActualDateTime.GET_TIME_XMLGC(eventTime + timeZoneOffset));
        ePCISEventType.setEventTimeZoneOffset(timeZoneOffset);
        ePCISEventType.setRecordTime(ActualDateTime.GET_NOW_XMLGC());

        // create the EPCISDocument containing the Event
        EPCISDocumentType epcisDocumentType = new EPCISDocumentType();
        EPCISBodyType     epcisBodyType     = new EPCISBodyType();
        EventListType     eventListType     = new EventListType();

        eventListType.getObjectEventOrAggregationEventOrQuantityEvent().add(ePCISEventType);
        epcisBodyType.setEventList(eventListType);
        epcisDocumentType.setEPCISBody(epcisBodyType);
        epcisDocumentType.setSchemaVersion(new BigDecimal("1.0"));
        epcisDocumentType.setCreationDate(ActualDateTime.GET_NOW_XMLGC());

        // create the EPCISDocument XML String containing the Event
        StringWriter writer = new StringWriter();

        try {
            JAXBContext jaxbContext;

            jaxbContext = JAXBContext.newInstance("org.epcis.fosstrak.restadapter.ws.generated");

            JAXBElement<EPCISDocumentType> item       = objectFactory.createEPCISDocument(epcisDocumentType);
            Marshaller                     marshaller = jaxbContext.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(item, writer);
        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        String event = writer.toString();


        return addCapture(context, event);
    }
}
