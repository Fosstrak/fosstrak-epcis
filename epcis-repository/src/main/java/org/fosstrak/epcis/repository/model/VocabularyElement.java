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

import static org.accada.epcis.repository.Utils.eq;
import static org.accada.epcis.repository.Utils.hc;

/**
 * A base class for vocabulary elements as described in section 6 of the spec.
 * 
 * @author Sean Wellington
 */
public abstract class VocabularyElement {

    private Long id;

    private String uri;

    /**
     * The database id of this vocabulary element.
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * The URI representation of this vocabulary element.
     */
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        return hc(uri);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof VocabularyElement) {
            VocabularyElement that = (VocabularyElement) o;
            return eq(this.uri, that.uri);
        } else {
            return false;
        }
    }

    /**
     * The formal name of the vocabulary to which this element belongs.
     */
    public abstract String getVocabularyType();

}
