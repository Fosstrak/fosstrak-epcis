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
package org.epcis.fosstrak.restadapter.model.epc;

import org.epcis.fosstrak.restadapter.model.Entry;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The JAXB Electronic Product Code Model
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 */
@XmlRootElement(namespace = "fosstrak.org/epcis/restadapter")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class ElectronicProductCode {

    private Entry epc;
    private EPC   epcFormat;

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElement(name = "electronicProductCode")
    public Entry getEpc() {
        return epc;
    }

    /**
     * Method description
     *
     *
     * @param epc
     */
    public void setEpc(Entry epc) {
        this.epc = epc;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @XmlElementWrapper(name = "components")
    @XmlElement(name = "component")
    public List<Entry> getComponents() {
        List<Entry> res    = new ArrayList<Entry>();

        String      prefix = null;

        try {
            String e = epc.getValue();

            if (e.startsWith(EPC.SSCC_PREFIX)) {
                prefix = EPC.SSCC_PREFIX;

                String myPart = e.split(prefix)[1];

                epcFormat = new SSCC(myPart);
                epc.setDescription(epcFormat.getName());
            }

            if (e.startsWith(EPC.SGLN_PREFIX)) {
                prefix = EPC.SGLN_PREFIX;

                String myPart = e.split(prefix)[1];

                epcFormat = new SGLN(myPart);
                epc.setDescription(epcFormat.getName());
            }

            if (e.startsWith(EPC.SGTIN_PREFIX)) {
                prefix = EPC.SGTIN_PREFIX;

                String myPart = e.split(prefix)[1];

                epcFormat = new SGTIN(myPart);
                epc.setDescription(epcFormat.getName());
            }

            if (e.startsWith(EPC.GRAI_PREFIX)) {
                prefix = EPC.GRAI_PREFIX;

                String myPart = e.split(prefix)[1];

                epcFormat = new GRAI(myPart);
                epc.setDescription(epcFormat.getName());
            }

            if (e.startsWith(EPC.GIAI_PREFIX)) {
                prefix = EPC.GIAI_PREFIX;

                String myPart = e.split(prefix)[1];

                epcFormat = new GIAI(myPart);
                epc.setDescription(epcFormat.getName());
            }

            if (e.startsWith(EPC.GSRN_PREFIX)) {
                prefix = EPC.GSRN_PREFIX;

                String myPart = e.split(prefix)[1];

                epcFormat = new GSRN(myPart);
                epc.setDescription(epcFormat.getName());
            }

            if (e.startsWith(EPC.GDTI_PREFIX)) {
                prefix = EPC.GDTI_PREFIX;

                String myPart = e.split(prefix)[1];

                epcFormat = new GDTI(myPart);
                epc.setDescription(epcFormat.getName());
            }

        } catch (Exception ex) {

            // nothing to do here
        }

        String queryUrl = "";

        res = new ArrayList<Entry>();

        try {
            res = epcFormat.getComponents();
        } catch (Exception ex) {

            // ok
        }

        try {
            queryUrl = epc.getValueRef();

            queryUrl = queryUrl.split(prefix)[0];

            for (Entry entry : res) {
                entry.setValueRef(queryUrl + entry.getValueRef());
            }
        } catch (Exception ex) {

            // do nothing
        }

        return res;
    }
}
