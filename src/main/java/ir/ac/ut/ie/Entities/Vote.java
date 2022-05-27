package ir.ac.ut.ie.Entities;

import ir.ac.ut.ie.Exceptions.InvalidVoteValue;
import ir.ac.ut.ie.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Entity
@Table(name = "Vote")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer voteID;
    private String userEmail;
    private Integer commentId;
    private Integer vote;

    public Vote(String userEmail, Integer commentId, Integer vote) {
        this.userEmail = userEmail;
        this.commentId = commentId;
        this.vote = vote;
    }

    public Vote() {}

    public void hasError() throws Exception {
        if (vote == null)
            throw new InvalidVoteValue();
        if (!(vote == 1 || vote == -1 || vote == 0))
            throw new InvalidVoteValue();
    }

    public Integer getVoteID() {
        return voteID;
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
    public void setVote(Integer vote) {
        this.vote = vote;
    }
}
