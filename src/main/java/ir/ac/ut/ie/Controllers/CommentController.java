package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.Comment;
import ir.ac.ut.ie.Entities.Vote;
import ir.ac.ut.ie.Repository.CommentRepository;
import ir.ac.ut.ie.Repository.UserRepository;
import ir.ac.ut.ie.Repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommentController {
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    CommentRepository commentRepository;

    @RequestMapping(value = "/postMovieCommentRate", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment postMovieCommentRate(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "commentId") Integer commentId,
            @RequestParam(value = "like") Integer like) throws Exception {

        Vote vote = new Vote(userId, commentId, like);
        commentRepository.findCommentByCommentId(commentId).addVote(vote, voteRepository);
        voteRepository.save(vote);
        commentRepository.save(commentRepository.findCommentByCommentId(commentId));
        return commentRepository.findCommentByCommentId(commentId);
    }

    @RequestMapping(value = "/addComment", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment addComment(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "movieId") Integer movieId,
            @RequestParam(value = "text") String text) {
        return DataBase.getInstance().addComment(userId, movieId, text);
    }
}
