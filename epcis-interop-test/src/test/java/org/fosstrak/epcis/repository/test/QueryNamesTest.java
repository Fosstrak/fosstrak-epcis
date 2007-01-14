package org.accada.epcis.repository.test;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import org.accada.epcis.queryclient.QueryClientInterface;
import org.accada.epcis.queryclient.QueryClientSoapImpl;
import org.accada.epcis.soapapi.EPCISException;

/**
 * Test for getQueryNames() (SE45).
 * 
 * @author Marco Steybe
 */
public class QueryNamesTest extends TestCase {

    QueryClientInterface client = new QueryClientSoapImpl();

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
        String[] queryNames = client.queryNames();

        // must contain SimpleEventQuery and SimpleMasterDataQuery
        assertTrue(queryNames.length == 2);

        List<String> queryNameList = Arrays.asList(queryNames);
        assertTrue(queryNameList.contains("SimpleEventQuery"));
        assertTrue(queryNameList.contains("SimpleMasterDataQuery"));
    }
}