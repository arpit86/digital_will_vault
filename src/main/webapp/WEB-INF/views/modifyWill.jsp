<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<t3> The will content is as follows:</t3>
	<p>${willContent}</p>
    <br/>
    <br/>				
	<form action="modifyWill" method="post" enctype="multipart/form-data">
        <table border="0">
        	<tr>
            	<td>Please upload your new/ modified digital will using the Browse button:</td>
                <td><input type = "file" name = "updateWill" accept=".pdf, .txt, .doc"/></td>
           	</tr>
         	<tr>
            	<td colspan="2" align="center"><input type = "submit" value = "Modify File" /></td>
         		<td colspan="2" align="center"><a href="mainPage"><input type="button" value="Cancel" name="cancel"></a></td>
         	</tr>
         </table>
     </form>
</body>
</html>