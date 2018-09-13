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
        	<br/>An email was sent to the ${email} which contains your public token and your private token.
        	<br/>Please feel free to share your public token with anyone who wants to digitally verify your will.
        	<br/>Keep the private token safe so that no can use it to create a duplicate will.
        </p>
        <form action = "uploadFile" modelAttribute="will" method = "post" enctype = "multipart/form-data">
        	<p> Please provide the private key token:</p>
         	<input type = "text" name = "privKey" size = "50" />
         	<br />
        	<input type = "file" name = "file" size = "50" />
         	<br />
         	<input type = "submit" value = "Upload File" />
         	<br/>
     	</form>
		<br/>
		<br/>
        <a href="home.jsp">Home</a>
    </body>
</html>