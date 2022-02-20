package ir.ac.ut.ie;

import java.util.*;

public class Movie {
    private Integer id;
    private String name;
    private String summary;
    private Date releaseDate;
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
    private int commentId;

    public void initialValues() {
        rating = 0;
        ratingCount = 0;
        comments = new HashMap<>();
        commentId = 1;
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

    public void addComment(Comment comment) {
        comment.initialValues(commentId);
        comments.put(commentId, comment);
        commentId += 1;
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
    public Date getReleaseDate() {
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
