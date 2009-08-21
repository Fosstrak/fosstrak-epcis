/*
 * Copyright (C) 2007 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org).
 *
 * Fosstrak is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Fosstrak is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Fosstrak; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.fosstrak.epcis.captureclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple test utility class for demonstrating how to capture EPCIS events
 * given in its XML representation to a EPCIS capture application. This class
 * can be invoked on the command line with the following arguments:
 * 
 * <pre>
 * java org.fosstrak.epcis.captureclient.SimpleCaptureApp url=&lt;epcis-capture-url&gt; xml=&lt;xml-file-name&gt;
 * </pre>
 * 
 * @author Marco Steybe
 */
public class SimpleCaptureApp {

	protected static final String LOCAL_EPCIS_CAPTURE_URL = "http://localhost:8080/epcis-repository/capture";
	protected static final String DEMO_EPCIS_CAPTURE_URL = "http://demo.fosstrak.org/epcis/capture";
	private static final String SAMPLE_CAPTURE_REQUEST_XML = "sampleCaptureRequest.xml";

	/**
	 * Reads a sample EPCIS capture request and submits it to an EPCIS
	 * repository instance.
	 * 
	 * @param args
	 *            Valid command-line arguments are the URL of the EPCIS
	 *            repository (<code>url=</code>), and the name of the XML file
	 *            from which the EPCIS capture request is read (
	 *            <code>xml=</code>). Default values will be provided if any of
	 *            these arguments is omitted.
	 */
	public static void main(String[] args) throws Exception {
		String captureUrl = LOCAL_EPCIS_CAPTURE_URL;
		String xmlFile = SAMPLE_CAPTURE_REQUEST_XML;
		if (args.length > 0) {
			// read command-line arguments
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith("url=")) {
					captureUrl = args[i].substring(4);
				} else if (args[i].startsWith("xml=")) {
					xmlFile = args[i].substring(4);
				} else {
					System.out.println("ignoring command-line argument: " + args[i]);
				}
			}
		}
		System.out.println("using capture URL: " + captureUrl);
		System.out.println("reading input from: " + xmlFile);

		// configure the capture client and send the request
		CaptureClient client = new CaptureClient(captureUrl);
		InputStream xmlStream = getInputStream(xmlFile);
		int httpResponseCode = client.capture(xmlStream);

		if (httpResponseCode == 200) {
			System.out.println("capture of events successful");
		} else {
			System.err.println("HTTP response " + httpResponseCode);
		}
	}

	/**
	 * Tries to get an InputStream from the given file name. The file name can
	 * be given with an absolute path or relative to the current ClassLoader.
	 * 
	 * @throws IOException
	 *             If no file input could be found.
	 */
	private static InputStream getInputStream(String fileName) throws IOException {
		File file = new File(fileName);
		InputStream is;
		if (file.exists()) {
			is = new FileInputStream(file);
		} else {
			is = SimpleCaptureApp.class.getResourceAsStream(fileName);
		}
		if (is == null) {
			throw new IOException("input file not found: " + fileName);
		}
		return is;
	}
}
