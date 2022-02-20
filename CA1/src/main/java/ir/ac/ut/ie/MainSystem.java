package ir.ac.ut.ie;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainSystem {
    private ObjectMapper mapper;
    Map<Integer, Actor> existingActors;
    Map<Integer, Movie> movies;


    public MainSystem() {
        mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(df);
        existingActors = new HashMap<>();
        movies = new HashMap<>();
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
        else
            movies.put(movie.getId(), movie);
        CommandHandler.printOutput(new Output(true, ""));
    }

    private boolean checkCastExist(List<Integer> cast) {
        for (Integer id: cast) {
            if (! existingActors.containsKey(id))
                return false;
        }
        return true;
    }
}
