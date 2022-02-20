package ir.ac.ut.ie;

public class Comment {
    private Integer commentId;
    private String userEmail;
    private Integer movieId;
    private String text;
    private int like;
    private int dislike;

    public void initialValues(Integer commentId) {
        this.commentId = commentId;
        like = 0;
        dislike = 0;
    }

    public int getCommentId() {
        return commentId;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public Integer getMovieId() {
        return movieId;
    }
    public String getText() {
        return text;
    }
    public int getLike() {
        return like;
    }
    public int getDislike() {
        return dislike;
    }
}
