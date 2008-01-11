/*
 * Copyright (C) 2008 ETH Zurich
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

package org.accada.epcis.repository.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * A base event class that contains properties common to most events.
 * This is not intended to represent the EPCISEvent type described in section
 * 7 of the spec, but to provide convenient access to shared data elements.
 * @author Sean Wellington
 */
public abstract class BaseEvent {

	private Long id;
	private Timestamp eventTime;
	private Timestamp recordTime;
	private String eventTimeZoneOffset;
	private BusinessStepId bizStep;
	private DispositionId disposition;
	private ReadPointId readPoint;
	private BusinessLocationId bizLocation;
	private List<BusinessTransaction> bizTransList;
	private List<EventFieldExtension> extensions;

	public BaseEvent() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getEventTime() {
		return eventTime;
	}

	public void setEventTime(Timestamp eventTime) {
		this.eventTime = eventTime;
	}

	public Timestamp getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Timestamp recordTime) {
		this.recordTime = recordTime;
	}

	public String getEventTimeZoneOffset() {
		return eventTimeZoneOffset;
	}

	public void setEventTimeZoneOffset(String eventTimeZoneOffset) {
		this.eventTimeZoneOffset = eventTimeZoneOffset;
	}

	public BusinessStepId getBizStep() {
		return bizStep;
	}

	public void setBizStep(BusinessStepId bizStep) {
		this.bizStep = bizStep;
	}

	public DispositionId getDisposition() {
		return disposition;
	}

	public void setDisposition(DispositionId disposition) {
		this.disposition = disposition;
	}

	public ReadPointId getReadPoint() {
		return readPoint;
	}

	public void setReadPoint(ReadPointId readPoint) {
		this.readPoint = readPoint;
	}

	public BusinessLocationId getBizLocation() {
		return bizLocation;
	}

	public void setBizLocation(BusinessLocationId bizLocation) {
		this.bizLocation = bizLocation;
	}

	public List<BusinessTransaction> getBizTransList() {
		return bizTransList;
	}

	public void setBizTransList(List<BusinessTransaction> bizTransList) {
		this.bizTransList = bizTransList;
	}

	public List<EventFieldExtension> getExtensions() {
		return extensions;
	}

	public void setExtensions(List<EventFieldExtension> extensions) {
		this.extensions = extensions;
	}

	
}