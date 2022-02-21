package ir.ac.ut.ie;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Vote {
    private String userEmail;
    private Integer commentId;
    private int vote;

    public boolean hasError() throws JsonProcessingException {
        if (vote == 1 || vote == -1 || vote == 0)
            return false;
        CommandHandler.printOutput(new Output(false, "InvalidVoteValue"));
        return true;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public Integer getCommentId() {
        return commentId;
    }
    public int getVote() {
        return vote;
    }
}
