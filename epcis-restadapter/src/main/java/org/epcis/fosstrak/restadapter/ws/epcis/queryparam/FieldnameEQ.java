package org.epcis.fosstrak.restadapter.ws.epcis.queryparam;

import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterExceptionResponse;
import java.text.ParseException;
import static org.epcis.fosstrak.restadapter.config.QueryParamConstants.*;

/**
 * Parameter for Fieldname equal (used for extensions)
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 *
 */
public class FieldnameEQ extends AbstractRightClassChooserParameter {

    /**
     * Constructor
     *
     *
     * @param value
     *
     * @throws ParseException
     * @throws QueryParameterExceptionResponse
     */
    public FieldnameEQ(String value) throws ParseException, QueryParameterExceptionResponse {
        super(FIELDNAME_REST, EQ_fieldname, value);
    }
}
