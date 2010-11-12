/*
 * Copyright (C) 2010 ETH Zurich
 *
 * This file is part of Fosstrak (www.fosstrak.org) and
 * was developed as part of the webofthings.com initiative.
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
package org.epcis.fosstrak.restadapter.util;

import org.epcis.fosstrak.restadapter.config.Config;
import java.io.File;
import java.io.IOException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * This class writes the XML Schema of the JAXB annotated resource model.
 * Used only for documentation.
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 *
 */
public class FileSchemaOutputResolver extends SchemaOutputResolver {

    /**
     * Creates the XML Schema of the JAXB annotated resource model.
     *
     *
     * @param namespaceUri
     * @param suggestedFileName
     *
     * @return
     *
     * @throws IOException
     */
    @Override
    public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
        suggestedFileName = Config.XMLSCHEMA_FILEPATH + suggestedFileName;

        File         file   = new File(suggestedFileName);
        StreamResult result = new StreamResult(file);

        result.setSystemId(file.toURI().toURL().toString());

        return result;
    }
}
