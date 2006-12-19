package org.accada.epcis.captureclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * refactoring: this class should be renamed to CaptureInterfaceClient. the
 * existing class with this name should be renamed to CaptureInterfaceClientGui.
 * maybe also rename CaptureInterfaceClientSwingGui to
 * CaptureInterfaceClientWindow -> it should use this CaptureInterfaceClient
 * (has-a relation).
 * 
 * also these classes should not be in directory capturingGUI.
 * 
 * This client provides access to the EPCIS Capture Interface.
 * 
 * @author Marco Steybe
 */
public class CaptureClient {

    // TODO LOG4J

    // TODO properties file to hold url
    private static final String CAPTURE_INTERFACE_URL =
            "http://localhost:8080/capturing/servlet/capture";

    public CaptureClient() {
    }

    /**
     * Sends an EventObject to the EPCIS Capture Interface using an HTTP POST
     * request.
     * 
     * @param eventXml
     *            The XML containing the EventObject.
     * @return The response from the EPCIS Capture Interface
     * @throws IOException
     *             If an I/O Exception on the transport layer (HTTP) occurred.
     */
    public String sendEvent(String eventXml) throws IOException {
        byte[] data = ("event=" + eventXml).getBytes();
        return postData(data);
    }

    private String postData(byte[] data) throws IOException {
        String response;

        // the url where the capture interface listens
        URL serviceUrl = new URL(CAPTURE_INTERFACE_URL);

        // open an http connection
        HttpURLConnection connection =
                (HttpURLConnection) serviceUrl.openConnection();

        // post the data
        connection.setDoOutput(true);
        OutputStream out = connection.getOutputStream();
        out.write(data);
        out.flush();
        out.close();

        // check for http error
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            response =
                    "Error " + connection.getResponseCode() + " "
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
}
