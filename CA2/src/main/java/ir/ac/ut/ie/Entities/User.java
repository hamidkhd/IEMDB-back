package ir.ac.ut.ie.Entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ir.ac.ut.ie.Exceptions.AgeLimitError;
import ir.ac.ut.ie.Exceptions.MovieAlreadyExists;
import ir.ac.ut.ie.Exceptions.MovieNotFound;
import ir.ac.ut.ie.Exceptions.UserNotFound;
import ir.ac.ut.ie.Server;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class User {
    private String email;
    private String password;
    private String nickname;
    private String name;
    private Date birthDate;
    private Set<Integer> watchList = new HashSet<>();

    public void addToWatchList(Integer movieId, int ageLimit) throws Exception {
        movieAlreadyExists(movieId);
        ageLimitError(ageLimit);
        watchList.add(movieId);
        Server.printOutput(new Output(true, "movie added to watchlist successfully"));
    }

    public void movieAlreadyExists(Integer movieId) throws JsonProcessingException, MovieAlreadyExists {
        if (watchList.contains(movieId))
            throw new MovieAlreadyExists();
    }

    public void ageLimitError(int ageLimit) throws Exception {
        LocalDate birthDate = new java.sql.Date(this.birthDate.getTime()).toLocalDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < ageLimit)
            throw new AgeLimitError();
    }

    public void removeFromWatchList(Integer movieId) throws Exception {
        if (!watchList.contains(movieId))
            throw new MovieNotFound();
        watchList.remove(movieId);
        Server.printOutput(new Output(true, "movie removed from watchlist successfully"));
    }

    public List<Movie> getWatchList(ObjectMapper mapper, Map<Integer, Movie> movies) throws Exception {
        List<Movie> watchListMovies = new ArrayList<>();

        ObjectNode watchListNode = mapper.createObjectNode();
        List<ObjectNode> moviesObjectNode = new ArrayList<>();
        for (Integer movieId : watchList) {
            ObjectNode movie = mapper.createObjectNode();
            movies.get(movieId).createInformationJson(mapper, movie);
            moviesObjectNode.add(movie);
            watchListMovies.add(movies.get(movieId));
        }
        ArrayNode arrayNode = mapper.valueToTree(moviesObjectNode);
        watchListNode.putArray("WatchList").addAll(arrayNode);
        String data = mapper.writeValueAsString(watchListNode);
        Server.printOutput(new Output(true, data));
        return watchListMovies;
    }

    public void checkForInvalidCommand() throws Exception {
        if (email==null || password==null || nickname==null || name==null || birthDate==null)
            throw new UserNotFound();
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getNickname() {
        return nickname;
    }
    public String getName() {
        return name;
    }
    public Date getBirthDate() {
        return birthDate;
    }
    public Set<Integer> getWatchList() {
        return watchList;
    }

}
