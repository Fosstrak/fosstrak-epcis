package org.fosstrak.epcis.captureclient;

/**
 * This Exception indicates that the CaptureClient encountered a problem while
 * trying to send a request to the EPCIS capture interface.
 * 
 * @author Marco Steybe
 */
public class CaptureClientException extends Exception {

    private static final long serialVersionUID = 4034170925462066270L;

    public CaptureClientException() {
        super();
    }

    public CaptureClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptureClientException(String message) {
        super(message);
    }

    public CaptureClientException(Throwable cause) {
        super(cause);
    }
}
