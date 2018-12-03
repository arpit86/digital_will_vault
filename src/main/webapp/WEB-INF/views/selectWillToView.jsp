<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Welcome to Digital Vault</title>
    </head>
    <body>
    	<img alt="no image" src="<c:url value="/images/header.png"/>" align="middle" />
		<br>
	    <p> 
        	Please select the Will below to View:
        	<br><span style="color:red;"><em>Once you select the Will you wish to view, an email is sent to the owner to provide you the access token via email.</em></span> 
        </p>
        <br>
        <form action = "viewWill" method = "post">
        	<table border="0">
				<tr>
					<td>Will Owner Name:</td>
					<td>
					<select name ="willOwnerName">
               			<c:forEach var="willOwnerName" items="${willList}">
               		   		<option value="${willOwnerName}">${willOwnerName}</option>
               		   	</c:forEach>
					</select>
					</td>
				</tr>
				<tr>
					<td>
						<input type = "submit" value = "View Will" />
					</td>
					<td><a href="mainPage"><input type="button" value="Cancel" name="cancel"></a>
				</tr>
        	</table>
        </form>
	</body>
</html>