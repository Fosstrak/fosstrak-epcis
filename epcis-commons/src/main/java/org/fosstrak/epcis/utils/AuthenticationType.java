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

package org.fosstrak.epcis.utils;

/**
 * Authentication methods supported by the query and capture clients.
 *  
 * @author Sean Wellington
 */
public enum AuthenticationType {

	/**
	 * No authentication.
	 */
	NONE,

	/**
	 * Basic authentication as described in RFC 2617.
	 */
	BASIC,
	
	/**
	 * HTTPS using an X.509 certificate to authenticate the client.
	 */
	HTTPS_WITH_CLIENT_CERT
	
}
