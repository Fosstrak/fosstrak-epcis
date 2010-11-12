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

/**
 * This javascript is used to reformat the RFID data for mobile clients.
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, <a href="http://www.guinard.org">Dominique Guinard</a>
 *
 *
 */
var counter = 0;


function insertEvent() {
    var myMemberEntries = $(".memberEntry");
    for(var i = 0; i < myMemberEntries.length; i++) {
        var myElement = myMemberEntries[i].getElementsByTagName("td")[0];
        if (myElement != null) {
            var myNameElement = null;
            var myLinkElement = null;
            var temp = myElement.getElementsByTagName("b");
            if (temp.length > 0) {
                myNameElement = temp[0].firstChild.nodeValue;
            }
            temp = myElement.getElementsByTagName("a");
            if (temp.length > 0) {
                myLinkElement = temp[0].getAttribute("href");
            }
            if (myNameElement != null && myLinkElement != null) {
                var myLiElement = document.createElement("li");
                var myElementLink = document.createElement("a");
                myElementLink.setAttribute("href", myLinkElement);
                myElementLink.setAttribute("target", "_self");
                var myElementText = document.createTextNode(myNameElement);
                myElementLink.appendChild(myElementText);
                myLiElement.appendChild(myElementLink);
                $("#iphonecontent").append(myLiElement);
            }
        }
    }
}

function insertEventList() {
    var counter = 0;
    var myMemberEntries = $(".memberEntry");
    for(var i = 0; i < myMemberEntries.length; i++) {
        var tmp = myMemberEntries[i].getElementsByTagName("b")[0].firstChild.nodeValue;
        if (tmp == "RESTful Path ID") {
            var myIdUri = myMemberEntries[i].getElementsByTagName("a")[0].href;
            var myId = "Event " + counter++;
            var myElement = document.createElement("li");
            var myElementLink = document.createElement("a");
            myElementLink.setAttribute("href", myIdUri);
            myElementLink.setAttribute("target", "_self");
            var myElementText = document.createTextNode(myId);
            myElementLink.appendChild(myElementText);
            myElement.appendChild(myElementLink);
            $("#iphonecontent").append(myElement);
        }
    }
}

var isNotSupported = true;

$(document).ready(function() {
    var myListItems = $("li");

    //append normal content to iphone view
    for(var i = 0; i < myListItems.length; i++) {
        $("#iphonecontent").append(myListItems[i]);
    }
    $("a").attr("target", "_self");
    if (myListItems.length > 1) {
        isNotSupported = false;
    }

    //append epcis event to iphone view
    var counter = 0;
    var myMemberEntries = $(".memberEntry");
    for(var x = 0; x < myMemberEntries.length; x++) {
        var tmp = myMemberEntries[x].getElementsByTagName("b")[0].firstChild.nodeValue;
        if (tmp == "RESTful Path ID") {
            counter++;
        }
    }
    if (myMemberEntries.length > 0) {
        if (counter == 1) {
            insertEvent();
        }
        else {
            insertEventList();
        }
        isNotSupported = false;
    }
    if (isNotSupported) {
        var myDiv = document.createElement("div");
        myDiv.setAttribute("id", "notsupported");
        myDiv.setAttribute("class", "panel");
        myDiv.setAttribute("title", "Not Supported");
        myDiv.setAttribute("selected", "true");
        var myDivText = document.createTextNode("This Feature is not supported by the EPCIS REST Adapter mobile Interface.");
        myDiv.appendChild(myDivText);
        $("#iphonecontent").replaceWith(myDiv);
    }

    var myEmptyTextNode = document.createTextNode("");
    $("#restfulepcismobilecontent").replaceWith(myEmptyTextNode);

});


