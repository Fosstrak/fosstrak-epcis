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
package org.epcis.fosstrak.restadapter.feed;

import org.apache.abdera.Abdera;

/**
 * AtomPub Singleton class
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch
 *
 */
public class AtomPub {

    private static Abdera INSTANCE = null;

    /**
     * Abdera singleton instance for this web application.
     *
     * @return
     */
    public static Abdera getAbdera() {
        if (INSTANCE == null) {
            INSTANCE = Abdera.getInstance();
        }

        return INSTANCE;
    }
}
