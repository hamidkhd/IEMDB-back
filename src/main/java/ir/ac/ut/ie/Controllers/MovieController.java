package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.*;
import ir.ac.ut.ie.UserManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
public class MovieController {
    @RequestMapping(value = "/getMovie/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie getMovie(@PathVariable(value = "id") String id) throws Exception {
        TimeUnit.SECONDS.sleep(3);
        return DataBase.getInstance().getMovieById(Integer.parseInt(id));
    }

    @RequestMapping(value = "/getMovieActors/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Actor[] getMovieActors(@PathVariable(value = "id") String id) throws Exception {
        List<Actor> actors = new ArrayList<>();
        Movie movie = DataBase.getInstance().getMovieById(Integer.parseInt(id));
        for (Integer actorId : movie.getCast()) {
            actors.add(DataBase.getInstance().getActorById(actorId));
        }
        TimeUnit.SECONDS.sleep(3);
        return actors.toArray(new Actor[0]);
    }

    @RequestMapping(value = "/postMovieCommentRate/{movieId}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment postMovieCommentRate(
            @PathVariable(value = "movieId") String movieId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "commentId") String commentId,
            @RequestParam(value = "like") String like) throws Exception {
        Vote vote = new Vote(userId, Integer.parseInt(commentId), Integer.parseInt(like));
        DataBase.getInstance().getComments().get(Integer.parseInt(commentId)).addVote(vote);
        TimeUnit.SECONDS.sleep(3);
        return DataBase.getInstance().getComments().get(Integer.parseInt(commentId));
    }

    @RequestMapping(value = "/addComment", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment addComment(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "movieId") String movieId,
            @RequestParam(value = "text") String text) throws Exception {
        TimeUnit.SECONDS.sleep(3);
        return DataBase.getInstance().addComment(userId, Integer.parseInt(movieId), text);
    }

    @RequestMapping(value = "/postRate/{movieId}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie postRate(
            @PathVariable(value = "movieId") Integer movieId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "rate") int score) throws Exception {
        Rate newRate = new Rate(userId, movieId, score);
        DataBase.getInstance().getMovieById(movieId).addRate(newRate);
        TimeUnit.SECONDS.sleep(3);
        return DataBase.getInstance().getMovieById(movieId);
    }
}
