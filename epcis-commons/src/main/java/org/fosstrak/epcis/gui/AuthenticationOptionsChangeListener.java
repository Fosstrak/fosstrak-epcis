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

import java.util.EventListener;

/**
 * An interface implemented by GUI components that want notification
 * of changes to authentication options.
 * 
 * @author Sean Wellington
 */
public interface AuthenticationOptionsChangeListener extends EventListener {

	/**
	 * Call this when the settings in the AuthenticationOptionsPanel have changed.
	 * 
	 * @param ace an AuthenticationOptionsChangeEvent describing the changes.
	 */
	public void configurationChanged(AuthenticationOptionsChangeEvent ace);
}
