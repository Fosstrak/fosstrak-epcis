package org.fosstrak.epcis.queryclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class QueryClientTestHelper {

    public static final String LOCAL_EPCIS_QUERY_URL = "http://localhost:8080/epcis-repository/query";
    public static final String DEMO_EPCIS_QUERY_URL = "http://demo.fosstrak.org/epcis/query";
    public static final String SAMPLE_EVENT_QUERY_XML = "src/test/resources/sampleSimpleEventQuery.xml";
    public static final String SAMPLE_MASTERDATA_QUERY_XML = "src/test/resources/sampleMasterDataQuery.xml";

    /**
     * Tries to get an InputStream from the given file name. The file name can
     * be given with an absolute path or relative to the current ClassLoader.
     * 
     * @throws IOException
     *             If no file input could be found.
     */
    public static InputStream getInputStream(String fileName) throws IOException {
        File file = new File(fileName);
        InputStream is;
        if (file.exists()) {
            is = new FileInputStream(file);
        } else {
            is = QueryClientTestHelper.class.getResourceAsStream(fileName);
        }
        if (is == null) {
            throw new IOException("input file not found: " + fileName);
        }
        return is;
    }
}
