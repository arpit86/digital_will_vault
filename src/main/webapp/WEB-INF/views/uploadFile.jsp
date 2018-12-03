<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Welcome to Digital Vault</title>
</head>
<body>
	<img alt="no image" src="<c:url value="/images/header.png"/>" align="middle" />
	<br>
	<br>
	<font color="green">Welcome ${name},</font>
	<br>
	<em>The file format accepted are PDF, TEXT and DOC. Please upload file in these format only.</em>
	<br>
	<form action="uploadFile" method="post" enctype="multipart/form-data">
		<table border="0">
			<tr>
				<td>Please upload your digital will using the Browse button:</td>
				<td><input type="file" name="file" accept=".pdf, .txt, .doc" /></td>
			</tr>
			<tr>
				<td colspan="2" align="center"><input type="submit" value="Upload File" /></td>
				<td><a href="mainPage"><input type="button" value="Cancel" name="cancel"></a></td>
			</tr>
		</table>
	</form>
</body>
</html>