package ir.ac.ut.ie.Entities;

import ir.ac.ut.ie.Exceptions.InvalidVoteValue;

public class Vote {
    private final String userEmail;
    private final Integer commentId;
    private final Integer vote;

    public Vote(String userEmail, Integer commentId, Integer vote) {
        this.userEmail = userEmail;
        this.commentId = commentId;
        this.vote = vote;
    }

    public void hasError() throws Exception {
        if (vote == null)
            throw new InvalidVoteValue();
        if (!(vote == 1 || vote == -1 || vote == 0))
            throw new InvalidVoteValue();
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
