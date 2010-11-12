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

/*
 *
 * @author Mathias Mueller mathias.mueller(at)unifr.ch, Dominique Guinard (www.guinard.org)
 */

$(document).ready(function() {

    //set default epcis url
    $("input.restdefault").click(function() {
        var mydefault = $("input.restdefault").attr("title");
        $("input.restinput").attr("value", mydefault);
    });

    //set fosstrak epcis url
    $("input.restfosstrak").click(function() {
        var myfosstrak = $("input.restfosstrak").attr("title");
        $("input.restinput").attr("value", myfosstrak);
    });

    //add onclick listener to do a http put (change a config url)
    $("input.restput").click(function() {
        var myData = $("input.restinput").attr("value");
        var myURL = $("input.restput").attr("title");
        $.ajax({
            url: myURL,
            type: 'PUT',
            data: myData,
            dataType: 'html',
            cache: false,
            error: function(){
                alert('Error in HTTP POST');
            },
            success: function(data){
                location.reload();
            }
        });
    });

    //add onclick listener to do a http put (add a subscription)
    $("button.restput").click(function() {
        var myData = $("input.restinput").attr("value");
        var myURL = $("button.restput").attr("title");
        $.ajax({
            url: myURL,
            type: 'PUT',
            data: myData,
            dataType: 'xml',
            cache: false,
            error: function(){
                alert('Error in HTTP POST');
            },
            success: function(data){
                var subscriptionURL = data.getElementsByTagName("fields")[0].getElementsByTagName("valueRef")[0].firstChild.nodeValue;
                $("#putFormDiv").html("<a href='" + subscriptionURL + "' > SUBSCRIPTION </a>");
            }
        });
    });

    //add onclick listener to do a http delete (delete a subscription)
    $("button.restdelete").click(function() {
        var myURL = $("button.restdelete").attr("title");
        $.ajax({
            url: myURL,
            type: 'DELETE',
            //dataType: 'html',
            cache: false,
            //timeout: 1000,
            error: function(){
                alert('Error in HTTP DELETE');
            },
            success: function(data){
                location.reload();
            }
        });
    });
    //add javascript href values to the different representation links
    var myUri = $("div.restContentLinks").attr("title");
    $("a.restxml").attr("href", "javascript:loadContent('" + myUri + "', 'GET', 'application/xml');");
    $("a.restjson").attr("href", "javascript:loadContent('" + myUri + "', 'GET', 'application/json');");
    $("a.restoptions").attr("href", "javascript:loadContent('" + myUri + "', 'OPTIONS', 'application/xml');");
    $("a.resthead").attr("href", "javascript:loadContent('" + myUri + "', 'HEAD', 'text/html');");

});

function loadContent() {
    loadContent('', 'GET', 'text/html');
}


function loadContent(uri, method, type) {
    var myajax = $.ajax({
        beforeSend: function(xhrObj){
            xhrObj.setRequestHeader("Accept", type);
        },
        url: uri,
        type: method,
        error: function(){
            alert('Error loading document');
        },
        success: function(data){
            if (typeof data === "string") {
                
                if (method == "HEAD") {
                    var myHeader = myajax.getAllResponseHeaders();
                    myHeader = "<pre class='code'> <code class='xml'>" + myHeader + "</code> </pre>";
                    $("html").append(data);
                    $("#restfulContent").empty();
                    $("#restfulContent").append(myHeader);
                    $.beautyOfCode.init({
                        brushes: ['Xml']
                    });
                }
            }
            else if (data instanceof Document) {
                //alert("Document");
                var myXMLText = (new XMLSerializer()).serializeToString(data);
                var myXMLString = new String(myXMLText);
                myXMLString = myXMLString.replace(/>/g, "&gt;");
                myXMLString = myXMLString.replace(/</g, "&lt;");
                myXMLText = "<pre class='code'> <code class='xml'>" + myXMLString + "</code> </pre>";
                $("#restfulContent").empty();
                $("#restfulContent").append(myXMLText);
                $.beautyOfCode.init({
                    brushes: ['Xml']
                });
            }
            else {
                var myJSONText = JSON.stringify(data, null, '\t');
                myJSONText = "<pre class='code'> <code class='xml'>" + myJSONText + "</code> </pre>";
                $("#restfulContent").empty();
                $("#restfulContent").append(myJSONText);
                $.beautyOfCode.init({
                    brushes: ['Xml']
                });
            }
        }
    });
}
