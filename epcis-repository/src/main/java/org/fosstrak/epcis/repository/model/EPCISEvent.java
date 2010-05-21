/*
 * Copyright (C) 2008 ETH Zurich
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

package org.fosstrak.epcis.repository.model;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * An EPCISEvent as defined in section 7.2.8 of the spec.
 * 
 * @author Marco Steybe
 */
public abstract class EPCISEvent {

    private Timestamp eventTime;
    private long eventTimeMs;
    private Timestamp recordTime;
    private long recordTimeMs;
    private String eventTimeZoneOffset;

    public void setEventTime(Calendar eventTime) {
        this.eventTime = new Timestamp(eventTime.getTimeInMillis());
        this.eventTimeMs = eventTime.getTimeInMillis();
    }

    public void setRecordTime(Calendar recordTime) {
        this.recordTime = new Timestamp(recordTime.getTimeInMillis());
        this.recordTimeMs = recordTime.getTimeInMillis();
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public long getEventTimeMs() {
        return eventTimeMs;
    }

    public void setEventTimeMs(long eventTimeMs) {
        this.eventTimeMs = eventTimeMs;
    }

    public Timestamp getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Timestamp recordTime) {
        this.recordTime = recordTime;
    }

    public long getRecordTimeMs() {
        return recordTimeMs;
    }

    public void setRecordTimeMs(long recordTimeMs) {
        this.recordTimeMs = recordTimeMs;
    }

    public String getEventTimeZoneOffset() {
        return eventTimeZoneOffset;
    }

    public void setEventTimeZoneOffset(String eventTimeZoneOffset) {
        this.eventTimeZoneOffset = eventTimeZoneOffset;
    }
}
