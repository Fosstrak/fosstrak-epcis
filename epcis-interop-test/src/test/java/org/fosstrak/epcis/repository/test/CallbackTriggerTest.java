/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Accada (www.accada.org).
 *
 * Accada is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Accada is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Accada; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.accada.epcis.repository.test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.accada.epcis.captureclient.CaptureClient;
import org.accada.epcis.queryclient.QueryControlClient;
import org.accada.epcis.soapapi.NoSuchSubscriptionException;
import org.accada.epcis.soapapi.QueryResults;
import org.accada.epcis.utils.QueryCallbackListener;
import org.accada.epcis.utils.QueryResultsParser;

import junit.framework.TestCase;

public class CallbackTriggerTest extends TestCase {
	private static final String PATH = "src/test/resources/queries/webservice/";

    private QueryControlClient client = new QueryControlClient();

    /**
     * Tests if setting the initialRecordTime parameter has effect.
     * 
     * @throws Exception
     *             If an error executing the test occured.
     */
    public void testSE75() throws Exception {

        // run first query
        String query = "Test-EPCIS10-SE75-Request-1-Subscribe.xml";
        InputStream fis = new FileInputStream(PATH + "requests/" + query);
        client.subscribe(fis);
        fis.close();
        
        new CaptureTrigger().start();

        // wait for response callback
        QueryCallbackListener listener = QueryCallbackListener.getInstance();
        if (!listener.isRunning()) {
            listener.start();
        }
        System.out.println("waiting ...");
        synchronized (listener) {
            try {
                listener.wait(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String resp = listener.fetchResponse();
        assertNotNull(resp);
        assertTrue(resp.contains("urn:epc:id:sgtin:0614141.107340.1"));

        client.unsubscribe("QuerySE75");
        listener.stopRunning();
    }

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        try {
            client.unsubscribe("QuerySE75");
        } catch (NoSuchSubscriptionException e) {
        }
        super.tearDown();
    }
    
    private class CaptureTrigger extends Thread {
    	
    	private StringBuilder event = new StringBuilder();
    	
    	public CaptureTrigger() {
    		event.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    		event.append("<epcis:EPCISDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" xmlns:epcglobal=\"urn:epcglobal:xsd:1\" xsi:schemaLocation=\"urn:epcglobal:epcis:xsd:1 EPCglobal-epcis-1_0.xsd\" xmlns:hls=\"http://schema.hls.com/extension\" creationDate=\"2006-06-25T00:00:00Z\" schemaVersion=\"1.0\">");
    		event.append("<EPCISBody>");
    		event.append("<EventList>");
    		event.append("<ObjectEvent>");
    		event.append("<eventTime>2006-08-25T00:01:00Z</eventTime>");
    		event.append("<eventTimeZoneOffset>-06:00</eventTimeZoneOffset>");
    		event.append("<epcList>");
    		event.append("<epc>urn:epc:id:sgtin:0614141.107340.1</epc>");
    		event.append("</epcList>");
    		event.append("<action>OBSERVE</action>");
    		event.append("<bizStep>urn:epcglobal:hls:bizstep:commissioning</bizStep>");
    		event.append("<disposition>urn:epcglobal:hls:disp:active</disposition>");
    		event.append("<readPoint>");              
    		event.append("<id>urn:epcglobal:fmcg:loc:0614141073467.RP-1</id>");
    		event.append("</readPoint>");    
    		event.append("<bizLocation>");    
    		event.append("<id>urn:epcglobal:fmcg:loc:0614141073467.1</id>");    
    		event.append("</bizLocation>");    
    		event.append("</ObjectEvent>");    
    		event.append("</EventList>");    
    		event.append("</EPCISBody>");
    		event.append("</epcis:EPCISDocument>");
    	}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			CaptureClient client = new CaptureClient();
			try {
				this.sleep(10000);
				client.capture(event.toString());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
    	
    	
    }

}
