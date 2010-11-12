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
import org.epcis.fosstrak.restadapter.ws.generated.QueryParam;
import org.epcis.fosstrak.restadapter.ws.generated.QueryParameterExceptionResponse;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Abstract Parameter
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 */
public abstract class AbstractParameter implements Parameter {

    protected String     name;
    protected String     paramName;
    protected String     value;
    protected QueryParam queryParam;

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
    public AbstractParameter(String name, String paramName, String value) throws ParseException, QueryParameterExceptionResponse {
        this.name      = name;
        this.paramName = paramName;
        this.value     = value;

        queryParam     = new QueryParam();
        queryParam.setName(getParamName());

//      getQueryParamValue();
        queryParam.setValue(getQueryParamValue());
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public QueryParam getQueryParam() {
        return queryParam;
    }

    protected abstract Object getQueryParamValue();

    /**
     * Method description
     *
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Method description
     *
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getValue() {

        // get multiple values (any kind of sorting) in always the same sort
        String res = value;

        try {
            String myTemp = "";
            String temp[] = value.split(Config.SEPARATOR);

            Arrays.sort(temp);

            for (String s : temp) {
                myTemp = myTemp + Config.SEPARATOR + s;
            }

            myTemp = myTemp.substring(1);
            res    = myTemp;
        } catch (Exception ex) {

            // continue
        }

        return res;
    }

    /**
     * Method description
     *
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * Method description
     *
     *
     * @param paramName
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    /**
     * Method description
     *
     *
     * @param queryParam
     */
    public void setQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
    }
}
