<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<title>Fosstrak EPCIS Capture Service</title>
</head>
<body>
	<p>
		This service captures EPCIS events sent to it using HTTP POST requests.<br />
        The payload of the HTTP POST request is expected to be an XML document conforming to the EPCISDocument schema.
	</p>
	<p>
		For further information refer to the xml schema files, check the Example <br />
        in 'EPC Information Services (EPCIS) Version 1.0 Specification', Section 9.6,<br />
        or see the <a href="http://www.fosstrak.org/epcis/docs/user-guide.html#How%20to%20Capture%20EPCIS%20Events">Fosstrak EPCIS user guide</a>.
    </p>
</body>
</html>