<%@ page import="ir.ac.ut.ie.Entities.Movie" %>
<%@ page import="ir.ac.ut.ie.DataBase" %>
<%@ page import="ir.ac.ut.ie.UserManager" %>
<%@ page import="java.util.Locale" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!
    public String getMovieInformation(Movie movie, String path) {
        return
                "<tr>" + "<td>" + movie.getName() + "</td>"
                        + "<td>" + movie.getSummary() + "</td>"
                        + "<td>" + movie.getReleaseDate() + "</td>"
                        + "<td>" + movie.getDirector() + "</td>"
                        + "<td>" + String.join(", ", movie.getWriters()) + "</td>"
                        + "<td>" + String.join(", ", movie.getGenres()) + "</td>"
                        + "<td>" + String.join(", ", movie.getCastName()) + "</td>"
                        + "<td>" + movie.getImdbRate() + "</td>"
                        + "<td>" + movie.getRating() + "</td>"
                        + "<td>" + movie.getDuration() + "</td>"
                        + "<td>" + movie.getAgeLimit() + "</td>"
                        + "<td><a href=" + path + "/movies/" + movie.getId() + ">Link</a></td></tr>";
    }
%>

<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title> Movies </title>
        <style>
            li, td, th {
                padding: 5px;
            }
            h2 {
                color: rgb(207, 3, 3);
            }
        </style>
    </head>

    <body>
    <h2>Movies Page</h2>
    <a href=<%=request.getContextPath() + ""%>>Home</a>
    <h3>User</h3>
    <p id="email">email: <%= UserManager.getInstance().getCurrentUser()%></p>
    <br>
    <h3>Search:</h3>
    <form action= <%= request.getContextPath() + "/movies.jsp" %> method="get">
        <label>Search:</label>
        <input type="text" name="search" value="">
        <button type="submit" name="action" value="search">Search</button>
        <button type="submit" name="action" value="clear">Clear Search</button>
    </form>
    <br>
    <h3>Sort:</h3>
    <form <%= request.getContextPath() + "/movies.jsp" %> method="get">
        <label>Sort By:</label>
        <button type="submit" name="action" value="sort_by_imdb">imdb Rate</button>
        <button type="submit" name="action" value="sort_by_date">releaseDate</button>
    </form>
    <br>
    <h3>Movies:</h3>
    <table>
        <tr>
            <th>name</th>
            <th>summary</th>
            <th>releaseDate</th>
            <th>director</th>
            <th>writers</th>
            <th>genres</th>
            <th>cast</th>
            <th>imdb Rate</th>
            <th>rating</th>
            <th>duration</th>
            <th>ageLimit</th>
            <th>Links</th>
        </tr>

        <% if (request.getParameter("action") == null || request.getParameter("action").equals("clear")
                || request.getParameter("action").equals("sort_by_imdb")) {
            for (Movie movie : DataBase.getInstance().getMovies().values()) { %>
                <%= getMovieInformation(movie, request.getContextPath()) %>
        <% }}

         else if (request.getParameter("action").equals("search")) {
            String searchedMovie = request.getParameter("search").toUpperCase();
            for (Movie movie: DataBase.getInstance().getMovies().values()) {
                if (movie.getName().toUpperCase().contains(searchedMovie)) { %>
                    <%= getMovieInformation(movie, request.getContextPath()) %>
        <% }}}

         else if (request.getParameter("action").equals("sort_by_date")) {
             for (Movie movie: DataBase.getInstance().getMoviesSortedByDate().values()) { %>
                <%= getMovieInformation(movie, request.getContextPath()) %>
        <% }} %>

    </table>
    </body>
</html>