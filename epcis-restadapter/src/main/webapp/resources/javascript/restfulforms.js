/**
 *
 * @author Mathias Mueller
 * mathias.mueller(at)unifr.ch
 * Software Engineering Group
 * Departement of Informatics
 * University of Fribourg CH
 */

var epc;
var action;
var parentid;
var epcclass;
var quantity;

var notAvailable = "Not for this Event Type.";

function getReplaceText(myId) {
    var res = "<div id='" + myId + "'>" + notAvailable + "</div>";
    return res;
}

function selectObjectEvent() {
    $("#epc").replaceWith(epc);
    $("#action").replaceWith(action);
    $("#parentid").replaceWith(getReplaceText("parentid"));
    $("#epcclass").replaceWith(getReplaceText("epcclass"));
    $("#quantity").replaceWith(getReplaceText("quantity"));
}

function selectAggregationEvent() {
    $("#epc").replaceWith(epc);
    $("#action").replaceWith(action);
    $("#parentid").replaceWith(parentid);
    $("#epcclass").replaceWith(getReplaceText("epcclass"));
    $("#quantity").replaceWith(getReplaceText("quantity"));
}

function selectQuantityEvent() {
    $("#epc").replaceWith(getReplaceText("epc"));
    $("#action").replaceWith(getReplaceText("action"));
    $("#parentid").replaceWith(getReplaceText("parentid"));
    $("#epcclass").replaceWith(epcclass);
    $("#quantity").replaceWith(quantity);
}

function selectTransactionEvent() {
    $("#epc").replaceWith(epc);
    $("#action").replaceWith(action);
    $("#parentid").replaceWith(parentid);
    $("#epcclass").replaceWith(getReplaceText("epcclass"));
    $("#quantity").replaceWith(getReplaceText("quantity"));
}

function appendqueryparam(id, queryparam) {
    //alert(id);
    var myValue = $("#" + id)[0].value;
    if (myValue != "") {
        queryparam = queryparam + "&" + id + "=" + myValue;
    }
    //alert(myValue);
    return queryparam;
}

function appendqueryparamSelect(id, queryparam) {
    //alert(id);
    var myValue = $("#" + id)[0].value;
    if (myValue != "") {
        queryparam = queryparam + "&" + id + "=" + myValue;
    }
    //alert(myValue);
    return queryparam;
}

function appendqueryparamCheckbox(myClass, queryparam) {
    //alert(myClass);
    var res = "";
    var myValue;
    var myValues = $("." + myClass + ":checked");

    var i = 0;
    for (i = 0; i < myValues.length; i++) {
        res = res + "," + myValues[i].value;
    }

    if (res.length > 0) {
        res = res.substring(1);
    }

    myValue = res;

    if (myValue != "") {
        queryparam = queryparam + "&" + myClass + "=" + myValue;
    }
    //alert(myValue);
    return queryparam;
}

function saveVariableEventParams() {
    var myInputType;
    myInputType = $("#epc")[0];
    if (myInputType.type != null) {
        epc = "<input type='" + myInputType.type + "' name='" + myInputType.name + "' id='" + myInputType.id + "' size='" + myInputType.size + "' value='" + myInputType.value + "' />";
    }
    myInputType = $("#action")[0];
    if (myInputType.type != null) {
        //action = "<input type='" + myInputType.type + "' name='" + myInputType.name + "' id='" + myInputType.id + "' size='" + myInputType.size + "' value='" + myInputType.value + "' />";
        if ($("#recordtime").length == 0) {
            action = "<select id='" + myInputType.id + "' name='" + myInputType.name + "'><option value='ADD'>ADD</option><option selected='selected' value='OBSERVE'>OBSERVE</option><option value='DELETE'>DELETE</option></select>";
        }
    }
    myInputType = $("#parentid")[0];
    if (myInputType.type != null) {
        parentid = "<input type='" + myInputType.type + "' name='" + myInputType.name + "' id='" + myInputType.id + "' size='" + myInputType.size + "' value='" + myInputType.value + "' />";
    }
    myInputType = $("#epcclass")[0];
    if (myInputType.type != null) {
        epcclass = "<input type='" + myInputType.type + "' name='" + myInputType.name + "' id='" + myInputType.id + "' size='" + myInputType.size + "' value='" + myInputType.value + "' />";
    }
    myInputType = $("#quantity")[0];
    if (myInputType.type != null) {
        quantity = "<input type='" + myInputType.type + "' name='" + myInputType.name + "' id='" + myInputType.id + "' size='" + myInputType.size + "' value='" + myInputType.value + "' />";
    }

}

function initCaptureForm() {
    $("#type").replaceWith("<select id='type' name='type'><option value='ObjectEvent' selected='selected'>Object Event</option><option value='AggregationEvent'>Aggregation Event</option><option value='QuantityEvent'>Quantity Event</option><option value='TransactionEvent'>Transaction Event</option></select>");
    saveVariableEventParams();
    selectObjectEvent();

    $("#type").change(function() {
        saveVariableEventParams();
        var myValue = $("#type")[0].value;
        if (myValue == "ObjectEvent") {
            selectObjectEvent();
        }
        if (myValue == "AggregationEvent") {
            selectAggregationEvent();
        }
        if (myValue == "QuantityEvent") {
            selectQuantityEvent();
        }
        if (myValue == "TransactionEvent") {
            selectTransactionEvent();
        }
        if (myValue == "TransactionEvent") {
            selectTransactionEvent();
        }
    });
    $("#time").dynDateTime({
        showsTime: true,
        ifFormat: "%Y-%m-%dT%H:%M:%S"
    });
}

function initCheckBox(classId, myInputType) {
    var myInputValue;
    myInputValue = "";
    myInputValue = myInputType.value.split(",");
    if (myInputValue != "") {
        var myOptions = $("." + classId);
        for(var i0=0; i0<myOptions.length; i0++) {
            for(var t0=0; t0<myInputValue.length; t0++) {
                if (myOptions[i0].value == myInputValue[t0]) {
                    myOptions[i0].checked = true;
                }
            }
        }
    }
}

function initQueryForm() {
    
    var myInputType;
    var myInputValue;
    
    if ($("#type").length != 0) {
        myInputType = $("#type")[0];
        //$("#type").replaceWith("<select id='type' name='type' size='4' multiple><option value='ObjectEvent'>Object Event</option><option value='AggregationEvent'>Aggregation Event</option><option value='QuantityEvent'>Quantity Event</option><option value='TransactionEvent'>Transaction Event</option></select>");
        var myEventSelect = "<input name='type' type='checkbox' value='ObjectEvent' class='type'>Object Event<br>";
        myEventSelect += "<input name='type' type='checkbox' value='AggregationEvent' class='type'>Aggregation Event<br>";
        myEventSelect += "<input name='type' type='checkbox' value='QuantityEvent' class='type'>Quantity Event<br>";
        myEventSelect += "<input name='type' type='checkbox' value='TransactionEvent' class='type'>Transaction Event<br>";
        $("#type").replaceWith(myEventSelect);
        initCheckBox("type", myInputType);
    }

    if ($("#order").length != 0) {
        myInputType = $("#order")[0];
        myInputValue = "";
        myInputValue = myInputType.value;
        $("#order").replaceWith("<select id='order' name='order'><option value='' selected='selected'>Default Order</option><option value='eventTime'>Order by Event Time</option><option value='recordTime'>Order by Record Time</option><option value='quantity'>Order by Quantity</option></select>");
        if (myInputValue != "") {
            var myOrderOptions = $("#order > option");
            for(var i1=0; i1<myOrderOptions.length; i1++) {
                if (myOrderOptions[i1].value == myInputValue) {
                    myOrderOptions[i1].selected = true;
                }
            }
        }
    }

    if ($("#ordering").length != 0) {
        myInputType = $("#ordering")[0];
        myInputValue = "";
        myInputValue = myInputType.value;
        $("#ordering").replaceWith("<select id='ordering' name='ordering'><option value=''>Default Ordering</option><option value='ASC'>Ascending Order</option><option value='DESC'>Descending Order</option></select>");
        if (myInputValue != "") {
            var myOrderingOptions = $("#ordering > option");
            for(var i2=0; i2<myOrderingOptions.length; i2++) {
                if (myOrderingOptions[i2].value == myInputValue) {
                    myOrderingOptions[i2].selected = true;
                }
            }
        }
    }

    if ($("#action").length != 0) {
        myInputType = $("#action")[0];
        var myActionSelect = "<input name='action' type='checkbox' value='ADD' class='action'>ADD<br>";
        myActionSelect += "<input name='action' type='checkbox' value='OBSERVE' class='action'>OBSERVE<br>";
        myActionSelect += "<input name='action' type='checkbox' value='DELETE' class='action'>DELETE<br>";
        $("#action").replaceWith(myActionSelect);
        initCheckBox("action", myInputType);
    }
    
    if ($("#location").length != 0) {
        var myInputTypeLocation = $("#location")[0];

        var baseRestURL = document.URL.split("/eventquery")[0];
        var locationURL = baseRestURL + "/location";

        var myLocationSelect = "";
        
        $.ajax({
            url: locationURL,
            type: 'GET',
            dataType: 'xml',
            cache: false,
            //contentType: 'text/plain',
            //timeout: 1000,
            error: function(){
                alert('Error in HTTP GET: Cannot load Business Locations.');
            },
            success: function(data){

                var myContents = data.getElementsByTagName("fields")[0].getElementsByTagName("value");
                var i = 0;
                for (i = 0; i < myContents.length; i++) {
                    var myLocationValue = myContents[i].firstChild.nodeValue;
                    if (myLocationValue != "blank") {
                        myLocationSelect = myLocationSelect + "<input name='location' type='checkbox' value='" + myLocationValue + "' class='location'>" + myLocationValue + "<br>";
                    }
                }
                $("#location").replaceWith(myLocationSelect);
                initCheckBox("location", myInputTypeLocation);
            }
        });
    }

    if ($("#reader").length != 0) {
        var myInputTypeReader = $("#reader")[0];

        var baseRestURL2 = document.URL.split("/eventquery")[0];
        var readerURL = baseRestURL2 + "/reader";

        var myReaderSelect = "";

        $.ajax({
            url: readerURL,
            type: 'GET',
            dataType: 'xml',
            cache: false,
            error: function(){
                alert('Error in HTTP GET: Cannot load Business Locations.');
            },
            success: function(data){

                var myContents = data.getElementsByTagName("fields")[0].getElementsByTagName("value");
                var i = 0;
                for (i = 0; i < myContents.length; i++) {
                    var myReaderValue = myContents[i].firstChild.nodeValue;
                    if (myReaderValue != "blank") {
                        myReaderSelect = myReaderSelect + "<input name='reader' type='checkbox' value='" + myReaderValue + "' class='reader'>" + myReaderValue + "<br>";
                    }
                }
                $("#reader").replaceWith(myReaderSelect);
                initCheckBox("reader", myInputTypeReader);
            }
        });
    }

    var baseURL = document.URL.split("rest/")[0];
    $("#submit").replaceWith("<input type='submit' id='submit'><br/>");
    $("#submit").click(function() {

        var queryparam = "";

        queryparam = appendqueryparam("time", queryparam);
        queryparam = appendqueryparam("recordtime", queryparam);
        queryparam = appendqueryparamCheckbox("type", queryparam);
        queryparam = appendqueryparamCheckbox("action", queryparam);
        queryparam = appendqueryparam("step", queryparam);
        queryparam = appendqueryparam("disposition", queryparam);
        queryparam = appendqueryparamCheckbox("reader", queryparam);
        queryparam = appendqueryparam("readerdescendant", queryparam);
        queryparam = appendqueryparamCheckbox("location", queryparam);
        queryparam = appendqueryparam("locationdescendant", queryparam);
        queryparam = appendqueryparam("transaction", queryparam);
        queryparam = appendqueryparam("epc", queryparam);
        queryparam = appendqueryparam("parentid", queryparam);
        queryparam = appendqueryparam("anyepc", queryparam);
        queryparam = appendqueryparam("epcclass", queryparam);
        queryparam = appendqueryparam("quantity", queryparam);
        queryparam = appendqueryparamSelect("order", queryparam);
        queryparam = appendqueryparamSelect("ordering", queryparam);
        queryparam = appendqueryparam("eventlimit", queryparam);
        queryparam = appendqueryparam("maxeventlimit", queryparam);
        if (queryparam.length > 0) {
            queryparam = queryparam.substring(1);
            queryparam = "?" + queryparam;
        }
        var myQueryUrl = $("form").attr("action");
        myQueryUrl = myQueryUrl + queryparam;
        window.location.replace(myQueryUrl);
    });

    $("#time").dynDateTime({
        showsTime: true,
        ifFormat: "%Y-%m-%dT%H:%M:00"
    });

    $("#recordtime").dynDateTime({
        showsTime: true,
        ifFormat: "%Y-%m-%dT%H:%M:00"
    });
}

$(document).ready(function() {

    if ($("#type").length > 0) {
        if ($("#recordtime").length == 0) {
            initCaptureForm();
        }
        else {
            initQueryForm();
        }
    }
});
