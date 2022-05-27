package ir.ac.ut.ie.Repository;

import ir.ac.ut.ie.Entities.Comment;
import ir.ac.ut.ie.Entities.Rate;
import ir.ac.ut.ie.Entities.Vote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Integer> {
    Vote findVoteByUserEmailAndCommentId(String userEmail, Integer commentId);
}
