<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Modify entity</title>
</head>
<body>
	<h2>${headerContent }</h2>
		<form action="modify.html">
			<c:forEach items="${entity}" var="entry">
				<tr><td>${entry.key}</td><td><input type="text" value="${entry.value}"  readonly="true"/></td><br/></tr>
			</c:forEach>
			<input type="submit" value="edytuj" />
		</form>
	
</body>
</html>