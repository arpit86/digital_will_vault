<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Welcome to Digital Vault</title>
</head>
<body>
	<img alt="no image" src="<c:url value="/images/header.png"/>" align="middle" />
	<br>
	<font> The will content is as follows:</font>
	<br>
	<p>${willData}</p>
    <br>
    <br>				
	<a href="mainPage"><input type="button" value="Home" name="home"></a>
</body>
</html>