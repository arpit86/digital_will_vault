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
       	<br/>An email was sent to by the Will owner containing the System token.
       	<br/>Please upload the token file in order to view the requested will.
    </p>
    <br/>
    <br/>
    <form action="processVerifyToken" method="post" enctype="multipart/form-data">
        	<table border="0">
                <tr>
                	<td>Please upload your System token using the Browse button:</td>
                	<td><input type = "file" name = "file" accept=".pdf, .txt, .doc"/></td>
               	</tr>
         		<tr>
                    <td colspan="2" align="center">
         				<input type = "submit" value = "Verify Token" />
         			</td>
         			<td>
         				<a href="mainPage"><input type="button" value="Cancel" name="cancel"></a>
         			</td>
         		</tr>
         	</table>
     	</form>

</body>
</html>