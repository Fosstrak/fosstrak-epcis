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
 * Serial Shipping Container Code

 * @author Mathias Mueller mathias.mueller(at)unifr.ch, @author <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class SSCC implements EPC {

    public static final String NAME = "Serial Shipping Container Code";
    public static final String ID   = SSCC_PREFIX;    // "urn:epc:id:sscc:";
    private String             epc;
    private String             companyPrefix;
    private String             serialReference;

    /**
     * Constructs Serial Shipping Container Code
     *
     *
     * @param epc
     */
    public SSCC(String epc) {
        this.epc = epc;
        initEPC();
    }

    private void initEPC() {
        String part[] = epc.split("\\.");

        if (part.length == 2) {
            companyPrefix   = part[0];
            serialReference = part[1];
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

        entry1.setName(COMPANY_PREFIX);
        entry1.setValue(companyPrefix);
        entry1.setValueRef(ID + companyPrefix + "." + Config.STAR);

        entry2.setName(SERIAL_REFERENCE);
        entry2.setValue(serialReference);
        entry2.setValueRef(ID + companyPrefix + "." + serialReference);

        if (entry1.getValue() != null) {
            res.add(entry1);
        }

        if (entry2.getValue() != null) {
            res.add(entry2);
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
