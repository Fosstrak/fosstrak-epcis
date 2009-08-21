/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org).
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

package org.fosstrak.epcis.captureclient;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * A simple utility class to generate EPCIS events with some sample contents.
 * 
 * @author Marco Steybe
 */
public class SampleEventsGenerator {

    /**
     * Miscellaneous numeric formats used in formatting.
     */
    private static final DecimalFormat XX_FORMAT = new DecimalFormat("00");
    private static final DecimalFormat XXX_FORMAT = new DecimalFormat("000");
    private static final DecimalFormat XXXX_FORMAT = new DecimalFormat("0000");

    public static void main(String[] args) throws IOException {
        generateAggregationEvents("aggregationevents.xml", 5);
        generateObjectEvents("objectevents.xml", 5);
        generateQuantityEvents("quantityevents.xml", 5);
        generateTransactionEvents("transactionevents.xml", 5);
    }

    /**
     * Generates <code>nr</code> AggregationEvents and writes them to a file called <code>fileName</code>.
     */
    public static void generateAggregationEvents(String fileName, int nr) throws IOException {
        Calendar now = Calendar.getInstance();
        String time = format(now);
        String tz = getTimeZone(now);
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        sb.append("<epcis:EPCISDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" xmlns:epcglobal=\"urn:epcglobal:xsd:1\" xsi:schemaLocation=\"urn:epcglobal:epcis:xsd:1 EPCglobal-epcis-1_0.xsd\"");
        sb.append(" xmlns:ext=\"http://www.fosstrak.org/epcis/extension\" creationDate=\"").append(time).append(
                "\" schemaVersion=\"1.0\">");
        sb.append("<EPCISBody>");
        sb.append("<EventList>");
        for (int epcSerialNr = 0; epcSerialNr < nr; epcSerialNr++) {
            String epc = "urn:epc:id:sgtin:1.1." + epcSerialNr;
            sb.append("<AggregationEvent>");
            sb.append("<eventTime>").append(time).append("</eventTime>");
            sb.append("<recordTime>").append(time).append("</recordTime>");
            sb.append("<eventTimeZoneOffset>").append(tz).append("</eventTimeZoneOffset>");
            sb.append("<parentID>").append(epc).append("</parentID>");
            sb.append("<childEPCs>");
            sb.append("<epc>").append(epc).append("</epc>");
            sb.append("</childEPCs>");
            sb.append("<action>ADD</action>");
            sb.append("<bizStep>urn:fosstrak:demo:bizstep:testing</bizStep>");
            sb.append("<disposition>urn:fosstrak:demo:disp:testing</disposition>");
            sb.append("<readPoint>");
            sb.append("<id>urn:fosstrak:demo:rp:1.1</id>");
            sb.append("</readPoint>");
            sb.append("<bizLocation>");
            sb.append("<id>urn:fosstrak:demo:loc:1.1</id>");
            sb.append("</bizLocation>");
            sb.append("<bizTransactionList>");
            sb.append("<bizTransaction type=\"urn:fosstrak:demo:btt:testing\">urn:fosstrak:demo:biztrans:1.1</bizTransaction>");
            sb.append("</bizTransactionList>");
            sb.append("<ext:temperature>25</ext:temperature>");
            sb.append("<ext:batchnumber>17</ext:batchnumber>");
            sb.append("</AggregationEvent>");
        }
        sb.append("</EventList>");
        sb.append("</EPCISBody>");
        sb.append("</epcis:EPCISDocument>");
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write(sb.toString());
        bw.close();
    }

    /**
     * Generates <code>nr</code> ObjectEvents and writes them to a file called <code>fileName</code>.
     */
    public static void generateObjectEvents(String fileName, int nr) throws IOException {
        Calendar now = Calendar.getInstance();
        String time = format(now);
        String tz = getTimeZone(now);
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        sb.append("<epcis:EPCISDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" xmlns:epcglobal=\"urn:epcglobal:xsd:1\" xsi:schemaLocation=\"urn:epcglobal:epcis:xsd:1 EPCglobal-epcis-1_0.xsd\"");
        sb.append(" creationDate=\"").append(time).append("\" schemaVersion=\"1.0\">");
        sb.append("<EPCISBody>");
        sb.append("<EventList>");
        for (int epcSerialNr = 0; epcSerialNr < nr; epcSerialNr++) {
            String epc = "urn:epc:id:sgtin:1.1." + epcSerialNr;
            sb.append("<ObjectEvent>");
            sb.append("<eventTime>").append(time).append("</eventTime>");
            sb.append("<recordTime>").append(time).append("</recordTime>");
            sb.append("<eventTimeZoneOffset>").append(tz).append("</eventTimeZoneOffset>");
            sb.append("<epcList>");
            sb.append("<epc>").append(epc).append("</epc>");
            sb.append("</epcList>");
            sb.append("<action>ADD</action>");
            sb.append("<bizStep>urn:fosstrak:demo:bizstep:testing</bizStep>");
            sb.append("<disposition>urn:fosstrak:demo:disp:testing</disposition>");
            sb.append("<readPoint>");
            sb.append("<id>urn:fosstrak:demo:rp:1.1</id>");
            sb.append("</readPoint>");
            sb.append("<bizLocation>");
            sb.append("<id>urn:fosstrak:demo:loc:1.1</id>");
            sb.append("</bizLocation>");
            sb.append("<bizTransactionList>");
            sb.append("<bizTransaction type=\"urn:fosstrak:demo:btt:testing\">urn:fosstrak:demo:biztrans:1.1</bizTransaction>");
            sb.append("</bizTransactionList>");
            sb.append("</ObjectEvent>");
        }
        sb.append("</EventList>");
        sb.append("</EPCISBody>");
        sb.append("</epcis:EPCISDocument>");
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write(sb.toString());
        bw.close();
    }

    /**
     * Generates <code>nr</code> TransactionEvents and writes them to a file called <code>fileName</code>.
     */
    public static void generateTransactionEvents(String fileName, int nr) throws IOException {
        Calendar now = Calendar.getInstance();
        String time = format(now);
        String tz = getTimeZone(now);
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        sb.append("<epcis:EPCISDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" xmlns:epcglobal=\"urn:epcglobal:xsd:1\" xsi:schemaLocation=\"urn:epcglobal:epcis:xsd:1 EPCglobal-epcis-1_0.xsd\"");
        sb.append(" creationDate=\"").append(time).append("\" schemaVersion=\"1.0\">");
        sb.append("<EPCISBody>");
        sb.append("<EventList>");
        for (int epcSerialNr = 0; epcSerialNr < 5; epcSerialNr++) {
            String epc = "urn:epc:id:sgtin:1.1." + epcSerialNr;
            sb.append("<TransactionEvent>");
            sb.append("<eventTime>").append(time).append("</eventTime>");
            sb.append("<recordTime>").append(time).append("</recordTime>");
            sb.append("<eventTimeZoneOffset>").append(tz).append("</eventTimeZoneOffset>");
            sb.append("<bizTransactionList>");
            sb.append("<bizTransaction type=\"urn:fosstrak:demo:btt:testing\">urn:fosstrak:demo:biztrans:1.1</bizTransaction>");
            sb.append("</bizTransactionList>");
            sb.append("<epcList>");
            sb.append("<epc>").append(epc).append("</epc>");
            sb.append("</epcList>");
            sb.append("<action>ADD</action>");
            sb.append("<bizStep>urn:fosstrak:demo:bizstep:testing</bizStep>");
            sb.append("<disposition>urn:fosstrak:demo:disp:testing</disposition>");
            sb.append("<readPoint>");
            sb.append("<id>urn:fosstrak:demo:rp:1.1</id>");
            sb.append("</readPoint>");
            sb.append("<bizLocation>");
            sb.append("<id>urn:fosstrak:demo:loc:1.1</id>");
            sb.append("</bizLocation>");
            sb.append("</TransactionEvent>");
        }
        sb.append("</EventList>");
        sb.append("</EPCISBody>");
        sb.append("</epcis:EPCISDocument>");
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write(sb.toString());
        bw.close();
    }

    /**
     * Generates <code>nr</code> QuantityEvents and writes them to a file called <code>fileName</code>.
     */
    public static void generateQuantityEvents(String fileName, int nr) throws IOException {
        Calendar now = Calendar.getInstance();
        String time = format(now);
        String tz = getTimeZone(now);
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        sb.append("<epcis:EPCISDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" xmlns:epcglobal=\"urn:epcglobal:xsd:1\" xsi:schemaLocation=\"urn:epcglobal:epcis:xsd:1 EPCglobal-epcis-1_0.xsd\"");
        sb.append(" creationDate=\"").append(time).append("\" schemaVersion=\"1.0\">");
        sb.append("<EPCISBody>");
        sb.append("<EventList>");
        for (int epcSerialNr = 0; epcSerialNr < nr; epcSerialNr++) {
            String epc = "urn:epc:id:sgtin:1.1." + epcSerialNr;
            sb.append("<QuantityEvent>");
            sb.append("<eventTime>").append(time).append("</eventTime>");
            sb.append("<recordTime>").append(time).append("</recordTime>");
            sb.append("<eventTimeZoneOffset>").append(tz).append("</eventTimeZoneOffset>");
            sb.append("<epcClass>").append(epc).append("</epcClass>");
            sb.append("<quantity>1</quantity>");
            sb.append("<bizStep>urn:fosstrak:demo:bizstep:testing</bizStep>");
            sb.append("<disposition>urn:fosstrak:demo:disp:testing</disposition>");
            sb.append("<readPoint>");
            sb.append("<id>urn:fosstrak:demo:rp:1.1</id>");
            sb.append("</readPoint>");
            sb.append("<bizLocation>");
            sb.append("<id>urn:fosstrak:demo:loc:1.1</id>");
            sb.append("</bizLocation>");
            sb.append("<bizTransactionList>");
            sb.append("<bizTransaction type=\"urn:fosstrak:demo:btt:testing\">urn:fosstrak:demo:biztrans:1.1</bizTransaction>");
            sb.append("</bizTransactionList>");
            sb.append("</QuantityEvent>");
        }
        sb.append("</EventList>");
        sb.append("</EPCISBody>");
        sb.append("</epcis:EPCISDocument>");
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        bw.write(sb.toString());
        bw.close();
    }

    /**
     * Formats a <code>Calendar</code> value into an ISO8601-compliant
     * date/time string. This method is copied from
     * org.fosstrak.epcis.utils.TimeParser (module epcis-commons).
     * 
     * @see org.fosstrak.epcis.utils.TimeParser#format(Calendar)
     * @param cal
     *            The time value to be formatted into a date/time string.
     * @return The formatted date/time string.
     */
    private static String format(final Calendar cal) {
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
     * Returns the time zone designator in a ISO6601-compliant format from the
     * given <code>Calendar</code> value.
     * 
     * @param cal
     *            The Calendar to be formatted.
     * @return The time zone designator from the given Calendar.
     */
    private static String getTimeZone(final Calendar cal) {
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
}
