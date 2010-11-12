/*
 * Copyright (C) 2010 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org) and
 * was developed as part of the webofthings.com initiative.
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
package org.epcis.fosstrak.restadapter.db.exceptions;

/**
 * This exception is raised whenever a requested entry is not found in the Internal
 * database.
 * <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
public class NotFoundInDBException extends Exception {

    /**
     * Creates a new instance of <code>NotFoundInDBException</code> without detail message.
     */
    public NotFoundInDBException() {
    }


    /**
     * Constructs an instance of <code>NotFoundInDBException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NotFoundInDBException(String msg) {
        super(msg);
    }
}
