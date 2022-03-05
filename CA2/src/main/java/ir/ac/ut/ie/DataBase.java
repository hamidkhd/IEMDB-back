package ir.ac.ut.ie;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.ut.ie.Entities.*;
import ir.ac.ut.ie.Exceptions.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataBase {
    static private ObjectMapper mapper;
    static private String host;
    private static Map<Integer, Actor> existingActors;
    private static Map<Integer, Movie> movies;
    private static Map<String, User> users;
    private static Map<Integer, Comment> comments;
    private static Integer commentId;


    public DataBase() throws IOException {
        mapper = new ObjectMapper();
        host = "http://138.197.181.131:5000";
        existingActors = new HashMap<>();
        movies = new HashMap<>();
        users = new HashMap<>();
        comments = new HashMap<>();
        commentId = 1;
        setInformation();
    }

    static private String getConnection(String path) throws IOException {
        URL url = new URL(host + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        return line;
    }

    static void setInformation() throws IOException {
        try {
            setActorsList();
            setMoviesList();
            setUsersList();
            setCommentsList();
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
    }

    static void setMoviesList() throws Exception {
        String data = getConnection("/api/movies");
        Movie[] moviesList = mapper.readValue(data, Movie[].class);
        for (Movie movie: moviesList) {
            movie.checkForInvalidCommand();
            checkCastExist(movie.getCast());

            List<String> actors = new ArrayList<>();
            for (Integer i : movie.getCast())
                actors.add(existingActors.get(i).getName());
            movie.setCastName(actors);

            if (movies.containsKey(movie.getId()))
                movies.get(movie.getId()).update(movie);
            else {
                movie.initialValues();
                movies.put(movie.getId(), movie);
            }
        }
    }

    static private void setActorsList() throws Exception {
        String data = getConnection("/api/actors");
        Actor[] actorsList = mapper.readValue(data, Actor[].class);
        for (Actor actor: actorsList) {
            actor.checkForInvalidCommand();
            if (existingActors.containsKey(actor.getId()))
                existingActors.get(actor.getId()).update(actor);
            else
                existingActors.put(actor.getId(), actor);
        }
    }

    static private void setUsersList() throws Exception {
        String data = getConnection("/api/users");
        User[] usersList = mapper.readValue(data, User[].class);
        for (User user: usersList) {
            checkUserExist(user.getEmail());
            users.put(user.getEmail(), user);
        }

    }

    static private void setCommentsList() throws Exception {
        String data = getConnection("/api/comments");
        Comment[] commentsList = mapper.readValue(data, Comment[].class);
        for(Comment comment: commentsList) {
            comment.checkForInvalidCommand();
            userNotFound(comment.getUserEmail());
            movieNotFound(comment.getMovieId());
            movies.get(comment.getMovieId()).addComment(comment, commentId);
            comments.put(commentId, comment);
            commentId += 1;
        }
    }

    public static void removeFromWatchList(String userEmail, Integer movieId) throws Exception {
        userNotFound(userEmail);
        movieNotFound(movieId);
        users.get(userEmail).removeFromWatchList(movieId);
    }

    public static void addToWatchList(String userEmail, Integer movieId) throws Exception {
        userNotFound(userEmail);
        movieNotFound(movieId);
        int ageLimit = movies.get(movieId).getAgeLimit();
        users.get(userEmail).addToWatchList(movieId, ageLimit);
    }

    public static void rateMovie(Rate rate) throws Exception {
        userNotFound(rate.getUserEmail());
        movieNotFound(rate.getMovieId());
        rate.hasError();
        movies.get(rate.getMovieId()).addRate(rate);
    }

    public static void voteComment(Vote vote) throws Exception {
        userNotFound(vote.getUserEmail());
        commentNotFound(vote.getCommentId());
        vote.hasError();
        comments.get(vote.getCommentId()).addVote(vote);
    }


    public static Movie getMovieById(Integer id) throws Exception {
        return movies.get(id);
    }

    public static void checkCastExist(List<Integer> cast) throws Exception {
        for (Integer id : cast)
            if (!existingActors.containsKey(id))
                throw new ActorNotFound();
    }

    public static void checkUserExist(String userEmail) throws Exception {
        if (users.containsKey(userEmail))
            throw new UserAlreadyExists();
    }

    public static void userNotFound(String userEmail) throws Exception {
        if (!users.containsKey(userEmail))
            throw new UserNotFound();
    }

    public static void movieNotFound(Integer movieId) throws Exception {
        if (!movies.containsKey(movieId))
            throw new MovieNotFound();
    }

    public static void commentNotFound(Integer commentId) throws Exception {
        if (!comments.containsKey(commentId))
            throw new CommentNotFound();
    }

    public static Map<String, User> getUsers() {
        return users;
    }

    public static Map<Integer, Movie> getMovies() {
        return movies;
    }

    public static Map<Integer, Actor> getExistingActors() {
        return existingActors;
    }

    public static Map<Integer, Comment> getComments() {
        return comments;
    }
}
