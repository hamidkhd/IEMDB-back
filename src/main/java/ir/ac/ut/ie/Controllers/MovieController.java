package ir.ac.ut.ie.Controllers;
import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.*;
import ir.ac.ut.ie.Entities.Actor;
import ir.ac.ut.ie.Entities.Movie;
import ir.ac.ut.ie.Entities.Rate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


@RestController
public class MovieController {
    @RequestMapping(value = "/getMovie/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie getMovie(@PathVariable(value = "id") Integer id) throws Exception {
        return DataBase.getInstance().getMovieById(id);
    }

    @RequestMapping(value = "/getMovieActors/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Actor[] getMovieActors(@PathVariable(value = "id") Integer id) throws Exception {
        List<Actor> actors = new ArrayList<>();
        Movie movie = DataBase.getInstance().getMovieById(id);
//        for (Integer actorId : movie.getCast()) {
//            actors.add(DataBase.getInstance().getActorById(actorId));
//        }
        return actors.toArray(new Actor[0]);
    }

    @RequestMapping(value = "/postRate/{movieId}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie postRate(
            @PathVariable(value = "movieId") Integer movieId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "rate") int score) throws Exception {
        Rate newRate = new Rate(userId, movieId, score);
        DataBase.getInstance().getMovieById(movieId).addRate(newRate);
        return DataBase.getInstance().getMovieById(movieId);
    }
}
