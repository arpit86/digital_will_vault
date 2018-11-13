<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Authorized User to View the Will</title>
</head>
<body>
	<t2>Please provide the user's name and email in order to authorize them to view the Will</t2>
	<br/>
	<br/>
	<form id="authorizeForm" action="authorizeUserProcess" method="post">
		<table>
	    	<tr>
	        	<td>First Name:
	              	<span style="color:red;">*</span>
	            </td>
                <td>
                	<input type="text" name="user_firstName"/>
                </td>
			</tr>
			<tr>
	            <td>Last Name:</td>
                <td>
                    <input type="text" name="user_lastName"/>
                </td>
			</tr>
			<tr>
	            <td>Email:
	            	<span style="color:red;">*</span>
	            </td>
	           	<td>
                    <input type="text" name="userEmail"/>
                </td>
			</tr>
			<tr>
	            <td>
	        	    <input type="submit" value="Authorize"/>
	            </td>
	            <td>
	            	<a href="mainPage"><input type="button" value="Cancel" name="cancel"></a>
	            </td>
            </tr>
	   	</table>
	</form>
</body>
</html>