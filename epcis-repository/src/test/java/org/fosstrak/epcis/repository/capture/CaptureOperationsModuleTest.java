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

package org.fosstrak.epcis.repository.capture;

import junit.framework.TestCase;

import org.fosstrak.epcis.repository.InvalidFormatException;

/**
 * Tests some features of the CaptureOperationsModule class.
 * <p>
 * TODO: add more tests
 * 
 * @author Marco Steybe
 */
public class CaptureOperationsModuleTest extends TestCase {

    static {
        // provide the catalina.base property which is not available when the
        // application is not deployed, i.e., when running tests
        if (System.getenv("CATALINA_HOME") != null) {
            System.setProperty("catalina.base", System.getenv("CATALINA_HOME"));
        }
    }
    private static CaptureOperationsModule module = new CaptureOperationsModule();

    public void testCheckEventTimeZoneOffset() throws InvalidFormatException {
        assertEquals(module.checkEventTimeZoneOffset("+05:30"), "+05:30");
        assertEquals(module.checkEventTimeZoneOffset("-00:00"), "-00:00");
        assertEquals(module.checkEventTimeZoneOffset("+14:00"), "+14:00");
        try {
            module.checkEventTimeZoneOffset("+14:30");
            fail("InvalidFormatException expected");
        } catch (InvalidFormatException e) {
        }
        try {
            module.checkEventTimeZoneOffset("-16:30");
            fail("InvalidFormatException expected");
        } catch (InvalidFormatException e) {
        }
        try {
            module.checkEventTimeZoneOffset("-05:87");
            fail("InvalidFormatException expected");
        } catch (InvalidFormatException e) {
        }
    }

    public void testCheckCommonEpcs() throws InvalidFormatException {
        module.checkEpc("urn:epc:id:sgtin:0652642.800031.400");
        module.checkEpc("urn:epc:id:sscc:0652642.0123456789");
        module.checkEpc("urn:epc:id:sgln:0652642.12345.40");
        module.checkEpc("urn:epc:id:sgln:0652642.12345.0");
        module.checkEpc("urn:epc:id:grai:0652642.12345.1234");
        module.checkEpc("urn:epc:id:giai:0652642.123456");
    }

    public void testCheckValidEpcs() throws InvalidFormatException {
        module.checkEpc("urn:epc:id:sgtin:0.0.%AB-+:judihui");
    }

    public void testCheckInvalidEpcs() throws InvalidFormatException {
        try {
            module.checkEpc("urn:epc:id:gid:1652642.800031.400.123");
            fail("InvalidFormatException expected");
        } catch (InvalidFormatException e) {
        }
        try {
            module.checkEpc("urn:epc:id:sgtin:0652642.800031.");
            fail("InvalidFormatException expected");
        } catch (InvalidFormatException e) {
        }
        try {
            module.checkEpc("urn:epc:id:sgtin:0652642");
            fail("InvalidFormatException expected");
        } catch (InvalidFormatException e) {
        }
        try {
            module.checkEpc("urn:epc:id:sgtin:0652642.800A031.400");
            fail("InvalidFormatException expected");
        } catch (InvalidFormatException e) {
        }
        try {
            module.checkEpc("urn:epc:id:1234:0652642.800031.400");
            fail("InvalidFormatException expected");
        } catch (InvalidFormatException e) {
        }
        try {
            module.checkEpc("urn:epc:ident:sgtin:0652642.800031.400");
            fail("InvalidFormatException expected");
        } catch (InvalidFormatException e) {
        }
        try {
            module.checkEpc("urn.epc.id:sgtin:0652642.800031.400");
            fail("InvalidFormatException expected");
        } catch (InvalidFormatException e) {
        }
    }
}
