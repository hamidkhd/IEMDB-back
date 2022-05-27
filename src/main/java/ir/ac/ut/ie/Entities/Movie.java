package ir.ac.ut.ie.Entities;

import ir.ac.ut.ie.Exceptions.InvalidCommand;
import ir.ac.ut.ie.Repository.RateRepository;
import org.w3c.dom.Text;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name= "Movie")
public class Movie {
    @Id
    @Column(name = "movie_id")
    private Integer id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String summary;
    private String releaseDate;
    private String director;
    @ElementCollection
    private List<String> writers;
    @ElementCollection
    private List<String> genres;
    @ElementCollection
    private List<Integer> cast;
    private Float imdbRate;
    private Integer duration;
    private Integer ageLimit;
    public Float rating;
    private Integer ratingCount;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Comment> comments;
    private String image;
    private String coverImage;
    Integer score;

    public Movie() {
        rating = (float) 0;
        ratingCount = 0;
        score = 0;
        comments = new ArrayList<>();
    }

    public void initialValues() {
        rating = (float) 0;
        ratingCount = 0;
        comments = new ArrayList<>();
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
        comment.initialValues();
        comments.add(comment);
    }

    public void addRate(Rate rate, RateRepository rateRepository) {
        Rate rateOb = rateRepository.findRateByUserEmailAndMovieId(rate.getUserEmail(), rate.getMovieId());

        if (rateOb != null) {
            rating = (rating * ratingCount - rateOb.getScore() + rate.getScore()) / ratingCount;
            rateRepository.delete(rateOb);
        }
        else {
            rating = (rating * ratingCount + rate.getScore()) / (ratingCount + 1);
            ratingCount += 1;
        }
    }

    public boolean genreMatch(String genre) {
        for (String curGenre : getGenres())
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

    public Float getImdbRate() {
        return imdbRate;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getAgeLimit() {
        return ageLimit;
    }

    public Float getRating() {
        return rating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getImage() {
        return image;
    }
}
