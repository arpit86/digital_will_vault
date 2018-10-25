<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Authorized User to View the Will</title>
	
	<script language="javascript">
		function addNewAuthorizedUser(tableDataId) {
			var tableData = document.getElementById(tableDataId);
			var rowNumber = tableData.rows.length;
			var row = tableData.insertRow(rowNumber);

			var cell1 = row.insertCell(0);
			cell1.innerHTML = rowNumber + 1;

			var cell2 = row.insertCell(1);
			var element2 = document.createElement("input");
			element2.type = "text";
			element2.name = "user_firstName[]";
			cell2.appendChild(element2);
			
			var cell3 = row.insertCell(2);
			var element3 = document.createElement("input");
			element3.type = "text";
			element3.name = "user_lastName[]";
			cell3.appendChild(element3);
			
			var cell4 = row.insertCell(3);
			var element4 = document.createElement("input");
			element4.type = "text";
			element4.name = "userEmail[]";
			cell4.appendChild(element4);
			
			
			var cell5 = row.insertCell(4);
			var element5 = document.createElement("input");
			element5.type = "checkbox";
			element5.name="checkboxes[]";
			cell5.appendChild(element5);
		}

		function deleteAuthorizedUser(tableDataId) {
			try {
				var tableData = document.getElementById(tableDataId);
				var rowNumber = tableData.rows.length;

				for(var r=0; r<rowNumber; r++) {
					var row = tableData.rows[r];
					var chkbox = row.cells[0].childNodes[0];
					if(null != checkboxes && true == checkboxes.checked) {
						tableData.deleteAuthorizedUser(r);
						rowNumber--;
						r--;
					}
				}
			} catch(ex) {
				alert(ex);
			}
		}
	</script>
</head>
<body>
	<form id="authorizeForm" action = "authorizeUserView" method = "post" modelAttribute="authorizedUserList">
		<t2>Please provide the user's name and email in order to authorize them to view the Will</t2>
		<br/>
		<input type="button" value="Add User" onclick="addNewAuthorizedUser('authorizeUserDataTable')"/>
		<input type="button" value="Remove User" onclick="deleteAuthorizedUser('authorizeUserDataTable')"/>
		
		<table id="authorizeUserDataTable">
			<tr>
				<th/>
				<th>First Name</th>
				<th>Last Name</th>
				<th>Email</th>
				<th>Remove User</th>
			</tr>
    		<c:forEach items="${authorizedUserList}" varStatus="i">
			<tr>
        		<td>${i.index}</td>
				<td>
					<input type="text" path="authorizedUserList[${i.index}].user_firstName"/>
        		</td>
				<td>
					<input type="text" path="authorizedUserList[${i.index}].user_lastName"/>
				</td>
				<td>
					<input type="text" path="authorizedUserList[${i.index}].userEmail"/>
				</td>
				<td>
					<input type="checkbox" name="checkboxes" />
				</td>
			</tr>
			</c:forEach>
		</table>
		<input type="submit" value="Authorize User"/>
</form>
</body>
</html>