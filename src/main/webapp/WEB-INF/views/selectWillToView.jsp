<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Will view from Digital Vault</title>
    </head>
    <body>
	    <t3>Welcome to your Personal Vault ${name}</t3>
        
        <br/>
        <p> 
        	<br/>Please select the Will below to View:
        	<br/><span style="color:red;">Once you select the Will you wish to view, an email is sent to the owner to provide you the Will content.</span> 
        </p>
        <br/>
        <form action = "viewWill" method = "post">
        	<table border="0">
				<th> Will Number </th>
				<tr>
					<td>
					<select name ="willId">
               			<c:forEach var="willId" items="${willList}">
               		   		<option value="${willId}">${willId}</option>
               		   	</c:forEach>
					</select>
					</td>
				</tr>
				<tr>
					<td>
						<input="input type = "submit" value = "View Will" />
					</td>
				</tr>
        	</table>
        </form>
	</body>
</html>