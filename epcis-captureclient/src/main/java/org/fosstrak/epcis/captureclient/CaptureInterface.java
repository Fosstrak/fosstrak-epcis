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

package org.accada.epcis.captureclient;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is an interface to the EPCIS repository's Capture Operations Module.
 * 
 * @author Marco Steybe
 */
public interface CaptureInterface {

    /**
     * Sends a capture request to the repository's Capture Interface. The
     * request must conform to the EPCISDocument schema.
     * 
     * @param event
     *            An XML String containing the EPCISDocument.
     * @return The HTTP response code from the repository's Capture Operations
     *         Module. Code 200 indicates successfull capturing of the given
     *         events.
     * @throws IOException
     *             If a problem with the given input or an error on the
     *             transport layer occurred.
     */
    int capture(final String event) throws IOException;

    /**
     * Sends a capture request to the repository's Capture Interface. The
     * request must conform to the EPCISDocument schema.
     * 
     * @param event
     *            An InputStream to read the XML containing the EPCISDocument
     *            from.
     * @return The HTTP response code from the repository's Capture Operations
     *         Module. Code 200 indicates successfull capturing of the given
     *         events.
     * @throws IOException
     *             If a problem with the given input or an error on the
     *             transport layer occurred.
     */
    int capture(final InputStream event) throws IOException;

}
