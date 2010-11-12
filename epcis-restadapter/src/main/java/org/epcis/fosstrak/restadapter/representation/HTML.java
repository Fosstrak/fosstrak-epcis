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
package org.epcis.fosstrak.restadapter.representation;

/**
 * Class description
 * Class to handle static form strings and the escape of HTML tags
 *
 *
 * @version        1.0, 09/08/07
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 */
public class HTML {

    public static final String BUTTON_TYPE_BUTTON   = "button";
    public static final String BUTTON_TYPE_RESET    = "reset";
    public static final String BUTTON_TYPE_SUBMIT   = "submit";
    public static final String INPUT_TYPE_TEXT      = "text";
    public static final String INPUT_TYPE_SUBMIT    = "submit";
    public static final String INPUT_TYPE_TEXT_SIZE = "50";
    public static final String ENCTYPE_TEXTPLAIN    = "text/plain";

    /**
     * Method to escape the HTML tags
     *
     *
     * @param xml
     *
     * @return
     */
    public static String escapeXML(String xml) {
        String res = xml.replaceAll(">", "&gt;");

        res = xml.replaceAll("<", "&lt;");

        return res;
    }
}
