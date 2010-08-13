package org.fosstrak.epcis.captureclient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class CaptureClientHelper {

    /**
     * Miscellaneous numeric formats used in formatting.
     */
    public static final DecimalFormat XX_FORMAT = new DecimalFormat("00");
    public static final DecimalFormat XXX_FORMAT = new DecimalFormat("000");
    public static final DecimalFormat XXXX_FORMAT = new DecimalFormat("0000");

    /**
     * The various tooltips.
     */
    public static final String toolTipDate = "Format is ISO 8601, i.e. YYYY-MM-DDThh:mm:ss.SSSZ";
    public static final String toolTipUri = "URI";
    public static final String toolTipUris = "One or multiple URIs, separated by spaces";
    public static final String toolTipInteger = "Integer number";
    public static final String toolTipOptional = ". This field is optional";
    public static final String toolTipBizTransType = "Business Transaction Type";
    public static final String toolTipBizTransID = "Business Transactio ID";

    /**
     * The possible values for the "actions" parameter.
     */
    public static final String[] ACTIONS = { "ADD", "OBSERVE", "DELETE" };

    /**
     * The four possible event types, in human readable form.
     */
    public static final String[] EPCIS_EVENT_NAMES = {
            "Object event", "Aggregation event", "Quantity event", "Transaction event" };


    public enum EpcisEventType {
        
        ObjectEvent(0, "Object event"), AggregationEvent(1, "Aggregation event"), QuantityEvent(2, "Quantity event"),
        TransactionEvent(3, "Transaction event");

        private int guiIndex;
        private String guiName;

        private EpcisEventType(int guiIndex, String guiName) {
            this.guiIndex = guiIndex;
            this.guiName = guiName;
        }
        
        public static EpcisEventType fromGuiIndex(int index) {
            for (EpcisEventType eventType : values()) {
                if (eventType.guiIndex == index) {
                    return eventType;
                }
            }
            return null;
        }
        
        public static String[] guiNames() {
            String[] guiNames = new String[values().length];
            for (int i = 0; i < values().length; i++) {
                guiNames[i] = values()[i].getGuiName();
            }
            return guiNames;
        }

        public String getGuiName() {
            return guiName;
        }
    }
    

    /**
     * Returns the time zone designator in a ISO6601-compliant format from the
     * given <code>Calendar</code> value.
     * 
     * @param cal
     *            The Calendar to be formatted.
     * @return The time zone designator from the given Calendar.
     */
    public static String getTimeZone(final Calendar cal) {
        StringBuilder buf = new StringBuilder();
        TimeZone tz = cal.getTimeZone();
        // determine offset of timezone from UTC (incl. daylight saving)
        int offset = tz.getOffset(cal.getTimeInMillis());
        int hours = Math.abs((offset / (60 * 1000)) / 60);
        int minutes = Math.abs((offset / (60 * 1000)) % 60);
        buf.append(offset < 0 ? '-' : '+');
        buf.append(XX_FORMAT.format(hours));
        buf.append(':');
        buf.append(XX_FORMAT.format(minutes));
        return buf.toString();
    }

    /**
     * Formats a <code>Calendar</code> value into an ISO8601-compliant
     * date/time string.
     * 
     * @param cal
     *            The time value to be formatted into a date/time string.
     * @return The formatted date/time string.
     */
    public static String format(final Calendar cal) {
        if (cal == null) {
            throw new IllegalArgumentException("argument can not be null");
        }

        // determine era and adjust year if necessary
        int year = cal.get(Calendar.YEAR);
        if (cal.isSet(Calendar.ERA) && cal.get(Calendar.ERA) == GregorianCalendar.BC) {
            /**
             * calculate year using astronomical system: year n BCE =>
             * astronomical year -n + 1
             */
            year = 0 - year + 1;
        }

        /**
         * the format of the date/time string is: YYYY-MM-DDThh:mm:ss.SSSTZD
         * note that we cannot use java.text.SimpleDateFormat for formatting
         * because it can't handle years <= 0 and TZD's
         */
        StringBuilder buf = new StringBuilder();
        // year ([-]YYYY)
        buf.append(XXXX_FORMAT.format(year));
        buf.append('-');
        // month (MM)
        buf.append(XX_FORMAT.format(cal.get(Calendar.MONTH) + 1));
        buf.append('-');
        // day (DD)
        buf.append(XX_FORMAT.format(cal.get(Calendar.DAY_OF_MONTH)));
        buf.append('T');
        // hour (hh)
        buf.append(XX_FORMAT.format(cal.get(Calendar.HOUR_OF_DAY)));
        buf.append(':');
        // minute (mm)
        buf.append(XX_FORMAT.format(cal.get(Calendar.MINUTE)));
        buf.append(':');
        // second (ss)
        buf.append(XX_FORMAT.format(cal.get(Calendar.SECOND)));
        buf.append('.');
        // millisecond (SSS)
        buf.append(XXX_FORMAT.format(cal.get(Calendar.MILLISECOND)));
        // time zone designator (+/-hh:mm)
        buf.append(getTimeZone(cal));
        return buf.toString();
    }

    /**
     * Implements a class that holds examples for the EPCIS Capture Interface
     * Client. Uses class CaptureInterfaceEventExample to store them.
     * 
     * @author David Gubler
     */
    public static final class ExampleEvents {
        /**
         * List that holds all the examples.
         */
        public List<CaptureEvent> examples = new ArrayList<CaptureEvent>();

        /**
         * Constructor. Sets up the examples. Add examples here if you wish.
         */
        public ExampleEvents() {
            CaptureEvent ex = new CaptureEvent();
            ex.setDescription("DEMO 1: Item is assigned a new EPC");
            ex.setType(0);
            ex.setEventTime("2006-09-20T06:36:17Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setAction(0);
            ex.setBizStep("urn:epcglobal:cbv:bizstep:commissioning");
            ex.setDisposition("urn:epcglobal:cbv:bizstep:active");
            ex.setBizLocation("urn:epc:id:sgln:0614141.00729.loc5");
            ex.setReadPoint("urn:epc:id:sgln:0614141.00729.rp97");
            ex.setEpcList("urn:epc:id:sgtin:0057000.123780.7788");
            examples.add(ex);

            ex = new CaptureEvent();
            ex.setDescription("DEMO 2: Item is inspected for QA");
            ex.setType(0);
            ex.setEventTime("2006-09-20T07:33:31.116Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setAction(1);
            ex.setBizStep("urn:epcglobal:cbv:bizstep:inspecting");
            ex.setDisposition("urn:epcglobal:cbv:bizstep:active");
            ex.setBizLocation("urn:epc:id:sgln:0614141.00729.loc5");
            ex.setReadPoint("urn:epc:id:sgln:0614141.00729.handheld8");
            ex.setEpcList("urn:epc:id:sgtin:0057000.123780.7788");
            examples.add(ex);

            ex = new CaptureEvent();
            ex.setDescription("DEMO 3: Item is aggregated onto a pallet");
            ex.setType(1);
            ex.setAction(0);
            ex.setEventTime("2006-09-20T08:55:04Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setBizStep("urn:epcglobal:cbv:bizstep:packing");
            ex.setDisposition("urn:epcglobal:cbv:disp:in_progress");
            ex.setBizLocation("urn:epc:id:sgln:0614141.00729.loc450");
            ex.setReadPoint("urn:epc:id:sgln:0614141.00729.rp104");
            ex.setBizTransaction("urn:epcglobal:cbv:btt:po", "http://transaction.fosstrak.org/po/12345678");
            ex.setParentID("urn:epc:id:sscc:0614141.1234567890");
            ex.setChildEPCs("urn:epc:id:sgtin:0057000.123780.7788 urn:epc:id:sgtin:0057000.123430.2027 "
                    + "urn:epc:id:sgtin:0057000.123430.2028 urn:epc:id:sgtin:0057000.123430.2029");
            examples.add(ex);

            ex = new CaptureEvent();
            ex.setDescription("DEMO 4: Pallet arrives at port of Kaohsiung");
            ex.setType(0);
            ex.setEventTime("2006-09-20T10:33:31.116Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setAction(1);
            ex.setBizStep("urn:epcglobal:cbv:bizstep:arriving");
            ex.setDisposition("urn:epcglobal:cbv:disp:in_progress");
            ex.setBizLocation("http://epcis.fosstrak.org/demo/loc/china/kaohsiung");
            ex.setReadPoint("http://epcis.fosstrak.org/demo/loc/china/kaohsiung/e62");
            ex.setEpcList("urn:epc:id:sscc:0614141.1234567890");
            examples.add(ex);

            ex = new CaptureEvent();
            ex.setDescription("DEMO 5: Pallet departs from port of Kaohsiung");
            ex.setType(0);
            ex.setEventTime("2006-09-21T13:27:08.155Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setAction(1);
            ex.setBizStep("urn:epcglobal:cbv:bizstep:departing");
            ex.setDisposition("urn:epcglobal:cbv:disp:in_transit");
            ex.setBizLocation("http://epcis.fosstrak.org/demo/loc/china/kaohsiung");
            ex.setReadPoint("http://epcis.fosstrak.org/demo/loc/china/kaohsiung/b18");
            ex.setEpcList("urn:epc:id:sscc:0614141.1234567890");
            examples.add(ex);

            ex = new CaptureEvent();
            ex.setDescription("Item passes a reader during manufacturing process");
            ex.setType(0);
            ex.setEventTime("2006-04-03T20:33:31.116Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setAction(1);
            ex.setBizStep("http://epcis.fosstrak.org/bizstep/production");
            ex.setBizLocation("urn:epc:id:sgln:0614141.00101.loc210");
            ex.setReadPoint("urn:epc:id:sgln:0614141.00101.rp210");
            ex.setEpcList("urn:epc:id:sgtin:0057000.123780.3167");
            examples.add(ex);

            ex = new CaptureEvent();
            ex.setDescription("Two pallets are loaded onto a truck");
            ex.setType(0);
            ex.setEventTime("2006-05-09T21:01:44Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setAction(1);
            ex.setBizStep("urn:epcglobal:cbv:bizstep:loading");
            ex.setDisposition("urn:epcglobal:cbv:disp:in_transit");
            ex.setBizLocation("urn:epc:id:sgln:0614141.00101.loc209");
            ex.setReadPoint("urn:epc:id:sgln:0614141.00101.rp53");
            ex.setEpcList("urn:epc:id:sscc:0614141.2644895423 urn:epc:id:sscc:0614141.2644895424");
            ex.setBizTransaction("urn:epcglobal:cbv:fmcg:btt:po", "http://transaction.example.com/po/12345678");
            examples.add(ex);

            ex = new CaptureEvent();
            ex.setDescription("Item arrives for repair");
            ex.setType(0);
            ex.setEventTime("2006-05-10T04:50:35Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setAction(1);
            ex.setBizStep("urn:epcglobal:cbv:bizstep:receiving");
            ex.setDisposition("http://epcis.fosstrak.org/disp/in_repair");
            ex.setBizLocation("urn:epc:id:sgln:0614141.00101.repair-center");
            ex.setReadPoint("urn:epc:id:sgln:0614141.00101.rp77");
            ex.setEpcList("urn:epc:id:sgtin:0034000.987650.2686");
            examples.add(ex);

            ex = new CaptureEvent();
            ex.setDescription("Three items are aggregated onto a barcode-labeled pallet");
            ex.setType(1);
            ex.setAction(0);
            ex.setEventTime("2006-06-01T15:55:04Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setBizStep("urn:epcglobal:cbv:bizstep:packing");
            ex.setDisposition("urn:epcglobal:cbv:disp:in_progress");
            ex.setBizLocation("urn:epc:id:sgln:0614141.00101.loc208");
            ex.setReadPoint("urn:epc:id:sgln:0614141.00101.rp98");
            ex.setBizTransaction("urn:epcglobal:cbv:fmcg:btt:po", "http://transaction.example.com/po/12345678");
            ex.setBizTransaction("urn:epcglobal:cbv:btt:asn", "http://transaction.example.com/asn/1152");
            ex.setParentID("urn:epc:id:sscc:0614141.2644895423");
            ex.setChildEPCs("urn:epc:id:sgtin:0057000.123430.2025 urn:epc:id:sgtin:0057000.123430.2027 "
                    + "urn:epc:id:sgtin:0057000.123430.2028");
            examples.add(ex);

            ex = new CaptureEvent();
            ex.setDescription("Aggregation ends at customer site");
            ex.setType(1);
            ex.setAction(2);
            ex.setEventTime("2006-06-05T09:26:06Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setBizLocation("urn:epc:id:sgln:0614141.82863.loc3");
            ex.setReadPoint("urn:epc:id:sgln:0614141.82863.reader10");
            ex.setBizTransaction("urn:epcglobal:cbv:fmcg:btt:po", "http://po.example.org/E58J3Q");
            ex.setBizTransaction("urn:epcglobal:cbv:btt:asn", "http://transaction.example.com/asn/1152");
            ex.setParentID("urn:epc:id:sscc:0614141.2644895423");
            examples.add(ex);

            ex = new CaptureEvent();
            ex.setDescription("Physical inventory count: 67 items");
            ex.setType(2);
            ex.setEventTime("2006-01-15T16:15:31Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setBizStep("urn:epcglobal:cbv:bizstep:storing");
            ex.setDisposition("urn:epcglobal:cbv:disp:active");
            ex.setBizLocation("urn:epc:id:sgln:0614141.82863.loc6");
            ex.setReadPoint("urn:epc:id:sgln:0614141.82863.loc6-virt");
            ex.setEpcClass("urn:epc:id:sgtin:0069000.957110");
            ex.setQuantity(67);
            examples.add(ex);

            ex = new CaptureEvent();
            ex.setDescription("Order changed by customer - two more objects added to transaction");
            ex.setType(3);
            ex.setEventTime("2006-08-18T11:53:01Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setAction(0);
            ex.setBizTransaction("urn:epcglobal:cbv:fmcg:btt:po", "http://transaction.example.com/po/6677150");
            ex.setEpcList("urn:epc:id:sgtin:0057000.678930.5003 urn:epc:id:sgtin:0057000.678930.5004");
            examples.add(ex);

            ex = new CaptureEvent();
            ex.setDescription("Transaction is finished");
            ex.setType(3);
            ex.setEventTime("2006-08-20T07:03:51Z");
            ex.setEventTimeZoneOffset("+00:00");
            ex.setAction(2);
            ex.setBizTransaction("urn:epcglobal:cbv:fmcg:btt:po", "http://transaction.example.com/po/6677150");
            ex.setEpcList("urn:epc:id:sgtin:0057000.678930.5003 urn:epc:id:sgtin:0057000.678930.5004");
            examples.add(ex);
        }
    }
}
