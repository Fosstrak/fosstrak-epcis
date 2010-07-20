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

import static org.fosstrak.epcis.repository.Utils.eq;

import java.io.Serializable;

/**
 * A vocabulary type for representing business step identifiers Attributes
 * 
 * @author Nikos Kefalakis (nkef)
 */
public class VocabularyAttrCiD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5025973585282842235L;
	
	private String privateID;
	private Long id;
	private String attribute;
	
	public VocabularyAttrCiD() {
		this.privateID = java.util.UUID.randomUUID().toString();
	}

	public int hashCode() {
		return privateID.hashCode();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof VocabularyAttrCiD) {
			VocabularyAttrCiD that = (VocabularyAttrCiD) o;
			return eq(this.id, that.id) && eq(this.attribute, that.attribute);
		}
		else {
			return false;
		}

	}

}
