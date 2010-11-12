package org.epcis.fosstrak.restadapter.model.epc;

import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.model.Entry;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Serialized Global Trade Item Number
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 *
 */
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class SGTIN implements EPC {

    public static final String NAME = "Serialized Global Trade Item Number";
    public static final String ID   = SGTIN_PREFIX;    // "urn:epc:id:sgtin:";
    private String             epc;
    private String             companyPrefix;
    private String             itemReference;
    private String             serialNumber;

    /**
     * Constructs Serialized Global Trade Item Number
     *
     *
     * @param epc
     */
    public SGTIN(String epc) {
        this.epc = epc;
        initEPC();
    }

    private void initEPC() {
        String part[] = epc.split("\\.");

        if (part.length == 3) {
            companyPrefix = part[0];
            itemReference = part[1];
            serialNumber  = part[2];
        }
    }

    /**
     * Get a list of the components of this EPC
     *
     *
     * @return
     */
    @XmlElement(name = "component")
    public List<Entry> getComponents() {
        List<Entry> res    = new ArrayList<Entry>();

        Entry       entry1 = new Entry();
        Entry       entry2 = new Entry();
        Entry       entry3 = new Entry();

        entry1.setName(COMPANY_PREFIX);
        entry1.setValue(companyPrefix);
        entry1.setValueRef(ID + companyPrefix + "." + Config.STAR);

        entry2.setName(ITEM_REFERENCE);
        entry2.setValue(itemReference);
        entry2.setValueRef(ID + companyPrefix + "." + itemReference + "." + Config.STAR);

        entry3.setName(SERIAL_NUMBER);
        entry3.setValue(serialNumber);
        entry3.setValueRef(ID + companyPrefix + "." + itemReference + "." + serialNumber);

        if (entry1.getValue() != null) {
            res.add(entry1);
        }

        if (entry2.getValue() != null) {
            res.add(entry2);
        }

        if (entry3.getValue() != null) {
            res.add(entry3);
        }

        return res;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getName() {
        return NAME;
    }
}
