package ir.ac.ut.ie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Vote {
    private String userEmail;
    private Integer commentId;
    private Integer vote;

    public boolean hasError() throws JsonProcessingException {
        if (!(vote instanceof Integer)) {
            CommandHandler.printOutput(new Output(false, "InvalidVoteValue"));
            return true;
        }
        if (vote == 1 || vote == -1 || vote == 0)
            return false;
        CommandHandler.printOutput(new Output(false, "InvalidVoteValue"));
        return true;
    }

    public boolean checkForCommand(String data) throws IOException {
        if (userEmail==null || commentId==null || vote==null)
            return false;
        else {
            return true;
        }
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
