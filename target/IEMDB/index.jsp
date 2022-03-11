<%@ page import="ir.ac.ut.ie.UserManager" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home</title>
</head>
<body>
    <h2>Home Page</h2>

    <h3>User:</h3>
    <ul>
    <li id="email">email: <%= UserManager.getInstance().getCurrentUser()%></li>
    <br>
    <li>
        <a href= <%=request.getContextPath() + "/movies.jsp" %>>Movies</a>
    </li>
    <li>
        <a href="/watchlist">Watch List</a>
    </li>
    <li>
        <a href=<%=request.getContextPath() + "/logout" %>>Log Out</a>
    </li>
</ul>
</body>
</html>