package ir.ac.ut.ie;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainSystem {
    private ObjectMapper mapper;
    private Map<Integer, Actor> existingActors;
    private Map<Integer, Movie> movies;
    private Map<String, User> users;
    private Map<Integer, Comment> comments;
    private Integer commentId;


    public MainSystem() {
        mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(df);
        existingActors = new HashMap<>();
        movies = new HashMap<>();
        users = new HashMap<>();
        comments = new HashMap<>();
        commentId = 1;
    }

    public void addActor(String data) throws IOException {
        Actor actor = mapper.readValue(data, Actor.class);

        if (! actor.checkForCommand())
            CommandHandler.printOutput(new Output(false, "InvalidCommand"));

        else {
            if (existingActors.containsKey(actor.getId()))
                existingActors.get(actor.getId()).update(actor);
            else
                existingActors.put(actor.getId(), actor);
            CommandHandler.printOutput(new Output(true, "actor added successfully"));
        }
    }

    public void addMovie(String data) throws IOException {
        Movie movie = mapper.readValue(data, Movie.class);

        if (! movie.checkForCommand())
            CommandHandler.printOutput(new Output(false, "InvalidCommand"));

        else {
            boolean noError = checkCastExist(movie.getCast());
            if (!noError) {
                CommandHandler.printOutput(new Output(false, "ActorNotFound"));
                return;
            }
            if (movies.containsKey(movie.getId()))
                movies.get(movie.getId()).update(movie);
            else {
                movie.initialValues();
                movies.put(movie.getId(), movie);
            }
            CommandHandler.printOutput(new Output(true, "movie added successfully"));
        }
    }

    private boolean checkCastExist(List<Integer> cast) {
        for (Integer id: cast) {
            if (! existingActors.containsKey(id))
                return false;
        }
        return true;
    }

    public void addUser(String data) throws IOException {
        User user = mapper.readValue(data, User.class);

        if (! user.checkForCommand())
            CommandHandler.printOutput(new Output(false, "InvalidCommand"));

        else {
            users.put(user.getEmail(), user);
            CommandHandler.printOutput(new Output(true, "user added successfully"));
        }
    }

    public void addComment(String data) throws IOException {
        Comment comment = mapper.readValue(data, Comment.class);

        if (! comment.checkForCommand())
            CommandHandler.printOutput(new Output(false, "InvalidCommand"));

        else {
            if (userNotFound(comment.getUserEmail()) || movieNotFound(comment.getMovieId()))
                return;
            movies.get(comment.getMovieId()).addComment(comment, commentId);
            comments.put(commentId, comment);
            CommandHandler.printOutput(new Output(true, "comment with id " + commentId.toString() + " added successfully"));
            commentId += 1;
        }
    }

    private boolean userNotFound(String userEmail) throws JsonProcessingException {
        if (!users.containsKey(userEmail)) {
            CommandHandler.printOutput(new Output(false, "UserNotFound"));
            return true;
        }
        return false;
    }

    private boolean movieNotFound(Integer movieId) throws  JsonProcessingException {
        if (! movies.containsKey(movieId)) {
            CommandHandler.printOutput(new Output(false, "MovieNotFound"));
            return true;
        }
        return false;
    }

    private boolean commentNotFound(Integer commentId) throws JsonProcessingException {
        if (! comments.containsKey(commentId)) {
            CommandHandler.printOutput(new Output(false, "CommentNotFound"));
            return true;
        }
        return false;
    }

    public void rateMovie(String data) throws IOException {
        Rate rate = mapper.readValue(data, Rate.class);

        if (!rate.checkForCommand())
            CommandHandler.printOutput(new Output(false, "InvalidCommand"));

        else {
            if (userNotFound(rate.getUserEmail()) || movieNotFound(rate.getMovieId()))
                return;
            if (rate.hasError())
                return;
            movies.get(rate.getMovieId()).addRate(rate);
            CommandHandler.printOutput(new Output(true, "movie rated successfully"));
        }
    }

    public void voteComment(String data) throws IOException {
        Vote vote = mapper.readValue(data, Vote.class);

        if (!vote.checkForCommand())
            CommandHandler.printOutput(new Output(false, "InvalidCommand"));

        else {
            if (userNotFound(vote.getUserEmail()) || commentNotFound(vote.getCommentId()))
                return;
            if (vote.hasError())
                return;
            comments.get(vote.getCommentId()).addVote(vote);
            CommandHandler.printOutput(new Output(true, "comment voted successfully"));
        }
    }

    public void watchListHandler(String data, boolean add) throws IOException {
        JsonNode jsonNode = mapper.readTree(data);
        String userEmail = jsonNode.get("userEmail").asText();
        Integer movieId = jsonNode.get("movieId").asInt();

        if (userEmail==null || movieId==null)
            CommandHandler.printOutput(new Output(false, "InvalidCommand"));

        else {
            if (userNotFound(userEmail) || movieNotFound(movieId))
                return;
            if (add)
                addToWatchList(userEmail, movieId);
            else
                removeFromWatchList(userEmail, movieId);
        }
    }

    public void addToWatchList(String userEmail, Integer movieId) throws IOException {
        int ageLimit = movies.get(movieId).getAgeLimit();
        users.get(userEmail).addToWatchList(movieId, ageLimit);
    }

    public void removeFromWatchList(String userEmail, Integer movieId) throws IOException {
        users.get(userEmail).removeFromWatchList(movieId);
    }

    public void getMoviesList(String inputData) throws JsonProcessingException {
        if (inputData.length() > 0)
            CommandHandler.printOutput(new Output(false, "InvalidCommand"));

        List<ObjectNode> objects = new ArrayList<>();
        for (Map.Entry<Integer, Movie> entry : movies.entrySet()) {
            ObjectNode movie = mapper.createObjectNode();
            entry.getValue().createInformationJson(mapper, movie);
            objects.add(movie);
        }
        ArrayNode arrayNode = mapper.valueToTree(objects);
        ObjectNode movieList = mapper.createObjectNode();
        movieList.putArray("MoviesList").addAll(arrayNode);
        String data = mapper.writeValueAsString(movieList);
        CommandHandler.printOutput(new Output(true, data));
    }

    public void getMovieById(String data) throws IOException {
        Integer id = mapper.readTree(data).get("movieId").asInt();
        if (id==null)
            CommandHandler.printOutput(new Output(false, "InvalidCommand"));
        else {
            if (movieNotFound(id))
                return;
            movies.get(id).printMovieInformation(mapper, existingActors);
        }
    }

    public void getMoviesByGenre(String data) throws IOException {
        String genre = mapper.readTree(data).get("genre").asText();

        if (genre==null)
            CommandHandler.printOutput(new Output(false, "InvalidCommand"));
        else {
            List<ObjectNode> moviesObjectNode = new ArrayList<>();
            for (Map.Entry<Integer, Movie> entry : movies.entrySet()) {
                if (entry.getValue().genreMatch(genre)) {
                    ObjectNode movie = mapper.createObjectNode();
                    entry.getValue().createInformationJson(mapper, movie);
                    moviesObjectNode.add(movie);
                }
            }
            ObjectNode moviesListByGenre = mapper.createObjectNode();
            ArrayNode moviesArrayNode = mapper.valueToTree(moviesObjectNode);
            moviesListByGenre.putArray("MoviesListByGenre").addAll(moviesArrayNode);
            String outputData = mapper.writeValueAsString(moviesListByGenre);
            CommandHandler.printOutput(new Output(true, outputData));
        }
    }

    public void getWatchList(String data) throws IOException {
        String userEmail = mapper.readTree(data).get("userEmail").asText();
        if (userEmail==null)
            CommandHandler.printOutput(new Output(false, "InvalidCommand"));

        else {
            if (userNotFound(userEmail))
                return;
            users.get(userEmail).getWatchList(mapper, movies);
        }
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public Map<Integer, Movie> getMovies() {
        return movies;
    }

    public Map<Integer, Comment> getComments() {
        return comments;
    }

}
