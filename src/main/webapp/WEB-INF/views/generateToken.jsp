<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
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
	<br>
	<font>Please enter the requestor's email and the Will number you want him to give access.</font>
	<br>
	<em>The generated token will be sent through email to the will owner to share with the requester.
	<br> This token will be required in order to view the will. </em>
	<br>
	<form action="generateTokenrequest" method="post">
		<table>
	    	<tr>
	        	<td>Requestor's Email:
	              	<span style="color:red;">*</span>
	            </td>
                <td>
                	<input type="text" name="requestorEmail"/>
                </td>
			</tr>
			<tr>
	            <td>Will Number:</td>
                <td>
                    <input type="text" name="willNo"/>
                </td>
			</tr>
			<tr>
	            <td>
	        	    <input type="submit" value="Generate Token"/>
	            </td>
	            <td>
	            	<a href="mainPage"><input type="button" value="Cancel" name="cancel"></a>
	            </td>
            </tr>
	   	</table>
 	</form>
</body>
</html>