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
package org.epcis.fosstrak.restadapter.db;

/**
 *
 * Bean to hold information about the hierarchy of locations and readers
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch
 *
 */
public class LocationReaderConnection {

    private String reader;
    private String location;

    /**
     * Constructs ...
     *
     *
     * @param location
     * @param reader
     */
    public LocationReaderConnection(String location, String reader) {
        this.location = location;
        this.reader   = reader;
    }

    /**
     * @return the reader
     */
    public String getReader() {
        return reader;
    }

    /**
     * @param reader the reader to set
     */
    public void setReader(String reader) {
        this.reader = reader;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Method description
     *
     *
     * @param o
     *
     * @return
     */
    @Override
    public boolean equals(Object o) {
        boolean res = false;

        if (o instanceof LocationReaderConnection) {
            LocationReaderConnection lrConnection = (LocationReaderConnection) o;

            if (this.location.equals(lrConnection.getLocation())) {
                if (this.reader.equals(lrConnection.getReader())) {
                    res = true;
                }
            }
        } else {
            res = super.equals(o);
        }

        return res;
    }

    /**
     * Hash code check
     *
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 5;

        hash = 73 * hash + ((this.reader != null)
                            ? this.reader.hashCode()
                            : 0);
        hash = 73 * hash + ((this.location != null)
                            ? this.location.hashCode()
                            : 0);

        return hash;
    }

}
