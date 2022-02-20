package ir.ac.ut.ie;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Rate {
    private String userEmail;
    private Integer movieId;
    private float score;

    public boolean hasError() throws JsonProcessingException {
        if (((int) score != score) || (score < 1 || score > 10)) {
            CommandHandler.printOutput(new Output(false, "InvalidRateScore"));
            return true;
        }
        return false;
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
