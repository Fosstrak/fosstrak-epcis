package org.accada.epcis.queryclient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.accada.epcis.soapapi.DuplicateSubscriptionException;
import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.InvalidURIException;
import org.accada.epcis.soapapi.NoSuchNameException;
import org.accada.epcis.soapapi.QueryParameterException;
import org.accada.epcis.soapapi.QueryTooComplexException;
import org.accada.epcis.soapapi.SecurityException;
import org.accada.epcis.soapapi.SubscribeNotPermittedException;
import org.accada.epcis.soapapi.SubscriptionControlsException;
import org.accada.epcis.soapapi.ValidationException;
import org.apache.log4j.Logger;

/**
 * This client provides access to the EPCIS Capture Interface.
 * 
 * @author Marco Steybe
 */
public class QueryClientPostImpl extends QueryClientBase {

    private static final Logger LOG = Logger.getLogger(QueryClientPostImpl.class);

    /**
     * Constructs a new QueryClientSoapImpl.
     */
    public QueryClientPostImpl() {
        super();
    }

    /**
     * Constructs a new QueryClientPostImpl.
     * 
     * @param address
     *            The address at which the query service listens.
     */
    public QueryClientPostImpl(final String address) {
        super(address);
    }

    /**
     * Sends an EPCIS query to the EPCIS Query Interface using an HTTP POST
     * request. The query will be wrapped into a SOAP message; the response will
     * be unwrapped from the SOAP response message.
     * 
     * @param queryXml
     *            The XML containing the query.
     * @return The response XML from the EPCIS Query Interface
     * @throws IOException
     *             If an I/O Exception on the transport layer (HTTP) occurred.
     */
    public String runQuery(InputStream query) throws IOException {
        byte[] q = new byte[query.available()];
        query.read(q);
        query.close();
        String queryXml = new String(q);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sending query request:");
            LOG.debug(queryXml);
        }

        String soapReq = wrapIntoSoap(queryXml);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sending SOAP request:");
            LOG.debug(soapReq);
        }

        String soapResp = postData(soapReq.getBytes());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Receiving SOAP response:");
            LOG.debug(soapResp);
        }

        String queryResp = unwrapFromSoap(soapResp);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Receiving query response:");
            LOG.debug(queryResp);
        }

        return queryResp;
    }

    /**
     * @see org.accada.epcis.queryclient.QueryClientInterface#subscribeQuery(java.io.InputStream)
     */
    public void subscribeQuery(InputStream xmlQuery) throws ServiceException,
            QueryTooComplexException, ImplementationException,
            InvalidURIException, SubscribeNotPermittedException,
            SubscriptionControlsException, QueryParameterException,
            ValidationException, SecurityException,
            DuplicateSubscriptionException, NoSuchNameException,
            RemoteException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not yet implemented");
    }

    private String wrapIntoSoap(String queryXml) {
        StringBuffer soap = new StringBuffer();
        soap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        soap.append("<soapenv:Envelope ");
        soap.append("xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        soap.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        soap.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
        soap.append("<soapenv:Body>");
        soap.append(queryXml);
        soap.append("</soapenv:Body>\n");
        soap.append("</soapenv:Envelope>");
        return soap.toString();
    }

    private String unwrapFromSoap(String soapResp) {
        int beginIndex = soapResp.indexOf("<soapenv:Body>")
                + "<soapenv:Body>".length();
        int endIndex = soapResp.lastIndexOf("</soapenv:Body>");
        return soapResp.substring(beginIndex, endIndex);
    }

    private String postData(byte[] data) throws IOException {
        String response;

        // the url where the query interface listens
        URL serviceUrl = new URL(queryUrl);

        // open an http connection
        HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();

        // post the data
        connection.setDoOutput(true);
        connection.addRequestProperty("SOAPAction", "");
        OutputStream out = connection.getOutputStream();
        out.write(data);
        out.flush();
        out.close();

        // check for http error
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            response = "Error " + connection.getResponseCode() + " "
                    + connection.getResponseMessage() + ": ";
        } else {
            response = "200 OK: ";
        }

        // read and return response
        InputStream in = null;
        try {
            in = connection.getInputStream();
        } catch (IOException e) {
            in = connection.getErrorStream();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine()) != null) {
            response = response + line + "\n";
        }
        return response.trim();
    }

    /**
     * Only for testing purposes!
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        QueryClientPostImpl client = new QueryClientPostImpl(
                "http://localhost:8080/epcis-repository/services/EPCglobalEPCISService");
        String query = "JUDIHUI";
        System.out.println("query to be sent:");
        System.out.println(query);

        String soapReq = client.wrapIntoSoap(query);
        System.out.println();
        System.out.println("constructed soap message:");
        System.out.println(soapReq);

        String soapResp = client.unwrapFromSoap(soapReq);
        System.out.println();
        System.out.println("extracted soap body:");
        System.out.println(soapResp);

        // testing the actual query service
        FileInputStream fis = new FileInputStream(
                "test/data/queries/webservice/requests/Test-EPCIS10-SE10-Request-1-poll_mod.xml");
        // String queryXml = "<Poll
        // xmlns=\"urn:epcglobal:epcis-query:xsd:1\"><queryName
        // xmlns=\"\">SimpleEventQuery</queryName><params
        // xmlns=\"\"><param><name>eventType</name><value
        // xsi:type=\"ns1:ArrayOfString\"
        // xmlns:ns1=\"urn:epcglobal:epcis-query:xsd:1\"><string>AggregationEvent</string></value></param><param><name>EQ_action</name><value
        // xsi:type=\"ns2:ArrayOfString\"
        // xmlns:ns2=\"urn:epcglobal:epcis-query:xsd:1\"><string>ADD</string></value></param><param><name>MATCH_parentID</name><value
        // xsi:type=\"ns3:ArrayOfString\"
        // xmlns:ns3=\"urn:epcglobal:epcis-query:xsd:1\"><string>urn:x:bar:5:036544:007325</string></value></param></params></Poll>";
        String resp = client.runQuery(fis);
        System.out.println(resp);
    }
}
