/**
 * 
 */
package org.accada.epcis.queryclient;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.accada.epcis.soapapi.ArrayOfString;
import org.accada.epcis.soapapi.EPCISServiceBindingStub;
import org.accada.epcis.soapapi.EPCglobalEPCISServiceLocator;
import org.accada.epcis.soapapi.EmptyParms;
import org.accada.epcis.soapapi.GetSubscriptionIDs;
import org.accada.epcis.soapapi.ImplementationException;
import org.accada.epcis.soapapi.NoSuchNameException;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;
import org.accada.epcis.soapapi.SecurityException;
import org.accada.epcis.soapapi.Unsubscribe;
import org.accada.epcis.soapapi.ValidationException;
import org.apache.log4j.Logger;

/**
 * This query client implements the base client operations. For a running
 * client, instantiate one of the subclasses of this QueryClient.
 * 
 * @author Marco Steybe
 */
public abstract class QueryClientBase implements QueryClientInterface {

    private static final Logger LOG = Logger.getLogger(QueryClientBase.class);

    private static final String PROPERTY_FILE = "/queryclient.properties";

    private static final String PROPERTY_QUERY_URL = "default.url";
    
    String queryUrl = null;

    /**
     * Holds the locator for the service.
     */
    EPCglobalEPCISServiceLocator service;

    /**
     * Constructs a new QueryClient. In order to connect to the EPCIS Query
     * Interface service properly you need to call
     * {@link org.accada.epcis.queryclient.QueryClientBase#setAddress(String)}
     * explicitly.
     */
    QueryClientBase() {
        service = new EPCglobalEPCISServiceLocator();
        InputStream is = this.getClass().getResourceAsStream(PROPERTY_FILE);
        if (is == null) {
            throw new RuntimeException("Unable to load properties from file "
                    + PROPERTY_FILE);
        }
        try {
            Properties props = new Properties();
            props.load(is);
            queryUrl = props.getProperty(PROPERTY_QUERY_URL);
            service.setEPCglobalEPCISServicePortEndpointAddress(queryUrl);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties from file "
                    + PROPERTY_FILE);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Reading property file from " + PROPERTY_FILE);
        }
    }

    /**
     * Constructs a new QueryClient which connects to the EPCIS Query Interface
     * at the given address (URL).
     * 
     * @param address
     *            The URL the query service is listening at.
     */
    QueryClientBase(final String address) {
        service = new EPCglobalEPCISServiceLocator();
        service.setEPCglobalEPCISServicePortEndpointAddress(address);
    }

    /**
     * Sets the EPCIS Query Interface service's address.
     * 
     * @param address
     *            The URL the query service is listening at.
     */
    public void setAddress(final String address) {
        service.setEPCglobalEPCISServicePortEndpointAddress(address);
    }

    /**
     * @see org.accada.epcis.queryclient.QueryClientInterface#queryNames()
     */
    public String[] queryNames() throws ServiceException,
            ImplementationException, ValidationException, SecurityException,
            RemoteException {
        EPCISServiceBindingStub stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();
        ArrayOfString temp = stub.getQueryNames(new EmptyParms());

        return temp.getString();
    }

    /**
     * @see org.accada.epcis.queryclient.QueryClientInterface#queryStandardVersion()
     */
    public String queryStandardVersion() throws ServiceException,
            ImplementationException, ValidationException, SecurityException,
            RemoteException {
        EPCISServiceBindingStub stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();

        return stub.getStandardVersion(null);
    }

    /**
     * @see org.accada.epcis.queryclient.QueryClientInterface#querySubscriptionIds()
     */
    public String[] querySubscriptionIds() throws ServiceException,
            ImplementationException, ValidationException, SecurityException,
            NoSuchNameException, RemoteException {
        EPCISServiceBindingStub stub;
        stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();

        GetSubscriptionIDs parms = new GetSubscriptionIDs();
        parms.setQueryName("simpleQuery");

        ArrayOfString res = stub.getSubscriptionIDs(parms);

        return res.getString();
    }

    /**
     * @see org.accada.epcis.queryclient.QueryClientInterface#queryVendorVersion()
     */
    public String queryVendorVersion() throws ServiceException,
            ImplementationException, ValidationException, SecurityException,
            RemoteException {
        EPCISServiceBindingStub stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();

        return stub.getVendorVersion(null);
    }

    /**
     * @see org.accada.epcis.queryclient.QueryClientInterface#unsubscribeQuery(java.lang.String)
     */
    public void unsubscribeQuery(String subscriptionId)
            throws ServiceException, ImplementationException,
            NoSuchSubscriptionException, ValidationException,
            SecurityException, RemoteException {

        EPCISServiceBindingStub stub;
        stub = (EPCISServiceBindingStub) service.getEPCglobalEPCISServicePort();

        Unsubscribe parms = new Unsubscribe();
        parms.setSubscriptionID(subscriptionId);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Unsubscribing query with subscriptionID "
                    + subscriptionId);
        }

        stub.unsubscribe(parms);
    }

}
