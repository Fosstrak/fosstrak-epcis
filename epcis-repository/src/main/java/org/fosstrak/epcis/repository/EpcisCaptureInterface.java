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

package org.fosstrak.epcis.repository;

import java.util.List;

import org.fosstrak.epcis.repository.model.EPCISEvent;

/**
 * The EPCIS Capture Interface defines the delivery of EPCIS events from EPCIS
 * Capturing Applications to an EPCIS Repository. The interface contains only a
 * single method, <code>capture</code>, which takes a single argument and
 * returns no results. Implementations of the EPCIS Capture Interface SHALL
 * accept each element of the argument list that is a valid
 * <code>EPCISEvent</code> or subtype thereof according to the EPCIS
 * specification. Implementations MAY accept other types of events through
 * vendor extension.
 */
public interface EpcisCaptureInterface {

    /**
     * The <code>capture</code> operation records one or more EPCIS events, of
     * any type.
     * 
     * @param events
     *            The event(s) to capture. All relevant information such as the
     *            event time, EPCs, etc., are contained within each event.
     *            Exception: the <code>recordTime</code> MAY be omitted.
     *            Whether the <code>recordTime</code> is omitted or not in the
     *            input, following the capture operation the
     *            <code>recordTime</code> of the event as recorded by the
     *            EPCIS Repository or EPCIS Accessing Application is the time of
     *            capture.
     */
    public void capture(List<EPCISEvent> events);
}
