<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<title>Fosstrak EPCIS Capture Service</title>
	<link rel="stylesheet" type="text/css" href="static/style.css" />
</head>
<body>
	<%@ include file="banner.jsp" %>
	<div id="content">
		<h1>Fosstrak EPCIS Capture Service</h1>
		<div>
			This service captures EPCIS events sent to it using HTTP POST requests.<br />
	        The payload of the HTTP POST request is expected to be an XML document conforming to the EPCISDocument schema.
		</div>
	    <br />
		<div id="captureFormDiv">
		    <form name="captureForm?showCaptureForm=true" action="capture" method="post" enctype="multipart/form-data">
		    	<input name="showCaptureForm" type="hidden" value="true" />
		    	<textarea rows="12" cols="100" name="event"><% if (request.getAttribute("xml") == null) { %><%@ include file="../../static/sampleCaptureRequest.xml" %><% } else { %><%= request.getAttribute("xml") %><% } %></textarea>
		    	<br />
		    	<input type="submit" value="submit" />
		    </form>
	    </div>
	    <div><%= request.getAttribute("responseMsg") %></div>
	    <br />
	    <div><%= request.getAttribute("detailedMsg") %></div>
	</div>
</body>
</html>