<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Registration</title>
	</head>
    
    <body>
    	<img alt="no image" src="<c:url value="/images/header.png"/>" align="middle"/>
    	<br>
    	<br>	
	    <form:form id="registerForm" modelAttribute="user" action="registerProcess" method="post">
	        <table>
	            <tr>
	                <td>
	                    <form:label path="user_firstName">First Name:
	                    	<span style="color:red;">*</span>
	                    </form:label>
                    </td>
                    <td>
                        <form:input path="user_firstName" name="user_firstName" id="user_firstName" />
                    </td>
				</tr>
				<tr>
	                <td>
	                    <form:label path="user_lastName">Last Name:</form:label>
                    </td>
                    <td>
                        <form:input path="user_lastName" name="user_lastName" id="user_lastName" />
                    </td>
				</tr>
				<tr>
	                <td>
	                    <form:label path="userEmail">Email:
	                    	<span style="color:red;">*</span>
	                    </form:label>
                    </td>
	            	<td>
                        <form:input path="userEmail" name="userEmail" id="userEmail" />
                    </td>
				</tr>
				<tr>
	                <td>
	                    <form:label path="userPhone">Phone:</form:label>
                    </td>
	            	<td>
                        <form:input path="userPhone" name="userPhone" id="userPhone" />
                    </td>
				</tr>
				
                <tr>
	                <td>
	                    <form:label path="userPassword">Password:
	                    	<span style="color:red;">*</span>
	                    </form:label>
                    </td>
                    <td>
	                    <form:password path="userPassword" name="userPassword" id="userPassword" />
                    </td>
				</tr>
                <tr>
	                <td></td>
                    <td>
	                    <form:button id="register" name="register">Register</form:button>
	                </td>
                </tr>
                <tr></tr>
            </table>
       	</form:form>
	</body>
</html>