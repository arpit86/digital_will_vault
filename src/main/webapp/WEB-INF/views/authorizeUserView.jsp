<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Authorized User to View the Will</title>
</head>
<body>
	<img alt="no image" src="<c:url value="/images/header.png"/>" align="middle" />
	<br>
	<br>
	<font>Please provide the user's <em>name and email address</em> in order to authorize to view the Will</font>
	<br>
	<form id="authorizeForm" action="authorizeUserProcess" method="post">
		<table>
	    	<tr>
	        	<td>First Name:<span style="color:red;">*</span></td>
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
	            <td>Email:<span style="color:red;">*</span></td>
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