package org.accada.epcis.repository.test;

import java.rmi.RemoteException;
import java.util.List;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.EPCISException;

/**
 * Test for getQueryNames() (SE45).
 * 
 * @author Marco Steybe
 */
public class QueryNamesTest extends TestCase {

    private QueryControlClient client = new QueryControlClient();

    /**
     * Tests if the two query types "SimpleEventQuery" and
     * "SimpleMasterDataQuery" are supported by the implementation.
     * 
     * @throws ServiceException
     *             If an EPCIS query service error occurs.
     * @throws RemoteException
     *             If an Axis error occurs.
     * @throws EPCISException
     *             If an error with the EPCIS repository implementation occurs.
     */
    public void testSE45() throws ServiceException, RemoteException,
            EPCISException {
        List<String> queryNames = client.getQueryNames();

        // must contain SimpleEventQuery and SimpleMasterDataQuery
        assertTrue(queryNames.size() == 2);

        assertTrue(queryNames.contains("SimpleEventQuery"));
        assertTrue(queryNames.contains("SimpleMasterDataQuery"));
    }
}