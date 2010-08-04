<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<title>Fosstrak EPCIS Capture and Query Services</title>
	<link rel="stylesheet" type="text/css" href="static/style.css" />
</head>
<body>
	<%@ include file="banner.jsp" %>
	<div id="content">
		<h1>Fosstrak EPCIS Capture and Query Services</h1>
		<div>This is the Fosstrak EPCIS Web application providing the following two services</div>
		<br />
		<ul>
			<li><a href="capture">Fosstrak EPCIS Capture interface</a> (HTTP POST)</li>
			<li><a href="query/services">Fosstrak EPCIS Query interface</a> (Web service, <a href="query/?wsdl">WSDL</a>)</li>
		</ul>
	</div>
</body>
</html>