package ir.ac.ut.ie.Entities;

import ir.ac.ut.ie.Exceptions.InvalidRateScore;

import javax.persistence.*;

@Entity
@Table(name = "Rate")
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer rateId;
    private String userEmail;
    private Integer movieId;
    private float score;

    public Rate(String userEmail, Integer movieId, float score) {
        this.userEmail = userEmail;
        this.movieId = movieId;
        this.score = score;
    }

    public Rate() {}

    public void hasError() throws Exception {
        if ((((int) score != score) || (score < 1 || score > 10)))
            throw new InvalidRateScore();
    }

    public String getUserEmail() {
        return userEmail;
    }
    public Integer getMovieId() {
        return movieId;
    }
    public float getScore() {
        return score;
    }
}
