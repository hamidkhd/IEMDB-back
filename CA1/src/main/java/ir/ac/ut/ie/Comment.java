package ir.ac.ut.ie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

public class Comment {
    private Integer commentId;
    private String userEmail;
    private Integer movieId;
    private String text;
    private int like;
    private int dislike;
    private Map<String, Integer> votes;

    public void initialValues(Integer commentId) {
        this.commentId = commentId;
        like = 0;
        dislike = 0;
        votes = new HashMap<>();
    }

    public void addVote(Vote vote) {
        if (votes.containsKey(vote.getUserEmail())) {
            if (votes.get(vote.getUserEmail()) == 1)
                like -= 1;
            if (votes.get(vote.getUserEmail()) == -1)
                dislike -= 1;
        }
        updateLikeDislike(vote);
        votes.put(vote.getUserEmail(), vote.getVote());
    }

    private void updateLikeDislike(Vote vote) {
        if (vote.getVote() == 1)
            like += 1;
        if (vote.getVote() == -1)
            dislike += 1;
    }

    public ObjectNode getInformation(ObjectMapper mapper) {
        ObjectNode commentMapper = mapper.createObjectNode();
        commentMapper.put("commentId", commentId);
        commentMapper.put("userEmail", userEmail);
        commentMapper.put("text", text);
        commentMapper.put("like", like);
        commentMapper.put("dislike", dislike);
        return commentMapper;
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
