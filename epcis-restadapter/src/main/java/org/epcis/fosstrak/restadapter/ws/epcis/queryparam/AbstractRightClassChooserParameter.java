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
package org.epcis.fosstrak.restadapter.ws.epcis.queryparam;

import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.util.TimeParser;
import org.epcis.fosstrak.restadapter.ws.generated.ArrayOfString;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterExceptionResponse;
import java.text.ParseException;
import java.util.Calendar;

/**
 * Abstract Class to choose the right Parameters.
 * Used for extensions.
 * Not fully usable.
 *
 * @author Mathias Muellermathias.mueller(at)unifr.ch
 */
public class AbstractRightClassChooserParameter extends AbstractParameter {

    /**
     * Constructor
     *
     *
     * @param name
     * @param paramName
     * @param value
     *
     * @throws ParseException
     * @throws QueryParameterExceptionResponse
     */
    public AbstractRightClassChooserParameter(String name, String paramName, String value) throws ParseException, QueryParameterExceptionResponse {
        super(name, paramName, value);
    }

    @Override
    protected Object getQueryParamValue() {

        // integer
        try {
            int i = Integer.parseInt(getValue());

            return i;
        } catch (Exception ex) {

            // continue
        }

        // float
        try {
            float f = Float.parseFloat(getValue());

            return f;
        } catch (Exception ex) {

            // continue
        }

        // time
        try {

            Calendar cal = TimeParser.parseAsCalendar(getValue());

            return cal;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // else it is an array of string
        ArrayOfString aos        = new ArrayOfString();
        String[]      parameters = getValue().split(Config.SEPARATOR);

        for (int i = 0; i < parameters.length; i++) {
            aos.getString().add(parameters[i]);
        }

        return aos;
    }
}
