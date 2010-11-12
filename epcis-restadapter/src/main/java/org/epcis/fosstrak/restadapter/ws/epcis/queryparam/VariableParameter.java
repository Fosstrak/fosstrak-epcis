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

import org.epcis.fosstrak.restadapter.ws.generated.ArrayOfString;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterExceptionResponse;
import java.text.ParseException;

/**
 * Parameter for Variable Parameters
 *
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 */
public class VariableParameter extends AbstractParameter {

    /**
     * Constructor
     *
     *
     *
     * @param name
     * @param epcisVocabularyName
     * @param value
     *
     * @throws ParseException
     * @throws QueryParameterExceptionResponse
     */
    public VariableParameter(String name, String epcisVocabularyName, String value) throws ParseException, QueryParameterExceptionResponse {
        super(name, epcisVocabularyName, value);
    }

    @Override
    protected Object getQueryParamValue() {
        ArrayOfString myAOS = new ArrayOfString();
        String        temp  = getValue();

        temp = temp.split("\\(")[1];
        temp = temp.substring(0, temp.length() - 1);

        String[] myQPValues = temp.split(",");

        for (String myQPValue : myQPValues) {
            myAOS.getString().add(myQPValue);
        }

        return myAOS;
    }

    /**
     * Returns the value of the parameter
     *
     *
     * @return
     */
    @Override
    public String getValue() {
        return value;
    }
}
