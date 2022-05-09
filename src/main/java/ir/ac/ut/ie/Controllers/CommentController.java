package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.Comment;
import ir.ac.ut.ie.Entities.Vote;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.TimeUnit;

@RestController
public class CommentController {
    @RequestMapping(value = "/postMovieCommentRate", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment postMovieCommentRate(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "commentId") Integer commentId,
            @RequestParam(value = "like") Integer like) throws Exception {
        Vote vote = new Vote(userId, commentId, like);
        DataBase.getInstance().getComments().get(commentId).addVote(vote);
        return DataBase.getInstance().getComments().get(commentId);
    }

    @RequestMapping(value = "/addComment", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment addComment(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "movieId") Integer movieId,
            @RequestParam(value = "text") String text) throws Exception {
        return DataBase.getInstance().addComment(userId, movieId, text);
    }
}
