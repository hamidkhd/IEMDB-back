package ir.ac.ut.ie.Entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ir.ac.ut.ie.Exceptions.InvalidCommand;
import ir.ac.ut.ie.Repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer commentId;
    private String userEmail;
    private Integer movieId;
    private String text;
    private String username;
    private Integer commentLike;
    private Integer commentDislike;

    public Comment() {
        this.commentLike = 0;
        this.commentDislike = 0;
    }

    public Comment(String userEmail, Integer movieId, String text, String username) {
        this.userEmail = userEmail;
        this.text = text;
        this.username = username;
        this.commentLike = 0;
        this.commentDislike = 0;
        this.movieId = movieId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Comment(Integer comment_id) {
        commentId = comment_id;
        this.commentLike = 0;
        this.commentDislike = 0;
    }

    public void initialValues() {
        this.commentLike = 0;
        this.commentDislike = 0;
    }

    public void addVote(Vote vote, VoteRepository voteRepository) {
        Vote voteOb = voteRepository.findVoteByUserEmailAndCommentId(vote.getUserEmail(), vote.getCommentId());

        if (voteOb != null) {
            if (voteOb.getVote() == 1)
                commentLike -= 1;
            if (voteOb.getVote() == -1)
                commentDislike -= 1;
            voteRepository.deleteById(voteOb.getVoteID());
        }
        updateLikeDislike(vote);
    }

    private void updateLikeDislike(Vote vote) {
        if (vote.getVote() == 1)
            commentLike += 1;
        if (vote.getVote() == -1)
            commentDislike += 1;
    }

    public ObjectNode getInformation(ObjectMapper mapper) {
        ObjectNode commentMapper = mapper.createObjectNode();
        commentMapper.put("commentId", commentId);
        commentMapper.put("userEmail", userEmail);
        commentMapper.put("text", text);
        commentMapper.put("like", commentLike);
        commentMapper.put("dislike", commentDislike);
        return commentMapper;
    }


    public Integer getCommentId() {
        return commentId;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getText() {
        return text;
    }

    public Integer getLike() {
        return commentLike;
    }

    public Integer getDislike() {
        return commentDislike;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }


    public void setText(String text) {
        this.text = text;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public void setLike(Integer like) {
        this.commentLike = like;
    }
    public void setDislike(Integer dislike) { this.commentDislike = dislike; }
}
