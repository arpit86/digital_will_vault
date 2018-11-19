<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Welcome to Digital Vault</title>
</head>
<body>
	<p>
       	An email will be sent to containing the Public Key for the Email entered.
    </p>
    <br/>
	<form action = "requestPubKey" method = "post">
       	<table border="0">
    	    <tr>
            	<td>Please enter the Email address for the Person whose Public key is requested:</td>
               	<td><input type="text" name="email"/></td>
           	</tr>
        	<tr>
                <td colspan="2" align="center">
        			<input type = "submit" value = "Get Public Key" />
         		</td>
         	</tr>
         </table>
     </form>
</body>
</html>