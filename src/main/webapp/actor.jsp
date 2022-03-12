<%@ page import="ir.ac.ut.ie.UserManager" %>
<%@ page import="ir.ac.ut.ie.Entities.Actor" %>
<%@ page import="ir.ac.ut.ie.Entities.Movie" %>
<%@ page import="ir.ac.ut.ie.DataBase" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title> Actor </title>
        <style>
            li, td, th {
                padding: 5px;
            }
            h2 {
                color: rgb(207, 3, 3);
            }
        </style>
    </head>

    <%
        int actorId = (Integer) request.getAttribute("actor_id");
        Actor actor = DataBase.getInstance().getExistingActors().get(actorId);
        List <Movie> movies_act_in = new ArrayList<>();
        for (Movie movie : DataBase.getInstance().getMovies().values())
            if (movie.getCast().contains(actor.getId()))
                movies_act_in.add(movie);
    %>

    <%!
        public String getMovieInformation(Movie movie, String path) {
            return
                    "<tr>" + "<td>" + movie.getName() + "</td>"
                            + "<td>" + movie.getImdbRate() + "</td>"
                            + "<td>" + movie.getRating() + "</td>"
                            + "<td><a href=" + path + "/movie.jsp?movieId=" + movie.getId() + ">Link</a></td></tr>";
        }
    %>


    <body>
    <h2>Actor Page</h2>
    <a href= <%= request.getContextPath() + "" %>>Home</a>
        <h3>User:</h3>
        <p id="email">email: <%= UserManager.getInstance().getCurrentUser()%></p>
        <h3>Actor:</h3>
        <ul>
            <li id="name">name: <%= actor.getName()%></li>
            <li id="birthDate">birthDate: <%= actor.getBirthDate() %></li>
            <li id="nationality">nationality: <%= actor.getNationality()%></li>
            <li id="tma">Total movies acted in: <%= movies_act_in.size()%></li>
        </ul>
        <br>
        <h3>Movies:</h3>
        <table>
            <tr>
                <th>Movie</th>
                <th>imdb Rate</th>
                <th>rating</th>
                <th>page</th>
            </tr>

                <% if (request.getParameter("action") == null) {
                        for (Movie movie : movies_act_in) { %>
                            <%= getMovieInformation(movie, request.getContextPath()) %>
                <% }} %>

        </table>

    </body>

</html>
