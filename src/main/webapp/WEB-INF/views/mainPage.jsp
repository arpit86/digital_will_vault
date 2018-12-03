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
	<img alt="no image" src="<c:url value="/images/header.png"/>" align="middle" />
	<br>
	<br>
	<font color="green">Welcome ${name},</font>
	<br>
	<br>
	<font>Please select the operations you wish to perform from the below options:</font>
	<br>
	<table border="0">
		<tr>
			<td><font color="blue">Will Owner Operations</font></td>
			<td/>
		</tr>
		<tr>
			<td><font>Upload a new Will</font></td>
			<td><a href="upload"><input type="button" value="Upload a New Will" name="upload"></a></td>
		</tr>
		<tr>
			<td><font>Modify the last uploaded Will</font></td>
			<td><a href="modifyWill"><input type="button" value="Modify Existing Will" name="modifyWill"></a></td>
		</tr>
		<tr>
			<td><font>Generate a System token to grant view Will permission to the Requester</font></td>
			<td><a href="generateToken"><input type="button" value="Generate System Token" name="generateToken"></a></td>
		</tr>
	</table>
	<br>
	<hr color="green">
	<table border="0">
		<tr>
			<td><font color="blue">Authorized Users Operations</font></td>
			<td />
		</tr>
		<tr>
			<td><font>Select the Will to be Viewed</font></td>
			<td><a href="viewWill"><input type="button"
					value="Request for View" name="viewWill"></a></td>
		</tr>
		<tr>
			<td><font>Upload System token provided by Owner to View Will</font></td>
			<td><a href="verifyToken"><input type="button"
					value="Upload System Token" name="verifyToken"></a></td>
		</tr>
	</table>
</body>