
package org.epcis.fosstrak.restadapter.representation;

import java.io.StringWriter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * Class to pretty print XML and HTML.

 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 */
public class PrettyPrint {

    /**
     * Pretty print the XML
     *
     *
     * @param xml
     *
     * @return
     */
    public static String prettyPrintXML(String xml) {
        String res = xml;

        try {
            final OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            final Document     document     = DocumentHelper.parseText(xml);
            final StringWriter stringWriter = new StringWriter();
            final XMLWriter    writer       = new XMLWriter(stringWriter, outputFormat);

            writer.write(document);
            writer.flush();
            res = stringWriter.toString();
        } catch (Exception ex) {

            // ex.printStackTrace();
        }

        return res;
    }

    /**
     * Pretty print the HTML
     *
     *
     * @param html
     *
     * @return
     */
    public static String prettyPrintHTML(String html) {
        String res = html;

        try {
            StringWriter stringWriter = new StringWriter();
            OutputFormat format       = OutputFormat.createPrettyPrint();


            format.setTrimText(false);
            format.setIndent("  ");
            format.setXHTML(true);
            format.setExpandEmptyElements(false);


            HTMLWriter writer   = new org.dom4j.io.HTMLWriter(stringWriter, format);
            Document   document = DocumentHelper.parseText(html);

            writer.write(document);
            writer.flush();
            res = stringWriter.toString();


        } catch (Exception ex) {
        }

        return res;
    }

}
