package ir.ac.ut.ie.Controllers;

import ir.ac.ut.ie.DataBase;
import ir.ac.ut.ie.Entities.Actor;
import ir.ac.ut.ie.Entities.Movie;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


@RestController
public class ActorController {
    @RequestMapping(value = "/getActorMovies/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Movie[] getActorMovies(@PathVariable(value = "id") Integer id) throws Exception {
        return DataBase.getInstance().getActorMoviesPlayed(id).toArray(new Movie[0]);
    }

    @RequestMapping(value = "/getActor/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Actor getActor(@PathVariable(value = "id") Integer id) throws Exception {
        return DataBase.getInstance().getActorById(id);
    }
}
