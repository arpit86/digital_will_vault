<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Will upload to Digital Vault</title>
    </head>
    <body>
	    Welcome to your Personal Vault
        
        <br/>
        <p> Please upload your digital will using the Browse button.
        	<br/>An email was sent to the ${name} which contains your public token and your private token.
        	<br/>Please feel free to share your public token with anyone who wants to digitally verify your will.
        	<br/>Keep the private token safe so that no can use it to create a duplicate will.
        </p>
        <form action = "uploadFile" method = "post" enctype = "multipart/form-data">
        	<table border="0">
                <!-- <tr>
                    <td>Please provide the private key token:</td>
                    <td><input type = "text" name = "privKey" size = "50"/></td>
                </tr> -->
                <tr>
                	<td>Please select the file:</td>
                	<td><input type = "file" name = "file" accept=".pdf, .txt, .doc"/></td>
               	</tr>
         		<tr>
                    <td colspan="2" align="center">
         				<input type = "submit" value = "Upload File" />
         			</td>
         		</tr>
         	</table>
     	</form>
	</body>
</html>