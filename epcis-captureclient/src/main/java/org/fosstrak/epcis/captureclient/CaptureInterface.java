package org.accada.epcis.captureclient;

import java.io.IOException;

/**
 * This is an interface to the EPCIS repository's Capture Operations Module.
 * 
 * @author Marco Steybe
 */
public interface CaptureInterface {

    /**
     * Sends an EPCISEvent to the repository's Capture Operations Module.
     * 
     * @param event
     *            An XML String containing the EPCISEvent.
     * @return The response from the repository's Capture Operations Module.
     * @throws IOException
     *             If a problem with the given input or an error on the
     *             transport layer occurred.
     */
    String capture(final String event) throws IOException;

}
