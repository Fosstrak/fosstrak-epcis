/* Copyright (c) 2006 ETH Zurich, Switzerland.
 * All rights reserved.
 *
 * For copying and distribution information, please see the file
 * LICENSE.
 */

package org.accada.epcis.captureclient;

import java.util.Vector;

/**
 * Implements a class that holds examples for the EPCIS Capture Interface
 * Client. Uses class CaptureInterfaceEventExample to store them.
 *
 * @author David Gubler
 *
 */
public class CaptureClientGuiExampleEvents {
    /**
     * Vector that holds all the examples.
     */
    public Vector<CaptureEvent> examples = new Vector<CaptureEvent>();

    /**
     * Constructor. Sets up the examples. Add examples here if you wish.
     *
     */
    public CaptureClientGuiExampleEvents() {


        CaptureEvent ex;

        ex = new CaptureEvent();
        ex.setDescription("DEMO 1: Prototype has been assigned a new EPC");
        ex.setType(0);
        ex.setEventTime("2006-09-20T06:36:17");
        ex.setAction(0);
        ex.setBizStep("urn:accada:demo:bizstep:fmcg:production");
        ex.setDisposition("urn:accada:demo:disp:fmcg:pendingQA");
        ex.setBizLocation("urn:accada:demo:fmcg:ssl:0037000.00729.210");
        ex.setReadPoint("urn:accada:demo:fmcg:ssl:0037000.00729.210,432");
        ex.setEpcList("urn:epc:id:sgtin:0057000.123780.7788");
        examples.add(ex);

        ex = new CaptureEvent();
        ex.setDescription("DEMO 2: Prototype passed reader in QA lab");
        ex.setType(0);
        ex.setEventTime("2006-09-20T07:33:31.116");
        ex.setAction(1);
        ex.setBizStep("urn:accada:demo:bizstep:fmcg:production");
        ex.setBizLocation("urn:accada:demo:fmcg:ssl:0037000.00729.210");
        ex.setReadPoint("urn:accada:demo:fmcg:ssl:0037000.00729.210,414");
        ex.setEpcList("urn:epc:id:sgtin:0057000.123780.7788");
        examples.add(ex);
        
        ex = new CaptureEvent();
        ex.setDescription("DEMO 3: Prototype finished QA process.");
        ex.setType(3);
        ex.setEventTime("2006-09-20T07:53:01");
        ex.setAction(0);
        ex.setBizTransaction("urn:accada:demo:biztrans:fmcg:QApassed",
                "http://demo.accada.org/QAtracker/q3432q4324");
        ex.setBizLocation("urn:accada:demo:fmcg:ssl:0037000.00729.210");
        ex.setReadPoint("urn:accada:demo:fmcg:ssl:0037000.00729.210,414");
        ex.setEpcList("urn:epc:id:sgtin:0057000.123780.7788");
        examples.add(ex);        
        
        ex = new CaptureEvent();
        ex.setDescription("DEMO 4: Reader and other products are "
                + "aggregated onto a pallet.");
        ex.setType(1);
        ex.setAction(0);
        ex.setEventTime("2006-09-20T08:55:04");
        ex.setBizStep("urn:accada:demo:bizstep:fmcg:pickandpack");
        ex.setDisposition("urn:accada:demo:disp:fmcg:readyforpickup");
        ex.setBizLocation("urn:accada:demo:fmcg:ssl:0037000.00729.450");
        ex.setReadPoint("urn:accada:demo:fmcg:ssl:0037000.00729.450,9");
        ex.setBizTransaction("urn:accada:demo:fmcg:btt:po",
                "http://transaction.accada.org/po/12345678");
        ex.setParentID("urn:x:bar:5:036544:007325");
        ex.setChildEPCs("urn:epc:id:sgtin:0057000.123780.7788 "
                + "urn:epc:id:sgtin:0057000.123430.2027 "
                + "urn:epc:id:sgtin:0057000.123430.2028"
                + "urn:epc:id:sgtin:0057000.123430.2029");
        examples.add(ex);
       
        ex = new CaptureEvent();
        ex.setDescription("DEMO 5: Tag has been read "
                + "at port of Kaohsiung together with other tags");
        ex.setType(0);
        ex.setEventTime("2006-09-20T10:33:31.116");
        ex.setAction(1);
        ex.setBizStep("urn:accada:demo:bizstep:fmcg:shipment");
        ex.setBizLocation("urn:accada:demo:RepublicOfChina:Kaohsiung");
        ex.setReadPoint("urn:accada:demo:fmcg:ssl:0037200.00729.210,414");
        ex.setEpcList("urn:epc:id:sgtin:0057000.123780.7788 "
                + "urn:epc:id:sgtin:0057000.123430.2027 "
                + "urn:epc:id:sgtin:0057000.123430.2028"
                + "urn:epc:id:sgtin:0057000.123430.2029");
        examples.add(ex);
        
        ex = new CaptureEvent();
        ex.setDescription("DEMO 6: Tag has been read "
                + "at port of Rotterdam together with other tags");
        ex.setType(0);
        ex.setEventTime("2006-09-20T12:33:31.116");
        ex.setAction(1);
        ex.setBizStep("urn:accada:demo:bizstep:fmcg:shipment");
        ex.setBizLocation("urn:accada:demo:Netherlands:Rotterdam");
        ex.setReadPoint("urn:accada:demo:fmcg:ssl:0037200.00729.210,234");
        ex.setEpcList("urn:epc:id:sgtin:0057000.123780.7788 "
                + "urn:epc:id:sgtin:0057000.123430.2027 "
                + "urn:epc:id:sgtin:0057000.123430.2028"
                + "urn:epc:id:sgtin:0057000.123430.2029");
        examples.add(ex);
        
        
        ex = new CaptureEvent();
        ex.setDescription("Object has passed a reader "
                + "during the manufacturing process");
        ex.setType(0);
        ex.setEventTime("2006-04-03T20:33:31.116");
        ex.setAction(1);
        ex.setBizStep("urn:epcglobal:epcis:bizstep:fmcg:production");
        ex.setBizLocation("urn:epcglobal:fmcg:ssl:0037000.00729.210");
        ex.setReadPoint("urn:epcglobal:fmcg:ssl:0037000.00729.210,414");
        ex.setEpcList("urn:epc:id:sgtin:0057000.123780.3167");
        examples.add(ex);

        ex = new CaptureEvent();
        ex.setDescription("An object has been assigned a new EPC");
        ex.setType(0);
        ex.setEventTime("2006-04-03T22:36:17");
        ex.setAction(0);
        ex.setBizStep("urn:epcglobal:epcis:bizstep:fmcg:production");
        ex.setDisposition("urn:epcglobal:epcis:disp:fmcg:readyforuse");
        ex.setBizLocation("urn:epcglobal:fmcg:ssl:0037000.00729.210");
        ex.setReadPoint("urn:epcglobal:fmcg:ssl:0037000.00729.210,432");
        ex.setEpcList("urn:epc:id:sgtin:0057000.123780.7788");
        examples.add(ex);

        ex = new CaptureEvent();
        ex.setDescription("Two pallets (identified by EPCs) have been "
                + "loaded onto a truck");
        ex.setType(0);
        ex.setEventTime("2006-05-09T21:01:44");
        ex.setAction(1);
        ex.setBizStep("urn:epcglobal:epcis:bizstep:fmcg:loading");
        ex.setDisposition("urn:epcglobal:epcis:disp:fmcg:transit");
        ex.setBizLocation("urn:epcglobal:fmcg:ssl:0037000.00729.215");
        ex.setReadPoint("urn:epcglobal:fmcg:ssl:0037000.00729.215,803");
        ex.setEpcList("urn:epc:id:sgtin:0034000.987650.2686 "
                + "urn:epc:id:sgtin:0034000.987650.3542");
        ex.setBizTransaction("urn:epcglobal:fmcg:btt:po",
                "http://transaction.acme.com/po/12345678");
        examples.add(ex);

        ex = new CaptureEvent();
        ex.setDescription("An object has arrived for repair");
        ex.setType(0);
        ex.setEventTime("2006-05-10T04:50:35");
        ex.setAction(1);
        ex.setBizStep("urn:epcglobal:epcis:bizstep:fmcg:received");
        ex.setDisposition("urn:epcglobal:epcis:disp:fmcg:inrepair");
        ex.setBizLocation("urn:epcglobal:fmcg:ssl:0037000.00811.217");
        ex.setReadPoint("urn:epcglobal:fmcg:ssl:0037000.00811.217,058");
        ex.setEpcList("urn:epc:id:sgtin:0034000.987650.2686");
        examples.add(ex);

        ex = new CaptureEvent();
        ex.setDescription("Three objects have been aggregated onto "
                + "a barcode-labeled pallet");
        ex.setType(1);
        ex.setAction(0);
        ex.setEventTime("2006-06-01T15:55:04");
        ex.setBizStep("urn:epcglobal:epcis:bizstep:fmcg:pickandpack");
        ex.setDisposition("urn:epcglobal:epcis:disp:fmcg:readyforpickup");
        ex.setBizLocation("urn:epcglobal:fmcg:ssl:0037000.00729.450");
        ex.setReadPoint("urn:epcglobal:fmcg:ssl:0037000.00729.450,9");
        ex.setBizTransaction("urn:epcglobal:fmcg:btt:po",
                "http://transaction.acme.com/po/12345678");
        ex.setBizTransaction("urn:epcglobal:fmcg:btt:asn",
                "http://transaction.acme.com/asn/1152");
        ex.setParentID("urn:x:bar:5:036544:007325");
        ex.setChildEPCs("urn:epc:id:sgtin:0057000.123430.2025 "
                + "urn:epc:id:sgtin:0057000.123430.2027 "
                + "urn:epc:id:sgtin:0057000.123430.2028");
        examples.add(ex);

        ex = new CaptureEvent();
        ex.setDescription("Aggregation ended at customer's site");
        ex.setType(1);
        ex.setAction(2);
        ex.setEventTime("2006-06-05T09:26:06");
        ex.setBizLocation("urn:epcglobal:fmcg:ssl:0066000.00101.032");
        ex.setReadPoint("urn:epcglobal:fmcg:ssl:0066000.00101.450,009");
        ex.setBizTransaction("urn:epcglobal:fmcg:btt:po",
        	"http://trans.customer.com/po/E58J3Q");
        ex.setBizTransaction("urn:epcglobal:fmcg:btt:asn",
        	"http://transaction.acme.com/asn/1152");
        ex.setParentID("urn:x:bar:5:036544:007325");
        examples.add(ex);

        ex = new CaptureEvent();
        ex.setDescription("Physical inventory count: 67 parts counted");
        ex.setType(2);
        ex.setEventTime("2006-01-15T16:15:31");
        ex.setBizStep("urn:epcglobal:epcis:bizstep:fmcg:physinv");
        ex.setDisposition("urn:epcglobal:epcis:disp:fmcg:readyforuse");
        ex.setBizLocation("urn:epcglobal:fmcg:ssl:0066000.00102.007");
        ex.setReadPoint("urn:epcglobal:fmcg:ssl:0066000.00102.014,001");
        ex.setEpcClass("urn:epc:id:sgtin:0069000.957110");
        ex.setQuantity(67);
        examples.add(ex);

        ex = new CaptureEvent();
        ex.setDescription("1000 pieces have been produced and can be found "
                + "at the production site");
        ex.setType(2);
        ex.setEventTime("2006-08-10T18:14:00");
        ex.setBizLocation("urn:epcglobal:fmcg:ssl:0037000.00729.450");
        ex.setReadPoint("urn:epcglobal:fmcg:ssl:0037000.00729.451,2");
        ex.setEpcClass("urn:epc:id:sgtin:0069000.919923");
        ex.setQuantity(1000);
        examples.add(ex);

        ex = new CaptureEvent();
        ex.setDescription("Order changed by customer - two "
                + "more objects added to transaction");
        ex.setType(3);
        ex.setEventTime("2006-08-18T11:53:01");
        ex.setAction(0);
        ex.setBizTransaction("urn:epcglobal:fmcg:btt:po",
                "http://transaction.acme.com/tracker/6677150");
        ex.setEpcList("urn:epc:id:sgtin:0057000.678930.5003 "
                + "urn:epc:id:sgtin:0057000.678930.5004");
        examples.add(ex);

        ex = new CaptureEvent();
        ex.setDescription("Transaction is finished");
        ex.setType(3);
        ex.setEventTime("2006-08-20T07:03:51");
        ex.setAction(2);
        ex.setBizTransaction("urn:epcglobal:fmcg:btt:po",
                "http://transaction.acme.com/tracker/6677150");
        ex.setEpcList("urn:epc:id:sgtin:0057000.678930.5003 "
                + "urn:epc:id:sgtin:0057000.678930.5004");
        examples.add(ex);
    }
}
