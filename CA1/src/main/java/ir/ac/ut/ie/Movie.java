package ir.ac.ut.ie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Movie {
    private Integer id;
    private String name;
    private String summary;
    private String releaseDate;
    private String director;
    private List<String> writers;
    private List<String> genres;
    private List<Integer> cast;
    private float imdbRate;
    private int duration;
    private int ageLimit;
    public float rating;
    private int ratingCount;
    private Map<Integer, Comment> comments;
    private Map<String, Integer> rates;

    public void initialValues() {
        rating = 0;
        ratingCount = 0;
        comments = new HashMap<>();
        rates = new HashMap<>();
    }

    public void update(Movie updatedMovie) {
        name = updatedMovie.getName();
        summary = updatedMovie.getSummary();
        releaseDate = updatedMovie.getReleaseDate();
        director = updatedMovie.getDirector();
        writers = updatedMovie.getWriters();
        genres = updatedMovie.getGenres();
        cast = updatedMovie.getCast();
        imdbRate = updatedMovie.getImdbRate();
        duration = updatedMovie.getDuration();
        ageLimit = updatedMovie.getAgeLimit();
    }

    public void addComment(Comment comment, Integer commentId) {
        comment.initialValues(commentId);
        comments.put(commentId, comment);
    }

    public void addRate(Rate rate) {
        if (rates.containsKey(rate.getUserEmail()))
            rating = (rating * ratingCount - rates.get(rate.getUserEmail()) + rate.getScore()) / ratingCount;
        else {
            rating = (rating * ratingCount + rate.getScore()) / (ratingCount + 1);
            ratingCount += 1;
        }
        rates.put(rate.getUserEmail(), (int) rate.getScore());
    }

    public void createInformationJson(ObjectMapper mapper, ObjectNode movie) {
        movie.put("movieId", id);
        movie.put("name", name);
        movie.put("director", director);
        ArrayNode genreArrayNode = mapper.valueToTree(genres);
        movie.putArray("genres").addAll(genreArrayNode);
        if (ratingCount == 0)
            movie.put("rating", "null");
        else
            movie.put("rating", rating);
    }

    public void printMovieInformation(ObjectMapper mapper, Map<Integer, Actor> actors) throws JsonProcessingException {
        ObjectNode movie = mapper.createObjectNode();
        movie.put("movieId", id);
        movie.put("name", name);
        movie.put("summary", summary);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        movie.put("releaseDate", releaseDate);
        movie.put("director", director);
        ArrayNode writersArray = mapper.valueToTree(writers);
        movie.putArray("writers").addAll(writersArray);
        ArrayNode genreArrayNode = mapper.valueToTree(genres);
        movie.putArray("genres").addAll(genreArrayNode);
        List<ObjectNode> actorsObjNode = new ArrayList<>();
        for (Integer actorId: cast)
            actorsObjNode.add(actors.get(actorId).getInformation(mapper));
        ArrayNode actorsArrayNode = mapper.valueToTree(actorsObjNode);
        String actorsString = mapper.writeValueAsString(actorsArrayNode);
        movie.put("cast", actorsString);
        if (ratingCount == 0)
            movie.put("rating", "null");
        else
            movie.put("rating", rating);
        movie.put("duration", duration);
        movie.put("ageLimit", ageLimit);
        List<ObjectNode> commentsObjNodes = new ArrayList<>();
        for (Map.Entry<Integer, Comment> entry: comments.entrySet())
            commentsObjNodes.add(entry.getValue().getInformation(mapper));
        ArrayNode commentsArrayNode = mapper.valueToTree(commentsObjNodes);
        String commentsString = mapper.writeValueAsString(commentsArrayNode);
        movie.put("comments", commentsString);
        String data = mapper.writeValueAsString(movie);
        CommandHandler.printOutput(new Output(true, data));
    }

    public boolean genreMatch(String genre) {
        for (String curGenre: genres)
            if (curGenre.equals(genre))
                return true;
        return false;
    }


    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getSummary() {
        return summary;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public String getDirector() {
        return director;
    }
    public List<String> getWriters() {
        return writers;
    }
    public List<String> getGenres() {
        return genres;
    }
    public List<Integer> getCast() {
        return cast;
    }
    public float getImdbRate() {
        return imdbRate;
    }
    public int getDuration() {
        return duration;
    }
    public int getAgeLimit() {
        return ageLimit;
    }
    public float getRating() {
        return rating;
    }
    public int getRatingCount() {
        return ratingCount;
    }
    public Map<Integer, Comment> getComments() {
        return comments;
    }
}
