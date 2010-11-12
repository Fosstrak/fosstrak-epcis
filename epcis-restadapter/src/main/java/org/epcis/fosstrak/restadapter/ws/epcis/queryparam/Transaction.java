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
import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterExceptionResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import static org.epcis.fosstrak.restadapter.config.QueryParamConstants.*;

/**
 * Parameter for Transaction
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 *
 */
public class Transaction {

    private static final String appendix = "EQ_bizTransaction_";
    private static final String name     = BUSINESS_TRANSACTION_TYPE_REST;

    private String              value;

    /**
     * Constructor
     *
     *
     * @param value
     *
     * @throws ParseException
     * @throws QueryParameterExceptionResponse
     */
    public Transaction(String value) {
        this.value = value;
    }

    /**
     * Get the list of parameters of this transaction
     *
     *
     * @return
     */
    public List<VariableParameter> getQueryParamValue() {

        // ugly code here, is a patch to fix transaction queries
        List<VariableParameter> result     = new ArrayList<VariableParameter>();
        String                  temp       = value.substring(0, value.length() - 1);
        String[]                parameters = temp.split("\\),");

        for (String param : parameters) {
            try {
                String[]          myTransaction = param.split("\\(");
                String            myTypeRel     = myTransaction[0];
                String            myValue       = myTransaction[1];
                String            myType        = appendix + myTypeRel;
                String            myTypeValue   = myTypeRel + Config.PARAM_START + myValue + Config.PARAM_END;
                VariableParameter myParam       = new VariableParameter(name, myType, myTypeValue);

                result.add(myParam);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }
}
