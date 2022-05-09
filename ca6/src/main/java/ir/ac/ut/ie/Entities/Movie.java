package ir.ac.ut.ie.Entities;

import ir.ac.ut.ie.Exceptions.InvalidCommand;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.*;

@Entity
public class Movie {
    @Id
    private Integer id;
    private String name;
    private String summary;
    private String releaseDate;
    private String director;
//    private List<String> writers;
//    private List<String> genres;
//    private List<Integer> cast;
//    private List<String> castName;
    private Float imdbRate;
    private Integer duration;
    private Integer ageLimit;
    public float rating;
    private int ratingCount;
    @OneToMany(mappedBy = "movie")
    private List<Comment> comments;
//    private Map<String, Integer> rates;
    private String image;
    private String coverImage;
    int score;

    public void initialValues() {
        rating = 0;
        ratingCount = 0;
        comments = new ArrayList<>();
//        rates = new HashMap<>();
    }

    public void update(Movie updatedMovie) {
        name = updatedMovie.getName();
        summary = updatedMovie.getSummary();
        releaseDate = updatedMovie.getReleaseDate();
        director = updatedMovie.getDirector();
//        writers = updatedMovie.getWriters();
//        genres = updatedMovie.getGenres();
//        cast = updatedMovie.getCast();
        imdbRate = updatedMovie.getImdbRate();
        duration = updatedMovie.getDuration();
        ageLimit = updatedMovie.getAgeLimit();
    }

    public void addComment(Comment comment, Integer commentId) {
        comment.initialValues(commentId);
        comments.add(comment);
    }

    public void addRate(Rate rate) {
//        if (rates.containsKey(rate.getUserEmail()))
//            rating = (rating * ratingCount - rates.get(rate.getUserEmail()) + rate.getScore()) / ratingCount;
//        else {
//            rating = (rating * ratingCount + rate.getScore()) / (ratingCount + 1);
//            ratingCount += 1;
//        }
//        rates.put(rate.getUserEmail(), (int) rate.getScore());
    }

//    public boolean genreMatch(String genre) {
//        for (String curGenre : getGenres())
//            if (curGenre.equals(genre))
//                return true;
//        return false;
//    }



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

//    public List<String> getWriters() {
//        return writers;
//    }
//
//    public List<String> getGenres() {
//        return genres;
//    }
//
//    public List<Integer> getCast() {
//        return cast;
//    }

    public Float getImdbRate() {
        return imdbRate;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getAgeLimit() {
        return ageLimit;
    }

    public float getRating() {
        return rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public List<Comment> getComments() {
        return comments;
    }

//    public Map<String, Integer> getRates() {
//        return rates;
//    }
//
//    public void setCastName(List<String> castName) {
//        this.castName = castName;
//    }
//
//    public List<String> getCastName() {
//        return castName;
//    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getImage() {
        return image;
    }
}
