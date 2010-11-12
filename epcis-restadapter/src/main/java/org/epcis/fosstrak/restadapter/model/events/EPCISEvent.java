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
package org.epcis.fosstrak.restadapter.model.events;

import org.epcis.fosstrak.restadapter.model.epc.ElectronicProductCode;
import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.util.URI;
import org.epcis.fosstrak.restadapter.model.Entry;
import org.epcis.fosstrak.restadapter.util.TimeParser;
import org.epcis.fosstrak.restadapter.ws.generated.ActionType;
import org.epcis.fosstrak.restadapter.ws.generated.AggregationEventType;
import org.epcis.fosstrak.restadapter.ws.generated.BusinessLocationType;
import org.epcis.fosstrak.restadapter.ws.generated.BusinessTransactionListType;
import org.epcis.fosstrak.restadapter.ws.generated.BusinessTransactionType;
import org.epcis.fosstrak.restadapter.ws.generated.EPCISEventType;
import org.epcis.fosstrak.restadapter.ws.generated.EPCListType;
import org.epcis.fosstrak.restadapter.ws.generated.ObjectEventType;
import org.epcis.fosstrak.restadapter.ws.generated.QuantityEventType;
import org.epcis.fosstrak.restadapter.ws.generated.ReadPointType;
import org.epcis.fosstrak.restadapter.ws.generated.TransactionEventType;
import org.epcis.fosstrak.restadapter.config.URIConstants;
import org.epcis.fosstrak.restadapter.config.QueryParamConstants;
import org.epcis.fosstrak.restadapter.ws.epcis.EPCISWebServiceClient;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * The JAXB class to handle Events of the EPCIS REST Adapter model
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public abstract class EPCISEvent {

    public static final String TimeZoneOffset       = "Time Zone Offset";
    public static final String EventTime            = "Event Time";
    public static final String RecordTime           = "Record Time";
    public static final String BusinessStep         = "Business Step";
    public static final String ReadPoint            = "Read Point";
    public static final String BusinessLocation     = "Business Location";
    public static final String Disposition          = "Disposition";
    public static final String Type                 = "Event Type";
    public static final String Epcs                 = "EPC List";
    public static final String Epc                  = "EPC";
    public static final String Action               = "Action";
    public static final String ParentID             = "Parent ID";
    public static final String EpcClass             = "EPC Class";
    public static final String Quantity             = "Quantity";
    public static final String BusinessTransactions = "Business Transaction List";
    public static final String BusinessTransaction  = "Business Transaction";

    /**
     * Method description
     *
     *
     * @param identifier
     *
     * @return
     */
    public static String translateQueryParamName(String identifier) {
        if (identifier.equals(TimeZoneOffset)) {
            return "";
        }

        if (identifier.equals(EventTime)) {
            return QueryParamConstants.EVENT_TIME_REST;
        }

        if (identifier.equals(RecordTime)) {
            return QueryParamConstants.RECORD_TIME_REST;
        }

        if (identifier.equals(BusinessStep)) {
            return QueryParamConstants.BUSINESS_STEP_REST;
        }

        if (identifier.equals(ReadPoint)) {
            return QueryParamConstants.READ_POINT_REST;
        }

        if (identifier.equals(BusinessLocation)) {
            return QueryParamConstants.BUSINESS_LOCATION_REST;
        }

        if (identifier.equals(Disposition)) {
            return QueryParamConstants.DISPOSITION_REST;
        }

        if (identifier.equals(Type)) {
            return QueryParamConstants.EVENT_TYPE_REST;
        }

        if (identifier.equals(Epc)) {
            return QueryParamConstants.EPC_REST;
        }

        if (identifier.equals(Action)) {
            return QueryParamConstants.ACTION_REST;
        }

        if (identifier.equals(ParentID)) {
            return QueryParamConstants.PARENT_ID_REST;
        }

        if (identifier.equals(EpcClass)) {
            return QueryParamConstants.EPC_CLASS_REST;
        }

        if (identifier.equals(Quantity)) {
            return QueryParamConstants.QUANTITY_REST;
        }

        if (identifier.equals(BusinessTransaction)) {
            return QueryParamConstants.BUSINESS_TRANSACTION_TYPE_REST;
        }

        return "unknown";
    }

    /**
     * Constructs and empty EPCISEvent (used by JAXB).
     *
     */
    public EPCISEvent() {}

    /**
     * Constructs and EPCIS Event.
     * @param event
     * @param index
     */
    public EPCISEvent(EPCISEventType event) {
        initEvent(event);
    }

    protected abstract void initFillSpecificData();

    @XmlTransient
    protected XMLGregorianCalendar        eventTime          = null;
    @XmlTransient
    protected XMLGregorianCalendar        recordTime         = null;
    @XmlTransient
    protected String                      timeZoneOffset     = null;
    @XmlTransient
    protected String                      bizStep            = null;
    @XmlTransient
    protected String                      disposition        = null;
    @XmlTransient
    protected ReadPointType               readPoint          = null;
    @XmlTransient
    protected BusinessLocationType        bizLocation        = null;
    @XmlTransient
    protected EPCListType                 epcList            = null;
    @XmlTransient
    protected ActionType                  action             = null;
    @XmlTransient
    protected BusinessTransactionListType bizTransactionList = null;
    @XmlTransient
    protected EPCListType                 childEPC           = null;
    @XmlTransient
    protected String                      parentID           = null;
    @XmlTransient
    protected int                         quantity           = Integer.MIN_VALUE;
    @XmlTransient
    protected String                      epcClass           = null;
    @XmlTransient
    protected String                      type               = "EPCISEvent";


    // @XmlElement(name = "restfulid")
    protected Entry restfulIDEntry = null;

    // @XmlElement(name = "eventTime")
    protected Entry eventTimeEntry = null;

    // @XmlElement(name = "recordTime")
    protected Entry recordTimeEntry = null;

    // @XmlElement(name = "timeZoneOffset")
    protected Entry timeZoneOffsetEntry = null;

    // @XmlElement(name = "bizStep")
    protected Entry bizStepEntry = null;

    // @XmlElement(name = "disposition")
    protected Entry dispositionEntry = null;

    // @XmlElement(name = "readPoint")
    protected Entry readPointEntry = null;

    // @XmlElement(name = "bizLocation")
    protected Entry bizLocationEntry = null;

    // @XmlElement(name = "epcs")
    protected List<ElectronicProductCode> epcEntry = new LinkedList<ElectronicProductCode>();

    // @XmlElement(name = "action")
    protected Entry actionEntry = null;

    // @XmlElement(name = "parentID")
    protected Entry parentIDEntry = null;

    // @XmlElement(name = "bizTransactions")
    protected List<Entry> bizTransactionEntry = new LinkedList<Entry>();

    // @XmlElement(name = "quantity")
    protected Entry quantityEntry = null;

    // @XmlElement(name = "epcClass")
    protected Entry epcClassEntry = null;

    // @XmlElement(name = "type")
    protected Entry typeEntry         = null;
    protected Entry epcsEntry         = null;
    protected Entry transactionsEntry = null;

    /**
     * Method description
     *
     *
     * @param epcisEventType
     */
    public void initEvent(EPCISEventType epcisEventType) {
        if (epcisEventType instanceof ObjectEventType) {
            initObjectEvent((ObjectEventType) epcisEventType);
        }

        if (epcisEventType instanceof AggregationEventType) {
            initAggregationEvent((AggregationEventType) epcisEventType);
        }

        if (epcisEventType instanceof QuantityEventType) {
            initQuantityEvent((QuantityEventType) epcisEventType);
        }

        if (epcisEventType instanceof TransactionEventType) {
            initTransactionEvent((TransactionEventType) epcisEventType);
        }

        initFillData();
    }

    protected void initObjectEvent(ObjectEventType event) {
        type               = Config.OBJECT_EVENT;
        action             = event.getAction();
        bizLocation        = event.getBizLocation();
        bizStep            = event.getBizStep();
        bizTransactionList = event.getBizTransactionList();
        disposition        = event.getDisposition();
        epcList            = event.getEpcList();
        eventTime          = event.getEventTime();
        timeZoneOffset     = event.getEventTimeZoneOffset();
        readPoint          = event.getReadPoint();
        recordTime         = event.getRecordTime();

//      extensionObjectEventType = event.getExtension();
//      otherAttributes = event.getOtherAttributes();
//      any = event.getAny();
//      baseExtension = event.getBaseExtension();
    }

    protected void initAggregationEvent(AggregationEventType event) {
        type               = Config.AGGREGATION_EVENT;
        action             = event.getAction();
        bizLocation        = event.getBizLocation();
        bizStep            = event.getBizStep();
        bizTransactionList = event.getBizTransactionList();
        childEPC           = event.getChildEPCs();
        disposition        = event.getDisposition();
        eventTime          = event.getEventTime();
        timeZoneOffset     = event.getEventTimeZoneOffset();
        parentID           = event.getParentID();
        readPoint          = event.getReadPoint();
        recordTime         = event.getRecordTime();

//      extensionAggregationEventType = event.getExtension();
//      otherAttributes = event.getOtherAttributes();
//      any = event.getAny();
//      baseExtension = event.getBaseExtension();
    }

    protected void initQuantityEvent(QuantityEventType event) {
        type               = Config.QUANTITY_EVENT;
        bizLocation        = event.getBizLocation();
        bizStep            = event.getBizStep();
        bizTransactionList = event.getBizTransactionList();
        disposition        = event.getDisposition();
        epcClass           = event.getEpcClass();
        eventTime          = event.getEventTime();
        timeZoneOffset     = event.getEventTimeZoneOffset();
        quantity           = event.getQuantity();
        readPoint          = event.getReadPoint();
        recordTime         = event.getRecordTime();

//      extensionAggregationEventType = event.getExtension();
//      otherAttributes = event.getOtherAttributes();
//      any = event.getAny();
//      baseExtension = event.getBaseExtension();
    }

    protected void initTransactionEvent(TransactionEventType event) {
        type               = Config.TRANSACTION_EVENT;
        action             = event.getAction();
        bizLocation        = event.getBizLocation();
        bizStep            = event.getBizStep();
        bizTransactionList = event.getBizTransactionList();
        disposition        = event.getDisposition();
        epcList            = event.getEpcList();
        eventTime          = event.getEventTime();
        timeZoneOffset     = event.getEventTimeZoneOffset();
        parentID           = event.getParentID();
        readPoint          = event.getReadPoint();
        recordTime         = event.getRecordTime();

//      extensionAggregationEventType = event.getExtension();
//      otherAttributes = event.getOtherAttributes();
//      any = event.getAny();
//      baseExtension = event.getBaseExtension();
    }

    protected String getType() {

        // this one is safe, never null
        return type;
    }

    protected String getEventTime() {
        String res = "";

        if (eventTime != null) {
            res = TimeParser.format(eventTime.toGregorianCalendar());
        }

        return res;
    }

    protected String getRecordTime() {
        String res = "";

        if (recordTime != null) {
            res = TimeParser.format(recordTime.toGregorianCalendar());
        }

        return res;
    }

    protected String getTimeZoneOffset() {
        String res = "";

        if (timeZoneOffset != null) {
            res = timeZoneOffset;
        }

        return res;
    }

    protected String getBizStep() {
        String res = "";

        if (bizStep != null) {
            res = bizStep;
        }

        return res;
    }

    protected String getDisposition() {
        String res = "";

        if (disposition != null) {
            res = disposition;
        }

        return res;
    }

    protected String getReadPoint() {
        String res = Config.NO_VALUE;

        if (readPoint != null) {
            res = readPoint.getId();
        }

        return res;
    }

    protected String getBizLocation() {
        String res = Config.NO_VALUE;

        if (bizLocation != null) {
            res = bizLocation.getId();
        }

        return res;
    }

    protected List<Entry> getBizTransactionList() {
        List<Entry> res = new LinkedList<Entry>();

        if (bizTransactionList != null) {
            for (BusinessTransactionType businessTransactionType : bizTransactionList.getBizTransaction()) {
                String myValue = businessTransactionType.getValue();
                String myType  = businessTransactionType.getType();
                Entry  myEntry = new Entry();

                myEntry.setValue(myType + Config.PARAM_START + myValue + Config.PARAM_END);
                res.add(myEntry);
            }
        }

        return res;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "restfulID")
    public Entry getRestfulIDEntry() {
        return restfulIDEntry;
    }

    /**
     * Method description
     *
     *
     * @param restfulID
     */
    public void setRestfulIDEntry(Entry restfulID) {
        this.restfulIDEntry = restfulID;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "eventTime")
    public Entry getEventTimeEntry() {
        return eventTimeEntry;
    }

    /**
     * Method description
     *
     *
     * @param eventTimeEntry
     */
    public void setEventTimeEntry(Entry eventTimeEntry) {
        this.eventTimeEntry = eventTimeEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "recordTime")
    public Entry getRecordTimeEntry() {
        return recordTimeEntry;
    }

    /**
     * Method description
     *
     *
     * @param recordTimeEntry
     */
    public void setRecordTimeEntry(Entry recordTimeEntry) {
        this.recordTimeEntry = recordTimeEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "timeZoneOffset")
    public Entry getTimeZoneOffsetEntry() {
        return timeZoneOffsetEntry;
    }

    /**
     * Method description
     *
     *
     * @param timeZoneOffsetEntry
     */
    public void setTimeZoneOffsetEntry(Entry timeZoneOffsetEntry) {
        this.timeZoneOffsetEntry = timeZoneOffsetEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "bizStep")
    public Entry getBizStepEntry() {
        return bizStepEntry;
    }

    /**
     * Method description
     *
     *
     * @param bizStepEntry
     */
    public void setBizStepEntry(Entry bizStepEntry) {
        this.bizStepEntry = bizStepEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "disposition")
    public Entry getDispositionEntry() {
        return dispositionEntry;
    }

    /**
     * Method description
     *
     *
     * @param dispositionEntry
     */
    public void setDispositionEntry(Entry dispositionEntry) {
        this.dispositionEntry = dispositionEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "readPoint")
    public Entry getReadPointEntry() {
        return readPointEntry;
    }

    /**
     * Method description
     *
     *
     * @param readPointEntry
     */
    public void setReadPointEntry(Entry readPointEntry) {
        this.readPointEntry = readPointEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "bizLocation")
    public Entry getBizLocationEntry() {
        return bizLocationEntry;
    }

    /**
     * Method description
     *
     *
     * @param bizLocationEntry
     */
    public void setBizLocationEntry(Entry bizLocationEntry) {
        this.bizLocationEntry = bizLocationEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "type")
    public Entry getTypeEntry() {
        return typeEntry;
    }

    /**
     * Method description
     *
     *
     * @param typeEntry
     */
    public void setTypeEntry(Entry typeEntry) {
        this.typeEntry = typeEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElementWrapper(name = "businessTransactions")
    @XmlElement(name = "businessTransaction")
    public List<Entry> getBizTransactionEntry() {
        return bizTransactionEntry;
    }

    /**
     * Method description
     *
     *
     * @param bizTransactionEntry
     */
    public void setBizTransactionEntry(List<Entry> bizTransactionEntry) {
        this.bizTransactionEntry = bizTransactionEntry;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "businessTransactionList")
    public Entry getBizTransactionsEntry() {
        return transactionsEntry;
    }

    /**
     * Method description
     *
     *
     * @param bizTransactionsEntry
     */
    public void setBizTransactionsEntry(Entry bizTransactionsEntry) {
        this.transactionsEntry = bizTransactionsEntry;
    }

    protected void initFillData() {
        restfulIDEntry = new Entry();

        if (!getEventTime().equals("")) {
            eventTimeEntry = new Entry();
            eventTimeEntry.setValue(getEventTime());
        }

        if (!getRecordTime().equals("")) {
            recordTimeEntry = new Entry();
            recordTimeEntry.setValue(getRecordTime());
        }

        if (!getTimeZoneOffset().equals("")) {
            timeZoneOffsetEntry = new Entry();
            timeZoneOffsetEntry.setValue(getTimeZoneOffset());
        }

        if (!getBizStep().equals("")) {
            bizStepEntry = new Entry();
            bizStepEntry.setValue(getBizStep());
        }

        if (!getDisposition().equals("")) {
            dispositionEntry = new Entry();
            dispositionEntry.setValue(getDisposition());
        }

        if (!getReadPoint().equals("")) {
            readPointEntry = new Entry();
            readPointEntry.setValue(getReadPoint());
        }

        if (!getBizLocation().equals("")) {
            bizLocationEntry = new Entry();
            bizLocationEntry.setValue(getBizLocation());
        }

        if (!getType().equals("")) {
            typeEntry = new Entry();
            typeEntry.setValue(getType());
        }

        if (!getBizTransactionList().isEmpty()) {
            transactionsEntry = new Entry();
            transactionsEntry.setName(BusinessTransactions);
            transactionsEntry.setNameRef(URI.buildEventIdLink(URIConstants.BUSINESS_TRANSACTIONS, transactionsEntry, bizLocationEntry.getValue(), readPointEntry.getValue(), eventTimeEntry.getValue()));
            setBizTransactionEntry(getBizTransactionList());
        }

    }

    private void setUpEntryCollection(Entry entry, String name, String valueRefQueryParam, String collectionName, int entryIndex, int index) {
        setUpEntry(entry, name, valueRefQueryParam, index);

        String nameRef = URI.buildEventIdLink(collectionName, entry, bizLocationEntry.getValue(), readPointEntry.getValue(), eventTimeEntry.getValue());

        nameRef = URI.addSubPath(nameRef, "/" + entryIndex++);

        if (index != 1) {
            nameRef = URI.addQueryParameter(nameRef, URIConstants.INDEX_QP, index + "");
        }

        entry.setNameRef(nameRef);
    }

    private void setUpEntry(Entry entry, String name, String valueRefQueryParam, int index) {
        if (entry != null) {
            String value   = entry.getValue();
            String nameRef = URI.buildEventIdLink(translateQueryParamName(name), entry, bizLocationEntry.getValue(), readPointEntry.getValue(), eventTimeEntry.getValue());

            if (index != 1) {
                nameRef = URI.buildEventIdLink(translateQueryParamName(name), entry, bizLocationEntry.getValue(), readPointEntry.getValue(), eventTimeEntry.getValue(), index + "");
            }

            String valueRef = URI.buildOneDimentionalQueryLinkFromParameter(valueRefQueryParam, value);

            entry.setName(name);
            entry.setNameRef(nameRef);
            entry.setValueRef(valueRef);

            if (name.equals(BusinessLocation)) {
                if (value.equals(Config.NO_VALUE)) {
                    entry.setValueRef(null);
                }
            }

            if (name.equals(ReadPoint)) {
                if (value.equals(Config.NO_VALUE)) {
                    entry.setValueRef(null);
                }
            }
        }
    }

    /**
     * Method description
     *
     */
    public void calculateRESTfulPathID() {
        restfulIDEntry.setName("RESTful Path ID");
        restfulIDEntry.setValue("ID");

        int id;

        id = EPCISWebServiceClient.getEventPathID(this);

        String myBusinessLocation = getBizLocationEntry().getValue();
        String myReadPoint        = getReadPointEntry().getValue();
        String myEventTime        = getEventTimeEntry().getValue();

        restfulIDEntry.setValueRef(URI.buildRESTfulEventPathIdLink(myBusinessLocation, myReadPoint, myEventTime, id + ""));
        restfulIDEntry.setDescription("Unique Path ID to represent the Event");

        if (id != 1) {
            setUp(id);

            if (epcsEntry != null) {
                String newNameRef = URI.addQueryParameter(epcsEntry.getNameRef(), URIConstants.INDEX_QP, id + "");

                epcsEntry.setNameRef(newNameRef);
            }

            if (transactionsEntry != null) {
                String newNameRef = URI.addQueryParameter(transactionsEntry.getNameRef(), URIConstants.INDEX_QP, id + "");

                transactionsEntry.setNameRef(newNameRef);
            }


        }
    }

    protected void setUp() {
        setUp(1);
    }

    protected void setUp(int i) {

        // no link to timezoneoffset as there is no such query
        // setUpEntry(timeZoneOffsetEntry, TimeZoneOffset, URIConstants.TIME_ZONE_OFFSET);
        timeZoneOffsetEntry.setName(TimeZoneOffset);
        setUpEntry(eventTimeEntry, EventTime, URIConstants.EVENT_TIME, i);
        setUpEntry(recordTimeEntry, RecordTime, URIConstants.RECORD_TIME, i);
        setUpEntry(bizStepEntry, BusinessStep, URIConstants.BUSINESS_STEP, i);
        setUpEntry(dispositionEntry, Disposition, URIConstants.DISPOSITION, i);
        setUpEntry(readPointEntry, ReadPoint, URIConstants.READ_POINT, i);
        setUpEntry(bizLocationEntry, BusinessLocation, URIConstants.BUSINESS_LOCATION, i);
        setUpEntry(actionEntry, Action, URIConstants.ACTION, i);
        setUpEntry(parentIDEntry, ParentID, URIConstants.PARENT_ID, i);
        setUpEntry(quantityEntry, Quantity, URIConstants.QUANTITY, i);
        setUpEntry(epcClassEntry, EpcClass, URIConstants.EPC_CLASS, i);
        setUpEntry(typeEntry, Type, URIConstants.EVENT_TYPE, i);

        int epcNo = 1;

        for (ElectronicProductCode e : epcEntry) {
            Entry entry = e.getEpc();

            setUpEntryCollection(entry, Epc, URIConstants.EPC, URIConstants.EPCS, epcNo++, i);
        }

        int transactionNo = 1;

        for (Entry e : bizTransactionEntry) {
            setUpEntryCollection(e, BusinessTransaction, URIConstants.BUSINESS_TRANSACTION, URIConstants.BUSINESS_TRANSACTIONS, transactionNo++, i);
        }
    }

    /**
     * Method description
     *
     *
     * @param otherEvent
     *
     * @return
     */
    public boolean isLikeEvent(EPCISEvent otherEvent) {
        boolean res = true;

        try {
            if (!(getBizLocationEntry().getValue().equals(otherEvent.getBizLocationEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            if (!(getBizStepEntry().getValue().equals(otherEvent.getBizStepEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            if (!(getDispositionEntry().getValue().equals(otherEvent.getDispositionEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            if (!(getEventTimeEntry().getValue().equals(otherEvent.getEventTimeEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            if (!(getReadPointEntry().getValue().equals(otherEvent.getReadPointEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            if (!(getRecordTimeEntry().getValue().equals(otherEvent.getRecordTimeEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            if (!(getTimeZoneOffsetEntry().getValue().equals(otherEvent.getTimeZoneOffsetEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            if (!(getBizLocationEntry().getValue().equals(otherEvent.getBizLocationEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            if (!(getTypeEntry().getValue().equals(otherEvent.getTypeEntry().getValue()))) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            if (!isSubclassLikeEvent(otherEvent)) {
                return false;
            }
        } catch (Exception ex) {}

        try {

            List<Entry> myTransactions    = getBizTransactionEntry();
            List<Entry> otherTransactions = otherEvent.getBizTransactionEntry();

            for (Entry myTransaction : myTransactions) {
                boolean isContained = false;

                for (Entry otherTransaction : otherTransactions) {
                    if ((myTransaction.getValue().equals(otherTransaction.getValue()))) {
                        isContained = true;
                    }
                }

                if (!isContained) {
                    return false;
                }
            }

            if (myTransactions.size() != otherTransactions.size()) {
                return false;
            }
        } catch (Exception ex) {}

        return res;
    }

    /**
     * Method description
     *
     *
     * @param otherEvent
     *
     * @return
     */
    public abstract boolean isSubclassLikeEvent(EPCISEvent otherEvent);
}
