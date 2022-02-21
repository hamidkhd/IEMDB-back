package ir.ac.ut.ie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    public void addToWatchList(Integer movieId, int ageLimit) throws JsonProcessingException {
        if (movieAlreadyExists(movieId) || ageLimitError(ageLimit))
            return;
        watchList.add(movieId);
        CommandHandler.printOutput(new Output(true, "movie added to watchlist successfully"));
    }

    private boolean movieAlreadyExists(Integer movieId) throws JsonProcessingException {
        if (watchList.contains(movieId)) {
            CommandHandler.printOutput(new Output(false, "MovieAlreadyExists"));
            return true;
        }
        return false;
    }

    private boolean ageLimitError(int ageLimit) throws JsonProcessingException {
        LocalDate birthDate = new java.sql.Date(this.birthDate.getTime()).toLocalDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < ageLimit) {
            CommandHandler.printOutput(new Output(false, "AgeLimitError"));
            return true;
        }
        return false;
    }

    public void removeFromWatchList(Integer movieId) throws JsonProcessingException {
        if (! watchList.contains(movieId)) {
            CommandHandler.printOutput(new Output(false, "MovieNotFound"));
            return;
        }
        watchList.remove(movieId);
        CommandHandler.printOutput(new Output(true, "movie removed from watchlist successfully"));
    }

    public void getWatchList(ObjectMapper mapper, Map<Integer, Movie> movies) throws JsonProcessingException {
        ObjectNode watchListNode = mapper.createObjectNode();
        List<ObjectNode> moviesObjectNode = new ArrayList<>();
        for (Integer movieId : watchList) {
            ObjectNode movie = mapper.createObjectNode();
            movies.get(movieId).createInformationJson(mapper, movie);
            moviesObjectNode.add(movie);
        }
        ArrayNode arrayNode = mapper.valueToTree(moviesObjectNode);
        watchListNode.putArray("WatchList").addAll(arrayNode);
        String data = mapper.writeValueAsString(watchListNode);
        CommandHandler.printOutput(new Output(true, data));
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

}
