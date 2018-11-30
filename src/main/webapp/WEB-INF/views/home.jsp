<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate, private" />
        <meta http-equiv="Pragma" content="no-cache" />
        <meta http-equiv="Expires" content="0" />
		<title>Welcome to Digital Vault</title>
	</head>

	<body>
		<img alt="no image" src="<c:url value="/images/header.png"/>" align="middle"/>
	
		<p>Please register to use the application using the link below:</p>
		<table>
			<tr>
				<td><a href="register"><input type="button" value="Register" name="register"></a></td>
			</tr>
		</table>
		<br/>
		<p>If registered already, please log in:</p>
		<table>
			<tr>
				<td><a href="login"><input type="button" value="Login" name="login"></a></td>
			</tr>
		</table>
</body>