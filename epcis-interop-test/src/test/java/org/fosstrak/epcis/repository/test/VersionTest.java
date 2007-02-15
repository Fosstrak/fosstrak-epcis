package org.accada.epcis.repository.test;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.EPCISException;

/**
 * Test for getStandardVersion() and getVendorVersion() (SE47 and SE67).
 * 
 * @author Marco Steybe
 */
public class VersionTest extends TestCase {

    private QueryControlClient client = new QueryControlClient();

    /**
     * Tests if the supported Standard Version is "1.0".
     * 
     * @throws ServiceException
     *             If an EPCIS query service error occurs.
     * @throws RemoteException
     *             If an Axis error occurs.
     * @throws EPCISException
     *             If an error with the EPCIS repository implementation occurs.
     */
    public void testSE47() throws EPCISException, RemoteException,
            ServiceException {
        String stdVersion = client.getStandardVersion();
        assertEquals(stdVersion, "1.0");
    }

    /**
     * Tests if the vendor version is defined.
     * 
     * @throws EPCISException
     *             If an error with the EPCIS repository implementation occurs.
     * @throws RemoteException
     *             If an Axis error occurs.
     * @throws ServiceException
     *             If an EPCIS query service error occurs.
     */
    public void testSE67() throws EPCISException, RemoteException,
            ServiceException {
        String version = client.getVendorVersion();
        assertTrue(version.startsWith("http://www.accada.org/releases/"));
    }
}