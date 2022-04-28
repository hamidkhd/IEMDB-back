<%@ page import="ir.ac.ut.ie.UserManager" %>
<%@ page import="ir.ac.ut.ie.DataBase" %>
<%@ page import="ir.ac.ut.ie.Entities.User" %>
<%@ page import="ir.ac.ut.ie.Entities.Movie" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Collections" %>
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
        String userId = UserManager.getInstance().getCurrentUser();
        User current_user = DataBase.getInstance().getUsers().get(userId);
    %>

    <%
        List <Integer> recommended_movies = new ArrayList<>();
        List <Integer> movieId_byScore = new ArrayList<>();
        List <Integer> scores = new ArrayList<>();

        for (Movie movie : DataBase.getInstance().getMovies().values()) {
            int genre_similarity_score = 0;
            for (Integer movieId_in_WatchList : current_user.getWatchList()) {
                Movie movie_in_WatchList = DataBase.getInstance().getMovieById(movieId_in_WatchList);
                ArrayList <String> temp_list = new ArrayList<>(movie.getGenres());
                temp_list.retainAll(movie_in_WatchList.getGenres());
                genre_similarity_score += temp_list.size();
            }
            scores.add((int) (3 * genre_similarity_score + movie.getImdbRate() + movie.getRating()));
            movieId_byScore.add(movie.getId());
            movie.setScore((int) (3 * genre_similarity_score + movie.getImdbRate() + movie.getRating()));
        }

        while (movieId_byScore.size() != 0) {
            int max_score_index = scores.indexOf(Collections.max(scores));
            recommended_movies.add(movieId_byScore.get(max_score_index));
            scores.remove(max_score_index);
            movieId_byScore.remove((max_score_index));
        }
    %>

    <body>
    <h2>Watch List Page</h2>
    <a href= <%= request.getContextPath() + "" %>>Home</a>
    <h3>User:</h3>
    <p id="email">email: <%= UserManager.getInstance().getCurrentUser()%></p>
    <ul>
        <li id="name">name: <%= current_user.getName()%></li>
        <li id="nickname">nickname: <%= current_user.getNickname()%></li>
    </ul>
    <br>
    <h3>Watch List:</h3>
    <table>
        <tr>
            <th>Movie</th>
            <th>releaseDate</th>
            <th>director</th>
            <th>genres</th>
            <th>imdb Rate</th>
            <th>rating</th>
            <th>duration</th>
            <th></th>
            <th></th>
        </tr>

        <% for (Integer movieId: current_user.getWatchList()) {
            Movie movie = DataBase.getInstance().getMovieById(movieId); %>
        <tr>
            <td> <%= movie.getName()%> </td>
            <td> <%= movie.getReleaseDate()%> </td>
            <td> <%= movie.getDirector()%> </td>
            <td> <%= String.join(", ", movie.getGenres())%> </td>
            <td> <%= movie.getImdbRate()%> </td>
            <td> <%= movie.getRating()%> </td>
            <td> <%= movie.getDuration()%> </td>
            <td><a href= <%=request.getContextPath() + "/movies/" + movie.getId()%> >Link</a></td>
            <td>
                <form action="" method="POST" >
                    <input type="hidden" id="form_action" name="action" value="remove">
                    <input type="hidden" id="form_user_id" name="user_id" value=<%= current_user.getEmail()%>>
                    <input type="hidden" id="form_movie_id" name="movie_id" value=<%= movie.getId()%>>
                    <button type="submit">Remove</button>
                </form>
            </td>
        </tr>
        <% } %>
    </table>
    <br>
    <h3>Recommendation List:</h3>
    <table>
        <tr>
            <th>Movie</th>
            <th>imdb Rate</th>
            <th></th>
            <th></th>
        </tr>

        <% int counter = 0;
            for (Integer movieId: recommended_movies) {
                if (counter == 3)
                    break;
                Movie movie = DataBase.getInstance().getMovieById(movieId);
                if (current_user.getWatchList().contains(movieId)) {
                    continue;
                }
                counter += 1;
        %>
        <tr>
            <td> <%= movie.getName()%> </td>
            <td> <%= movie.getImdbRate()%> </td>
            <td> <%= String.join(", ", movie.getGenres())%> </td>
            <td><a href= <%=request.getContextPath() + "/movies/" + movie.getId()%> >Link</a></td>
        </tr>
        <% } %>
    </table>
    <br/>
    </body>
</html>
