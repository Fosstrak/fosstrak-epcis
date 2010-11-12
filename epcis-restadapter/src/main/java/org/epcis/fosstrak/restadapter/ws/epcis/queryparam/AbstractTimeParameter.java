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

import java.text.ParseException;
import java.util.Calendar;
import org.epcis.fosstrak.restadapter.util.TimeParser;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterExceptionResponse;

/**
 * Abstract Parameter for Time
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch
 *
 */
public class AbstractTimeParameter extends AbstractParameter {

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
    public AbstractTimeParameter(String name, String paramName, String value) throws ParseException, QueryParameterExceptionResponse {
        super(name, paramName, value);
    }

    @Override
    protected Object getQueryParamValue() {
        Calendar cal = null;

        try {
            cal = TimeParser.parseAsCalendar(getValue());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return cal;
    }
}
