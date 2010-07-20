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

import org.fosstrak.epcis.repository.EpcisConstants;


/**
 * A vocabulary type for representing business step identifiers Attributes
 * 
 * @author Nikos Kefalakis (nkef)
 */
public class BusinessStepAttrId extends VocabularyAttributeElement{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5349224565764950852L;

	@Override
    public String getVocabularyType() {
        return EpcisConstants.BUSINESS_STEP_ID;
    }

}
