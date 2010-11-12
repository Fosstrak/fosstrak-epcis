package org.epcis.fosstrak.restadapter.ws.epcis.queryparam;

import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterExceptionResponse;
import java.text.ParseException;

/**
 * Abstract Parameter for String
 *
 *
 * @version        1.0, 09/08/07
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 * This project is a collaboration between:
 * Software Engineering Group, Departement of Informatics
 * University of Fribourg, Switzerland
 * and
 * Institute for Pervasive Computing
 * ETH Zurich, Switzerland
 * Project team: Mathias Mueller, Patrik Fuhrer, Dominique Guinard
 * (c) University of Fribourg, ETH Zurich
 *
 */
public class AbstractStringParameter extends AbstractParameter {

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
    public AbstractStringParameter(String name, String paramName, String value) throws ParseException, QueryParameterExceptionResponse {
        super(name, paramName, value);
    }

    @Override
    protected Object getQueryParamValue() {
        return getValue();
    }
}
