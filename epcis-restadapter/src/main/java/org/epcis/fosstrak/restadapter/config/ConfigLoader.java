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
package org.epcis.fosstrak.restadapter.config;


/**
 * This class loads the user-configurable properties from the config.property
 * file located in /WEB-INF/classes/.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the ConfiguLoader Singleton which which loads all
 * user-configurable properties from the config.property
 * file located in /WEB-INF/classes/.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
public class ConfigLoader extends Properties {

    private static ConfigLoader UNIQUE_CONFIGURATOR = null;

    /**
     * The ConfigLoader is a Singleton, thus the private constructor.
     */
    private ConfigLoader() {
        String propsFileName = "config.properties";
        try {
            InputStream propsStream = this.getClass().getClassLoader().getResourceAsStream(propsFileName);
            URL loc = this.getClass().getClassLoader().getResource(propsFileName);
            System.out.println(loc);
            super.load(propsStream);
        } catch (IOException ex) {
            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, "Unable to load the property file: " + propsFileName, ex);
        }
    }

    public static ConfigLoader getInstance() {
        if (UNIQUE_CONFIGURATOR == null) {
            return UNIQUE_CONFIGURATOR = new ConfigLoader();
        } else {
            return UNIQUE_CONFIGURATOR;
        }
    }
}

