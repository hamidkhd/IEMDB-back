package ir.ac.ut.ie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainSystem {
    private ObjectMapper mapper;
    Map<Integer, Actor> existingActors;
    Map<Integer, Movie> movies;
    Map<String, User> users;


    public MainSystem() {
        mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(df);
        existingActors = new HashMap<>();
        movies = new HashMap<>();
        users = new HashMap<>();
    }

    public void addActor(String data) throws IOException {
        Actor actor = mapper.readValue(data, Actor.class);
        if (existingActors.containsKey(actor.getId()))
            existingActors.get(actor.getId()).update(actor);
        else
            existingActors.put(actor.getId(), actor);
        CommandHandler.printOutput(new Output(true, ""));
//        Calendar c = Calendar.getInstance();
//        c.setTime(actor.getBirthDate());
//        System.out.println(c.get(Calendar.MONTH + 1));
    }

    public void addMovie(String data) throws IOException {
        Movie movie = mapper.readValue(data, Movie.class);
        boolean noError = checkCastExist(movie.getCast());
        if (! noError) {
            CommandHandler.printOutput(new Output(false, "ActorNotFound"));
            return;
        }
        if (movies.containsKey(movie.getId()))
            movies.get(movie.getId()).update(movie);
        else {
            movie.initialValues();
            movies.put(movie.getId(), movie);
        }
        CommandHandler.printOutput(new Output(true, ""));
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
        users.put(user.getEmail(), user);
        CommandHandler.printOutput(new Output(true, ""));
    }

    public void addComment(String data) throws IOException {
        Comment comment = mapper.readValue(data, Comment.class);
        if (userOrMovieNotFound(comment.getUserEmail(), comment.getMovieId()))
            return;
        movies.get(comment.getMovieId()).addComment(comment);
        CommandHandler.printOutput(new Output(true, ""));

    }

    private boolean userOrMovieNotFound(String userEmail, Integer movieId) throws JsonProcessingException {
        if (!users.containsKey(userEmail)) {
            CommandHandler.printOutput(new Output(false, "UserNotFound"));
            return true;
        }
        if (! movies.containsKey(movieId)) {
            CommandHandler.printOutput(new Output(false, "MovieNotFound"));
            return true;
        }
        return false;
    }

    public void rateMovie(String data) throws IOException {
        Rate rate = mapper.readValue(data, Rate.class);
        if (userOrMovieNotFound(rate.getUserEmail(), rate.getMovieId()))
            return;
        if (rate.hasError())
            return;
        movies.get(rate.getMovieId()).addRate(rate);
        CommandHandler.printOutput(new Output(true, ""));
    }

}
