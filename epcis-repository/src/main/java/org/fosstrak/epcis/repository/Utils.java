/*
 * Copyright (C) 2007 ETH Zurich
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

package org.accada.epcis.repository;

/**
 * Miscellaneous utility functions.
 * 
 * @author Sean Wellington
 */
public class Utils {

    /**
     * @return <code>true</code> if both <code>o1</code> and <code>o2</code>
     *         are <code>null</code>; or <code>o1</code> and
     *         <code>o2</code> are both non-<code>null</code> and equal
     *         according to their <code>equals()</code> methods.
     */
    public static final boolean eq(Object o1, Object o2) {
        return (o1 == null && o2 == null) || (o1 != null && o2 != null && o1.equals(o2));
    }

    /**
     * @return <code>o.hashCode()</code> or <code>0</code> if <code>o</code>
     *         is <code>null</code>.
     */
    public static final int hc(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    /**
     * Hidden default constructor.
     */
    private Utils() {
    }
}
