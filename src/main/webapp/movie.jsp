<%@ page import="ir.ac.ut.ie.UserManager" %>
<%@ page import="ir.ac.ut.ie.DataBase" %>
<%@ page import="ir.ac.ut.ie.Entities.Movie" %>
<%@ page import="ir.ac.ut.ie.Entities.Actor" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="ir.ac.ut.ie.Entities.Comment" %>
<%@ page import="java.io.IOException" %>
<%@ page import="ir.ac.ut.ie.Entities.User" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">

    <head>
        <meta charset="UTF-8" />
        <title> Movie </title>
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
        int movieId = (Integer) request.getAttribute("movie_id");
        Movie movie = DataBase.getInstance().getMovieById(movieId);
    %>

    <body>
    <h2>Movie Page</h2>
    <a href= <%= request.getContextPath() + "" %>>Home</a>
        <br>
        <h3>User:</h3>
        <p id="email">email: <%= UserManager.getInstance().getCurrentUser()%></p>
        <h3>Movie:</h3>
        <ul>
            <li id="name">name: <%= movie.getName()%></li>
            <li id="summary">summary: <%= movie.getSummary() %></li>
            <li id="releaseDate">releaseDate: <%= movie.getReleaseDate()%></li>
            <li id="director">director: <%= movie.getDirector()%></li>
            <li id="writers">writers: <%= String.join(", ", movie.getWriters())%></li>
            <li id="genres">genres: <%= String.join(", ", movie.getGenres())%></li>
            <li id="imdbRate">imdb Rate: <%= movie.getImdbRate()%></li>
            <li id="rating">rating: <%= movie.getRating()%></li>
            <li id="duration">duration: <%= movie.getDuration()%> minutes</li>
            <li id="ageLimit">ageLimit: <%= movie.getAgeLimit()%></li>
        </ul>
        <h3>Casts:</h3>
        <table>
            <tr>
                <th>name</th>
                <th>age</th>
            </tr>
            <% for (int actorId: movie.getCast()) {
                Actor actor = DataBase.getInstance().getExistingActors().get(actorId); %>
                <tr>
                    <td> <%= actor.getName()%> </td>
                    <td> <%= actor.getAge()%> </td>
                    <td> <a href= <%=request.getContextPath() + "/actors/" + actor.getId()%> >Link</a> </td>
                </tr>
            <% } %>
        </table>
        <br>
        <h3>Rate:</h3>
        <form action="" method="POST">
            <label> Rate (between 1 and 10): </label>
            <input type="hidden" id="form_action" name="action" value="rate">
            <input type="number" id="rate_value" name="rate_value" min="1" max="10">
            <input type="hidden" id="form_movie_id" name="movie_id" value=<%= movie.getId()%>>
            <button type="submit"> rate </button>
        </form>
        <br/>
        <br>
        <h3>Add to watchlist:</h3>
        <form action="" method="POST">
            <input type="hidden" id="form_action" name="action" value="add">
            <input type="hidden" id="form_movie_id" name="movie_id" value=<%= movie.getId()%>>
            <button type="submit">Add to WatchList</button>
        </form>
        <br/>
        <br>
        <h3>Comments:</h3>
        <table>
            <tr>
                <th>nickname</th>
                <th>comment</th>
                <th>like</th>
                <th>dislike</th>
            </tr>
            <% for (Map.Entry<Integer, Comment> comment: movie.getComments().entrySet()) {
                Comment current_comment = comment.getValue();
                String nickname = DataBase.getInstance().getUsers().get(current_comment.getUserEmail()).getNickname(); %>
            <tr>
                <td> <%= nickname%> </td>
                <td> <%= current_comment.getText() %> </td>
                <td>
                    <form action="" method="POST">
                        <label> <%=current_comment.getLike()%> </label>
                        <input type="hidden" id="form_action" name="action" value="like">
                        <input type="hidden" id="form_movie_id" name="movie_id" value=<%=current_comment.getMovieId()%>>
                        <input id="form_like_id" type="hidden" name="like_id" value=<%=current_comment.getCommentId() %>>
                        <button type="submit">like</button>
                    </form>
                </td>
                <td>
                    <form action="" method="POST">
                        <label> <%=current_comment.getDislike()%> </label>
                        <input type="hidden" id="form_action" name="action" value="dislike">
                        <input type="hidden" id="form_movie_id" name="movie_id" value=<%=current_comment.getMovieId()%>>
                        <input id="form_dislike_id" type="hidden" name="dislike_id" value=<%=current_comment.getCommentId() %>>
                        <button type="submit">dislike</button>
                    </form>
                </td>
            </tr>
            <% } %>
        </table>
        <br/>
        <br>
        <h3>New Comment:</h3>
        <form action="" method="POST">
            <label>Your Comment:</label>
            <input type="hidden" id="form_action" name="action" value="comment">
            <input type="text" name="text" value="">
            <input type="hidden" id="form_movie_id" name="movie_id" value=<%= movie.getId()%>>
            <button type="submit">Add Comment</button>
        </form>
        <br/>
    </body>
</html>
