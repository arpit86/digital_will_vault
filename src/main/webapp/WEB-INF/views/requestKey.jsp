<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
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
        	<br/>Please upload the private key file in order to view the will.
        	<br/><span style="color:red;">The private key was sent in the email after registration.</span> 
        </p>
        <br/>
        <form action = "requestKey" method = "post" enctype = "multipart/form-data">
        	<table border="0">
                <tr>
                	<td>Please upload the (.txt) private key file using the Browse button:</td>
                	<td><input type = "file" name = "file" accept=".txt"/></td>
               	</tr>
         		<tr>
                    <td colspan="2" align="center">
         				<input type = "submit" value = "Upload Key" />
         			</td>
         		</tr>
         	</table>
     	</form>
	</body>
</html>