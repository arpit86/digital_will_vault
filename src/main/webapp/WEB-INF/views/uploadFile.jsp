<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Welcome to Digital Vault</title>
    </head>
    <body>
	    <t3>Welcome to your Personal Vault ${name}</t3>
        
        <br/>
        <p> 
        	<br/>An email was sent to the registered Email id which contains your public token and your private token.
        	<br/>Please feel free to share your public token with anyone who wants to digitally verify your will.
        	<br/>Keep the private token safe so that no can use it to create a duplicate will.
        </p>
        <br/>
        <p>
        	The file format accepted are PDF, TEXT and DOC.  Please upload file in these format only.
        </p>
        <br/>
        <form action = "uploadFile" method = "post" enctype = "multipart/form-data">
        	<table border="0">
                <tr>
                	<td>Please upload your digital will using the Browse button:</td>
                	<td><input type = "file" name = "file" accept=".pdf, .txt, .doc"/></td>
               	</tr>
         		<tr>
                    <td colspan="2" align="center">
         				<input type = "submit" value = "Upload File" />
         			</td>
         			<td>
         				<a href="mainPage"><input type="button" value="Cancel" name="cancel"></a>
         			</td>
         		</tr>
         	</table>
     	</form>
	</body>
</html>