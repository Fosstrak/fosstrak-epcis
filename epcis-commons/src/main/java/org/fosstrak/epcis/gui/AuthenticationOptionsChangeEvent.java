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

package org.fosstrak.epcis.gui;

import java.util.EventObject;

/**
 * Notification that authentication options have changed.
 * 
 * @author Sean Wellington
 */
public class AuthenticationOptionsChangeEvent extends EventObject {

	static final long serialVersionUID = 7641439802544240559L;
	
	private boolean complete;
	
	public AuthenticationOptionsChangeEvent(Object source, boolean complete) {
		super(source);
		this.complete = complete;
	}
	
	/**
	 * Indicates whether the supplied options are complete (i.e. sufficient filled out
	 * to allow the desired type of authentication to proceed), and the GUI buttons
	 * can be reactivated.
	 * @return true, if the options are sufficiently filled out to allow the
	 * desired type of authentication to proceed, false otherwise.
	 */
	public boolean isComplete() {
		return complete;
	}
	
}
