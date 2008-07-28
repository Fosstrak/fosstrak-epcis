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

import java.util.List;

/**
 * A base event class that contains properties common to most events. This
 * extends the EPCISEvent type described in section 7 of the spec, providing
 * convenient access to shared data elements.
 * 
 * @author Sean Wellington
 */
public abstract class BaseEvent extends EPCISEvent {

    private Long id;
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