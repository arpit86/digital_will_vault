<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Login Page</title>
	</head>
    
    <body>
    	<img alt="no image" src="<c:url value="/images/header.png"/>" align="middle" />
		<br>
		<br>
	    <form:form id="loginForm" modelAttribute="user" action="loginProcess" method="post">
	        <table>
	            <tr>
	                <td>
	                    <form:label path="userEmail">Email:</form:label>
                    </td>
	            	<td>
                        <form:input path="userEmail" name="userEmail" id="userEmail" />
                    </td>
				</tr>
                <tr>
	                <td>
	                    <form:label path="userPassword">Password:</form:label>
                    </td>
                    <td>
	                    <form:password path="userPassword" name="userPassword" id="userPassword" />
                    </td>
				</tr>
                <tr>
	                <td></td>
                    <td>
	                    <form:button id="login" name="login">Login</form:button>
	                </td>
                </tr>
                <tr></tr>
            </table>
       	</form:form>
	</body>
</html>