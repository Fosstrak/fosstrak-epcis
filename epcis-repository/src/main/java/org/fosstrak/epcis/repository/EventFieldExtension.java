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
