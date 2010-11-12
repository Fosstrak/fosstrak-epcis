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

import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterExceptionResponse;
import java.text.ParseException;
import static org.epcis.fosstrak.restadapter.config.QueryParamConstants.*;

/**
 * Parameter for Quantity greater than
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 *
 */
public class QuantityGT extends AbstractIntegerParameter {

    /**
     * Constructor
     *
     *
     * @param value
     *
     * @throws ParseException
     * @throws QueryParameterExceptionResponse
     */
    public QuantityGT(String value) throws ParseException, QueryParameterExceptionResponse {
        super(QUANTITY_REST_GT, GT_quantity, value);
    }
}
