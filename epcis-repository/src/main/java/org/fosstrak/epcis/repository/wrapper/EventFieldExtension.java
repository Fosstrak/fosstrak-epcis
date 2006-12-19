/**
 * 
 */
package org.accada.epcis.repository.wrapper;

import java.sql.Timestamp;

import org.accada.epcis.repository.TimeParser;
import org.apache.log4j.Logger;

/**
 * @author Marco Steybe
 */
public class EventFieldExtension {

    private String prefix = null;
    private String namespace = null;
    private String localname = null;
    private String fieldname = null;
    private String valueColumnName = null;
    private Integer intValue = null;
    private Float floatValue = null;
    private Timestamp dateValue = null;
    private String strValue = null;

    private static final Logger LOG = Logger.getLogger(EventFieldExtension.class);

    public EventFieldExtension(String prefix, String namespace,
            String localname, String value) {
        this.localname = localname;
        this.prefix = prefix;
        this.namespace = namespace;
        this.fieldname = namespace + "#" + localname;
        LOG.info("Resolved fieldname of extension tag to '" + fieldname + "'");
        strValue = value;
        // try parsing the value
        try {
            this.intValue = new Integer(strValue);
            this.valueColumnName = "intValue";
            LOG.info("Value of extension tag is of type Integer.");
        } catch (NumberFormatException e1) {
            try {
                this.floatValue = new Float(strValue);
                this.valueColumnName = "floatValue";
                LOG.info("Value of extension tag is of type Float.");
            } catch (NumberFormatException e2) {
                try {
                    this.dateValue = TimeParser.parseAsTimestamp(strValue);
                    this.valueColumnName = "dateValue";
                    LOG.info("Value of extension tag is of type Date.");
                } catch (Exception e3) {
                    // treat value as String
                    LOG.info("Value of extension tag is of type String.");
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
