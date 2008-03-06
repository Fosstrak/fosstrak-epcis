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

package org.accada.epcis.repository.query;

import org.accada.epcis.repository.EpcisQueryControlInterface;
import org.accada.epcis.soap.DuplicateSubscriptionExceptionResponse;
import org.accada.epcis.soap.EPCISServicePortType;
import org.accada.epcis.soap.ImplementationExceptionResponse;
import org.accada.epcis.soap.InvalidURIExceptionResponse;
import org.accada.epcis.soap.NoSuchNameExceptionResponse;
import org.accada.epcis.soap.NoSuchSubscriptionExceptionResponse;
import org.accada.epcis.soap.QueryParameterExceptionResponse;
import org.accada.epcis.soap.QueryTooComplexExceptionResponse;
import org.accada.epcis.soap.QueryTooLargeExceptionResponse;
import org.accada.epcis.soap.SecurityExceptionResponse;
import org.accada.epcis.soap.SubscribeNotPermittedExceptionResponse;
import org.accada.epcis.soap.SubscriptionControlsExceptionResponse;
import org.accada.epcis.soap.ValidationExceptionResponse;
import org.accada.epcis.soap.model.ArrayOfString;
import org.accada.epcis.soap.model.EmptyParms;
import org.accada.epcis.soap.model.GetSubscriptionIDs;
import org.accada.epcis.soap.model.ImplementationException;
import org.accada.epcis.soap.model.ImplementationExceptionSeverity;
import org.accada.epcis.soap.model.Poll;
import org.accada.epcis.soap.model.QueryResults;
import org.accada.epcis.soap.model.Subscribe;
import org.accada.epcis.soap.model.Unsubscribe;
import org.accada.epcis.soap.model.VoidHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class redirects the calls received from the Web service stack to the
 * underlying QueryOperationsModule and ensures that any uncaught exception is
 * properly catched and wrapped into an ImplementationExceptionResponse.
 * 
 * @author Marco Steybe
 */
public class QueryOperationsWebService implements EPCISServicePortType {

    private static final Log LOG = LogFactory.getLog(QueryOperationsWebService.class);

    private EpcisQueryControlInterface queryModule;

    public QueryOperationsWebService() {
    }

    public QueryOperationsWebService(EpcisQueryControlInterface queryModule) {
        this.queryModule = queryModule;
    }

    /**
     * @see org.accada.epcis.soap.EPCISServicePortType#getQueryNames(org.accada.epcis.soap.model.EmptyParms)
     */
    public ArrayOfString getQueryNames(EmptyParms empty) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse {
        ArrayOfString aos = new ArrayOfString();
        aos.getString().addAll(queryModule.getQueryNames());
        return aos;
    }

    /**
     * @see org.accada.epcis.soap.EPCISServicePortType#getStandardVersion(org.accada.epcis.soap.model.EmptyParms)
     */
    public String getStandardVersion(EmptyParms empty) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse {
        return queryModule.getStandardVersion();
    }

    /**
     * @see org.accada.epcis.soap.EPCISServicePortType#getSubscriptionIDs(org.accada.epcis.soap.model.GetSubscriptionIDs)
     */
    public ArrayOfString getSubscriptionIDs(GetSubscriptionIDs req) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse {
        ArrayOfString aos = new ArrayOfString();
        aos.getString().addAll(queryModule.getSubscriptionIDs(req.getQueryName()));
        return aos;
    }

    /**
     * @see org.accada.epcis.soap.EPCISServicePortType#getVendorVersion(org.accada.epcis.soap.model.EmptyParms)
     */
    public String getVendorVersion(EmptyParms empty) throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse {
        return queryModule.getVendorVersion();
    }

    /**
     * @see org.accada.epcis.soap.EPCISServicePortType#poll(org.accada.epcis.soap.model.Poll)
     */
    public QueryResults poll(Poll poll) throws ImplementationExceptionResponse, QueryTooComplexExceptionResponse,
            QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse,
            NoSuchNameExceptionResponse, QueryParameterExceptionResponse {
        // log and wrap any error that is not one of the expected exceptions
        try {
            return queryModule.poll(poll.getQueryName(), poll.getParams());
        } catch (ImplementationExceptionResponse e) {
            throw e;
        } catch (QueryTooComplexExceptionResponse e) {
            throw e;
        } catch (QueryTooLargeExceptionResponse e) {
            throw e;
        } catch (SecurityExceptionResponse e) {
            throw e;
        } catch (ValidationExceptionResponse e) {
            throw e;
        } catch (NoSuchNameExceptionResponse e) {
            throw e;
        } catch (QueryParameterExceptionResponse e) {
            throw e;
        } catch (Exception e) {
            String msg = "Unexpected error occurred while processing request";
            LOG.error(msg, e);
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            if (poll != null) {
                ie.setQueryName(poll.getQueryName());
            }
            throw new ImplementationExceptionResponse(msg, ie, e);
        }
    }

    /**
     * @see org.accada.epcis.soap.EPCISServicePortType#subscribe(org.accada.epcis.soap.model.Subscribe)
     */
    public VoidHolder subscribe(Subscribe subscribe) throws DuplicateSubscriptionExceptionResponse,
            ImplementationExceptionResponse, QueryTooComplexExceptionResponse, SecurityExceptionResponse,
            InvalidURIExceptionResponse, ValidationExceptionResponse, SubscribeNotPermittedExceptionResponse,
            NoSuchNameExceptionResponse, SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse {
        // log and wrap any error that is not one of the expected exceptions
        try {
            queryModule.subscribe(subscribe.getQueryName(), subscribe.getParams(), subscribe.getDest(),
                    subscribe.getControls(), subscribe.getSubscriptionID());
            return new VoidHolder();
        } catch (DuplicateSubscriptionExceptionResponse e) {
            throw e;
        } catch (ImplementationExceptionResponse e) {
            throw e;
        } catch (QueryTooComplexExceptionResponse e) {
            throw e;
        } catch (SecurityExceptionResponse e) {
            throw e;
        } catch (InvalidURIExceptionResponse e) {
            throw e;
        } catch (ValidationExceptionResponse e) {
            throw e;
        } catch (SubscribeNotPermittedExceptionResponse e) {
            throw e;
        } catch (NoSuchNameExceptionResponse e) {
            throw e;
        } catch (SubscriptionControlsExceptionResponse e) {
            throw e;
        } catch (QueryParameterExceptionResponse e) {
            throw e;
        } catch (Exception e) {
            String msg = "Unknown error occurred while processing request";
            LOG.error(msg, e);
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            if (subscribe != null) {
                ie.setQueryName(subscribe.getQueryName());
                ie.setSubscriptionID(subscribe.getSubscriptionID());
            }
            throw new ImplementationExceptionResponse(msg, ie, e);
        }
    }

    /**
     * @see org.accada.epcis.soap.EPCISServicePortType#unsubscribe(org.accada.epcis.soap.model.Unsubscribe)
     */
    public VoidHolder unsubscribe(Unsubscribe unsubscribe) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchSubscriptionExceptionResponse {
        // log and wrap any error that is not one of the expected exceptions
        try {
            queryModule.unsubscribe(unsubscribe.getSubscriptionID());
            return new VoidHolder();
        } catch (ImplementationExceptionResponse e) {
            throw e;
        } catch (SecurityExceptionResponse e) {
            throw e;
        } catch (NoSuchSubscriptionExceptionResponse e) {
            throw e;
        } catch (Exception e) {
            String msg = "Unknown error occurred while processing request";
            LOG.error(msg, e);
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            if (unsubscribe != null) {
                ie.setSubscriptionID(unsubscribe.getSubscriptionID());
            }
            throw new ImplementationExceptionResponse(msg, ie, e);
        }
    }

    /**
     * @return the queryModule
     */
    public EpcisQueryControlInterface getQueryModule() {
        return queryModule;
    }

    /**
     * @param queryModule
     *            the queryModule to set
     */
    public void setQueryModule(EpcisQueryControlInterface queryModule) {
        this.queryModule = queryModule;
    }

}
