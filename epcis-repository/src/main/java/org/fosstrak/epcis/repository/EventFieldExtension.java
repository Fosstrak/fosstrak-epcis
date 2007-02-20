/*
 * Copyright (c) 2006, 2007, ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the ETH Zurich nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.accada.epcis.repository;

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

    public Timestamp getDateValue() {
        return dateValue;
    }

    public String getFieldname() {
        return fieldname;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public String getLocalname() {
        return localname;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getStrValue() {
        return strValue;
    }

    public String getValueColumnName() {
        return valueColumnName;
    }
}
