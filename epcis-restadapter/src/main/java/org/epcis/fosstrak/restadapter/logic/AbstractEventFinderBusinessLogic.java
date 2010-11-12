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
package org.epcis.fosstrak.restadapter.logic;

import org.epcis.fosstrak.restadapter.db.InternalDatabase;
import org.epcis.fosstrak.restadapter.db.LocationReaderConnection;
import org.epcis.fosstrak.restadapter.http.HTTPStatusCodeMapper;
import org.epcis.fosstrak.restadapter.model.events.EPCISEvent;
import org.epcis.fosstrak.restadapter.ws.generated.QueryResults;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * Class containing methods to fetch the EPCIS for events, locations, readers and times
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch
 *
 */
public abstract class AbstractEventFinderBusinessLogic extends AbstractEventBusinessLogic {

    private Collection<LocationReaderConnection> loadLocationReaderConnections() {
        Collection<LocationReaderConnection> values = new HashSet<LocationReaderConnection>();

        try {
            QueryResults objectEvents = getEventTypeEvents("ObjectEvent");

            values.addAll(filterResultsForLocationReaderConnections(objectEvents));

            QueryResults aggregationEvents = getEventTypeEvents("AggregationEvent");

            values.addAll(filterResultsForLocationReaderConnections(aggregationEvents));

            QueryResults quantityEvents = getEventTypeEvents("QuantityEvent");

            values.addAll(filterResultsForLocationReaderConnections(quantityEvents));

            QueryResults transactionEvents = getEventTypeEvents("TransactionEvent");

            values.addAll(filterResultsForLocationReaderConnections(transactionEvents));

        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        return values;
    }

    private Collection<String> loadLocations(Collection<LocationReaderConnection> connections) {
        Collection<String> values = new HashSet<String>();

        for (LocationReaderConnection connection : connections) {
            values.add(connection.getLocation());
        }

        return values;
    }

    /**
     * Reloads the event finder in the database
     *
     */
    public void reload() {
        Collection<LocationReaderConnection> connections = null;
        Collection<String>                   locations   = null;

        connections = loadLocationReaderConnections();
        locations   = loadLocations(connections);

        InternalDatabase.getInstance().setLocations(locations);
        InternalDatabase.getInstance().setLocationReaderConnections(connections);
    }

    /**
     * Fetches all readers from the EPCIS
     *
     *
     * @return
     */
    public List<String> getAllReaders() {
        List<String> res = InternalDatabase.getInstance().getReaders();

        Collections.sort(res);

        return res;
    }

    /**
     * Fetches all locations from the EPCIS
     *
     *
     * @return
     */
    public List<String> getLocations() {
        List<String> res = InternalDatabase.getInstance().getLocations();

        Collections.sort(res);

        return res;
    }

    /**
     * Fetches all readers of a specific location from the EPCIS
     *
     *
     * @param location
     *
     * @return
     */
    public List<String> getReaders(String location) {
        List<String> res = InternalDatabase.getInstance().getReaders(location);

        Collections.sort(res);

        return res;
    }

    /**
     * Fetches all times of all events determined by its location and reader from the EPCIS
     *
     *
     * @param location
     * @param reader
     *
     * @return
     */
    public Collection<String> getTimes(String location, String reader) {
        Collection<String> values = new HashSet<String>();

        try {
            QueryResults timeEvents = getFilteredEvents(location, reader);

            values = filterResultsForEventTimes(timeEvents);

        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        return values;
    }

    /**
     * Fetches an event determined by its location, reader and time from the EPCIS
     *
     *
     * @param location
     * @param reader
     * @param time
     * @param index
     *
     * @return
     */
    public EPCISEvent getEvent(String location, String reader, String time, String index) {
        EPCISEvent event = null;

        try {
            QueryResults timeEvents = getFilteredEvents(location, reader, time);
            int          i          = Integer.parseInt(index);

            event = getEPCISEvent(timeEvents, i);

        } catch (Exception ex) {
            ex.printStackTrace();

            throw new WebApplicationException(Response.status(HTTPStatusCodeMapper.mapExceptionToHttpStatusCode(ex)).entity(ex.getMessage()).build());
        }

        return event;
    }
}
