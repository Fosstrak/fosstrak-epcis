package org.accada.epcis.captureclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * This client provides access to the EPCIS Capture Interface.
 *
 * @author Marco Steybe
 */
public class CaptureInterfaceClient {

    private static final Logger LOG =
            Logger.getLogger(CaptureInterfaceClient.class);

    private static final String PROPERTY_FILE = "src/main/resources/application.properties";

    private static final String PROPERTY_CAPTURE_URL = "default.url.capture";

    private String captureUrl;

    private Properties props = new Properties();

    /**
     * Constructs a new CaptureClient which connects to the EPCIS capture
     * interface listening at the default URL.
     */
    public CaptureInterfaceClient() {
        init();
        captureUrl = props.getProperty(PROPERTY_CAPTURE_URL);
    }

    /**
     * Constructs a new CaptureClient which connects to the EPCIS capture
     * interface listening at the given URL.
     */
    public CaptureInterfaceClient(String url) {
        init();
        captureUrl = url;
    }

    /**
     * Reads the property file.
     */
    private void init() {
        File f = null;
        try {
            f = new File(PROPERTY_FILE);
            props.load(new FileInputStream(f));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties from file "
                    + f, e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Reading property file from " + f);
        }
    }

    /**
     * Sends an EPCIS ObjectEvent to the EPCIS Capture Interface using an HTTP
     * POST request.
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
        URL serviceUrl = new URL(captureUrl);

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
