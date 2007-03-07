package org.accada.epcis.repository;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.management.Notification;
import javax.management.timer.Timer;
import javax.xml.rpc.ServiceException;

import org.accada.epcis.soapapi.ActionType;
import org.accada.epcis.soapapi.ArrayOfString;
import org.accada.epcis.soapapi.EPCISQueryBodyType;
import org.accada.epcis.soapapi.EPCISServiceBindingStub;
import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.NoSuchNameException;
import org.accada.epcis.soapapi.Poll;
import org.accada.epcis.soapapi.QueryParam;
import org.accada.epcis.soapapi.QueryParameterException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.soapapi.QueryTooComplexException;
import org.accada.epcis.soapapi.QueryTooLargeException;
import org.accada.epcis.soapapi.SecurityException;
import org.accada.epcis.soapapi.ValidationException;
import org.apache.axis.types.URI;
import org.apache.log4j.Logger;

/**
 * Implementation of triggers. The Schedule checks every 10 minuntes on the
 * trigger condition. If the trigger condition is met: execute the query
 * associated to the subscription The checking frequency may be changed in the
 * QueryOperationsModule.
 * 
 * @author Andrea Grössbauer
 * 
 */
public class QuerySubscriptionTriggered extends QuerySubscriptionScheduled {

	private static final Logger LOG = Logger
			.getLogger(QuerySubscriptionTriggered.class);

	private static URI trigger;

	private static final long serialVersionUID = 1L;

	private static String triggerQuery;

	public QuerySubscriptionTriggered(String subscriptionID,
			QueryParam[] queryParams, URI dest, Boolean reportIfEmpty,
			GregorianCalendar initialRecordTime,
			GregorianCalendar lastTimeExecuted, String queryName, URI aTrigger,
			Schedule every10min) throws ImplementationException {
		super(subscriptionID, queryParams, dest, reportIfEmpty,
				initialRecordTime, lastTimeExecuted, every10min, queryName);
		this.trigger = aTrigger;
		prepareTriggerQuery();
	}

	private void prepareTriggerQuery() {
		// TODO This part may be clear in subsequent versions of the spec
	}

	/**
	 * @see org.accada.epcis.repository.QuerySubscriptionScheduled#handleNotification(javax.management.Notification, java.lang.Object)
	 *  First checks on the trigger condition: if fulfilled then execute Query.
	 */
	@Override
	public void handleNotification(Notification pNotification, Object pHandback) {
		EPCISServiceBindingStub epcisQueryService;
		 if (pHandback == null) {
	            LOG.error("The timer stating the next scheduled query execution time is null!");
	            return;
	        }
		 if (!doItAgain) {
	            ((Timer) pHandback).stop();
	        } else {
					try {
						try {
							LOG.debug("Checking trigger condition.");
							epcisQueryService = (EPCISServiceBindingStub) this.service
									.getEPCglobalEPCISServicePort();
							String queryName = "SimpleEventQuery";
							String[] epcs = {trigger.toString()};
							QueryParam[] queryParam = {
									new QueryParam("MATCH_anyEPC", new ArrayOfString(epcs, null)),
									new QueryParam("GE_recordTime", initialRecordTime) };
							QueryResults results = epcisQueryService
									.poll(new Poll(queryName, queryParam));
							if (results.getResultsBody().getEventList() != null) {
								LOG.debug("Trigger condition fulfilled!");
								LOG.debug("Executing subscribed query associated with trigger event.");
								// returns the events specified by the query parameters of
								// the subscription
								super.executeQuery();
							}
						} catch (ServiceException e) {
							String msg = "An error occured getting the service locator : "
									+ e.getMessage();
							LOG.error(msg, e);
						} catch (QueryTooComplexException e) {
							// send exception back to client
							EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
							queryBody.setQueryTooComplexException(e);
							serializeAndSend(queryBody);
							return;
						} catch (ImplementationException e) {
							// send exception back to client
							EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
							queryBody.setImplementationException(e);
							serializeAndSend(queryBody);
							return;
						} catch (QueryTooLargeException e) {
							// send exception back to client
							EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
							queryBody.setQueryTooLargeException(e);
							serializeAndSend(queryBody);
							return;
						} catch (QueryParameterException e) {
							// send exception back to client
							EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
							queryBody.setQueryParameterException(e);
							serializeAndSend(queryBody);
							return;
						} catch (ValidationException e) {
							// send exception back to client
							EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
							queryBody.setValidationException(e);
							serializeAndSend(queryBody);
							return;
						} catch (SecurityException e) {
							// send exception back to client
							EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
							queryBody.setSecurityException(e);
							serializeAndSend(queryBody);
							return;
						} catch (NoSuchNameException e) {
							// send exception back to client
							EPCISQueryBodyType queryBody = new EPCISQueryBodyType();
							queryBody.setNoSuchNameException(e);
							serializeAndSend(queryBody);
							return;
						}
					} catch (IOException e) {
						String msg = "An error opening a connection to '" + this.dest
								+ "' or serializing and sending contents occured: "
								+ e.getMessage();
						LOG.error(msg, e);
					}
				
					 // determine next scheduled execution time
	                Date nextSchedule;
					try {
						nextSchedule = schedule.nextScheduledTime().getTime();
						LOG.debug("Next scheduled time for the subscribed query is '"
		                        + nextSchedule + "'.");
		                ((Timer) pHandback).addNotification("SubscriptionSchedule",
		                        "Please do the query", (Timer) pHandback, nextSchedule);
					} catch (ImplementationException e) {
						 String msg = "The next scheduled date for the subscribed query with ID '"
		                        + getSubscriptionID()
		                        + "' cannot be evaluated: "
		                        + e.getReason();
		                LOG.error(msg, e);
					}
	        }
	}

	/**
	 * Getter method for the trigger attribute
	 * 
	 * @return
	 */
	public URI getTrigger() {
		return this.getTrigger();
	}

}
