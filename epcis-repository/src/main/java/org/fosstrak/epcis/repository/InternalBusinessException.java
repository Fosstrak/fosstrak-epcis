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

/**
 * This Exception is thrown by the different modules to indicate problems while
 * processing capture or query requests.
 * 
 * @author Marco Steybe
 */
public class InternalBusinessException extends Exception {

    private static final long serialVersionUID = 1390149913827592956L;

    public InternalBusinessException() {
        super();
    }

    public InternalBusinessException(String message) {
        super(message);
    }

    public InternalBusinessException(Throwable cause) {
        super(cause);
    }

    public InternalBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
