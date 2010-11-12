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
package org.epcis.fosstrak.restadapter.representation;

import org.epcis.fosstrak.restadapter.config.Config;
import org.epcis.fosstrak.restadapter.model.Resource;
import org.epcis.fosstrak.restadapter.util.URI;

/**
 * Class to build the HTML iPhone Representation of a Resource
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 *
 */
public class HTMLPageIphoneBuilder extends HTMLBuilder {

    /**
     * Validation of the ease of building another Interface upon the EPCIS REST Adapter with just adding some javascript and css and minor changing the html (better change with DOM)
     *
     *
     * @param resource
     *
     * @return
     */
    @Override
    public String buildRepresentation(Resource resource) {
        StringBuilder res           = new StringBuilder();

        String        myName        = "EPCIS REST-adapter Mobile";
        String        myHomeURI     = "#";
        String        myParentURI   = "#";
        String        myDescription = "EPCIS REST-adapter Mobile";
        String        unifrLogoURL  = URI.addSubPath(Config.GET_RESOURCES_URL(), "/images/unifr.gif");
        String        ethzLogoURL   = URI.addSubPath(Config.GET_RESOURCES_URL(), "/images/ethz.gif");

        try {
            myName        = resource.getName();
            myDescription = resource.getDescription();
            myHomeURI     = Config.GET_RESTFUL_EPCIS_URL();
            myParentURI   = resource.getUri();

            if (myParentURI.endsWith("/")) {
                myParentURI = myParentURI.substring(0, myParentURI.length() - 1);
            }

            int mySplit = myParentURI.lastIndexOf("/");

            myParentURI = myParentURI.substring(0, mySplit);
        } catch (Exception ex) {
            ex.printStackTrace();

            // continue
        }

        res.append("<html xmlns='http://www.w3.org/1999/xhtml'>");

        res.append("<head>");
        res.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />");
        res.append("<title>Fosstrak EPCIS REST-adapter Mobile UI</title>");
        res.append("<meta name='viewport' content='width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;' />");
        res.append("<style type='text/css' media='screen'>@import '" + URI.addSubPath(Config.GET_RESOURCES_URL(), "/smartPhones/iui/iui.css") + "';</style>");
        res.append("<script type='application/x-javascript' src='" + URI.addSubPath(Config.GET_RESOURCES_URL(), "/smartPhones/iui/iui.js") + "'> </script>");
        res.append("<script type='text/javascript' src='" + URI.addSubPath(Config.GET_RESOURCES_URL(), "/javascript/jquery.js") + "'> </script>");
        res.append("<script type='text/javascript' src='" + URI.addSubPath(Config.GET_RESOURCES_URL(), "/javascript/restfuliphone.js") + "'> </script>");

        res.append("</head>");

        res.append("<body>");

        res.append("<div id='iphone' class='toolbar'>");
        res.append("<h1 id='pageTitle'> </h1>");
        res.append("<a class='button' href='" + myParentURI + "'>Back</a>");
        res.append("<a class='button' href='" + myHomeURI + "'>Home</a>");
        res.append("</div>");

        res.append("<ul id='iphonecontent' title='" + myName + "' selected='true'> ");
        res.append("<li><i><h4>" + myDescription + "</h4></i></li>");
        res.append("</ul>");
        res.append("<div id='restfulepcismobilecontent'>");
        res.append(super.buildRepresentation(resource));
        res.append("</div>");

        res.append("</body>");

        res.append("</html>");

        String result = res.toString();


        return result;
    }
}
