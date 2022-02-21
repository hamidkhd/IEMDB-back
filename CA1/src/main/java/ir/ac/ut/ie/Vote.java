package ir.ac.ut.ie;

public class Vote {
    private String userEmail;
    private Integer commentId;
    private int vote;

    public boolean hasError() {
        if (vote == 1 || vote == -1 || vote == 0)
            return false;
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
