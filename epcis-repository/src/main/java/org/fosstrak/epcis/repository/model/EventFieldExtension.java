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

package org.accada.epcis.repository.model;

import java.sql.Timestamp;

import org.accada.epcis.utils.TimeParser;
import org.apache.log4j.Logger;

/**
 * A simple representation for an event field extension.
 * 
 * @author Marco Steybe
 */
public class EventFieldExtension {

    private static final Logger LOG = Logger.getLogger(EventFieldExtension.class);

    private Long id;
    private String prefix = null;
    private String namespace = null;
    private String localname = null;
    private String fieldname = null;
    private String valueColumnName = null;
    private Integer intValue = null;
    private Float floatValue = null;
    private Timestamp dateValue = null;
    private String strValue = null;

    /**
     * Constructs a new EventFieldExtension from the parameters. The value will
     * be parsed to see if it represents an integer, a float, a date, or a
     * string.
     * 
     * @param prefix
     *            The prefix of the event field.
     * @param namespace
     *            The namespace for the prefix.
     * @param localname
     *            The localname (tag name) of the event field.
     * @param value
     *            The value of the event field.
     */
    public EventFieldExtension(final String prefix, final String namespace,
            final String localname, final String value) {
        this.localname = localname;
        this.prefix = prefix;
        this.namespace = namespace;
        this.fieldname = namespace + "#" + localname;
        LOG.debug("    resolved fieldname of extension to '" + fieldname + "'");
        this.strValue = value;
        // try parsing the value
        try {
            this.intValue = new Integer(strValue);
            this.valueColumnName = "intValue";
            LOG.debug("    value of extension is of type Integer.");
        } catch (NumberFormatException e1) {
            try {
                this.floatValue = new Float(strValue);
                this.valueColumnName = "floatValue";
                LOG.info("    value of extension is of type Float.");
            } catch (NumberFormatException e2) {
                try {
                    this.dateValue = TimeParser.parseAsTimestamp(strValue);
                    this.valueColumnName = "dateValue";
                    LOG.info("    value of extension is of type Date.");
                } catch (Exception e3) {
                    // treat value as String
                    LOG.info("    value of extension is of type String.");
                    this.valueColumnName = "strValue";
                }
            }
        }
    }
    
    public EventFieldExtension() {
    	super();
    }
    
    public Long getId() {
		return id;
	}

    public void setId(Long id) {
		this.id = id;
	}

	/**
     * @return The date value of the event field extension.
     */
    public Timestamp getDateValue() {
        return dateValue;
    }

    /**
     * @return The name of the event field extension.
     */
    public String getFieldname() {
        return fieldname;
    }

    /**
     * @return The float value of the event field extension.
     */
    public Float getFloatValue() {
        return floatValue;
    }

    /**
     * @return The int value of the event field extension.
     */
    public Integer getIntValue() {
        return intValue;
    }

    /**
     * @return The localname of the event field extension.
     */
    public String getLocalname() {
        return localname;
    }

    /**
     * @return The namespace of the event field extension.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @return The prefix of the event field extension.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return The string value of the event field extension.
     */
    public String getStrValue() {
        return strValue;
    }

    /**
     * @return The value column name of the event field extension.
     */
    public String getValueColumnName() {
        return valueColumnName;
    }

	public void setDateValue(Timestamp dateValue) {
		this.dateValue = dateValue;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public void setFloatValue(Float floatValue) {
		this.floatValue = floatValue;
	}

	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}

	public void setLocalname(String localname) {
		this.localname = localname;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public void setValueColumnName(String valueColumnName) {
		this.valueColumnName = valueColumnName;
	}

}
